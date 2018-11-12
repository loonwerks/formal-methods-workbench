package com.rockwellcollins.atc.darpacase.architecture.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.rockwellcollins.atc.darpacase.architecture.Activator;

public class CasePreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	private FileFieldEditor splatOutputFileFieldEditor;
	private StringFieldEditor baggageServerNameFieldEditor;
	private StringFieldEditor baggageServerPortFieldEditor;
	private FileFieldEditor baggageServerFileFieldEditor;
	private FileFieldEditor suitCaseOutputFileFieldEditor;

	public CasePreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("CASE Settings");
	}


	@Override
	public void createFieldEditors() {

		splatOutputFileFieldEditor = new FileFieldEditor(CasePreferenceConstants.CASE_SPLAT_OUTPUT_FILENAME,
				"SPLAT output filename:", true, getFieldEditorParent()) {

			@Override
			protected String changePressed() {

				FileDialog dlgSaveAs = new FileDialog(getShell(), SWT.SAVE | SWT.SHEET);
				dlgSaveAs.setText("SPLAT theory file");
				if (!getTextControl().getText().isEmpty()) {
					dlgSaveAs.setFileName(getTextControl().getText());
				} else {
					dlgSaveAs.setFileName("fooTheory.sml");
				}
				dlgSaveAs.setOverwrite(false);
				dlgSaveAs.setFilterExtensions(new String[] { "*.sml", "*.*" });
				String fileName = dlgSaveAs.open();
				if (fileName == null) {
					return null;
				} else {
					fileName = fileName.trim();
				}

				return fileName;
			}

			@Override
			protected boolean checkState() {
				// Don't want to enforce proper path/filenaming
				clearErrorMessage();
				return true;
			}
		};
		addField(splatOutputFileFieldEditor);

		baggageServerNameFieldEditor = new StringFieldEditor(CasePreferenceConstants.CASE_BAGGAGE_SERVER_NAME,
				"Baggage Server Name:", getFieldEditorParent());
		addField(baggageServerNameFieldEditor);

		baggageServerPortFieldEditor = new StringFieldEditor(CasePreferenceConstants.CASE_BAGGAGE_SERVER_PORT,
				"Baggage Server Port:", getFieldEditorParent());
		addField(baggageServerPortFieldEditor);

		baggageServerFileFieldEditor = new FileFieldEditor(
				CasePreferenceConstants.CASE_BAGGAGE_SERVER_FILENAME,
				"Baggage Server filename:", true, getFieldEditorParent()) {

			@Override
			protected String changePressed() {

				FileDialog dlgSaveAs = new FileDialog(getShell());
				dlgSaveAs.setText("Baggage Server image");
				if (!getTextControl().getText().isEmpty()) {
					dlgSaveAs.setFileName(getTextControl().getText());
				} else {
					dlgSaveAs.setFileName("docker-image-baggage-server.tar.gz");
				}
				dlgSaveAs.setOverwrite(false);
				dlgSaveAs.setFilterExtensions(new String[] { "*.tar.gz", "*.*" });
				String fileName = dlgSaveAs.open();
				if (fileName == null) {
					return null;
				} else {
					fileName = fileName.trim();
				}

				return fileName;
			}

		};
		addField(baggageServerFileFieldEditor);

		suitCaseOutputFileFieldEditor = new FileFieldEditor(CasePreferenceConstants.CASE_SUITCASE_OUTPUT_FILENAME,
				"SuitCASE output filename:", true, getFieldEditorParent()) {

			@Override
			protected String changePressed() {

				FileDialog dlgSaveAs = new FileDialog(getShell(), SWT.SAVE | SWT.SHEET);
				dlgSaveAs.setText("SuitCASE output file name");
				if (!getTextControl().getText().isEmpty()) {
					dlgSaveAs.setFileName(getTextControl().getText());
				} else {
					dlgSaveAs.setFileName("");
				}
				dlgSaveAs.setOverwrite(false);
				dlgSaveAs.setFilterExtensions(new String[] { "*.out", "*.*" });
				String fileName = dlgSaveAs.open();
				if (fileName == null) {
					return null;
				} else {
					fileName = fileName.trim();
				}

				return fileName;
			}

			@Override
			protected boolean checkState() {
				// Don't want to enforce proper path/filenaming
				clearErrorMessage();
				return true;
			}
		};
		addField(suitCaseOutputFileFieldEditor);

	}

//	@Override
//	public void propertyChange(PropertyChangeEvent event) {
//		super.propertyChange(event);
//	}

//	@Override
//	protected void performDefaults() {
//		super.performDefaults();
//	}

	@Override
	protected void initialize() {
		super.initialize();
	}

	@Override
	public void init(IWorkbench workbench) {
	}

}
