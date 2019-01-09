package com.collins.atc.ace.cyres.architecture.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.collins.atc.ace.cyres.architecture.Activator;

/**
 * Class used to initialize default preference values.
 */
public class CasePreferenceInitializer extends AbstractPreferenceInitializer {
	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(CasePreferenceConstants.CASE_SPLAT_OUTPUT_FILENAME, "fooTheory.sml");
		store.setDefault(CasePreferenceConstants.CASE_BAGGAGE_SERVER_NAME, "baggage-server");
		store.setDefault(CasePreferenceConstants.CASE_BAGGAGE_SERVER_PORT, "127.0.0.1:5000:5000");
		store.setDefault(CasePreferenceConstants.CASE_BAGGAGE_SERVER_FILENAME,
				"docker-image-baggage-server.tar.gz");
		store.setDefault(CasePreferenceConstants.CASE_SUITCASE_OUTPUT_FILENAME, "suitCase.out");
		store.setDefault(CasePreferenceConstants.CASE_DEBUG, false);
	}
}
