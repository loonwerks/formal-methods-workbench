package com.collins.fmw.cyres.architecture.requirements;

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.EcoreUtil2;
import org.osate.aadl2.Aadl2Package;
import org.osate.aadl2.AadlPackage;
import org.osate.aadl2.AnnexLibrary;
import org.osate.aadl2.AnnexSubclause;
import org.osate.aadl2.Classifier;
import org.osate.aadl2.DefaultAnnexLibrary;
import org.osate.aadl2.DefaultAnnexSubclause;
import org.osate.aadl2.PrivatePackageSection;
import org.osate.aadl2.util.Aadl2Util;

import com.collins.fmw.cyres.util.plugin.Filesystem;
import com.collins.fmw.cyres.util.plugin.TraverseProject;
import com.rockwellcollins.atc.agree.agree.NamedSpecStatement;
import com.rockwellcollins.atc.resolute.resolute.AnalysisStatement;
import com.rockwellcollins.atc.resolute.resolute.Arg;
import com.rockwellcollins.atc.resolute.resolute.Definition;
import com.rockwellcollins.atc.resolute.resolute.Expr;
import com.rockwellcollins.atc.resolute.resolute.FnCallExpr;
import com.rockwellcollins.atc.resolute.resolute.FunctionDefinition;
import com.rockwellcollins.atc.resolute.resolute.ProveStatement;
import com.rockwellcollins.atc.resolute.resolute.ResoluteFactory;
import com.rockwellcollins.atc.resolute.resolute.ResoluteLibrary;
import com.rockwellcollins.atc.resolute.resolute.ResoluteSubclause;

public class CyberRequirement {

	private String type = ""; // this is the requirement classification type as defined by the TA1 tool
	private String id = "";
	private String text = "";
	private String context = ""; // this is the qualified name of the component
	private Classifier contextClassifier = null;
	private boolean agree = false;
	private String rationale = "";

	private NamedSpecStatement agreeSpec = null;
	private FunctionDefinition claimDefinition = null;
	private ProveStatement claimCall = null;

	private Classifier modificationContext = null;
//	private XtextEditor editor = null;

	public CyberRequirement(String type) {
		this.type = type;
	}

	public CyberRequirement(String type, String id, String text, String context, boolean agree,
			String rationale) {
		this.type = type;
		this.id = id;
		this.text = text;
		this.context = context;
		this.contextClassifier = getClassifier(context);
		this.agree = agree;
		this.rationale = rationale;
	}

	public CyberRequirement(String type, String id, String text, Classifier contextClassifier, boolean agree,
			String rationale) {
		this.type = type;
		this.id = id;
		this.text = text;
		this.context = contextClassifier.getQualifiedName();
		this.contextClassifier = contextClassifier;
		this.agree = agree;
		this.rationale = rationale;
	}

	public String getType() {
		return this.type;
	}

	public String getId() {
		return this.id;
	}

	public String getText() {
		return this.text;
	}

	public String getContext() {
		return this.context;
	}

	public boolean hasAgree() {
		return this.agree;
	}

	public Classifier getContextClassifier() {
		return this.contextClassifier;
	}

	public FunctionDefinition getClaimDefinition() {
		Classifier classifier = getClassifier(this.context);
		for (AnnexSubclause annexSubclause : classifier.getOwnedAnnexSubclauses()) {
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
								this.claimDefinition = fd;
								break;
							}
						}
					}
				}
				break;
			}
		}
		return this.claimDefinition;
	}

	public ProveStatement getClaimCall() {
		Classifier classifier = getClassifier(this.context);
		for (AnnexSubclause annexSubclause : classifier.getOwnedAnnexSubclauses()) {
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
								this.claimCall = prove;
								break;
							}
						}
					}
				}
				break;
			}
		}
		return this.claimCall;
	}

//	private boolean setContextClassifier() {
//		this.contextClassifier = getClassifier(this.context);
//		return this.contextClassifier == null;
//	}

