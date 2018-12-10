package com.rockwellcollins.atc.darpacase.architecture.handlers;

import java.util.Iterator;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.PlatformUI;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.utils.EditorUtils;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;
import org.osate.aadl2.AadlPackage;
import org.osate.aadl2.AnnexSubclause;
import org.osate.aadl2.Classifier;
import org.osate.aadl2.DefaultAnnexSubclause;
import org.osate.aadl2.PackageSection;
import org.osate.aadl2.PrivatePackageSection;
import org.osate.aadl2.ProcessType;
import org.osate.aadl2.PropertySet;
import org.osate.aadl2.PublicPackageSection;
import org.osate.aadl2.ThreadType;
import org.osate.ui.dialogs.Dialog;

import com.rockwellcollins.atc.darpacase.architecture.CaseClaimsManager;
import com.rockwellcollins.atc.darpacase.architecture.dialogs.SelectImplementationDialog;

public class SelectImplementation extends AadlHandler {

//	private String legacyComponentImplementationType;
	private String legacyComponentImplementationLocation;
	private String legacyComponentImplementationEntryFunction;
//	private String legacyComponentImplementationFunctionAddress;

	static final String RESOLUTE_CLAUSE = "prove (LegacyComponentVerificationCheck(this))";

	@Override
	public void runCommand(URI uri) {

		// Make sure selection is a process or thread
		final EObject eObj = getEObject(uri);
		if (!(eObj instanceof ProcessType) && !(eObj instanceof ThreadType)) {
			Dialog.showError("No software component is selected",
					"A process or thread must be selected to assign an implementation.");
			return;
		}

		// Get location of legacy source or binary
		// Open wizard to input implementation info
		SelectImplementationDialog wizard = new SelectImplementationDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		wizard.create();
		if (wizard.open() == Window.OK) {
//			legacyComponentImplementationType = wizard.getImplementationType();
			legacyComponentImplementationLocation = wizard.getImplementationLocation();
			// AADL doesn't like backslashes
			// We can replace with forward slashes
			legacyComponentImplementationLocation = legacyComponentImplementationLocation.replace("\\", "/");
			legacyComponentImplementationEntryFunction = wizard.getImplementationEntryFunction();
//			legacyComponentImplementationFunctionAddress = wizard.getImplementationFunctionAddress();
		} else {
			return;
		}

		// Add Legacy Component Properties
		addLegacyComponentProperties(uri);

		return;
	}

