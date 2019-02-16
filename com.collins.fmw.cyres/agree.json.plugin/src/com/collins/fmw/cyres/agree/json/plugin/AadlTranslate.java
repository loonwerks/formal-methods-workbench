package com.collins.fmw.cyres.agree.json.plugin;

import java.util.ArrayList;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;
import org.osate.aadl2.AadlInteger;
import org.osate.aadl2.AadlPackage;
import org.osate.aadl2.AadlReal;
import org.osate.aadl2.AadlString;
import org.osate.aadl2.AbstractNamedValue;
import org.osate.aadl2.AccessConnection;
import org.osate.aadl2.AnnexLibrary;
import org.osate.aadl2.AnnexSubclause;
import org.osate.aadl2.BooleanLiteral;
import org.osate.aadl2.BusAccess;
import org.osate.aadl2.BusFeatureClassifier;
import org.osate.aadl2.BusImplementation;
import org.osate.aadl2.BusType;
import org.osate.aadl2.Classifier;
import org.osate.aadl2.ClassifierType;
import org.osate.aadl2.ClassifierValue;
import org.osate.aadl2.ComponentClassifier;
import org.osate.aadl2.ComponentImplementation;
import org.osate.aadl2.ComponentType;
import org.osate.aadl2.ConnectedElement;
import org.osate.aadl2.Connection;
import org.osate.aadl2.ContainedNamedElement;
import org.osate.aadl2.DataPort;
import org.osate.aadl2.DefaultAnnexLibrary;
import org.osate.aadl2.DefaultAnnexSubclause;
import org.osate.aadl2.EnumerationLiteral;
import org.osate.aadl2.EnumerationType;
import org.osate.aadl2.EventDataPort;
import org.osate.aadl2.EventPort;
import org.osate.aadl2.Feature;
import org.osate.aadl2.IntegerLiteral;
import org.osate.aadl2.ListType;
import org.osate.aadl2.ListValue;
import org.osate.aadl2.MetaclassReference;
import org.osate.aadl2.ModalPropertyValue;
import org.osate.aadl2.ModelUnit;
import org.osate.aadl2.NamedValue;
import org.osate.aadl2.NumberType;
import org.osate.aadl2.PackageRename;
import org.osate.aadl2.PackageSection;
import org.osate.aadl2.PortConnection;
import org.osate.aadl2.Property;
import org.osate.aadl2.PropertyAssociation;
import org.osate.aadl2.PropertyConstant;
import org.osate.aadl2.PropertyExpression;
import org.osate.aadl2.PropertySet;
import org.osate.aadl2.PropertyType;
import org.osate.aadl2.RangeType;
import org.osate.aadl2.RangeValue;
import org.osate.aadl2.RealLiteral;
import org.osate.aadl2.ReferenceValue;
import org.osate.aadl2.StringLiteral;
import org.osate.aadl2.Subcomponent;
import org.osate.aadl2.util.Aadl2Switch;

import com.collins.fmw.json.ArrayValue;
import com.collins.fmw.json.ObjectValue;
import com.collins.fmw.json.Pair;
import com.collins.fmw.json.StringValue;
import com.collins.fmw.json.Value;

public class AadlTranslate extends Aadl2Switch<Value> {

	AgreeTranslate agreeTranslate = new AgreeTranslate();

	@Override
	public ObjectValue casePackageRename(PackageRename packageRename) {
		ArrayList<Pair> pairList = new ArrayList<Pair>();
		pairList.add(Pair.build("package", packageRename.getRenamedPackage().getName()));
		if (packageRename.isRenameAll()) {
			pairList.add(Pair.build("rename", "all"));
		} else {
			pairList.add(Pair.build("rename", packageRename.getName()));
		}
		return ObjectValue.build(pairList);
	}

	@Override
	public ObjectValue caseAadlPackage(AadlPackage pkg) {
		ArrayList<Pair> pkgBuilder = new ArrayList<Pair>();
		pkgBuilder.add(Pair.build("name", pkg.getName()));
		pkgBuilder.add(Pair.build("kind", "AadlPackage"));

		// Build Package Sections
		Value v = buildPackageSection(pkg.getOwnedPublicSection());
		if (!isEmptyValue(v)) {
			pkgBuilder.add(Pair.build("public", v));
		}
		v = buildPackageSection(pkg.getOwnedPrivateSection());
		if (!isEmptyValue(v)) {
			pkgBuilder.add(Pair.build("private", v));
		}

		return ObjectValue.build(pkgBuilder);
	}

