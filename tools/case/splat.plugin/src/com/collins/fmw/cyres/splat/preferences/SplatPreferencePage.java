package com.collins.fmw.cyres.splat.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.collins.fmw.cyres.splat.Activator;

public class SplatPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public SplatPreferencePage() {
		super(GRID);
	}

	@Override
	public void createFieldEditors() {

		// Prove regexp properties
		addField(new BooleanFieldEditor(SplatPreferenceConstants.PROVE_REGEXP_PROPERTIES, "Prove regexp properties",
				getFieldEditorParent()));

		// Compilation assurance level
		addField(new RadioGroupFieldEditor(SplatPreferenceConstants.COMPILATION_ASSURANCE_LEVEL,
				"Compilation assurance level", 1,
				new String[][] { { "Generate C code", SplatPreferenceConstants.COMPILATION_ASSURANCE_LEVEL_STANDARD },
						{ "Generate CakeML and invoke CakeML binary to compile",
								SplatPreferenceConstants.COMPILATION_ASSURANCE_LEVEL_HIGH },
						{ "Generate CakeML and invoke CakeML binary to compile, running the CakeML compiler in the logic",
								SplatPreferenceConstants.COMPILATION_ASSURANCE_LEVEL_FANATIC } },
				getFieldEditorParent(), true));

		// Generate Bitcodec file
		addField(new BooleanFieldEditor(SplatPreferenceConstants.GENERATE_BITCODEC_FILE, "Generate bitcodec file",
				getFieldEditorParent()));

		// Output directory
		addField(new DirectoryFieldEditor(SplatPreferenceConstants.OUTPUT_DIRECTORY, "Output directory",
				getFieldEditorParent()));

	}

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("SPLAT Settings");
	}

}
