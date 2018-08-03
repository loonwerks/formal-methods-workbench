package com.rockwellcollins.atc.darpacase.architecture.handlers;

import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.xtext.resource.EObjectAtOffsetHelper;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.outline.impl.EObjectNode;
import org.eclipse.xtext.ui.editor.utils.EditorUtils;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;
import org.osate.aadl2.Aadl2Package;
import org.osate.aadl2.ConnectedElement;
import org.osate.aadl2.Context;
import org.osate.aadl2.EventDataPort;
import org.osate.aadl2.ModelUnit;
import org.osate.aadl2.NamedElement;
import org.osate.aadl2.PackageSection;
import org.osate.aadl2.PortConnection;
import org.osate.aadl2.PrivatePackageSection;
import org.osate.aadl2.Property;
import org.osate.aadl2.PublicPackageSection;
import org.osate.aadl2.ThreadSubcomponent;
import org.osate.aadl2.ThreadType;
import org.osate.aadl2.impl.AadlPackageImpl;
import org.osate.aadl2.impl.AadlStringImpl;
import org.osate.aadl2.impl.EnumerationLiteralImpl;
import org.osate.aadl2.impl.EnumerationTypeImpl;
import org.osate.aadl2.impl.EventDataPortImpl;
import org.osate.aadl2.impl.ModalPropertyValueImpl;
import org.osate.aadl2.impl.NamedValueImpl;
import org.osate.aadl2.impl.ProcessImplementationImpl;
import org.osate.aadl2.impl.PropertyAssociationImpl;
import org.osate.aadl2.impl.PropertyImpl;
import org.osate.aadl2.impl.PropertySetImpl;
import org.osate.aadl2.impl.StringLiteralImpl;
import org.osate.aadl2.impl.SubcomponentImpl;
import org.osate.pluginsupport.PluginSupportUtil;
import org.osate.ui.dialogs.Dialog;

import com.rockwellcollins.atc.darpacase.architecture.dialogs.AddFilterDialog;

public class AddFilter extends AbstractHandler {

	static final String CASE_PROP_NAME = "CASE";
	static final String CASE_PROP_FILE = "CASE.aadl";
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
	public Object execute(ExecutionEvent event) {

		// Get the current selection
		// TODO: Handle same functionality in the Graphical Editor?
		URI uri = getSelectionURI(HandlerUtil.getCurrentSelection(event));
		if (uri == null) {
			return null;
		}

		// Check if it is a connection
		final EObject eObj = getEObject(uri);
		if (!(eObj instanceof PortConnection)) {
			Dialog.showError("No connection is selected",
					"A connection between two components must be selected to add a filter.");
			return null;
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
			return null;
		}

		// Insert the filter component
		insertFilterComponent(uri);

		return null;

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
						if (modelUnit.getName().equalsIgnoreCase(CASE_PROP_NAME)) {
							casePropSet = (PropertySetImpl) modelUnit;
							break;
						}
					}
				}

				if (casePropSet == null) {
					// Try importing the resource
					casePropSet = getPropertySet(CASE_PROP_NAME, CASE_PROP_FILE, resource.getResourceSet());
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

				// Create filter event data ports
				final EventDataPort eventDataPortIn = filterThreadType.createOwnedEventDataPort();
				eventDataPortIn.setIn(true);
				eventDataPortIn.setName(FILTER_PORT_IN_NAME);
				final EventDataPortImpl eventDataPortImpl = (EventDataPortImpl) selectedConnection.getDestination()
						.getConnectionEnd();
				eventDataPortIn.setDataFeatureClassifier(eventDataPortImpl.getDataFeatureClassifier());

				final EventDataPort eventDataPortOut = filterThreadType.createOwnedEventDataPort();
				eventDataPortOut.setOut(true);
				eventDataPortOut.setName(FILTER_PORT_OUT_NAME);
				eventDataPortOut.setDataFeatureClassifier(eventDataPortImpl.getDataFeatureClassifier());

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
				filterOutSrc.setConnectionEnd(eventDataPortOut);
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
				portConnIn.getDestination().setConnectionEnd(eventDataPortIn);

			}
		});

	}


	/**
	 * Gets the property set from either the current resource, or
	 * the specified file, provided as an OSATE plugin.
	 * @param resourceSet - The ResourceSet that contains all open resources
	 * @return PropertySetImpl
	 */
	private PropertySetImpl getPropertySet(String propSetName, String propSetFile, ResourceSet resourceSet)
			throws Exception {

		PropertySetImpl propSet = null;

		// Check to see if the property set file resource has already been loaded
		// but not imported into this model
		for (Resource r : resourceSet.getResources()) {
			final EObject eObj = r.getContents().get(0);
			if (eObj instanceof PropertySetImpl) {
				PropertySetImpl propSetImpl = (PropertySetImpl) eObj;
				if (propSetImpl.getName().equalsIgnoreCase(propSetName)) {
					propSet = propSetImpl;
					break;
				}
			}
		}

		// If the logical resource has not been loaded, create it
		if (propSet == null) {

			// Find the Property Set File
			// The file is provided as an OSATE Plugin_Contribution,
			// so retrieve its URI, which has already been created at launch
			final List<URI> contributedAadl = PluginSupportUtil.getContributedAadl();
			URI uri = null;
			for (URI u : contributedAadl) {
				if (u.lastSegment().equalsIgnoreCase(CASE_PROP_FILE)) {
					uri = u;
					break;
				}
			}
			if (uri == null) {
				Dialog.showError(propSetName + " Properties", "Could not find the " + propSetFile + " property file.");
				return null;
			}
			// Create a resource for the property set file URI
			Resource propResource = resourceSet.createResource(uri);
			propResource.load(null);
			// Grab the PropertySet specified in the CASE Prop file
			propSet = (PropertySetImpl) propResource.getContents().get(0);
		}

		return propSet;
	}

	/**
	 * Adds a name/value property association to the provided thread component.
	 * The property association must have a type that is defined in the specified property set.
	 * @param propName - Property name
	 * @param propVal - Property value
	 * @param threadType - Thread that will contain the property association
	 * @param casePropSet - PropertySet that defines the property
	 * @return A boolean indicating success
	 */
	private boolean addPropertyAssociation(String propName, String propVal, ThreadType threadType,
			PropertySetImpl casePropSet) {

		final PropertyAssociationImpl propAssoc = (PropertyAssociationImpl) threadType.createOwnedPropertyAssociation();

		PropertyImpl prop = null;
		for (Property p : casePropSet.getOwnedProperties()) {
			if (p.getName().equalsIgnoreCase(propName)) {
				prop = (PropertyImpl) p;
				break;
			}
		}

		// Make sure the property type was found
		if (prop != null) {
			propAssoc.setProperty(prop);
		} else {
			Dialog.showError(CASE_PROP_NAME + " Properties",
					propName + " property not found in " + CASE_PROP_NAME + " property set.");
			return false;
		}

		// Add property value
		final ModalPropertyValueImpl modalPropVal = (ModalPropertyValueImpl) propAssoc.createOwnedValue();

		// Figure out what type the property value is
		if (prop.getOwnedPropertyType() instanceof EnumerationTypeImpl) {
			final NamedValueImpl namedVal = (NamedValueImpl) modalPropVal
					.createOwnedValue(Aadl2Package.eINSTANCE.getNamedValue());
			final EnumerationLiteralImpl enumLiteralCompType = (EnumerationLiteralImpl) EcoreUtil
					.create(Aadl2Package.eINSTANCE.getEnumerationLiteral());
			enumLiteralCompType.setName(propVal);
			namedVal.setNamedValue(enumLiteralCompType);
		} else if (prop.getOwnedPropertyType() instanceof AadlStringImpl) {
			final StringLiteralImpl stringVal = (StringLiteralImpl) modalPropVal
					.createOwnedValue(Aadl2Package.eINSTANCE.getStringLiteral());
			stringVal.setValue(propVal);
		} else {
			// Couldn't figure it out
			Dialog.showError(CASE_PROP_NAME + " Properties", "Could not determine property type of " + propName + ".");
			return false;
		}

		return true;
	}


	/**
	 * Returns the component EObject corresponding to its URI.
	 * @param uri - Component URI
	 * @return EObject
	 */
	private EObject getEObject(URI uri) {
		XtextEditor xtextEditor = EditorUtils.getActiveXtextEditor();
		return xtextEditor.getDocument().readOnly(resource -> {
			return resource.getResourceSet().getEObject(uri, true);
		});
	}

	/**
	 * Gets the URI for the current selection in the editor
	 * @param currentSelection - Selected model object
	 * @return A URI representing the selected model object
	 */
