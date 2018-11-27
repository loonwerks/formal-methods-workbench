package com.rockwellcollins.atc.darpacase.architecture;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.utils.EditorUtils;
import org.osate.ui.dialogs.Dialog;

import com.rockwellcollins.atc.darpacase.eclipse.util.Filesystem;

public class CaseClaimsManager {

	private static CaseClaimsManager instance = null;
	private static Resource caseClaimsResource = null;

	private CaseClaimsManager() {

	}

	public static CaseClaimsManager getInstance() {
		if (instance == null) {
			instance = new CaseClaimsManager();
		}
		// Make sure the resource is current
		updateResource();
		return instance;
	}

	private static void updateResource() {

		if (caseClaimsResource == null || !caseClaimsResource.isLoaded()) {

			caseClaimsResource = null;

			XtextEditor xtextEditor = EditorUtils.getActiveXtextEditor();
			XtextResource modelResource = xtextEditor.getDocument().readOnly(r -> r);
			String modelFileName = modelResource.getURI().trimFileExtension().lastSegment();
			String claimFileName = modelFileName + "_CASE_Claims";
			// Check to see if the CASE_Claims resource has already been loaded
			for (Resource r : modelResource.getResourceSet().getResources()) {
				if (r.getURI().trimFileExtension().lastSegment().equals(claimFileName)) {
					caseClaimsResource = r;
					break;
				}
			}

			if (caseClaimsResource == null) {
				// Check to see if file exists in working directory
				// If not, create it and get the resource
				URI uri = modelResource.getURI().trimSegments(1).appendSegment(claimFileName)
						.appendFileExtension("aadl");
				IFile file = Filesystem.getFile(uri);
				if (!file.exists()) {
					// Create it
					String pkg = "package " + claimFileName + System.lineSeparator() + "public" + System.lineSeparator()
							+ System.lineSeparator() + "\twith Model_Transformations;" + System.lineSeparator()
							+ System.lineSeparator() + "\tannex resolute {**" + System.lineSeparator()
							+ System.lineSeparator() + "\t**};"
							+ System.lineSeparator() + "end " + claimFileName + ";";
					try {
						file.create(new ByteArrayInputStream(pkg.getBytes()), true, new NullProgressMonitor());
					} catch (CoreException e) {
						Dialog.showError("CASE Claims file", "An error occured when creating the CASE Claims file.");
					}
				}
				// Load resource
				caseClaimsResource = modelResource.getResourceSet().createResource(uri);
				try {
					caseClaimsResource.load(null);
				} catch (IOException e) {
					Dialog.showError("CASE Claims file", "Could not load the CASE Claims file resource.");
				}
			}
		}

	}


	public void addFunctionDefinition(String reqName, String reqID, String reqText) {

		// Add basic function body to resolute annex
		String clause = "\t\t" + reqName + "(c : component, property_id : string) <=" + System.lineSeparator()
				+ "\t\t\t** \"" + reqID + ": " + reqText + "\" **" + System.lineSeparator()
				+ "\t\t\tagree_prop_checked(c, property_id)" + System.lineSeparator();

		addToResoluteAnnex(clause);

	}

	public void addFilter(String reqName) {

		IFile file = Filesystem.getFile(caseClaimsResource.getURI());

		// Read in the claims file
		String annex = "";
		try {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(file.getContents()));
			String line = null;

			while ((line = bufferedReader.readLine()) != null) {
				annex = annex + line + System.lineSeparator();
			}
			bufferedReader.close();
		} catch (IOException | CoreException e) {
			Dialog.showError("CASE Claims file", "Error writing the CASE Claims file.");
		}

		int startIdx = annex.indexOf("**", annex.indexOf(reqName + "("));
		String descriptor = "\t\t\t" + annex.substring(startIdx, annex.indexOf("**", startIdx + 2) + 2)
				+ System.lineSeparator();
		String basic = reqName + "(c : component, property_id : string) <=" + System.lineSeparator()
				+ descriptor + "\t\t\tagree_prop_checked(c, property_id)";
		String funSig = reqName + "(c : component, property_id : string, msg_type : data) <=" + System.lineSeparator();
		String funDef = "\t\t\tagree_prop_checked(c, property_id) and add_filter(c, msg_type)";
		if (annex.contains(basic)) {
			annex = annex.replace(basic, funSig + descriptor + funDef);
		} else {
			annex = annex.replace("\t**};",
					"\t\t" + funSig + funDef + System.lineSeparator() + "\t**};");
		}

		// Write back to file
		// The contents of the file will be overwritten
		try {
			final ByteArrayInputStream source = new ByteArrayInputStream(annex.getBytes());
			file.setContents(source, IResource.FORCE, new NullProgressMonitor());
		} catch (CoreException e) {
			Dialog.showError("CASE Claims file", "Error writing the CASE Claims file.");
		}

	}

	public void addLegacyComponentVerification() {

		IFile file = Filesystem.getFile(caseClaimsResource.getURI());

		// Read in the claims file
		String annex = "";
		try {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(file.getContents()));
			String line = null;

			while ((line = bufferedReader.readLine()) != null) {
				annex = annex + line + System.lineSeparator();
			}
			bufferedReader.close();
		} catch (IOException | CoreException e) {
			Dialog.showError("CASE Claims file", "Error writing the CASE Claims file.");
		}

		String funDef = "\t\tLegacyComponentVerificationCheck(c : component) <=" + System.lineSeparator()
				+ "\t\t\t** c \" legacy component has been verified\" **" + System.lineSeparator()
				+ "\t\t\tanalysis(\"ToolCheck\", \"SuitCASE\")" + System.lineSeparator();
		annex = annex.replace("\t**};", funDef + System.lineSeparator() + "\t**};");

		// Write back to file
		// The contents of the file will be overwritten
		try {
			final ByteArrayInputStream source = new ByteArrayInputStream(annex.getBytes());
			file.setContents(source, IResource.FORCE, new NullProgressMonitor());
		} catch (CoreException e) {
			Dialog.showError("CASE Claims file", "Error writing the CASE Claims file.");
		}

	}

	private void addToResoluteAnnex(String clause) {

		IFile file = Filesystem.getFile(caseClaimsResource.getURI());

		// Read in the claims file
		String annex = "";
		try {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(file.getContents()));
			String line = null;

			while ((line = bufferedReader.readLine()) != null) {
				annex = annex + line + System.lineSeparator();
			}
			bufferedReader.close();
		} catch (IOException | CoreException e) {
			Dialog.showError("CASE Claims file", "Error writing the CASE Claims file.");
		}

		// Add clause to end of annex
		annex = annex.replace("\t**};", clause + System.lineSeparator() + "\t**};");

		// Write back to file
		// The contents of the file will be overwritten
		try {
			final ByteArrayInputStream source = new ByteArrayInputStream(annex.getBytes());
			file.setContents(source, IResource.FORCE, new NullProgressMonitor());
		} catch (CoreException e) {
			Dialog.showError("CASE Claims file", "Error writing the CASE Claims file.");
		}

	}

}
