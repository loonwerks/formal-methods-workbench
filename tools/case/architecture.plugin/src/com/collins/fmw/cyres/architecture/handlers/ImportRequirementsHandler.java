package com.collins.fmw.cyres.architecture.handlers;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.PlatformUI;
import org.osate.ui.dialogs.Dialog;

import com.collins.fmw.cyres.architecture.dialogs.ImportRequirementsDialog;
import com.collins.fmw.cyres.architecture.requirements.CyberRequirement;
import com.collins.fmw.cyres.architecture.requirements.JsonRequirementsFile;
import com.collins.fmw.cyres.architecture.requirements.JsonRequirementsFile.JsonRequirement;
import com.collins.fmw.cyres.architecture.requirements.RequirementsManager;
import com.collins.fmw.cyres.util.plugin.TraverseProject;

public class ImportRequirementsHandler extends AbstractHandler {


	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		// Get the current project
		IProject project = TraverseProject.getCurrentProject();
		if (project == null) {
			Dialog.showError("Could not determine current project",
					"Unable to determine current project.  Open a project file in the editor.");
			return null;
		}

		// If a filename was passed to this command, open the file.
		// Otherwise, prompt the user for the file
		String filename = event.getParameter("filename");
		File reqFile = null;
		if (filename == null || filename.isEmpty()) {

			FileDialog dlgReqFile = new FileDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
			dlgReqFile.setText("Select requirements file to import.");

			filename = dlgReqFile.open();
			if (filename == null) {
				return null;
			}
			reqFile = new File(filename);
		}
		if (!reqFile.exists()) {
			Dialog.showError("File not found", "Cannot find the requirements file " + reqFile.getName() + ".");
			return null;
		}

		JsonRequirementsFile jsonFile = new JsonRequirementsFile();
		if (!jsonFile.importFile(reqFile)) {
			Dialog.showError("Problem with " + reqFile.getName(),
					"Could not load cyber requirements file " + reqFile.getName() + ".");
			return null;
		}
		RequirementsManager.getInstance().reset();
		// Compare new requirements with existing requirements and ignore the existing ones
		removeExistingRequirements(jsonFile, RequirementsManager.getInstance().getImportedRequirements());
		removeExistingRequirements(jsonFile, RequirementsManager.getInstance().getOmittedRequirements());

		// Alert user if there aren't any requirements to import
		if (jsonFile.getRequirements().isEmpty()) {
			Dialog.showError("No new requirements to import", reqFile.getName()
					+ " does not contain any requirements that are not already present in this model.");
			return null;
		}

		// Open wizard to enter filter info
		ImportRequirementsDialog wizard = new ImportRequirementsDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());

		wizard.setRequirements(jsonFile.getRequirements());
		wizard.create();
		if (wizard.open() == Window.OK) {

			List<CyberRequirement> importedReqs = wizard.getImportedRequirements();
			List<CyberRequirement> omittedReqs = wizard.getOmittedRequirements();

//			InsertMapping reqMap = new InsertMapping();
//			reqMap.setMainEditor(EditorUtils.getActiveXtextEditor());
//
//			// Insert selected requirements into model
//			for (CyberRequirement req : importedReqs) {
//				reqMap.insert(req);
//			}
//			reqMap.insertIntoFiles();

			RequirementsManager.getInstance().importRequirements(importedReqs);

			// Write omitted requirements to log
			if (!omittedReqs.isEmpty()) {
				RequirementsManager.getInstance().addOmittedRequirements(omittedReqs);
			}
		}

		return null;
	}


//	private List<CyberRequirement> getImportedRequirements() {
//
//		XtextEditor xtextEditor = EditorUtils.getActiveXtextEditor();
//
//		return xtextEditor.getDocument().readOnly(resource -> {
//
//			List<CyberRequirement> resoluteClauses = new ArrayList<>();
//
//			// Get all the packages in this project
//			IProject project = OsateResourceUtil.convertToIResource(resource).getProject();
//			for (AadlPackage aadlPkg : TraverseProject.getPackagesInProject(project)) {
//
//				// Get the components in the model
//				PublicPackageSection pkgSection = aadlPkg.getOwnedPublicSection();
//				for (Classifier classifier : pkgSection.getOwnedClassifiers()) {
//
//					for (AnnexSubclause annexSubclause : classifier.getOwnedAnnexSubclauses()) {
//						DefaultAnnexSubclause defaultSubclause = (DefaultAnnexSubclause) annexSubclause;
//						// See if there's a resolute annex
//						if (defaultSubclause.getParsedAnnexSubclause() instanceof ResoluteSubclause) {
//							ResoluteSubclause resoluteClause = (ResoluteSubclause) defaultSubclause
//									.getParsedAnnexSubclause();
//							// See if there are any 'prove' clauses
//							for (AnalysisStatement as : resoluteClause.getProves()) {
//								if (as instanceof ProveStatement) {
//									ProveStatement prove = (ProveStatement) as;
//									Expr expr = prove.getExpr();
//									if (expr instanceof FnCallExpr) {
//										FnCallExpr fnCall = (FnCallExpr) expr;
//										// TODO: Check if the function definition is in req package or priv8 section
////										FunctionDefinition fd = fnCall.getFn();
//										CyberRequirement req = new CyberRequirement(fnCall.getFn().getName(), "", "",
//												classifier, false, "");
//										if (fnCall.getFn().getName() != null && !resoluteClauses.contains(req)) {
//											resoluteClauses.add(req);
//										}
//									}
//								}
//							}
//							break;
//						}
//					}
//				}
//			}
//
//			return resoluteClauses;
//		});
//	}
//
//	private List<CyberRequirement> getOmittedRequirements() {
//		// TODO: Get existing omitted requirements from omission log
//		// for now returning an empty list
//		return Collections.emptyList();
//	}

//	private void writeOmissionLog(List<CyberRequirement> omittedReqs) {
//		// TODO: Write omitted requirements to log
//	}

	/**
	 * Removes requirements from jsonFile if they appear in reqList
	 * @param jsonFile
	 * @param reqList
	 */
	private void removeExistingRequirements(JsonRequirementsFile jsonFile, final List<CyberRequirement> reqList) {
		Iterator<JsonRequirement> i = jsonFile.getRequirements().iterator();
//		Iterator<CyberRequirement> i = jsonFile.getRequirements().iterator();
		while (i.hasNext()) {
			JsonRequirement jsonReq = i.next();
//			CyberRequirement jsonReq = i.next();
			for (CyberRequirement req : reqList) {
				if (req.getType().equalsIgnoreCase(jsonReq.getType())
						&& req.getContext().equalsIgnoreCase(jsonReq.getContext())) {
					i.remove();
					break;
				}
			}
		}
	}

}
