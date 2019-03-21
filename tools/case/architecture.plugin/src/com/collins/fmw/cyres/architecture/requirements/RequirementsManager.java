package com.collins.fmw.cyres.architecture.requirements;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
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
import org.osate.aadl2.DefaultAnnexSubclause;
import org.osate.aadl2.PrivatePackageSection;
import org.osate.aadl2.PublicPackageSection;
import org.osate.aadl2.modelsupport.util.AadlUtil;

import com.collins.fmw.cyres.util.plugin.TraverseProject;
import com.rockwellcollins.atc.agree.agree.AgreeContract;
import com.rockwellcollins.atc.agree.agree.AgreeContractSubclause;
import com.rockwellcollins.atc.agree.agree.NamedSpecStatement;
import com.rockwellcollins.atc.agree.agree.SpecStatement;
import com.rockwellcollins.atc.resolute.resolute.AnalysisStatement;
import com.rockwellcollins.atc.resolute.resolute.ClaimBody;
import com.rockwellcollins.atc.resolute.resolute.ClaimString;
import com.rockwellcollins.atc.resolute.resolute.ClaimText;
import com.rockwellcollins.atc.resolute.resolute.DefinitionBody;
import com.rockwellcollins.atc.resolute.resolute.Expr;
import com.rockwellcollins.atc.resolute.resolute.FnCallExpr;
import com.rockwellcollins.atc.resolute.resolute.FunctionDefinition;
import com.rockwellcollins.atc.resolute.resolute.ProveStatement;
import com.rockwellcollins.atc.resolute.resolute.ResoluteSubclause;

/**
 *  Manages insertion, deletion, and modification of logical CASE requirements/claims in the model
 */
public class RequirementsManager {

	private static IProject currentProject = null;
	private static List<CyberRequirement> importedRequirements = new ArrayList<>();
	private static List<CyberRequirement> omittedRequirements = new ArrayList<>();
//	private static Map<IFile, List<CyberRequirement>> fileMap = new HashMap<>();
	private static XtextEditor mainEditor = null;

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

	private static void initRequirements() {
		// Initialize requirements list
		importedRequirements = new ArrayList<>();
		omittedRequirements = new ArrayList<>();
		// Set main editor
		if (mainEditor == null) {
			// TODO: What if nothing is open?
			mainEditor = EditorUtils.getActiveXtextEditor();
		}
		if (currentProject == null) {
			currentProject = TraverseProject.getCurrentProject();
		}
		// Read in any existing imported requirements
		findImportedRequirements();

		// Read in any existing omitted requirements
		readOmittedRequirements();

	}

//	/**
//	 * Reads in the cyber requirements that have already been imported into this model.
//	 * These requirements are stored in json format in the projects "requirements" folder.
//	 * A corresponding "prove" claim call should be present in the model, as well as the claim
//	 * call definition.
//	 */
//	private static void readImportedRequirements() {
//		JsonRequirementsFile jsonFile = new JsonRequirementsFile();
//		if (!jsonFile.importFile(new File(IMPORTED_REQUIREMENTS_FILE))) {
//			return;
//		}
//		importedRequirements = jsonFile.getRequirements();
//
//		// Figure out the classifier for each requirement context
//		for (CyberRequirement req : importedRequirements) {
//			if (!req.setContextClassifier()) {
//				// TODO: Alert user that classifier could not be found in model, ask to remove it
//			}
//		}
//
//		// TODO: Make sure each requirement is in the model. Otherwise, alert user.
//
//
//	}

