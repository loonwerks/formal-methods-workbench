package com.collins.fmw.cyres.architecture.requirements;

import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.utils.EditorUtils;
import org.osate.aadl2.Aadl2Package;
import org.osate.aadl2.AadlPackage;
import org.osate.aadl2.AnnexLibrary;
import org.osate.aadl2.AnnexSubclause;
import org.osate.aadl2.Classifier;
import org.osate.aadl2.DefaultAnnexLibrary;
import org.osate.aadl2.DefaultAnnexSubclause;
import org.osate.aadl2.PrivatePackageSection;
import org.osate.aadl2.PublicPackageSection;
import org.osate.aadl2.modelsupport.util.AadlUtil;

import com.collins.fmw.cyres.architecture.utils.CaseUtils;
import com.rockwellcollins.atc.resolute.resolute.AnalysisStatement;
import com.rockwellcollins.atc.resolute.resolute.Definition;
import com.rockwellcollins.atc.resolute.resolute.Expr;
import com.rockwellcollins.atc.resolute.resolute.FnCallExpr;
import com.rockwellcollins.atc.resolute.resolute.FunctionDefinition;
import com.rockwellcollins.atc.resolute.resolute.ProveStatement;
import com.rockwellcollins.atc.resolute.resolute.ResoluteFactory;
import com.rockwellcollins.atc.resolute.resolute.ResoluteLibrary;
import com.rockwellcollins.atc.resolute.resolute.ResoluteSubclause;

public abstract class BuiltInClaim {

	protected AadlPackage casePkg = null;
	public final String claim;

	protected FunctionDefinition claimDefinition = null;
	protected ProveStatement claimCall = null;
	protected CyberRequirement req = null;
	private Classifier modificationContext = null;

	public BuiltInClaim(String claim) {
		this.claim = claim;
	}

	public abstract FunctionDefinition buildClaimDefinition();

	public abstract ProveStatement buildClaimCall();


	public void insert(CyberRequirement req) {

		this.req = req;

		// Get the file to insert into
		IFile file = req.getContainingFile();
		XtextEditor editor = getEditor(file);

		if (editor != null) {
			editor.getDocument().modify(resource -> {

				// Get modification context
				TreeIterator<EObject> x = EcoreUtil.getAllContents(resource, true);
				while (x.hasNext()) {
					EObject next = x.next();
					if (next instanceof Classifier) {
						Classifier nextClass = (Classifier) next;
						if (nextClass.getQualifiedName().equalsIgnoreCase(req.getContext())) {
							this.modificationContext = nextClass;
							break;
						}
					}
				}
				if (this.modificationContext == null) {
					throw new RuntimeException("Unable to determine requirement context.");
				}

				// Insert CASE package import
				if (this.claim != null) {
					this.casePkg = importCasePackage();
					if (this.casePkg == null) {
						throw new RuntimeException(
								"Unable to locate " + CaseUtils.CASE_MODEL_TRANSFORMATIONS_NAME + " package.");
					}
				}

				// Build Claim Definition
				this.claimDefinition = buildClaimDefinition();

				// Insert it into model
				insertClaimDefinition();

				// Build Claim Call
				if (this.claimDefinition != null) {
					this.claimCall = buildClaimCall();

					// Insert it into model
					insertClaimCall();

				}

				return null;
			});

			closeEditor(editor, true);
		}
	}

