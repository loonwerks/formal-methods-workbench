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
import org.osate.aadl2.PublicPackageSection;
import org.osate.aadl2.ThreadSubcomponent;
import org.osate.aadl2.ThreadType;
import org.osate.aadl2.impl.AadlPackageImpl;
import org.osate.aadl2.impl.PortImpl;
import org.osate.aadl2.impl.ProcessImplementationImpl;
import org.osate.aadl2.impl.PropertySetImpl;
import org.osate.aadl2.impl.SubcomponentImpl;
import org.osate.ui.dialogs.Dialog;

import com.rockwellcollins.atc.darpacase.architecture.dialogs.AddFilterDialog;

public class AddFilter extends AadlHandler {

	static final String FILTER_COMP_BASE_NAME = "Filter";
	static final String FILTER_PORT_IN_NAME = "filter_in";
	static final String FILTER_PORT_OUT_NAME = "filter_out";
	static final String FILTER_IMPL_BASE_NAME = "FLT";
	static final String CONNECTION_IMPL_BASE_NAME = "c";

	private String filterImplementationLanguage;
	private String filterRegularExpression;
	private String filterTypeName;
	private String filterImplName;

	@Override
	protected void runCommand(URI uri) {

		// Check if it is a connection
		final EObject eObj = getEObject(uri);
		if (!(eObj instanceof PortConnection)) {
			Dialog.showError("No connection is selected",
					"A connection between two components must be selected to add a filter.");
			return;
		}

		// Open wizard to enter filter info
		AddFilterDialog wizard = new AddFilterDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		wizard.create();
		if (wizard.open() == Window.OK) {
			filterImplementationLanguage = wizard.getFilterImplementationLanguage();
			filterRegularExpression = wizard.getFilterRegularExpression();
			filterTypeName = wizard.getFilterTypeName();
			filterImplName = wizard.getFilterImplName();
			if (filterTypeName == "") {
				filterTypeName = FILTER_COMP_BASE_NAME;
			}
			if (filterImplName == "") {
				filterImplName = FILTER_IMPL_BASE_NAME;
			}
		}
		else {
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
				PropertySetImpl casePropSet = null;
				for (ModelUnit modelUnit : importedUnits) {
					if (modelUnit instanceof PropertySetImpl) {
						if (modelUnit.getName().equals(CASE_PROPSET_NAME)) {
							casePropSet = (PropertySetImpl) modelUnit;
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
				final ThreadType filterThreadType = (ThreadType) pkgSection
						.createOwnedClassifier(Aadl2Package.eINSTANCE.getThreadType());
				// Give it a unique name
				filterThreadType.setName(getUniqueName(filterTypeName, true, pkgSection.getOwnedClassifiers()));

				// Create filter ports
				final PortImpl portImpl = (PortImpl) selectedConnection.getDestination().getConnectionEnd();
				Port portIn = null;
				Port portOut = null;
				if (portImpl instanceof EventDataPort) {
					portIn = filterThreadType.createOwnedEventDataPort();
					((EventDataPort) portIn)
							.setDataFeatureClassifier(((EventDataPort) portImpl).getDataFeatureClassifier());
					portOut = filterThreadType.createOwnedEventDataPort();
					((EventDataPort) portOut)
							.setDataFeatureClassifier(((EventDataPort) portImpl).getDataFeatureClassifier());
				} else if (portImpl instanceof EventPort) {
					portIn = filterThreadType.createOwnedEventPort();
					portOut = filterThreadType.createOwnedEventPort();
				} else if (portImpl instanceof DataPort) {
					portIn = filterThreadType.createOwnedDataPort();
					((DataPort) portIn).setDataFeatureClassifier(((DataPort) portImpl).getDataFeatureClassifier());
					portOut = filterThreadType.createOwnedDataPort();
					((DataPort) portOut).setDataFeatureClassifier(((DataPort) portImpl).getDataFeatureClassifier());
				} else {
					Dialog.showError("Undetermined port type",
							"Could not determine the port type of the destination component.");
					return;
				}

				portIn.setIn(true);
				portIn.setName(FILTER_PORT_IN_NAME);
//				final EventDataPort eventDataPortIn = filterThreadType.createOwnedEventDataPort();
//				eventDataPortIn.setIn(true);
//				eventDataPortIn.setName(FILTER_PORT_IN_NAME);
//				final EventDataPortImpl eventDataPortImpl = (EventDataPortImpl) selectedConnection.getDestination()
//						.getConnectionEnd();
//				eventDataPortIn.setDataFeatureClassifier(eventDataPortImpl.getDataFeatureClassifier());

				portOut.setOut(true);
				portOut.setName(FILTER_PORT_OUT_NAME);
//				final EventDataPort eventDataPortOut = filterThreadType.createOwnedEventDataPort();
//				eventDataPortOut.setOut(true);
//				eventDataPortOut.setName(FILTER_PORT_OUT_NAME);
//				eventDataPortOut.setDataFeatureClassifier(eventDataPortImpl.getDataFeatureClassifier());

				// Add filter properties
				// CASE::COMP_TYPE Property
				if (!addPropertyAssociation("COMP_TYPE", "FILTER", filterThreadType, casePropSet)) {
//					return;
				}
				// CASE::COMP_IMPL property
				if (!addPropertyAssociation("COMP_IMPL", filterImplementationLanguage, filterThreadType, casePropSet)) {
//					return;
				}
				// CASE::COMP_SPEC property
				if (!addPropertyAssociation("COMP_SPEC", filterRegularExpression, filterThreadType, casePropSet)) {
//					return;
				}

				// Move filter to proper location
				// (just before component it connects to on communication pathway)
				final Context context = selectedConnection.getDestination().getContext();
				String destName = ((SubcomponentImpl) context).getSubcomponentType().getName();
				pkgSection.getOwnedClassifiers().move(getIndex(destName, pkgSection.getOwnedClassifiers()),
						pkgSection.getOwnedClassifiers().size() - 1);

				// Insert filter feature in process component implementation
				final ProcessImplementationImpl procImpl = (ProcessImplementationImpl) selectedConnection
						.getContainingComponentImpl();
				final ThreadSubcomponent filterThreadSubComp = procImpl.createOwnedThreadSubcomponent();

				// Give it a unique name
				filterThreadSubComp
						.setName(getUniqueName(filterImplName, true, procImpl.getOwnedSubcomponents()));
				filterThreadSubComp.setThreadSubcomponentType(filterThreadType);

				// Put it in the right place
				destName = selectedConnection.getDestination().getContext().getName();
				procImpl.getOwnedThreadSubcomponents().move(getIndex(destName, procImpl.getOwnedThreadSubcomponents()),
						procImpl.getOwnedThreadSubcomponents().size() - 1);

				// Create connection from filter to connection destination
				final PortConnection portConnOut = procImpl.createOwnedPortConnection();
				// Give it a unique name
				portConnOut
						.setName(getUniqueName(CONNECTION_IMPL_BASE_NAME, false, procImpl.getOwnedPortConnections()));
				portConnOut.setBidirectional(false);
				final ConnectedElement filterOutSrc = portConnOut.createSource();
				filterOutSrc.setContext(filterThreadSubComp);
//				filterOutSrc.setConnectionEnd(eventDataPortOut);
				filterOutSrc.setConnectionEnd(portOut);
				final ConnectedElement filterOutDst = portConnOut.createDestination();
				filterOutDst.setContext(selectedConnection.getDestination().getContext());
				filterOutDst.setConnectionEnd(selectedConnection.getDestination().getConnectionEnd());

				// Put portConnOut in right place (after portConnIn)
				destName = selectedConnection.getName();
				procImpl.getOwnedPortConnections().move(getIndex(destName, procImpl.getOwnedPortConnections()) + 1,
						procImpl.getOwnedPortConnections().size() - 1);

				// Rewire selected connection so the filter is the destination
				final PortConnection portConnIn = selectedConnection;
				portConnIn.getDestination().setContext(filterThreadSubComp);
//				portConnIn.getDestination().setConnectionEnd(eventDataPortIn);
				portConnIn.getDestination().setConnectionEnd(portIn);

			}
		});

	}

}