	public ObjectValue buildPackageSection(PackageSection packageSection) {

		ArrayList<Pair> pkgBuilder = new ArrayList<Pair>();

		if (packageSection == null) {
			return ObjectValue.build(pkgBuilder);
		}

		// Get imported models
		ArrayList<Value> modelUnits = new ArrayList<Value>();
		for (ModelUnit modelUnit : packageSection.getImportedUnits()) {
			modelUnits.add(StringValue.build(modelUnit.getName()));
		}
		if (!modelUnits.isEmpty()) {
			pkgBuilder.add(Pair.build("with", ArrayValue.build(modelUnits)));
		}

		// Get package renames
		ArrayList<Value> packageRenames = new ArrayList<Value>();
		for (PackageRename packageRename : EcoreUtil2.getAllContentsOfType(packageSection, PackageRename.class)) {
			packageRenames.add(doSwitch(packageRename));
		}
		if (!packageRenames.isEmpty()) {
			pkgBuilder.add(Pair.build("packageRenames", ArrayValue.build(packageRenames)));
		}

		// Get components
		ArrayList<Value> components = new ArrayList<Value>();
		for (Classifier classifier : EcoreUtil2.getAllContentsOfType(packageSection, Classifier.class)) {
			components.add(doSwitch(classifier));
		}
		if (!components.isEmpty()) {
			pkgBuilder.add(Pair.build("components", ArrayValue.build(components)));
		}

		// Get annex libraries
		ArrayList<Value> annexes = new ArrayList<Value>();
		for (AnnexLibrary annexLib : packageSection.getOwnedAnnexLibraries()) {
			annexes.add(doSwitch(annexLib));
		}
		if (!annexes.isEmpty()) {
			pkgBuilder.add(Pair.build("annexes", ArrayValue.build(annexes)));
		}

		return ObjectValue.build(pkgBuilder);
	}

	@Override
	public Value casePropertySet(PropertySet ps) {
		ArrayList<Pair> pairList = new ArrayList<Pair>();
		pairList.add(Pair.build("name", ps.getName()));
		pairList.add(Pair.build("kind", "PropertySet"));

		ArrayList<Value> properties = new ArrayList<Value>();
		for (Property p : ps.getOwnedProperties()) {
			properties.add(buildProperty(p));
		}
		if (!properties.isEmpty()) {
			pairList.add(Pair.build("properties", ArrayValue.build(properties)));
		}
		ArrayList<Value> propertyConstants = new ArrayList<Value>();
		for (PropertyConstant p : ps.getOwnedPropertyConstants()) {
			propertyConstants.add(doSwitch(p));
		}
		if (!propertyConstants.isEmpty()) {
			pairList.add(Pair.build("propertyConstants", ArrayValue.build(propertyConstants)));
		}
		ArrayList<Value> propertyTypes = new ArrayList<Value>();
		for (PropertyType p : ps.getOwnedPropertyTypes()) {
			propertyTypes.add(buildPropertyType(p));
		}
		if (!propertyTypes.isEmpty()) {
			pairList.add(Pair.build("propertyTypes", ArrayValue.build(propertyTypes)));
		}
		return ObjectValue.build(pairList);
	}

	@Override
	public Value caseComponentType(ComponentType ct) {
		ArrayList<Pair> pairList = new ArrayList<Pair>();
		pairList.add(Pair.build("name", ct.getName()));
		pairList.add(Pair.build("kind", "ComponentType"));
		pairList.add(Pair.build("category", ct.getCategory().getName()));

		ArrayList<Value> features = new ArrayList<Value>();
		for (Feature feature : ct.getOwnedFeatures()) {
			features.add(doSwitch(feature));
		}
		if (!features.isEmpty()) {
			pairList.add(Pair.build("features", ArrayValue.build(features)));
		}

		ArrayList<Value> properties = new ArrayList<Value>();
		for (PropertyAssociation pa : ct.getOwnedPropertyAssociations()) {
			properties.add(doSwitch(pa));
		}
		if (!properties.isEmpty()) {
			pairList.add(Pair.build("properties", ArrayValue.build(properties)));
		}

		// add annex subclauses
		ArrayList<Value> annexes = new ArrayList<Value>();
		for (AnnexSubclause annex : ct.getOwnedAnnexSubclauses()) {
			annexes.add(doSwitch(annex));
		}
		if (!annexes.isEmpty()) {
			pairList.add(Pair.build("annexes", ArrayValue.build(annexes)));
		}

		return ObjectValue.build(pairList);
	}

