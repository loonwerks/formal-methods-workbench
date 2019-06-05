package com.collins.fmw.cyres.architecture.handlers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
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
import org.osate.aadl2.ComponentCategory;
import org.osate.aadl2.ComponentImplementation;
import org.osate.aadl2.ContainedNamedElement;
import org.osate.aadl2.ImplementationExtension;
import org.osate.aadl2.ListValue;
import org.osate.aadl2.ModalPropertyValue;
import org.osate.aadl2.PackageSection;
import org.osate.aadl2.PortCategory;
import org.osate.aadl2.PrivatePackageSection;
import org.osate.aadl2.ProcessSubcomponent;
import org.osate.aadl2.ProcessorSubcomponent;
import org.osate.aadl2.PropertyAssociation;
import org.osate.aadl2.PropertyExpression;
import org.osate.aadl2.PublicPackageSection;
import org.osate.aadl2.Realization;
import org.osate.aadl2.ReferenceValue;
import org.osate.aadl2.Subcomponent;
import org.osate.aadl2.ThreadSubcomponent;
import org.osate.aadl2.VirtualProcessorImplementation;
import org.osate.aadl2.VirtualProcessorSubcomponent;
import org.osate.aadl2.VirtualProcessorType;
import org.osate.ui.dialogs.Dialog;

import com.collins.fmw.cyres.architecture.dialogs.AddIsolatorDialog;
import com.collins.fmw.cyres.architecture.requirements.RequirementsManager;
import com.collins.fmw.cyres.architecture.utils.CaseUtils;
import com.collins.fmw.cyres.architecture.utils.ComponentCreateHelper;
import com.collins.fmw.cyres.architecture.utils.ModifyUtils;
import com.collins.fmw.cyres.util.plugin.Filesystem;

public class AddIsolatorHandler extends AadlHandler {

	static final String ISOLATOR_COMP_TYPE_NAME = "CASE_Isolator";
	static final String ISOLATOR_PORT_IN_NAME = "isolator_in";
	static final String ISOLATOR_PORT_OUT_NAME = "isolator_out";
	static final String ISOLATOR_LOG_PORT_NAME = "message_log";
	static final String ISOLATOR_IMPL_NAME = "VM";
	static final String VIRTUAL_PROCESSOR_TYPE_NAME = "CASE_Virtual_Processor";
	static final String VIRTUAL_PROCESSOR_IMPL_NAME = "VPROC";
	static final String CONNECTION_IMPL_NAME = "c";

	private String isolatorImplementationName;
	private String virtualProcessorName;
	private List<String> isolatedComponents;
	private PortCategory logPortType;
	private String isolatorRequirement;
	private String isolatorAgreeProperty;

	@Override
	protected void runCommand(URI uri) {

		// Get the current selection
		EObject eObj = getEObject(uri);
		Subcomponent sub = null;
		// TODO: handle thread group, system, device, abstract subcomponents
		if (eObj instanceof ProcessSubcomponent || eObj instanceof ThreadSubcomponent) {
			sub = (Subcomponent) eObj;
		} else {
			Dialog.showError("Add Isolator",
					"A process or thread implementation subcomponent must be selected in order to add isolation.");
			return;
		}

		// Make sure subcomponent refers to a component implementation
		if (sub.getComponentImplementation() == null) {
			Dialog.showError("Add Isolator", "Selected subcomponent must be a component implementation.");
			return;
		}

		// Check if this subcomponent (or its parent if subcomponent is a thread or thread group)
		// is bound to a processor
		Subcomponent processor = getBoundProcessor(sub);
		if (processor == null) {
			// TODO: Remove this restriction and present user with list of processors and virtual processors to choose from
			// TODO: May need to look at Available_Processor_Bindings property to do this
			Dialog.showError("Add Isolator",
					"The selected subcomponent must be bound to a processor or virtual processor.");
			return;
		} else if (processor.getComponentImplementation() == null) {
			Dialog.showError("Add Isolator",
					"The selected subcomponent must be bound to a processor or virtual processor implementation.");
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
			isolatorImplementationName = wizard.getIsolatorImplementationName();
			if (isolatorImplementationName == "") {
				isolatorImplementationName = ISOLATOR_IMPL_NAME;
			}
			virtualProcessorName = wizard.getVirtualProcessorName();
			if (virtualProcessorName == "") {
				virtualProcessorName = VIRTUAL_PROCESSOR_IMPL_NAME;
			}
			isolatedComponents = wizard.getIsolatedComponents();
			logPortType = wizard.getLogPortType();
			isolatorRequirement = wizard.getRequirement();
			isolatorAgreeProperty = wizard.getAgreeProperty();
		} else {
			return;
		}

		// Insert the virtual processor type and implementation components
		// into the same package as the processor.
		// Note that this could be a different package than the selected subcomponent
		insertVirtualProcessor(EcoreUtil.getURI(processor));

		// Insert the isolator component
//		insertIsolatorComponent(uri);

		return;

	}

