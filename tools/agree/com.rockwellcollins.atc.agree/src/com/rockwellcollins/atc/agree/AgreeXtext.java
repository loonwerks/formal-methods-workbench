package com.rockwellcollins.atc.agree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.osate.aadl2.AadlBoolean;
import org.osate.aadl2.AadlInteger;
import org.osate.aadl2.AadlReal;
import org.osate.aadl2.AbstractNamedValue;
import org.osate.aadl2.AnnexSubclause;
import org.osate.aadl2.ArrayDimension;
import org.osate.aadl2.ArraySize;
import org.osate.aadl2.ArraySizeProperty;
import org.osate.aadl2.Classifier;
import org.osate.aadl2.ClassifierValue;
import org.osate.aadl2.ComponentClassifier;
import org.osate.aadl2.ComponentImplementation;
import org.osate.aadl2.ComponentType;
import org.osate.aadl2.DataImplementation;
import org.osate.aadl2.DataType;
import org.osate.aadl2.DirectionType;
import org.osate.aadl2.EnumerationLiteral;
import org.osate.aadl2.EventDataPort;
import org.osate.aadl2.EventPort;
import org.osate.aadl2.Feature;
import org.osate.aadl2.IntegerLiteral;
import org.osate.aadl2.ListValue;
import org.osate.aadl2.NamedElement;
import org.osate.aadl2.NamedValue;
import org.osate.aadl2.Port;
import org.osate.aadl2.Property;
import org.osate.aadl2.PropertyAssociation;
import org.osate.aadl2.PropertyConstant;
import org.osate.aadl2.PropertyExpression;
import org.osate.aadl2.PropertyType;
import org.osate.aadl2.RangeValue;
import org.osate.aadl2.RealLiteral;
import org.osate.aadl2.StringLiteral;
import org.osate.aadl2.Subcomponent;
import org.osate.annexsupport.AnnexUtil;

import com.rockwellcollins.atc.agree.Agree.ArrayContract;
import com.rockwellcollins.atc.agree.Agree.Contract;
import com.rockwellcollins.atc.agree.Agree.DataContract;
import com.rockwellcollins.atc.agree.Agree.EnumContract;
import com.rockwellcollins.atc.agree.Agree.Prim;
import com.rockwellcollins.atc.agree.Agree.RangeIntContract;
import com.rockwellcollins.atc.agree.Agree.RangeRealContract;
import com.rockwellcollins.atc.agree.Agree.RecordContract;
import com.rockwellcollins.atc.agree.agree.AgreeContract;
import com.rockwellcollins.atc.agree.agree.AgreeContractSubclause;
import com.rockwellcollins.atc.agree.agree.AgreePackage;
import com.rockwellcollins.atc.agree.agree.Arg;
import com.rockwellcollins.atc.agree.agree.ArrayLiteralExpr;
import com.rockwellcollins.atc.agree.agree.ArraySubExpr;
import com.rockwellcollins.atc.agree.agree.ArrayType;
import com.rockwellcollins.atc.agree.agree.ArrayUpdateExpr;
import com.rockwellcollins.atc.agree.agree.BinaryExpr;
import com.rockwellcollins.atc.agree.agree.BoolLitExpr;
import com.rockwellcollins.atc.agree.agree.CallExpr;
import com.rockwellcollins.atc.agree.agree.ComponentRef;
import com.rockwellcollins.atc.agree.agree.ConstStatement;
import com.rockwellcollins.atc.agree.agree.DoubleDotRef;
import com.rockwellcollins.atc.agree.agree.EnumLitExpr;
import com.rockwellcollins.atc.agree.agree.EnumStatement;
import com.rockwellcollins.atc.agree.agree.OutputStatement;
import com.rockwellcollins.atc.agree.agree.EventExpr;
import com.rockwellcollins.atc.agree.agree.ExistsExpr;
import com.rockwellcollins.atc.agree.agree.Expr;
import com.rockwellcollins.atc.agree.agree.FlatmapExpr;
import com.rockwellcollins.atc.agree.agree.FloorCast;
import com.rockwellcollins.atc.agree.agree.FnDef;
import com.rockwellcollins.atc.agree.agree.FoldLeftExpr;
import com.rockwellcollins.atc.agree.agree.FoldRightExpr;
import com.rockwellcollins.atc.agree.agree.ForallExpr;
import com.rockwellcollins.atc.agree.agree.GetPropertyExpr;
import com.rockwellcollins.atc.agree.agree.IfThenElseExpr;
import com.rockwellcollins.atc.agree.agree.IndicesExpr;
import com.rockwellcollins.atc.agree.agree.InputStatement;
import com.rockwellcollins.atc.agree.agree.IntLitExpr;
import com.rockwellcollins.atc.agree.agree.LatchedExpr;
import com.rockwellcollins.atc.agree.agree.LibraryFnDef;
import com.rockwellcollins.atc.agree.agree.LinearizationDef;
import com.rockwellcollins.atc.agree.agree.NamedElmExpr;
import com.rockwellcollins.atc.agree.agree.NamedID;
import com.rockwellcollins.atc.agree.agree.NodeDef;
import com.rockwellcollins.atc.agree.agree.PreExpr;
import com.rockwellcollins.atc.agree.agree.PrevExpr;
import com.rockwellcollins.atc.agree.agree.PrimType;
import com.rockwellcollins.atc.agree.agree.BoolOutputStatement;
import com.rockwellcollins.atc.agree.agree.RealCast;
import com.rockwellcollins.atc.agree.agree.RealLitExpr;
import com.rockwellcollins.atc.agree.agree.RecordDef;
import com.rockwellcollins.atc.agree.agree.RecordLitExpr;
import com.rockwellcollins.atc.agree.agree.RecordUpdateExpr;
import com.rockwellcollins.atc.agree.agree.SelectionExpr;
import com.rockwellcollins.atc.agree.agree.SpecStatement;
import com.rockwellcollins.atc.agree.agree.TagExpr;
import com.rockwellcollins.atc.agree.agree.ThisRef;
import com.rockwellcollins.atc.agree.agree.TimeExpr;
import com.rockwellcollins.atc.agree.agree.TimeFallExpr;
import com.rockwellcollins.atc.agree.agree.TimeOfExpr;
import com.rockwellcollins.atc.agree.agree.TimeRiseExpr;
import com.rockwellcollins.atc.agree.agree.Type;
import com.rockwellcollins.atc.agree.agree.UnaryExpr;

