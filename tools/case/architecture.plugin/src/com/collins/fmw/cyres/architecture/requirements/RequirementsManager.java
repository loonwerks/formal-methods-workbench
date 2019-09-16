package com.collins.fmw.cyres.architecture.requirements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
	private List<CyberRequirement> importedRequirements = new ArrayList<>();

	public static RequirementsManager getInstance() {

		if (instance == null || currentProject == null) {
			instance = new RequirementsManager();
		}

		return instance;
	}

	private static void closeEditor(XtextEditor editor, boolean save) {
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

	private static XtextEditor getEditor(IFile file) {
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

	private RequirementsManager() {
		// Initialize requirements list

		if (currentProject == null) {
			currentProject = TraverseProject.getCurrentProject();
		}

		// Read in any existing imported requirements
		findImportedRequirements();

//		// Read in any existing omitted requirements
//		readOmittedRequirements();
	}

//	public void addOmittedRequirements(List<CyberRequirement> omittedReqs, String implementation) {
//		omittedRequirements.addAll(omittedReqs);
//	}

	public boolean formalizeRequirement(String reqId) {
		CyberRequirement req = getRequirement(reqId);
		if (req == null) {
			return false;
		}
		req.setAgree();
		formalizeRequirement(req);
		return true;
	}

	public boolean unformalizeRequirement(String reqId) {
		CyberRequirement req = getRequirement(reqId);
		if (req == null) {
			return false;
		}
		req.setStatus(CyberRequirement.add);
		unformalizeRequirement(req);
		return true;
	}

	public List<CyberRequirement> getImportedRequirements() {
		return importedRequirements;
	}

//	public List<CyberRequirement> getOmittedRequirements() {
//		return omittedRequirements;
//	}

	public CyberRequirement getRequirement(String requirementId) {
		for (CyberRequirement req : importedRequirements) {
			if (req.getId().equalsIgnoreCase(requirementId)) {
				return req;
			}
		}
		return null;
	}

	public void importRequirement(CyberRequirement req) {
		if (req == null) {
			return;
		}

		insertClaim(req, new BaseClaim(req));

		// Add the requirement to the imported requirements list
		importedRequirements.add(req);

//		if (req.hasAgree()) {
//			formalizeRequirement(req.getId());
//		}
	}

	public void removeRequirement(String reqId, boolean removeAgree) {
		CyberRequirement req = getRequirement(reqId);
		if (req == null) {
			return;
		}
		if (req.hasAgree()) {
			if (!removeAgree) {
				throw new RuntimeException(
						"Formalized requirement can only be removed after removing the formalization.");
			}
//			removeAgree(req);
			unformalizeRequirement(reqId);
		}

		removeClaim(req, new BaseClaim(req));

		// Add the requirement to the imported requirements list
		importedRequirements.remove(req);
	}

	public void importRequirements(List<CyberRequirement> reqs) {
		for (CyberRequirement req : reqs) {
			importRequirement(req);
		}
	}

	public void modifyRequirement(String reqId, BuiltInClaim claim) {
		CyberRequirement req = getRequirement(reqId);
		if (req != null) {
			insertClaim(req, claim);
		}
	}

	public void reset() {
		currentProject = null;
		importedRequirements.clear();
		findImportedRequirements();
//		omittedRequirements.clear();
	}

//	public void saveOmittedRequirements(String filename) {
//		// TODO: Write to file
//		JsonRequirementsFile jsonFile = new JsonRequirementsFile("", System.currentTimeMillis(), "", "",
//				omittedRequirements);
//		jsonFile.exportFile(new File(filename));
//	}

	protected void findImportedRequirements() {

		// TODO: this call is necessary to initialize the resolute requirements file (if it hasn't been initialized).
		CaseUtils.getCaseRequirementsPackage();
		IFile file = CaseUtils.getCaseRequirementsFile();
		XtextEditor editor = getEditor(file);

		if (editor != null) {
			importedRequirements = editor.getDocument().readOnly(resource -> {

				// Get modification context
				AadlPackage aadlPkg = CyberRequirement.getResoluteModificationContext(CaseUtils.CASE_REQUIREMENTS_NAME,
						resource);
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
									reqClaimString += ct;
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
		}
	}

	protected void formalizeRequirement(CyberRequirement req) {
		insertClaim(req, new AgreePropCheckedClaim(req.getId(), req.getContext()));
		// TODO: make it more efficient by pushing this inside the insertClaimCall?
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
			return CyberRequirement.getModificationContext(req.getContext(), resource).getQualifiedName();
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
				req.insertAgree(compQualifiedName, resource);
			} else {
				req.removeAgree(compQualifiedName, resource);
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
		Map.Entry<BuiltInClaim, FunctionDefinition> ret = insertClaimDefinition(req, claim);
		if (ret != null) {
			insertClaimCall(req, ret.getKey(), ret.getValue());
		}
	}

	protected boolean removeClaim(CyberRequirement req, BuiltInClaim claim) {
//		return removeClaimDefinition(req, claim);
		FunctionDefinition fd = removeClaimDefinition(req, claim);
		if (claim instanceof BaseClaim) {
			return fd == null ? false : removeClaimCall(req, fd);
		}
		// TODO: poorly written; meaning of return value is not clear; rewrite
		return true;
	}

	protected void insertClaimCall(CyberRequirement req, BuiltInClaim claim, FunctionDefinition fd) {
		if (req == null || claim == null || fd == null) {
			return;
		}

		// Get the file to insert into
		IFile file = req.getContainingFile();
		XtextEditor editor = getEditor(file);

		if (editor == null) {
			return;
		}

		editor.getDocument().modify(resource -> {
			req.insertClaimCall(claim, fd, resource);
			return null;
		});

		// Close editor, if necessary
		closeEditor(editor, true);
	}

	protected Map.Entry<BuiltInClaim, FunctionDefinition> insertClaimDefinition(CyberRequirement req,
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

		Map.Entry<BuiltInClaim, FunctionDefinition> ret = editor.getDocument().modify(resource -> {
			return req.insertClaimDef(claim, resource);
		});

		// Close editor, if necessary
		closeEditor(editor, true);

		return ret;
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
			return req.removeClaimDef(claim, resource);
		});

		// Close editor, if necessary
		closeEditor(editor, true);

		return fd;
	}

	protected boolean removeClaimCall(CyberRequirement req, FunctionDefinition fd) {
		if (req == null || fd == null) {
			return false;
		}

		// Get the file to insert into
		IFile file = req.getContainingFile();
		XtextEditor editor = getEditor(file);

		if (editor == null) {
			return false;
		}

		boolean success = editor.getDocument().modify(resource -> {
			return req.removeClaimCall(fd, resource);
		});

		// Close editor, if necessary
		closeEditor(editor, true);

		return success;
	}

}
