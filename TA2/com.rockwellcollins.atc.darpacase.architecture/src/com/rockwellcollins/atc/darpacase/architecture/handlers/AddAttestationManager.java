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
import org.osate.aadl2.Aadl2Factory;
import org.osate.aadl2.Aadl2Package;
import org.osate.aadl2.AadlPackage;
import org.osate.aadl2.AnnexLibrary;
import org.osate.aadl2.AnnexSubclause;
import org.osate.aadl2.Classifier;
import org.osate.aadl2.ComponentImplementation;
import org.osate.aadl2.ComponentType;
import org.osate.aadl2.ConnectedElement;
import org.osate.aadl2.DataImplementation;
import org.osate.aadl2.DataPort;
import org.osate.aadl2.DataSubcomponent;
import org.osate.aadl2.DataSubcomponentType;
import org.osate.aadl2.DefaultAnnexLibrary;
import org.osate.aadl2.DefaultAnnexSubclause;
import org.osate.aadl2.EventDataPort;
import org.osate.aadl2.Feature;
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
import com.rockwellcollins.atc.darpacase.architecture.dialogs.AddAttestationManagerDialog;
import com.rockwellcollins.atc.resolute.resolute.Expr;
import com.rockwellcollins.atc.resolute.resolute.FnCallExpr;
import com.rockwellcollins.atc.resolute.resolute.ProveStatement;
import com.rockwellcollins.atc.resolute.resolute.ResoluteSubclause;

public class AddAttestationManager extends AadlHandler {

	static final String AM_REQUEST_MSG_NAME = "CASE_AttestationRequestMsg";
	static final String AM_RESPONSE_MSG_NAME = "CASE_AttestationResponseMsg";
	static final String AM_COMP_TYPE_NAME = "CASE_AttestationManager";
	static final String AM_PORT_ATTESTATION_REQUEST_NAME = "am_request";
	static final String AM_PORT_ATTESTATION_RESPONSE_NAME = "am_response";
	static final String AM_IMPL_NAME = "AM";
	static final String CONNECTION_IMPL_NAME = "c";

	private String commDriverComponent;
	private String implementationName;
	private String implementationLanguage;
	private String cacheTimeout;
	private String cacheSize;
	private String logSize;
	private String attestationResoluteClause;
	private boolean propagateGuarantees;
	private String attestationAgreeProperty;

	@Override
	protected void runCommand(URI uri) {

		// Check if it is a subcomponent or component implementation
		final EObject eObj = getEObject(uri);
		ComponentImplementation ci = null;
		String selectedSubcomponent = "";
		if (eObj instanceof Subcomponent) {
			ci = ((Subcomponent) eObj).getContainingComponentImpl();
			selectedSubcomponent = ((Subcomponent) eObj).getName();
		} else if (eObj instanceof ComponentImplementation) {
			ci = (ComponentImplementation) eObj;
		} else {
			Dialog.showError("No component is selected",
					"A communication driver subcomponent (or its containing implementation) must be selected to add an attestation manager.");
			return;
		}

		// Open wizard to enter filter info
		final AddAttestationManagerDialog wizard = new AddAttestationManagerDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		List<String> subcomponents = new ArrayList<>();
		for (Subcomponent comp : ci.getAllSubcomponents()) {
			subcomponents.add(comp.getName());
		}
		wizard.create(selectedSubcomponent, subcomponents, getResoluteClauses(selectedSubcomponent, ci));
		if (wizard.open() == Window.OK) {
			commDriverComponent = wizard.getCommDriverComponent();
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
			attestationResoluteClause = wizard.getResoluteClause();
			propagateGuarantees = wizard.getPropagateGuarantees();
			attestationAgreeProperty = wizard.getAgreeProperty();
		} else {
			return;
		}

		// Insert the attestation manager
		insertAttestationManager(ci);

		return;

	}

