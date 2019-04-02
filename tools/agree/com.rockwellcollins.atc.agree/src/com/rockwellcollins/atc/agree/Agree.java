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

	public class XYZ {

		public final SpecTag specTag;
		public final String name;
		public final String description;
		public final Prop prop;

		public XYZ(SpecTag specTag, String name, String description, Prop prop) {
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

	public static interface ExprDef {
		public String getName();

	}

	// inline: ConstStatement

	// global defs: NodeDef, FnDef, LinearizationDef

	// local assertions: AssertionStatement, AssignStatement, PropertyStatement, EqStatement
	// local assumptions: AssumeStatement, InputStatement
	// local guarantees: GuaranteeStatement, EqStatement
	// local lemmas: LemmaStatement
	// local input vars: InputStatement
	// local output vars: PropertyStatement, EqStatement


	// not implemented: LibraryFnDef

	public static interface Contract {

		public String getName();

		public jkind.lustre.Type getLustreType();

		public boolean staticEquals(Contract other);

	}


	public static enum Prim implements Contract {
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

	public static class RangeIntContract implements Contract {
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

	public static class RangeRealContract implements Contract {
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


		public final Map<String, ExprDef> exprDefMap;
		public final List<XYZ> contractList;

		public EnumContract(String name, List<String> values, Map<String, ExprDef> exprDefMap,
				List<XYZ> contractList) {
			this.name = name;
			this.values = new ArrayList<>();
			this.values.addAll(values);

			this.exprDefMap = exprDefMap;
			this.contractList = contractList;
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

	public static enum Topo {
		System, Data
	}

	public static enum Direc {
		In, Out
	}


	public static class RecordContract implements Contract {

		private final String name;
		public final Topo topo;
		public final Map<String, Field> fields;

		public final Map<String, ExprDef> exprDefMap;
		public final List<XYZ> contractList;

		/* reference to Xtext elm for gui update */
		public final NamedElement namedElement;

		public RecordContract(String name, Topo topo, Map<String, Field> fields, Map<String, ExprDef> exprDefMap,
				List<XYZ> contractList, NamedElement namedElement) {
			this.name = name;
			this.topo = topo;
			this.fields = new HashMap<>();
			this.fields.putAll(fields);
			this.exprDefMap = exprDefMap;
			this.contractList = contractList;

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
			for (Entry<String, Field> entry : fields.entrySet()) {
				String key = entry.getKey();
				jkind.lustre.Type lt = entry.getValue().contract.getLustreType();
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

	public static class ArrayContract implements Contract {

		private final String name;
		public final Contract stemContract;
		public final int size;

		public final Map<String, ExprDef> exprDefMap;
		public final List<XYZ> contractList;

		public ArrayContract(String name, Contract stemContract, int size, Map<String, ExprDef> exprDefMap,
				List<XYZ> contractList) {
			this.name = name;
			this.size = size;
			this.stemContract = stemContract;
			this.exprDefMap = exprDefMap;
			this.contractList = contractList;
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
		public final Direc direction;
		public final boolean isEvent;

		public Port(Direc direction, boolean isEvent) {
			this.direction = direction;
			this.isEvent = isEvent;
		}
	}

	public static class Field {
		public final String name;
		public final Contract contract;
		public final Optional<Port> portOption;

		public Field(String name, Contract contract, Optional<Port> portOption) {
			this.name = name;
			this.contract = contract;
			this.portOption = portOption;
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





}