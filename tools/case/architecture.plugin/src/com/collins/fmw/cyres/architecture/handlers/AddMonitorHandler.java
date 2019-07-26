package com.collins.fmw.cyres.architecture.handlers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.PlatformUI;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.utils.EditorUtils;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;
import org.osate.aadl2.AadlPackage;
import org.osate.aadl2.ComponentCategory;
import org.osate.aadl2.ComponentImplementation;
import org.osate.aadl2.ComponentType;
import org.osate.aadl2.ConnectedElement;
import org.osate.aadl2.DataPort;
import org.osate.aadl2.DataSubcomponentType;
import org.osate.aadl2.EventDataPort;
import org.osate.aadl2.EventPort;
import org.osate.aadl2.PackageSection;
import org.osate.aadl2.Port;
import org.osate.aadl2.PortCategory;
import org.osate.aadl2.PortConnection;
import org.osate.aadl2.PrivatePackageSection;
import org.osate.aadl2.PublicPackageSection;
import org.osate.aadl2.Realization;
import org.osate.aadl2.Subcomponent;
import org.osate.ui.dialogs.Dialog;

import com.collins.fmw.cyres.architecture.dialogs.AddMonitorDialog;
import com.collins.fmw.cyres.architecture.requirements.AddMonitorClaim;
import com.collins.fmw.cyres.architecture.requirements.CyberRequirement;
import com.collins.fmw.cyres.architecture.requirements.RequirementsManager;
import com.collins.fmw.cyres.architecture.utils.CaseUtils;
import com.collins.fmw.cyres.architecture.utils.ComponentCreateHelper;

public class AddMonitorHandler extends AadlHandler {

	static final String MONITOR_COMP_TYPE_NAME = "CASE_Monitor";
	static final String MONITOR_PORT_EXPECTED_NAME = "expected";
	static final String MONITOR_PORT_OBSERVED_NAME = "observed";
	static final String MONITOR_ALERT_PORT_NAME = "alert";
	public static final String MONITOR_IMPL_NAME = "MON";
	static final String CONNECTION_IMPL_NAME = "c";

	private String monitorImplementationName;
	private String monitorImplementationLanguage;
	private PortCategory alertPortType;
	private String monitorRequirement;
	private String monitorAgreeProperty;

	@Override
	protected void runCommand(URI uri) {

		// Check if it is a connection
		final EObject eObj = getEObject(uri);
		if (!(eObj instanceof PortConnection)) {
			Dialog.showError("Add Monitor",
					"A connection between two components must be selected to add a monitor.");
			return;
		}

		// Provide list of requirements so the user can choose which requirement is driving this
		// model transformation.
		List<String> requirements = new ArrayList<>();
		RequirementsManager.getInstance().getImportedRequirements().forEach(r -> requirements.add(r.getId()));

		// Open wizard to enter monitor info
		AddMonitorDialog wizard = new AddMonitorDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());

		wizard.setRequirements(requirements);
		wizard.create();
		if (wizard.open() == Window.OK) {
			monitorImplementationName = wizard.getMonitorImplementationName();
			if (monitorImplementationName == "") {
				monitorImplementationName = MONITOR_IMPL_NAME;
			}
			monitorImplementationLanguage = wizard.getMonitorImplementationLanguage();
			alertPortType = wizard.getAlertPortType();
			monitorRequirement = wizard.getRequirement();
			monitorAgreeProperty = wizard.getAgreeProperty();
		} else {
			return;
		}

		// Insert the monitor component
		insertMonitorComponent(uri);

		// Display message that user still needs to wire expected input and alert output
		Dialog.showWarning("Add Monitor",
				"The monitor has been added to the model. However, you must manually wire the monitor's expected input and alert output ports to the appropriate components.");