public class AgreeXtext {

	public static Contract toContractFromType(Type t) {

		if (t instanceof PrimType) {

			int lowSign = ((PrimType) t).getLowNeg() == null ? 1 : -1;
			int highSign = ((PrimType) t).getHighNeg() == null ? 1 : -1;

			String lowStr = ((PrimType) t).getRangeLow();
			String highStr = ((PrimType) t).getRangeHigh();

			if (((PrimType) t).getName().equals("int")) {
				if (lowStr == null || highStr == null) {
					return Prim.IntContract;
				} else {

					long low = Long.valueOf(lowStr) * lowSign;
					long high = Long.valueOf(highStr) * highSign;
					return new RangeIntContract(low, high);
				}
			} else if (((PrimType) t).getName().equals("real")) {
				if (lowStr == null || highStr == null) {
					return Prim.RealContract;
				} else {
					double low = Double.valueOf(lowStr) * lowSign;
					double high = Double.valueOf(highStr) * highSign;
					return new RangeRealContract(low, high);
				}
			} else if (((PrimType) t).getName().equals("bool")) {
				return Prim.BoolContract;
			} else {
				return Prim.ErrorContract;
			}

		} else if (t instanceof ArrayType) {
			String size = ((ArrayType) t).getSize();
			Contract baseTypeDef = toContractFromType(((ArrayType) t).getStem());

			if (baseTypeDef instanceof Agree.DataContract) {
				return new ArrayContract("", (Agree.DataContract) baseTypeDef, Integer.parseInt(size));
			}
		} else if (t instanceof DoubleDotRef) {
			return toContractFromNamedElm(((DoubleDotRef) t).getElm());

		}

		return Prim.ErrorContract;



	}

	public static Contract toContractFromNamedElm(NamedElement ne) {
		if (ne instanceof Classifier) {
			return toContractFromClassifier((Classifier) ne);

		} else if (ne instanceof RecordDef) {

			EList<Arg> args = ((RecordDef) ne).getArgs();
			Map<String, Agree.DataContract> fields = new HashMap<>();
			for (Arg arg : args) {
				String key = arg.getName();
				Contract typeDef = toContractFromType(arg.getType());

				if (typeDef instanceof DataContract) {
					fields.putIfAbsent(key, (DataContract) typeDef);
				}
			}

			return new RecordContract(ne.getQualifiedName(), fields, ne);

		} else if (ne instanceof EnumStatement) {
			String name = ne.getQualifiedName();
			List<String> enumValues = new ArrayList<String>();

			for (NamedID nid : ((EnumStatement) ne).getEnums()) {
				String enumValue = name + "_" + nid.getName();
				enumValues.add(enumValue);
			}

			return new EnumContract(name, enumValues);

		} else if (ne instanceof Arg) {
			return toContractFromType(((Arg) ne).getType());

		} else {
			return Prim.ErrorContract;

		}
	}


