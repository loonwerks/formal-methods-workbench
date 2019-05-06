package com.collins.fmw.cyres.architecture.handlers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
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
import org.osate.aadl2.ComponentCategory;
import org.osate.aadl2.ComponentImplementation;
import org.osate.aadl2.ComponentType;
import org.osate.aadl2.ConnectedElement;
import org.osate.aadl2.DataPort;
import org.osate.aadl2.DataSubcomponentType;
import org.osate.aadl2.DefaultAnnexSubclause;
import org.osate.aadl2.EventDataPort;
import org.osate.aadl2.EventPort;
import org.osate.aadl2.PackageSection;
import org.osate.aadl2.Port;
import org.osate.aadl2.PortConnection;
import org.osate.aadl2.PrivatePackageSection;
import org.osate.aadl2.PropertySet;
import org.osate.aadl2.PublicPackageSection;
import org.osate.aadl2.Realization;
import org.osate.aadl2.Subcomponent;
import org.osate.ui.dialogs.Dialog;

import com.collins.fmw.cyres.architecture.dialogs.AddFilterDialog;
import com.collins.fmw.cyres.architecture.requirements.AddFilterClaim;
import com.collins.fmw.cyres.architecture.requirements.RequirementsManager;
import com.collins.fmw.cyres.architecture.utils.ComponentCreateHelper;
import com.rockwellcollins.atc.agree.agree.AgreeContract;
import com.rockwellcollins.atc.agree.agree.AgreeContractSubclause;
import com.rockwellcollins.atc.agree.agree.GuaranteeStatement;
import com.rockwellcollins.atc.agree.agree.SpecStatement;
import com.rockwellcollins.atc.agree.unparsing.AgreeAnnexUnparser;

public class AddFilterHandler extends AadlHandler {

	static final String FILTER_COMP_TYPE_NAME = "CASE_Filter";
	static final String FILTER_PORT_IN_NAME = "filter_in";
	static final String FILTER_PORT_OUT_NAME = "filter_out";
	static final String FILTER_IMPL_NAME = "FLT";
	static final String CONNECTION_IMPL_NAME = "c";

	private ComponentCategory filterComponentType;
	private String filterImplementationName;
	private String filterImplementationLanguage;
	private String filterResoluteClause;
	private String filterAgreeProperty;
	private List<String> propagatedGuarantees;

	@Override
	protected void runCommand(URI uri) {

		// Check if it is a connection
		// TODO: if user selects system implementation, allow the user to specify connection in the wizard
		final EObject eObj = getEObject(uri);
		if (!(eObj instanceof PortConnection)) {
			Dialog.showError("Add Filter",
					"A connection between two components must be selected to add a filter.");
			return;
		}

		// Open wizard to enter filter info
		final AddFilterDialog wizard = new AddFilterDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		wizard.setGuaranteeList(getSourceName(uri), getSourceGuarantees(uri));
		List<String> resoluteClauses = new ArrayList<>();
		RequirementsManager.getInstance().getImportedRequirements().forEach(r -> resoluteClauses.add(r.getId()));
		wizard.setResoluteClauses(resoluteClauses);
		wizard.setAadlComponentTypes(getParentComponentCategory(eObj), getFilterComponentCategory(eObj));
		wizard.create();
		if (wizard.open() == Window.OK) {
			filterComponentType = wizard.getAadlComponentType();
			filterImplementationLanguage = wizard.getFilterImplementationLanguage();
			filterImplementationName = wizard.getFilterImplementationName();
			if (filterImplementationName == "") {
				filterImplementationName = FILTER_IMPL_NAME;
			}
			filterResoluteClause = wizard.getResoluteClause();
			filterAgreeProperty = wizard.getAgreeProperty();
			propagatedGuarantees = wizard.getGuaranteeList();
		} else {
			return;
		}

		// Insert the filter component
		insertFilterComponent(uri);

		return;

	}

