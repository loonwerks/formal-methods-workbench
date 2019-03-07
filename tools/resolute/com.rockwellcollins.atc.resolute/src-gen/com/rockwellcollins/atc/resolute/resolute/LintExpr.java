/**
 */
package com.rockwellcollins.atc.resolute.resolute;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Lint Expr</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link com.rockwellcollins.atc.resolute.resolute.LintExpr#getLintStmt <em>Lint Stmt</em>}</li>
 * </ul>
 *
 * @see com.rockwellcollins.atc.resolute.resolute.ResolutePackage#getLintExpr()
 * @model
 * @generated
 */
public interface LintExpr extends Expr
{
  /**
   * Returns the value of the '<em><b>Lint Stmt</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Lint Stmt</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Lint Stmt</em>' containment reference.
   * @see #setLintStmt(LintStatement)
   * @see com.rockwellcollins.atc.resolute.resolute.ResolutePackage#getLintExpr_LintStmt()
   * @model containment="true"
   * @generated
   */
  LintStatement getLintStmt();

  /**
   * Sets the value of the '{@link com.rockwellcollins.atc.resolute.resolute.LintExpr#getLintStmt <em>Lint Stmt</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Lint Stmt</em>' containment reference.
   * @see #getLintStmt()
   * @generated
   */
  void setLintStmt(LintStatement value);

} // LintExpr
