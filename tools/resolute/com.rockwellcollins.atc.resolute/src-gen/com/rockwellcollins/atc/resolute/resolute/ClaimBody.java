/**
 */
package com.rockwellcollins.atc.resolute.resolute;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Claim Body</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link com.rockwellcollins.atc.resolute.resolute.ClaimBody#getClaim <em>Claim</em>}</li>
 *   <li>{@link com.rockwellcollins.atc.resolute.resolute.ClaimBody#getContext <em>Context</em>}</li>
 *   <li>{@link com.rockwellcollins.atc.resolute.resolute.ClaimBody#getAssumptions <em>Assumptions</em>}</li>
 *   <li>{@link com.rockwellcollins.atc.resolute.resolute.ClaimBody#getStrategies <em>Strategies</em>}</li>
 * </ul>
 *
 * @see com.rockwellcollins.atc.resolute.resolute.ResolutePackage#getClaimBody()
 * @model
 * @generated
 */
public interface ClaimBody extends DefinitionBody
{
  /**
   * Returns the value of the '<em><b>Claim</b></em>' containment reference list.
   * The list contents are of type {@link com.rockwellcollins.atc.resolute.resolute.ClaimText}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Claim</em>' containment reference list.
   * @see com.rockwellcollins.atc.resolute.resolute.ResolutePackage#getClaimBody_Claim()
   * @model containment="true"
   * @generated
   */
  EList<ClaimText> getClaim();

  /**
   * Returns the value of the '<em><b>Context</b></em>' containment reference list.
   * The list contents are of type {@link com.rockwellcollins.atc.resolute.resolute.ClaimContext}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Context</em>' containment reference list.
   * @see com.rockwellcollins.atc.resolute.resolute.ResolutePackage#getClaimBody_Context()
   * @model containment="true"
   * @generated
   */
  EList<ClaimContext> getContext();

  /**
   * Returns the value of the '<em><b>Assumptions</b></em>' containment reference list.
   * The list contents are of type {@link com.rockwellcollins.atc.resolute.resolute.ClaimAssumption}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Assumptions</em>' containment reference list.
   * @see com.rockwellcollins.atc.resolute.resolute.ResolutePackage#getClaimBody_Assumptions()
   * @model containment="true"
   * @generated
   */
  EList<ClaimAssumption> getAssumptions();

  /**
   * Returns the value of the '<em><b>Strategies</b></em>' containment reference list.
   * The list contents are of type {@link com.rockwellcollins.atc.resolute.resolute.ClaimStrategy}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Strategies</em>' containment reference list.
   * @see com.rockwellcollins.atc.resolute.resolute.ResolutePackage#getClaimBody_Strategies()
   * @model containment="true"
   * @generated
   */
  EList<ClaimStrategy> getStrategies();

} // ClaimBody
