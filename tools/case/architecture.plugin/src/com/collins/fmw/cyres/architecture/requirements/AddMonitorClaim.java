package com.collins.fmw.cyres.architecture.requirements;

import java.util.ArrayList;
import java.util.List;

import org.osate.aadl2.Subcomponent;

import com.rockwellcollins.atc.resolute.resolute.Arg;
import com.rockwellcollins.atc.resolute.resolute.Expr;

public class AddMonitorClaim extends BuiltInClaim {

	private static final String ADD_MONITOR = "Add_Monitor";

	private final String reqContext;
	private final Subcomponent monitor;

	public AddMonitorClaim(String context, Subcomponent monitor) {
		super(ADD_MONITOR);
		this.reqContext = context;
		this.monitor = monitor;
	}

	@Override
	public List<Expr> getCallArgs() {
		List<Expr> callArgs = new ArrayList<>();
		callArgs.add(Create.THIS(this.reqContext));
		callArgs.add(Create.THIS(this.monitor));
		return callArgs;
	}

	@Override
	public List<Arg> getDefinitionParams() {
		List<Arg> defParams = new ArrayList<>();
		defParams.add(Create.arg("context", Create.baseType("component")));
		defParams.add(Create.arg("monitor", Create.baseType("component")));
		return defParams;
	}

}