	private static List<Agree.Spec> extractContractList(Classifier c) {
		List<Agree.Spec> result = new ArrayList<Agree.Spec>();
		return result;
	}

	public static Contract toContractFromClassifier(Classifier c) {

		if (c instanceof AadlBoolean || c.getName().contains("Boolean")) {
			return Prim.BoolContract;
		} else if (c instanceof AadlInteger || c.getName().contains("Integer") || c.getName().contains("Natural")
				|| c.getName().contains("Unsigned")) {
			return Prim.IntContract;
		} else if (c instanceof AadlReal || c.getName().contains("Float")) {
			return Prim.RealContract;
		} else if (c instanceof DataType) {
			Classifier ext = c.getExtended();
			if (ext != null && (ext instanceof AadlInteger || ext.getName().contains("Integer")
					|| ext.getName().contains("Natural") || ext.getName().contains("Unsigned"))) {

				List<PropertyAssociation> pas = c.getAllPropertyAssociations();
				for (PropertyAssociation choice : pas) {
					Property p = choice.getProperty();

					PropertyExpression v = choice.getOwnedValues().get(0).getOwnedValue();

					String key = p.getQualifiedName();

					if (key.equals("Data_Model::Integer_Range")) {
						if (v instanceof RangeValue) {
							try {
								RangeValue rangeValue = (RangeValue) v;
								long min = intFromPropExp(rangeValue.getMinimum()).get();
								long max = intFromPropExp(rangeValue.getMaximum()).get();
								return new RangeIntContract(min, max);
							} catch (Exception e) {
								return Prim.ErrorContract;
							}
						}
					}
				}
				return Prim.IntContract;

			} else if (ext != null && (ext instanceof AadlReal || ext.getName().contains("Float"))) {

				List<PropertyAssociation> pas = c.getAllPropertyAssociations();
				for (PropertyAssociation choice : pas) {
					Property p = choice.getProperty();

					PropertyExpression v = choice.getOwnedValues().get(0).getOwnedValue();

					String key = p.getQualifiedName();

					if (key.equals("Data_Model::Real_Range")) {
						if (v instanceof RangeValue) {
							try {
								RangeValue rangeValue = (RangeValue) v;
								double min = realFromPropExp(rangeValue.getMinimum()).get();
								double max = realFromPropExp(rangeValue.getMaximum()).get();
								return new RangeRealContract(min, max);
							} catch (Exception e) {
								return Prim.ErrorContract;
							}
						}
					}
				}
				return Prim.RealContract;
			}

			List<PropertyAssociation> pas = c.getAllPropertyAssociations();

			boolean prop_isArray = false;
			int prop_arraySize = 0;
			Contract prop_arrayBaseType = null;

			boolean prop_isEnum = false;
			List<String> prop_enumValues = null;

			for (PropertyAssociation choice : pas) {
				Property p = choice.getProperty();

				PropertyExpression v = choice.getOwnedValues().get(0).getOwnedValue();

				String key = p.getQualifiedName();

				if (key.equals("Data_Model::Data_Representation")) {
					if (v instanceof NamedValue) {
						AbstractNamedValue anv = ((NamedValue) v).getNamedValue();
						if (anv instanceof EnumerationLiteral) {
							EnumerationLiteral el = (EnumerationLiteral) anv;
							prop_isArray = el.getName().equals("Array");
							prop_isEnum = el.getName().equals("Enum");
						}
					}

				} else if (key.equals("Data_Model::Enumerators")) {
					if (v instanceof ListValue) {
						EList<PropertyExpression> peList = ((ListValue) v).getOwnedListElements();
						String prefix = c.getQualifiedName() + "_";
						prop_enumValues = new ArrayList<>();
						for (PropertyExpression pe : peList) {
							if (pe instanceof StringLiteral) {
								String enumString = prefix + ((StringLiteral) pe).getValue();
								prop_enumValues.add(enumString);
							}
						}
					}

				} else if (key.equals("Data_Model::Base_Type")) {
					if (v instanceof ListValue) {
						ListValue l = (ListValue) v;
						PropertyExpression pe = l.getOwnedListElements().get(0);
						if (pe instanceof ClassifierValue) {
							prop_arrayBaseType = toContractFromClassifier(((ClassifierValue) pe).getClassifier());
						}

					}

				} else if (key.equals("Data_Model::Dimension")) {
					if (v instanceof ListValue) {
						ListValue l = (ListValue) v;
						PropertyExpression pe = l.getOwnedListElements().get(0);
						prop_arraySize = Math.toIntExact(intFromPropExp(pe).orElse((long) -1).longValue());

					}
				}

			}

			if (prop_isArray && prop_arraySize > 0 && prop_arrayBaseType != null
					&& prop_arrayBaseType instanceof Agree.DataContract) {

				return new ArrayContract(c.getQualifiedName(), (Agree.DataContract) prop_arrayBaseType, prop_arraySize);

			} else if (prop_isEnum && prop_enumValues != null) {
				String name = c.getQualifiedName();
				return new EnumContract(name, prop_enumValues);

			}
		} else if (c instanceof DataImplementation) {

			Map<String, Agree.DataContract> fields = new HashMap<>();

			DataImplementation currClsfr = (DataImplementation) c;
			while (currClsfr != null) {

				EList<Subcomponent> subcomps = currClsfr.getAllSubcomponents();
				for (Subcomponent sub : subcomps) {
					String fieldName = sub.getName();
					if (sub.getClassifier() != null) {

						if (sub.getArrayDimensions().size() == 0) {
							Contract typeDef = toContractFromClassifier(sub.getClassifier());
							if (typeDef instanceof Agree.DataContract) {
								fields.putIfAbsent(fieldName, (Agree.DataContract) typeDef);
							}

						} else if (sub.getArrayDimensions().size() == 1) {


							Contract stemContract = toContractFromClassifier(sub.getClassifier());
							if (stemContract instanceof Agree.DataContract) {
								long size = getArraySize(sub.getArrayDimensions().get(0));
								DataContract arrayContract = new Agree.ArrayContract("",
										(Agree.DataContract) stemContract, Math.toIntExact(size));
								fields.putIfAbsent(fieldName, arrayContract);

							}
							throw new RuntimeException("Arrays may not be used in node subcomponent");
						}
					}
				}


			}

			String name = c.getQualifiedName();

			return new Agree.RecordContract(name, fields, c);

		} else if (c instanceof ComponentClassifier) {


			Map<String, Agree.Port> ports = new HashMap<>();
			Map<String, Agree.NodeContract> subNodes = new HashMap<>();
			List<Agree.Spec> contractList = new ArrayList<Agree.Spec>();

			Classifier currClsfr = c;
			while (currClsfr != null) {



				List<Agree.Spec> localContractList = extractContractList(currClsfr);
				contractList.addAll(localContractList);

				ComponentType ct = null;
				if (currClsfr instanceof ComponentImplementation) {

					EList<Subcomponent> subcomps = ((ComponentImplementation) currClsfr).getAllSubcomponents();
					for (Subcomponent sub : subcomps) {
						String fieldName = sub.getName();
						if (sub.getClassifier() != null) {

							if (sub.getArrayDimensions().size() == 0) {
								Contract typeDef = toContractFromClassifier(sub.getClassifier());
								if (typeDef instanceof Agree.NodeContract) {
									subNodes.putIfAbsent(fieldName, (Agree.NodeContract) typeDef);
								}

							} else if (sub.getArrayDimensions().size() == 1) {
								throw new RuntimeException("Arrays may not be used in node subcomponent");
							}
						}
					}

					ct = ((ComponentImplementation) currClsfr).getType();
				} else if (c instanceof ComponentType) {
					ct = (ComponentType) currClsfr;
				}

				if (ct != null) {

					EList<Feature> features = ct.getAllFeatures();
					for (Feature feature : features) {
						String fieldName = feature.getName();

						boolean isEvent = feature instanceof EventDataPort || feature instanceof EventPort;
						Agree.Direc direction = null;
						if (feature instanceof Port) {
							int v = ((Port) feature).getDirection().getValue();
							if (v == DirectionType.IN_VALUE) {
								direction = new Agree.In();
							} else if (v == DirectionType.OUT_VALUE) {
								direction = new Agree.Out(Optional.empty());
							}
						}

						if (direction != null) {


							if (feature.getClassifier() != null) {
								if (feature.getArrayDimensions().size() == 0) {
									Contract typeDef = toContractFromClassifier(feature.getClassifier());
									if (typeDef instanceof DataContract) {
										Agree.Port port = new Agree.Port((DataContract) typeDef, direction, isEvent);
										ports.putIfAbsent(fieldName, port);
									}
								} else if (feature.getArrayDimensions().size() == 1) {
									ArrayDimension ad = feature.getArrayDimensions().get(0);
									int size = Math.toIntExact(getArraySize(ad));
									Contract stem = toContractFromClassifier(feature.getClassifier());
									if (stem instanceof Agree.DataContract) {
										DataContract typeDef = new ArrayContract("", (Agree.DataContract) stem, size);
										Agree.Port port = new Agree.Port(typeDef, direction, isEvent);
										ports.putIfAbsent(fieldName, port);
									}

								}
							}
						}
					}

					for (AnnexSubclause annex : AnnexUtil.getAllAnnexSubclauses(currClsfr,
							AgreePackage.eINSTANCE.getAgreeContractSubclause())) {
						AgreeContract contract = (AgreeContract) ((AgreeContractSubclause) annex).getContract();

						for (SpecStatement spec : contract.getSpecs()) {

							List<Arg> args = new ArrayList<>();
							if (spec instanceof OutputStatement) {
								args = ((OutputStatement) spec).getLhs();
							} else if (spec instanceof InputStatement) {
								args = ((InputStatement) spec).getLhs();
							}

							for (Arg arg : args) {
								String fieldName = arg.getName();
								Contract typeDef = toContractFromNamedElm(arg);

								if (typeDef instanceof DataContract) {
									Agree.Port port = new Agree.Port((DataContract) typeDef,
											new Agree.Out(Optional.empty()), false);
									ports.putIfAbsent(fieldName, port);
								}

							}
						}

					}
				}

				currClsfr = currClsfr.getExtended();
			}

			String name = c.getQualifiedName();

			return new Agree.NodeContract(name, ports, subNodes, contractList, c);

		}

		return Prim.ErrorContract;

	}

