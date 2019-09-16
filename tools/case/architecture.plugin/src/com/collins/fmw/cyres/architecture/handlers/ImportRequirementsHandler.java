package com.collins.fmw.cyres.architecture.handlers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.PlatformUI;
import org.osate.ui.dialogs.Dialog;

import com.collins.fmw.cyres.architecture.dialogs.ImportRequirementsGUI;
import com.collins.fmw.cyres.architecture.requirements.CyberRequirement;
import com.collins.fmw.cyres.architecture.requirements.JsonRequirementsFile;
import com.collins.fmw.cyres.architecture.requirements.RequirementsDatabase;
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


		/*
		 * Steps for requirements manager:
		 * (1) Accept requirement input files from user.
		 * (2) Filter requirement input files: select those that match the hashcode of the AADL model.
		 * (3) Remove from the requirements database all "ToDo" requirements that do not match the hashcode of the current AADL model.
		 * (4) Read requirements from the filtered files and add them to the requirements database.
		 * (5) Collect the list of requirements from the current AADL model.
		 * (6) Display to the user the requirements database and from the AADL model.
		 * (7) User can reclassify these requirements with the following restrictions:
		 * (7a) "Omit" with old hashcode: cannot be modified.
		 * (7b) "ToDo" to "Add" or "Add+Agree": add requirement to the AADL model.
		 * (7c) "ToDo" to "Omit": add requirement to the requirements database as an omitted requirement.
		 * (7d) "Add" or "Add+Agree" to "Omit": remove from AADL model and add to requirements database as omitted requirement (note hashcode of the model).
		 * (7e) "Add" or "Add+Agree" to "ToDo": not allowed.
		 * (7f) "Add+Agree" to "Add": not allowed.
		 * (8) "ToDo" and "Omit" requirements stay in the requirements database, "Add" and "Add+Agree" go into the AADL model.
		 */

		RequirementsManager reqMgr = RequirementsManager.getInstance();
		reqMgr.reset();
		List<CyberRequirement> existingReqs = reqMgr.getImportedRequirements();

		RequirementsDatabase reqDb = RequirementsDatabase.getInstance();
		List<JsonRequirementsFile> jsonReqFiles = readInputFiles(event);
		if (!jsonReqFiles.isEmpty()) {
			reqDb.reset();
			reqDb.importJsonRequrementsFiles(jsonReqFiles);
		}
		reqDb.importRequirements(existingReqs);

		/*
		 * // Open wizard to enter filter info
		 * ImportRequirementsDialog wizard = new ImportRequirementsDialog(
		 * PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		 *
		 * wizard.setRequirements(jsonFile.getRequirements());
		 * wizard.create();
		 * if (wizard.open() == Window.OK) {
		 *
		 * List<CyberRequirement> importedReqs = wizard.getImportedRequirements();
		 * List<CyberRequirement> omittedReqs = wizard.getOmittedRequirements();
		 *
		 * RequirementsManager.getInstance().importRequirements(importedReqs);
		 *
		 * // Write omitted requirements to log
		 * if (!omittedReqs.isEmpty()) {
		 * RequirementsManager.getInstance().addOmittedRequirements(omittedReqs, jsonFile.getImplementation());
		 * }
		 * }
		 */

		ImportRequirementsGUI wizard = new ImportRequirementsGUI(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		wizard.setRequirements(reqDb.getRequirements());

		if (wizard.open() == SWT.OK) {
			List<CyberRequirement> updatedReqs = wizard.getRequirements();

			updatedReqs.forEach(r -> {
				CyberRequirement existing = reqDb.get(r);
				if (existing == null) {
					// not possible; signal error
					throw new RuntimeException("Updated requirement not found in requirements database : " + r);
				} else {
					if (existing.getStatus() == CyberRequirement.toDo
							|| existing.getStatus() == CyberRequirement.omit) {
						switch (r.getStatus()) {
						case CyberRequirement.toDo:
						case CyberRequirement.omit:
							// do nothing
							break;
						case CyberRequirement.add:
							// add to model
							reqMgr.importRequirement(r);
							break;
						case CyberRequirement.addPlusAgree:
							// add to model and formalize
							reqMgr.importRequirement(r);
							reqMgr.formalizeRequirement(r.getId());
							break;
						default:
							// Unknown status; signal error
							throw new RuntimeException("Updated requirement has invalid status : " + r);
						}
					} else if (existing.getStatus() == CyberRequirement.add) {
						switch (r.getStatus()) {
						case CyberRequirement.toDo:
							// remove resolute claim definition and claim call
							reqMgr.removeRequirement(r.getId(), true);
							break;
						case CyberRequirement.omit:
							// remove resolute claim definition and claim call
							reqMgr.removeRequirement(r.getId(), true);
							break;
						case CyberRequirement.add:
							// no change permitted
							break;
						case CyberRequirement.addPlusAgree:
							// formalize
							reqMgr.formalizeRequirement(r.getId());
							break;
						default:
							// Unknown status; signal error
							throw new RuntimeException("Updated requirement has invalid status : " + r);
						}
					} else if (existing.getStatus() == CyberRequirement.addPlusAgree) {
						switch (r.getStatus()) {
						case CyberRequirement.toDo:
							// remove resolute claim definition, claim call and agree call
							reqMgr.removeRequirement(r.getId(), true);
							break;
						case CyberRequirement.omit:
							// remove resolute claim definition, claim call and agree call
							reqMgr.removeRequirement(r.getId(), true);
							break;
						case CyberRequirement.add:
							// remove agree call
							reqMgr.unformalizeRequirement(r.getId());
							break;
						case CyberRequirement.addPlusAgree:
							// no change permitted
							break;
						default:
							// Unknown status; signal error
							throw new RuntimeException("Updated requirement has invalid status : " + r);
						}
					} else {
						// Unknown status; signal error
						throw new RuntimeException("Existing requirement has invalid status : " + existing);
					}
					reqDb.updateRequirement(r);
				}
			});

//			reqMgr.importRequirements(updatedReqs);
//			importedReqs.forEach(req -> {
//				if (req.getStatus() == CyberRequirement.addPlusAgree) {
//					reqMgr.formalizeRequirement(req.getId());
//				}
//			});

//			// Write omitted requirements to log
//			if (!omittedReqs.isEmpty()) {
//				reqMgr.addOmittedRequirements(omittedReqs, jsonFile.getImplementation());
////				reqMgr.saveOmittedRequirements(CaseUtils.CASE_OMITTED_REQUIREMENTS_FILE);
//			}
			reqDb.saveRequirementsDatabase();
		}

		return null;
	}

	protected List<JsonRequirementsFile> readInputFiles(ExecutionEvent event) {
		List<JsonRequirementsFile> reqs = new ArrayList<>();

		// If a filename was passed to this command, open the file.
		// Otherwise, prompt the user for the file
		String filename = event.getParameter("filename");
		if (filename == null || filename.isEmpty()) {

			FileDialog dlgReqFile = new FileDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					SWT.MULTI);
			dlgReqFile.setText("Select requirements file to import.");

			dlgReqFile.open();
			String[] filenames = dlgReqFile.getFileNames();
			String filterPath = dlgReqFile.getFilterPath();

			for (String fn : filenames) {
				File reqFile = new File(filterPath, fn);
				if (!reqFile.exists()) {
					Dialog.showError("File not found",
							"Cannot find the requirements file " + reqFile.getAbsolutePath() + ".");
					continue;
				}
				JsonRequirementsFile jsonFile = new JsonRequirementsFile();
				if (!jsonFile.importFile(reqFile)) {
					Dialog.showError("Problem with " + reqFile.getName(),
							"Could not load cyber requirements file " + reqFile.getName() + ".");
					continue;
				}
				// Alert user if there aren't any requirements to import
				if (jsonFile.getRequirements().isEmpty()) {
					Dialog.showError("No new requirements to import", reqFile.getName()
							+ " does not contain any requirements that are not already present in this model.");
					continue;
				}
				// Add the requirements in this file to the accumulated list of requirements
				reqs.add(jsonFile);
			}

		}
		return reqs;
	}

}
