package com.collins.fmw.cyres.architecture.requirements;

import java.util.HashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.xtext.EcoreUtil2;
import org.osate.aadl2.AadlPackage;
import org.osate.aadl2.ModelUnit;
import org.osate.aadl2.modelsupport.util.AadlUtil;
import org.osgi.framework.Bundle;

import com.rockwellcollins.atc.resolute.resolute.FunctionDefinition;

public class ClaimsManager {

	// Package containing built-in claims
	private final static String CASE_CLAIMS_PKG = "plugin:/resources/Case_Model_Transformations.aadl";

	// Built-in CASE claims
	public static String AGREE_PROP_CHECKED = "Agree_Prop_Checked";
	public static String ADD_FILTER = "Add_Filter";
	public static String ADD_ATTESTATION_MANAGER = "Add_Attestation_Manager";
	public static String ADD_ISOLATOR = "Add_Isolator";
	private static HashMap<String, FunctionDefinition> caseClaims = null;

	// Set of imported requirements claims
	private static HashMap<String, CyberRequirement> requirements = null;

	// Singleton instance
	private static ClaimsManager instance = null;

//	private static Resource caseClaimsResource = null;
//	private static PrivatePackageSection priv = null;
//	private static DefaultAnnexLibrary resoluteLib = null;

	private ClaimsManager() {

	}

	public static ClaimsManager getInstance() {
		if (instance == null) {
			requirements = new HashMap<>();
			initCaseClaims();
			instance = new ClaimsManager();
		}
//		// Make sure the resource is current
//		updateResource();
		return instance;
	}

	public FunctionDefinition getClaim(String claim) {
		return caseClaims.getOrDefault(claim, null);
	}

	public void addRequirement(CyberRequirement req) {
		requirements.put(req.getType(), req);
	}

	public CyberRequirement getRequirement(String req) {
		return requirements.getOrDefault(req, null);
	}

