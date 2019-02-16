package com.collins.fmw.cyres.agree.json.plugin;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.osate.aadl2.AadlPackage;
import org.osate.aadl2.AnnexLibrary;
import org.osate.aadl2.AnnexSubclause;
import org.osate.aadl2.Comment;
import org.osate.aadl2.ComponentClassifier;
import org.osate.aadl2.DefaultAnnexLibrary;
import org.osate.aadl2.DefaultAnnexSubclause;
import org.osate.aadl2.PackageSection;
import org.osate.annexsupport.AnnexUtil;

import com.collins.fmw.json.ArrayValue;
import com.collins.fmw.json.ObjectValue;
import com.collins.fmw.json.Pair;
import com.collins.fmw.json.StringValue;
import com.collins.fmw.json.Value;
import com.rockwellcollins.atc.agree.agree.AgreeContract;
import com.rockwellcollins.atc.agree.agree.AgreeContractLibrary;
import com.rockwellcollins.atc.agree.agree.AgreeContractSubclause;
import com.rockwellcollins.atc.agree.agree.AgreePackage;
import com.rockwellcollins.atc.agree.agree.Arg;
import com.rockwellcollins.atc.agree.agree.AssertStatement;
import com.rockwellcollins.atc.agree.agree.AssignStatement;
import com.rockwellcollins.atc.agree.agree.AssumeStatement;
import com.rockwellcollins.atc.agree.agree.BinaryExpr;
import com.rockwellcollins.atc.agree.agree.BoolLitExpr;
import com.rockwellcollins.atc.agree.agree.CallExpr;
import com.rockwellcollins.atc.agree.agree.ConstStatement;
import com.rockwellcollins.atc.agree.agree.Contract;
import com.rockwellcollins.atc.agree.agree.DoubleDotRef;
import com.rockwellcollins.atc.agree.agree.EnumLitExpr;
import com.rockwellcollins.atc.agree.agree.EqStatement;
import com.rockwellcollins.atc.agree.agree.EventExpr;
import com.rockwellcollins.atc.agree.agree.ExistsExpr;
import com.rockwellcollins.atc.agree.agree.Expr;
import com.rockwellcollins.atc.agree.agree.FnDef;
import com.rockwellcollins.atc.agree.agree.ForallExpr;
import com.rockwellcollins.atc.agree.agree.GuaranteeStatement;
import com.rockwellcollins.atc.agree.agree.IfThenElseExpr;
import com.rockwellcollins.atc.agree.agree.IntLitExpr;
import com.rockwellcollins.atc.agree.agree.NamedElmExpr;
import com.rockwellcollins.atc.agree.agree.NodeDef;
import com.rockwellcollins.atc.agree.agree.NodeEq;
import com.rockwellcollins.atc.agree.agree.NodeLemma;
import com.rockwellcollins.atc.agree.agree.NodeStmt;
import com.rockwellcollins.atc.agree.agree.PreExpr;
import com.rockwellcollins.atc.agree.agree.PrimType;
import com.rockwellcollins.atc.agree.agree.PropertyStatement;
import com.rockwellcollins.atc.agree.agree.RealLitExpr;
import com.rockwellcollins.atc.agree.agree.RecordLitExpr;
import com.rockwellcollins.atc.agree.agree.SelectionExpr;
import com.rockwellcollins.atc.agree.agree.SpecStatement;
import com.rockwellcollins.atc.agree.agree.Type;
import com.rockwellcollins.atc.agree.agree.UnaryExpr;

public class AgreeTranslate {

	private Value genBinaryExpr(BinaryExpr expr) {
		ArrayList<Pair> pairList = new ArrayList<Pair>();
		pairList.add(Pair.build("kind", "BinaryExpr"));
		pairList.add(Pair.build("left", genExpr(expr.getLeft())));
		pairList.add(Pair.build("op", (expr.getOp())));
		pairList.add(Pair.build("right", genExpr(expr.getRight())));
		return ObjectValue.build(pairList);
	}

