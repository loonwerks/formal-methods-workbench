package com.rockwellcollins.atc.darpacase.requirements.fromTA6;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.resource.EObjectAtOffsetHelper;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.utils.EditorUtils;
import org.osate.aadl2.ComponentCategory;
import org.osate.aadl2.Property;
import org.osate.aadl2.SystemImplementation;
import org.osate.aadl2.instance.ComponentInstance;
import org.osate.aadl2.instance.ConnectionInstance;
import org.osate.aadl2.instance.SystemInstance;
import org.osate.aadl2.instantiation.InstantiateModel;

import com.rockwellcollins.atc.darpacase.requirements.api.InteractionAPI;

public class JsonHandler extends AbstractHandler {

	private IWorkbenchWindow window;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		XtextEditor xtextEditor = EditorUtils.getActiveXtextEditor();
		this.window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		TextSelection ts = (TextSelection) xtextEditor.getSelectionProvider().getSelection();

		xtextEditor.getDocument().readOnly(resource -> {
			EObject e = new EObjectAtOffsetHelper().resolveContainedElementAt(resource, ts.getOffset());

			SystemImplementation sysImpl = EcoreUtil2.getContainerOfType(e, SystemImplementation.class);

			if(sysImpl == null) {
				MessageDialog.openError(window.getShell(), "System Implementation Not Found",
						"Please place the cursor inside a valid system implementation.");
				return null;
			}

			SystemInstance si = null;
			try {
				si = InstantiateModel.buildInstanceModelFile(sysImpl);
			} catch (Exception ex) {
				MessageDialog.openError(window.getShell(), "Model Instantiation",
						"Error while instantiating the model: " + ex.getMessage());
			}

			if (si == null) {
				return null;
			}

			InteractionAPI api = new InteractionAPI(si);

			Map<ConnectionInstance, Set<ComponentInstance>> connMap = api.getConnectedComponents(api.instance);

			EList<ComponentInstance> all = api.instance.getAllComponentInstances();
			EList<ComponentInstance> busses = api.instance.getAllComponentInstances(ComponentCategory.BUS);
			EList<ComponentInstance> devices = api.instance.getAllComponentInstances(ComponentCategory.DEVICE);
			EList<ComponentInstance> processes = api.instance.getAllComponentInstances(ComponentCategory.THREAD);

			List<ComponentInstance> software = api.getSoftwareComponents(api.instance);

			ComponentInstance bus1 = busses.get(0);
			List<Property> bus1properties = api.getAppliedProperties(bus1);

			return null;
		});
		return null;
	}
}
