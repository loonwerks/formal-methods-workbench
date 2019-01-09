package com.collins.atc.ace.cyres.architecture.handlers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;
import org.osate.aadl2.ComponentImplementation;
import org.osate.aadl2.ComponentType;
import org.osate.aadl2.ProcessImplementation;
import org.osate.aadl2.ProcessType;
import org.osate.aadl2.PropertyExpression;
import org.osate.aadl2.ThreadImplementation;
import org.osate.aadl2.ThreadType;
import org.osate.aadl2.impl.ComponentImplementationImpl;
import org.osate.aadl2.instance.ComponentInstance;
import org.osate.aadl2.instance.SystemInstance;
import org.osate.aadl2.instance.impl.ComponentInstanceImpl;
import org.osate.aadl2.instantiation.InstantiateModel;
import org.osate.ui.dialogs.Dialog;

import com.collins.atc.ace.cyres.architecture.Activator;
import com.collins.atc.ace.cyres.architecture.preferences.CasePreferenceConstants;
import com.collins.atc.ace.cyres.architecture.tools.DockerClient;
import com.rockwellcollins.atc.agree.agree.impl.AssumeStatementImpl;
import com.rockwellcollins.atc.agree.agree.impl.GuaranteeStatementImpl;

public class VerifyLegacyImplementation extends AadlHandler {

	@Override
	public void runCommand(URI uri) {

		boolean NOT_IMPLEMENTED = true;
		if (NOT_IMPLEMENTED) {
			Dialog.showError("Functionality not yet implemented",
					"We're still working on integrating TA3 tools into our framework.  Consequently, legacy component verification cannot be performed at this time.");
			return;
		}

		// Make sure selection is either a component or component implementation
		EObject eObj = getEObject(uri);
		if (!((eObj instanceof ProcessType) || (eObj instanceof ThreadType) || (eObj instanceof ProcessImplementation)
				|| (eObj instanceof ThreadImplementation))) {
			Dialog.showError("No component is selected",
					"A component type or component implementation must be selected for verification.");
			return;
		}

		// TODO: Get name of docker image from preferences

		// TODO: Describe what I'm doing here
		Job docker = new Job("Docker Client") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {

				// Start Docker Client
				final String dockerImage = Activator.getDefault().getPreferenceStore()
						.getString(CasePreferenceConstants.CASE_BAGGAGE_SERVER_FILENAME);
				final String dockerContainerPort = Activator.getDefault().getPreferenceStore()
						.getString(CasePreferenceConstants.CASE_BAGGAGE_SERVER_PORT);
				final String dockerContainerName = Activator.getDefault().getPreferenceStore()
						.getString(CasePreferenceConstants.CASE_BAGGAGE_SERVER_NAME);
				if (dockerImage.isEmpty() || dockerContainerPort.isEmpty() || dockerContainerName.isEmpty()) {
					Dialog.showError("Baggage Server", "Baggage Server details are missing in CASE preferences.");
					return Status.CANCEL_STATUS;
				}
				DockerClient dockerClient = new DockerClient(dockerImage, dockerContainerPort, dockerContainerName,
						executionEvent);

				try {
					dockerClient.schedule();

					// Get components to verify
					Set<ComponentType> components = getLegacyComponents(eObj);

					// Make sure there is at least one legacy component to verify
					if (components.isEmpty()) {
						Dialog.showError("No legacy component in model",
								"At least one component in the model must point to an implementation source or binary file.");
						return Status.CANCEL_STATUS;
					}

					// Wait until Docker Client is started
					while (!dockerClient.isStarted() && !monitor.isCanceled()) {
						if (dockerClient.getResult() != null) {
							// Could not launch docker client for some reason
							return Status.CANCEL_STATUS;
						}
					}

					for (ComponentType component : components) {

						// TODO: Generate Manifest
						String manifest = generateManifest(component);
						if (manifest == null) {
							return Status.CANCEL_STATUS;
						}

						// TODO: Generate Contract (including hash)
						String contracts = generateContracts(component);
						if (contracts == null) {
							return Status.CANCEL_STATUS;
						}

						// TODO: Upload binary
						if (!uploadBinary(component)) {
							return Status.CANCEL_STATUS;
						}

						// TODO: Upload manifest

						// TODO: Upload contract

						// TODO: Poll for result

					}
				} finally {
					// Stop docker client when complete
					dockerClient.cancel();
				}

				return Status.OK_STATUS;
			}
		};
		docker.schedule();