	private Value genUnaryExpr(UnaryExpr expr) {
		ArrayList<Pair> pairList = new ArrayList<Pair>();
		pairList.add(Pair.build("kind", "UnaryExpr"));
		pairList.add(Pair.build("operand", genExpr(expr.getExpr())));
		pairList.add(Pair.build("op", (expr.getOp())));
		return ObjectValue.build(pairList);
	}

	private Value genIntLitExpr(IntLitExpr expr) {
		ArrayList<Pair> pairList = new ArrayList<Pair>();
		pairList.add(Pair.build("kind", "IntLitExpr"));
		pairList.add(Pair.build("value", StringValue.build(expr.getVal())));

		return ObjectValue.build(pairList);
	}

	private Value genRealLitExpr(RealLitExpr expr) {
		ArrayList<Pair> pairList = new ArrayList<Pair>();
		pairList.add(Pair.build("kind", "RealLitExpr"));
		pairList.add(Pair.build("value", StringValue.build(expr.getVal())));
		return ObjectValue.build(pairList);
	}

	private Value genBoolLitExpr(BoolLitExpr expr) {
		ArrayList<Pair> pairList = new ArrayList<Pair>();
		pairList.add(Pair.build("kind", "BoolLitExpr"));
		pairList.add(Pair.build("value", StringValue.build(expr.getVal().getValue() + "")));
		return ObjectValue.build(pairList);
	}

	private Value genSelectionExpr(SelectionExpr expr) {
		ArrayList<Pair> pairList = new ArrayList<Pair>();
		pairList.add(Pair.build("kind", "Selection"));
		String selection = expr.getField().getName();

		pairList.add(Pair.build("target", genExpr(expr.getTarget())));
		pairList.add(Pair.build("field", selection));
		return ObjectValue.build(pairList);
	}

	private Value genNamedElmExpr(NamedElmExpr expr) {
		String id = expr.getElm().getName();
		return StringValue.build(id);
	}


	private Value genCallExpr(CallExpr expr) {
		ArrayList<Pair> pairList = new ArrayList<Pair>();
		pairList.add(Pair.build("kind", "CallExpr"));
		pairList.add(Pair.build("function", genDoubleDotRef(expr.getRef())));
		ArrayList<Value> argList = new ArrayList<Value>();
		for (Expr arg : expr.getArgs()) {
			argList.add(genExpr(arg));
		}
		pairList.add(Pair.build("args", ArrayValue.build(argList)));
		return ObjectValue.build(pairList);
	}

	private Value genIfThenElseExpr(IfThenElseExpr expr) {
		ArrayList<Pair> pairList = new ArrayList<Pair>();
		pairList.add(Pair.build("kind", "IfThenElseExpr"));
		pairList.add(Pair.build("if", genExpr(expr.getA())));
		pairList.add(Pair.build("then", genExpr(expr.getB())));
		pairList.add(Pair.build("else", genExpr(expr.getC())));
		return ObjectValue.build(pairList);
	}

	private Value genRecordLitExpr(RecordLitExpr expr) {
		ArrayList<Pair> pairList = new ArrayList<Pair>();

		pairList.add(Pair.build("kind", "RecordLitExpr"));
		pairList.add(Pair.build("recordType", genDoubleDotRef(expr.getRecordType())));
		ArrayList<Pair> fieldList = new ArrayList<Pair>();
		int sz = expr.getArgs().size();
		for (int i = 0; i < sz; i++) {
			String name = expr.getArgs().get(i).getName();
			Value v = genExpr(expr.getArgExpr().get(i));
			Pair field = Pair.build(name, v);
			fieldList.add(field);
		}

		pairList.add(Pair.build("value", ObjectValue.build(fieldList)));
		return ObjectValue.build(pairList);
	}

