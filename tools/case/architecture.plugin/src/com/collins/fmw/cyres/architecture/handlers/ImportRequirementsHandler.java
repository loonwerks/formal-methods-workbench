package com.collins.fmw.cyres.architecture.handlers;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.PlatformUI;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.osate.ui.dialogs.Dialog;

import com.collins.fmw.cyres.architecture.dialogs.ImportRequirementsGUI;
import com.collins.fmw.cyres.architecture.requirements.AgreePropCheckedClaim;
import com.collins.fmw.cyres.architecture.requirements.BaseClaim;
import com.collins.fmw.cyres.architecture.requirements.CyberRequirement;
import com.collins.fmw.cyres.architecture.requirements.JsonRequirementsFile;
import com.collins.fmw.cyres.architecture.requirements.RequirementsDatabase;
import com.collins.fmw.cyres.architecture.requirements.RequirementsManager;
import com.collins.fmw.cyres.architecture.utils.CaseUtils;
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

//		if (wizard.open() == SWT.OK) {
//			List<CyberRequirement> updatedReqs = wizard.getRequirements();
//
//			updatedReqs.forEach(r -> {
//				CyberRequirement existing = reqDb.get(r);
//				if (existing == null) {
//					// not possible; signal error
//					throw new RuntimeException("Updated requirement not found in requirements database : " + r);
//				} else {
//					if (existing.getStatus() == CyberRequirement.toDo
//							|| existing.getStatus() == CyberRequirement.omit) {
//						switch (r.getStatus()) {
//						case CyberRequirement.toDo:
//						case CyberRequirement.omit:
//							// do nothing
//							break;
//						case CyberRequirement.add:
//							// add to model
//							reqMgr.importRequirement(r);
//							break;
//						case CyberRequirement.addPlusAgree:
//							// add to model and formalize
////							reqMgr.importRequirement(r, true);
//							reqMgr.importRequirement(r);
//							reqMgr.formalizeRequirement(r.getId());
//							break;
//						default:
//							// Unknown status; signal error
//							throw new RuntimeException("Updated requirement has invalid status : " + r);
//						}
//					} else if (existing.getStatus() == CyberRequirement.add) {
//						switch (r.getStatus()) {
//						case CyberRequirement.toDo:
//							// remove resolute claim definition and claim call
//							reqMgr.removeRequirement(r.getId(), true);
//							break;
//						case CyberRequirement.omit:
//							// remove resolute claim definition and claim call
//							reqMgr.removeRequirement(r.getId(), true);
//							break;
//						case CyberRequirement.add:
//							// no change permitted
//							break;
//						case CyberRequirement.addPlusAgree:
//							// formalize
//							reqMgr.formalizeRequirement(r.getId());
//							break;
//						default:
//							// Unknown status; signal error
//							throw new RuntimeException("Updated requirement has invalid status : " + r);
//						}
//					} else if (existing.getStatus() == CyberRequirement.addPlusAgree) {
//						switch (r.getStatus()) {
//						case CyberRequirement.toDo:
//							// remove resolute claim definition, claim call and agree call
//							reqMgr.removeRequirement(r.getId(), true);
//							break;
//						case CyberRequirement.omit:
//							// remove resolute claim definition, claim call and agree call
//							reqMgr.removeRequirement(r.getId(), true);
//							break;
//						case CyberRequirement.add:
//							// remove agree call
//							reqMgr.unformalizeRequirement(r.getId());
//							break;
//						case CyberRequirement.addPlusAgree:
//							// no change permitted
//							break;
//						default:
//							// Unknown status; signal error
//							throw new RuntimeException("Updated requirement has invalid status : " + r);
//						}
//					} else {
//						// Unknown status; signal error
//						throw new RuntimeException("Existing requirement has invalid status : " + existing);
//					}
//					reqDb.updateRequirement(r);
//				}
//			});
//			reqDb.saveRequirementsDatabase();
//		}

		if (wizard.open() == SWT.OK) {
			List<CyberRequirement> updatedReqs = wizard.getRequirements();
			RequirementHelper helper = new RequirementHelper();

			for (CyberRequirement r : updatedReqs) {
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
							helper.insertRequirement(r, true, false);
							break;
						case CyberRequirement.addPlusAgree:
							// add to model and formalize
							helper.insertRequirement(r, true, true);
							break;
						default:
							// Unknown status; signal error
							throw new RuntimeException("Updated requirement has invalid status : " + r);
						}
					} else if (existing.getStatus() == CyberRequirement.add) {
						switch (r.getStatus()) {
						case CyberRequirement.toDo:
						case CyberRequirement.omit:
							// remove resolute claim definition and claim call
							helper.removeRequirement(r, false, true);
							break;
						case CyberRequirement.add:
							// no change permitted
							break;
						case CyberRequirement.addPlusAgree:
							// formalize
							helper.insertRequirement(r, false, true);
							break;
						default:
							// Unknown status; signal error
							throw new RuntimeException("Updated requirement has invalid status : " + r);
						}
					} else if (existing.getStatus() == CyberRequirement.addPlusAgree) {
						switch (r.getStatus()) {
						case CyberRequirement.toDo:
						case CyberRequirement.omit:
							// remove resolute claim definition, claim call and agree call
							helper.removeRequirement(r, true, true);
							break;
						case CyberRequirement.add:
							// remove agree call
							helper.removeRequirement(r, true, false);
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
				}
			}

			helper.commitChanges();
			updatedReqs.forEach(r -> reqDb.updateRequirement(r));
			reqDb.saveRequirementsDatabase();
		}

		return null;
	}

