package com.rockwellcollins.atc.agree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.osate.aadl2.NamedElement;

import jkind.lustre.EnumType;
import jkind.lustre.NamedType;

public class Agree {


	public static class Name {
		String name;

		public Name(String name) {
			this.name = name;
		}
	}

	public static interface Expr {

	}

	public static interface Prop {

	}

	public static class ExprProp implements Prop {
		public final Expr expr;

		public ExprProp(Expr expr) {
			this.expr = expr;
		}
	}

	public static interface WheneverProp extends Prop {

	}

	public static interface WhenProp extends Prop {

	}

	public static interface RealTimeProp extends Prop {

	}

	public static class AlwaysProp implements Prop {

	}

	public enum SpecTag {
		Assume, Guarantee, Lemma, Assert
	}

	public class Spec {

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

	public class Parameter {
		public final Expr expr;

		public Parameter(Expr expr) {
			this.expr = expr;
		}
	}


	public static class Lift {
		public final Name fieldName;

		public Lift(Name fieldName) {
			this.fieldName = fieldName;
		}
	}

	public static class Connection {
		public final Name connName;
		public final Expr expr;

		public Connection(Name connName, Expr expr) {
			this.connName = connName;
			this.expr = expr;
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

	public static class Port {
		public final DataContract dataContract;
		public final Direc direction;
		public final boolean isEvent;

		public Port(DataContract dataContract, Direc direction, boolean isEvent) {
			this.dataContract = dataContract;
			this.direction = direction;
			this.isEvent = isEvent;
		}

	}

	public static class NodeContract implements Contract {

		private final String name;
		public final Map<String, Port> ports;
		public final Map<String, NodeContract> subNodes;
		public final List<Spec> specList;

		/* reference to Xtext elm for gui update */
		public final NamedElement namedElement;

		public NodeContract(String name, Map<String, Port> ports, Map<String, NodeContract> subNodes,
				List<Spec> specList, NamedElement namedElement) {
			this.name = name;
			this.ports = new HashMap<>();
			this.ports.putAll(ports);
			this.subNodes = new HashMap<>();
			this.subNodes.putAll(subNodes);
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
			for (Entry<String, Port> entry : ports.entrySet()) {
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

	public static class Program {
		public final Contract main;
		public final Map<String, Contract> contractMap;

		public Program(Contract main, Map<String, Contract> contractMap) {
			this.main = main;
			this.contractMap = contractMap;
		}
	}

	// inline: ConstStatement

	// global contracts: Classifier, ArrayType, PrimType, EnumStatement, RecordDef, ComponentClassifier

	// global simple nodes: NodeDef, FnDef, LinearizationDef / or should these be contracts?

	// local assertions: AssertionStatement, AssertEqualStatement,
	// local assumptions: AssumeStatement
	// local guarantees: GuaranteeStatement
	// local lemmas: LemmaStatement
	// local input ports: Feature, InputStatement
	// local output ports: Feature, BoolOutputStatement, OutputStatement
	// local subNodes: Subcomponent
	// local connections: ConnectedElement, ConnectionStatement

	// not implemented: LibraryFnDef





}