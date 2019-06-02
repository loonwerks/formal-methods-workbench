package com.collins.fmw.cyres.architecture.utils;

import java.io.IOException;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.osate.aadl2.Aadl2Factory;
import org.osate.aadl2.Aadl2Package;
import org.osate.aadl2.AadlBoolean;
import org.osate.aadl2.AadlInteger;
import org.osate.aadl2.AadlPackage;
import org.osate.aadl2.AadlString;
import org.osate.aadl2.BooleanLiteral;
import org.osate.aadl2.Classifier;
import org.osate.aadl2.EnumerationLiteral;
import org.osate.aadl2.EnumerationType;
import org.osate.aadl2.IntegerLiteral;
import org.osate.aadl2.ListType;
import org.osate.aadl2.ListValue;
import org.osate.aadl2.ModalPropertyValue;
import org.osate.aadl2.ModelUnit;
import org.osate.aadl2.NamedValue;
import org.osate.aadl2.PackageSection;
import org.osate.aadl2.Property;
import org.osate.aadl2.PropertyAssociation;
import org.osate.aadl2.PropertySet;
import org.osate.aadl2.PropertyType;
import org.osate.aadl2.StringLiteral;
import org.osate.aadl2.impl.PropertySetImpl;
import org.osate.aadl2.modelsupport.resources.OsateResourceUtil;
import org.osate.pluginsupport.PluginSupportUtil;
import org.osate.ui.dialogs.Dialog;

public class CaseUtils {

	public static final String CASE_PROPSET_NAME = "CASE_Properties";
	public static final String CASE_PROPSET_FILE = "CASE_Properties.aadl";
	public static final String CASE_MODEL_TRANSFORMATIONS_NAME = "CASE_Model_Transformations";
	public static final String CASE_MODEL_TRANSFORMATIONS_FILE = "CASE_Model_Transformations.aadl";

	/**
	 * Adds the CASE_Properties file to the list of imported model units via the 'with' statement
	 * to the specified package section
	 * @param pkgSection - The package section (public or private) to add the imported file to
	 * @return the property set
	 */
	public static boolean addCasePropertyImport(PackageSection pkgSection) throws Exception {

		// First check if CASE Property file has already been imported in the model
		PropertySet casePropSet = null;
		for (ModelUnit modelUnit : pkgSection.getImportedUnits()) {
			if (modelUnit instanceof PropertySet) {
				if (modelUnit.getName().equalsIgnoreCase(CASE_PROPSET_NAME)) {
					casePropSet = (PropertySet) modelUnit;
					break;
				}
			}
		}

		if (casePropSet == null) {
			// Try importing the resource
			casePropSet = getPropertySet(CASE_PROPSET_NAME, CASE_PROPSET_FILE);
			if (casePropSet == null) {
				Dialog.showError("Could not import " + CASE_PROPSET_NAME,
						"Property set " + CASE_PROPSET_NAME + " could not be found.");
				return false;
			}
			// Add as "importedUnit" to package section
			pkgSection.getImportedUnits().add(casePropSet);
		}

		return true;
	}

	/**
	 * Adds the CASE_Model_Transformations file to the list of imported model units via the 'with' statement
	 * to the specified package section
	 * @param pkgSection - The package section (public or private) to add the imported file to
	 * @param addRenameAll - If true, will add a rename::all for the package
	 * @return CASE_Model_Transformations package
	 */
	public static boolean addCaseModelTransformationsImport(PackageSection pkgSection, boolean addRenameAll) {

		// First check if CASE_Model_Transformations file has already been imported in the model
		AadlPackage casePackage = null;
		for (ModelUnit modelUnit : pkgSection.getImportedUnits()) {
			if (modelUnit instanceof AadlPackage) {
				if (modelUnit.getName().equalsIgnoreCase(CASE_MODEL_TRANSFORMATIONS_NAME)) {
					casePackage = (AadlPackage) modelUnit;
					break;
				}
			}
		}

		if (casePackage == null) {
			// Try importing the resource
			casePackage = getAadlPackage(CASE_MODEL_TRANSFORMATIONS_NAME, CASE_MODEL_TRANSFORMATIONS_FILE);
			if (casePackage == null) {
				Dialog.showError("Could not import " + CASE_MODEL_TRANSFORMATIONS_NAME,
						"Package " + CASE_MODEL_TRANSFORMATIONS_NAME + " could not be found.");
				return false;
			}
			// Add as "importedUnit" to package section
			pkgSection.getImportedUnits().add(casePackage);
		}

//		if (addRenameAll) {
//			// Check if the rename already exists
//			PackageRename pkgRename = null;
//			for (PackageRename pr : pkgSection.getOwnedPackageRenames()) {
//				if (pr.getRenamedPackage().getName().equalsIgnoreCase(CASE_MODEL_TRANSFORMATIONS_NAME)) {
//					pkgRename = pr;
//					break;
//				}
//			}
//
//			if (pkgRename == null) {
//				// Add the rename
//				pkgRename = pkgSection.createOwnedPackageRename();
////				pkgRename = Aadl2Factory.eINSTANCE.createPackageRename();
//				pkgRename.setRenameAll(true);
//				pkgRename.setRenamedPackage(casePackage);
////				pkgSection.getOwnedPackageRenames().add(pkgRename);
//			}
//
//		}

		return true;
	}

