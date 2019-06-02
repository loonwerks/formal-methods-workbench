package com.collins.fmw.cyres.architecture.handlers;

import java.util.ArrayList;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.utils.EditorUtils;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;
import org.osate.aadl2.Aadl2Package;
import org.osate.aadl2.AadlPackage;
import org.osate.aadl2.ConnectedElement;
import org.osate.aadl2.ConnectionEnd;
import org.osate.aadl2.Feature;
import org.osate.aadl2.Port;
import org.osate.aadl2.PortConnection;
import org.osate.aadl2.ProcessImplementation;
import org.osate.aadl2.PublicPackageSection;
import org.osate.aadl2.Realization;
import org.osate.aadl2.Subcomponent;
import org.osate.aadl2.ThreadImplementation;
import org.osate.aadl2.ThreadSubcomponent;
import org.osate.aadl2.ThreadType;
import org.osate.ui.dialogs.Dialog;

import com.collins.fmw.cyres.architecture.utils.CaseUtils;

public class AddSimplexHandler extends AadlHandler {

	static final String CONNECTION_IMPL_NAME = "c";

	@Override
	protected void runCommand(URI uri) {

		// Wrap selected component in a simplex architecture

		// Check if it is a subcomponent or component implementation
		final EObject eObj = getEObject(uri);
//		ComponentImplementation ci = null;
		Subcomponent controller = null;
//		String selectedSubcomponent = "";
		if (eObj instanceof Subcomponent) {
//			ci = ((Subcomponent) eObj).getContainingComponentImpl();
//			selectedSubcomponent = ((Subcomponent) eObj).getName();
			controller = (Subcomponent) eObj;
//		} else if (eObj instanceof ComponentImplementation) {
//			ci = (ComponentImplementation) eObj;
		} else {
			Dialog.showError("No component is selected",
					"A communication driver subcomponent (or its containing implementation) must be selected to add an attestation manager.");
			return;
		}

		createSimplex(controller);
	}

