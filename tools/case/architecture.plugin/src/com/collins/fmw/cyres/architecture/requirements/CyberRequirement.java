package com.collins.fmw.cyres.architecture.requirements;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.XtextEditor;
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
import com.rockwellcollins.atc.agree.agree.AgreeContract;
import com.rockwellcollins.atc.agree.agree.AgreeContractSubclause;
import com.rockwellcollins.atc.agree.agree.AgreeFactory;
import com.rockwellcollins.atc.agree.agree.NamedSpecStatement;
import com.rockwellcollins.atc.agree.parsing.AgreeAnnexParser;
import com.rockwellcollins.atc.resolute.resolute.Expr;
import com.rockwellcollins.atc.resolute.resolute.FunctionDefinition;
import com.rockwellcollins.atc.resolute.resolute.ProveStatement;
import com.rockwellcollins.atc.resolute.resolute.ResoluteFactory;
import com.rockwellcollins.atc.resolute.resolute.ResoluteLibrary;
import com.rockwellcollins.atc.resolute.resolute.ResoluteSubclause;

public class CyberRequirement {

//	private static String AGREE_PROP_CHECKED = "agree_prop_checked()";

//	public final Integer id;
	private String type = ""; // this is the requirement classification type as defined by the TA1 tool
	private String id = "";
	private String text = "";
	private String context = ""; // this is the name of the component
	private Classifier contextClassifier = null;
	private boolean agree = false;
	private String rationale = "";

	private FunctionDefinition claimDefinition = null;
	private ProveStatement claimCall = null;

	private Classifier modificationContext = null;
	private XtextEditor editor = null;

//	public TA1Requirement(Integer id) {
//		this.id = id;
//	}

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

	public Classifier getContextClassifier() {
		return this.contextClassifier;
	}

	public FunctionDefinition getClaimDefinition() {
		return claimDefinition;
	}

