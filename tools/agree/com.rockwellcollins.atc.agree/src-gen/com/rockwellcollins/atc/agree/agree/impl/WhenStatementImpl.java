/**
 */
package com.rockwellcollins.atc.agree.agree.impl;

import com.rockwellcollins.atc.agree.agree.AgreePackage;
import com.rockwellcollins.atc.agree.agree.Expr;
import com.rockwellcollins.atc.agree.agree.WhenStatement;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>When Statement</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link com.rockwellcollins.atc.agree.agree.impl.WhenStatementImpl#getCauseCondition <em>Cause Condition</em>}</li>
 *   <li>{@link com.rockwellcollins.atc.agree.agree.impl.WhenStatementImpl#getExcl <em>Excl</em>}</li>
 * </ul>
 *
 * @generated
 */
public class WhenStatementImpl extends PatternStatementImpl implements WhenStatement
{
  /**
   * The cached value of the '{@link #getCauseCondition() <em>Cause Condition</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getCauseCondition()
   * @generated
   * @ordered
   */
  protected Expr causeCondition;

  /**
   * The default value of the '{@link #getExcl() <em>Excl</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getExcl()
   * @generated
   * @ordered
   */
  protected static final String EXCL_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getExcl() <em>Excl</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getExcl()
   * @generated
   * @ordered
   */
  protected String excl = EXCL_EDEFAULT;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected WhenStatementImpl()
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
    return AgreePackage.Literals.WHEN_STATEMENT;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Expr getCauseCondition()
  {
    return causeCondition;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetCauseCondition(Expr newCauseCondition, NotificationChain msgs)
  {
    Expr oldCauseCondition = causeCondition;
    causeCondition = newCauseCondition;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, AgreePackage.WHEN_STATEMENT__CAUSE_CONDITION, oldCauseCondition, newCauseCondition);
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
  public void setCauseCondition(Expr newCauseCondition)
  {
    if (newCauseCondition != causeCondition)
    {
      NotificationChain msgs = null;
      if (causeCondition != null)
        msgs = ((InternalEObject)causeCondition).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - AgreePackage.WHEN_STATEMENT__CAUSE_CONDITION, null, msgs);
      if (newCauseCondition != null)
        msgs = ((InternalEObject)newCauseCondition).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - AgreePackage.WHEN_STATEMENT__CAUSE_CONDITION, null, msgs);
      msgs = basicSetCauseCondition(newCauseCondition, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, AgreePackage.WHEN_STATEMENT__CAUSE_CONDITION, newCauseCondition, newCauseCondition));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getExcl()
  {
    return excl;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setExcl(String newExcl)
  {
    String oldExcl = excl;
    excl = newExcl;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, AgreePackage.WHEN_STATEMENT__EXCL, oldExcl, excl));
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
      case AgreePackage.WHEN_STATEMENT__CAUSE_CONDITION:
        return basicSetCauseCondition(null, msgs);
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
      case AgreePackage.WHEN_STATEMENT__CAUSE_CONDITION:
        return getCauseCondition();
      case AgreePackage.WHEN_STATEMENT__EXCL:
        return getExcl();
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
      case AgreePackage.WHEN_STATEMENT__CAUSE_CONDITION:
        setCauseCondition((Expr)newValue);
        return;
      case AgreePackage.WHEN_STATEMENT__EXCL:
        setExcl((String)newValue);
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
      case AgreePackage.WHEN_STATEMENT__CAUSE_CONDITION:
        setCauseCondition((Expr)null);
        return;
      case AgreePackage.WHEN_STATEMENT__EXCL:
        setExcl(EXCL_EDEFAULT);
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
      case AgreePackage.WHEN_STATEMENT__CAUSE_CONDITION:
        return causeCondition != null;
      case AgreePackage.WHEN_STATEMENT__EXCL:
        return EXCL_EDEFAULT == null ? excl != null : !EXCL_EDEFAULT.equals(excl);
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String toString()
  {
    if (eIsProxy()) return super.toString();

    StringBuilder result = new StringBuilder(super.toString());
    result.append(" (excl: ");
    result.append(excl);
    result.append(')');
    return result.toString();
  }

} //WhenStatementImpl