	protected void insertClaimDefinition() {

		if (this.modificationContext == null) {
			return;
		}

//		AadlPackage pkg = EcoreUtil2.getContainerOfType(this.modificationContext, AadlPackage.class);
		AadlPackage pkg = AadlUtil.getContainingPackage(this.modificationContext);
		if (pkg == null) {
			throw new RuntimeException("Could not find containing package for " + this.modificationContext);
		}

		PrivatePackageSection priv8 = pkg.getOwnedPrivateSection();
		if (priv8 == null) {
			priv8 = pkg.createOwnedPrivateSection();
		}

		DefaultAnnexLibrary defResLib = null;
		ResoluteLibrary resLib = null;
		for (AnnexLibrary library : priv8.getOwnedAnnexLibraries()) {
			if (library instanceof DefaultAnnexLibrary && library.getName().equalsIgnoreCase("resolute")) {
				defResLib = (DefaultAnnexLibrary) library;
				resLib = EcoreUtil.copy((ResoluteLibrary) defResLib.getParsedAnnexLibrary());
				break;
			}
		}

		if (defResLib == null) {
			defResLib = (DefaultAnnexLibrary) priv8
					.createOwnedAnnexLibrary(Aadl2Package.eINSTANCE.getDefaultAnnexLibrary());
			defResLib.setName("resolute");
			defResLib.setSourceText("{** **}");

			resLib = ResoluteFactory.eINSTANCE.createResoluteLibrary();
		}

		// If this function definition already exists, remove it
		Iterator<Definition> i = resLib.getDefinitions().iterator();
		while (i.hasNext()) {
			Definition def = i.next();
			if (def.getName().equalsIgnoreCase(this.claimDefinition.getName())) {
				i.remove();
				break;
			}
		}

		resLib.getDefinitions().add(this.claimDefinition);
		defResLib.setParsedAnnexLibrary(resLib);

	}

	protected void insertClaimCall() {

		if (this.modificationContext == null) {
			return;
		}

		DefaultAnnexSubclause subclause = null;
		ResoluteSubclause resclause = null;
		for (AnnexSubclause sc : this.modificationContext.getOwnedAnnexSubclauses()) {
			if (sc instanceof DefaultAnnexSubclause && sc.getName().equalsIgnoreCase("resolute")) {
				subclause = (DefaultAnnexSubclause) sc;
				resclause = EcoreUtil.copy((ResoluteSubclause) subclause.getParsedAnnexSubclause());
				break;
			}
		}

		if (subclause == null) {
			subclause = (DefaultAnnexSubclause) this.modificationContext
					.createOwnedAnnexSubclause(Aadl2Package.eINSTANCE.getDefaultAnnexSubclause());
			subclause.setName("resolute");
			subclause.setSourceText("{** **}");

			resclause = ResoluteFactory.eINSTANCE.createResoluteSubclause();
		}

		// If the prove statement already exists, remove it
		Iterator<AnalysisStatement> i = resclause.getProves().iterator();
		while (i.hasNext()) {
			AnalysisStatement as = i.next();
			if (as instanceof ProveStatement) {
				ProveStatement prove = (ProveStatement) as;
				Expr expr = prove.getExpr();
				if (expr instanceof FnCallExpr) {
					FnCallExpr fnCallExpr = (FnCallExpr) expr;
					if (fnCallExpr.getFn().getName().equalsIgnoreCase(this.claimDefinition.getName())) {
						i.remove();
						break;
					}
				}
			}
		}

		resclause.getProves().add(this.claimCall);
		subclause.setParsedAnnexSubclause(resclause);
	}


	private AadlPackage importCasePackage() {

		if (this.modificationContext == null) {
			return null;
		}

//		AadlPackage contextPkg = EcoreUtil2.getContainerOfType(this.modificationContext, AadlPackage.class);
		AadlPackage contextPkg = AadlUtil.getContainingPackage(this.modificationContext);
		if (contextPkg == null) {
			throw new RuntimeException("Could not find containing package for " + this.modificationContext);
		}

		PrivatePackageSection priv8 = contextPkg.getOwnedPrivateSection();
		if (priv8 == null) {
			priv8 = contextPkg.createOwnedPrivateSection();
		}

		AadlPackage pkg = null;
		try {
			pkg = CaseUtils.addCaseModelTransformationsImport(priv8, false);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}

		return pkg;

	}

