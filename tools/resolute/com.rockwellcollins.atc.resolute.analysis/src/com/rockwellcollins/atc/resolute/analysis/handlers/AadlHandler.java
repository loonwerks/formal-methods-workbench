package com.rockwellcollins.atc.resolute.analysis.handlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.resource.EObjectAtOffsetHelper;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.outline.impl.EObjectNode;
import org.eclipse.xtext.ui.editor.utils.EditorUtils;
import org.osate.aadl2.AadlPackage;
import org.osate.aadl2.Classifier;
import org.osate.aadl2.ComponentImplementation;
import org.osate.aadl2.ComponentType;
import org.osate.aadl2.Element;
import org.osate.aadl2.modelsupport.util.AadlUtil;
import org.osate.ui.dialogs.Dialog;

public abstract class AadlHandler extends AbstractHandler {
	private IWorkbenchWindow window;
	private ExecutionEvent executionEvent;

	abstract protected IStatus runJob(Element sel, IProgressMonitor monitor);

	abstract protected String getJobName();

	protected ExecutionEvent getExecutionEvent() {
		return this.executionEvent;
	}

	@Override
	public Object execute(ExecutionEvent event) {
		this.executionEvent = event;

		final URI uri = getSelectionURI(HandlerUtil.getCurrentSelection(event));
		if (uri == null) {
			return null;
		}

		window = HandlerUtil.getActiveWorkbenchWindow(event);
		if (window == null) {
			return null;
		}

		return executeURI(uri);
	}

	public Object executeURI(final URI uri) {
		final XtextEditor xtextEditor = EditorUtils.getActiveXtextEditor();
		if (xtextEditor == null) {
			return null;
		}

		if (!saveChanges(window.getActivePage().getDirtyEditors())) {
			return null;
		}

		WorkspaceJob job = new WorkspaceJob(getJobName()) {
			@Override
			public IStatus runInWorkspace(final IProgressMonitor monitor) {
				return xtextEditor.getDocument().readOnly(resource -> {
					EObject eobj = resource.getResourceSet().getEObject(uri, true);
					if (eobj instanceof Element) {
						return runJob((Element) eobj, monitor);
					} else {
						return Status.CANCEL_STATUS;
					}
				});
			}
		};

		job.setRule(ResourcesPlugin.getWorkspace().getRoot());
		job.schedule();
		return null;
	}

	private boolean saveChanges(IEditorPart[] dirtyEditors) {
		if (dirtyEditors.length == 0) {
			return true;
		}

		if (MessageDialog.openConfirm(window.getShell(), "Save editors", "Save editors and continue?")) {
			NullProgressMonitor monitor = new NullProgressMonitor();
			for (IEditorPart e : dirtyEditors) {
				e.doSave(monitor);
			}
			return true;
		} else {
			return false;
		}
	}


	private URI getSelectionURI(ISelection currentSelection) {

		if (currentSelection instanceof IStructuredSelection) {
			final IStructuredSelection iss = (IStructuredSelection) currentSelection;
			if (iss.size() == 1) {
				final Object obj = iss.getFirstElement();
				return ((EObjectNode) obj).getEObjectURI();
			}
		} else if (currentSelection instanceof TextSelection) {
			// Selection may be stale, get latest from editor
			XtextEditor xtextEditor = EditorUtils.getActiveXtextEditor();
			TextSelection ts = (TextSelection) xtextEditor.getSelectionProvider().getSelection();
			return xtextEditor.getDocument().readOnly(resource -> {
				EObject e = new EObjectAtOffsetHelper().resolveContainedElementAt(resource, ts.getOffset());
				return EcoreUtil.getURI(e);
			});
		}
		return null;
	}

	protected IWorkbenchWindow getWindow() {
		return window;
	}

	private List<ComponentImplementation> getComponentImplementations(ComponentType ct) {
		List<ComponentImplementation> result = new ArrayList<>();
		AadlPackage pkg = AadlUtil.getContainingPackage(ct);
		for (ComponentImplementation ci : EcoreUtil2.getAllContentsOfType(pkg, ComponentImplementation.class)) {
			if (ci.getType().equals(ct)) {
				result.add(ci);
			}
		}
		return result;
	}

	private Classifier getOutermostClassifier(Element element) {
		List<EObject> containers = new ArrayList<>();
		EObject curr = element;
		while (curr != null) {
			containers.add(curr);
			curr = curr.eContainer();
		}
		Collections.reverse(containers);
		for (EObject container : containers) {
			if (container instanceof Classifier) {
				// System.out.println(container);
				return (Classifier) container;
			}
		}
		return null;
	}

	protected ComponentImplementation getComponentImplementation(Element root) {
		Classifier classifier = getOutermostClassifier(root);

		if (classifier instanceof ComponentImplementation) {
			return (ComponentImplementation) classifier;
		}
		if (!(classifier instanceof ComponentType)) {
			Dialog.showError("Model Instantiate", "Must select an AADL Component Type or Implementation");
			return null;
		}
		ComponentType ct = (ComponentType) classifier;
		List<ComponentImplementation> cis = getComponentImplementations(ct);
		if (cis.size() == 0) {
			Dialog.showError("Model Instantiate", "AADL Component Type has no implementation to check");
			return null;
		} else if (cis.size() == 1) {
			ComponentImplementation ci = cis.get(0);
			String message = "User selected " + ct.getFullName() + ". Run checks on " + ci.getFullName() + " instead?";
			if (Dialog.askQuestion("Model Instantiation", message)) {
				return ci;
			} else {
				return null;
			}
		} else {
			Dialog.showError("Model Instantiate",
					"AADL Component Type has multiple implementation to verify: please select just one");
			return null;
		}

	}

}
