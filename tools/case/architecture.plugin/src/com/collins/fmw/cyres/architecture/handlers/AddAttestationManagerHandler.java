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
import org.osate.aadl2.PortConnection;
import org.osate.aadl2.PrivatePackageSection;
import org.osate.aadl2.PropertyExpression;
import org.osate.aadl2.PropertySet;
import org.osate.aadl2.PublicPackageSection;
import org.osate.aadl2.Realization;
import org.osate.aadl2.Subcomponent;
import org.osate.ui.dialogs.Dialog;

import com.collins.fmw.cyres.architecture.dialogs.AddAttestationManagerDialog;
import com.collins.fmw.cyres.architecture.requirements.AddAttestationManagerClaim;
import com.collins.fmw.cyres.architecture.requirements.RequirementsManager;
import com.collins.fmw.cyres.architecture.utils.ComponentCreateHelper;
import com.rockwellcollins.atc.agree.agree.AgreeContract;
import com.rockwellcollins.atc.agree.agree.AgreeContractSubclause;
import com.rockwellcollins.atc.agree.unparsing.AgreeAnnexUnparser;

public class AddAttestationManagerHandler extends AadlHandler {

	static final String AM_REQUEST_MSG_NAME = "CASE_AttestationRequestMsg";
	static final String AM_RESPONSE_MSG_NAME = "CASE_AttestationResponseMsg";
	static final String AM_COMP_TYPE_NAME = "CASE_AttestationManager";
	static final String AM_PORT_ATTESTATION_REQUEST_NAME = "am_request";
	static final String AM_PORT_ATTESTATION_RESPONSE_NAME = "am_response";
	static final String AM_IMPL_NAME = "AM";
	static final String CONNECTION_IMPL_NAME = "c";

	private String implementationName;
	private String implementationLanguage;
	private String cacheTimeout;
	private String cacheSize;
	private String logSize;
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
		if (hasAttestationManager(selectedSubcomponent)) {
			Dialog.showError("Add Attestation Manager",
					"Component " + selectedSubcomponent.getName() + " already has an associated attestation manager.");
			return;
		}