	/**
	 * Inserts an attestation manager component into the model.  The attestation manager is inserted at
	 * the location of the selected connection
	 * @param uri - The URI of the selected connection
	 */
	private void insertAttestationManager(ComponentImplementation ci) {

		// Get the active xtext editor so we can make modifications
		final XtextEditor xtextEditor = EditorUtils.getActiveXtextEditor();

		xtextEditor.getDocument().modify(new IUnitOfWork.Void<XtextResource>() {

			@Override
			public void process(final XtextResource resource) throws Exception {

				ThreadSubcomponent commDriver = null;
				for (Subcomponent comp : ci.getAllSubcomponents()) {
					if (comp.getName().equalsIgnoreCase(commDriverComponent)) {
						commDriver = (ThreadSubcomponent) comp;
						break;
					}
				}
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

				// TODO: Should this be part of AadlHandler?
				// CASE Model Transformations file
				// CASE Property file
				// First check if CASE Property file has already been imported in the model
				final EList<ModelUnit> importedUnits = pkgSection.getImportedUnits();
				PropertySet casePropSet = null;
				AadlPackage caseModelTransformationsPkg = null;
				for (ModelUnit modelUnit : importedUnits) {
					if (modelUnit instanceof PropertySet) {
						if (modelUnit.getName().equalsIgnoreCase(CASE_PROPSET_NAME)) {
							casePropSet = (PropertySet) modelUnit;
						}
					}
					else if (modelUnit instanceof AadlPackage) {
						if (modelUnit.getName().equalsIgnoreCase(CASE_MODEL_TRANSFORMATIONS_NAME)) {
							caseModelTransformationsPkg = (AadlPackage) modelUnit;
						}
					}
				}

				if (casePropSet == null) {
					// Try importing the resource
					casePropSet = getPropertySet(CASE_PROPSET_NAME, CASE_PROPSET_FILE, resource.getResourceSet());
					if (casePropSet == null) {
						Dialog.showError("Could not import " + CASE_PROPSET_NAME,
								"Property set " + CASE_PROPSET_NAME + " could not be found.");
						return;
					}
					// Add as "importedUnit" to package section
					pkgSection.getImportedUnits().add(casePropSet);
				}
				if (caseModelTransformationsPkg == null) {
					// Try importing the resource
					caseModelTransformationsPkg = getAadlPackage(CASE_MODEL_TRANSFORMATIONS_NAME,
							CASE_MODEL_TRANSFORMATIONS_FILE, resource.getResourceSet());
					if (caseModelTransformationsPkg == null) {
						Dialog.showError("Could not import " + CASE_MODEL_TRANSFORMATIONS_NAME,
								"AADL package " + CASE_MODEL_TRANSFORMATIONS_NAME + " could not be found.");
						return;
					}
				}

				// TODO: Add CASE_Model_Transformations rename, if not already present

				// TODO: check to see if the comm driver already has an attestation manager?

//				// Create Attestation Request and Response message types, if they don't already exist
//				DataImplementation requestMsgImpl = null;
//				DataImplementation responseMsgImpl = null;
//				for (Classifier classifier : pkgSection.getOwnedClassifiers()) {
//					if (classifier.getName().equalsIgnoreCase(AM_REQUEST_MSG_NAME + ".Impl")) {
//						requestMsgImpl = (DataImplementation) classifier;
//					} else if (classifier.getName().equalsIgnoreCase(AM_RESPONSE_MSG_NAME + ".Impl")) {
//						responseMsgImpl = (DataImplementation) classifier;
//					}
//				}
//				if (requestMsgImpl == null) {
//					// Data type
//					final DataType requestMsg = (DataType) pkgSection
//							.createOwnedClassifier(Aadl2Package.eINSTANCE.getDataType());
//					// Give it a name
//					requestMsg.setName(AM_REQUEST_MSG_NAME);
//					// Move it to the top
//					pkgSection.getOwnedClassifiers().move(0, pkgSection.getOwnedClassifiers().size() - 1);
//					// Data implementation
//					requestMsgImpl = (DataImplementation) pkgSection
//							.createOwnedClassifier(Aadl2Package.eINSTANCE.getDataImplementation());
//					// Give it a name
//					requestMsgImpl.setName(AM_REQUEST_MSG_NAME + ".Impl");
//					// Assign realization
//					final Realization r = requestMsgImpl.createOwnedRealization();
//					r.setImplemented(requestMsg);
//					// Set data subcomponents
//					DataSubcomponent data = requestMsgImpl.createOwnedDataSubcomponent();
//					data.setName("problem");
//					DataType dataType = Aadl2Factory.eINSTANCE.createDataType();
//					dataType.setName("Base_Types::String");
//					data.setDataSubcomponentType(dataType);
//					// Move it under data type
//					pkgSection.getOwnedClassifiers().move(1, pkgSection.getOwnedClassifiers().size() - 1);
//				}
//				if (responseMsgImpl == null) {
//					// Data type
//					final DataType responseMsg = (DataType) pkgSection
//							.createOwnedClassifier(Aadl2Package.eINSTANCE.getDataType());
//					// Give it a name
//					responseMsg.setName(AM_RESPONSE_MSG_NAME);
//					// Move it under request
//					pkgSection.getOwnedClassifiers().move(2, pkgSection.getOwnedClassifiers().size() - 1);
//					// Data implementation
//					responseMsgImpl = (DataImplementation) pkgSection
//							.createOwnedClassifier(Aadl2Package.eINSTANCE.getDataImplementation());
//					// Give it a name
//					responseMsgImpl.setName(AM_RESPONSE_MSG_NAME + ".Impl");
//					// Assign realization
//					final Realization r = responseMsgImpl.createOwnedRealization();
//					r.setImplemented(responseMsg);
//					// Set data subcomponents
//					DataSubcomponent data = responseMsgImpl.createOwnedDataSubcomponent();
//					data.setName("solution");
//					DataType dataType = Aadl2Factory.eINSTANCE.createDataType();
//					dataType.setName("Base_Types::String");
//					data.setDataSubcomponentType(dataType);
//					// Move it under data type
//					pkgSection.getOwnedClassifiers().move(3, pkgSection.getOwnedClassifiers().size() - 1);
//				}

				// Add AGREE to Comm Driver implementation
				String commAgree = "";
				ThreadImplementation commImpl = (ThreadImplementation) commDriver.getComponentImplementation();
				for (AnnexSubclause annexSubclause : commImpl.getOwnedAnnexSubclauses()) {
					if (annexSubclause.getName().equalsIgnoreCase("agree")) {
						DefaultAnnexSubclause commSubclause = (DefaultAnnexSubclause) annexSubclause;
						commAgree = commSubclause.getSourceText();
						break;
					}
				}

				if (commAgree.isEmpty()) {
					commAgree = "{**" + System.lineSeparator();
				} else {
					commAgree = commAgree.replace("**}", "").trim() + System.lineSeparator() + System.lineSeparator();
				}

				commAgree = commAgree + System.lineSeparator();
				commAgree = commAgree + "\t\t\t-- a request will trigger a response" + System.lineSeparator();
				commAgree = commAgree + "\t\t\teq response_header : CASE_MsgHeader.Impl = " + System.lineSeparator();
				commAgree = commAgree + "\t\t\t\tif radio_msg then" + System.lineSeparator();
				commAgree = commAgree + "\t\t\t\t\t" + CASE_MODEL_TRANSFORMATIONS_NAME + ".NULL_HEADER"
						+ System.lineSeparator();
				commAgree = commAgree + "\t\t\t\telse" + System.lineSeparator();
				commAgree = commAgree
						+ "\t\t\t\t\tCASE_MsgHeader.Impl {src = am_response.header.dst; dst = CASE_UAV_ID; HMAC = true};"
						+ System.lineSeparator() + System.lineSeparator();
				commAgree = commAgree + "\t\t\t-- assign header info to am_response message" + System.lineSeparator();
				commAgree = commAgree + "\t\t\tassert(am_response.header = response_header);" + System.lineSeparator();
				commAgree = commAgree + System.lineSeparator() + "\t\t**}";

				commImpl.getOwnedAnnexSubclauses().removeIf(subclause -> subclause.getName().equalsIgnoreCase("agree"));
				DefaultAnnexSubclause commImplSubclause = commImpl.createOwnedAnnexSubclause();
				commImplSubclause.setName("agree");
				commImplSubclause.setSourceText(commAgree);

				// Create Attestation Manager thread type
				final ThreadType attestationManagerThreadType = (ThreadType) pkgSection
						.createOwnedClassifier(Aadl2Package.eINSTANCE.getThreadType());
				// Give it a unique name
				attestationManagerThreadType
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
							portIn = attestationManagerThreadType.createOwnedEventDataPort();
							dataFeatureClassifier = ((EventDataPort) commPort).getDataFeatureClassifier();
							((EventDataPort) portIn).setDataFeatureClassifier(dataFeatureClassifier);
							portOut = attestationManagerThreadType.createOwnedEventDataPort();
							((EventDataPort) portOut).setDataFeatureClassifier(dataFeatureClassifier);
						} else if (commPort instanceof DataPort) {
							portIn = attestationManagerThreadType.createOwnedDataPort();
							dataFeatureClassifier = ((DataPort) commPort).getDataFeatureClassifier();
							((DataPort) portIn).setDataFeatureClassifier(dataFeatureClassifier);
							portOut = attestationManagerThreadType.createOwnedDataPort();
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
				for (Classifier classifier : caseModelTransformationsPkg.getOwnedPublicSection()
						.getOwnedClassifiers()) {
					if (classifier.getName().equalsIgnoreCase("CASE_AttestationRequestMsg.Impl")) {
						requestMsgImpl = (DataImplementation) classifier;
					} else if (classifier.getName().equalsIgnoreCase("CASE_AttestationResponseMsg.Impl")) {
						responseMsgImpl = (DataImplementation) classifier;
					}
				}


				// Add the ports for communicating attestation requests/responses with the Comm Driver
				final EventDataPort amReq = attestationManagerThreadType.createOwnedEventDataPort();
				final EventDataPort amRes = attestationManagerThreadType.createOwnedEventDataPort();
				// Set data feature classifier
				amReq.setDataFeatureClassifier(requestMsgImpl);
				amRes.setDataFeatureClassifier(responseMsgImpl);
				amReq.setName(AM_PORT_ATTESTATION_REQUEST_NAME);
				amReq.setOut(true);
				amRes.setName(AM_PORT_ATTESTATION_RESPONSE_NAME);
				amRes.setIn(true);

				// Add attestation request/response ports on comm driver (or create new comm driver component?)
				final ThreadType commDriverThreadType = (ThreadType) commDriver.getComponentType();
				final EventDataPort commReq = commDriverThreadType.createOwnedEventDataPort();
				final EventDataPort commRes = commDriverThreadType.createOwnedEventDataPort();
				commReq.setDataFeatureClassifier(requestMsgImpl);
				commRes.setDataFeatureClassifier(responseMsgImpl);
				commReq.setName(AM_PORT_ATTESTATION_REQUEST_NAME);
				commReq.setIn(true);
				commRes.setName(AM_PORT_ATTESTATION_RESPONSE_NAME);
				commRes.setOut(true);

				// Add Attestation Manager properties
				// CASE_Properties::COMP_TYPE Property
				if (!addPropertyAssociation("COMP_TYPE", "ATTESTATION", attestationManagerThreadType,
						casePropSet)) {
//					return;
				}
				// CASE_Properties::COMP_IMPL property
				if (!addPropertyAssociation("COMP_IMPL", implementationLanguage, attestationManagerThreadType,
						casePropSet)) {
//					return;
				}

				// CASE_Properties::COMP_SPEC property
				// Parse the ID from the Attestation Manager AGREE property
				String attestationPropId = "";
				try {
					attestationPropId = attestationAgreeProperty
						.substring(attestationAgreeProperty.toLowerCase().indexOf("guarantee ") + "guarantee ".length(),
								attestationAgreeProperty.indexOf("\""))
						.trim();
				} catch (IndexOutOfBoundsException e) {
					// agree property is malformed, so leave blank
				}
				if (!addPropertyAssociation("COMP_SPEC", attestationPropId, attestationManagerThreadType,
						casePropSet)) {
//					return;
				}

				// CASE_Properties::CACHE_TIMEOUT property
				if (!addPropertyAssociation("CACHE_TIMEOUT", cacheTimeout, attestationManagerThreadType, casePropSet)) {
//					return;
				}

				// CASE_Properties::CACHE_SIZE property
				if (!addPropertyAssociation("CACHE_SIZE", cacheSize, attestationManagerThreadType, casePropSet)) {
//					return;
				}

				// CASE_Properties::LOG_SIZE property
				if (!addPropertyAssociation("LOG_SIZE", logSize, attestationManagerThreadType, casePropSet)) {
//					return;
				}

				// Put Attestation Manager in proper location (just after the comm driver)
				String destName = "";
				if (commDriver.getThreadSubcomponentType() instanceof ThreadImplementation) {
					// Get the component type implementation name
					destName = commDriver.getComponentImplementation().getName();
				} else {
					// Get the component type name
					destName = commDriver.getComponentImplementation().getType().getName();
				}

				pkgSection.getOwnedClassifiers().move(getIndex(destName, pkgSection.getOwnedClassifiers()) + 1,
						pkgSection.getOwnedClassifiers().size() - 1);

				// Create Attestation Manager implementation
				final ThreadImplementation attestationManagerThreadImpl = (ThreadImplementation) pkgSection
						.createOwnedClassifier(Aadl2Package.eINSTANCE.getThreadImplementation());
				attestationManagerThreadImpl.setName(attestationManagerThreadType.getName() + ".Impl");
				final Realization r = attestationManagerThreadImpl.createOwnedRealization();
				r.setImplemented(attestationManagerThreadType);

				// Add AGREE
				String amImplAgree = "{**" + System.lineSeparator() + System.lineSeparator();
				amImplAgree = amImplAgree + "\t\t\t-- Determine if it is a response message" + System.lineSeparator();
				amImplAgree = amImplAgree + "\t\t\teq am_response_msg : bool = not " + CASE_MODEL_TRANSFORMATIONS_NAME
						+ ".NULL_MESSAGE(am_response.header);" + System.lineSeparator() + System.lineSeparator();

				amImplAgree = amImplAgree
						+ "\t\t\t-- Attestation Manager will not process more than one regular message at a time"
						+ System.lineSeparator();
				amImplAgree = amImplAgree + "\t\t\tfun MSG_CTR (null_msg : bool) : int = if null_msg then 0 else 1;"
						+ System.lineSeparator();
				amImplAgree = amImplAgree + "\t\t\tproperty one_incoming_message = (";
				for (int i = 0; i < amPortNames.size(); i++) {
					amImplAgree = amImplAgree + "\t\t\t\tMSG_CTR(" + CASE_MODEL_TRANSFORMATIONS_NAME
							+ ".NULL_MESSAGE(am_" + amPortNames.get(i) + "_in.header))" + " + "
							+ System.lineSeparator();
//					if (i < amPortNames.size() - 1) {
//						amImplAgree = amImplAgree + " + " + System.lineSeparator();
//					}
				}
				amImplAgree = amImplAgree + "\t\t\t\tMSG_CTR(" + CASE_MODEL_TRANSFORMATIONS_NAME
						+ ".NULL_MESSAGE(am_response.header))";
				amImplAgree = amImplAgree + ") <= 1;" + System.lineSeparator();
				amImplAgree = amImplAgree + "\t\t\tassert(one_incoming_message);" + System.lineSeparator()
						+ System.lineSeparator();

				amImplAgree = amImplAgree + "\t\t\t-- source of the transmitted message, if one was sent"
						+ System.lineSeparator();
				amImplAgree = amImplAgree + "\t\t\teq msg_src : int = " + System.lineSeparator();
				for (int i = 0; i < amPortNames.size(); i++) {
					if (i == 0) {
						amImplAgree = amImplAgree + "\t\t\t\tif not " + CASE_MODEL_TRANSFORMATIONS_NAME
								+ ".NULL_MESSAGE(am_" + amPortNames.get(i) + "_in.header) then"
								+ System.lineSeparator();
						amImplAgree = amImplAgree + "\t\t\t\t\tam_" + amPortNames.get(i) + "_in.header.src"
								+ System.lineSeparator();
					} else {
						amImplAgree = amImplAgree + "\t\t\t\telse if not " + CASE_MODEL_TRANSFORMATIONS_NAME
								+ ".MESSAGE_HEADER(am_" + amPortNames.get(i) + "_in.header) then"
								+ System.lineSeparator();
						amImplAgree = amImplAgree + "\t\t\t\t\tam_" + amPortNames.get(i) + "_in.header.src"
								+ System.lineSeparator();
					}
				}
				amImplAgree = amImplAgree + "\t\t\t\telse" + System.lineSeparator();
				amImplAgree = amImplAgree + "\t\t\t\t\t" + CASE_MODEL_TRANSFORMATIONS_NAME + ".NULL_ID;"
						+ System.lineSeparator() + System.lineSeparator();

				amImplAgree = amImplAgree
						+ "\t\t\t-- Helper function to get the header from the attestation manager inport with the specified ID"
						+ System.lineSeparator();
				amImplAgree = amImplAgree + "\t\t\tfun GET_HEADER(src : int) : CASE_MsgHeader.Impl = "
						+ System.lineSeparator();
				for (int i = 0; i < amPortNames.size(); i++) {
					if (i == 0) {
						amImplAgree = amImplAgree + "\t\t\t\tif am_" + amPortNames.get(i)
								+ "_in.header.src = src then am_" + amPortNames.get(i) + "_in.header"
								+ System.lineSeparator();
					} else {
						amImplAgree = amImplAgree + "\t\t\t\telse if am_" + amPortNames.get(i)
								+ "_in.header.src = src then am_" + amPortNames.get(i) + "_in.header"
								+ System.lineSeparator();
					}
				}
				amImplAgree = amImplAgree + "\t\t\t\telse " + CASE_MODEL_TRANSFORMATIONS_NAME + ".NULL_HEADER;"
						+ System.lineSeparator() + System.lineSeparator();

				amImplAgree = amImplAgree + "\t\t\t-- Set am_request message" + System.lineSeparator();
				amImplAgree = amImplAgree + "\t\t\teq am_request_msg : CASE_AttestationRequestMsg.Impl = "
						+ System.lineSeparator();
				amImplAgree = amImplAgree + "\t\t\t\tif am_response_msg then" + System.lineSeparator();
				amImplAgree = amImplAgree + "\t\t\t\t\t-- it's a response message so no need to send a request"
						+ System.lineSeparator();
				amImplAgree = amImplAgree + "\t\t\t\t\t" + CASE_MODEL_TRANSFORMATIONS_NAME + ".NULL_AM_REQUEST_MESSAGE"
						+ System.lineSeparator();
				amImplAgree = amImplAgree + "\t\t\t\telse if " + CASE_MODEL_TRANSFORMATIONS_NAME
						+ ".NULL_MESSAGE(GET_HEADER(msg_src)) then" + System.lineSeparator();
				amImplAgree = amImplAgree + "\t\t\t\t\t-- it's an empty message" + System.lineSeparator();
				amImplAgree = amImplAgree + "\t\t\t\t\t" + CASE_MODEL_TRANSFORMATIONS_NAME + ".NULL_AM_REQUEST_MESSAGE"
						+ System.lineSeparator();
				amImplAgree = amImplAgree + "\t\t\t\telse if not " + CASE_MODEL_TRANSFORMATIONS_NAME
						+ ".IN_CACHE(msg_src) or " + CASE_MODEL_TRANSFORMATIONS_NAME + ".IS_STALE(msg_src) then"
						+ System.lineSeparator();
				amImplAgree = amImplAgree
						+ "\t\t\t\t\t-- it's a transmitted message from a source not in the cache, so send an am_request"
						+ System.lineSeparator();
				amImplAgree = amImplAgree
						+ "\t\t\t\t\t-- or it's a transmitted message from a source in the cache, but stale, so send a new am_request"
						+ System.lineSeparator();
				amImplAgree = amImplAgree
						+ "\t\t\t\t\tCASE_AttestationRequestMsg.Impl {header = CASE_MsgHeader.Impl {src = CASE_UAV_ID; dst = msg_src; HMAC = true}}"
						+ System.lineSeparator();
				amImplAgree = amImplAgree + "\t\t\t\telse" + System.lineSeparator();
				amImplAgree = amImplAgree + "\t\t\t\t\t-- it's a tramsmitted message from a source in the cache"
						+ System.lineSeparator();
				amImplAgree = amImplAgree + "\t\t\t\t\t-- regardless of the attestation status, we won't send a request"
						+ System.lineSeparator();
				amImplAgree = amImplAgree + "\t\t\t\t\t" + CASE_MODEL_TRANSFORMATIONS_NAME + ".NULL_AM_REQUEST_MESSAGE;"
						+ System.lineSeparator() + System.lineSeparator();

				amImplAgree = amImplAgree + "\t\t\t-- set attestation manager out messages" + System.lineSeparator();
				for (int i = 0; i < amPortNames.size(); i++) {
					DataImplementation di = amPortTypes.get(i);
					amImplAgree = amImplAgree + "\t\t\teq am_" + amPortNames.get(i) + "_out_msg : " + di.getName()
							+ " = "
							+ System.lineSeparator();
					amImplAgree = amImplAgree + "\t\t\t\tif am_response_msg then" + System.lineSeparator();
					String nullMsg = di.getName() + " {header = " + CASE_MODEL_TRANSFORMATIONS_NAME + ".NULL_HEADER";
					for (DataSubcomponent sub : di.getOwnedDataSubcomponents()) {
						if (sub.getName().equalsIgnoreCase("header")) {
							continue;
						}
						nullMsg = nullMsg + "; " + sub.getName() + " = am_" + amPortNames.get(i) + "_out."
								+ sub.getName();
					}
					nullMsg = nullMsg + "}";
					amImplAgree = amImplAgree + "\t\t\t\t\t" + nullMsg + System.lineSeparator();
					amImplAgree = amImplAgree + "\t\t\t\telse if " + CASE_MODEL_TRANSFORMATIONS_NAME
							+ ".NULL_MESSAGE(am_"
							+ amPortNames.get(i) + "_in.header) then" + System.lineSeparator();
					amImplAgree = amImplAgree + "\t\t\t\t\tam_" + amPortNames.get(i) + "_in" + System.lineSeparator();
					amImplAgree = amImplAgree + "\t\t\t\telse if not " + CASE_MODEL_TRANSFORMATIONS_NAME
							+ ".IN_CACHE(msg_src) or " + CASE_MODEL_TRANSFORMATIONS_NAME + ".IS_STALE(msg_src) then"
							+ System.lineSeparator();
					amImplAgree = amImplAgree + "\t\t\t\t\t" + nullMsg + System.lineSeparator();
					amImplAgree = amImplAgree + "\t\t\t\telse if " + CASE_MODEL_TRANSFORMATIONS_NAME
							+ ".PASS_ATTESTATION(msg_src) then"
							+ System.lineSeparator();
					amImplAgree = amImplAgree + "\t\t\t\t\tam_" + amPortNames.get(i) + "_in"
							+ System.lineSeparator();
					amImplAgree = amImplAgree + "\t\t\t\telse" + System.lineSeparator();
					amImplAgree = amImplAgree + "\t\t\t\t\t" + nullMsg + ";" + System.lineSeparator()
							+ System.lineSeparator();
				}

				amImplAgree = amImplAgree + "\t\t\t-- Update cache" + System.lineSeparator();
				amImplAgree = amImplAgree + "\t\t\tconst AM_CACHE_SIZE : int = " + cacheSize + ";"
						+ System.lineSeparator();
				amImplAgree = amImplAgree + "\t\t\teq was_response_requested : bool = am_response_msg and "
						+ CASE_MODEL_TRANSFORMATIONS_NAME + ".GET_STATUS(am_response.header.src) = "
						+ CASE_MODEL_TRANSFORMATIONS_NAME + ".AM_REQUESTING;" + System.lineSeparator();
				amImplAgree = amImplAgree + "\t\t\teq am_status : int = if am_response_msg and am_response.status then "
						+ CASE_MODEL_TRANSFORMATIONS_NAME + ".AM_PASS else " + CASE_MODEL_TRANSFORMATIONS_NAME
						+ ".AM_FAIL;" + System.lineSeparator();
				amImplAgree = amImplAgree + "\t\t\teq src_added : bool = " + System.lineSeparator();
				amImplAgree = amImplAgree + "\t\t\t\tif was_response_requested then" + System.lineSeparator();
				amImplAgree = amImplAgree + "\t\t\t\t\t" + CASE_MODEL_TRANSFORMATIONS_NAME
						+ ".ADD_RECORD(am_response.header.src, am_status, 0, AM_CACHE_SIZE)"
						+ System.lineSeparator();
				amImplAgree = amImplAgree + "\t\t\t\telse if not (am_request_msg = " + CASE_MODEL_TRANSFORMATIONS_NAME
						+ ".NULL_AM_REQUEST_MESSAGE) then" + System.lineSeparator();
				amImplAgree = amImplAgree + "\t\t\t\t\t" + CASE_MODEL_TRANSFORMATIONS_NAME + ".ADD_RECORD(msg_src, "
						+ CASE_MODEL_TRANSFORMATIONS_NAME + ".AM_REQUESTING, 0, AM_CACHE_SIZE)"
						+ System.lineSeparator();
				amImplAgree = amImplAgree + "\t\t\t\telse" + System.lineSeparator();
				amImplAgree = amImplAgree + "\t\t\t\t\tfalse;" + System.lineSeparator() + System.lineSeparator();

				amImplAgree = amImplAgree + "\t\t\t-- assert outputs" + System.lineSeparator();
				amImplAgree = amImplAgree + "\t\t\tassert(am_request = am_request_msg);" + System.lineSeparator();
				amImplAgree = amImplAgree + "\t\t\tassert(was_response_requested => src_added);"
						+ System.lineSeparator();
				for (int i = 0; i < amPortNames.size(); i++) {
					amImplAgree = amImplAgree + "\t\t\tassert(am_" + amPortNames.get(i) + "_out = am_"
							+ amPortNames.get(i) + "_out_msg);" + System.lineSeparator();
				}
				amImplAgree = amImplAgree + System.lineSeparator() + "\t\t**}";

				final DefaultAnnexSubclause amAgreeSubclause = attestationManagerThreadImpl.createOwnedAnnexSubclause();
				amAgreeSubclause.setName("agree");
				amAgreeSubclause.setSourceText(amImplAgree);

				// Add it to proper place
				pkgSection.getOwnedClassifiers().move(getIndex(destName, pkgSection.getOwnedClassifiers()) + 2,
						pkgSection.getOwnedClassifiers().size() - 1);

				// Make a copy of the process component implementation
				final ProcessImplementation procImpl = (ProcessImplementation) commDriver
						.getContainingComponentImpl();
				final ProcessImplementation newImpl = EcoreUtil.copy(procImpl);
				// Give it a unique name
				newImpl.setName(getUniqueName(newImpl.getName(), true, pkgSection.getOwnedClassifiers()));

				// Change commDriver to refer to the subcomponent on the new implementation
				for (ThreadSubcomponent c : newImpl.getOwnedThreadSubcomponents()) {
					if (c.getName().equalsIgnoreCase(commDriver.getName())) {
						commDriver = c;
						break;
					}
				}

				// Insert attestation manager in process component implementation
				final ThreadSubcomponent attestationManagerThreadSubComp = newImpl.createOwnedThreadSubcomponent();

				// Give it a unique name
				attestationManagerThreadSubComp
						.setName(getUniqueName(implementationName, true, newImpl.getOwnedSubcomponents()));
				// Assign thread implementation
				attestationManagerThreadSubComp.setThreadSubcomponentType(attestationManagerThreadImpl);

				// Put it in the right place
				newImpl.getOwnedThreadSubcomponents().move(
						getIndex(commDriver.getName(), newImpl.getOwnedThreadSubcomponents()) + 1,
						newImpl.getOwnedThreadSubcomponents().size() - 1);

//				// Create attestation request / response connections between comm driver and attestation manager
				List<PortConnection> newPortConns = new ArrayList<>();
////				final PortConnection portConnReq = newImpl.createOwnedPortConnection();
//				final PortConnection portConnReq = Aadl2Factory.eINSTANCE.createPortConnection();
//				// Give it a unique name
////				portConnReq.setName(getUniqueName(CONNECTION_IMPL_NAME, false, newImpl.getOwnedPortConnections()));
//				portConnReq.setBidirectional(false);
//				final ConnectedElement reqSrc = portConnReq.createSource();
//				reqSrc.setContext(attestationManagerThreadSubComp);
//				reqSrc.setConnectionEnd(amReq);
//				final ConnectedElement reqDst = portConnReq.createDestination();
//				reqDst.setContext(commDriver);
//				reqDst.setConnectionEnd(commReq);
//				newPortConns.add(portConnReq);
//
////				final PortConnection portConnRes = newImpl.createOwnedPortConnection();
//				final PortConnection portConnRes = Aadl2Factory.eINSTANCE.createPortConnection();
//				// Give it a unique name
////				portConnRes.setName(getUniqueName(CONNECTION_IMPL_NAME, false, newImpl.getOwnedPortConnections()));
//				portConnRes.setBidirectional(false);
//				final ConnectedElement resSrc = portConnRes.createSource();
//				resSrc.setContext(commDriver);
//				resSrc.setConnectionEnd(amRes);
//				final ConnectedElement resDst = portConnRes.createDestination();
//				resDst.setContext(attestationManagerThreadSubComp);
//				resDst.setConnectionEnd(commRes);
//				newPortConns.add(portConnRes);

				// Create new connections between comm driver / attestation manager / destination components
				String connName = "";
				for (PortConnection conn : newImpl.getOwnedPortConnections()) {
					// Ignore bus connections (destination context not null)
					if (conn.getSource().getContext() == commDriver && conn.getDestination().getContext() != null) {
						// Create connection from attestation manager to destination components
//						final PortConnection portConnOut = newImpl.createOwnedPortConnection();
						final PortConnection portConnOut = Aadl2Factory.eINSTANCE.createPortConnection();
						// Give it a unique name
//						portConnOut
//								.setName(getUniqueName(CONNECTION_IMPL_NAME, false, newImpl.getOwnedPortConnections()));
						portConnOut.setBidirectional(false);
						final ConnectedElement connSrc = portConnOut.createSource();
						connSrc.setContext(attestationManagerThreadSubComp);
						for (Feature feature : attestationManagerThreadType.getAllFeatures()) {
							if (feature.getName().equalsIgnoreCase(
									"am_" + conn.getSource().getConnectionEnd().getName() + "_out")) {
								connSrc.setConnectionEnd(feature);
								break;
							}
						}
						final ConnectedElement connDst = portConnOut.createDestination();
						connDst.setContext(conn.getDestination().getContext());
						connDst.setConnectionEnd(conn.getDestination().getConnectionEnd());

						newPortConns.add(portConnOut);

						// Rewire connections from comm driver into attestation manager
						conn.getDestination().setContext(attestationManagerThreadSubComp);
						for (Feature feature : attestationManagerThreadType.getAllFeatures()) {
							if (feature.getName().equalsIgnoreCase(
									"am_" + conn.getSource().getConnectionEnd().getName() + "_in")) {
								conn.getDestination().setConnectionEnd(feature);
							}
						}

						// In order to put new connections in right place, keep track of
						// the last relevant comm driver connection name
						connName = conn.getName();

					}
				}

				// Create attestation request / response connections between comm driver and attestation manager
//				List<PortConnection> newPortConns = new ArrayList<>();
//				final PortConnection portConnReq = newImpl.createOwnedPortConnection();
				final PortConnection portConnReq = Aadl2Factory.eINSTANCE.createPortConnection();
				// Give it a unique name
//				portConnReq.setName(getUniqueName(CONNECTION_IMPL_NAME, false, newImpl.getOwnedPortConnections()));
				portConnReq.setBidirectional(false);
				final ConnectedElement reqSrc = portConnReq.createSource();
				reqSrc.setContext(attestationManagerThreadSubComp);
				reqSrc.setConnectionEnd(amReq);
				final ConnectedElement reqDst = portConnReq.createDestination();
				reqDst.setContext(commDriver);
				reqDst.setConnectionEnd(commReq);
				newPortConns.add(portConnReq);

//				final PortConnection portConnRes = newImpl.createOwnedPortConnection();
				final PortConnection portConnRes = Aadl2Factory.eINSTANCE.createPortConnection();
				// Give it a unique name
//				portConnRes.setName(getUniqueName(CONNECTION_IMPL_NAME, false, newImpl.getOwnedPortConnections()));
				portConnRes.setBidirectional(false);
				final ConnectedElement resSrc = portConnRes.createSource();
				resSrc.setContext(commDriver);
				resSrc.setConnectionEnd(amRes);
				final ConnectedElement resDst = portConnRes.createDestination();
				resDst.setContext(attestationManagerThreadSubComp);
				resDst.setConnectionEnd(commRes);
				newPortConns.add(portConnRes);

				int idxOffset = 1;
				for (PortConnection newPortConn : newPortConns) {
					// Make sure each new connection has a unique name
					newPortConn.setName(getUniqueName(CONNECTION_IMPL_NAME, false, newImpl.getOwnedPortConnections()));
					newImpl.getOwnedPortConnections().add(newPortConn);
					// Move to right place
					newImpl.getOwnedPortConnections().move(
							getIndex(connName, newImpl.getOwnedPortConnections()) + idxOffset,
							newImpl.getOwnedPortConnections().size() - 1);
					idxOffset++;
				}

				// Add new implementation to package and place immediately above original implementation
				pkgSection.getOwnedClassifiers().add(newImpl);
				pkgSection.getOwnedClassifiers().move(getIndex(procImpl.getName(), pkgSection.getOwnedClassifiers()),
						pkgSection.getOwnedClassifiers().size() - 1);

				// Add add_attestation claims to resolute prove statement, if applicable
				if (!attestationResoluteClause.isEmpty()) {
					// Add arguments to prove statements in components
					// TODO: Make sure they are just the components connected to the selected comm driver
					for (Subcomponent comp : ci.getOwnedSubcomponents()) {
						if (comp instanceof ThreadSubcomponent) {
//							final ThreadType clauseThread = (ThreadType) commDriver.getComponentType();
							final ThreadType clauseThread = (ThreadType) comp.getComponentType();
							EList<AnnexSubclause> annexSubclauses = clauseThread.getOwnedAnnexSubclauses();
							for (AnnexSubclause annexSubclause : annexSubclauses) {
								// Get the Resolute clause
								if (annexSubclause.getName().equalsIgnoreCase("resolute")) {
									DefaultAnnexSubclause annexSubclauseImpl = (DefaultAnnexSubclause) annexSubclause;
									String sourceText = annexSubclauseImpl.getSourceText();
									if (sourceText.contains(attestationResoluteClause + "(")) {
										// Add arguments
										int startIdx = sourceText.indexOf(attestationResoluteClause + "(")
												+ attestationResoluteClause.length() + 1;
										String args = sourceText.substring(startIdx, sourceText.indexOf(")", startIdx));
										sourceText = sourceText.replace(attestationResoluteClause + "(" + args + ")",
												attestationResoluteClause + "(" + args + ", "
														+ commDriverThreadType.getName() + ", "
														+ attestationManagerThreadType.getName() + ")");
										annexSubclauseImpl.setSourceText(sourceText);
									}
									break;
								}
							}
						}
					}

					// Add add_attestation claims to *_CASE_Claims file
					// If the prove statement exists, the *_CASE_Claims file should also already
					// exist, but double check just to be sure, and create it if it doesn't
					CaseClaimsManager.getInstance().addAttestationManager(attestationResoluteClause);

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
							String specs = unparser.unparseContract((AgreeContract) agreeContract.getContract(), "");
							int gCtr = 1;
							for (String line : specs.split(System.lineSeparator())) {
								if (line.trim().toLowerCase().startsWith("guarantee")) {
									String guarantee = line.trim();

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
										expr = expr.replace(feature.getName(),
												"am_" + feature.getName() + "_out");
									}

									guarantee = "guarantee " + id + " " + desc + " : " + expr + ";";

									guarantees.add(guarantee);
								}
							}
							break;
						}
					}

					for (String guarantee : guarantees) {
						agreeClauses = agreeClauses + "\t\t\t" + guarantee + System.lineSeparator();
					}

					if (!attestationAgreeProperty.isEmpty()) {
						agreeClauses = agreeClauses + "\t\t\t" + attestationAgreeProperty + System.lineSeparator();
//						for (String clause : filterAgreeProperty.split(System.lineSeparator())) {
//							agreeClauses = agreeClauses + "\t\t\t" + clause + System.lineSeparator();
//						}
					}

					agreeClauses = agreeClauses + "\t\t**}";

					final DefaultAnnexSubclause annexSubclauseImpl = attestationManagerThreadType
							.createOwnedAnnexSubclause();
					annexSubclauseImpl.setName("agree");
					annexSubclauseImpl.setSourceText(agreeClauses);
				}

				// Add AGREE helper constants/functions to package annex library
				String annexLibraryText = "";
				for (AnnexLibrary annexLibrary : pkgSection.getOwnedAnnexLibraries()) {
					if (annexLibrary.getName().equalsIgnoreCase("agree")) {
						DefaultAnnexLibrary defaultAnnexLibrary = (DefaultAnnexLibrary) annexLibrary;
//						defaultAnnexLibrary.setSourceText(defaultAnnexLibrary.getSourceText()
//								.replace("{**" + System.lineSeparator(), annexLibraryText));
						annexLibraryText = defaultAnnexLibrary.getSourceText();
//						agreeAnnexLibraryFound = true;
						break;
					}
				}
				if (annexLibraryText.isEmpty()) {
					annexLibraryText = "{**" + System.lineSeparator();
				} else {
					annexLibraryText = annexLibraryText.replace("**}", "").trim() + System.lineSeparator()
							+ System.lineSeparator();
				}
				// Cache size
				annexLibraryText = annexLibraryText + "\t\t-- Size of the Attestation Manager cache as specified by the CASE_Properties::CACHE_SIZE property" + System.lineSeparator();
				annexLibraryText = annexLibraryText + "\t\tconst AM_CACHE_SIZE : int = " + cacheSize + ";" + System.lineSeparator() + System.lineSeparator();
				// TRUSTED function
				annexLibraryText = annexLibraryText + "\t\t-- This function checks whether a message sender has passed attestation" + System.lineSeparator();
				annexLibraryText = annexLibraryText + "\t\tfun TRUSTED_MESSAGE(header : CASE_MsgHeader.Impl) : bool = "
						+ CASE_MODEL_TRANSFORMATIONS_NAME + ".NULL_MESSAGE(header) or "
						+ CASE_MODEL_TRANSFORMATIONS_NAME + ".TRUSTED(header.src);" + System.lineSeparator()
						+ System.lineSeparator();
				annexLibraryText = annexLibraryText + "\t**}";

				// TODO: Figure out how to write this to the package section annex library

			}
		});

	}


	/**
	 * Collects Resolute prove() clauses from each subcomponent in the implementation
	 * @param ci - Implementation
	 * @return List<String> - List of Resolute prove clauses
	 */
	private List<String> getResoluteClauses(String selectedSubcomponent, ComponentImplementation ci) {

		XtextEditor xtextEditor = EditorUtils.getActiveXtextEditor();

		return xtextEditor.getDocument().readOnly(resource -> {

			List<String> resoluteClauses = new ArrayList<>();

			for (Subcomponent comp : ci.getOwnedSubcomponents()) {

				// TODO: Only get the prove statements from components connected to the comm driver

//				if (!selectedSubcomponent.isEmpty() && !comp.getName().equalsIgnoreCase(selectedSubcomponent)) {
//					continue;
//				}

				ComponentType compType = comp.getComponentType();
				final EList<AnnexSubclause> annexSubclauses = compType.getOwnedAnnexSubclauses();

				for (AnnexSubclause annexSubclause : annexSubclauses) {
					// See if there's a resolute annex
					if (annexSubclause.getName().equalsIgnoreCase("resolute")) {
						DefaultAnnexSubclause annexSubclauseImpl = (DefaultAnnexSubclause) annexSubclause;
						// See if there are any 'prove' clauses
						ResoluteSubclause resoluteClause = (ResoluteSubclause) annexSubclauseImpl
								.getParsedAnnexSubclause();
						EList<ProveStatement> proves = resoluteClause.getProves();
						for (ProveStatement prove : proves) {
							Expr expr = prove.getExpr();
							if (expr instanceof FnCallExpr) {
								FnCallExpr fnCall = (FnCallExpr) expr;
								if (fnCall.getFn().getName() != null
										&& !resoluteClauses.contains(fnCall.getFn().getName())) {
									resoluteClauses.add(fnCall.getFn().getName());
								}
							}
						}
						break;
					}
				}
//				if (comp.getName().equalsIgnoreCase(selectedSubcomponent)) {
//					break;
//				}
			}

			return resoluteClauses;
		});
	}

}
