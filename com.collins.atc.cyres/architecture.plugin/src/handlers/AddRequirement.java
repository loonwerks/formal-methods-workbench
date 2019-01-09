package com.collins.atc.ace.cyres.architecture.handlers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.PlatformUI;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.utils.EditorUtils;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;
import org.osate.aadl2.AadlPackage;
import org.osate.aadl2.AnnexSubclause;
import org.osate.aadl2.Classifier;
import org.osate.aadl2.ComponentType;
import org.osate.aadl2.DefaultAnnexSubclause;
import org.osate.aadl2.NamedElement;
import org.osate.aadl2.PublicPackageSection;
import org.osate.aadl2.ThreadType;
import org.osate.aadl2.impl.AadlPackageImpl;
import org.osate.aadl2.impl.DefaultAnnexSubclauseImpl;
import org.osate.aadl2.impl.PackageSectionImpl;

import com.collins.atc.ace.cyres.architecture.CaseClaimsManager;
import com.collins.atc.ace.cyres.architecture.dialogs.ImportRequirementsDialog;
import com.rockwellcollins.atc.resolute.resolute.Expr;
import com.rockwellcollins.atc.resolute.resolute.FnCallExpr;
import com.rockwellcollins.atc.resolute.resolute.ProveStatement;
import com.rockwellcollins.atc.resolute.resolute.ResoluteSubclause;

public class AddRequirement extends AbstractHandler {

	public static class CASE_Requirement {
		public String name = "";
		public String id = "";
		public String text = "";
		public String component = "";
		public String rationale = "";

		public CASE_Requirement(String name, String id, String text, String component, String rationale) {
			this.name = name;
			this.id = id;
			this.text = text;
			this.component = component;
			this.rationale = rationale;
		}
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

//		// TODO: These should all be in a single class
//		List<String> reqNames = null;
//		List<String> reqIDs = null;
//		List<String> reqTexts = null;
//		List<String> reqComps = null;
		List<CASE_Requirement> importReqs = null;

		// Open wizard to enter filter info
		ImportRequirementsDialog wizard = new ImportRequirementsDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		wizard.setResoluteClauses(getResoluteClauses());
		wizard.create();
		if (wizard.open() == Window.OK) {
//			reqNames = wizard.getReqNames();
//			reqIDs = wizard.getReqIDs();
//			reqTexts = wizard.getReqTexts();
//			reqComps = wizard.getComponents();
			importReqs = wizard.getImportRequirements();

//			for (int i = 0; i < reqNames.size(); i++) {
			// Insert selected requirements into model
			for (CASE_Requirement req : importReqs) {
//				// Insert requirements into model
//				addRequirement(reqComps.get(i), reqNames.get(i), reqIDs.get(i), reqTexts.get(i));
//				// Add corresponding resolute clauses to *_CASE_Claims.aadl
//				addClaims(reqNames.get(i), reqIDs.get(i), reqTexts.get(i));
				// Insert requirements into model
				addRequirement(req.component, req.name, req.id, req.text);
				// Add corresponding resolute clauses to *_CASE_Claims.aadl
				addClaims(req.name, req.id, req.text);
			}

			// TODO: Write omitted requirements to log
		}

		return null;
	}

