package com.collins.fmw.cyres.architecture.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.osate.aadl2.ComponentCategory;
import org.osate.aadl2.Subcomponent;
import org.osate.ui.dialogs.Dialog;

import com.collins.fmw.cyres.architecture.handlers.AddIsolatorHandler;

public class AddIsolatorDialog extends TitleAreaDialog {

	private Text txtVirtualProcessorName;
	private Text txtVirtualMachineOS;
	private List<Button> btnCompSelectionType = new ArrayList<>();
	private List<Button> btnComponents = new ArrayList<>();
	private Combo cboIsolatorRequirement;

	private String virtualProcessorName = "";
	private String virtualMachineOS = "";
	private List<String> components = new ArrayList<>();
	private String isolatorRequirement = "";

	private Subcomponent component = null;
	private List<String> requirements = new ArrayList<>();

	private static final String DEFAULT_OS = "Linux";
	private static final String NO_REQUIREMENT_SELECTED = "<No requirement selected>";

	public AddIsolatorDialog(Shell parentShell) {
		super(parentShell);
		setHelpAvailable(false);
	}

	@Override
	public void create() {
		super.create();
		setTitle("Add Isolator");
		setMessage(
				"Enter Isolator details.  You may optionally leave these fields empty and manually edit the AADL isolator component once it is added to the model.",
				IMessageProvider.NONE);
	}

	@Override
	protected Point getInitialSize() {
		final Point size = super.getInitialSize();
		size.y += convertHeightInCharsToPixels(1);
		return size;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		GridLayout layout = new GridLayout(2, false);
		container.setLayout(layout);

		// Add filter information fields
		createVirtualProcessorNameField(container);
		createOSField(container);
		createComponentSelectionField(container);
		createRequirementField(container);

		return area;
	}


	/**
	 * Creates the input text field for specifying the virtual processor name
	 * @param container
	 */
	private void createVirtualProcessorNameField(Composite container) {
		Label lblVirtualProcessorNameField = new Label(container, SWT.NONE);
		lblVirtualProcessorNameField.setText("Virtual Processor implementation name");

		GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = SWT.FILL;
		txtVirtualProcessorName = new Text(container, SWT.BORDER);
		txtVirtualProcessorName.setLayoutData(dataInfoField);
		txtVirtualProcessorName.setText(AddIsolatorHandler.VIRTUAL_PROCESSOR_IMPL_NAME);
	}

	/**
	 * Creates the input text field for specifying the OS on the VM
	 * @param container
	 */
	private void createOSField(Composite container) {
		Label lblOSField = new Label(container, SWT.NONE);
		lblOSField.setText("Virtual machine OS");

		GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = SWT.FILL;
		txtVirtualMachineOS = new Text(container, SWT.BORDER);
		txtVirtualMachineOS.setLayoutData(dataInfoField);
		txtVirtualMachineOS.setText(DEFAULT_OS);
	}

