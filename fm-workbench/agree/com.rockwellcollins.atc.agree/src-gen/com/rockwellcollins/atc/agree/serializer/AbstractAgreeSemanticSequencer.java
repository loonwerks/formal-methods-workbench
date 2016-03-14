/*
 * generated by Xtext
 */
package com.rockwellcollins.atc.agree.serializer;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.rockwellcollins.atc.agree.agree.AgreeContract;
import com.rockwellcollins.atc.agree.agree.AgreeContractLibrary;
import com.rockwellcollins.atc.agree.agree.AgreeContractSubclause;
import com.rockwellcollins.atc.agree.agree.AgreePackage;
import com.rockwellcollins.atc.agree.agree.AlwaysStatement;
import com.rockwellcollins.atc.agree.agree.Arg;
import com.rockwellcollins.atc.agree.agree.AssertStatement;
import com.rockwellcollins.atc.agree.agree.AssignStatement;
import com.rockwellcollins.atc.agree.agree.AssumeStatement;
import com.rockwellcollins.atc.agree.agree.AsynchStatement;
import com.rockwellcollins.atc.agree.agree.BinaryExpr;
import com.rockwellcollins.atc.agree.agree.BoolLitExpr;
import com.rockwellcollins.atc.agree.agree.CalenStatement;
import com.rockwellcollins.atc.agree.agree.ClosedTimeInterval;
import com.rockwellcollins.atc.agree.agree.ConnectionStatement;
import com.rockwellcollins.atc.agree.agree.ConstStatement;
import com.rockwellcollins.atc.agree.agree.EqStatement;
import com.rockwellcollins.atc.agree.agree.EventExpr;
import com.rockwellcollins.atc.agree.agree.FloorCast;
import com.rockwellcollins.atc.agree.agree.FnCallExpr;
import com.rockwellcollins.atc.agree.agree.FnDefExpr;
import com.rockwellcollins.atc.agree.agree.GetPropertyExpr;
import com.rockwellcollins.atc.agree.agree.GuaranteeStatement;
import com.rockwellcollins.atc.agree.agree.IfThenElseExpr;
import com.rockwellcollins.atc.agree.agree.InitialStatement;
import com.rockwellcollins.atc.agree.agree.InputStatement;
import com.rockwellcollins.atc.agree.agree.IntLitExpr;
import com.rockwellcollins.atc.agree.agree.LatchedStatement;
import com.rockwellcollins.atc.agree.agree.LemmaStatement;
import com.rockwellcollins.atc.agree.agree.LiftStatement;
import com.rockwellcollins.atc.agree.agree.MNSynchStatement;
import com.rockwellcollins.atc.agree.agree.NestedDotID;
import com.rockwellcollins.atc.agree.agree.NodeBodyExpr;
import com.rockwellcollins.atc.agree.agree.NodeDefExpr;
import com.rockwellcollins.atc.agree.agree.NodeEq;
import com.rockwellcollins.atc.agree.agree.NodeLemma;
import com.rockwellcollins.atc.agree.agree.OpenLeftTimeInterval;
import com.rockwellcollins.atc.agree.agree.OpenRightTimeInterval;
import com.rockwellcollins.atc.agree.agree.OpenTimeInterval;
import com.rockwellcollins.atc.agree.agree.OrderStatement;
import com.rockwellcollins.atc.agree.agree.ParamStatement;
import com.rockwellcollins.atc.agree.agree.PeriodicStatement;
import com.rockwellcollins.atc.agree.agree.PreExpr;
import com.rockwellcollins.atc.agree.agree.PrevExpr;
import com.rockwellcollins.atc.agree.agree.PrimType;
import com.rockwellcollins.atc.agree.agree.PropertyStatement;
import com.rockwellcollins.atc.agree.agree.RealCast;
import com.rockwellcollins.atc.agree.agree.RealLitExpr;
import com.rockwellcollins.atc.agree.agree.RecordDefExpr;
import com.rockwellcollins.atc.agree.agree.RecordExpr;
import com.rockwellcollins.atc.agree.agree.RecordType;
import com.rockwellcollins.atc.agree.agree.RecordUpdateExpr;
import com.rockwellcollins.atc.agree.agree.SporadicStatement;
import com.rockwellcollins.atc.agree.agree.SynchStatement;
import com.rockwellcollins.atc.agree.agree.ThisExpr;
import com.rockwellcollins.atc.agree.agree.TimeExpr;
import com.rockwellcollins.atc.agree.agree.UnaryExpr;
import com.rockwellcollins.atc.agree.agree.WhenHoldsStatement;
import com.rockwellcollins.atc.agree.agree.WhenOccursStatment;
import com.rockwellcollins.atc.agree.agree.WheneverBecomesTrueStatement;
import com.rockwellcollins.atc.agree.agree.WheneverHoldsStatement;
import com.rockwellcollins.atc.agree.agree.WheneverImpliesStatement;
import com.rockwellcollins.atc.agree.agree.WheneverOccursStatement;
import com.rockwellcollins.atc.agree.services.AgreeGrammarAccess;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.serializer.acceptor.ISemanticSequenceAcceptor;
import org.eclipse.xtext.serializer.acceptor.SequenceFeeder;
import org.eclipse.xtext.serializer.diagnostic.ISemanticSequencerDiagnosticProvider;
import org.eclipse.xtext.serializer.diagnostic.ISerializationDiagnostic.Acceptor;
import org.eclipse.xtext.serializer.sequencer.GenericSequencer;
import org.eclipse.xtext.serializer.sequencer.ISemanticNodeProvider.INodesForEObjectProvider;
import org.eclipse.xtext.serializer.sequencer.ISemanticSequencer;
import org.eclipse.xtext.serializer.sequencer.ITransientValueService;
import org.eclipse.xtext.serializer.sequencer.ITransientValueService.ValueTransient;
import org.osate.aadl2.Aadl2Package;
import org.osate.aadl2.ArrayRange;
import org.osate.aadl2.BasicPropertyAssociation;
import org.osate.aadl2.BooleanLiteral;
import org.osate.aadl2.ClassifierValue;
import org.osate.aadl2.ComputedValue;
import org.osate.aadl2.ContainedNamedElement;
import org.osate.aadl2.ContainmentPathElement;
import org.osate.aadl2.IntegerLiteral;
import org.osate.aadl2.ListValue;
import org.osate.aadl2.ModalPropertyValue;
import org.osate.aadl2.NamedValue;
import org.osate.aadl2.Operation;
import org.osate.aadl2.PropertyAssociation;
import org.osate.aadl2.RangeValue;
import org.osate.aadl2.RealLiteral;
import org.osate.aadl2.RecordValue;
import org.osate.aadl2.ReferenceValue;
import org.osate.aadl2.StringLiteral;
import org.osate.xtext.aadl2.properties.serializer.PropertiesSemanticSequencer;

