package com.collins.fmw.cyres.splat.preferences;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.collins.fmw.cyres.splat.Activator;

public class SplatPreferenceInitializer extends AbstractPreferenceInitializer {
	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(SplatPreferenceConstants.PROVE_REGEXP_PROPERTIES, true);
		store.setDefault(SplatPreferenceConstants.COMPILATION_ASSURANCE_LEVEL,
				SplatPreferenceConstants.COMPILATION_ASSURANCE_LEVEL_HIGH);
		store.setDefault(SplatPreferenceConstants.COMPILATION_ASSURANCE_LEVEL_STANDARD, false);
		store.setDefault(SplatPreferenceConstants.COMPILATION_ASSURANCE_LEVEL_HIGH, true);
		store.setDefault(SplatPreferenceConstants.COMPILATION_ASSURANCE_LEVEL_FANATIC, false);
		store.setDefault(SplatPreferenceConstants.GENERATE_BITCODEC_FILE, false);
		store.setDefault(SplatPreferenceConstants.OUTPUT_DIRECTORY,
				ResourcesPlugin.getWorkspace().getRoot().getLocation().toString());
	}
}
