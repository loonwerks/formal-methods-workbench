/**
 */
package com.rockwellcollins.atc.agree.agree.impl;

import com.rockwellcollins.atc.agree.agree.AgreePackage;
import com.rockwellcollins.atc.agree.agree.Expr;
import com.rockwellcollins.atc.agree.agree.WheneverHoldsStatement;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Whenever Holds Statement</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link com.rockwellcollins.atc.agree.agree.impl.WheneverHoldsStatementImpl#getCauseEvent <em>Cause Event</em>}</li>
 *   <li>{@link com.rockwellcollins.atc.agree.agree.impl.WheneverHoldsStatementImpl#getEffectCondition <em>Effect Condition</em>}</li>
 * </ul>
 *
 * @generated
 */
public class WheneverHoldsStatementImpl extends WheneverStatementImpl implements WheneverHoldsStatement
{
  /**
   * The cached value of the '{@link #getCauseEvent() <em>Cause Event</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getCauseEvent()
   * @generated
   * @ordered
   */
  protected Expr causeEvent;

  /**
   * The cached value of the '{@link #getEffectCondition() <em>Effect Condition</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getEffectCondition()
   * @generated
   * @ordered
   */
  protected Expr effectCondition;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected WheneverHoldsStatementImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return AgreePackage.Literals.WHENEVER_HOLDS_STATEMENT;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Expr getCauseEvent()
  {
    return causeEvent;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetCauseEvent(Expr newCauseEvent, NotificationChain msgs)
  {
    Expr oldCauseEvent = causeEvent;
    causeEvent = newCauseEvent;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, AgreePackage.WHENEVER_HOLDS_STATEMENT__CAUSE_EVENT, oldCauseEvent, newCauseEvent);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setCauseEvent(Expr newCauseEvent)
  {
    if (newCauseEvent != causeEvent)
    {
      NotificationChain msgs = null;
      if (causeEvent != null)
        msgs = ((InternalEObject)causeEvent).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - AgreePackage.WHENEVER_HOLDS_STATEMENT__CAUSE_EVENT, null, msgs);
      if (newCauseEvent != null)
        msgs = ((InternalEObject)newCauseEvent).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - AgreePackage.WHENEVER_HOLDS_STATEMENT__CAUSE_EVENT, null, msgs);
      msgs = basicSetCauseEvent(newCauseEvent, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, AgreePackage.WHENEVER_HOLDS_STATEMENT__CAUSE_EVENT, newCauseEvent, newCauseEvent));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Expr getEffectCondition()
  {
    return effectCondition;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetEffectCondition(Expr newEffectCondition, NotificationChain msgs)
  {
    Expr oldEffectCondition = effectCondition;
    effectCondition = newEffectCondition;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, AgreePackage.WHENEVER_HOLDS_STATEMENT__EFFECT_CONDITION, oldEffectCondition, newEffectCondition);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setEffectCondition(Expr newEffectCondition)
  {
    if (newEffectCondition != effectCondition)
    {
      NotificationChain msgs = null;
      if (effectCondition != null)
        msgs = ((InternalEObject)effectCondition).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - AgreePackage.WHENEVER_HOLDS_STATEMENT__EFFECT_CONDITION, null, msgs);
      if (newEffectCondition != null)
        msgs = ((InternalEObject)newEffectCondition).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - AgreePackage.WHENEVER_HOLDS_STATEMENT__EFFECT_CONDITION, null, msgs);
      msgs = basicSetEffectCondition(newEffectCondition, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, AgreePackage.WHENEVER_HOLDS_STATEMENT__EFFECT_CONDITION, newEffectCondition, newEffectCondition));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
      case AgreePackage.WHENEVER_HOLDS_STATEMENT__CAUSE_EVENT:
        return basicSetCauseEvent(null, msgs);
      case AgreePackage.WHENEVER_HOLDS_STATEMENT__EFFECT_CONDITION:
        return basicSetEffectCondition(null, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
      case AgreePackage.WHENEVER_HOLDS_STATEMENT__CAUSE_EVENT:
        return getCauseEvent();
      case AgreePackage.WHENEVER_HOLDS_STATEMENT__EFFECT_CONDITION:
        return getEffectCondition();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
      case AgreePackage.WHENEVER_HOLDS_STATEMENT__CAUSE_EVENT:
        setCauseEvent((Expr)newValue);
        return;
      case AgreePackage.WHENEVER_HOLDS_STATEMENT__EFFECT_CONDITION:
        setEffectCondition((Expr)newValue);
        return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
      case AgreePackage.WHENEVER_HOLDS_STATEMENT__CAUSE_EVENT:
        setCauseEvent((Expr)null);
        return;
      case AgreePackage.WHENEVER_HOLDS_STATEMENT__EFFECT_CONDITION:
        setEffectCondition((Expr)null);
        return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
      case AgreePackage.WHENEVER_HOLDS_STATEMENT__CAUSE_EVENT:
        return causeEvent != null;
      case AgreePackage.WHENEVER_HOLDS_STATEMENT__EFFECT_CONDITION:
        return effectCondition != null;
    }
    return super.eIsSet(featureID);
  }

} //WheneverHoldsStatementImpl