@SuppressWarnings("all")
public abstract class AbstractAgreeSemanticSequencer extends PropertiesSemanticSequencer {

	@Inject
	private AgreeGrammarAccess grammarAccess;
	
	@Override
	public void createSequence(EObject context, EObject semanticObject) {
		if(semanticObject.eClass().getEPackage() == Aadl2Package.eINSTANCE) switch(semanticObject.eClass().getClassifierID()) {
			case Aadl2Package.ARRAY_RANGE:
				sequence_ArrayRange(context, (ArrayRange) semanticObject); 
				return; 
			case Aadl2Package.BASIC_PROPERTY_ASSOCIATION:
				sequence_FieldPropertyAssociation(context, (BasicPropertyAssociation) semanticObject); 
				return; 
			case Aadl2Package.BOOLEAN_LITERAL:
				sequence_BooleanLiteral(context, (BooleanLiteral) semanticObject); 
				return; 
			case Aadl2Package.CLASSIFIER_VALUE:
				sequence_ComponentClassifierTerm(context, (ClassifierValue) semanticObject); 
				return; 
			case Aadl2Package.COMPUTED_VALUE:
				sequence_ComputedTerm(context, (ComputedValue) semanticObject); 
				return; 
			case Aadl2Package.CONTAINED_NAMED_ELEMENT:
				sequence_ContainmentPath(context, (ContainedNamedElement) semanticObject); 
				return; 
			case Aadl2Package.CONTAINMENT_PATH_ELEMENT:
				sequence_ContainmentPathElement(context, (ContainmentPathElement) semanticObject); 
				return; 
			case Aadl2Package.INTEGER_LITERAL:
				sequence_IntegerTerm(context, (IntegerLiteral) semanticObject); 
				return; 
			case Aadl2Package.LIST_VALUE:
				sequence_ListTerm(context, (ListValue) semanticObject); 
				return; 
			case Aadl2Package.MODAL_PROPERTY_VALUE:
				if(context == grammarAccess.getModalPropertyValueRule()) {
					sequence_ModalPropertyValue(context, (ModalPropertyValue) semanticObject); 
					return; 
				}
				else if(context == grammarAccess.getOptionalModalPropertyValueRule()) {
					sequence_OptionalModalPropertyValue(context, (ModalPropertyValue) semanticObject); 
					return; 
				}
				else if(context == grammarAccess.getPropertyValueRule()) {
					sequence_PropertyValue(context, (ModalPropertyValue) semanticObject); 
					return; 
				}
				else break;
			case Aadl2Package.NAMED_VALUE:
				if(context == grammarAccess.getConstantValueRule() ||
				   context == grammarAccess.getNumAltRule()) {
					sequence_ConstantValue(context, (NamedValue) semanticObject); 
					return; 
				}
				else if(context == grammarAccess.getLiteralorReferenceTermRule() ||
				   context == grammarAccess.getPropertyExpressionRule()) {
					sequence_LiteralorReferenceTerm(context, (NamedValue) semanticObject); 
					return; 
				}
				else break;
			case Aadl2Package.OPERATION:
				sequence_SignedConstant(context, (Operation) semanticObject); 
				return; 
			case Aadl2Package.PROPERTY_ASSOCIATION:
				if(context == grammarAccess.getBasicPropertyAssociationRule()) {
					sequence_BasicPropertyAssociation(context, (PropertyAssociation) semanticObject); 
					return; 
				}
				else if(context == grammarAccess.getContainedPropertyAssociationRule() ||
				   context == grammarAccess.getPModelRule()) {
					sequence_ContainedPropertyAssociation(context, (PropertyAssociation) semanticObject); 
					return; 
				}
				else if(context == grammarAccess.getPropertyAssociationRule()) {
					sequence_PropertyAssociation(context, (PropertyAssociation) semanticObject); 
					return; 
				}
				else break;
			case Aadl2Package.RANGE_VALUE:
				sequence_NumericRangeTerm(context, (RangeValue) semanticObject); 
				return; 
			case Aadl2Package.REAL_LITERAL:
				sequence_RealTerm(context, (RealLiteral) semanticObject); 
				return; 
			case Aadl2Package.RECORD_VALUE:
				if(context == grammarAccess.getOldRecordTermRule()) {
					sequence_OldRecordTerm(context, (RecordValue) semanticObject); 
					return; 
				}
				else if(context == grammarAccess.getPropertyExpressionRule() ||
				   context == grammarAccess.getRecordTermRule()) {
					sequence_RecordTerm(context, (RecordValue) semanticObject); 
					return; 
				}
				else break;
			case Aadl2Package.REFERENCE_VALUE:
				sequence_ReferenceTerm(context, (ReferenceValue) semanticObject); 
				return; 
			case Aadl2Package.STRING_LITERAL:
				sequence_StringTerm(context, (StringLiteral) semanticObject); 
				return; 
			}
		else if(semanticObject.eClass().getEPackage() == AgreePackage.eINSTANCE) switch(semanticObject.eClass().getClassifierID()) {
			case AgreePackage.AGREE_CONTRACT:
				sequence_AgreeContract(context, (AgreeContract) semanticObject); 
				return; 
			case AgreePackage.AGREE_CONTRACT_LIBRARY:
				sequence_AgreeLibrary(context, (AgreeContractLibrary) semanticObject); 
				return; 
			case AgreePackage.AGREE_CONTRACT_SUBCLAUSE:
				sequence_AgreeSubclause(context, (AgreeContractSubclause) semanticObject); 
				return; 
			case AgreePackage.ALWAYS_STATEMENT:
				sequence_PatternStatement(context, (AlwaysStatement) semanticObject); 
				return; 
			case AgreePackage.ARG:
				sequence_Arg(context, (Arg) semanticObject); 
				return; 
			case AgreePackage.ASSERT_STATEMENT:
				sequence_SpecStatement(context, (AssertStatement) semanticObject); 
				return; 
			case AgreePackage.ASSIGN_STATEMENT:
				sequence_AssignStatement(context, (AssignStatement) semanticObject); 
				return; 
			case AgreePackage.ASSUME_STATEMENT:
				sequence_SpecStatement(context, (AssumeStatement) semanticObject); 
				return; 
			case AgreePackage.ASYNCH_STATEMENT:
				sequence_SynchStatement(context, (AsynchStatement) semanticObject); 
				return; 
			case AgreePackage.BINARY_EXPR:
				sequence_AddSubExpr_AndExpr_ArrowExpr_EquivExpr_ImpliesExpr_MultDivExpr_OrExpr_RelateExpr(context, (BinaryExpr) semanticObject); 
				return; 
			case AgreePackage.BOOL_LIT_EXPR:
				sequence_TermExpr(context, (BoolLitExpr) semanticObject); 
				return; 
			case AgreePackage.CALEN_STATEMENT:
				sequence_SynchStatement(context, (CalenStatement) semanticObject); 
				return; 
			case AgreePackage.CLOSED_TIME_INTERVAL:
				sequence_TimeInterval(context, (ClosedTimeInterval) semanticObject); 
				return; 
			case AgreePackage.CONNECTION_STATEMENT:
				sequence_SpecStatement(context, (ConnectionStatement) semanticObject); 
				return; 
			case AgreePackage.CONST_STATEMENT:
				sequence_ConstStatement(context, (ConstStatement) semanticObject); 
				return; 
			case AgreePackage.EQ_STATEMENT:
				sequence_EqStatement(context, (EqStatement) semanticObject); 
				return; 
			case AgreePackage.EVENT_EXPR:
				sequence_TermExpr(context, (EventExpr) semanticObject); 
				return; 
			case AgreePackage.FLOOR_CAST:
				sequence_TermExpr(context, (FloorCast) semanticObject); 
				return; 
			case AgreePackage.FN_CALL_EXPR:
				sequence_ComplexExpr(context, (FnCallExpr) semanticObject); 
				return; 
			case AgreePackage.FN_DEF_EXPR:
				sequence_FnDefExpr(context, (FnDefExpr) semanticObject); 
				return; 
			case AgreePackage.GET_PROPERTY_EXPR:
				sequence_PreDefFnExpr(context, (GetPropertyExpr) semanticObject); 
				return; 
			case AgreePackage.GUARANTEE_STATEMENT:
				sequence_SpecStatement(context, (GuaranteeStatement) semanticObject); 
				return; 
			case AgreePackage.IF_THEN_ELSE_EXPR:
				sequence_IfThenElseExpr(context, (IfThenElseExpr) semanticObject); 
				return; 
			case AgreePackage.INITIAL_STATEMENT:
				sequence_SpecStatement(context, (InitialStatement) semanticObject); 
				return; 
			case AgreePackage.INPUT_STATEMENT:
				sequence_InputStatement(context, (InputStatement) semanticObject); 
				return; 
			case AgreePackage.INT_LIT_EXPR:
				sequence_TermExpr(context, (IntLitExpr) semanticObject); 
				return; 
			case AgreePackage.LATCHED_STATEMENT:
				sequence_SynchStatement(context, (LatchedStatement) semanticObject); 
				return; 
			case AgreePackage.LEMMA_STATEMENT:
				sequence_SpecStatement(context, (LemmaStatement) semanticObject); 
				return; 
			case AgreePackage.LIFT_STATEMENT:
				sequence_SpecStatement(context, (LiftStatement) semanticObject); 
				return; 
			case AgreePackage.MN_SYNCH_STATEMENT:
				sequence_SynchStatement(context, (MNSynchStatement) semanticObject); 
				return; 
			case AgreePackage.NESTED_DOT_ID:
				sequence_NestedDotID(context, (NestedDotID) semanticObject); 
				return; 
			case AgreePackage.NODE_BODY_EXPR:
				sequence_NodeBodyExpr(context, (NodeBodyExpr) semanticObject); 
				return; 
			case AgreePackage.NODE_DEF_EXPR:
				sequence_NodeDefExpr(context, (NodeDefExpr) semanticObject); 
				return; 
			case AgreePackage.NODE_EQ:
				sequence_NodeStmt(context, (NodeEq) semanticObject); 
				return; 
			case AgreePackage.NODE_LEMMA:
				sequence_NodeStmt(context, (NodeLemma) semanticObject); 
				return; 
			case AgreePackage.OPEN_LEFT_TIME_INTERVAL:
				sequence_TimeInterval(context, (OpenLeftTimeInterval) semanticObject); 
				return; 
			case AgreePackage.OPEN_RIGHT_TIME_INTERVAL:
				sequence_TimeInterval(context, (OpenRightTimeInterval) semanticObject); 
				return; 
			case AgreePackage.OPEN_TIME_INTERVAL:
				sequence_TimeInterval(context, (OpenTimeInterval) semanticObject); 
				return; 
			case AgreePackage.ORDER_STATEMENT:
				sequence_OrderStatement(context, (OrderStatement) semanticObject); 
				return; 
			case AgreePackage.PARAM_STATEMENT:
				sequence_SpecStatement(context, (ParamStatement) semanticObject); 
				return; 
			case AgreePackage.PERIODIC_STATEMENT:
				sequence_RealTimeStatement(context, (PeriodicStatement) semanticObject); 
				return; 
			case AgreePackage.PRE_EXPR:
				sequence_TermExpr(context, (PreExpr) semanticObject); 
				return; 
			case AgreePackage.PREV_EXPR:
				sequence_PreDefFnExpr(context, (PrevExpr) semanticObject); 
				return; 
			case AgreePackage.PRIM_TYPE:
				sequence_Type(context, (PrimType) semanticObject); 
				return; 
			case AgreePackage.PROPERTY_STATEMENT:
				sequence_PropertyStatement(context, (PropertyStatement) semanticObject); 
				return; 
			case AgreePackage.REAL_CAST:
				sequence_TermExpr(context, (RealCast) semanticObject); 
				return; 
			case AgreePackage.REAL_LIT_EXPR:
				sequence_TermExpr(context, (RealLitExpr) semanticObject); 
				return; 
			case AgreePackage.RECORD_DEF_EXPR:
				sequence_RecordDefExpr(context, (RecordDefExpr) semanticObject); 
				return; 
			case AgreePackage.RECORD_EXPR:
				sequence_ComplexExpr(context, (RecordExpr) semanticObject); 
				return; 
			case AgreePackage.RECORD_TYPE:
				sequence_Type(context, (RecordType) semanticObject); 
				return; 
			case AgreePackage.RECORD_UPDATE_EXPR:
				sequence_RecordUpdateExpr(context, (RecordUpdateExpr) semanticObject); 
				return; 
			case AgreePackage.SPORADIC_STATEMENT:
				sequence_RealTimeStatement(context, (SporadicStatement) semanticObject); 
				return; 
			case AgreePackage.SYNCH_STATEMENT:
				sequence_SynchStatement(context, (SynchStatement) semanticObject); 
				return; 
			case AgreePackage.THIS_EXPR:
				sequence_TermExpr(context, (ThisExpr) semanticObject); 
				return; 
			case AgreePackage.TIME_EXPR:
				sequence_TermExpr(context, (TimeExpr) semanticObject); 
				return; 
			case AgreePackage.UNARY_EXPR:
				sequence_UnaryExpr(context, (UnaryExpr) semanticObject); 
				return; 
			case AgreePackage.WHEN_HOLDS_STATEMENT:
				sequence_WhenStatement(context, (WhenHoldsStatement) semanticObject); 
				return; 
			case AgreePackage.WHEN_OCCURS_STATMENT:
				sequence_WhenStatement(context, (WhenOccursStatment) semanticObject); 
				return; 
			case AgreePackage.WHENEVER_BECOMES_TRUE_STATEMENT:
				sequence_WheneverStatement(context, (WheneverBecomesTrueStatement) semanticObject); 
				return; 
			case AgreePackage.WHENEVER_HOLDS_STATEMENT:
				sequence_WheneverStatement(context, (WheneverHoldsStatement) semanticObject); 
				return; 
			case AgreePackage.WHENEVER_IMPLIES_STATEMENT:
				sequence_WheneverStatement(context, (WheneverImpliesStatement) semanticObject); 
				return; 
			case AgreePackage.WHENEVER_OCCURS_STATEMENT:
				sequence_WheneverStatement(context, (WheneverOccursStatement) semanticObject); 
				return; 
			}
		if (errorAcceptor != null) errorAcceptor.accept(diagnosticProvider.createInvalidContextOrTypeDiagnostic(semanticObject, context));
	}
	
