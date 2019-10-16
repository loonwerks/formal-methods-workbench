package com.collins.fmw.cyres.architecture.handlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.PlatformUI;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.utils.EditorUtils;
import org.osate.aadl2.Aadl2Package;
import org.osate.aadl2.AadlPackage;
import org.osate.aadl2.ComponentCategory;
import org.osate.aadl2.ComponentImplementation;
import org.osate.aadl2.ContainedNamedElement;
import org.osate.aadl2.ContainmentPathElement;
import org.osate.aadl2.ListValue;
import org.osate.aadl2.ModalPropertyValue;
import org.osate.aadl2.PackageSection;
import org.osate.aadl2.PrivatePackageSection;
import org.osate.aadl2.ProcessSubcomponent;
import org.osate.aadl2.ProcessorSubcomponent;
import org.osate.aadl2.Property;
import org.osate.aadl2.PropertyAssociation;
import org.osate.aadl2.PropertyExpression;
import org.osate.aadl2.PublicPackageSection;
import org.osate.aadl2.Realization;
import org.osate.aadl2.ReferenceValue;
import org.osate.aadl2.Subcomponent;
import org.osate.aadl2.ThreadGroupSubcomponent;
import org.osate.aadl2.ThreadSubcomponent;
import org.osate.aadl2.VirtualProcessorImplementation;
import org.osate.aadl2.VirtualProcessorSubcomponent;
import org.osate.aadl2.VirtualProcessorType;
import org.osate.ui.dialogs.Dialog;
import org.osate.xtext.aadl2.properties.util.DeploymentProperties;
import org.osate.xtext.aadl2.properties.util.GetProperties;

import com.collins.fmw.cyres.architecture.dialogs.AddIsolatorDialog;
import com.collins.fmw.cyres.architecture.requirements.AddIsolatorClaim;
import com.collins.fmw.cyres.architecture.requirements.CyberRequirement;
import com.collins.fmw.cyres.architecture.requirements.RequirementsManager;
import com.collins.fmw.cyres.architecture.utils.CaseUtils;
import com.collins.fmw.cyres.architecture.utils.ComponentCreateHelper;

public class AddIsolatorHandler extends AadlHandler {

	static final String VIRTUAL_PROCESSOR_TYPE_NAME = "CASE_Virtual_Processor";
	public static final String VIRTUAL_PROCESSOR_IMPL_NAME = "VPROC";
	static final String CONNECTION_IMPL_NAME = "c";

	private String virtualProcessorName;
	private String virtualMachineOS;
	private List<String> isolatedComponents;
	private String isolatorRequirement;

	@Override
	protected void runCommand(URI uri) {

		// ASSUMPTIONS:
		// Selected subcomponent or it's subcomponents are bound to a processor
		// Selected subcomponent is in same component implementation as processor it is bound to

		// Get the current selection
		EObject eObj = getEObject(uri);
		Subcomponent sub = null;
		// TODO: handle system, device, abstract subcomponents
		if (eObj instanceof ProcessSubcomponent || eObj instanceof ThreadSubcomponent
				|| eObj instanceof ThreadGroupSubcomponent) {
			sub = (Subcomponent) eObj;
		} else {
			Dialog.showError("Add Isolator",
					"A process, thread, or thread group implementation subcomponent must be selected in order to add isolation.");
			return;
		}

		// Make sure subcomponent refers to a component implementation
		if (sub.getComponentImplementation() == null) {
			Dialog.showError("Add Isolator", "Selected subcomponent must be a component implementation.");
			return;
		}

		// Check if this subcomponent is bound to a processor
		// ASSUMPTION: processor binding will be specified for selected subcomponent, or its subcomponents
		Map<String, Set<String>> processorBindings = getProcessorBindings(sub);
		// TODO: Remove this restriction and present user with list of processors and virtual processors to choose from
		// TODO: May need to look at Available_Processor_Bindings property to do this
		if (processorBindings.isEmpty()) {
			Dialog.showError("Add Isolator",
					"The selected component (or at least one of its subcomponents) must be bound to a processor or virtual processor.");
			return;
		}

		// Open wizard to enter filter info
		final AddIsolatorDialog wizard = new AddIsolatorDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());