//	private void insert(HashMap<IFile, List<CyberRequirement>> map, IFile s, CyberRequirement r) {
//		if (!map.containsKey(s)) {
//			map.put(s, new ArrayList<CyberRequirement>());
//		}
//		map.get(s).add(r);
//	}

	class RequirementHelper {
		private Set<CyberRequirement> addBaseClaimDefinition = new HashSet<CyberRequirement>();
		private Set<CyberRequirement> addAgreeCheckClaimDefinition = new HashSet<CyberRequirement>();
		private Map<IFile, HashSet<CyberRequirement>> addBaseClaimProveStatement = new HashMap<IFile, HashSet<CyberRequirement>>();
		private Map<IFile, HashSet<CyberRequirement>> addAgreeCheckClaimProveStatement = new HashMap<IFile, HashSet<CyberRequirement>>();
		private Map<IFile, HashSet<CyberRequirement>> addAgreeAssumption = new HashMap<IFile, HashSet<CyberRequirement>>();
		private Map<IFile, HashSet<CyberRequirement>> removeAgreeAssumption = new HashMap<IFile, HashSet<CyberRequirement>>();
		private Map<IFile, HashSet<CyberRequirement>> removeProveStatement = new HashMap<IFile, HashSet<CyberRequirement>>();
		private Set<CyberRequirement> removeAgreeCheckFromClaimDefinition = new HashSet<CyberRequirement>();
		private Set<CyberRequirement> removeClaimDefinition = new HashSet<CyberRequirement>();

		private Map<CyberRequirement, BaseClaim> baseClaims = new HashMap<CyberRequirement, BaseClaim>();
		private Map<CyberRequirement, AgreePropCheckedClaim> agreePropCheckedClaims = new HashMap<CyberRequirement, AgreePropCheckedClaim>();

		void resetChanges() {
			addBaseClaimDefinition.clear();
			addAgreeCheckClaimDefinition.clear();
			addBaseClaimProveStatement.clear();
			addAgreeCheckClaimProveStatement.clear();
			addAgreeAssumption.clear();
			removeAgreeAssumption.clear();
			removeProveStatement.clear();
			removeAgreeCheckFromClaimDefinition.clear();
			removeClaimDefinition.clear();

			baseClaims.clear();
			agreePropCheckedClaims.clear();
		}

		private void insert(Set<CyberRequirement> s, CyberRequirement r) {
			s.add(r);
		}

		private void insert(Map<IFile, HashSet<CyberRequirement>> map, IFile key, CyberRequirement r) {
			Set<CyberRequirement> s = map.get(key);
			if (s == null) {
				map.put(key, new HashSet<CyberRequirement>());
				s = map.get(key);
			}
			s.add(r);
		}

		void insertRequirement(CyberRequirement r, boolean define, boolean formalize) {
			if (define) {
				insert(addBaseClaimDefinition, r);
				insert(addBaseClaimProveStatement, r.getContainingFile(), r);
			}
			if (formalize) {
				insert(addAgreeCheckClaimDefinition, r);
				insert(addAgreeCheckClaimProveStatement, r.getContainingFile(), r);
				insert(addAgreeAssumption, r.getSubcomponentContainingFile(), r);
			}
		}

		void removeRequirement(CyberRequirement r, boolean removeFormalization, boolean removeDefinition) {
			if (removeDefinition) {
				insert(removeProveStatement, r.getContainingFile(), r);
				insert(removeClaimDefinition, r);
				if (removeFormalization) {
					insert(removeAgreeAssumption, r.getSubcomponentContainingFile(), r);
				}
			} else if (removeFormalization) {
				insert(removeAgreeAssumption, r.getSubcomponentContainingFile(), r);
				insert(removeAgreeCheckFromClaimDefinition, r);
			}
		}

		void commitChanges() {
			commitChangesToClaimsFile();

			// List of files to be modified
			Set<IFile> files = new HashSet<IFile>();
			files.addAll(addBaseClaimProveStatement.keySet());
			files.addAll(addAgreeCheckClaimProveStatement.keySet());
			files.addAll(addAgreeAssumption.keySet());
			files.addAll(removeAgreeAssumption.keySet());
			files.addAll(removeProveStatement.keySet());

			for (IFile file : files) {
				commitChangestoFile(file);
			}

			resetChanges();
		}

		private void commitChangesToClaimsFile() {
			// Get the file to insert into
			IFile file = CaseUtils.getCaseRequirementsFile();
			XtextEditor editor = RequirementsManager.getEditor(file);

			if (editor == null) {
				throw new RuntimeException("Cannot open claim definition file: " + file);
			}

			editor.getDocument().modify(resource -> {
				for (CyberRequirement req : addBaseClaimDefinition) {
					BaseClaim c = new BaseClaim(req);
					req.insertClaimDef(c, resource);
					baseClaims.put(req, c);
				}

				for (CyberRequirement req : addAgreeCheckClaimDefinition) {
					AgreePropCheckedClaim c = new AgreePropCheckedClaim(req.getId(), req.getContext());
					req.insertClaimDef(c, resource);
					agreePropCheckedClaims.put(req, c);
				}

				removeAgreeCheckFromClaimDefinition.removeAll(removeClaimDefinition);
				for (CyberRequirement req : removeAgreeCheckFromClaimDefinition) {
					req.removeClaimDef(new AgreePropCheckedClaim(req.getId(), req.getContext()), resource);
				}

				for (CyberRequirement req : removeClaimDefinition) {
					req.removeClaimDef(new BaseClaim(req), resource);
				}

				return null;
			});

//			editor.forceReconcile();

			// Close editor, if necessary
			RequirementsManager.closeEditor(editor, true);
		}

		private void commitChangestoFile(IFile file) {
			XtextEditor editor = RequirementsManager.getEditor(file);

			if (editor == null) {
				throw new RuntimeException("Cannot open claim definition file: " + file);
			}

			editor.getDocument().modify(resource -> {
				if (addBaseClaimProveStatement.containsKey(file)) {
					for (CyberRequirement req : addBaseClaimProveStatement.get(file)) {
						BaseClaim c = baseClaims.get(req);
						req.insertClaimCall(c, resource);
					}
				}

				if (addAgreeCheckClaimProveStatement.containsKey(file)) {
					for (CyberRequirement req : addAgreeCheckClaimProveStatement.get(file)) {
						AgreePropCheckedClaim c = agreePropCheckedClaims.get(req);
						req.insertClaimCall(c, resource);
					}
				}

				if (addAgreeAssumption.containsKey(file)) {
					for (CyberRequirement req : addAgreeAssumption.get(file)) {
						req.insertAgree(resource);
					}
				}

				if (removeAgreeAssumption.containsKey(file)) {
					for (CyberRequirement req : removeAgreeAssumption.get(file)) {
						req.removeAgree(resource);
					}
				}

				if (removeProveStatement.containsKey(file)) {
					for (CyberRequirement req : removeProveStatement.get(file)) {
						req.removeClaimCall(resource);
					}
				}

				return null;
			});

//			editor.forceReconcile();

			// Close editor, if necessary
			RequirementsManager.closeEditor(editor, true);
		}
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
