package com.rockwellcollins.atc.darpacase.architecture.handlers;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.utils.EditorUtils;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;
import org.osate.aadl2.Aadl2Package;
import org.osate.aadl2.ConnectedElement;
import org.osate.aadl2.Context;
import org.osate.aadl2.EventDataPort;
import org.osate.aadl2.ModelUnit;
import org.osate.aadl2.PackageSection;
import org.osate.aadl2.PortConnection;
import org.osate.aadl2.PrivatePackageSection;
import org.osate.aadl2.PropertySet;
import org.osate.aadl2.PublicPackageSection;
import org.osate.aadl2.ThreadSubcomponent;
import org.osate.aadl2.ThreadType;
import org.osate.aadl2.impl.AadlPackageImpl;
import org.osate.aadl2.impl.EventDataPortImpl;
import org.osate.aadl2.impl.ProcessImplementationImpl;
import org.osate.aadl2.impl.SubcomponentImpl;
import org.osate.ui.dialogs.Dialog;

public class AddRouter extends AadlHandler {

	static final String ROUTER_COMP_BASE_NAME = "Router";
	static final String ROUTER_PORT_IN_NAME = "router_in";
	static final String ROUTER_PORT_OUT_NAME = "router_out";
	static final String ROUTER_IMPL_BASE_NAME = "RTR";
	static final String CONNECTION_IMPL_BASE_NAME = "c";

	private String routerImplementationLanguage;
//	private String routerRegularExpression;
	private String routerTypeName;
	private String routerImplName;

	@Override
	protected void runCommand(URI uri) {

		// Check if it is a connection
		// TODO: What should be selected? Possibly something other than a single connection
		final EObject eObj = getEObject(uri);
		if (!(eObj instanceof PortConnection)) {
			Dialog.showError("No connection is selected",
					"A connection between two components must be selected to add a router.");
			return;
		}

//		// Open wizard to enter filter info
//		AddRouterDialog wizard = new AddRouterDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
//		wizard.create();
//		if (wizard.open() == Window.OK) {
//			routerImplementationLanguage = wizard.getFilterImplementationLanguage();
////			routerRegularExpression = wizard.getFilterRegularExpression();
//			routerTypeName = wizard.getFilterTypeName();
//			routerImplName = wizard.getFilterImplName();
//			if (routerTypeName == "") {
//				routerTypeName = ROUTER_COMP_BASE_NAME;
//			}
//			if (routerImplName == "") {
//				routerImplName = ROUTER_IMPL_BASE_NAME;
//			}
//		} else {
//			return;
//		}

		// Insert the router component
//		insertRouterComponent(uri);

		return;

	}

	/**
	 * Inserts a router component into the model, including router type definition
	 * and implementation (including correct wiring from data source).  The router is inserted
	 * following the position of the selected data source
	 * @param uri - The URI of the selected data source
	 */
	public void insertRouterComponent(URI uri) {

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

				// Create Filter thread type
				final ThreadType routerThreadType = (ThreadType) pkgSection
						.createOwnedClassifier(Aadl2Package.eINSTANCE.getThreadType());
				// Give it a unique name
				routerThreadType.setName(getUniqueName(routerTypeName, true, pkgSection.getOwnedClassifiers()));

				// Create router event data ports
				// TODO: what if they are just data ports?
				final EventDataPort eventDataPortIn = routerThreadType.createOwnedEventDataPort();
				eventDataPortIn.setIn(true);
				eventDataPortIn.setName(ROUTER_PORT_IN_NAME);
				final EventDataPortImpl eventDataPortImpl = (EventDataPortImpl) selectedConnection.getDestination()
						.getConnectionEnd();
				eventDataPortIn.setDataFeatureClassifier(eventDataPortImpl.getDataFeatureClassifier());

				// TODO: Multiple out ports needed
				// TODO: What if they are just data ports?
				final EventDataPort eventDataPortOut = routerThreadType.createOwnedEventDataPort();
				eventDataPortOut.setOut(true);
				eventDataPortOut.setName(ROUTER_PORT_OUT_NAME);
				eventDataPortOut.setDataFeatureClassifier(eventDataPortImpl.getDataFeatureClassifier());

				// Add filter properties
				// CASE::COMP_TYPE Property
				if (!addPropertyAssociation("COMP_TYPE", "ROUTER", routerThreadType, casePropSet)) {
//					return;
				}
				// CASE::COMP_IMPL property
				if (!addPropertyAssociation("COMP_IMPL", routerImplementationLanguage, routerThreadType, casePropSet)) {
//					return;
				}
				// CASE::COMP_SPEC property
//				if (!addPropertyAssociation("COMP_SPEC", routerRegularExpression, routerThreadType, casePropSet)) {
////					return;
//				}

				// Move router to proper location
				// (just before component it connects to on communication pathway)
				final Context context = selectedConnection.getDestination().getContext();
				String destName = ((SubcomponentImpl) context).getSubcomponentType().getName();
				pkgSection.getOwnedClassifiers().move(getIndex(destName, pkgSection.getOwnedClassifiers()),
						pkgSection.getOwnedClassifiers().size() - 1);

				// Insert router feature in process component implementation
				final ProcessImplementationImpl procImpl = (ProcessImplementationImpl) selectedConnection
						.getContainingComponentImpl();
				final ThreadSubcomponent routerThreadSubComp = procImpl.createOwnedThreadSubcomponent();

				// Give it a unique name
				routerThreadSubComp.setName(getUniqueName(routerImplName, true, procImpl.getOwnedSubcomponents()));
				routerThreadSubComp.setThreadSubcomponentType(routerThreadType);

				// Put it in the right place
				destName = selectedConnection.getDestination().getContext().getName();
				procImpl.getOwnedThreadSubcomponents().move(getIndex(destName, procImpl.getOwnedThreadSubcomponents()),
						procImpl.getOwnedThreadSubcomponents().size() - 1);

				// Create connection from router to connection destination
				final PortConnection portConnOut = procImpl.createOwnedPortConnection();
				// Give it a unique name
				portConnOut
						.setName(getUniqueName(CONNECTION_IMPL_BASE_NAME, false, procImpl.getOwnedPortConnections()));
				portConnOut.setBidirectional(false);
				final ConnectedElement routerOutSrc = portConnOut.createSource();
				routerOutSrc.setContext(routerThreadSubComp);
				routerOutSrc.setConnectionEnd(eventDataPortOut);
				final ConnectedElement routerOutDst = portConnOut.createDestination();
				routerOutDst.setContext(selectedConnection.getDestination().getContext());
				routerOutDst.setConnectionEnd(selectedConnection.getDestination().getConnectionEnd());

				// Put portConnOut in right place (after portConnIn)
				destName = selectedConnection.getName();
				procImpl.getOwnedPortConnections().move(getIndex(destName, procImpl.getOwnedPortConnections()) + 1,
						procImpl.getOwnedPortConnections().size() - 1);

				// Rewire selected connection so the filter is the destination
				final PortConnection portConnIn = selectedConnection;
				portConnIn.getDestination().setContext(routerThreadSubComp);
				portConnIn.getDestination().setConnectionEnd(eventDataPortIn);

			}
		});

	}
}