	/**
	 * Inserts a filter component into the model, including filter type definition
	 * and implementation (including correct wiring).  The filter is inserted at
	 * the location of the selected connection
	 * @param uri - The URI of the selected connection
	 */
	public void insertFilterComponent(URI uri) {

		// Get the active xtext editor so we can make modifications
		final XtextEditor xtextEditor = EditorUtils.getActiveXtextEditor();

		xtextEditor.getDocument().modify(new IUnitOfWork.Void<XtextResource>() {

			@Override
			public void process(final XtextResource resource) throws Exception {

				// Retrieve the model object to modify
				PortConnection selectedConnection = (PortConnection) resource.getEObject(uri.fragment());
				final AadlPackage aadlPkg = (AadlPackage) resource.getContents().get(0);
				PackageSection pkgSection = null;
				// Figure out if the selected connection is in the public or private section
				EObject eObj = selectedConnection.eContainer();
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

				// Import CASE_Properties file
				if (!addCasePropertyImport(pkgSection)) {
					return;
				}
				// Import CASE_Model_Transformations file
				if (!addCaseModelTransformationsImport(pkgSection, true)) {
					return;
				}

				// Figure out component type by looking at the component type of the destination component
//				ComponentCategory compCategory = ((Subcomponent) selectedConnection.getDestination().getContext())
//						.getCategory();

				// If the component type is a process, we will need to put a single thread inside.
				// Per convention, we will attach all properties and contracts to the thread.
				// For this model transformation, we will create the thread first, then wrap it in a process
				// component, using the same mechanism we use for the seL4 transformation
				boolean isProcess = (filterComponentType == ComponentCategory.PROCESS);
				if (isProcess) {
					filterComponentType = ComponentCategory.THREAD;
				}

				final ComponentType filterType = (ComponentType) pkgSection
						.createOwnedClassifier(ComponentCreateHelper.getTypeClass(filterComponentType));

				// Give it a unique name
				filterType.setName(getUniqueName(FILTER_COMP_TYPE_NAME, true, pkgSection.getOwnedClassifiers()));

				// Create filter ports
				final Port port = (Port) selectedConnection.getDestination().getConnectionEnd();
				Port portIn = null;
				Port portOut = null;
				DataSubcomponentType dataFeatureClassifier = null;
				if (port instanceof EventDataPort) {
					portIn = ComponentCreateHelper.createOwnedEventDataPort(filterType);
					dataFeatureClassifier = ((EventDataPort) port).getDataFeatureClassifier();
					((EventDataPort) portIn).setDataFeatureClassifier(dataFeatureClassifier);
					portOut = ComponentCreateHelper.createOwnedEventDataPort(filterType);
					((EventDataPort) portOut).setDataFeatureClassifier(dataFeatureClassifier);
				} else if (port instanceof DataPort) {
					portIn = ComponentCreateHelper.createOwnedDataPort(filterType);
					dataFeatureClassifier = ((DataPort) port).getDataFeatureClassifier();
					((DataPort) portIn).setDataFeatureClassifier(dataFeatureClassifier);
					portOut = ComponentCreateHelper.createOwnedDataPort(filterType);
					((DataPort) portOut).setDataFeatureClassifier(dataFeatureClassifier);
				} else if (port instanceof EventPort) {
					Dialog.showError("Incompatible port type", "Cannot connect a filter to a non-data port.");
					return;
				} else {
					Dialog.showError("Undetermined port type",
							"Could not determine the port type of the destination component.");
					return;
				}

				portIn.setIn(true);
				portIn.setName(FILTER_PORT_IN_NAME);

				portOut.setOut(true);
				portOut.setName(FILTER_PORT_OUT_NAME);

				// Add filter properties
				PropertySet casePropSet = getPropertySet(CASE_PROPSET_NAME, CASE_PROPSET_FILE,
						resource.getResourceSet());
				// CASE::COMP_TYPE Property
				if (!addPropertyAssociation("COMP_TYPE", "FILTER", filterType, casePropSet)) {
//					return;
				}
				// CASE::COMP_IMPL property
				if (!addPropertyAssociation("COMP_IMPL", filterImplementationLanguage, filterType, casePropSet)) {
//					return;
				}
				// CASE::COMP_SPEC property
				// Parse the ID from the Filter AGREE property
				String filterPropId = "";
				try {
					filterPropId = filterAgreeProperty
							.substring(filterAgreeProperty.toLowerCase().indexOf("guarantee ") + "guarantee ".length(),
									filterAgreeProperty.indexOf("\""))
							.trim();

				} catch (IndexOutOfBoundsException e) {
					// agree property is malformed, so leave blank
				}
				if (!addPropertyAssociation("COMP_SPEC", filterPropId, filterType, casePropSet)) {
//					return;
				}

				// Move filter to proper location
				// (just before component it connects to on communication pathway)
				final Subcomponent subcomponent = (Subcomponent) selectedConnection.getDestination().getContext();
				String destName = "";
				if (subcomponent.getSubcomponentType() instanceof ComponentImplementation) {
					// Get the component type name
					destName = subcomponent.getComponentImplementation().getType().getName();
				} else {
					destName = subcomponent.getName();
				}

				pkgSection.getOwnedClassifiers().move(getIndex(destName, pkgSection.getOwnedClassifiers()),
						pkgSection.getOwnedClassifiers().size() - 1);

				// Create Filter implementation
				final ComponentImplementation filterImpl = (ComponentImplementation) pkgSection
						.createOwnedClassifier(ComponentCreateHelper.getImplClass(filterComponentType));
				filterImpl.setName(filterType.getName() + ".Impl");
				final Realization r = filterImpl.createOwnedRealization();
				r.setImplemented(filterType);

				// Add it to proper place
				pkgSection.getOwnedClassifiers().move(getIndex(destName, pkgSection.getOwnedClassifiers()),
						pkgSection.getOwnedClassifiers().size() - 1);

				final ComponentImplementation containingImpl = selectedConnection
						.getContainingComponentImpl();

				// Insert filter feature in process component implementation
				final Subcomponent filterSubcomp = ComponentCreateHelper.createOwnedSubcomponent(containingImpl,
						filterComponentType);

				// Give it a unique name
				filterSubcomp
						.setName(getUniqueName(filterImplementationName, true, containingImpl.getOwnedSubcomponents()));

				ComponentCreateHelper.setSubcomponentType(filterSubcomp, filterImpl);

				// Create connection from filter to connection destination
				final PortConnection portConnOut = containingImpl.createOwnedPortConnection();
				// Give it a unique name
				portConnOut
						.setName(getUniqueName(CONNECTION_IMPL_NAME, false, containingImpl.getOwnedPortConnections()));
				portConnOut.setBidirectional(false);
				final ConnectedElement filterOutSrc = portConnOut.createSource();
				filterOutSrc.setContext(filterSubcomp);
				filterOutSrc.setConnectionEnd(portOut);
				final ConnectedElement filterOutDst = portConnOut.createDestination();
				filterOutDst.setContext(selectedConnection.getDestination().getContext());
				filterOutDst.setConnectionEnd(selectedConnection.getDestination().getConnectionEnd());

				// Put portConnOut in right place (after portConnIn)
				destName = selectedConnection.getName();
				containingImpl.getOwnedPortConnections().move(
						getIndex(destName, containingImpl.getOwnedPortConnections()) + 1,
						containingImpl.getOwnedPortConnections().size() - 1);

				// Add add_filter claims to resolute prove statement, if applicable
				if (!filterResoluteClause.isEmpty()) {

					RequirementsManager.getInstance().modifyRequirement(filterResoluteClause, resource,
							new AddFilterClaim(dataFeatureClassifier));

				}

				// Rewire selected connection so the filter is the destination
				selectedConnection.getDestination().setContext(filterSubcomp);
				selectedConnection.getDestination().setConnectionEnd(portIn);

				// Propagate Agree Guarantees from source component, if there are any
				if (filterAgreeProperty.length() > 0 || propagatedGuarantees.size() > 0) {
					String agreeClauses = "{**" + System.lineSeparator();

					for (String guarantee : propagatedGuarantees) {
						agreeClauses = agreeClauses + "\t\t\t" + guarantee + System.lineSeparator();
					}

					// replace source out port name with filter out port name
					agreeClauses = agreeClauses.replace(selectedConnection.getSource().getConnectionEnd().getName(),
							FILTER_PORT_OUT_NAME);

					if (!filterAgreeProperty.isEmpty()) {
						agreeClauses = agreeClauses + "\t\t\t" + filterAgreeProperty + System.lineSeparator();
					}

					agreeClauses = agreeClauses + "\t\t**}";

					final DefaultAnnexSubclause annexSubclauseImpl = ComponentCreateHelper
							.createOwnedAnnexSubclause(filterType);
					annexSubclauseImpl.setName("agree");
					annexSubclauseImpl.setSourceText(agreeClauses);
				}

				if (isProcess) {

					// TODO: Wrap thread component in a process

					// TODO: Bind process to processor
				}

			}
		});

	}

