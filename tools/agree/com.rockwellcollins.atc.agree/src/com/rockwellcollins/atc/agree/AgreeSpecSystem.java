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

public class AgreeSpecSystem {

	public static interface Spec {

		public String getName();

		public jkind.lustre.Type toLustreType();

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
		public jkind.lustre.Type toLustreType() {
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
		public jkind.lustre.Type toLustreType() {
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
		public jkind.lustre.Type toLustreType() {
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


	}

	public static enum Topo {
		System, Data
	}

	public static enum Direc {
		In, Out
	}

	public static interface Mode {
		public boolean isEvent();

		public Optional<Spec> getPayloadOption();
	}

	public static class DataMode implements Mode {
		private Spec payload;

		@Override
		public boolean isEvent() {
			return false;
		}

		@Override
		public Optional<Spec> getPayloadOption() {
			return Optional.of(payload);
		}
	}

	public static class EventDataMode implements Mode {
		private Spec payload;

		@Override
		public boolean isEvent() {
			return true;
		}

		@Override
		public Optional<Spec> getPayloadOption() {
			return Optional.of(payload);
		}
	}

	public static class EventMode implements Mode {

		@Override
		public boolean isEvent() {
			return true;
		}

		@Override
		public Optional<Spec> getPayloadOption() {
			return Optional.empty();
		}
	}

	public static class Port {

	}

	public static class RecordSpec implements Spec {

		public final String name;


		public final Topo topo;

		public final Map<String, Spec> fields;


		public final Map<String, Port> ports;

		/* reference to Xtext elm for gui update */
		public final NamedElement namedElement;


		public RecordSpec(String name, Map<String, Spec> fields, NamedElement namedElement) {
			this.name = name;
			this.fields = new HashMap<>();
			this.fields.putAll(fields);
			this.namedElement = namedElement;
			this.topo = null;
			this.ports = new HashMap<>();

		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public jkind.lustre.Type toLustreType() {
			String lustreName = name.replace("::", "__").replace(".", "__");
			Map<String, AgreeSpecSystem.Spec> agreeFields = fields;

			Map<String, jkind.lustre.Type> lustreFields = new HashMap<>();
			for (Entry<String, AgreeSpecSystem.Spec> entry : agreeFields.entrySet()) {
				String key = entry.getKey();
				jkind.lustre.Type lt = entry.getValue().toLustreType();
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
		public jkind.lustre.Type toLustreType() {

			jkind.lustre.Type lustreBaseType = stemType.toLustreType();
			if (lustreBaseType != null) {
				jkind.lustre.ArrayType lustreArrayType = new jkind.lustre.ArrayType(lustreBaseType, size);
				return lustreArrayType;
			} else {
				return null;
			}

		}

	}

	public static boolean staticEqual(Spec t1, Spec t2) {
		String str1 = t1.getName();
		String str2 = t2.getName();
		return str1.equals(str2);
	}





}