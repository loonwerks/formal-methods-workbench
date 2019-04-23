/**
 */
package com.rockwellcollins.atc.agree.agree.impl;

import com.rockwellcollins.atc.agree.agree.AgreePackage;
import com.rockwellcollins.atc.agree.agree.Expr;
import com.rockwellcollins.atc.agree.agree.WheneverOccursStatement;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Whenever Occurs Statement</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link com.rockwellcollins.atc.agree.agree.impl.WheneverOccursStatementImpl#getCauseEvent <em>Cause Event</em>}</li>
 *   <li>{@link com.rockwellcollins.atc.agree.agree.impl.WheneverOccursStatementImpl#getEffectEvent <em>Effect Event</em>}</li>
 * </ul>
 *
 * @generated
 */
public class WheneverOccursStatementImpl extends WheneverStatementImpl implements WheneverOccursStatement
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
   * The cached value of the '{@link #getEffectEvent() <em>Effect Event</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getEffectEvent()
   * @generated
   * @ordered
   */
  protected Expr effectEvent;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected WheneverOccursStatementImpl()
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
    return AgreePackage.Literals.WHENEVER_OCCURS_STATEMENT;
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
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, AgreePackage.WHENEVER_OCCURS_STATEMENT__CAUSE_EVENT, oldCauseEvent, newCauseEvent);
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
        msgs = ((InternalEObject)causeEvent).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - AgreePackage.WHENEVER_OCCURS_STATEMENT__CAUSE_EVENT, null, msgs);
      if (newCauseEvent != null)
        msgs = ((InternalEObject)newCauseEvent).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - AgreePackage.WHENEVER_OCCURS_STATEMENT__CAUSE_EVENT, null, msgs);
      msgs = basicSetCauseEvent(newCauseEvent, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, AgreePackage.WHENEVER_OCCURS_STATEMENT__CAUSE_EVENT, newCauseEvent, newCauseEvent));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Expr getEffectEvent()
  {
    return effectEvent;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetEffectEvent(Expr newEffectEvent, NotificationChain msgs)
  {
    Expr oldEffectEvent = effectEvent;
    effectEvent = newEffectEvent;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, AgreePackage.WHENEVER_OCCURS_STATEMENT__EFFECT_EVENT, oldEffectEvent, newEffectEvent);
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
  public void setEffectEvent(Expr newEffectEvent)
  {
    if (newEffectEvent != effectEvent)
    {
      NotificationChain msgs = null;
      if (effectEvent != null)
        msgs = ((InternalEObject)effectEvent).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - AgreePackage.WHENEVER_OCCURS_STATEMENT__EFFECT_EVENT, null, msgs);
      if (newEffectEvent != null)
        msgs = ((InternalEObject)newEffectEvent).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - AgreePackage.WHENEVER_OCCURS_STATEMENT__EFFECT_EVENT, null, msgs);
      msgs = basicSetEffectEvent(newEffectEvent, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, AgreePackage.WHENEVER_OCCURS_STATEMENT__EFFECT_EVENT, newEffectEvent, newEffectEvent));
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
      case AgreePackage.WHENEVER_OCCURS_STATEMENT__CAUSE_EVENT:
        return basicSetCauseEvent(null, msgs);
      case AgreePackage.WHENEVER_OCCURS_STATEMENT__EFFECT_EVENT:
        return basicSetEffectEvent(null, msgs);
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
      case AgreePackage.WHENEVER_OCCURS_STATEMENT__CAUSE_EVENT:
        return getCauseEvent();
      case AgreePackage.WHENEVER_OCCURS_STATEMENT__EFFECT_EVENT:
        return getEffectEvent();
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
      case AgreePackage.WHENEVER_OCCURS_STATEMENT__CAUSE_EVENT:
        setCauseEvent((Expr)newValue);
        return;
      case AgreePackage.WHENEVER_OCCURS_STATEMENT__EFFECT_EVENT:
        setEffectEvent((Expr)newValue);
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
      case AgreePackage.WHENEVER_OCCURS_STATEMENT__CAUSE_EVENT:
        setCauseEvent((Expr)null);
        return;
      case AgreePackage.WHENEVER_OCCURS_STATEMENT__EFFECT_EVENT:
        setEffectEvent((Expr)null);
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
      case AgreePackage.WHENEVER_OCCURS_STATEMENT__CAUSE_EVENT:
        return causeEvent != null;
      case AgreePackage.WHENEVER_OCCURS_STATEMENT__EFFECT_EVENT:
        return effectEvent != null;
    }
    return super.eIsSet(featureID);
  }

} //WheneverOccursStatementImpl
