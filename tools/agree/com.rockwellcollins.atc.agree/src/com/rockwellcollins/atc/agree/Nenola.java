package com.rockwellcollins.atc.agree;


import static jkind.lustre.parsing.LustreParseUtil.equation;
import static jkind.lustre.parsing.LustreParseUtil.expr;
import static jkind.lustre.parsing.LustreParseUtil.to;

import java.math.BigDecimal;
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
import jkind.lustre.BoolExpr;
import jkind.lustre.EnumType;
import jkind.lustre.Equation;
import jkind.lustre.IfThenElseExpr;
import jkind.lustre.Location;
import jkind.lustre.NamedType;
import jkind.lustre.Node;
import jkind.lustre.NodeCallExpr;
import jkind.lustre.RealExpr;
import jkind.lustre.TypeDef;
import jkind.lustre.UnaryOp;
import jkind.lustre.VarDecl;
import jkind.lustre.builders.NodeBuilder;
import jkind.lustre.parsing.LustreParseUtil;

//Nenola = Nested Node Language
public class Nenola {

	public static class StaticState {

		public final Map<String, NodeContract> nodeContractMap;
		public final Map<String, DataContract> typeEnv;
		public final Map<String, DataContract> valueEnv;
		public final Map<String, List<DataContract>> nodeEnv;
		public final Map<String, Map<String, DataContract>> props;
		public final Optional<String> currentOp;

		public StaticState(Map<String, NodeContract> nodeContractMap,
				Map<String, DataContract> types, Map<String, DataContract> values,
				Map<String, List<DataContract>> funcs,
				Map<String, Map<String, DataContract>> props,
				Optional<String> currentOp) {
			this.nodeContractMap = new HashMap<>();
			this.nodeContractMap.putAll(nodeContractMap);

			this.typeEnv = new HashMap<>();
			this.typeEnv.putAll(types);

			this.valueEnv = new HashMap<>();
			this.valueEnv.putAll(values);

			this.nodeEnv = new HashMap<>();
			this.nodeEnv.putAll(funcs);

			this.props = new HashMap<>();
			this.props.putAll(props);

			this.currentOp = currentOp;

		}


		public StaticState newTypes(Map<String, DataContract> types) {
			return new StaticState(nodeContractMap, types, valueEnv, nodeEnv, props, currentOp);
		}

		public StaticState newValues(Map<String, DataContract> values) {
			return new StaticState(nodeContractMap, typeEnv, values, nodeEnv, props, currentOp);
		}

		public StaticState newFuncs(Map<String, List<DataContract>> funcs) {
			return new StaticState(nodeContractMap, typeEnv, valueEnv, funcs, props, currentOp);
		}

		public StaticState newProps(Map<String, Map<String, DataContract>> props) {
			return new StaticState(nodeContractMap, typeEnv, valueEnv, nodeEnv, props, currentOp);
		}

		public StaticState newCurrentOp(Optional<String> currentOp) {
			return new StaticState(nodeContractMap, typeEnv, valueEnv, nodeEnv, props, currentOp);
		}

	}


	public static enum BinRator {
		Equal, StreamCons, Implies, Equiv, Conj, Disj, NotEqual, LessThan, LessEq, GreatThan, GreatEq, Plus, Minus, Mult, Div, Mod, Pow;

