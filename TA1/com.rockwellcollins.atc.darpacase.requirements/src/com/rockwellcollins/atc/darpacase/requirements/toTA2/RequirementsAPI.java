package com.rockwellcollins.atc.darpacase.requirements.toTA2;

public class RequirementsAPI {

	public static void main(String[] args) {
		RequirementsAPI api = new RequirementsAPI();
		System.out.println(api.authenticate("waypoint_manager", "flight_director", "uav.impl"));
		System.out.println(api.preventSpoofing("c1", "uav.impl"));
		System.out.println(api.permitOnlyWellFormedData("s1", "s2", "c", "uav.impl"));
		System.out.println(api.sandboxing("p1", "uav.impl"));
		System.out.println(api.stackCanary("p1", "uav.impl"));
		System.out.println(api.integrity("flight_plan", "uav.impl"));
		System.out.println(api.useLayeredProtection("p1", "p2", "uav.impl"));
		System.out.println(api.controlAccess("flight_plan", "waypoint_manager", "uav.impl"));
		System.out.println(api.confidentiality("c1", "uav.impl"));
	}

	private static final String newline = System.lineSeparator();
	private final int tabSpaces;

	private StringBuilder build = null;
	private int tabs = 0;

	private String getTabs() {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < tabs * tabSpaces; i++) {
			builder.append(" ");
		}
		return builder.toString();
	}

	private void println(String s) {
		if (build == null) {
			System.err.println("Builder is null");
			return;
		}

		build.append(getTabs());
		build.append(s.replaceAll("\\R", System.lineSeparator() + getTabs()));
		println();
	}

	private void println() {
		build.append(newline);
	}

	private void resetTabs() {
		tabs = 0;
	}

	private void refreshBuilder() {
		build = new StringBuilder();
		resetTabs();
	}

	private String get() {
		String r = build.toString();
		build = null;
		return r;
	}

	public RequirementsAPI() {
		tabSpaces = 3;
	}

	public RequirementsAPI(int spacesPerTab) {
		tabSpaces = spacesPerTab;
	}

	/*
	 * addresses: lack_of_authentication, man_in_the_middle
	 */
	public String authenticate(String authenticator, String authenticatee, String context) {
		refreshBuilder();
		println("Context: " + context);
		println();
		tabs++;
		println("authenticate(" + authenticator + "," + authenticatee + ")");
		println();
		println(authenticateClaim());
		return get();
	}

	private String authenticateClaim() {
		return "authenticate(s1 : system, s2 : system) <= " + System.lineSeparator() + "** \"s1 authenticates s2\" **"
				+ System.lineSeparator() + "true";
	}

	/*
	 * addresses: replay attacks
	 */
	public String preventSpoofing(String connection, String context) {
		refreshBuilder();
		println("Context:" + context);
		println();
		tabs++;
		println("preventSpoofing(" + connection + ")");
		println();
		println(preventSpoofingClaim());
		return get();
	}

	private String preventSpoofingClaim() {
		return "preventSpoofing(c : connection) <= " + System.lineSeparator() + "** \"spoofing of communication on c is prevented\" **"
				+ System.lineSeparator() + "true";
	}

	/*
	 * addresses: command-injection
	 */
	public String permitOnlyWellFormedData(String srcSystem, String destSys, String connection, String context) {
		refreshBuilder();
		println("Context: " + context);
		println();
		tabs++;
		println("well_formed(" + srcSystem + "," + destSys + "," + connection + ")");
		println();
		println(permitWellFormedDataClaim());
		return get();
	}

	private String permitWellFormedDataClaim() {
		return "permitWellFormedData(s1 : system, s2 : system, c : connection) <= " + System.lineSeparator()
				+ "** \"connection c only permits well-formed data to flow from s1 to s2\" **" + System.lineSeparator()
				+ "true";
	}

	public String sandboxing(String process, String context) {
		refreshBuilder();
		println("Context: " + context);
		println();
		tabs++;
		println("sandboxed(" + process + ")");
		println();
		println(sandboxClaim());
		return get();
	}

	private String sandboxClaim() {
		return "sandboxed(p : process) <= " + System.lineSeparator()
				+ "** \"process p is run is sandboxed to prevent injection attacks\" **" + System.lineSeparator()
				+ "true";
	}

	public String stackCanary(String process, String context) {
		refreshBuilder();
		println("Context: " + context);
		println();
		tabs++;
		println("stackCanary(" + process + ")");
		println();
		println(stackCanaryClaim());
		return get();
	}

	private String stackCanaryClaim() {
		return "stackCanary(p : process) <= " + System.lineSeparator()
				+ "** \"process p is protected from stack exploits through the use" + System.lineSeparator()
				+ "a stack canary\" **" + System.lineSeparator() + "true";
	}

	/*
	 * addresses: data_overwrites
	 */
	public String integrity(String data, String context) {
		refreshBuilder();
		println("Context: " + context);
		println();
		tabs++;
		println("integrity(" + data + ")");
		println();
		println(integrityClaim());
		return get();
	}

	private String integrityClaim() {
		return "integrity(d : data) <= " + System.lineSeparator() + "** \"the integrity of data d is maintained\" **"
				+ System.lineSeparator() + "true";
	}

	/*
	 * addresses: protocol weakness
	 */
	public String useLayeredProtection(String process, String protocol, String context) {
		refreshBuilder();
		println("Context: " + context);
		println();
		tabs++;
		println("layeredProtection(" + process + "," + protocol + ")");
		println();
		println(layeredProtectionClaim());
		return get();
	}

	private String layeredProtectionClaim() {
		return "layeredProtection(p1 : process, p2 : protocol) <= " + System.lineSeparator()
				+ "** \"process p1 enforces multiple layers of protection " + System.lineSeparator()
				+ "when employing protocol p2\" **" + System.lineSeparator() + "true";
	}

	/*
	 * addresses: open, uncontrolled data
	 */
	public String controlAccess(String data, String system, String context) {
		refreshBuilder();
		println("Context: " + context);
		println();
		tabs++;
		println("integrity(" + data + "," + system + ")");
		println();
		println(controlAccessClaim());
		return get();
	}

	private String controlAccessClaim() {
		return "control_access(d : data, s : system) <= " + System.lineSeparator()
				+ "** \"access to data d is controlled on system s\" **"
				+ System.lineSeparator() + "true";
	}

	/*
	 * addresses: unencrypted data
	 */
	public String confidentiality(String connection, String context) {
		refreshBuilder();
		println("Context: " + context);
		println();
		tabs++;
		println("confidentiality(" + connection + ")");
		println();
		println(confidentialityClaim());
		return get();
	}

	private String confidentialityClaim() {
		return "confidential(c : connection) <= " + System.lineSeparator()
				+ "** \"information exchanged on connection c is confidential\" **" + System.lineSeparator() + "true";
	}
}
