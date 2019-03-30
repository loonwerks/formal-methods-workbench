package com.collins.fmw.cyres.architecture.requirements;

import java.util.ArrayList;
import java.util.List;

import com.rockwellcollins.atc.resolute.resolute.Arg;
import com.rockwellcollins.atc.resolute.resolute.BoolExpr;
import com.rockwellcollins.atc.resolute.resolute.ClaimArg;
import com.rockwellcollins.atc.resolute.resolute.ClaimBody;
import com.rockwellcollins.atc.resolute.resolute.ClaimString;
import com.rockwellcollins.atc.resolute.resolute.ClaimText;
import com.rockwellcollins.atc.resolute.resolute.DefinitionBody;
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
		if (name == null || name.isEmpty()) {
			throw new RuntimeException("Claim name cannot be null or empty.");
		}
		this.name = name;
	}

	public ClaimBuilder(FunctionDefinition claim) {
		if (claim == null) {
			throw new RuntimeException("Claim cannot be null.");
		}
		this.name = claim.getName();
		claim.getArgs().forEach(a -> this.args.add(a));
		DefinitionBody db = claim.getBody();
		if (db instanceof ClaimBody) {
			ClaimBody cb = (ClaimBody) db;
			cb.getClaim().forEach(c -> this.claimText.add(c));
			this.claimExpr = cb.getExpr();
		} else {
			throw new RuntimeException(claim.getName() + " must be a Resoltue claim");
		}
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

//	public Expr setClaimExpr(Expr e) {
//		this.claimExpr = e;
//		return e;
//	}

	public Expr addClaimExpr(Expr e) {
		// Remove "true" or "false" claim, if they exist
		if (this.claimExpr instanceof BoolExpr || this.claimExpr == null) {
			this.claimExpr = e;
		} else {
			this.claimExpr = Create.andExpr(this.claimExpr, e);
		}

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