	public void createSimplex(Subcomponent controller) {

		// Get the active xtext editor so we can make modifications
		final XtextEditor xtextEditor = EditorUtils.getActiveXtextEditor();

		xtextEditor.getDocument().modify(new IUnitOfWork.Void<XtextResource>() {

			@Override
			public void process(final XtextResource resource) throws Exception {

				final AadlPackage aadlPkg = (AadlPackage) resource.getContents().get(0);
				final PublicPackageSection pkgSection = aadlPkg.getOwnedPublicSection();

				// Import CASE_Properties file
				if (!CaseUtils.addCasePropertyImport(pkgSection)) {
					return;
				}
				// Import CASE_Model_Transformations file
				if (!CaseUtils.addCaseModelTransformationsImport(pkgSection, true)) {
					return;
				}

				// Get inputs and outputs
//				int numInputs = 0;
//				int numOutputs = 0;
				ArrayList<Port> controllerPortIn = new ArrayList<>();
				ArrayList<Port> controllerPortOut = new ArrayList<>();
				for (Feature feature : controller.getAllFeatures()) {
					if (feature instanceof Port) {
						Port p = (Port) feature;
						// TODO: Handle bidirectional ports?
						if (p.isIn()) {
//							numInputs++;
							controllerPortIn.add(p);
						} else
						if (p.isOut()) {
//							numOutputs++;
							controllerPortOut.add(p);
						}
					}
				}

//				// Import AAHAA_Properties file
//				if (!addAahaaPropertyImport(pkgSection)) {
//					return;
//				}
//
//				// Get AAHAA property set
//				PropertySet aahaaPropSet = getPropertySet(AAHAA_PROPSET_NAME, AAHAA_PROPSET_FILE,
//						resource.getResourceSet());


				// Add Monitor
				final ThreadType monitorThreadType = (ThreadType) pkgSection
						.createOwnedClassifier(Aadl2Package.eINSTANCE.getThreadType());
				// Give it a name
				monitorThreadType.setName("Monitor");
				// Create ports
				ArrayList<Port> monitorActualPortIn = new ArrayList<>();
				ArrayList<Port> monitorEstimatedPortIn = new ArrayList<>();
				// Ground Truth connections
				for (int i = 0; i < controllerPortIn.size(); i++) {
					Port portIn = monitorThreadType.createOwnedEventDataPort();
//					Port portIn = ComponentSwitch.createOwnedEventDataPort(monitorThreadType);
					portIn.setIn(true);
//					if (controllerPortOut.size() == 1) {
//						portIn.setName("Actual");
//					} else {
//						portIn.setName("Actual_" + (i + 1));
//					}
					portIn.setName("Actual_" + controllerPortIn.get(i).getName());
					monitorActualPortIn.add(portIn);
				}
				// Controller connections
				for (int i = 0; i < controllerPortIn.size(); i++) {
					Port portIn = monitorThreadType.createOwnedEventDataPort();
//					Port portIn = ComponentSwitch.createOwnedEventDataPort(monitorThreadType);
					portIn.setIn(true);
//					if (controllerPortOut.size() == 1) {
//						portIn.setName("Estimated");
//					} else {
//						portIn.setName("Estimated_" + (i + 1));
//					}
					portIn.setName("Estimated_" + controllerPortIn.get(i).getName());
					monitorEstimatedPortIn.add(portIn);
				}
				// Out ports
				Port monitorPortOut = monitorThreadType.createOwnedEventDataPort(); // Alert
//				Port monitorPortOut = ComponentSwitch.createOwnedEventDataPort(monitorThreadType); // Alert
				monitorPortOut.setOut(true);
				monitorPortOut.setName("Alert");

				// Add Monitor properties
				if (!CaseUtils.addCasePropertyAssociation("COMP_TYPE", "MONITOR", monitorThreadType)) {
//					return;
				}

				// Create Monitor implementation
				final ThreadImplementation monitorThreadImpl = (ThreadImplementation) pkgSection
						.createOwnedClassifier(Aadl2Package.eINSTANCE.getThreadImplementation());
				monitorThreadImpl.setName(monitorThreadType.getName() + ".Impl");
				final Realization monitorRealization = monitorThreadImpl.createOwnedRealization();
				monitorRealization.setImplemented(monitorThreadType);

//				// Add Alert
//				final ThreadType alertThreadType = (ThreadType) pkgSection
//						.createOwnedClassifier(Aadl2Package.eINSTANCE.getThreadType());
//				// Give it a name
//				alertThreadType.setName("Alert");
//				// Create in port
//				Port alertPortIn = alertThreadType.createOwnedEventDataPort();
////				Port alertPortIn = ComponentSwitch.createOwnedEventDataPort(alertThreadType);
//				alertPortIn.setIn(true);
//				alertPortIn.setName("Alert");
//
//				// Create Alert implementation
//				final ThreadImplementation alertThreadImpl = (ThreadImplementation) pkgSection
//						.createOwnedClassifier(Aadl2Package.eINSTANCE.getThreadImplementation());
//				alertThreadImpl.setName(alertThreadType.getName() + ".Impl");
//				final Realization alertRealization = alertThreadImpl.createOwnedRealization();
//				alertRealization.setImplemented(alertThreadType);

				// Add Safety Controller
				final ThreadType safetyThreadType = (ThreadType) pkgSection
						.createOwnedClassifier(Aadl2Package.eINSTANCE.getThreadType());
				// Give it a name
				safetyThreadType.setName("Safety_Controller");
				// Create out port
				ArrayList<Port> safetyPortOut = new ArrayList<>();
				for (int i = 0; i < controllerPortOut.size(); i++) {
					Port portOut = safetyThreadType.createOwnedEventDataPort();
//				Port safetyPortOut = ComponentSwitch.createOwnedEventDataPort(safetyThreadType);
					portOut.setOut(true);
//					if (controllerPortOut.size() == 1) {
//						portOut.setName("Safe_Command");
//					} else {
//						portOut.setName("Safe_Command_" + (i + 1));
//					}
					portOut.setName("Safe_" + controllerPortOut.get(i).getName());
					safetyPortOut.add(portOut);
				}

				// Add Safety Controller properties
				// AAHAA::Component_Type Property
				if (!CaseUtils.addCasePropertyAssociation("COMP_TYPE", "SAFETY_CONTROLLER", safetyThreadType)) {
//					return;
				}

				// Create Safety Controller implementation
				final ThreadImplementation safetyThreadImpl = (ThreadImplementation) pkgSection
						.createOwnedClassifier(Aadl2Package.eINSTANCE.getThreadImplementation());
				safetyThreadImpl.setName(safetyThreadType.getName() + ".Impl");
				final Realization safetyRealization = safetyThreadImpl.createOwnedRealization();
				safetyRealization.setImplemented(safetyThreadType);

				// Add Switch
				final ThreadType switchThreadType = (ThreadType) pkgSection
						.createOwnedClassifier(Aadl2Package.eINSTANCE.getThreadType());
				// Give it a name
				switchThreadType.setName("Switch");

				// Create ports
				// Alert
				Port switchTriggerPortIn = switchThreadType.createOwnedEventDataPort();
//				Port switchTriggerPortIn = ComponentSwitch.createOwnedEventDataPort(switchThreadType);
				switchTriggerPortIn.setIn(true);
				switchTriggerPortIn.setName("Safety_Trigger");
				// Safety
				ArrayList<Port> switchSafetyPortIn = new ArrayList<>();
				for (int i = 0; i < controllerPortOut.size(); i++) {
					Port portIn = switchThreadType.createOwnedEventDataPort();
//					Port portIn = ComponentSwitch.createOwnedEventDataPort(switchThreadType);
					portIn.setIn(true);
//					if (controllerPortOut.size() == 1) {
//						portIn.setName("Safe_Command");
//					} else {
//						portIn.setName("Safe_Command_" + (i + 1));
//					}
					portIn.setName("Safe_" + controllerPortOut.get(i).getName());
					switchSafetyPortIn.add(portIn);
				}
				// Controller
				ArrayList<Port> switchControllerPortIn = new ArrayList<>();
				for (int i = 0; i < controllerPortOut.size(); i++) {
					Port portIn = switchThreadType.createOwnedEventDataPort();
//					Port portIn = ComponentSwitch.createOwnedEventDataPort(switchThreadType);
					portIn.setIn(true);
//					if (controllerPortOut.size() == 1) {
//						portIn.setName("Controller_Command");
//					} else {
//						portIn.setName("Controller_Command_" + (i + 1));
//					}
					portIn.setName(controllerPortOut.get(i).getName());
					switchControllerPortIn.add(portIn);
				}
				// Out ports
				ArrayList<Port> switchPortOut = new ArrayList<>();
				for (int i = 0; i < controllerPortOut.size(); i++) {
					Port portOut = switchThreadType.createOwnedEventDataPort();
//					Port portOut = ComponentSwitch.createOwnedEventDataPort(switchThreadType);
					portOut.setOut(true);
//					if (controllerPortOut.size() == 1) {
//						portOut.setName("Switch_Command");
//					} else {
//						portOut.setName("Switch_Command_" + (i + 1));
//					}
					portOut.setName("Switch_" + controllerPortOut.get(i).getName());
					switchPortOut.add(portOut);
				}

				// Add Controller properties
				// AAHAA::Component_Type Property
				if (!CaseUtils.addCasePropertyAssociation("COMP_TYPE", "SWITCH", switchThreadType)) {
//					return;
				}

				// Create Switch implementation
				final ThreadImplementation switchThreadImpl = (ThreadImplementation) pkgSection
						.createOwnedClassifier(Aadl2Package.eINSTANCE.getThreadImplementation());
				switchThreadImpl.setName(switchThreadType.getName() + ".Impl");
				final Realization switchRealization = switchThreadImpl.createOwnedRealization();
				switchRealization.setImplemented(switchThreadType);

//				// Create System
//				final ProcessType systemProcessType = (ProcessType) pkgSection
//						.createOwnedClassifier(Aadl2Package.eINSTANCE.getProcessType());
//				// Give it a name
//				systemProcessType.setName("LEC_SIMPLEX_SYSTEM");
//				// Create in ports
//				ArrayList<Port> systemActualPortIn = new ArrayList<>();
//				ArrayList<Port> systemLecPortIn = new ArrayList<>();
//				for (int i = 0; i < numLecOutputs; i++) {
//					Port portIn = systemProcessType.createOwnedEventDataPort();
////					Port portIn = ComponentSwitch.createOwnedEventDataPort(systemProcessType);
//					portIn.setIn(true);
//					portIn.setName("Actual_" + (i + 1));
//					systemActualPortIn.add(portIn);
//				}
//				for (int i = 0; i < numLecInputs; i++) {
//					Port portIn = systemProcessType.createOwnedEventDataPort();
////					Port portIn = ComponentSwitch.createOwnedEventDataPort(systemProcessType);
//					portIn.setIn(true);
//					portIn.setName("LEC_" + (i + 1));
//					systemLecPortIn.add(portIn);
//				}
//				Port systemPortOut = systemProcessType.createOwnedEventDataPort();
////				Port systemPortOut = ComponentSwitch.createOwnedEventDataPort(systemProcessType);
//				systemPortOut.setOut(true);
//				systemPortOut.setName("Command");
//
//				// Create System implementation
//				final ProcessImplementation systemProcessImpl = (ProcessImplementation) pkgSection
//						.createOwnedClassifier(Aadl2Package.eINSTANCE.getProcessImplementation());
//				systemProcessImpl.setName(systemProcessType.getName() + ".Impl");
//				final Realization systemRealization = systemProcessImpl.createOwnedRealization();
//				systemRealization.setImplemented(systemProcessType);

				// Put threads in process
				ProcessImplementation processImpl = (ProcessImplementation) controller.getContainingComponentImpl();
				// Monitor
				ThreadSubcomponent monitorThreadSubComp = processImpl.createOwnedThreadSubcomponent();
				monitorThreadSubComp.setName("Monitor");
				monitorThreadSubComp.setThreadSubcomponentType(monitorThreadImpl);
				// Safety Controller
				ThreadSubcomponent safetyThreadSubComp = processImpl.createOwnedThreadSubcomponent();
				safetyThreadSubComp.setName("Safety");
				safetyThreadSubComp.setThreadSubcomponentType(safetyThreadImpl);
				// Switch
				ThreadSubcomponent switchThreadSubComp = processImpl.createOwnedThreadSubcomponent();
				switchThreadSubComp.setName("Switch");
				switchThreadSubComp.setThreadSubcomponentType(switchThreadImpl);
//				// Alert
//				ThreadSubcomponent alertThreadSubComp = processImpl.createOwnedThreadSubcomponent();
//				alertThreadSubComp.setName("Alert");
//				alertThreadSubComp.setThreadSubcomponentType(alertThreadImpl);

				// Wire subcomponents together
				PortConnection portConn = null;
				ConnectedElement src = null;
				ConnectedElement dst = null;

				// pre-controller->monitor
				// Get whatever feeds the controller
				ArrayList<Subcomponent> sub = new ArrayList<>();
				ArrayList<ConnectionEnd> ce = new ArrayList<>();
				for (PortConnection pc : processImpl.getOwnedPortConnections()) {
					if (pc.getDestination().getContext() == controller) {
						sub.add((Subcomponent) pc.getSource().getContext());
						ce.add(pc.getSource().getConnectionEnd());
					}
				}
				for (int i = 0; i < controllerPortIn.size(); i++) {
					portConn = processImpl.createOwnedPortConnection();
					// Give it a unique name
					portConn.setName(
							getUniqueName(CONNECTION_IMPL_NAME, false, processImpl.getOwnedPortConnections()));
					portConn.setBidirectional(false);
					src = portConn.createSource();
					src.setContext(sub.get(i));
					src.setConnectionEnd(ce.get(i));
					dst = portConn.createDestination();
					dst.setContext(monitorThreadSubComp);
					dst.setConnectionEnd(monitorEstimatedPortIn.get(i));
				}

//				// monitor->alert
//				portConn = processImpl.createOwnedPortConnection();
//				// Give it a unique name
//				portConn.setName(
//						getUniqueName(CONNECTION_IMPL_NAME, false, processImpl.getOwnedPortConnections()));
//				portConn.setBidirectional(false);
//				src = portConn.createSource();
//				src.setContext(monitorThreadSubComp);
//				src.setConnectionEnd(monitorPortOut);
//				dst = portConn.createDestination();
//				dst.setContext(alertThreadSubComp);
//				dst.setConnectionEnd(alertPortIn);

				// monitor->switch
				portConn = processImpl.createOwnedPortConnection();
				// Give it a unique name
				portConn.setName(
						getUniqueName(CONNECTION_IMPL_NAME, false, processImpl.getOwnedPortConnections()));
				portConn.setBidirectional(false);
				src = portConn.createSource();
				src.setContext(monitorThreadSubComp);
				src.setConnectionEnd(monitorPortOut);
				dst = portConn.createDestination();
				dst.setContext(switchThreadSubComp);
				dst.setConnectionEnd(switchTriggerPortIn);

				// safety->switch
				for (int i = 0; i < controllerPortOut.size(); i++) {
					portConn = processImpl.createOwnedPortConnection();
					// Give it a unique name
					portConn.setName(
						getUniqueName(CONNECTION_IMPL_NAME, false, processImpl.getOwnedPortConnections()));
					portConn.setBidirectional(false);

					src = portConn.createSource();
					src.setContext(safetyThreadSubComp);
					src.setConnectionEnd(safetyPortOut.get(i));
					dst = portConn.createDestination();
					dst.setContext(switchThreadSubComp);
					dst.setConnectionEnd(switchSafetyPortIn.get(i));
				}

				// controller->switch
				for (int i = 0; i < controllerPortOut.size(); i++) {
					portConn = processImpl.createOwnedPortConnection();
					// Give it a unique name
					portConn.setName(
						getUniqueName(CONNECTION_IMPL_NAME, false, processImpl.getOwnedPortConnections()));
					portConn.setBidirectional(false);

					src = portConn.createSource();
					src.setContext(controller);
					src.setConnectionEnd(controllerPortOut.get(i));
					dst = portConn.createDestination();
					dst.setContext(switchThreadSubComp);
					dst.setConnectionEnd(switchControllerPortIn.get(i));
				}

				// switch->post-controller
				// Get whatever the switch feeds
				sub.clear();
				ce.clear();
				ArrayList<PortConnection> deleteConn = new ArrayList<>();
//				Iterator<PortConnection> it = processImpl.getOwnedPortConnections().iterator();
				for (PortConnection pc : processImpl.getOwnedPortConnections()) {
//				while (it.hasNext()) {
//					PortConnection pc = it.next();
					if (pc.getSource().getContext() == controller
							&& pc.getDestination().getContext() != switchThreadSubComp) {
						sub.add((Subcomponent) pc.getDestination().getContext());
						ce.add(pc.getDestination().getConnectionEnd());
						// delete this connection
//						it.remove();
						deleteConn.add(pc);
					}
				}
				for (int i = 0; i < controllerPortOut.size(); i++) {
					portConn = processImpl.createOwnedPortConnection();
					// Give it a unique name
					portConn.setName(
						getUniqueName(CONNECTION_IMPL_NAME, false, processImpl.getOwnedPortConnections()));
					portConn.setBidirectional(false);

					src = portConn.createSource();
					src.setContext(switchThreadSubComp);
					src.setConnectionEnd(switchPortOut.get(i));
					dst = portConn.createDestination();
					dst.setContext(sub.get(i));
					dst.setConnectionEnd(ce.get(i));
				}
				for (PortConnection pc : deleteConn) {
					processImpl.getOwnedPortConnections().remove(pc);
				}

				// Move process and process implementation to bottom of package
				pkgSection.getOwnedClassifiers().move(pkgSection.getOwnedClassifiers().size() - 1,
						getIndex(processImpl.getContainingClassifier().getName(), pkgSection.getOwnedClassifiers()));
				pkgSection.getOwnedClassifiers().move(pkgSection.getOwnedClassifiers().size() - 1,
						getIndex(processImpl.getName(), pkgSection.getOwnedClassifiers()));

			}

		});

	}

}
