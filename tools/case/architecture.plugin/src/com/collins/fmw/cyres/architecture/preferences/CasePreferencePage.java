package com.collins.fmw.cyres.architecture.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.collins.fmw.cyres.architecture.Activator;

public class CasePreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

//	private FileFieldEditor splatOutputFileFieldEditor;

	public CasePreferencePage() {
		super(GRID);
	}


	@Override
	public void createFieldEditors() {

//		splatOutputFileFieldEditor = new FileFieldEditor(CasePreferenceConstants.CASE_SPLAT_OUTPUT_FILENAME,
//				"SPLAT output filename:", true, getFieldEditorParent()) {
//
//			@Override
//			protected String changePressed() {
//
//				FileDialog dlgSaveAs = new FileDialog(getShell(), SWT.SAVE | SWT.SHEET);
//				dlgSaveAs.setText("SPLAT theory file");
//				if (!getTextControl().getText().isEmpty()) {
//					dlgSaveAs.setFileName(getTextControl().getText());
//				} else {
//					dlgSaveAs.setFileName("SWTheory.sml");
//				}
//				dlgSaveAs.setOverwrite(false);
//				dlgSaveAs.setFilterExtensions(new String[] { "*.sml", "*.*" });
//				String fileName = dlgSaveAs.open();
//				if (fileName == null) {
//					return null;
//				} else {
//					fileName = fileName.trim();
//				}
//
//				return fileName;
//			}
//
//			@Override
//			protected boolean checkState() {
//				// Don't want to enforce proper path/filenaming
//				clearErrorMessage();
//				return true;
//			}
//		};
//		addField(splatOutputFileFieldEditor);

	}

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("CASE Settings");
	}

}