	/**
	 * Inserts the virtual processor type and implementation components
	 * into the same package as the specified processor.
	 * @param processorURI - URI of the processor component implementation subcomponent to put the virtual processor in
	 */
	private void insertVirtualProcessor(URI processorURI) {

		// Get the file to insert into
		IFile file = Filesystem.getFile(processorURI);
		XtextEditor editor = ModifyUtils.getEditor(file);

		if (editor != null) {
			editor.getDocument().modify(resource -> {

				final Subcomponent processorSub = (Subcomponent) resource.getEObject(processorURI.fragment());
				final ComponentImplementation processorImpl = processorSub.getComponentImplementation();
				final AadlPackage aadlPkg = (AadlPackage) resource.getContents().get(0);
				PackageSection pkgSection = null;
				// Figure out if the processor is in the public or private section
				EObject eObj = processorImpl.eContainer();
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

				// Create virtual processor component implementation
				VirtualProcessorImplementation vpImpl = (VirtualProcessorImplementation) pkgSection
						.createOwnedClassifier(Aadl2Package.eINSTANCE.getVirtualProcessorImplementation());
				vpImpl.setName(vpType.getName() + ".Impl");
				final Realization r = vpImpl.createOwnedRealization();
				r.setImplemented(vpType);

				// Extend the specified processor implementation
				ComponentImplementation procExImpl = (ComponentImplementation) pkgSection
						.createOwnedClassifier(ComponentCreateHelper.getImplClass(processorImpl.getCategory()));
				ImplementationExtension implEx = procExImpl.createOwnedExtension();
				implEx.setExtended(processorImpl);

				// Create the virtual processor subcomponent of the specified processor
				VirtualProcessorSubcomponent vpSub = (VirtualProcessorSubcomponent) ComponentCreateHelper
						.createOwnedSubcomponent(procExImpl, ComponentCategory.VIRTUAL_PROCESSOR);

				// Replace the original processor implementation subcomponent with the extended one
				// containing the virtual processor
				ComponentCreateHelper.setSubcomponentType(processorSub, procExImpl);

				return null;
			});
		}

		// Close editor, if necessary
		ModifyUtils.closeEditor(editor, true);

	}

	private void insertIsolatorComponent(URI uri) {


		// Get the active xtext editor so we can make modifications
		final XtextEditor xtextEditor = EditorUtils.getActiveXtextEditor();

		xtextEditor.getDocument().modify(new IUnitOfWork.Void<XtextResource>() {

			@Override
			public void process(final XtextResource resource) throws Exception {

				// Retrieve the model object to modify
				Subcomponent selectedComponent = (Subcomponent) resource.getEObject(uri.fragment());
				final AadlPackage aadlPkg = (AadlPackage) resource.getContents().get(0);
				PackageSection pkgSection = null;
				// Figure out if the selected connection is in the public or private section
				EObject eObj = selectedComponent.eContainer();
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

				// TODO: Create virtual processor type

				// TODO: Create virtual processor implementation

				// TODO: Create virtual processor subcomponent


			}
		});

	}

	/**
	 * Returns the subcomponent that the specified subcomponent is bound to.
	 * Will return null if specified subcomponent is not bound to a processor or virtual processor.
	 * @param sub - Subcomponent
	 * @return - ProcessSubcomponent or VirtualProcessSubcomponent
	 */
	private Subcomponent getBoundProcessor(Subcomponent sub) {
		ComponentImplementation ci = sub.getContainingComponentImpl();
		for (PropertyAssociation pa : ci.getOwnedPropertyAssociations()) {
			if (pa.getProperty().getName().equalsIgnoreCase("Actual_Processor_Binding")) {
				for (ContainedNamedElement cne : pa.getAppliesTos()) {
					// Check if the property association applies to the selected subcomponent
					if (cne.getPath().getNamedElement().getName().equalsIgnoreCase(sub.getName())) {
						for (ModalPropertyValue val : pa.getOwnedValues()) {
							ListValue listVal = (ListValue) val.getOwnedValue();
							for (PropertyExpression prop : listVal.getOwnedListElements()) {
								if (prop instanceof ReferenceValue) {
									ReferenceValue refVal = (ReferenceValue) prop;
									// Get the processor this subcomponent is bound to
									if (refVal.getPath().getNamedElement() instanceof ProcessorSubcomponent || refVal
											.getPath().getNamedElement() instanceof VirtualProcessorSubcomponent) {
										return (Subcomponent) refVal.getPath().getNamedElement();
									}
									break;
								}
							}
						}
						break;
					}
				}
			}
		}
		return null;
	}

}
