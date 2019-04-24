/**
 */
package com.rockwellcollins.atc.agree.agree;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Whenever Holds Statement</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link com.rockwellcollins.atc.agree.agree.WheneverHoldsStatement#getEffectCondition <em>Effect Condition</em>}</li>
 *   <li>{@link com.rockwellcollins.atc.agree.agree.WheneverHoldsStatement#getCauseEvent <em>Cause Event</em>}</li>
 * </ul>
 *
 * @see com.rockwellcollins.atc.agree.agree.AgreePackage#getWheneverHoldsStatement()
 * @model
 * @generated
 */
public interface WheneverHoldsStatement extends WheneverStatement
{
  /**
   * Returns the value of the '<em><b>Effect Condition</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Effect Condition</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Effect Condition</em>' containment reference.
   * @see #setEffectCondition(Expr)
   * @see com.rockwellcollins.atc.agree.agree.AgreePackage#getWheneverHoldsStatement_EffectCondition()
   * @model containment="true"
   * @generated
   */
  Expr getEffectCondition();

  /**
   * Sets the value of the '{@link com.rockwellcollins.atc.agree.agree.WheneverHoldsStatement#getEffectCondition <em>Effect Condition</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Effect Condition</em>' containment reference.
   * @see #getEffectCondition()
   * @generated
   */
  void setEffectCondition(Expr value);

  /**
   * Returns the value of the '<em><b>Cause Event</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Cause Event</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Cause Event</em>' containment reference.
   * @see #setCauseEvent(Expr)
   * @see com.rockwellcollins.atc.agree.agree.AgreePackage#getWheneverHoldsStatement_CauseEvent()
   * @model containment="true"
   * @generated
   */
  Expr getCauseEvent();

  /**
   * Sets the value of the '{@link com.rockwellcollins.atc.agree.agree.WheneverHoldsStatement#getCauseEvent <em>Cause Event</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Cause Event</em>' containment reference.
   * @see #getCauseEvent()
   * @generated
   */
  void setCauseEvent(Expr value);

} // WheneverHoldsStatement
