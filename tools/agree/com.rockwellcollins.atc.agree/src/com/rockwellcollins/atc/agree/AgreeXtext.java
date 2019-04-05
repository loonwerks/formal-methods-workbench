package com.rockwellcollins.atc.agree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.osate.aadl2.AadlBoolean;
import org.osate.aadl2.AadlInteger;
import org.osate.aadl2.AadlPackage;
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

import com.rockwellcollins.atc.agree.Nenola.ArrayContract;
import com.rockwellcollins.atc.agree.Nenola.Contract;
import com.rockwellcollins.atc.agree.Nenola.DataContract;
import com.rockwellcollins.atc.agree.Nenola.EnumContract;
import com.rockwellcollins.atc.agree.Nenola.NodeGen;
import com.rockwellcollins.atc.agree.Nenola.Prim;
import com.rockwellcollins.atc.agree.Nenola.RangeIntContract;
import com.rockwellcollins.atc.agree.Nenola.RangeRealContract;
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
import com.rockwellcollins.atc.agree.agree.BoolOutputStatement;
import com.rockwellcollins.atc.agree.agree.CallExpr;
import com.rockwellcollins.atc.agree.agree.ComponentRef;
import com.rockwellcollins.atc.agree.agree.ConstStatement;
import com.rockwellcollins.atc.agree.agree.DoubleDotRef;
import com.rockwellcollins.atc.agree.agree.EnumLitExpr;
import com.rockwellcollins.atc.agree.agree.EnumStatement;
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
import com.rockwellcollins.atc.agree.agree.NodeEq;
import com.rockwellcollins.atc.agree.agree.NodeLemma;
import com.rockwellcollins.atc.agree.agree.NodeStmt;
import com.rockwellcollins.atc.agree.agree.OutputStatement;
import com.rockwellcollins.atc.agree.agree.PreExpr;
import com.rockwellcollins.atc.agree.agree.PrevExpr;
import com.rockwellcollins.atc.agree.agree.PrimType;
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

	public static Nenola.Contract toContractFromType(Type t) {

		if (t instanceof PrimType) {

			int lowSign = ((PrimType) t).getLowNeg() == null ? 1 : -1;
			int highSign = ((PrimType) t).getHighNeg() == null ? 1 : -1;

			String lowStr = ((PrimType) t).getRangeLow();
			String highStr = ((PrimType) t).getRangeHigh();

			if (((PrimType) t).getName().equals("int")) {
				if (lowStr == null || highStr == null) {
					return Nenola.Prim.IntContract;
				} else {

					long low = Long.valueOf(lowStr) * lowSign;
					long high = Long.valueOf(highStr) * highSign;
					return new Nenola.RangeIntContract(low, high);
				}
			} else if (((PrimType) t).getName().equals("real")) {
				if (lowStr == null || highStr == null) {
					return Nenola.Prim.RealContract;
				} else {
					double low = Double.valueOf(lowStr) * lowSign;
					double high = Double.valueOf(highStr) * highSign;
					return new Nenola.RangeRealContract(low, high);
				}
			} else if (((PrimType) t).getName().equals("bool")) {
				return Nenola.Prim.BoolContract;
			} else {
				return Nenola.Prim.ErrorContract;
			}

		} else if (t instanceof ArrayType) {
			String size = ((ArrayType) t).getSize();
			Nenola.Contract baseTypeDef = toContractFromType(((ArrayType) t).getStem());

			if (baseTypeDef instanceof Nenola.DataContract) {
				return new Nenola.ArrayContract("", (Nenola.DataContract) baseTypeDef, Integer.parseInt(size));
			}
		} else if (t instanceof DoubleDotRef) {
			return toContractFromNamedElm(((DoubleDotRef) t).getElm());

		}

		return Nenola.Prim.ErrorContract;



	}

	public static Nenola.Contract toContractFromNamedElm(NamedElement ne) {
		if (ne instanceof Classifier) {
			return toContractFromClassifier((Classifier) ne);

		} else if (ne instanceof RecordDef) {

			EList<Arg> args = ((RecordDef) ne).getArgs();
			Map<String, Nenola.DataContract> fields = new HashMap<>();
			for (Arg arg : args) {
				String key = arg.getName();
				Nenola.Contract typeDef = toContractFromType(arg.getType());

				if (typeDef instanceof Nenola.DataContract) {
					fields.putIfAbsent(key, (Nenola.DataContract) typeDef);
				}
			}

			return new Nenola.RecordContract(ne.getQualifiedName(), fields, ne);

		} else if (ne instanceof EnumStatement) {
			String name = ne.getQualifiedName();
			List<String> enumValues = new ArrayList<String>();

			for (NamedID nid : ((EnumStatement) ne).getEnums()) {
				String enumValue = name + "_" + nid.getName();
				enumValues.add(enumValue);
			}

			return new Nenola.EnumContract(name, enumValues);

		} else if (ne instanceof Arg) {
			return toContractFromType(((Arg) ne).getType());

		} else {
			return Nenola.Prim.ErrorContract;

		}
	}


	private static List<Nenola.Spec> extractContractList(Classifier c) {
		List<Nenola.Spec> result = new ArrayList<Nenola.Spec>();
		return result;
	}

	public static Nenola.Contract toContractFromClassifier(Classifier c) {

		if (c instanceof AadlBoolean || c.getName().contains("Boolean")) {
			return Nenola.Prim.BoolContract;
		} else if (c instanceof AadlInteger || c.getName().contains("Integer") || c.getName().contains("Natural")
				|| c.getName().contains("Unsigned")) {
			return Nenola.Prim.IntContract;
		} else if (c instanceof AadlReal || c.getName().contains("Float")) {
			return Nenola.Prim.RealContract;
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
								return Nenola.Prim.ErrorContract;
							}
						}
					}
				}
				return Nenola.Prim.IntContract;

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
								return Nenola.Prim.ErrorContract;
							}
						}
					}
				}
				return Nenola.Prim.RealContract;
			}

			List<PropertyAssociation> pas = c.getAllPropertyAssociations();

			boolean prop_isArray = false;
			int prop_arraySize = 0;
			Nenola.Contract prop_arrayBaseType = null;

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
					&& prop_arrayBaseType instanceof Nenola.DataContract) {

				return new ArrayContract(c.getQualifiedName(), (Nenola.DataContract) prop_arrayBaseType, prop_arraySize);

			} else if (prop_isEnum && prop_enumValues != null) {
				String name = c.getQualifiedName();
				return new EnumContract(name, prop_enumValues);

			}
		} else if (c instanceof DataImplementation) {

			Map<String, Nenola.DataContract> fields = new HashMap<>();

			DataImplementation currClsfr = (DataImplementation) c;
			while (currClsfr != null) {

				EList<Subcomponent> subcomps = currClsfr.getAllSubcomponents();
				for (Subcomponent sub : subcomps) {
					String fieldName = sub.getName();
					if (sub.getClassifier() != null) {

						if (sub.getArrayDimensions().size() == 0) {
							Nenola.Contract typeDef = toContractFromClassifier(sub.getClassifier());
							if (typeDef instanceof Nenola.DataContract) {
								fields.putIfAbsent(fieldName, (Nenola.DataContract) typeDef);
							}

						} else if (sub.getArrayDimensions().size() == 1) {

							Nenola.Contract stemContract = toContractFromClassifier(sub.getClassifier());
							if (stemContract instanceof Nenola.DataContract) {
								long size = getArraySize(sub.getArrayDimensions().get(0));
								DataContract arrayContract = new Nenola.ArrayContract("",
										(Nenola.DataContract) stemContract, Math.toIntExact(size));
								fields.putIfAbsent(fieldName, arrayContract);

							}
							throw new RuntimeException("Arrays may not be used in node subcomponent");
						}
					}
				}


			}

			String name = c.getQualifiedName();

			return new Nenola.RecordContract(name, fields, c);

		} else if (c instanceof ComponentClassifier) {


			Map<String, Nenola.Channel> channels = new HashMap<>();
			Map<String, Nenola.NodeContract> subNodes = new HashMap<>();
			List<Nenola.Spec> contractList = new ArrayList<Nenola.Spec>();

			Classifier currClsfr = c;
			while (currClsfr != null) {



				List<Nenola.Spec> localContractList = extractContractList(currClsfr);
				contractList.addAll(localContractList);

				ComponentType ct = null;
				if (currClsfr instanceof ComponentImplementation) {

					EList<Subcomponent> subcomps = ((ComponentImplementation) currClsfr).getAllSubcomponents();
					for (Subcomponent sub : subcomps) {
						String fieldName = sub.getName();
						if (sub.getClassifier() != null) {

							if (sub.getArrayDimensions().size() == 0) {
								Nenola.Contract typeDef = toContractFromClassifier(sub.getClassifier());
								if (typeDef instanceof Nenola.NodeContract) {
									subNodes.putIfAbsent(fieldName, (Nenola.NodeContract) typeDef);
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
						Nenola.Direc direction = null;
						if (feature instanceof Port) {
							int v = ((Port) feature).getDirection().getValue();
							if (v == DirectionType.IN_VALUE) {
								direction = new Nenola.In();
							} else if (v == DirectionType.OUT_VALUE) {
								direction = new Nenola.Out(Optional.empty());
							}
						}

						if (direction != null) {


							if (feature.getClassifier() != null) {
								if (feature.getArrayDimensions().size() == 0) {
									Nenola.Contract typeDef = toContractFromClassifier(feature.getClassifier());
									if (typeDef instanceof DataContract) {
										Nenola.Channel channel = new Nenola.Channel(fieldName,
												(DataContract) typeDef, direction, isEvent);
										channels.putIfAbsent(fieldName, channel);
									}
								} else if (feature.getArrayDimensions().size() == 1) {
									ArrayDimension ad = feature.getArrayDimensions().get(0);
									int size = Math.toIntExact(getArraySize(ad));
									Nenola.Contract stem = toContractFromClassifier(feature.getClassifier());
									if (stem instanceof Nenola.DataContract) {
										DataContract typeDef = new ArrayContract("", (Nenola.DataContract) stem, size);
										Nenola.Channel channel = new Nenola.Channel(fieldName, typeDef,
												direction, isEvent);
										channels.putIfAbsent(fieldName, channel);
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
								Nenola.Contract typeDef = toContractFromNamedElm(arg);

								if (typeDef instanceof DataContract) {
									Nenola.Channel channel = new Nenola.Channel(fieldName,
											(DataContract) typeDef,
											new Nenola.Out(Optional.empty()), false);
									channels.putIfAbsent(fieldName, channel);
								}

							}
						}

					}
				}

				currClsfr = currClsfr.getExtended();
			}

			String name = c.getQualifiedName();

			return new Nenola.NodeContract(name, channels, subNodes, contractList, c);

		}

		return Nenola.Prim.ErrorContract;

	}

	public static Nenola.Contract inferContractFromNamedElement(NamedElement ne) {

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
				Nenola.Contract arrType = inferContract(arrExpr);
				if (arrType instanceof ArrayContract) {
					return ((ArrayContract) arrType).stemContract;
				}
			}

			if (container instanceof FoldLeftExpr) {
				Expr initExpr = ((FoldLeftExpr) container).getInitial();
				Nenola.Contract initType = inferContract(initExpr);
				return initType;

			} else if (container instanceof FoldRightExpr) {
				Expr initExpr = ((FoldRightExpr) container).getInitial();
				Nenola.Contract initType = inferContract(initExpr);
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
			Nenola.Contract clsTypeDef = toContractFromClassifier(cl);
			if (dims.size() == 0) {
				return clsTypeDef;
			} else if (dims.size() == 1 && clsTypeDef instanceof Nenola.DataContract) {
				long size = getArraySize(dims.get(0));
				return new ArrayContract("", (Nenola.DataContract) clsTypeDef, Math.toIntExact(size));
			}

		} else if (ne instanceof Feature) {

			Classifier cl = ((Feature) ne).getClassifier();
			List<ArrayDimension> dims = ((Feature) ne).getArrayDimensions();
			Nenola.Contract clsTypeDef = toContractFromClassifier(cl);
			if (dims.size() == 0) {
				return clsTypeDef;
			} else if (dims.size() == 1 && clsTypeDef instanceof Nenola.DataContract) {
				long size = getArraySize(dims.get(0));
				return new ArrayContract("", (Nenola.DataContract) clsTypeDef, Math.toIntExact(size));

			}

		} else if (ne instanceof PropertyConstant) {
			PropertyExpression pe = ((PropertyConstant) ne).getConstantValue();
			return inferContractFromPropExp(pe);

		}

		return Nenola.Prim.ErrorContract;

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

	private static Nenola.Contract inferContractFromPropExp(PropertyExpression pe) {
		if (pe instanceof IntegerLiteral) {
			return Nenola.Prim.IntContract;

		} else if (pe instanceof RealLiteral) {
			return Nenola.Prim.RealContract;

		} else if (pe instanceof NamedValue) {
			NamedValue nv = (NamedValue) pe;
			AbstractNamedValue anv = nv.getNamedValue();
			if (anv instanceof PropertyConstant) {
				return inferContractFromPropExp(((PropertyConstant) anv).getConstantValue());
			}
		}

		return Nenola.Prim.ErrorContract;

	}

	public static Nenola.Contract inferContract(Expr expr) {

		if (expr instanceof SelectionExpr) {

			Expr target = ((SelectionExpr) expr).getTarget();
			Nenola.Contract targetType = inferContract(target);
			String name = ((SelectionExpr) expr).getField().getName();

			if (targetType instanceof Nenola.RecordContract) {
				Map<String, Nenola.DataContract> fields = ((Nenola.RecordContract) targetType).fields;
				for (Entry<String, Nenola.DataContract> entry : fields.entrySet()) {
					if (entry.getKey().equals(name)) {
						return entry.getValue();
					}
				}

			} else if (targetType instanceof Nenola.NodeContract) {
				Map<String, Nenola.Channel> channels = ((Nenola.NodeContract) targetType).channels;
				for (Entry<String, Nenola.Channel> entry : channels.entrySet()) {
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
					return Nenola.Prim.BoolContract;
				case "_COUNT":
					return Nenola.Prim.IntContract;
				}
			}

		} else if (expr instanceof ArraySubExpr) {
			Expr arrExpr = ((ArraySubExpr) expr).getExpr();
			Nenola.Contract arrType = inferContract(arrExpr);
			if (arrType instanceof ArrayContract) {
				return ((ArrayContract) arrType).stemContract;
			}

		} else if (expr instanceof IndicesExpr) {
			Nenola.Contract arrType = inferContract(((IndicesExpr) expr).getArray());
			if (arrType instanceof ArrayContract) {
				int size = ((ArrayContract) arrType).size;
				return new ArrayContract("", Nenola.Prim.IntContract, size);
			}

		} else if (expr instanceof ForallExpr) {
			return Nenola.Prim.BoolContract;

		} else if (expr instanceof ExistsExpr) {
			return Nenola.Prim.BoolContract;

		} else if (expr instanceof FlatmapExpr) {
			Nenola.Contract innerArrType = inferContract(((FlatmapExpr) expr).getExpr());
			if (innerArrType instanceof ArrayContract) {
				Nenola.Contract stemType = ((ArrayContract) innerArrType).stemContract;
				Nenola.Contract arrType = inferContract(((FlatmapExpr) expr).getArray());

				if (arrType instanceof ArrayContract && arrType instanceof Nenola.DataContract) {
					int size = ((ArrayContract) arrType).size;
					return new ArrayContract("", (Nenola.DataContract) stemType, size);
				}
			}

		} else if (expr instanceof FoldLeftExpr) {
			return inferContract(((FoldLeftExpr) expr).getExpr());

		} else if (expr instanceof FoldRightExpr) {
			return inferContract(((FoldRightExpr) expr).getExpr());

		} else if (expr instanceof BinaryExpr) {
			Nenola.Contract leftType = inferContract(((BinaryExpr) expr).getLeft());
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
				return Nenola.Prim.BoolContract;
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
			return Nenola.Prim.IntContract;

		} else if (expr instanceof RealLitExpr) {
			return Nenola.Prim.RealContract;

		} else if (expr instanceof BoolLitExpr) {
			return Nenola.Prim.BoolContract;

		} else if (expr instanceof FloorCast) {
			return Nenola.Prim.IntContract;

		} else if (expr instanceof RealCast) {
			return Nenola.Prim.RealContract;

		} else if (expr instanceof EventExpr) {
			return Nenola.Prim.BoolContract;

		} else if (expr instanceof TimeExpr) {
			return Nenola.Prim.RealContract;

		} else if (expr instanceof EnumLitExpr) {
			return toContractFromType(((EnumLitExpr) expr).getEnumType());

		} else if (expr instanceof LatchedExpr) {
			return inferContract(((LatchedExpr) expr).getExpr());

		} else if (expr instanceof TimeOfExpr) {
			return Nenola.Prim.RealContract;

		} else if (expr instanceof TimeRiseExpr) {
			return Nenola.Prim.RealContract;

		} else if (expr instanceof TimeFallExpr) {
			return Nenola.Prim.RealContract;

		} else if (expr instanceof TimeOfExpr) {
			return Nenola.Prim.RealContract;

		} else if (expr instanceof TimeRiseExpr) {
			return Nenola.Prim.RealContract;

		} else if (expr instanceof TimeFallExpr) {
			return Nenola.Prim.RealContract;

		} else if (expr instanceof PreExpr) {
			return inferContract(((PreExpr) expr).getExpr());

		} else if (expr instanceof ArrayLiteralExpr) {
			EList<Expr> elems = ((ArrayLiteralExpr) expr).getElems();
			Expr first = elems.get(0);
			int size = elems.size();
			Nenola.Contract firstType = inferContract(first);

			if (firstType instanceof Nenola.DataContract) {
				return new ArrayContract("", (Nenola.DataContract) firstType, size);
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
					return Nenola.Prim.RealContract;
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
					return Nenola.Prim.RealContract;
				} else if (namedEl instanceof LibraryFnDef) {
					LibraryFnDef fnDef = (LibraryFnDef) namedEl;
					return toContractFromType(fnDef.getType());
				}

			}

		}
		return Nenola.Prim.ErrorContract;

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



	private static Map<String, Contract> extractContractMapFromArgs(List<Arg> args) {

		Map<String, Contract> result = new HashMap<>();

		for (Arg arg : args) {
			Contract contract = toContractFromType(arg.getType());
			result.put(contract.getName(), contract);
		}

		return result;

	}

	private static Map<String, Contract> extractContractMapFromSpecStatement(SpecStatement spec) {

		Map<String, Contract> result = new HashMap<>();
		if (spec instanceof NodeDef) {
			EList<Arg> inputArgs = ((NodeDef) spec).getArgs();
			EList<Arg> outputArgs = ((NodeDef) spec).getRets();
			EList<Arg> internalArgs = ((NodeDef) spec).getNodeBody().getLocs();

			List<Arg> argList = new ArrayList<>();
			argList.addAll(inputArgs);
			argList.addAll(outputArgs);
			argList.addAll(internalArgs);

			result.putAll(extractContractMapFromArgs(argList));

		} else if (spec instanceof FnDef) {
			List<Arg> argList = new ArrayList<>();
			argList.addAll(((FnDef) spec).getArgs());
			result.putAll(extractContractMapFromArgs(argList));

		} else if (spec instanceof LinearizationDef) {
			List<Arg> argList = new ArrayList<>();
			argList.addAll(((LinearizationDef) spec).getArgs());
			result.putAll(extractContractMapFromArgs(argList));

		} else if (spec instanceof OutputStatement) {
			List<Arg> argList = new ArrayList<>();
			argList.addAll(((OutputStatement) spec).getLhs());
			result.putAll(extractContractMapFromArgs(argList));

		} else if (spec instanceof InputStatement) {
			List<Arg> argList = new ArrayList<>();
			argList.addAll(((InputStatement) spec).getLhs());
			result.putAll(extractContractMapFromArgs(argList));

		}

		return result;
	}


	public static Map<String, Nenola.Contract> extractContractMap(ComponentClassifier c) {
		Map<String, Nenola.Contract> result = new HashMap<String, Nenola.Contract>();
		AgreeContractSubclause annex = getAgreeAnnex(c);
		if (annex != null) {
			AgreeContract contract = (AgreeContract) annex.getContract();

			for (SpecStatement spec : contract.getSpecs()) {

				Map<String, Nenola.Contract> specResult = extractContractMapFromSpecStatement(spec);
				result.putAll(specResult);

			}

		}

		if (c instanceof ComponentImplementation) {
			Map<String, Nenola.Contract> ccResult = extractContractMap(((ComponentImplementation) c).getType());
			result.putAll(ccResult);

			for (Subcomponent sub : ((ComponentImplementation) c).getAllSubcomponents()) {

				Nenola.Contract contract = toContractFromClassifier(sub.getClassifier());
				result.put(contract.getName(), contract);

				Map<String, Nenola.Contract> subResult = extractContractMap(sub.getClassifier());
				result.putAll(subResult);
			}
		} else if (c instanceof ComponentClassifier) {
			for (Feature feature : c.getAllFeatures()) {

				Nenola.Contract contract = toContractFromClassifier(feature.getClassifier());
				result.put(contract.getName(), contract);
			}
		}
		return result;
	}

	private static AgreeContractSubclause getAgreeAnnex(ComponentClassifier comp) {
		for (AnnexSubclause annex : AnnexUtil.getAllAnnexSubclauses(comp,
				AgreePackage.eINSTANCE.getAgreeContractSubclause())) {
			if (annex instanceof AgreeContractSubclause) {
				// in newer versions of osate the annex this returns annexes in
				// the type
				// as well as the implementation. We want the annex in the
				// specific component
				EObject container = annex.eContainer();
				while (!(container instanceof ComponentClassifier)) {
					container = container.eContainer();
				}
				if (container == comp) {
					return (AgreeContractSubclause) annex;
				}
			}
		}
		return null;
	}

	public static String getNodeName(NamedElement nodeDef) {
		EObject container = nodeDef.eContainer();
		List<String> segments = new ArrayList<>();

		segments.add(nodeDef.getName());
		while (container != null) {
			if (container instanceof ComponentClassifier || container instanceof AadlPackage) {
				segments.add(0, ((NamedElement) container).getName().replace(".", "__"));
			}
			container = container.eContainer();
		}

		return String.join("__", segments);
	}

	public static Nenola.Channel toChannelFromArg(Arg arg, Nenola.Direc direc) {
		Nenola.Contract contract = toContractFromType(arg.getType());

		return new Nenola.Channel(arg.getName(), (DataContract) contract, direc, false);

	}

	public static Map<String, Nenola.Channel> toChannelsFromArgs(EList<Arg> args, Nenola.Direc direc) {
		Map<String, Nenola.Channel> channels = new HashMap<>();
		for (Arg arg : args) {
			Nenola.Channel channel = toChannelFromArg(arg, direc);
			channels.put(channel.name, channel);
		}
		return channels;
	}

	public static Nenola.Expr toExprFromExpr(Expr expr) {
		// TODO Auto-generated method stub
		return null;
	}

	public static NodeGen toNodeGenFromNodeDef(NodeDef nodeDef) {

		String nodeName = getNodeName(nodeDef);

		Map<String, Nenola.Channel> inputs = toChannelsFromArgs(nodeDef.getArgs(), new Nenola.In());
		Map<String, Nenola.Channel> outputs = toChannelsFromArgs(nodeDef.getRets(), new Nenola.Out(Optional.empty()));
		Map<String, Nenola.Channel> internals = toChannelsFromArgs(nodeDef.getNodeBody().getLocs(), new Nenola.Bi());

		Map<String, Nenola.Channel> channels = new HashMap<>();
		channels.putAll(inputs);
		channels.putAll(outputs);
		channels.putAll(internals);

		List<Nenola.DataFlow> dataFlows = new ArrayList<>();
		List<String> properties = new ArrayList<>();

		String lemmaName = "__nodeLemma";
		int lemmaIndex = 0;
		for (NodeStmt stmt : nodeDef.getNodeBody().getStmts()) {
			if (stmt instanceof NodeLemma) {
				NodeLemma nodeLemma = (NodeLemma) stmt;
				String propName = lemmaName + lemmaIndex++;
				internals.put(propName, new Nenola.Channel(propName, Nenola.Prim.BoolContract, new Nenola.Bi(), false));
				Nenola.Expr eqExpr = toExprFromExpr(nodeLemma.getExpr());
				Nenola.DataFlow eq = new Nenola.DataFlow(propName, eqExpr);
				dataFlows.add(eq);
				properties.add(propName);
			} else if (stmt instanceof NodeEq) {
				Nenola.Expr expr = toExprFromExpr(stmt.getExpr());
				List<String> ids = new ArrayList<>();
				for (Arg arg : ((NodeEq) stmt).getLhs()) {
					ids.add(arg.getName());
				}
				Nenola.DataFlow df = new Nenola.DataFlow(ids, expr);
				dataFlows.add(df);
			}
		}

		return new Nenola.NodeGen(nodeName, channels, dataFlows, properties);

	}


	private static NodeGen toNodeGenFromFnDef(FnDef fnDef) {
		String nodeName = getNodeName(fnDef).replace("::", "__");
		Map<String, Nenola.Channel> channels = toChannelsFromArgs(fnDef.getArgs(), new Nenola.In());
		Nenola.Expr bodyExpr = toExprFromExpr(fnDef.getExpr());

		Nenola.Contract outContract = toContractFromType(fnDef.getType());

		String outputName = "_outvar";
		Nenola.Channel output = new Nenola.Channel(outputName, (DataContract) outContract,
				new Nenola.Out(Optional.empty()), false);

		channels.put(outputName, output);

		Nenola.DataFlow dataFlow = new Nenola.DataFlow("_outvar", bodyExpr);
		List<Nenola.DataFlow> dataFlows = Collections.singletonList(dataFlow);

		return new NodeGen(nodeName, channels, dataFlows, Collections.emptyList());

	}

	public static Map<String, Nenola.NodeGen> extractNodeGenMap(ComponentClassifier c) {
		Map<String, Nenola.NodeGen> result = new HashMap<String, Nenola.NodeGen>();
		AgreeContractSubclause annex = getAgreeAnnex(c);
		if (annex != null) {
			AgreeContract contract = (AgreeContract) annex.getContract();

			for (SpecStatement spec : contract.getSpecs()) {
				if (spec instanceof NodeDef) {
					Nenola.NodeGen ng = toNodeGenFromNodeDef((NodeDef) spec);
					result.putIfAbsent(ng.name, ng);
				} else if (spec instanceof FnDef) {
					Nenola.NodeGen ng = toNodeGenFromFnDef((FnDef) spec);
					result.putIfAbsent(ng.name, ng);
				}
			}

		}

		if (c instanceof ComponentImplementation) {
			Map<String, Nenola.NodeGen> ccResult = extractNodeGenMap(((ComponentImplementation) c).getType());
			result.putAll(ccResult);

			for (Subcomponent sub : ((ComponentImplementation) c).getAllSubcomponents()) {
				Map<String, Nenola.NodeGen> subResult = extractNodeGenMap(sub.getClassifier());
				result.putAll(subResult);
			}
		}
		return result;
	}

	public static Nenola.Program toProgram(ComponentImplementation ci) {
		Nenola.Contract main = toContractFromClassifier(ci);
		Map<String, Nenola.Contract> specMap = extractContractMap(ci);
		Map<String, Nenola.NodeGen> nodeGenMap = extractNodeGenMap(ci);
		return new Nenola.Program(main, specMap, nodeGenMap);
	}

}