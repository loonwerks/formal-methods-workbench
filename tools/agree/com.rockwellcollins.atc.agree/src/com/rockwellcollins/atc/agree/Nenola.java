package com.rockwellcollins.atc.agree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.osate.aadl2.NamedElement;

import com.rockwellcollins.atc.agree.analysis.translation.LustreExprFactory;

import jkind.lustre.BinaryExpr;
import jkind.lustre.BinaryOp;
import jkind.lustre.BoolExpr;
import jkind.lustre.EnumType;
import jkind.lustre.Equation;
import jkind.lustre.Location;
import jkind.lustre.NamedType;
import jkind.lustre.Node;
import jkind.lustre.TypeDef;
import jkind.lustre.VarDecl;
import jkind.lustre.builders.NodeBuilder;

//Nenola = Nested Node Language
public class Nenola {


	public static enum Rator {
		Equal, StreamCons, Implies, Equiv, Conj, Disj, NotEqual, LessThan, LessEq, GreatThan, GreatEq, Plus, Minus, Mult, Div, Mod, Pow, Neg, Not
	}

	public static enum Tag {
		Clock, Insert, Remove, Count
	}

	public static interface Expr {

	}

	public static class TagExpr implements Expr {

		public final Expr target;
		public final Tag tag;

		public TagExpr(Expr target, Tag tag) {
			this.target = target;
			this.tag = tag;
		}
	}

	public static class IdExpr implements Expr {
		public final String name;

		public IdExpr(String name) {
			this.name = name;
		}
	}

	public static class SelectionExpr implements Expr {
		public final Expr target;
		public final String selection;

		public SelectionExpr(Expr target, String selection) {
			this.target = target;
			this.selection = selection;

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
	}

	public static class PrevExpr implements Expr {
		public final Expr body;
		public final Expr init;

		public PrevExpr(Expr body, Expr init) {
			this.body = body;
			this.init = init;
		}
	}

	public static class LocalProperty implements Expr {
		public final String propName;

		public LocalProperty(String propName) {
			this.propName = propName;
		}
	}

	public static class ForeignProperty implements Expr {
		public final String nodeName;
		public final String propName;

		public ForeignProperty(String nodeName, String propName) {
			this.nodeName = nodeName;
			this.propName = propName;
		}
	}

	public static class IntLit implements Expr {
		String val;

		public IntLit(String val) {
			this.val = val;
		}
	}

	public static class RealLit implements Expr {
		String val;

		public RealLit(String val) {
			this.val = val;
		}
	}

	public static class BoolLit implements Expr {
		boolean val;

		public BoolLit(boolean val) {
			this.val = val;
		}
	}

	public static class Floor implements Expr {
		Expr arg;

		public Floor(Expr arg) {
			this.arg = arg;
		}
	}

	public static class RealCast implements Expr {
		Expr arg;

		public RealCast(Expr arg) {
			this.arg = arg;
		}
	}

	public static class Latch implements Expr {
		Expr arg;

		public Latch(Expr arg) {
			this.arg = arg;
		}
	}

	public static class Pre implements Expr {
		Expr arg;

		public Pre(Expr arg) {
			this.arg = arg;
		}
	}

	public static class Event implements Expr {
		String arg;

		public Event(String arg) {
			this.arg = arg;
		}
	}

	public static class TimeOf implements Expr {
		String arg;

		public TimeOf(String arg) {
			this.arg = arg;
		}
	}

	public static class TimeRise implements Expr {
		String arg;

		public TimeRise(String arg) {
			this.arg = arg;
		}
	}

	public static class TimeFall implements Expr {
		String arg;

		public TimeFall(String arg) {
			this.arg = arg;
		}
	}

	public static class Time implements Expr {

		public Time() {

		}
	}

	public static class EnumLit implements Expr {
		public final String contractName;
		public final String variantName;

		public EnumLit(String contractName, String variantName) {
			this.contractName = contractName;
			this.variantName = variantName;

		}
	}

	public static class ArrayLit implements Expr {
		public final List<Expr> elements;

		public ArrayLit(List<Expr> elements) {
			this.elements = elements;
		}
	}

	public static class ArrayUpdate implements Expr {
		public final List<Expr> indices;
		public final List<Expr> elements;

		public ArrayUpdate(List<Expr> indices, List<Expr> elements) {
			this.indices = indices;
			this.elements = elements;
		}
	}

	public static class RecordLit implements Expr {
		public final Map<String, Expr> fields;

