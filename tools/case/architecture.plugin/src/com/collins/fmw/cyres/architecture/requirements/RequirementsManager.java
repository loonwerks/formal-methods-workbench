package com.collins.fmw.cyres.architecture.requirements;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.utils.EditorUtils;
import org.osate.aadl2.AadlPackage;
import org.osate.aadl2.AnnexSubclause;
import org.osate.aadl2.Classifier;
import org.osate.aadl2.ComponentImplementation;
import org.osate.aadl2.DefaultAnnexSubclause;
import org.osate.aadl2.ModelUnit;
import org.osate.aadl2.PrivatePackageSection;
import org.osate.aadl2.PublicPackageSection;
import org.osate.aadl2.Subcomponent;
import org.osate.aadl2.modelsupport.util.AadlUtil;

import com.collins.fmw.cyres.util.plugin.TraverseProject;
import com.rockwellcollins.atc.resolute.resolute.AnalysisStatement;
import com.rockwellcollins.atc.resolute.resolute.Arg;
import com.rockwellcollins.atc.resolute.resolute.ClaimBody;
import com.rockwellcollins.atc.resolute.resolute.ClaimString;
import com.rockwellcollins.atc.resolute.resolute.ClaimText;
import com.rockwellcollins.atc.resolute.resolute.DefinitionBody;
import com.rockwellcollins.atc.resolute.resolute.Expr;
import com.rockwellcollins.atc.resolute.resolute.FnCallExpr;
import com.rockwellcollins.atc.resolute.resolute.FunctionDefinition;
import com.rockwellcollins.atc.resolute.resolute.NestedDotID;
import com.rockwellcollins.atc.resolute.resolute.ProveStatement;
import com.rockwellcollins.atc.resolute.resolute.ResoluteSubclause;
import com.rockwellcollins.atc.resolute.resolute.ThisExpr;

/**
 *  Manages insertion, deletion, and modification of logical CASE requirements/claims in the model
 */
public class RequirementsManager {

	private static IProject currentProject = null;
	private static List<CyberRequirement> importedRequirements = new ArrayList<>();
	private static List<CyberRequirement> omittedRequirements = new ArrayList<>();

	// Singleton instance
	private static RequirementsManager instance = null;

	private RequirementsManager() {

	}

	public static RequirementsManager getInstance() {

		if (instance == null || currentProject == null) {
			initRequirements();
			instance = new RequirementsManager();
		}

		return instance;
	}

	public void reset() {
		currentProject = null;
		importedRequirements = new ArrayList<>();
		omittedRequirements = new ArrayList<>();
	}

