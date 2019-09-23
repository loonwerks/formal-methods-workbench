package com.collins.fmw.cyres.architecture.handlers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.PlatformUI;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.utils.EditorUtils;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;
import org.osate.aadl2.Aadl2Factory;
import org.osate.aadl2.AadlPackage;
import org.osate.aadl2.AbstractNamedValue;
import org.osate.aadl2.AnnexSubclause;
import org.osate.aadl2.Classifier;
import org.osate.aadl2.ComponentCategory;
import org.osate.aadl2.ComponentImplementation;
import org.osate.aadl2.ComponentType;
import org.osate.aadl2.ConnectedElement;
import org.osate.aadl2.Connection;
import org.osate.aadl2.DataImplementation;
import org.osate.aadl2.DataPort;
import org.osate.aadl2.DataSubcomponentType;
import org.osate.aadl2.DefaultAnnexSubclause;
import org.osate.aadl2.EnumerationLiteral;
import org.osate.aadl2.EventDataPort;
import org.osate.aadl2.Feature;
import org.osate.aadl2.NamedElement;
import org.osate.aadl2.NamedValue;
import org.osate.aadl2.PackageSection;
import org.osate.aadl2.Port;
import org.osate.aadl2.PortCategory;
import org.osate.aadl2.PortConnection;
import org.osate.aadl2.PrivatePackageSection;
import org.osate.aadl2.PropertyExpression;
import org.osate.aadl2.PublicPackageSection;
import org.osate.aadl2.Realization;
import org.osate.aadl2.Subcomponent;
import org.osate.aadl2.properties.PropertyDoesNotApplyToHolderException;
import org.osate.ui.dialogs.Dialog;

import com.collins.fmw.cyres.architecture.dialogs.AddAttestationManagerDialog;
import com.collins.fmw.cyres.architecture.requirements.AddAttestationManagerClaim;
import com.collins.fmw.cyres.architecture.requirements.RequirementsManager;
import com.collins.fmw.cyres.architecture.utils.CaseUtils;
import com.collins.fmw.cyres.architecture.utils.ComponentCreateHelper;
import com.rockwellcollins.atc.agree.agree.AgreeContract;
import com.rockwellcollins.atc.agree.agree.AgreeContractSubclause;
import com.rockwellcollins.atc.agree.unparsing.AgreeAnnexUnparser;

public class AddAttestationManagerHandler extends AadlHandler {

	static final String AM_REQUEST_MSG_NAME = "CASE_AttestationRequestMsg";
	static final String AM_RESPONSE_MSG_NAME = "CASE_AttestationResponseMsg";
	static final String AM_REQUEST_MSG_IMPL_NAME = "CASE_AttestationRequestMsg.Impl";
	static final String AM_RESPONSE_MSG_IMPL_NAME = "CASE_AttestationResponseMsg.Impl";
	static final String AM_COMP_TYPE_NAME = "CASE_AttestationManager";
	static final String AM_LOG_PORT_NAME = "message_log";
	static final String AM_PORT_ATTESTATION_REQUEST_NAME = "am_request";
	static final String AM_PORT_ATTESTATION_RESPONSE_NAME = "am_response";
	public static final String AM_IMPL_NAME = "AM";
	static final String CONNECTION_IMPL_NAME = "c";

	private String implementationName;
	private String implementationLanguage;
	private String cacheTimeout;
	private String cacheSize;
	private PortCategory logPortType;
	private String attestationRequirement;
	private boolean propagateGuarantees;
	private String attestationAgreeProperty;

