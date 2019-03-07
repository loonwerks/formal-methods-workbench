package com.rockwellcollins.atc.resolute.analysis.handlers;

import static java.util.stream.Collectors.joining;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.xtext.EcoreUtil2;
import org.osate.aadl2.AnnexSubclause;
import org.osate.aadl2.ComponentImplementation;
import org.osate.aadl2.DefaultAnnexSubclause;
import org.osate.aadl2.Element;
import org.osate.aadl2.NamedElement;
import org.osate.aadl2.instance.ComponentInstance;
import org.osate.aadl2.instance.EndToEndFlowInstance;
import org.osate.aadl2.instance.FlowSpecificationInstance;
import org.osate.aadl2.instance.InstanceObject;
import org.osate.aadl2.instance.SystemInstance;
import org.osate.aadl2.instantiation.InstantiateModel;
import org.osate.ui.dialogs.Dialog;

import com.rockwellcollins.atc.resolute.analysis.execution.EvaluationContext;
import com.rockwellcollins.atc.resolute.analysis.execution.FeatureToConnectionsMap;
import com.rockwellcollins.atc.resolute.analysis.execution.NamedElementComparator;
import com.rockwellcollins.atc.resolute.analysis.execution.ResoluteInterpreter;
import com.rockwellcollins.atc.resolute.analysis.results.LintResult;
import com.rockwellcollins.atc.resolute.analysis.results.ResoluteResult;
import com.rockwellcollins.atc.resolute.resolute.AnalysisStatement;
import com.rockwellcollins.atc.resolute.resolute.CheckStatement;
import com.rockwellcollins.atc.resolute.resolute.FnCallExpr;
import com.rockwellcollins.atc.resolute.resolute.IdExpr;
import com.rockwellcollins.atc.resolute.resolute.LintExpr;
import com.rockwellcollins.atc.resolute.resolute.LintStatement;
import com.rockwellcollins.atc.resolute.resolute.ResoluteSubclause;
import com.rockwellcollins.atc.resolute.resolute.Ruleset;
import com.rockwellcollins.atc.resolute.resolute.ThisExpr;
import com.rockwellcollins.atc.resolute.validation.BaseType;

public class LinterHandler extends AadlHandler {

	protected final String MARKER_TYPE = "com.rockwellcollins.atc.resolute.linter.marker";

	@Override
	protected String getJobName() {
		return "Resolute Lint Analysis";
	}

	@Override
	protected IStatus runJob(Element root, IProgressMonitor monitor) {

		long start = System.currentTimeMillis();
		SystemInstance si;
		ComponentImplementation compImpl = getComponentImplementation(root);
		if (compImpl == null) {
			return Status.CANCEL_STATUS;
		} else {
			try {
				si = InstantiateModel.buildInstanceModelFile(compImpl);
			} catch (Exception e) {
				Dialog.showError("Model Instantiate", "Error while re-instantiating the model: " + e.getMessage());
				return Status.CANCEL_STATUS;
			}
		}
		long stop = System.currentTimeMillis();
		System.out.println("Instantiation time: " + (stop - start) / 1000.0 + "s");

		// Clear the problems pane
		clearMarkers(si.getComponentImplementation());

		start = System.currentTimeMillis();

		Map<String, SortedSet<NamedElement>> sets = new HashMap<>();
		initializeSets(si, sets);
		FeatureToConnectionsMap featToConnsMap = new FeatureToConnectionsMap(si);
		List<ResoluteResult> checkTrees = new ArrayList<>();

		// Get the resolute subclause for the selected component implementation
		for (AnnexSubclause annexSubclause : si.getComponentImplementation().getOwnedAnnexSubclauses()) {
			DefaultAnnexSubclause defaultSubclause = (DefaultAnnexSubclause) annexSubclause;
			if (defaultSubclause.getParsedAnnexSubclause() instanceof ResoluteSubclause) {
				ResoluteSubclause resoluteSubclause = (ResoluteSubclause) defaultSubclause.getParsedAnnexSubclause();
				EvaluationContext context = new EvaluationContext(si.getComponentInstance(), sets, featToConnsMap);
				ResoluteInterpreter interpreter = new ResoluteInterpreter(context);

				// Evaluate each check statement in selected implementation
				for (AnalysisStatement as : resoluteSubclause.getProves()) {
					if (as instanceof CheckStatement) {
						CheckStatement cs = (CheckStatement) as;
						if (cs.getExpr() instanceof IdExpr) {
							IdExpr idExpr = (IdExpr) cs.getExpr();
							if (idExpr.getId() instanceof Ruleset) {
								Ruleset ruleset = (Ruleset) idExpr.getId();
								for (LintStatement lint : ruleset.getBody().getLintStatements()) {
									try {
										checkTrees.add(interpreter.evaluateLintStatement(lint));
									} catch (Exception e) {
										handleCheckStatementException(cs, e);
									}
								}
							}
						} else if (cs.getExpr() instanceof LintExpr) {
							LintStatement lint = ((LintExpr) cs.getExpr()).getLintStmt();
							try {
								checkTrees.add(interpreter.evaluateLintStatement(lint));
							} catch (Exception e) {
								handleCheckStatementException(cs, e);
							}
						}
					}
				}
				break;
			}
		}

		displayResults(checkTrees);

		stop = System.currentTimeMillis();
		System.out.println("Evaluation time: " + (stop - start) / 1000.0 + "s");

		return Status.OK_STATUS;
	}

