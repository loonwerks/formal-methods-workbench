/**
 */
package com.rockwellcollins.atc.agree.agree.impl;

import com.rockwellcollins.atc.agree.agree.AgreePackage;
import com.rockwellcollins.atc.agree.agree.Expr;
import com.rockwellcollins.atc.agree.agree.TimeInterval;
import com.rockwellcollins.atc.agree.agree.WhenOccursStatment;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>When Occurs Statment</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link com.rockwellcollins.atc.agree.agree.impl.WhenOccursStatmentImpl#getTimes <em>Times</em>}</li>
 *   <li>{@link com.rockwellcollins.atc.agree.agree.impl.WhenOccursStatmentImpl#getInterval <em>Interval</em>}</li>
 *   <li>{@link com.rockwellcollins.atc.agree.agree.impl.WhenOccursStatmentImpl#getEffectCondition <em>Effect Condition</em>}</li>
 * </ul>
 *
 * @generated
 */
public class WhenOccursStatmentImpl extends WhenStatementImpl implements WhenOccursStatment
{
  /**
   * The cached value of the '{@link #getTimes() <em>Times</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getTimes()
   * @generated
   * @ordered
   */
  protected Expr times;

  /**
   * The cached value of the '{@link #getInterval() <em>Interval</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getInterval()
   * @generated
   * @ordered
   */
  protected TimeInterval interval;

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
  protected WhenOccursStatmentImpl()
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
    return AgreePackage.Literals.WHEN_OCCURS_STATMENT;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Expr getTimes()
  {
    return times;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetTimes(Expr newTimes, NotificationChain msgs)
  {
    Expr oldTimes = times;
    times = newTimes;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, AgreePackage.WHEN_OCCURS_STATMENT__TIMES, oldTimes, newTimes);
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
  public void setTimes(Expr newTimes)
  {
    if (newTimes != times)
    {
      NotificationChain msgs = null;
      if (times != null)
        msgs = ((InternalEObject)times).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - AgreePackage.WHEN_OCCURS_STATMENT__TIMES, null, msgs);
      if (newTimes != null)
        msgs = ((InternalEObject)newTimes).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - AgreePackage.WHEN_OCCURS_STATMENT__TIMES, null, msgs);
      msgs = basicSetTimes(newTimes, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, AgreePackage.WHEN_OCCURS_STATMENT__TIMES, newTimes, newTimes));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public TimeInterval getInterval()
  {
    return interval;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetInterval(TimeInterval newInterval, NotificationChain msgs)
  {
    TimeInterval oldInterval = interval;
    interval = newInterval;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, AgreePackage.WHEN_OCCURS_STATMENT__INTERVAL, oldInterval, newInterval);
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
  public void setInterval(TimeInterval newInterval)
  {
    if (newInterval != interval)
    {
      NotificationChain msgs = null;
      if (interval != null)
        msgs = ((InternalEObject)interval).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - AgreePackage.WHEN_OCCURS_STATMENT__INTERVAL, null, msgs);
      if (newInterval != null)
        msgs = ((InternalEObject)newInterval).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - AgreePackage.WHEN_OCCURS_STATMENT__INTERVAL, null, msgs);
      msgs = basicSetInterval(newInterval, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, AgreePackage.WHEN_OCCURS_STATMENT__INTERVAL, newInterval, newInterval));
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
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, AgreePackage.WHEN_OCCURS_STATMENT__EFFECT_CONDITION, oldEffectCondition, newEffectCondition);
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
        msgs = ((InternalEObject)effectCondition).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - AgreePackage.WHEN_OCCURS_STATMENT__EFFECT_CONDITION, null, msgs);
      if (newEffectCondition != null)
        msgs = ((InternalEObject)newEffectCondition).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - AgreePackage.WHEN_OCCURS_STATMENT__EFFECT_CONDITION, null, msgs);
      msgs = basicSetEffectCondition(newEffectCondition, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, AgreePackage.WHEN_OCCURS_STATMENT__EFFECT_CONDITION, newEffectCondition, newEffectCondition));
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
      case AgreePackage.WHEN_OCCURS_STATMENT__TIMES:
        return basicSetTimes(null, msgs);
      case AgreePackage.WHEN_OCCURS_STATMENT__INTERVAL:
        return basicSetInterval(null, msgs);
      case AgreePackage.WHEN_OCCURS_STATMENT__EFFECT_CONDITION:
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
      case AgreePackage.WHEN_OCCURS_STATMENT__TIMES:
        return getTimes();
      case AgreePackage.WHEN_OCCURS_STATMENT__INTERVAL:
        return getInterval();
      case AgreePackage.WHEN_OCCURS_STATMENT__EFFECT_CONDITION:
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
      case AgreePackage.WHEN_OCCURS_STATMENT__TIMES:
        setTimes((Expr)newValue);
        return;
      case AgreePackage.WHEN_OCCURS_STATMENT__INTERVAL:
        setInterval((TimeInterval)newValue);
        return;
      case AgreePackage.WHEN_OCCURS_STATMENT__EFFECT_CONDITION:
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
      case AgreePackage.WHEN_OCCURS_STATMENT__TIMES:
        setTimes((Expr)null);
        return;
      case AgreePackage.WHEN_OCCURS_STATMENT__INTERVAL:
        setInterval((TimeInterval)null);
        return;
      case AgreePackage.WHEN_OCCURS_STATMENT__EFFECT_CONDITION:
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
      case AgreePackage.WHEN_OCCURS_STATMENT__TIMES:
        return times != null;
      case AgreePackage.WHEN_OCCURS_STATMENT__INTERVAL:
        return interval != null;
      case AgreePackage.WHEN_OCCURS_STATMENT__EFFECT_CONDITION:
        return effectCondition != null;
    }
    return super.eIsSet(featureID);
  }

} //WhenOccursStatmentImpl
