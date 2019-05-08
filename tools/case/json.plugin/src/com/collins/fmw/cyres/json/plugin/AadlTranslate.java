package com.collins.fmw.cyres.json.plugin;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;
import org.osate.aadl2.AadlInteger;
import org.osate.aadl2.AadlPackage;
import org.osate.aadl2.AadlReal;
import org.osate.aadl2.AadlString;
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
import org.osate.aadl2.ImplementationExtension;
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
import org.osate.aadl2.TypeExtension;
import org.osate.aadl2.util.Aadl2Switch;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class AadlTranslate extends Aadl2Switch<JsonElement> {

	AgreeTranslate agreeTranslate = new AgreeTranslate();

	@Override
	public JsonObject casePackageRename(PackageRename packageRename) {
		JsonObject result = new JsonObject();
		result.add("package", new JsonPrimitive(packageRename.getRenamedPackage().getQualifiedName()));
		if (packageRename.isRenameAll()) {
			result.add("rename", new JsonPrimitive("all"));
		} else {
			result.add("rename", new JsonPrimitive(packageRename.getName()));
		}
		return result;
	}

	@Override
	public JsonObject caseAadlPackage(AadlPackage pkg) {
		JsonObject result = new JsonObject();
		result.add("name", new JsonPrimitive(pkg.getQualifiedName()));
		result.add("kind", new JsonPrimitive("AadlPackage"));

		// Build Package Sections
		JsonElement v = buildPackageSection(pkg.getOwnedPublicSection());
		if (!isEmptyValue(v)) {
			result.add("public", v);
		}
		v = buildPackageSection(pkg.getOwnedPrivateSection());
		if (!isEmptyValue(v)) {
			result.add("private", v);
		}

		return result;
	}

	public JsonObject buildPackageSection(PackageSection packageSection) {

		JsonObject result = new JsonObject();

		if (packageSection == null) {
			return result;
		}

		// Get imported models
		JsonArray modelUnits = new JsonArray();
		for (ModelUnit modelUnit : packageSection.getImportedUnits()) {
			modelUnits.add(new JsonPrimitive(modelUnit.getName()));
		}
		if (modelUnits.size() > 0) {
			result.add("with", modelUnits);
		}

		// Get package renames
		JsonArray packageRenames = new JsonArray();
		for (PackageRename packageRename : EcoreUtil2.getAllContentsOfType(packageSection, PackageRename.class)) {
			packageRenames.add(doSwitch(packageRename));
		}
		if (packageRenames.size() > 0) {
			result.add("packageRenames", packageRenames);
		}

		// Get components
		JsonArray components = new JsonArray();
		for (Classifier classifier : EcoreUtil2.getAllContentsOfType(packageSection, Classifier.class)) {
			components.add(doSwitch(classifier));
		}
		if (components.size() > 0) {
			result.add("components", components);
		}

		// Get annex libraries
		JsonArray annexes = new JsonArray();
		for (AnnexLibrary annexLib : packageSection.getOwnedAnnexLibraries()) {
			annexes.add(doSwitch(annexLib));
		}
		if (annexes.size() > 0) {
			result.add("annexes", annexes);
		}

		return result;
	}

	@Override
	public JsonElement casePropertySet(PropertySet ps) {
		JsonObject result = new JsonObject();
		result.add("name", new JsonPrimitive(ps.getName()));
		result.add("kind", new JsonPrimitive("PropertySet"));

		JsonArray properties = new JsonArray();
		for (Property p : ps.getOwnedProperties()) {
			properties.add(buildProperty(p));
		}
		if (properties.size() > 0) {
			result.add("properties", properties);
		}
		JsonArray propertyConstants = new JsonArray();
		for (PropertyConstant p : ps.getOwnedPropertyConstants()) {
			propertyConstants.add(doSwitch(p));
		}
		if (propertyConstants.size() > 0) {
			result.add("propertyConstants", propertyConstants);
		}
		JsonArray propertyTypes = new JsonArray();
		for (PropertyType p : ps.getOwnedPropertyTypes()) {
			propertyTypes.add(buildPropertyType(p));
		}
		if (propertyTypes.size() > 0) {
			result.add("propertyTypes", propertyTypes);
		}
		return result;
	}

	@Override
	public JsonElement caseComponentType(ComponentType ct) {
		JsonObject result = new JsonObject();
		result.add("name", new JsonPrimitive(ct.getQualifiedName()));
		result.add("localName", new JsonPrimitive(ct.getName()));
		result.add("kind", new JsonPrimitive("ComponentType"));
		result.add("category", new JsonPrimitive(ct.getCategory().getName()));
		TypeExtension te = ct.getOwnedExtension();
		if (te != null) {
			result.add("extends", doSwitch(te.getExtended()));
		}

		JsonArray features = new JsonArray();
		for (Feature feature : ct.getOwnedFeatures()) {
			features.add(doSwitch(feature));
		}
		if (features.size() > 0) {
			result.add("features", features);
		}

		JsonArray properties = new JsonArray();
		for (PropertyAssociation pa : ct.getOwnedPropertyAssociations()) {
			properties.add(doSwitch(pa));
		}
		if (properties.size() > 0) {
			result.add("properties", properties);
		}

		// add annex subclauses
		JsonArray annexes = new JsonArray();
		for (AnnexSubclause annex : ct.getOwnedAnnexSubclauses()) {
			annexes.add(doSwitch(annex));
		}
		if (annexes.size() > 0) {
			result.add("annexes", annexes);
		}

		return result;
	}

	@Override
	public JsonElement caseComponentImplementation(ComponentImplementation ci) {
		JsonObject result = new JsonObject();
		AadlPackage pkg = (AadlPackage) Aadl2Json.getContainingModelUnit(ci);
		result.add("packageName", new JsonPrimitive(pkg.getName()));
		result.add("name", new JsonPrimitive(ci.getQualifiedName()));
		result.add("localName", new JsonPrimitive(ci.getName()));
		result.add("kind", new JsonPrimitive("ComponentImplementation"));
		result.add("category", new JsonPrimitive(ci.getCategory().getName()));
		ImplementationExtension ie = ci.getOwnedExtension();
		if (ie != null) {
			result.add("extends", doSwitch(ie.getExtended()));
		}

		JsonArray subcomponents = new JsonArray();
		for (Subcomponent sc : ci.getOwnedSubcomponents()) {
			subcomponents.add(doSwitch(sc));
		}
		if (subcomponents.size() > 0) {
			result.add("subcomponents", subcomponents);
		}

		JsonArray connections = new JsonArray();
		for (Connection c : ci.getOwnedConnections()) {
			connections.add(doSwitch(c));
		}
		if (connections.size() > 0) {
			result.add("connections", connections);
		}

		JsonArray properties = new JsonArray();
		for (PropertyAssociation pa : ci.getOwnedPropertyAssociations()) {
			properties.add(doSwitch(pa));
		}
		if (properties.size() > 0) {
			result.add("properties", properties);
		}

		// add annex subclauses
		JsonArray annexes = new JsonArray();
		for (AnnexSubclause annex : ci.getOwnedAnnexSubclauses()) {
			annexes.add(doSwitch(annex));
		}
		if (annexes.size() > 0) {
			result.add("annexes", annexes);
		}

		return result;
	}

	@Override
	public JsonElement caseConnection(Connection c) {
		JsonObject result = new JsonObject();

		result.add("name", new JsonPrimitive(c.getName()));

		String kind = "";
		if (c instanceof PortConnection) {
			kind = "port";
		} else if (c instanceof AccessConnection) {
			kind = ((AccessConnection) c).getAccessCategory().getName() + "Access";
		}
		if (!kind.isEmpty()) {
			result.add("kind", new JsonPrimitive(kind));
		}

		result.add("source", new JsonPrimitive(getName(c.getSource())));
		result.add("destination", new JsonPrimitive(getName(c.getDestination())));
		result.add("bidirectional", new JsonPrimitive(c.isBidirectional()));

		return result;
	}

	@Override
	public JsonElement caseAnnexLibrary(AnnexLibrary al) {

		JsonObject result = new JsonObject();
		result.add("name", new JsonPrimitive(al.getName()));
		result.add("kind", new JsonPrimitive("AnnexLibrary"));
		if (al.getName().equalsIgnoreCase("agree")) {
			// AGREE annex will be parsed
			result.add("parsedAnnexLibrary", agreeTranslate.genAnnexLibrary(al));
		} else {
			DefaultAnnexLibrary defaultAnnexLib = (DefaultAnnexLibrary) al;
			result.add("sourceText", new JsonPrimitive(defaultAnnexLib.getSourceText().replace("\"", "\\\"")));
		}
		return result;
	}

	@Override
	public JsonElement caseAnnexSubclause(AnnexSubclause as) {

		JsonObject result = new JsonObject();
		result.add("name", new JsonPrimitive(as.getName()));
		result.add("kind", new JsonPrimitive("AnnexSubclause"));
		if (as.getName().equalsIgnoreCase("agree")) {
			// AGREE annex will be parsed
			result.add("parsedAnnexSubclause", agreeTranslate.genAnnexSubclause(as));
		} else {
			DefaultAnnexSubclause defaultAnnexSubclause = (DefaultAnnexSubclause) as;
			result.add("sourceText", new JsonPrimitive(defaultAnnexSubclause.getSourceText().replace("\"", "\\\"")));
		}
		return result;
	}

	/* Begin: Subcomponents */
	@Override
	public JsonElement caseSubcomponent(Subcomponent sc) {
		JsonObject result = new JsonObject();
		result.add("name", new JsonPrimitive(sc.getName()));
		result.add("kind", new JsonPrimitive("Subcomponent"));
		result.add("category", new JsonPrimitive(sc.getCategory().getName()));
		ComponentClassifier c = sc.getClassifier();
		if (c == null) {
			result.add("classifier", JsonNull.INSTANCE);
		} else {
			result.add("classifier", new JsonPrimitive(sc.getClassifier().getQualifiedName()));
		}
		return result;
	}

	/* Begin: Features */

	@Override
	public JsonElement caseDataPort(DataPort port) {
		return buildPort(port.getName(), "DataPort", port.getClassifier(), port.isIn(), port.isOut());
	}

	@Override
	public JsonElement caseEventDataPort(EventDataPort port) {
		return buildPort(port.getName(), "EventDataPort", port.getClassifier(), port.isIn(), port.isOut());
	}

	@Override
	public JsonElement caseEventPort(EventPort port) {
		return buildPort(port.getName(), "EventPort", port.getClassifier(), port.isIn(), port.isOut());
	}

	@Override
	public JsonElement caseBusAccess(BusAccess access) {

		JsonObject result = new JsonObject();
		result.add("name", new JsonPrimitive(access.getName()));
		result.add("kind", new JsonPrimitive("BusAccess"));
		result.add("accessType", new JsonPrimitive(access.getKind().getName()));

		BusFeatureClassifier classifier = access.getBusFeatureClassifier();
		String bus = "";
		if (classifier instanceof BusType) {
			bus = ((BusType) classifier).getQualifiedName();
		} else if (classifier instanceof BusImplementation) {
			bus = ((BusImplementation) classifier).getQualifiedName();
		}
		if (!bus.isEmpty()) {
			result.add("bus", new JsonPrimitive(bus));
		}

		return result;
	}

	private JsonElement buildPort(String name, String kind, Classifier classifier, boolean in, boolean out) {

		JsonObject result = new JsonObject();
		result.add("name", new JsonPrimitive(name));
		result.add("kind", new JsonPrimitive(kind));
		if (classifier == null) {
			result.add("classifier", JsonNull.INSTANCE);
		} else {
			result.add("classifier", new JsonPrimitive(classifier.getQualifiedName()));
		}
		result.add("direction", new JsonPrimitive(getInOutString(in, out)));

		return result;
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

	private JsonElement getEnumerationLiteral(EnumerationLiteral enumLit) {
		return new JsonPrimitive(enumLit.getName().toString());
	}

	private JsonElement getListValue(ListValue lv) {
		JsonArray vsJson = new JsonArray();
		for (PropertyExpression pe : lv.getOwnedListElements()) {
			vsJson.add(genPropertyExpression(pe));
		}
		return vsJson;
	}

	private JsonElement genNamedValue(NamedValue v) {
		if (v.getNamedValue() instanceof EnumerationLiteral) {
			return getEnumerationLiteral((EnumerationLiteral) v.getNamedValue());

		} else if (v.getNamedValue() instanceof PropertyConstant) {
			JsonObject o = new JsonObject();
			o.add("kind", new JsonPrimitive("PropertyConstant"));
			o.add("name", new JsonPrimitive(((PropertyConstant) v.getNamedValue()).getName()));
			return o;

		}

		return new JsonPrimitive("new_case/genNamedValue/" + v.getNamedValue());
	}

	private JsonElement genRangeValue(RangeValue v) {

		PropertyExpression pMin = v.getMinimum();
		PropertyExpression pMax = v.getMaximum();

		JsonObject result = new JsonObject();
		result.add("kind", new JsonPrimitive("RangeValue"));
		result.add("min", genPropertyExpression(pMin));
		result.add("max", genPropertyExpression(pMax));

		return result;

	}

	private JsonElement genPropertyExpression(PropertyExpression v) {
		if (v instanceof NamedValue) {
			return genNamedValue((NamedValue) v);

		} else if (v instanceof ListValue) {
			return getListValue((ListValue) v);

		} else if (v instanceof StringLiteral) {
			return new JsonPrimitive(((StringLiteral) v).getValue());

		} else if (v instanceof IntegerLiteral) {
			return new JsonPrimitive((((IntegerLiteral) v).getValue()));

		} else if (v instanceof ReferenceValue) {
			return new JsonPrimitive(((ReferenceValue) v).getPath().getNamedElement().getName());

		} else if (v instanceof RangeValue) {
			return genRangeValue((RangeValue) v);

		} else if (v instanceof ClassifierValue) {
			return new JsonPrimitive(((ClassifierValue) v).getClassifier().getQualifiedName());

		} else if (v instanceof BooleanLiteral) {
			return new JsonPrimitive(((BooleanLiteral) v).getValue());

		} else if (v instanceof RealLiteral) {
			return new JsonPrimitive(((RealLiteral) v).getValue());

		} else {
			return new JsonPrimitive("new_case/genRangeValue/" + v.toString());

		}

	}

	@Override
	public JsonElement caseModalPropertyValue(ModalPropertyValue v) {
		return genPropertyExpression(v.getOwnedValue());
	}

	@Override
	public JsonElement caseProperty(Property p) {
		JsonObject result = new JsonObject();

		result.add("name", new JsonPrimitive(p.getQualifiedName()));
		result.add("kind", new JsonPrimitive("Property"));
		result.add("inherit", new JsonPrimitive(p.isInherit()));
		result.add("propertyType", doSwitch(p.getPropertyType()));
		JsonArray appliesTo = new JsonArray();
		for (MetaclassReference ref : p.getAppliesToMetaclasses()) {
			String name = "";
			for (String n : ref.getMetaclassNames()) {
				name = name + n + " ";
			}
			appliesTo.add(new JsonPrimitive(name.trim()));
		}
		if (appliesTo.size() > 0) {
			result.add("appliesTo", appliesTo);
		}

		return result;
	}

	// This function displays the non-qualified property name
	public JsonElement buildProperty(Property p) {
		JsonObject result = new JsonObject();

//		result.add("name", p.getName()));
		result.add("name", new JsonPrimitive(p.getQualifiedName()));
		result.add("kind", new JsonPrimitive("Property"));
		result.add("inherit", new JsonPrimitive(p.isInherit()));
		result.add("propertyType", doSwitch(p.getPropertyType()));
		JsonArray appliesTo = new JsonArray();
		for (MetaclassReference ref : p.getAppliesToMetaclasses()) {
			String name = "";
			for (String n : ref.getMetaclassNames()) {
				name = name + n + " ";
			}
			appliesTo.add(new JsonPrimitive(name.trim()));
		}
		if (appliesTo.size() > 0) {
			result.add("appliesTo", appliesTo);
		}

		return result;
	}

	@Override
	public JsonElement casePropertyConstant(PropertyConstant p) {
		JsonObject result = new JsonObject();
//		result.add("name", p.getName()));
		result.add("name", new JsonPrimitive(p.getQualifiedName()));
		result.add("kind", new JsonPrimitive("PropertyConstant"));
		result.add("propertyType", doSwitch(p.getPropertyType()));
		result.add("value", genPropertyExpression(p.getConstantValue()));
		return result;
	}

	public JsonElement buildPropertyType(PropertyType p) {
		JsonObject result = new JsonObject();
//		result.add("name", p.getName()));
		result.add("name", new JsonPrimitive(p.getQualifiedName()));
		result.add("kind", new JsonPrimitive("PropertyType"));
		result.add("type", doSwitch(p));
		return result;
	}

	@Override
	public JsonElement caseEnumerationType(EnumerationType et) {
		JsonObject result = new JsonObject();
		result.add("kind", new JsonPrimitive("EnumerationType"));
		JsonArray enumList = new JsonArray();
		for (org.osate.aadl2.EnumerationLiteral l : et.getOwnedLiterals()) {
			enumList.add(new JsonPrimitive(l.getName()));
		}
		result.add("values", enumList);
		return result;
	}

	@Override
	public JsonElement caseListType(ListType lt) {
		JsonObject result = new JsonObject();
		result.add("kind", new JsonPrimitive("ListType"));
		PropertyType pt = lt.getOwnedElementType();
		if (pt == null) {
			pt = lt.getReferencedElementType();
		}
		result.add("elementType", doSwitch(pt));

		return result;
	}

	@Override
	public JsonElement caseRangeType(RangeType rt) {
		JsonObject result = new JsonObject();
		result.add("kind", new JsonPrimitive("RangeType"));
		NumberType nt = rt.getOwnedNumberType();
		if (nt == null) {
			nt = rt.getReferencedNumberType();
		}
		result.add("numberType", doSwitch(nt));
		return result;
	}

	@Override
	public JsonElement caseClassifierType(ClassifierType ct) {
		JsonObject result = new JsonObject();
		result.add("kind", new JsonPrimitive("ClassifierType"));
		JsonArray classifierReferences = new JsonArray();
		for (MetaclassReference mr : ct.getClassifierReferences()) {
			String name = "";
			for (String n : mr.getMetaclassNames()) {
				name = name + n + " ";
			}
			classifierReferences.add(new JsonPrimitive(name.trim()));
		}
		result.add("name", classifierReferences);
		return result;
	}

	@Override
	public JsonElement caseAadlString(AadlString s) {
		JsonObject result = new JsonObject();
		result.add("kind", new JsonPrimitive("AadlString"));
		return result;
	}

	@Override
	public JsonElement caseAadlInteger(AadlInteger i) {
		JsonObject result = new JsonObject();
		result.add("kind", new JsonPrimitive("AadlInteger"));
		return result;
	}

	@Override
	public JsonElement caseAadlReal(AadlReal r) {
		JsonObject result = new JsonObject();
		result.add("kind", new JsonPrimitive("AadlReal"));
		return result;
	}

	@Override
	public JsonElement casePropertyAssociation(PropertyAssociation pa) {
		JsonObject result = new JsonObject();
		result.add("name", new JsonPrimitive(pa.getProperty().getQualifiedName()));
		result.add("kind", new JsonPrimitive("PropertyAssociation"));

		// Seems like the list always has exactly one element
		ModalPropertyValue v = pa.getOwnedValues().get(0);
		result.add("value", doSwitch(v));

		JsonArray appliesTo = new JsonArray();
		for (ContainedNamedElement ne : pa.getAppliesTos()) {
			appliesTo.add(new JsonPrimitive(ne.getPath().getNamedElement().getName()));
		}
		if (appliesTo.size() > 0) {
			result.add("appliesTo", appliesTo);
		}

		return result;
	}

	private static String getName(ConnectedElement ce) {
		String prefix = "";
		if (ce.getContext() != null) {
			prefix = ce.getContext().getName() + ".";
		}
		return prefix + ce.getConnectionEnd().getName();
	}

	@Override
	public JsonElement defaultCase(EObject eo) {
		System.out.println(eo.toString());
		return new JsonPrimitive("UNKNOWN");
	}

	private boolean isEmptyValue(JsonElement v) {
		if (v.isJsonNull()) {
			return true;
		} else if (v.isJsonArray()) {
			return ((JsonArray) v).size() == 0;
		} else if (v.isJsonObject()) {
			return ((JsonObject) v).size() == 0;
		} else if (v.isJsonPrimitive()) {
			JsonPrimitive p = (JsonPrimitive) v;
			if (p.isString()) {
				return p.getAsString().isEmpty();
			}
		}
		return false;
	}
}
