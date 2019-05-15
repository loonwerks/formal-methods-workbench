package com.rockwellcollins.atc.agree;

import static jkind.lustre.parsing.LustreParseUtil.expr;
import static jkind.lustre.parsing.LustreParseUtil.to;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import jkind.lustre.ArrayAccessExpr;
import jkind.lustre.ArrayExpr;
import jkind.lustre.BinaryExpr;
import jkind.lustre.BinaryOp;
import jkind.lustre.BoolExpr;
import jkind.lustre.CastExpr;
import jkind.lustre.CondactExpr;
import jkind.lustre.Equation;
import jkind.lustre.Expr;
import jkind.lustre.FunctionCallExpr;
import jkind.lustre.IdExpr;
import jkind.lustre.IfThenElseExpr;
import jkind.lustre.IntExpr;
import jkind.lustre.NamedType;
import jkind.lustre.Node;
import jkind.lustre.NodeCallExpr;
import jkind.lustre.RealExpr;
import jkind.lustre.RecordAccessExpr;
import jkind.lustre.RecordExpr;
import jkind.lustre.TupleExpr;
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

	public static Expr substitute(Expr context, String name, Expr newExpr) {

		if (context instanceof IdExpr) {
			if (((IdExpr) context).id.equals(name)) {
				return newExpr;
			} else {
				return context;
			}
		} else if (context instanceof ArrayAccessExpr) {

			Expr arrayExpr = substitute(((ArrayAccessExpr) context).array, name, newExpr);
			Expr indexExpr = substitute(((ArrayAccessExpr) context).index, name, newExpr);
			return new ArrayAccessExpr(arrayExpr, indexExpr);

		} else if (context instanceof ArrayExpr) {
			List<Expr> elems = new ArrayList<>();
			for (Expr raw : ((ArrayExpr) context).elements) {
				Expr elem = substitute(raw, name, newExpr);
				elems.add(elem);
			}
			return new ArrayExpr(elems);

		} else if (context instanceof jkind.lustre.ArrayUpdateExpr) {
			Expr arrayExpr = substitute(((jkind.lustre.ArrayUpdateExpr) context).array, name, newExpr);
			Expr indexExpr = substitute(((jkind.lustre.ArrayUpdateExpr) context).index, name, newExpr);
			Expr valExpr = substitute(((jkind.lustre.ArrayUpdateExpr) context).value, name, newExpr);
			return new jkind.lustre.ArrayUpdateExpr(arrayExpr, indexExpr, valExpr);

		} else if (context instanceof BinaryExpr) {
			Expr left = substitute(((BinaryExpr) context).left, name, newExpr);
			Expr right = substitute(((BinaryExpr) context).right, name, newExpr);
			return new BinaryExpr(left, ((BinaryExpr) context).op, right);

		} else if (context instanceof BoolExpr) {
			return context;
		} else if (context instanceof CastExpr) {
			Expr expr = substitute(((CastExpr) context).expr, name, newExpr);
			return new CastExpr(((CastExpr) context).type, expr);
		} else if (context instanceof CondactExpr) {
			Expr clock = substitute(((CondactExpr) context).clock, name, newExpr);
			Expr call = substitute(((CondactExpr) context).call, name, newExpr);
			List<Expr> args = new ArrayList<>();
			for (Expr raw : ((CondactExpr) context).args) {
				Expr arg = substitute(raw, name, newExpr);
				args.add(arg);
			}
			return new CondactExpr(clock, (NodeCallExpr) call, args);

		} else if (context instanceof FunctionCallExpr) {
			List<Expr> args = new ArrayList<>();
			for (Expr raw : ((FunctionCallExpr) context).args) {
				Expr arg = substitute(raw, name, newExpr);
				args.add(arg);
			}
			return new FunctionCallExpr(((FunctionCallExpr) context).function, args);

		} else if (context instanceof IfThenElseExpr) {
			Expr cond = substitute(((IfThenElseExpr) context).cond, name, newExpr);
			Expr thenExpr = substitute(((IfThenElseExpr) context).thenExpr, name, newExpr);
			Expr elseExpr = substitute(((IfThenElseExpr) context).elseExpr, name, newExpr);
			return new IfThenElseExpr(cond, thenExpr, elseExpr);

		} else if (context instanceof IntExpr) {
			return context;

		} else if (context instanceof NodeCallExpr) {
			List<Expr> args = new ArrayList<>();
			for (Expr raw : ((NodeCallExpr) context).args) {
				Expr arg = substitute(raw, name, newExpr);
				args.add(arg);
			}
			return new FunctionCallExpr(((NodeCallExpr) context).node, args);

		} else if (context instanceof RealExpr) {
			return context;

		} else if (context instanceof RecordAccessExpr) {
			Expr rec = substitute(((RecordAccessExpr) context).record, name, newExpr);
			return new RecordAccessExpr(rec, ((RecordAccessExpr) context).field);

		} else if (context instanceof RecordExpr) {

			Map<String, Expr> fields = new TreeMap<>();
			for (Entry<String, Expr> raw : ((RecordExpr) context).fields.entrySet()) {
				String key = raw.getKey();
				Expr fieldExpr = substitute(raw.getValue(), name, newExpr);
				fields.put(key, fieldExpr);
			}

			return new RecordExpr(((RecordExpr) context).id, fields);

		} else if (context instanceof jkind.lustre.RecordUpdateExpr) {

			Expr rec = substitute(((jkind.lustre.RecordUpdateExpr) context).record, name, newExpr);
			Expr valueExpr = substitute(((jkind.lustre.RecordUpdateExpr) context).value, name, newExpr);
			return new jkind.lustre.RecordUpdateExpr(rec, ((jkind.lustre.RecordUpdateExpr) context).field, valueExpr);

		} else if (context instanceof TupleExpr) {

			List<Expr> elems = new ArrayList<>();
			for (Expr raw : ((TupleExpr) context).elements) {
				Expr elem = substitute(raw, name, newExpr);
				elems.add(elem);
			}
			return new TupleExpr(elems);

		} else if (context instanceof UnaryExpr) {
			Expr expr = substitute(((UnaryExpr) context).expr, name, newExpr);
			return new UnaryExpr(((UnaryExpr) context).op, expr);
		}

		throw new RuntimeException("Error: substitute - " + context);
	}

	public final static String statVarPrefix = "_STATE";
	public final static String clockVarName = "_CLK";
	public final static String initVarName = "_INIT";
	public final static String clockedNodePrefix = "_CLOCKED_";

