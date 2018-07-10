package agreeToJson.json;

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
