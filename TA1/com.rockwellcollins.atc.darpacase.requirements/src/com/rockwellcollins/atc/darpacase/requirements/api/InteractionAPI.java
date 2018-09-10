package com.rockwellcollins.atc.darpacase.requirements.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.osate.aadl2.ComponentCategory;
import org.osate.aadl2.Property;
import org.osate.aadl2.PropertyAssociation;
import org.osate.aadl2.PropertySet;
import org.osate.aadl2.instance.ComponentInstance;
import org.osate.aadl2.instance.ConnectionInstance;
import org.osate.aadl2.instance.SystemInstance;

public class InteractionAPI {

	public static final String TA1_PROPERTY_SET_NAME = "CASETA1";
	public final SystemInstance instance;

	/*******************************************************************
	 * Constructor and functions to help create the API
	 ******************************************************************/

	public InteractionAPI(SystemInstance instance) {
		this.instance = instance;
	}

	/******************************* Connectedness ************************************/
	private Set<ComponentInstance> recurse(ComponentInstance top, ComponentInstance src) {
		Set<ComponentInstance> set = new HashSet<>();
		ComponentInstance curr = src;
		while (curr != top) {
			set.add(curr);
			curr = curr.getContainingComponentInstance();
		}
		return set;
	}

	private Set<ComponentInstance> getConnectedComponents(ComponentInstance component, ConnectionInstance connection) {
		Set<ComponentInstance> current = new HashSet<>();
		current.addAll(recurse(component, connection.getSource().getComponentInstance()));
		current.addAll(recurse(component, connection.getDestination().getComponentInstance()));
		current.remove(component);
		return current;
	}

	public Map<ConnectionInstance, Set<ComponentInstance>> getConnectedComponents(ComponentInstance component) {
		Map<ConnectionInstance, Set<ComponentInstance>> connectedMap = new HashMap<>();
		for (ConnectionInstance connection : component.getConnectionInstances()) {
			Set<ComponentInstance> current = getConnectedComponents(component, connection);
			connectedMap.put(connection, current);
		}
		return connectedMap;
	}

	/**************************************************************************************/
	public List<ComponentInstance> getSoftwareComponents(SystemInstance si) {
		List<ComponentInstance> software = new ArrayList<>();
		software.addAll(si.getAllComponentInstances(ComponentCategory.SUBPROGRAM));
		software.addAll(si.getAllComponentInstances(ComponentCategory.SUBPROGRAM_GROUP));
		software.addAll(si.getAllComponentInstances(ComponentCategory.THREAD));
		software.addAll(si.getAllComponentInstances(ComponentCategory.THREAD_GROUP));
		software.addAll(si.getAllComponentInstances(ComponentCategory.PROCESS));

		return software;
	}

	/**************************************************************************************/

	public List<Property> getAppliedProperties(ComponentInstance ci) {
		return ci.getOwnedPropertyAssociations().stream().map(pa -> pa.getProperty()).collect(Collectors.toList());
	}

	public static boolean isTA1Property(PropertyAssociation pa) {
		return isTA1Property(pa.getProperty());
	}

	public static boolean isTA1Property(Property p) {
		if (p.eContainer() instanceof PropertySet) {
			PropertySet pset = (PropertySet) p.eContainer();
			if (pset.getName().equals(TA1_PROPERTY_SET_NAME)) {
				return true;
			}
		}
		return false;
	}

}
