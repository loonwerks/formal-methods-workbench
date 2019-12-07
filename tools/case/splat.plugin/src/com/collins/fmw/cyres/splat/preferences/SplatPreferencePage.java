package com.collins.fmw.cyres.splat.preferences;

import java.io.File;
import java.io.IOException;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.osate.ui.dialogs.Dialog;

import com.collins.fmw.cyres.splat.Activator;

public class SplatPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	private BooleanFieldEditor splatLogFieldEditor = null;
	private FileFieldEditor splatLogFileFieldEditor = null;

	public SplatPreferencePage() {
		super(GRID);
	}

	@Override
	public void createFieldEditors() {

		Label label = null; // separator

		// Assurance level
		addField(new RadioGroupFieldEditor(SplatPreferenceConstants.ASSURANCE_LEVEL, "Assurance level", 4,
				new String[][] { { "Basic", SplatPreferenceConstants.ASSURANCE_LEVEL_BASIC },
						{ "CakeML", SplatPreferenceConstants.ASSURANCE_LEVEL_CAKE },
						{ "HOL", SplatPreferenceConstants.ASSURANCE_LEVEL_HOL },
						{ "Full", SplatPreferenceConstants.ASSURANCE_LEVEL_FULL } },
				getFieldEditorParent(), true));

		// Prove regexp properties
		addField(new BooleanFieldEditor(SplatPreferenceConstants.CHECK_PROPERTIES, "Check properties",
				getFieldEditorParent()));

		label = new Label(getFieldEditorParent(), SWT.NONE);
		label.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 3, 1));

		// Output directory
		addField(new DirectoryFieldEditor(SplatPreferenceConstants.OUTPUT_DIRECTORY, "Output directory",
				getFieldEditorParent()));

		label = new Label(getFieldEditorParent(), SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 3, 2));

		// Integer width / optimize
		addField(new IntegerWidthFieldEditor(SplatPreferenceConstants.INTEGER_WIDTH, "Integer width",
				SplatPreferenceConstants.OPTIMIZE, "Optimize", getFieldEditorParent()));

		// Endianess
		addField(new RadioGroupFieldEditor(SplatPreferenceConstants.ENDIAN, "Endianess", 2, new String[][] {
				{ "Little", SplatPreferenceConstants.ENDIAN_LITTLE }, { "Big", SplatPreferenceConstants.ENDIAN_BIG } },
				getFieldEditorParent(), true));

		// Encoding
		addField(new RadioGroupFieldEditor(SplatPreferenceConstants.ENCODING, "Integer encoding", 2,
				new String[][] { { "Unsigned", SplatPreferenceConstants.ENCODING_UNSIGNED },
						{ "Two's Compliment", SplatPreferenceConstants.ENCODING_TWOS_COMP },
						{ "Signed Magnitude", SplatPreferenceConstants.ENCODING_SIGN_MAG },
						{ "ZigZag", SplatPreferenceConstants.ENCODING_ZIGZAG } },
				getFieldEditorParent(), true));

		splatLogFieldEditor = new BooleanFieldEditor(SplatPreferenceConstants.GENERATE_LOG, "Generate SPLAT run log",
				getFieldEditorParent());
		addField(splatLogFieldEditor);

		splatLogFileFieldEditor = new FileFieldEditor(SplatPreferenceConstants.LOG_FILENAME, "SPLAT log filename:",
				true, getFieldEditorParent()) {

			@Override
			protected String changePressed() {

				FileDialog dlgSaveAs = new FileDialog(getShell(), SWT.SAVE | SWT.SHEET);
				dlgSaveAs.setText("SPLAT log file");
				if (!getTextControl().getText().isEmpty()) {
					dlgSaveAs.setFileName(getTextControl().getText());
				} else {
					dlgSaveAs.setFileName("splat.log");
				}
				dlgSaveAs.setOverwrite(false);
				dlgSaveAs.setFilterExtensions(new String[] { "*.log", "*.*" });
				String fileName = dlgSaveAs.open();
				if (fileName == null) {
					return null;
				} else {
					fileName = fileName.trim();
				}

				// Create the file if it doesn't exist
				try {
					File file = new File(fileName);
					file.createNewFile();
				} catch (IOException e) {
					Dialog.showError("SPLAT log file - Error", "A problem occurred while creating the file.");
					return null;
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
		addField(splatLogFileFieldEditor);

	}

	private void configureEnabledFieldEditors() {
		splatLogFileFieldEditor.setEnabled(splatLogFieldEditor.getBooleanValue(), getFieldEditorParent());
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		super.propertyChange(event);
		configureEnabledFieldEditors();
	}

	@Override
	protected void performDefaults() {
		super.performDefaults();
		configureEnabledFieldEditors();
	}

	@Override
	protected void initialize() {
		super.initialize();
		configureEnabledFieldEditors();
	}

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("SPLAT Settings");
	}

}