	@Override
	protected void runCommand(URI uri) {

		// Check if selection is a subcomponent
		final EObject eObj = getEObject(uri);
		Subcomponent selectedSubcomponent = null;
		if (eObj instanceof Subcomponent) {
			selectedSubcomponent = (Subcomponent) eObj;
		} else {
			Dialog.showError("Add Attestation Manager",
					"A communication driver subcomponent must be selected to add an attestation manager.");
			return;
		}

		// Check if selected subcomponent is a comm driver
		if (!isCompType(selectedSubcomponent, "COMM_DRIVER")) {
			Dialog.showError("Add Attestation Manager",
					"A communication driver subcomponent must be selected to add an attestation manager.");
			return;
		}

		// Check if the selected subcomponent already has an attestation manager connected
		// If there is, ask the user if they would like to associate the attestation manager with another requirement
		boolean associateWithNewRequirement = false;
		Subcomponent attestationManager = getAttestationManager(selectedSubcomponent);
//		if (hasAttestationManager(selectedSubcomponent)) {
		if (attestationManager != null) {
			Dialog.showError("Add Attestation Manager",
					"Component " + selectedSubcomponent.getName() + " already has an associated attestation manager.");
// TODO: associate AM with requirement
//			if (Dialog.askQuestion("Add Attestation Manager", "Component " + selectedSubcomponent.getName()
//					+ " already has an associated attestation manager. Would you like to associate it with a new requirement?")) {
//				associateWithNewRequirement = true;
//			} else {
				return;
//			}
		}

		// Open wizard to enter filter info
		final AddAttestationManagerDialog wizard = new AddAttestationManagerDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		String amName = AM_IMPL_NAME;
		if (attestationManager != null) {
			amName = attestationManager.getName();
		}
		List<String> importedRequirements = new ArrayList<>();
		RequirementsManager.getInstance().getImportedRequirements().forEach(r -> importedRequirements.add(r.getId()));
		wizard.create(selectedSubcomponent.getName(), importedRequirements, associateWithNewRequirement, amName);
		if (wizard.open() == Window.OK) {
			implementationName = wizard.getImplementationName();
			implementationLanguage = wizard.getImplementationLanguage();
			cacheTimeout = wizard.getCacheTimeout();
			if (cacheTimeout.isEmpty()) {
				cacheTimeout = "0";
			}
			cacheSize = wizard.getCacheSize();
			if (cacheSize.isEmpty()) {
				cacheSize = "0";
			}
			logPortType = wizard.getLogPortType();
			attestationRequirement = wizard.getRequirement();
			propagateGuarantees = wizard.getPropagateGuarantees();
			attestationAgreeProperty = wizard.getAgreeProperty();
		} else {
			return;
		}

		// Insert the attestation manager
		if (associateWithNewRequirement) {

			// Add add_attestation claims to resolute prove statement, if applicable
			// TODO: Test after requirements import has been updated
//			if (!attestationRequirement.isEmpty()) {
//				RequirementsManager.getInstance().modifyRequirement(attestationRequirement,
//						new AddAttestationManagerClaim(selectedSubcomponent, attestationManager));
//
//			}

		} else {
			insertAttestationManager(uri);
		}

		return;

	}

