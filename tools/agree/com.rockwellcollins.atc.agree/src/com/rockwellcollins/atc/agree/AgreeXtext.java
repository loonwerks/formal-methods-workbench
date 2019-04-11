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
import org.osate.aadl2.BooleanLiteral;
import org.osate.aadl2.Classifier;
import org.osate.aadl2.ClassifierValue;
import org.osate.aadl2.ComponentClassifier;
import org.osate.aadl2.ComponentImplementation;
import org.osate.aadl2.ComponentType;
import org.osate.aadl2.ConnectedElement;
import org.osate.aadl2.Connection;
import org.osate.aadl2.ConnectionEnd;
import org.osate.aadl2.Context;
import org.osate.aadl2.DataImplementation;
import org.osate.aadl2.DataType;
import org.osate.aadl2.DirectionType;
import org.osate.aadl2.EnumerationLiteral;
import org.osate.aadl2.EventDataPort;
import org.osate.aadl2.EventPort;
import org.osate.aadl2.Feature;
import org.osate.aadl2.FeatureGroup;
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
import com.rockwellcollins.atc.agree.Nenola.Channel;
import com.rockwellcollins.atc.agree.Nenola.Contract;
import com.rockwellcollins.atc.agree.Nenola.DataContract;
import com.rockwellcollins.atc.agree.Nenola.EnumContract;
import com.rockwellcollins.atc.agree.Nenola.Interval;
import com.rockwellcollins.atc.agree.Nenola.NodeGen;
import com.rockwellcollins.atc.agree.Nenola.Prim;
import com.rockwellcollins.atc.agree.Nenola.RangeIntContract;
import com.rockwellcollins.atc.agree.Nenola.RangeRealContract;
import com.rockwellcollins.atc.agree.Nenola.Spec;
import com.rockwellcollins.atc.agree.agree.AgreeContract;
import com.rockwellcollins.atc.agree.agree.AgreeContractSubclause;
import com.rockwellcollins.atc.agree.agree.AgreePackage;
import com.rockwellcollins.atc.agree.agree.AlwaysStatement;
import com.rockwellcollins.atc.agree.agree.Arg;
import com.rockwellcollins.atc.agree.agree.ArrayLiteralExpr;
import com.rockwellcollins.atc.agree.agree.ArraySubExpr;
import com.rockwellcollins.atc.agree.agree.ArrayType;
import com.rockwellcollins.atc.agree.agree.ArrayUpdateExpr;
import com.rockwellcollins.atc.agree.agree.AssertEqualStatement;
import com.rockwellcollins.atc.agree.agree.AssertStatement;
import com.rockwellcollins.atc.agree.agree.AssumeStatement;
import com.rockwellcollins.atc.agree.agree.BinaryExpr;
import com.rockwellcollins.atc.agree.agree.BoolLitExpr;
import com.rockwellcollins.atc.agree.agree.BoolOutputStatement;
import com.rockwellcollins.atc.agree.agree.CallExpr;
import com.rockwellcollins.atc.agree.agree.ClosedTimeInterval;
import com.rockwellcollins.atc.agree.agree.ComponentRef;
import com.rockwellcollins.atc.agree.agree.ConnectionStatement;
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
import com.rockwellcollins.atc.agree.agree.GuaranteeStatement;
import com.rockwellcollins.atc.agree.agree.IfThenElseExpr;
import com.rockwellcollins.atc.agree.agree.IndicesExpr;
import com.rockwellcollins.atc.agree.agree.InputStatement;
import com.rockwellcollins.atc.agree.agree.IntLitExpr;
import com.rockwellcollins.atc.agree.agree.LatchedExpr;
import com.rockwellcollins.atc.agree.agree.LemmaStatement;
import com.rockwellcollins.atc.agree.agree.LibraryFnDef;
import com.rockwellcollins.atc.agree.agree.LinearizationDef;
import com.rockwellcollins.atc.agree.agree.NamedElmExpr;
import com.rockwellcollins.atc.agree.agree.NamedID;
import com.rockwellcollins.atc.agree.agree.NodeDef;
import com.rockwellcollins.atc.agree.agree.NodeEq;
import com.rockwellcollins.atc.agree.agree.NodeLemma;
import com.rockwellcollins.atc.agree.agree.NodeStmt;
import com.rockwellcollins.atc.agree.agree.OpenLeftTimeInterval;
import com.rockwellcollins.atc.agree.agree.OpenRightTimeInterval;
import com.rockwellcollins.atc.agree.agree.OpenTimeInterval;
import com.rockwellcollins.atc.agree.agree.OutputStatement;
import com.rockwellcollins.atc.agree.agree.PatternStatement;
import com.rockwellcollins.atc.agree.agree.PeriodicStatement;
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
import com.rockwellcollins.atc.agree.agree.SporadicStatement;
import com.rockwellcollins.atc.agree.agree.TagExpr;
import com.rockwellcollins.atc.agree.agree.ThisRef;
import com.rockwellcollins.atc.agree.agree.TimeExpr;
import com.rockwellcollins.atc.agree.agree.TimeFallExpr;
import com.rockwellcollins.atc.agree.agree.TimeInterval;
import com.rockwellcollins.atc.agree.agree.TimeOfExpr;
import com.rockwellcollins.atc.agree.agree.TimeRiseExpr;
import com.rockwellcollins.atc.agree.agree.Type;
import com.rockwellcollins.atc.agree.agree.UnaryExpr;
import com.rockwellcollins.atc.agree.agree.WhenHoldsStatement;
import com.rockwellcollins.atc.agree.agree.WhenOccursStatment;
import com.rockwellcollins.atc.agree.agree.WheneverBecomesTrueStatement;
import com.rockwellcollins.atc.agree.agree.WheneverHoldsStatement;
import com.rockwellcollins.atc.agree.agree.WheneverImpliesStatement;
import com.rockwellcollins.atc.agree.agree.WheneverOccursStatement;

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

	private static Interval toInterval(TimeInterval interval) {
		boolean lowOpen = false;
		Nenola.Expr low = toExprFromExpr(interval.getLow());
		Nenola.Expr high = toExprFromExpr(interval.getHigh());
		boolean highOpen = false;

		if (interval instanceof OpenTimeInterval) {
			lowOpen = true;
			highOpen = true;
		} else if (interval instanceof OpenLeftTimeInterval) {
			lowOpen = true;
			highOpen = false;
		} else if (interval instanceof OpenRightTimeInterval) {
			lowOpen = false;
			highOpen = true;
		} else if (interval instanceof ClosedTimeInterval) {
			lowOpen = false;
			highOpen = false;
		}

		return new Nenola.Interval(lowOpen, low, high, highOpen);
	}

	private static Nenola.Prop extractPropFromPattern(PatternStatement pattern) {
		if (pattern instanceof AlwaysStatement) {
			Nenola.Expr expr = toExprFromExpr(((AlwaysStatement) pattern).getExpr());
			return new Nenola.AlwaysProp(expr);
		} else if (pattern instanceof PeriodicStatement) {
			Nenola.Expr event = toExprFromExpr(((PeriodicStatement) pattern).getEvent());

			Expr jitter = ((PeriodicStatement) pattern).getJitter();
			Optional<Nenola.Expr> jitterOp = jitter != null ?
				Optional.of(toExprFromExpr(jitter)) : Optional.empty();

			Nenola.Expr period = toExprFromExpr(((PeriodicStatement) pattern).getPeriod());

			return new Nenola.PeriodicProp(event, period, jitterOp);
		} else if (pattern instanceof SporadicStatement) {
			Nenola.Expr event = toExprFromExpr(((SporadicStatement) pattern).getEvent());
			Expr jitter = ((SporadicStatement) pattern).getJitter();
			Optional<Nenola.Expr> jitterOp = (jitter != null) ?
				Optional.of(toExprFromExpr(jitter)) : Optional.empty();

			Nenola.Expr iat = toExprFromExpr(((SporadicStatement) pattern).getIat());

			return new Nenola.SporadicProp(event, iat, jitterOp);
		} else if (pattern instanceof WheneverHoldsStatement ) {
			Nenola.Expr cause = toExprFromExpr(((WheneverHoldsStatement) pattern).getCause());
			Nenola.Expr effect = toExprFromExpr(((WheneverHoldsStatement) pattern).getEffect());
			boolean exclusive = ((WheneverHoldsStatement) pattern).getExcl() != null;

			Nenola.Interval interval = toInterval(((WheneverHoldsStatement) pattern).getInterval());

			return new Nenola.WheneverHoldsProp(cause, effect, exclusive, interval);
		} else if (pattern instanceof WheneverImpliesStatement) {
			throw new RuntimeException("We do not support this pattern currently");

		} else if (pattern instanceof WheneverOccursStatement) {
			Nenola.Expr cause = toExprFromExpr(((WheneverOccursStatement) pattern).getCause());
			Nenola.Expr effect = toExprFromExpr(((WheneverOccursStatement) pattern).getEffect());
			boolean exclusive = ((WheneverOccursStatement) pattern).getExcl() != null;
			Interval interval = toInterval(((WheneverOccursStatement) pattern).getInterval());

			return new Nenola.WheneverOccursProp(cause, effect, exclusive, interval);

		} else if (pattern instanceof WheneverBecomesTrueStatement) {
			Nenola.Expr cause = toExprFromExpr(((WheneverBecomesTrueStatement) pattern).getCause());
			Nenola.Expr effect = toExprFromExpr(((WheneverBecomesTrueStatement) pattern).getEffect());
			boolean exclusive = ((WheneverBecomesTrueStatement) pattern).getExcl() != null;
			Nenola.Interval interval = toInterval(((WheneverBecomesTrueStatement) pattern).getInterval());
			return new Nenola.WheneverBecomesTrueProp(cause, effect, exclusive, interval);
		} else if (pattern instanceof WhenHoldsStatement) {
			Nenola.Expr condition = toExprFromExpr(((WhenHoldsStatement) pattern).getCondition());
			Nenola.Expr effect = toExprFromExpr(((WhenHoldsStatement) pattern).getEvent());
			boolean exclusive = ((WhenHoldsStatement) pattern).getExcl() != null;
			Nenola.Interval conditionInterval = toInterval(((WhenHoldsStatement) pattern).getConditionInterval());
			Nenola.Interval effectInterval = toInterval(((WhenHoldsStatement) pattern).getEventInterval());

			return new Nenola.WhenHoldsProp(condition, conditionInterval, effect, exclusive, effectInterval);

		} else if (pattern instanceof WhenOccursStatment) {
			Nenola.Expr condition = toExprFromExpr(((WhenOccursStatment) pattern).getCondition());
			Nenola.Expr frequency = toExprFromExpr(((WhenOccursStatment) pattern).getTimes());
			boolean exclusive = ((WhenOccursStatment) pattern).getExcl() != null;
			Nenola.Interval interval = toInterval(((WhenOccursStatment) pattern).getInterval());
			Nenola.Expr event = toExprFromExpr(((WhenOccursStatment) pattern).getEvent());

			return new  Nenola.WhenOccursProp(condition, frequency, interval, exclusive, event);
		}

		throw new RuntimeException("Pattern not recognized: " + pattern);


	}


	private static Nenola.Prop extractPropFromExpr(Expr expr) {
		Nenola.Expr nenolaExpr = toExprFromExpr(expr);
		return new Nenola.ExprProp(nenolaExpr);
	}

	private static Spec extractSpecFromSpecStatement(SpecStatement spec) {

		if (spec instanceof AssertStatement) {
			String name = ((AssertStatement) spec).getName();
			String description = ((AssertStatement) spec).getStr();
			Nenola.Prop prop = null;
			if (((AssertStatement) spec).getExpr() != null) {
				prop = extractPropFromExpr(((AssertStatement) spec).getExpr());
			} else if (((AssertStatement) spec).getPattern() != null) {
				prop = extractPropFromPattern(((AssertStatement) spec).getPattern());
			}

			return new Nenola.Spec(Nenola.SpecTag.Assert, name, description, prop);

		} else if (spec instanceof AssertEqualStatement) {
			String name = "";
			String description = "";

			Nenola.Expr leftExpr = new Nenola.IdExpr(((AssertEqualStatement) spec).getId().getName());
			Nenola.Expr rightExpr = toExprFromExpr(((AssertEqualStatement) spec).getExpr());
			Nenola.Expr expr = new Nenola.BinExpr(leftExpr, Nenola.Rator.Equal, rightExpr);
			Nenola.Prop prop = new Nenola.ExprProp(expr);
			return new Nenola.Spec(Nenola.SpecTag.Assert, name, description, prop);

		} else if (spec instanceof LemmaStatement) {
			String name = ((LemmaStatement) spec).getName();
			String description = ((LemmaStatement) spec).getStr();
			Nenola.Prop prop = null;
			if (((LemmaStatement) spec).getExpr() != null) {
				prop = extractPropFromExpr(((LemmaStatement) spec).getExpr());
			} else if (((LemmaStatement) spec).getPattern() != null) {
				prop = extractPropFromPattern(((LemmaStatement) spec).getPattern());
			}

			return new Nenola.Spec(Nenola.SpecTag.Lemma, name, description, prop);

		} else if (spec instanceof AssumeStatement) {
			String name = ((AssumeStatement) spec).getName();
			String description = ((AssumeStatement) spec).getStr();
			Nenola.Prop prop = null;
			if (((AssumeStatement) spec).getExpr() != null) {
				prop = extractPropFromExpr(((AssumeStatement) spec).getExpr());
			} else if (((AssumeStatement) spec).getPattern() != null) {
				prop = extractPropFromPattern(((AssumeStatement) spec).getPattern());
			}

			return new Nenola.Spec(Nenola.SpecTag.Assume, name, description, prop);

		} else if (spec instanceof GuaranteeStatement) {
			String name = ((GuaranteeStatement) spec).getName();
			String description = ((GuaranteeStatement) spec).getStr();
			Nenola.Prop prop = null;
			if (((GuaranteeStatement) spec).getExpr() != null) {
				prop = extractPropFromExpr(((GuaranteeStatement) spec).getExpr());
			} else if (((GuaranteeStatement) spec).getPattern() != null) {
				prop = extractPropFromPattern(((GuaranteeStatement) spec).getPattern());
			}

			return new Nenola.Spec(Nenola.SpecTag.Guarantee, name, description, prop);

		}

		return null;
	}


	private static List<Nenola.Spec> extractSpecList(ComponentClassifier c) {
		List<Nenola.Spec> result = new ArrayList<>();
		AgreeContractSubclause annex = getAgreeAnnex(c);
		if (annex != null) {
			AgreeContract contract = (AgreeContract) annex.getContract();

			for (SpecStatement specStatement : contract.getSpecs()) {

				Nenola.Spec spec = extractSpecFromSpecStatement(specStatement);
				if (spec != null) {
					result.add(spec);
				}

			}

		}

		if (c instanceof ComponentImplementation) {
			List<Nenola.Spec> ccResult = extractSpecList(((ComponentImplementation) c).getType());
			result.addAll(ccResult);
		}

		return result;
	}


	private static Nenola.Expr toExprFromConnectedElement(ConnectedElement ce) {
		Context context = ce.getContext();
		ConnectionEnd end = ce.getConnectionEnd();

		Nenola.Expr idExpr = new Nenola.IdExpr(context.getName());
		return new Nenola.SelectionExpr(idExpr, end.getName());

	}

	private static List<Nenola.Connection> extractConnections(ComponentImplementation currClsfr) {

		Map<String, Expr> exprMap = new HashMap<>();

		AgreeContractSubclause annex = getAgreeAnnex(currClsfr);
		if (annex != null) {
			AgreeContract contract = (AgreeContract) annex.getContract();

			for (SpecStatement specStatement : contract.getSpecs()) {
				if (specStatement instanceof ConnectionStatement) {
					String connName = ((ConnectionStatement) specStatement).getConn().getName();
					Expr expr = ((ConnectionStatement) specStatement).getExpr();
					exprMap.put(connName, expr);
				}
			}
		}

		List<Nenola.Connection> connections = new ArrayList<>();
		for (Connection aadlConn : currClsfr.getAllConnections()) {
			String name = aadlConn.getName();

			Nenola.Expr sourceExpr = toExprFromConnectedElement(aadlConn.getSource());
			Nenola.Expr destExpr = toExprFromConnectedElement(aadlConn.getDestination());

			// TODO handle featureGroup case

			Expr expr = exprMap.get(name);
			Optional<Nenola.Expr> connExpr = expr == null ? Optional.empty() : Optional.of(toExprFromExpr(expr));

			Nenola.Connection conn = new Nenola.Connection(name, sourceExpr, destExpr, connExpr);

			connections.add(conn);

		}

		return connections;
	}


	private static Map<String, Channel> extractChannels(String prefix, Classifier ct) {
		Map<String, Channel> channels = new HashMap<>();
		EList<Feature> features = ct.getAllFeatures();
		for (Feature feature : features) {


			if (feature instanceof FeatureGroup) {

				channels.putAll(extractChannels(prefix + feature.getName() + "__", feature.getClassifier()));

			} else {
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

					String fieldName = prefix + feature.getName();
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
								DataContract typeDef = new ArrayContract("", (Nenola.DataContract) stem,
										size);
								Nenola.Channel channel = new Nenola.Channel(fieldName, typeDef, direction,
										isEvent);
								channels.putIfAbsent(fieldName, channel);
							}

						}
					}
				}
			}
		}

		for (AnnexSubclause annex : AnnexUtil.getAllAnnexSubclauses(ct,
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

		return channels;
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
			List<Nenola.Connection> connections = new ArrayList<>();
			List<Nenola.Spec> specs = new ArrayList<Nenola.Spec>();

			Classifier currClsfr = c;
			while (currClsfr != null) {

				List<Nenola.Spec> localSpecs = extractSpecList((ComponentClassifier) currClsfr);
				specs.addAll(localSpecs);

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


					connections.addAll(extractConnections((ComponentImplementation) currClsfr));


					ct = ((ComponentImplementation) currClsfr).getType();
				} else if (c instanceof ComponentType) {
					ct = (ComponentType) currClsfr;
				}

				if (ct != null) {

					channels.putAll(extractChannels("", ct));


				}

				currClsfr = currClsfr.getExtended();
			}

			String name = c.getQualifiedName();

			return new Nenola.NodeContract(name, channels, subNodes, connections, specs, c);

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


	public static Map<String, Nenola.Contract> extractContractMap(Classifier classifier) {
		Map<String, Nenola.Contract> result = new HashMap<String, Nenola.Contract>();
		AgreeContractSubclause annex = getAgreeAnnex(classifier);
		if (annex != null) {
			AgreeContract contract = (AgreeContract) annex.getContract();

			for (SpecStatement spec : contract.getSpecs()) {

				Map<String, Nenola.Contract> specResult = extractContractMapFromSpecStatement(spec);
				result.putAll(specResult);

			}

		}

		if (classifier instanceof ComponentImplementation) {
			Map<String, Nenola.Contract> ccResult = extractContractMap(
					((ComponentImplementation) classifier).getType());
			result.putAll(ccResult);

			for (Subcomponent sub : ((ComponentImplementation) classifier).getAllSubcomponents()) {

				Nenola.Contract contract = toContractFromClassifier(sub.getClassifier());
				result.put(contract.getName(), contract);

				Map<String, Nenola.Contract> subResult = extractContractMap(sub.getClassifier());
				result.putAll(subResult);
			}
		} else if (classifier instanceof ComponentClassifier) {
			for (Feature feature : classifier.getAllFeatures()) {

				Nenola.Contract contract = toContractFromClassifier(feature.getClassifier());
				result.put(contract.getName(), contract);

				Map<String, Nenola.Contract> subResult = extractContractMap(feature.getClassifier());
				result.putAll(subResult);
			}
		}
		return result;
	}

	private static AgreeContractSubclause getAgreeAnnex(Classifier classifier) {
		for (AnnexSubclause annex : AnnexUtil.getAllAnnexSubclauses(classifier,
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
				if (container == classifier) {
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

	private static Optional<Nenola.Expr> toChainedExpr(SelectionExpr expr, String chain) {
		Expr target = expr.getTarget();
		NamedElement field = expr.getField();
		String fieldName = field.getName();

		if (target instanceof NamedElmExpr) {
			NamedElement ne = ((NamedElmExpr) target).getElm();
			if (ne instanceof FeatureGroup) {
				return Optional.of(new Nenola.IdExpr(ne.getName() + "__" + fieldName + "__" + chain));
			} else if (ne instanceof Subcomponent) {

				if (field instanceof ConstStatement) {
					// constant propagation
					return Optional.of(toExprFromExpr(((ConstStatement) field).getExpr()));
				}

				return Optional.of(new Nenola.SelectionExpr(new Nenola.IdExpr(ne.getName()), fieldName + "__" + chain));
			}
		} else if (target instanceof SelectionExpr) {
			return toChainedExpr((SelectionExpr) target, fieldName + "__" + chain);

		}

		return Optional.empty();


	}


	public static Nenola.Expr toExprFromExpr(Expr expr) {

		if (expr instanceof SelectionExpr) {

			Optional<Nenola.Expr> chainOp = toChainedExpr((SelectionExpr) expr, "");

			if (chainOp.isPresent()) {
				return chainOp.get();
			} else {

				Expr target = ((SelectionExpr) expr).getTarget();
				Nenola.Expr nenolaTarget = toExprFromExpr(target);
				String selection = ((SelectionExpr) expr).getField().getName();
				return new Nenola.SelectionExpr(nenolaTarget, selection);

			}


		} else if (expr instanceof TagExpr) {

			String tag = ((TagExpr) expr).getTag();
			Nenola.Expr target = toExprFromExpr(((TagExpr) expr).getStem());
			if (tag != null) {
				switch (tag) {
				case "_CLK":
					return new Nenola.TagExpr(target, Nenola.Tag.Clock);
				case "_INSERT":
					return new Nenola.TagExpr(target, Nenola.Tag.Insert);
				case "_REMOVE":
					return new Nenola.TagExpr(target, Nenola.Tag.Remove);
				case "_COUNT":
					return new Nenola.TagExpr(target, Nenola.Tag.Count);
				}
			}

		} else if (expr instanceof ArraySubExpr) {
			Expr arrExpr = ((ArraySubExpr) expr).getExpr();
			Expr index = ((ArraySubExpr) expr).getIndex();
			return new Nenola.ArraySubExpr(toExprFromExpr(arrExpr), toExprFromExpr(index));

		} else if (expr instanceof IndicesExpr) {
			Expr array = ((IndicesExpr) expr).getArray();
			return new Nenola.IndicesExpr(toExprFromExpr(array));

		} else if (expr instanceof ForallExpr) {

			String binding = ((ForallExpr) expr).getBinding().getName();
			Expr array = ((ForallExpr) expr).getArray();
			Expr body = ((ForallExpr) expr).getExpr();

			return new Nenola.ForallExpr(binding, toExprFromExpr(array), toExprFromExpr(body));

		} else if (expr instanceof ExistsExpr) {

			String binding = ((ExistsExpr) expr).getBinding().getName();
			Expr array = ((ExistsExpr) expr).getArray();
			Expr body = ((ExistsExpr) expr).getExpr();

			return new Nenola.ExistsExpr(binding, toExprFromExpr(array), toExprFromExpr(body));

		} else if (expr instanceof FlatmapExpr) {
			String binding = ((FlatmapExpr) expr).getBinding().getName();
			Expr array = ((FlatmapExpr) expr).getArray();
			Expr body = ((FlatmapExpr) expr).getExpr();

			return new Nenola.FlatmapExpr(binding, toExprFromExpr(array), toExprFromExpr(body));


		} else if (expr instanceof FoldLeftExpr) {
			String binding = ((FlatmapExpr) expr).getBinding().getName();
			Expr array = ((FlatmapExpr) expr).getArray();
			String acc = ((FoldLeftExpr) expr).getAccumulator().getName();
			Expr init = ((FoldLeftExpr) expr).getInitial();
			Expr update = ((FoldLeftExpr) expr).getExpr();

			return new Nenola.FoldLeftExpr(binding, toExprFromExpr(array), acc, toExprFromExpr(init),
					toExprFromExpr(update));


		} else if (expr instanceof FoldRightExpr) {
			String binding = ((FlatmapExpr) expr).getBinding().getName();
			Expr array = ((FlatmapExpr) expr).getArray();
			String acc = ((FoldRightExpr) expr).getAccumulator().getName();
			Expr init = ((FoldLeftExpr) expr).getInitial();
			Expr update = ((FoldRightExpr) expr).getExpr();

			return new Nenola.FoldRightExpr(binding, toExprFromExpr(array), acc, toExprFromExpr(init),
					toExprFromExpr(update));

		} else if (expr instanceof BinaryExpr) {

			Expr left = ((BinaryExpr) expr).getLeft();
			Expr right = ((BinaryExpr) expr).getRight();
			Nenola.Rator rator = null;

			switch (((BinaryExpr) expr).getOp()) {
			case "->":
				rator = Nenola.Rator.StreamCons;
			case "=>":
				rator = Nenola.Rator.Implies;
			case "<=>":
				rator = Nenola.Rator.Equiv;
			case "and":
				rator = Nenola.Rator.Conj;
			case "or":
				rator = Nenola.Rator.Disj;
			case "<>":
				rator = Nenola.Rator.NotEqual;
			case "!=":
				rator = Nenola.Rator.NotEqual;
			case "<":
				rator = Nenola.Rator.LessThan;
			case "<=":
				rator = Nenola.Rator.LessEq;
			case ">":
				rator = Nenola.Rator.GreatThan;
			case ">=":
				rator = Nenola.Rator.GreatEq;
			case "=":
				rator = Nenola.Rator.Equal;
			case "+":
				rator = Nenola.Rator.Plus;
			case "-":
				rator = Nenola.Rator.Minus;
			case "*":
				rator = Nenola.Rator.Mult;
			case "/":
				rator = Nenola.Rator.Div;
			case "mod":
				rator = Nenola.Rator.Mod;
			case "div":
				rator = Nenola.Rator.Div;
			case "^":
				rator = Nenola.Rator.Pow;
			}

			return new Nenola.BinExpr(toExprFromExpr(left), rator, toExprFromExpr(right));

		} else if (expr instanceof UnaryExpr) {

			Expr rand = ((UnaryExpr) expr).getExpr();
			Nenola.Rator rator = null;

			switch (((BinaryExpr) expr).getOp()) {
			case "-":
				rator = Nenola.Rator.Neg;
			case "not":
				rator = Nenola.Rator.Not;
			}

			return new Nenola.UnaryExpr(rator, toExprFromExpr(rand));

		} else if (expr instanceof IfThenElseExpr) {

			Expr a = ((IfThenElseExpr) expr).getA();
			Expr b = ((IfThenElseExpr) expr).getB();
			Expr c = ((IfThenElseExpr) expr).getC();

			return new Nenola.DistinctionExpr(toExprFromExpr(a), toExprFromExpr(b), toExprFromExpr(c));

		} else if (expr instanceof PrevExpr) {
			Expr body = ((PrevExpr) expr).getDelay();
			Expr init = ((PrevExpr) expr).getInit();

			return new Nenola.PrevExpr(toExprFromExpr(body), toExprFromExpr(init));

		} else if (expr instanceof GetPropertyExpr) {

			ComponentRef ref = ((GetPropertyExpr) expr).getComponentRef();
			String propName = ((GetPropertyExpr) expr).getProp().getName();
			if (ref instanceof ThisRef) {
				return new Nenola.LocalProperty(propName);
			} else if (ref instanceof DoubleDotRef) {
				String nodeName = ((DoubleDotRef) ref).getElm().getQualifiedName().replace("::", "__").replace(".",
						"__");
				return new Nenola.ForeignProperty(nodeName, propName);
			}

		} else if (expr instanceof IntLitExpr) {
			String val = ((IntLitExpr) expr).getVal();
			return new Nenola.IntLit(val);

		} else if (expr instanceof RealLitExpr) {
			String val = ((RealLitExpr) expr).getVal();
			return new Nenola.RealLit(val);

		} else if (expr instanceof BoolLitExpr) {
			BooleanLiteral val = ((BoolLitExpr) expr).getVal();
			return new Nenola.BoolLit(val.getValue());

		} else if (expr instanceof FloorCast) {
			Expr arg = ((FloorCast) expr).getExpr();
			return new Nenola.Floor(toExprFromExpr(arg));

		} else if (expr instanceof RealCast) {
			Expr arg = ((RealCast) expr).getExpr();
			return new Nenola.RealCast(toExprFromExpr(arg));

		} else if (expr instanceof EventExpr) {
			String arg = ((EventExpr) expr).getId().getName();
			return new Nenola.Event(arg);

		} else if (expr instanceof TimeExpr) {
			return new Nenola.Time();

		} else if (expr instanceof EnumLitExpr) {

			String contractName = ((EnumLitExpr) expr).getEnumType().getElm().getQualifiedName().replace("::", "__")
					.replace(".", "__");
			String variantName = ((EnumLitExpr) expr).getValue();

			return new Nenola.EnumLit(contractName, variantName);

		} else if (expr instanceof LatchedExpr) {
			Expr arg = ((LatchedExpr) expr).getExpr();
			return new Nenola.Latch(toExprFromExpr(arg));

		} else if (expr instanceof TimeOfExpr) {
			String arg = ((TimeOfExpr) expr).getId().getName();
			return new Nenola.TimeOf(arg);

		} else if (expr instanceof TimeRiseExpr) {
			String arg = ((TimeRiseExpr) expr).getId().getName();
			return new Nenola.TimeRise(arg);

		} else if (expr instanceof TimeFallExpr) {
			String arg = ((TimeFallExpr) expr).getId().getName();
			return new Nenola.TimeFall(arg);

		} else if (expr instanceof PreExpr) {
			Expr arg = ((PreExpr) expr).getExpr();
			return new Nenola.Pre(toExprFromExpr(arg));

		} else if (expr instanceof ArrayLiteralExpr) {
			List<Nenola.Expr> elements = new ArrayList<>();
			for (Expr e : ((ArrayLiteralExpr) expr).getElems()) {
				elements.add(toExprFromExpr(e));
			}
			return new Nenola.ArrayLit(elements);

		} else if (expr instanceof ArrayUpdateExpr) {

			List<Nenola.Expr> indices = new ArrayList<>();
			for (Expr e : ((ArrayUpdateExpr) expr).getIndices()) {
				indices.add(toExprFromExpr(e));
			}

			List<Nenola.Expr> elements = new ArrayList<>();
			for (Expr e : ((ArrayUpdateExpr) expr).getValueExprs()) {
				elements.add(toExprFromExpr(e));
			}
			return new Nenola.ArrayUpdate(indices, elements);


		} else if (expr instanceof RecordLitExpr) {

			Map<String, Nenola.Expr> fields = new HashMap<>();
			for (int i = 0; i < ((RecordLitExpr) expr).getArgs().size(); i++) {
				String key = ((RecordLitExpr) expr).getArgs().get(i).getName();
				Nenola.Expr value = toExprFromExpr(((RecordLitExpr) expr).getArgExpr().get(i));

				fields.put(key, value);

			}

			return new Nenola.RecordLit(fields);

		} else if (expr instanceof RecordUpdateExpr) {
			Nenola.Expr record = toExprFromExpr(((RecordUpdateExpr) expr).getRecord());

			String key = ((RecordUpdateExpr) expr).getKey().getName();
			Nenola.Expr value = toExprFromExpr(((RecordUpdateExpr) expr).getExpr());

			return new Nenola.RecordUpdate(record, key, value);

		} else if (expr instanceof NamedElmExpr) {
			String name = ((NamedElmExpr) expr).getElm().getName();
			return new Nenola.IdExpr(name);

		} else if (expr instanceof CallExpr) {

			NamedElement nodeDef = ((CallExpr) expr).getRef().getElm();
			EObject container = nodeDef.eContainer();
			List<String> segments = new ArrayList<>();

			segments.add(nodeDef.getName());
			while (container != null) {
				if (container instanceof ComponentClassifier || container instanceof AadlPackage) {
					segments.add(0, ((NamedElement) container).getName().replace(".", "__"));
				}
				container = container.eContainer();
			}

			String fnName = String.join("__", segments);

			List<Nenola.Expr> args = new ArrayList<>();
			for (Expr e : ((CallExpr) expr).getArgs()) {
				args.add(toExprFromExpr(e));
			}

			return new Nenola.App(fnName, args);

		}
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

		Nenola.DataFlow dataFlow = new Nenola.DataFlow(outputName, bodyExpr);
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