package com.collins.atc.ace.cyres.agree.json.plugin;

import java.util.ArrayList;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;
import org.osate.aadl2.AadlPackage;
import org.osate.aadl2.AadlString;
import org.osate.aadl2.BusAccess;
import org.osate.aadl2.Classifier;
import org.osate.aadl2.ComponentImplementation;
import org.osate.aadl2.ComponentType;
import org.osate.aadl2.ConnectedElement;
import org.osate.aadl2.Connection;
import org.osate.aadl2.DataPort;
import org.osate.aadl2.EnumerationType;
import org.osate.aadl2.EventDataPort;
import org.osate.aadl2.EventPort;
import org.osate.aadl2.Feature;
import org.osate.aadl2.ModalPropertyValue;
import org.osate.aadl2.Property;
import org.osate.aadl2.PropertyAssociation;
import org.osate.aadl2.PropertyExpression;
import org.osate.aadl2.PropertySet;
import org.osate.aadl2.Subcomponent;
import org.osate.aadl2.impl.EnumerationLiteralImpl;
import org.osate.aadl2.impl.IntegerLiteralImpl;
import org.osate.aadl2.impl.ListValueImpl;
import org.osate.aadl2.impl.NamedValueImpl;
import org.osate.aadl2.impl.StringLiteralImpl;
import org.osate.aadl2.util.Aadl2Switch;

import com.collins.atc.json.ArrayValue;
import com.collins.atc.json.ObjectValue;
import com.collins.atc.json.Pair;
import com.collins.atc.json.StringValue;
import com.collins.atc.json.Value;

public class AadlTranslate extends Aadl2Switch<Value> {

	AgreeTranslate agreeTranslate = new AgreeTranslate();

	@Override
	public ObjectValue caseAadlPackage(AadlPackage pkg) {
		ArrayList<Pair> pkgBuilder = new ArrayList<Pair>();
		pkgBuilder.add(Pair.build("kind", "package"));
		pkgBuilder.add(Pair.build("name", pkg.getName()));

		ArrayList<Value> components = new ArrayList<Value>();
		for (Classifier classifier : EcoreUtil2.getAllContentsOfType(pkg, Classifier.class)) {
			components.add(doSwitch(classifier));
		}

		pkgBuilder.add(Pair.build("agree", agreeTranslate.genAadlPackage(pkg)));

		pkgBuilder.add(Pair.build("components", ArrayValue.build(components)));
		return ObjectValue.build(pkgBuilder);
	}

	@Override
	public Value casePropertySet(PropertySet ps) {
		ArrayList<Pair> pairList = new ArrayList<Pair>();
		pairList.add(Pair.build("kind", "propertySet"));
		pairList.add(Pair.build("name", ps.getName()));
		ArrayList<Value> properties = new ArrayList<Value>();
		for (Property p : ps.getOwnedProperties()) {
			properties.add(doSwitch(p));
		}
		for (PropertyAssociation pa : ps.getOwnedPropertyAssociations()) {
			properties.add(doSwitch(pa));
		}
		pairList.add(Pair.build("properties", ArrayValue.build(properties)));
		return ObjectValue.build(pairList);
	}

	@Override
	public Value caseComponentType(ComponentType ty) {
		ArrayList<Pair> pairList = new ArrayList<Pair>();
		pairList.add(Pair.build("name", ty.getName()));
		pairList.add(Pair.build("type", getType(ty)));

		ArrayList<Value> features = new ArrayList<Value>();
		for (Feature feature : ty.getOwnedFeatures()) {
			features.add(doSwitch(feature));
		}
		pairList.add(Pair.build("features", ArrayValue.build(features)));

		ArrayList<Value> properties = new ArrayList<Value>();
		for (PropertyAssociation pa : ty.getOwnedPropertyAssociations()) {
			properties.add(doSwitch(pa));
		}
		pairList.add(Pair.build("properties", ArrayValue.build(properties)));
		pairList.add(Pair.build("agree", agreeTranslate.genComponentClassifier(ty)));
		return ObjectValue.build(pairList);
	}

	@Override
	public Value caseComponentImplementation(ComponentImplementation ci) {
		ArrayList<Pair> pairList = new ArrayList<Pair>();
		pairList.add(Pair.build(ci.getTypeName(), ci.getName()));

		ArrayList<Value> subcomponents = new ArrayList<Value>();
		for (Subcomponent sc : ci.getAllSubcomponents()) {
			subcomponents.add(doSwitch(sc));
		}

		ArrayList<Value> connections = new ArrayList<Value>();
		for (Connection c : ci.getAllConnections()) {
			connections.add(doSwitch(c));
		}
		pairList.add(Pair.build("subcomponents", ArrayValue.build(subcomponents)));
		pairList.add(Pair.build("connections", ArrayValue.build(connections)));
		pairList.add(Pair.build("agree", agreeTranslate.genComponentClassifier(ci)));
		return ObjectValue.build(pairList);
	}