		public BinaryOp toLustreRator() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public static enum UniRator {
		Neg, Not, Pre;

		public UnaryOp toLustreRator() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public static enum Tag {
		Clock, Insert, Remove, Count
	}

	public static interface Expr {

		DataContract inferDataContract(StaticState state);

		jkind.lustre.Expr toLustreExpr();

		jkind.lustre.Expr toLustreClockedExpr();

		List<jkind.lustre.VarDecl> toLustreClockedLocals(StaticState state);

		List<jkind.lustre.Equation> toLustreClockedEquations();

	}

	public static class TagExpr implements Expr {

		public final Expr target;
		public final Tag tag;

		public TagExpr(Expr target, Tag tag) {
			this.target = target;
			this.tag = tag;
		}

		@Override
		public DataContract inferDataContract(StaticState state) {

			switch (this.tag) {
			case Clock:
			case Insert:
			case Remove:
				return Nenola.Prim.BoolContract;
			case Count:
				return Nenola.Prim.IntContract;
			}

			throw new RuntimeException("Error: Tag.inferDataContract");

		}

		@Override
		public jkind.lustre.Expr toLustreExpr() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public jkind.lustre.Expr toLustreClockedExpr() {
			return this.toLustreExpr();
		}

		@Override
		public List<VarDecl> toLustreClockedLocals(StaticState state) {
			return new ArrayList<>();
		}

		@Override
		public List<Equation> toLustreClockedEquations() {
			return new ArrayList<>();
		}

	}


	public static class IdExpr implements Expr {
		public final String name;

		public IdExpr(String name) {
			this.name = name;
		}

		@Override
		public DataContract inferDataContract(StaticState state) {
			return state.valueEnv.get(this.name);
		}

		@Override
		public jkind.lustre.Expr toLustreExpr() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public jkind.lustre.Expr toLustreClockedExpr() {
			return this.toLustreExpr();
		}

		@Override
		public List<VarDecl> toLustreClockedLocals(StaticState state) {
			return new ArrayList<>();
		}

		@Override
		public List<Equation> toLustreClockedEquations() {
			return new ArrayList<>();
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
		public DataContract inferDataContract(StaticState state) {

			Contract targetContract = this.target.inferDataContract(state);
			if (targetContract instanceof RecordContract) {
				return ((RecordContract) targetContract).fields.get(this.selection);
			}

			throw new RuntimeException("Error: SelectionExpr.inferDataContract");
		}

		@Override
		public jkind.lustre.Expr toLustreExpr() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public jkind.lustre.Expr toLustreClockedExpr() {
			return this.toLustreExpr();
		}

		@Override
		public List<VarDecl> toLustreClockedLocals(StaticState state) {
			return new ArrayList<>();
		}

		@Override
		public List<Equation> toLustreClockedEquations() {
			return new ArrayList<>();
		}

	}

	public static class BinExpr implements Expr {

		public final Expr e1;
		public final BinRator rator;
		public final Expr e2;

		public BinExpr(Expr e1, BinRator rator, Expr e2) {
			this.e1 = e1;
			this.rator = rator;
			this.e2 = e2;
		}

		@Override
		public DataContract inferDataContract(StaticState state) {

			switch (this.rator) {
			case Equal :
				return this.e1.inferDataContract(state);
			case StreamCons:
				return this.e1.inferDataContract(state);
			case Implies:
				return Nenola.Prim.BoolContract;
			case Equiv:
				return Nenola.Prim.BoolContract;
			case Conj:
				return Nenola.Prim.BoolContract;
			case Disj:
				return Nenola.Prim.BoolContract;
			case NotEqual:
				return Nenola.Prim.BoolContract;
			case LessThan:
				return Nenola.Prim.BoolContract;
			case LessEq:
				return Nenola.Prim.BoolContract;
			case GreatThan:
				return Nenola.Prim.BoolContract;
			case GreatEq:
				return Nenola.Prim.BoolContract;
			case Plus:
				return this.e1.inferDataContract(state);
			case Minus:
				return this.e1.inferDataContract(state);
			case Mult:
				return this.e1.inferDataContract(state);
			case Div:
				return this.e1.inferDataContract(state);
			case Mod:
				return this.e1.inferDataContract(state);
			case Pow:
				return this.e1.inferDataContract(state);
			}

			throw new RuntimeException();

		}

		@Override
		public jkind.lustre.Expr toLustreExpr() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public jkind.lustre.Expr toLustreClockedExpr() {
			if (this.rator == BinRator.StreamCons) {
				return new jkind.lustre.IfThenElseExpr(new jkind.lustre.IdExpr(Lustre.initVarName), this.e1.toLustreClockedExpr(), this.e2.toLustreClockedExpr());
			} else {
				return new BinaryExpr(this.e1.toLustreClockedExpr(), rator.toLustreRator(), this.e2.toLustreClockedExpr());
			}
		}

		@Override
		public List<VarDecl> toLustreClockedLocals(StaticState state) {
			List<VarDecl> vars = new ArrayList<>();
			vars.addAll(this.e1.toLustreClockedLocals(state));
			vars.addAll(this.e2.toLustreClockedLocals(state));
			return vars;
		}

		@Override
		public List<Equation> toLustreClockedEquations() {
			List<Equation> eqs = new ArrayList<>();
			eqs.addAll(this.e1.toLustreClockedEquations());
			eqs.addAll(this.e2.toLustreClockedEquations());
			return eqs;
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
		public DataContract inferDataContract(StaticState state) {
			return this.trueBody.inferDataContract(state);
		}

		@Override
		public jkind.lustre.Expr toLustreExpr() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public jkind.lustre.Expr toLustreClockedExpr() {
			return this.toLustreExpr();
		}

		@Override
		public List<VarDecl> toLustreClockedLocals(StaticState state) {
			return new ArrayList<>();
		}

		@Override
		public List<Equation> toLustreClockedEquations() {
			return new ArrayList<>();
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
		public DataContract inferDataContract(StaticState state) {
			return this.init.inferDataContract(state);
		}

		@Override
		public jkind.lustre.Expr toLustreExpr() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public jkind.lustre.Expr toLustreClockedExpr() {
			return this.toLustreExpr();
		}

		@Override
		public List<VarDecl> toLustreClockedLocals(StaticState state) {
			return new ArrayList<>();
		}

		@Override
		public List<Equation> toLustreClockedEquations() {
			return new ArrayList<>();
		}

	}

	public static class LocalProperty implements Expr {
		public final String propName;

		public LocalProperty(String propName) {
			this.propName = propName;
		}


		@Override
		public DataContract inferDataContract(StaticState state) {
			return state.props.get(state.currentOp.get()).get(propName);
		}

		@Override
		public jkind.lustre.Expr toLustreExpr() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public jkind.lustre.Expr toLustreClockedExpr() {
			return this.toLustreExpr();
		}

		@Override
		public List<VarDecl> toLustreClockedLocals(StaticState state) {
			return new ArrayList<>();
		}

		@Override
		public List<Equation> toLustreClockedEquations() {
			return new ArrayList<>();
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
		public DataContract inferDataContract(StaticState state) {
			return state.props.get(nodeName).get(propName);
		}

		@Override
		public jkind.lustre.Expr toLustreExpr() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public jkind.lustre.Expr toLustreClockedExpr() {
			return this.toLustreExpr();
		}

		@Override
		public List<VarDecl> toLustreClockedLocals(StaticState state) {
			return new ArrayList<>();
		}

		@Override
		public List<Equation> toLustreClockedEquations() {
			return new ArrayList<>();
		}

	}

	public static class IntLit implements Expr {
		String val;

		public IntLit(String val) {
			this.val = val;
		}

		@Override
		public DataContract inferDataContract(StaticState state) {
			return Nenola.Prim.IntContract;
		}

		@Override
		public jkind.lustre.Expr toLustreExpr() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public jkind.lustre.Expr toLustreClockedExpr() {
			return this.toLustreExpr();
		}

		@Override
		public List<VarDecl> toLustreClockedLocals(StaticState state) {
			return new ArrayList<>();
		}

		@Override
		public List<Equation> toLustreClockedEquations() {
			return new ArrayList<>();
		}

	}

	public static class RealLit implements Expr {
		String val;

		public RealLit(String val) {
			this.val = val;
		}

		@Override
		public DataContract inferDataContract(StaticState state) {
			return Nenola.Prim.RealContract;
		}

		@Override
		public jkind.lustre.Expr toLustreExpr() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public jkind.lustre.Expr toLustreClockedExpr() {
			return this.toLustreExpr();
		}

		@Override
		public List<VarDecl> toLustreClockedLocals(StaticState state) {
			return new ArrayList<>();
		}

		@Override
		public List<Equation> toLustreClockedEquations() {
			return new ArrayList<>();
		}

	}

	public static class BoolLit implements Expr {
		boolean val;

		public BoolLit(boolean val) {
			this.val = val;
		}

		@Override
		public DataContract inferDataContract(StaticState state) {
			return Nenola.Prim.BoolContract;
		}

		@Override
		public jkind.lustre.Expr toLustreExpr() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public jkind.lustre.Expr toLustreClockedExpr() {
			return this.toLustreExpr();
		}

		@Override
		public List<VarDecl> toLustreClockedLocals(StaticState state) {
			return new ArrayList<>();
		}

		@Override
		public List<Equation> toLustreClockedEquations() {
			return new ArrayList<>();
		}

	}

	public static class Floor implements Expr {
		Expr arg;

		public Floor(Expr arg) {
			this.arg = arg;
		}

		@Override
		public DataContract inferDataContract(StaticState state) {
			return this.arg.inferDataContract(state);
		}

		@Override
		public jkind.lustre.Expr toLustreExpr() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public jkind.lustre.Expr toLustreClockedExpr() {
			return this.toLustreExpr();
		}

		@Override
		public List<VarDecl> toLustreClockedLocals(StaticState state) {
			return new ArrayList<>();
		}

		@Override
		public List<Equation> toLustreClockedEquations() {
			return new ArrayList<>();
		}

	}

	public static class RealCast implements Expr {
		Expr arg;

		public RealCast(Expr arg) {
			this.arg = arg;
		}

		@Override
		public DataContract inferDataContract(StaticState state) {
			return Nenola.Prim.RealContract;
		}

		@Override
		public jkind.lustre.Expr toLustreExpr() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public jkind.lustre.Expr toLustreClockedExpr() {
			return this.toLustreExpr();
		}

		@Override
		public List<VarDecl> toLustreClockedLocals(StaticState state) {
			return new ArrayList<>();
		}

		@Override
		public List<Equation> toLustreClockedEquations() {
			return new ArrayList<>();
		}

	}

	public static class Latch implements Expr {
		Expr arg;

		public Latch(Expr arg) {
			this.arg = arg;
		}

		@Override
		public DataContract inferDataContract(StaticState state) {
			return this.arg.inferDataContract(state);
		}

		@Override
		public jkind.lustre.Expr toLustreExpr() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public jkind.lustre.Expr toLustreClockedExpr() {
			return this.toLustreExpr();
		}

		@Override
		public List<VarDecl> toLustreClockedLocals(StaticState state) {
			return new ArrayList<>();
		}

		@Override
		public List<Equation> toLustreClockedEquations() {
			return new ArrayList<>();
		}

	}

	public static class Pre implements Expr {
		Expr arg;

		public Pre(Expr arg) {
			this.arg = arg;
		}

		@Override
		public DataContract inferDataContract(StaticState state) {
			return this.arg.inferDataContract(state);
		}

		@Override
		public jkind.lustre.Expr toLustreExpr() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public jkind.lustre.Expr toLustreClockedExpr() {
			return this.toLustreExpr();
		}

		@Override
		public List<VarDecl> toLustreClockedLocals(StaticState state) {
			return new ArrayList<>();
		}

		@Override
		public List<Equation> toLustreClockedEquations() {
			return new ArrayList<>();
		}

	}

	public static class Event implements Expr {
		String arg;

		public Event(String arg) {
			this.arg = arg;
		}

		@Override
		public DataContract inferDataContract(StaticState state) {
			return Nenola.Prim.BoolContract;
		}

		@Override
		public jkind.lustre.Expr toLustreExpr() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public jkind.lustre.Expr toLustreClockedExpr() {
			return this.toLustreExpr();
		}

		@Override
		public List<VarDecl> toLustreClockedLocals(StaticState state) {
			return new ArrayList<>();
		}

		@Override
		public List<Equation> toLustreClockedEquations() {
			return new ArrayList<>();
		}

	}

	public static class TimeOf implements Expr {
		String arg;

		public TimeOf(String arg) {
			this.arg = arg;
		}

		@Override
		public DataContract inferDataContract(StaticState state) {
			return Nenola.Prim.RealContract;
		}

		@Override
		public jkind.lustre.Expr toLustreExpr() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public jkind.lustre.Expr toLustreClockedExpr() {
			return this.toLustreExpr();
		}

		@Override
		public List<VarDecl> toLustreClockedLocals(StaticState state) {
			return new ArrayList<>();
		}

		@Override
		public List<Equation> toLustreClockedEquations() {
			return new ArrayList<>();
		}

	}

	public static class TimeRise implements Expr {
		String arg;

		public TimeRise(String arg) {
			this.arg = arg;
		}

		@Override
		public DataContract inferDataContract(StaticState state) {
			return Nenola.Prim.BoolContract;
		}

		@Override
		public jkind.lustre.Expr toLustreExpr() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public jkind.lustre.Expr toLustreClockedExpr() {
			return this.toLustreExpr();
		}

		@Override
		public List<VarDecl> toLustreClockedLocals(StaticState state) {
			return new ArrayList<>();
		}

		@Override
		public List<Equation> toLustreClockedEquations() {
			return new ArrayList<>();
		}

	}

	public static class TimeFall implements Expr {
		String arg;

		public TimeFall(String arg) {
			this.arg = arg;
		}

		@Override
		public DataContract inferDataContract(StaticState state) {
			return Nenola.Prim.RealContract;
		}

		@Override
		public jkind.lustre.Expr toLustreExpr() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public jkind.lustre.Expr toLustreClockedExpr() {
			return this.toLustreExpr();
		}

		@Override
		public List<VarDecl> toLustreClockedLocals(StaticState state) {
			return new ArrayList<>();
		}

		@Override
		public List<Equation> toLustreClockedEquations() {
			return new ArrayList<>();
		}

	}

	public static class Time implements Expr {

		public Time() {

		}

		@Override
		public DataContract inferDataContract(StaticState state) {
			return Nenola.Prim.RealContract;
		}

		@Override
		public jkind.lustre.Expr toLustreExpr() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public jkind.lustre.Expr toLustreClockedExpr() {
			return this.toLustreExpr();
		}

		@Override
		public List<VarDecl> toLustreClockedLocals(StaticState state) {
			return new ArrayList<>();
		}

		@Override
		public List<Equation> toLustreClockedEquations() {
			return new ArrayList<>();
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
		public DataContract inferDataContract(StaticState state) {
			return state.typeEnv.get(contractName);
		}

		@Override
		public jkind.lustre.Expr toLustreExpr() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public jkind.lustre.Expr toLustreClockedExpr() {
			return this.toLustreExpr();
		}

		@Override
		public List<VarDecl> toLustreClockedLocals(StaticState state) {
			return new ArrayList<>();
		}

		@Override
		public List<Equation> toLustreClockedEquations() {
			return new ArrayList<>();
		}

	}

	public static class ArrayLit implements Expr {
		public final List<Expr> elements;

		public ArrayLit(List<Expr> elements) {
			this.elements = elements;
		}

		@Override
		public DataContract inferDataContract(StaticState state) {
			return new Nenola.ArrayContract("", elements.get(0).inferDataContract(state), elements.size());
		}

		@Override
		public jkind.lustre.Expr toLustreExpr() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public jkind.lustre.Expr toLustreClockedExpr() {
			return this.toLustreExpr();
		}

		@Override
		public List<VarDecl> toLustreClockedLocals(StaticState state) {
			return new ArrayList<>();
		}

		@Override
		public List<Equation> toLustreClockedEquations() {
			return new ArrayList<>();
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
		public DataContract inferDataContract(StaticState state) {
			return new Nenola.ArrayContract("", elements.get(0).inferDataContract(state), elements.size());
		}

		@Override
		public jkind.lustre.Expr toLustreExpr() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public jkind.lustre.Expr toLustreClockedExpr() {
			return this.toLustreExpr();
		}

		@Override
		public List<VarDecl> toLustreClockedLocals(StaticState state) {
			return new ArrayList<>();
		}

		@Override
		public List<Equation> toLustreClockedEquations() {
			return new ArrayList<>();
		}

	}

	public static class RecordLit implements Expr {
		public final String contractName;
		public final Map<String, Expr> fields;

		public RecordLit(String contractName, Map<String, Expr> fields) {
			this.contractName = contractName;
			this.fields = fields;
		}

		@Override
		public DataContract inferDataContract(StaticState state) {
			return state.typeEnv.get(contractName);
		}

		@Override
		public jkind.lustre.Expr toLustreExpr() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public jkind.lustre.Expr toLustreClockedExpr() {
			return this.toLustreExpr();
		}

		@Override
		public List<VarDecl> toLustreClockedLocals(StaticState state) {
			return new ArrayList<>();
		}

		@Override
		public List<Equation> toLustreClockedEquations() {
			return new ArrayList<>();
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
		public DataContract inferDataContract(StaticState state) {
			return record.inferDataContract(state);
		}

		@Override
		public jkind.lustre.Expr toLustreExpr() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public jkind.lustre.Expr toLustreClockedExpr() {
			return this.toLustreExpr();
		}

		@Override
		public List<VarDecl> toLustreClockedLocals(StaticState state) {
			return new ArrayList<>();
		}

		@Override
		public List<Equation> toLustreClockedEquations() {
			return new ArrayList<>();
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
		public DataContract inferDataContract(StaticState state) {
			return state.nodeEnv.get(fnName).get(0);
		}

		@Override
		public jkind.lustre.Expr toLustreExpr() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public jkind.lustre.Expr toLustreClockedExpr() {
			return this.toLustreExpr();
		}

		@Override
		public List<VarDecl> toLustreClockedLocals(StaticState state) {
			return new ArrayList<>();
		}

		@Override
		public List<Equation> toLustreClockedEquations() {
			return new ArrayList<>();
		}

	}

	public static class UnaryExpr implements Expr {
		public final UniRator rator;
		public final Expr rand;

		public UnaryExpr(UniRator rator, Expr rand) {
			this.rator = rator;
			this.rand = rand;
		}

		@Override
		public DataContract inferDataContract(StaticState state) {
			return rand.inferDataContract(state);
		}

		@Override
		public jkind.lustre.Expr toLustreExpr() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public jkind.lustre.Expr toLustreClockedExpr() {

			if (this.rator == UniRator.Pre) {

				String clockedId = Lustre.statVarPrefix + this.hashCode();
				jkind.lustre.IdExpr stateVarId = new jkind.lustre.IdExpr(clockedId);
				return stateVarId;
			} else {
				return new jkind.lustre.UnaryExpr(UnaryOp.PRE, this.rand.toLustreClockedExpr());
			}
		}

		@Override
		public List<VarDecl> toLustreClockedLocals(StaticState state) {

			if (this.rator == UniRator.Pre) {

				List<VarDecl> vars = new ArrayList<>();

				String clockedId = Lustre.statVarPrefix + this.hashCode();
				jkind.lustre.IdExpr stateVarId = new jkind.lustre.IdExpr(clockedId);
				DataContract dc = this.inferDataContract(state);
				vars.add(new VarDecl(stateVarId.id, dc.toLustreType()));

				return vars;
			} else {
				return this.rand.toLustreClockedLocals(state);

			}

		}


		@Override
		public List<Equation> toLustreClockedEquations() {

			if (this.rator == UniRator.Pre) {

				List<Equation> eqs = new ArrayList<>();

				jkind.lustre.Expr preExpr = new jkind.lustre.UnaryExpr(UnaryOp.PRE, this.rand.toLustreClockedExpr());

				String clockedId = Lustre.statVarPrefix + this.hashCode();
				jkind.lustre.IdExpr stateVarId = new jkind.lustre.IdExpr(clockedId);

				jkind.lustre.Expr stateVarExpr = expr("if clk then stateVarExpr else (pre stateVar)",
						to("stateVar", stateVarId), to("stateVarExpr", preExpr), to("clk", Lustre.clockVarName));

				eqs.add(new Equation(stateVarId, stateVarExpr));
				return eqs;

			} else {
				return this.rand.toLustreClockedEquations();

			}

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
		public DataContract inferDataContract(StaticState state) {
			ArrayContract ac = (ArrayContract) array.inferDataContract(state);
			return ac.stemContract;
		}

		@Override
		public jkind.lustre.Expr toLustreExpr() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public jkind.lustre.Expr toLustreClockedExpr() {
			return this.toLustreExpr();
		}

		@Override
		public List<VarDecl> toLustreClockedLocals(StaticState state) {
			return new ArrayList<>();
		}

		@Override
		public List<Equation> toLustreClockedEquations() {
			return new ArrayList<>();
		}

	}

	public static class IndicesExpr implements Expr {
		public final Expr array;

		public IndicesExpr(Expr array) {
			this.array = array;
		}

		@Override
		public DataContract inferDataContract(StaticState state) {
			ArrayContract ac = (ArrayContract) array.inferDataContract(state);
			return new Nenola.ArrayContract("", Nenola.Prim.IntContract, ac.size);
		}

		@Override
		public jkind.lustre.Expr toLustreExpr() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public jkind.lustre.Expr toLustreClockedExpr() {
			return this.toLustreExpr();
		}

		@Override
		public List<VarDecl> toLustreClockedLocals(StaticState state) {
			return new ArrayList<>();
		}

		@Override
		public List<Equation> toLustreClockedEquations() {
			return new ArrayList<>();
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
		public DataContract inferDataContract(StaticState state) {
			return Nenola.Prim.BoolContract;
		}

		@Override
		public jkind.lustre.Expr toLustreExpr() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public jkind.lustre.Expr toLustreClockedExpr() {
			return this.toLustreExpr();
		}

		@Override
		public List<VarDecl> toLustreClockedLocals(StaticState state) {
			return new ArrayList<>();
		}

		@Override
		public List<Equation> toLustreClockedEquations() {
			return new ArrayList<>();
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
		public DataContract inferDataContract(StaticState state) {
			return Nenola.Prim.BoolContract;
		}

		@Override
		public jkind.lustre.Expr toLustreExpr() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public jkind.lustre.Expr toLustreClockedExpr() {
			return this.toLustreExpr();
		}

		@Override
		public List<VarDecl> toLustreClockedLocals(StaticState state) {
			return new ArrayList<>();
		}

		@Override
		public List<Equation> toLustreClockedEquations() {
			return new ArrayList<>();
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
		public DataContract inferDataContract(StaticState state) {
			Map<String, DataContract> newValues = new HashMap<>();
			newValues.putAll(state.valueEnv);
			ArrayContract ac = (ArrayContract) this.array.inferDataContract(state);
			DataContract stemType = ac.stemContract;
			newValues.put(binding, stemType);
			StaticState newState = state.newValues(newValues);
			return this.body.inferDataContract(newState);
		}

		@Override
		public jkind.lustre.Expr toLustreExpr() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public jkind.lustre.Expr toLustreClockedExpr() {
			return this.toLustreExpr();
		}

		@Override
		public List<VarDecl> toLustreClockedLocals(StaticState state) {
			return new ArrayList<>();
		}

		@Override
		public List<Equation> toLustreClockedEquations() {
			return new ArrayList<>();
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
		public DataContract inferDataContract(StaticState state) {
			return this.initial.inferDataContract(state);
		}

		@Override
		public jkind.lustre.Expr toLustreExpr() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public jkind.lustre.Expr toLustreClockedExpr() {
			return this.toLustreExpr();
		}

		@Override
		public List<VarDecl> toLustreClockedLocals(StaticState state) {
			return new ArrayList<>();
		}

		@Override
		public List<Equation> toLustreClockedEquations() {
			return new ArrayList<>();
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
		public DataContract inferDataContract(StaticState state) {
			return this.initial.inferDataContract(state);
		}

		@Override
		public jkind.lustre.Expr toLustreExpr() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public jkind.lustre.Expr toLustreClockedExpr() {
			return this.toLustreExpr();
		}

		@Override
		public List<VarDecl> toLustreClockedLocals(StaticState state) {
			return new ArrayList<>();
		}

		@Override
		public List<Equation> toLustreClockedEquations() {
			return new ArrayList<>();
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

	public static class PatternProp implements Prop {
		public final Pattern pattern;

		public PatternProp(Pattern pattern) {
			this.pattern = pattern;
		}
	}


	public static interface Pattern {
		public jkind.lustre.Expr toLustreExprProperty();

		public jkind.lustre.Expr toLustreExprConstraint();

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

		public List<jkind.lustre.Equation> toLustrePatternEquationPropertyList();

		public List<jkind.lustre.Equation> toLustrePatternEquationConstraintList();

		public List<jkind.lustre.VarDecl> toLustrePatternTimeEventPropertyList();

		public List<jkind.lustre.VarDecl> toLustrePatternTimeEventConstraintList();

	}


	public static class AlwaysPattern implements Pattern {

		public final Expr expr;

		public AlwaysPattern(Expr expr) {
			this.expr = expr;
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
			return new ArrayList<>();
		}

		@Override
		public List<jkind.lustre.Expr> toLustrePatternAssertConstraintList() {
			return new ArrayList<>();
		}

		@Override
		public List<VarDecl> toLustrePatternChanInPropertyList() {
			return new ArrayList<>();
		}

		@Override
		public List<VarDecl> toLustrePatternChanInConstraintList() {
			return new ArrayList<>();
		}

		@Override
		public List<VarDecl> toLustrePatternChanOutPropertyList() {
			return new ArrayList<>();
		}

		@Override
		public List<VarDecl> toLustrePatternChanOutConstraintList() {
			return new ArrayList<>();
		}

		@Override
		public List<VarDecl> toLustrePatternChanBiPropertyList() {
			return new ArrayList<>();
		}

		@Override
		public List<VarDecl> toLustrePatternChanBiConstraintList() {
			return new ArrayList<>();
		}

		@Override
		public List<Equation> toLustrePatternEquationPropertyList() {
			return new ArrayList<>();
		}

		@Override
		public List<Equation> toLustrePatternEquationConstraintList() {
			return new ArrayList<>();
		}

		@Override
		public jkind.lustre.Expr toLustreExprProperty() {
			return this.expr.toLustreExpr();
		}

		@Override
		public jkind.lustre.Expr toLustreExprConstraint() {
			return this.expr.toLustreExpr();
		}

		@Override
		public List<VarDecl> toLustrePatternTimeEventPropertyList() {
			return new ArrayList<>();
		}

		@Override
		public List<VarDecl> toLustrePatternTimeEventConstraintList() {
			return new ArrayList<>();
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
		public final Interval causeInterval;
		public final Expr effectEvent;
		public final boolean exclusive;
		public final Interval effectInterval;

		private final WheneverOccursPattern refinementPattern;

		public WhenHoldsPattern(Expr causeCondition, Interval causeInterval, Expr effectEvent, boolean exclusive,
				Interval effectInterval) {
			this.causeCondition = causeCondition;
			this.causeInterval = causeInterval;
			this.effectEvent = effectEvent;
			this.exclusive = exclusive;
			this.effectInterval = effectInterval;

			{
				String causeConditionString = ((jkind.lustre.IdExpr) causeCondition.toLustreExpr()).id;
				Expr causeEvent = new IdExpr(Lustre.getCauseHeldVar(causeConditionString).id);
				this.refinementPattern = new WheneverOccursPattern(causeEvent, effectEvent, exclusive, effectInterval);
			}
		}

		@Override
		public Map<String, jkind.lustre.Expr> toLustrePatternPropertyMap() {
			return refinementPattern.toLustrePatternPropertyMap();
		}

		@Override
		public Map<String, jkind.lustre.Expr> toLustrePatternConstraintMap() {
			return refinementPattern.toLustrePatternConstraintMap();
		}

		private List<jkind.lustre.Expr> toLustreCauseAssertList() {

			List<jkind.lustre.Expr> assertList = new ArrayList<>();

			jkind.lustre.IdExpr lustreCauseCondition = (jkind.lustre.IdExpr) this.causeCondition.toLustreExpr();

			VarDecl causeRiseTimeVar = Lustre.getTimeRiseVar(lustreCauseCondition.id);
			VarDecl causeFallTimeVar = Lustre.getTimeFallVar(lustreCauseCondition.id);
			VarDecl causeHeldTimeoutVar = Lustre.getCauseConditionTimeOutVar(lustreCauseCondition.id);

			jkind.lustre.IdExpr causeFallTimeId = new jkind.lustre.IdExpr(causeFallTimeVar.id);
			jkind.lustre.IdExpr causeRiseTimeId = new jkind.lustre.IdExpr(causeRiseTimeVar.id);
			jkind.lustre.IdExpr causeHeldTimeoutId = new jkind.lustre.IdExpr(causeHeldTimeoutVar.id);

			jkind.lustre.Expr posRise = new jkind.lustre.BinaryExpr(causeRiseTimeId, BinaryOp.GREATER,
					new RealExpr(BigDecimal.valueOf(-1)));
			jkind.lustre.Expr gtFall = new jkind.lustre.BinaryExpr(causeRiseTimeId, BinaryOp.GREATER, causeFallTimeId);
			jkind.lustre.Expr cond = new jkind.lustre.BinaryExpr(posRise, BinaryOp.AND, gtFall);

			jkind.lustre.Expr heldTime = new BinaryExpr(causeRiseTimeId, BinaryOp.PLUS,
					this.causeInterval.high.toLustreExpr());
			jkind.lustre.Expr ifExpr = new IfThenElseExpr(cond, heldTime, new RealExpr(BigDecimal.valueOf(-1)));
			assertList.add(new BinaryExpr(causeHeldTimeoutId, BinaryOp.EQUAL, ifExpr));

			{

				jkind.lustre.Expr rise = new NodeCallExpr("__Rise", new jkind.lustre.IdExpr(lustreCauseCondition.id));
				jkind.lustre.Expr timeVarExpr = expr("timeRise = (if rise then time else (-1.0 -> pre timeRise))",
						to("timeRise", causeRiseTimeVar.id), to("rise", rise),
						to("time", Lustre.timeExpr));
				assertList.add(timeVarExpr);

				jkind.lustre.Expr lemmaExpr = expr("timeRise <= time and timeRise >= -1.0",
						to("timeRise", Lustre.timeExpr), to("time", Lustre.timeExpr));

				assertList.add(lemmaExpr);

			}

			{

				jkind.lustre.Expr Fall = new NodeCallExpr("__Fall", new jkind.lustre.IdExpr(lustreCauseCondition.id));
				jkind.lustre.Expr timeVarExpr = expr("timeFall = (if Fall then time else (-1.0 -> pre timeFall))",
						to("timeFall", causeFallTimeVar.id), to("Fall", Fall),
						to("time", Lustre.timeExpr));
				assertList.add(timeVarExpr);

				jkind.lustre.Expr lemmaExpr = expr("timeFall <= time and timeFall >= -1.0",
						to("timeFall", causeFallTimeVar.id), to("time", Lustre.timeExpr));

				// add this assertion to help with proofs (it should always be true)
				assertList.add(lemmaExpr);
			}
			return assertList;

		}

		@Override
		public List<jkind.lustre.Expr> toLustrePatternAssertPropertyList() {
			List<jkind.lustre.Expr> asserts = new ArrayList<>();
			asserts.addAll(toLustreCauseAssertList());
			asserts.addAll(refinementPattern.toLustrePatternAssertPropertyList());
			return asserts;
		}

		@Override
		public List<jkind.lustre.Expr> toLustrePatternAssertConstraintList() {
			List<jkind.lustre.Expr> asserts = new ArrayList<>();
			asserts.addAll(toLustreCauseAssertList());
			asserts.addAll(refinementPattern.toLustrePatternAssertConstraintList());
			return asserts;
		}

		private List<VarDecl> toLustreCauseChanInList() {
			List<VarDecl> vars = new ArrayList<>();
			jkind.lustre.IdExpr lustreCauseCondition = (jkind.lustre.IdExpr) this.causeCondition.toLustreExpr();
			VarDecl causeHeldTimeoutVar = Lustre.getCauseConditionTimeOutVar(lustreCauseCondition.id);
			vars.add(causeHeldTimeoutVar);
			return vars;
		}

		@Override
		public List<VarDecl> toLustrePatternChanInPropertyList() {
			List<VarDecl> vars = new ArrayList<>();
			vars.addAll(this.toLustreCauseChanInList());
			vars.addAll(refinementPattern.toLustrePatternChanInPropertyList());
			return vars;
		}

		@Override
		public List<VarDecl> toLustrePatternChanInConstraintList() {
			List<VarDecl> vars = new ArrayList<>();
			vars.addAll(this.toLustreCauseChanInList());
			vars.addAll(refinementPattern.toLustrePatternChanInConstraintList());
			return vars;
		}

		private List<VarDecl> toLustreCauseChanOutList() {
			List<VarDecl> vars = new ArrayList<>();

			jkind.lustre.IdExpr lustreCauseCondition = (jkind.lustre.IdExpr) this.causeCondition.toLustreExpr();
			{
				VarDecl causeRiseTimeVar = Lustre.getTimeRiseVar(lustreCauseCondition.id);
				vars.add(causeRiseTimeVar);
			}
			{
				VarDecl causeRiseFallVar = Lustre.getTimeFallVar(lustreCauseCondition.id);
				vars.add(causeRiseFallVar);
			}
			return vars;
		}

		@Override
		public List<VarDecl> toLustrePatternChanOutPropertyList() {
			List<VarDecl> vars = new ArrayList<>();

			vars.addAll(this.toLustreCauseChanOutList());
			vars.addAll(refinementPattern.toLustrePatternChanOutPropertyList());

			return vars;
		}

		@Override
		public List<VarDecl> toLustrePatternChanOutConstraintList() {
			List<VarDecl> vars = new ArrayList<>();
			vars.addAll(this.toLustreCauseChanOutList());
			vars.addAll(refinementPattern.toLustrePatternChanOutConstraintList());
			return vars;
		}

		private List<VarDecl> toLustreCauseChanBiList() {
			List<VarDecl> vars = new ArrayList<>();
			jkind.lustre.IdExpr lustreCauseCondition = (jkind.lustre.IdExpr) this.causeCondition.toLustreExpr();
			vars.add(Lustre.getCauseHeldVar(lustreCauseCondition.id));
			return vars;
		}

		@Override
		public List<VarDecl> toLustrePatternChanBiPropertyList() {
			List<VarDecl> vars = new ArrayList<>();
			vars.addAll(this.toLustreCauseChanBiList());
			vars.addAll(refinementPattern.toLustrePatternChanBiPropertyList());
			return vars;
		}

		@Override
		public List<VarDecl> toLustrePatternChanBiConstraintList() {
			List<VarDecl> vars = new ArrayList<>();
			vars.addAll(this.toLustreCauseChanBiList());
			vars.addAll(refinementPattern.toLustrePatternChanBiConstraintList());
			return vars;
		}

		private List<jkind.lustre.Equation> toLustreCauseEquationList() {
			List<jkind.lustre.Equation> equations = new ArrayList<>();
			jkind.lustre.IdExpr lustreCauseCondition = (jkind.lustre.IdExpr) this.causeCondition.toLustreExpr();

			VarDecl causeHeldVar = Lustre.getCauseHeldVar(lustreCauseCondition.id);
			VarDecl causeHeldTimeoutVar = Lustre.getCauseConditionTimeOutVar(lustreCauseCondition.id);
			jkind.lustre.IdExpr causeHeldId = new jkind.lustre.IdExpr(causeHeldVar.id);
			jkind.lustre.IdExpr causeHeldTimeoutId = new jkind.lustre.IdExpr(causeHeldTimeoutVar.id);

			jkind.lustre.Expr causeHeldExpr = new BinaryExpr(Lustre.timeExpr, BinaryOp.EQUAL,
					causeHeldTimeoutId);
			jkind.lustre.Equation equation = new jkind.lustre.Equation(causeHeldId, causeHeldExpr);
			equations.add(equation);

			return equations;
		}

		@Override
		public List<Equation> toLustrePatternEquationPropertyList() {
			List<Equation> equations = new ArrayList<>();
			equations.addAll(this.toLustreCauseEquationList());
			equations.addAll(refinementPattern.toLustrePatternEquationPropertyList());
			return equations;
		}

		@Override
		public List<Equation> toLustrePatternEquationConstraintList() {
			List<Equation> equations = new ArrayList<>();
			equations.addAll(this.toLustreCauseEquationList());
			equations.addAll(refinementPattern.toLustrePatternEquationConstraintList());
			return equations;
		}

		@Override
		public jkind.lustre.Expr toLustreExprProperty() {
			return refinementPattern.toLustreExprProperty();
		}

		@Override
		public jkind.lustre.Expr toLustreExprConstraint() {
			return refinementPattern.toLustreExprConstraint();
		}

		@Override
		public List<VarDecl> toLustrePatternTimeEventPropertyList() {
			List<VarDecl> vars = new ArrayList<>();
			VarDecl causeFallTimeVar = Lustre
					.getTimeFallVar(((jkind.lustre.IdExpr) this.causeCondition.toLustreExpr()).id);
			vars.add(causeFallTimeVar);
			vars.addAll(refinementPattern.toLustrePatternTimeEventPropertyList());
			return vars;
		}

		@Override
		public List<VarDecl> toLustrePatternTimeEventConstraintList() {
			List<VarDecl> vars = new ArrayList<>();
			VarDecl causeFallTimeVar = Lustre
					.getTimeFallVar(((jkind.lustre.IdExpr) this.causeCondition.toLustreExpr()).id);
			vars.addAll(refinementPattern.toLustrePatternTimeEventConstraintList());
			vars.add(causeFallTimeVar);
			return vars;
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

		private String patternIndex = this.hashCode() + "";

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
			List<jkind.lustre.Expr> asserts = new ArrayList<>();
			VarDecl recordVar = Lustre.getRecordVar(patternIndex);
			asserts.addAll(Lustre.getTimeOfAsserts(recordVar.id));

			jkind.lustre.Expr expr = expr("record => cause", to("record", recordVar),
					to("cause", ((jkind.lustre.IdExpr) this.causeEvent.toLustreExpr()).id));
			asserts.add(expr);

			return asserts;
		}

		@Override
		public List<jkind.lustre.Expr> toLustrePatternAssertConstraintList() {

			List<jkind.lustre.Expr> asserts = new ArrayList<>();

			VarDecl timeCauseVar = Lustre.getTimeOfVar(((jkind.lustre.IdExpr) this.causeEvent.toLustreExpr()).id);
			VarDecl timeoutVar = Lustre.getTimeoutVar(patternIndex);

			jkind.lustre.Expr timeoutExpr = expr("timeout = if timeCause >= 0.0 then (timeCause + l) else -1.0",
					to("timeout", timeoutVar), to("timeCause", timeCauseVar),
					to("l", this.interval.low.toLustreExpr()));

			asserts.add(timeoutExpr);

			return asserts;

		}

		@Override
		public List<VarDecl> toLustrePatternChanInPropertyList() {
			List<VarDecl> vars = new ArrayList<>();
			vars.add(Lustre.getRecordVar(patternIndex));
			return vars;
		}

		@Override
		public List<VarDecl> toLustrePatternChanInConstraintList() {
			return new ArrayList<>();
		}

		@Override
		public List<VarDecl> toLustrePatternChanOutPropertyList() {
			List<VarDecl> vars = new ArrayList<>();
			VarDecl recordVar = Lustre.getRecordVar(patternIndex);
			VarDecl timeCause = Lustre.getTimeOfVar(recordVar.id);
			vars.add(timeCause);
			return vars;
		}

		@Override
		public List<VarDecl> toLustrePatternChanOutConstraintList() {
			List<VarDecl> vars = new ArrayList<>();
			VarDecl timeoutVar = Lustre.getTimeoutVar(patternIndex);
			vars.add(timeoutVar);
			return vars;
		}

		@Override
		public List<VarDecl> toLustrePatternChanBiPropertyList() {
			List<VarDecl> vars = new ArrayList<>();
			VarDecl windowVar = Lustre.getWindowVar(patternIndex);
			vars.add(windowVar);
			return vars;
		}

		@Override
		public List<VarDecl> toLustrePatternChanBiConstraintList() {
			return new ArrayList<>();
		}

		@Override
		public List<Equation> toLustrePatternEquationPropertyList() {
			List<Equation> equations = new ArrayList<>();

			VarDecl recordVar = Lustre.getRecordVar(patternIndex);
			VarDecl windowVar = Lustre.getWindowVar(patternIndex);
			VarDecl tRecord = Lustre.getTimeOfVar(recordVar.id);

			jkind.lustre.BinaryOp left = this.interval.lowOpen ? BinaryOp.LESS : BinaryOp.LESSEQUAL;
			jkind.lustre.BinaryOp right = this.interval.highOpen ? BinaryOp.LESS : BinaryOp.LESSEQUAL;

			Equation eq = equation(
					"in_window = (trecord <> -1.0) and " + "(l + trecord " + left + " time) and (time " + right
							+ " h + trecord);",
					to("in_window", windowVar), to("trecord", tRecord), to("time", Lustre.timeExpr),
					to("l", this.interval.low.toLustreExpr()), to("h", this.interval.high.toLustreExpr()));

			equations.add(eq);
			return equations;
		}

		@Override
		public List<Equation> toLustrePatternEquationConstraintList() {
			return new ArrayList<>();
		}

		@Override
		public jkind.lustre.Expr toLustreExprProperty() {
			VarDecl windowVar = Lustre.getWindowVar(patternIndex);
			return expr("in_window => effect", to("in_window", windowVar),
					to("effect", this.effectCondition.toLustreExpr()));
		}

		@Override
		public jkind.lustre.Expr toLustreExprConstraint() {
			VarDecl timeCauseVar = Lustre.getTimeOfVar(((jkind.lustre.IdExpr) this.causeEvent.toLustreExpr()).id);

			jkind.lustre.Expr intervalLeft = expr("timeCause + l", to("timeCause", timeCauseVar),
					to("l", this.interval.low.toLustreExpr()));
			jkind.lustre.Expr intervalRight = expr("timeCause + h", to("timeCause", timeCauseVar),
					to("h", this.interval.high.toLustreExpr()));

			jkind.lustre.BinaryOp left = this.interval.lowOpen ? BinaryOp.LESS : BinaryOp.LESSEQUAL;
			jkind.lustre.BinaryOp right = this.interval.highOpen ? BinaryOp.LESS : BinaryOp.LESSEQUAL;

			intervalLeft = new BinaryExpr(intervalLeft, left, Lustre.timeExpr);
			intervalRight = new BinaryExpr(Lustre.timeExpr, right, intervalRight);

			jkind.lustre.Expr inInterval = new BinaryExpr(intervalLeft, BinaryOp.AND, intervalRight);

			String constrString;
			if (this.exclusive) {
				constrString = "if timeCause > -1.0 and inInterval then effectTrue else not effectTrue";
			} else {
				constrString = "timeCause > -1.0 => inInterval => effectTrue";
			}

			jkind.lustre.Expr expr = expr(constrString, to("timeCause", timeCauseVar), to("inInterval", inInterval),
					to("effectTrue", this.effectCondition.toLustreExpr()));

			return expr;
		}

		@Override
		public List<VarDecl> toLustrePatternTimeEventPropertyList() {
			return new ArrayList<>();
		}

		@Override
		public List<VarDecl> toLustrePatternTimeEventConstraintList() {
			List<VarDecl> vars = new ArrayList<>();
			VarDecl timeoutVar = Lustre.getTimeoutVar(patternIndex);
			vars.add(timeoutVar);
			return vars;
		}

	}


	public static class WheneverOccursPattern implements Pattern {
		public final Expr causeEvent;
		public final Expr effectEvent;
		public final boolean exclusive;
		public final Interval effectInterval;

		public WheneverOccursPattern(Expr causeEvent, Expr effectEvent, boolean exclusive, Interval interval) {
			this.causeEvent = causeEvent;
			this.effectEvent = effectEvent;
			this.exclusive = exclusive;
			this.effectInterval = interval;
		}

		public String patternIndex = this.hashCode() + "";

		private jkind.lustre.Expr toTimeRangeConstraint() {
			VarDecl effectTimeRangeVar = Lustre.getEffectTimeRangeVar(patternIndex);
			jkind.lustre.IdExpr timeRangeId = new jkind.lustre.IdExpr(effectTimeRangeVar.id);

			jkind.lustre.Expr occurs = new BinaryExpr(timeRangeId, BinaryOp.MINUS, Lustre.timeExpr);
			jkind.lustre.BinaryOp left = this.effectInterval.lowOpen ? BinaryOp.LESS : BinaryOp.LESSEQUAL;
			jkind.lustre.BinaryOp right = this.effectInterval.highOpen ? BinaryOp.LESS : BinaryOp.LESSEQUAL;

			jkind.lustre.Expr lower = new BinaryExpr(this.effectInterval.low.toLustreExpr(), left, occurs);
			jkind.lustre.Expr higher = new BinaryExpr(occurs, right, this.effectInterval.high.toLustreExpr());
			return new BinaryExpr(lower, BinaryOp.AND, higher);
		}


		@Override
		public Map<String, jkind.lustre.Expr> toLustrePatternPropertyMap() {
			String patternIndex = Integer.toString(this.hashCode());

			jkind.lustre.IdExpr lustreCause = (jkind.lustre.IdExpr) causeEvent.toLustreExpr();
			jkind.lustre.IdExpr lustreEffect = (jkind.lustre.IdExpr) effectEvent.toLustreExpr();

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
					LustreParseUtil.to("time", Lustre.timeExpr),
					LustreParseUtil.to("low", this.effectInterval.low.toLustreExpr()),
					LustreParseUtil.to("high", this.effectInterval.high.toLustreExpr()),
					LustreParseUtil.to("run", runVar));

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

			List<jkind.lustre.Expr> assertions = new ArrayList<>();

			jkind.lustre.IdExpr causeEventExpr = ((jkind.lustre.IdExpr) this.causeEvent.toLustreExpr());

			VarDecl timerVar = Lustre.getTimerVar(patternIndex);
			VarDecl recordVar = Lustre.getRecordVar(patternIndex);

			jkind.lustre.IdExpr timerId = new jkind.lustre.IdExpr(timerVar.id);
			jkind.lustre.IdExpr recordId = new jkind.lustre.IdExpr(recordVar.id);

			{
				jkind.lustre.Expr expr = new jkind.lustre.BinaryExpr(timerId, BinaryOp.GREATEREQUAL,
						new RealExpr(BigDecimal.ZERO));
				assertions.add(expr);
			}

			{
				jkind.lustre.Expr causeExpr;

				if (this.effectInterval.lowOpen) {
					causeExpr = new jkind.lustre.IdExpr(causeEventExpr.id);
				} else {
					jkind.lustre.Expr eAndLZero = new jkind.lustre.BinaryExpr(this.effectInterval.low.toLustreExpr(),
							BinaryOp.EQUAL, new jkind.lustre.RealExpr(BigDecimal.ZERO));
					eAndLZero = new BinaryExpr(this.effectEvent.toLustreExpr(), BinaryOp.AND, eAndLZero);
					jkind.lustre.Expr notEAndLZero = new jkind.lustre.UnaryExpr(UnaryOp.NOT, eAndLZero);
					causeExpr = new BinaryExpr(new jkind.lustre.IdExpr(causeEventExpr.id), BinaryOp.AND, notEAndLZero);
				}
				jkind.lustre.Expr recordExpr = new BinaryExpr(recordId, BinaryOp.IMPLIES, causeExpr);
				assertions.add(recordExpr);
			}

			return assertions;
		}

		@Override
		public List<jkind.lustre.Expr> toLustrePatternAssertConstraintList() {

			List<jkind.lustre.Expr> assertions = new ArrayList<>();

			jkind.lustre.IdExpr causeEventExpr = ((jkind.lustre.IdExpr) this.causeEvent.toLustreExpr());

			VarDecl effectTimeRangeVar = Lustre.getEffectTimeRangeVar(patternIndex);
			jkind.lustre.IdExpr effectTimeRangeId = new jkind.lustre.IdExpr(effectTimeRangeVar.id);
			//
			VarDecl timeEffectVar = Lustre.getTimeWillVar(patternIndex);

			jkind.lustre.IdExpr timeEffectId = new jkind.lustre.IdExpr(timeEffectVar.id);

			jkind.lustre.Expr effectTimeRangeConstraint = this.toTimeRangeConstraint();
			assertions.add(effectTimeRangeConstraint);
			// make a constraint that triggers when the event WILL happen

			jkind.lustre.Expr expr = expr(
					"timeEffect = if causeId then effectTimeRangeId else (-1.0 -> pre timeEffect)",
					to("timeEffect", timeEffectId), to("causeId", causeEventExpr.id),
					to("effectTimeRangeId", effectTimeRangeId));

			assertions.add(expr);

			// a lemma that may be helpful

			jkind.lustre.Expr lemma1 = expr("timeEffect <= time + intHigh", to("timeEffect", timeEffectVar),
					to("time", Lustre.timeExpr),
					to("intHigh", this.effectInterval.high.toLustreExpr()));

			assertions.add(lemma1);

			jkind.lustre.Expr lemma2 = expr(
					"timeWill <= causeTime + high and (causeTime >= 0.0 => causeTime + low <= timeWill)",
					to("timeWill", timeEffectVar), to("causeTime", Lustre.getTimeOfVar(causeEventExpr.id)),
					to("high", this.effectInterval.high.toLustreExpr()),
					to("low", this.effectInterval.low.toLustreExpr()));

			assertions.add(lemma2);
			assertions.addAll(Lustre.getTimeOfAsserts(causeEventExpr.id));

			jkind.lustre.IdExpr lustreEffect = (jkind.lustre.IdExpr) this.effectEvent.toLustreExpr();

			jkind.lustre.Expr lemma3 = expr("timeWill <= time => timeWill <= timeEffect", to("timeWill", timeEffectVar),
					to("timeEffect", Lustre.getTimeOfVar(lustreEffect.id)));

			assertions.add(lemma3);
			assertions.addAll(Lustre.getTimeOfAsserts(lustreEffect.id));

			return assertions;

		}

		@Override
		public List<VarDecl> toLustrePatternChanInPropertyList() {
			List<VarDecl> vars = new ArrayList<>();
			vars.add(Lustre.getRecordVar(patternIndex));
			return vars;
		}

		@Override
		public List<VarDecl> toLustrePatternChanInConstraintList() {
			List<VarDecl> vars = new ArrayList<>();
			vars.add(Lustre.getEffectTimeRangeVar(patternIndex));
			return vars;
		}

		@Override
		public List<VarDecl> toLustrePatternChanOutPropertyList() {
			List<VarDecl> vars = new ArrayList<>();


			jkind.lustre.IdExpr causeEventExpr = ((jkind.lustre.IdExpr) this.causeEvent.toLustreExpr());
			vars.add(Lustre.getTimeOfVar(causeEventExpr.id));

			jkind.lustre.IdExpr effectEventExpr = ((jkind.lustre.IdExpr) this.effectEvent.toLustreExpr());
			vars.add(Lustre.getTimeFallVar(effectEventExpr.id));

			return vars;
		}

		@Override
		public List<VarDecl> toLustrePatternChanOutConstraintList() {

			List<VarDecl> vars = new ArrayList<>();
			vars.add(Lustre.getTimeWillVar(patternIndex));

			jkind.lustre.IdExpr causeEventExpr = ((jkind.lustre.IdExpr) this.causeEvent.toLustreExpr());

			vars.add(Lustre.getTimeOfVar(causeEventExpr.id));

			jkind.lustre.IdExpr effectEventExpr = ((jkind.lustre.IdExpr) this.effectEvent.toLustreExpr());
			vars.add(Lustre.getTimeFallVar(effectEventExpr.id));

			return vars;
		}

		@Override
		public List<VarDecl> toLustrePatternChanBiPropertyList() {
			List<VarDecl> vars = new ArrayList<>();
			vars.add(Lustre.getTimerVar(patternIndex));
			vars.add(Lustre.getRunningVar(patternIndex));
			return vars;
		}

		@Override
		public List<VarDecl> toLustrePatternChanBiConstraintList() {
			return new ArrayList<>();
		}

		@Override
		public List<Equation> toLustrePatternEquationPropertyList() {

			List<Equation> equations = new ArrayList<>();

			jkind.lustre.VarDecl timerVar = Lustre.getTimerVar(patternIndex);
			jkind.lustre.VarDecl runVar = Lustre.getRunningVar(patternIndex);
			jkind.lustre.VarDecl recordVar = Lustre.getRecordVar(patternIndex);
			jkind.lustre.IdExpr timerId = new jkind.lustre.IdExpr(timerVar.id);
			jkind.lustre.IdExpr runId = new jkind.lustre.IdExpr(runVar.id);
			jkind.lustre.IdExpr recordId = new jkind.lustre.IdExpr(recordVar.id);

			jkind.lustre.Expr preRun = new jkind.lustre.UnaryExpr(UnaryOp.PRE, runId);

			{
				jkind.lustre.Expr if2 = new IfThenElseExpr(recordId, new BoolExpr(true), preRun);
				jkind.lustre.BinaryOp left = this.effectInterval.lowOpen ? BinaryOp.LESS : BinaryOp.LESSEQUAL;
				jkind.lustre.BinaryOp right = this.effectInterval.highOpen ? BinaryOp.LESS : BinaryOp.LESSEQUAL;
				jkind.lustre.Expr timerLow = new BinaryExpr(this.effectInterval.low.toLustreExpr(), left, timerId);
				jkind.lustre.Expr timerHigh = new BinaryExpr(timerId, right, this.effectInterval.high.toLustreExpr());
				jkind.lustre.Expr cond1 = new BinaryExpr(preRun, BinaryOp.AND, this.effectEvent.toLustreExpr());
				cond1 = new BinaryExpr(cond1, BinaryOp.AND, timerLow);
				cond1 = new BinaryExpr(cond1, BinaryOp.AND, timerHigh);
				jkind.lustre.Expr if1 = new IfThenElseExpr(cond1, new BoolExpr(false), if2);
				jkind.lustre.Expr runExpr = new BinaryExpr(recordId, BinaryOp.ARROW, if1);

				jkind.lustre.Equation equa = new jkind.lustre.Equation(runId, runExpr);
				equations.add(equa);
			}

			// timer = (0 -> if pre(run) then pre(timer) + (t - pre(t)) else 0)
			{
				jkind.lustre.Expr preTimer = new jkind.lustre.UnaryExpr(UnaryOp.PRE, timerId);
				jkind.lustre.Expr preT = new jkind.lustre.UnaryExpr(UnaryOp.PRE, Lustre.timeExpr);
				jkind.lustre.Expr elapsed = new BinaryExpr(Lustre.timeExpr, BinaryOp.MINUS, preT);
				jkind.lustre.Expr total = new BinaryExpr(preTimer, BinaryOp.PLUS, elapsed);
				jkind.lustre.Expr timerExpr = new IfThenElseExpr(preRun, total, new RealExpr(BigDecimal.ZERO));
				timerExpr = new BinaryExpr(new RealExpr(BigDecimal.ZERO), BinaryOp.ARROW, timerExpr);
				jkind.lustre.Equation equa = new jkind.lustre.Equation(timerId, timerExpr);
				equations.add(equa);
			}

			return equations;

		}

		@Override
		public List<Equation> toLustrePatternEquationConstraintList() {
			return new ArrayList<>();
		}

		@Override
		public jkind.lustre.Expr toLustreExprProperty() {
			VarDecl timerVar = Lustre.getTimerVar(patternIndex);
			jkind.lustre.IdExpr timerId = new jkind.lustre.IdExpr(timerVar.id);

			// timer <= h
			jkind.lustre.BinaryOp right = this.effectInterval.highOpen ? BinaryOp.LESS : BinaryOp.LESSEQUAL;
			return new jkind.lustre.BinaryExpr(timerId, right, this.effectInterval.high.toLustreExpr());
		}

		@Override
		public jkind.lustre.Expr toLustreExprConstraint() {
			VarDecl timeEffectVar = Lustre.getTimeWillVar(patternIndex);
			jkind.lustre.IdExpr timeEffectId = new jkind.lustre.IdExpr(timeEffectVar.id);
			jkind.lustre.Expr timeEqualsEffectTime = new BinaryExpr(Lustre.timeExpr, BinaryOp.EQUAL,
					timeEffectId);
			// if the event is exclusive it only occurs when scheduled
			jkind.lustre.BinaryOp effectOp = this.exclusive ? BinaryOp.EQUAL : BinaryOp.IMPLIES;
			jkind.lustre.Expr impliesEffect = new BinaryExpr(timeEqualsEffectTime, effectOp,
					this.effectEvent.toLustreExpr());
			return impliesEffect;
		}

		@Override
		public List<VarDecl> toLustrePatternTimeEventPropertyList() {
			return new ArrayList<>();
		}

		@Override
		public List<VarDecl> toLustrePatternTimeEventConstraintList() {
			List<VarDecl> vars = new ArrayList<>();
			vars.add(Lustre.getTimeWillVar(patternIndex));
			return vars;
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

		public String patternIndex = this.hashCode() + "";

		@Override
		public Map<String, jkind.lustre.Expr> toLustrePatternPropertyMap() {
			return new HashMap<>();
		}

		@Override
		public Map<String, jkind.lustre.Expr> toLustrePatternConstraintMap() {

			Map<String, jkind.lustre.Expr> localMap = new HashMap<>();

			String patternIndex = this.hashCode() + "";

			jkind.lustre.VarDecl periodVar = Lustre.getPeriodVar(patternIndex);
			jkind.lustre.VarDecl timeoutVar = Lustre.getTimerVar(patternIndex);

			jkind.lustre.IdExpr timeoutId = new jkind.lustre.IdExpr(timeoutVar.id);
			jkind.lustre.VarDecl timeofEvent = Lustre
					.getTimeOfVar(((jkind.lustre.IdExpr) this.event.toLustreExpr()).id);

			jkind.lustre.Expr jitter = this.jitterOp.isPresent() ? this.jitterOp.get().toLustreExpr() : null;

			jkind.lustre.Expr lemma1 = expr(
					"(timeOfEvent >= 0.0 and timeOfEvent <> time => timeout - timeOfEvent >= p - j) and "
							+ "(true -> (period <> pre(period) => period - pre(period) <= p + j)) and "
							+ "(timeOfEvent >= 0.0 => timeout - timeOfEvent <= p + j)",
					to("timeOfEvent", timeofEvent), to("time", Lustre.timeExpr),
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
			return new ArrayList<>();
		}

		@Override
		public List<jkind.lustre.Expr> toLustrePatternAssertConstraintList() {

			List<jkind.lustre.Expr> asserts = new ArrayList<>();

			VarDecl jitterVar = Lustre.getJitterVar(patternIndex);
			VarDecl periodVar = Lustre.getPeriodVar(patternIndex);
			VarDecl timeoutVar = Lustre.getTimeoutVar(patternIndex);

			jkind.lustre.IdExpr jitterId = new jkind.lustre.IdExpr(jitterVar.id);
			jkind.lustre.IdExpr periodId = new jkind.lustre.IdExpr(periodVar.id);
			jkind.lustre.IdExpr timeoutId = new jkind.lustre.IdExpr(timeoutVar.id);
			//

//			// -j <= jitter <= j
			jkind.lustre.Expr lustreJitter = this.jitterOp.isPresent() ? this.jitterOp.get().toLustreExpr() : null;
			jkind.lustre.Expr jitterLow = new jkind.lustre.BinaryExpr(
					new jkind.lustre.UnaryExpr(UnaryOp.NEGATIVE, lustreJitter), BinaryOp.LESSEQUAL,
					jitterId);
			jkind.lustre.Expr jitterHigh = new jkind.lustre.BinaryExpr(jitterId, BinaryOp.LESSEQUAL, lustreJitter);
			asserts.add(new BinaryExpr(jitterLow, BinaryOp.AND, jitterHigh));

			jkind.lustre.Expr expr = expr(
					"(0.0 <= period) and (period < p) -> " + "(period = (pre period) + (if pre(e) then p else 0.0))",
					to("period", periodVar), to("p", this.period.toLustreExpr()), to("e", this.event.toLustreExpr()));

			asserts.add(expr);

			// helper assertion (should be true)
			jkind.lustre.Expr lemma = expr("period - time < p - j and period >= time", to("period", periodVar),
					to("p", this.period.toLustreExpr()), to("time", Lustre.timeExpr),
					to("j", lustreJitter));

			asserts.add(lemma);
			asserts.addAll(Lustre.getTimeOfAsserts(((jkind.lustre.IdExpr) this.event.toLustreExpr()).id));

			// timeout = pnext + jitter
			jkind.lustre.Expr timeoutExpr = new BinaryExpr(periodId, BinaryOp.PLUS, jitterId);
			timeoutExpr = new BinaryExpr(timeoutId, BinaryOp.EQUAL, timeoutExpr);
			asserts.add(timeoutExpr);

			return asserts;
		}

		@Override
		public List<VarDecl> toLustrePatternChanInPropertyList() {
			return new ArrayList<>();
		}

		@Override
		public List<VarDecl> toLustrePatternChanInConstraintList() {
			return new ArrayList<>();
		}

		@Override
		public List<VarDecl> toLustrePatternChanOutPropertyList() {
			return new ArrayList<>();
		}

		@Override
		public List<VarDecl> toLustrePatternChanOutConstraintList() {
			List<VarDecl> vars = new ArrayList<>();

			VarDecl jitterVar = Lustre.getJitterVar(patternIndex);
			vars.add(jitterVar);
			VarDecl periodVar = Lustre.getPeriodVar(patternIndex);
			vars.add(periodVar);
			VarDecl timeoutVar = Lustre.getTimeoutVar(patternIndex);
			vars.add(timeoutVar);

			VarDecl var = Lustre.getTimeOfVar(((jkind.lustre.IdExpr) this.event.toLustreExpr()).id);
			vars.add(var);
			return vars;
		}

		@Override
		public List<VarDecl> toLustrePatternChanBiPropertyList() {
			List<VarDecl> vars = new ArrayList<>();
			vars.add(Lustre.getPeriodVar(patternIndex));
			return vars;
		}

		@Override
		public List<VarDecl> toLustrePatternChanBiConstraintList() {
			return new ArrayList<>();
		}

		@Override
		public List<Equation> toLustrePatternEquationPropertyList() {

			List<Equation> eqs = new ArrayList<>();

			VarDecl periodVar = Lustre.getPeriodVar(patternIndex);

			jkind.lustre.Equation eq = equation(
					"period = if event then (if time <= P then time  else (0.0 -> pre period)) + P else (P -> pre period);",
					to("event", this.event.toLustreExpr()), to("period", periodVar),
					to("P", this.period.toLustreExpr()));

			eqs.add(eq);

			return eqs;
		}

		@Override
		public List<Equation> toLustrePatternEquationConstraintList() {
			return new ArrayList<>();
		}

		@Override
		public jkind.lustre.Expr toLustreExprProperty() {
			VarDecl periodVar = Lustre.getPeriodVar(patternIndex);

			jkind.lustre.Expr lustreJitter = this.jitterOp.isPresent() ? this.jitterOp.get().toLustreExpr() : null;

			jkind.lustre.Expr prop = expr(
					"true -> (time >= P + j => event => (pre period) - j <= time and time <= (pre period) + j)",
					to("time", Lustre.timeExpr), to("period", periodVar),
					to("P", this.period.toLustreExpr()), to("j", lustreJitter), to("event", this.event.toLustreExpr()));
			return prop;
		}

		@Override
		public jkind.lustre.Expr toLustreExprConstraint() {
			VarDecl timeoutVar = Lustre.getTimeoutVar(patternIndex);

			jkind.lustre.IdExpr timeoutId = new jkind.lustre.IdExpr(timeoutVar.id);

			// event = (t = timeout)
			jkind.lustre.Expr eventExpr = new BinaryExpr(Lustre.timeExpr, BinaryOp.EQUAL, timeoutId);
			eventExpr = new BinaryExpr(this.event.toLustreExpr(), BinaryOp.EQUAL, eventExpr);

			return eventExpr;
		}

		@Override
		public List<VarDecl> toLustrePatternTimeEventPropertyList() {
			return new ArrayList<>();
		}

		@Override
		public List<VarDecl> toLustrePatternTimeEventConstraintList() {
			List<VarDecl> vars = new ArrayList<>();
			VarDecl timeoutVar = Lustre.getTimeoutVar(patternIndex);
			vars.add(timeoutVar);
			return vars;
		}
	}

	public static class SporadicPattern implements Pattern {
		public final Expr event;
		public final Expr iat;
		public final Optional<Expr> jitterOp;

		public final String patternIndex = this.hashCode() + "";

		public SporadicPattern(Expr event, Expr iat, Optional<Expr> jitterOp) {
			this.event = event;
			this.iat = iat;
			this.jitterOp = jitterOp;
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
			List<jkind.lustre.Expr> asserts = new ArrayList<>();
			asserts.addAll(Lustre.getTimeOfAsserts(((jkind.lustre.IdExpr) this.event.toLustreExpr()).id));
			return asserts;
		}

		@Override
		public List<jkind.lustre.Expr> toLustrePatternAssertConstraintList() {

			List<jkind.lustre.Expr> asserts = new ArrayList<>();

			VarDecl jitterVar = Lustre.getJitterVar(patternIndex);
			VarDecl periodVar = Lustre.getPeriodVar(patternIndex);
			VarDecl timeoutVar = Lustre.getTimeoutVar(patternIndex);

			jkind.lustre.IdExpr jitterId = new jkind.lustre.IdExpr(jitterVar.id);
			jkind.lustre.IdExpr periodId = new jkind.lustre.IdExpr(periodVar.id);
			jkind.lustre.IdExpr timeoutId = new jkind.lustre.IdExpr(timeoutVar.id);

			jkind.lustre.Expr lustreJitter = this.jitterOp.isPresent() ? this.jitterOp.get().toLustreExpr() : null;

			// -j <= jitter <= j
			jkind.lustre.Expr jitterLow = new jkind.lustre.BinaryExpr(
					new jkind.lustre.UnaryExpr(UnaryOp.NEGATIVE, lustreJitter), BinaryOp.LESSEQUAL, jitterId);
			jkind.lustre.Expr jitterHigh = new BinaryExpr(jitterId, BinaryOp.LESSEQUAL, lustreJitter);
			asserts.add(new BinaryExpr(jitterLow, BinaryOp.AND, jitterHigh));

			// pnext >= 0 -> if pre ((pnext + jitter) = t) then pnext >= p +
			// pre(pnext) else pre(pnext)

			jkind.lustre.Expr prePNext = new jkind.lustre.UnaryExpr(UnaryOp.PRE, periodId);
			jkind.lustre.Expr pNextInit = new BinaryExpr(periodId, BinaryOp.GREATEREQUAL,
					new RealExpr(BigDecimal.ZERO));
			jkind.lustre.Expr pNextCond = new BinaryExpr(periodId, BinaryOp.PLUS, jitterId);
			pNextCond = new BinaryExpr(pNextCond, BinaryOp.EQUAL, Lustre.timeExpr);
			pNextCond = new jkind.lustre.UnaryExpr(UnaryOp.PRE, pNextCond);
			jkind.lustre.Expr pNextThen = new BinaryExpr(this.iat.toLustreExpr(), BinaryOp.PLUS, prePNext);
			pNextThen = new BinaryExpr(periodId, BinaryOp.GREATEREQUAL, pNextThen);
			jkind.lustre.Expr pNextHold = new BinaryExpr(periodId, BinaryOp.EQUAL, prePNext);
			jkind.lustre.Expr pNextIf = new IfThenElseExpr(pNextCond, pNextThen, pNextHold);
			jkind.lustre.Expr pNext = new BinaryExpr(pNextInit, BinaryOp.ARROW, pNextIf);

			asserts.add(pNext);

			// timeout = pnext + jitter
			jkind.lustre.Expr timeoutExpr = new BinaryExpr(periodId, BinaryOp.PLUS, jitterId);
			timeoutExpr = new BinaryExpr(timeoutId, BinaryOp.EQUAL, timeoutExpr);
			asserts.add(timeoutExpr);

			return asserts;
		}

		@Override
		public List<VarDecl> toLustrePatternChanInPropertyList() {
			return new ArrayList<>();
		}

		@Override
		public List<VarDecl> toLustrePatternChanInConstraintList() {
			return new ArrayList<>();
		}

		@Override
		public List<VarDecl> toLustrePatternChanOutPropertyList() {
			List<VarDecl> vars = new ArrayList<>();
			vars.add(Lustre.getTimeOfVar(((jkind.lustre.IdExpr) this.event.toLustreExpr()).id));
			return vars;
		}

		@Override
		public List<VarDecl> toLustrePatternChanOutConstraintList() {
			List<VarDecl> vars = new ArrayList<>();

			VarDecl jitterVar = Lustre.getJitterVar(patternIndex);
			vars.add(jitterVar);
			VarDecl periodVar = Lustre.getPeriodVar(patternIndex);
			vars.add(periodVar);
			VarDecl timeoutVar = Lustre.getTimeoutVar(patternIndex);
			vars.add(timeoutVar);

			VarDecl var = Lustre.getTimeOfVar(((jkind.lustre.IdExpr) this.event.toLustreExpr()).id);
			vars.add(var);
			return vars;
		}

		@Override
		public List<VarDecl> toLustrePatternChanBiPropertyList() {
			return new ArrayList<>();
		}

		@Override
		public List<VarDecl> toLustrePatternChanBiConstraintList() {
			return new ArrayList<>();
		}

		@Override
		public List<Equation> toLustrePatternEquationPropertyList() {
			return new ArrayList<>();
		}

		@Override
		public List<Equation> toLustrePatternEquationConstraintList() {
			return new ArrayList<>();
		}

		@Override
		public jkind.lustre.Expr toLustreExprProperty() {
			VarDecl timeofEvent = Lustre.getTimeOfVar(((jkind.lustre.IdExpr) this.event.toLustreExpr()).id);

			jkind.lustre.Expr propExpr = expr(
					"(true -> (not ((pre laste) = -1.0) => event => time - (pre laste) >= period))",
					to("laste", timeofEvent), to("event", this.event.toLustreExpr()),
					to("time", Lustre.timeExpr), to("period", this.iat.toLustreExpr()));

			return propExpr;
		}

		@Override
		public jkind.lustre.Expr toLustreExprConstraint() {
			VarDecl timeoutVar = Lustre.getTimeoutVar(patternIndex);
			jkind.lustre.IdExpr timeoutId = new jkind.lustre.IdExpr(timeoutVar.id);
			// event = (t = timeout)
			jkind.lustre.Expr eventExpr = new BinaryExpr(Lustre.timeExpr, BinaryOp.EQUAL, timeoutId);
			eventExpr = new BinaryExpr(this.event.toLustreExpr(), BinaryOp.EQUAL, eventExpr);

			return eventExpr;
		}

		@Override
		public List<VarDecl> toLustrePatternTimeEventPropertyList() {
			return new ArrayList<>();
		}

		@Override
		public List<VarDecl> toLustrePatternTimeEventConstraintList() {
			List<VarDecl> vars = new ArrayList<>();
			VarDecl timeoutVar = Lustre.getTimeoutVar(patternIndex);
			vars.add(timeoutVar);
			return vars;
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

	public static interface PropVal {
	}

	public static class StringPropVal implements PropVal {
		public final String val;

		public StringPropVal(String val) {
			this.val = val;
		}
	}

	public static class IntPropVal implements PropVal {
		public final long val;

		public IntPropVal(long val) {
			this.val = val;
		}
	}

	public static class RealPropVal implements PropVal {
		public final double val;

		public RealPropVal(double val) {
			this.val = val;
		}
	}

	public static class NodeContract implements Contract {

		private final String name;
		public final Map<String, Channel> channels;
		public final Map<String, NodeContract> subNodes;
		public final Map<String, NodeGen> nodeGenMap;
		public final List<Connection> connections;
		public final List<Spec> specList;
		public final Optional<TimingMode> timingMode;
		public final boolean isImpl;

		/* reference to Xtext elm for gui update */
		public final NamedElement namedElement;


		public NodeContract(String name, Map<String, Channel> channels, Map<String, NodeContract> subNodes,
				Map<String, NodeGen> nodeGenMap,
				Map<String, PropVal> propAssocs, List<Connection> connections, List<Spec> specList,
				Optional<TimingMode> timingMode, boolean isImpl,
				NamedElement namedElement) {
			this.name = name;
			this.channels = new HashMap<>();
			this.channels.putAll(channels);
			this.subNodes = new HashMap<>();
			this.subNodes.putAll(subNodes);

			this.nodeGenMap = new HashMap<>();
			this.nodeGenMap.putAll(nodeGenMap);

			this.connections = new ArrayList<>();
			this.connections.addAll(connections);
			this.specList = specList;
			this.timingMode = timingMode;
			this.isImpl = isImpl;

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

		private List<Node> toLustreClockedNodesFromNodeGenList(StaticState state) {
			Map<String, DataContract> values = new HashMap<>();
			values.putAll(state.valueEnv);
			values.putAll(this.getValueTypes());
			Map<String, List<DataContract>> funcs = new HashMap<>();
			funcs.putAll(state.nodeEnv);
			funcs.putAll(getNodeTypes(this.nodeGenMap));
			StaticState newState = state.newValues(values).newFuncs(funcs).newCurrentOp(Optional.of(this.name));

			List<jkind.lustre.Node> lustreNodes = new ArrayList<>();
			for (NodeGen nodeGen : this.nodeGenMap.values()) {

				jkind.lustre.Node lustreNode = nodeGen.toLustreClockedNode(newState);
				lustreNodes.add(lustreNode);

			}

			return lustreNodes;
		}

		private Map<String, DataContract> getValueTypes() {

			for (Channel c : this.channels.values()) {
				// TODO Auto-generated method stub
			}
			return null;
		}


		private jkind.lustre.Node toLustreSubNode(boolean isMonolithic) {
			List<jkind.lustre.VarDecl> inputs = new ArrayList<>();
			List<jkind.lustre.VarDecl> locals = new ArrayList<>();
			List<jkind.lustre.Equation> equations = new ArrayList<>();
			List<jkind.lustre.Expr> assertions = this.toLustreAssertList(isMonolithic);
			List<String> ivcs = new ArrayList<>();

			for (Entry<String, jkind.lustre.Expr> entry : this.toLustreAssumeMap(isMonolithic).entrySet()) {
				String inputName = entry.getKey();
				jkind.lustre.Expr expr = entry.getValue();
				inputs.add(new VarDecl(inputName, NamedType.BOOL));
				assertions.add(new BinaryExpr(new jkind.lustre.IdExpr(inputName), BinaryOp.EQUAL, expr));
			}

			for (Entry<String, jkind.lustre.Expr> entry : this.toLustreLemmaMap(isMonolithic).entrySet()) {
				String inputName = entry.getKey();
				jkind.lustre.Expr expr = entry.getValue();
				inputs.add(new VarDecl(inputName, NamedType.BOOL));
				assertions.add(new BinaryExpr(new jkind.lustre.IdExpr(inputName), BinaryOp.EQUAL, expr));
			}

			jkind.lustre.Expr guarConjExpr = new jkind.lustre.BoolExpr(true);
			for (Entry<String, jkind.lustre.Expr> entry : this.toLustreGuaranteeMap(isMonolithic).entrySet()) {
				String inputName = entry.getKey();
				jkind.lustre.Expr expr = entry.getValue();
				locals.add(new VarDecl(inputName, NamedType.BOOL));
				jkind.lustre.IdExpr guarId = new jkind.lustre.IdExpr(inputName);
				equations.add(new Equation(guarId, expr));
				ivcs.add(inputName);
				guarConjExpr = Lustre.makeANDExpr(guarId, guarConjExpr);
			}

			for (Entry<String, jkind.lustre.Expr> entry : this.toLustreLemmaMap(isMonolithic).entrySet()) {
				jkind.lustre.Expr expr = entry.getValue();
				guarConjExpr = Lustre.makeANDExpr(expr, guarConjExpr);
			}

			jkind.lustre.IdExpr assumHist = new jkind.lustre.IdExpr("__ASSUME__HIST");
			inputs.add(new VarDecl(assumHist.id, NamedType.BOOL));


			jkind.lustre.Expr assertExpr = new BinaryExpr(assumHist, BinaryOp.IMPLIES, guarConjExpr);
			for (jkind.lustre.Expr expr : assertions) {
				assertExpr = Lustre.makeANDExpr(expr, assertExpr);
			}

			for (Entry<String, jkind.lustre.Expr> entry : this.toLustrePatternPropMap(isMonolithic).entrySet()) {
				String patternVarName = entry.getKey();
				inputs.add(new VarDecl(patternVarName, NamedType.BOOL));
				jkind.lustre.Expr expr = new jkind.lustre.BinaryExpr(new jkind.lustre.IdExpr(patternVarName),
						BinaryOp.EQUAL, entry.getValue());
				assertExpr = Lustre.makeANDExpr(expr, assertExpr);
			}

			inputs.addAll(this.toLustreChanInList(isMonolithic));

			inputs.addAll(this.toLustreChanOutList(isMonolithic));

			inputs.addAll(this.toLustreChanBiList(isMonolithic));

			equations.addAll(this.toLustreEquationList(isMonolithic));

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
			return node;
		}

//		public Node toLustreNode(boolean isMain, boolean isMonolithic) {
//
//			if (this.lustreNodeCache.containsKey(isMonolithic)) {
//				return lustreNodeCache.get(isMonolithic);
//			}
//
//			Node node = null;
//			if (isMain && this.isImpl) {
//				node = this.toLustreMainNode(isMonolithic);
//			} else {
//				node = this.toLustreSubNode(isMonolithic);
//			}
//
//			lustreNodeCache.put(isMonolithic, node);
//			return node;
//
//		}

		private Node toLustreMainNode(boolean isMonolithic) {
			List<jkind.lustre.Expr> assertions = new ArrayList<>();
			List<VarDecl> locals = new ArrayList<>();
			List<VarDecl> inputs = new ArrayList<>();
			List<Equation> equations = new ArrayList<>();
			List<String> properties = new ArrayList<>();
			List<String> ivcs = new ArrayList<>();


			for (Entry<String, jkind.lustre.Expr> entry : this.toLustreAssumeMap(isMonolithic).entrySet()) {

				String inputName = entry.getKey();
				jkind.lustre.Expr assumeExpr = entry.getValue();
				locals.add(new VarDecl(inputName, NamedType.BOOL));
				jkind.lustre.IdExpr idExpr = new jkind.lustre.IdExpr(inputName);
				equations.add(new Equation(idExpr, assumeExpr));
				assertions.add(idExpr);
				ivcs.add(inputName);
			}

			for (jkind.lustre.Expr assertion : this.toLustreAssertList(isMonolithic)) {
				assertions.add(assertion);
			}

			// add assumption and monolithic lemmas first (helps with proving)
			for (VarDecl var : this.toLustreChanOutList(isMonolithic)) {
				inputs.add(var);
			}

			for (String propStr : this.toLustreStringPropertyList(isMonolithic)) {
				properties.add(propStr);
			}

			// add property that all assumption history is true
			jkind.lustre.Expr assumeConj = new BoolExpr(true);
			for (Entry<String,NodeContract> entry : this.subNodes.entrySet()) {

				String id = entry.getKey() + Lustre.assumeHistVar;
				assumeConj = new jkind.lustre.BinaryExpr(new jkind.lustre.IdExpr(id), jkind.lustre.BinaryOp.AND,
						assumeConj);
			}

			VarDecl assumeHistVar = Lustre.assumeHistVar;
			locals.add(assumeHistVar);
			equations.add(new Equation(new jkind.lustre.IdExpr(assumeHistVar.id), assumeConj));
			properties.add(assumeHistVar.id);


			for (Entry<String, jkind.lustre.Expr> entry : this.toLustrePatternPropMap(isMonolithic).entrySet()) {
				String name = entry.getKey();
				jkind.lustre.Expr expr = entry.getValue();
				locals.add(new VarDecl(name, NamedType.BOOL));
				equations.add(new Equation(new jkind.lustre.IdExpr(name), expr));
				properties.add(name);
			}

			for (Entry<String, jkind.lustre.Expr> entry : this.toLustreLemmaMap(isMonolithic).entrySet()) {
				String name = entry.getKey();
				jkind.lustre.Expr expr = entry.getValue();
				locals.add(new VarDecl(name, NamedType.BOOL));
				equations.add(new Equation(new jkind.lustre.IdExpr(name), expr));
				properties.add(name);
			}

			for (Entry<String, jkind.lustre.Expr> entry : this.toLustreGuaranteeMap(isMonolithic).entrySet()) {
				String name = entry.getKey();
				jkind.lustre.Expr expr = entry.getValue();
				locals.add(new VarDecl(name, NamedType.BOOL));
				equations.add(new Equation(new jkind.lustre.IdExpr(name), expr));
				properties.add(name);
			}

			for (VarDecl var : this.toLustreChanInList(isMonolithic)) {
				inputs.add(var);
			}
			for (VarDecl var : this.toLustreChanBiList(isMonolithic)) {
				locals.add(var);
			}



			equations.addAll(this.toLustreEquationList(isMonolithic));
			assertions.add(Lustre.getTimeConstraint(this.toEventTimeVarList(isMonolithic)));

			NodeBuilder builder = new NodeBuilder("main");
			builder.addInputs(inputs);
			builder.addLocals(locals);
			builder.addEquations(equations);
			builder.addProperties(properties);
			builder.addAssertions(assertions);
			builder.addIvcs(ivcs);

			Node main = builder.build();
			return main;
		}

		private List<String> toLustreStringPropertyList(boolean isMonolithic) {
			List<String> strs = new ArrayList<>();

			for (Spec spec : this.specList) {

				if (spec.prop instanceof PatternProp) {
					Pattern pattern = ((PatternProp) spec.prop).pattern;
					for (VarDecl var : pattern.toLustrePatternTimeEventPropertyList()) {
						strs.add(var.id);
					}
				}

			}

			return strs;
		}

		private List<jkind.lustre.VarDecl> toEventTimeVarList(boolean isMonolithic) {

			List<jkind.lustre.VarDecl> vars = new ArrayList<>();

			for (Spec spec : this.specList) {

				if (spec.prop instanceof PatternProp) {
					Pattern pattern = ((PatternProp) spec.prop).pattern;
					List<jkind.lustre.VarDecl> localList = this.isProperty(isMonolithic, spec.specTag)
							? pattern.toLustrePatternTimeEventPropertyList()
							: pattern.toLustrePatternTimeEventConstraintList();

					vars.addAll(localList);
				}

			}

			for (Entry<String, NodeContract> entry : this.subNodes.entrySet()) {

				String prefix = entry.getKey() + "__";
				NodeContract nc = entry.getValue();
				for (VarDecl subTimeVar : nc.toEventTimeVarList(isMonolithic)) {
					vars.add(new VarDecl(prefix + subTimeVar.id, subTimeVar.type));
				}
			}

			return vars;
		}


		private List<Equation> toLustreEquationList(boolean isMonolithic) {
			List<Equation> equations = new ArrayList<>();

			for (Connection conn : this.connections) {
				equations.add(conn.toLustreEquation());
			}

			for (Spec spec : this.specList) {

				if (spec.prop instanceof PatternProp) {
					Pattern pattern = ((PatternProp) spec.prop).pattern;

					List<jkind.lustre.Equation> localList = this.isProperty(isMonolithic, spec.specTag)
							? pattern.toLustrePatternEquationPropertyList()
							: pattern.toLustrePatternEquationConstraintList();
					equations.addAll(localList);
				}

			}
			return equations;
		}

		private List<VarDecl> toLustreChanBiList(boolean isMonolithic) {

			List<VarDecl> vars = new ArrayList<>();

			for (Channel chan : this.channels.values()) {
				if (chan.direction instanceof Bi) {
					vars.add(chan.toLustreVar());
				}
			}

			for (Spec spec : this.specList) {

				if (spec.prop instanceof PatternProp) {
					Pattern pattern = ((PatternProp) spec.prop).pattern;

					List<jkind.lustre.VarDecl> localList = this.isProperty(isMonolithic, spec.specTag)
							? pattern.toLustrePatternChanBiPropertyList()
							: pattern.toLustrePatternChanBiConstraintList();
					vars.addAll(localList);
				}

			}

			return vars;
		}


		private List<VarDecl> toLustreChanOutList(boolean isMonolithic) {

			List<VarDecl> vars = new ArrayList<>();
			for (Channel chan : this.channels.values()) {
				if (chan.direction instanceof Out) {
					vars.add(chan.toLustreVar());
				}
			}

			for (Spec spec : this.specList) {

				if (spec.prop instanceof PatternProp) {
					Pattern pattern = ((PatternProp) spec.prop).pattern;

					List<jkind.lustre.VarDecl> localList = this.isProperty(isMonolithic, spec.specTag)
							? pattern.toLustrePatternChanOutPropertyList()
							: pattern.toLustrePatternChanOutConstraintList();
					vars.addAll(localList);
				}

			}

			for (Entry<String, NodeContract> entry : this.subNodes.entrySet()) {
				String prefix = entry.getKey();
				NodeContract nc = entry.getValue();

				for (VarDecl nestedVar : nc.toLustreChanOutList(isMonolithic)) {
					String id = prefix + "__" + nestedVar.id;
					jkind.lustre.Type type = nestedVar.type;
					vars.add(new VarDecl(id, type));
				}

				for (String assumeKey : nc.toLustreAssumeMap(isMonolithic).keySet()) {
					String id = prefix + "__" + assumeKey;
					vars.add(new VarDecl(id, NamedType.BOOL));
				}

				for (String propKey : nc.toLustreLemmaMap(isMonolithic).keySet()) {
					String id = prefix + "__" + propKey;
					vars.add(new VarDecl(id, NamedType.BOOL));
				}

				for (String propKey : nc.toLustrePatternPropMap(isMonolithic).keySet()) {
					String id = prefix + "__" + propKey;
					vars.add(new VarDecl(id, NamedType.BOOL));
				}

				vars.add(new VarDecl(prefix + "__ASSUME__HIST", NamedType.BOOL));

			}

			return vars;
		}

		private List<VarDecl> toLustreChanInList(boolean isMonolithic) {

			List<VarDecl> vars = new ArrayList<>();
			for (Channel chan : this.channels.values()) {
				if (chan.direction instanceof In) {
					vars.add(chan.toLustreVar());
				}
			}

			for (Spec spec : this.specList) {

				if (spec.prop instanceof PatternProp) {
					Pattern pattern = ((PatternProp) spec.prop).pattern;

					List<jkind.lustre.VarDecl> localList = this.isProperty(isMonolithic, spec.specTag)
							? pattern.toLustrePatternChanInPropertyList()
							: pattern.toLustrePatternChanInConstraintList();
					vars.addAll(localList);
				}

			}

			for (Entry<String, NodeContract> entry : this.subNodes.entrySet()) {
				String prefix = entry.getKey();
				NodeContract nc = entry.getValue();
				for (VarDecl nestedVar : nc.toLustreChanInList(isMonolithic)) {
					String id = prefix + "__" + nestedVar.id;
					jkind.lustre.Type type = nestedVar.type;
					vars.add(new VarDecl(id, type));
				}

				vars.add(nc.toLustreClockVar());


			}

			return vars;
		}


		private VarDecl toLustreClockVar() {
			return new jkind.lustre.VarDecl(this.getName() + "__CLOCK_", jkind.lustre.NamedType.BOOL);
		}


		private boolean isProperty(boolean isMonolithic, SpecTag specTag) {
			return (specTag == SpecTag.Assume && !this.isImpl && !isMonolithic)
					|| (specTag == SpecTag.Lemma && (this.isImpl || isMonolithic))
					|| (specTag == SpecTag.Guarantee && (this.isImpl || isMonolithic));

		}


		private Map<String, jkind.lustre.Expr> toLustrePatternPropMap(boolean isMonolithic) {

			Map<String, jkind.lustre.Expr> props = new HashMap<>();
			for (Spec spec : this.specList) {

				if (spec.prop instanceof PatternProp) {
					Pattern pattern = ((PatternProp) spec.prop).pattern;

					Map<String, jkind.lustre.Expr> localMap = this.isProperty(isMonolithic, spec.specTag)
							? pattern.toLustrePatternPropertyMap()
							: pattern.toLustrePatternConstraintMap();
					props.putAll(localMap);
				}
			}

			for (Entry<String, NodeContract> entry : this.subNodes.entrySet()) {
				String prefix = entry.getKey();
				NodeContract nc = entry.getValue();

				for (Entry<String, jkind.lustre.Expr> nestedEntry : nc.toLustrePatternPropMap(isMonolithic)
						.entrySet()) {
					String key = prefix + "__" + nestedEntry.getKey();
					jkind.lustre.Expr expr = nestedEntry.getValue();
					props.put(key, expr);
				}

			}

			return props;
		}

		private List<jkind.lustre.Expr> toLustreAssertList(boolean isMonolithic) {

			List<jkind.lustre.Expr> exprs = new ArrayList<>();

			for (Spec spec : this.specList) {
				if (spec.specTag == SpecTag.Assert) {

					if (spec.prop instanceof PatternProp) {

						Pattern pattern = ((PatternProp) spec.prop).pattern;

						jkind.lustre.Expr expr = this.isProperty(isMonolithic, spec.specTag)
								? pattern.toLustreExprProperty()
								: pattern.toLustreExprConstraint();
						exprs.add(expr);
					} else if (spec.prop instanceof ExprProp) {
						jkind.lustre.Expr expr = ((ExprProp) spec.prop).expr.toLustreExpr();
						exprs.add(expr);
					}
				}

				if (spec.prop instanceof PatternProp) {
					Pattern pattern = ((PatternProp) spec.prop).pattern;

					List<jkind.lustre.Expr> localList = this.isProperty(isMonolithic, spec.specTag)
							? pattern.toLustrePatternAssertPropertyList()
							: pattern.toLustrePatternAssertConstraintList();
					exprs.addAll(localList);
				}

			}

			for (Entry<String, NodeContract> entry : this.subNodes.entrySet()) {

				NodeContract nc = entry.getValue();

				for (jkind.lustre.Expr subAssert : nc.toLustreAssertList(isMonolithic)) {
					exprs.add(subAssert);
				}

				String prefix = entry.getKey() + "__";
				jkind.lustre.IdExpr timeId = Lustre.timeExpr;

				exprs.add(new jkind.lustre.BinaryExpr(timeId, BinaryOp.EQUAL,
						new jkind.lustre.IdExpr(prefix + timeId.id)));


			}

			return exprs;
		}


		private Map<String, jkind.lustre.Expr> toLustreGuaranteeMap(boolean isMonolithic) {

			Map<String, jkind.lustre.Expr> exprMap = new HashMap<>();
			int suffix = 0;
			for (Spec spec : this.specList) {

				if (spec.specTag == SpecTag.Guarantee) {
					String key = SpecTag.Guarantee.name() + "__" + suffix;
					if (spec.prop instanceof PatternProp) {
						Pattern pattern = ((PatternProp) spec.prop).pattern;
						jkind.lustre.Expr expr = this.isProperty(isMonolithic, spec.specTag)
								? pattern.toLustreExprProperty()
								: pattern.toLustreExprConstraint();
						exprMap.put(key, expr);

					} else if (spec.prop instanceof ExprProp) {
						jkind.lustre.Expr expr = ((ExprProp) spec.prop).expr.toLustreExpr();
						exprMap.put(key, expr);

					}

					suffix = suffix + 1;
				}

			}

			return exprMap;
		}


		private Map<String, jkind.lustre.Expr> toLustreLemmaMap(boolean isMonolithic) {

			Map<String, jkind.lustre.Expr> exprMap = new HashMap<>();
			int suffix = 0;
			for (Spec spec : this.specList) {

				if (spec.specTag == SpecTag.Lemma) {
					String key = SpecTag.Lemma.name() + "__" + suffix;

					if (spec.prop instanceof PatternProp) {
						Pattern pattern = ((PatternProp) spec.prop).pattern;
						jkind.lustre.Expr expr = this.isProperty(isMonolithic, spec.specTag)
								? pattern.toLustreExprProperty()
								: pattern.toLustreExprConstraint();
						exprMap.put(key, expr);

					} else if (spec.prop instanceof ExprProp) {
						jkind.lustre.Expr expr = ((ExprProp) spec.prop).expr.toLustreExpr();
						exprMap.put(key, expr);

					}

					suffix = suffix + 1;
				}

			}

			return exprMap;
		}


		private Map<String, jkind.lustre.Expr> toLustreAssumeMap(boolean isMonolithic) {

			Map<String, jkind.lustre.Expr> exprMap = new HashMap<>();
			int suffix = 0;
			for (Spec spec : this.specList) {

				if (spec.specTag == SpecTag.Assume) {
					String key = SpecTag.Assume.name() + "__" + suffix;
					if (spec.prop instanceof PatternProp) {
						Pattern pattern = ((PatternProp) spec.prop).pattern;
						jkind.lustre.Expr expr = this.isProperty(isMonolithic, spec.specTag)
								? pattern.toLustreExprProperty()
								: pattern.toLustreExprConstraint();
						exprMap.put(key, expr);

					} else if (spec.prop instanceof ExprProp) {
						jkind.lustre.Expr expr = ((ExprProp) spec.prop).expr.toLustreExpr();
						exprMap.put(key, expr);

					}

					suffix = suffix + 1;
				}

			}

			return exprMap;
		}

		public List<Node> toLustreSubNodes(boolean isMonolithic) {

			List<Node> nodes = new ArrayList<>();
			for (NodeContract subNodeContract : this.subNodes.values()) {
				nodes.add(this.toLustreSubNode(isMonolithic));
				nodes.addAll(subNodeContract.toLustreSubNodes(isMonolithic));
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


		private Optional<Node> clockedNodeMap = Optional.empty();

		public Node toLustreClockedNode(StaticState state) {
			if (!clockedNodeMap.isPresent()) {

				NodeBuilder builder = new NodeBuilder(this.toLustreNode());
				builder.setId(Lustre.clockedNodePrefix + this.toLustreNode().id);
				builder.clearEquations();
				builder.clearInputs();
				builder.addInput(new VarDecl(Lustre.clockVarName, NamedType.BOOL));
				builder.addInput(new VarDecl(Lustre.initVarName, NamedType.BOOL));
				builder.addInputs(this.toLustreNode().inputs);


				for (DataFlow df : this.dataFlows) {
//
					jkind.lustre.Expr clockedExpr = df.src.toLustreClockedExpr();
					List<jkind.lustre.IdExpr> idExprs = new ArrayList<>();
					for (String id : df.tgts) {
						idExprs.add(new jkind.lustre.IdExpr(id));
					}
					// this will make an unguarded pre expression, but any non initialized
					// outputs should be masked by the init expression in the calling agree node
					jkind.lustre.Expr lustreExpr = new jkind.lustre.IfThenElseExpr(
							new jkind.lustre.IdExpr(Lustre.clockVarName), clockedExpr,
							new jkind.lustre.UnaryExpr(UnaryOp.PRE, idExprs.get(0)));
					builder.addEquation(new Equation(idExprs, lustreExpr));
					builder.addLocals(df.src.toLustreClockedLocals(state));
					builder.addEquations(df.src.toLustreClockedEquations());

				}
				return builder.build();
			} else {
				return clockedNodeMap.get();
			}
		}

	}

	private static List<Node> lustreNodesFromMain(NodeContract main, boolean isMonolithic) {
		List<Node> nodes = new ArrayList<>();
		Node mainNode = main.toLustreMainNode(isMonolithic);
		nodes.add(mainNode);
		List<Node> subs = main.toLustreSubNodes(isMonolithic);
		nodes.addAll(subs);
		return nodes;
	}

	private static Map<String, List<DataContract>> getNodeTypes(Map<String, NodeGen> nodeGenMap) {
		Map<String, List<DataContract>> result = new HashMap<>();

		for (Entry<String, NodeGen> entry : nodeGenMap.entrySet()) {

			String name = entry.getKey();
			NodeGen ng = entry.getValue();

			List<DataContract> nodeTypes = new ArrayList<>();
			for (Channel c : ng.channels.values()) {
				if (c.direction instanceof Nenola.Out) {
					nodeTypes.add(c.dataContract);
				}
			}

			result.put(name, nodeTypes);

		}

		return result;
	}


	public static class Program {
		public final NodeContract main;
		public final Map<String, NodeContract> nodeContractMap;
		public final Map<String, DataContract> types;
		public final Map<String, NodeGen> nodeGenMap;

		public Program(NodeContract main, Map<String, NodeContract> nodeContractMap,
				Map<String, DataContract> types, Map<String, NodeGen> nodeGenMap) {
			this.main = main;
			this.nodeContractMap = nodeContractMap;
			this.types = types;
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

		public Map<String, jkind.lustre.Program> toMonolithicLustrePrograms(boolean usingKind2) {
			Map<String, jkind.lustre.Program> programMap = this.toConsistencyPrograms();

			if (usingKind2) {
				programMap.putAll(this.toContractPrograms());
			} else {
				programMap.putAll(this.toAssumeGuaranteePrograms(true));
			}

			return programMap;
		}

		public Map<String, jkind.lustre.Program> toSingleLustrePrograms() {
			Map<String, jkind.lustre.Program> programMap = this.toConsistencyPrograms();
			programMap.putAll(this.toAssumeGuaranteePrograms(false));
			return programMap;
		}

		private Map<String, jkind.lustre.Program> toContractPrograms() {
			// TODO Auto-generated method stub

			return null;
		}

		private Map<String, jkind.lustre.Program> toAssumeGuaranteePrograms(boolean isMonolithic) {

			List<jkind.lustre.TypeDef> lustreTypes = this.lustreTypesFromDataContracts();
			List<jkind.lustre.Node> lustreNodes = new ArrayList<>();
			lustreNodes.addAll(this.toLustreNodesFromNodeGenList());
			lustreNodes.addAll(this.toLustreClockedNodesFromNodeGenList());
			lustreNodes.addAll(Nenola.lustreNodesFromMain(main, isMonolithic));

			lustreNodes.add(Lustre.getHistNode());
			lustreNodes.addAll(Lustre.getRealTimeNodes());
			jkind.lustre.Program program = new jkind.lustre.Program(Location.NULL, lustreTypes, null, null, lustreNodes,
					main.getName());
			Map<String, jkind.lustre.Program> programs = new HashMap<>();
			programs.put("Contract Guarantees", program);
			return programs;
		}



		private List<Node> toLustreClockedNodesFromNodeGenList() {
			List<jkind.lustre.Node> lustreNodes = new ArrayList<>();
			Map<String, DataContract> values = new HashMap<>();
			Map<String, List<DataContract>> funcs = getNodeTypes(this.nodeGenMap);
			Map<String, Map<String, DataContract>> props = this.getPropTypes();
			StaticState state = new StaticState(this.nodeContractMap, this.types, values, funcs, props,
					Optional.empty());

			for (NodeGen nodeGen : this.nodeGenMap.values()) {

				jkind.lustre.Node lustreNode = nodeGen.toLustreClockedNode(state);
				lustreNodes.add(lustreNode);

			}

			for (Entry<String, NodeContract> entry : this.nodeContractMap.entrySet()) {

				NodeContract nc = entry.getValue();

				lustreNodes.addAll(nc.toLustreClockedNodesFromNodeGenList(state));

			}

			return lustreNodes;
		}


		private Map<String, Map<String, DataContract>> getPropTypes() {
			for (Entry<String, NodeContract> entry : this.nodeContractMap.entrySet()) {
				// TODO Auto-generated method stub
			}
			return null;
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
			for (Entry<String, DataContract> entry : this.types.entrySet()) {

				String name = entry.getKey();
				Contract contract = entry.getValue();
				jkind.lustre.Type lustreType = contract.toLustreType();
				jkind.lustre.TypeDef lustreTypeDef = new jkind.lustre.TypeDef(name, lustreType);
				lustreTypes.add(lustreTypeDef);


			}

			return lustreTypes;
		}


		private Map<String, jkind.lustre.Program> toConsistencyPrograms() {
			// TODO Auto-generated method stub

			return null;
		}

	}



}