//	public void setId(String id) {
//		this.id = id;
//	}
//
//	public void setText(String text) {
//		this.text = text;
//	}
//
//	public void setContext(Classifier context) {
//		this.context = context;
//	}
//
//	public void setAgree(boolean agree) {
//		this.agree = agree;
//	}
//
//	public void setRationale(String rationale) {
//		this.rationale = rationale;
//	}

	public void setClaimDefinition(FunctionDefinition claimDefinition) {
		this.claimDefinition = claimDefinition;
	}

	public void setClaimCall(ProveStatement claimCall) {
		this.claimCall = claimCall;
	}

	public void setAgreeSpec(NamedSpecStatement spec) {
		this.agreeSpec = spec;
	}

	public void buildClaimDefinition(List<Arg> fnArgs, Expr body) {

		ClaimBuilder builder = new ClaimBuilder(this.id);

//		builder.addArg(Create.arg("c", Create.baseType("component")));
//		for (Arg arg : fnArgs) {
//			builder.addArg(arg);
//		}
		fnArgs.forEach(a -> builder.addArg(a));

//		if (this.agree) {
//			builder.addArg(Create.arg("property_id", Create.baseType("string")));
//		}

		builder.addClaimString("[" + this.type + "] " + this.text);

//		if (this.agree) {
////			builder.setClaimExpr(
////					Create.fnCall(ClaimsManager.getInstance().getClaim(ClaimsManager.AGREE_PROP_CHECKED)));
//
//			builder.setClaimExpr(Create.FALSE());
//		} else {
//			builder.setClaimExpr(Create.FALSE());
//		}
//		builder.setClaimExpr(Create.FALSE());
//		builder.setClaimExpr(body);
		builder.addClaimExpr(body);

		this.claimDefinition = builder.build();

	}

//	@Override
//	public String toString() {
//
//	}

	public void buildClaimCall(List<Expr> fnCallArgs) {
		if (this.claimDefinition == null) {
			throw new RuntimeException("Claim definition must be defined before generating a call to it.");
		}

		ClaimCallBuilder builder = new ClaimCallBuilder(this.claimDefinition);
		fnCallArgs.forEach(a -> builder.addArg(a));
		this.claimCall = builder.build();
	}

	public void insertClaimDef() {

		if (this.modificationContext == null) {
			return;
		}

		AadlPackage pkg = EcoreUtil2.getContainerOfType(this.modificationContext, AadlPackage.class);
		if (pkg == null) {
			throw new RuntimeException("Could not find containing package for " + this.modificationContext);
		}

		PrivatePackageSection priv8 = pkg.getOwnedPrivateSection();
//		PrivatePackageSection priv8 = pkg.getPrivateSection();
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

//		ArrayList<Arg> args = new ArrayList<>();
//		args.add(Create.arg("c", Create.baseType("component")));
//		buildClaimDefinition(args);

		// If this function definition already exists, remove it
		Iterator<Definition> i = resLib.getDefinitions().iterator();
		while (i.hasNext()) {
			Definition def = i.next();
			if (def.getName().equalsIgnoreCase(this.claimDefinition.getName())) {
				i.remove();
				break;
			}
		}

		resLib.getDefinitions().add(this.claimDefinition);
		defResLib.setParsedAnnexLibrary(resLib);

	}

	public void insertClaimCall() {

		if (this.modificationContext == null) {
			return;
		}

		DefaultAnnexSubclause subclause = null;
		ResoluteSubclause resclause = null;
		for (AnnexSubclause sc : this.modificationContext.getOwnedAnnexSubclauses()) {
			if (sc instanceof DefaultAnnexSubclause && sc.getName().equalsIgnoreCase("resolute")) {
				subclause = (DefaultAnnexSubclause) sc;
				resclause = EcoreUtil.copy((ResoluteSubclause) subclause.getParsedAnnexSubclause());
				break;
			}
		}

		if (subclause == null) {
			subclause = (DefaultAnnexSubclause) this.modificationContext
					.createOwnedAnnexSubclause(Aadl2Package.eINSTANCE.getDefaultAnnexSubclause());
			subclause.setName("resolute");
			subclause.setSourceText("{** **}");

			resclause = ResoluteFactory.eINSTANCE.createResoluteSubclause();
		}

//		List<Expr> args = new ArrayList<>();
//		args.add(Create.THIS());
//		if (this.agree) {
//			args.add(Create.stringExpr(this.id));
//		}
//		buildClaimCall(args);

		// If the prove statement already exists, remove it
		Iterator<AnalysisStatement> i = resclause.getProves().iterator();
		while (i.hasNext()) {
			AnalysisStatement as = i.next();
			if (as instanceof ProveStatement) {
				ProveStatement prove = (ProveStatement) as;
				Expr expr = prove.getExpr();
				if (expr instanceof FnCallExpr) {
					FnCallExpr fnCallExpr = (FnCallExpr) expr;
					if (fnCallExpr.getFn().getName().equalsIgnoreCase(this.claimDefinition.getName())) {
						i.remove();
						break;
					}
				}
			}
		}

		resclause.getProves().add(this.claimCall);
		subclause.setParsedAnnexSubclause(resclause);

	}

