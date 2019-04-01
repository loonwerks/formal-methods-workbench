package com.rockwellcollins.atc.agree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
	}

	public static class EnumSpec implements Spec {
		public final String name;
		public final List<String> values;
		public final NamedElement elm;

		public EnumSpec(String name, List<String> values, NamedElement elm) {
			this.name = name;
			this.values = new ArrayList<>();
			this.values.addAll(values);
			this.elm = elm;
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


	}

	public static enum Topo {
		System, Data
	}

	public static enum Direc {
		In, Out
	}


	public static class RecordSpec implements Spec {

		public final String name;
		public final Topo topo;
		public final List<Field> fields;

		/* reference to Xtext elm for gui update */
		public final NamedElement namedElement;

		public RecordSpec(String name, Topo topo, List<Field> fields, NamedElement namedElement) {
			this.name = name;
			this.topo = topo;
			this.fields = new ArrayList<>();
			this.fields.addAll(fields);
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
			for (Field field : fields) {
				String key = field.name;
				jkind.lustre.Type lt = field.spec.getLustreType();
				if (lt != null) {
					lustreFields.put(key, lt);
				}
			}
			jkind.lustre.RecordType lustreRecType = new jkind.lustre.RecordType(lustreName, lustreFields);
			return lustreRecType;
		}

	}

	public static class ArraySpec implements Spec {
		public final Spec stemType;
		public final int size;
		public final Optional<NamedElement> elmOp;

		public ArraySpec(Spec stemType, int size, Optional<NamedElement> elmOp) {
			this.size = size;
			this.stemType = stemType;
			this.elmOp = elmOp;
		}

		@Override
		public String getName() {
			return stemType.getName() + "[" + size + "]";
		}

		@Override
		public jkind.lustre.Type getLustreType() {

			jkind.lustre.Type lustreBaseType = stemType.getLustreType();
			if (lustreBaseType != null) {
				jkind.lustre.ArrayType lustreArrayType = new jkind.lustre.ArrayType(lustreBaseType, size);
				return lustreArrayType;
			} else {
				return null;
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
		String str1 = t1.getName();
		String str2 = t2.getName();
		return str1.equals(str2);
	}





}