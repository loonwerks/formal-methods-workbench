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

	public enum ContractType {
		Assume, Guarantee, Lemma, Assert
	}

	public class Contract {

		public final ContractType ct;
		public final String name;
		public final String description;
		public final Prop prop;

		public Contract(ContractType ct, String name, String description, Prop prop) {
			this.ct = ct;
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

	// | PropertyStatement
	// | ConstStatement
	// | EqStatement
	// | AssignStatement
	// | LinearizationDef
	// | FnDef
	// | LibraryFnDef
	// | NodeDef

	public static interface Spec {

		public String getName();

		public jkind.lustre.Type getLustreType();

		public boolean staticEquals(Spec other);

	}


	public static enum Prim implements Spec {
		IntSpec("int", NamedType.INT), RealSpec("real", NamedType.REAL), BoolSpec("bool",
				NamedType.BOOL), ErrorSpec("<error>", null);

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
		public boolean staticEquals(Spec other) {
			return this.getName().equals(other.getName());
		}


	}

	public static class RangeIntSpec implements Spec {
		public final String name;
		public final long low;
		public final long high;

		public RangeIntSpec(long low, long high) {
			this.name = Prim.IntSpec.name;
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
		public boolean staticEquals(Spec other) {
			return this.getName().equals(other.getName());
		}

	}

	public static class RangeRealSpec implements Spec {
		public final String name;
		public final double low;
		public final double high;

		public RangeRealSpec(double f, double g) {
			this.name = Prim.RealSpec.name;
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
		public boolean staticEquals(Spec other) {
			return this.getName().equals(other.getName());
		}
	}

	public static class EnumSpec implements Spec {
		private final String name;
		public final List<String> values;


		public final Map<String, ExprDef> exprDefMap;
		public final List<Contract> contractList;

		public EnumSpec(String name, List<String> values, Map<String, ExprDef> exprDefMap,
				List<Contract> contractList) {
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
		public boolean staticEquals(Spec other) {
			return this.getName().equals(other.getName());
		}


	}

	public static enum Topo {
		System, Data
	}

	public static enum Direc {
		In, Out
	}


	public static class RecordSpec implements Spec {

		private final String name;
		public final Topo topo;
		public final Map<String, Field> fields;

		public final Map<String, ExprDef> exprDefMap;
		public final List<Contract> contractList;

		/* reference to Xtext elm for gui update */
		public final NamedElement namedElement;

		public RecordSpec(String name, Topo topo, Map<String, Field> fields, Map<String, ExprDef> exprDefMap,
				List<Contract> contractList, NamedElement namedElement) {
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
				jkind.lustre.Type lt = entry.getValue().spec.getLustreType();
				if (lt != null) {
					lustreFields.put(key, lt);
				}
			}
			jkind.lustre.RecordType lustreRecType = new jkind.lustre.RecordType(lustreName, lustreFields);
			return lustreRecType;
		}

		@Override
		public boolean staticEquals(Spec other) {
			return this.getName().equals(other.getName());
		}

	}

	public static class ArraySpec implements Spec {

		private final String name;
		public final Spec stemSpec;
		public final int size;

		public final Map<String, ExprDef> exprDefMap;
		public final List<Contract> contractList;

		public ArraySpec(String name, Spec stemSpec, int size, Map<String, ExprDef> exprDefMap,
				List<Contract> contractList) {
			this.name = name;
			this.size = size;
			this.stemSpec = stemSpec;
			this.exprDefMap = exprDefMap;
			this.contractList = contractList;
		}

		@Override
		public String getName() {
			return name.isEmpty() ? stemSpec.getName() + "[" + size + "]" : name;
		}

		@Override
		public jkind.lustre.Type getLustreType() {

			jkind.lustre.Type lustreBaseType = stemSpec.getLustreType();
			if (lustreBaseType != null) {
				jkind.lustre.ArrayType lustreArrayType = new jkind.lustre.ArrayType(lustreBaseType, size);
				return lustreArrayType;
			} else {
				return null;
			}

		}

		@Override
		public boolean staticEquals(Spec other) {
			if (other instanceof ArraySpec) {
				return size == ((ArraySpec) other).size && stemSpec.staticEquals(((ArraySpec) other).stemSpec);
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
		public final Spec spec;
		public final Optional<Port> portOption;

		public Field(String name, Spec spec, Optional<Port> portOption) {
			this.name = name;
			this.spec = spec;
			this.portOption = portOption;
		}

	}

	public static boolean staticEqual(Spec t1, Spec t2) {
		return t1.staticEquals(t2);
	}

	public static class Program {
		public final Spec main;
		public final Map<String, Spec> specMap;

		public Program(Spec main, Map<String, Spec> specMap) {
			this.main = main;
			this.specMap = specMap;
		}
	}





}