	public static Contract inferContractFromNamedElement(NamedElement ne) {

		if (ne instanceof BoolOutputStatement) {
			return inferContract(((BoolOutputStatement) ne).getExpr());

		} else if (ne instanceof NamedID && ne.eContainer() instanceof EnumStatement) {

			EnumStatement enumDef = (EnumStatement) ne.eContainer();
			String name = enumDef.getQualifiedName();
			List<String> enumValues = new ArrayList<String>();

			for (NamedID nid : enumDef.getEnums()) {
				String enumValue = name + "_" + nid.getName();
				enumValues.add(enumValue);
			}
			return new EnumContract(name, enumValues);

		} else if (ne instanceof NamedID) {

			EObject container = ne.eContainer();

			Expr arrExpr = null;

			if (container instanceof ForallExpr) {
				arrExpr = ((ForallExpr) container).getArray();

			} else if (container instanceof ExistsExpr) {
				arrExpr = ((ExistsExpr) container).getArray();

			} else if (container instanceof FlatmapExpr) {
				arrExpr = ((FlatmapExpr) container).getArray();

			} else if (container instanceof FoldLeftExpr) {
				arrExpr = ((FoldLeftExpr) container).getArray();

			} else if (container instanceof FoldRightExpr) {
				arrExpr = ((FoldRightExpr) container).getArray();

			}

			if (arrExpr != null) {
				Contract arrType = inferContract(arrExpr);
				if (arrType instanceof ArrayContract) {
					return ((ArrayContract) arrType).stemContract;
				}
			}

			if (container instanceof FoldLeftExpr) {
				Expr initExpr = ((FoldLeftExpr) container).getInitial();
				Contract initType = inferContract(initExpr);
				return initType;

			} else if (container instanceof FoldRightExpr) {
				Expr initExpr = ((FoldRightExpr) container).getInitial();
				Contract initType = inferContract(initExpr);
				return initType;
			}

		} else if (ne instanceof ConstStatement) {
			return toContractFromType(((ConstStatement) ne).getType());

		} else if (ne instanceof Arg) {
			return toContractFromType(((Arg) ne).getType());

		} else if (ne instanceof Subcomponent) {

			Subcomponent sub = (Subcomponent) ne;
			Classifier cl = sub.getClassifier();
			List<ArrayDimension> dims = sub.getArrayDimensions();
			Contract clsTypeDef = toContractFromClassifier(cl);
			if (dims.size() == 0) {
				return clsTypeDef;
			} else if (dims.size() == 1 && clsTypeDef instanceof Agree.DataContract) {
				long size = getArraySize(dims.get(0));
				return new ArrayContract("", (Agree.DataContract) clsTypeDef, Math.toIntExact(size));
			}

		} else if (ne instanceof Feature) {

			Classifier cl = ((Feature) ne).getClassifier();
			List<ArrayDimension> dims = ((Feature) ne).getArrayDimensions();
			Contract clsTypeDef = toContractFromClassifier(cl);
			if (dims.size() == 0) {
				return clsTypeDef;
			} else if (dims.size() == 1 && clsTypeDef instanceof Agree.DataContract) {
				long size = getArraySize(dims.get(0));
				return new ArrayContract("", (Agree.DataContract) clsTypeDef, Math.toIntExact(size));

			}

		} else if (ne instanceof PropertyConstant) {
			PropertyExpression pe = ((PropertyConstant) ne).getConstantValue();
			return inferContractFromPropExp(pe);

		}

		return Prim.ErrorContract;

	}