//	@SuppressWarnings("restriction")
	private URI getSelectionURI(ISelection currentSelection) {

		if (currentSelection instanceof IStructuredSelection) {
			final IStructuredSelection iss = (IStructuredSelection) currentSelection;
			if (iss.size() == 1) {
				final Object obj = iss.getFirstElement();
//				if (obj instanceof DiagramElement) {
//					final DiagramElement diagramElement = (DiagramElement) obj;
//					EObject eObj = (EObject) diagramElement.getBusinessObject();
//					return EcoreUtil.getURI(eObj);
//				} else {
				return ((EObjectNode) obj).getEObjectURI();
//				}
			}
		} else if (currentSelection instanceof TextSelection) {
			// Selection may be stale, get latest from editor
			XtextEditor xtextEditor = EditorUtils.getActiveXtextEditor();
			TextSelection ts = (TextSelection) xtextEditor.getSelectionProvider().getSelection();
			return xtextEditor.getDocument().readOnly(resource -> {
				EObject e = new EObjectAtOffsetHelper().resolveContainedElementAt(resource, ts.getOffset());
				return EcoreUtil.getURI(e);
			});
		}
		return null;
	}

	/**
	 * Builds an identifier using the specified base name that doesn't conflict with identifiers in the specified element list.
	 * @param baseIdentifier - Name
	 * @param startWithBase - If true, the baseIdentifier is the only one of its kind,
	 * it will be returned as 'baseIdentifier' rather than 'baseIdentifier1'
	 * @param elements - Collection of names which cannot match base name
	 * @return An identifier that is unique in the specified list
	 */
	private String getUniqueName(String baseIdentifier, boolean startWithBase,
			final Collection<? extends NamedElement> elements) {

		// Sort names list alphabetically
		TreeSet<String> names = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
		elements.forEach(n -> {
			if (n.getName() != null) {
				names.add(n.getName());
			}
		});

		// Resolve naming conflicts
		String newIdentifier = baseIdentifier + (startWithBase ? "" : "1");
		boolean done = false;
		int num = (startWithBase ? 0 : 1);
		do {
			if (names.contains(newIdentifier)) {
				num++;
				newIdentifier = baseIdentifier + num;
			} else {
				done = true;
			}
		} while (!done);

		return newIdentifier;
	}

	/**
	 * Returns the index of a component with the specified name in the specified element list.
	 * @param compName - Component name
	 * @param elements - Collection of elements
	 * @return An identifier that is unique in the specified list
	 */
	private int getIndex(String compName, final Collection<? extends NamedElement> elements) {
		int idx = 0;

		for (NamedElement e : elements) {
			if (e.getName().equalsIgnoreCase(compName)) {
				break;
			} else {
				idx++;
			}
		}
		return idx;
	}

}
