package com.collins.fmw.cyres.architecture.handlers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.PlatformUI;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.utils.EditorUtils;
import org.osate.aadl2.Aadl2Factory;
import org.osate.aadl2.AadlPackage;
import org.osate.aadl2.ComponentCategory;
import org.osate.aadl2.ComponentImplementation;
import org.osate.aadl2.ComponentType;
import org.osate.aadl2.ConnectedElement;
import org.osate.aadl2.DataPort;
import org.osate.aadl2.DataSubcomponentType;
import org.osate.aadl2.EnumerationLiteral;
import org.osate.aadl2.EventDataPort;
import org.osate.aadl2.EventPort;
import org.osate.aadl2.Feature;
import org.osate.aadl2.NamedValue;
import org.osate.aadl2.PackageSection;
import org.osate.aadl2.Port;
import org.osate.aadl2.PortConnection;
import org.osate.aadl2.PrivatePackageSection;
import org.osate.aadl2.Property;
import org.osate.aadl2.PublicPackageSection;
import org.osate.aadl2.Realization;
import org.osate.aadl2.Subcomponent;
import org.osate.ui.dialogs.Dialog;
import org.osate.xtext.aadl2.properties.util.GetProperties;
import org.osate.xtext.aadl2.properties.util.ThreadProperties;

import com.collins.fmw.cyres.architecture.dialogs.AddMonitorDialog;
import com.collins.fmw.cyres.architecture.requirements.AddMonitorClaim;
import com.collins.fmw.cyres.architecture.requirements.CyberRequirement;
import com.collins.fmw.cyres.architecture.requirements.RequirementsManager;
import com.collins.fmw.cyres.architecture.utils.CaseUtils;
import com.collins.fmw.cyres.architecture.utils.ComponentCreateHelper;

public class AddMonitorHandler extends AadlHandler {

	static final String MONITOR_COMP_TYPE_NAME = "CASE_Monitor";
	static final String MONITOR_EXPECTED_PORT_NAME = "expected";
	static final String MONITOR_OBSERVED_PORT_NAME = "observed";
	static final String MONITOR_ALERT_PORT_NAME = "alert";
	public static final String MONITOR_IMPL_NAME = "MON";
	static final String CONNECTION_IMPL_NAME = "c";

	private String monitorImplementationName;
	private String dispatchProtocol;
	private String expectedPort;
	private String alertPort;
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
		final PortConnection selectedConnection = (PortConnection) eObj;
		ComponentImplementation ci = selectedConnection.getContainingComponentImpl();

		// Provide list of outports that can be connected to monitor's expected in port
		List<String> outports = getOutports(ci);

		// Provide list of inports that monitor's alert out port can be connected to
		List<String> inports = getInports(ci);

		// Provide list of requirements so the user can choose which requirement is driving this
		// model transformation.
		List<String> requirements = new ArrayList<>();
		RequirementsManager.getInstance().getImportedRequirements().forEach(r -> requirements.add(r.getId()));

