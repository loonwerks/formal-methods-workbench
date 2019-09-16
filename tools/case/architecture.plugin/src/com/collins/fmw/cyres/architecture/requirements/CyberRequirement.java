package com.collins.fmw.cyres.architecture.requirements;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.AbstractMap;
import java.util.Date;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.EcoreUtil2;
import org.osate.aadl2.Aadl2Factory;
import org.osate.aadl2.Aadl2Package;
import org.osate.aadl2.AadlPackage;
import org.osate.aadl2.AnnexLibrary;
import org.osate.aadl2.AnnexSubclause;
import org.osate.aadl2.Classifier;
import org.osate.aadl2.DefaultAnnexLibrary;
import org.osate.aadl2.DefaultAnnexSubclause;
import org.osate.aadl2.NamedElement;
import org.osate.aadl2.PrivatePackageSection;
import org.osate.aadl2.StringLiteral;
import org.osate.aadl2.Subcomponent;
import org.osate.aadl2.util.Aadl2Util;
import org.osate.ui.dialogs.Dialog;

import com.collins.fmw.cyres.architecture.utils.CaseUtils;
import com.collins.fmw.cyres.util.plugin.Filesystem;
import com.collins.fmw.cyres.util.plugin.TraverseProject;
import com.rockwellcollins.atc.agree.agree.AgreeContract;
import com.rockwellcollins.atc.agree.agree.AgreeContractSubclause;
import com.rockwellcollins.atc.agree.agree.AgreeFactory;
import com.rockwellcollins.atc.agree.agree.NamedSpecStatement;
import com.rockwellcollins.atc.agree.agree.SpecStatement;
import com.rockwellcollins.atc.agree.parsing.AgreeAnnexParser;
import com.rockwellcollins.atc.resolute.resolute.AnalysisStatement;
import com.rockwellcollins.atc.resolute.resolute.BinaryExpr;
import com.rockwellcollins.atc.resolute.resolute.ClaimBody;
import com.rockwellcollins.atc.resolute.resolute.ClaimContext;
import com.rockwellcollins.atc.resolute.resolute.Definition;
import com.rockwellcollins.atc.resolute.resolute.DefinitionBody;
import com.rockwellcollins.atc.resolute.resolute.Expr;
import com.rockwellcollins.atc.resolute.resolute.FnCallExpr;
import com.rockwellcollins.atc.resolute.resolute.FunctionDefinition;
import com.rockwellcollins.atc.resolute.resolute.ProveStatement;
import com.rockwellcollins.atc.resolute.resolute.ResoluteFactory;
import com.rockwellcollins.atc.resolute.resolute.ResoluteLibrary;
import com.rockwellcollins.atc.resolute.resolute.ResoluteSubclause;

/**
 * @author jbabar
 *
 */
public class CyberRequirement {

	// Requirement status
	// TODO: change them to enum
	public static final String addPlusAgree = "Add+Agree";
	public static final String toDo = "ToDo";
	public static final String add = "Add";
	public static final String omit = "Omit";
	public static final String unknown = "Unknown";
	public static final String notApplicable = "N/A";
	private static final boolean addQuotes = true;

	public static String formalized() {
		return "Formalized";
	}

	public static String reqComponent() {
		return "Req_Component";
	}

	public static String generatedBy() {
		return "Generated_By";
	}

	public static String generatedOn() {
		return "Generated_On";
	}

	public static String True() {
		return "True";
	}

	public static String False() {
		return "False";
	}
	private String type = ""; // this is the requirement classification type as defined by the TA1 tool
	private String id = "";
	private String text = "";
	private String context = ""; // this is the qualified name of the component
	private String rationale = "";
	private long date = 0L;
	private String tool = "";
	private String status = toDo;

//	transient private boolean inResolute = false;
//	transient private boolean inAgree = false;

	public CyberRequirement(long date, String tool, String status, String type, String id, String text, String context,
			String rationale) {
		this.date = date;
		this.tool = (tool == null ? unknown : tool);
		this.type = (type == null ? unknown : type);
		this.id = (id == null ? "" : id);
		this.text = (text == null ? "" : text);
		this.context = (context == null ? "" : context);
		this.rationale = (rationale == null ? notApplicable : rationale);
		this.status = ((status == null || toDo.equals(status)) ? toDo
				: add.equals(status) ? add : addPlusAgree.equals(status) ? addPlusAgree : omit);
	}

	public CyberRequirement(String type) {
		this(0L, null, null, type, null, null, null, null);
	}

