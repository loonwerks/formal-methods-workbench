package com.rockwellcollins.atc.resolute.analysis.execution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.emf.ecore.EObject;

import com.rockwellcollins.atc.resolute.analysis.results.ClaimResult;
import com.rockwellcollins.atc.resolute.analysis.results.LintResult;
import com.rockwellcollins.atc.resolute.analysis.results.ResoluteResult;
import com.rockwellcollins.atc.resolute.resolute.ErrorStatement;
import com.rockwellcollins.atc.resolute.resolute.InfoStatement;
import com.rockwellcollins.atc.resolute.resolute.LintStatement;
import com.rockwellcollins.atc.resolute.resolute.ProveStatement;
import com.rockwellcollins.atc.resolute.resolute.WarningStatement;

public class ResoluteInterpreter {
    final private EvaluationContext globalContext;

    public ResoluteInterpreter(EvaluationContext globalContext) {
        this.globalContext = globalContext;
    }

    public ResoluteResult evaluateProveStatement(ProveStatement proveStatement) {
        String proveText = ResoluteProver.proveStatementToString(proveStatement,
                globalContext.getThisInstance());
        ResoluteResult subResult = evaluateProveStatementBody(proveStatement);
        Map<String, EObject> references = Collections.emptyMap();
        return new ClaimResult(proveText, subResult, references, proveStatement);
    }

    private ResoluteResult evaluateProveStatementBody(ProveStatement proveStatement) {
        ResoluteProver prover = new ResoluteProver(globalContext);
        return prover.doSwitch(proveStatement.getExpr());
    }

	public ResoluteResult evaluateLintStatement(LintStatement lintStatement) {
		int severity = 0;
		if (lintStatement instanceof ErrorStatement) {
			severity = IMarker.SEVERITY_ERROR;
		} else if (lintStatement instanceof WarningStatement) {
			severity = IMarker.SEVERITY_WARNING;
		} else if (lintStatement instanceof InfoStatement) {
			severity = IMarker.SEVERITY_INFO;
		}

		ResoluteResult subResult = evaluateLintStatementBody(lintStatement);
		String lintText = ((ClaimResult) subResult).getText();
//		Map<String, EObject> references = Collections.emptyMap();
		Map<String, EObject> references = ((ClaimResult) subResult).getReferences();
		ArrayList<EObject> refList = new ArrayList<>(references.values());
		EObject eObj = lintStatement;
		if (!refList.isEmpty()) {
			eObj = refList.get(0);
		}
		return new LintResult(severity, lintText, subResult, references, eObj);
	}

	private ResoluteResult evaluateLintStatementBody(LintStatement lintStatement) {
		ResoluteProver prover = new ResoluteProver(globalContext);
		return prover.doSwitch(lintStatement.getExpr());
	}
}
