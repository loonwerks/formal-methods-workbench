/**
 */
package com.rockwellcollins.atc.agree.agree.impl;

import com.rockwellcollins.atc.agree.agree.AgreePackage;
import com.rockwellcollins.atc.agree.agree.Expr;
import com.rockwellcollins.atc.agree.agree.WheneverBecomesTrueStatement;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Whenever Becomes True Statement</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link com.rockwellcollins.atc.agree.agree.impl.WheneverBecomesTrueStatementImpl#getCause <em>Cause</em>}</li>
 *   <li>{@link com.rockwellcollins.atc.agree.agree.impl.WheneverBecomesTrueStatementImpl#getEffect <em>Effect</em>}</li>
 * </ul>
 *
 * @generated
 */
public class WheneverBecomesTrueStatementImpl extends WheneverStatementImpl implements WheneverBecomesTrueStatement
{
  /**
   * The cached value of the '{@link #getCause() <em>Cause</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getCause()
   * @generated
   * @ordered
   */
  protected Expr cause;

  /**
   * The cached value of the '{@link #getEffect() <em>Effect</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getEffect()
   * @generated
   * @ordered
   */
  protected Expr effect;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected WheneverBecomesTrueStatementImpl()
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
    return AgreePackage.Literals.WHENEVER_BECOMES_TRUE_STATEMENT;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Expr getCause()
  {
    return cause;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetCause(Expr newCause, NotificationChain msgs)
  {
    Expr oldCause = cause;
    cause = newCause;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, AgreePackage.WHENEVER_BECOMES_TRUE_STATEMENT__CAUSE, oldCause, newCause);
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
  public void setCause(Expr newCause)
  {
    if (newCause != cause)
    {
      NotificationChain msgs = null;
      if (cause != null)
        msgs = ((InternalEObject)cause).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - AgreePackage.WHENEVER_BECOMES_TRUE_STATEMENT__CAUSE, null, msgs);
      if (newCause != null)
        msgs = ((InternalEObject)newCause).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - AgreePackage.WHENEVER_BECOMES_TRUE_STATEMENT__CAUSE, null, msgs);
      msgs = basicSetCause(newCause, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, AgreePackage.WHENEVER_BECOMES_TRUE_STATEMENT__CAUSE, newCause, newCause));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Expr getEffect()
  {
    return effect;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetEffect(Expr newEffect, NotificationChain msgs)
  {
    Expr oldEffect = effect;
    effect = newEffect;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, AgreePackage.WHENEVER_BECOMES_TRUE_STATEMENT__EFFECT, oldEffect, newEffect);
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
  public void setEffect(Expr newEffect)
  {
    if (newEffect != effect)
    {
      NotificationChain msgs = null;
      if (effect != null)
        msgs = ((InternalEObject)effect).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - AgreePackage.WHENEVER_BECOMES_TRUE_STATEMENT__EFFECT, null, msgs);
      if (newEffect != null)
        msgs = ((InternalEObject)newEffect).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - AgreePackage.WHENEVER_BECOMES_TRUE_STATEMENT__EFFECT, null, msgs);
      msgs = basicSetEffect(newEffect, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, AgreePackage.WHENEVER_BECOMES_TRUE_STATEMENT__EFFECT, newEffect, newEffect));
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
      case AgreePackage.WHENEVER_BECOMES_TRUE_STATEMENT__CAUSE:
        return basicSetCause(null, msgs);
      case AgreePackage.WHENEVER_BECOMES_TRUE_STATEMENT__EFFECT:
        return basicSetEffect(null, msgs);
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
      case AgreePackage.WHENEVER_BECOMES_TRUE_STATEMENT__CAUSE:
        return getCause();
      case AgreePackage.WHENEVER_BECOMES_TRUE_STATEMENT__EFFECT:
        return getEffect();
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
      case AgreePackage.WHENEVER_BECOMES_TRUE_STATEMENT__CAUSE:
        setCause((Expr)newValue);
        return;
      case AgreePackage.WHENEVER_BECOMES_TRUE_STATEMENT__EFFECT:
        setEffect((Expr)newValue);
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
      case AgreePackage.WHENEVER_BECOMES_TRUE_STATEMENT__CAUSE:
        setCause((Expr)null);
        return;
      case AgreePackage.WHENEVER_BECOMES_TRUE_STATEMENT__EFFECT:
        setEffect((Expr)null);
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
      case AgreePackage.WHENEVER_BECOMES_TRUE_STATEMENT__CAUSE:
        return cause != null;
      case AgreePackage.WHENEVER_BECOMES_TRUE_STATEMENT__EFFECT:
        return effect != null;
    }
    return super.eIsSet(featureID);
  }

} //WheneverBecomesTrueStatementImpl