	/**
	 * Adds the properties to the selected component that specify it
	 * is a legacy implementation.
	 * Also adds a resolute clause for checking that the legacy component
	 * verification was performed on the latest version of the implementation
	 * @param uri - The URI of the selected component
	 */
	private void addLegacyComponentProperties(URI uri) {

		// Get the active xtext editor so we can make modifications
		final XtextEditor xtextEditor = EditorUtils.getActiveXtextEditor();

		xtextEditor.getDocument().modify(new IUnitOfWork.Void<XtextResource>() {

			@Override
			public void process(final XtextResource resource) throws Exception {

				// Retrieve the model object to modify
				final ThreadType selectedComponent = (ThreadType) resource.getEObject(uri.fragment());
				final AadlPackage aadlPkg = (AadlPackage) resource.getContents().get(0);
				PackageSection pkgSection = null;
				// Figure out if the selected component is in the public or private section
				EObject eObj = selectedComponent.eContainer();
				while (eObj != null) {
					if (eObj instanceof PublicPackageSection) {
						pkgSection = aadlPkg.getOwnedPublicSection();
						break;
					} else if (eObj instanceof PrivatePackageSection) {
						pkgSection = aadlPkg.getOwnedPrivateSection();
						break;
					} else {
						eObj = eObj.eContainer();
					}
				}

				if (pkgSection == null) {
					// Something went wrong
					Dialog.showError("No package section found", "No public or private package sections found.");
					return;
				}

				// CASE Property file
				// First check if CASE Property file has already been imported in the model
//				final EList<ModelUnit> importedUnits = pkgSection.getImportedUnits();
//				PropertySetImpl casePropSet = null;
//				for (ModelUnit modelUnit : importedUnits) {
//					if (modelUnit instanceof PropertySetImpl) {
//						if (modelUnit.getName().equals(CASE_PROPSET_NAME)) {
//							casePropSet = (PropertySetImpl) modelUnit;
//							break;
//						}
//					}
//				}
//				if (casePropSet == null) {
//					// Try importing the resource
//					casePropSet = getPropertySet(CASE_PROPSET_NAME, CASE_PROPSET_FILE, resource.getResourceSet());
//					if (casePropSet == null) {
//						return;
//					}
//					// Add as "importedUnit" to package section
//					pkgSection.getImportedUnits().add(casePropSet);
//				}

				// Add legacy component implementation properties
				// CASE::IMPL_TYPE property
//				if (!addPropertyAssociation("IMPL_TYPE", legacyComponentImplementationType, selectedComponent,
//						casePropSet)) {
////					return;
//				}
				// CASE::IMPL_FILE property
//				if (!addPropertyAssociation("IMPL_FILE", legacyComponentImplementationLocation, selectedComponent,
//						casePropSet)) {
////					return;
//				}

				PropertySet propSet = getPropertySet("Programming_Properties", "Programming_Properties.aadl",
						resource.getResourceSet());
				if (propSet == null) {
					return;
				}
				if (!addPropertyAssociation("Source_Text", legacyComponentImplementationLocation, selectedComponent,
						propSet)) {
//					return;
				}
				if (!addPropertyAssociation("Compute_Entrypoint_Source_Text",
						legacyComponentImplementationEntryFunction,
						selectedComponent, propSet)) {
//					return;
				}

				// Add Resolute check clause
//				EList<AnnexSubclause> annexSubclauses = selectedComponent.getOwnedAnnexSubclauses();
				Iterator<AnnexSubclause> subclause = selectedComponent.getOwnedAnnexSubclauses().iterator();
				DefaultAnnexSubclause annexSubclause = null;
				String sourceText = "";
//				for (AnnexSubclause subclause : annexSubclauses) {
				while (subclause.hasNext()) {
					annexSubclause = (DefaultAnnexSubclause) subclause.next();
					if (annexSubclause.getName().equalsIgnoreCase("resolute")) {
//						annexSubclause = (DefaultAnnexSubclause) subclause;
						sourceText = annexSubclause.getSourceText();
						subclause.remove();
//						break;
					}
				}
				// If any Resolute annex clause does not exist for this component, create it
//				if (annexSubclause == null) {
				annexSubclause = selectedComponent.createOwnedAnnexSubclause();
				annexSubclause.setName("resolute");
//					annexSubclause.setSourceText(formatResoluteClause(""));
				annexSubclause.setSourceText(formatResoluteClause(sourceText));
//				} else {
//					// otherwise add the legacy check resolute statement to the existing clause
//					annexSubclause.setSourceText(formatResoluteClause(annexSubclause.getSourceText()));
//				}

				// Add the corresponding resolute claim in the _CASE_Claims file
				CaseClaimsManager.getInstance().addLegacyComponentVerification();

				// Delete and re-insert this component from package section
				// This seems to be the only way to get the formatting (mostly) correct
				int idx = getIndex(selectedComponent.getName(), pkgSection.getOwnedClassifiers());
				Classifier classifier = pkgSection.getOwnedClassifiers().get(idx);
				pkgSection.getOwnedClassifiers().remove(idx);
//				pkgSection.getOwnedClassifiers().add(idx, selectedComponent);
				pkgSection.getOwnedClassifiers().add(idx, classifier);

			}
		});

		return;
	}

	/**
	 * Formats the LegacyComponentVerificationCheck Resolute annex clause correctly.
	 * An existing Resolute clause can be passed in, in which case it will be
	 * combined with the LegacyComponentVerificationCheck clause.
	 * @param clause - A string representing an existing clause to be combined with the
	 * LegacyComponentVerificationCheck clause.
	 */
	private String formatResoluteClause(String clause) {

		String formattedClause = "";

		if (clause.isEmpty()) {
			formattedClause = "{**" + System.lineSeparator() + "\t\t\t" + RESOLUTE_CLAUSE + System.lineSeparator()
					+ "\t\t**}";
		} else if (clause.contains(RESOLUTE_CLAUSE)) {
			// If RESOLUTE_CLAUSE is already present, no need to add it again
			formattedClause = clause;
		} else {
			// Remove '**}' from clause, add the clause, a newline, then add back '**}'
			formattedClause = clause.replace("**}", "") + "\t" + RESOLUTE_CLAUSE + System.lineSeparator() + "\t\t**}";
		}

		return formattedClause;
	}

}