//	public void insertAgree() {
//		if (this.modificationContext == null) {
//			return;
//		}
//
//		DefaultAnnexSubclause subclause = null;
//		AgreeContractSubclause agreeSubclause = null;
//		for (AnnexSubclause sc : this.modificationContext.getOwnedAnnexSubclauses()) {
//			if (sc instanceof DefaultAnnexSubclause && sc.getName().equalsIgnoreCase("agree")) {
//				subclause = (DefaultAnnexSubclause) sc;
//				agreeSubclause = EcoreUtil.copy((AgreeContractSubclause) subclause.getParsedAnnexSubclause());
//				break;
//			}
//		}
//
//		if (subclause == null) {
//			subclause = (DefaultAnnexSubclause) this.modificationContext
//					.createOwnedAnnexSubclause(Aadl2Package.eINSTANCE.getDefaultAnnexSubclause());
//			subclause.setName("agree");
//			subclause.setSourceText("{** **}");
//
//			agreeSubclause = AgreeFactory.eINSTANCE.createAgreeContractSubclause();
//		}
//
//		String assume = "assume ";
//		if (!id.isEmpty()) {
//			assume += id + " ";
//		}
//		assume += "\"" + this.text + "\" : FALSE;";
//		AgreeAnnexParser parser = new AgreeAnnexParser();
//		this.agreeSpec = parser.parseNamedSpecStatement(assume);
//
////		agreeSubclause.getProves().add(this.claimCall);
//		AgreeContract contract = (AgreeContract) agreeSubclause.getContract();
//		contract.getSpecs().add(this.agreeSpec);
//		subclause.setParsedAnnexSubclause(agreeSubclause);
//	}

//	public boolean modify(BuiltInClaim claim) {
//		return claim.modify(this.claimDefinition, this.claimCall);
//	}

	public static Classifier getClassifier(String qualifiedName) {
		Classifier classifier = null;
		if (!qualifiedName.contains("::")) {
			return null;
		}
		String pkgName = Aadl2Util.getPackageName(qualifiedName);

		for (AadlPackage pkg : TraverseProject.getPackagesInProject(TraverseProject.getCurrentProject())) {
			if (pkg.getName().equalsIgnoreCase(pkgName)) {
				for (Classifier c : EcoreUtil2.getAllContentsOfType(pkg, Classifier.class)) {
					if (c.getQualifiedName().equalsIgnoreCase(qualifiedName)) {
						classifier = c;
						break;
					}
				}
				break;
			}
		}

		return classifier;
	}

	public IFile getContainingFile() {
		return Filesystem.getFile(this.contextClassifier.eResource().getURI());
	}

