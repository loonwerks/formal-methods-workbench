package com.rockwellcollins.atc.darpacase.requirements.builders;

import java.util.ArrayList;
import java.util.List;

import com.rockwellcollins.atc.darpacase.requirements.json.ObjectValue;
import com.rockwellcollins.atc.darpacase.requirements.json.Pair;

public class ObjectBuilder {

	private List<Pair> pairs = new ArrayList<>();

	public void addPair(Pair p) {
		pairs.add(p);
	}

	public ObjectValue build() {
		return ObjectValue.build(pairs);
	}
}
