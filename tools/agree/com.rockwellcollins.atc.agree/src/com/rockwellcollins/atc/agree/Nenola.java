package com.rockwellcollins.atc.agree;


import static jkind.lustre.parsing.LustreParseUtil.expr;
import static jkind.lustre.parsing.LustreParseUtil.to;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.osate.aadl2.NamedElement;

import jkind.lustre.BinaryExpr;
import jkind.lustre.BinaryOp;
import jkind.lustre.EnumType;
import jkind.lustre.Equation;
import jkind.lustre.Location;
import jkind.lustre.NamedType;
import jkind.lustre.Node;
import jkind.lustre.TypeDef;
import jkind.lustre.VarDecl;
import jkind.lustre.builders.NodeBuilder;
import jkind.lustre.parsing.LustreParseUtil;

//Nenola = Nested Node Language
public class Nenola {


	public static enum Rator {
		Equal, StreamCons, Implies, Equiv, Conj, Disj, NotEqual, LessThan, LessEq, GreatThan, GreatEq, Plus, Minus, Mult, Div, Mod, Pow, Neg, Not
	}

	public static enum Tag {
		Clock, Insert, Remove, Count
	}

	public static interface Expr {

		jkind.lustre.IdExpr toLustreExpr();

	}

	public static class TagExpr implements Expr {

		public final Expr target;
		public final Tag tag;

		public TagExpr(Expr target, Tag tag) {
			this.target = target;
			this.tag = tag;
		}