	/**
	 * Gets the CASE property set from either the current resource, or
	 * the specified file, provided as an OSATE plugin.
	 * @return PropertySet
	 */
	public static PropertySet getCasePropertySet() {
		return getPropertySet(CASE_PROPSET_NAME, CASE_PROPSET_FILE);
	}

	/**
	 * Gets the property set from either the current resource, or
	 * the specified file, provided as an OSATE plugin.
	 * @param propSetName - The name of the property set
	 * @param propSetFile - The file name containing the property set
	 * @return PropertySet
	 */
	public static PropertySet getPropertySet(String propSetName, String propSetFile) {

		XtextResourceSet resourceSet = OsateResourceUtil.getResourceSet();

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
			try {
				Resource propResource = resourceSet.createResource(uri);
				propResource.load(null);
				// Grab the PropertySet specified in the CASE Prop file
				propSet = (PropertySet) propResource.getContents().get(0);
			} catch (IOException e) {
				Dialog.showError(propSetName + " Properties", "Could not load the " + propSetFile + " property file.");
				return null;
			}
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
	public static boolean addCasePropertyAssociation(String propName, String propVal, Classifier classifier) {
		return addPropertyAssociation(propName, propVal, classifier, getCasePropertySet());
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
	public static boolean addPropertyAssociation(String propName, String propVal, Classifier classifier,
			PropertySet propSet) {

		PropertyAssociation propAssocImpl = null;
		Property prop = null;

		// Check if the property is already present in the component.
		// If so, we don't need to create a new property association, just overwrite the existing one
		for (PropertyAssociation propAssoc : classifier.getOwnedPropertyAssociations()) {
			if (propAssoc.getProperty().getName().equalsIgnoreCase(propName)) {
				propAssocImpl = propAssoc;
				break;
			}
		}

		if (propAssocImpl == null) {

			// Property is not already present in the component. Need to create a new property association
			propAssocImpl = classifier.createOwnedPropertyAssociation();

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

		} else {
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
			final ListValue listVal = (ListValue) modalPropVal.createOwnedValue(Aadl2Package.eINSTANCE.getListValue());

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
	 * Gets the CASE Model Transformations Package from either the current resource, or
	 * the specified file, provided as an OSATE plugin.
	 * @param resourceSet - The ResourceSet that contains all open resources
	 * @return AadlPackage
	 */
	public static AadlPackage getCaseModelTransformationsPackage() {
		return getAadlPackage(CASE_MODEL_TRANSFORMATIONS_NAME, CASE_MODEL_TRANSFORMATIONS_FILE);
	}

	/**
	 * Gets the AADL Package from either the current resource, or
	 * the specified file, provided as an OSATE plugin.
	 * @param packageName - The name of the AADL package
	 * @param packageFile - The name of the file containing the AADL package
	 * @return AadlPackage
	 */
	public static AadlPackage getAadlPackage(String packageName, String packageFile) {

		AadlPackage aadlPackage = null;
		XtextResourceSet resourceSet = OsateResourceUtil.getResourceSet();

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
			try {
				Resource packageResource = resourceSet.createResource(uri);
				packageResource.load(null);
				// Grab the PropertySet specified in the CASE Prop file
				aadlPackage = (AadlPackage) packageResource.getContents().get(0);
			} catch (IOException e) {
				Dialog.showError(packageName + " Package", "Could not laod the " + packageFile + " file.");
				return null;
			}
		}

		return aadlPackage;
	}

}
