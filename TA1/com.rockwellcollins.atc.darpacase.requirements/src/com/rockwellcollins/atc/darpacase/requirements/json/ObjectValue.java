package com.rockwellcollins.atc.darpacase.requirements.json;

import java.util.Iterator;
import java.util.List;

public class ObjectValue extends Value {

	public static ObjectValue build(List<Pair> pairs) {
		return new ObjectValue(pairs);
	}

	public List<Pair> pairs;

	private ObjectValue(List<Pair> pairs) {
		this.pairs = pairs;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("{ ");
		Iterator<Pair> it = pairs.iterator();

		while (it.hasNext()) {
			Pair next = it.next();
			builder.append(next.toString());

			if (it.hasNext()) {
				builder.append(", \n");
			}
		}

		builder.append("}");
		return builder.toString();
	}
}