	/**
	 * Creates the field for specifying the components to isolate
	 * @param container
	 */
	private void createComponentSelectionField(Composite container) {

		btnCompSelectionType.clear();
		btnComponents.clear();

		// Don't display this field if
		// Component is contains one or no subcomponents OR component is a thread
		if (this.component.getComponentImplementation().getOwnedSubcomponents() == null
				|| this.component.getComponentImplementation().getOwnedSubcomponents().size() <= 1
				|| this.component.getComponentType().getCategory() == ComponentCategory.THREAD) {
			return;
		}

		Label lblComponentSelectionField = new Label(container, SWT.NONE);
		lblComponentSelectionField.setText("Components to isolate");
		lblComponentSelectionField.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		// Create a container to hold options and checkboxes
		GridData selectionFieldLayoutData = new GridData(SWT.FILL, SWT.FILL, true, false);
		Composite selectionField = new Composite(container, SWT.BORDER);
		GridLayout layout = new GridLayout(1, true);
		selectionField.setLayout(layout);
		selectionField.setLayoutData(selectionFieldLayoutData);

		// Create a group to contain selection options
		Composite selectionGroup = new Composite(selectionField, SWT.NONE);
		selectionGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		selectionGroup.setLayout(new GridLayout(1, true));

		Button option = new Button(selectionGroup, SWT.RADIO);
		option.setText(this.component.getName() + " and its subcomponents");
		option.setSelection(true);
		option.addListener(SWT.Selection, e -> {
			if (e.type == SWT.Selection) {
				enableComponentSelectionFields(false);
			}
		});
		btnCompSelectionType.add(option);

		option = new Button(selectionGroup, SWT.RADIO);
		option.setText("Selected subcomponents of " + this.component.getName());
		option.setSelection(false);
		option.addListener(SWT.Selection, e -> {
			if (e.type == SWT.Selection) {
				enableComponentSelectionFields(true);
			}
		});
		btnCompSelectionType.add(option);

		Group compSelectionField = new Group(selectionField, SWT.NO_RADIO_GROUP);
		compSelectionField.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		compSelectionField.setLayout(new GridLayout(3, true));
		for (Subcomponent sub : this.component.getComponentImplementation().getOwnedSubcomponents()) {
			Button comp = new Button(compSelectionField, SWT.CHECK);
			comp.setText(sub.getName());
			comp.setSelection(false);
			btnComponents.add(comp);
		}

		// Initialize check boxes
		enableComponentSelectionFields(false);

	}


	/**
	 * Creates the input field for selecting the resolute clause that drives
	 * the addition of this isolator to the design
	 * @param container
	 */
	private void createRequirementField(Composite container) {
		Label lblResoluteField = new Label(container, SWT.NONE);
		lblResoluteField.setText("Requirement");

		GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = GridData.FILL;
		cboIsolatorRequirement = new Combo(container, SWT.BORDER);
		cboIsolatorRequirement.setLayoutData(dataInfoField);
		cboIsolatorRequirement.add(NO_REQUIREMENT_SELECTED);
		requirements.forEach(r -> cboIsolatorRequirement.add(r));
		cboIsolatorRequirement.setText(NO_REQUIREMENT_SELECTED);
	}


	@Override
	protected void okPressed() {
		saveInput();
		super.okPressed();
	}

	private void enableComponentSelectionFields(boolean enable) {

		for (Button btn : btnComponents) {
			btn.setEnabled(enable);
			if (!enable) {
				btn.setSelection(true);
			}
		}

	}

	/**
	 * Saves information entered into the text fields.  This is needed because the
	 * text fields are disposed when the dialog closes.
	 * @param container
	 */
	private boolean saveInput() {

		virtualProcessorName = txtVirtualProcessorName.getText();
		virtualMachineOS = txtVirtualMachineOS.getText();

		components.clear();
		if (btnCompSelectionType.isEmpty()) {
			components.add(component.getQualifiedName());
		} else if (btnCompSelectionType.get(0).getSelection()) {
			components.add(component.getQualifiedName());
		} else {
			for (Button btn : btnComponents) {
				if (btn.getSelection()) {
					components.add(component.getQualifiedName() + "." + btn.getText());
				}
			}
		}

		isolatorRequirement = cboIsolatorRequirement.getText();
		if (isolatorRequirement.equals(NO_REQUIREMENT_SELECTED)) {
			isolatorRequirement = "";
		} else if (!requirements.contains(isolatorRequirement)) {
			Dialog.showError("Add Isolator",
					"Isolator requirement " + isolatorRequirement
							+ " does not exist in the model.  Select a requirement from the list, or choose "
							+ NO_REQUIREMENT_SELECTED + ".");
			return false;
		}

		return true;
	}

	public String getVirtualProcessorName() {
		return virtualProcessorName;
	}

	public String getVirtualMachineOS() {
		return virtualMachineOS;
	}

	public List<String> getIsolatedComponents() {
		return components;
	}

	public String getRequirement() {
		return isolatorRequirement;
	}

	public void setSelectedComponent(Subcomponent component) {
		this.component = component;
	}

	public void setRequirements(List<String> requirements) {
		this.requirements = requirements;
	}

}