	@Override
	public Value caseConnection(Connection c) {
		ArrayList<Pair> pairList = new ArrayList<Pair>();
		pairList.add(Pair.build("name", c.getName()));
		pairList.add(Pair.build("source", getName(c.getSource())));
		pairList.add(Pair.build("destination", getName(c.getDestination())));
		return ObjectValue.build(pairList);
	}

	/* Begin: Subcomponents */
	@Override
	public Value caseSubcomponent(Subcomponent sc) {
		ArrayList<Pair> pairList = new ArrayList<Pair>();
		pairList.add(Pair.build("name", sc.getName()));
		pairList.add(Pair.build("category", sc.getCategory().getName()));
		pairList.add(Pair.build("classifier", sc.getClassifier().getQualifiedName()));
		return ObjectValue.build(pairList);
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
		ArrayList<Pair> portJson = new ArrayList<Pair>();
		ArrayList<Pair> pairList = new ArrayList<Pair>();
		pairList.add(Pair.build("name", access.getName()));
		pairList.add(Pair.build("type", "bus"));
		portJson.add(Pair.build("access", ObjectValue.build(pairList)));
		return ObjectValue.build(portJson);
	}

	private Value buildPort(String name, String type, boolean in, boolean out) {
		ArrayList<Pair> portJson = new ArrayList<Pair>();
		ArrayList<Pair> pairList = new ArrayList<Pair>();
		pairList.add(Pair.build("name", name));
		pairList.add(Pair.build("type", type));
		pairList.add(Pair.build("flow", getInOutString(in, out)));
		portJson.add(Pair.build("port", ObjectValue.build(pairList)));
		return ObjectValue.build(portJson);
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

	private Value getEnumerationLiteralImpl(EnumerationLiteralImpl enumLit) {
		return StringValue.build(enumLit.getName().toString());
	}

	private Value getListValueImpl(ListValueImpl lv) {
		ArrayList<Value> vsJson = new ArrayList<Value>();
		for (PropertyExpression pe : lv.getOwnedListElements()) {
			vsJson.add(genPropertyExpression(pe));
		}
		return ArrayValue.build(vsJson);
	}

	private Value genNamedValueImpl(NamedValueImpl v) {
		if (v.getNamedValue() instanceof EnumerationLiteralImpl) {
			return getEnumerationLiteralImpl((EnumerationLiteralImpl) v.getNamedValue());
		}

		return StringValue.build("new_case/genNamedValueImpl/" + v.getNamedValue());
	}

	private Value getStringLiteralImpl(StringLiteralImpl v) {
		return StringValue.build(v.getValue());
	}

	private Value getIntegerLiteralImpl(IntegerLiteralImpl v) {
		return StringValue.build(Long.toString((v.getValue())));
	}

	private Value genPropertyExpression(PropertyExpression v) {
		if (v instanceof NamedValueImpl) {
			return genNamedValueImpl((NamedValueImpl) v);
		} else if (v instanceof ListValueImpl) {
			return getListValueImpl((ListValueImpl) v);
		} else if (v instanceof StringLiteralImpl) {
			return getStringLiteralImpl((StringLiteralImpl) v);
		} else if (v instanceof IntegerLiteralImpl) {
			return getIntegerLiteralImpl((IntegerLiteralImpl) v);
		}
		return StringValue.build("new_case/genPropertyExpression/" + v.toString());
	}

	@Override
	public Value caseModalPropertyValue(ModalPropertyValue v) {
		return genPropertyExpression(v.getOwnedValue());
	}

	@Override
	public Value caseProperty(Property p) {
		ArrayList<Pair> pairList = new ArrayList<Pair>();
		pairList.add(Pair.build("name", p.getName()));
		pairList.add(Pair.build("propertyType", doSwitch(p.getPropertyType())));
		return ObjectValue.build(pairList);
	}

	@Override
	public Value caseEnumerationType(EnumerationType et) {
		ArrayList<Pair> pairList = new ArrayList<Pair>();
		pairList.add(Pair.build("kind", "enumType"));

		ArrayList<Value> enumList = new ArrayList<Value>();
		for (org.osate.aadl2.EnumerationLiteral l : et.getOwnedLiterals()) {
			enumList.add(StringValue.build(l.getName()));
		}
		pairList.add(Pair.build("enums", ArrayValue.build(enumList)));
		return ObjectValue.build(pairList);
	}

	@Override
	public Value caseAadlString(AadlString s) {
		ArrayList<Pair> pairList = new ArrayList<Pair>();
		pairList.add(Pair.build("kind", "string"));
		return ObjectValue.build(pairList);
	}

	@Override
	public Value casePropertyAssociation(PropertyAssociation pa) {
		ArrayList<Pair> pairList = new ArrayList<Pair>();
		pairList.add(Pair.build("name", pa.getProperty().getName()));

		// Seems like the list always has exactly one element
		ModalPropertyValue v = pa.getOwnedValues().get(0);

		pairList.add(Pair.build("value", doSwitch(v)));
		return ObjectValue.build(pairList);
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
