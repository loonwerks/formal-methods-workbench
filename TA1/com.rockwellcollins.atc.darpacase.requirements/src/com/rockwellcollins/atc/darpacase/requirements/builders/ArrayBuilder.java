package com.rockwellcollins.atc.darpacase.requirements.builders;

import java.util.ArrayList;
import java.util.List;

import com.rockwellcollins.atc.darpacase.requirements.json.ArrayValue;
import com.rockwellcollins.atc.darpacase.requirements.json.Value;

public class ArrayBuilder {

	public List<Value> values = new ArrayList<>();

	public void addValue(Value v) {
		values.add(v);
	}

	public ArrayValue build() {
		return ArrayValue.build(values);
	}
}
