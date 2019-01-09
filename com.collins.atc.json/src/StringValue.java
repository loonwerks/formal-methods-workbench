package com.collins.atc.json;

public class StringValue extends Value {

	public static StringValue build(String v) {
		return new StringValue(v);
	}

	public final String value;

	private StringValue(String v) {
		this.value = v;
	}

	@Override
	public String toString() {
		return quoted(value);
	}
}
