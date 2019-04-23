/**
 */
package com.rockwellcollins.atc.agree.agree;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Whenever Occurs Statement</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link com.rockwellcollins.atc.agree.agree.WheneverOccursStatement#getCauseEvent <em>Cause Event</em>}</li>
 *   <li>{@link com.rockwellcollins.atc.agree.agree.WheneverOccursStatement#getEffectEvent <em>Effect Event</em>}</li>
 * </ul>
 *
 * @see com.rockwellcollins.atc.agree.agree.AgreePackage#getWheneverOccursStatement()
 * @model
 * @generated
 */
public interface WheneverOccursStatement extends WheneverStatement
{
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
   * @see com.rockwellcollins.atc.agree.agree.AgreePackage#getWheneverOccursStatement_CauseEvent()
   * @model containment="true"
   * @generated
   */
  Expr getCauseEvent();

  /**
   * Sets the value of the '{@link com.rockwellcollins.atc.agree.agree.WheneverOccursStatement#getCauseEvent <em>Cause Event</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Cause Event</em>' containment reference.
   * @see #getCauseEvent()
   * @generated
   */
  void setCauseEvent(Expr value);

  /**
   * Returns the value of the '<em><b>Effect Event</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Effect Event</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Effect Event</em>' containment reference.
   * @see #setEffectEvent(Expr)
   * @see com.rockwellcollins.atc.agree.agree.AgreePackage#getWheneverOccursStatement_EffectEvent()
   * @model containment="true"
   * @generated
   */
  Expr getEffectEvent();

  /**
   * Sets the value of the '{@link com.rockwellcollins.atc.agree.agree.WheneverOccursStatement#getEffectEvent <em>Effect Event</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Effect Event</em>' containment reference.
   * @see #getEffectEvent()
   * @generated
   */
  void setEffectEvent(Expr value);

} // WheneverOccursStatement
