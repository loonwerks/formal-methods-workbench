package com.collins.atc.ace.cyres.agree.json.plugin;

import java.io.IOException;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.utils.EditorUtils;
import org.osate.aadl2.AadlPackage;
import org.osate.aadl2.ModelUnit;
import org.osate.aadl2.PropertySet;

import com.collins.atc.json.Value;
import com.collins.atc.ace.cyres.util.plugin.Filesystem;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class AgreeJson {

	static private URI makeJsonFile(XtextEditor state) throws CoreException, IOException {
		XtextResource resource = state.getDocument().readOnly(r -> r);

		URI dan = resource.getURI();
		URI folder = dan.trimSegments(1);
		String base = Filesystem.getBase(dan);

		URI writeFolder = Filesystem.createFolder(folder, new String[] { "json-generated" });
		URI json = writeFolder.appendSegment(base).appendFileExtension("json");

		return json;
	}

	static private void printJson(URI json, String whatToPrint) throws CoreException, IOException {

		IFile print = Filesystem.getFile(json);
		Filesystem.writeFile(print, whatToPrint.getBytes());
	}

	static public URI createJson(ExecutionEvent event) throws CoreException, IOException {
		XtextEditor xtextEditor = EditorUtils.getActiveXtextEditor();

		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);

		if (xtextEditor == null) {
			MessageDialog.openError(window.getShell(), "No AADL editor is active",
					"An AADL editor must be active in order to generate JSON.");
		}

		EObject original = xtextEditor.getDocument().readOnly(resource -> resource.getContents().get(0));
		ModelUnit model = (ModelUnit) EcoreUtil.copy(original);

		AadlTranslate aadlTranslate = new AadlTranslate();

		Value jsonValue = null;
		if (model instanceof AadlPackage) {
			jsonValue = aadlTranslate.doSwitch(model);
		} else if (model instanceof PropertySet) {
			jsonValue = aadlTranslate.doSwitch(model);
		} else {
			MessageDialog.openError(window.getShell(), "Expecting AADL Package or Property Set",
					"An AADL package or property set must be the entry point for generating JSON.");
		}

		Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
		JsonParser jp = new JsonParser();
		JsonElement je = jp.parse(jsonValue.toString());

		URI jsonURI = null;
		try {

			jsonURI = makeJsonFile(xtextEditor);
			printJson(jsonURI, gson.toJson(je));

		} catch (CoreException | IOException e) {
			System.err.println("Trouble writing Json representation to filesystem.");
			e.printStackTrace();
		}

		return jsonURI;
	}
}