	public static boolean hasSpec(NamedElement ne) {
		return inferContractFromNamedElement(ne) != (Prim.ErrorContract);
	}

	public static boolean isInLinearizationBody(Expr expr) {
		boolean result = false;
		EObject current = expr;
		while (current != null && current instanceof Expr) {
			EObject container = current.eContainer();
			if (container instanceof LinearizationDef) {
				result = ((LinearizationDef) container).getExprBody().equals(current);
			}
			current = container;
		}
		return result;
	}

	public static List<Type> typesFromArgs(List<Arg> args) {
		ArrayList<Type> list = new ArrayList<>();
		for (Arg arg : args) {
			list.add(arg.getType());
		}
		return list;
	}

	private static long getArraySize(ArrayDimension arrayDimension) {
		ArraySize arraySize = arrayDimension.getSize();
		long size = arraySize.getSize();
		if (size == 0) {
			ArraySizeProperty arraySizeProperty = arraySize.getSizeProperty();
			if (arraySizeProperty instanceof PropertyConstant) {
				PropertyExpression pe = ((PropertyConstant) arraySizeProperty).getConstantValue();
				size = intFromPropExp(pe).orElse((long) -1);
			}
		}
		assert size > 0;
		return size;
	}

	public static Optional<Long> intFromPropExp(PropertyExpression pe) {
		if (pe instanceof IntegerLiteral) {
			return Optional.of(((IntegerLiteral) pe).getValue());

		} else if (pe instanceof NamedValue) {
			NamedValue nv = (NamedValue) pe;
			AbstractNamedValue anv = nv.getNamedValue();
			if (anv instanceof PropertyConstant) {
				return intFromPropExp(((PropertyConstant) anv).getConstantValue());
			}
		}

		return Optional.empty();
	}

