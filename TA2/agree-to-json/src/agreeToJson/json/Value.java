package agreeToJson.json;

public abstract class Value {

	private final static String quote = "\"";

	public static String quoted(String s) {
		StringBuilder b = new StringBuilder();
		b.append(quote);
		b.append(s);
		b.append(quote);
		return b.toString();
	}
}