	@Override
	public Value caseComponentImplementation(ComponentImplementation ci) {
		ArrayList<Pair> pairList = new ArrayList<Pair>();
		pairList.add(Pair.build("name", ci.getName()));
		pairList.add(Pair.build("kind", "ComponentImplementation"));
		pairList.add(Pair.build("category", ci.getCategory().getName()));

		ArrayList<Value> subcomponents = new ArrayList<Value>();
		for (Subcomponent sc : ci.getOwnedSubcomponents()) {
			subcomponents.add(doSwitch(sc));
		}
		if (!subcomponents.isEmpty()) {
			pairList.add(Pair.build("subcomponents", ArrayValue.build(subcomponents)));
		}

		ArrayList<Value> connections = new ArrayList<Value>();
		for (Connection c : ci.getOwnedConnections()) {
			connections.add(doSwitch(c));
		}
		if (!connections.isEmpty()) {
			pairList.add(Pair.build("connections", ArrayValue.build(connections)));
		}

		ArrayList<Value> properties = new ArrayList<Value>();
		for (PropertyAssociation pa : ci.getOwnedPropertyAssociations()) {
			properties.add(doSwitch(pa));
		}
		if (!properties.isEmpty()) {
			pairList.add(Pair.build("properties", ArrayValue.build(properties)));
		}

		// add annex subclauses
		ArrayList<Value> annexes = new ArrayList<Value>();
		for (AnnexSubclause annex : ci.getOwnedAnnexSubclauses()) {
			annexes.add(doSwitch(annex));
		}
		if (!annexes.isEmpty()) {
			pairList.add(Pair.build("annexes", ArrayValue.build(annexes)));
		}

		return ObjectValue.build(pairList);
	}

	@Override
	public Value caseConnection(Connection c) {
		ArrayList<Pair> pairList = new ArrayList<Pair>();

		pairList.add(Pair.build("name", c.getName()));

		String kind = "";
		if (c instanceof PortConnection) {
			kind = "port";
		} else if (c instanceof AccessConnection) {
			kind = ((AccessConnection) c).getAccessCategory().getName() + "Access";
		}
		if (!kind.isEmpty()) {
			pairList.add(Pair.build("kind", kind));
		}

		pairList.add(Pair.build("source", getName(c.getSource())));
		pairList.add(Pair.build("destination", getName(c.getDestination())));
		pairList.add(Pair.build("bidirectional", c.isBidirectional() ? "true" : "false"));

		return ObjectValue.build(pairList);
	}

	@Override
	public Value caseAnnexLibrary(AnnexLibrary al) {

		ArrayList<Pair> pairList = new ArrayList<Pair>();
		pairList.add(Pair.build("name", al.getName()));
		pairList.add(Pair.build("kind", "AnnexLibrary"));
		if (al.getName().equalsIgnoreCase("agree")) {
			// AGREE annex will be parsed
			pairList.add(Pair.build("parsedAnnexLibrary", agreeTranslate.genAnnexLibrary(al)));
		} else {
			DefaultAnnexLibrary defaultAnnexLib = (DefaultAnnexLibrary) al;
			pairList.add(Pair.build("sourceText", defaultAnnexLib.getSourceText().replace("\"", "\\\"")));
		}
		return ObjectValue.build(pairList);
	}

	@Override
	public Value caseAnnexSubclause(AnnexSubclause as) {

		ArrayList<Pair> pairList = new ArrayList<Pair>();
		pairList.add(Pair.build("name", as.getName()));
		pairList.add(Pair.build("kind", "AnnexSubclause"));
		if (as.getName().equalsIgnoreCase("agree")) {
			// AGREE annex will be parsed
			pairList.add(Pair.build("parsedAnnexSubclause", agreeTranslate.genAnnexSubclause(as)));
		} else {
			DefaultAnnexSubclause defaultAnnexSubclause = (DefaultAnnexSubclause) as;
			pairList.add(Pair.build("sourceText", defaultAnnexSubclause.getSourceText().replace("\"", "\\\"")));
		}
		return ObjectValue.build(pairList);
	}

