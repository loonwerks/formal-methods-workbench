package com.collins.fmw.cyres.architecture.requirements;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.utils.EditorUtils;
import org.osate.aadl2.AadlPackage;
import org.osate.ui.dialogs.Dialog;

import com.collins.fmw.cyres.architecture.utils.CaseUtils;
import com.collins.fmw.cyres.util.plugin.TraverseProject;
import com.rockwellcollins.atc.resolute.resolute.ClaimBody;
import com.rockwellcollins.atc.resolute.resolute.ClaimString;
import com.rockwellcollins.atc.resolute.resolute.ClaimText;
import com.rockwellcollins.atc.resolute.resolute.Definition;
import com.rockwellcollins.atc.resolute.resolute.DefinitionBody;
import com.rockwellcollins.atc.resolute.resolute.FunctionDefinition;
import com.rockwellcollins.atc.resolute.resolute.ResoluteLibrary;

/**
 *  Manages insertion, deletion, and modification of logical CASE requirements/claims in the model
 */
public class RequirementsManager {

	private static IProject currentProject = null;
	// Singleton instance
	private static RequirementsManager instance = null;
	private final RequirementsDatabaseHelper reqDb = new RequirementsDatabaseHelper();

	public static RequirementsManager getInstance() {
		if (instance == null || currentProject == null) {
			instance = new RequirementsManager();
		} else if (currentProject != TraverseProject.getCurrentProject()) {
			instance = new RequirementsManager();
		}
		return instance;
	}

	private static XtextEditor activeEditor = null;

	public static void closeEditor(XtextEditor editor, boolean save) {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if (save) {
			page.saveEditor(editor, false);
		}

		if (editor.equals(activeEditor)) {
			return;
		} else {
			page.closeEditor(editor, false);
		}
	}