		return;

	}

	/**
	 * Inserts a monitor component into the model, including monitor type definition
	 * and implementation (including correct wiring for monitored signal).
	 * The monitor is inserted at the location of the selected connection
	 * @param uri - The URI of the selected connection
	 */
	private void insertMonitorComponent(URI uri) {

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
					Dialog.showError("Add Monitor", "No public or private package sections found.");
					return;
				}

				// Import CASE_Properties file
				if (!CaseUtils.addCasePropertyImport(pkgSection)) {
					return;
				}
				// Import CASE_Model_Transformations file
				if (!CaseUtils.addCaseModelTransformationsImport(pkgSection, true)) {
					return;
				}

				// Figure out component type by looking at the component type of the destination component
				ComponentCategory compCategory = ((Subcomponent) selectedConnection.getDestination().getContext())
						.getCategory();

				// If the component type is a process, we will need to put a single thread inside.
				// Per convention, we will attach all properties and contracts to the thread.
				// For this model transformation, we will create the thread first, then wrap it in a process
				// component, using the same mechanism we use for the seL4 transformation
				boolean isProcess = (compCategory == ComponentCategory.PROCESS);
				if (isProcess) {
					compCategory = ComponentCategory.THREAD;
				}

				final ComponentType monitorType = (ComponentType) pkgSection
						.createOwnedClassifier(ComponentCreateHelper.getTypeClass(compCategory));

				// Give it a unique name
				monitorType.setName(getUniqueName(MONITOR_COMP_TYPE_NAME, true, pkgSection.getOwnedClassifiers()));

				// Create monitor input ports
				final Port port = (Port) selectedConnection.getDestination().getConnectionEnd();
				Port portExpected = null;
				Port portObserved = null;
				DataSubcomponentType dataFeatureClassifier = null;
				if (port instanceof EventDataPort) {
					portExpected = ComponentCreateHelper.createOwnedEventDataPort(monitorType);
					dataFeatureClassifier = ((EventDataPort) port).getDataFeatureClassifier();
					((EventDataPort) portExpected).setDataFeatureClassifier(dataFeatureClassifier);
					portObserved = ComponentCreateHelper.createOwnedEventDataPort(monitorType);
					((EventDataPort) portObserved).setDataFeatureClassifier(dataFeatureClassifier);
				} else if (port instanceof DataPort) {
					portExpected = ComponentCreateHelper.createOwnedDataPort(monitorType);
					dataFeatureClassifier = ((DataPort) port).getDataFeatureClassifier();
					((DataPort) portExpected).setDataFeatureClassifier(dataFeatureClassifier);
					portObserved = ComponentCreateHelper.createOwnedDataPort(monitorType);
					((DataPort) portObserved).setDataFeatureClassifier(dataFeatureClassifier);
				} else if (port instanceof EventPort) {
					portExpected = ComponentCreateHelper.createOwnedEventPort(monitorType);
					portObserved = ComponentCreateHelper.createOwnedEventPort(monitorType);
//					Dialog.showError("Add Monitor", "Cannot connect a monitor to a non-data port.");
					return;
				} else {
					Dialog.showError("Add Monitor", "Could not determine the port type of the destination component.");
					return;
				}

				portExpected.setIn(true);
				portExpected.setName(MONITOR_PORT_EXPECTED_NAME);

				portObserved.setIn(true);
				portObserved.setName(MONITOR_PORT_OBSERVED_NAME);

				// Create log port, if necessary
				if (alertPortType != null) {
					Port alertPort = null;
					if (alertPortType == PortCategory.EVENT) {
						alertPort = ComponentCreateHelper.createOwnedEventPort(monitorType);
					} else if (alertPortType == PortCategory.DATA) {
						alertPort = ComponentCreateHelper.createOwnedDataPort(monitorType);
					} else {
						alertPort = ComponentCreateHelper.createOwnedEventDataPort(monitorType);
					}
					alertPort.setOut(true);
					alertPort.setName(MONITOR_ALERT_PORT_NAME);
				}

				// Add monitor properties
				// CASE::COMP_TYPE Property
				if (!CaseUtils.addCasePropertyAssociation("COMP_TYPE", "MONITOR", monitorType)) {
//					return;
				}

				// CASE::COMP_SPEC property
				// Parse the ID from the Monitor AGREE property
				String monitorPropId = "";
				try {
					monitorPropId = monitorAgreeProperty
							.substring(monitorAgreeProperty.toLowerCase().indexOf("guarantee ") + "guarantee ".length(),
									monitorAgreeProperty.indexOf("\""))
							.trim();

				} catch (IndexOutOfBoundsException e) {
					if (!monitorAgreeProperty.isEmpty()) {
						// Agree property is malformed
						Dialog.showWarning("Add Monitor", "Monitor AGREE statement is malformed.");
					}
//					return;
				}

				if (!monitorPropId.isEmpty()) {
					if (!CaseUtils.addCasePropertyAssociation("COMP_SPEC", monitorPropId, monitorType)) {
//						return;
					}
				}

				// Move monitor to proper location
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

				// Create monitor implementation
				final ComponentImplementation monitorImpl = (ComponentImplementation) pkgSection
						.createOwnedClassifier(ComponentCreateHelper.getImplClass(compCategory));
				monitorImpl.setName(monitorType.getName() + ".Impl");
				final Realization r = monitorImpl.createOwnedRealization();
				r.setImplemented(monitorType);

				// Add it to proper place
				pkgSection.getOwnedClassifiers().move(getIndex(destName, pkgSection.getOwnedClassifiers()),
						pkgSection.getOwnedClassifiers().size() - 1);

				// CASE::COMP_IMPL property
				if (!monitorImplementationLanguage.isEmpty()) {
					if (!CaseUtils.addCasePropertyAssociation("COMP_IMPL", monitorImplementationLanguage,
							monitorImpl)) {
//						return;
					}
				}

				final ComponentImplementation containingImpl = selectedConnection.getContainingComponentImpl();

				// Insert monitor subcomponent in containing component implementation
				final Subcomponent monitorSubcomp = ComponentCreateHelper.createOwnedSubcomponent(containingImpl,
						compCategory);

				// Give it a unique name
				monitorSubcomp.setName(
						getUniqueName(monitorImplementationName, true, containingImpl.getOwnedSubcomponents()));

				ComponentCreateHelper.setSubcomponentType(monitorSubcomp, monitorImpl);

				// Create a connection from selected connection source to monitor observed input
				final PortConnection portConnObserved = containingImpl.createOwnedPortConnection();
				// Give it a unique name
				portConnObserved
						.setName(getUniqueName(CONNECTION_IMPL_NAME, false, containingImpl.getOwnedPortConnections()));
				portConnObserved.setBidirectional(false);
				final ConnectedElement monitorObservedSrc = portConnObserved.createSource();
				monitorObservedSrc.setContext(selectedConnection.getSource().getContext());
				monitorObservedSrc.setConnectionEnd(selectedConnection.getSource().getConnectionEnd());
				final ConnectedElement monitorObservedDst = portConnObserved.createDestination();
				monitorObservedDst.setContext(monitorSubcomp);
				monitorObservedDst.setConnectionEnd(portObserved);

				// Put portConnObserved in right place (after selected connection)
				destName = selectedConnection.getName();
				containingImpl.getOwnedPortConnections().move(
						getIndex(destName, containingImpl.getOwnedPortConnections()) + 1,
						containingImpl.getOwnedPortConnections().size() - 1);

				// TODO: Add add_monitor claims to resolute prove statement, if applicable
				if (!monitorRequirement.isEmpty()) {
					CyberRequirement req = RequirementsManager.getInstance().getRequirement(monitorRequirement);
					RequirementsManager.getInstance().modifyRequirement(monitorRequirement, resource,
							new AddMonitorClaim(req.getContext(), monitorSubcomp));

				}

			}
		});
	}



}
