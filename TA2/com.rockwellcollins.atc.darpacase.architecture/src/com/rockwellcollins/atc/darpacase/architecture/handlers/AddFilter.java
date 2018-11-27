package com.rockwellcollins.atc.darpacase.architecture.handlers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.PlatformUI;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.utils.EditorUtils;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;
import org.osate.aadl2.Aadl2Package;
import org.osate.aadl2.AadlPackage;
import org.osate.aadl2.AnnexSubclause;
import org.osate.aadl2.ComponentImplementation;
import org.osate.aadl2.ConnectedElement;
import org.osate.aadl2.Connection;
import org.osate.aadl2.DataPort;
import org.osate.aadl2.DataSubcomponentType;
import org.osate.aadl2.DefaultAnnexSubclause;
import org.osate.aadl2.EventDataPort;
import org.osate.aadl2.EventPort;
import org.osate.aadl2.ModelUnit;
import org.osate.aadl2.PackageSection;
import org.osate.aadl2.Port;
import org.osate.aadl2.PortConnection;
import org.osate.aadl2.PrivatePackageSection;
import org.osate.aadl2.ProcessImplementation;
import org.osate.aadl2.PropertySet;
import org.osate.aadl2.PublicPackageSection;
import org.osate.aadl2.Realization;
import org.osate.aadl2.Subcomponent;
import org.osate.aadl2.ThreadImplementation;
import org.osate.aadl2.ThreadSubcomponent;
import org.osate.aadl2.ThreadType;
import org.osate.ui.dialogs.Dialog;

import com.rockwellcollins.atc.agree.agree.AgreeContract;
import com.rockwellcollins.atc.agree.agree.AgreeContractSubclause;
import com.rockwellcollins.atc.agree.unparsing.AgreeAnnexUnparser;
import com.rockwellcollins.atc.darpacase.architecture.CaseClaimsManager;
import com.rockwellcollins.atc.darpacase.architecture.dialogs.AddFilterDialog;
import com.rockwellcollins.atc.resolute.resolute.Expr;
import com.rockwellcollins.atc.resolute.resolute.FnCallExpr;
import com.rockwellcollins.atc.resolute.resolute.ProveStatement;
import com.rockwellcollins.atc.resolute.resolute.ResoluteSubclause;

public class AddFilter extends AadlHandler {

	static final String FILTER_COMP_TYPE_NAME = "CASE_Filter";
	static final String FILTER_PORT_IN_NAME = "filter_in";
	static final String FILTER_PORT_OUT_NAME = "filter_out";
	static final String FILTER_IMPL_NAME = "FLT";
	static final String CONNECTION_IMPL_NAME = "c";

//	private String filterComponentType;
	private String filterImplementationName;
	private String filterImplementationLanguage;
	private String filterResoluteClause;
	private String filterAgreeProperty;
	private List<String> propagatedGuarantees;

	@Override
	protected void runCommand(URI uri) {

		// Check if it is a connection
		final EObject eObj = getEObject(uri);
		if (!(eObj instanceof PortConnection)) {
			Dialog.showError("No connection is selected",
					"A connection between two components must be selected to add a filter.");
			return;
		}

		// Open wizard to enter filter info
		AddFilterDialog wizard = new AddFilterDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
//		wizard.setFilterComponentTypeInfo(getDestinationType(uri), getParentType(uri));
		wizard.setGuaranteeList(getSourceName(uri), getSourceGuarantees(uri));
		List<String> resoluteClauses = getResoluteClauses(uri);
		if (resoluteClauses == null) {
			Dialog.showError("Undefined Resolute proves",
					"Undefined Resolute prove() statements exist in the model.  Make sure all prove() statements have corresponding definitions before continuing.");
			return;
		} else {
			wizard.setResoluteClauses(resoluteClauses);
		}
		wizard.create();
		if (wizard.open() == Window.OK) {
//			filterComponentType = wizard.getFilterComponentType();
			filterImplementationLanguage = wizard.getFilterImplementationLanguage();
			filterImplementationName = wizard.getFilterImplementationName();
			if (filterImplementationName == "") {
				filterImplementationName = FILTER_IMPL_NAME;
			}
			filterResoluteClause = wizard.getResoluteClause();
			filterAgreeProperty = wizard.getAgreeProperty();
			propagatedGuarantees = wizard.getGuaranteeList();
		}
		else {
			return;
		}

		// Insert the filter component
		insertFilterComponent(uri);

		return;

	}