	public static XtextEditor getEditor(IFile file) {
		IWorkbenchPage page = null;
		IEditorPart part = null;

		activeEditor = EditorUtils.getActiveXtextEditor();

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

	private RequirementsManager() {
		// Initialize requirements list

		if (currentProject == null) {
			currentProject = TraverseProject.getCurrentProject();
		}

		// Read in any existing imported requirements
		reqDb.importRequirements(findImportedRequirements());
	}

	public boolean formalizeRequirement(String reqId) {
		CyberRequirement req = getRequirement(reqId);
		if (req == null) {
			return false;
		}
		req.setAgree();
		formalizeRequirement(req);
		reqDb.updateRequirement(req);
		reqDb.saveRequirementsDatabase();
		return true;
	}

	public boolean unformalizeRequirement(String reqId) {
		CyberRequirement req = getRequirement(reqId);
		if (req == null) {
			return false;
		}
		req.setStatus(CyberRequirement.add);
		unformalizeRequirement(req);
		reqDb.updateRequirement(req);
		reqDb.saveRequirementsDatabase();
		return true;
	}

	public List<CyberRequirement> getImportedRequirements() {
		return reqDb.getImportedRequirements();
//		return findImportedRequirements();
	}

	public CyberRequirement getRequirement(String requirementId) {
		return reqDb.get(requirementId);
	}

	public List<CyberRequirement> getRequirements() {
		return reqDb.getRequirements();
	}

//	public void removeRequirement(String reqId, boolean removeAgree) {
//		CyberRequirement req = getRequirement(reqId);
//		if (req == null) {
//			return;
//		}
//		if (req.hasAgree()) {
//			if (!removeAgree) {
//				throw new RuntimeException(
//						"Formalized requirement can only be removed after removing the formalization.");
//			}
//			unformalizeRequirement(reqId);
//		}
//
//		removeClaim(req, new BaseClaim(req));
//
//		// Add the requirement to the imported requirements list
//		importedRequirements.remove(req);
//	}

//	public void importRequirements(List<CyberRequirement> reqs) {
//		for (CyberRequirement req : reqs) {
//			// Insert base claim for requirement and add the requirement to the imported requirements list
//			insertClaim(req, new BaseClaim(req));
//			importedRequirements.add(req);
//		}
//	}

	public void modifyRequirement(String reqId, BuiltInClaim claim) {
		CyberRequirement req = getRequirement(reqId);
		if (req != null) {
			insertClaim(req, claim);
		}
		reqDb.updateRequirement(req);
	}

	public void updateRequirements(List<CyberRequirement> updatedReqs) {
		BatchUpdateHelper helper = new BatchUpdateHelper();

		for (CyberRequirement r : updatedReqs) {
			CyberRequirement existing = reqDb.get(r);
			if (existing == null) {
				// not possible; signal error
				throw new RuntimeException("Updated requirement not found in requirements database : " + r);
			} else {
				if (existing.getStatus() == CyberRequirement.toDo || existing.getStatus() == CyberRequirement.omit) {
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

	public boolean readRequirementFiles(boolean importRequirements, String filename) {
		final List<CyberRequirement> existing = findImportedRequirements();
		Set<String> reqIds = new HashSet<String>();
		for (CyberRequirement r : existing) {
			if (r.getId() != null && !r.getId().isEmpty() && !reqIds.add(r.getId())) {
				Dialog.showError("Error in Claims file",
						"Duplicate requirement ID found in claims file " + r.getId() + ".");
				return false;
			}
		}

		if (importRequirements) {
			final List<JsonRequirementsFile> jsonReqFiles = readInputFiles(filename);
			if (jsonReqFiles.isEmpty()) {
				return false;
			}
			reqDb.reset();
			reqDb.importJsonRequrementsFiles(jsonReqFiles);
		}
		reqDb.importRequirements(existing);
		reqDb.saveRequirementsDatabase();

		return true;
	}

	protected List<CyberRequirement> findImportedRequirements() {

		IFile file = CaseUtils.getCaseRequirementsFile();
		XtextEditor editor = getEditor(file);

		if (editor != null) {
			List<CyberRequirement> embeddedRequirements = editor.getDocument().readOnly(resource -> {

				// Get modification context
				AadlPackage aadlPkg = CyberRequirement.getResoluteModificationContext(resource,
						CaseUtils.CASE_REQUIREMENTS_NAME);
				if (aadlPkg == null) {
					return Collections.emptyList();
				}

				ResoluteLibrary resLib = CyberRequirement.getResoluteLibrary(aadlPkg);
				if (resLib == null) {
					return Collections.emptyList();
				}

				// If this function definition already exists, remove it
				List<CyberRequirement> resoluteClauses = new ArrayList<>();
				for (Definition def : resLib.getDefinitions()) {
					if (def instanceof FunctionDefinition) {
						FunctionDefinition fd = (FunctionDefinition) def;
						DefinitionBody db = fd.getBody();
						if (db instanceof ClaimBody) {
							String reqClaimString = "";
							ClaimBody cb = (ClaimBody) db;
							for (ClaimText ct : cb.getClaim()) {
								if (ct instanceof ClaimString) {
									reqClaimString += ((ClaimString) ct).getStr();
								}
							}
							// Annotate claim with requirement information
							CyberRequirement r = CyberRequirement.parseClaimString(fd.getName(), reqClaimString, cb);
							if (r != null) {
								resoluteClauses.add(r);
							}
						}
					}
				}

				return resoluteClauses;
			});

			// Close editor, if necessary (no saving, read-only)
			closeEditor(editor, false);

			return embeddedRequirements;
		}

		return new ArrayList<CyberRequirement>();
	}

	protected void formalizeRequirement(CyberRequirement req) {
		insertClaim(req, new AgreePropCheckedClaim(req.getId(), req.getContext()));
		insertAgree(req);
	}

	protected void unformalizeRequirement(CyberRequirement req) {
		removeClaim(req, new AgreePropCheckedClaim(req.getId(), req.getContext()));
		removeAgree(req);
	}

	protected void editAgree(CyberRequirement req, final boolean insert) {
		if (req == null) {
			return;
		}

		// Read the file that contains the requirement's context and determine the subcomponent's type
		IFile file = req.getContainingFile();
		XtextEditor editor = getEditor(file);

		if (editor == null) {
			return;
		}

		String compQualifiedName = editor.getDocument().readOnly(resource -> {
			return CyberRequirement.getModificationContext(resource, req.getContext()).getQualifiedName();
		});

		closeEditor(editor, false);

		// Insert requirement into the subcomponent type's agree annex
		file = CyberRequirement.getContainingFile(compQualifiedName);
		editor = getEditor(file);

		if (editor == null) {
			return;
		}

		editor.getDocument().modify(resource -> {
			if (insert) {
				req.insertAgree(resource, compQualifiedName);
			} else {
				req.removeAgree(resource, compQualifiedName);
			}
			return null;
		});

		// Close editor, if necessary
		closeEditor(editor, true);
	}

	protected void insertAgree(CyberRequirement req) {
		editAgree(req, true);
	}

	protected void removeAgree(CyberRequirement req) {
		editAgree(req, false);
	}

	protected void insertClaim(CyberRequirement req, BuiltInClaim claim) {
		insertClaimDefinition(req, claim);
		insertClaimCall(req, claim);
	}

	protected boolean removeClaim(CyberRequirement req, BuiltInClaim claim) {
		if (claim instanceof BaseClaim) {
			removeClaimCall(req);
		}
		removeClaimDefinition(req, claim);
		return true;
	}

	protected void insertClaimCall(CyberRequirement req, BuiltInClaim claim) {
		if (req == null || claim == null) {
			return;
		}

		// Get the file to insert into
		IFile file = req.getContainingFile();
		XtextEditor editor = getEditor(file);

		if (editor == null) {
			return;
		}

		editor.getDocument().modify(resource -> {
			req.insertClaimCall(resource, claim);
			return null;
		});

		editor.forceReconcile();

		// Close editor
		closeEditor(editor, true);
	}

	protected void insertClaimDefinition(CyberRequirement req, BuiltInClaim claim) {
		if (req == null || claim == null) {
			throw new RuntimeException("Cannot insert claim definition. Requirement: " + req + " Claim: " + claim);
		}

		// Get the file to insert into
		IFile file = CaseUtils.getCaseRequirementsFile();
		XtextEditor editor = getEditor(file);

		if (editor == null) {
			throw new RuntimeException("Cannot open claim definition file: " + file);
		}

		editor.getDocument().modify(resource -> {
			req.insertClaimDef(resource, claim);
			return null;
		});

		editor.forceReconcile();

		// Close editor
		closeEditor(editor, true);
	}

	protected FunctionDefinition removeClaimDefinition(CyberRequirement req,
			BuiltInClaim claim) {
		if (req == null || claim == null) {
			return null;
		}

		// Get the file to insert into
		IFile file = CaseUtils.getCaseRequirementsFile();
		XtextEditor editor = getEditor(file);

		if (editor == null) {
			return null;
		}

		FunctionDefinition fd = editor.getDocument().modify(resource -> {
			return req.removeClaimDef(resource, claim);
		});

		// Close editor, if necessary
		closeEditor(editor, true);

		return fd;
	}

	protected boolean removeClaimCall(CyberRequirement req) {
		if (req == null) {
			return false;
		}

		// Get the file to insert into
		IFile file = req.getContainingFile();
		XtextEditor editor = getEditor(file);

		if (editor == null) {
			return false;
		}

		boolean success = editor.getDocument().modify(resource -> {
			return req.removeClaimCall(resource);
		});

		// Close editor, if necessary
		closeEditor(editor, true);

		return success;
	}

	protected List<JsonRequirementsFile> readInputFiles(String filename) {
		List<JsonRequirementsFile> reqs = new ArrayList<>();
		List<String> filenames = new ArrayList<String>();
		String filterPath = null;

		// If a filename was passed to this command, open the file.
		// Otherwise, prompt the user for the file
		if (filename == null || filename.isEmpty()) {
			FileDialog dlgReqFile = new FileDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					SWT.MULTI);
			dlgReqFile.setText("Select requirements file to import.");
			dlgReqFile.open();
			for (String fn : dlgReqFile.getFileNames()) {
				filenames.add(fn);
			}
			filterPath = dlgReqFile.getFilterPath();
		} else {
			filenames.add(filename);
		}

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

		return reqs;
	}

	class BatchUpdateHelper {
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

		private void resetChanges() {
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

		public void insertRequirement(CyberRequirement r, boolean define, boolean formalize) {
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

		public void removeRequirement(CyberRequirement r, boolean removeFormalization, boolean removeDefinition) {
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

		public void commitChanges() {
			// List of files to be modified
			Set<IFile> files = new HashSet<IFile>();
			files.addAll(addBaseClaimProveStatement.keySet());
			files.addAll(addAgreeCheckClaimProveStatement.keySet());
			files.addAll(addAgreeAssumption.keySet());
			files.addAll(removeAgreeAssumption.keySet());
			files.addAll(removeProveStatement.keySet());

			// Remove prove statements before removing claim definitions in the main claims file
			// to avoid null cross references.

			for (IFile file : files) {
				removeProveStatements(file);
			}

			// Modify claim definitions
			commitChangesToClaimsFile();

			// Make the rest of the changes: adding prove calls, adding/removing agree statements
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
					req.insertClaimDef(resource, c);
					baseClaims.put(req, c);
				}

				for (CyberRequirement req : addAgreeCheckClaimDefinition) {
					AgreePropCheckedClaim c = new AgreePropCheckedClaim(req.getId(), req.getContext());
					req.insertClaimDef(resource, c);
					agreePropCheckedClaims.put(req, c);
				}

				removeAgreeCheckFromClaimDefinition.removeAll(removeClaimDefinition);
				for (CyberRequirement req : removeAgreeCheckFromClaimDefinition) {
					req.removeClaimDef(resource, new AgreePropCheckedClaim(req.getId(), req.getContext()));
				}

				for (CyberRequirement req : removeClaimDefinition) {
					req.removeClaimDef(resource, new BaseClaim(req));
				}

				CyberRequirement.sortClaimDefinitions(resource);

				return null;
			});

			// Close editor
			RequirementsManager.closeEditor(editor, true);
		}

		private void removeProveStatements(IFile file) {
			// Remove prove calls before removing claim definitions in the main claims file
			// to avoid null cross references.

			// Get the file to insert into
			XtextEditor editor = RequirementsManager.getEditor(file);

			if (editor == null) {
				throw new RuntimeException("Cannot open claim definition file: " + file);
			}

			editor.getDocument().modify(resource -> {
				if (removeProveStatement.containsKey(file)) {
					for (CyberRequirement req : removeProveStatement.get(file)) {
						req.removeClaimCall(resource);
					}
				}

				return null;
			});

			// Close editor
			RequirementsManager.closeEditor(editor, true);
		}

		private void commitChangestoFile(IFile file) {
			// Get the file to insert into
			XtextEditor editor = RequirementsManager.getEditor(file);

			if (editor == null) {
				throw new RuntimeException("Cannot open claim definition file: " + file);
			}

			editor.getDocument().modify(resource -> {
				if (addBaseClaimProveStatement.containsKey(file)) {
					for (CyberRequirement req : addBaseClaimProveStatement.get(file)) {
						BaseClaim c = baseClaims.get(req);
						req.insertClaimCall(resource, c);
					}
				}

				if (addAgreeCheckClaimProveStatement.containsKey(file)) {
					for (CyberRequirement req : addAgreeCheckClaimProveStatement.get(file)) {
						AgreePropCheckedClaim c = agreePropCheckedClaims.get(req);
						req.insertClaimCall(resource, c);
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

				return null;
			});

			// Close editor
			RequirementsManager.closeEditor(editor, true);
		}
	}

	/**
	 * Requirements Database helper class.
	 *
	 * <p>Its purpose is to provide a central entity to keep track of
	 * requirements generated via TA1 tools,
	 * requirements imported into the AADL models,
	 * and requirements saved to the disk.
	 *
	 * <p>It provides quick insertion, searching and updating of requirements,
	 * as well a mechanism to modify a set of requirements in the AADL model.
	 *
	 * <p>It is expected to be used as a singleton class by the enclosing RequirementsManager singleton instance.
	 *
	 * @author Junaid Babar
	 *
	 */
	class RequirementsDatabaseHelper {

		private HashMap<String, CyberRequirement> requirements;
		private HashMap<String, String> idToKey;

		private String makeKey(CyberRequirement req) {
			return req.getType() + req.getContext();
		}

		private String makeKey(String type, String context) {
			return type + context;
		}

		public RequirementsDatabaseHelper() {
			requirements = new HashMap<String, CyberRequirement>();
			idToKey = new HashMap<String, String>();
			readRequirementsDatabase();
		}

		public void reset() {
			requirements.clear();
			idToKey.clear();
		}

		public void readRequirementsDatabase() {
			// Read database from the physical requirements database file
//			final File reqFile = new File(CaseUtils.CASE_REQUIREMENTS_DATABASE_FILE);
			final IPath reqFilePath = TraverseProject.getCurrentProject()
					.getFile(CaseUtils.CASE_REQUIREMENTS_DATABASE_FILE).getLocation();
			File reqFile = null;
			if (reqFilePath != null) {
				reqFile = reqFilePath.toFile();
			}
			JsonRequirementsFile jsonReqFile = new JsonRequirementsFile();
			if (!reqFile.exists() || !jsonReqFile.importFile(reqFile)) {
//				Dialog.showInfo("Missing requirements database",
//						"No requirements database found. Starting a new database.");
			} else {
				// Add the requirements in this file to the accumulated list of requirements
				importRequirements(jsonReqFile.getRequirements());
			}
		}

		public void saveRequirementsDatabase() {
			// Write database to physical requirements database file
			if (requirements.isEmpty()) {
				return;
			}
//			final File reqFile = new File(CaseUtils.CASE_REQUIREMENTS_DATABASE_FILE);
			final IPath reqFilePath = TraverseProject.getCurrentProject()
					.getFile(CaseUtils.CASE_REQUIREMENTS_DATABASE_FILE).getLocation();
			File reqFile = null;
			if (reqFilePath != null) {
				reqFile = reqFilePath.toFile();
			}
			JsonRequirementsFile jsonReqFile = new JsonRequirementsFile(CyberRequirement.notApplicable,
					new Date().getTime(), CyberRequirement.notApplicable, CyberRequirement.notApplicable,
					getRequirements());
			if (!jsonReqFile.exportFile(reqFile)) {
				throw new RuntimeException("Could not save cyber requirements file " + reqFile.getName() + ".");
			}
		}

		public void importJsonRequrementsFiles(List<JsonRequirementsFile> reqFiles) {
			reqFiles.forEach(file -> {
				importRequirements(file.getRequirements());
			});
		}

		public void importRequirements(List<CyberRequirement> reqs) {
			// Add reqs to the database of requirements
			// Note: if requirement already exists, it will get updated
			reqs.forEach(e -> importRequirement(e));
		}

		public void importRequirement(CyberRequirement req) {
			// Add req to the database of requirements
			// Note: if requirement already exists, it will get updated
			updateRequirement(req);
		}

		public void removeToDos() {
			getToDoRequirements().forEach(req -> {
				requirements.remove(makeKey(req));
				if (req.getId() != null && !req.getId().isEmpty()) {
					idToKey.remove(req.getId());
				}
			});
		}

		public void updateRequirement(CyberRequirement req) {
			final String key = makeKey(req);
			requirements.put(key, req);
			if (req.getId() != null && !req.getId().isEmpty()) {
				idToKey.put(req.getId(), key);
			}
		}

		public boolean contains(CyberRequirement req) {
			return requirements.containsKey(makeKey(req));
		}

		public boolean contains(String type, String context) {
			return requirements.containsKey(makeKey(type, context));
		}

		public boolean contains(String reqId) {
			return idToKey.containsKey(reqId);
		}

		public CyberRequirement get(CyberRequirement req) {
			return requirements.get(makeKey(req));
		}

		public CyberRequirement get(String type, String context) {
			return requirements.get(makeKey(type, context));
		}

		public CyberRequirement get(String reqId) {
			if (contains(reqId)) {
				return requirements.get(idToKey.get(reqId));
			}
			return null;
		}

		public List<CyberRequirement> getRequirements() {
			List<CyberRequirement> list = new ArrayList<CyberRequirement>();
			requirements.values().forEach(e -> list.add(new CyberRequirement(e)));
			return list;
		}

		private List<CyberRequirement> getRequirements(final String filterString) {
			List<CyberRequirement> list = new ArrayList<CyberRequirement>();
			requirements.values().forEach(r -> {
				// Using "==" instead of equals because filterString is one of four constants
				if (r.getStatus() == filterString) {
					list.add(new CyberRequirement(r));
				}
			});
			return list;
		}

		public final List<CyberRequirement> getOmittedRequirements() {
			return getRequirements(CyberRequirement.omit);
		}

		public List<CyberRequirement> getToDoRequirements() {
			return getRequirements(CyberRequirement.toDo);
		}

		public List<CyberRequirement> getAddRequirements() {
			return getRequirements(CyberRequirement.add);
		}

		public List<CyberRequirement> getAddPlusAgreeRequirements() {
			return getRequirements(CyberRequirement.addPlusAgree);
		}

		public List<CyberRequirement> getImportedRequirements() {
			List<CyberRequirement> list = getAddRequirements();
			list.addAll(getAddPlusAgreeRequirements());
			return list;
		}
	}
}