	/**
	 * Inserts an attestation manager component into the model.  The attestation manager is inserted at
	 * the location of the selected connection
	 * @param uri - The URI of the selected connection
	 */
	private void insertAttestationManager(URI uri) {

		// Get the active xtext editor so we can make modifications
		final XtextEditor xtextEditor = EditorUtils.getActiveXtextEditor();

		xtextEditor.getDocument().modify(new IUnitOfWork.Void<XtextResource>() {

			@Override
			public void process(final XtextResource resource) throws Exception {

				// Retrieve the model object to modify
				Subcomponent commDriver = (Subcomponent) resource.getEObject(uri.fragment());
				// Get comm driver component type
				ComponentType selectedCommDriverType = commDriver.getComponentType();
				// Get containing component implementation
				ComponentImplementation ci = commDriver.getContainingComponentImpl();

				final AadlPackage aadlPkg = (AadlPackage) resource.getContents().get(0);
				PackageSection pkgSection = null;
				// Figure out if the comm driver's containing implementation is in the public or private section
				EObject eObj = commDriver.eContainer();
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
					Dialog.showError("Add Attestation Manager", "No public or private package sections found.");
					return;
				}

				// Import CASE_Properties file
				if (!CaseUtils.addCasePropertyImport(pkgSection)) {
					return;
				}
				// Import CASE_Model_Transformations file
				if (!CaseUtils.addCaseModelTransformationsImport(pkgSection, true)) {
					return;
				}

				ComponentCategory compCategory = commDriver.getCategory();
				// If the component type is a process, we will need to put a single thread inside.
				// Per convention, we will attach all properties and contracts to the thread.
				// For this model transformation, we will create the thread first, then wrap it in a process
				// component, using the same mechanism we use for the seL4 transformation
				final boolean isProcess = (compCategory == ComponentCategory.PROCESS);
				if (isProcess) {
					compCategory = ComponentCategory.THREAD;
				}

				// Create new comm driver component that extends the selected one
				final ComponentType commDriverType = (ComponentType) pkgSection
						.createOwnedClassifier(ComponentCreateHelper.getTypeClass(compCategory));
				// Give it a unique name
				commDriverType.setName(getUniqueName(commDriver.getComponentType().getName() + "_Attestation", true,
						pkgSection.getOwnedClassifiers()));
				commDriverType.setExtended(commDriver.getComponentType());

				// Get the request and response message types from the CASE_Model_Transformations package
				DataImplementation requestMsgImpl = null;
				DataImplementation responseMsgImpl = null;
				AadlPackage caseModelTransformationsPkg = CaseUtils.getCaseModelTransformationsPackage();
				for (Classifier classifier : caseModelTransformationsPkg.getOwnedPublicSection()
						.getOwnedClassifiers()) {
					if (classifier.getName().equalsIgnoreCase(AM_REQUEST_MSG_IMPL_NAME)) {
						requestMsgImpl = (DataImplementation) classifier;
					} else if (classifier.getName().equalsIgnoreCase(AM_RESPONSE_MSG_IMPL_NAME)) {
						responseMsgImpl = (DataImplementation) classifier;
					}
				}

				// Create attestation request and response ports
				final EventDataPort commReq = (EventDataPort) ComponentCreateHelper
						.createOwnedEventDataPort(commDriverType);
				final EventDataPort commRes = (EventDataPort) ComponentCreateHelper
						.createOwnedEventDataPort(commDriverType);
				commReq.setDataFeatureClassifier(requestMsgImpl);
				commRes.setDataFeatureClassifier(responseMsgImpl);
				commReq.setName(AM_PORT_ATTESTATION_REQUEST_NAME);
				commReq.setIn(true);
				commRes.setName(AM_PORT_ATTESTATION_RESPONSE_NAME);
				commRes.setOut(true);

				// TODO: AGREE?

				// Put just above it's containing implementation
				pkgSection.getOwnedClassifiers().move(getIndex(ci.getTypeName(), pkgSection.getOwnedClassifiers()),
						pkgSection.getOwnedClassifiers().size() - 1);
//				// Move extended comm driver to top of file
//				pkgSection.getOwnedClassifiers().move(0, pkgSection.getOwnedClassifiers().size() - 1);

				// Create extended comm driver implementation
				final ComponentImplementation commDriverImpl = (ComponentImplementation) pkgSection
						.createOwnedClassifier(ComponentCreateHelper.getImplClass(compCategory));
				commDriverImpl.setName(commDriverType.getName() + ".Impl");
				commDriverImpl.setExtended(commDriver.getComponentImplementation());
				final Realization commRealization = commDriverImpl.createOwnedRealization();
				commRealization.setImplemented(commDriverType);

				// Add it to proper place (below extended comm driver type)
				pkgSection.getOwnedClassifiers().move(getIndex(ci.getTypeName(), pkgSection.getOwnedClassifiers()),
						pkgSection.getOwnedClassifiers().size() - 1);

				// Create Attestation Manager component type
				final ComponentType attestationManagerType = (ComponentType) pkgSection
						.createOwnedClassifier(ComponentCreateHelper.getTypeClass(compCategory));
				// Give it a unique name
				attestationManagerType
						.setName(getUniqueName(AM_COMP_TYPE_NAME, true, pkgSection.getOwnedClassifiers()));

				// Create Attestation Manager ports
				// To do this we need to look at the current connections of the comm driver in the implementation
				// However, we do not want to consider the outports that lead to the comm bus
				// Also, save the port names, for use later
				List<String> amPortNames = new ArrayList<>();
				List<DataImplementation> amPortTypes = new ArrayList<>();
				for (PortConnection conn : ci.getOwnedPortConnections()) {
					if (conn.getSource().getContext() == commDriver && conn.getDestination().getContext() != null) {
						final Port commPort = (Port) conn.getSource().getConnectionEnd();
						Port portIn = null;
						Port portOut = null;
						DataSubcomponentType dataFeatureClassifier = null;
						if (commPort instanceof EventDataPort) {
							portIn = ComponentCreateHelper.createOwnedEventDataPort(attestationManagerType);
							dataFeatureClassifier = ((EventDataPort) commPort).getDataFeatureClassifier();
							((EventDataPort) portIn).setDataFeatureClassifier(dataFeatureClassifier);
							portOut = ComponentCreateHelper.createOwnedEventDataPort(attestationManagerType);
							((EventDataPort) portOut).setDataFeatureClassifier(dataFeatureClassifier);
						} else if (commPort instanceof DataPort) {
							portIn = ComponentCreateHelper.createOwnedDataPort(attestationManagerType);
							dataFeatureClassifier = ((DataPort) commPort).getDataFeatureClassifier();
							((DataPort) portIn).setDataFeatureClassifier(dataFeatureClassifier);
							portOut = ComponentCreateHelper.createOwnedDataPort(attestationManagerType);
							((DataPort) portOut).setDataFeatureClassifier(dataFeatureClassifier);
						}

						portIn.setIn(true);
						portIn.setName("am_" + commPort.getName() + "_in");

						portOut.setOut(true);
						portOut.setName("am_" + commPort.getName() + "_out");

						amPortNames.add(commPort.getName());
						amPortTypes.add((DataImplementation) dataFeatureClassifier);

						// The data subcomponent type could be in a different package.
						// Make sure to include it in the with clause
						importContainingPackage(dataFeatureClassifier, pkgSection);

					}
				}

				// Add the ports for communicating attestation requests/responses with the Comm Driver
				final EventDataPort amReq = (EventDataPort) ComponentCreateHelper
						.createOwnedEventDataPort(attestationManagerType);
				final EventDataPort amRes = (EventDataPort) ComponentCreateHelper
						.createOwnedEventDataPort(attestationManagerType);
				// Set data feature classifier
				amReq.setDataFeatureClassifier(requestMsgImpl);
				amRes.setDataFeatureClassifier(responseMsgImpl);
				amReq.setName(AM_PORT_ATTESTATION_REQUEST_NAME);
				amReq.setOut(true);
				amRes.setName(AM_PORT_ATTESTATION_RESPONSE_NAME);
				amRes.setIn(true);

				// Create log port, if necessary
				if (logPortType != null) {
					Port logPort = null;
					if (logPortType == PortCategory.EVENT) {
						logPort = ComponentCreateHelper.createOwnedEventPort(attestationManagerType);
					} else if (logPortType == PortCategory.DATA) {
						logPort = ComponentCreateHelper.createOwnedDataPort(attestationManagerType);
					} else {
						logPort = ComponentCreateHelper.createOwnedEventDataPort(attestationManagerType);
					}
					logPort.setOut(true);
					logPort.setName(AM_LOG_PORT_NAME);
				}

//				// Add attestation request/response ports on comm driver (or create new comm driver component?)
//				final ComponentType commDriverType = commDriver.getComponentType();
//				final EventDataPort commReq = (EventDataPort) ComponentCreateHelper
//						.createOwnedEventDataPort(commDriverType);
//				final EventDataPort commRes = (EventDataPort) ComponentCreateHelper
//						.createOwnedEventDataPort(commDriverType);
//				commReq.setDataFeatureClassifier(requestMsgImpl);
//				commRes.setDataFeatureClassifier(responseMsgImpl);
//				commReq.setName(AM_PORT_ATTESTATION_REQUEST_NAME);
//				commReq.setIn(true);
//				commRes.setName(AM_PORT_ATTESTATION_RESPONSE_NAME);
//				commRes.setOut(true);

				// Add Attestation Manager properties
				// CASE_Properties::COMP_TYPE Property
				if (!CaseUtils.addCasePropertyAssociation("COMP_TYPE", "ATTESTATION", attestationManagerType)) {
//					return;
				}

				// CASE_Properties::COMP_SPEC property
				// Parse the ID from the Attestation Manager AGREE property
				String attestationPropId = "";
				try {
					attestationPropId = attestationAgreeProperty.substring(
							attestationAgreeProperty.toLowerCase().indexOf("guarantee ") + "guarantee ".length(),
							attestationAgreeProperty.indexOf("\"")).trim();
				} catch (IndexOutOfBoundsException e) {
					if (!attestationAgreeProperty.isEmpty()) {
						// agree property is malformed, so leave blank
						Dialog.showWarning("Add Attestation Manager",
								"Attestation Manager AGREE statement is malformed.");
					}
				}
				if (!attestationPropId.isEmpty()) {
					if (!CaseUtils.addCasePropertyAssociation("COMP_SPEC", attestationPropId, attestationManagerType)) {
//						return;
					}
				}

//				// Put Attestation Manager in proper location (just after the extended comm driver)
//				String destName = "";
//				if (commDriver.getSubcomponentType() instanceof ComponentImplementation) {
//					// Get the component type implementation name
//					destName = commDriver.getComponentImplementation().getName();
//				} else {
//					// Get the component type name
//					destName = commDriver.getComponentImplementation().getType().getName();
//				}

//				pkgSection.getOwnedClassifiers().move(getIndex(destName, pkgSection.getOwnedClassifiers()) + 1,
//						pkgSection.getOwnedClassifiers().size() - 1);

//				pkgSection.getOwnedClassifiers().move(getIndex(ci.getTypeName(), pkgSection.getOwnedClassifiers()),
//						pkgSection.getOwnedClassifiers().size() - 1);
				// Move filter to top of file
				pkgSection.getOwnedClassifiers().move(0, pkgSection.getOwnedClassifiers().size() - 1);

				// Create Attestation Manager implementation
				final ComponentImplementation attestationManagerImpl = (ComponentImplementation) pkgSection
						.createOwnedClassifier(ComponentCreateHelper.getImplClass(compCategory));
				attestationManagerImpl.setName(attestationManagerType.getName() + ".Impl");
				final Realization r = attestationManagerImpl.createOwnedRealization();
				r.setImplemented(attestationManagerType);

//				// Add it to proper place
//				pkgSection.getOwnedClassifiers().move(getIndex(destName, pkgSection.getOwnedClassifiers()) + 2,
//						pkgSection.getOwnedClassifiers().size() - 1);

//				pkgSection.getOwnedClassifiers().move(getIndex(ci.getTypeName(), pkgSection.getOwnedClassifiers()),
//						pkgSection.getOwnedClassifiers().size() - 1);
				// Add it to proper place (just below component type)
				pkgSection.getOwnedClassifiers().move(1, pkgSection.getOwnedClassifiers().size() - 1);

				// CASE_Properties::COMP_IMPL property
				if (!implementationLanguage.isEmpty()) {
					if (!CaseUtils.addCasePropertyAssociation("COMP_IMPL", implementationLanguage,
							attestationManagerImpl)) {
//						return;
					}
				}

				// CASE_Properties::CACHE_TIMEOUT property
				if (!cacheTimeout.isEmpty()) {
					if (!CaseUtils.addCasePropertyAssociation("CACHE_TIMEOUT", cacheTimeout, attestationManagerImpl)) {
//						return;
					}
				}

				// CASE_Properties::CACHE_SIZE property
				if (!cacheSize.isEmpty()) {
					if (!CaseUtils.addCasePropertyAssociation("CACHE_SIZE", cacheSize, attestationManagerImpl)) {
//						return;
					}
				}

//				// Get the parent component implementation
//				final ComponentImplementation containingImpl = commDriver.getContainingComponentImpl();

				// Replace the comm driver with the extended comm driver
				for (Subcomponent sub : ci.getOwnedSubcomponents()) {
					if (sub.getName().equalsIgnoreCase(commDriver.getName())) {
						ComponentCreateHelper.setSubcomponentType(sub, commDriverImpl);
					}
				}

				// Insert attestation manager in process component implementation
//				final Subcomponent attestationManagerSubcomp = ComponentCreateHelper
//						.createOwnedSubcomponent(containingImpl, compCategory);
				final Subcomponent attestationManagerSubcomp = ComponentCreateHelper
						.createOwnedSubcomponent(ci, compCategory);

				// Give it a unique name
//				attestationManagerSubcomp
//						.setName(getUniqueName(implementationName, true, containingImpl.getOwnedSubcomponents()));
				attestationManagerSubcomp
						.setName(getUniqueName(implementationName, true, ci.getOwnedSubcomponents()));
				// Assign thread implementation
				ComponentCreateHelper.setSubcomponentType(attestationManagerSubcomp, attestationManagerImpl);

				List<PortConnection> newPortConns = new ArrayList<>();

				// Create new connections between comm driver / attestation manager / destination components
				String connName = "";
//				for (PortConnection conn : containingImpl.getOwnedPortConnections()) {
				for (PortConnection conn : ci.getOwnedPortConnections()) {
					// Ignore bus connections (destination context not null)
					if (conn.getSource().getContext() == commDriver && conn.getDestination().getContext() != null) {
						// Create connection from attestation manager to destination components
						final PortConnection portConnOut = Aadl2Factory.eINSTANCE.createPortConnection();
						// Give it a unique name
						portConnOut.setBidirectional(false);
						final ConnectedElement connSrc = portConnOut.createSource();
						connSrc.setContext(attestationManagerSubcomp);
						for (Feature feature : attestationManagerType.getAllFeatures()) {
							if (feature.getName()
									.equalsIgnoreCase("am_" + conn.getSource().getConnectionEnd().getName() + "_out")) {
								connSrc.setConnectionEnd(feature);
								break;
							}
						}
						final ConnectedElement connDst = portConnOut.createDestination();
						connDst.setContext(conn.getDestination().getContext());
						connDst.setConnectionEnd(conn.getDestination().getConnectionEnd());

						newPortConns.add(portConnOut);

						// Rewire connections from comm driver into attestation manager
						conn.getDestination().setContext(attestationManagerSubcomp);
						for (Feature feature : attestationManagerType.getAllFeatures()) {
							if (feature.getName()
									.equalsIgnoreCase("am_" + conn.getSource().getConnectionEnd().getName() + "_in")) {
								conn.getDestination().setConnectionEnd(feature);
							}
						}

						// In order to put new connections in right place, keep track of
						// the last relevant comm driver connection name
						connName = conn.getName();

					}
				}

				// Create attestation request / response connections between comm driver and attestation manager
				final PortConnection portConnReq = Aadl2Factory.eINSTANCE.createPortConnection();
				// Give it a unique name
				portConnReq.setBidirectional(false);
				final ConnectedElement reqSrc = portConnReq.createSource();
				reqSrc.setContext(attestationManagerSubcomp);
				reqSrc.setConnectionEnd(amReq);
				final ConnectedElement reqDst = portConnReq.createDestination();
				reqDst.setContext(commDriver);
				reqDst.setConnectionEnd(commReq);
				newPortConns.add(portConnReq);

				final PortConnection portConnRes = Aadl2Factory.eINSTANCE.createPortConnection();
				// Give it a unique name
				portConnRes.setBidirectional(false);
				final ConnectedElement resSrc = portConnRes.createSource();
				resSrc.setContext(commDriver);
				resSrc.setConnectionEnd(amRes);
				final ConnectedElement resDst = portConnRes.createDestination();
				resDst.setContext(attestationManagerSubcomp);
				resDst.setConnectionEnd(commRes);
				newPortConns.add(portConnRes);

				int idxOffset = 1;
				for (PortConnection newPortConn : newPortConns) {
					// Make sure each new connection has a unique name
//					newPortConn.setName(
//							getUniqueName(CONNECTION_IMPL_NAME, false, containingImpl.getOwnedPortConnections()));
					newPortConn.setName(
							getUniqueName(CONNECTION_IMPL_NAME, false, ci.getOwnedPortConnections()));
//					containingImpl.getOwnedPortConnections().add(newPortConn);
					ci.getOwnedPortConnections().add(newPortConn);
					// Move to right place
//					containingImpl.getOwnedPortConnections().move(
//							getIndex(connName, containingImpl.getOwnedPortConnections()) + idxOffset,
//							containingImpl.getOwnedPortConnections().size() - 1);
					ci.getOwnedPortConnections().move(getIndex(connName, ci.getOwnedPortConnections()) + idxOffset,
							ci.getOwnedPortConnections().size() - 1);
					idxOffset++;
				}

				// Add add_attestation claims to resolute prove statement, if applicable
				if (!attestationRequirement.isEmpty()) {
					RequirementsManager.getInstance().modifyRequirement(attestationRequirement, resource,
							new AddAttestationManagerClaim(commDriver, attestationManagerSubcomp));

				}

				// Propagate Agree Guarantees from comm driver, if there are any
				if (attestationAgreeProperty.length() > 0 || propagateGuarantees) {

					String agreeClauses = "{**" + System.lineSeparator();

					// Get guarantees from comm driver
					List<String> guarantees = new ArrayList<>();
					for (AnnexSubclause annexSubclause : selectedCommDriverType.getOwnedAnnexSubclauses()) {
						// Get the Agree annex
						if (annexSubclause.getName().equalsIgnoreCase("agree")) {
							DefaultAnnexSubclause annexSubclauseImpl = (DefaultAnnexSubclause) annexSubclause;
							// See if the agree annex contains guarantee statements
							AgreeContractSubclause agreeContract = (AgreeContractSubclause) annexSubclauseImpl
									.getParsedAnnexSubclause();
							AgreeAnnexUnparser unparser = new AgreeAnnexUnparser();
							// TODO: use unparser to unparse just guarantee statements
							String specs = unparser.unparseContract((AgreeContract) agreeContract.getContract(), "");
							int gCtr = 1;
							for (String spec : specs.split(";")) {
								if (spec.trim().toLowerCase().startsWith("guarantee")) {
									String guarantee = "";
									for (String line : spec.trim().concat(";").split(System.lineSeparator())) {
										guarantee += line.trim() + " ";
									}

									String expr = guarantee
											.substring(guarantee.lastIndexOf(":") + 1, guarantee.lastIndexOf(";"))
											.trim();
									String desc = guarantee
											.substring(guarantee.indexOf("\""), guarantee.lastIndexOf("\"") + 1).trim();
									String id = guarantee.substring(
											guarantee.toLowerCase().indexOf("guarantee ") + "guarantee ".length(),
											guarantee.indexOf("\"")).trim();

									// If guarantee has an ID, append a suffix to maintain ID uniqueness
									if (id.length() > 0) {
										id = id.concat("_AttestationManager");
									} else {
										id = "Req" + gCtr++ + "_AttestationManager";
									}

									// Replace comm driver out port name with attestation manager out port name
									for (Feature feature : selectedCommDriverType.getOwnedFeatures()) {
										expr = expr.replace(feature.getName(), "am_" + feature.getName() + "_out");
									}

									guarantee = "guarantee " + id + " " + desc + " : " + expr + ";";

									guarantees.add(guarantee);
								}
							}
							break;
						}
					}

					for (String guarantee : guarantees) {
						agreeClauses = agreeClauses + guarantee + System.lineSeparator();
					}

					if (!attestationAgreeProperty.isEmpty()) {
						agreeClauses = agreeClauses + attestationAgreeProperty + System.lineSeparator();
					}

					agreeClauses = agreeClauses + "**}";

					// If agreeClauses is not an empty annex, print it
					if (attestationAgreeProperty.length() > 0 || guarantees.size() > 0) {
						final DefaultAnnexSubclause annexSubclauseImpl = ComponentCreateHelper
								.createOwnedAnnexSubclause(attestationManagerType);
						annexSubclauseImpl.setName("agree");
						annexSubclauseImpl.setSourceText(agreeClauses);
					}
				}

				if (isProcess) {

					// TODO: Wrap thread component in a process

					// TODO: Bind process to processor
				}

			}
		});

	}

	private boolean isCompType(Subcomponent comp, String compType) {

		try {

			EList<PropertyExpression> propVal = comp.getPropertyValues(CaseUtils.CASE_PROPSET_NAME, "COMP_TYPE");

			if (propVal != null) {
				for (PropertyExpression expr : propVal) {
					if (expr instanceof NamedValue) {
						NamedValue namedVal = (NamedValue) expr;
						AbstractNamedValue absVal = namedVal.getNamedValue();
						if (absVal instanceof EnumerationLiteral) {
							EnumerationLiteral enVal = (EnumerationLiteral) absVal;
							if (enVal.getName().equalsIgnoreCase(compType)) {
								return true;
							}
						}
					}
				}
			}
		} catch (PropertyDoesNotApplyToHolderException e) {
			return false;
		}
		return false;
	}

	private Subcomponent getAttestationManager(Subcomponent comp) {
//	private boolean hasAttestationManager(Subcomponent comp) {

		ComponentImplementation ci = comp.getContainingComponentImpl();

		// Look at each connection in the subcomponent's containing implementation
		for (Connection conn : ci.getAllConnections()) {

			// Get the source component of the connection
			NamedElement ne = conn.getAllSrcContextComponent();
			// If source component is the specified subcomponent, get the destination component of the connection
			if (ne instanceof Subcomponent && ne.getName().equalsIgnoreCase(comp.getName())) {
				ne = conn.getAllDstContextComponent();
				Subcomponent dst = null;
				if (ne instanceof Subcomponent) {
					dst = (Subcomponent) ne;
					// Check if it's an attestation manager
					if (isCompType(dst, "ATTESTATION")) {
						return dst;
//						return true;
					}
				}
			}

		}

//		return false;
		return null;
	}

}
