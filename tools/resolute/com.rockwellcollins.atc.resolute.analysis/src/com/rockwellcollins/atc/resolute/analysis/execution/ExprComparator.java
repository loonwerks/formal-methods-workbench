package com.rockwellcollins.atc.resolute.analysis.execution;

import org.eclipse.emf.common.util.EList;

import com.rockwellcollins.atc.resolute.resolute.BinaryExpr;
import com.rockwellcollins.atc.resolute.resolute.BoolExpr;
import com.rockwellcollins.atc.resolute.resolute.BuiltInFnCallExpr;
import com.rockwellcollins.atc.resolute.resolute.Expr;
import com.rockwellcollins.atc.resolute.resolute.FailExpr;
import com.rockwellcollins.atc.resolute.resolute.FnCallExpr;
import com.rockwellcollins.atc.resolute.resolute.IdExpr;
import com.rockwellcollins.atc.resolute.resolute.IfThenElseExpr;
import com.rockwellcollins.atc.resolute.resolute.IntExpr;
import com.rockwellcollins.atc.resolute.resolute.LibraryFnCallExpr;
import com.rockwellcollins.atc.resolute.resolute.QuantifiedExpr;
import com.rockwellcollins.atc.resolute.resolute.RealExpr;
import com.rockwellcollins.atc.resolute.resolute.StringExpr;
import com.rockwellcollins.atc.resolute.resolute.ThisExpr;

public class ExprComparator {

	public static boolean compare(Expr expr0, Expr expr1) {

		try {

			if (expr0 instanceof BinaryExpr && expr1 instanceof BinaryExpr) {
				BinaryExpr e0 = (BinaryExpr) expr0;
				BinaryExpr e1 = (BinaryExpr) expr1;
				return compare(e0.getLeft(), e1.getLeft()) & e0.getOp().equals(e1.getOp())
						& compare(e0.getRight(), e1.getRight());
			} else if (expr0 instanceof IdExpr && expr1 instanceof IdExpr) {
				IdExpr e0 = (IdExpr) expr0;
				IdExpr e1 = (IdExpr) expr1;
				return e0.getId().getName().contentEquals(e1.getId().getName());
			} else if (expr0 instanceof FailExpr && expr1 instanceof FailExpr) {
				FailExpr f0 = (FailExpr) expr0;
				FailExpr f1 = (FailExpr) expr1;
				if (f0.getFailmsg() != null && f1.getFailmsg() != null) {
					return f0.getFailmsg().equals(f1.getFailmsg());
				} else if (f0.getVal() != null && f1.getVal() != null) {
					return compare(f0.getVal(), f1.getVal());
				} else {
					return false;
				}
			} else if (expr0 instanceof ThisExpr && expr1 instanceof ThisExpr) {
				ThisExpr e0 = (ThisExpr) expr0;
				ThisExpr e1 = (ThisExpr) expr1;
				if (e0.getSub() != null || e1.getSub() != null) {
					return e0.getSub().equals(e1.getSub());
				} else {
					return true;
				}
			} else if (expr0 instanceof FailExpr && expr1 instanceof FailExpr) {
				FailExpr f0 = (FailExpr) expr0;
				FailExpr f1 = (FailExpr) expr1;
				return compare(f0.getVal(), f1.getVal()) & f0.getFailmsg().equals(f1.getFailmsg());
			} else if (expr0 instanceof IntExpr && expr1 instanceof IntExpr) {
				IntExpr i0 = (IntExpr) expr0;
				IntExpr i1 = (IntExpr) expr1;
				return i0.getVal().getValue() == i1.getVal().getValue();
			} else if (expr0 instanceof RealExpr && expr1 instanceof RealExpr) {
				RealExpr r0 = (RealExpr) expr0;
				RealExpr r1 = (RealExpr) expr1;
				return r0.getVal().getValue() == r1.getVal().getValue();
			} else if (expr0 instanceof BoolExpr && expr1 instanceof BoolExpr) {
				BoolExpr b0 = (BoolExpr) expr0;
				BoolExpr b1 = (BoolExpr) expr1;
				return b0.getVal().getValue() == b1.getVal().getValue();
			} else if (expr0 instanceof StringExpr && expr1 instanceof StringExpr) {
				String s0 = ((StringExpr) expr0).getVal().getValue();
				String s1 = ((StringExpr) expr1).getVal().getValue();
				return s0.equals(s1);
			} else if (expr0 instanceof IfThenElseExpr && expr1 instanceof IfThenElseExpr) {
				IfThenElseExpr i0 = (IfThenElseExpr) expr0;
				IfThenElseExpr i1 = (IfThenElseExpr) expr1;
				return compare(i0.getCond(), i1.getCond()) & compare(i0.getElse(), i1.getThen())
						& compare(i0.getElse(), i1.getElse());
			} else if (expr0 instanceof QuantifiedExpr && expr1 instanceof QuantifiedExpr) {
				QuantifiedExpr q0 = (QuantifiedExpr) expr0;
				QuantifiedExpr q1 = (QuantifiedExpr) expr1;
				return q0.getQuant().equals(q1.getQuant()) & q0.getArgs().equals(q1.getArgs())
						& ExprComparator.compare(q0.getExpr(), q1.getExpr());
			} else if (expr0 instanceof LibraryFnCallExpr && expr1 instanceof LibraryFnCallExpr) {
				LibraryFnCallExpr l0 = (LibraryFnCallExpr) expr0;
				LibraryFnCallExpr l1 = (LibraryFnCallExpr) expr1;
				return l0.getLibName().equals(l1.getLibName()) & l0.getFnName().equals(l1.getFnName())
						& compare(l0.getArgs(), l1.getArgs());
			} else if (expr0 instanceof BuiltInFnCallExpr && expr1 instanceof BuiltInFnCallExpr) {
				BuiltInFnCallExpr b0 = (BuiltInFnCallExpr) expr0;
				BuiltInFnCallExpr b1 = (BuiltInFnCallExpr) expr1;
				return b0.getFn().equals(b1.getFn()) & compare(b0.getArgs(), b1.getArgs());
			} else if (expr0 instanceof FnCallExpr && expr1 instanceof FnCallExpr) {
				FnCallExpr f0 = (FnCallExpr) expr0;
				FnCallExpr f1 = (FnCallExpr) expr1;
				return f0.getFn().getName().equals(f1.getFn().getName());
			}

		} catch (NullPointerException e) {
			// if either expression is null, the comparison should fail
			return false;
		}

		return false;
	}

	public static boolean compare(EList<Expr> expr0, EList<Expr> expr1) {

		if (expr0.size() != expr1.size()) {
			return false;
		}

		for (int i = 0; i < expr0.size(); i++) {
			if (!compare(expr0.get(i), expr1.get(i))) {
				return false;
			}
		}

		return true;
	}

}
