/**
 */
package com.rockwellcollins.atc.resolute.resolute;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Ruleset Body</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link com.rockwellcollins.atc.resolute.resolute.RulesetBody#getLintStatements <em>Lint Statements</em>}</li>
 * </ul>
 *
 * @see com.rockwellcollins.atc.resolute.resolute.ResolutePackage#getRulesetBody()
 * @model
 * @generated
 */
public interface RulesetBody extends EObject
{
  /**
   * Returns the value of the '<em><b>Lint Statements</b></em>' containment reference list.
   * The list contents are of type {@link com.rockwellcollins.atc.resolute.resolute.LintStatement}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Lint Statements</em>' containment reference list.
   * @see com.rockwellcollins.atc.resolute.resolute.ResolutePackage#getRulesetBody_LintStatements()
   * @model containment="true"
   * @generated
   */
  EList<LintStatement> getLintStatements();

} // RulesetBody
