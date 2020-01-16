package com.collins.trustedsystems.briefcase.staircase.requirements;

import java.util.ArrayList;
import java.util.List;

import org.osate.aadl2.Subcomponent;

import com.rockwellcollins.atc.resolute.resolute.Arg;
import com.rockwellcollins.atc.resolute.resolute.Expr;

public class AddVirtualizationClaim extends BuiltInClaim {

	private static final String ADD_VIRTUALIZATION = "Add_Virtualization";

	private final List<String> boundComponents;
	private final Subcomponent virtualProcessor;

	public AddVirtualizationClaim(List<String> boundComponents, Subcomponent virtualProcessor) {
		super(ADD_VIRTUALIZATION);
		this.boundComponents = boundComponents;
		this.virtualProcessor = virtualProcessor;
	}

	@Override
	public List<Expr> getCallArgs() {
		List<Expr> callArgs = new ArrayList<>();
		List<Expr> boundComps = new ArrayList<>();
		boundComponents.forEach(c -> boundComps.add(Create.THIS(c)));
		callArgs.add(Create.setExpr(boundComps));
		callArgs.add(Create.THIS(this.virtualProcessor));
		return callArgs;
	}

	@Override
	public List<Arg> getDefinitionParams() {
		List<Arg> defParams = new ArrayList<>();
		defParams.add(Create.arg("bound_components", Create.setType(Create.baseType("component"))));
		defParams.add(Create.arg("virtual_machine", Create.baseType("component")));
		return defParams;
	}

}
