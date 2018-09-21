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
		return instance;
	}

	private Resource getResource() {

		if (caseClaimsResource == null) {

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
							+ "\tannex resolute {**" + System.lineSeparator() + System.lineSeparator() + "\t**};"
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

		return caseClaimsResource;

	}


	public void addFunctionDefinition(String reqName, String reqText) {

		// Add agree_prop_checked
		String clause = "\t-- This connects to evidence that AGREE was previously run on the current version of the design."
				+ System.lineSeparator() + "\t\tagree_prop_checked() <=" + System.lineSeparator()
				+ "\t\t\t** \"AGREE properties passed\" **" + System.lineSeparator() + "\t\t\tanalysis(\"AgreeCheck\")"
				+ System.lineSeparator() + System.lineSeparator();
//		addToResoluteAnnex(clause);

		// Add basic function body to resolute annex
		clause = clause + "\t\t" + reqName + "() <=" + System.lineSeparator() + "\t\t\t** \"" + reqText + "\" **"
				+ System.lineSeparator() + "\t\t\tagree_prop_checked()" + System.lineSeparator();

		addToResoluteAnnex(clause);

	}

	public void addFilter(String reqName) {

		Resource claimResource = getResource();

		IFile file = Filesystem.getFile(claimResource.getURI());

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

		String funSig = reqName + "(c : component, msg_type : data) <=";
		String funDef = "agree_prop_checked() and add_filter(c, msg_type)" + System.lineSeparator();
		annex = annex.replace(reqName + "() <=", funSig);
		annex = annex.replace("agree_prop_checked()" + System.lineSeparator(), funDef);

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

		Resource claimResource = getResource();

		IFile file = Filesystem.getFile(claimResource.getURI());

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
		annex = annex.replace("**}", clause + System.lineSeparator() + "\t**}");

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
