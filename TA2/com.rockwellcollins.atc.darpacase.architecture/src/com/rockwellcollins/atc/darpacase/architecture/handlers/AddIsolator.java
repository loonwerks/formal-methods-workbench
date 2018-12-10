package com.rockwellcollins.atc.darpacase.architecture.handlers;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
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
import org.osate.aadl2.ComponentType;
import org.osate.aadl2.impl.ComponentImplementationImpl;
import org.osate.aadl2.instance.ComponentInstance;
import org.osate.aadl2.instance.SystemInstance;
import org.osate.aadl2.instance.impl.ComponentInstanceImpl;
import org.osate.aadl2.instantiation.InstantiateModel;
import org.osate.ui.dialogs.Dialog;

/**
 * This is a canned example in which a system instance is generated from
 * a selected component implementation.  The system instance is then searched
 * for all component instances that have the "Trusted_Component" property.
 */

public class AddIsolator extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) {

		// Get the current selection
		URI uri = getSelectionURI(HandlerUtil.getCurrentSelection(event));
		if (uri == null) {
			return null;
		}

		EObject eObj = getEObject(uri);
		if (eObj instanceof ComponentImplementation) {
			Set<ComponentType> trustedComponents = getTrustedComponents(eObj);
			// Do other stuff
		} else {
			Dialog.showError("No component implementation selected",
					"A component implementation must be selected in order to perform this analysis.");
		}

		return null;
	}

	/**
	 * Gets the URI for the current selection in the editor
	 * @param currentSelection - Selected model object
	 * @return A URI representing the selected model object
	 */
	private URI getSelectionURI(ISelection currentSelection) {

		if (currentSelection instanceof IStructuredSelection) {
			final IStructuredSelection iss = (IStructuredSelection) currentSelection;
			if (iss.size() == 1) {
				final Object obj = iss.getFirstElement();
				return ((EObjectNode) obj).getEObjectURI();
			}
		} else if (currentSelection instanceof TextSelection) {
			XtextEditor xtextEditor = EditorUtils.getActiveXtextEditor();
			TextSelection ts = (TextSelection) xtextEditor.getSelectionProvider().getSelection();
			return xtextEditor.getDocument().readOnly(resource -> {
				EObject e = new EObjectAtOffsetHelper().resolveContainedElementAt(resource, ts.getOffset());
				return EcoreUtil.getURI(e);
			});
		}
		return null;
	}

	/**
	 * Returns the component EObject corresponding to its URI.
	 * @param uri - Component URI
	 * @return EObject
	 */
	private EObject getEObject(URI uri) {
		XtextEditor xtextEditor = EditorUtils.getActiveXtextEditor();
		return xtextEditor.getDocument().readOnly(resource -> {
			return resource.getResourceSet().getEObject(uri, true);
		});
	}

	/**
	 * Returns a set of components that have the CASE::Trusted_Component property.
	 * @param eObj - Component implementation that represents the root node of the model
	 * @return Set of component types that have the "Trusted_Component" property
	 */
	private Set<ComponentType> getTrustedComponents(EObject eObj) {

		Set<ComponentType> trustedComponents = new HashSet<>();

		// Generate system instance
		SystemInstance si;
		ComponentImplementationImpl compImpl = (ComponentImplementationImpl) eObj;
		try {
			si = InstantiateModel.buildInstanceModelFile(compImpl);
		} catch (Exception e) {
			Dialog.showError("Model Instantiation", "Error while instantiating the model: " + e.getMessage());
			return trustedComponents;
		}

		// Iterate through the components in the implementation
		// and add the legacy components to the set (duplicates will be ignored)
		EList<ComponentInstance> compInstances = si.getAllComponentInstances();
		for (ComponentInstance compInstance : compInstances) {
			// Get component type from instance
			ComponentInstanceImpl compInstanceImpl = (ComponentInstanceImpl) compInstance;
			ComponentType component = (ComponentType) compInstanceImpl.getComponentClassifier();
			// Check if this is a trusted component. If so, add it to the trusted components set
			if (component.getPropertyValues("CASE", "Trusted_Component").size() > 0) {
				trustedComponents.add(component);
			}
		}

		return trustedComponents;

	}
}