	/**
	 * This function sets the FunctionDefinition of built-in CASE claims
	 */
	private static void initCaseClaims() {
		String path = "";
		try {
			Bundle bundle = Platform.getBundle("com.collins.fmw.cyres.architecture.plugin");
			path = (FileLocator
					.toFileURL(FileLocator.find(bundle, new Path("resources/CASE_Model_Transformations.aadl"), null)))
							.getFile();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Path p = new Path(path);
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(p);
		ModelUnit target = (ModelUnit) AadlUtil.getElement(file);
		AadlPackage aadlPkg = null;
		if (target instanceof AadlPackage) {
			aadlPkg = (AadlPackage) target;
		}
//		for (AadlPackage aadlPkg : TraverseProject.getPackagesInProject(TraverseProject.getCurrentProject())) {
//			if (aadlPkg.getName().equalsIgnoreCase(CASE_CLAIMS_PKG)) {
				for (FunctionDefinition fd : EcoreUtil2.getAllContentsOfType(aadlPkg, FunctionDefinition.class)) {
					if (fd.getName().equalsIgnoreCase(AGREE_PROP_CHECKED)) {
						caseClaims.put(AGREE_PROP_CHECKED, fd);
					} else if (fd.getName().equalsIgnoreCase(ADD_FILTER)) {
						caseClaims.put(ADD_FILTER, fd);
					} else if (fd.getName().equalsIgnoreCase(ADD_ATTESTATION_MANAGER)) {
						caseClaims.put(ADD_ATTESTATION_MANAGER, fd);
					} else if (fd.getName().equalsIgnoreCase(ADD_ISOLATOR)) {
						caseClaims.put(ADD_ISOLATOR, fd);
					}
				}
//				break;
//			}
//		}
	}

//	public static CaseClaimsManager getInstance(AadlPackage pkg) {
//		if (instance == null) {
//			instance = new CaseClaimsManager();
//		}
//		// Make sure the private section is initialized
//		initPrivateSection(pkg);
//		return instance;
//	}

//	private static void initPrivateSection(AadlPackage pkg) {
//		PrivatePackageSection priv = pkg.getOwnedPrivateSection();
//		if (priv == null) {
//			priv = pkg.createOwnedPrivateSection();
//		}
//		boolean resoluteFound = false;
//		for (AnnexLibrary annexLib : priv.getOwnedAnnexLibraries()) {
//			DefaultAnnexLibrary annex = (DefaultAnnexLibrary) annexLib;
//			if (annex.getName().equalsIgnoreCase("resolute")) {
//				resoluteLib = annex;
//				resoluteFound = true;
//				break;
//			}
//		}
//		if (!resoluteFound) {
//			resoluteLib = (DefaultAnnexLibrary) priv.createOwnedAnnexLibrary();
////			DefaultAnnexLibrary annex = (DefaultAnnexLibrary) annexLib;
//			resoluteLib.setName("resolute");
//			resoluteLib.setSourceText("{** **}");
//		}
//	}

//	private static void updateResource() {
//
//		if (caseClaimsResource == null || !caseClaimsResource.isLoaded()) {
//
//			caseClaimsResource = null;
//
//			XtextEditor xtextEditor = EditorUtils.getActiveXtextEditor();
//			XtextResource modelResource = xtextEditor.getDocument().readOnly(r -> r);
//			String modelFileName = modelResource.getURI().trimFileExtension().lastSegment();
//			String claimFileName = modelFileName + "_CASE_Claims";
//			// Check to see if the CASE_Claims resource has already been loaded
//			for (Resource r : modelResource.getResourceSet().getResources()) {
//				if (r.getURI().trimFileExtension().lastSegment().equals(claimFileName)) {
//					caseClaimsResource = r;
//					break;
//				}
//			}
//
//			if (caseClaimsResource == null) {
//				// Check to see if file exists in working directory
//				// If not, create it and get the resource
//				URI uri = modelResource.getURI().trimSegments(1).appendSegment(claimFileName)
//						.appendFileExtension("aadl");
//				IFile file = Filesystem.getFile(uri);
//				if (!file.exists()) {
//					// Create it
//					String pkg = "package " + claimFileName + System.lineSeparator() + "public" + System.lineSeparator()
//							+ System.lineSeparator() + "\twith CASE_Model_Transformations;" + System.lineSeparator()
//							+ System.lineSeparator() + "\tannex resolute {**" + System.lineSeparator()
//							+ System.lineSeparator() + "\t**};"
//							+ System.lineSeparator() + "end " + claimFileName + ";";
//					try {
//						file.create(new ByteArrayInputStream(pkg.getBytes()), true, new NullProgressMonitor());
//					} catch (CoreException e) {
//						Dialog.showError("CASE Claims file", "An error occured when creating the CASE Claims file.");
//					}
//				}
//				// Load resource
//				caseClaimsResource = modelResource.getResourceSet().createResource(uri);
//				try {
//					caseClaimsResource.load(null);
//				} catch (IOException e) {
//					Dialog.showError("CASE Claims file", "Could not load the CASE Claims file resource.");
//				}
//			}
//		}
//
//	}


//	public void addFunctionDefinition(String reqName, String reqID, String reqText, boolean agreeProp) {
//
//		// Add basic function body to resolute annex
//		String clause = "";
//		if (agreeProp) {
//			clause = "\t\t" + reqName + "(c : component, property_id : string) <=" + System.lineSeparator()
//				+ "\t\t\t** \"" + reqID + ": " + reqText + "\" **" + System.lineSeparator()
//				+ "\t\t\tagree_prop_checked(c, property_id)" + System.lineSeparator();
//		} else {
//			clause = "\t\t" + reqName + "(c : component) <=" + System.lineSeparator() + "\t\t\t** \"" + reqID + ": "
//					+ reqText + "\" **" + System.lineSeparator() + "\t\t\tfalse" + System.lineSeparator();
//		}
//		addToResoluteAnnex(clause);
//
//	}

//	public void addFilter(String reqName) {
//
//		// TODO: make sure to check if agree property exists
//
//		String annex = readClaimsFile();
////		String annex = readPrivateSectionClaims();
//
//		int startIdx = annex.indexOf("**", annex.indexOf(reqName + "("));
//		String descriptor = "\t\t\t" + annex.substring(startIdx, annex.indexOf("**", startIdx + 2) + 2)
//				+ System.lineSeparator();
//		String basic = reqName + "(c : component, property_id : string) <=" + System.lineSeparator()
//				+ descriptor + "\t\t\tagree_prop_checked(c, property_id)";
//		String funSig = reqName + "(c : component, property_id : string, msg_type : data) <=" + System.lineSeparator();
//		String funDef = "\t\t\tagree_prop_checked(c, property_id) and add_filter(c, msg_type)";
//		if (annex.contains(basic)) {
//			annex = annex.replace(basic, funSig + descriptor + funDef);
//		} else {
//			annex = annex.replace("\t**};", "\t\t" + funSig + funDef + System.lineSeparator() + "\t**};");
////			annex = annex.replace("**};", "\t\t" + funSig + funDef + System.lineSeparator() + "**};");
//		}
//
//		// Write back to file
//		// The contents of the file will be overwritten
//		writeClaimsFile(annex);
////		writePrivateSectionClaims(annex);
//
//	}

//	public void addAttestationManager(String reqName) {
//
//		// TODO: make sure to check if agree property exists
//
//		String annex = readClaimsFile();
////		String annex = readPrivateSectionClaims();
//
//		int startIdx = annex.indexOf("**", annex.indexOf(reqName + "("));
//		String descriptor = "\t\t\t" + annex.substring(startIdx, annex.indexOf("**", startIdx + 2) + 2)
//				+ System.lineSeparator();
//		String basic = reqName + "(c : component, property_id : string) <=" + System.lineSeparator() + descriptor
//				+ "\t\t\tagree_prop_checked(c, property_id)";
//		String funSig = reqName
//				+ "(c : component, property_id : string, comm_driver : component, attestation_manager : component) <="
//				+ System.lineSeparator();
//		String funDef = "\t\t\tagree_prop_checked(c, property_id) and add_attestation_manager(comm_driver, attestation_manager)";
//		if (annex.contains(basic)) {
//			annex = annex.replace(basic, funSig + descriptor + funDef);
//		} else {
//			annex = annex.replace("\t**};", "\t\t" + funSig + funDef + System.lineSeparator() + "\t**};");
////			annex = annex.replace("**};", "\t\t" + funSig + funDef + System.lineSeparator() + "**};");
//		}
//
//		writeClaimsFile(annex);
////		writePrivateSectionClaims(annex);
//	}

//	public void addLegacyComponentVerification() {
//
//		String annex = readClaimsFile();
//
//		String funDef = "\t\tLegacyComponentVerificationCheck(c : component) <=" + System.lineSeparator()
//				+ "\t\t\t** c \" legacy component has been verified\" **" + System.lineSeparator()
//				+ "\t\t\tanalysis(\"ToolCheck\", \"IVALDI\")" + System.lineSeparator();
//		annex = annex.replace("\t**};", funDef + System.lineSeparator() + "\t**};");
//
//		// Write back to file
//		// The contents of the file will be overwritten
//		writeClaimsFile(annex);
//
//	}

//	private void addToResoluteAnnex(String clause) {
//
//		String annex = readClaimsFile();
////		String annex = readPrivateSectionClaims();
//
//		// Add clause to end of annex
//		annex = annex.replace("\t**};", clause + System.lineSeparator() + "\t**};");
////		annex = annex.replace("**};", clause + System.lineSeparator() + "**};");
//
//		// Write back to file
//		// The contents of the file will be overwritten
//		writeClaimsFile(annex);
////		writePrivateSectionClaims(annex);
//
//	}

//	private String readClaimsFile() {
//		IFile file = Filesystem.getFile(caseClaimsResource.getURI());
//
//		// Read in the claims file
//		String annex = "";
//		try {
//			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(file.getContents()));
//			String line = null;
//
//			while ((line = bufferedReader.readLine()) != null) {
//				annex = annex + line + System.lineSeparator();
//			}
//			bufferedReader.close();
//		} catch (IOException | CoreException e) {
//			Dialog.showError("CASE Claims file", "Error writing the CASE Claims file.");
//		}
//		return annex;
//	}
//
//	private void writeClaimsFile(String annex) {
//		IFile file = Filesystem.getFile(caseClaimsResource.getURI());
//		try {
//			final ByteArrayInputStream source = new ByteArrayInputStream(annex.getBytes());
//			file.setContents(source, IResource.FORCE, new NullProgressMonitor());
//		} catch (CoreException e) {
//			Dialog.showError("CASE Claims file", "Error writing the CASE Claims file.");
//		}
//	}
//
//	private String readPrivateSectionClaims() {
//		String annex = resoluteLib.getSourceText();
////		for (AnnexLibrary annexLib : priv.getOwnedAnnexLibraries()) {
////			DefaultAnnexLibrary defAnnexLib = (DefaultAnnexLibrary) annexLib;
////			if (defAnnexLib.getName().equalsIgnoreCase("resolute")) {
////				annex = defAnnexLib.getSourceText();
////				break;
////			}
////		}
//		if (annex.isEmpty()) {
//			annex = "{** **}";
//		}
//		return annex;
//	}
//
//	private void writePrivateSectionClaims(String annex) {
////		for (AnnexLibrary annexLib : priv.getOwnedAnnexLibraries()) {
////			DefaultAnnexLibrary defAnnexLib = (DefaultAnnexLibrary) annexLib;
////			if (defAnnexLib.getName().equalsIgnoreCase("resolute")) {
////				defAnnexLib.setSourceText(annex);
////				break;
////			}
////		}
//		resoluteLib.setSourceText(annex);
//	}

}