		List<String> requirements = new ArrayList<>();
		RequirementsManager.getInstance().getImportedRequirements().forEach(r -> requirements.add(r.getId()));
		wizard.setSelectedComponent(sub);
		wizard.setRequirements(requirements);

		wizard.create();
		if (wizard.open() == Window.OK) {
			virtualProcessorName = wizard.getVirtualProcessorName();
			virtualMachineOS = wizard.getVirtualMachineOS();
			if (virtualProcessorName == "") {
				virtualProcessorName = VIRTUAL_PROCESSOR_IMPL_NAME;
			}
			isolatedComponents = wizard.getIsolatedComponents();
			isolatorRequirement = wizard.getRequirement();
		} else {
			return;
		}

		// create set of bound processors
		Set<String> boundProcessors = new HashSet<>();
		for (Map.Entry<String, Set<String>> entry : processorBindings.entrySet()) {
			if (isolatedComponents.contains(entry.getKey())) {
				boundProcessors.addAll(entry.getValue());
			}
		}
		// If specific subcomponents of the selected subcomponent aren't bound to a processor,
		// but the selected subcomponent is, then the selected subcomponent's subcomponents will use
		// the binding of their parent
		if (boundProcessors.isEmpty() && !isolatedComponents.contains(sub.getName())
				&& processorBindings.get(sub.getName()) != null) {
			boundProcessors.addAll(processorBindings.get(sub.getName()));
		}
		if (boundProcessors.isEmpty()) {
			Dialog.showError("Add Isolator",
					"The selected subcomponent(s) must already be bound to a processor in order to be isolated.");
			return;
		}

		// Insert the virtual processor type and implementation components
		// into the same package as the selected subcomponent's containing implementation.
		// Note that this could be a different package than the bound processor(s).
		insertVirtualProcessor(EcoreUtil.getURI(sub), boundProcessors);

		// ASSUMPTION: The selected component or subcomponents are bound to the same processors
		// (in other words, processor bindings are the same for all selected components)

