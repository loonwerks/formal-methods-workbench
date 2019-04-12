package com.rockwellcollins.atc.agree.analysis.handlers;

public class VerifySingleHandler extends VerifyHandler {

	public VerifySingleHandler() {
		this.programType = ProgramType.Single;

	}

	@Override
	protected String getJobName() {
		return "AGREE - Verify Single Layer";
	}



}
