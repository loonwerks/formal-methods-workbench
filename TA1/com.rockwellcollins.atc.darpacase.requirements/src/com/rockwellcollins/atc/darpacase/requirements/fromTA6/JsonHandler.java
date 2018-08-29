package com.rockwellcollins.atc.darpacase.requirements.fromTA6;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.utils.EditorUtils;
import org.osate.aadl2.AadlPackage;
import org.osate.aadl2.ComponentImplementation;
import org.osate.aadl2.ComponentType;
import org.osate.aadl2.ModelUnit;
import org.osate.aadl2.PropertyAssociation;

import com.rockwellcollins.atc.darpacase.requirements.api.InteractionAPI;

public class JsonHandler extends AbstractHandler {

	private IWorkbenchWindow window;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		XtextEditor xtextEditor = EditorUtils.getActiveXtextEditor();
		window = HandlerUtil.getActiveWorkbenchWindowChecked(event);

		if (xtextEditor == null) {
			MessageDialog.openError(window.getShell(), "No AADL editor is active",
					"An AADL editor must be active in order to generate JSON.");
			return null;
		}

		EObject original = xtextEditor.getDocument().readOnly(resource -> resource.getContents().get(0));
		ModelUnit model = (ModelUnit) EcoreUtil.copy(original);

		AadlPackage pkg = null;
		if (model instanceof AadlPackage) {
			pkg = (AadlPackage) model;
		} else {
			MessageDialog.openError(window.getShell(), "Expecting AADL Package",
					"A AADL package must be the entry point for generating JSON.");
			return null;
		}

		InteractionAPI api = new InteractionAPI(pkg);
		List<ComponentImplementation> components = api.getImplementations();

		Map<ComponentImplementation, ComponentType> map = new HashMap<>();
		Map<ComponentImplementation, List<PropertyAssociation>> properties = new HashMap<>();

		for (ComponentImplementation ci : components) {
			map.put(ci, InteractionAPI.getComponentType(ci));
			properties.put(ci, api.getComponentTA1Properties(ci));
		}

		ComponentType ct = InteractionAPI.getComponentType(components.get(0));
		List<ComponentImplementation> prototypes = api.getImplementations(ct);

		// create the IR
//		Translate translate = new Translate();
//		Value v = translate.doSwitch(pkg);
//		Gson gson = new GsonBuilder().setPrettyPrinting().create();
//		JsonParser jp = new JsonParser();
//		JsonElement je = jp.parse(v.toString());
//
//		try {
//			printJson(xtextEditor, gson.toJson(je));
//		} catch (CoreException | IOException e) {
//			System.err.println("Trouble writing Json representation to filesystem.");
//			e.printStackTrace();
//		}

		return null;
	}

//	private void printJson(XtextEditor state, String whatToPrint) throws CoreException, IOException {
//		XtextResource resource = state.getDocument().readOnly(r -> r);
//
//		URI dan = resource.getURI();
//		URI folder = dan.trimSegments(1);
//		String base = FilesystemUtils.getBase(dan);
//
//		URI writeFolder = FilesystemUtils.createFolder(folder, new String[] { "json-generated" });
//		URI json = writeFolder.appendSegment(base).appendFileExtension("json");
//
//		IFile print = FilesystemUtils.getFile(json);
//		FilesystemUtils.writeFile(print, whatToPrint.getBytes());
//	}
}