		return;

	}

	/**
	 * Inserts the virtual processor type and implementation components
	 * into the same package as the selected subcomponent.
	 * @param selectedComponentURI - URI of the selected subcomponent
	 */
	private void insertVirtualProcessor(URI selectedComponentURI, Set<String> boundProcessors) {

		// Get the active xtext editor so we can make modifications
		final XtextEditor editor = EditorUtils.getActiveXtextEditor();
		AddIsolatorClaim claim = null;

		if (editor != null) {
			claim = editor.getDocument().modify(resource -> {

				final Subcomponent selectedSub = (Subcomponent) resource.getEObject(selectedComponentURI.fragment());
				final ComponentImplementation containingImpl = selectedSub.getContainingComponentImpl();
				final AadlPackage aadlPkg = (AadlPackage) resource.getContents().get(0);
				PackageSection pkgSection = null;
				// Figure out if the selected subcomponent is in the public or private section
				EObject eObj = selectedSub.eContainer();
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
					Dialog.showError("Add Isolator", "No public or private package sections found.");
					return null;
				}

				// Import CASE_Properties file
				if (!CaseUtils.addCasePropertyImport(pkgSection)) {
					return null;
				}
				// Import CASE_Model_Transformations file
				if (!CaseUtils.addCaseModelTransformationsImport(pkgSection, true)) {
					return null;
				}

				// Create virtual processor component type
				VirtualProcessorType vpType = (VirtualProcessorType) pkgSection
						.createOwnedClassifier(Aadl2Package.eINSTANCE.getVirtualProcessorType());
				// Give it a unique name
				vpType.setName(getUniqueName(VIRTUAL_PROCESSOR_TYPE_NAME, true, pkgSection.getOwnedClassifiers()));

				// Put in the right place in the package (before the selected subcomponent's containing implementation)
				pkgSection.getOwnedClassifiers().move(
						getIndex(selectedSub.getContainingComponentImpl().getTypeName(),
								pkgSection.getOwnedClassifiers()),
						pkgSection.getOwnedClassifiers().size() - 1);

				// CASE::COMP_TYPE Property
				if (!CaseUtils.addCasePropertyAssociation("COMP_TYPE", "ISOLATOR", vpType)) {
//					return;
				}

				// Create virtual processor component implementation
				VirtualProcessorImplementation vpImpl = (VirtualProcessorImplementation) pkgSection
						.createOwnedClassifier(Aadl2Package.eINSTANCE.getVirtualProcessorImplementation());
				vpImpl.setName(vpType.getName() + ".Impl");
				final Realization rVpImpl = vpImpl.createOwnedRealization();
				rVpImpl.setImplemented(vpType);
				// Put in the right place in the package (after the virtual processor component type)
				pkgSection.getOwnedClassifiers().move(getIndex(vpType.getName(), pkgSection.getOwnedClassifiers()) + 1,
						pkgSection.getOwnedClassifiers().size() - 1);

				// CASE::OS Property
				if (!virtualMachineOS.isEmpty()) {
					if (!CaseUtils.addCasePropertyAssociation("OS", virtualMachineOS, vpImpl)) {
//						return;
					}
				}

				// Create virtual processor subcomponent
				final VirtualProcessorSubcomponent vpSub = (VirtualProcessorSubcomponent) ComponentCreateHelper
						.createOwnedSubcomponent(containingImpl,
						ComponentCategory.VIRTUAL_PROCESSOR);

				// Give it a unique name
				vpSub.setName(getUniqueName(virtualProcessorName, true, containingImpl.getOwnedSubcomponents()));

				// Set subcomponent type
				vpSub.setVirtualProcessorSubcomponentType(vpImpl);

				// Bind the virtual processor to the processor(s)
				// If a binding to a processor already exists, add virtual processor to Applies To
				// Also remove isolated components from this processor binding
				boolean propertyAssociationFound = false;
				for (PropertyAssociation pa : containingImpl.getOwnedPropertyAssociations()) {
					// Find bindings to processors (there could be multiple)
					for (ModalPropertyValue val : pa.getOwnedValues()) {
						ListValue listVal = (ListValue) val.getOwnedValue();
						for (PropertyExpression prop : listVal.getOwnedListElements()) {
							if (prop instanceof ReferenceValue) {
								ReferenceValue refVal = (ReferenceValue) prop;
								// Check if the referenced processor was bound to an isolated component
								if (boundProcessors.contains(refVal.getPath().getNamedElement().getName())) {

									// Add virtual processor subcomponent to Applies To
									ContainedNamedElement cne = pa.createAppliesTo();
									ContainmentPathElement cpe = cne.createPath();
									cpe.setNamedElement(vpSub);

									// Remove isolated components from this binding
									Iterator<ContainedNamedElement> i = pa.getAppliesTos().iterator();
									while (i.hasNext()) {
										ContainedNamedElement containedNamedElement = i.next();
										ContainmentPathElement containmentPathElement = containedNamedElement.getPath();
										String appliesTo = containmentPathElement.getNamedElement().getQualifiedName();
										// If an entire subcomponent (including its subcomponents) is selected, remove
										// any of its subcomponent bindings to this processor
										if (isolatedComponents.contains(appliesTo)) {
											i.remove();
										} else if (containmentPathElement.getPath() != null) {
											containmentPathElement = containmentPathElement.getPath();
											appliesTo += "." + containmentPathElement.getNamedElement().getName();
											if (isolatedComponents.contains(appliesTo)) {
												i.remove();
											}
										}
									}

									propertyAssociationFound = true;
								}
							}
						}
					}
				}

				// If a binding to any processor doesn't exist, create one and add virtual processor
				if (!propertyAssociationFound) {
					PropertyAssociation pa = containingImpl.createOwnedPropertyAssociation();
					Property prop = GetProperties.lookupPropertyDefinition(containingImpl, DeploymentProperties._NAME,
							DeploymentProperties.ACTUAL_PROCESSOR_BINDING);
					pa.setProperty(prop);
					ModalPropertyValue mpv = pa.createOwnedValue();
					ListValue lv = (ListValue) mpv.createOwnedValue(Aadl2Package.eINSTANCE.getListValue());
					for (String s : boundProcessors) {
						for (Subcomponent sub : containingImpl.getOwnedSubcomponents()) {
							if (sub.getName().equalsIgnoreCase(s)) {
								ReferenceValue rv = (ReferenceValue) lv
										.createOwnedListElement(Aadl2Package.eINSTANCE.getReferenceValue());
								ContainmentPathElement cpe = rv.createPath();
								cpe.setNamedElement(sub);
								break;
							}
						}
					}
					ContainedNamedElement cne = pa.createAppliesTo();
					ContainmentPathElement cpe = cne.createPath();
					cpe.setNamedElement(vpSub);
				}

				// Bind the selected subcomponent(s) to the virtual processor
				PropertyAssociation pa = containingImpl.createOwnedPropertyAssociation();
				Property prop = GetProperties.lookupPropertyDefinition(containingImpl, DeploymentProperties._NAME,
						DeploymentProperties.ACTUAL_PROCESSOR_BINDING);
				pa.setProperty(prop);
				ModalPropertyValue mpv = pa.createOwnedValue();
				ListValue lv = (ListValue) mpv.createOwnedValue(Aadl2Package.eINSTANCE.getListValue());
				ReferenceValue rv = (ReferenceValue) lv
						.createOwnedListElement(Aadl2Package.eINSTANCE.getReferenceValue());
				ContainmentPathElement cpe = rv.createPath();
				cpe.setNamedElement(vpSub);
				for (String s : isolatedComponents) {
					ContainedNamedElement cne = pa.createAppliesTo();
					cpe = cne.createPath();
					cpe.setNamedElement(selectedSub);
					if (!s.equalsIgnoreCase(selectedSub.getQualifiedName())) {
						for (Subcomponent sub : selectedSub.getComponentImplementation().getOwnedSubcomponents()) {
							if (s.equalsIgnoreCase(selectedSub.getQualifiedName() + "." + sub.getName())) {
								cpe = cpe.createPath();
								cpe.setNamedElement(sub);
								break;
							}
						}
					}
				}

				// Add add_isolator claims to resolute prove statement, if applicable
				if (!isolatorRequirement.isEmpty()) {
					CyberRequirement req = RequirementsManager.getInstance().getRequirement(isolatorRequirement);
//					return new AddIsolatorClaim(req.getContext(), isolatedComponents, vpSub);
					return new AddIsolatorClaim(isolatedComponents, vpSub);
				}

				return null;
			});
		}

		// Add add_isolator claims to resolute prove statement, if applicable
		if (claim != null) {
			RequirementsManager.getInstance().modifyRequirement(isolatorRequirement, claim);
		}

	}


	// Maps a subcomponent to the processor(s) it is bound to
	private Map<String, Set<String>> getProcessorBindings(Subcomponent sub) {

		Map<String, Set<String>> processorMap = new HashMap<>();

		// Get selected subcomponent + child subcomponents
		Set<String> subcomponents = new HashSet<>();
		subcomponents.add(sub.getName());
		sub.getComponentImplementation().getOwnedSubcomponents()
				.forEach(s -> subcomponents.add(sub.getName() + "." + s.getName()));

		// Get processor bindings for all components within containing component implementation of selected subcomponent
		ComponentImplementation ci = sub.getContainingComponentImpl();

		for (PropertyAssociation pa : ci.getOwnedPropertyAssociations()) {
			if (pa.getProperty().getName().equalsIgnoreCase("Actual_Processor_Binding")) {
				for (ContainedNamedElement cne : pa.getAppliesTos()) {
					ContainmentPathElement cpe = cne.getPath();
					String appliesTo = cpe.getNamedElement().getName();
					while (cpe.getPath() != null) {
						cpe = cpe.getPath();
						appliesTo += "." + cpe.getNamedElement().getName();
					}
					// Check if the property association applies to the selected subcomponent
					if (subcomponents.contains(appliesTo)) {
						for (ModalPropertyValue val : pa.getOwnedValues()) {
							ListValue listVal = (ListValue) val.getOwnedValue();
							for (PropertyExpression prop : listVal.getOwnedListElements()) {
								if (prop instanceof ReferenceValue) {
									ReferenceValue refVal = (ReferenceValue) prop;
									// Get the processor this subcomponent is bound to
									if (refVal.getPath().getNamedElement() instanceof ProcessorSubcomponent || refVal
											.getPath().getNamedElement() instanceof VirtualProcessorSubcomponent) {

										if (processorMap.get(appliesTo) == null) {
											processorMap.put(appliesTo, new HashSet<String>(
													Arrays.asList(refVal.getPath().getNamedElement().getName())));
										} else {
											processorMap.get(appliesTo)
													.add(refVal.getPath().getNamedElement().getName());
										}

									}
								}
							}
						}
						break;
					}
				}
			}
		}

		return processorMap;
	}

}