	/* Begin: Subcomponents */
	@Override
	public Value caseSubcomponent(Subcomponent sc) {
		ArrayList<Pair> pairList = new ArrayList<Pair>();
		pairList.add(Pair.build("name", sc.getName()));
		pairList.add(Pair.build("kind", "Subcomponent"));
		pairList.add(Pair.build("category", sc.getCategory().getName()));
		ComponentClassifier c = sc.getClassifier();
		if (c == null) {
			pairList.add(Pair.build("classifier", "null"));
		} else {
			pairList.add(Pair.build("classifier", sc.getClassifier().getQualifiedName()));
		}
		return ObjectValue.build(pairList);
	}

	/* Begin: Features */

	@Override
	public Value caseDataPort(DataPort port) {
		return buildPort(port.getName(), "DataPort", port.getClassifier(), port.isIn(), port.isOut());
	}

	@Override
	public Value caseEventDataPort(EventDataPort port) {
		return buildPort(port.getName(), "EventDataPort", port.getClassifier(), port.isIn(), port.isOut());
	}

	@Override
	public Value caseEventPort(EventPort port) {
		return buildPort(port.getName(), "EventPort", port.getClassifier(), port.isIn(), port.isOut());
	}

	@Override
	public Value caseBusAccess(BusAccess access) {

		ArrayList<Pair> pairList = new ArrayList<Pair>();
		pairList.add(Pair.build("name", access.getName()));
		pairList.add(Pair.build("kind", "BusAccess"));
		pairList.add(Pair.build("accessType", access.getKind().getName()));

		BusFeatureClassifier classifier = access.getBusFeatureClassifier();
		String bus = "";
		if (classifier instanceof BusType) {
			bus = ((BusType) classifier).getQualifiedName();
		} else if (classifier instanceof BusImplementation) {
			bus = ((BusImplementation) classifier).getQualifiedName();
		}
		if (!bus.isEmpty()) {
			pairList.add(Pair.build("bus", bus));
		}

		return ObjectValue.build(pairList);
	}

	private Value buildPort(String name, String kind, Classifier classifier, boolean in, boolean out) {

		ArrayList<Pair> pairList = new ArrayList<Pair>();
		pairList.add(Pair.build("name", name));
		pairList.add(Pair.build("kind", kind));
		if (classifier == null) {
			pairList.add(Pair.build("classifier", "null"));
		} else {
			pairList.add(Pair.build("classifier", classifier.getQualifiedName()));
		}
		pairList.add(Pair.build("direction", getInOutString(in, out)));

		return ObjectValue.build(pairList);
	}

	private String getInOutString(boolean in, boolean out) {
		if (in && out) {
			return "inout";
		} else if (in) {
			return "in";
		} else if (out) {
			return "out";
		}

		return "";
	}

	/* End Features */

	/* Begin Properties */

	private Value getEnumerationLiteral(EnumerationLiteral enumLit) {
		return StringValue.build(enumLit.getName().toString());
	}

	private Value getListValue(ListValue lv) {
		ArrayList<Value> vsJson = new ArrayList<Value>();
		for (PropertyExpression pe : lv.getOwnedListElements()) {
			vsJson.add(genPropertyExpression(pe));
		}
		return ArrayValue.build(vsJson);
	}

	private Value genNamedValue(NamedValue v) {
		if (v.getNamedValue() instanceof EnumerationLiteral) {
			return getEnumerationLiteral((EnumerationLiteral) v.getNamedValue());
		}

		return StringValue.build("new_case/genNamedValue/" + v.getNamedValue());
	}

