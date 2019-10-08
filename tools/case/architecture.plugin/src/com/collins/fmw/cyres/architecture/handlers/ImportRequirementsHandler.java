package com.collins.fmw.cyres.architecture.handlers;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.swt.SWT;
import org.eclipse.ui.PlatformUI;
import org.osate.ui.dialogs.Dialog;

import com.collins.fmw.cyres.architecture.dialogs.ImportRequirementsGUI;
import com.collins.fmw.cyres.architecture.requirements.CyberRequirement;
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
		reqMgr.readRequirementFiles(event.getParameter("filename"));

		ImportRequirementsGUI wizard = new ImportRequirementsGUI(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		wizard.setRequirements(reqMgr.getRequirements());

		if (wizard.open() == SWT.OK) {
			List<CyberRequirement> updatedReqs = wizard.getRequirements();
			reqMgr.updateRequirements(updatedReqs);
		}

		return null;
	}

}