		// Open wizard to enter filter info
		final AddAttestationManagerDialog wizard = new AddAttestationManagerDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());

		List<String> importedRequirements = new ArrayList<>();
		RequirementsManager.getInstance().getImportedRequirements().forEach(r -> importedRequirements.add(r.getId()));
		wizard.create(selectedSubcomponent.getName(), importedRequirements);
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
			logSize = wizard.getLogSize();
			if (logSize.isEmpty()) {
				logSize = "0";
			}
			attestationRequirement = wizard.getRequirement();
			propagateGuarantees = wizard.getPropagateGuarantees();
			attestationAgreeProperty = wizard.getAgreeProperty();
		} else {
			return;
		}

		// Insert the attestation manager
		insertAttestationManager(uri);

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

				ComponentImplementation ci = commDriver.getContainingComponentImpl();

				final AadlPackage aadlPkg = (AadlPackage) resource.getContents().get(0);
				PackageSection pkgSection = null;
				// Figure out if the comm driver is in the public or private section
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
					Dialog.showError("No package section found", "No public or private package sections found.");
					return;
				}

				// Import CASE_Properties file
				if (!addCasePropertyImport(pkgSection)) {
					return;
				}
				// Import CASE_Model_Transformations file
				if (!addCaseModelTransformationsImport(pkgSection, true)) {
					return;
				}

				ComponentCategory compCategory = commDriver.getCategory();
				// If the component type is a process, we will need to put a single thread inside.
				// Per convention, we will attach all properties and contracts to the thread.
				// For this model transformation, we will create the thread first, then wrap it in a process
				// component, using the same mechanism we use for the seL4 transformation
				boolean isProcess = (compCategory == ComponentCategory.PROCESS);
				if (isProcess) {
					compCategory = ComponentCategory.THREAD;
				}

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
					}
				}

				// Get the request and response message types from the CASE_Model_Transformations package
				DataImplementation requestMsgImpl = null;
				DataImplementation responseMsgImpl = null;
				AadlPackage caseModelTransformationsPkg = getAadlPackage(CASE_MODEL_TRANSFORMATIONS_NAME,
						CASE_MODEL_TRANSFORMATIONS_FILE, resource.getResourceSet());
				for (Classifier classifier : caseModelTransformationsPkg.getOwnedPublicSection()
						.getOwnedClassifiers()) {
					if (classifier.getName().equalsIgnoreCase("CASE_AttestationRequestMsg.Impl")) {
						requestMsgImpl = (DataImplementation) classifier;
					} else if (classifier.getName().equalsIgnoreCase("CASE_AttestationResponseMsg.Impl")) {
						responseMsgImpl = (DataImplementation) classifier;
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

				// Add attestation request/response ports on comm driver (or create new comm driver component?)
				final ComponentType commDriverType = commDriver.getComponentType();
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

				// Add Attestation Manager properties
				PropertySet casePropSet = getPropertySet(CASE_PROPSET_NAME, CASE_PROPSET_FILE,
						resource.getResourceSet());
				// CASE_Properties::COMP_TYPE Property
				if (!addPropertyAssociation("COMP_TYPE", "ATTESTATION", attestationManagerType, casePropSet)) {
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
					// agree property is malformed, so leave blank
				}
				if (!addPropertyAssociation("COMP_SPEC", attestationPropId, attestationManagerType, casePropSet)) {
//					return;
				}

				// Put Attestation Manager in proper location (just after the comm driver)
				String destName = "";
				if (commDriver.getSubcomponentType() instanceof ComponentImplementation) {
					// Get the component type implementation name
					destName = commDriver.getComponentImplementation().getName();
				} else {
					// Get the component type name
					destName = commDriver.getComponentImplementation().getType().getName();
				}

				pkgSection.getOwnedClassifiers().move(getIndex(destName, pkgSection.getOwnedClassifiers()) + 1,
						pkgSection.getOwnedClassifiers().size() - 1);

				// Create Attestation Manager implementation
				final ComponentImplementation attestationManagerImpl = (ComponentImplementation) pkgSection
						.createOwnedClassifier(ComponentCreateHelper.getImplClass(compCategory));
				attestationManagerImpl.setName(attestationManagerType.getName() + ".Impl");
				final Realization r = attestationManagerImpl.createOwnedRealization();
				r.setImplemented(attestationManagerType);

				// Add it to proper place
				pkgSection.getOwnedClassifiers().move(getIndex(destName, pkgSection.getOwnedClassifiers()) + 2,
						pkgSection.getOwnedClassifiers().size() - 1);

				// CASE_Properties::COMP_IMPL property
				if (!addPropertyAssociation("COMP_IMPL", implementationLanguage, attestationManagerImpl,
						casePropSet)) {
//					return;
				}

				// CASE_Properties::CACHE_TIMEOUT property
				if (!addPropertyAssociation("CACHE_TIMEOUT", cacheTimeout, attestationManagerImpl, casePropSet)) {
//					return;
				}

				// CASE_Properties::CACHE_SIZE property
				if (!addPropertyAssociation("CACHE_SIZE", cacheSize, attestationManagerImpl, casePropSet)) {
//					return;
				}

				// CASE_Properties::LOG_SIZE property
				if (!addPropertyAssociation("LOG_SIZE", logSize, attestationManagerImpl, casePropSet)) {
//					return;
				}

				// Get the parent component implementation
				final ComponentImplementation containingImpl = commDriver.getContainingComponentImpl();

				// Insert attestation manager in process component implementation
				final Subcomponent attestationManagerSubcomp = ComponentCreateHelper
						.createOwnedSubcomponent(containingImpl, compCategory);

				// Give it a unique name
				attestationManagerSubcomp
						.setName(getUniqueName(implementationName, true, containingImpl.getOwnedSubcomponents()));
				// Assign thread implementation
				ComponentCreateHelper.setSubcomponentType(attestationManagerSubcomp, attestationManagerImpl);

				List<PortConnection> newPortConns = new ArrayList<>();

				// Create new connections between comm driver / attestation manager / destination components
				String connName = "";
				for (PortConnection conn : containingImpl.getOwnedPortConnections()) {
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
					newPortConn.setName(
							getUniqueName(CONNECTION_IMPL_NAME, false, containingImpl.getOwnedPortConnections()));
					containingImpl.getOwnedPortConnections().add(newPortConn);
					// Move to right place
					containingImpl.getOwnedPortConnections().move(
							getIndex(connName, containingImpl.getOwnedPortConnections()) + idxOffset,
							containingImpl.getOwnedPortConnections().size() - 1);
					idxOffset++;
				}

				// Add add_attestation claims to resolute prove statement, if applicable
				if (!attestationRequirement.isEmpty()) {

					NamedElement commDriverComp = null;
					if (commDriver.getSubcomponentType() instanceof ComponentImplementation) {
						// Get the component implementation
						commDriverComp = commDriver.getComponentImplementation();
					} else {
						// Get the component type
						commDriverComp = commDriverType;
					}

					RequirementsManager.getInstance().modifyRequirement(attestationRequirement, resource,
							new AddAttestationManagerClaim(commDriverComp, attestationManagerImpl));

				}

				// Propagate Agree Guarantees from comm driver, if there are any
				if (attestationAgreeProperty.length() > 0 || propagateGuarantees) {

					String agreeClauses = "{**" + System.lineSeparator();

					// Get guarantees from comm driver
					List<String> guarantees = new ArrayList<>();
					for (AnnexSubclause annexSubclause : commDriver.getComponentType().getOwnedAnnexSubclauses()) {
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
									for (Feature feature : commDriver.getComponentType().getOwnedFeatures()) {
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

					final DefaultAnnexSubclause annexSubclauseImpl = ComponentCreateHelper
							.createOwnedAnnexSubclause(attestationManagerType);
					annexSubclauseImpl.setName("agree");
					annexSubclauseImpl.setSourceText(agreeClauses);
				}

				if (isProcess) {

					// TODO: Wrap thread component in a process

					// TODO: Bind process to processor
				}

			}
		});

	}

	private boolean isCompType(Subcomponent comp, String compType) {

		EList<PropertyExpression> propVal = comp.getPropertyValues(CASE_PROPSET_NAME, "COMP_TYPE");
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
		return false;
	}

	private boolean hasAttestationManager(Subcomponent comp) {

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
						return true;
					}
				}
			}

		}

		return false;
	}

}
