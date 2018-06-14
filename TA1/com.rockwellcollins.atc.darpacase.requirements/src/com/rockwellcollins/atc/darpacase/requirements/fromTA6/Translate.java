package com.rockwellcollins.atc.darpacase.requirements.fromTA6;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;
import org.osate.aadl2.AadlPackage;
import org.osate.aadl2.BooleanLiteral;
import org.osate.aadl2.BusAccess;
import org.osate.aadl2.Classifier;
import org.osate.aadl2.ComponentImplementation;
import org.osate.aadl2.ComponentType;
import org.osate.aadl2.ConnectedElement;
import org.osate.aadl2.Connection;
import org.osate.aadl2.DataPort;
import org.osate.aadl2.EventDataPort;
import org.osate.aadl2.EventPort;
import org.osate.aadl2.Feature;
import org.osate.aadl2.ModalPropertyValue;
import org.osate.aadl2.Property;
import org.osate.aadl2.PropertyAssociation;
import org.osate.aadl2.PropertySet;
import org.osate.aadl2.util.Aadl2Switch;

import com.rockwellcollins.atc.darpacase.requirements.builders.ArrayBuilder;
import com.rockwellcollins.atc.darpacase.requirements.builders.ObjectBuilder;
import com.rockwellcollins.atc.darpacase.requirements.json.ObjectValue;
import com.rockwellcollins.atc.darpacase.requirements.json.Pair;
import com.rockwellcollins.atc.darpacase.requirements.json.StringValue;
import com.rockwellcollins.atc.darpacase.requirements.json.Value;

public class Translate extends Aadl2Switch<Value> {

	@Override
	public ObjectValue caseAadlPackage(AadlPackage pkg) {
		ObjectBuilder pkgBuilder = new ObjectBuilder();
		pkgBuilder.addPair(Pair.build("package", pkg.getName()));

		ArrayBuilder components = new ArrayBuilder();
		for (Classifier classifier : EcoreUtil2.getAllContentsOfType(pkg, Classifier.class)) {
			components.addValue(doSwitch(classifier));
		}

		pkgBuilder.addPair(Pair.build("components", components.build()));
		return pkgBuilder.build();
	}

	@Override
	public Value caseComponentType(ComponentType ty) {
		ObjectBuilder builder = new ObjectBuilder();
		builder.addPair(Pair.build("name", ty.getName()));
		builder.addPair(Pair.build("type", getType(ty)));

		ArrayBuilder features = new ArrayBuilder();
		for (Feature feature : ty.getOwnedFeatures()) {
			features.addValue(doSwitch(feature));
		}
		builder.addPair(Pair.build("features", features.build()));

		ArrayBuilder properties = new ArrayBuilder();
		for (PropertyAssociation pa : ty.getOwnedPropertyAssociations()) {
			Property p = pa.getProperty();
			PropertySet set = EcoreUtil2.getContainerOfType(p, PropertySet.class);
			if (set.getName().equals("CASETA1")) {
				properties.addValue(doSwitch(pa));
			}
		}
		builder.addPair(Pair.build("properties", properties.build()));
		return builder.build();
	}

	@Override
	public Value caseComponentImplementation(ComponentImplementation ci) {
		ObjectBuilder builder = new ObjectBuilder();
		builder.addPair(Pair.build(ci.getTypeName(), ci.getName()));

		ArrayBuilder connections = new ArrayBuilder();
		for (Connection c : ci.getAllConnections()) {
			connections.addValue(doSwitch(c));
		}
		builder.addPair(Pair.build("connections", connections.build()));
		return builder.build();
	}

	@Override
	public Value caseConnection(Connection c) {
		ObjectBuilder builder = new ObjectBuilder();
		builder.addPair(Pair.build("name", c.getName()));
		builder.addPair(Pair.build("source", getName(c.getSource())));
		builder.addPair(Pair.build("destination", getName(c.getDestination())));
		return builder.build();
	}

	/* Begin: Features */

	@Override
	public Value caseDataPort(DataPort port) {
		return buildPort(port.getName(), "data", port.isIn(), port.isOut());
	}

	@Override
	public Value caseEventDataPort(EventDataPort port) {
		return buildPort(port.getName(), "event data", port.isIn(), port.isOut());
	}

	@Override
	public Value caseEventPort(EventPort port) {
		return buildPort(port.getName(), "event", port.isIn(), port.isOut());
	}

	@Override
	public Value caseBusAccess(BusAccess access) {
		ObjectBuilder portJson = new ObjectBuilder();
		ObjectBuilder builder = new ObjectBuilder();
		builder.addPair(Pair.build("name", access.getName()));
		builder.addPair(Pair.build("type", "bus"));
		portJson.addPair(Pair.build("access", builder.build()));
		return portJson.build();
	}

	private Value buildPort(String name, String type, boolean in, boolean out) {
		ObjectBuilder portJson = new ObjectBuilder();
		ObjectBuilder builder = new ObjectBuilder();
		builder.addPair(Pair.build("name", name));
		builder.addPair(Pair.build("type", type));
		builder.addPair(Pair.build("flow", getInOutString(in, out)));
		portJson.addPair(Pair.build("port", builder.build()));
		return portJson.build();
	}

	private String getInOutString(boolean in, boolean out) {
		if (in && out) {
			return "inout";
		}

		if (in) {
			return "in";
		}

		if (out) {
			return "out";
		}

		return null;
	}

	/* End Features */

	/* Begin Properties */

	// this is *ONLY* going to support boolean properties for now.
	@Override
	public Value casePropertyAssociation(PropertyAssociation pa) {
		ObjectBuilder property = new ObjectBuilder();
		Property p = pa.getProperty();

		List<ModalPropertyValue> values = new ArrayList<>(pa.getOwnedValues());
		//expect just one, because it's boolean
		ModalPropertyValue x = values.get(0);
		if (x.getOwnedValue() instanceof BooleanLiteral) {
			BooleanLiteral bv = (BooleanLiteral) x.getOwnedValue();
			if (bv.getValue()) {
				property.addPair(Pair.build("property", p.getName()));
			}
		}

		return property.build();
	}

	private static String getName(ConnectedElement ce) {
		String prefix = "";
		if (ce.getContext() != null) {
			prefix = ce.getContext().getName() + ".";
		}
		return prefix + ce.getConnectionEnd().getName();
	}

	private static String getType(ComponentType ct) {
		return ct.getCategory().getName();
	}

	@Override
	public Value defaultCase(EObject eo) {
		System.out.println(eo.toString());
		return StringValue.build("UNKNOWN");
	}
}
