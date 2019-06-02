package com.collins.fmw.cyres.architecture.handlers;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.osate.aadl2.ComponentImplementation;
import org.osate.ui.dialogs.Dialog;

public class AddIsolatorHandler extends AadlHandler {

	@Override
	protected void runCommand(URI uri) {

		// Get the current selection
		EObject eObj = getEObject(uri);
		if (eObj instanceof ComponentImplementation) {
			// Do other stuff
		} else {
			Dialog.showError("Add Isolator", "A process implementation must be selected in order to add isolation.");
		}

	}


}