	private static void initRequirements() {
		// Initialize requirements list
		importedRequirements = new ArrayList<>();
		omittedRequirements = new ArrayList<>();

		if (currentProject == null) {
			currentProject = TraverseProject.getCurrentProject();
		}
		// Read in any existing imported requirements
		findImportedRequirements();

		// Read in any existing omitted requirements
		readOmittedRequirements();

	}

//	private static void findImportedRequirements() {
//
//		XtextEditor xtextEditor = EditorUtils.getActiveXtextEditor();
//
//		importedRequirements = xtextEditor.getDocument().readOnly(resource -> {
//
//			List<CyberRequirement> resoluteClauses = new ArrayList<>();
//
//			// Get all the packages in this project
//			ResourceSet rs = resource.getResourceSet();
//			for (Resource r : rs.getResources()) {
//
//				// Get the components in the model
//				ModelUnit modelUnit = (ModelUnit) r.getContents().get(0);
//				if (modelUnit instanceof AadlPackage) {
//					AadlPackage aadlPkg = (AadlPackage) modelUnit;
//					PublicPackageSection pkgSection = aadlPkg.getOwnedPublicSection();
//					for (Classifier classifier : pkgSection.getOwnedClassifiers()) {
//
//						for (AnnexSubclause annexSubclause : classifier.getOwnedAnnexSubclauses()) {
//							DefaultAnnexSubclause defaultSubclause = (DefaultAnnexSubclause) annexSubclause;
//							// See if there's a resolute annex
//							if (defaultSubclause.getParsedAnnexSubclause() instanceof ResoluteSubclause) {
//								ResoluteSubclause resoluteClause = (ResoluteSubclause) defaultSubclause
//										.getParsedAnnexSubclause();
//								// See if there are any 'prove' clauses
//								for (AnalysisStatement as : resoluteClause.getProves()) {
//									if (as instanceof ProveStatement) {
//										ProveStatement prove = (ProveStatement) as;
//										Expr expr = prove.getExpr();
//										if (expr instanceof FnCallExpr) {
//											FnCallExpr fnCall = (FnCallExpr) expr;
//											// Check if the corresponding function definition is in private section
//											FunctionDefinition fd = fnCall.getFn();
//											if (AadlUtil
//													.getContainingPackageSection(fd) instanceof PrivatePackageSection
//													&& AadlUtil.getContainingPackage(fd) == aadlPkg) {
//												String reqType = "";
//												String reqText = "";
//												DefinitionBody db = fd.getBody();
//												if (db instanceof ClaimBody) {
//													ClaimBody cb = (ClaimBody) db;
//													for (ClaimText ct : cb.getClaim()) {
//														// Assumption that claim text is only a string
//														if (ct instanceof ClaimString) {
//															ClaimString cs = (ClaimString) ct;
//															reqText += cs.getStr();
//														}
//													}
//												}
//												if (reqText.matches("^\\[.+\\]\\s.+")) {
//													// Extract the Requirement type from the text
//													reqType = reqText.substring(1, reqText.indexOf("]"));
//													reqText = reqText.substring(reqText.indexOf("]") + 1).trim();
//												}
//												// See if there is an agree statement with this requirement id
//												boolean agree = false;
//
//												for (AnnexSubclause agreeAnnexSubclause : classifier
//														.getOwnedAnnexSubclauses()) {
//													DefaultAnnexSubclause defaultAgreeSubclause = (DefaultAnnexSubclause) agreeAnnexSubclause;
//													// See if there's an agree annex
//													if (defaultAgreeSubclause
//															.getParsedAnnexSubclause() instanceof AgreeContractSubclause) {
//														AgreeContractSubclause agreeSubclause = (AgreeContractSubclause) defaultAgreeSubclause
//																.getParsedAnnexSubclause();
//														AgreeContract contract = (AgreeContract) agreeSubclause
//																.getContract();
//														for (SpecStatement ss : contract.getSpecs()) {
//															if (ss instanceof NamedSpecStatement) {
//																NamedSpecStatement nss = (NamedSpecStatement) ss;
//																if (nss.getName().equalsIgnoreCase(fd.getName())) {
//																	agree = true;
//																	break;
//																}
//															}
//														}
//														break;
//													}
//												}
////												CyberRequirement req = new CyberRequirement(reqType, fd.getName(),
////														reqText, classifier, agree, "");
//												CyberRequirement req = new CyberRequirement(reqType, fd.getName(),
//														reqText, classifier.getQualifiedName(), agree, "");
//
//												if (fd.getName() != null && !resoluteClauses.contains(req)) {
//													resoluteClauses.add(req);
//												}
//											}
//										}
//									}
//								}
//								break;
//							}
//						}
//					}
//				}
//			}
//
//			return resoluteClauses;
//		});
//	}

