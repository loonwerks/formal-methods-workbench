package com.rockwellcollins.atc.darpacase.architecture.handlers;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.PlatformUI;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.utils.EditorUtils;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;
import org.osate.aadl2.Aadl2Package;
import org.osate.aadl2.ConnectedElement;
import org.osate.aadl2.Context;
import org.osate.aadl2.DataPort;
import org.osate.aadl2.EventDataPort;
import org.osate.aadl2.EventPort;
import org.osate.aadl2.ModelUnit;
import org.osate.aadl2.PackageSection;
import org.osate.aadl2.Port;
import org.osate.aadl2.PortConnection;
import org.osate.aadl2.PrivatePackageSection;
import org.osate.aadl2.PropertySet;
import org.osate.aadl2.PublicPackageSection;
import org.osate.aadl2.ThreadSubcomponent;
import org.osate.aadl2.ThreadType;
import org.osate.aadl2.impl.AadlPackageImpl;
import org.osate.aadl2.impl.PortImpl;
import org.osate.aadl2.impl.ProcessImplementationImpl;
import org.osate.aadl2.impl.SubcomponentImpl;
import org.osate.ui.dialogs.Dialog;

import com.rockwellcollins.atc.darpacase.architecture.dialogs.AddMonitorDialog;

public class AddMonitor extends AadlHandler {

	static final String MONITOR_COMP_BASE_NAME = "Monitor";
	static final String MONITOR_PORT_IN_NAME = "monitor_in";
	static final String MONITOR_PORT_OUT_NAME = "monitor_out";
	static final String MONITOR_ALERT_PORT_NAME = "monitor_event";
	static final String MONITOR_IMPL_BASE_NAME = "MON";
	static final String CONNECTION_IMPL_BASE_NAME = "c";

	private String monitorImplementationLanguage;
//	private String monitorRegularExpression;
	private String monitorTypeName;
	private String monitorImplName;

