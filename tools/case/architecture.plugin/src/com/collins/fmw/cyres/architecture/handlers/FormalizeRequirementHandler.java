package com.collins.fmw.cyres.architecture.handlers;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.utils.EditorUtils;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;
import org.osate.ui.dialogs.Dialog;

import com.collins.fmw.cyres.architecture.requirements.RequirementsManager;
import com.rockwellcollins.atc.resolute.resolute.Expr;
import com.rockwellcollins.atc.resolute.resolute.FnCallExpr;
import com.rockwellcollins.atc.resolute.resolute.FunctionDefinition;
import com.rockwellcollins.atc.resolute.resolute.ProveStatement;

public class FormalizeRequirementHandler extends AadlHandler {

	@Override
	protected void runCommand(URI uri) {

		// Check if a requirement is selected
		// It can either be the prove statement in the component or the claim in the private section
		EObject eObj = getEObject(uri);
		FunctionDefinition funDef = null;
		while (eObj != null) {
			if (eObj instanceof FunctionDefinition) {
				funDef = (FunctionDefinition) eObj;
				break;
			} else if (eObj instanceof ProveStatement) {
				ProveStatement prove = (ProveStatement) eObj;
				Expr expr = prove.getExpr();
				if (expr instanceof FnCallExpr) {
					FnCallExpr fnCallExpr = (FnCallExpr) expr;
					funDef = fnCallExpr.getFn();
					break;
				}
			} else {
				eObj = eObj.eContainer();
			}
		}

		if (funDef == null) {
			Dialog.showError("Formalize Requirement",
					"A requirement must be selected to add a corresponding AGREE statement.");
			return;
		}

		final String reqId = funDef.getName();

		// Get the active xtext editor so we can make modifications
		final XtextEditor xtextEditor = EditorUtils.getActiveXtextEditor();

		xtextEditor.getDocument().modify(new IUnitOfWork.Void<XtextResource>() {

			@Override
			public void process(final XtextResource resource) throws Exception {

				if (!RequirementsManager.getInstance().formalizeRequirement(reqId, resource)) {
					Dialog.showError("Formalize Requirement",
							"Selection does not appear to be an imported cyber requirement.");
					return;
				}

			}
		});
	}

}