	/**
	 * Constraint:
	 *     (
	 *         (left=AddSubExpr_BinaryExpr_1_0_0_0 (op='+' | op='-') right=MultDivExpr) | 
	 *         (left=MultDivExpr_BinaryExpr_1_0_0_0 (op='*' | op='/' | op='div' | op='mod') right=UnaryExpr) | 
	 *         (left=RelateExpr_BinaryExpr_1_0_0_0 op=RelateOp right=AddSubExpr) | 
	 *         (left=AndExpr_BinaryExpr_1_0_0_0 op='and' right=RelateExpr) | 
	 *         (left=OrExpr_BinaryExpr_1_0_0_0 op='or' right=AndExpr) | 
	 *         (left=EquivExpr_BinaryExpr_1_0_0_0 op='<=>' right=OrExpr) | 
	 *         (left=ImpliesExpr_BinaryExpr_1_0_0_0 op='=>' right=ImpliesExpr) | 
	 *         (left=ArrowExpr_BinaryExpr_1_0_0_0 op='->' right=ArrowExpr)
	 *     )
	 */
	protected void sequence_AddSubExpr_AndExpr_ArrowExpr_EquivExpr_ImpliesExpr_MultDivExpr_OrExpr_RelateExpr(EObject context, BinaryExpr semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     specs+=SpecStatement+
	 */
	protected void sequence_AgreeContract(EObject context, AgreeContract semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     contract=AgreeContract
	 */
	protected void sequence_AgreeLibrary(EObject context, AgreeContractLibrary semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     contract=AgreeContract
	 */
	protected void sequence_AgreeSubclause(EObject context, AgreeContractSubclause semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (name=ID type=Type)
	 */
	protected void sequence_Arg(EObject context, Arg semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (id=NestedDotID expr=Expr)
	 */
	protected void sequence_AssignStatement(EObject context, AssignStatement semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     ((fn=ComplexExpr_FnCallExpr_1_0_0_0_0 (args+=Expr args+=Expr*)?) | fn=ComplexExpr_FnCallExpr_1_0_0_0_0)
	 */
	protected void sequence_ComplexExpr(EObject context, FnCallExpr semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (record=ComplexExpr_RecordExpr_1_1_0_0_0 args+=[NamedElement|ID] argExpr+=Expr (args+=[NamedElement|ID] argExpr+=Expr)*)
	 */
	protected void sequence_ComplexExpr(EObject context, RecordExpr semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (name=ID type=Type expr=Expr)
	 */
	protected void sequence_ConstStatement(EObject context, ConstStatement semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (lhs+=Arg lhs+=Arg* expr=Expr?)
	 */
	protected void sequence_EqStatement(EObject context, EqStatement semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (name=ID args+=Arg args+=Arg* type=Type expr=Expr)
	 */
	protected void sequence_FnDefExpr(EObject context, FnDefExpr semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (a=Expr b=Expr c=Expr)
	 */
	protected void sequence_IfThenElseExpr(EObject context, IfThenElseExpr semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (lhs+=Arg lhs+=Arg*)
	 */
	protected void sequence_InputStatement(EObject context, InputStatement semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (base=[NamedElement|QCPREF] (tag=ReservedVarTag | sub=NestedDotID)?)
	 */
	protected void sequence_NestedDotID(EObject context, NestedDotID semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (locs+=Arg* stmts+=NodeStmt+)
	 */
	protected void sequence_NodeBodyExpr(EObject context, NodeBodyExpr semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (name=ID (args+=Arg args+=Arg*)? (rets+=Arg rets+=Arg*)? nodeBody=NodeBodyExpr)
	 */
	protected void sequence_NodeDefExpr(EObject context, NodeDefExpr semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (lhs+=[Arg|ID] lhs+=[Arg|ID]* expr=Expr)
	 */
	protected void sequence_NodeStmt(EObject context, NodeEq semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (str=STRING expr=Expr)
	 */
	protected void sequence_NodeStmt(EObject context, NodeLemma semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (comps+=[NamedElement|ID] comps+=[NamedElement|ID]*)
	 */
	protected void sequence_OrderStatement(EObject context, OrderStatement semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     expr=Expr
	 */
	protected void sequence_PatternStatement(EObject context, AlwaysStatement semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, AgreePackage.Literals.ALWAYS_STATEMENT__EXPR) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, AgreePackage.Literals.ALWAYS_STATEMENT__EXPR));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getPatternStatementAccess().getExprExprParserRuleCall_1_2_0(), semanticObject.getExpr());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     (component=Expr prop=[NamedElement|QCLREF])
	 */
	protected void sequence_PreDefFnExpr(EObject context, GetPropertyExpr semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (delay=Expr init=Expr)
	 */
	protected void sequence_PreDefFnExpr(EObject context, PrevExpr semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (name=ID expr=Expr)
	 */
	protected void sequence_PropertyStatement(EObject context, PropertyStatement semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (event=Expr period=Expr jitter=Expr?)
	 */
	protected void sequence_RealTimeStatement(EObject context, PeriodicStatement semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (event=Expr iat=Expr jitter=Expr?)
	 */
	protected void sequence_RealTimeStatement(EObject context, SporadicStatement semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (name=ID args+=Arg args+=Arg*)
	 */
	protected void sequence_RecordDefExpr(EObject context, RecordDefExpr semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (record=RecordUpdateExpr_RecordUpdateExpr_1_0_0 (args+=[NamedElement|ID] argExpr+=Expr)+)
	 */
	protected void sequence_RecordUpdateExpr(EObject context, RecordUpdateExpr semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (str=STRING? (expr=Expr | pattern=PatternStatement))
	 */
	protected void sequence_SpecStatement(EObject context, AssertStatement semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (str=STRING (expr=Expr | pattern=PatternStatement))
	 */
	protected void sequence_SpecStatement(EObject context, AssumeStatement semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (conn=[NamedElement|ID] expr=Expr)
	 */
	protected void sequence_SpecStatement(EObject context, ConnectionStatement semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (str=STRING (expr=Expr | pattern=PatternStatement))
	 */
	protected void sequence_SpecStatement(EObject context, GuaranteeStatement semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     expr=Expr
	 */
	protected void sequence_SpecStatement(EObject context, InitialStatement semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (str=STRING expr=Expr)
	 */
	protected void sequence_SpecStatement(EObject context, LemmaStatement semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     subcomp=NestedDotID
	 */
	protected void sequence_SpecStatement(EObject context, LiftStatement semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (expr=Expr type=Type)
	 */
	protected void sequence_SpecStatement(EObject context, ParamStatement semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     {AsynchStatement}
	 */
	protected void sequence_SynchStatement(EObject context, AsynchStatement semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (els+=[NamedElement|ID] els+=[NamedElement|ID]*)
	 */
	protected void sequence_SynchStatement(EObject context, CalenStatement semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     {LatchedStatement}
	 */
	protected void sequence_SynchStatement(EObject context, LatchedStatement semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (comp1+=[NamedElement|ID] comp2+=[NamedElement|ID] max+=INTEGER_LIT min+=INTEGER_LIT)+
	 */
	protected void sequence_SynchStatement(EObject context, MNSynchStatement semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (val=INTEGER_LIT val2=INTEGER_LIT? (sim='simult' | sim='no_simult')?)
	 */
	protected void sequence_SynchStatement(EObject context, SynchStatement semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     val=BooleanLiteral
	 */
	protected void sequence_TermExpr(EObject context, BoolLitExpr semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     id=NestedDotID
	 */
	protected void sequence_TermExpr(EObject context, EventExpr semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     expr=Expr
	 */
	protected void sequence_TermExpr(EObject context, FloorCast semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     val=INTEGER_LIT
	 */
	protected void sequence_TermExpr(EObject context, IntLitExpr semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     expr=Expr
	 */
	protected void sequence_TermExpr(EObject context, PreExpr semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     expr=Expr
	 */
	protected void sequence_TermExpr(EObject context, RealCast semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     val=REAL_LIT
	 */
	protected void sequence_TermExpr(EObject context, RealLitExpr semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (subThis=NestedDotID?)
	 */
	protected void sequence_TermExpr(EObject context, ThisExpr semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     {TimeExpr}
	 */
	protected void sequence_TermExpr(EObject context, TimeExpr semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (low=Expr high=Expr)
	 */
	protected void sequence_TimeInterval(EObject context, ClosedTimeInterval semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, AgreePackage.Literals.TIME_INTERVAL__LOW) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, AgreePackage.Literals.TIME_INTERVAL__LOW));
			if(transientValues.isValueTransient(semanticObject, AgreePackage.Literals.TIME_INTERVAL__HIGH) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, AgreePackage.Literals.TIME_INTERVAL__HIGH));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getTimeIntervalAccess().getLowExprParserRuleCall_0_0_2_0(), semanticObject.getLow());
		feeder.accept(grammarAccess.getTimeIntervalAccess().getHighExprParserRuleCall_0_0_4_0(), semanticObject.getHigh());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     (low=Expr high=Expr)
	 */
	protected void sequence_TimeInterval(EObject context, OpenLeftTimeInterval semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, AgreePackage.Literals.TIME_INTERVAL__LOW) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, AgreePackage.Literals.TIME_INTERVAL__LOW));
			if(transientValues.isValueTransient(semanticObject, AgreePackage.Literals.TIME_INTERVAL__HIGH) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, AgreePackage.Literals.TIME_INTERVAL__HIGH));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getTimeIntervalAccess().getLowExprParserRuleCall_1_0_2_0(), semanticObject.getLow());
		feeder.accept(grammarAccess.getTimeIntervalAccess().getHighExprParserRuleCall_1_0_4_0(), semanticObject.getHigh());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     (low=Expr high=Expr)
	 */
	protected void sequence_TimeInterval(EObject context, OpenRightTimeInterval semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, AgreePackage.Literals.TIME_INTERVAL__LOW) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, AgreePackage.Literals.TIME_INTERVAL__LOW));
			if(transientValues.isValueTransient(semanticObject, AgreePackage.Literals.TIME_INTERVAL__HIGH) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, AgreePackage.Literals.TIME_INTERVAL__HIGH));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getTimeIntervalAccess().getLowExprParserRuleCall_2_0_2_0(), semanticObject.getLow());
		feeder.accept(grammarAccess.getTimeIntervalAccess().getHighExprParserRuleCall_2_0_4_0(), semanticObject.getHigh());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     (low=Expr high=Expr)
	 */
	protected void sequence_TimeInterval(EObject context, OpenTimeInterval semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, AgreePackage.Literals.TIME_INTERVAL__LOW) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, AgreePackage.Literals.TIME_INTERVAL__LOW));
			if(transientValues.isValueTransient(semanticObject, AgreePackage.Literals.TIME_INTERVAL__HIGH) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, AgreePackage.Literals.TIME_INTERVAL__HIGH));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getTimeIntervalAccess().getLowExprParserRuleCall_3_0_2_0(), semanticObject.getLow());
		feeder.accept(grammarAccess.getTimeIntervalAccess().getHighExprParserRuleCall_3_0_4_0(), semanticObject.getHigh());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     (string=primTypes (lowNeg='-'? (rangeLow=INTEGER_LIT | rangeLow=REAL_LIT) highNeg='-'? (rangeHigh=INTEGER_LIT | rangeHigh=REAL_LIT))?)
	 */
	protected void sequence_Type(EObject context, PrimType semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     record=NestedDotID
	 */
	protected void sequence_Type(EObject context, RecordType semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     ((op='-' | op='not') expr=UnaryExpr)
	 */
	protected void sequence_UnaryExpr(EObject context, UnaryExpr semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (condition=Expr conditionInterval=TimeInterval event=Expr excl='exclusively'? eventInterval=TimeInterval?)
	 */
	protected void sequence_WhenStatement(EObject context, WhenHoldsStatement semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (condition=Expr times=Expr interval=TimeInterval excl='exclusively'? event=Expr)
	 */
	protected void sequence_WhenStatement(EObject context, WhenOccursStatment semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (cause=Expr effect=Expr excl='exclusively'? interval=TimeInterval?)
	 */
	protected void sequence_WheneverStatement(EObject context, WheneverBecomesTrueStatement semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (cause=Expr effect=Expr excl='exclusively'? interval=TimeInterval?)
	 */
	protected void sequence_WheneverStatement(EObject context, WheneverHoldsStatement semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (cause=Expr lhs=Expr rhs=Expr excl='exclusively'? interval=TimeInterval?)
	 */
	protected void sequence_WheneverStatement(EObject context, WheneverImpliesStatement semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (cause=Expr effect=Expr excl='exclusively'? interval=TimeInterval?)
	 */
	protected void sequence_WheneverStatement(EObject context, WheneverOccursStatement semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
}
