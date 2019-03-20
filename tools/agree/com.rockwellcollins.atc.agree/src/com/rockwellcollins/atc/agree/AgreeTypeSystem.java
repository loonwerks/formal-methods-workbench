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

public class AgreeTypeSystem {

	public static interface TypeDef {

		public String getName();

		public jkind.lustre.Type toLustreType();

	}


	public static enum Prim implements TypeDef {
		IntTypeDef("int", NamedType.INT), RealTypeDef("real", NamedType.REAL), BoolTypeDef("bool",
				NamedType.BOOL), ErrorTypeDef("<error>", null);

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

	public static class RangeIntTypeDef implements TypeDef {
		public final String name;
		public final long low;
		public final long high;

		public RangeIntTypeDef(long low, long high) {
			this.name = Prim.IntTypeDef.name;
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

	public static class RangeRealTypeDef implements TypeDef {
		public final String name;
		public final double low;
		public final double high;

		public RangeRealTypeDef(double f, double g) {
			this.name = Prim.RealTypeDef.name;
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

	public static class EnumTypeDef implements TypeDef {
		public final String name;
		public final List<String> values;
		public final NamedElement elm;

		public EnumTypeDef(String name, List<String> values, NamedElement elm) {
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

	public static class RecordTypeDef implements TypeDef {
		public final String name;
		public final Map<String, TypeDef> fields;
		public final NamedElement namedElement;

		public RecordTypeDef(String name, Map<String, TypeDef> fields, NamedElement namedElement) {
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
			Map<String, AgreeTypeSystem.TypeDef> agreeFields = fields;

			Map<String, jkind.lustre.Type> lustreFields = new HashMap<>();
			for (Entry<String, AgreeTypeSystem.TypeDef> entry : agreeFields.entrySet()) {
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

	public static class ArrayTypeDef implements TypeDef {
		public final TypeDef stemType;
		public final int size;
		public final Optional<NamedElement> elmOp;

		public ArrayTypeDef(TypeDef stemType, int size, Optional<NamedElement> elmOp) {
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





	public static boolean typesEqual(TypeDef t1, TypeDef t2) {
		String str1 = t1.getName();
		String str2 = t2.getName();
		return str1.equals(str2);
	}





}