package com.rockwellcollins.atc.agree.analysis.handlers;

public class VerifyAllHandler extends VerifyHandler {

	public VerifyAllHandler() {
		this.programType = ProgramType.Recursive;

	}

	@Override
	protected String getJobName() {
		return "AGREE - Verify All Layers";
	}



}
