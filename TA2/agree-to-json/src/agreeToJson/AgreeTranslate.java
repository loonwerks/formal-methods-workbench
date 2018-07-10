package agreeToJson;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.osate.aadl2.AadlPackage;
import org.osate.aadl2.AnnexLibrary;
import org.osate.aadl2.AnnexSubclause;
import org.osate.aadl2.Comment;
import org.osate.aadl2.ComponentClassifier;
import org.osate.annexsupport.AnnexUtil;

import com.rockwellcollins.atc.agree.agree.AADLEnumerator;
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
import com.rockwellcollins.atc.agree.agree.Contract;
import com.rockwellcollins.atc.agree.agree.EqStatement;
import com.rockwellcollins.atc.agree.agree.Expr;
import com.rockwellcollins.atc.agree.agree.FnCallExpr;
import com.rockwellcollins.atc.agree.agree.FnDefExpr;
import com.rockwellcollins.atc.agree.agree.GuaranteeStatement;
import com.rockwellcollins.atc.agree.agree.IntLitExpr;
import com.rockwellcollins.atc.agree.agree.NestedDotID;
import com.rockwellcollins.atc.agree.agree.PrimType;
import com.rockwellcollins.atc.agree.agree.PropertyStatement;
import com.rockwellcollins.atc.agree.agree.RealLitExpr;
import com.rockwellcollins.atc.agree.agree.RecordType;
import com.rockwellcollins.atc.agree.agree.SpecStatement;
import com.rockwellcollins.atc.agree.agree.Type;
import com.rockwellcollins.atc.agree.agree.UnaryExpr;

import agreeToJson.json.ArrayValue;
import agreeToJson.json.ObjectValue;
import agreeToJson.json.Pair;
import agreeToJson.json.StringValue;
import agreeToJson.json.Value;

public class AgreeTranslate {

	private Value genBinaryExpr(BinaryExpr expr) {
		ArrayList<Pair> pairList = new ArrayList<Pair>();
		pairList.add(Pair.build("kind", "binary"));
		pairList.add(Pair.build("left", genExpr(expr.getLeft())));
		pairList.add(Pair.build("op", (expr.getOp())));
		pairList.add(Pair.build("right", genExpr(expr.getRight())));
		return ObjectValue.build(pairList);
	}

	private Value genUnaryExpr(UnaryExpr expr) {
		ArrayList<Pair> pairList = new ArrayList<Pair>();
		pairList.add(Pair.build("kind", "unary"));
		pairList.add(Pair.build("operand", genExpr(expr.getExpr())));
		pairList.add(Pair.build("op", (expr.getOp())));
		return ObjectValue.build(pairList);
	}

	private Value genIntLitExpr(IntLitExpr expr) {
		ArrayList<Pair> pairList = new ArrayList<Pair>();
		pairList.add(Pair.build("kind", "intLit"));
		pairList.add(Pair.build("value", StringValue.build(expr.getVal())));
		return ObjectValue.build(pairList);
	}

	private Value genRealLitExpr(RealLitExpr expr) {
		ArrayList<Pair> pairList = new ArrayList<Pair>();
		pairList.add(Pair.build("kind", "realLit"));
		pairList.add(Pair.build("value", StringValue.build(expr.getVal())));
		return ObjectValue.build(pairList);
	}

	private Value genBoolLitExpr(BoolLitExpr expr) {
		ArrayList<Pair> pairList = new ArrayList<Pair>();
		pairList.add(Pair.build("kind", "boolLit"));
		pairList.add(Pair.build("value", StringValue.build(expr.getVal().getValue() + "")));
		return ObjectValue.build(pairList);
	}

	private Value genNestedDotID(NestedDotID expr) {
		ArrayList<Pair> pairList = new ArrayList<Pair>();
		pairList.add(Pair.build("kind", "nestedDotId"));
		String id = expr.getBase().getName();
		NestedDotID sub = expr.getSub();

		while (sub != null) {
			if (sub instanceof NestedDotID) {
				id += "." + sub.getBase().getName();
				sub = sub.getSub();
			}
		}


		if (expr.getTag() != null) {
			id += "." + expr.getTag();
		}

		pairList.add(Pair.build("name", id));
		return ObjectValue.build(pairList);
	}

