package com.rockwellcollins.atc.darpacase.architecture.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.rockwellcollins.atc.darpacase.architecture.Activator;

public class CasePreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	private StringFieldEditor propBaggageServerNameFieldEditor;
	private StringFieldEditor propBaggageServerPortFieldEditor;
	private FileFieldEditor propBaggageServerFileFieldEditor;

	public CasePreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("SuitCASE Settings");
	}


	@Override
	public void createFieldEditors() {

		propBaggageServerNameFieldEditor = new StringFieldEditor(CasePreferenceConstants.CASE_PREF_BAGGAGE_SERVER_NAME,
				"Baggage Server Name:", getFieldEditorParent());
		addField(propBaggageServerNameFieldEditor);

		propBaggageServerPortFieldEditor = new StringFieldEditor(CasePreferenceConstants.CASE_PREF_BAGGAGE_SERVER_PORT,
				"Baggage Server Port:", getFieldEditorParent());
		addField(propBaggageServerPortFieldEditor);

		propBaggageServerFileFieldEditor = new FileFieldEditor(
				CasePreferenceConstants.CASE_PREF_BAGGAGE_SERVER_FILENAME,
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
		addField(propBaggageServerFileFieldEditor);

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
