package com.collins.fmw.cyres.json.plugin;

import java.io.IOException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Adapters;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.xtext.resource.EObjectAtOffsetHelper;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.outline.impl.EObjectNode;
import org.eclipse.xtext.ui.editor.utils.EditorUtils;
import org.osate.aadl2.Realization;
import org.osate.aadl2.modelsupport.resources.OsateResourceUtil;
import org.osate.ge.BusinessObjectSelection;

import com.collins.fmw.cyres.util.plugin.Filesystem;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

public class TranslateHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		URI uri = getSelectionURI(HandlerUtil.getCurrentSelection(event));
		if (uri == null) {
			return null;
		}

		XtextResourceSet resourceSet = OsateResourceUtil.getResourceSet();
		EObject object = resourceSet.getEObject(uri, true);
		JsonElement json = Aadl2Json.toJson(object);

		Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();

		try {
			URI jsonURI = Aadl2Json.makeJsonFile(uri);
			IFile jsonIFile = Filesystem.getFile(jsonURI);
			Filesystem.writeFile(jsonIFile, gson.toJson(json).getBytes());
		} catch (CoreException | IOException e) {
			e.printStackTrace();
		}
		return gson;

	}

	private URI getSelectionURI(ISelection currentSelection) {
		if (currentSelection instanceof IStructuredSelection) {
			IStructuredSelection iss = (IStructuredSelection) currentSelection;
			if (iss.size() == 1 && iss.getFirstElement() instanceof EObjectNode) {
				EObjectNode node = (EObjectNode) iss.getFirstElement();
				return node.getEObjectURI();
			} else {
				final BusinessObjectSelection bos = Adapters.adapt(currentSelection, BusinessObjectSelection.class);
				if (bos != null) {
					if (bos.boStream(EObject.class).count() == 1) {
						return bos.boStream(EObject.class).findFirst().map(e -> EcoreUtil.getURI(e)).orElse(null);
					}
				}
			}
		} else if (currentSelection instanceof TextSelection) {
			// Selection may be stale, get latest from editor
			XtextEditor xtextEditor = EditorUtils.getActiveXtextEditor();
			TextSelection ts = (TextSelection) xtextEditor.getSelectionProvider().getSelection();
			return xtextEditor.getDocument().readOnly(resource -> {
				EObject e = new EObjectAtOffsetHelper().resolveContainedElementAt(resource, ts.getOffset());
				if (e instanceof Realization) {
					e = e.eContainer();
				}
				return EcoreUtil.getURI(e);
			});
		}
		return null;
	}



}