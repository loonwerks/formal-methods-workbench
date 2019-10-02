package com.rockwellcollins.atc.resolute.analysis.results;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;

import com.rockwellcollins.atc.resolute.resolute.FunctionDefinition;
import com.rockwellcollins.atc.resolute.resolute.LintStatement;

public class LintResult extends ClaimResult {

	final private static String LINT_CHECK = "Lint_Check";

	final private int severity;
	final private LintStatement statement;


	public LintResult(int severity, LintStatement statement, ClaimResult result) {
		super(result.getText(), result, result.getReferences(), result.getLocation());
		this.severity = severity;
		this.statement = statement;
	}

	public int getSeverity() {
		return severity;
	}

	public Set<EObject> getLocations() {
		Set<EObject> locations = new HashSet<>();

		findLocations(getChildren(), locations);

		// If there aren't any lint_check claims,
		// use the list statement call
		if (locations.isEmpty()) {
			locations.add(statement);
		}

		return locations;
	}

	private void findLocations(List<ResoluteResult> result, Set<EObject> locations) {

		for (ResoluteResult r : result) {
			if (!r.isValid()) {
				if (r instanceof ClaimResult) {
					ClaimResult c = (ClaimResult) r;
					EObject loc = c.getLocation();
					if (loc instanceof FunctionDefinition
							&& ((FunctionDefinition) loc).getName().equalsIgnoreCase(LINT_CHECK)) {
						locations.add(c.getReferences().get(c.getText()));
					} else {
						findLocations(c.getChildren(), locations);
					}
				} else if (r instanceof ResoluteResult) {
					findLocations(r.getChildren(), locations);
				}
			}
		}

	}

}