	private void clearMarkers(ComponentImplementation ci) {

		for (Resource r : ci.eResource().getResourceSet().getResources()) {
			try {
				IResource resource = getIResource(r);
				resource.deleteMarkers(MARKER_TYPE, true, IResource.DEPTH_INFINITE);
			} catch (Exception e) {
				continue;
			}
		}

	}

	private void displayResults(List<ResoluteResult> checkTrees) {
		// Print the errors to standard 'Problems' pane.
		int errors = 0;
		int warnings = 0;

		for (ResoluteResult resoluteResult : checkTrees) {
			if (!resoluteResult.isValid()) {
				LintResult result = (LintResult) resoluteResult;
				try {
					IMarker marker = getIResource(result.getLocation().eResource()).createMarker(MARKER_TYPE);
					marker.setAttribute(IMarker.MESSAGE, result.getText());
					int severity = result.getSeverity();
					marker.setAttribute(IMarker.SEVERITY, severity);
					if (severity == IMarker.SEVERITY_ERROR) {
						errors++;
					} else if (severity == IMarker.SEVERITY_WARNING) {
						warnings++;
					}
				} catch (CoreException exception) {
					exception.printStackTrace();
				}
			}
		}

		if ((errors + warnings) == 0) {
			Dialog.showInfo("AADL Linter", "No problems found");
		} else {
			String message = "";
			if (errors > 0) {
				message += errors + " error" + (errors > 1 ? "s" : "") + " found.";
			}
			if (warnings > 0) {
				if (!message.isEmpty()) {
					message += "\n";
				}
				message += warnings + " warning" + (warnings > 1 ? "s" : "") + " found.";
			}
			Dialog.showInfo("AADL Linter", message);
		}

	}

	protected static IResource getIResource(Resource r) {
		final URI uri = r.getURI();
		final IPath path = new Path(uri.toPlatformString(true));
		final IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(path);
		if (resource == null) {
			throw new RuntimeException("Unable to get IResource for Resource: " + r);
		}
		return resource;
	}

	private void handleCheckStatementException(CheckStatement cs, Exception e) {
		String bodyText = simpleSerializer(cs.getExpr());
		getWindow().getShell().getDisplay().syncExec(() -> {
			MessageDialog.openError(getWindow().getShell(), "Error in check statement: " + bodyText, e.getMessage());
		});
		e.printStackTrace();
	}

	private String simpleSerializer(EObject e) {
		if (e instanceof FnCallExpr) {
			FnCallExpr fce = (FnCallExpr) e;
			String args = fce.getArgs().stream().map(this::simpleSerializer).collect(joining(", "));
			return fce.getFn().getName() + "(" + args + ")";
		} else if (e instanceof ThisExpr) {
			return "this";
		} else if (e instanceof IdExpr) {
			IdExpr ide = (IdExpr) e;
			return ide.getId().getFullName();
		} else {
			return e.toString();
		}
	}

	private void initializeSets(ComponentInstance ci, Map<String, SortedSet<NamedElement>> sets) {
		if (ci == null) {
			return;
		}

		addToSet(sets, ci);
		for (InstanceObject io : EcoreUtil2.getAllContentsOfType(ci, InstanceObject.class)) {
			addToSet(sets, io);
		}

		for (FlowSpecificationInstance flowSpec : ci.getFlowSpecifications()) {
			addToSet(sets, "flow_specification", flowSpec);
		}

		for (EndToEndFlowInstance etef : ci.getEndToEndFlows()) {
			addToSet(sets, "end_to_end_flow", etef);
		}
	}

	private void addToSet(Map<String, SortedSet<NamedElement>> sets, InstanceObject io) {
		BaseType type = new BaseType(io);
		for (BaseType superType : type.getAllSuperTypes()) {
			addToSet(sets, superType.name, io);
		}

	}

	private void addToSet(Map<String, SortedSet<NamedElement>> sets, String name, NamedElement ne) {
		SortedSet<NamedElement> set = sets.get(name);
		if (set == null) {
			set = new TreeSet<>(new NamedElementComparator());
			sets.put(name, set);
		}
		set.add(ne);
	}
}