	/**
	 * Inserts a filter component into the model, including filter type definition
	 * and implementation (including correct wiring).  The filter is inserted at
	 * the location of the selected connection
	 * @param uri - The URI of the selected connection
	 */
	public void insertFilterComponent(URI uri) {

		// Get the active xtext editor so we can make modifications
		final XtextEditor xtextEditor = EditorUtils.getActiveXtextEditor();

		xtextEditor.getDocument().modify(new IUnitOfWork.Void<XtextResource>() {

			@Override
			public void process(final XtextResource resource) throws Exception {

				// Retrieve the model object to modify
				PortConnection selectedConnection = (PortConnection) resource.getEObject(uri.fragment());
				final AadlPackage aadlPkg = (AadlPackage) resource.getContents().get(0);
				PackageSection pkgSection = null;
				// Figure out if the selected connection is in the public or private section
				EObject eObj = selectedConnection.eContainer();
				while (eObj != null) {
					if (eObj instanceof PublicPackageSection) {
						pkgSection = aadlPkg.getOwnedPublicSection();
						break;
					} else if (eObj instanceof PrivatePackageSection) {
						pkgSection = aadlPkg.getOwnedPrivateSection();
						break;
					} else {
						eObj = eObj.eContainer();
					}
				}

				if (pkgSection == null) {
					// Something went wrong
					Dialog.showError("No package section found", "No public or private package sections found.");
					return;
				}

				// CASE Property file
				// First check if CASE Property file has already been imported in the model
				final EList<ModelUnit> importedUnits = pkgSection.getImportedUnits();
				PropertySet casePropSet = null;
				for (ModelUnit modelUnit : importedUnits) {
					if (modelUnit instanceof PropertySet) {
						if (modelUnit.getName().equals(CASE_PROPSET_NAME)) {
							casePropSet = (PropertySet) modelUnit;
							break;
						}
					}
				}

				if (casePropSet == null) {
					// Try importing the resource
					casePropSet = getPropertySet(CASE_PROPSET_NAME, CASE_PROPSET_FILE, resource.getResourceSet());
					if (casePropSet == null) {
						return;
					}
					// Add as "importedUnit" to package section
					pkgSection.getImportedUnits().add(casePropSet);
				}

				// Create Filter thread type
//				EClass componentClass;
//				switch (filterComponentType.toLowerCase()) {
//				case "system":
//					componentClass = Aadl2Package.eINSTANCE.getSystemType();
//					break;
//				case "process":
//					componentClass = Aadl2Package.eINSTANCE.getProcessorType();
//					break;
//				case "thread":
//					componentClass = Aadl2Package.eINSTANCE.getThreadType();
//					break;
//				case "device":
//					componentClass = Aadl2Package.eINSTANCE.getDeviceType();
//					break;
//				case "abstract":
//				default:
//					componentClass = Aadl2Package.eINSTANCE.getAbstractType();
//				}
//				final ComponentType componentType = (ComponentType) pkgSection.createOwnedClassifier(componentClass);
				final ThreadType filterThreadType = (ThreadType) pkgSection
						.createOwnedClassifier(Aadl2Package.eINSTANCE.getThreadType());
				// Give it a unique name
//				componentType.setName(getUniqueName(filterTypeName, true, pkgSection.getOwnedClassifiers()));
				filterThreadType.setName(getUniqueName(FILTER_COMP_TYPE_NAME, true, pkgSection.getOwnedClassifiers()));

				// Create filter ports
				final Port port = (Port) selectedConnection.getDestination().getConnectionEnd();
				Port portIn = null;
				Port portOut = null;
				DataSubcomponentType dataFeatureClassifier = null;
				if (port instanceof EventDataPort) {
//					portIn = componentType.createOwnedEventDataPort();
					portIn = filterThreadType.createOwnedEventDataPort();
					dataFeatureClassifier = ((EventDataPort) port).getDataFeatureClassifier();
//					((EventDataPort) portIn)
//							.setDataFeatureClassifier(((EventDataPort) portImpl).getDataFeatureClassifier());
					((EventDataPort) portIn).setDataFeatureClassifier(dataFeatureClassifier);
					portOut = filterThreadType.createOwnedEventDataPort();
//					((EventDataPort) portOut)
//							.setDataFeatureClassifier(((EventDataPort) portImpl).getDataFeatureClassifier());
					((EventDataPort) portOut).setDataFeatureClassifier(dataFeatureClassifier);
				} else if (port instanceof DataPort) {
					portIn = filterThreadType.createOwnedDataPort();
					dataFeatureClassifier = ((EventDataPort) port).getDataFeatureClassifier();
//					((DataPort) portIn).setDataFeatureClassifier(((DataPort) portImpl).getDataFeatureClassifier());
					((DataPort) portIn).setDataFeatureClassifier(dataFeatureClassifier);
					portOut = filterThreadType.createOwnedDataPort();
//					((DataPort) portOut).setDataFeatureClassifier(((DataPort) portImpl).getDataFeatureClassifier());
					((DataPort) portOut).setDataFeatureClassifier(dataFeatureClassifier);
				} else if (port instanceof EventPort) {
//					portIn = filterThreadType.createOwnedEventPort();
//					portOut = filterThreadType.createOwnedEventPort();
					Dialog.showError("Incompatible port type",
							"Cannot connect a filter to a non-data port.");
					return;
				} else {
					Dialog.showError("Undetermined port type",
							"Could not determine the port type of the destination component.");
					return;
				}

				portIn.setIn(true);
				portIn.setName(FILTER_PORT_IN_NAME);

				portOut.setOut(true);
				portOut.setName(FILTER_PORT_OUT_NAME);

				// Add filter properties
				// CASE::COMP_TYPE Property
				if (!addPropertyAssociation("COMP_TYPE", "FILTER", filterThreadType, casePropSet)) {
//					return;
				}
				// CASE::COMP_IMPL property
				if (!addPropertyAssociation("COMP_IMPL", filterImplementationLanguage, filterThreadType, casePropSet)) {
//					return;
				}
				// CASE::COMP_SPEC property
				// Parse the ID from the Filter AGREE property
				String filterPropId = filterAgreeProperty
						.substring(filterAgreeProperty.toLowerCase().indexOf("guarantee ") + "guarantee ".length(),
								filterAgreeProperty.indexOf("\""))
						.trim();
				if (filterPropId.length() > 0) {
					if (!addPropertyAssociation("COMP_SPEC", filterPropId, filterThreadType, casePropSet)) {
//						return;
					}
				}
//				if (!addPropertyAssociation("COMP_SPEC", filterAgreeSpecIDs, filterThreadType, casePropSet)) {
//					return;
//				}


				// Move filter to proper location
				// (just before component it connects to on communication pathway)
				final Subcomponent subcomponent = (Subcomponent) selectedConnection.getDestination().getContext();
				String destName = "";
				if (subcomponent.getSubcomponentType() instanceof ComponentImplementation) {
					// Get the component type name
					destName = subcomponent.getComponentImplementation().getType().getName();
				} else {
					destName = subcomponent.getName();
				}

				pkgSection.getOwnedClassifiers().move(getIndex(destName, pkgSection.getOwnedClassifiers()),
						pkgSection.getOwnedClassifiers().size() - 1);

				// Create Filter implementation
				final ThreadImplementation filterThreadImpl = (ThreadImplementation) pkgSection
						.createOwnedClassifier(Aadl2Package.eINSTANCE.getThreadImplementation());
				filterThreadImpl.setName(filterThreadType.getName() + ".Impl");
				Realization r = filterThreadImpl.createOwnedRealization();
				r.setImplemented(filterThreadType);

				// Add it to proper place
				pkgSection.getOwnedClassifiers().move(getIndex(destName, pkgSection.getOwnedClassifiers()),
						pkgSection.getOwnedClassifiers().size() - 1);

				// Make a copy of the process component implementation
				final ProcessImplementation procImpl = (ProcessImplementation) selectedConnection
						.getContainingComponentImpl();
				ProcessImplementation newImpl = EcoreUtil.copy(procImpl);
				newImpl.setName(getUniqueName(newImpl.getName(), true, pkgSection.getOwnedClassifiers()));

				// Change selectedConnection to refer to the connection on the new implementation
				for (Connection c : newImpl.getAllConnections()) {
					if (c.getName().equalsIgnoreCase(selectedConnection.getName())) {
						selectedConnection = (PortConnection) c;
						break;
					}
				}

				// Insert filter feature in process component implementation
				final ThreadSubcomponent filterThreadSubComp = newImpl.createOwnedThreadSubcomponent();

				// Give it a unique name
				filterThreadSubComp
						.setName(getUniqueName(filterImplementationName, true, newImpl.getOwnedSubcomponents()));
				filterThreadSubComp.setThreadSubcomponentType(filterThreadImpl);

				// Put it in the right place
				destName = selectedConnection.getDestination().getContext().getName();
				newImpl.getOwnedThreadSubcomponents().move(getIndex(destName, newImpl.getOwnedThreadSubcomponents()),
						newImpl.getOwnedThreadSubcomponents().size() - 1);

				// Create connection from filter to connection destination
				final PortConnection portConnOut = newImpl.createOwnedPortConnection();
				// Give it a unique name
				portConnOut
						.setName(getUniqueName(CONNECTION_IMPL_NAME, false, newImpl.getOwnedPortConnections()));
				portConnOut.setBidirectional(false);
				final ConnectedElement filterOutSrc = portConnOut.createSource();
				filterOutSrc.setContext(filterThreadSubComp);
				filterOutSrc.setConnectionEnd(portOut);
				final ConnectedElement filterOutDst = portConnOut.createDestination();
				filterOutDst.setContext(selectedConnection.getDestination().getContext());
				filterOutDst.setConnectionEnd(selectedConnection.getDestination().getConnectionEnd());

				// Put portConnOut in right place (after portConnIn)
				destName = selectedConnection.getName();
				newImpl.getOwnedPortConnections().move(getIndex(destName, newImpl.getOwnedPortConnections()) + 1,
						newImpl.getOwnedPortConnections().size() - 1);

				// Add new implementation to package and place immediately below original implementation
				pkgSection.getOwnedClassifiers().add(newImpl);
				pkgSection.getOwnedClassifiers().move(
						getIndex(procImpl.getName(), pkgSection.getOwnedClassifiers()),
						pkgSection.getOwnedClassifiers().size() - 1);

				// Add add_filter claims to resolute prove statement, if applicable
				if (!filterResoluteClause.isEmpty()) {
					// Add arguments to prove statement in destination component
					ThreadType clauseThread = (ThreadType) selectedConnection.getDestination().getConnectionEnd()
							.getContainingClassifier();
					EList<AnnexSubclause> annexSubclauses = clauseThread.getOwnedAnnexSubclauses();
					for (AnnexSubclause annexSubclause : annexSubclauses) {
						// Get the Resolute clause
						if (annexSubclause.getName().equalsIgnoreCase("resolute")) {
							DefaultAnnexSubclause annexSubclauseImpl = (DefaultAnnexSubclause) annexSubclause;
							String sourceText = annexSubclauseImpl.getSourceText();
							if (sourceText.contains(filterResoluteClause + "(")) {
								// Add arguments
								int startIdx = sourceText.indexOf(filterResoluteClause + "(")
										+ filterResoluteClause.length() + 1;
								String args = sourceText.substring(startIdx,
										sourceText.indexOf(")", startIdx));
								sourceText = sourceText.replace(filterResoluteClause + "(" + args + ")",
										filterResoluteClause + "(" + args + ", " + dataFeatureClassifier.getName()
												+ ")");
								annexSubclauseImpl.setSourceText(sourceText);
							}
							break;
						}
					}
					// Add add_filter claims to *_CASE_Claims file
					// If the prove statement exists, the *_CASE_Claims file should also already
					// exist, but double check just to be sure, and create it if it doesn't
					CaseClaimsManager.getInstance().addFilter(filterResoluteClause);

				}

				// Rewire selected connection so the filter is the destination
				selectedConnection.getDestination().setContext(filterThreadSubComp);
				selectedConnection.getDestination().setConnectionEnd(portIn);

				// Propagate Agree Guarantees from source component, if there are any
				if (filterAgreeProperty.length() > 0 || propagatedGuarantees.size() > 0) {
					String agreeClauses = "{**" + System.lineSeparator();

					if (!filterAgreeProperty.isEmpty()) {
						agreeClauses = agreeClauses + "\t\t\t" + filterAgreeProperty + System.lineSeparator();
//						for (String clause : filterAgreeProperty.split(System.lineSeparator())) {
//							agreeClauses = agreeClauses + "\t\t\t" + clause + System.lineSeparator();
//						}
					}

					for (String guarantee : propagatedGuarantees) {
						agreeClauses = agreeClauses + "\t\t\t" + guarantee + System.lineSeparator();
					}
					agreeClauses = agreeClauses + "\t\t**}";
					// replace source out port name with filter out port name
					agreeClauses = agreeClauses.replace(selectedConnection.getSource().getConnectionEnd().getName(),
							FILTER_PORT_OUT_NAME);
					DefaultAnnexSubclause annexSubclauseImpl = filterThreadType
							.createOwnedAnnexSubclause();
					annexSubclauseImpl.setName("agree");
					annexSubclauseImpl.setSourceText(agreeClauses);
				}


			}
		});

	}