	private Value genEventExpr(EventExpr expr) {
		ArrayList<Pair> pairList = new ArrayList<Pair>();
		pairList.add(Pair.build("kind", "EventExpr"));
		pairList.add(Pair.build("id", expr.getId().getName()));
		return ObjectValue.build(pairList);
	}

	private Value genPreExpr(PreExpr expr) {
		ArrayList<Pair> pairList = new ArrayList<Pair>();
		pairList.add(Pair.build("kind", "PreExpr"));
		pairList.add(Pair.build("expr", genExpr(expr.getExpr())));
		return ObjectValue.build(pairList);
	}

	private Value genForallExpr(ForallExpr expr) {
		ArrayList<Pair> pairList = new ArrayList<Pair>();
		pairList.add(Pair.build("kind", "ForallExpr"));
		pairList.add(Pair.build("binding", expr.getBinding().getName()));
		pairList.add(Pair.build("array", genExpr(expr.getArray())));
		pairList.add(Pair.build("expr", genExpr(expr.getExpr())));
		return ObjectValue.build(pairList);
	}

	private Value genExistsExpr(ExistsExpr expr) {
		ArrayList<Pair> pairList = new ArrayList<Pair>();
		pairList.add(Pair.build("kind", "ExistsExpr"));
		pairList.add(Pair.build("binding", expr.getBinding().getName()));
		pairList.add(Pair.build("array", genExpr(expr.getArray())));
		pairList.add(Pair.build("expr", genExpr(expr.getExpr())));
		return ObjectValue.build(pairList);
	}

	private Value genExpr(Expr expr) {

		if (expr instanceof IntLitExpr) {
			return genIntLitExpr((IntLitExpr) expr);
		} else if (expr instanceof RealLitExpr) {
			return genRealLitExpr((RealLitExpr) expr);
		} else if (expr instanceof BoolLitExpr) {
			return genBoolLitExpr((BoolLitExpr) expr);
		} else if (expr instanceof BinaryExpr) {
			return genBinaryExpr((BinaryExpr) expr);
		} else if (expr instanceof UnaryExpr) {
			return genUnaryExpr((UnaryExpr) expr);
		} else if (expr instanceof SelectionExpr) {
			return genSelectionExpr((SelectionExpr) expr);
		} else if (expr instanceof NamedElmExpr) {
			return genNamedElmExpr((NamedElmExpr) expr);
		} else if (expr instanceof EnumLitExpr) {
			return genEnumLitExpr((EnumLitExpr) expr);
		} else if (expr instanceof CallExpr) {
			return genCallExpr((CallExpr) expr);
		} else if (expr instanceof IfThenElseExpr) {
			return genIfThenElseExpr((IfThenElseExpr) expr);
		} else if (expr instanceof RecordLitExpr) {
			return genRecordLitExpr((RecordLitExpr) expr);
		} else if (expr instanceof EventExpr) {
			return genEventExpr((EventExpr) expr);
		} else if (expr instanceof PreExpr) {
			return genPreExpr((PreExpr) expr);
		} else if (expr instanceof ForallExpr) {
			return genForallExpr((ForallExpr) expr);
		} else if (expr instanceof ExistsExpr) {
			return genExistsExpr((ExistsExpr) expr);
		} else {
			return StringValue.build("new_case/genExpr/" + (expr == null ? "null" : expr.toString()));
		}
	}


	private Value genEnumLitExpr(EnumLitExpr expr) {
		ArrayList<Pair> pairList = new ArrayList<Pair>();
		pairList.add(Pair.build("kind", "AadlEnumerator"));
		pairList.add(Pair.build("type", genDoubleDotRef(expr.getEnumType())));
		pairList.add(Pair.build("value", expr.getValue()));
		return ObjectValue.build(pairList);
	}