//	public static XtextEditor getEditor(IFile file) {
//		IWorkbenchPage page = null;
//		IEditorPart part = null;
//
//		if (file.exists()) {
//			page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
//			IEditorDescriptor desc = PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor(file.getName());
//			try {
//				part = page.openEditor(new FileEditorInput(file), desc.getId());
//			} catch (PartInitException e) {
//				e.printStackTrace();
//			}
//		}
//
//		if (part == null) {
//			return null;
//		}
//
//		XtextEditor xedit = null;
//		xedit = (XtextEditor) part;
//
//		return xedit;
//	}
//
//	public static void closeEditor(XtextEditor editor, boolean save) {
//		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
//		if (save) {
//			page.saveEditor(editor, false);
//		}
//		page.closeEditor(editor, false);
//	}
//
//	public void insert(XtextResource resource) {
//
//		this.modificationContext = null;
//
//		TreeIterator<EObject> x = EcoreUtil.getAllContents(resource, true);
//		while (x.hasNext()) {
//			EObject next = x.next();
//			if (next instanceof Classifier) {
//				Classifier nextClass = (Classifier) next;
//				if (nextClass.getName().equalsIgnoreCase(this.contextClassifier.getName())) {
//					this.modificationContext = nextClass;
//					break;
//				}
//			}
//		}
//
//		if (this.type == null || this.type.isEmpty()) {
//			throw new RuntimeException("Cannot insert requirement because it does not have a type.");
//		} else if (this.id == null || this.id.isEmpty()) {
//			throw new RuntimeException("Cannot insert requirement because it does not have an ID.");
//		} else if (this.modificationContext == null) {
//			throw new RuntimeException("Cannot insert requirement because the modification context is missing.");
//		}
//
////		insertClaimDef();
////		insertClaimCall();
////		ArrayList<Arg> args = new ArrayList<>();
////		if (this.agree) {
////			insertAgree();
////			AgreePropCheckedClaim agreePropChecked = new AgreePropCheckedClaim();
////			agreePropChecked.insert(this.claimCall);
////		}
//
//		// Claim definition
//		ArrayList<Arg> defArgs = new ArrayList<>();
//		defArgs.add(Create.arg("c", Create.baseType("component")));
//		Expr body = Create.FALSE();
//		buildClaimDefinition(defArgs, body);
////		insertClaimDef();
//
//		// Claim call
//		List<Expr> callArgs = new ArrayList<>();
//		callArgs.add(Create.THIS());
//		buildClaimCall(callArgs);
////		insertClaimCall();
//
//		BaseClaim baseClaim = new BaseClaim();
//		baseClaim.insert(this);
//
//		// AGREE
//		if (this.agree) {
//			AgreePropCheckedClaim agreePropChecked = new AgreePropCheckedClaim();
//			agreePropChecked.insert(this.claimDefinition, this.claimCall);
//		}
//
//		// Insert
//		insertClaimDef();
//		insertClaimCall();
//
//		this.modificationContext = null;
//	}
//
//	public void modify(XtextResource resource, BuiltInClaim claim) {
//
//		this.modificationContext = null;
//
//		TreeIterator<EObject> x = EcoreUtil.getAllContents(resource, true);
//		while (x.hasNext()) {
//			EObject next = x.next();
//			if (next instanceof Classifier) {
//				Classifier nextClass = (Classifier) next;
//				if (nextClass.getName().equalsIgnoreCase(this.contextClassifier.getName())) {
//					this.modificationContext = nextClass;
//					break;
//				}
//			}
//		}
//
//		if (this.modificationContext == null) {
//			throw new RuntimeException("Cannot insert claim because the requirement modification context is missing.");
//		} else if (this.claimCall == null) {
//			throw new RuntimeException("Cannot insert claim because the requirement claim call is missing.");
//		}
//
//		claim.insert(this.claimDefinition, this.claimCall);
//
//		// Insert
//		insertClaimDef();
//		insertClaimCall();
//
//		this.modificationContext = null;
//	}

}

