package com.collins.fmw.cyres.architecture.requirements;

import java.util.ArrayList;
import java.util.List;

import com.rockwellcollins.atc.resolute.resolute.Expr;
import com.rockwellcollins.atc.resolute.resolute.FnCallExpr;
import com.rockwellcollins.atc.resolute.resolute.FunctionDefinition;
import com.rockwellcollins.atc.resolute.resolute.ProveStatement;
import com.rockwellcollins.atc.resolute.resolute.ResoluteFactory;

public class ClaimCallBuilder {

	private static ResoluteFactory f = ResoluteFactory.eINSTANCE;

	public final FunctionDefinition def;
	public List<Expr> args = new ArrayList<>();

	public ClaimCallBuilder(FunctionDefinition fd) {
		this.def = fd;
		args = new ArrayList<>();
	}

	public ClaimCallBuilder(ProveStatement prove) {
		if (prove == null) {
			throw new RuntimeException("Claim call cannot be null.");
		}
		if (prove.getExpr() instanceof FnCallExpr) {
			FnCallExpr fnCallExpr = (FnCallExpr) prove.getExpr();
			this.def = fnCallExpr.getFn();
			this.args = fnCallExpr.getArgs();
		} else {
			throw new RuntimeException("Prove statement can only contain a claim call.");
		}
	}

	public Expr addArg(Expr e) {
		args.add(e);
		return e;
	}

	public ProveStatement build() {
		ProveStatement prove = f.createProveStatement();

		FnCallExpr fn = f.createFnCallExpr();
		fn.setFn(def);
		args.forEach(a -> fn.getArgs().add(a));

		prove.setExpr(fn);
		return prove;
	}
}