	private String getSourceName(URI uri) {
		XtextEditor xtextEditor = EditorUtils.getActiveXtextEditor();

		return xtextEditor.getDocument().readOnly(resource -> {
			final PortConnection selectedConnection = (PortConnection) resource.getEObject(uri.fragment());
			return selectedConnection.getSource().getConnectionEnd().getContainingClassifier().getName();
		});
	}

//	private String getDestinationType(URI uri) {
//		XtextEditor xtextEditor = EditorUtils.getActiveXtextEditor();
//
//		return xtextEditor.getDocument().readOnly(resource -> {
//			final PortConnection selectedConnection = (PortConnection) resource.getEObject(uri.fragment());
//			ComponentType ctype = (ComponentType) selectedConnection.getDestination().getConnectionEnd()
//					.getContainingClassifier();
//			return ctype.getCategory().toString();
//		});
//	}
//
//	private String getParentType(URI uri) {
//		XtextEditor xtextEditor = EditorUtils.getActiveXtextEditor();
//
//		return xtextEditor.getDocument().readOnly(resource -> {
//			final PortConnection selectedConnection = (PortConnection) resource.getEObject(uri.fragment());
//			ComponentImplementation componentImpl = selectedConnection.getContainingComponentImpl();
//			return componentImpl.getCategory().toString();
//		});
//	}

