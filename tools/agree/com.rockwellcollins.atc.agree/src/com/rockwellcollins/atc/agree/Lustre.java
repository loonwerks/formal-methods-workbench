package com.rockwellcollins.atc.agree;

import static jkind.lustre.parsing.LustreParseUtil.expr;
import static jkind.lustre.parsing.LustreParseUtil.to;

import java.util.ArrayList;
import java.util.List;

import jkind.lustre.BinaryExpr;
import jkind.lustre.BinaryOp;
import jkind.lustre.BoolExpr;
import jkind.lustre.Expr;
import jkind.lustre.NamedType;
import jkind.lustre.VarDecl;

public class Lustre {
	public static Expr makeANDExpr(Expr left, Expr right) {
		if (left instanceof BoolExpr) {
			if (((BoolExpr) left).value == true) {
				return right;
			}
			return new BoolExpr(false);
		}

		if (right instanceof BoolExpr) {
			if (((BoolExpr) right).value == true) {
				return left;
			}
			return new BoolExpr(false);
		}

		return new BinaryExpr(left, BinaryOp.AND, right);
	}

	public static Expr makeORExpr(Expr left, Expr right) {
		if (left instanceof BoolExpr) {
			if (((BoolExpr) left).value == false) {
				return right;
			}
			return new BoolExpr(true);
		}

		if (right instanceof BoolExpr) {
			if (((BoolExpr) right).value == false) {
				return left;
			}
			return new BoolExpr(true);
		}

		return new BinaryExpr(left, BinaryOp.OR, right);
	}

	public static VarDecl getTimeRiseVar(String id) {
		return new VarDecl(id + "__RISE__", NamedType.REAL);
	}

	public static VarDecl getTimeFallVar(String id) {
		return new VarDecl(id + "__FALL__", NamedType.REAL);
	}

	public static VarDecl getTimeOfVar(String id) {
		return new VarDecl(id + "__TIME__", NamedType.REAL);
	}

	public static VarDecl getTimerVar(String patternIndex) {
		return new VarDecl("__TIMER__" + patternIndex, NamedType.REAL);
	}

	public static VarDecl getRunningVar(String patternIndex) {
		return new VarDecl("__RUNNING__" + patternIndex, NamedType.BOOL);

	}

	public static VarDecl getAssumeHistVar() {
		return new VarDecl("__ASSUME__HIST", NamedType.BOOL);
	}

	public static VarDecl getPatternVar(String index) {
		return new VarDecl("__PATTERN__" + index, NamedType.BOOL);
	}

	public static VarDecl getCauseHeldVar(String causeId) {
		return new VarDecl("__CAUSE_CONDITION_HELD__" + causeId, NamedType.BOOL);
	}

	public static VarDecl getRecordVar(String patternIndex) {
		return new VarDecl("__RECORD__" + patternIndex, NamedType.BOOL);
	}

	public static VarDecl getEffectTimeRangeVar(String patternIndex) {
		return new VarDecl("__EFFECT_TIME_RANGE__" + patternIndex, NamedType.REAL);
	}

	public static VarDecl getTimeWillVar(String patternIndex) {
		return new VarDecl("__TIME_WILL__" + patternIndex, NamedType.REAL);
	}

	public static List<jkind.lustre.Expr> getTimeOfAsserts(String id) {

		List<jkind.lustre.Expr> asserts = new ArrayList<>();

		VarDecl timeCause = Lustre.getTimeOfVar(id);

		jkind.lustre.Expr timeVarExpr = expr("timeCause = (if cause then time else (-1.0 -> pre timeCause))",
				to("timeCause", timeCause), to("cause", id), to("time", new jkind.lustre.IdExpr("time")));
		asserts.add(timeVarExpr);

		jkind.lustre.Expr lemmaExpr = expr("timeCause <= time and timeCause >= -1.0", to("timeCause", timeCause),
				to("time", new jkind.lustre.IdExpr("time")));

		// add this assertion to help with proofs (it should always be true)
		asserts.add(lemmaExpr);

		return asserts;
	}

	public static VarDecl getCauseConditionTimeOutVar(String id) {
		return new VarDecl("__CAUSE_CONDITION_TIMEOUT__" + id, NamedType.BOOL);
	}

	public static VarDecl getWindowVar(String patternIndex) {
		return new VarDecl("__WINDOW__" + patternIndex, NamedType.BOOL);
	}

	public static VarDecl getTimeoutVar(String patternIndex) {
		return new VarDecl("__TIMEOUT__" + patternIndex, NamedType.BOOL);
	}

	public static VarDecl getPeriodVar(String patternIndex) {
		return new VarDecl("__PERIOD__" + patternIndex, NamedType.BOOL);
	}

	public static VarDecl getJitterVar(String patternIndex) {
		return new VarDecl("__JITTER__" + patternIndex, NamedType.BOOL);
	}

}