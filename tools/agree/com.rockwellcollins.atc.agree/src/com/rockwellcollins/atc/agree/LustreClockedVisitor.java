package com.rockwellcollins.atc.agree;

import static jkind.lustre.parsing.LustreParseUtil.expr;
import static jkind.lustre.parsing.LustreParseUtil.to;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import jkind.lustre.CondactExpr;
import jkind.lustre.Equation;
import jkind.lustre.Expr;
import jkind.lustre.IdExpr;
import jkind.lustre.NodeCallExpr;
import jkind.lustre.TypeDef;
import jkind.lustre.UnaryExpr;
import jkind.lustre.UnaryOp;
import jkind.lustre.VarDecl;
import jkind.lustre.builders.ProgramBuilder;
import jkind.lustre.visitors.ExprMapVisitor;
import jkind.lustre.visitors.TypeReconstructor;

public class LustreClockedVisitor extends ExprMapVisitor {

	public List<Equation> glueEquations;
	public List<VarDecl> glueVars;

	private int numStateVars = 0;
	private Set<String> globalLustreNodeNames = new HashSet<>();

	private TypeReconstructor typeReconstructor;

	public LustreClockedVisitor(List<TypeDef> types, List<jkind.lustre.Node> globalLustreNodes, String main,
			jkind.lustre.Node node) {

		jkind.lustre.Program program = new ProgramBuilder().addTypes(types).addNodes(globalLustreNodes)
				.setMain(main).build();
		globalLustreNodeNames.addAll(globalLustreNodes.stream().map(n -> n.id).collect(Collectors.toSet()));
		typeReconstructor = new TypeReconstructor(program);
		typeReconstructor.setNodeContext(node);

	}

	@Override
	public Expr visit(CondactExpr e) {
		throw new RuntimeException("There should not be be any condacts present in the generated lustre");
	}

	@Override
	public Expr visit(NodeCallExpr e) {
		if (globalLustreNodeNames.contains(e.node)) {
			List<Expr> newArgs = new ArrayList<>();
			newArgs.add(new IdExpr(Lustre.clockVarName));
			newArgs.add(new IdExpr(Lustre.initVarName));
			newArgs.addAll(acceptList(e.args));
			return new NodeCallExpr(Lustre.clockedNodePrefix + e.node, newArgs);
		} else {
			return new NodeCallExpr(e.node, acceptList(e.args));
		}
	}

	@Override
	public Expr visit(UnaryExpr e) {
		if (e.op == UnaryOp.PRE) {

			IdExpr stateVarId = new IdExpr(Lustre.statVarPrefix + numStateVars++);

			Expr stateVarExpr = new UnaryExpr(UnaryOp.PRE, e.expr.accept(this));
			stateVarExpr = expr("if clk then stateVarExpr else (pre stateVar)", to("stateVar", stateVarId),
					to("stateVarExpr", stateVarExpr), to("clk", Lustre.clockVarName));

			glueVars.add(new VarDecl(stateVarId.id, e.accept(typeReconstructor)));
			glueEquations.add(new Equation(stateVarId, stateVarExpr));

			return stateVarId;
		}
		return new UnaryExpr(e.op, e.expr.accept(this));
	}

	private List<Expr> acceptList(List<Expr> exprs) {
		List<Expr> result = new ArrayList<>();

		for (Expr expr : exprs) {
			result.add(expr.accept(this));
		}

		return result;
	}
}
