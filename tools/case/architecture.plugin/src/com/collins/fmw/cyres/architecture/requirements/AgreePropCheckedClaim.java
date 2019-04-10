package com.collins.fmw.cyres.architecture.requirements;

import java.util.ArrayList;
import java.util.List;

import com.rockwellcollins.atc.resolute.resolute.Arg;
import com.rockwellcollins.atc.resolute.resolute.Expr;

public class AgreePropCheckedClaim extends BuiltInClaim {

	private static final String AGREE_PROP_CHECKED = "Agree_Prop_Checked";
	private final String reqId;

	public AgreePropCheckedClaim(String reqId) {
		super(AGREE_PROP_CHECKED);
		this.reqId = reqId;
	}

	@Override
	public List<Arg> getDefinitionParams() {
		List<Arg> defParams = new ArrayList<>();
		defParams.add(Create.arg("c", Create.baseType("component")));
		defParams.add(Create.arg("property_id", Create.baseType("string")));
		return defParams;
	}

	@Override
	public List<Expr> getCallArgs() {
		List<Expr> callArgs = new ArrayList<>();
		callArgs.add(Create.THIS());
		callArgs.add(Create.stringExpr(this.reqId));
		return callArgs;
	}

}