	@Override
	protected void runCommand(URI uri) {

		// Check if it is a connection
		final EObject eObj = getEObject(uri);
		if (!(eObj instanceof PortConnection)) {
			Dialog.showError("No connection is selected",
					"A connection between two components must be selected to add a monitor.");
			return;
		}

		// Open wizard to enter monitor info
		AddMonitorDialog wizard = new AddMonitorDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		wizard.create();
		if (wizard.open() == Window.OK) {
			monitorImplementationLanguage = wizard.getMonitorImplementationLanguage();
//			monitorRegularExpression = wizard.getMonitorRegularExpression();
			monitorTypeName = wizard.getMonitorTypeName();
			monitorImplName = wizard.getMonitorImplName();
			if (monitorTypeName == "") {
				monitorTypeName = MONITOR_COMP_BASE_NAME;
			}
			if (monitorImplName == "") {
				monitorImplName = MONITOR_IMPL_BASE_NAME;
			}
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

		xtextEditor.getDocument().modify(new IUnitOfWork.Void<XtextResource>() {

			@Override
			public void process(final XtextResource resource) throws Exception {

				// Retrieve the model object to modify
				final PortConnection selectedConnection = (PortConnection) resource.getEObject(uri.fragment());
				final AadlPackageImpl aadlPkg = (AadlPackageImpl) resource.getContents().get(0);
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

				// CASE Property file
				// First check if CASE Property file has already been imported in the model
				final EList<ModelUnit> importedUnits = pkgSection.getImportedUnits();
				PropertySet casePropSet = null;
				for (ModelUnit modelUnit : importedUnits) {
					if (modelUnit instanceof PropertySet) {
						if (modelUnit.getName().equals(CASE_PROPSET_NAME)) {
							casePropSet = (PropertySet) modelUnit;
							break;
						}
					}
				}

				if (casePropSet == null) {
					// Try importing the resource
					casePropSet = getPropertySet(CASE_PROPSET_NAME, CASE_PROPSET_FILE, resource.getResourceSet());
					if (casePropSet == null) {
						return;
					}
					// Add as "importedUnit" to package section
					pkgSection.getImportedUnits().add(casePropSet);
				}

				// Create Monitor thread type
				final ThreadType monitorThreadType = (ThreadType) pkgSection
						.createOwnedClassifier(Aadl2Package.eINSTANCE.getThreadType());
				// Give it a unique name
				monitorThreadType.setName(getUniqueName(monitorTypeName, true, pkgSection.getOwnedClassifiers()));

				// Create monitor ports
				final PortImpl portImpl = (PortImpl) selectedConnection.getDestination().getConnectionEnd();
				Port portIn = null;
				Port portOut = null;
				if (portImpl instanceof EventDataPort) {
					portIn = monitorThreadType.createOwnedEventDataPort();
					((EventDataPort) portIn)
							.setDataFeatureClassifier(((EventDataPort) portImpl).getDataFeatureClassifier());
					portOut = monitorThreadType.createOwnedEventDataPort();
					((EventDataPort) portOut)
							.setDataFeatureClassifier(((EventDataPort) portImpl).getDataFeatureClassifier());
				} else if (portImpl instanceof EventPort) {
					portIn = monitorThreadType.createOwnedEventPort();
					portOut = monitorThreadType.createOwnedEventPort();
				} else if (portImpl instanceof DataPort) {
					portIn = monitorThreadType.createOwnedDataPort();
					((DataPort) portIn).setDataFeatureClassifier(((DataPort) portImpl).getDataFeatureClassifier());
					portOut = monitorThreadType.createOwnedDataPort();
					((DataPort) portOut).setDataFeatureClassifier(((DataPort) portImpl).getDataFeatureClassifier());
				} else {
					Dialog.showError("Undetermined port type",
							"Could not determine the port type of the destination component.");
					return;
				}

				portIn.setIn(true);
				portIn.setName(MONITOR_PORT_IN_NAME);
//				final EventDataPort eventDataPortIn = monitorThreadType.createOwnedEventDataPort();
//				eventDataPortIn.setIn(true);
//				eventDataPortIn.setName(MONITOR_PORT_IN_NAME);
//				final EventDataPortImpl eventDataPortImpl = (EventDataPortImpl) selectedConnection.getDestination()
//						.getConnectionEnd();
//				eventDataPortIn.setDataFeatureClassifier(eventDataPortImpl.getDataFeatureClassifier());

				portOut.setOut(true);
				portOut.setName(MONITOR_PORT_OUT_NAME);
//				final EventDataPort eventDataPortOut = monitorThreadType.createOwnedEventDataPort();
//				eventDataPortOut.setOut(true);
//				eventDataPortOut.setName(MONITOR_PORT_OUT_NAME);
//				eventDataPortOut.setDataFeatureClassifier(eventDataPortImpl.getDataFeatureClassifier());

				// Create the threshold exceeded event port
				// TODO: Should this be an event data port?
				final EventPort alertPort = monitorThreadType.createOwnedEventPort();
				alertPort.setOut(true);
				alertPort.setName(MONITOR_ALERT_PORT_NAME);

				// Add monitor properties
				// CASE::COMP_TYPE Property
				if (!addPropertyAssociation("COMP_TYPE", "MONITOR", monitorThreadType, casePropSet)) {
//					return;
				}
				// CASE::COMP_IMPL property
				if (!addPropertyAssociation("COMP_IMPL", monitorImplementationLanguage, monitorThreadType,
						casePropSet)) {
//					return;
				}
				// CASE::COMP_SPEC property
//				if (!addPropertyAssociation("COMP_SPEC", monitorRegularExpression, monitorThreadType, casePropSet)) {
////					return;
//				}

				// Move monitor to proper location
				// (just before component it connects to on communication pathway)
				final Context context = selectedConnection.getDestination().getContext();
				String destName = ((SubcomponentImpl) context).getSubcomponentType().getName();
				pkgSection.getOwnedClassifiers().move(getIndex(destName, pkgSection.getOwnedClassifiers()),
						pkgSection.getOwnedClassifiers().size() - 1);

				// Insert monitor feature in process component implementation
				final ProcessImplementationImpl procImpl = (ProcessImplementationImpl) selectedConnection
						.getContainingComponentImpl();
				final ThreadSubcomponent monitorThreadSubComp = procImpl.createOwnedThreadSubcomponent();

				// Give it a unique name
				monitorThreadSubComp.setName(getUniqueName(monitorImplName, true, procImpl.getOwnedSubcomponents()));
				monitorThreadSubComp.setThreadSubcomponentType(monitorThreadType);

				// Put it in the right place
				destName = selectedConnection.getDestination().getContext().getName();
				procImpl.getOwnedThreadSubcomponents().move(getIndex(destName, procImpl.getOwnedThreadSubcomponents()),
						procImpl.getOwnedThreadSubcomponents().size() - 1);

				// Create connection from monitor to connection destination
				final PortConnection portConnOut = procImpl.createOwnedPortConnection();
				// Give it a unique name
				portConnOut
						.setName(getUniqueName(CONNECTION_IMPL_BASE_NAME, false, procImpl.getOwnedPortConnections()));
				portConnOut.setBidirectional(false);
				final ConnectedElement monitorOutSrc = portConnOut.createSource();
				monitorOutSrc.setContext(monitorThreadSubComp);
//				monitorOutSrc.setConnectionEnd(eventDataPortOut);
				monitorOutSrc.setConnectionEnd(portOut);
				final ConnectedElement monitorOutDst = portConnOut.createDestination();
				monitorOutDst.setContext(selectedConnection.getDestination().getContext());
				monitorOutDst.setConnectionEnd(selectedConnection.getDestination().getConnectionEnd());

				// Put portConnOut in right place (after portConnIn)
				destName = selectedConnection.getName();
				procImpl.getOwnedPortConnections().move(getIndex(destName, procImpl.getOwnedPortConnections()) + 1,
						procImpl.getOwnedPortConnections().size() - 1);

				// Rewire selected connection so the monitor is the destination
				final PortConnection portConnIn = selectedConnection;
				portConnIn.getDestination().setContext(monitorThreadSubComp);
//				portConnIn.getDestination().setConnectionEnd(eventDataPortIn);
				portConnIn.getDestination().setConnectionEnd(portIn);

			}
		});

	}

}
