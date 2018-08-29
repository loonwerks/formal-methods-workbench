package com.rockwellcollins.atc.darpacase.requirements.api;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;
import org.osate.aadl2.AadlPackage;
import org.osate.aadl2.ComponentImplementation;
import org.osate.aadl2.ComponentType;
import org.osate.aadl2.ModelUnit;
import org.osate.aadl2.PropertyAssociation;
import org.osate.aadl2.PropertySet;
import org.osate.aadl2.modelsupport.util.AadlUtil;

public class InteractionAPI {

	private static final Object TA1PROPERTYSETNAME = "CASETA1";

	public static ComponentType getComponentType(ComponentImplementation ci) {
		return ci.getOwnedRealization().getImplemented();
	}

	/*******************************************************************
	 * Constructor and functions to help create the API
	 ******************************************************************/
	private AadlPackage rootPackage;
	private Set<AadlPackage> packages;
	private Set<PropertySet> propertySets;

	public InteractionAPI(AadlPackage pkg) {
		rootPackage = pkg;
		packages = new HashSet<>();
		propertySets = new HashSet<>();

		processPackage(rootPackage);
	}

	public void processPackage(AadlPackage pkg) {
		if (packages.contains(pkg)) {
			return;
		}

		packages.add(pkg);

		if (pkg.getPrivateSection() != null) {
			for (ModelUnit mu : pkg.getPrivateSection().getImportedUnits()) {
				if (mu instanceof AadlPackage) {
					AadlPackage importedPkg = (AadlPackage) mu;
					processPackage(importedPkg);
				} else if (mu instanceof PropertySet) {
					PropertySet pset = (PropertySet) mu;
					processPropertySet(pset);
				}
			}
		}
	}

	private void processPropertySet(PropertySet pset) {
		if (propertySets.contains(pset)) {
			return;
		}

		propertySets.add(pset);
		for (ModelUnit mu : pset.getImportedUnits()) {
			PropertySet imported = (PropertySet) mu;
			processPropertySet(imported);
		}
	}

	public List<PropertyAssociation> getTA1Properties() {
		List<PropertyAssociation> ta1 = new ArrayList<>();
		for (AadlPackage pkg : packages) {
			ta1.addAll(getTA1Properties(pkg));
		}
		return ta1;
	}

	/*******************************************************************
	 * TA1 Property functions
	 ******************************************************************/
	private List<PropertyAssociation> getTA1Properties(EObject eo) {
		List<PropertyAssociation> ta1 = new ArrayList<>();
		for (PropertyAssociation pa : EcoreUtil2.getAllContentsOfType(eo, PropertyAssociation.class)) {
			PropertySet set = EcoreUtil2.getContainerOfType(pa.getProperty(), PropertySet.class);
			if (set.getName().equals(TA1PROPERTYSETNAME)) {
				ta1.add(pa);
			}
		}
		return ta1;
	}

	public List<PropertyAssociation> getComponentTA1Properties(ComponentImplementation ci) {
		ComponentType ct = getComponentType(ci);
		List<PropertyAssociation> props = new ArrayList<>();
		props.addAll(getTA1Properties(ci));
		props.addAll(getTA1Properties(ct));
		return props;
	}

	/*******************************************************************
	 * Getting implementations
	 ******************************************************************/
	public List<ComponentImplementation> getImplementations() {
		List<ComponentImplementation> componentImpls = new ArrayList<>();
		for (AadlPackage pkg : packages) {
			componentImpls.addAll(AadlUtil.getAllComponentImpl(pkg));
		}
		return componentImpls;
	}

	public List<ComponentImplementation> getImplementations(ComponentType ct) {
		List<ComponentImplementation> impls = getImplementations();
		return impls.stream().filter(ci -> getComponentType(ci).equals(ct)).collect(Collectors.toList());
	}

	/*******************************************************************
	 * Connectedness
	 *******************************************************************/
}
