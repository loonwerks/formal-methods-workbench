package com.rockwellcollins.atc.agree.analysis.handlers;

import org.eclipse.jface.preference.IPreferenceStore;

import com.rockwellcollins.atc.agree.analysis.Activator;
import com.rockwellcollins.atc.agree.analysis.preferences.PreferenceConstants;

public class VerifySingleHandler extends VerifyHandler {

	public VerifySingleHandler() {
		this.programType = ProgramType.Single;
		IPreferenceStore prefs = Activator.getDefault().getPreferenceStore();
		String solver = prefs.getString(PreferenceConstants.PREF_SOLVER);
		if (solver != PreferenceConstants.SOLVER_Z3) {
			throw new RuntimeException("You must select Z3 as your solver to check realizability.");
		}
	}


	@Override
	protected String getJobName() {
		return "AGREE - Verify Single Layer";
	}



}
