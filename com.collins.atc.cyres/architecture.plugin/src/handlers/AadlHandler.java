package com.collins.atc.ace.cyres.architecture.handlers;

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
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.xtext.resource.EObjectAtOffsetHelper;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.outline.impl.EObjectNode;
import org.eclipse.xtext.ui.editor.utils.EditorUtils;
import org.osate.aadl2.Aadl2Factory;
import org.osate.aadl2.Aadl2Package;
import org.osate.aadl2.AadlBoolean;
import org.osate.aadl2.AadlInteger;
import org.osate.aadl2.AadlPackage;
import org.osate.aadl2.AadlString;
import org.osate.aadl2.BooleanLiteral;
import org.osate.aadl2.ComponentType;
import org.osate.aadl2.EnumerationLiteral;
import org.osate.aadl2.EnumerationType;
import org.osate.aadl2.IntegerLiteral;
import org.osate.aadl2.ListType;
import org.osate.aadl2.ListValue;
import org.osate.aadl2.ModalPropertyValue;
import org.osate.aadl2.NamedElement;
import org.osate.aadl2.NamedValue;
import org.osate.aadl2.Property;
import org.osate.aadl2.PropertyAssociation;
import org.osate.aadl2.PropertySet;
import org.osate.aadl2.PropertyType;
import org.osate.aadl2.Realization;
import org.osate.aadl2.StringLiteral;
import org.osate.aadl2.impl.PropertySetImpl;
import org.osate.pluginsupport.PluginSupportUtil;
import org.osate.ui.dialogs.Dialog;

public abstract class AadlHandler extends AbstractHandler {

	abstract protected void runCommand(URI uri);

	static final String CASE_PROPSET_NAME = "CASE_Properties";
	static final String CASE_PROPSET_FILE = "CASE_Properties.aadl";
	static final String CASE_MODEL_TRANSFORMATIONS_NAME = "CASE_Model_Transformations";
	static final String CASE_MODEL_TRANSFORMATIONS_FILE = "CASE_Model_Transformations.aadl";
	protected ExecutionEvent executionEvent;

	@Override
	public Object execute(ExecutionEvent event) {

		this.executionEvent = event;

		// Get the current selection
		// TODO: Handle same functionality in the Graphical Editor?
		URI uri = getSelectionURI(HandlerUtil.getCurrentSelection(event));
		if (uri == null) {
			return null;
		}

		// Run the command in the handler
		runCommand(uri);

		return null;
	}

