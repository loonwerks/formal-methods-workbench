package com.collins.fmw.cyres.architecture.requirements;

import org.osate.aadl2.Aadl2Factory;
import org.osate.aadl2.BooleanLiteral;
import org.osate.aadl2.NamedElement;
import org.osate.aadl2.StringLiteral;

import com.rockwellcollins.atc.resolute.resolute.Arg;
import com.rockwellcollins.atc.resolute.resolute.BaseType;
import com.rockwellcollins.atc.resolute.resolute.BoolExpr;
import com.rockwellcollins.atc.resolute.resolute.FnCallExpr;
import com.rockwellcollins.atc.resolute.resolute.FunctionDefinition;
import com.rockwellcollins.atc.resolute.resolute.IdExpr;
import com.rockwellcollins.atc.resolute.resolute.ResoluteFactory;
import com.rockwellcollins.atc.resolute.resolute.StringExpr;
import com.rockwellcollins.atc.resolute.resolute.ThisExpr;
import com.rockwellcollins.atc.resolute.resolute.Type;

public class Create {

	public static ResoluteFactory factory = ResoluteFactory.eINSTANCE;
	public static Aadl2Factory aadlFactory = Aadl2Factory.eINSTANCE;

	public static IdExpr id(NamedElement element) {
		IdExpr idExpr = factory.createIdExpr();
		idExpr.setId(element);
		return idExpr;
	}

	public static FnCallExpr fnCall(FunctionDefinition fnDef) {
		FnCallExpr fnCallExpr = factory.createFnCallExpr();
		fnCallExpr.setFn(fnDef);
		return fnCallExpr;
	}

	public static Arg arg(String name, Type t) {
		Arg arg = factory.createArg();
		arg.setName(name);
		arg.setType(t);
		return arg;
	}

	public static StringExpr stringExpr(String val) {
		StringExpr se = factory.createStringExpr();
		StringLiteral x = aadlFactory.createStringLiteral();
		x.setValue("\"" + val + "\"");
		se.setVal(x);
		return se;
	}

	public static BaseType baseType(String typeString) {
		BaseType t = factory.createBaseType();
		t.setType(typeString);
		return t;
	}

	public static ThisExpr THIS() {
		return factory.createThisExpr();
	}

	public static BoolExpr TRUE() {
		BoolExpr be = factory.createBoolExpr();
		be.setVal(trueAADLLiteral());
		return be;
	}

	public static BoolExpr FALSE() {
		BoolExpr be = factory.createBoolExpr();
		be.setVal(falseAADLLiteral());
		return be;
	}

	private static BooleanLiteral trueAADLLiteral() {
		BooleanLiteral lit = aadlFactory.createBooleanLiteral();
		lit.setValue(true);
		return lit;
	}

	private static BooleanLiteral falseAADLLiteral() {
		BooleanLiteral lit = aadlFactory.createBooleanLiteral();
		lit.setValue(false);
		return lit;
	}
}