	private Value genFnCallExpr(FnCallExpr expr) {
		ArrayList<Pair> pairList = new ArrayList<Pair>();
		pairList.add(Pair.build("kind", "fnCall"));
		pairList.add(Pair.build("function", genExpr(expr.getFn())));
		ArrayList<Value> argList = new ArrayList<Value>();
		for (Expr arg : expr.getArgs()) {
			argList.add(genExpr(arg));
		}
		pairList.add(Pair.build("args", ArrayValue.build(argList)));
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
		} else if (expr instanceof NestedDotID) {
			return genNestedDotID((NestedDotID) expr);
		} else if (expr instanceof AADLEnumerator) {
			return genAADLEnumerator((AADLEnumerator) expr);
		} else if (expr instanceof FnCallExpr) {
			return genFnCallExpr((FnCallExpr) expr);
		} else {
			return StringValue.build("new_case/genExpr/" + (expr == null ? "null" : expr.toString()));
		}
	}


	private Value genAADLEnumerator(AADLEnumerator expr) {
		ArrayList<Pair> pairList = new ArrayList<Pair>();
		pairList.add(Pair.build("kind", "aadlEnum"));
		pairList.add(Pair.build("type", genExpr(expr.getEnumType())));
		pairList.add(Pair.build("value", expr.getValue()));
		return ObjectValue.build(pairList);
	}

	private Value genAssertStatement(AssertStatement stmt) {
		ArrayList<Pair> pairList = new ArrayList<Pair>();
		pairList.add(Pair.build("kind", "assert"));
		pairList.add(Pair.build("label", stmt.getStr()));
		pairList.add(Pair.build("expr", genExpr(stmt.getExpr())));
		return ObjectValue.build(pairList);
	}


	private Value genAssumeStatement(AssumeStatement stmt) {
		ArrayList<Pair> pairList = new ArrayList<Pair>();
		pairList.add(Pair.build("kind", "assume"));
		pairList.add(Pair.build("label", stmt.getStr()));
		pairList.add(Pair.build("expr", genExpr(stmt.getExpr())));
		return ObjectValue.build(pairList);
	}

	private Value genEqStatement(EqStatement stmt) {
		ArrayList<Pair> pairList = new ArrayList<Pair>();
		pairList.add(Pair.build("kind", "eq"));
		pairList.add(Pair.build("expr", genExpr(stmt.getExpr())));
		return ObjectValue.build(pairList);
	}

	private Value genGuaranteeStatement(GuaranteeStatement stmt) {
		ArrayList<Pair> pairList = new ArrayList<Pair>();
		pairList.add(Pair.build("kind", "guarantee"));
		pairList.add(Pair.build("label", stmt.getStr()));
		pairList.add(Pair.build("expr", genExpr(stmt.getExpr())));
		return ObjectValue.build(pairList);
	}

	private Value genAssignStatement(AssignStatement stmt) {
		ArrayList<Pair> pairList = new ArrayList<Pair>();
		pairList.add(Pair.build("kind", "assign"));
		pairList.add(Pair.build("expr", genExpr(stmt.getExpr())));
		return ObjectValue.build(pairList);
	}

	private Value genPropertyStatement(PropertyStatement stmt) {
		ArrayList<Pair> pairList = new ArrayList<Pair>();
		pairList.add(Pair.build("kind", "property"));
		pairList.add(Pair.build("name", stmt.getName()));
		pairList.add(Pair.build("expr", genExpr(stmt.getExpr())));
		return ObjectValue.build(pairList);
	}

	private Value genRecordType(RecordType type) {
		ArrayList<Pair> pairList = new ArrayList<Pair>();
		pairList.add(Pair.build("kind", "recordType"));
		pairList.add(Pair.build("recordType", genNestedDotID(type.getRecord())));
		return ObjectValue.build(pairList);
	}

	private Value genPrimType(PrimType type) {
		ArrayList<Pair> pairList = new ArrayList<Pair>();
		pairList.add(Pair.build("kind", "primType"));
		pairList.add(Pair.build("primType", type.getString()));
		return ObjectValue.build(pairList);
	}

	private Value genType(Type type) {
		ArrayList<Pair> pairList = new ArrayList<Pair>();
		pairList.add(Pair.build("kind", "type"));
		if (type instanceof RecordType) {
			pairList.add(Pair.build("type", genRecordType((RecordType) type)));
		} else if (type instanceof PrimType) {
			pairList.add(Pair.build("type", genPrimType((PrimType) type)));
		} else {
			pairList.add(Pair.build("type", "new_case/genType/" + type));
		}
		return ObjectValue.build(pairList);
	}


	private Value genParam(Arg param) {
		ArrayList<Pair> pairList = new ArrayList<Pair>();
		pairList.add(Pair.build("kind", "param"));
		pairList.add(Pair.build("name", param.getName()));
		pairList.add(Pair.build("type", genType(param.getType())));
		return ObjectValue.build(pairList);
	}


	private Value genFnDefExpr(FnDefExpr stmt) {
		ArrayList<Pair> pairList = new ArrayList<Pair>();
		pairList.add(Pair.build("kind", "funDef"));
		pairList.add(Pair.build("name", stmt.getName()));

		ArrayList<Value> paramList = new ArrayList<Value>();
		for (Arg param : stmt.getArgs()) {
			paramList.add(genParam(param));
		}
		pairList.add(Pair.build("params", ArrayValue.build(paramList)));
		pairList.add(Pair.build("expr", genExpr(stmt.getExpr())));
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
		} else if (stmt instanceof FnDefExpr) {
			return genFnDefExpr((FnDefExpr) stmt);

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


		components.add(StringValue.build("size-" + annexLibraries.size()));
		for (AnnexLibrary anl : annexLibraries) {
			if (anl instanceof AgreeContractLibrary) {
				AgreeContract contr = (AgreeContract) ((AgreeContractLibrary) anl).getContract();
				components.add(genContract(contr));
			}
		}

		return ArrayValue.build(components);
	}

}