	public ProveStatement getClaimCall() {
		return claimCall;
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

	public void buildClaimDefinition() {

		ClaimBuilder builder = new ClaimBuilder(this.id);

		builder.addArg(Create.arg("c", Create.baseType("component")));

		if (this.agree) {
			builder.addArg(Create.arg("property_id", Create.baseType("string")));
		}

		builder.addClaimString("[" + this.type + "] " + this.text);

		if (this.agree) {
			// TODO: figure this out
//			builder.setClaimExpr(
//					Create.fnCall(ClaimsManager.getInstance().getClaim(ClaimsManager.AGREE_PROP_CHECKED)));
			builder.setClaimExpr(Create.FALSE());
		} else {
			builder.setClaimExpr(Create.FALSE());
		}

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
//		if (this.context == null) {
			return;
		}

		AadlPackage pkg = EcoreUtil2.getContainerOfType(this.modificationContext, AadlPackage.class);
//		AadlPackage pkg1 = EcoreUtil2.getContainerOfType(this.context, AadlPackage.class);
		if (pkg == null) {
//			throw new RuntimeException("Could not find containing package for " + this.context);
			throw new RuntimeException("Could not find containing package for " + this.modificationContext);
		}

		PrivatePackageSection priv8 = pkg.getOwnedPrivateSection();
//		PrivatePackageSection priv8 = pkg.getPrivateSection();
		if (priv8 == null) {
			priv8 = pkg.createOwnedPrivateSection();
		}

		DefaultAnnexLibrary defResLib = null;
		ResoluteLibrary resLib = null;
		for(AnnexLibrary library : priv8.getOwnedAnnexLibraries()) {
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

		buildClaimDefinition();
		resLib.getDefinitions().add(this.claimDefinition);
		defResLib.setParsedAnnexLibrary(resLib);

	}

	public void insertClaimCall() {

		if (this.modificationContext == null) {
//		if (this.context == null) {
			return;
		}

		DefaultAnnexSubclause subclause = null;
		ResoluteSubclause resclause = null;
		for (AnnexSubclause sc : modificationContext.getOwnedAnnexSubclauses()) {
//		for (AnnexSubclause sc : this.context.getOwnedAnnexSubclauses()) {
			if (sc instanceof DefaultAnnexSubclause && sc.getName().equalsIgnoreCase("resolute")) {
				subclause = (DefaultAnnexSubclause) sc;
				resclause = EcoreUtil.copy((ResoluteSubclause) subclause.getParsedAnnexSubclause());
				break;
			}
		}

		if (subclause == null) {
			subclause = (DefaultAnnexSubclause) modificationContext
					.createOwnedAnnexSubclause(Aadl2Package.eINSTANCE.getDefaultAnnexSubclause());
//			subclause = (DefaultAnnexSubclause) this.context
//					.createOwnedAnnexSubclause(Aadl2Package.eINSTANCE.getDefaultAnnexSubclause());
			subclause.setName("resolute");
			subclause.setSourceText("{** **}");

			resclause = ResoluteFactory.eINSTANCE.createResoluteSubclause();
		}

		List<Expr> args = new ArrayList<>();
		args.add(Create.THIS());
		if (this.agree) {
			args.add(Create.stringExpr(this.id));
		}
		buildClaimCall(args);
		resclause.getProves().add(this.claimCall);
		subclause.setParsedAnnexSubclause(resclause);
	}

	public void insertAgree() {
		if (this.modificationContext == null) {
//		if (this.context == null) {
			return;
		}

		DefaultAnnexSubclause subclause = null;
		AgreeContractSubclause agreeSubclause = null;
		for (AnnexSubclause sc : modificationContext.getOwnedAnnexSubclauses()) {
//		for (AnnexSubclause sc : this.context.getOwnedAnnexSubclauses()) {
			if (sc instanceof DefaultAnnexSubclause && sc.getName().equalsIgnoreCase("agree")) {
				subclause = (DefaultAnnexSubclause) sc;
				agreeSubclause = EcoreUtil.copy((AgreeContractSubclause) subclause.getParsedAnnexSubclause());
				break;
			}
		}

		if (subclause == null) {
			subclause = (DefaultAnnexSubclause) modificationContext
					.createOwnedAnnexSubclause(Aadl2Package.eINSTANCE.getDefaultAnnexSubclause());
//			subclause = (DefaultAnnexSubclause) this.context
//					.createOwnedAnnexSubclause(Aadl2Package.eINSTANCE.getDefaultAnnexSubclause());
			subclause.setName("agree");
			subclause.setSourceText("{** **}");

			agreeSubclause = AgreeFactory.eINSTANCE.createAgreeContractSubclause();
		}

		String assume = "assume ";
		if (!id.isEmpty()) {
			assume += id + " ";
		}
		assume += "\"" + this.text + "\" : FALSE;";
		AgreeAnnexParser parser = new AgreeAnnexParser();
		NamedSpecStatement spec = parser.parseNamedSpecStatement(assume);

//		agreeSubclause.getProves().add(this.claimCall);
		AgreeContract contract = (AgreeContract) agreeSubclause.getContract();
		contract.getSpecs().add(spec);
		subclause.setParsedAnnexSubclause(agreeSubclause);
	}

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

	public static XtextEditor getEditor(IFile file) {
		IWorkbenchPage page = null;
		IEditorPart part = null;

		if (file.exists()) {
			page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			IEditorDescriptor desc = PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor(file.getName());
			try {
				part = page.openEditor(new FileEditorInput(file), desc.getId());
			} catch (PartInitException e) {
				e.printStackTrace();
			}
		}

		if (part == null) {
			return null;
		}

		XtextEditor xedit = null;
		xedit = (XtextEditor) part;

		return xedit;
	}

	public static void closeEditor(XtextEditor editor, boolean save) {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if (save) {
			page.saveEditor(editor, false);
		}
		page.closeEditor(editor, false);
	}

	public void insert(XtextResource resource) {

		this.modificationContext = null;

		TreeIterator<EObject> x = EcoreUtil.getAllContents(resource, true);
		while (x.hasNext()) {
			EObject next = x.next();
			if (next instanceof Classifier) {
				Classifier nextClass = (Classifier) next;
				if (nextClass.getName().equalsIgnoreCase(this.contextClassifier.getName())) {
					this.modificationContext = nextClass;
					break;
				}
			}
		}

		if (this.type == null || this.type.isEmpty()) {
			throw new RuntimeException("Cannot insert requirement because it does not have a type.");
		} else if (this.id == null || this.id.isEmpty()) {
			throw new RuntimeException("Cannot insert requirement because it does not have an ID.");
//		} else if (this.context == null) {
		} else if (this.modificationContext == null) {
			throw new RuntimeException("Cannot insert requirement because the context is missing.");
		}

		insertClaimDef();
		insertClaimCall();
		if (this.agree) {
			insertAgree();
		}

//		// Add to claims manager
//		ClaimsManager.getInstance().addRequirement(this);
		this.modificationContext = null;
	}

}