	private static void findImportedRequirements() {

		XtextEditor xtextEditor = EditorUtils.getActiveXtextEditor();

		importedRequirements = xtextEditor.getDocument().readOnly(resource -> {

			List<CyberRequirement> resoluteClauses = new ArrayList<>();

			// Get all the packages in this project
//			IProject project = OsateResourceUtil.convertToIResource(resource).getProject();
			for (AadlPackage aadlPkg : TraverseProject.getPackagesInProject(currentProject)) {

				// Get the components in the model
				PublicPackageSection pkgSection = aadlPkg.getOwnedPublicSection();
				for (Classifier classifier : pkgSection.getOwnedClassifiers()) {

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
										// Check if the function definition is in priv8 section
										FunctionDefinition fd = fnCall.getFn();
										if (AadlUtil.getContainingPackageSection(fd) instanceof PrivatePackageSection
												&& AadlUtil.getContainingPackage(fd) == aadlPkg) {
											DefinitionBody db = fd.getBody();
											String reqType = "";
											String reqText = "";
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
												// Extract the Requirement type from the text.
												reqType = reqText.substring(1, reqText.indexOf("]") - 2);
											}
											// See if there is an agree statement with this requirement id
											boolean agree = false;
											for (AnnexSubclause agreeAnnexSubclause : classifier
													.getOwnedAnnexSubclauses()) {
												DefaultAnnexSubclause defaultAgreeSubclause = (DefaultAnnexSubclause) agreeAnnexSubclause;
												// See if there's an agree annex
												if (defaultAgreeSubclause
														.getParsedAnnexSubclause() instanceof AgreeContractSubclause) {
													AgreeContractSubclause agreeSubclause = (AgreeContractSubclause) defaultAgreeSubclause
															.getParsedAnnexSubclause();
													AgreeContract contract = (AgreeContract) agreeSubclause
															.getContract();
													for (SpecStatement ss : contract.getSpecs()) {
														if (ss instanceof NamedSpecStatement) {
															NamedSpecStatement nss = (NamedSpecStatement) ss;
															if (nss.getName().equalsIgnoreCase(fd.getName())) {
																agree = true;
																break;
															}
														}
													}
													break;
												}
											}
											CyberRequirement req = new CyberRequirement(reqType, fd.getName(), reqText,
													classifier, agree, "");
											// Set claimDefinition and claimCall for requirement
											req.setClaimCall(prove);
											req.setClaimDefinition(fd);
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

//	public void setMainEditor(XtextEditor xtextEditor) {
//		RequirementsManager.mainEditor = xtextEditor;
//	}

	public void addImportedRequirements(List<CyberRequirement> reqs) {
		for (CyberRequirement req : reqs) {
//			IFile file = req.getContainingFile();
//			List<CyberRequirement> mappedReqs = new ArrayList<>();
//			if (fileMap.containsKey(key)) {
//				mappedReqs = fileMap.get(key);
//			}
//			mappedReqs.add(req);
//			fileMap.put(key, mappedReqs);

			// Insert the requirement into the model
			insertIntoModel(req);

			// Add the requirement to the imported requirements list
			importedRequirements.add(req);
		}

	}

//	public void addImportedRequirement(CyberRequirement req) {
//		IFile key = req.getContainingFile();
//		List<CyberRequirement> reqs = new ArrayList<>();
//		if (fileMap.containsKey(key)) {
//			reqs = fileMap.get(key);
//		}
//		reqs.add(req);
//		fileMap.put(key, reqs);
//		importedRequirements.add(req);
//	}
//
//	public void insertIntoFiles() {
//		for (IFile f : fileMap.keySet()) {
//			insertIntoFile(f);
//		}
//	}

	private void insertIntoModel(CyberRequirement req) {
		// Get the file to insert into
		IFile file = req.getContainingFile();

		XtextEditor editor = getEditor(file);

		if (editor != null) {
			editor.getDocument().modify(resource -> {
				req.insert(resource);
				return null;
			});

		}

		closeEditor(editor, true);

	}

//	private void insertIntoFile(IFile f) {
//		List<CyberRequirement> toInsert = fileMap.get(f);
//
//		XtextEditor editor = getEditor(f);
//
//		if (editor != null) {
//			editor.getDocument().modify(resource -> {
//				for (CyberRequirement r : toInsert) {
//					r.insert(resource);
//				}
//				return null;
//			});
//
//		}
//
//		closeEditor(editor, true);
//	}

	public void addOmittedRequirements(List<CyberRequirement> omittedReqs) {
		omittedRequirements.addAll(omittedReqs);
		// TODO: Write to file

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

		if (editor.equals(RequirementsManager.mainEditor)) {
			return;
		} else {
			page.closeEditor(editor, false);
		}
	}

}
