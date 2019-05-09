package com.rockwellcollins.atc.resolute.analysis.results;

import java.util.Map;

import org.eclipse.emf.ecore.EObject;

public class LintResult extends ResoluteResult {

	final private String text;
	final private Map<String, EObject> references;
	final private EObject location;
	final private int severity;

	public LintResult(int severity, String text, ResoluteResult body, Map<String, EObject> references,
			EObject location) {
		super(body);
		this.severity = severity;
		this.text = text;
		this.references = references;
		this.location = location;
	}

	public String getText() {
		return text;
	}

	public Map<String, EObject> getReferences() {
		return references;
	}

	public EObject getLocation() {
		return location;
	}

	public int getSeverity() {
		return severity;
	}

}
