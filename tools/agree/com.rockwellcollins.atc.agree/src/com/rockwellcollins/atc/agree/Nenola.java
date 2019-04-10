package com.rockwellcollins.atc.agree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.osate.aadl2.NamedElement;

import jkind.lustre.EnumType;
import jkind.lustre.NamedType;

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

	}

	public static class ExprProp implements Prop {
		public final Expr expr;

		public ExprProp(Expr expr) {
			this.expr = expr;
		}
	}


	public static class AlwaysProp implements Prop {

		public final Expr expr;

		public AlwaysProp(Expr expr) {
			this.expr = expr;
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

	public static class WhenHoldsProp implements Prop {

		public final Expr condition;
		public final Interval conditionInterval;
		public final Expr event;
		public final boolean exclusive;
		public final Interval eventInterval;

		public WhenHoldsProp(Expr condition, Interval conditionInterval, Expr event, boolean exclusive,
				Interval eventInterval) {
			this.condition = condition;
			this.conditionInterval = conditionInterval;
			this.event = event;
			this.exclusive = exclusive;
			this.eventInterval = eventInterval;
		}
	}

	public static class WhenOccursProp implements Prop {
		public final Expr condition;
		public final Expr frequency;
		public final Interval interval;
		public final boolean exclusive;
		public final Expr event;

		public WhenOccursProp(Expr condition, Expr frequency, Interval interval, boolean exclusive, Expr event) {
			this.condition = condition;
			this.frequency = frequency;
			this.interval = interval;
			this.exclusive = exclusive;
			this.event = event;
		}
	}


	public static class WheneverOccursProp implements Prop {
		public final Expr cause;
		public final Expr effect;
		public final boolean exclusive;
		public final Interval interval;

		public WheneverOccursProp(Expr cause, Expr effect, boolean exclusive, Interval interval) {
			this.cause = cause;
			this.effect = effect;
			this.exclusive = exclusive;
			this.interval = interval;
		}
	}

	public static class WheneverBecomesTrueProp implements Prop {
		public final Expr cause;
		public final Expr effect;
		public final boolean exclusive;
		public final Interval interval;

		public WheneverBecomesTrueProp(Expr cause, Expr effect, boolean exclusive, Interval interval) {
			this.cause = cause;
			this.effect = effect;
			this.exclusive = exclusive;
			this.interval = interval;
		}
	}

	public static class WheneverHoldsProp implements Prop {
		public final Expr cause;
		public final Expr effect;
		public final boolean exclusive;
		public final Interval interval;

		public WheneverHoldsProp(Expr cause, Expr effect, boolean exclusive, Interval interval) {
			this.cause = cause;
			this.effect = effect;
			this.exclusive = exclusive;
			this.interval = interval;
		}
	}

	public static class WheneverImpliesProp implements Prop {
		public final Expr cause;
		public final Expr lhs;
		public final Expr rhs;
		public final boolean exclusive;
		public final Interval interval;

		public WheneverImpliesProp(Expr cause, Expr lhs, Expr rhs, boolean exclusive, Interval interval) {
			this.cause = cause;
			this.lhs = lhs;
			this.rhs = rhs;
			this.exclusive = exclusive;
			this.interval = interval;
		}
	}

	public static class PeriodicProp implements Prop {
		public final Expr event;
		public final Expr period;
		public final Optional<Expr> jitterOp;

		public PeriodicProp(Expr event, Expr period, Optional<Expr> jitterOp) {
			this.event = event;
			this.period = period;
			this.jitterOp = jitterOp;
		}
	}


	public static class SporadicProp implements Prop {
		public final Expr event;
		public final Expr iat;
		public final Optional<Expr> jitterOp;

		public SporadicProp(Expr event, Expr iat, Optional<Expr> jitterOp) {
			this.event = event;
			this.iat = iat;
			this.jitterOp = jitterOp;
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
	}

	public static interface TimingMode {

	}

	public static class SynchMode implements TimingMode {

	}

	public static interface Contract {

		public String getName();

		public jkind.lustre.Type getLustreType();

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
		public jkind.lustre.Type getLustreType() {
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
		public jkind.lustre.Type getLustreType() {
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
		public jkind.lustre.Type getLustreType() {
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
		public jkind.lustre.Type getLustreType() {
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
		public jkind.lustre.Type getLustreType() {
			String lustreName = name.replace("::", "__").replace(".", "__");

			Map<String, jkind.lustre.Type> lustreFields = new HashMap<>();
			for (Entry<String, DataContract> entry : fields.entrySet()) {
				String key = entry.getKey();
				jkind.lustre.Type lt = entry.getValue().getLustreType();
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
		public jkind.lustre.Type getLustreType() {

			jkind.lustre.Type lustreBaseType = stemContract.getLustreType();
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

	}

	public static class NodeContract implements Contract {

		private final String name;
		public final Map<String, Channel> channels;
		public final Map<String, NodeContract> subNodes;
		public final List<Connection> connections;
		public final List<Spec> specList;

		/* reference to Xtext elm for gui update */
		public final NamedElement namedElement;

		public NodeContract(String name, Map<String, Channel> channels, Map<String, NodeContract> subNodes,
				List<Connection> connections, List<Spec> specList, NamedElement namedElement) {
			this.name = name;
			this.channels = new HashMap<>();
			this.channels.putAll(channels);
			this.subNodes = new HashMap<>();
			this.subNodes.putAll(subNodes);
			this.connections = new ArrayList<>();
			this.connections.addAll(connections);
			this.specList = specList;
			this.namedElement = namedElement;

		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public jkind.lustre.Type getLustreType() {
			String lustreName = name.replace("::", "__").replace(".", "__");

			Map<String, jkind.lustre.Type> lustreFields = new HashMap<>();
			for (Entry<String, Channel> entry : channels.entrySet()) {
				String key = entry.getKey();
				jkind.lustre.Type lt = entry.getValue().dataContract.getLustreType();
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

	}

	public static class Program {
		public final Contract main;
		public final Map<String, Contract> contractMap;
		public final Map<String, NodeGen> nodeGenMap;

		public Program(Contract main, Map<String, Contract> contractMap, Map<String, NodeGen> nodeGenMap) {
			this.main = main;
			this.contractMap = contractMap;
			this.nodeGenMap = nodeGenMap;
		}
	}

	// inline: ConstStatement

	// global contracts: Classifier, ArrayType, PrimType, EnumStatement, RecordDef, ComponentClassifier

	// global node generator: NodeDef, FnDef, LinearizationDef

	// local assertions: AssertionStatement, AssertEqualStatement,
	// local lemmas: LemmaStatement
	// local assumptions: AssumeStatement
	// local guarantees: GuaranteeStatement
	// local input channels: Feature, InputStatement
	// local output channels: Feature, BoolOutputStatement, OutputStatement
	// local subNodes: Subcomponent
	// local connections: ConnectedElement, ConnectionStatement

	// not implemented: LibraryFnDef





}