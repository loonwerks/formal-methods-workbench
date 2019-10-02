/**
 */
package com.rockwellcollins.atc.resolute.resolute;

import org.eclipse.emf.ecore.EObject;

import org.osate.aadl2.StringLiteral;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Claim Context</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link com.rockwellcollins.atc.resolute.resolute.ClaimContext#getName <em>Name</em>}</li>
 *   <li>{@link com.rockwellcollins.atc.resolute.resolute.ClaimContext#getVal <em>Val</em>}</li>
 * </ul>
 *
 * @see com.rockwellcollins.atc.resolute.resolute.ResolutePackage#getClaimContext()
 * @model
 * @generated
 */
public interface ClaimContext extends EObject
{
  /**
   * Returns the value of the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Name</em>' attribute.
   * @see #setName(String)
   * @see com.rockwellcollins.atc.resolute.resolute.ResolutePackage#getClaimContext_Name()
   * @model
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link com.rockwellcollins.atc.resolute.resolute.ClaimContext#getName <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

  /**
   * Returns the value of the '<em><b>Val</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Val</em>' containment reference.
   * @see #setVal(StringLiteral)
   * @see com.rockwellcollins.atc.resolute.resolute.ResolutePackage#getClaimContext_Val()
   * @model containment="true"
   * @generated
   */
  StringLiteral getVal();

  /**
   * Sets the value of the '{@link com.rockwellcollins.atc.resolute.resolute.ClaimContext#getVal <em>Val</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Val</em>' containment reference.
   * @see #getVal()
   * @generated
   */
  void setVal(StringLiteral value);

} // ClaimContext