	protected FunctionDefinition getRequirementClaimDefinition() {
		if (this.modificationContext == null) {
			return null;
		}
		for (AnnexSubclause annexSubclause : this.modificationContext.getOwnedAnnexSubclauses()) {
			DefaultAnnexSubclause defaultSubclause = (DefaultAnnexSubclause) annexSubclause;
			// See if there's a resolute annex
			if (defaultSubclause.getParsedAnnexSubclause() instanceof ResoluteSubclause) {
				ResoluteSubclause resoluteClause = (ResoluteSubclause) defaultSubclause.getParsedAnnexSubclause();
				// See if there are any 'prove' clauses
				for (AnalysisStatement as : resoluteClause.getProves()) {
					if (as instanceof ProveStatement) {
						ProveStatement prove = (ProveStatement) as;
						Expr expr = prove.getExpr();
						if (expr instanceof FnCallExpr) {
							FnCallExpr fnCall = (FnCallExpr) expr;
							FunctionDefinition fd = fnCall.getFn();
							if (fd.getName().equalsIgnoreCase(req.getId())) {
								return fd;
							}
						}
					}
				}
				break;
			}
		}
		return null;
	}

	protected ProveStatement getRequirementClaimCall() {
		if (this.modificationContext == null) {
			return null;
		}
		for (AnnexSubclause annexSubclause : this.modificationContext.getOwnedAnnexSubclauses()) {
			DefaultAnnexSubclause defaultSubclause = (DefaultAnnexSubclause) annexSubclause;
			// See if there's a resolute annex
			if (defaultSubclause.getParsedAnnexSubclause() instanceof ResoluteSubclause) {
				ResoluteSubclause resoluteClause = (ResoluteSubclause) defaultSubclause.getParsedAnnexSubclause();
				// See if there are any 'prove' clauses
				for (AnalysisStatement as : resoluteClause.getProves()) {
					if (as instanceof ProveStatement) {
						ProveStatement prove = (ProveStatement) as;
						Expr expr = prove.getExpr();
						if (expr instanceof FnCallExpr) {
							FnCallExpr fnCall = (FnCallExpr) expr;
							FunctionDefinition fd = fnCall.getFn();
							if (fd.getName().equalsIgnoreCase(req.getId())) {
								return prove;
							}
						}
					}
				}
				break;
			}
		}
		return null;
	}

	protected FunctionDefinition getBuiltInClaimDefinition() {
		if (this.casePkg == null || this.claim == null) {
			return null;
		}
		PublicPackageSection publicSection = casePkg.getOwnedPublicSection();
		for (AnnexLibrary annexLibrary : publicSection.getOwnedAnnexLibraries()) {
			DefaultAnnexLibrary defaultLib = (DefaultAnnexLibrary) annexLibrary;
			if (defaultLib.getParsedAnnexLibrary() instanceof ResoluteLibrary) {
				ResoluteLibrary resoluteLib = (ResoluteLibrary) defaultLib.getParsedAnnexLibrary();
				for (Definition def : resoluteLib.getDefinitions()) {
					if (def instanceof FunctionDefinition) {
						FunctionDefinition fd = (FunctionDefinition) def;
						if (fd.getName().equalsIgnoreCase(this.claim)) {
							return fd;
						}
					}
				}
				break;
			}
		}
		return null;
	}

	private XtextEditor getEditor(IFile file) {
		IWorkbenchPage page = null;
		IEditorPart part = null;

		if (file.exists()) {
			page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			IEditorDescriptor desc = PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor(file.getName());
			try {
				part = page.openEditor(new FileEditorInput(file), desc.getId());
			} catch (PartInitException e) {
				e.printStackTrace();
			}
		}

		if (part == null) {
			return null;
		}

		XtextEditor xedit = null;
		xedit = (XtextEditor) part;

		return xedit;
	}

	private void closeEditor(XtextEditor editor, boolean save) {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if (save) {
			page.saveEditor(editor, false);
		}

		if (editor.equals(EditorUtils.getActiveXtextEditor())) {
			return;
		} else {
			page.closeEditor(editor, false);
		}
	}

}