	/**
	 * Returns the component EObject corresponding to its URI.
	 * @param uri - Component URI
	 * @return EObject
	 */
	protected EObject getEObject(URI uri) {
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
				if (e instanceof Realization) {
					e = e.eContainer();
				}
				return EcoreUtil.getURI(e);
			});
		}
		return null;
	}

	/**
	 * Gets the property set from either the current resource, or
	 * the specified file, provided as an OSATE plugin.
	 * @param propSetName - The name of the property set
	 * @param propSetFile - The file name containing the property set
	 * @param resourceSet - The ResourceSet that contains all open resources
	 * @return PropertySet
	 */
	protected PropertySet getPropertySet(String propSetName, String propSetFile, ResourceSet resourceSet)
			throws Exception {

		PropertySet propSet = null;

		// Check to see if the property set file resource has already been loaded
		// but not imported into this model
		for (Resource r : resourceSet.getResources()) {
			final EObject eObj = r.getContents().get(0);
			if (eObj instanceof PropertySetImpl) {
				PropertySet propSetImpl = (PropertySet) eObj;
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
				if (u.lastSegment().equalsIgnoreCase(propSetFile)) {
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
			propSet = (PropertySet) propResource.getContents().get(0);
		}

		return propSet;
	}

	/**
	 * Gets the AADL Package from either the current resource, or
	 * the specified file, provided as an OSATE plugin.
	 * @param packageName - The name of the AADL package
	 * @param packageFile - The name of the file containing the AADL package
	 * @param resourceSet - The ResourceSet that contains all open resources
	 * @return AadlPackage
	 */
	protected AadlPackage getAadlPackage(String packageName, String packageFile, ResourceSet resourceSet)
			throws Exception {

		AadlPackage aadlPackage = null;

		// Check to see if the package file resource has already been loaded
		// but not imported into this model
		for (Resource r : resourceSet.getResources()) {
			final EObject eObj = r.getContents().get(0);
			if (eObj instanceof AadlPackage) {
				AadlPackage tmpPkg = (AadlPackage) eObj;
				if (tmpPkg.getName().equalsIgnoreCase(packageName)) {
					aadlPackage = tmpPkg;
					break;
				}
			}
		}

		// If the logical resource has not been loaded, create it
		if (aadlPackage == null) {

			// Find the Property Set File
			// The file is provided as an OSATE Plugin_Contribution,
			// so retrieve its URI, which has already been created at launch
			final List<URI> contributedAadl = PluginSupportUtil.getContributedAadl();
			URI uri = null;
			for (URI u : contributedAadl) {
				if (u.lastSegment().equalsIgnoreCase(packageFile)) {
					uri = u;
					break;
				}
			}
			if (uri == null) {
				Dialog.showError(packageName + " Package", "Could not find the " + packageFile + " file.");
				return null;
			}
			// Create a resource for the property set file URI
			Resource packageResource = resourceSet.createResource(uri);
			packageResource.load(null);
			// Grab the PropertySet specified in the CASE Prop file
			aadlPackage = (AadlPackage) packageResource.getContents().get(0);
		}

		return aadlPackage;
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
	protected boolean addPropertyAssociation(String propName, String propVal, ComponentType componentType,
			PropertySet propSet) {

		PropertyAssociation propAssocImpl = null;
		Property prop = null;

		// Check if the property is already present in the component.
		// If so, we don't need to create a new property association, just overwrite the existing one
		EList<PropertyAssociation> propAssociations = componentType.getOwnedPropertyAssociations();
		for (PropertyAssociation propAssoc : propAssociations) {
			if (propAssoc.getProperty().getName().equalsIgnoreCase(propName)) {
				propAssocImpl = propAssoc;
				break;
			}
		}

		if (propAssocImpl == null) {

			// Property is not already present in the component. Need to create a new property association
			propAssocImpl = componentType.createOwnedPropertyAssociation();

			// Find the property in the specified property set
			for (Property p : propSet.getOwnedProperties()) {
				if (p.getName().equalsIgnoreCase(propName)) {
					prop = p;
					break;
				}
			}

			// Make sure the property type was found
			if (prop != null) {
				propAssocImpl.setProperty(prop);
			} else {
				Dialog.showError(propSet.getName() + " Properties",
						propName + " property not found in " + propSet.getName() + " property set.");
				return false;
			}

		}
		else {
			// Property is already present in the component.
			prop = propAssocImpl.getProperty();
			// Clear the current value. We write the new value below.
			propAssocImpl.getOwnedValues().clear();
		}

		// Add property value
		final ModalPropertyValue modalPropVal = propAssocImpl.createOwnedValue();

		// Figure out what type the property value is
		if (prop.getOwnedPropertyType() instanceof EnumerationType) {
			final NamedValue namedVal = (NamedValue) modalPropVal
					.createOwnedValue(Aadl2Package.eINSTANCE.getNamedValue());
			final EnumerationLiteral enumLiteralCompType = (EnumerationLiteral) EcoreUtil
					.create(Aadl2Package.eINSTANCE.getEnumerationLiteral());
			enumLiteralCompType.setName(propVal);
			namedVal.setNamedValue(enumLiteralCompType);
		} else if (prop.getOwnedPropertyType() instanceof AadlBoolean) {
			final BooleanLiteral boolVal = (BooleanLiteral) modalPropVal
					.createOwnedValue(Aadl2Package.eINSTANCE.getBooleanLiteral());
			boolVal.setValue(Boolean.parseBoolean(propVal));
		} else if (prop.getOwnedPropertyType() instanceof AadlInteger) {
			final IntegerLiteral intVal = (IntegerLiteral) modalPropVal
					.createOwnedValue(Aadl2Package.eINSTANCE.getIntegerLiteral());
			try {
				intVal.setValue(Long.parseLong(propVal));
			} catch (NumberFormatException e) {
				Dialog.showError(CASE_PROPSET_NAME + " Properties", "Value for " + propName + " must be a number.");
				return false;
			}
		} else if (prop.getOwnedPropertyType() instanceof AadlString) {
			final StringLiteral stringVal = (StringLiteral) modalPropVal
					.createOwnedValue(Aadl2Package.eINSTANCE.getStringLiteral());
			stringVal.setValue(propVal);
		} else if (prop.getOwnedPropertyType() instanceof ListType) {
			final ListValue listVal = (ListValue) modalPropVal
					.createOwnedValue(Aadl2Package.eINSTANCE.getListValue());

			String[] elements = propVal.split(",");

			// TODO: This property could be a list of enums, strings, numbers, lists, etc
			// TODO: This really needs to be set up in a recursive manner
			// Figure out which
			ListType listType = (ListType) prop.getOwnedPropertyType();
			PropertyType listSubType = listType.getOwnedElementType();
			if (listSubType instanceof AadlString) {
				for (String element : elements) {
					StringLiteral stringVal = (StringLiteral) listVal
							.createOwnedListElement(Aadl2Package.eINSTANCE.getStringLiteral());
					stringVal.setValue(element);
				}
			} else if (listSubType instanceof AadlInteger) {

			} else if (listSubType instanceof AadlBoolean) {

			} else if (listSubType instanceof EnumerationType) {
				for (String element : elements) {
					NamedValue namedVal = (NamedValue) listVal
							.createOwnedListElement(Aadl2Package.eINSTANCE.getNamedValue());
					EnumerationLiteral enumLiteral = Aadl2Factory.eINSTANCE.createEnumerationLiteral();
					enumLiteral.setName(element);
					namedVal.setNamedValue(enumLiteral);
				}
			} else {

			}


		} else {
			// TODO: Add other property types
			// Couldn't figure it out
			Dialog.showError(CASE_PROPSET_NAME + " Properties",
					"Could not determine property type of " + propName + ".");
			return false;
		}

		return true;
	}

	/**
	 * Returns the index of a component with the specified name in the specified element list.
	 * TODO: What if the name isn't in the list?
	 * @param compName - Component name
	 * @param elements - Collection of elements
	 * @return An identifier that is unique in the specified list
	 */
	protected int getIndex(String compName, final Collection<? extends NamedElement> elements) {
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

	/**
	 * Builds an identifier using the specified base name that doesn't conflict with identifiers in the specified element list.
	 * @param baseIdentifier - Name
	 * @param startWithBase - If true, if the baseIdentifier is the only one of its kind,
	 * it will be returned as 'baseIdentifier' rather than 'baseIdentifier1'
	 * @param elements - Collection of names which cannot match base name
	 * @return An identifier that is unique in the specified list
	 */
	protected String getUniqueName(String baseIdentifier, boolean startWithBase,
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
		int num = getLastInt(baseIdentifier);
		if (num > 0) {
			// If the baseIdentifier already has a number at the end, start with it
			baseIdentifier = baseIdentifier.substring(0, baseIdentifier.length() - Integer.toString(num).length());
		} else if (num == 0 && !startWithBase) {
			num = 1;
		}
//		int num = (startWithBase ? 0 : 1);

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
	 * Helper function for getUniqueName() for extracting a number at the end of a string
	 * @param name - String
	 * @return An integer
	 */
	private int getLastInt(String name) {

		int offset = name.length();
		for (int i = name.length() - 1; i >= 0; i--) {
			char c = name.charAt(i);
			if (Character.isDigit(c)) {
				offset--;
			} else {
				if (offset == name.length()) {
					// No int at the end
					return 0;
				}
				return Integer.parseInt(name.substring(offset));
			}
		}
		return Integer.parseInt(name.substring(offset));
	}

}
