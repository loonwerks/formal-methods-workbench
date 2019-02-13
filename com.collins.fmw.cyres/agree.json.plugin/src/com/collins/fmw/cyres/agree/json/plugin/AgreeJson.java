package com.collins.fmw.cyres.agree.json.plugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.utils.EditorUtils;
import org.osate.aadl2.AadlPackage;
import org.osate.aadl2.ModelUnit;
import org.osate.aadl2.PropertySet;

import com.collins.fmw.cyres.util.plugin.Filesystem;
import com.collins.fmw.json.ArrayValue;
import com.collins.fmw.json.Value;
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
		ModelUnit model = (ModelUnit) original;

		AadlTranslate aadlTranslate = new AadlTranslate();

		// Get (recursively) the set of models referenced in this file
		List<ModelUnit> modelUnits = new ArrayList<>();
		getModelDependencies(model, modelUnits);

		ArrayList<Value> modelBuilder = new ArrayList<Value>();
		Iterator<ModelUnit> i = modelUnits.iterator();

		while (i.hasNext()) {

			ModelUnit m = i.next();
			if (m instanceof AadlPackage || m instanceof PropertySet) {
				modelBuilder.add(aadlTranslate.doSwitch(m));
			}

		}

		Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
		JsonParser jp = new JsonParser();
		JsonElement je = jp.parse(ArrayValue.build(modelBuilder).toString());

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

	static private void getModelDependencies(ModelUnit model, List<ModelUnit> modelUnits) {

		// Add the parent package if it's not there, otherwise return
		if (modelUnits.contains(model)) {
			return;
		} else {
			modelUnits.add(model);
		}

		if (model instanceof AadlPackage) {
			AadlPackage pkg = (AadlPackage) model;
			// Look at direct dependencies in private section
			if (pkg.getPrivateSection() != null) {
				for (ModelUnit mu : pkg.getPrivateSection().getImportedUnits()) {
					getModelDependencies(mu, modelUnits);
				}
			}

			// Look at direct dependencies in public section
			if (pkg.getPublicSection() != null) {
				for (ModelUnit mu : pkg.getPublicSection().getImportedUnits()) {
					getModelDependencies(mu, modelUnits);
				}
			}
		}

	}

}
