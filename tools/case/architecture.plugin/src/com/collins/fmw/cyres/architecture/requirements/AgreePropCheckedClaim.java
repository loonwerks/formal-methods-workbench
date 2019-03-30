package com.collins.fmw.cyres.architecture.requirements;

import org.eclipse.emf.common.util.EList;

import com.rockwellcollins.atc.resolute.resolute.Arg;
import com.rockwellcollins.atc.resolute.resolute.Expr;
import com.rockwellcollins.atc.resolute.resolute.FnCallExpr;
import com.rockwellcollins.atc.resolute.resolute.FunctionDefinition;
import com.rockwellcollins.atc.resolute.resolute.ProveStatement;
import com.rockwellcollins.atc.resolute.resolute.StringExpr;
import com.rockwellcollins.atc.resolute.resolute.ThisExpr;

public class AgreePropCheckedClaim extends BuiltInClaim {

	private static final String AGREE_PROP_CHECKED = "Agree_Prop_Checked";

	public AgreePropCheckedClaim() {
		super(AGREE_PROP_CHECKED);
	}

//	@Override
//	public boolean modifyClaimDefinition(FunctionDefinition funDef) {
//
//		if (funDef == null) {
//			return false;
//		}
//
//		// Add requirement ID to arg list
//		// Make sure arg isn't already present
//		EList<Arg> args = funDef.getArgs();
//		Arg arg = Create.arg("c", Create.baseType("component"));
//		if (!args.contains(arg)) {
//			args.add(arg);
//		}
//		arg = Create.arg("property_id", Create.baseType("string"));
//		if (!args.contains(arg)) {
//			args.add(arg);
//		}
//
//		// Add Agree_Prop_Checked to body
//		// TODO: make sure fn call isn't already present
//		Expr expr = funDef.getBody().getExpr();
//		funDef.getBody().setExpr(Create.andExpr(expr, Create.fnCallExpr(getFunctionDefinition())));
//
//		return true;
//	}
//
//	@Override
//	public boolean modifyClaimCall(ProveStatement prove) {
//
//		if (prove == null) {
//			return false;
//		}
//
//		// Get claim call
//		if (prove.getExpr() instanceof FnCallExpr) {
//			FnCallExpr expr = (FnCallExpr) prove.getExpr();
//			// Add requirement ID to arg list
//			// TODO: make sure arg isn't already present
//			EList<Expr> args = expr.getArgs();
//			Expr arg = Create.THIS();
//			if (!args.contains(arg)) {
//				args.add(arg);
//			}
//			arg = Create.stringExpr(expr.getFn().getName());
//			if (!args.contains(arg)) {
//				args.add(arg);
//			}
//			return true;
//		}
//
//		return false;
//	}

	@Override
	public FunctionDefinition buildClaimDefinition() {

		// Get current claim definition for the requirement
		FunctionDefinition fd = getRequirementClaimDefinition();

		ClaimBuilder builder = new ClaimBuilder(fd);

		// Add requirement ID to arg list
		// Make sure arg isn't already present
		EList<Arg> args = fd.getArgs();
		Arg arg = Create.arg("c", Create.baseType("component"));
		boolean argFound = false;
		for (Arg a : fd.getArgs()) {
			if (a.getName().equalsIgnoreCase(arg.getName())) {
				argFound = true;
				break;
			}
		}
		if (!argFound) {
			builder.addArg(arg);
		}
		arg = Create.arg("property_id", Create.baseType("string"));
		argFound = false;
		for (Arg a : fd.getArgs()) {
			if (a.getName().equalsIgnoreCase(arg.getName())) {
				argFound = true;
				break;
			}
		}
		if (!argFound) {
			builder.addArg(arg);
		}

		FnCallExpr fnCallExpr = Create.fnCallExpr(getBuiltInClaimDefinition());
		fnCallExpr.getArgs().add(Create.stringExpr("c"));
		fnCallExpr.getArgs().add(Create.stringExpr("property_id"));

		builder.addClaimExpr(fnCallExpr);

		return builder.build();

	}

	@Override
	public ProveStatement buildClaimCall() {

		// Get current claim call for the requirement
		ProveStatement prove = getRequirementClaimCall();

		ClaimCallBuilder builder = new ClaimCallBuilder(prove);

		FnCallExpr expr = (FnCallExpr) prove.getExpr();
//		EList<Expr> args = expr.getArgs();
		Expr arg = Create.THIS();
		boolean argFound = false;
		// TODO: Need a better way of handling args
		for (Expr e : expr.getArgs()) {
			if (e instanceof ThisExpr) {
				argFound = true;
				break;
			}
		}
		if (!argFound) {
			builder.addArg(arg);
		}

		arg = Create.stringExpr(req.getId());
		argFound = false;
		for (Expr e : expr.getArgs()) {
			if (e instanceof StringExpr) {
				StringExpr s = (StringExpr) e;
				if (s.getVal().getValue().equalsIgnoreCase(req.getId())) {
					argFound = true;
					break;
				}
			}
		}
		if (!argFound) {
			builder.addArg(arg);
		}
		return builder.build();
	}

}