	private Value genAssertStatement(AssertStatement stmt) {
		ArrayList<Pair> pairList = new ArrayList<Pair>();
		pairList.add(Pair.build("kind", "AssertStatement"));
		pairList.add(Pair.build("name", stmt.getName()));
		pairList.add(Pair.build("label", stmt.getStr()));
		pairList.add(Pair.build("expr", genExpr(stmt.getExpr())));
		return ObjectValue.build(pairList);
	}


	private Value genAssumeStatement(AssumeStatement stmt) {
		ArrayList<Pair> pairList = new ArrayList<Pair>();
		pairList.add(Pair.build("kind", "AssumeStatement"));
		pairList.add(Pair.build("name", stmt.getName()));
		pairList.add(Pair.build("label", stmt.getStr()));
		pairList.add(Pair.build("expr", genExpr(stmt.getExpr())));
		return ObjectValue.build(pairList);
	}

	private Value genEqStatement(EqStatement stmt) {
		ArrayList<Pair> pairList = new ArrayList<Pair>();
		pairList.add(Pair.build("kind", "EqStatement"));
		ArrayList<Value> args = new ArrayList<Value>();
		for (Arg arg : stmt.getLhs()) {
			args.add(genArg(arg));
		}
		pairList.add(Pair.build("left", ArrayValue.build(args)));
		pairList.add(Pair.build("expr", genExpr(stmt.getExpr())));
		return ObjectValue.build(pairList);
	}

	private Value genGuaranteeStatement(GuaranteeStatement stmt) {
		ArrayList<Pair> pairList = new ArrayList<Pair>();
		pairList.add(Pair.build("kind", "GuaranteeStatement"));
		pairList.add(Pair.build("name", stmt.getName()));
		pairList.add(Pair.build("label", stmt.getStr()));
		pairList.add(Pair.build("expr", genExpr(stmt.getExpr())));
		return ObjectValue.build(pairList);
	}

	private Value genAssignStatement(AssignStatement stmt) {
		ArrayList<Pair> pairList = new ArrayList<Pair>();
		pairList.add(Pair.build("kind", "AssignStatement"));
		pairList.add(Pair.build("expr", genExpr(stmt.getExpr())));
		return ObjectValue.build(pairList);
	}

	private Value genPropertyStatement(PropertyStatement stmt) {
		ArrayList<Pair> pairList = new ArrayList<Pair>();
		pairList.add(Pair.build("kind", "PropertyStatement"));
		pairList.add(Pair.build("name", stmt.getName()));
		pairList.add(Pair.build("expr", genExpr(stmt.getExpr())));
		return ObjectValue.build(pairList);
	}


	private Value genDoubleDotRef(DoubleDotRef ref) {
		ArrayList<Pair> pairList = new ArrayList<Pair>();
		pairList.add(Pair.build("kind", "DoubleDotRef"));
		String id = ref.getElm().getName();
		pairList.add(Pair.build("name", id));
		return ObjectValue.build(pairList);
//		return StringValue.build(typeID.getElm().getName());
	}



	private Value genPrimType(PrimType type) {
		ArrayList<Pair> pairList = new ArrayList<Pair>();
		pairList.add(Pair.build("kind", "PrimType"));

		pairList.add(Pair.build("primType", type.getName()));
		return ObjectValue.build(pairList);
	}

	private Value genType(Type type) {
		Value v = null;
		if (type instanceof DoubleDotRef) {
			v = genDoubleDotRef((DoubleDotRef) type);
		} else if (type instanceof PrimType) {
			v = genPrimType((PrimType) type);
		} else {
			v = StringValue.build("new_case/genType/" + type);
		}
		return v;
	}


	private Value genArg(Arg arg) {
		ArrayList<Pair> pairList = new ArrayList<Pair>();
//		pairList.add(Pair.build("kind", "Arg"));
		pairList.add(Pair.build("name", arg.getName()));
		pairList.add(Pair.build("type", genType(arg.getType())));
		return ObjectValue.build(pairList);
	}


