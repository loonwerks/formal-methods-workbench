package com.collins.fmw.cyres.architecture.requirements;

import com.rockwellcollins.atc.resolute.resolute.FunctionDefinition;
import com.rockwellcollins.atc.resolute.resolute.ProveStatement;

public class BaseClaim extends BuiltInClaim {

	public final static String BASE_CLAIM = null;

	public BaseClaim() {
		super(BASE_CLAIM);
	}

	@Override
	public FunctionDefinition buildClaimDefinition() {

		ClaimBuilder builder = new ClaimBuilder(req.getId());
		builder.addArg(Create.arg("c", Create.baseType("component")));
		builder.addClaimString("[" + req.getType() + "] " + req.getText());
		builder.addClaimExpr(Create.FALSE());

		return builder.build();

	}

	@Override
	public ProveStatement buildClaimCall() {
		ClaimCallBuilder builder = new ClaimCallBuilder(this.claimDefinition);
		builder.addArg(Create.THIS());
		return builder.build();
	}

}
