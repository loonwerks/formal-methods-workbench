package com.rockwellcollins.atc.darpacase.eclipse.agree_to_json.json;

public class NullValue extends Value {

	public static NullValue build() {
		return new NullValue();
	}

	private NullValue() {
	}

	@Override
	public String toString() {
		return "";
	}
}
