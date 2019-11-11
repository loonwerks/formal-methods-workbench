package com.collins.fmw.cyres.splat.preferences;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.collins.fmw.cyres.splat.Activator;

public class SplatPreferenceInitializer extends AbstractPreferenceInitializer {
	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(SplatPreferenceConstants.CHECK_PROPERTIES, false);
		store.setDefault(SplatPreferenceConstants.ASSURANCE_LEVEL, SplatPreferenceConstants.ASSURANCE_LEVEL_BASIC);
		store.setDefault(SplatPreferenceConstants.ASSURANCE_LEVEL_BASIC, true);
		store.setDefault(SplatPreferenceConstants.ASSURANCE_LEVEL_CAKE, false);
		store.setDefault(SplatPreferenceConstants.ASSURANCE_LEVEL_HOL, false);
		store.setDefault(SplatPreferenceConstants.ASSURANCE_LEVEL_FULL, false);
		store.setDefault(SplatPreferenceConstants.INTEGER_WIDTH, 32);
		store.setDefault(SplatPreferenceConstants.OPTIMIZE, false);
		store.setDefault(SplatPreferenceConstants.ENDIAN, SplatPreferenceConstants.ENDIAN_LITTLE);
		store.setDefault(SplatPreferenceConstants.ENDIAN_LITTLE, true);
		store.setDefault(SplatPreferenceConstants.ENDIAN_BIG, false);
		store.setDefault(SplatPreferenceConstants.ENCODING, SplatPreferenceConstants.ENCODING_TWOS_COMP);
		store.setDefault(SplatPreferenceConstants.ENCODING_UNSIGNED, false);
		store.setDefault(SplatPreferenceConstants.ENCODING_TWOS_COMP, true);
		store.setDefault(SplatPreferenceConstants.ENCODING_SIGN_MAG, false);
		store.setDefault(SplatPreferenceConstants.ENCODING_ZIGZAG, false);
		store.setDefault(SplatPreferenceConstants.OUTPUT_DIRECTORY,
				ResourcesPlugin.getWorkspace().getRoot().getLocation().toString());
	}
}
