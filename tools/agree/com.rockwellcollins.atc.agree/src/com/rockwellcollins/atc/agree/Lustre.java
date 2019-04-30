package com.rockwellcollins.atc.agree;

import static jkind.lustre.parsing.LustreParseUtil.expr;
import static jkind.lustre.parsing.LustreParseUtil.to;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jkind.lustre.BinaryExpr;
import jkind.lustre.BinaryOp;
import jkind.lustre.BoolExpr;
import jkind.lustre.Equation;
import jkind.lustre.Expr;
import jkind.lustre.IdExpr;
import jkind.lustre.IfThenElseExpr;
import jkind.lustre.NamedType;
import jkind.lustre.Node;
import jkind.lustre.NodeCallExpr;
import jkind.lustre.RealExpr;
import jkind.lustre.UnaryExpr;
import jkind.lustre.UnaryOp;
import jkind.lustre.VarDecl;
import jkind.lustre.builders.NodeBuilder;

public class Lustre {
	public static final jkind.lustre.IdExpr timeExpr = new jkind.lustre.IdExpr("time");

	public static final VarDecl assumeHistVar = new VarDecl("__ASSUME__HIST", NamedType.BOOL);

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
				to("timeCause", timeCause), to("cause", id), to("time", Lustre.timeExpr));
		asserts.add(timeVarExpr);

		jkind.lustre.Expr lemmaExpr = expr("timeCause <= time and timeCause >= -1.0", to("timeCause", timeCause),
				to("time", Lustre.timeExpr));

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

	public static Expr getTimeConstraint(List<VarDecl> eventTimeVarList) {
		IdExpr timeId = Lustre.timeExpr;
		Expr preTime = new UnaryExpr(UnaryOp.PRE, timeId);

		Expr nodeCall = new BinaryExpr(timeId, BinaryOp.MINUS, preTime);
		for (VarDecl eventVar : eventTimeVarList) {
			Expr event = new IdExpr(eventVar.id);
			BinaryExpr timeChange = new BinaryExpr(event, BinaryOp.MINUS, timeId);
			Expr preTimeChange = new UnaryExpr(UnaryOp.PRE, timeChange);
			nodeCall = new NodeCallExpr(MIN_POS_NODE_NAME, preTimeChange, nodeCall);
		}

		nodeCall = new BinaryExpr(preTime, BinaryOp.PLUS, nodeCall);
		Expr timeExpr = new BinaryExpr(timeId, BinaryOp.EQUAL, nodeCall);
		timeExpr = new BinaryExpr(new BoolExpr(true), BinaryOp.ARROW, timeExpr);
		Expr timeGrtPreTime = new BinaryExpr(timeId, BinaryOp.GREATER, preTime);
		Expr timeInitZero = new BinaryExpr(timeId, BinaryOp.EQUAL, new RealExpr(BigDecimal.ZERO));
		timeInitZero = new BinaryExpr(timeInitZero, BinaryOp.ARROW, timeGrtPreTime);
		return new BinaryExpr(timeInitZero, BinaryOp.AND, timeExpr);
	}

	public static Node getHistNode() {
		NodeBuilder builder = new NodeBuilder("__HIST");
		builder.addInput(new VarDecl("input", NamedType.BOOL));
		builder.addOutput(new VarDecl("hist", NamedType.BOOL));

		IdExpr histId = new IdExpr("hist");
		IdExpr inputId = new IdExpr("input");
		Expr preHist = new UnaryExpr(UnaryOp.PRE, histId);
		Expr histExpr = new BinaryExpr(preHist, BinaryOp.AND, inputId);
		histExpr = new BinaryExpr(inputId, BinaryOp.ARROW, histExpr);
		builder.addEquation(new Equation(histId, histExpr));
		return builder.build();

	}

	private static final String MIN_POS_NODE_NAME = "__MinPos";
	public static final String RISE_NODE_NAME = "__Rise";
	public static final String FALL_NODE_NAME = "__Fall";

	public static List<Node> getRealTimeNodes() {
		List<Node> nodes = new ArrayList<>();
		nodes.add(getMinPosNode());
		nodes.add(getRiseNode());
		nodes.add(getFallNode());
		return nodes;
	}

	private static Node getRiseNode() {
		NodeBuilder builder = new NodeBuilder(RISE_NODE_NAME);
		builder.addInput(new VarDecl("input", NamedType.BOOL));
		builder.addOutput(new VarDecl("output", NamedType.BOOL));

		IdExpr inputId = new IdExpr("input");
		IdExpr outputId = new IdExpr("output");

		Expr outputExpr = new UnaryExpr(UnaryOp.NOT, inputId);
		outputExpr = new UnaryExpr(UnaryOp.PRE, outputExpr);
		outputExpr = new BinaryExpr(outputExpr, BinaryOp.AND, inputId);
		outputExpr = new BinaryExpr(inputId, BinaryOp.ARROW, outputExpr);

		builder.addEquation(new Equation(outputId, outputExpr));
		return builder.build();
	}

	private static Node getFallNode() {
		NodeBuilder builder = new NodeBuilder(FALL_NODE_NAME);
		builder.addInput(new VarDecl("input", NamedType.BOOL));
		builder.addOutput(new VarDecl("output", NamedType.BOOL));

		IdExpr inputId = new IdExpr("input");
		IdExpr outputId = new IdExpr("output");

		Expr outputExpr = new UnaryExpr(UnaryOp.PRE, inputId);
		Expr notInput = new UnaryExpr(UnaryOp.NOT, inputId);
		outputExpr = new BinaryExpr(outputExpr, BinaryOp.AND, notInput);
		outputExpr = new BinaryExpr(notInput, BinaryOp.ARROW, outputExpr);

		builder.addEquation(new Equation(outputId, outputExpr));
		return builder.build();
	}

	private static Node getMinPosNode() {
		NodeBuilder builder = new NodeBuilder(MIN_POS_NODE_NAME);
		IdExpr a = new IdExpr("a");
		IdExpr b = new IdExpr("b");
		IdExpr ret = new IdExpr("ret");
		builder.addInput(new VarDecl(a.id, NamedType.REAL));
		builder.addInput(new VarDecl(b.id, NamedType.REAL));
		builder.addOutput(new VarDecl(ret.id, NamedType.REAL));

		Expr aLessB = new BinaryExpr(a, BinaryOp.LESSEQUAL, b);
		Expr bNeg = new BinaryExpr(b, BinaryOp.LESSEQUAL, new RealExpr(BigDecimal.ZERO));
		Expr aNeg = new BinaryExpr(a, BinaryOp.LESSEQUAL, new RealExpr(BigDecimal.ZERO));
		Expr ifALessB = new IfThenElseExpr(aLessB, a, b);
		Expr ifBNeg = new IfThenElseExpr(bNeg, a, ifALessB);
		Expr ifANeg = new IfThenElseExpr(aNeg, b, ifBNeg);

		builder.addEquation(new Equation(ret, ifANeg));
		return builder.build();
	}


}