		@Override
		public jkind.lustre.IdExpr toLustreExpr() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public static class IdExpr implements Expr {
		public final String name;

		public IdExpr(String name) {
			this.name = name;
		}

		@Override
		public jkind.lustre.IdExpr toLustreExpr() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public static class SelectionExpr implements Expr {
		public final Expr target;
		public final String selection;

		public SelectionExpr(Expr target, String selection) {
			this.target = target;
			this.selection = selection;

		}

		@Override
		public jkind.lustre.IdExpr toLustreExpr() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public static class BinExpr implements Expr {

		public final Expr e1;
		public final Rator rator;
		public final Expr e2;

		public BinExpr(Expr e1, Rator rator, Expr e2) {
			this.e1 = e1;
			this.rator = rator;
			this.e2 = e2;
		}

		@Override
		public jkind.lustre.IdExpr toLustreExpr() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public static class DistinctionExpr implements Expr {
		public final Expr condition;
		public final Expr trueBody;
		public final Expr falseBody;

		public DistinctionExpr(Expr condition, Expr trueBody, Expr falseBody) {
			this.condition = condition;
			this.trueBody = trueBody;
			this.falseBody = falseBody;
		}

		@Override
		public jkind.lustre.IdExpr toLustreExpr() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public static class PrevExpr implements Expr {
		public final Expr body;
		public final Expr init;

		public PrevExpr(Expr body, Expr init) {
			this.body = body;
			this.init = init;
		}

		@Override
		public jkind.lustre.IdExpr toLustreExpr() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public static class LocalProperty implements Expr {
		public final String propName;

		public LocalProperty(String propName) {
			this.propName = propName;
		}

		@Override
		public jkind.lustre.IdExpr toLustreExpr() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public static class ForeignProperty implements Expr {
		public final String nodeName;
		public final String propName;

		public ForeignProperty(String nodeName, String propName) {
			this.nodeName = nodeName;
			this.propName = propName;
		}

		@Override
		public jkind.lustre.IdExpr toLustreExpr() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public static class IntLit implements Expr {
		String val;

		public IntLit(String val) {
			this.val = val;
		}

		@Override
		public jkind.lustre.IdExpr toLustreExpr() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public static class RealLit implements Expr {
		String val;

		public RealLit(String val) {
			this.val = val;
		}

		@Override
		public jkind.lustre.IdExpr toLustreExpr() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public static class BoolLit implements Expr {
		boolean val;

		public BoolLit(boolean val) {
			this.val = val;
		}

		@Override
		public jkind.lustre.IdExpr toLustreExpr() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public static class Floor implements Expr {
		Expr arg;

		public Floor(Expr arg) {
			this.arg = arg;
		}

		@Override
		public jkind.lustre.IdExpr toLustreExpr() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public static class RealCast implements Expr {
		Expr arg;

		public RealCast(Expr arg) {
			this.arg = arg;
		}

		@Override
		public jkind.lustre.IdExpr toLustreExpr() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public static class Latch implements Expr {
		Expr arg;

		public Latch(Expr arg) {
			this.arg = arg;
		}

		@Override
		public jkind.lustre.IdExpr toLustreExpr() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public static class Pre implements Expr {
		Expr arg;

		public Pre(Expr arg) {
			this.arg = arg;
		}

		@Override
		public jkind.lustre.IdExpr toLustreExpr() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public static class Event implements Expr {
		String arg;

		public Event(String arg) {
			this.arg = arg;
		}

		@Override
		public jkind.lustre.IdExpr toLustreExpr() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public static class TimeOf implements Expr {
		String arg;

		public TimeOf(String arg) {
			this.arg = arg;
		}

		@Override
		public jkind.lustre.IdExpr toLustreExpr() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public static class TimeRise implements Expr {
		String arg;

		public TimeRise(String arg) {
			this.arg = arg;
		}

		@Override
		public jkind.lustre.IdExpr toLustreExpr() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public static class TimeFall implements Expr {
		String arg;

		public TimeFall(String arg) {
			this.arg = arg;
		}

		@Override
		public jkind.lustre.IdExpr toLustreExpr() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public static class Time implements Expr {

		public Time() {

		}

		@Override
		public jkind.lustre.IdExpr toLustreExpr() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public static class EnumLit implements Expr {
		public final String contractName;
		public final String variantName;

		public EnumLit(String contractName, String variantName) {
			this.contractName = contractName;
			this.variantName = variantName;

		}

		@Override
		public jkind.lustre.IdExpr toLustreExpr() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public static class ArrayLit implements Expr {
		public final List<Expr> elements;

		public ArrayLit(List<Expr> elements) {
			this.elements = elements;
		}

		@Override
		public jkind.lustre.IdExpr toLustreExpr() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public static class ArrayUpdate implements Expr {
		public final List<Expr> indices;
		public final List<Expr> elements;

		public ArrayUpdate(List<Expr> indices, List<Expr> elements) {
			this.indices = indices;
			this.elements = elements;
		}

		@Override
		public jkind.lustre.IdExpr toLustreExpr() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public static class RecordLit implements Expr {
		public final Map<String, Expr> fields;

		public RecordLit(Map<String, Expr> fields) {
			this.fields = fields;
		}

		@Override
		public jkind.lustre.IdExpr toLustreExpr() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public static class RecordUpdate implements Expr {
		public final Expr record;
		public final String selector;
		public final Expr element;

		public RecordUpdate(Expr record, String selector, Expr element) {
			this.record = record;
			this.selector = selector;
			this.element = element;
		}

		@Override
		public jkind.lustre.IdExpr toLustreExpr() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public static class App implements Expr {
		public final String fnName;
		public final List<Expr> args;

		public App(String fnName, List<Expr> args) {
			this.fnName = fnName;
			this.args = args;
		}

		@Override
		public jkind.lustre.IdExpr toLustreExpr() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public static class UnaryExpr implements Expr {
		public final Rator rator;
		public final Expr rand;

		public UnaryExpr(Rator rator, Expr rand) {
			this.rator = rator;
			this.rand = rand;
		}

		@Override
		public jkind.lustre.IdExpr toLustreExpr() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public static class ArraySubExpr implements Expr {
		public final Expr array;
		public final Expr index;

		public ArraySubExpr(Expr array, Expr index) {
			this.array = array;
			this.index = index;
		}

		@Override
		public jkind.lustre.IdExpr toLustreExpr() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public static class IndicesExpr implements Expr {
		public final Expr array;

		public IndicesExpr(Expr array) {
			this.array = array;
		}

		@Override
		public jkind.lustre.IdExpr toLustreExpr() {
			// TODO Auto-generated method stub
			return null;
		}

	}

	public static class ForallExpr implements Expr {
		public final String binding;
		public final Expr array;
		public final Expr body;

		public ForallExpr(String binding, Expr array, Expr body) {
			this.binding = binding;
			this.array = array;
			this.body = body;

		}

		@Override
		public jkind.lustre.IdExpr toLustreExpr() {
			// TODO Auto-generated method stub
			return null;
		}

	}

	public static class ExistsExpr implements Expr {
		public final String binding;
		public final Expr array;
		public final Expr body;

		public ExistsExpr(String binding, Expr array, Expr body) {
			this.binding = binding;
			this.array = array;
			this.body = body;

		}

		@Override
		public jkind.lustre.IdExpr toLustreExpr() {
			// TODO Auto-generated method stub
			return null;
		}

	}

	public static class FlatmapExpr implements Expr {
		public final String binding;
		public final Expr array;
		public final Expr body;

		public FlatmapExpr(String binding, Expr array, Expr body) {
			this.binding = binding;
			this.array = array;
			this.body = body;

		}

		@Override
		public jkind.lustre.IdExpr toLustreExpr() {
			// TODO Auto-generated method stub
			return null;
		}

	}

	public static class FoldLeftExpr implements Expr {
		public final String binding;
		public final Expr array;
		public final String acc;
		public final Expr initial;
		public final Expr update;

		public FoldLeftExpr(String binding, Expr array, String acc, Expr initial, Expr update) {
			this.binding = binding;
			this.array = array;
			this.acc = acc;
			this.initial = initial;
			this.update = update;

		}

		@Override
		public jkind.lustre.IdExpr toLustreExpr() {
			// TODO Auto-generated method stub
			return null;
		}

	}

	public static class FoldRightExpr implements Expr {
		public final String binding;
		public final Expr array;
		public final String acc;
		public final Expr initial;
		public final Expr update;

		public FoldRightExpr(String binding, Expr array, String acc, Expr initial, Expr update) {
			this.binding = binding;
			this.array = array;
			this.acc = acc;
			this.initial = initial;
			this.update = update;

		}

		@Override
		public jkind.lustre.IdExpr toLustreExpr() {
			// TODO Auto-generated method stub
			return null;
		}

	}


	public static interface Prop {

		jkind.lustre.Expr toLustreExpr();

	}

	public static class ExprProp implements Prop {
		public final Expr expr;

		public ExprProp(Expr expr) {
			this.expr = expr;
		}

		@Override
		public jkind.lustre.Expr toLustreExpr() {
			// TODO Auto-generated method stub
			return null;
		}
	}




	public static class PatternProp implements Prop {
		public final Pattern pattern;

		public PatternProp(Pattern pattern) {
			this.pattern = pattern;
		}

		@Override
		public jkind.lustre.Expr toLustreExpr() {
			// TODO Auto-generated method stub
			return null;
		}
	}


	public static interface Pattern {
		public jkind.lustre.Expr toLustreExpr();

		public Map<String, jkind.lustre.Expr> toLustrePatternPropertyMap();

		public Map<String, jkind.lustre.Expr> toLustrePatternConstraintMap();

		public List<jkind.lustre.Expr> toLustrePatternAssertPropertyList();

		public List<jkind.lustre.Expr> toLustrePatternAssertConstraintList();

		public List<jkind.lustre.VarDecl> toLustrePatternChanInPropertyList();

		public List<jkind.lustre.VarDecl> toLustrePatternChanInConstraintList();

		public List<jkind.lustre.VarDecl> toLustrePatternChanOutPropertyList();

		public List<jkind.lustre.VarDecl> toLustrePatternChanOutConstraintList();

		public List<jkind.lustre.VarDecl> toLustrePatternChanBiPropertyList();

		public List<jkind.lustre.VarDecl> toLustrePatternChanBiConstraintList();

	}


	public static class AlwaysPattern implements Pattern {

		public final Expr expr;

		public AlwaysPattern(Expr expr) {
			this.expr = expr;
		}

		@Override
		public jkind.lustre.Expr toLustreExpr() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Map<String, jkind.lustre.Expr> toLustrePatternPropertyMap() {
			return new HashMap<>();
		}

		@Override
		public Map<String, jkind.lustre.Expr> toLustrePatternConstraintMap() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<jkind.lustre.Expr> toLustrePatternAssertPropertyList() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<jkind.lustre.Expr> toLustrePatternAssertConstraintList() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<VarDecl> toLustrePatternChanInPropertyList() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<VarDecl> toLustrePatternChanInConstraintList() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<VarDecl> toLustrePatternChanOutPropertyList() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<VarDecl> toLustrePatternChanOutConstraintList() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<VarDecl> toLustrePatternChanBiPropertyList() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<VarDecl> toLustrePatternChanBiConstraintList() {
			// TODO Auto-generated method stub
			return null;
		}

	}

	public static class Interval {
		public final boolean lowOpen;
		public final Expr low;
		public final Expr high;
		public final boolean highOpen;

		public Interval(boolean lowOpen, Expr low, Expr high, boolean highOpen) {
			this.lowOpen = lowOpen;
			this.low = low;
			this.high = high;
			this.highOpen = highOpen;
		}
	}

	public static class WhenHoldsPattern implements Pattern {

		public final Expr causeCondition;
		public final Interval conditionInterval;
		public final Expr effectEvent;
		public final boolean exclusive;
		public final Interval eventInterval;

		public WhenHoldsPattern(Expr causeCondition, Interval conditionInterval, Expr effectEvent, boolean exclusive,
				Interval eventInterval) {
			this.causeCondition = causeCondition;
			this.conditionInterval = conditionInterval;
			this.effectEvent = effectEvent;
			this.exclusive = exclusive;
			this.eventInterval = eventInterval;
		}

		@Override
		public jkind.lustre.Expr toLustreExpr() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Map<String, jkind.lustre.Expr> toLustrePatternPropertyMap() {
			return new HashMap<>();
		}

		@Override
		public Map<String, jkind.lustre.Expr> toLustrePatternConstraintMap() {
			return new HashMap<>();
		}

		@Override
		public List<jkind.lustre.Expr> toLustrePatternAssertPropertyList() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<jkind.lustre.Expr> toLustrePatternAssertConstraintList() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<VarDecl> toLustrePatternChanInPropertyList() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<VarDecl> toLustrePatternChanInConstraintList() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<VarDecl> toLustrePatternChanOutPropertyList() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<VarDecl> toLustrePatternChanOutConstraintList() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<VarDecl> toLustrePatternChanBiPropertyList() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<VarDecl> toLustrePatternChanBiConstraintList() {
			// TODO Auto-generated method stub
			return null;
		}
	}


	public static class WheneverHoldsPattern implements Pattern {
		public final Expr causeEvent;
		public final Expr effectCondition;
		public final boolean exclusive;
		public final Interval interval;

		public WheneverHoldsPattern(Expr causeEvent, Expr effectCondition, boolean exclusive, Interval interval) {
			this.causeEvent = causeEvent;
			this.effectCondition = effectCondition;
			this.exclusive = exclusive;
			this.interval = interval;
		}

		@Override
		public jkind.lustre.Expr toLustreExpr() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Map<String, jkind.lustre.Expr> toLustrePatternPropertyMap() {
			return new HashMap<>();
		}

		@Override
		public Map<String, jkind.lustre.Expr> toLustrePatternConstraintMap() {
			return new HashMap<>();
		}

		@Override
		public List<jkind.lustre.Expr> toLustrePatternAssertPropertyList() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<jkind.lustre.Expr> toLustrePatternAssertConstraintList() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<VarDecl> toLustrePatternChanInPropertyList() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<VarDecl> toLustrePatternChanInConstraintList() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<VarDecl> toLustrePatternChanOutPropertyList() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<VarDecl> toLustrePatternChanOutConstraintList() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<VarDecl> toLustrePatternChanBiPropertyList() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<VarDecl> toLustrePatternChanBiConstraintList() {
			// TODO Auto-generated method stub
			return null;
		}

	}


	public static class WheneverOccursPattern implements Pattern {
		public final Expr causeEvent;
		public final Expr effectEvent;
		public final boolean exclusive;
		public final Interval interval;

		public WheneverOccursPattern(Expr causeEvent, Expr effectEvent, boolean exclusive, Interval interval) {
			this.causeEvent = causeEvent;
			this.effectEvent = effectEvent;
			this.exclusive = exclusive;
			this.interval = interval;
		}

		@Override
		public jkind.lustre.Expr toLustreExpr() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Map<String, jkind.lustre.Expr> toLustrePatternPropertyMap() {
			String patternIndex = Integer.toString(this.hashCode());

			jkind.lustre.IdExpr lustreCause = causeEvent.toLustreExpr();
			jkind.lustre.IdExpr lustreEffect = effectEvent.toLustreExpr();

			VarDecl timerVar = Lustre.getTimerVar(patternIndex);
			VarDecl runVar = Lustre.getRunningVar(patternIndex);

			VarDecl timeOfCause = Lustre.getTimeOfVar(lustreCause.id);
			VarDecl timeOfEffect = Lustre.getTimeOfVar(lustreEffect.id);

			jkind.lustre.Expr patternExpr = LustreParseUtil.expr("(timer > 0.0 => timeOfCause >= 0.0) and "
					+ "(timer <= time) and"
					+ "(timeOfEffect >= timeOfCause and timer <= high and timeOfEffect >= time - timer + low => not run) and"
					+ "(true -> (pre(timeOfEffect >= timeOfCause + low and timeOfEffect <= timeOfCause + high and timer <= high) => timer = 0.0)) and"
					+ "(timer = 0.0 or timer >= time - timeOfCause)", LustreParseUtil.to("timer", timerVar),
					LustreParseUtil.to("timeOfCause", timeOfCause), LustreParseUtil.to("timeOfEffect", timeOfEffect),
					LustreParseUtil.to("time", new jkind.lustre.IdExpr("time")),
					LustreParseUtil.to("low", this.interval.low.toLustreExpr()),
					LustreParseUtil.to("high", this.interval.high.toLustreExpr()), LustreParseUtil.to("run", runVar));

			HashMap<String, jkind.lustre.Expr> result = new HashMap<>();
			result.put("__PATTERN__" + patternIndex, patternExpr);
			return result;

		}

		@Override
		public Map<String, jkind.lustre.Expr> toLustrePatternConstraintMap() {
			return new HashMap<>();
		}

		@Override
		public List<jkind.lustre.Expr> toLustrePatternAssertPropertyList() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<jkind.lustre.Expr> toLustrePatternAssertConstraintList() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<VarDecl> toLustrePatternChanInPropertyList() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<VarDecl> toLustrePatternChanInConstraintList() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<VarDecl> toLustrePatternChanOutPropertyList() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<VarDecl> toLustrePatternChanOutConstraintList() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<VarDecl> toLustrePatternChanBiPropertyList() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<VarDecl> toLustrePatternChanBiConstraintList() {
			// TODO Auto-generated method stub
			return null;
		}
	}



	public static class PeriodicPattern implements Pattern {
		public final Expr event;
		public final Expr period;
		public final Optional<Expr> jitterOp;

		public PeriodicPattern(Expr event, Expr period, Optional<Expr> jitterOp) {
			this.event = event;
			this.period = period;
			this.jitterOp = jitterOp;
		}

		@Override
		public jkind.lustre.Expr toLustreExpr() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Map<String, jkind.lustre.Expr> toLustrePatternPropertyMap() {
			return new HashMap<>();
		}

		@Override
		public Map<String, jkind.lustre.Expr> toLustrePatternConstraintMap() {

			Map<String, jkind.lustre.Expr> localMap = new HashMap<>();

			String patternIndex = this.hashCode() + "";

			jkind.lustre.VarDecl periodVar = new jkind.lustre.VarDecl("__PERIOD__" + patternIndex, NamedType.REAL);
			jkind.lustre.VarDecl timeoutVar = new jkind.lustre.VarDecl("__TIMER__" + patternIndex, NamedType.REAL);

			jkind.lustre.IdExpr timeoutId = new jkind.lustre.IdExpr(timeoutVar.id);
			jkind.lustre.VarDecl timeofEvent = Lustre.getTimeOfVar(this.event.toLustreExpr().id);

			jkind.lustre.Expr jitter = this.jitterOp.isPresent() ? this.jitterOp.get().toLustreExpr() : null;

			jkind.lustre.Expr lemma1 = expr(
					"(timeOfEvent >= 0.0 and timeOfEvent <> time => timeout - timeOfEvent >= p - j) and "
							+ "(true -> (period <> pre(period) => period - pre(period) <= p + j)) and "
							+ "(timeOfEvent >= 0.0 => timeout - timeOfEvent <= p + j)",
					to("timeOfEvent", timeofEvent), to("time", new jkind.lustre.IdExpr("time")),
					to("timeout", timeoutId), to("p", this.period.toLustreExpr()), to("j", jitter),
					to("period", periodVar));



			jkind.lustre.Expr lemma2 = expr("true -> timeout <> pre(timeout) => timeout - pre(timeout) >= p - j",
					to("timeout", timeoutId), to("p", this.period.toLustreExpr()), to("j", jitter));

			localMap.put("__PATTERN_PERIODIC__1__" + patternIndex, lemma1);
			localMap.put("__PATTERN_PERIODIC__2__" + patternIndex, lemma2);

			return localMap;
		}

		@Override
		public List<jkind.lustre.Expr> toLustrePatternAssertPropertyList() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<jkind.lustre.Expr> toLustrePatternAssertConstraintList() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<VarDecl> toLustrePatternChanInPropertyList() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<VarDecl> toLustrePatternChanInConstraintList() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<VarDecl> toLustrePatternChanOutPropertyList() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<VarDecl> toLustrePatternChanOutConstraintList() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<VarDecl> toLustrePatternChanBiPropertyList() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<VarDecl> toLustrePatternChanBiConstraintList() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public static class SporadicPattern implements Pattern {
		public final Expr event;
		public final Expr iat;
		public final Optional<Expr> jitterOp;

		public SporadicPattern(Expr event, Expr iat, Optional<Expr> jitterOp) {
			this.event = event;
			this.iat = iat;
			this.jitterOp = jitterOp;
		}

		@Override
		public jkind.lustre.Expr toLustreExpr() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Map<String, jkind.lustre.Expr> toLustrePatternPropertyMap() {
			return new HashMap<>();
		}

		@Override
		public Map<String, jkind.lustre.Expr> toLustrePatternConstraintMap() {
			return new HashMap<>();
		}

		@Override
		public List<jkind.lustre.Expr> toLustrePatternAssertPropertyList() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<jkind.lustre.Expr> toLustrePatternAssertConstraintList() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<VarDecl> toLustrePatternChanInPropertyList() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<VarDecl> toLustrePatternChanInConstraintList() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<VarDecl> toLustrePatternChanOutPropertyList() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<VarDecl> toLustrePatternChanOutConstraintList() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<VarDecl> toLustrePatternChanBiPropertyList() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<VarDecl> toLustrePatternChanBiConstraintList() {
			// TODO Auto-generated method stub
			return null;
		}

	}


	public enum SpecTag {
		Assume, Guarantee, Lemma, Assert
	}

	public static class Spec {

		public final SpecTag specTag;
		public final String name;
		public final String description;
		public final Prop prop;

		public Spec(SpecTag specTag, String name, String description, Prop prop) {
			this.specTag = specTag;
			this.name = name;
			this.description = description;
			this.prop = prop;
		}
	}

	public class Initial {
		public final Expr expr;

		public Initial(Expr expr) {
			this.expr = expr;
		}
	}

	public static class Connection {
		public final String name;
		public final Expr src;
		public final Expr dst;
		public final Optional<Expr> exprOp;

		public Connection(String name, Expr src, Expr dst, Optional<Expr> exprOp) {
			this.name = name;
			this.src = src;
			this.dst = dst;
			this.exprOp = exprOp;
		}

		public Equation toLustreEquation() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public static interface TimingMode {

	}

	public static class SyncMode implements TimingMode {

		public final int v1;
		public final Optional<Integer> v2Op;

		public SyncMode(int v1, Optional<Integer> v2Op) {
			this.v1 = v1;
			this.v2Op = v2Op;
		}

	}

	public static class AsyncMode implements TimingMode {
		public AsyncMode() {

		}
	}

	public static class LatchedMode implements TimingMode {
		public LatchedMode() {

		}
	}

	public static class MNSyncMode implements TimingMode {

		public final List<String> subNodeList1;
		public final List<String> subNodeList2;
		public final List<Integer> maxList;
		public final List<Integer> minList;

		public MNSyncMode(List<String> subNodeList1, List<String> subNodeList2, List<Integer> maxList,
				List<Integer> minList) {
			this.subNodeList1 = new ArrayList<>();
			this.subNodeList1.addAll(subNodeList1);
			this.subNodeList2 = new ArrayList<>();
			this.subNodeList2.addAll(subNodeList2);
			this.maxList = new ArrayList<>();
			this.maxList.addAll(maxList);
			this.minList = new ArrayList<>();
			this.minList.addAll(minList);

		}
	}

	public static interface Contract {

		public String getName();

		public jkind.lustre.Type toLustreType();

		public boolean staticEquals(Contract other);

	}

	public static interface DataContract extends Contract {

	}


	public static enum Prim implements DataContract {
		IntContract("int", NamedType.INT), RealContract("real", NamedType.REAL), BoolContract("bool",
				NamedType.BOOL), ErrorContract("<error>", null);

		public final String name;
		public final jkind.lustre.Type lustreType;

		Prim(String name, jkind.lustre.Type lustreType) {
			this.name = name;
			this.lustreType = lustreType;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public jkind.lustre.Type toLustreType() {
			return lustreType;
		}

		@Override
		public boolean staticEquals(Contract other) {
			return this.getName().equals(other.getName());
		}


	}

	public static class RangeIntContract implements DataContract {
		public final String name;
		public final long low;
		public final long high;

		public RangeIntContract(long low, long high) {
			this.name = Prim.IntContract.name;
			this.low = low;
			this.high = high;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public jkind.lustre.Type toLustreType() {
			return NamedType.INT;
		}

		@Override
		public boolean staticEquals(Contract other) {
			return this.getName().equals(other.getName());
		}

	}

	public static class RangeRealContract implements DataContract {
		public final String name;
		public final double low;
		public final double high;

		public RangeRealContract(double f, double g) {
			this.name = Prim.RealContract.name;
			this.low = f;
			this.high = g;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public jkind.lustre.Type toLustreType() {
			return NamedType.REAL;
		}

		@Override
		public boolean staticEquals(Contract other) {
			return this.getName().equals(other.getName());
		}
	}

	public static class EnumContract implements Contract {
		private final String name;
		public final List<String> values;


		public EnumContract(String name, List<String> values) {
			this.name = name;
			this.values = new ArrayList<>();
			this.values.addAll(values);
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public jkind.lustre.Type toLustreType() {
			String lustreName = name.replace("::", "__").replace(".", "__");
			List<String> enumValues = new ArrayList<String>();
			for (String raw : values) {
				String enumValue = raw.replace("::", "__").replace(".", "__");
				enumValues.add(enumValue);
			}
			EnumType lustreEnumType = new EnumType(lustreName, enumValues);
			return lustreEnumType;
		}

		@Override
		public boolean staticEquals(Contract other) {
			return this.getName().equals(other.getName());
		}


	}

	public static interface Direc {
	}

	public static class In implements Direc {

		public In() {

		}

	}

	public static class Out implements Direc {
		public final Optional<Expr> exprOp;

		public Out(Optional<Expr> exprOp) {
			this.exprOp = exprOp;
		}
	}

	public static class Bi implements Direc {

		public Bi() {

		}

	}



	public static class RecordContract implements DataContract {

		private final String name;
		public final Map<String, DataContract> fields;

		/* reference to Xtext elm for gui update */
		public final NamedElement namedElement;

		public RecordContract(String name, Map<String, DataContract> fields, NamedElement namedElement) {
			this.name = name;
			this.fields = new HashMap<>();
			this.fields.putAll(fields);

			this.namedElement = namedElement;

		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public jkind.lustre.Type toLustreType() {
			String lustreName = name.replace("::", "__").replace(".", "__");

			Map<String, jkind.lustre.Type> lustreFields = new HashMap<>();
			for (Entry<String, DataContract> entry : fields.entrySet()) {
				String key = entry.getKey();
				jkind.lustre.Type lt = entry.getValue().toLustreType();
				if (lt != null) {
					lustreFields.put(key, lt);
				}
			}
			jkind.lustre.RecordType lustreRecType = new jkind.lustre.RecordType(lustreName, lustreFields);
			return lustreRecType;
		}

		@Override
		public boolean staticEquals(Contract other) {
			return this.getName().equals(other.getName());
		}

	}


	public static class ArrayContract implements DataContract {

		private final String name;
		public final DataContract stemContract;
		public final int size;

		public ArrayContract(String name, DataContract stemContract, int size) {
			this.name = name;
			this.size = size;
			this.stemContract = stemContract;
		}

		@Override
		public String getName() {
			return name.isEmpty() ? stemContract.getName() + "[" + size + "]" : name;
		}

		@Override
		public jkind.lustre.Type toLustreType() {

			jkind.lustre.Type lustreBaseType = stemContract.toLustreType();
			if (lustreBaseType != null) {
				jkind.lustre.ArrayType lustreArrayType = new jkind.lustre.ArrayType(lustreBaseType, size);
				return lustreArrayType;
			} else {
				return null;
			}

		}

		@Override
		public boolean staticEquals(Contract other) {
			if (other instanceof ArrayContract) {
				return size == ((ArrayContract) other).size && stemContract.staticEquals(((ArrayContract) other).stemContract);
			} else {
				return false;
			}
		}

	}

	public static class Channel {
		public final String name;
		public final DataContract dataContract;
		public final Direc direction;
		public final boolean isEvent;

		public Channel(String name, DataContract dataContract, Direc direction, boolean isEvent) {
			this.name = name;
			this.dataContract = dataContract;
			this.direction = direction;
			this.isEvent = isEvent;
		}

		public VarDecl toLustreVar() {
			// TODO Auto-generated method stub
			return null;
		}

	}

	public static class NodeContract implements Contract {

		private final String name;
		public final Map<String, Channel> channels;
		public final Map<String, NodeContract> subNodes;
		public final List<Connection> connections;
		public final List<Spec> specList;
		public final Optional<TimingMode> timingMode;
		public final boolean isMain;

		/* reference to Xtext elm for gui update */
		public final NamedElement namedElement;

		public NodeContract(String name, Map<String, Channel> channels, Map<String, NodeContract> subNodes,
				List<Connection> connections, List<Spec> specList, Optional<TimingMode> timingMode, boolean isMain,
				NamedElement namedElement) {
			this.name = name;
			this.channels = new HashMap<>();
			this.channels.putAll(channels);
			this.subNodes = new HashMap<>();
			this.subNodes.putAll(subNodes);
			this.connections = new ArrayList<>();
			this.connections.addAll(connections);
			this.specList = specList;
			this.timingMode = timingMode;
			this.isMain = isMain;

			this.namedElement = namedElement;

		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public jkind.lustre.Type toLustreType() {
			String lustreName = name.replace("::", "__").replace(".", "__");

			Map<String, jkind.lustre.Type> lustreFields = new HashMap<>();
			for (Entry<String, Channel> entry : channels.entrySet()) {
				String key = entry.getKey();
				jkind.lustre.Type lt = entry.getValue().dataContract.toLustreType();
				if (lt != null) {
					lustreFields.put(key, lt);
				}
			}
			jkind.lustre.RecordType lustreRecType = new jkind.lustre.RecordType(lustreName, lustreFields);
			return lustreRecType;
		}

		@Override
		public boolean staticEquals(Contract other) {
			return this.getName().equals(other.getName());
		}

		private Optional<Node> lustreNodeCache = Optional.empty();

		public Node toLustreNode() {

			if (this.lustreNodeCache.isPresent()) {
				return lustreNodeCache.get();
			}

			List<jkind.lustre.VarDecl> inputs = new ArrayList<>();
			List<jkind.lustre.VarDecl> locals = new ArrayList<>();
			List<jkind.lustre.Equation> equations = new ArrayList<>();
			List<jkind.lustre.Expr> assertions = this.toLustreAssertList();
			List<String> ivcs = new ArrayList<>();

			for (Entry<String, jkind.lustre.Expr> entry : this.toLustreAssumeMap().entrySet()) {
				String inputName = entry.getKey();
				jkind.lustre.Expr expr = entry.getValue();
				inputs.add(new VarDecl(inputName, NamedType.BOOL));
				assertions.add(new BinaryExpr(new jkind.lustre.IdExpr(inputName), BinaryOp.EQUAL, expr));
			}

			for (Entry<String, jkind.lustre.Expr> entry : this.toLustreLemmaMap().entrySet()) {
				String inputName = entry.getKey();
				jkind.lustre.Expr expr = entry.getValue();
				inputs.add(new VarDecl(inputName, NamedType.BOOL));
				assertions.add(new BinaryExpr(new jkind.lustre.IdExpr(inputName), BinaryOp.EQUAL, expr));
			}

			jkind.lustre.Expr guarConjExpr = new jkind.lustre.BoolExpr(true);
			for (Entry<String, jkind.lustre.Expr> entry : this.toLustreGuaranteeMap().entrySet()) {
				String inputName = entry.getKey();
				jkind.lustre.Expr expr = entry.getValue();
				locals.add(new VarDecl(inputName, NamedType.BOOL));
				jkind.lustre.IdExpr guarId = new jkind.lustre.IdExpr(inputName);
				equations.add(new Equation(guarId, expr));
				ivcs.add(inputName);
				guarConjExpr = Lustre.makeANDExpr(guarId, guarConjExpr);
			}

			for (Entry<String, jkind.lustre.Expr> entry : this.toLustreLemmaMap().entrySet()) {
				jkind.lustre.Expr expr = entry.getValue();
				guarConjExpr = Lustre.makeANDExpr(expr, guarConjExpr);
			}

			jkind.lustre.IdExpr assumHist = new jkind.lustre.IdExpr("__ASSUME__HIST");
			inputs.add(new VarDecl(assumHist.id, NamedType.BOOL));


			jkind.lustre.Expr assertExpr = new BinaryExpr(assumHist, BinaryOp.IMPLIES, guarConjExpr);
			for (jkind.lustre.Expr expr : assertions) {
				assertExpr = Lustre.makeANDExpr(expr, assertExpr);
			}

			for (Entry<String, jkind.lustre.Expr> entry : this.toLustrePatternPropMap().entrySet()) {
				String patternVarName = entry.getKey();
				inputs.add(new VarDecl(patternVarName, NamedType.BOOL));
				jkind.lustre.Expr expr = new jkind.lustre.BinaryExpr(new jkind.lustre.IdExpr(patternVarName),
						BinaryOp.EQUAL, entry.getValue());
				assertExpr = Lustre.makeANDExpr(expr, assertExpr);
			}

			inputs.addAll(this.toLustreChanInList());

			inputs.addAll(this.toLustreChanOutList());

			inputs.addAll(this.toLustreChanBiList());

			equations.addAll(this.toLustreEquationList());

			String outputName = "__ASSERT";
			List<VarDecl> outputs = new ArrayList<>();
			outputs.add(new VarDecl(outputName, NamedType.BOOL));
			equations.add(new Equation(new jkind.lustre.IdExpr(outputName), assertExpr));

			// TODO : add in properties for Nenola connections - see ASTBuilder

			NodeBuilder builder = new NodeBuilder(this.getName());
			builder.addInputs(inputs);
			builder.addOutputs(outputs);
			builder.addLocals(locals);
			builder.addEquations(equations);
			builder.addIvcs(ivcs);
			Node node = builder.build();

			lustreNodeCache = Optional.of(node);

			return node;

		}

		private List<Equation> toLustreEquationList() {
			List<Equation> equations = new ArrayList<>();

			for (Connection conn : this.connections) {
				equations.add(conn.toLustreEquation());
			}

			// TODO add real time pattern connections
			return equations;
		}

		private List<VarDecl> toLustreChanBiList() {

			List<VarDecl> vars = new ArrayList<>();

			for (Channel chan : this.channels.values()) {
				if (chan.direction instanceof Bi) {
					vars.add(chan.toLustreVar());
				}
			}

			for (Spec spec : this.specList) {

				if (spec.prop instanceof PatternProp) {
					Pattern pattern = ((PatternProp) spec.prop).pattern;

					List<jkind.lustre.VarDecl> localList = this.isProperty(spec.specTag)
							? pattern.toLustrePatternChanBiPropertyList()
							: pattern.toLustrePatternChanBiConstraintList();
					vars.addAll(localList);
				}

			}

			return null;
		}

		private Optional<List<VarDecl>> chanOutListCache = Optional.empty();
		private List<VarDecl> toLustreChanOutList() {

			if (chanOutListCache.isPresent()) {
				return chanOutListCache.get();
			}

			List<VarDecl> vars = new ArrayList<>();
			for (Channel chan : this.channels.values()) {
				if (chan.direction instanceof Out) {
					vars.add(chan.toLustreVar());
				}
			}

			for (Spec spec : this.specList) {

				if (spec.prop instanceof PatternProp) {
					Pattern pattern = ((PatternProp) spec.prop).pattern;

					List<jkind.lustre.VarDecl> localList = this.isProperty(spec.specTag)
							? pattern.toLustrePatternChanOutPropertyList()
							: pattern.toLustrePatternChanOutConstraintList();
					vars.addAll(localList);
				}

			}

			for (Entry<String, NodeContract> entry : this.subNodes.entrySet()) {
				String prefix = entry.getKey();
				NodeContract nc = entry.getValue();

				for (VarDecl nestedVar : nc.toLustreChanOutList()) {
					String id = prefix + "__" + nestedVar.id;
					jkind.lustre.Type type = nestedVar.type;
					vars.add(new VarDecl(id, type));
				}

				for (String assumeKey : nc.toLustreAssumeMap().keySet()) {
					String id = prefix + "__" + assumeKey;
					vars.add(new VarDecl(id, NamedType.BOOL));
				}

				for (String propKey : nc.toLustreLemmaMap().keySet()) {
					String id = prefix + "__" + propKey;
					vars.add(new VarDecl(id, NamedType.BOOL));
				}

				for (String propKey : nc.toLustrePatternPropMap().keySet()) {
					String id = prefix + "__" + propKey;
					vars.add(new VarDecl(id, NamedType.BOOL));
				}

				vars.add(new VarDecl(prefix + "__ASSUME__HIST", NamedType.BOOL));

			}

			chanOutListCache = Optional.of(vars);

			return vars;
		}

		private Optional<List<VarDecl>> chanInListCache = Optional.empty();
		private List<VarDecl> toLustreChanInList() {

			if (chanInListCache.isPresent()) {
				return chanInListCache.get();
			}

			List<VarDecl> vars = new ArrayList<>();
			for (Channel chan : this.channels.values()) {
				if (chan.direction instanceof In) {
					vars.add(chan.toLustreVar());
				}
			}

			for (Spec spec : this.specList) {

				if (spec.prop instanceof PatternProp) {
					Pattern pattern = ((PatternProp) spec.prop).pattern;

					List<jkind.lustre.VarDecl> localList = this.isProperty(spec.specTag)
							? pattern.toLustrePatternChanInPropertyList()
							: pattern.toLustrePatternChanInConstraintList();
					vars.addAll(localList);
				}

			}

			for (Entry<String, NodeContract> entry : this.subNodes.entrySet()) {
				String prefix = entry.getKey();
				NodeContract nc = entry.getValue();
				for (VarDecl nestedVar : nc.toLustreChanInList()) {
					String id = prefix + "__" + nestedVar.id;
					jkind.lustre.Type type = nestedVar.type;
					vars.add(new VarDecl(id, type));
				}

				vars.add(nc.toLustreClockVar());


			}

			chanInListCache = Optional.of(vars);

			return vars;
		}


		private VarDecl toLustreClockVar() {
			return new jkind.lustre.VarDecl(this.getName() + "__CLOCK_", jkind.lustre.NamedType.BOOL);
		}

		private Optional<Map<String, jkind.lustre.Expr>> lustrePatternMapCache = Optional.empty();

		private boolean isProperty(SpecTag specTag) {
			return (specTag == SpecTag.Assume && !this.isMain) || (specTag == SpecTag.Lemma && this.isMain)
					|| (specTag == SpecTag.Guarantee && this.isMain);

		}

		private Map<String, jkind.lustre.Expr> toLustrePatternPropMap() {

			if (lustrePatternMapCache.isPresent()) {
				return lustrePatternMapCache.get();
			}


			Map<String, jkind.lustre.Expr> props = new HashMap<>();
			for (Spec spec : this.specList) {

				if (spec.prop instanceof PatternProp) {
					Pattern pattern = ((PatternProp) spec.prop).pattern;

					Map<String, jkind.lustre.Expr> localMap = this.isProperty(spec.specTag)
							? pattern.toLustrePatternPropertyMap()
							: pattern.toLustrePatternConstraintMap();
					props.putAll(localMap);
				}
			}

			for (Entry<String, NodeContract> entry : this.subNodes.entrySet()) {
				String prefix = entry.getKey();
				NodeContract nc = entry.getValue();

				for (Entry<String, jkind.lustre.Expr> nestedEntry : nc.toLustrePatternPropMap().entrySet()) {
					String key = prefix + "__" + nestedEntry.getKey();
					jkind.lustre.Expr expr = nestedEntry.getValue();
					props.put(key, expr);
				}

			}

			lustrePatternMapCache = Optional.of(props);

			return props;
		}

		private Optional<List<jkind.lustre.Expr>> lustreAssertListCache = Optional.empty();

		private List<jkind.lustre.Expr> toLustreAssertList() {

			if (lustreAssertListCache.isPresent()) {
				return lustreAssertListCache.get();
			}

			List<jkind.lustre.Expr> exprs = new ArrayList<>();

			for (Spec spec : this.specList) {
				if (spec.specTag == SpecTag.Assert) {
					jkind.lustre.Expr expr = spec.prop.toLustreExpr();
					exprs.add(expr);
				}

				if (spec.prop instanceof PatternProp) {
					Pattern pattern = ((PatternProp) spec.prop).pattern;

					List<jkind.lustre.Expr> localList = this.isProperty(spec.specTag)
							? pattern.toLustrePatternAssertPropertyList()
							: pattern.toLustrePatternAssertConstraintList();
					exprs.addAll(localList);
				}

			}

			for (Entry<String, NodeContract> entry : this.subNodes.entrySet()) {
				NodeContract nc = entry.getValue();

				for (jkind.lustre.Expr subAssert : nc.toLustreAssertList()) {
					exprs.add(subAssert);
				}
			}

			lustreAssertListCache = Optional.of(exprs);
			return null;
		}


		private Optional<Map<String, jkind.lustre.Expr>> lustreGuaranteeMapCache = Optional.empty();
		private Map<String, jkind.lustre.Expr> toLustreGuaranteeMap() {
			if (lustreGuaranteeMapCache.isPresent()) {
				return lustreGuaranteeMapCache.get();
			}

			Map<String, jkind.lustre.Expr> exprMap = new HashMap<>();
			int suffix = 0;
			for (Spec spec : this.specList) {

				if (spec.specTag == SpecTag.Guarantee) {
					String key = SpecTag.Guarantee.name() + "__" + suffix;
					jkind.lustre.Expr expr = spec.prop.toLustreExpr();
					exprMap.put(key, expr);

					suffix = suffix + 1;
				}

			}

			lustreGuaranteeMapCache = Optional.of(exprMap);

			return exprMap;
		}

		private Optional<Map<String, jkind.lustre.Expr>> lustreLemmaMapCache = Optional.empty();
		private Map<String, jkind.lustre.Expr> toLustreLemmaMap() {
			if (lustreLemmaMapCache.isPresent()) {
				return lustreLemmaMapCache.get();
			}

			Map<String, jkind.lustre.Expr> exprMap = new HashMap<>();
			int suffix = 0;
			for (Spec spec : this.specList) {

				if (spec.specTag == SpecTag.Lemma) {
					String key = SpecTag.Lemma.name() + "__" + suffix;
					jkind.lustre.Expr expr = spec.prop.toLustreExpr();
					exprMap.put(key, expr);

					suffix = suffix + 1;
				}

			}

			lustreLemmaMapCache = Optional.of(exprMap);

			return exprMap;
		}

		private Optional<Map<String, jkind.lustre.Expr>> lustreAssumeMapCache = Optional.empty();
		private Map<String, jkind.lustre.Expr> toLustreAssumeMap() {

			if (lustreAssumeMapCache.isPresent()) {
				return lustreAssumeMapCache.get();
			}

			Map<String, jkind.lustre.Expr> exprMap = new HashMap<>();
			int suffix = 0;
			for (Spec spec : this.specList) {

				if (spec.specTag == SpecTag.Assume) {
					String key = SpecTag.Assume.name() + "__" + suffix;
					jkind.lustre.Expr expr = spec.prop.toLustreExpr();
					exprMap.put(key, expr);

					suffix = suffix + 1;
				}

			}

			lustreAssumeMapCache = Optional.of(exprMap);

			return exprMap;
		}

		public List<Node> toLustreNodesFromNesting() {

			List<Node> nodes = new ArrayList<>();
			nodes.add(this.toLustreNode());
			for (NodeContract subNodeContract : this.subNodes.values()) {
				nodes.addAll(subNodeContract.toLustreNodesFromNesting());
			}

			return nodes;

		}

	}


	public static boolean staticEqual(Contract t1, Contract t2) {
		return t1.staticEquals(t2);
	}

	public static class DataFlow {
		public final List<String> tgts;
		public final Expr src;

		public DataFlow(List<String> tgts, Expr src) {
			this.tgts = tgts;
			this.src = src;
		}

		public DataFlow(String tgt, Expr src) {
			this.tgts = Collections.singletonList(tgt);
			this.src = src;
		}
	}

	public static class NodeGen {

		public final String name;
		public final Map<String, Channel> channels;
		public final List<DataFlow> dataFlows;
		public final List<String> properties;

		public NodeGen(String name, Map<String, Channel> channels, List<DataFlow> dataFlows, List<String> properties) {
			this.name = name;
			this.channels = channels;
			this.dataFlows = dataFlows;
			this.properties = properties;
		}

		public Node toLustreNode() {
			// TODO Auto-generated method stub
			return null;
		}

	}

	public static class Program {
		public final NodeContract main;
		public final Map<String, Contract> contractMap;
		public final Map<String, NodeGen> nodeGenMap;

		public Program(NodeContract main, Map<String, Contract> contractMap, Map<String, NodeGen> nodeGenMap) {
			this.main = main;
			this.contractMap = contractMap;
			this.nodeGenMap = nodeGenMap;
		}

		public Map<String, jkind.lustre.Program> toRecursiveLustrePrograms() {
			// TODO Auto-generated method stub
			return null;
		}

		public Map<String, jkind.lustre.Program> toRealizabilityLustrePrograms() {
			// TODO Auto-generated method stub
			return null;
		}

		public Map<String, jkind.lustre.Program> toMonolithicLustrePrograms() {
			// TODO Auto-generated method stub
			return null;
		}

		public Map<String, jkind.lustre.Program> toSingleLustrePrograms(boolean usingKind2) {
			// TODO Auto-generated method stub

			Map<String, jkind.lustre.Program> programMap = this.toConsistencyPrograms();

			if (usingKind2) {
				programMap.putAll(this.toContractPrograms());
			} else {
				programMap.putAll(this.toAssumeGuaranteePrograms());
			}

			return null;
		}

		private Map<String, jkind.lustre.Program> toContractPrograms() {
			// TODO Auto-generated method stub

			return null;
		}

		private Map<String, jkind.lustre.Program> toAssumeGuaranteePrograms() {
			List<jkind.lustre.TypeDef> lustreTypes = this.lustreTypesFromDataContracts();
			List<jkind.lustre.Node> lustreNodes = new ArrayList<>();
			lustreNodes.addAll(this.toLustreNodesFromNodeGenList());
			lustreNodes.addAll(this.lustreNodesFromMain());
			jkind.lustre.Program program = new jkind.lustre.Program(Location.NULL, lustreTypes, null, null, lustreNodes,
					this.main.getName());
			Map<String, jkind.lustre.Program> programs = new HashMap<>();
			programs.put("Contract Guarantees", program);
			return programs;
		}

		private List<Node> lustreNodesFromMain() {
			return this.main.toLustreNodesFromNesting();
		}


		private List<Node> toLustreNodesFromNodeGenList() {
			List<jkind.lustre.Node> lustreNodes = new ArrayList<>();
			for (NodeGen nodeGen : this.nodeGenMap.values()) {

				jkind.lustre.Node lustreNode = nodeGen.toLustreNode();
				lustreNodes.add(lustreNode);

			}

			return lustreNodes;
		}

		private List<TypeDef> lustreTypesFromDataContracts() {
			List<jkind.lustre.TypeDef> lustreTypes = new ArrayList<>();
			for (Entry<String, Contract> entry : this.contractMap.entrySet()) {

				String name = entry.getKey();
				Contract contract = entry.getValue();
				if (contract instanceof DataContract) {
					jkind.lustre.Type lustreType = ((DataContract) contract).toLustreType();
					jkind.lustre.TypeDef lustreTypeDef = new jkind.lustre.TypeDef(name, lustreType);
					lustreTypes.add(lustreTypeDef);

				}
			}

			return lustreTypes;
		}


		private Map<String, jkind.lustre.Program> toConsistencyPrograms() {
			// TODO Auto-generated method stub

			return null;
		}

	}



}