	public CyberRequirement(CyberRequirement req) {
		this(req.getDate(), req.getTool(), req.getStatus(), req.getType(), req.getId(), req.getText(), req.getContext(),
				req.getRationale());
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getText() {
		return this.text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getContext() {
		return this.context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public boolean hasAgree() {
		return this.status == addPlusAgree;
	}

	public void setAgree() {
		this.status = addPlusAgree;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getRationale() {
		return rationale;
	}

	public void setRationale(String rationale) {
		this.rationale = rationale;
	}

	public long getDate() {
		return this.date;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public String getTool() {
		return this.tool;
	}

	public void setTool(String tool) {
		this.tool = tool;
	}

	/*
	 * public void insertClaim(BuiltInClaim claim, Resource resource) {
	 *
	 * if (claim == null) {
	 * return;
	 * }
	 *
	 * // Get modification context
	 * Classifier implementationContext = getImplementationClassifier(this.context);
	 * if (implementationContext == null) {
	 * throw new RuntimeException("Unable to determine requirement context.");
	 * }
	 * Classifier modificationContext = getModificationContext(implementationContext.getQualifiedName(), resource);
	 * if (modificationContext == null) {
	 * throw new RuntimeException("Unable to determine requirement context.");
	 * }
	 *
	 * FunctionDefinition currentClaimDef = getClaimDefinition(modificationContext);
	 * FunctionDefinition newClaimDef = claim.buildClaimDefinition(currentClaimDef);
	 *
	 * // Insert it into model
	 * insertClaimDefinition(modificationContext, newClaimDef);
	 *
	 * // Build Claim Call
	 * ProveStatement newClaimCall = null;
	 * if (newClaimDef != null) {
	 * ProveStatement currentClaimCall = getClaimCall(modificationContext);
	 * newClaimCall = claim.buildClaimCall(currentClaimCall);
	 *
	 * if (newClaimCall == null) {
	 * throw new RuntimeException("Unable to generate the claim call.");
	 * }
	 *
	 * // Insert it into model
	 * insertClaimCall(modificationContext, newClaimDef, newClaimCall);
	 *
	 * }
	 *
	 * }
	 */

	@Override
	public int hashCode() {
		return Objects.hash(context, type);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof CyberRequirement)) {
			return false;
		}
		CyberRequirement other = (CyberRequirement) obj;
		return Objects.equals(context, other.context) && Objects.equals(type, other.type);

	}

	public boolean completelyEqual(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof CyberRequirement)) {
			return false;
		}
		CyberRequirement other = (CyberRequirement) obj;
		return Objects.equals(context, other.context) && date == other.date && Objects.equals(id, other.id)
				&& Objects.equals(rationale, other.rationale) && Objects.equals(status, other.status)
				&& Objects.equals(text, other.text) && Objects.equals(tool, other.tool)
				&& Objects.equals(type, other.type);
	}

	private static String getStringLiteral(String string) {
		return addQuotes ? ("\"" + string + "\"") : string;
	}

	private ClaimContext createResoluteContext(String contextName, String contextValue) {
		ClaimContext context = ResoluteFactory.eINSTANCE.createClaimContext();
		context.setName(contextName);
		StringLiteral s = Aadl2Factory.eINSTANCE.createStringLiteral();
		s.setValue(getStringLiteral(contextValue));
		context.setVal(s);
		return context;
	}

	static DefaultAnnexLibrary getResoluteDefaultAnnexLibrary(AadlPackage pkg) {
		if (pkg == null) {
			throw new RuntimeException("Null AADL Package");
		}
		PrivatePackageSection priv8 = pkg.getOwnedPrivateSection();
		if (priv8 == null) {
			throw new RuntimeException("Could not find private package section for " + pkg);
		}

		for (AnnexLibrary library : priv8.getOwnedAnnexLibraries()) {
			if (library instanceof DefaultAnnexLibrary && library.getName().equalsIgnoreCase("resolute")) {
				return (DefaultAnnexLibrary) library;
			}
		}
		return null;
	}

	static ResoluteLibrary getResoluteLibrary(DefaultAnnexLibrary defResLib, boolean copy) {
		return (defResLib == null || !defResLib.getName().equalsIgnoreCase("resolute")) ? null
				: (copy ? EcoreUtil.copy((ResoluteLibrary) defResLib.getParsedAnnexLibrary())
						: (ResoluteLibrary) defResLib.getParsedAnnexLibrary());
	}

	static ResoluteLibrary getResoluteLibrary(AadlPackage pkg) {
		return getResoluteLibrary(getResoluteDefaultAnnexLibrary(pkg), true);

//		PrivatePackageSection priv8 = pkg.getOwnedPrivateSection();
//		if (priv8 == null) {
//			throw new RuntimeException("Could not find private package section for " + pkg);
//		}
//
//		for (AnnexLibrary library : priv8.getOwnedAnnexLibraries()) {
//			if (library instanceof DefaultAnnexLibrary && library.getName().equalsIgnoreCase("resolute")) {
//				DefaultAnnexLibrary defResLib = (DefaultAnnexLibrary) library;
//				return EcoreUtil.copy((ResoluteLibrary) defResLib.getParsedAnnexLibrary());
////				return (ResoluteLibrary) defResLib.getParsedAnnexLibrary();
//			}
//		}
//		return null;
	}

	private FunctionDefinition getClaimDefinition(AadlPackage modificationContext) {
		ResoluteLibrary resLib = getResoluteLibrary(modificationContext);
		if (resLib == null) {
			throw new RuntimeException("Could not find resolute library for " + modificationContext);
		}

		// If this function definition already exists, remove it
		Iterator<Definition> i = resLib.getDefinitions().iterator();
		while (i.hasNext()) {
			Definition def = i.next();
			if (def instanceof FunctionDefinition && def.getName().equalsIgnoreCase(this.getId())) {
				return (FunctionDefinition) def;
			}
		}

		return null;
	}

	private ProveStatement getClaimCall(Classifier modificationContext) {
		if (modificationContext == null) {
			return null;
		}
		for (AnnexSubclause annexSubclause : modificationContext.getOwnedAnnexSubclauses()) {
			DefaultAnnexSubclause defaultSubclause = (DefaultAnnexSubclause) annexSubclause;
			// See if there's a resolute annex
			if (defaultSubclause.getParsedAnnexSubclause() instanceof ResoluteSubclause) {
				ResoluteSubclause resoluteClause = (ResoluteSubclause) defaultSubclause.getParsedAnnexSubclause();
				// See if there are any 'prove' clauses
				for (AnalysisStatement as : resoluteClause.getProves()) {
					if (as instanceof ProveStatement) {
						ProveStatement prove = (ProveStatement) as;
						Expr expr = prove.getExpr();
						if (expr instanceof FnCallExpr) {
							FnCallExpr fnCall = (FnCallExpr) expr;
							FunctionDefinition fd = fnCall.getFn();
							if (fd.getName().equalsIgnoreCase(this.id)) {
								return prove;
							}
						}
					}
				}
				break;
			}
		}
		return null;
	}

	public Map.Entry<BuiltInClaim, FunctionDefinition> insertClaimDef(BuiltInClaim claim, Resource resource) {
		if (claim == null) {
			return null;
		}

		// Get modification context
		AadlPackage aadlPkg = getResoluteModificationContext(CaseUtils.CASE_REQUIREMENTS_NAME, resource);
		if (aadlPkg == null) {
			throw new RuntimeException("Unable to determine requirement context.");
		}

		FunctionDefinition currentClaimDef = getClaimDefinition(aadlPkg);
		FunctionDefinition newClaimDef = claim.buildClaimDefinition(currentClaimDef);

		if (claim instanceof BaseClaim || claim instanceof AgreePropCheckedClaim) {
			// System.out.println("Comments in Base Claim " + getId() + ": " + newClaimDef.getOwnedComments().size());
			ClaimBody claimBody = (ClaimBody) newClaimDef.getBody();

			// Annotate claim with requirement information
			EList<ClaimContext> contexts = claimBody.getContext();
			contexts.clear();
			contexts.add(createResoluteContext(generatedBy(), this.getTool()));
			contexts.add(createResoluteContext(generatedOn(),
					DateFormat.getDateInstance().format(new Date(this.getDate() * 1000))));
			contexts.add(createResoluteContext(reqComponent(), this.getContext()));
			contexts.add(createResoluteContext(formalized(),
					(getStatus() == CyberRequirement.addPlusAgree ? True() : False())));
		}

		// Insert it into model
		insertClaimDefinition(aadlPkg, newClaimDef);

		return new AbstractMap.SimpleImmutableEntry<>(claim, newClaimDef);
	}

	private void insertClaimDefinition(AadlPackage pkg, FunctionDefinition claimDefinition) {

			if (pkg == null) {
				throw new RuntimeException("Null AADL Package.");
			}

			if (claimDefinition == null) {
				throw new RuntimeException(
						"Null claim definition cannnot be inserted into package (" + pkg.getQualifiedName() + ").");
			}

			PrivatePackageSection priv8 = pkg.getOwnedPrivateSection();
			if (priv8 == null) {
				priv8 = pkg.createOwnedPrivateSection();
			}

			DefaultAnnexLibrary defResLib = null;
			ResoluteLibrary resLib = null;
			for (AnnexLibrary library : priv8.getOwnedAnnexLibraries()) {
				if (library instanceof DefaultAnnexLibrary && library.getName().equalsIgnoreCase("resolute")) {
					defResLib = (DefaultAnnexLibrary) library;
					resLib = EcoreUtil.copy((ResoluteLibrary) defResLib.getParsedAnnexLibrary());
					break;
				}
			}

			if (defResLib == null) {
				defResLib = (DefaultAnnexLibrary) priv8
						.createOwnedAnnexLibrary(Aadl2Package.eINSTANCE.getDefaultAnnexLibrary());
				defResLib.setName("resolute");
				defResLib.setSourceText("{** **}");

				resLib = ResoluteFactory.eINSTANCE.createResoluteLibrary();
			}

			// If this function definition already exists, remove it
			Iterator<Definition> i = resLib.getDefinitions().iterator();
			while (i.hasNext()) {
				Definition def = i.next();
				if (def.getName().equalsIgnoreCase(claimDefinition.getName())) {
					i.remove();
					break;
				}
			}

			resLib.getDefinitions().add(claimDefinition);
			defResLib.setParsedAnnexLibrary(resLib);
		}

	public void insertClaimCall(BuiltInClaim claim, FunctionDefinition claimDef, Resource resource) {
		if (claim == null || claimDef == null) {
			return;
		}

		// Get modification context
		Classifier implementationContext = getImplementationClassifier(this.context);
		if (implementationContext == null) {
			throw new RuntimeException("Unable to determine requirement context.");
		}
		Classifier modificationContext = getModificationContext(implementationContext.getQualifiedName(), resource);
		if (modificationContext == null) {
			throw new RuntimeException("Unable to determine requirement context.");
		}

		// Build Claim Call
		ProveStatement currentClaimCall = getClaimCall(modificationContext);
		ProveStatement newClaimCall = claim.buildClaimCall(currentClaimCall);

		if (newClaimCall == null) {
			throw new RuntimeException("Unable to generate the claim call.");
		}

		// Insert it into model
		insertClaimCall(modificationContext, claimDef, newClaimCall);
	}

	private void insertClaimCall(Classifier modificationContext, FunctionDefinition claimDefinition,
			ProveStatement claimCall) {

		if (modificationContext == null) {
			return;
		}

		DefaultAnnexSubclause subclause = null;
		ResoluteSubclause resclause = null;
		for (AnnexSubclause sc : modificationContext.getOwnedAnnexSubclauses()) {
			if (sc instanceof DefaultAnnexSubclause && sc.getName().equalsIgnoreCase("resolute")) {
				subclause = (DefaultAnnexSubclause) sc;
				resclause = EcoreUtil.copy((ResoluteSubclause) subclause.getParsedAnnexSubclause());
				break;
			}
		}

		if (subclause == null) {
			subclause = (DefaultAnnexSubclause) modificationContext
					.createOwnedAnnexSubclause(Aadl2Package.eINSTANCE.getDefaultAnnexSubclause());
			subclause.setName("resolute");
			subclause.setSourceText("{** **}");

			resclause = ResoluteFactory.eINSTANCE.createResoluteSubclause();
		}

		// If the prove statement already exists, remove it
		Iterator<AnalysisStatement> i = resclause.getProves().iterator();
		while (i.hasNext()) {
			AnalysisStatement as = i.next();
			if (as instanceof ProveStatement) {
				ProveStatement prove = (ProveStatement) as;
				Expr expr = prove.getExpr();
				if (expr instanceof FnCallExpr) {
					FnCallExpr fnCallExpr = (FnCallExpr) expr;
					if (fnCallExpr.getFn().getName().equalsIgnoreCase(claimDefinition.getName())) {
						i.remove();
						break;
					}
				}
			}
		}

		resclause.getProves().add(claimCall);
		subclause.setParsedAnnexSubclause(resclause);
	}

	public FunctionDefinition removeClaimDef(BuiltInClaim claim, Resource resource) {
		if (claim == null) {
			throw new RuntimeException("Cannot remove claim definition for a NULL claim.");
		}

		if (resource == null) {
			throw new RuntimeException("Cannot remove claim definition from a NULL resource.");
		}

		// Get modification context
		AadlPackage aadlPkg = getResoluteModificationContext(CaseUtils.CASE_REQUIREMENTS_NAME, resource);
		if (aadlPkg == null) {
			throw new RuntimeException("Unable to determine requirement context.");
		}

		if (claim instanceof BaseClaim) {
			return removeBaseClaimDefinition(aadlPkg);
		} else if (claim instanceof AgreePropCheckedClaim) {
			removeBuiltInClaimDefinition(aadlPkg, claim);
			return null;
		} else {
			throw new RuntimeException("Can only remove BaseClaim or AgreePropCheckedClaim. Cannot remove: " + claim);
		}
	}

	private FunctionDefinition removeBaseClaimDefinition(AadlPackage pkg) {
		DefaultAnnexLibrary defResLib = getResoluteDefaultAnnexLibrary(pkg);
		if (defResLib == null) {
			throw new RuntimeException("Could not find resolute library for " + pkg);
		}

		ResoluteLibrary resLib = getResoluteLibrary(defResLib, true);
		if (resLib == null) {
			throw new RuntimeException("Could not find resolute library for " + pkg);
		}

		// If this function definition already exists, remove it
		Iterator<Definition> i = resLib.getDefinitions().iterator();
		while (i.hasNext()) {
			Definition def = i.next();
			if (def instanceof FunctionDefinition && def.getName().equalsIgnoreCase(this.getId())) {
				i.remove();
				defResLib.setParsedAnnexLibrary(resLib);
				return (FunctionDefinition) def;
			}
		}
		return null;
	}

	private boolean removeBuiltInClaimDefinition(AadlPackage pkg, BuiltInClaim claim) {
		DefaultAnnexLibrary defResLib = getResoluteDefaultAnnexLibrary(pkg);
		if (defResLib == null) {
			throw new RuntimeException("Could not find resolute library for " + pkg);
		}

		ResoluteLibrary resLib = getResoluteLibrary(defResLib, true);
		if (resLib == null) {
			throw new RuntimeException("Could not find resolute library for " + pkg);
		}

		// If this function definition already exists, remove it
		Iterator<Definition> i = resLib.getDefinitions().iterator();
		while (i.hasNext()) {
			Definition def = i.next();
			if (def instanceof FunctionDefinition && def.getName().equalsIgnoreCase(this.getId())) {
				FunctionDefinition fd = (FunctionDefinition) def;
				DefinitionBody body = fd.getBody();
				Expr expr = removeClaim(body.getExpr(), claim.getName());
				body.setExpr(expr == null ? Create.FALSE() : expr);
				if (body instanceof ClaimBody) {
					ClaimBody claimBody = (ClaimBody) body;
					for (ClaimContext context : claimBody.getContext()) {
						if (context.getName().equalsIgnoreCase(formalized())) {
							StringLiteral s = Aadl2Factory.eINSTANCE.createStringLiteral();
							s.setValue(getStringLiteral(False()));
							context.setVal(s);
							break;
						}
					}
				}
				defResLib.setParsedAnnexLibrary(resLib);
				return true;
			}
		}
		return false;
	}

	/**
	 * Removes expressions matching the name argument from the expr argument.
	 * The name argument is a {@link String} that represents the name of the function call (for example, Agree_Prop_Checked).
	 * <p>
	 * This method assumes that the expr argument is composed of {@link FnCallExpr} and {@link BinaryExpr} elements.
	 * @param expr	expression to be modified
	 * @param name	name of the function call to remove
	 * @return		the expression with any references to the name argument removed; could be null if the top-level expression is a function call that matches the name argument.
	 */
	private Expr removeClaim(Expr expr, String name) {
		if (expr instanceof FnCallExpr) {
			FnCallExpr fnCallExpr = (FnCallExpr) expr;
			if (fnCallExpr.getFn().getName().equalsIgnoreCase(name)) {
				// found name
				expr = null;
			}
		} else if (expr instanceof BinaryExpr) {
			BinaryExpr binaryExpr = (BinaryExpr) expr;
			Expr left = removeClaim(binaryExpr.getLeft(), name);
			Expr right = removeClaim(binaryExpr.getRight(), name);
			if (left == null) {
				// left side is name
				expr = right;
			} else if (right == null) {
				// right side is name
				expr = left;
			} else {
				binaryExpr.setLeft(left);
				binaryExpr.setRight(right);
			}
		} else {
			// unexpected expression
			throw new RuntimeException(
					"Unexpected expression encountered. Exepecting FnCallExpr or BinaryExpr but found this: " + expr);
		}
		return expr;
	}

	public boolean removeClaimCall(FunctionDefinition claimDef, Resource resource) {
		if (claimDef == null || resource == null) {
			throw new RuntimeException("Unable to determine requirement context.");
		}

		// Get modification context
		Classifier implementationContext = getImplementationClassifier(this.context);
		if (implementationContext == null) {
			throw new RuntimeException("Unable to determine requirement context.");
		}
		Classifier modificationContext = getModificationContext(implementationContext.getQualifiedName(), resource);
		if (modificationContext == null) {
			throw new RuntimeException("Unable to determine requirement context.");
		}

		DefaultAnnexSubclause subclause = null;
		ResoluteSubclause resclause = null;
		for (AnnexSubclause sc : modificationContext.getOwnedAnnexSubclauses()) {
			if (sc instanceof DefaultAnnexSubclause && sc.getName().equalsIgnoreCase("resolute")) {
				subclause = (DefaultAnnexSubclause) sc;
				resclause = EcoreUtil.copy((ResoluteSubclause) subclause.getParsedAnnexSubclause());
				break;
			}
		}

		if (subclause == null) {
			throw new RuntimeException("Unable to determine requirement resolute annex: " + claimDef);
		}

		// If the prove statement already exists, remove it
		Iterator<AnalysisStatement> i = resclause.getProves().iterator();
		while (i.hasNext()) {
			AnalysisStatement as = i.next();
			if (as instanceof ProveStatement) {
				ProveStatement prove = (ProveStatement) as;
				Expr expr = prove.getExpr();
				if (expr instanceof FnCallExpr) {
					FnCallExpr fnCallExpr = (FnCallExpr) expr;
					if (fnCallExpr.getFn().getName().equalsIgnoreCase(claimDef.getName())) {
						i.remove();
						subclause.setParsedAnnexSubclause(resclause);
						return true;
					}
				}
			}
		}

		return false;
	}

	public void insertAgree(String qualifiedComponentName, Resource resource) {

		Classifier modificationContext = getModificationContext(qualifiedComponentName, resource);
		if (modificationContext == null) {
			throw new RuntimeException("Unable to determine requirement context.");
		}

		DefaultAnnexSubclause subclause = null;
		AgreeContractSubclause agreeSubclause = null;
		for (AnnexSubclause sc : modificationContext.getOwnedAnnexSubclauses()) {
			if (sc instanceof DefaultAnnexSubclause && sc.getName().equalsIgnoreCase("agree")) {
				subclause = (DefaultAnnexSubclause) sc;
				agreeSubclause = EcoreUtil.copy((AgreeContractSubclause) subclause.getParsedAnnexSubclause());
				break;
			}
		}

		if (subclause == null) {
			subclause = (DefaultAnnexSubclause) modificationContext
					.createOwnedAnnexSubclause(Aadl2Package.eINSTANCE.getDefaultAnnexSubclause());
			subclause.setName("agree");
			subclause.setSourceText("{** **}");

			agreeSubclause = AgreeFactory.eINSTANCE.createAgreeContractSubclause();
		} else {
			// If an agree statement with this id already exists, do nothing
			for (SpecStatement spec : ((AgreeContract) agreeSubclause.getContract()).getSpecs()) {
				if (spec instanceof NamedSpecStatement) {
					if (((NamedSpecStatement) spec).getName().equalsIgnoreCase(this.id)) {
						Dialog.showError("Formalize Requirement",
								"Requirement " + this.id + " has already been formalized in AGREE.");
						return;
					}
				}
			}
		}

		String assume = "assume ";
		if (!this.id.isEmpty()) {
			assume += this.id + " ";
		}
		assume += "\"" + this.text + "\" : false;";
		AgreeAnnexParser parser = new AgreeAnnexParser();
		NamedSpecStatement agreeSpec = parser.parseNamedSpecStatement(assume);

		AgreeContract contract = (AgreeContract) agreeSubclause.getContract();
		if (contract == null) {
			// Add an empty contract to agreeSubclause
			contract = AgreeFactory.eINSTANCE.createAgreeContract();
			agreeSubclause.setContract(contract);
		}

		contract.getSpecs().add(agreeSpec);
		subclause.setParsedAnnexSubclause(agreeSubclause);
	}

	public boolean removeAgree(String qualifiedComponentName, Resource resource) {

		Classifier modificationContext = getModificationContext(qualifiedComponentName, resource);
		if (modificationContext == null) {
			throw new RuntimeException("Unable to determine requirement context.");
		}

		DefaultAnnexSubclause subclause = null;
		AgreeContractSubclause agreeSubclause = null;
		for (AnnexSubclause sc : modificationContext.getOwnedAnnexSubclauses()) {
			if (sc instanceof DefaultAnnexSubclause && sc.getName().equalsIgnoreCase("agree")) {
				subclause = (DefaultAnnexSubclause) sc;
				agreeSubclause = EcoreUtil.copy((AgreeContractSubclause) subclause.getParsedAnnexSubclause());
				break;
			}
		}

		if (agreeSubclause != null) {
			// If an agree statement with this id already exists, remove it
			for (ListIterator<SpecStatement> iterator = ((AgreeContract) agreeSubclause.getContract()).getSpecs()
					.listIterator(); iterator.hasNext();) {
				SpecStatement spec = iterator.next();
				if (spec instanceof NamedSpecStatement) {
					if (((NamedSpecStatement) spec).getName().equalsIgnoreCase(this.id)) {
						// found; remove and return
						iterator.remove();
						subclause.setParsedAnnexSubclause(agreeSubclause);
						return true;
					}
				}
			}
		}
		return false;
	}

	static AadlPackage getResoluteModificationContext(String qualifiedName, Resource resource) {
		// Get modification context
		AadlPackage modificationContext = null;
		TreeIterator<EObject> x = EcoreUtil.getAllContents(resource, true);
		while (x.hasNext()) {
			EObject next = x.next();
			if (next instanceof NamedElement) {
				NamedElement nextElement = (NamedElement) next;
				if (nextElement.getQualifiedName() != null
						&& nextElement.getQualifiedName().equalsIgnoreCase(qualifiedName)) {
					if (nextElement instanceof AadlPackage) {
						modificationContext = (AadlPackage) nextElement;
					}
					break;
				}
			}
		}
		return modificationContext;
	}

	static Classifier getModificationContext(String qualifiedName, Resource resource) {
		// Get modification context
		Classifier modificationContext = null;
		TreeIterator<EObject> x = EcoreUtil.getAllContents(resource, true);
		while (x.hasNext()) {
			EObject next = x.next();
			if (next instanceof NamedElement) {
				NamedElement nextElement = (NamedElement) next;
				if (nextElement.getQualifiedName() != null
						&& nextElement.getQualifiedName().equalsIgnoreCase(qualifiedName)) {
					if (nextElement instanceof Subcomponent) {
						Subcomponent sub = (Subcomponent) nextElement;
						modificationContext = sub.getComponentType();
					} else {
						modificationContext = (Classifier) nextElement;
					}
					break;
				}
			}
		}
		return modificationContext;
	}

	/**
	 * Return the component implementation referred to by the qualified name
	 * @param qualifiedName
	 * @return
	 */
	public static Classifier getImplementationClassifier(String qualifiedName) {
		Classifier classifier = null;
		if (!qualifiedName.contains("::")) {
			return null;
		}
		String pkgName = Aadl2Util.getPackageName(qualifiedName);

		// The qualified name should either refer to a component implementation
		// or a component implementation's subcomponent or connection
		// A component implementation qualified name will appear as <Package>::<Component Implementation>
		// A subcomponent/connection qualified name will appear as <Package>::<Component Implementation>.<Subcomponent/Connection>
		// Since we want to return the component implementation, we want to truncate the subcomponent from the qualified name

//		String[] parts = qualifiedName.split("\\.");
//		String compImplName = "";
//		if (parts.length > 0) {
//			compImplName = parts[0] + "." + parts[1];
//		}

		int lastDot = qualifiedName.lastIndexOf('.');
		String compImplName = qualifiedName.substring(0, (lastDot == -1 ? qualifiedName.length() : lastDot));

		for (AadlPackage pkg : TraverseProject.getPackagesInProject(TraverseProject.getCurrentProject())) {
			if (pkg.getName().equalsIgnoreCase(pkgName)) {
				for (Classifier c : EcoreUtil2.getAllContentsOfType(pkg, Classifier.class)) {
					if (c.getQualifiedName().equalsIgnoreCase(compImplName)) {
						classifier = c;
						break;
					}
				}
				break;
			}
		}

		return classifier;
	}

	public Classifier getImplementationClassifier() {
		return getImplementationClassifier(this.context);
	}

	public FunctionDefinition getResoluteClaim() {
		// Get AADL Package
		AadlPackage aadlPkg = CaseUtils.getCaseRequirementsPackage();
		if (aadlPkg == null) {
			return null;
		}

		// Get private section
		PrivatePackageSection privateSection = aadlPkg.getOwnedPrivateSection();
		if (privateSection == null) {
			return null;
		}

		// Get Resolute annex
		FunctionDefinition fnDef = null;
		for (AnnexLibrary annexLib : privateSection.getOwnedAnnexLibraries()) {
			if (annexLib instanceof DefaultAnnexLibrary && annexLib.getName().equalsIgnoreCase("resolute")) {
				DefaultAnnexLibrary defaultLib = (DefaultAnnexLibrary) annexLib;
				ResoluteLibrary resLib = (ResoluteLibrary) defaultLib.getParsedAnnexLibrary();
				// Iterate over requirements
				for (Definition def : resLib.getDefinitions()) {
					if (def instanceof FunctionDefinition && def.getName().equalsIgnoreCase(this.id)) {
						fnDef = (FunctionDefinition) def;
						break;
					}
				}
				break;
			}
		}

		return fnDef;
	}

	public static IFile getContainingFile(String context) {
		Classifier classifier = getImplementationClassifier(context);
		if (classifier == null) {
			return null;
		}
		return Filesystem.getFile(classifier.eResource().getURI());
	}

	public IFile getContainingFile() {
		return getContainingFile(this.context);
	}

	/**
	 * Claim String expected format: [status, date, tool, type, context] description
	 * @param claimString
	 * @return
	 */
	static CyberRequirement parseClaimString(String id, String claimString) {
		if (claimString == null || !claimString.matches(".+\\[.+\\]\\s.+")) {
			return null;
		}

		int start = claimString.indexOf('[');
		int end = claimString.indexOf(']');
		String text = claimString.substring(end + 1);
		String part1 = claimString.substring(start + 1, end);
		String[] parts = part1.replaceAll("\\s", "").split(",");
		String status = parts[0];
		String tool = parts[2];
		String type = parts[3];
		String context = parts[4];
		String rationale = "N/A";
		long date;

		try {
			date = DateFormat.getDateInstance().parse(parts[1]).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
			date = 0L;
		}
		return new CyberRequirement(date, tool, status, type, id, text, context, rationale);
	}

	/**
	 * Claim String expected format: [status, date, tool, type, context] description
	 * @param claimBody
	 * @return
	 */
	static CyberRequirement parseClaimString(String id, String claimString, ClaimBody claimBody) {
		if (id == null || claimBody == null || claimString == null || !claimString.matches(".+\\[.+\\]\\s.+")) {
			throw new RuntimeException("Invalid arguments: (id : " + id + ") (claimString :" + claimString
					+ ") (claimBody : " + claimBody + ")");
		}

		String dateString = getContext(claimBody, generatedOn());
		long date;
		try {
			date = DateFormat.getDateInstance().parse(dateString).getTime() / 1000;
		} catch (ParseException e) {
			e.printStackTrace();
			System.out.println(
					"Error in parsing date from resolute requirement: " + dateString);
			date = 0L;
		}

		String tool = getContext(claimBody, generatedBy());
		String component = getContext(claimBody, reqComponent());
		String status = getContext(claimBody, formalized()).equalsIgnoreCase(True()) ? CyberRequirement.addPlusAgree
				: CyberRequirement.add;

		int start = claimString.indexOf('[');
		int end = claimString.indexOf(']');
		String text = claimString.substring(end + 1);
		String type = claimString.substring(start + 1, end);

		return new CyberRequirement(date, tool, status, type, id, text, component,
				CyberRequirement.notApplicable);
	}

	static String getContext(ClaimBody claimBody, String context) {
		if (claimBody != null) {
			for (ClaimContext c : claimBody.getContext()) {
				if (c.getName().equalsIgnoreCase(context)) {
					String val = c.getVal().getValue();
					if (addQuotes) {
						return val.substring(1, val.length() - 1);
					} else {
						return val;
					}
				}
			}
		}
		return "";
	}
}
