package com.collins.fmw.cyres.json.plugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.utils.EditorUtils;
import org.osate.aadl2.AadlPackage;
import org.osate.aadl2.ModelUnit;
import org.osate.aadl2.NamedElement;
import org.osate.aadl2.PropertySet;
import org.osate.aadl2.modelsupport.util.AadlUtil;

import com.collins.fmw.cyres.json.plugin.AadlTranslate.AgreePrintOption;
import com.collins.fmw.cyres.util.plugin.Filesystem;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Aadl2Json {

	static private URI makeJsonFile(XtextEditor state) throws CoreException, IOException {
		XtextResource resource = state.getDocument().readOnly(r -> r);

		URI dan = resource.getURI();
		URI folder = dan.trimSegments(1);
		String base = Filesystem.getBase(dan);

		URI writeFolder = Filesystem.createFolder(folder, new String[] { "json-generated" });
		URI json = writeFolder.appendSegment(base).appendFileExtension("json");

		return json;
	}

	static public URI makeJsonFile(URI dan) throws CoreException, IOException {

		URI folder = dan.trimSegments(1);
		String base = Filesystem.getBase(dan);

		URI writeFolder = Filesystem.createFolder(folder, new String[] { "json-generated" });
		URI json = writeFolder.appendSegment(base).appendFileExtension("json");

		return json;
	}

	static private void printJson(URI json, String whatToPrint) throws CoreException, IOException {

		IFile print = Filesystem.getFile(json);
		Filesystem.writeFile(print, whatToPrint.getBytes());
	}

	static public URI createJson() throws Exception {
		return createJson(null, AgreePrintOption.PARSE);
	}

	static public URI createJson(JsonObject header, AgreePrintOption agreePrintOption) throws Exception {

		XtextEditor xtextEditor = EditorUtils.getActiveXtextEditor();

		if (xtextEditor == null) {
			throw new Exception("An AADL editor must be active in order to generate JSON.");
		}

		EObject original = xtextEditor.getDocument().readOnly(resource -> resource.getContents().get(0));

		JsonElement je = toJson(original, agreePrintOption);

		if (header != null) {
			header.add("modelUnits", je);
			je = header;
		}

		Gson gson = new GsonBuilder().serializeNulls().disableHtmlEscaping().setPrettyPrinting().create();

		URI jsonURI = null;
		try {

			jsonURI = makeJsonFile(xtextEditor);
			printJson(jsonURI, gson.toJson(je));

		} catch (CoreException | IOException e) {
			System.err.println("Trouble writing Json representation to filesystem.");
			e.printStackTrace();
		}

		return jsonURI;
	}

	public static ModelUnit getContainingModelUnit(NamedElement ne) {

		EObject o = ne.eContainer();
		while (o != null && !(o instanceof ModelUnit)) {
			o = o.eContainer();
		}

		return (ModelUnit) o;

	}

	public static JsonElement toJson(EObject o, AgreePrintOption agreePrintOption) {

		AadlTranslate aadlTranslate = new AadlTranslate(agreePrintOption);

//		AgreeTranslate agreeTranslate = new AgreeTranslate();

		if (o instanceof ModelUnit) {
			ModelUnit model = (ModelUnit) o;

			// Get (recursively) the set of models referenced in this file
			List<ModelUnit> modelUnits = new ArrayList<>();
			getModelDependencies(model, modelUnits);
			// Add AADL Predeclared property sets
			getPredeclaredPropertySets(modelUnits);

			JsonArray modelsJson = new JsonArray();


			Iterator<ModelUnit> i = modelUnits.iterator();

			while (i.hasNext()) {

				ModelUnit m = i.next();
				if (m instanceof AadlPackage || m instanceof PropertySet) {
					modelsJson.add(aadlTranslate.doSwitch(m));
				}

			}

			return modelsJson;


//		} else if (o instanceof ComponentImplementation) {
//
//			ComponentImplementation ci = (ComponentImplementation) o;
//
//			List<ComponentClassifier> components = new ArrayList<>(gatherReferencedComponents(ci).values());
//
//			JsonArray componentsJson = new JsonArray();
//			for (ComponentClassifier component : components) {
//				componentsJson.add(aadlTranslate.doSwitch(component));
//			}
//
//			List<Abstraction> abstractions = gatherPackageAbstractions(components);
//			JsonArray abstractionsJson = new JsonArray();
//			for (Abstraction abstraction : abstractions) {
//				abstractionsJson.add(agreeTranslate.genSpecStatement((SpecStatement) abstraction));
//			}
//
//			List<Property> properties = gatherReferencedProperties(components);
//			JsonArray propertiesJson = new JsonArray();
//			for (Property property : properties) {
//				propertiesJson.add(aadlTranslate.doSwitch(property));
//			}
//
//			JsonObject jo = new JsonObject();
//			jo.add("mainName", new JsonPrimitive(ci.getQualifiedName()));
//			jo.add("timestamp", new JsonPrimitive(System.currentTimeMillis()));
//			jo.add("properties", propertiesJson);
//			jo.add("abstractions", abstractionsJson);
//			jo.add("components", componentsJson);
//
//			return jo;

		} else {
			throw new RuntimeException();
		}
	}

//	private static List<Property> gatherReferencedProperties(List<ComponentClassifier> components) {
//		Map<String, Property> propertyMap = new HashMap<>();
//
//		for (ComponentClassifier c : components) {
//
//			List<PropertyAssociation> propAssocs = c.getOwnedPropertyAssociations();
//			for (PropertyAssociation propAssoc : propAssocs) {
//				Property prop = propAssoc.getProperty();
//				String name = prop.getQualifiedName();
//				propertyMap.put(name, prop);
//			}
//		}
//
//		return new ArrayList<>(propertyMap.values());
//	}
//
//
//	private static Map<String, ComponentClassifier> gatherReferencedComponents(ComponentClassifier c) {
//		Map<String, ComponentClassifier> compMap = new HashMap<>();
//
//		compMap.put(c.getQualifiedName(), c);
//
//		if (c instanceof ComponentImplementation) {
//			ComponentClassifier ct = (((ComponentImplementation) c).getType());
//			compMap.put(ct.getQualifiedName(), ct);
//
//			for (Subcomponent sub : ((ComponentImplementation) c).getAllSubcomponents()) {
//
//				Classifier subClassifier = sub.getClassifier();
//				compMap.putAll(gatherReferencedComponents((ComponentClassifier) subClassifier));
//
//			}
//
//		}
//
//
//		for (AnnexSubclause annex : AnnexUtil.getAllAnnexSubclauses(c,
//				AgreePackage.eINSTANCE.getAgreeContractSubclause())) {
//			AgreeContract contract = (AgreeContract) ((AgreeContractSubclause) annex).getContract();
//			for (SpecStatement spec : contract.getSpecs()) {
//
//				Map<String, ComponentClassifier> comps = extractComponentsFromSpec(spec);
//				compMap.putAll(comps);
//
//			}
//
//		}
//
//
//		return compMap;
//	}

//	private static Map<String, ComponentClassifier> extractComponentsFromSpec(SpecStatement spec) {
//		if (spec instanceof InitialStatement) {
//			return extractComponentsFromExpr(((InitialStatement) spec).getExpr());
//		} else if (spec instanceof ParamStatement) {
//			return extractComponentsFromExpr(((ParamStatement) spec).getExpr());
//		} else if (spec instanceof EqStatement) {
//			return extractComponentsFromExpr(((EqStatement) spec).getExpr());
//		} else if (spec instanceof AssumeStatement) {
//
//			if (((AssumeStatement) spec).getExpr() != null) {
//				return extractComponentsFromExpr(((AssumeStatement) spec).getExpr());
//			}
//
//			if (((AssumeStatement) spec).getPattern() != null) {
//				return extractComponentsFromPattern(((AssumeStatement) spec).getPattern());
//			}
//
//		} else if (spec instanceof GuaranteeStatement) {
//
//			if (((GuaranteeStatement) spec).getExpr() != null) {
//				return extractComponentsFromExpr(((GuaranteeStatement) spec).getExpr());
//			}
//
//			if (((GuaranteeStatement) spec).getPattern() != null) {
//				return extractComponentsFromPattern(((GuaranteeStatement) spec).getPattern());
//			}
//
//		} else if (spec instanceof LemmaStatement) {
//
//			if (((LemmaStatement) spec).getExpr() != null) {
//				return extractComponentsFromExpr(((LemmaStatement) spec).getExpr());
//			}
//
//			if (((LemmaStatement) spec).getPattern() != null) {
//				return extractComponentsFromPattern(((LemmaStatement) spec).getPattern());
//			}
//
//		} else if (spec instanceof AssertStatement) {
//
//			if (((AssertStatement) spec).getExpr() != null) {
//				return extractComponentsFromExpr(((AssertStatement) spec).getExpr());
//			}
//
//			if (((AssertStatement) spec).getPattern() != null) {
//				return extractComponentsFromPattern(((AssertStatement) spec).getPattern());
//			}
//
//		}
//
//		return new HashMap<>();
//	}
//
//	private static Map<String, ComponentClassifier> extractComponentsFromPattern(PatternStatement pattern) {
//		HashMap<String, ComponentClassifier> component = new HashMap<>();
//		if (pattern instanceof WhenHoldsStatement) {
//			component.putAll(extractComponentsFromExpr(((WhenHoldsStatement) pattern).getCondition()));
//			component.putAll(extractComponentsFromExpr(((WhenHoldsStatement) pattern).getEvent()));
//
//		} else if (pattern instanceof WhenOccursStatment) {
//			component.putAll(extractComponentsFromExpr(((WhenOccursStatment) pattern).getCondition()));
//			component.putAll(extractComponentsFromExpr(((WhenOccursStatment) pattern).getEvent()));
//			component.putAll(extractComponentsFromExpr(((WhenOccursStatment) pattern).getTimes()));
//
//		} else if (pattern instanceof WheneverOccursStatement) {
//			component.putAll(extractComponentsFromExpr(((WheneverOccursStatement) pattern).getCause()));
//			component.putAll(extractComponentsFromExpr(((WheneverOccursStatement) pattern).getEffect()));
//
//		} else if (pattern instanceof WheneverBecomesTrueStatement) {
//			component.putAll(extractComponentsFromExpr(((WheneverBecomesTrueStatement) pattern).getCause()));
//			component.putAll(extractComponentsFromExpr(((WheneverBecomesTrueStatement) pattern).getEffect()));
//
//		} else if (pattern instanceof WheneverHoldsStatement) {
//			component.putAll(extractComponentsFromExpr(((WheneverHoldsStatement) pattern).getCause()));
//			component.putAll(extractComponentsFromExpr(((WheneverHoldsStatement) pattern).getEffect()));
//
//		} else if (pattern instanceof WheneverImpliesStatement) {
//			component.putAll(extractComponentsFromExpr(((WheneverImpliesStatement) pattern).getCause()));
//			component.putAll(extractComponentsFromExpr(((WheneverImpliesStatement) pattern).getLhs()));
//			component.putAll(extractComponentsFromExpr(((WheneverImpliesStatement) pattern).getRhs()));
//
//		} else if (pattern instanceof PeriodicStatement) {
//			component.putAll(extractComponentsFromExpr(((PeriodicStatement) pattern).getEvent()));
//			component.putAll(extractComponentsFromExpr(((PeriodicStatement) pattern).getPeriod()));
//
//			if (((PeriodicStatement) pattern).getJitter() != null) {
//				component.putAll(extractComponentsFromExpr(((PeriodicStatement) pattern).getJitter()));
//			}
//		} else if (pattern instanceof SporadicStatement) {
//			component.putAll(extractComponentsFromExpr(((SporadicStatement) pattern).getEvent()));
//			component.putAll(extractComponentsFromExpr(((SporadicStatement) pattern).getIat()));
//
//			if (((SporadicStatement) pattern).getJitter() != null) {
//				component.putAll(extractComponentsFromExpr(((SporadicStatement) pattern).getJitter()));
//			}
//		}
//
//		return component;
//	}
//
//	private static Map<String, ComponentClassifier> extractComponentsFromExpr(Expr expr) {
//		HashMap<String, ComponentClassifier> component = new HashMap<>();
//		if (expr instanceof ForallExpr) {
//			component.putAll(extractComponentsFromExpr(((ForallExpr) expr).getArray()));
//			component.putAll(extractComponentsFromExpr(((ForallExpr) expr).getExpr()));
//		} else if (expr instanceof ExistsExpr) {
//			component.putAll(extractComponentsFromExpr(((ExistsExpr) expr).getArray()));
//			component.putAll(extractComponentsFromExpr(((ExistsExpr) expr).getExpr()));
//		} else if (expr instanceof FlatmapExpr) {
//			component.putAll(extractComponentsFromExpr(((FlatmapExpr) expr).getArray()));
//			component.putAll(extractComponentsFromExpr(((FlatmapExpr) expr).getExpr()));
//		} else if (expr instanceof FoldLeftExpr) {
//			component.putAll(extractComponentsFromExpr(((FoldLeftExpr) expr).getArray()));
//			component.putAll(extractComponentsFromExpr(((FoldLeftExpr) expr).getInitial()));
//			component.putAll(extractComponentsFromExpr(((FoldLeftExpr) expr).getExpr()));
//		} else if (expr instanceof FoldRightExpr) {
//			component.putAll(extractComponentsFromExpr(((FoldRightExpr) expr).getArray()));
//			component.putAll(extractComponentsFromExpr(((FoldRightExpr) expr).getInitial()));
//			component.putAll(extractComponentsFromExpr(((FoldRightExpr) expr).getExpr()));
//
//		} else if (expr instanceof BinaryExpr) {
//			component.putAll(extractComponentsFromExpr(((BinaryExpr) expr).getLeft()));
//			component.putAll(extractComponentsFromExpr(((BinaryExpr) expr).getRight()));
//
//		} else if (expr instanceof UnaryExpr) {
//			component.putAll(extractComponentsFromExpr(((UnaryExpr) expr).getExpr()));
//
//		} else if (expr instanceof IfThenElseExpr) {
//			component.putAll(extractComponentsFromExpr(((IfThenElseExpr) expr).getA()));
//			component.putAll(extractComponentsFromExpr(((IfThenElseExpr) expr).getB()));
//			component.putAll(extractComponentsFromExpr(((IfThenElseExpr) expr).getC()));
//
//		} else if (expr instanceof PrevExpr) {
//			component.putAll(extractComponentsFromExpr(((PrevExpr) expr).getDelay()));
//			component.putAll(extractComponentsFromExpr(((PrevExpr) expr).getInit()));
//		} else if (expr instanceof ArrayUpdateExpr) {
//			component.putAll(extractComponentsFromExpr(((ArrayUpdateExpr) expr).getArray()));
//			for (Expr indexExpr : ((ArrayUpdateExpr) expr).getIndices()) {
//				component.putAll(extractComponentsFromExpr(indexExpr));
//			}
//
//			for (Expr valExpr : ((ArrayUpdateExpr) expr).getValueExprs()) {
//				component.putAll(extractComponentsFromExpr(valExpr));
//			}
//		} else if (expr instanceof RecordUpdateExpr) {
//			component.putAll(extractComponentsFromExpr(((RecordUpdateExpr) expr).getRecord()));
//			component.putAll(extractComponentsFromExpr(((RecordUpdateExpr) expr).getExpr()));
//		} else if (expr instanceof ArraySubExpr) {
//
//			component.putAll(extractComponentsFromExpr(((RecordUpdateExpr) expr).getExpr()));
//			component.putAll(extractComponentsFromExpr(((ArraySubExpr) expr).getIndex()));
//		} else if (expr instanceof TagExpr) {
//			component.putAll(extractComponentsFromExpr(((TagExpr) expr).getStem()));
//		} else if (expr instanceof SelectionExpr) {
//			component.putAll(extractComponentsFromExpr(((SelectionExpr) expr).getTarget()));
//
//		} else if (expr instanceof IndicesExpr) {
//			component.putAll(extractComponentsFromExpr(((IndicesExpr) expr).getArray()));
//
//		} else if (expr instanceof CallExpr) {
//			for (Expr argExpr : ((CallExpr) expr).getArgs()) {
//				component.putAll(extractComponentsFromExpr((argExpr)));
//			}
//		} else if (expr instanceof PreExpr) {
//			component.putAll(extractComponentsFromExpr(((PreExpr) expr).getExpr()));
//
//		} else if (expr instanceof LatchedExpr) {
//			component.putAll(extractComponentsFromExpr(((LatchedExpr) expr).getExpr()));
//
//		} else if (expr instanceof RecordLitExpr) {
//			for (Expr argExpr : ((RecordLitExpr) expr).getArgExpr()) {
//
//				component.putAll(extractComponentsFromExpr(argExpr));
//			}
//
//		} else if (expr instanceof ArrayLiteralExpr) {
//			for (Expr argExpr : ((ArrayLiteralExpr) expr).getElems()) {
//				component.putAll(extractComponentsFromExpr(argExpr));
//			}
//
//		} else if (expr instanceof FloorCast) {
//			component.putAll(extractComponentsFromExpr(((FloorCast) expr).getExpr()));
//
//		} else if (expr instanceof RealCast) {
//			component.putAll(extractComponentsFromExpr(((RealCast) expr).getExpr()));
//
//		} else if (expr instanceof GetPropertyExpr) {
//			ComponentRef cr = ((GetPropertyExpr) expr).getComponentRef();
//			if (cr instanceof DoubleDotRef) {
//				NamedElement ne = ((DoubleDotRef) cr).getElm();
//				if (ne instanceof ComponentClassifier) {
//					component.put(ne.getQualifiedName(), (ComponentClassifier) ne);
//				}
//			}
//
//		}
//
//		return component;
//	}

//	private static List<Abstraction> gatherPackageAbstractions(List<ComponentClassifier> componentList) {
//
//		Map<String, Abstraction> abstractionMap = new HashMap<>();
//
//		for (ComponentClassifier c : componentList) {
//
//			for (AnnexSubclause annex : AnnexUtil.getAllAnnexSubclauses(c,
//					AgreePackage.eINSTANCE.getAgreeContractSubclause())) {
//				AgreeContract contract = (AgreeContract) ((AgreeContractSubclause) annex).getContract();
//				for (SpecStatement spec : contract.getSpecs()) {
//
//					Map<String, Abstraction> abstractions = extractAbstractionsFromSpec(spec);
//					abstractionMap.putAll(abstractions);
//
//				}
//
//			}
//		}
//
//		return new ArrayList<>(abstractionMap.values());
//	}
//
//	private static Map<String, Abstraction> extractAbstractionsFromSpec(SpecStatement spec) {
//		if (spec instanceof InitialStatement) {
//			return extractAbstractionsFromExpr(((InitialStatement) spec).getExpr());
//		} else if (spec instanceof ParamStatement) {
//			return extractAbstractionsFromExpr(((ParamStatement) spec).getExpr());
//		} else if (spec instanceof EqStatement) {
//			return extractAbstractionsFromExpr(((EqStatement) spec).getExpr());
//		} else if (spec instanceof AssumeStatement) {
//
//			if (((AssumeStatement) spec).getExpr() != null) {
//				return extractAbstractionsFromExpr(((AssumeStatement) spec).getExpr());
//			}
//
//			if (((AssumeStatement) spec).getPattern() != null) {
//				return extractAbstractionsFromPattern(((AssumeStatement) spec).getPattern());
//			}
//
//		} else if (spec instanceof GuaranteeStatement) {
//
//			if (((GuaranteeStatement) spec).getExpr() != null) {
//				return extractAbstractionsFromExpr(((GuaranteeStatement) spec).getExpr());
//			}
//
//			if (((GuaranteeStatement) spec).getPattern() != null) {
//				return extractAbstractionsFromPattern(((GuaranteeStatement) spec).getPattern());
//			}
//
//		} else if (spec instanceof LemmaStatement) {
//
//			if (((LemmaStatement) spec).getExpr() != null) {
//				return extractAbstractionsFromExpr(((LemmaStatement) spec).getExpr());
//			}
//
//			if (((LemmaStatement) spec).getPattern() != null) {
//				return extractAbstractionsFromPattern(((LemmaStatement) spec).getPattern());
//			}
//
//		} else if (spec instanceof AssertStatement) {
//
//			if (((AssertStatement) spec).getExpr() != null) {
//				return extractAbstractionsFromExpr(((AssertStatement) spec).getExpr());
//			}
//
//			if (((AssertStatement) spec).getPattern() != null) {
//				return extractAbstractionsFromPattern(((AssertStatement) spec).getPattern());
//			}
//
//		}
//
//		return new HashMap<>();
//	}
//
//	private static Map<String, Abstraction> extractAbstractionsFromPattern(PatternStatement pattern) {
//
//		HashMap<String, Abstraction> abstractions = new HashMap<>();
//		if (pattern instanceof WhenHoldsStatement) {
//			abstractions.putAll(extractAbstractionsFromExpr(((WhenHoldsStatement) pattern).getCondition()));
//			abstractions.putAll(extractAbstractionsFromExpr(((WhenHoldsStatement) pattern).getEvent()));
//
//		} else if (pattern instanceof WhenOccursStatment) {
//			abstractions.putAll(extractAbstractionsFromExpr(((WhenOccursStatment) pattern).getCondition()));
//			abstractions.putAll(extractAbstractionsFromExpr(((WhenOccursStatment) pattern).getEvent()));
//			abstractions.putAll(extractAbstractionsFromExpr(((WhenOccursStatment) pattern).getTimes()));
//
//		} else if (pattern instanceof WheneverOccursStatement) {
//			abstractions.putAll(extractAbstractionsFromExpr(((WheneverOccursStatement) pattern).getCause()));
//			abstractions.putAll(extractAbstractionsFromExpr(((WheneverOccursStatement) pattern).getEffect()));
//
//		} else if (pattern instanceof WheneverBecomesTrueStatement) {
//			abstractions.putAll(extractAbstractionsFromExpr(((WheneverBecomesTrueStatement) pattern).getCause()));
//			abstractions.putAll(extractAbstractionsFromExpr(((WheneverBecomesTrueStatement) pattern).getEffect()));
//
//		} else if (pattern instanceof WheneverHoldsStatement) {
//			abstractions.putAll(extractAbstractionsFromExpr(((WheneverHoldsStatement) pattern).getCause()));
//			abstractions.putAll(extractAbstractionsFromExpr(((WheneverHoldsStatement) pattern).getEffect()));
//
//		} else if (pattern instanceof WheneverImpliesStatement) {
//			abstractions.putAll(extractAbstractionsFromExpr(((WheneverImpliesStatement) pattern).getCause()));
//			abstractions.putAll(extractAbstractionsFromExpr(((WheneverImpliesStatement) pattern).getLhs()));
//			abstractions.putAll(extractAbstractionsFromExpr(((WheneverImpliesStatement) pattern).getRhs()));
//
//		} else if (pattern instanceof PeriodicStatement) {
//			abstractions.putAll(extractAbstractionsFromExpr(((PeriodicStatement) pattern).getEvent()));
//			abstractions.putAll(extractAbstractionsFromExpr(((PeriodicStatement) pattern).getPeriod()));
//
//			if (((PeriodicStatement) pattern).getJitter() != null) {
//				abstractions.putAll(extractAbstractionsFromExpr(((PeriodicStatement) pattern).getJitter()));
//			}
//		} else if (pattern instanceof SporadicStatement) {
//			abstractions.putAll(extractAbstractionsFromExpr(((SporadicStatement) pattern).getEvent()));
//			abstractions.putAll(extractAbstractionsFromExpr(((SporadicStatement) pattern).getIat()));
//
//			if (((SporadicStatement) pattern).getJitter() != null) {
//				abstractions.putAll(extractAbstractionsFromExpr(((SporadicStatement) pattern).getJitter()));
//			}
//		}
//
//		return abstractions;
//
//	}
//
//	private static Map<String, Abstraction> extractAbstractionsFromExpr(Expr expr) {
//		HashMap<String, Abstraction> abstractions = new HashMap<>();
//		if (expr instanceof ForallExpr) {
//			abstractions.putAll(extractAbstractionsFromExpr(((ForallExpr) expr).getArray()));
//			abstractions.putAll(extractAbstractionsFromExpr(((ForallExpr) expr).getExpr()));
//		} else if (expr instanceof ExistsExpr) {
//			abstractions.putAll(extractAbstractionsFromExpr(((ExistsExpr) expr).getArray()));
//			abstractions.putAll(extractAbstractionsFromExpr(((ExistsExpr) expr).getExpr()));
//		} else if (expr instanceof FlatmapExpr) {
//			abstractions.putAll(extractAbstractionsFromExpr(((FlatmapExpr) expr).getArray()));
//			abstractions.putAll(extractAbstractionsFromExpr(((FlatmapExpr) expr).getExpr()));
//		} else if (expr instanceof FoldLeftExpr) {
//			abstractions.putAll(extractAbstractionsFromExpr(((FoldLeftExpr) expr).getArray()));
//			abstractions.putAll(extractAbstractionsFromExpr(((FoldLeftExpr) expr).getInitial()));
//			abstractions.putAll(extractAbstractionsFromExpr(((FoldLeftExpr) expr).getExpr()));
//		} else if (expr instanceof FoldRightExpr) {
//			abstractions.putAll(extractAbstractionsFromExpr(((FoldRightExpr) expr).getArray()));
//			abstractions.putAll(extractAbstractionsFromExpr(((FoldRightExpr) expr).getInitial()));
//			abstractions.putAll(extractAbstractionsFromExpr(((FoldRightExpr) expr).getExpr()));
//
//		} else if (expr instanceof BinaryExpr) {
//			abstractions.putAll(extractAbstractionsFromExpr(((BinaryExpr) expr).getLeft()));
//			abstractions.putAll(extractAbstractionsFromExpr(((BinaryExpr) expr).getRight()));
//
//		} else if (expr instanceof UnaryExpr) {
//			abstractions.putAll(extractAbstractionsFromExpr(((UnaryExpr) expr).getExpr()));
//
//		} else if (expr instanceof IfThenElseExpr) {
//			abstractions.putAll(extractAbstractionsFromExpr(((IfThenElseExpr) expr).getA()));
//			abstractions.putAll(extractAbstractionsFromExpr(((IfThenElseExpr) expr).getB()));
//			abstractions.putAll(extractAbstractionsFromExpr(((IfThenElseExpr) expr).getC()));
//
//		} else if (expr instanceof PrevExpr) {
//			abstractions.putAll(extractAbstractionsFromExpr(((PrevExpr) expr).getDelay()));
//			abstractions.putAll(extractAbstractionsFromExpr(((PrevExpr) expr).getInit()));
//		} else if (expr instanceof ArrayUpdateExpr) {
//			abstractions.putAll(extractAbstractionsFromExpr(((ArrayUpdateExpr) expr).getArray()));
//			for (Expr indexExpr : ((ArrayUpdateExpr) expr).getIndices()) {
//				abstractions.putAll(extractAbstractionsFromExpr(indexExpr));
//			}
//
//			for (Expr valExpr : ((ArrayUpdateExpr) expr).getValueExprs()) {
//				abstractions.putAll(extractAbstractionsFromExpr(valExpr));
//			}
//		} else if (expr instanceof RecordUpdateExpr) {
//			abstractions.putAll(extractAbstractionsFromExpr(((RecordUpdateExpr) expr).getRecord()));
//			abstractions.putAll(extractAbstractionsFromExpr(((RecordUpdateExpr) expr).getExpr()));
//		} else if (expr instanceof ArraySubExpr) {
//
//			abstractions.putAll(extractAbstractionsFromExpr(((RecordUpdateExpr) expr).getExpr()));
//			abstractions.putAll(extractAbstractionsFromExpr(((ArraySubExpr) expr).getIndex()));
//		} else if (expr instanceof TagExpr) {
//			abstractions.putAll(extractAbstractionsFromExpr(((TagExpr) expr).getStem()));
//		} else if (expr instanceof SelectionExpr) {
//			abstractions.putAll(extractAbstractionsFromExpr(((SelectionExpr) expr).getTarget()));
//
//
//		} else if (expr instanceof IndicesExpr) {
//			abstractions.putAll(extractAbstractionsFromExpr(((IndicesExpr) expr).getArray()));
//
//		} else if (expr instanceof CallExpr) {
//			NamedElement ne = ((CallExpr) expr).getRef().getElm();
//			if (ne instanceof FnDef | ne instanceof NodeDef | ne instanceof LinearizationDef) {
//				if (ne.getContainingClassifier() == null) {
//					AadlPackage pkg = (AadlPackage) Aadl2Json.getContainingModelUnit(ne);
//					String name = pkg.getName() + "::" + ne.getName();
//					abstractions.put(name, (Abstraction) ne);
//				}
//			}
//
//			for (Expr argExpr : ((CallExpr) expr).getArgs()) {
//				abstractions.putAll(extractAbstractionsFromExpr((argExpr)));
//			}
//		} else if (expr instanceof PreExpr) {
//			abstractions.putAll(extractAbstractionsFromExpr(((PreExpr) expr).getExpr()));
//
//		} else if (expr instanceof LatchedExpr) {
//			abstractions.putAll(extractAbstractionsFromExpr(((LatchedExpr) expr).getExpr()));
//
//		} else if (expr instanceof RecordLitExpr) {
//			for (Expr argExpr : ((RecordLitExpr) expr).getArgExpr()) {
//
//				abstractions.putAll(extractAbstractionsFromExpr(argExpr));
//			}
//
//		} else if (expr instanceof ArrayLiteralExpr) {
//			for (Expr argExpr : ((ArrayLiteralExpr) expr).getElems()) {
//				abstractions.putAll(extractAbstractionsFromExpr(argExpr));
//			}
//
//		} else if (expr instanceof FloorCast) {
//			abstractions.putAll(extractAbstractionsFromExpr(((FloorCast) expr).getExpr()));
//
//		} else if (expr instanceof RealCast) {
//			abstractions.putAll(extractAbstractionsFromExpr(((RealCast) expr).getExpr()));
//
//		}
//
//		return abstractions;
//	}

	static private void getModelDependencies(ModelUnit model, List<ModelUnit> modelUnits) {

		// Add the parent package if it's not there, otherwise return
		if (modelUnits.contains(model)) {
			return;
		} else {
			modelUnits.add(model);
		}

		if (model instanceof AadlPackage) {
			AadlPackage pkg = (AadlPackage) model;
			// Look at direct dependencies in private section
			if (pkg.getPrivateSection() != null) {
				for (ModelUnit mu : pkg.getPrivateSection().getImportedUnits()) {
					getModelDependencies(mu, modelUnits);
				}
			}

			// Look at direct dependencies in public section
			if (pkg.getPublicSection() != null) {
				for (ModelUnit mu : pkg.getPublicSection().getImportedUnits()) {
					getModelDependencies(mu, modelUnits);
				}
			}
		}

	}

	static private void getPredeclaredPropertySets(List<ModelUnit> modelUnits) {

		for (String s : AadlUtil.getPredeclaredPropertySetNames()) {

			final String pathName = "org.osate.workspace/resources/properties/Predeclared_Property_Sets/";
			final ResourceSet resourceSet = new ResourceSetImpl();
			String propSetFileName = pathName + s + ".aadl";
			final Resource r = resourceSet.getResource(URI.createPlatformPluginURI(propSetFileName, true), true);
			final EObject eObj = r.getContents().get(0);
			if (eObj instanceof PropertySet) {
				modelUnits.add((PropertySet) eObj);
			}

		}

	}

}
