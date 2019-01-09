package com.collins.atc.json;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ArrayValue extends Value {

	public static ArrayValue build(List<Value> values) {
		return new ArrayValue(values);
	}

	public final List<Value> values;

	private ArrayValue(List<Value> values) {
		this.values = new ArrayList<>(values);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("[ ");
		Iterator<Value> it = values.iterator();
		while (it.hasNext()) {
			Value next = it.next();
			builder.append(next.toString());
			if (it.hasNext()) {
				builder.append(", \n");
			}
		}
		builder.append("]");
		return builder.toString();
	}
}
