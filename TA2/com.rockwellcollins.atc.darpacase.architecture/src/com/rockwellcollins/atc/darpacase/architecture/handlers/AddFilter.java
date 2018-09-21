package com.rockwellcollins.atc.darpacase.architecture.handlers;

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
import org.osate.aadl2.Aadl2Package;
import org.osate.aadl2.AnnexSubclause;
import org.osate.aadl2.ConnectedElement;
import org.osate.aadl2.Context;
import org.osate.aadl2.DataPort;
import org.osate.aadl2.DataSubcomponentType;
import org.osate.aadl2.EventDataPort;
import org.osate.aadl2.EventPort;
import org.osate.aadl2.ModelUnit;
import org.osate.aadl2.PackageSection;
import org.osate.aadl2.Port;
import org.osate.aadl2.PortConnection;
import org.osate.aadl2.PrivatePackageSection;
import org.osate.aadl2.PublicPackageSection;
import org.osate.aadl2.ThreadSubcomponent;
import org.osate.aadl2.ThreadType;
import org.osate.aadl2.impl.AadlPackageImpl;
import org.osate.aadl2.impl.DefaultAnnexSubclauseImpl;
import org.osate.aadl2.impl.PortImpl;
import org.osate.aadl2.impl.ProcessImplementationImpl;
import org.osate.aadl2.impl.PropertySetImpl;
import org.osate.aadl2.impl.SubcomponentImpl;
import org.osate.ui.dialogs.Dialog;

import com.rockwellcollins.atc.agree.agree.AgreeContract;
import com.rockwellcollins.atc.agree.agree.impl.AgreeContractSubclauseImpl;
import com.rockwellcollins.atc.agree.unparsing.AgreeAnnexUnparser;
import com.rockwellcollins.atc.darpacase.architecture.CaseClaimsManager;
import com.rockwellcollins.atc.darpacase.architecture.dialogs.AddFilterDialog;
import com.rockwellcollins.atc.resolute.resolute.Expr;
import com.rockwellcollins.atc.resolute.resolute.ProveStatement;
import com.rockwellcollins.atc.resolute.resolute.impl.FnCallExprImpl;
import com.rockwellcollins.atc.resolute.resolute.impl.ResoluteSubclauseImpl;

public class AddFilter extends AadlHandler {

	static final String FILTER_COMP_BASE_NAME = "Filter";
	static final String FILTER_PORT_IN_NAME = "filter_in";
	static final String FILTER_PORT_OUT_NAME = "filter_out";
	static final String FILTER_IMPL_BASE_NAME = "FLT";
	static final String CONNECTION_IMPL_BASE_NAME = "c";

