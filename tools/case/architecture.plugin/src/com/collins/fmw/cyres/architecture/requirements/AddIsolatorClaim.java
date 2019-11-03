package com.collins.fmw.cyres.architecture.requirements;

import java.util.ArrayList;
import java.util.List;

import org.osate.aadl2.Subcomponent;

import com.rockwellcollins.atc.resolute.resolute.Arg;
import com.rockwellcollins.atc.resolute.resolute.Expr;

public class AddIsolatorClaim extends BuiltInClaim {

	private static final String ADD_ISOLATOR = "Add_Isolator";

//	private final String reqContext;
	private final List<String> isolatedComponents;
	private final Subcomponent isolator;

	public AddIsolatorClaim(List<String> isolatedComponents, Subcomponent isolator) {
//	public AddIsolatorClaim(String context, List<String> isolatedComponents, Subcomponent isolator) {
		super(ADD_ISOLATOR);
//		this.reqContext = context;
		this.isolatedComponents = isolatedComponents;
		this.isolator = isolator;
	}

	@Override
	public List<Expr> getCallArgs() {
		List<Expr> callArgs = new ArrayList<>();
//		callArgs.add(Create.THIS(this.reqContext));
		List<Expr> isolatedComps = new ArrayList<>();
		isolatedComponents.forEach(c -> isolatedComps.add(Create.THIS(c)));
		callArgs.add(Create.setExpr(isolatedComps));
		callArgs.add(Create.THIS(this.isolator));
		return callArgs;
	}

	@Override
	public List<Arg> getDefinitionParams() {
		List<Arg> defParams = new ArrayList<>();
//		defParams.add(Create.arg("comp_context", Create.baseType("component")));
		defParams.add(Create.arg("isolated_components", Create.setType(Create.baseType("component"))));
		defParams.add(Create.arg("isolator", Create.baseType("component")));
		return defParams;
	}

}
