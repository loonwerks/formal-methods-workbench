package com.collins.atc.json;

public class Pair {

	public static Pair build(String s, Value v) {
		return new Pair(s, v);
	}

	public static Pair build(String s1, String s2) {
		return new Pair(s1, StringValue.build(s2));
	}

	public final String string;
	public final Value value;

	private Pair(String s, Value v) {
		this.string = s;
		this.value = v;
	}

	@Override
	public String toString() {
		return Value.quoted(string) + " : " + value.toString();
	}
}