	private static Contract inferContractFromPropExp(PropertyExpression pe) {
		if (pe instanceof IntegerLiteral) {
			return Prim.IntContract;

		} else if (pe instanceof RealLiteral) {
			return Prim.RealContract;

		} else if (pe instanceof NamedValue) {
			NamedValue nv = (NamedValue) pe;
			AbstractNamedValue anv = nv.getNamedValue();
			if (anv instanceof PropertyConstant) {
				return inferContractFromPropExp(((PropertyConstant) anv).getConstantValue());
			}
		}

		return Prim.ErrorContract;

	}

	public static Contract inferContract(Expr expr) {

		if (expr instanceof SelectionExpr) {

			Expr target = ((SelectionExpr) expr).getTarget();
			Contract targetType = inferContract(target);
			String name = ((SelectionExpr) expr).getField().getName();

			if (targetType instanceof Agree.RecordContract) {
				Map<String, Agree.DataContract> fields = ((Agree.RecordContract) targetType).fields;
				for (Entry<String, Agree.DataContract> entry : fields.entrySet()) {
					if (entry.getKey().equals(name)) {
						return entry.getValue();
					}
				}

			} else if (targetType instanceof Agree.NodeContract) {
				Map<String, Agree.Port> ports = ((Agree.NodeContract) targetType).ports;
				for (Entry<String, Agree.Port> entry : ports.entrySet()) {
					if (entry.getKey().equals(name)) {
						return entry.getValue().dataContract;
					}
				}

			}

		} else if (expr instanceof TagExpr) {

			String tag = ((TagExpr) expr).getTag();
			if (tag != null) {
				switch (tag) {
				case "_CLK":
				case "_INSERT":
				case "_REMOVE":
					return Prim.BoolContract;
				case "_COUNT":
					return Prim.IntContract;
				}
			}

		} else if (expr instanceof ArraySubExpr) {
			Expr arrExpr = ((ArraySubExpr) expr).getExpr();
			Contract arrType = inferContract(arrExpr);
			if (arrType instanceof ArrayContract) {
				return ((ArrayContract) arrType).stemContract;
			}

		} else if (expr instanceof IndicesExpr) {
			Contract arrType = inferContract(((IndicesExpr) expr).getArray());
			if (arrType instanceof ArrayContract) {
				int size = ((ArrayContract) arrType).size;
				return new ArrayContract("", Prim.IntContract, size);
			}

		} else if (expr instanceof ForallExpr) {
			return Prim.BoolContract;

		} else if (expr instanceof ExistsExpr) {
			return Prim.BoolContract;

		} else if (expr instanceof FlatmapExpr) {
			Contract innerArrType = inferContract(((FlatmapExpr) expr).getExpr());
			if (innerArrType instanceof ArrayContract) {
				Contract stemType = ((ArrayContract) innerArrType).stemContract;
				Contract arrType = inferContract(((FlatmapExpr) expr).getArray());

				if (arrType instanceof ArrayContract && arrType instanceof Agree.DataContract) {
					int size = ((ArrayContract) arrType).size;
					return new ArrayContract("", (Agree.DataContract) stemType, size);
				}
			}

		} else if (expr instanceof FoldLeftExpr) {
			return inferContract(((FoldLeftExpr) expr).getExpr());

		} else if (expr instanceof FoldRightExpr) {
			return inferContract(((FoldRightExpr) expr).getExpr());

		} else if (expr instanceof BinaryExpr) {
			Contract leftType = inferContract(((BinaryExpr) expr).getLeft());
			String op = ((BinaryExpr) expr).getOp();

			switch (op) {
			case "->":
				return leftType;
			case "=>":
			case "<=>":
			case "and":
			case "or":
			case "<>":
			case "!=":
			case "<":
			case "<=":
			case ">":
			case ">=":
			case "=":
				return Prim.BoolContract;
			case "+":
			case "-":
			case "*":
			case "/":
			case "mod":
			case "div":
			case "^":
				return leftType;
			}

		} else if (expr instanceof UnaryExpr) {
			return inferContract(((UnaryExpr) expr).getExpr());

		} else if (expr instanceof IfThenElseExpr) {
			return inferContract(((IfThenElseExpr) expr).getB());

		} else if (expr instanceof PrevExpr) {
			return inferContract(((PrevExpr) expr).getInit());

		} else if (expr instanceof GetPropertyExpr) {

			ComponentRef cr = ((GetPropertyExpr) expr).getComponentRef();
			NamedElement prop = ((GetPropertyExpr) expr).getProp();
			if (cr instanceof ThisRef) {

				if (prop instanceof Property) {
					PropertyType pt = ((Property) prop).getPropertyType();

					return inferContractFromNamedElement(pt);

				} else {

					EObject container = expr.getContainingClassifier();
					List<PropertyAssociation> pas = ((Classifier) container).getAllPropertyAssociations();
					for (PropertyAssociation choice : pas) {
						if (choice.getProperty().getName().equals(prop.getName())) {
							PropertyType pt = choice.getProperty().getPropertyType();
							return inferContractFromNamedElement(pt);
						}
					}
				}

			} else if (cr instanceof DoubleDotRef) {

				EObject container = expr.getContainingComponentImpl();
				if (container instanceof ComponentImplementation) {
					List<Subcomponent> subcomps = ((ComponentImplementation) container).getAllSubcomponents();
					for (Subcomponent choice : subcomps) {

						List<PropertyAssociation> pas = choice.getOwnedPropertyAssociations();
						for (PropertyAssociation pchoice : pas) {

							if (pchoice.getProperty().getName().equals(prop.getName())) {
								PropertyType pt = pchoice.getProperty().getPropertyType();
								return inferContractFromNamedElement(pt);
							}
						}

					}

				}

			}

		} else if (expr instanceof IntLitExpr) {
			return Prim.IntContract;

		} else if (expr instanceof RealLitExpr) {
			return Prim.RealContract;

		} else if (expr instanceof BoolLitExpr) {
			return Prim.BoolContract;

		} else if (expr instanceof FloorCast) {
			return Prim.IntContract;

		} else if (expr instanceof RealCast) {
			return Prim.RealContract;

		} else if (expr instanceof EventExpr) {
			return Prim.BoolContract;

		} else if (expr instanceof TimeExpr) {
			return Prim.RealContract;

		} else if (expr instanceof EnumLitExpr) {
			return toContractFromType(((EnumLitExpr) expr).getEnumType());

		} else if (expr instanceof LatchedExpr) {
			return inferContract(((LatchedExpr) expr).getExpr());

		} else if (expr instanceof TimeOfExpr) {
			return Prim.RealContract;

		} else if (expr instanceof TimeRiseExpr) {
			return Prim.RealContract;

		} else if (expr instanceof TimeFallExpr) {
			return Prim.RealContract;

		} else if (expr instanceof TimeOfExpr) {
			return Prim.RealContract;

		} else if (expr instanceof TimeRiseExpr) {
			return Prim.RealContract;

		} else if (expr instanceof TimeFallExpr) {
			return Prim.RealContract;

		} else if (expr instanceof PreExpr) {
			return inferContract(((PreExpr) expr).getExpr());

		} else if (expr instanceof ArrayLiteralExpr) {
			EList<Expr> elems = ((ArrayLiteralExpr) expr).getElems();
			Expr first = elems.get(0);
			int size = elems.size();
			Contract firstType = inferContract(first);

			if (firstType instanceof Agree.DataContract) {
				return new ArrayContract("", (Agree.DataContract) firstType, size);
			}

		} else if (expr instanceof ArrayUpdateExpr) {
			return inferContract(((ArrayUpdateExpr) expr).getArray());

		} else if (expr instanceof RecordLitExpr) {
			return toContractFromType(((RecordLitExpr) expr).getRecordType());

		} else if (expr instanceof RecordUpdateExpr) {
			return inferContract(((RecordUpdateExpr) expr).getRecord());

		} else if (expr instanceof NamedElmExpr) {
			NamedElement ne = ((NamedElmExpr) expr).getElm();
			return inferContractFromNamedElement(ne);

		} else if (expr instanceof CallExpr) {

			CallExpr fnCall = ((CallExpr) expr);
			DoubleDotRef dotId = fnCall.getRef();
			NamedElement namedEl = dotId.getElm();

			if (isInLinearizationBody(fnCall)) {
				// extract in/out arguments
				if (namedEl instanceof LinearizationDef) {
					return Prim.RealContract;
				} else if (namedEl instanceof LibraryFnDef) {
					LibraryFnDef fnDef = (LibraryFnDef) namedEl;
					return toContractFromType(fnDef.getType());
				}

			} else {
				// extract in/out arguments
				if (namedEl instanceof FnDef) {
					FnDef fnDef = (FnDef) namedEl;
					return toContractFromType(fnDef.getType());
				} else if (namedEl instanceof NodeDef) {
					NodeDef nodeDef = (NodeDef) namedEl;
					List<Type> outDefTypes = typesFromArgs(nodeDef.getRets());
					if (outDefTypes.size() == 1) {
						return toContractFromType(outDefTypes.get(0));
					}
				} else if (namedEl instanceof LinearizationDef) {
					return Prim.RealContract;
				} else if (namedEl instanceof LibraryFnDef) {
					LibraryFnDef fnDef = (LibraryFnDef) namedEl;
					return toContractFromType(fnDef.getType());
				}

			}

		}
		return Prim.ErrorContract;

	}

	public static Optional<Double> realFromPropExp(PropertyExpression pe) {
		if (pe instanceof RealLiteral) {
			return Optional.of(((RealLiteral) pe).getValue());

		} else if (pe instanceof NamedValue) {
			NamedValue nv = (NamedValue) pe;
			AbstractNamedValue anv = nv.getNamedValue();
			if (anv instanceof PropertyConstant) {
				return realFromPropExp(((PropertyConstant) anv).getConstantValue());
			}
		}

		return Optional.empty();
	}

	public static Map<String, Agree.Contract> extractContractMap(Classifier c) {
		Map<String, Agree.Contract> result = new HashMap<String, Agree.Contract>();
		return result;
	}

	public static Agree.Program toProgram(ComponentImplementation ci) {
		Contract main = toContractFromClassifier(ci);
		Map<String, Agree.Contract> specMap = extractContractMap(ci);
		return new Agree.Program(main, specMap);
	}

}