	private List<String> getSourceGuarantees(URI uri) {
		XtextEditor xtextEditor = EditorUtils.getActiveXtextEditor();

		return xtextEditor.getDocument().readOnly(resource -> {
			List<String> guarantees = new ArrayList<>();
			final PortConnection selectedConnection = (PortConnection) resource.getEObject(uri.fragment());
			final EList<AnnexSubclause> annexSubclauses = selectedConnection.getSource().getConnectionEnd()
					.getContainingClassifier().getOwnedAnnexSubclauses();
			for (AnnexSubclause annexSubclause : annexSubclauses) {
				// See if there's an agree annex
				if (annexSubclause.getName().equalsIgnoreCase("agree")) {
					DefaultAnnexSubclause annexSubclauseImpl = (DefaultAnnexSubclause) annexSubclause;
					// See if the agree annex contains guarantee statements
					AgreeContractSubclause agreeContract = (AgreeContractSubclause) annexSubclauseImpl
							.getParsedAnnexSubclause();
					AgreeAnnexUnparser unparser = new AgreeAnnexUnparser();
					String specs = unparser.unparseContract((AgreeContract) agreeContract.getContract(), "");
					for (String line : specs.split(System.lineSeparator())) {
						if (line.trim().toLowerCase().startsWith("guarantee")) {
							guarantees.add(line.trim());
						}
					}
					break;
				}
			}
			return guarantees;
		});

	}

