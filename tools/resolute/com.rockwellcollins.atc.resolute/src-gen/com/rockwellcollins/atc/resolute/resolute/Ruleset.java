/**
 */
package com.rockwellcollins.atc.resolute.resolute;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Ruleset</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link com.rockwellcollins.atc.resolute.resolute.Ruleset#getBody <em>Body</em>}</li>
 * </ul>
 *
 * @see com.rockwellcollins.atc.resolute.resolute.ResolutePackage#getRuleset()
 * @model
 * @generated
 */
public interface Ruleset extends Definition
{
  /**
   * Returns the value of the '<em><b>Body</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Body</em>' containment reference.
   * @see #setBody(RulesetBody)
   * @see com.rockwellcollins.atc.resolute.resolute.ResolutePackage#getRuleset_Body()
   * @model containment="true"
   * @generated
   */
  RulesetBody getBody();

  /**
   * Sets the value of the '{@link com.rockwellcollins.atc.resolute.resolute.Ruleset#getBody <em>Body</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Body</em>' containment reference.
   * @see #getBody()
   * @generated
   */
  void setBody(RulesetBody value);

} // Ruleset