	private void addRequirement(String componentName, String reqName, String reqID, String reqText) {
		// Get the active xtext editor so we can make modifications
		final XtextEditor xtextEditor = EditorUtils.getActiveXtextEditor();

		xtextEditor.getDocument().modify(new IUnitOfWork.Void<XtextResource>() {

			@Override
			public void process(final XtextResource resource) throws Exception {
				// Get the component in the model
				final AadlPackageImpl aadlPkg = (AadlPackageImpl) resource.getContents().get(0);
				PackageSectionImpl pkgSection = (PackageSectionImpl) aadlPkg.getOwnedPublicSection();
				int idx = getIndex(componentName, pkgSection.getOwnedClassifiers());
				Classifier classifier = pkgSection.getOwnedClassifiers().get(idx);

				ThreadType threadType = (ThreadType) classifier;
				EList<AnnexSubclause> annexSubclauses = threadType.getOwnedAnnexSubclauses();
				String assumeStatement = "{**" + System.lineSeparator() + "\t\t\tassume " + reqID + " \"" + reqText
						+ "\" : FALSE;";
				String proveStatement = "{**" + System.lineSeparator() + "\t\t\tprove(" + reqName + "(this, \"" + reqID
						+ "\"))";

				Iterator<AnnexSubclause> annexSubclause = annexSubclauses.iterator();
				while (annexSubclause.hasNext()) {
					DefaultAnnexSubclauseImpl annexSubclauseImpl = (DefaultAnnexSubclauseImpl) annexSubclause.next();
					String sourceText = annexSubclauseImpl.getSourceText();

					if (annexSubclauseImpl.getName().equalsIgnoreCase("agree")) {
						// Add AGREE assume statement
						assumeStatement = sourceText.replace("{**", assumeStatement);
						// Delete annex subclause from owned subclauses. Will add it back later.
						// There must be a better way to get the formatting to display correctly.
						annexSubclause.remove();
					} else if (annexSubclauseImpl.getName().equalsIgnoreCase("resolute")) {
						// Add Resolute prove statement
						proveStatement = sourceText.replace("{**", proveStatement);
						// Delete annex subclause from owned subclauses. Will add it back later.
						// There must be a better way to get the formatting to display correctly.
						annexSubclause.remove();
					}
				}

				DefaultAnnexSubclauseImpl agreeSubclause = (DefaultAnnexSubclauseImpl) threadType
						.createOwnedAnnexSubclause();
				agreeSubclause.setName("agree");
				if (!assumeStatement.contains("**}")) {
					assumeStatement = assumeStatement + System.lineSeparator() + "\t\t**}";
				}
				agreeSubclause.setSourceText(assumeStatement);

				DefaultAnnexSubclauseImpl resoluteSubclause = (DefaultAnnexSubclauseImpl) threadType
						.createOwnedAnnexSubclause();
				resoluteSubclause.setName("resolute");
				if (!proveStatement.contains("**}")) {
					proveStatement = proveStatement + System.lineSeparator() + "\t\t**}";
				}
				resoluteSubclause.setSourceText(proveStatement);


				// Delete and re-insert this component from package section
				// This seems to be the only way to get the formatting (mostly) correct
				pkgSection.getOwnedClassifiers().remove(idx);
				pkgSection.getOwnedClassifiers().add(idx, classifier);
			}
		});
	}

	private void addClaims(String reqName, String reqID, String reqText) {

		// Add requirement
		CaseClaimsManager.getInstance().addFunctionDefinition(reqName, reqID, reqText);
	}

	/**
	 * Returns the index of a component with the specified name in the specified element list.
	 * @param compName - Component name
	 * @param elements - Collection of elements
	 * @return An identifier that is unique in the specified list
	 */
	private int getIndex(String compName, final Collection<? extends NamedElement> elements) {
		int idx = 0;

		for (NamedElement e : elements) {
			if (e.getName().equals(compName)) {
				break;
			} else {
				idx++;
			}
		}
		return idx;
	}

	private List<CASE_Requirement> getResoluteClauses() {

		XtextEditor xtextEditor = EditorUtils.getActiveXtextEditor();

		return xtextEditor.getDocument().readOnly(resource -> {


			List<CASE_Requirement> resoluteClauses = new ArrayList<>();

			// Get the components in the model
			final AadlPackage aadlPkg = (AadlPackageImpl) resource.getContents().get(0);
			PublicPackageSection pkgSection = aadlPkg.getOwnedPublicSection();
			for (Classifier classifier : pkgSection.getOwnedClassifiers()) {

				if (classifier instanceof ComponentType) {

					ComponentType compType = (ComponentType) classifier;
					final EList<AnnexSubclause> annexSubclauses = compType.getOwnedAnnexSubclauses();

					for (AnnexSubclause annexSubclause : annexSubclauses) {
						// See if there's a resolute annex
						if (annexSubclause.getName().equalsIgnoreCase("resolute")) {
							DefaultAnnexSubclause annexSubclauseImpl = (DefaultAnnexSubclause) annexSubclause;
							// See if there are any 'prove' clauses
							ResoluteSubclause resoluteClause = (ResoluteSubclause) annexSubclauseImpl
									.getParsedAnnexSubclause();
							EList<ProveStatement> proves = resoluteClause.getProves();
							for (ProveStatement prove : proves) {
								Expr expr = prove.getExpr();
								if (expr instanceof FnCallExpr) {
									FnCallExpr fnCall = (FnCallExpr) expr;
									if (fnCall.getFn().getName() != null
											&& !resoluteClauses.contains(fnCall.getFn().getName())) {
										resoluteClauses.add(new CASE_Requirement(fnCall.getFn().getName(), "", "",
												compType.getName(), ""));
									}
								}
							}
							break;
						}
					}
				}
			}

			return resoluteClauses;
		});
	}

}
