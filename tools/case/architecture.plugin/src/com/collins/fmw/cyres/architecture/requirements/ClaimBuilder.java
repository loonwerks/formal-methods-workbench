package com.collins.fmw.cyres.architecture.requirements;

import java.util.ArrayList;
import java.util.List;

import com.rockwellcollins.atc.resolute.resolute.Arg;
import com.rockwellcollins.atc.resolute.resolute.ClaimArg;
import com.rockwellcollins.atc.resolute.resolute.ClaimBody;
import com.rockwellcollins.atc.resolute.resolute.ClaimString;
import com.rockwellcollins.atc.resolute.resolute.ClaimText;
import com.rockwellcollins.atc.resolute.resolute.Expr;
import com.rockwellcollins.atc.resolute.resolute.FunctionDefinition;
import com.rockwellcollins.atc.resolute.resolute.ResoluteFactory;

public class ClaimBuilder {

	private static ResoluteFactory f = ResoluteFactory.eINSTANCE;

	private final String name;
	private List<Arg> args = new ArrayList<>();
	private List<ClaimText> claimText = new ArrayList<>();
	private Expr claimExpr;

	public ClaimBuilder(String name) {
		this.name = name;
	}

	public Arg addArg(Arg a) {
		args.add(a);
		return a;
	}

	public ClaimString addClaimString(String s) {
		ClaimString claimString = f.createClaimString();
		claimString.setStr(s);
		claimText.add(claimString);
		return claimString;
	}

	public ClaimArg addClaimArg(Arg a) {
		ClaimArg claimArg = f.createClaimArg();
		claimArg.setUnit(null);
		claimArg.setArg(a);
		claimText.add(claimArg);
		return claimArg;
	}

	public Expr setClaimExpr(Expr e) {
		this.claimExpr = e;
		return e;
	}

	public FunctionDefinition build() {

		FunctionDefinition def = f.createFunctionDefinition();
		def.setName(name);
		args.forEach(a -> def.getArgs().add(a));

		ClaimBody body = f.createClaimBody();
		claimText.forEach(ct -> body.getClaim().add(ct));
		body.setExpr(claimExpr);

		def.setBody(body);
		return def;
	}
}