	private List<String> getResoluteClauses(URI uri) {

		XtextEditor xtextEditor = EditorUtils.getActiveXtextEditor();

		return xtextEditor.getDocument().readOnly(resource -> {
			List<String> resoluteClauses = new ArrayList<>();
			final PortConnection selectedConnection = (PortConnection) resource.getEObject(uri.fragment());
			final EList<AnnexSubclause> annexSubclauses = selectedConnection.getDestination().getConnectionEnd()
					.getContainingClassifier().getOwnedAnnexSubclauses();
			for (AnnexSubclause annexSubclause : annexSubclauses) {
				// See if there's a resolute annex
				if (annexSubclause.getName().equalsIgnoreCase("resolute")) {
					DefaultAnnexSubclause annexSubclauseImpl = (DefaultAnnexSubclause) annexSubclause;
					// See if there are any 'prove' clauses
					ResoluteSubclause resoluteClause = (ResoluteSubclause) annexSubclauseImpl.getParsedAnnexSubclause();
					EList<ProveStatement> proves = resoluteClause.getProves();
					for (ProveStatement prove : proves) {
						Expr expr = prove.getExpr();
						if (expr instanceof FnCallExpr) {
							FnCallExpr fnCall = (FnCallExpr) expr;
							if (fnCall.getFn().getName() != null) {
								resoluteClauses.add(fnCall.getFn().getName());
							} else {
								return null;
							}
						}
					}
					break;
				}
			}

			return resoluteClauses;
		});
	}

}
