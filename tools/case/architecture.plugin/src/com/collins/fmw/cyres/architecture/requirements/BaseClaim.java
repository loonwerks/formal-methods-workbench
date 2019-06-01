package com.collins.fmw.cyres.architecture.requirements;

import java.util.ArrayList;
import java.util.List;

import com.rockwellcollins.atc.resolute.resolute.Arg;
import com.rockwellcollins.atc.resolute.resolute.Expr;
import com.rockwellcollins.atc.resolute.resolute.FunctionDefinition;
import com.rockwellcollins.atc.resolute.resolute.ProveStatement;

public class BaseClaim extends BuiltInClaim {

	public final static String BASE_CLAIM = null;

	private final String reqId;
	private final String reqType;
	private final String reqText;
	private final String reqContext;

	private FunctionDefinition claimDef = null;

	public BaseClaim(CyberRequirement requirement) {
		super(BASE_CLAIM);
		this.reqId = requirement.getId();
		this.reqType = requirement.getType();
		this.reqText = requirement.getText();
		this.reqContext = requirement.getContext();
	}

	@Override
	public List<Expr> getCallArgs() {
		List<Expr> callArgs = new ArrayList<>();
		callArgs.add(Create.THIS(this.reqContext));
		return callArgs;
	}

	@Override
	public List<Arg> getDefinitionParams() {
		List<Arg> defParams = new ArrayList<>();
		defParams.add(Create.arg("context", Create.baseType("component")));
		return defParams;
	}

	@Override
	public FunctionDefinition buildClaimDefinition(FunctionDefinition fd) {

		ClaimBuilder builder = new ClaimBuilder(this.reqId);
		List<Arg> defParams = getDefinitionParams();

		for (Arg arg : defParams) {
			builder.addArg(arg);
		}

		builder.addClaimString("[" + this.reqType + "] " + this.reqText);
		builder.addClaimExpr(Create.FALSE());

		this.claimDef = builder.build();

		return this.claimDef;

	}

	@Override
	public ProveStatement buildClaimCall(ProveStatement prove) {

		if (this.claimDef == null) {
			return null;
		}

		ClaimCallBuilder builder = new ClaimCallBuilder(this.claimDef);
		List<Expr> callArgs = getCallArgs();

		for (Expr e : callArgs) {
			builder.addArg(e);
		}

		return builder.build();
	}

}
