package com.collins.fmw.cyres.architecture.utils;

import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.osate.aadl2.AadlPackage;
import org.osate.aadl2.ModelUnit;
import org.osate.aadl2.PackageSection;
import org.osate.aadl2.PropertySet;
import org.osate.aadl2.impl.PropertySetImpl;
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
	public static PropertySet addCasePropertyImport(PackageSection pkgSection) throws Exception {

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
			casePropSet = getPropertySet(CASE_PROPSET_NAME, CASE_PROPSET_FILE, pkgSection.eResource().getResourceSet());
			if (casePropSet == null) {
				Dialog.showError("Could not import " + CASE_PROPSET_NAME,
						"Property set " + CASE_PROPSET_NAME + " could not be found.");
				return null;
			}
			// Add as "importedUnit" to package section
			pkgSection.getImportedUnits().add(casePropSet);
		}

		return casePropSet;
	}

	/**
	 * Adds the CASE_Model_Transformations file to the list of imported model units via the 'with' statement
	 * to the specified package section
	 * @param pkgSection - The package section (public or private) to add the imported file to
	 * @param addRenameAll - If true, will add a rename::all for the package
	 * @return CASE_Model_Transformations package
	 */
	public static AadlPackage addCaseModelTransformationsImport(PackageSection pkgSection, boolean addRenameAll)
			throws Exception {

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
			casePackage = getAadlPackage(CASE_MODEL_TRANSFORMATIONS_NAME, CASE_MODEL_TRANSFORMATIONS_FILE,
					pkgSection.eResource().getResourceSet());
			if (casePackage == null) {
				Dialog.showError("Could not import " + CASE_MODEL_TRANSFORMATIONS_NAME,
						"Package " + CASE_MODEL_TRANSFORMATIONS_NAME + " could not be found.");
				return null;
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

		return casePackage;
	}

	/**
	 * Gets the property set from either the current resource, or
	 * the specified file, provided as an OSATE plugin.
	 * @param propSetName - The name of the property set
	 * @param propSetFile - The file name containing the property set
	 * @param resourceSet - The ResourceSet that contains all open resources
	 * @return PropertySet
	 */
	public static PropertySet getPropertySet(String propSetName, String propSetFile, ResourceSet resourceSet)
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
	public static AadlPackage getAadlPackage(String packageName, String packageFile, ResourceSet resourceSet)
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

}