		// TODO: Cancellation handler?

	}

	private Set<ComponentType> getLegacyComponents(EObject eObj) {

		Set<ComponentType> components = new HashSet<>();

		if (eObj instanceof ComponentType) {
			// Make sure it is a legacy component (i.e. an implementation exists)
			ComponentType component = (ComponentType) eObj;
			if (component.getPropertyValues("Programming_Properties", "Source_Text").size() > 0) {
				components.add((ComponentType) eObj);
			}
		} else if (eObj instanceof ComponentImplementationImpl) {
			// Generate system instance
			SystemInstance si;
			ComponentImplementation compImpl = (ComponentImplementation) eObj;
			try {
				si = InstantiateModel.buildInstanceModelFile(compImpl);
			} catch (Exception e) {
				Dialog.showError("Model Instantiation", "Error while instantiating the model: " + e.getMessage());
				return components;
			}
			// Iterate through the components in the implementation
			// and add the legacy components to the set (duplicates will be ignored)
			EList<ComponentInstance> compInstances = si.getAllComponentInstances();
			for (ComponentInstance compInstance : compInstances) {
				// Get component type from instance
				ComponentInstanceImpl compInstanceImpl = (ComponentInstanceImpl) compInstance;
				ComponentType component = (ComponentType) compInstanceImpl.getComponentClassifier();
				// Check the "Source_Text" property value, which is the name of the binary or source file
				// This is how we know this component is a legacy component
				if (component.getPropertyValues("Programming_Properties", "Source_Text").size() > 0) {
					components.add(component);
				}
			}
		}

		return components;
	}

	private String generateManifest(ComponentType component) {
		StringBuilder manifest = new StringBuilder();

		manifest.append("{ \"functions\": [ { \"name\": ");

		return manifest.toString();
	}

	private String generateContracts(ComponentType component) {

		StringBuilder contract = new StringBuilder();
		String predicate = null;

		// Get entrypoint function name
		EList<PropertyExpression> funcNames = component.getPropertyValues("Programming_Properties",
				"Compute_Entrypoint");
		if (funcNames.isEmpty()) {
			Dialog.showError("No entrypoint function specified",
					"No entrypoint function is specified for " + component.getName() + ".");
			return null;
		}
		String funcName = funcNames.get(0).toString();
		if (funcName.isEmpty()) {
			Dialog.showError("No entrypoint function specified",
					"No entrypoint function is specified for " + component.getName() + ".");
			return null;
		}

		// TODO: Get AGREE Assume statements specified for this component
		contract.append("{ \"assume\": [ ");
		List<AssumeStatementImpl> assumeStatements = EcoreUtil2.getAllContentsOfType(component,
				AssumeStatementImpl.class);
		for (AssumeStatementImpl assumeStatement : assumeStatements) {
//			predicate = buildPredicate();
//			contract.append("{ \"name\": \"").append(assumeStatement.getStr()).append("\",").append(System.lineSeparator());
			contract.append("{  \"scope\": \"").append(funcName).append("\",").append(System.lineSeparator());
			contract.append("  \"predicate\": \"").append(predicate).append("\"").append(System.lineSeparator());
			contract.append("}");
			// TODO: Add comma (except to last one?
			contract.append(System.lineSeparator());
		}
		contract.append(" ],").append(System.lineSeparator());

		// TODO: Get AGREE Guarantee statements specified for this component
		contract.append("  \"guarantee\": [ ");
		List<GuaranteeStatementImpl> guaranteeStatements = EcoreUtil2.getAllContentsOfType(component,
				GuaranteeStatementImpl.class);
		for (GuaranteeStatementImpl guaranteeStatement : guaranteeStatements) {
//			predicate = buildPredicate();
			contract.append("{ \"name\": \"").append(guaranteeStatement.getStr()).append("\",")
					.append(System.lineSeparator());
			contract.append("{  \"scope\": \"").append(funcName).append("\",").append(System.lineSeparator());
			contract.append("  \"predicate\": \"").append(predicate).append("\"").append(System.lineSeparator());
			contract.append("}");
			// TODO: Add comma (except to last one?
			contract.append(System.lineSeparator());
		}
		contract.append(" ]").append(System.lineSeparator()).append("}").append(System.lineSeparator());

		return contract.toString();
	}

	private String buildPredicate(String expression) {
		String predicate = null;
		return predicate;
	}

	private boolean uploadBinary(ComponentType component) {

		// TODO: Get binary path/name
		EList<PropertyExpression> binaries = component.getPropertyValues("Programming_Properties", "Source_Text");
		if (binaries.isEmpty()) {
			Dialog.showError("No binary specified", "No binary is specified for " + component.getName() + ".");
			return false;
		}

		// TODO: Upload

		return true;
	}


}
