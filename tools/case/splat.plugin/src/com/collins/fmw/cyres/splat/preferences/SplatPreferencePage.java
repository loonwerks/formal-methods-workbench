package com.collins.fmw.cyres.splat.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.collins.fmw.cyres.splat.Activator;

public class SplatPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

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
		addField(new IntegerWidthFieldEditor(SplatPreferenceConstants.INTEGER_WIDTH, "Integer Width",
				SplatPreferenceConstants.OPTIMIZE, "Optimize", getFieldEditorParent()));

		// Endianess
		addField(new RadioGroupFieldEditor(SplatPreferenceConstants.ENDIAN, "Endian", 2, new String[][] {
				{ "Little", SplatPreferenceConstants.ENDIAN_LITTLE }, { "Big", SplatPreferenceConstants.ENDIAN_BIG } },
				getFieldEditorParent(), true));

		// Encoding
		addField(new RadioGroupFieldEditor(SplatPreferenceConstants.ENCODING, "Encoding", 2,
				new String[][] { { "Unsigned", SplatPreferenceConstants.ENCODING_UNSIGNED },
						{ "Two's Compliment", SplatPreferenceConstants.ENCODING_TWOS_COMP },
						{ "Signed Magnitude", SplatPreferenceConstants.ENCODING_SIGN_MAG },
						{ "ZigZag", SplatPreferenceConstants.ENCODING_ZIGZAG } },
				getFieldEditorParent(), true));

	}

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("SPLAT Settings");
	}

}
