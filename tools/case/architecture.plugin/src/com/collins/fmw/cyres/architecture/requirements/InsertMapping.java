package com.collins.fmw.cyres.architecture.requirements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.xtext.ui.editor.XtextEditor;

public class InsertMapping {

	public Map<IFile, List<CyberRequirement>> map = new HashMap<>();
	private XtextEditor mainEditor;

	public void setMainEditor(XtextEditor xtextEditor) {
		this.mainEditor = xtextEditor;
	}

	public void insert(CyberRequirement req) {
		IFile key = req.getContainingFile();
		List<CyberRequirement> requirements = new ArrayList<>();
		if (map.containsKey(key)) {
			requirements = map.get(key);
		}
		requirements.add(req);
		map.put(key, requirements);
	}

	public void insertIntoFiles() {
		for (IFile f : map.keySet()) {
			insertIntoFile(f);
		}
	}

	private void insertIntoFile(IFile f) {
		List<CyberRequirement> toInsert = map.get(f);

		XtextEditor editor = getEditor(f);

		if (editor != null) {
			editor.getDocument().modify(resource -> {
				for (CyberRequirement r : toInsert) {
					r.insert(resource);
				}
				return null;
			});

		}

		closeEditor(editor, true);
	}

	public static XtextEditor getEditor(IFile file) {
		IWorkbenchPage page = null;
		IEditorPart part = null;

		if (file.exists()) {
			page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			IEditorDescriptor desc = PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor(file.getName());
			try {
				part = page.openEditor(new FileEditorInput(file), desc.getId());
			} catch (PartInitException e) {
				e.printStackTrace();
			}
		}

		if (part == null) {
			return null;
		}

		XtextEditor xedit = null;
		xedit = (XtextEditor) part;

		return xedit;
	}

	public void closeEditor(XtextEditor editor, boolean save) {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if (save) {
			page.saveEditor(editor, false);
		}

		if (editor.equals(this.mainEditor)) {
			return;
		} else {
			page.closeEditor(editor, false);
		}
	}
}
