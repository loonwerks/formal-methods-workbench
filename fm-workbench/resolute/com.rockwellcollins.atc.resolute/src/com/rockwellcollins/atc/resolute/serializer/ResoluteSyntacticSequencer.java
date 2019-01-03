/*
 * generated by Xtext
 */
package com.rockwellcollins.atc.resolute.serializer;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.RuleCall;
import org.eclipse.xtext.nodemodel.INode;

public class ResoluteSyntacticSequencer extends AbstractResoluteSyntacticSequencer {
	/**
	 * AppliesToKeywords:
	 * 	'applies' 'to'
	 * ;
	 */
	@Override
	protected String getAppliesToKeywordsToken(EObject semanticObject, RuleCall ruleCall, INode node) {
		if (node != null) {
			return super.getAppliesToKeywordsToken(semanticObject, ruleCall, node);
		}
		return "applies to";
	}

	/**
	 * InBindingKeywords:
	 * 	'in' 'binding'
	 * ;
	 */
	@Override
	protected String getInBindingKeywordsToken(EObject semanticObject, RuleCall ruleCall, INode node) {
		if (node != null) {
			return super.getInBindingKeywordsToken(semanticObject, ruleCall, node);
		}
		return "in binding";
	}

	/**
	 * InModesKeywords:
	 * 	'in' 'modes'
	 * ;
	 */
	@Override
	protected String getInModesKeywordsToken(EObject semanticObject, RuleCall ruleCall, INode node) {
		if (node != null) {
			return super.getInModesKeywordsToken(semanticObject, ruleCall, node);
		}
		return "in modes";
	}
}