	private Value genFnDef(FnDef stmt) {
		ArrayList<Pair> pairList = new ArrayList<Pair>();
		pairList.add(Pair.build("kind", "FnDef"));
		pairList.add(Pair.build("name", stmt.getName()));

		ArrayList<Value> argList = new ArrayList<Value>();
		for (Arg arg : stmt.getArgs()) {
			argList.add(genArg(arg));
		}
		pairList.add(Pair.build("args", ArrayValue.build(argList)));
		pairList.add(Pair.build("type", genType(stmt.getType())));
		pairList.add(Pair.build("expr", genExpr(stmt.getExpr())));
		return ObjectValue.build(pairList);
	}

	private Value genConstStatement(ConstStatement stmt) {
		ArrayList<Pair> pairList = new ArrayList<Pair>();
		pairList.add(Pair.build("kind", "ConstStatement"));
		pairList.add(Pair.build("name", stmt.getName()));
		pairList.add(Pair.build("type", genType(stmt.getType())));
		pairList.add(Pair.build("expr", genExpr(stmt.getExpr())));
		return ObjectValue.build(pairList);
	}

	private Value genNodeStatement(NodeStmt stmt) {
		ArrayList<Pair> pairList = new ArrayList<Pair>();
		if (stmt instanceof NodeEq) {
			pairList.add(Pair.build("kind", "NodeEq"));
			ArrayList<Value> args = new ArrayList<Value>();
			for (Arg arg : ((NodeEq) stmt).getLhs()) {
				args.add(genArg(arg));
			}
			pairList.add(Pair.build("left", ArrayValue.build(args)));
			pairList.add(Pair.build("expr", genExpr(stmt.getExpr())));
		} else if (stmt instanceof NodeLemma) {
			pairList.add(Pair.build("kind", "NodeLemma"));
			pairList.add(Pair.build("lemma", ((NodeLemma) stmt).getStr()));
			pairList.add(Pair.build("expr", genExpr(stmt.getExpr())));
		}

		return ObjectValue.build(pairList);
	}

	private Value genNodeDef(NodeDef stmt) {
		ArrayList<Pair> pairList = new ArrayList<Pair>();
		pairList.add(Pair.build("kind", "NodeDef"));
		pairList.add(Pair.build("name", stmt.getName()));
		ArrayList<Value> argList = new ArrayList<Value>();
		for (Arg arg : stmt.getArgs()) {
			argList.add(genArg(arg));
		}
		pairList.add(Pair.build("args", ArrayValue.build(argList)));
		ArrayList<Value> retList = new ArrayList<Value>();
		for (Arg arg : stmt.getRets()) {
			retList.add(genArg(arg));
		}
		pairList.add(Pair.build("returns", ArrayValue.build(retList)));
		ArrayList<Value> exprList = new ArrayList<Value>();
		for (NodeStmt nodeStmt : stmt.getNodeBody().getStmts()) {
			exprList.add(genNodeStatement(nodeStmt));
//			exprList.add(genExpr(nodeStmt.getExpr()));
		}
		pairList.add(Pair.build("body", ArrayValue.build(exprList)));
		return ObjectValue.build(pairList);
	}

	public Value genSpecStatement(SpecStatement stmt) {

		if (stmt instanceof AssertStatement) {
			return genAssertStatement((AssertStatement) stmt);
		} else if (stmt instanceof AssumeStatement) {
			return genAssumeStatement((AssumeStatement) stmt);
		} else if (stmt instanceof EqStatement) {
			return genEqStatement((EqStatement) stmt);
		} else if (stmt instanceof GuaranteeStatement) {
			return genGuaranteeStatement((GuaranteeStatement) stmt);
		} else if (stmt instanceof AssignStatement) {
			return genAssignStatement((AssignStatement) stmt);
		} else if (stmt instanceof PropertyStatement) {
			return genPropertyStatement((PropertyStatement) stmt);
		} else if (stmt instanceof FnDef) {
			return genFnDef((FnDef) stmt);
		} else if (stmt instanceof ConstStatement) {
			return genConstStatement((ConstStatement) stmt);
		} else if (stmt instanceof NodeDef) {
			return genNodeDef((NodeDef) stmt);
		} else {
			return StringValue.build("new_case/genSpecStatement/" + stmt.toString());
		}

	}


