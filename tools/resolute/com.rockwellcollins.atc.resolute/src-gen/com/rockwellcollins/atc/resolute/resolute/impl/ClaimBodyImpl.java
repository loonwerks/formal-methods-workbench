/**
 */
package com.rockwellcollins.atc.resolute.resolute.impl;

import com.rockwellcollins.atc.resolute.resolute.ClaimAssumption;
import com.rockwellcollins.atc.resolute.resolute.ClaimBody;
import com.rockwellcollins.atc.resolute.resolute.ClaimContext;
import com.rockwellcollins.atc.resolute.resolute.ClaimStrategy;
import com.rockwellcollins.atc.resolute.resolute.ClaimText;
import com.rockwellcollins.atc.resolute.resolute.ResolutePackage;

import java.util.Collection;

import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Claim Body</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link com.rockwellcollins.atc.resolute.resolute.impl.ClaimBodyImpl#getClaim <em>Claim</em>}</li>
 *   <li>{@link com.rockwellcollins.atc.resolute.resolute.impl.ClaimBodyImpl#getContext <em>Context</em>}</li>
 *   <li>{@link com.rockwellcollins.atc.resolute.resolute.impl.ClaimBodyImpl#getAssumptions <em>Assumptions</em>}</li>
 *   <li>{@link com.rockwellcollins.atc.resolute.resolute.impl.ClaimBodyImpl#getStrategies <em>Strategies</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ClaimBodyImpl extends DefinitionBodyImpl implements ClaimBody
{
  /**
   * The cached value of the '{@link #getClaim() <em>Claim</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getClaim()
   * @generated
   * @ordered
   */
  protected EList<ClaimText> claim;

  /**
   * The cached value of the '{@link #getContext() <em>Context</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getContext()
   * @generated
   * @ordered
   */
  protected EList<ClaimContext> context;

  /**
   * The cached value of the '{@link #getAssumptions() <em>Assumptions</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAssumptions()
   * @generated
   * @ordered
   */
  protected EList<ClaimAssumption> assumptions;

  /**
   * The cached value of the '{@link #getStrategies() <em>Strategies</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getStrategies()
   * @generated
   * @ordered
   */
  protected EList<ClaimStrategy> strategies;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ClaimBodyImpl()
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
    return ResolutePackage.Literals.CLAIM_BODY;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EList<ClaimText> getClaim()
  {
    if (claim == null)
    {
      claim = new EObjectContainmentEList<ClaimText>(ClaimText.class, this, ResolutePackage.CLAIM_BODY__CLAIM);
    }
    return claim;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EList<ClaimContext> getContext()
  {
    if (context == null)
    {
      context = new EObjectContainmentEList<ClaimContext>(ClaimContext.class, this, ResolutePackage.CLAIM_BODY__CONTEXT);
    }
    return context;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EList<ClaimAssumption> getAssumptions()
  {
    if (assumptions == null)
    {
      assumptions = new EObjectContainmentEList<ClaimAssumption>(ClaimAssumption.class, this, ResolutePackage.CLAIM_BODY__ASSUMPTIONS);
    }
    return assumptions;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EList<ClaimStrategy> getStrategies()
  {
    if (strategies == null)
    {
      strategies = new EObjectContainmentEList<ClaimStrategy>(ClaimStrategy.class, this, ResolutePackage.CLAIM_BODY__STRATEGIES);
    }
    return strategies;
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
      case ResolutePackage.CLAIM_BODY__CLAIM:
        return ((InternalEList<?>)getClaim()).basicRemove(otherEnd, msgs);
      case ResolutePackage.CLAIM_BODY__CONTEXT:
        return ((InternalEList<?>)getContext()).basicRemove(otherEnd, msgs);
      case ResolutePackage.CLAIM_BODY__ASSUMPTIONS:
        return ((InternalEList<?>)getAssumptions()).basicRemove(otherEnd, msgs);
      case ResolutePackage.CLAIM_BODY__STRATEGIES:
        return ((InternalEList<?>)getStrategies()).basicRemove(otherEnd, msgs);
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
      case ResolutePackage.CLAIM_BODY__CLAIM:
        return getClaim();
      case ResolutePackage.CLAIM_BODY__CONTEXT:
        return getContext();
      case ResolutePackage.CLAIM_BODY__ASSUMPTIONS:
        return getAssumptions();
      case ResolutePackage.CLAIM_BODY__STRATEGIES:
        return getStrategies();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
      case ResolutePackage.CLAIM_BODY__CLAIM:
        getClaim().clear();
        getClaim().addAll((Collection<? extends ClaimText>)newValue);
        return;
      case ResolutePackage.CLAIM_BODY__CONTEXT:
        getContext().clear();
        getContext().addAll((Collection<? extends ClaimContext>)newValue);
        return;
      case ResolutePackage.CLAIM_BODY__ASSUMPTIONS:
        getAssumptions().clear();
        getAssumptions().addAll((Collection<? extends ClaimAssumption>)newValue);
        return;
      case ResolutePackage.CLAIM_BODY__STRATEGIES:
        getStrategies().clear();
        getStrategies().addAll((Collection<? extends ClaimStrategy>)newValue);
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
      case ResolutePackage.CLAIM_BODY__CLAIM:
        getClaim().clear();
        return;
      case ResolutePackage.CLAIM_BODY__CONTEXT:
        getContext().clear();
        return;
      case ResolutePackage.CLAIM_BODY__ASSUMPTIONS:
        getAssumptions().clear();
        return;
      case ResolutePackage.CLAIM_BODY__STRATEGIES:
        getStrategies().clear();
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
      case ResolutePackage.CLAIM_BODY__CLAIM:
        return claim != null && !claim.isEmpty();
      case ResolutePackage.CLAIM_BODY__CONTEXT:
        return context != null && !context.isEmpty();
      case ResolutePackage.CLAIM_BODY__ASSUMPTIONS:
        return assumptions != null && !assumptions.isEmpty();
      case ResolutePackage.CLAIM_BODY__STRATEGIES:
        return strategies != null && !strategies.isEmpty();
    }
    return super.eIsSet(featureID);
  }

} //ClaimBodyImpl
