package com.collins.trustedsystems.briefcase.staircase.handlers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.PlatformUI;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.utils.EditorUtils;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;
import org.osate.aadl2.Aadl2Factory;
import org.osate.aadl2.AadlPackage;
import org.osate.aadl2.AnnexSubclause;
import org.osate.aadl2.Classifier;
import org.osate.aadl2.ComponentImplementation;
import org.osate.aadl2.DefaultAnnexSubclause;
import org.osate.aadl2.PackageSection;
import org.osate.aadl2.PrivatePackageSection;
import org.osate.aadl2.Property;
import org.osate.aadl2.PublicPackageSection;
import org.osate.aadl2.StringLiteral;
import org.osate.ui.dialogs.Dialog;
import org.osate.xtext.aadl2.properties.util.GetProperties;
import org.osate.xtext.aadl2.properties.util.ProgrammingProperties;

public class SelectImplementationHandler extends AadlHandler {

	FileDialog dlgImplementationLocation;
//	private String legacyComponentImplementationType;
	private String legacyComponentImplementationLocation;
//	private String legacyComponentImplementationEntryFunction;
//	private String legacyComponentImplementationFunctionAddress;

	static final String RESOLUTE_CLAUSE = "prove (legacy_component_verification(this))";

	@Override
	public void runCommand(URI uri) {

		// Make sure selection is a process or thread
		final EObject eObj = getEObject(uri);
		if (!(eObj instanceof ComponentImplementation)) {
			Dialog.showError("No software component implementation is selected",
					"A component implementation must be selected to assign an implementation source.");
			return;
		}

		// Get location of legacy source or binary
		dlgImplementationLocation = new FileDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		dlgImplementationLocation.setText("Select Implementation");
		dlgImplementationLocation.open();
		legacyComponentImplementationLocation = dlgImplementationLocation.getFilterPath();
		if (!legacyComponentImplementationLocation.isEmpty()) {
			legacyComponentImplementationLocation += "/";
		}
		legacyComponentImplementationLocation += dlgImplementationLocation.getFileName();
		legacyComponentImplementationLocation = legacyComponentImplementationLocation.replace("\\", "/");

//		// Open wizard to input implementation info
//		SelectImplementationDialog wizard = new SelectImplementationDialog(
//				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
//		wizard.create();
//		if (wizard.open() == Window.OK) {
////			legacyComponentImplementationType = wizard.getImplementationType();
//			legacyComponentImplementationLocation = wizard.getImplementationLocation();
//			// AADL doesn't like backslashes
//			// We can replace with forward slashes
//			legacyComponentImplementationLocation = legacyComponentImplementationLocation.replace("\\", "/");
////			legacyComponentImplementationEntryFunction = wizard.getImplementationEntryFunction();
////			legacyComponentImplementationFunctionAddress = wizard.getImplementationFunctionAddress();
//		} else {
//			return;
//		}

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
				final ComponentImplementation selectedComponent = (ComponentImplementation) resource
						.getEObject(uri.fragment());
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

//				// Import CASE_Properties file
//				if (!CaseUtils.addCasePropertyImport(pkgSection)) {
//					return;
//				}
//				// Import CASE_Model_Transformations file
//				if (!CaseUtils.addCaseModelTransformationsImport(pkgSection, true)) {
//					return;
//				}

//				if (!CaseUtils.addCasePropertyAssociation("Source_Text", legacyComponentImplementationLocation,
//						selectedComponent)) {
//					return;
//				}
//				if (!CaseUtils.addCasePropertyAssociation("Compute_Entrypoint_Source_Text",
//						legacyComponentImplementationEntryFunction, selectedComponent)) {
//					return;
//				}

				Property sourceTextProp = GetProperties.lookupPropertyDefinition(selectedComponent,
						ProgrammingProperties._NAME, ProgrammingProperties.SOURCE_TEXT);
				StringLiteral sourceTextLit = Aadl2Factory.eINSTANCE.createStringLiteral();
				sourceTextLit.setValue(legacyComponentImplementationLocation);
				List<StringLiteral> listVal = new ArrayList<>();
				listVal.add(sourceTextLit);
				selectedComponent.setPropertyValue(sourceTextProp, listVal);

				// Add Resolute check clause
				Iterator<AnnexSubclause> subclause = selectedComponent.getOwnedAnnexSubclauses().iterator();
				DefaultAnnexSubclause annexSubclause = null;
				String sourceText = "";
				while (subclause.hasNext()) {
					annexSubclause = (DefaultAnnexSubclause) subclause.next();
					if (annexSubclause.getName().equalsIgnoreCase("resolute")) {
						sourceText = annexSubclause.getSourceText();
						subclause.remove();
					}
				}

				annexSubclause = selectedComponent.createOwnedAnnexSubclause();
				annexSubclause.setName("resolute");
				annexSubclause.setSourceText(formatResoluteClause(sourceText));

				// Delete and re-insert this component from package section
				// This seems to be the only way to get the formatting (mostly) correct
				int idx = getIndex(selectedComponent.getName(), pkgSection.getOwnedClassifiers());
				Classifier classifier = pkgSection.getOwnedClassifiers().get(idx);
				pkgSection.getOwnedClassifiers().remove(idx);
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
			formattedClause = "{**" + System.lineSeparator() + RESOLUTE_CLAUSE + System.lineSeparator() + "**}";
		} else if (clause.contains(RESOLUTE_CLAUSE)) {
			// If RESOLUTE_CLAUSE is already present, no need to add it again
			formattedClause = clause;
		} else {
			// Remove '**}' from clause, add the clause, a newline, then add back '**}'
			formattedClause = clause.replace("**}", "") + RESOLUTE_CLAUSE + System.lineSeparator() + "**}";
		}

		return formattedClause;
	}

}