		public RecordLit(Map<String, Expr> fields) {
			this.fields = fields;
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
	}

	public static class App implements Expr {
		public final String fnName;
		public final List<Expr> args;

		public App(String fnName, List<Expr> args) {
			this.fnName = fnName;
			this.args = args;
		}
	}

	public static class UnaryExpr implements Expr {
		public final Rator rator;
		public final Expr rand;

		public UnaryExpr(Rator rator, Expr rand) {
			this.rator = rator;
			this.rand = rand;
		}
	}

	public static class ArraySubExpr implements Expr {
		public final Expr array;
		public final Expr index;

		public ArraySubExpr(Expr array, Expr index) {
			this.array = array;
			this.index = index;
		}
	}

	public static class IndicesExpr implements Expr {
		public final Expr array;

		public IndicesExpr(Expr array) {
			this.array = array;
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

		public final Expr condition;
		public final Interval conditionInterval;
		public final Expr event;
		public final boolean exclusive;
		public final Interval eventInterval;

		public WhenHoldsPattern(Expr condition, Interval conditionInterval, Expr event, boolean exclusive,
				Interval eventInterval) {
			this.condition = condition;
			this.conditionInterval = conditionInterval;
			this.event = event;
			this.exclusive = exclusive;
			this.eventInterval = eventInterval;
		}

		@Override
		public jkind.lustre.Expr toLustreExpr() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public static class WhenOccursPattern implements Pattern {
		public final Expr condition;
		public final Expr frequency;
		public final Interval interval;
		public final boolean exclusive;
		public final Expr event;

		public WhenOccursPattern(Expr condition, Expr frequency, Interval interval, boolean exclusive, Expr event) {
			this.condition = condition;
			this.frequency = frequency;
			this.interval = interval;
			this.exclusive = exclusive;
			this.event = event;
		}

		@Override
		public jkind.lustre.Expr toLustreExpr() {
			// TODO Auto-generated method stub
			return null;
		}
	}


	public static class WheneverOccursPattern implements Pattern {
		public final Expr cause;
		public final Expr effect;
		public final boolean exclusive;
		public final Interval interval;

		public WheneverOccursPattern(Expr cause, Expr effect, boolean exclusive, Interval interval) {
			this.cause = cause;
			this.effect = effect;
			this.exclusive = exclusive;
			this.interval = interval;
		}

		@Override
		public jkind.lustre.Expr toLustreExpr() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public static class WheneverBecomesTruePattern implements Pattern {
		public final Expr cause;
		public final Expr effect;
		public final boolean exclusive;
		public final Interval interval;

		public WheneverBecomesTruePattern(Expr cause, Expr effect, boolean exclusive, Interval interval) {
			this.cause = cause;
			this.effect = effect;
			this.exclusive = exclusive;
			this.interval = interval;
		}

		@Override
		public jkind.lustre.Expr toLustreExpr() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public static class WheneverHoldsPattern implements Pattern {
		public final Expr cause;
		public final Expr effect;
		public final boolean exclusive;
		public final Interval interval;

		public WheneverHoldsPattern(Expr cause, Expr effect, boolean exclusive, Interval interval) {
			this.cause = cause;
			this.effect = effect;
			this.exclusive = exclusive;
			this.interval = interval;
		}

		@Override
		public jkind.lustre.Expr toLustreExpr() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public static class WheneverImpliesPattern implements Pattern {
		public final Expr cause;
		public final Expr lhs;
		public final Expr rhs;
		public final boolean exclusive;
		public final Interval interval;

		public WheneverImpliesPattern(Expr cause, Expr lhs, Expr rhs, boolean exclusive, Interval interval) {
			this.cause = cause;
			this.lhs = lhs;
			this.rhs = rhs;
			this.exclusive = exclusive;
			this.interval = interval;
		}

		@Override
		public jkind.lustre.Expr toLustreExpr() {
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

		/* reference to Xtext elm for gui update */
		public final NamedElement namedElement;

		public NodeContract(String name, Map<String, Channel> channels, Map<String, NodeContract> subNodes,
				List<Connection> connections, List<Spec> specList, Optional<TimingMode> timingMode,
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

		private Optional<Node> lustreMainNodeCache = Optional.empty();
		private Optional<Node> lustreSubNodeCache = Optional.empty();

		public Node toLustreNode(boolean isMain) {

			if (isMain && this.lustreMainNodeCache.isPresent()) {
				return lustreMainNodeCache.get();
			}

			if (!isMain && this.lustreSubNodeCache.isPresent()) {
				return lustreSubNodeCache.get();
			}

			List<jkind.lustre.VarDecl> inputs = new ArrayList<>();
			List<jkind.lustre.VarDecl> locals = new ArrayList<>();
			List<jkind.lustre.Equation> equations = new ArrayList<>();
			List<jkind.lustre.Expr> assertions = new ArrayList<>();
			List<String> ivcs = new ArrayList<>();

			for (jkind.lustre.Expr expr : this.toLustreAssertList()) {
				assertions.add(expr);
			}

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
				guarConjExpr = LustreExprFactory.makeANDExpr(guarId, guarConjExpr);
			}

			for (Entry<String, jkind.lustre.Expr> entry : this.toLustreLemmaMap().entrySet()) {
				jkind.lustre.Expr expr = entry.getValue();
				guarConjExpr = LustreExprFactory.makeANDExpr(expr, guarConjExpr);
			}

			jkind.lustre.IdExpr assumHist = new jkind.lustre.IdExpr("__ASSUME__HIST");
			inputs.add(new VarDecl(assumHist.id, NamedType.BOOL));
			assertions.add(new BinaryExpr(assumHist, BinaryOp.IMPLIES, guarConjExpr));

			for (Entry<String, jkind.lustre.Expr> entry : this.toLustrePatternMap(isMain).entrySet()) {
				String inputName = entry.getKey();
				jkind.lustre.Expr expr = entry.getValue();
				inputs.add(new VarDecl(inputName, NamedType.BOOL));
				assertions.add(new BinaryExpr(new jkind.lustre.IdExpr(inputName), BinaryOp.EQUAL, expr));
			}

			jkind.lustre.Expr assertExpr = new BoolExpr(true);
			for (jkind.lustre.Expr expr : assertions) {
				assertExpr = LustreExprFactory.makeANDExpr(expr, assertExpr);
			}


			// gather the remaining inputs

			for (VarDecl v : this.toLustreChanInList()) {
				inputs.add(v);
			}

			for (VarDecl v : this.toLustreChanOutList()) {
				inputs.add(v);
			}

			for (Channel chan : this.channels.values()) {
				if (chan.direction instanceof Bi) {
					locals.add(chan.toLustreVar());
				}
			}

			for (Connection conn : this.connections) {
				equations.add(conn.toLustreEquation());
			}

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

			if (isMain) {
				lustreMainNodeCache = Optional.of(node);
			} else {
				lustreSubNodeCache = Optional.of(node);
			}
			return node;
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

				for (String propKey : nc.toLustrePatternMap(false).keySet()) {
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

		private Optional<Map<String, jkind.lustre.Expr>> lustreMainPatternMapCache = Optional.empty();
		private Optional<Map<String, jkind.lustre.Expr>> lustreSubPatternMapCache = Optional.empty();
		private Map<String, jkind.lustre.Expr> toLustrePatternMap(boolean isMain) {
			if (isMain && lustreMainPatternMapCache.isPresent()) {
				return lustreMainPatternMapCache.get();
			}

			if (!isMain && lustreSubPatternMapCache.isPresent()) {
				return lustreSubPatternMapCache.get();
			}

			Map<String, jkind.lustre.Expr> props = new HashMap<>();
			for (Spec spec : this.specList) {
				if (spec.prop instanceof PatternProp) {

					// TODO - implement the patterns
					// cases on isMain
					// props.put("PATTERN", );
				}
			}

			for (Entry<String, NodeContract> entry : this.subNodes.entrySet()) {
				String prefix = entry.getKey();
				NodeContract nc = entry.getValue();

				for (Entry<String, jkind.lustre.Expr> nestedEntry : nc.toLustrePatternMap(false).entrySet()) {
					String key = prefix + "__" + nestedEntry.getKey();
					jkind.lustre.Expr expr = nestedEntry.getValue();
					props.put(key, expr);
				}

			}

			if (isMain) {
				lustreMainPatternMapCache = Optional.of(props);

			} else {
				lustreSubPatternMapCache = Optional.of(props);

			}

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

			}

			// TODO : gather asserts from subnodes
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

		public List<Node> toLustreNodesFromNesting(boolean isMain) {

			List<Node> nodes = new ArrayList<>();
			nodes.add(this.toLustreNode(isMain));
			for (NodeContract subNodeContract : this.subNodes.values()) {
				nodes.addAll(subNodeContract.toLustreNodesFromNesting(false));
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
			return this.main.toLustreNodesFromNesting(true);
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