	private String getSourceName(URI uri) {
		XtextEditor xtextEditor = EditorUtils.getActiveXtextEditor();

		return xtextEditor.getDocument().readOnly(resource -> {
			final PortConnection selectedConnection = (PortConnection) resource.getEObject(uri.fragment());
			return selectedConnection.getSource().getConnectionEnd().getContainingClassifier().getName();
		});
	}

	private List<String> getSourceGuarantees(URI uri) {
		XtextEditor xtextEditor = EditorUtils.getActiveXtextEditor();

		return xtextEditor.getDocument().readOnly(resource -> {
			List<String> guarantees = new ArrayList<>();
			final PortConnection selectedConnection = (PortConnection) resource.getEObject(uri.fragment());
			final EList<AnnexSubclause> annexSubclauses = selectedConnection.getSource().getConnectionEnd()
					.getContainingClassifier().getOwnedAnnexSubclauses();
			for (AnnexSubclause annexSubclause : annexSubclauses) {
				// See if there's an agree annex
				DefaultAnnexSubclause defaultSubclause = (DefaultAnnexSubclause) annexSubclause;
				if (defaultSubclause.getParsedAnnexSubclause() instanceof AgreeContractSubclause) {
					// See if the agree annex contains guarantee statements
					AgreeContractSubclause agreeSubclause = (AgreeContractSubclause) defaultSubclause
							.getParsedAnnexSubclause();
					AgreeAnnexUnparser unparser = new AgreeAnnexUnparser();
					AgreeContract agreeContract = (AgreeContract) agreeSubclause.getContract();
					for (SpecStatement ss : agreeContract.getSpecs()) {
						if (ss instanceof GuaranteeStatement) {
							GuaranteeStatement gs = (GuaranteeStatement) ss;
							String guarantee = "guarantee " + gs.getName().trim() + " \"" + gs.getStr().trim() + "\" : "
									+ unparser.unparseExpr(gs.getExpr(), "").trim() + ";";
							guarantees.add(guarantee);
						}
					}
					break;
				}
			}
			return guarantees;
		});

	}

	private ComponentCategory getParentComponentCategory(EObject eObj) {
		PortConnection selectedConnection = (PortConnection) eObj;
		return selectedConnection.getContainingComponentImpl().getCategory();
	}

	private ComponentCategory getFilterComponentCategory(EObject eObj) {
		return null;
	}

}
