package com.collins.fmw.cyres.architecture.handlers;

import java.util.Collection;
import java.util.TreeSet;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.xtext.resource.EObjectAtOffsetHelper;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.outline.impl.EObjectNode;
import org.eclipse.xtext.ui.editor.utils.EditorUtils;
import org.osate.aadl2.AadlPackage;
import org.osate.aadl2.ModelUnit;
import org.osate.aadl2.NamedElement;
import org.osate.aadl2.PackageSection;
import org.osate.aadl2.Realization;
import org.osate.aadl2.modelsupport.util.AadlUtil;

public abstract class AadlHandler extends AbstractHandler {

	abstract protected void runCommand(URI uri);

	static final String OUTLINE_VIEW_PART_ID = "org.eclipse.ui.views.ContentOutline";
	protected ExecutionEvent executionEvent;
	private IWorkbenchWindow window;

	@Override
	public Object execute(ExecutionEvent event) {

		this.executionEvent = event;
		window = HandlerUtil.getActiveWorkbenchWindow(event);
		if (window == null) {
			return null;
		}

		// Save changes
		if (!saveChanges(window.getActivePage().getDirtyEditors())) {
			return null;
		}

		// Get the current selection
		ISelection selection = null;
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IViewPart viewPart = page.findView(OUTLINE_VIEW_PART_ID);
		if (viewPart == null) {
			selection = HandlerUtil.getCurrentSelection(event);
		} else {
			IViewSite viewSite = viewPart.getViewSite();
			ISelectionProvider selectionProvider = viewSite.getSelectionProvider();
			selection = selectionProvider.getSelection();
		}

		// TODO: Handle same functionality in the Graphical Editor?
		URI uri = getSelectionURI(selection);
		if (uri == null) {
			return null;
		}

		// Run the command in the handler
		runCommand(uri);

		return null;
	}

	/**
	 * Returns the component EObject corresponding to its URI.
	 * @param uri - Component URI
	 * @return EObject
	 */
	protected EObject getEObject(URI uri) {
		XtextEditor xtextEditor = EditorUtils.getActiveXtextEditor();
		return xtextEditor.getDocument().readOnly(resource -> {
			return resource.getResourceSet().getEObject(uri, true);
		});
	}

	/**
	 * Gets the URI for the current selection in the editor
	 * @param currentSelection - Selected model object
	 * @return A URI representing the selected model object
	 */
//	@SuppressWarnings("restriction")
	private URI getSelectionURI(ISelection currentSelection) {

		if (currentSelection instanceof IStructuredSelection) {
			final IStructuredSelection iss = (IStructuredSelection) currentSelection;
			if (iss.size() == 1) {
				final Object obj = iss.getFirstElement();
//				if (obj instanceof DiagramElement) {
//					final DiagramElement diagramElement = (DiagramElement) obj;
//					EObject eObj = (EObject) diagramElement.getBusinessObject();
//					return EcoreUtil.getURI(eObj);
//				} else {
				return ((EObjectNode) obj).getEObjectURI();
//				}
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


	/**
	 * Returns the index of a component with the specified name in the specified element list.
	 * TODO: What if the name isn't in the list?
	 * @param compName - Component name
	 * @param elements - Collection of elements
	 * @return An identifier that is unique in the specified list
	 */
	protected int getIndex(String compName, final Collection<? extends NamedElement> elements) {
		int idx = 0;

		for (NamedElement e : elements) {
			if (e.getName().equalsIgnoreCase(compName)) {
				break;
			} else {
				idx++;
			}
		}
		return idx;
	}

	/**
	 * Builds an identifier using the specified base name that doesn't conflict with identifiers in the specified element list.
	 * @param baseIdentifier - Name
	 * @param startWithBase - If true, if the baseIdentifier is the only one of its kind,
	 * it will be returned as 'baseIdentifier' rather than 'baseIdentifier1'
	 * @param elements - Collection of names which cannot match base name
	 * @return An identifier that is unique in the specified list
	 */
	protected String getUniqueName(String baseIdentifier, boolean startWithBase,
			final Collection<? extends NamedElement> elements) {

		// Sort names list alphabetically
		TreeSet<String> names = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
		elements.forEach(n -> {
			if (n.getName() != null) {
				names.add(n.getName());
			}
		});

		// Resolve naming conflicts
		String newIdentifier = baseIdentifier + (startWithBase ? "" : "1");
		boolean done = false;
		int num = getLastInt(baseIdentifier);
		if (num > 0) {
			// If the baseIdentifier already has a number at the end, start with it
			baseIdentifier = baseIdentifier.substring(0, baseIdentifier.length() - Integer.toString(num).length());
		} else if (num == 0 && !startWithBase) {
			num = 1;
		}

		do {
			if (names.contains(newIdentifier)) {
				num++;
				newIdentifier = baseIdentifier + num;
			} else {
				done = true;
			}
		} while (!done);

		return newIdentifier;
	}

	/**
	 * Adds the containing package of the specified object to the specified package section's with clause if it isn't already present
	 * @param obj - Object whose containing package needs to be imported
	 * @param pkgSection - Package Section
	 */
	protected void importContainingPackage(EObject obj, PackageSection pkgSection) {

		AadlPackage pkg = AadlUtil.getContainingPackage(obj);
		if (pkg == null) {
			return;
		}
		// Don't add the with clause if it is for the current package
		AadlPackage thisPkg = AadlUtil.getContainingPackage(pkgSection);
		if (thisPkg == null || pkg.getName().equalsIgnoreCase(thisPkg.getName())) {
			return;
		}
		boolean pkgFound = false;
		for (ModelUnit mu : pkgSection.getImportedUnits()) {
			if (mu.getName().equalsIgnoreCase(pkg.getName())) {
				pkgFound = true;
				break;
			}
		}
		if (!pkgFound) {
			pkgSection.getImportedUnits().add(pkg);
		}
	}

	/**
	 * Helper function for getUniqueName() for extracting a number at the end of a string
	 * @param name - String
	 * @return An integer
	 */
	private int getLastInt(String name) {

		int offset = name.length();
		for (int i = name.length() - 1; i >= 0; i--) {
			char c = name.charAt(i);
			if (Character.isDigit(c)) {
				offset--;
			} else {
				if (offset == name.length()) {
					// No int at the end
					return 0;
				}
				return Integer.parseInt(name.substring(offset));
			}
		}
		return Integer.parseInt(name.substring(offset));
	}

}
