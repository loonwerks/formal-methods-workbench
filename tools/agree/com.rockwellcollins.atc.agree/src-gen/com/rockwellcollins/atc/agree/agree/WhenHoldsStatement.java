/**
 */
package com.rockwellcollins.atc.agree.agree;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>When Holds Statement</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link com.rockwellcollins.atc.agree.agree.WhenHoldsStatement#getCauseCondition <em>Cause Condition</em>}</li>
 *   <li>{@link com.rockwellcollins.atc.agree.agree.WhenHoldsStatement#getConditionInterval <em>Condition Interval</em>}</li>
 *   <li>{@link com.rockwellcollins.atc.agree.agree.WhenHoldsStatement#getEffectEvent <em>Effect Event</em>}</li>
 *   <li>{@link com.rockwellcollins.atc.agree.agree.WhenHoldsStatement#getExcl <em>Excl</em>}</li>
 *   <li>{@link com.rockwellcollins.atc.agree.agree.WhenHoldsStatement#getEventInterval <em>Event Interval</em>}</li>
 * </ul>
 *
 * @see com.rockwellcollins.atc.agree.agree.AgreePackage#getWhenHoldsStatement()
 * @model
 * @generated
 */
public interface WhenHoldsStatement extends WhenStatement
{
  /**
   * Returns the value of the '<em><b>Cause Condition</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Cause Condition</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Cause Condition</em>' containment reference.
   * @see #setCauseCondition(Expr)
   * @see com.rockwellcollins.atc.agree.agree.AgreePackage#getWhenHoldsStatement_CauseCondition()
   * @model containment="true"
   * @generated
   */
  Expr getCauseCondition();

  /**
   * Sets the value of the '{@link com.rockwellcollins.atc.agree.agree.WhenHoldsStatement#getCauseCondition <em>Cause Condition</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Cause Condition</em>' containment reference.
   * @see #getCauseCondition()
   * @generated
   */
  void setCauseCondition(Expr value);

  /**
   * Returns the value of the '<em><b>Condition Interval</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Condition Interval</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Condition Interval</em>' containment reference.
   * @see #setConditionInterval(TimeInterval)
   * @see com.rockwellcollins.atc.agree.agree.AgreePackage#getWhenHoldsStatement_ConditionInterval()
   * @model containment="true"
   * @generated
   */
  TimeInterval getConditionInterval();

  /**
   * Sets the value of the '{@link com.rockwellcollins.atc.agree.agree.WhenHoldsStatement#getConditionInterval <em>Condition Interval</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Condition Interval</em>' containment reference.
   * @see #getConditionInterval()
   * @generated
   */
  void setConditionInterval(TimeInterval value);

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
   * @see com.rockwellcollins.atc.agree.agree.AgreePackage#getWhenHoldsStatement_EffectEvent()
   * @model containment="true"
   * @generated
   */
  Expr getEffectEvent();

  /**
   * Sets the value of the '{@link com.rockwellcollins.atc.agree.agree.WhenHoldsStatement#getEffectEvent <em>Effect Event</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Effect Event</em>' containment reference.
   * @see #getEffectEvent()
   * @generated
   */
  void setEffectEvent(Expr value);

  /**
   * Returns the value of the '<em><b>Excl</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Excl</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Excl</em>' attribute.
   * @see #setExcl(String)
   * @see com.rockwellcollins.atc.agree.agree.AgreePackage#getWhenHoldsStatement_Excl()
   * @model
   * @generated
   */
  String getExcl();

  /**
   * Sets the value of the '{@link com.rockwellcollins.atc.agree.agree.WhenHoldsStatement#getExcl <em>Excl</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Excl</em>' attribute.
   * @see #getExcl()
   * @generated
   */
  void setExcl(String value);

  /**
   * Returns the value of the '<em><b>Event Interval</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Event Interval</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Event Interval</em>' containment reference.
   * @see #setEventInterval(TimeInterval)
   * @see com.rockwellcollins.atc.agree.agree.AgreePackage#getWhenHoldsStatement_EventInterval()
   * @model containment="true"
   * @generated
   */
  TimeInterval getEventInterval();

  /**
   * Sets the value of the '{@link com.rockwellcollins.atc.agree.agree.WhenHoldsStatement#getEventInterval <em>Event Interval</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Event Interval</em>' containment reference.
   * @see #getEventInterval()
   * @generated
   */
  void setEventInterval(TimeInterval value);

} // WhenHoldsStatement
