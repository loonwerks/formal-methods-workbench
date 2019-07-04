package com.collins.fmw.cyres.json.plugin;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Adapters;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.xtext.resource.EObjectAtOffsetHelper;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.outline.impl.EObjectNode;
import org.eclipse.xtext.ui.editor.utils.EditorUtils;
import org.osate.aadl2.ComponentImplementation;
import org.osate.aadl2.Realization;
import org.osate.ge.BusinessObjectSelection;
import org.osate.ui.dialogs.Dialog;

import com.collins.fmw.cyres.util.plugin.ModelHashcode;
import com.collins.fmw.cyres.util.plugin.TraverseProject;
import com.google.gson.JsonObject;

public class TranslateHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		final URI uri = getSelectionURI(HandlerUtil.getCurrentSelection(event));
		if (uri == null) {
			return null;
		}

		final ResourceSet resourceSet = new ResourceSetImpl();
		final EObject eObj = resourceSet.getEObject(uri, true);

		ComponentImplementation ci = null;
		if (eObj instanceof ComponentImplementation) {
			ci = (ComponentImplementation) eObj;
		}

		final IProject project = TraverseProject.getCurrentProject();

		String hashcode = null;
		try {
			hashcode = ModelHashcode.getHashcode(ci);
		} catch (Exception e) {
			;
			hashcode = null;
		}

		final JsonObject header = new JsonObject();
		if (project != null) {
			header.addProperty("project", project.getName());
		}
		if (ci != null) {
			header.addProperty("implementation", ci.getQualifiedName());
		}
		header.addProperty("date", System.currentTimeMillis());
		if (hashcode != null) {
			header.addProperty("hash", hashcode);
		}

		try {
			// Generate json
			Aadl2Json.createJson(header);
		} catch (Exception e) {
			Dialog.showError("JSON Generator", "Unable to export model to JSON format.");
			return null;
		}

		return null;

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