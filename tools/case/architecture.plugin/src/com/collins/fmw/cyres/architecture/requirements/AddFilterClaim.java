package com.collins.fmw.cyres.architecture.requirements;

import com.rockwellcollins.atc.resolute.resolute.FunctionDefinition;
import com.rockwellcollins.atc.resolute.resolute.ProveStatement;

public class AddFilterClaim extends BuiltInClaim {

	private static final String ADD_FILTER = "Add_Filter";

	public AddFilterClaim() {
		super(ADD_FILTER);
	}

	@Override
	public FunctionDefinition buildClaimDefinition() {
		return null;
	}

	@Override
	public ProveStatement buildClaimCall() {
		return null;
	}

//	@Override
//	public boolean modifyClaimDefinition(FunctionDefinition funDef) {
//
//		// Get the Add_Filter function definition from the CASE package
//
//		// Create the Add_Filter claim call
//
//		// Add the claim call to the funDef body
//
//		return false;
//	}
//
//	@Override
//	public boolean modifyClaimCall(ProveStatement prove) {
//
//		// Get the claim call from the prove statement
//
//		// Get the args from the claim call
//
//		// Add additional args for the filter model transformation
//
//		return false;
//	}

}