	public ObjectValue genContract(Contract contr) {
		ArrayList<Pair> pairList = new ArrayList<Pair>();

		if (contr instanceof AgreeContract) {
			EList<SpecStatement> stmts = ((AgreeContract) contr).getSpecs();

			ArrayList<Value> stmtJsonList = new ArrayList<Value>();
			for (SpecStatement stmt : stmts) {
				stmtJsonList.add(genSpecStatement(stmt));
			}
			pairList.add(Pair.build("statements", ArrayValue.build(stmtJsonList)));

			ArrayList<Value> comJsonList = new ArrayList<Value>();
			for (Comment com : contr.getOwnedComments()) {
				comJsonList.add(StringValue.build(com.getBody()));
			}
		}

		return ObjectValue.build(pairList);
	}

	public Value genComponentClassifier(ComponentClassifier cc) {

		ArrayList<Value> components = new ArrayList<Value>();

		EList<AnnexSubclause> annexSubClauses = AnnexUtil.getAllAnnexSubclauses(cc,
				AgreePackage.eINSTANCE.getAgreeContractSubclause());

		for (AnnexSubclause anx : annexSubClauses) {
			if (anx instanceof AgreeContractSubclause) {
				AgreeContract contr = (AgreeContract) ((AgreeContractSubclause) anx).getContract();
				components.add(genContract(contr));
			}
		}


		return ArrayValue.build(components);
	}

	public Value genAadlPackage(AadlPackage pkg) {

		ArrayList<Value> components = new ArrayList<Value>();

		List<AnnexLibrary> annexLibraries = AnnexUtil.getAllActualAnnexLibraries(pkg,
				AgreePackage.eINSTANCE.getAgreeContractLibrary());

		for (AnnexLibrary anl : annexLibraries) {
			if (anl instanceof AgreeContractLibrary) {
				AgreeContract contr = (AgreeContract) ((AgreeContractLibrary) anl).getContract();
				components.add(genContract(contr));
			}
		}

		return ArrayValue.build(components);
	}

	public ObjectValue genAadlPackageSection(PackageSection pkgSection) {

		ObjectValue agreeLib = ObjectValue.build(new ArrayList<Pair>());

		for (AnnexLibrary annexLib : pkgSection.getOwnedAnnexLibraries()) {
			DefaultAnnexLibrary defaultAnnexLib = (DefaultAnnexLibrary) annexLib;
			if (defaultAnnexLib.getParsedAnnexLibrary() instanceof AgreeContractLibrary) {
				AgreeContract contr = (AgreeContract) ((AgreeContractLibrary) defaultAnnexLib.getParsedAnnexLibrary())
						.getContract();
				agreeLib = genContract(contr);
				break;
			}
		}

		return agreeLib;
	}

	public ObjectValue genAnnexLibrary(AnnexLibrary annexLib) {
		DefaultAnnexLibrary defaultAnnexLib = (DefaultAnnexLibrary) annexLib;
		AgreeContract contr = (AgreeContract) ((AgreeContractLibrary) defaultAnnexLib.getParsedAnnexLibrary())
				.getContract();
		return genContract(contr);
	}

	public ObjectValue genAnnexSubclause(AnnexSubclause annexSub) {
		DefaultAnnexSubclause defaultAnnexSub = (DefaultAnnexSubclause) annexSub;
		AgreeContract contr = (AgreeContract) ((AgreeContractSubclause) defaultAnnexSub.getParsedAnnexSubclause())
				.getContract();
		return genContract(contr);
	}

}