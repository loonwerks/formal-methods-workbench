package com.collins.fmw.cyres.architecture.handlers;

import org.eclipse.core.resources.IProject;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.handlers.IHandlerService;
import org.osate.aadl2.ComponentImplementation;
import org.osate.ui.dialogs.Dialog;

import com.collins.fmw.cyres.json.plugin.Aadl2Json;
import com.collins.fmw.cyres.json.plugin.AadlTranslate.AgreePrintOption;
import com.collins.fmw.cyres.util.plugin.ModelHashcode;
import com.collins.fmw.cyres.util.plugin.TraverseProject;
import com.google.gson.JsonObject;

public class GenerateRequirementsHandler extends AadlHandler {

	private final static String GENERATE_REQUIREMENTS_TOOL_COMMAND = "com.collins.fmw.cyres.architecture.commands.GenerateRequirements.tool";

	@Override
	protected void runCommand(URI uri) {

		// Check if a component implementation is selected
		final EObject eObj = getEObject(uri);
		if (!(eObj instanceof ComponentImplementation)) {
			Dialog.showError("Generate Cyber Requirements", "Select the top-level system implementation for analysis.");
			return;
		}

		final ComponentImplementation ci = (ComponentImplementation) eObj;

		IProject project = TraverseProject.getCurrentProject();
		if (project == null) {
			Dialog.showError("Generate Cyber Requirements",
					"Unable to determine current AADL project name.  Make sure the top-level system implementation is open in the text editor.");
			return;
		}

		String hashcode = "";
		try {
			hashcode = ModelHashcode.getHashcode(ci);
		} catch (Exception e) {
			Dialog.showError("Generate Cyber Requirements", e.getMessage());
			return;
		}

		JsonObject header = new JsonObject();
		header.addProperty("project", project.getName());
		header.addProperty("implementation", ci.getQualifiedName());
		header.addProperty("date", System.currentTimeMillis());
		header.addProperty("hash", hashcode);

		String tool = this.executionEvent.getParameter(GENERATE_REQUIREMENTS_TOOL_COMMAND);
		switch (tool.toLowerCase()) {

		case "gearcase": // CRA

				break;

		case "dcrypps": // Vanderbilt / DOLL

			try {
				// Generate json
				URI jsonURI = Aadl2Json.createJson(header, AgreePrintOption.BOTH);
			} catch (Exception e) {
				Dialog.showError("Generate Cyber Requirements", "Unable to export model to JSON format.");
				return;
			}

			// TODO: Call tool
			if (Dialog.askQuestion("Generate Cyber Requirements", "DCRYPPS analysis complete.  Import requirements?")) {
				try {
					IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(this.executionEvent);
					IHandlerService handlerService = window.getService(IHandlerService.class);
					handlerService.executeCommand("com.collins.fmw.cyres.architecture.commands.ImportRequirements",
							null);
				} catch (Exception e) {
					Dialog.showError("Generate Cyber Requirements", e.getMessage());
					return;
				}
			}

			break;

		default:
			Dialog.showError("Generate Cyber Requirements", tool + " is not a recognized cyber requirements tool.");
			return;
		}

	}
}
