package com.collins.fmw.cyres.architecture.requirements;

import java.util.ArrayList;
import java.util.List;

import org.osate.aadl2.NamedElement;
import org.osate.aadl2.Subcomponent;

import com.rockwellcollins.atc.resolute.resolute.Arg;
import com.rockwellcollins.atc.resolute.resolute.Expr;

public class AddFilterClaim extends BuiltInClaim {

	private static final String ADD_FILTER = "Add_Filter";

	private final String reqContext;
	private final Subcomponent filter;
	private final String connName;
	private final NamedElement msgType;

	public AddFilterClaim(String context, Subcomponent filter, String connName, NamedElement msgType) {
		super(ADD_FILTER);
		this.reqContext = context;
		this.filter = filter;
		this.connName = connName;
		this.msgType = msgType;
	}

	@Override
	public List<Expr> getCallArgs() {
		List<Expr> callArgs = new ArrayList<>();
		callArgs.add(Create.THIS(this.reqContext));
		callArgs.add(Create.THIS(this.filter));
		callArgs.add(Create.stringExpr(this.connName));
		callArgs.add(Create.id(this.msgType));
		return callArgs;
	}

	@Override
	public List<Arg> getDefinitionParams() {
		List<Arg> defParams = new ArrayList<>();
		defParams.add(Create.arg("context", Create.baseType("component")));
		defParams.add(Create.arg("filter", Create.baseType("component")));
		defParams.add(Create.arg("connection_name", Create.baseType("string")));
		defParams.add(Create.arg("message_type", Create.baseType("data")));
		return defParams;
	}

}