	private Value genRangeValue(RangeValue v) {
		String s = "";
		PropertyExpression pMin = v.getMinimum();
		PropertyExpression pMax = v.getMaximum();

		if (pMin instanceof BooleanLiteral) {
			s = ((BooleanLiteral) pMin).getValue() ? "true" : "false";
		} else if (pMin instanceof IntegerLiteral) {
			s = Long.toString(((IntegerLiteral) pMin).getValue());
		} else if (pMin instanceof RealLiteral) {
			s = Double.toString(((RealLiteral) pMin).getValue());
		} else if (pMin instanceof NamedValue) {
			AbstractNamedValue anv = ((NamedValue) pMin).getNamedValue();
			if (anv instanceof PropertyConstant) {
				s = ((PropertyConstant) anv).getName();
			} else {
				return StringValue.build("new_case/genRangeValue/" + v.toString());
			}
		} else {
			return StringValue.build("new_case/genRangeValue/" + v.toString());
		}

		s += " .. ";

		if (pMax instanceof BooleanLiteral) {
			s += ((BooleanLiteral) pMax).getValue() ? "true" : "false";
		} else if (pMax instanceof IntegerLiteral) {
			s += Long.toString(((IntegerLiteral) pMax).getValue());
		} else if (pMax instanceof RealLiteral) {
			s += Double.toString(((RealLiteral) pMax).getValue());
		} else if (pMax instanceof NamedValue) {
			AbstractNamedValue anv = ((NamedValue) pMax).getNamedValue();
			if (anv instanceof PropertyConstant) {
				s += ((PropertyConstant) anv).getName();
			} else {
				return StringValue.build("new_case/genRangeValue/" + v.toString());
			}
		} else {
			return StringValue.build("new_case/genRangeValue/" + v.toString());
		}

		return StringValue.build(s);
	}