	private static void findImportedRequirements() {

		XtextEditor xtextEditor = EditorUtils.getActiveXtextEditor();

		importedRequirements = xtextEditor.getDocument().readOnly(resource -> {

			List<CyberRequirement> resoluteClauses = new ArrayList<>();

			// Get all the packages in this project
			ResourceSet rs = resource.getResourceSet();
			for (Resource r : rs.getResources()) {

				// Get the components in the model
				ModelUnit modelUnit = (ModelUnit) r.getContents().get(0);
				if (modelUnit instanceof AadlPackage) {
					AadlPackage aadlPkg = (AadlPackage) modelUnit;

					PublicPackageSection pkgSection = aadlPkg.getOwnedPublicSection();
					for (Classifier classifier : pkgSection.getOwnedClassifiers()) {

						if (classifier instanceof ComponentImplementation) {
							for (AnnexSubclause annexSubclause : classifier.getOwnedAnnexSubclauses()) {
								DefaultAnnexSubclause defaultSubclause = (DefaultAnnexSubclause) annexSubclause;
								// See if there's a resolute annex
								if (defaultSubclause.getParsedAnnexSubclause() instanceof ResoluteSubclause) {
									ResoluteSubclause resoluteClause = (ResoluteSubclause) defaultSubclause
											.getParsedAnnexSubclause();
									// See if there are any 'prove' clauses
									for (AnalysisStatement as : resoluteClause.getProves()) {
										if (as instanceof ProveStatement) {
											ProveStatement prove = (ProveStatement) as;
											Expr expr = prove.getExpr();
											if (expr instanceof FnCallExpr) {
												FnCallExpr fnCall = (FnCallExpr) expr;
												// Check if the corresponding function definition is in private section
												FunctionDefinition fd = fnCall.getFn();
												if (AadlUtil.getContainingPackageSection(
														fd) instanceof PrivatePackageSection
														&& AadlUtil.getContainingPackage(fd) == aadlPkg) {
													String reqType = "";
													String reqText = "";
													DefinitionBody db = fd.getBody();
													if (db instanceof ClaimBody) {
														ClaimBody cb = (ClaimBody) db;
														for (ClaimText ct : cb.getClaim()) {
															// Assumption that claim text is only a string
															if (ct instanceof ClaimString) {
																ClaimString cs = (ClaimString) ct;
																reqText += cs.getStr();
															}
														}
													}
													if (reqText.matches("^\\[.+\\]\\s.+")) {
														// Extract the Requirement type from the text
														reqType = reqText.substring(1, reqText.indexOf("]"));
														reqText = reqText.substring(reqText.indexOf("]") + 1).trim();
													}

													// Get the context from the context parameter (it will always be the first param)
													String context = "";
													Expr argExpr = fnCall.getArgs().get(0);
													// The context should always be "this" or "this.<subcomponent>"
													if (argExpr instanceof ThisExpr) {

														NestedDotID subExpr = ((ThisExpr) argExpr).getSub();
														if (subExpr != null) {
															while (subExpr.getSub() != null) {
																subExpr = subExpr.getSub();
															}
														}

														if (subExpr.getBase() instanceof Subcomponent) {
															context = ((Subcomponent) subExpr.getBase())
																	.getQualifiedName();
														} else {
															context = classifier.getQualifiedName();
														}

													}

													// See if there is an agree statement with this requirement id
													// If there's a property_id parameter then there is
													boolean agree = false;
													for (Arg arg : fd.getArgs()) {
														if (arg.getName().equalsIgnoreCase("property_id")) {
															agree = true;
															break;
														}
													}

													CyberRequirement req = new CyberRequirement(reqType, fd.getName(),
															reqText, context, agree, "");

													if (fd.getName() != null && !resoluteClauses.contains(req)) {
														resoluteClauses.add(req);
													}
												}
											}
										}
									}
									break;
								}
							}
						}
					}
				}
			}

			return resoluteClauses;
		});
	}

	private static void readOmittedRequirements() {
		// TODO: Read in omitted requirements file

	}

	public List<CyberRequirement> getImportedRequirements() {
		return importedRequirements;
	}

	public List<CyberRequirement> getOmittedRequirements() {
		return omittedRequirements;
	}

	public CyberRequirement getRequirement(String requirementId) {
		for (CyberRequirement req : importedRequirements) {
			if (req.getId().equalsIgnoreCase(requirementId)) {
				return req;
			}
		}
		return null;
	}

	public void importRequirements(List<CyberRequirement> reqs) {
		for (CyberRequirement req : reqs) {
			importRequirement(req);
		}
	}

	public void importRequirement(CyberRequirement req) {

		// Get the file to insert into
		IFile file = req.getContainingFile();
		XtextEditor editor = getEditor(file);

		if (editor != null) {
			editor.getDocument().modify(resource -> {

//				req.insertClaim(new BaseClaim(req.getId(), req.getType(), req.getText()), resource);
				req.insertClaim(new BaseClaim(req), resource);

				// Add AGREE, if necessary
				if (req.hasAgree()) {
					formalizeRequirement(req, resource);
				}

				return null;
			});
		}

		// Close editor, if necessary
		closeEditor(editor, true);

		// Add the requirement to the imported requirements list
		importedRequirements.add(req);
	}

	public void modifyRequirement(String reqId, Resource resource, BuiltInClaim claim) {

		for (CyberRequirement req : importedRequirements) {
			if (req.getId().equalsIgnoreCase(reqId)) {
				req.insertClaim(claim, resource);
				break;
			}
		}
	}

	public boolean formalizeRequirement(String reqId, Resource resource) {
		for (CyberRequirement req : importedRequirements) {
			if (req.getId().equalsIgnoreCase(reqId)) {
				req.setAgree();
				formalizeRequirement(req, resource);
				return true;
			}
		}
		return false;
	}

	private void formalizeRequirement(CyberRequirement req, Resource resource) {
		req.insertClaim(new AgreePropCheckedClaim(req.getId(), req.getContext()), resource);
		req.insertAgree(resource);
	}

	public void addOmittedRequirements(List<CyberRequirement> omittedReqs, String implementation) {
		omittedRequirements.addAll(omittedReqs);
		// TODO: Write to file
		JsonRequirementsFile jsonFile = new JsonRequirementsFile("StairCASE", System.currentTimeMillis(),
				implementation, "", omittedReqs);
//		jsonFile.exportFile(file);
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