		// Open wizard to enter monitor info
		AddMonitorDialog wizard = new AddMonitorDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());

		wizard.setPorts(inports, outports);
		wizard.setRequirements(requirements);
		wizard.create();
		if (wizard.open() == Window.OK) {
			monitorImplementationName = wizard.getMonitorImplementationName();
			if (monitorImplementationName == "") {
				monitorImplementationName = MONITOR_IMPL_NAME;
			}
			dispatchProtocol = wizard.getDispatchProtocol();
			expectedPort = wizard.getExpectedPort();
			alertPort = wizard.getAlertPort();
			monitorRequirement = wizard.getRequirement();
			monitorAgreeProperty = wizard.getAgreeProperty();
		} else {
			return;
		}

		// Insert the monitor component
		insertMonitorComponent(uri);

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

		AddMonitorClaim claim = xtextEditor.getDocument().modify(resource -> {

			// Retrieve the model object to modify
			PortConnection selectedConnection = (PortConnection) resource.getEObject(uri.fragment());
			final ComponentImplementation containingImpl = selectedConnection.getContainingComponentImpl();
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
				return null;
			}

			// Import CASE_Properties file
			if (!CaseUtils.addCasePropertyImport(pkgSection)) {
				return null;
			}
			// Import CASE_Model_Transformations file
			if (!CaseUtils.addCaseModelTransformationsImport(pkgSection, true)) {
				return null;
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
			} else if (compCategory == ComponentCategory.THREAD_GROUP) {
				compCategory = ComponentCategory.THREAD;
			}

			final ComponentType monitorType = (ComponentType) pkgSection
					.createOwnedClassifier(ComponentCreateHelper.getTypeClass(compCategory));

			// Give it a unique name
			monitorType.setName(getUniqueName(MONITOR_COMP_TYPE_NAME, true, pkgSection.getOwnedClassifiers()));

			// Create monitor observed port
			Port port = (Port) selectedConnection.getDestination().getConnectionEnd();
			Port portObserved = null;
			DataSubcomponentType dataFeatureClassifier = null;
			if (port instanceof EventDataPort) {
				dataFeatureClassifier = ((EventDataPort) port).getDataFeatureClassifier();
				portObserved = ComponentCreateHelper.createOwnedEventDataPort(monitorType);
				((EventDataPort) portObserved).setDataFeatureClassifier(dataFeatureClassifier);
			} else if (port instanceof DataPort) {
				dataFeatureClassifier = ((DataPort) port).getDataFeatureClassifier();
				portObserved = ComponentCreateHelper.createOwnedDataPort(monitorType);
				((DataPort) portObserved).setDataFeatureClassifier(dataFeatureClassifier);
			} else if (port instanceof EventPort) {
				portObserved = ComponentCreateHelper.createOwnedEventPort(monitorType);
				return null;
			} else {
				Dialog.showError("Add Monitor", "Could not determine the port type of the destination component.");
				return null;
			}

			portObserved.setIn(true);
			portObserved.setName(MONITOR_OBSERVED_PORT_NAME);

			// Create expected port
			Port monExpectedPort = null;
			Port srcExpectedPort = getPort(containingImpl, expectedPort);
			// If user didn't specify an expected outport, use the same type as the observed port
			if (srcExpectedPort == null) {
				srcExpectedPort = portObserved;
			}
			if (srcExpectedPort instanceof EventDataPort) {
				monExpectedPort = ComponentCreateHelper.createOwnedEventDataPort(monitorType);
				dataFeatureClassifier = ((EventDataPort) srcExpectedPort).getDataFeatureClassifier();
				((EventDataPort) monExpectedPort).setDataFeatureClassifier(dataFeatureClassifier);
			} else if (srcExpectedPort instanceof DataPort) {
				monExpectedPort = ComponentCreateHelper.createOwnedDataPort(monitorType);
				dataFeatureClassifier = ((DataPort) srcExpectedPort).getDataFeatureClassifier();
				((DataPort) monExpectedPort).setDataFeatureClassifier(dataFeatureClassifier);
			} else if (srcExpectedPort instanceof EventPort) {
				monExpectedPort = ComponentCreateHelper.createOwnedEventPort(monitorType);
			}
			monExpectedPort.setIn(true);
			monExpectedPort.setName(MONITOR_EXPECTED_PORT_NAME);

			// Create monitor alert port
			Port monAlertPort = null;
			final Port dstAlertPort = getPort(containingImpl, alertPort);
			// If user didn't specify an alert inport, make it an event data port
			if (dstAlertPort == null) {
				monAlertPort = ComponentCreateHelper.createOwnedEventDataPort(monitorType);
			} else if (dstAlertPort instanceof EventDataPort) {
				monAlertPort = ComponentCreateHelper.createOwnedEventDataPort(monitorType);
				dataFeatureClassifier = ((EventDataPort) dstAlertPort).getDataFeatureClassifier();
				((EventDataPort) monAlertPort).setDataFeatureClassifier(dataFeatureClassifier);
			} else if (dstAlertPort instanceof DataPort) {
				monAlertPort = ComponentCreateHelper.createOwnedDataPort(monitorType);
				dataFeatureClassifier = ((DataPort) dstAlertPort).getDataFeatureClassifier();
				((DataPort) monAlertPort).setDataFeatureClassifier(dataFeatureClassifier);
			} else if (dstAlertPort instanceof EventPort) {
				monAlertPort = ComponentCreateHelper.createOwnedEventPort(monitorType);
			}
			monAlertPort.setOut(true);
			monAlertPort.setName(MONITOR_ALERT_PORT_NAME);

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

//			// CASE::COMP_IMPL property
//			if (!monitorImplementationLanguage.isEmpty()) {
//				if (!CaseUtils.addCasePropertyAssociation("COMP_IMPL", monitorImplementationLanguage, monitorImpl)) {
////						return;
//				}
//			}

			// Dispatch protocol property
			if (!dispatchProtocol.isEmpty() && compCategory == ComponentCategory.THREAD) {
				Property dispatchProtocolProp = GetProperties.lookupPropertyDefinition(monitorImpl,
						ThreadProperties._NAME, ThreadProperties.DISPATCH_PROTOCOL);
				EnumerationLiteral dispatchProtocolLit = Aadl2Factory.eINSTANCE.createEnumerationLiteral();
				dispatchProtocolLit.setName(dispatchProtocol);
				NamedValue nv = Aadl2Factory.eINSTANCE.createNamedValue();
				nv.setNamedValue(dispatchProtocolLit);
				monitorImpl.setPropertyValue(dispatchProtocolProp, nv);
			}

			// Insert monitor subcomponent in containing component implementation
			final Subcomponent monitorSubcomp = ComponentCreateHelper.createOwnedSubcomponent(containingImpl,
					compCategory);

			// Give it a unique name
			monitorSubcomp
					.setName(getUniqueName(monitorImplementationName, true, containingImpl.getOwnedSubcomponents()));

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

			// Create Expected connection, if provided
			if (!expectedPort.isEmpty()) {
				final PortConnection portConnExpected = containingImpl.createOwnedPortConnection();
				portConnExpected
						.setName(getUniqueName(CONNECTION_IMPL_NAME, false, containingImpl.getOwnedPortConnections()));
				portConnExpected.setBidirectional(false);
				final ConnectedElement monitorExpectedSrc = portConnExpected.createSource();
				monitorExpectedSrc.setContext(getSubcomponent(containingImpl, expectedPort));
				monitorExpectedSrc.setConnectionEnd(srcExpectedPort);
				final ConnectedElement monitorExpectedDst = portConnExpected.createDestination();
				monitorExpectedDst.setContext(monitorSubcomp);
				monitorExpectedDst.setConnectionEnd(monExpectedPort);
				// Put portConnExpected in right place (before portConnObserved)
				destName = portConnObserved.getName();
				containingImpl.getOwnedPortConnections().move(
						getIndex(destName, containingImpl.getOwnedPortConnections()),
						containingImpl.getOwnedPortConnections().size() - 1);
			}

			// Create Alert connection, if provided
			if (!alertPort.isEmpty()) {
				final PortConnection portConnAlert = containingImpl.createOwnedPortConnection();
				portConnAlert
						.setName(getUniqueName(CONNECTION_IMPL_NAME, false, containingImpl.getOwnedPortConnections()));
				portConnAlert.setBidirectional(false);
				final ConnectedElement monitorAlertSrc = portConnAlert.createSource();
				monitorAlertSrc.setContext(monitorSubcomp);
				monitorAlertSrc.setConnectionEnd(monAlertPort);
				final ConnectedElement monitorAlertDst = portConnAlert.createDestination();
				monitorAlertDst.setContext(getSubcomponent(containingImpl, alertPort));
				monitorAlertDst.setConnectionEnd(dstAlertPort);
				// Put portConnAlert in right place (after portConnObserved)
				destName = portConnObserved.getName();
				containingImpl.getOwnedPortConnections().move(
						getIndex(destName, containingImpl.getOwnedPortConnections()) + 1,
						containingImpl.getOwnedPortConnections().size() - 1);
			}

			// Add add_monitor claims to resolute prove statement, if applicable
			if (!monitorRequirement.isEmpty()) {
				CyberRequirement req = RequirementsManager.getInstance().getRequirement(monitorRequirement);
				return new AddMonitorClaim(req.getContext(), monitorSubcomp);

			}

			return null;
		});

		if (claim != null) {
			RequirementsManager.getInstance().modifyRequirement(monitorRequirement, claim);
		}
	}

	/**
	 * Returns all the in data ports in the specified component implementation
	 * @param ci - component implementation
	 * @return list of in port names
	 */
	private List<String> getInports(ComponentImplementation ci) {
		List<String> inports = new ArrayList<>();
		// Get component implementation out ports
		for (Feature f : ci.getAllFeatures()) {
			if (f instanceof Port && ((Port) f).isOut()) {
				inports.add(f.getName());
			}
		}

		// Get subcomponent in ports
		for (Subcomponent s : ci.getOwnedSubcomponents()) {
			for (Feature f : s.getAllFeatures()) {
				if (f instanceof Port && ((Port) f).isIn()) {
					inports.add(s.getName() + "." + f.getName());
				}
			}
		}
		return inports;
	}

	/**
	 * Returns all the out data ports in the specified component implementation
	 * @param ci - component implementation
	 * @return list of out port names
	 */
	private List<String> getOutports(ComponentImplementation ci) {
		List<String> outports = new ArrayList<>();
		// Get component implementation in ports
		for (Feature f : ci.getAllFeatures()) {
			if (f instanceof Port && ((Port) f).isIn()) {
				outports.add(f.getName());
			}
		}

		// Get subcomponent out ports
		for (Subcomponent s : ci.getOwnedSubcomponents()) {
			for (Feature f : s.getAllFeatures()) {
				if (f instanceof Port && ((Port) f).isOut()) {
					outports.add(s.getName() + "." + f.getName());
				}
			}
		}
		return outports;
	}

	/**
	 * Returns the port of the specified subcomponent port name
	 * in the specified component implementation
	 * @param ci - component implementation
	 * @param portName - <subcomponent> . <feature name>
	 * @return
	 */
	private Port getPort(ComponentImplementation ci, String portName) {
		String[] parts = portName.split("\\.");
		if (parts.length == 1) {
			for (Feature f : ci.getAllFeatures()) {
				if (f.getName().equalsIgnoreCase(portName)) {
					if (f instanceof Port) {
						return (Port) f;
					} else {
						return null;
					}
				}
			}
		} else if (parts.length > 1) {
			for (Subcomponent s : ci.getOwnedSubcomponents()) {
				if (s.getName().equalsIgnoreCase(parts[0])) {
					for (Feature f : s.getAllFeatures()) {
						if (f.getName().equalsIgnoreCase(parts[1])) {
							if (f instanceof Port) {
								return (Port) f;
							} else {
								return null;
							}
						}
					}
				}
			}
		}
		return null;
	}

	private Subcomponent getSubcomponent(ComponentImplementation ci, String portName) {
		String[] parts = portName.split("\\.");
		if (parts.length == 2) {
			for (Subcomponent s : ci.getOwnedSubcomponents()) {
				if (s.getName().equalsIgnoreCase(parts[0])) {
					return s;
				}
			}
		}
		return null;
	}

}
