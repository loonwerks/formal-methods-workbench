/**
 */
package com.rockwellcollins.atc.resolute.resolute.impl;

import com.rockwellcollins.atc.resolute.resolute.LintExpr;
import com.rockwellcollins.atc.resolute.resolute.LintStatement;
import com.rockwellcollins.atc.resolute.resolute.ResolutePackage;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Lint Expr</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link com.rockwellcollins.atc.resolute.resolute.impl.LintExprImpl#getLintStmt <em>Lint Stmt</em>}</li>
 * </ul>
 *
 * @generated
 */
public class LintExprImpl extends ExprImpl implements LintExpr
{
  /**
   * The cached value of the '{@link #getLintStmt() <em>Lint Stmt</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getLintStmt()
   * @generated
   * @ordered
   */
  protected LintStatement lintStmt;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected LintExprImpl()
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
    return ResolutePackage.Literals.LINT_EXPR;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public LintStatement getLintStmt()
  {
    return lintStmt;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetLintStmt(LintStatement newLintStmt, NotificationChain msgs)
  {
    LintStatement oldLintStmt = lintStmt;
    lintStmt = newLintStmt;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, ResolutePackage.LINT_EXPR__LINT_STMT, oldLintStmt, newLintStmt);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setLintStmt(LintStatement newLintStmt)
  {
    if (newLintStmt != lintStmt)
    {
      NotificationChain msgs = null;
      if (lintStmt != null)
        msgs = ((InternalEObject)lintStmt).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - ResolutePackage.LINT_EXPR__LINT_STMT, null, msgs);
      if (newLintStmt != null)
        msgs = ((InternalEObject)newLintStmt).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - ResolutePackage.LINT_EXPR__LINT_STMT, null, msgs);
      msgs = basicSetLintStmt(newLintStmt, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, ResolutePackage.LINT_EXPR__LINT_STMT, newLintStmt, newLintStmt));
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
      case ResolutePackage.LINT_EXPR__LINT_STMT:
        return basicSetLintStmt(null, msgs);
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
      case ResolutePackage.LINT_EXPR__LINT_STMT:
        return getLintStmt();
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
      case ResolutePackage.LINT_EXPR__LINT_STMT:
        setLintStmt((LintStatement)newValue);
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
      case ResolutePackage.LINT_EXPR__LINT_STMT:
        setLintStmt((LintStatement)null);
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
      case ResolutePackage.LINT_EXPR__LINT_STMT:
        return lintStmt != null;
    }
    return super.eIsSet(featureID);
  }

} //LintExprImpl