	private Value genPropertyExpression(PropertyExpression v) {
		if (v instanceof NamedValue) {
			return genNamedValue((NamedValue) v);
		} else if (v instanceof ListValue) {
			return getListValue((ListValue) v);
		} else if (v instanceof StringLiteral) {
			return StringValue.build(((StringLiteral) v).getValue());
		} else if (v instanceof IntegerLiteral) {
			return StringValue.build(Long.toString(((IntegerLiteral) v).getValue()));
		} else if (v instanceof ReferenceValue) {
			return StringValue.build(((ReferenceValue) v).getPath().getNamedElement().getName());
		} else if (v instanceof RangeValue) {
			return genRangeValue((RangeValue) v);
		} else if (v instanceof ClassifierValue) {
			return StringValue.build(((ClassifierValue) v).getClassifier().getName());
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

		pairList.add(Pair.build("name", p.getQualifiedName()));
		pairList.add(Pair.build("kind", "Property"));
		pairList.add(Pair.build("inherit", p.isInherit() ? "true" : "false"));
		pairList.add(Pair.build("propertyType", doSwitch(p.getPropertyType())));
		ArrayList<Value> appliesTo = new ArrayList<Value>();
		for (MetaclassReference ref : p.getAppliesToMetaclasses()) {
			String name = "";
			for (String n : ref.getMetaclassNames()) {
				name = name + n + " ";
			}
			appliesTo.add(StringValue.build(name.trim()));
		}
		if (!appliesTo.isEmpty()) {
			pairList.add(Pair.build("appliesTo", ArrayValue.build(appliesTo)));
		}

		return ObjectValue.build(pairList);
	}

	// This function displays the non-qualified property name
	public Value buildProperty(Property p) {
		ArrayList<Pair> pairList = new ArrayList<Pair>();

		pairList.add(Pair.build("name", p.getName()));
		pairList.add(Pair.build("kind", "Property"));
		pairList.add(Pair.build("inherit", p.isInherit() ? "true" : "false"));
		pairList.add(Pair.build("propertyType", doSwitch(p.getPropertyType())));
		ArrayList<Value> appliesTo = new ArrayList<Value>();
		for (MetaclassReference ref : p.getAppliesToMetaclasses()) {
			String name = "";
			for (String n : ref.getMetaclassNames()) {
				name = name + n + " ";
			}
			appliesTo.add(StringValue.build(name.trim()));
		}
		if (!appliesTo.isEmpty()) {
			pairList.add(Pair.build("appliesTo", ArrayValue.build(appliesTo)));
		}

		return ObjectValue.build(pairList);
	}

	@Override
	public Value casePropertyConstant(PropertyConstant p) {
		ArrayList<Pair> pairList = new ArrayList<Pair>();
		pairList.add(Pair.build("name", p.getName()));
		pairList.add(Pair.build("kind", "PropertyConstant"));
		pairList.add(Pair.build("propertyType", doSwitch(p.getPropertyType())));
		pairList.add(Pair.build("value", genPropertyExpression(p.getConstantValue())));
		return ObjectValue.build(pairList);
	}

	public Value buildPropertyType(PropertyType p) {
		ArrayList<Pair> pairList = new ArrayList<Pair>();
		pairList.add(Pair.build("name", p.getName()));
		pairList.add(Pair.build("kind", "PropertyType"));
		pairList.add(Pair.build("type", doSwitch(p)));
		return ObjectValue.build(pairList);
	}

	@Override
	public Value caseEnumerationType(EnumerationType et) {
		ArrayList<Pair> pairList = new ArrayList<Pair>();
		pairList.add(Pair.build("kind", "EnumerationType"));
		ArrayList<Value> enumList = new ArrayList<Value>();
		for (org.osate.aadl2.EnumerationLiteral l : et.getOwnedLiterals()) {
			enumList.add(StringValue.build(l.getName()));
		}
		pairList.add(Pair.build("values", ArrayValue.build(enumList)));
		return ObjectValue.build(pairList);
	}

	@Override
	public Value caseListType(ListType lt) {
		ArrayList<Pair> pairList = new ArrayList<Pair>();
		pairList.add(Pair.build("kind", "ListType"));
		PropertyType pt = lt.getOwnedElementType();
		if (pt == null) {
			pt = lt.getReferencedElementType();
		}
		pairList.add(Pair.build("elementType", doSwitch(pt)));

		return ObjectValue.build(pairList);
	}

	@Override
	public Value caseRangeType(RangeType rt) {
		ArrayList<Pair> pairList = new ArrayList<Pair>();
		pairList.add(Pair.build("kind", "RangeType"));
		NumberType nt = rt.getOwnedNumberType();
		if (nt == null) {
			nt = rt.getReferencedNumberType();
		}
		pairList.add(Pair.build("numberType", doSwitch(nt)));
		return ObjectValue.build(pairList);
	}

	@Override
	public Value caseClassifierType(ClassifierType ct) {
		ArrayList<Pair> pairList = new ArrayList<Pair>();
		pairList.add(Pair.build("kind", "ClassifierType"));
		ArrayList<Value> classifierReferences = new ArrayList<Value>();
		for (MetaclassReference mr : ct.getClassifierReferences()) {
			String name = "";
			for (String n : mr.getMetaclassNames()) {
				name = name + n + " ";
			}
			classifierReferences.add(StringValue.build(name.trim()));
		}
		pairList.add(Pair.build("name", ArrayValue.build(classifierReferences)));
		return ObjectValue.build(pairList);
	}

	@Override
	public Value caseAadlString(AadlString s) {
		ArrayList<Pair> pairList = new ArrayList<Pair>();
		pairList.add(Pair.build("kind", "AadlString"));
		return ObjectValue.build(pairList);
	}

	@Override
	public Value caseAadlInteger(AadlInteger i) {
		ArrayList<Pair> pairList = new ArrayList<Pair>();
		pairList.add(Pair.build("kind", "AadlInteger"));
		return ObjectValue.build(pairList);
	}

	@Override
	public Value caseAadlReal(AadlReal r) {
		ArrayList<Pair> pairList = new ArrayList<Pair>();
		pairList.add(Pair.build("kind", "AadlReal"));
		return ObjectValue.build(pairList);
	}

	@Override
	public Value casePropertyAssociation(PropertyAssociation pa) {
		ArrayList<Pair> pairList = new ArrayList<Pair>();
		pairList.add(Pair.build("name", pa.getProperty().getQualifiedName()));
		pairList.add(Pair.build("kind", "PropertyAssociation"));

		// Seems like the list always has exactly one element
		ModalPropertyValue v = pa.getOwnedValues().get(0);
		pairList.add(Pair.build("value", doSwitch(v)));

		ArrayList<Value> appliesTo = new ArrayList<Value>();
		for (ContainedNamedElement ne : pa.getAppliesTos()) {
			appliesTo.add(StringValue.build(ne.getPath().getNamedElement().getName()));
		}
		if (!appliesTo.isEmpty()) {
			pairList.add(Pair.build("appliesTo", ArrayValue.build(appliesTo)));
		}

		return ObjectValue.build(pairList);
	}

	private static String getName(ConnectedElement ce) {
		String prefix = "";
		if (ce.getContext() != null) {
			prefix = ce.getContext().getName() + ".";
		}
		return prefix + ce.getConnectionEnd().getName();
	}

	@Override
	public Value defaultCase(EObject eo) {
		System.out.println(eo.toString());
		return StringValue.build("UNKNOWN");
	}

	private boolean isEmptyValue(Value v) {
		return v.toString().replace("{", "").replace("}", "").replace("[", "").replace("]", "").replace("\"", "").trim()
				.isEmpty();
	}
}
