package com.rockwellcollins.atc.agree.analysis.handlers;

public class VerifyMonolithicHandler extends VerifyHandler {
	public VerifyMonolithicHandler() {
		this.programType = ProgramType.Monolithic;
	}

	@Override
	protected String getJobName() {
		return "AGREE - Verify Monolithically";
	}

}