	private String filterImplementationLanguage;
	private String filterRegularExpression;
	private String filterTypeName;
	private String filterImplName;
	private String filterResoluteClause;
	private String filterAgreeProperties;
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
		wizard.setGuaranteeList(getSourceName(uri), getSourceGuarantees(uri));
		wizard.setResoluteClauses(getResoluteClauses(uri));
		wizard.create();
		if (wizard.open() == Window.OK) {
			filterImplementationLanguage = wizard.getFilterImplementationLanguage();
			filterRegularExpression = wizard.getFilterRegularExpression();
			filterTypeName = wizard.getFilterTypeName();
			filterImplName = wizard.getFilterImplName();
			if (filterTypeName == "") {
				filterTypeName = FILTER_COMP_BASE_NAME;
			}
			if (filterImplName == "") {
				filterImplName = FILTER_IMPL_BASE_NAME;
			}
			filterResoluteClause = wizard.getResoluteClause();
			filterAgreeProperties = wizard.getAgreeProperties();
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
				final PortConnection selectedConnection = (PortConnection) resource.getEObject(uri.fragment());
				final AadlPackageImpl aadlPkg = (AadlPackageImpl) resource.getContents().get(0);
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
				PropertySetImpl casePropSet = null;
				for (ModelUnit modelUnit : importedUnits) {
					if (modelUnit instanceof PropertySetImpl) {
						if (modelUnit.getName().equals(CASE_PROPSET_NAME)) {
							casePropSet = (PropertySetImpl) modelUnit;
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
				final ThreadType filterThreadType = (ThreadType) pkgSection
						.createOwnedClassifier(Aadl2Package.eINSTANCE.getThreadType());
				// Give it a unique name
				filterThreadType.setName(getUniqueName(filterTypeName, true, pkgSection.getOwnedClassifiers()));

				// Create filter ports
				final PortImpl portImpl = (PortImpl) selectedConnection.getDestination().getConnectionEnd();
				Port portIn = null;
				Port portOut = null;
				DataSubcomponentType dataFeatureClassifier = null;
				if (portImpl instanceof EventDataPort) {
					portIn = filterThreadType.createOwnedEventDataPort();
					dataFeatureClassifier = ((EventDataPort) portImpl).getDataFeatureClassifier();
//					((EventDataPort) portIn)
//							.setDataFeatureClassifier(((EventDataPort) portImpl).getDataFeatureClassifier());
					((EventDataPort) portIn).setDataFeatureClassifier(dataFeatureClassifier);
					portOut = filterThreadType.createOwnedEventDataPort();
//					((EventDataPort) portOut)
//							.setDataFeatureClassifier(((EventDataPort) portImpl).getDataFeatureClassifier());
					((EventDataPort) portOut).setDataFeatureClassifier(dataFeatureClassifier);
				} else if (portImpl instanceof DataPort) {
					portIn = filterThreadType.createOwnedDataPort();
//					((DataPort) portIn).setDataFeatureClassifier(((DataPort) portImpl).getDataFeatureClassifier());
					((DataPort) portIn).setDataFeatureClassifier(dataFeatureClassifier);
					portOut = filterThreadType.createOwnedDataPort();
//					((DataPort) portOut).setDataFeatureClassifier(((DataPort) portImpl).getDataFeatureClassifier());
					((DataPort) portOut).setDataFeatureClassifier(dataFeatureClassifier);
				} else if (portImpl instanceof EventPort) {
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
				if (!addPropertyAssociation("COMP_SPEC", filterRegularExpression, filterThreadType, casePropSet)) {
//					return;
				}

				// Move filter to proper location
				// (just before component it connects to on communication pathway)
				final Context context = selectedConnection.getDestination().getContext();
				String destName = ((SubcomponentImpl) context).getSubcomponentType().getName();
				pkgSection.getOwnedClassifiers().move(getIndex(destName, pkgSection.getOwnedClassifiers()),
						pkgSection.getOwnedClassifiers().size() - 1);

				// Insert filter feature in process component implementation
				final ProcessImplementationImpl procImpl = (ProcessImplementationImpl) selectedConnection
						.getContainingComponentImpl();
				final ThreadSubcomponent filterThreadSubComp = procImpl.createOwnedThreadSubcomponent();

				// Give it a unique name
				filterThreadSubComp
						.setName(getUniqueName(filterImplName, true, procImpl.getOwnedSubcomponents()));
				filterThreadSubComp.setThreadSubcomponentType(filterThreadType);

				// Put it in the right place
				destName = selectedConnection.getDestination().getContext().getName();
				procImpl.getOwnedThreadSubcomponents().move(getIndex(destName, procImpl.getOwnedThreadSubcomponents()),
						procImpl.getOwnedThreadSubcomponents().size() - 1);

				// Create connection from filter to connection destination
				final PortConnection portConnOut = procImpl.createOwnedPortConnection();
				// Give it a unique name
				portConnOut
						.setName(getUniqueName(CONNECTION_IMPL_BASE_NAME, false, procImpl.getOwnedPortConnections()));
				portConnOut.setBidirectional(false);
				final ConnectedElement filterOutSrc = portConnOut.createSource();
				filterOutSrc.setContext(filterThreadSubComp);
				filterOutSrc.setConnectionEnd(portOut);
				final ConnectedElement filterOutDst = portConnOut.createDestination();
				filterOutDst.setContext(selectedConnection.getDestination().getContext());
				filterOutDst.setConnectionEnd(selectedConnection.getDestination().getConnectionEnd());

				// Put portConnOut in right place (after portConnIn)
				destName = selectedConnection.getName();
				procImpl.getOwnedPortConnections().move(getIndex(destName, procImpl.getOwnedPortConnections()) + 1,
						procImpl.getOwnedPortConnections().size() - 1);

				// Add add_filter claims to resolute prove statement, if applicable
				if (!filterResoluteClause.isEmpty()) {
					// Add arguments to prove statement in destination component
					ThreadType clauseThread = (ThreadType) selectedConnection.getDestination().getConnectionEnd()
							.getContainingClassifier();
					EList<AnnexSubclause> annexSubclauses = clauseThread.getOwnedAnnexSubclauses();
					for (AnnexSubclause annexSubclause : annexSubclauses) {
						// get the resolute clause
						if (annexSubclause.getName().equalsIgnoreCase("resolute")) {
							DefaultAnnexSubclauseImpl annexSubclauseImpl = (DefaultAnnexSubclauseImpl) annexSubclause;
							String sourceText = annexSubclauseImpl.getSourceText();
							if (sourceText.contains(filterResoluteClause + "()")) {
								// Add arguments
								sourceText = sourceText.replace(filterResoluteClause + "()",
										filterResoluteClause + "(this, " + dataFeatureClassifier.getName() + ")");
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
				if (filterAgreeProperties.length() > 0 || propagatedGuarantees.size() > 0) {
					String agreeClauses = "{**" + System.lineSeparator();

					if (!filterAgreeProperties.isEmpty()) {
						for (String clause : filterAgreeProperties.split(System.lineSeparator())) {
							agreeClauses = agreeClauses + "\t\t\t" + clause + System.lineSeparator();
						}
					}

					for (String guarantee : propagatedGuarantees) {
						agreeClauses = agreeClauses + guarantee + System.lineSeparator();
					}
					agreeClauses = agreeClauses + "\t\t**}";
					// replace source out port name with filter out port name
					agreeClauses = agreeClauses.replace(selectedConnection.getSource().getConnectionEnd().getName(),
							FILTER_PORT_OUT_NAME);
					DefaultAnnexSubclauseImpl annexSubclauseImpl = (DefaultAnnexSubclauseImpl) filterThreadType
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
					DefaultAnnexSubclauseImpl annexSubclauseImpl = (DefaultAnnexSubclauseImpl) annexSubclause;
					// See if the agree annex contains guarantee statements
					AgreeContractSubclauseImpl agreeContract = (AgreeContractSubclauseImpl) annexSubclauseImpl
							.getParsedAnnexSubclause();
					AgreeAnnexUnparser unparser = new AgreeAnnexUnparser();
					String specs = unparser.unparseContract((AgreeContract) agreeContract.getContract(), "");
					for (String line : specs.split(System.lineSeparator())) {
						if (line.trim().toLowerCase().startsWith("guarantee")) {
							guarantees.add(line);
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
					DefaultAnnexSubclauseImpl annexSubclauseImpl = (DefaultAnnexSubclauseImpl) annexSubclause;
					// See if there are any 'prove' clauses
					ResoluteSubclauseImpl resoluteClause = (ResoluteSubclauseImpl) annexSubclauseImpl.getParsedAnnexSubclause();
					EList<ProveStatement> proves = resoluteClause.getProves();
					for (ProveStatement prove : proves) {
						Expr expr = prove.getExpr();
						if (expr instanceof FnCallExprImpl) {
							FnCallExprImpl fnCall = (FnCallExprImpl) expr;
							resoluteClauses.add(fnCall.getFn().getName());
						}
					}
					break;
				}
			}

			return resoluteClauses;
		});
	}

}