//
//	public static Expr toClockedExpr(Expr expr) {
//		if (expr instanceof UnaryExpr) {
//			UnaryExpr e = (UnaryExpr) expr;
//			if (e.op == UnaryOp.PRE) {
//				IdExpr stateVarId = new IdExpr(Lustre.statVarPrefix + e.hashCode());
//
//				Expr stateVarExpr = new UnaryExpr(UnaryOp.PRE, Lustre.toClockedExpr(e.expr));
//				stateVarExpr = expr("if clk then stateVarExpr else (pre stateVar)", to("stateVar", stateVarId),
//						to("stateVarExpr", stateVarExpr), to("clk", Luster.clockVarName));
//
////				stateVars.add(new AgreeVar(stateVarId.id, e.accept(typeReconstructor), null, null, null));
////				stateVarEqs.add(new Equation(stateVarId, stateVarExpr));
//
//				return stateVarId;
//			} else {
//				return new UnaryExpr(e.op, Lustre.toClockedExpr(e.expr));
//			}
//
//		} else if (expr instanceof BinaryExpr) {
//			BinaryExpr e = (BinaryExpr) expr;
//			if (e.op == BinaryOp.ARROW) {
//				return new IfThenElseExpr(new IdExpr(initVarName), Lustre.toClockedExpr(e.left),
//						Lustre.toClockedExpr(e.right));
//			} else {
//				return new BinaryExpr(Lustre.toClockedExpr(e.left), e.op, Lustre.toClockedExpr(e.right));
//			}
//		}
//
//		return expr;
//	}
//
//	public static List<VarDecl> toClockedLocals(Expr expr) {
//		List<VarDecl> vars = new ArrayList<>();
//		return vars;
//	}
//
//	public static List<Equation> toClockedEquations(Expr expr) {
//		// TODO Auto-generated method stub
//		return null;
//	}


}