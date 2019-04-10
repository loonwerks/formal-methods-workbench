package com.collins.fmw.cyres.architecture.handlers;

import java.io.IOException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.handlers.IHandlerService;
import org.osate.ui.dialogs.Dialog;

import com.collins.fmw.cyres.agree.json.plugin.AgreeJson;

public class GenerateRequirementsHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);

		String tool = event.getParameter("com.collins.fmw.cyres.architecture.commands.GenerateRequirements.tool");
		switch (tool.toLowerCase()) {

		case "gearcase": // CRA

				break;

		case "dcrypps": // Vanderbilt / DOLL

			try {
				// Generate json
				URI jsonURI = AgreeJson.createJson(event);
			} catch (CoreException | IOException e) {
				Dialog.showError("Model Export", "Unable to export model to JSON format.");
				e.printStackTrace();
				return null;
			}

			// TODO: Call tool
			IHandlerService handlerService = window.getService(IHandlerService.class);
			try {
				handlerService.executeCommand("com.collins.fmw.cyres.architecture.commands.ImportRequirements", null);
			} catch (Exception ex) {
				throw new RuntimeException("Command not found");
				// Give message

			}

			break;

		default:
			Dialog.showError("Unknown tool", tool + " is not a recognized cyber requirements tool.");
			return null;

		}

		return null;
	}
}
