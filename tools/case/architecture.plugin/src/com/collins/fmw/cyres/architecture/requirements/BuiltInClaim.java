package com.collins.fmw.cyres.architecture.requirements;

import java.util.ArrayList;
import java.util.List;

import org.osate.aadl2.AadlPackage;
import org.osate.aadl2.AnnexLibrary;
import org.osate.aadl2.DefaultAnnexLibrary;
import org.osate.aadl2.PrivatePackageSection;
import org.osate.aadl2.PublicPackageSection;
import org.osate.aadl2.modelsupport.util.AadlUtil;

import com.collins.fmw.cyres.architecture.utils.CaseUtils;
import com.rockwellcollins.atc.resolute.analysis.execution.ExprComparator;
import com.rockwellcollins.atc.resolute.resolute.Arg;
import com.rockwellcollins.atc.resolute.resolute.Definition;
import com.rockwellcollins.atc.resolute.resolute.Expr;
import com.rockwellcollins.atc.resolute.resolute.FnCallExpr;
import com.rockwellcollins.atc.resolute.resolute.FunctionDefinition;
import com.rockwellcollins.atc.resolute.resolute.ProveStatement;
import com.rockwellcollins.atc.resolute.resolute.ResoluteLibrary;

public abstract class BuiltInClaim {

	public final String claim;

	public BuiltInClaim(String claim) {
		this.claim = claim;
	}

	public abstract List<Expr> getCallArgs();

	public abstract List<Arg> getDefinitionParams();

	public String getName() {
		return this.claim;
	}

	protected FunctionDefinition buildClaimDefinition(FunctionDefinition reqClaimDef) {

		List<Arg> defParams = getDefinitionParams();

		ClaimBuilder builder = new ClaimBuilder(reqClaimDef);
		List<Arg> fnCallArgs = new ArrayList<>();

		// If the parameter isn't already in the function definition, add it
		for (Arg arg : defParams) {
			boolean argFound = false;
			for (Arg a : reqClaimDef.getArgs()) {
				if (a.getName().equalsIgnoreCase(arg.getName())) {
					argFound = true;
					fnCallArgs.add(a);
					break;
				}
			}
			if (!argFound) {
				Arg a = builder.addArg(arg);
				fnCallArgs.add(a);
			}

		}

		// Add the call to the built-in claim with arguments
		FnCallExpr fnCallExpr = Create.fnCallExpr(getBuiltInClaimDefinition(reqClaimDef));
		for (Arg arg : fnCallArgs) {
			fnCallExpr.getArgs().add(Create.id(arg));
		}

		builder.addClaimExpr(fnCallExpr);

		return builder.build();
	}


	protected ProveStatement buildClaimCall(ProveStatement prove) {

		// Get current claim call for the requirement (could be null if there isn't one)
		FnCallExpr expr = (FnCallExpr) prove.getExpr();

		// Get the required call arguments from the built-in claim
		List<Expr> callArgs = getCallArgs();

		ClaimCallBuilder builder = new ClaimCallBuilder(prove);

		for (Expr arg : callArgs) {
			boolean argFound = false;
			for (Expr e : expr.getArgs()) {

				if (ExprComparator.compare(arg, e)) {
					argFound = true;
					break;
				}
			}
			if (!argFound) {
				builder.addArg(arg);
			}
		}

		return builder.build();
	}


	private AadlPackage importCasePackage(FunctionDefinition reqClaimDef) {

		if (reqClaimDef == null || this.claim == null) {
			return null;
		}

		AadlPackage contextPkg = AadlUtil.getContainingPackage(reqClaimDef);
		if (contextPkg == null) {
			throw new RuntimeException("Could not find containing package for " + reqClaimDef.getName());
		}

		PrivatePackageSection priv8 = contextPkg.getOwnedPrivateSection();
		if (priv8 == null) {
			priv8 = contextPkg.createOwnedPrivateSection();
		}

		if (!CaseUtils.addCaseModelTransformationsImport(priv8, false)) {
			throw new RuntimeException("Could not import CASE_Model_Transformations package.");
		}

		return CaseUtils.getCaseModelTransformationsPackage();

	}

	protected FunctionDefinition getBuiltInClaimDefinition(FunctionDefinition reqClaimDef) {

		AadlPackage casePkg = importCasePackage(reqClaimDef);

		if (casePkg == null || this.claim == null) {
			return null;
		}
		PublicPackageSection publicSection = casePkg.getOwnedPublicSection();
		for (AnnexLibrary annexLibrary : publicSection.getOwnedAnnexLibraries()) {
			DefaultAnnexLibrary defaultLib = (DefaultAnnexLibrary) annexLibrary;
			if (defaultLib.getParsedAnnexLibrary() instanceof ResoluteLibrary) {
				ResoluteLibrary resoluteLib = (ResoluteLibrary) defaultLib.getParsedAnnexLibrary();
				for (Definition def : resoluteLib.getDefinitions()) {
					if (def instanceof FunctionDefinition) {
						FunctionDefinition fd = (FunctionDefinition) def;
						if (fd.getName().equalsIgnoreCase(this.claim)) {
							return fd;
						}
					}
				}
				break;
			}
		}
		return null;
	}

}
