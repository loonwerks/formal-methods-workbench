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
		this.args = new ArrayList<>();
	}

	public ClaimCallBuilder(ProveStatement prove) {
		if (prove == null) {
			throw new RuntimeException("Claim call cannot be null.");
		}
		if (prove.getExpr() instanceof FnCallExpr) {
			FnCallExpr fnCallExpr = (FnCallExpr) prove.getExpr();
			this.def = fnCallExpr.getFn();
			for (Expr expr : fnCallExpr.getArgs()) {
				this.args.add(expr);
			}
		} else {
			throw new RuntimeException("Prove statement can only contain a claim call.");
		}
	}

	public Expr addArg(Expr e) {
		this.args.add(e);
		return e;
	}

	public ProveStatement build() {
		ProveStatement prove = f.createProveStatement();

		FnCallExpr fn = f.createFnCallExpr();
		fn.setFn(this.def);
		for (Expr expr : this.args) {
			fn.getArgs().add(expr);
		}

		prove.setExpr(fn);
		return prove;
	}
}
