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
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.xtext.resource.EObjectAtOffsetHelper;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.outline.impl.EObjectNode;
import org.eclipse.xtext.ui.editor.utils.EditorUtils;
import org.osate.aadl2.Aadl2Package;
import org.osate.aadl2.ComponentType;
import org.osate.aadl2.NamedElement;
import org.osate.aadl2.Property;
import org.osate.aadl2.PropertyAssociation;
import org.osate.aadl2.impl.AadlStringImpl;
import org.osate.aadl2.impl.EnumerationLiteralImpl;
import org.osate.aadl2.impl.EnumerationTypeImpl;
import org.osate.aadl2.impl.ListTypeImpl;
import org.osate.aadl2.impl.ListValueImpl;
import org.osate.aadl2.impl.ModalPropertyValueImpl;
import org.osate.aadl2.impl.NamedValueImpl;
import org.osate.aadl2.impl.PropertyAssociationImpl;
import org.osate.aadl2.impl.PropertyImpl;
import org.osate.aadl2.impl.PropertySetImpl;
import org.osate.aadl2.impl.StringLiteralImpl;
import org.osate.pluginsupport.PluginSupportUtil;
import org.osate.ui.dialogs.Dialog;

public abstract class AadlHandler extends AbstractHandler {

	abstract protected void runCommand(URI uri);

	static final String CASE_PROPSET_NAME = "CASE";
	static final String CASE_PROPSET_FILE = "CASE.aadl";
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
				return EcoreUtil.getURI(e);
			});
		}
		return null;
	}

	/**
	 * Gets the property set from either the current resource, or
	 * the specified file, provided as an OSATE plugin.
	 * @param resourceSet - The ResourceSet that contains all open resources
	 * @return PropertySetImpl
	 */
	protected PropertySetImpl getPropertySet(String propSetName, String propSetFile, ResourceSet resourceSet)
			throws Exception {

		PropertySetImpl propSet = null;

		// Check to see if the property set file resource has already been loaded
		// but not imported into this model
		for (Resource r : resourceSet.getResources()) {
			final EObject eObj = r.getContents().get(0);
			if (eObj instanceof PropertySetImpl) {
				PropertySetImpl propSetImpl = (PropertySetImpl) eObj;
				if (propSetImpl.getName().equals(propSetName)) {
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
				if (u.lastSegment().equals(propSetFile)) {
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
	protected boolean addPropertyAssociation(String propName, String propVal, ComponentType componentType,
			PropertySetImpl propSet) {

		PropertyAssociationImpl propAssocImpl = null;
		PropertyImpl prop = null;

		// Check if the property is already present in the component.
		// If so, we don't need to create a new property association, just overwrite the existing one
		EList<PropertyAssociation> propAssociations = componentType.getOwnedPropertyAssociations();
		for (PropertyAssociation propAssoc : propAssociations) {
			if (propAssoc.getProperty().getName().equals(propName)) {
				propAssocImpl = (PropertyAssociationImpl) propAssoc;
				break;
			}
		}

		if (propAssocImpl == null) {

			// Property is not already present in the component. Need to create a new property association
			propAssocImpl = (PropertyAssociationImpl) componentType.createOwnedPropertyAssociation();

			// Find the property in the specified property set
			for (Property p : propSet.getOwnedProperties()) {
				if (p.getName().equals(propName)) {
					prop = (PropertyImpl) p;
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
			prop = (PropertyImpl) propAssocImpl.getProperty();
			// Clear the current value. We write the new value below.
			propAssocImpl.getOwnedValues().clear();
		}

		// Add property value
		final ModalPropertyValueImpl modalPropVal = (ModalPropertyValueImpl) propAssocImpl.createOwnedValue();

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
		} else if (prop.getOwnedPropertyType() instanceof ListTypeImpl) {
			final ListValueImpl listVal = (ListValueImpl) modalPropVal
					.createOwnedValue(Aadl2Package.eINSTANCE.getListValue());
			String[] elements = propVal.split(",");
			for (String element : elements) {
				StringLiteralImpl stringVal = (StringLiteralImpl) listVal
						.createOwnedListElement(Aadl2Package.eINSTANCE.getStringLiteral());
				stringVal.setValue(element);
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
	 * @param startWithBase - If true, the baseIdentifier is the only one of its kind,
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

}
