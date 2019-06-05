package com.collins.fmw.cyres.architecture.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.osate.aadl2.ComponentCategory;
import org.osate.aadl2.PortCategory;
import org.osate.aadl2.Subcomponent;

public class AddIsolatorDialog extends TitleAreaDialog {

	private Text txtIsolatorImplementationName;
	private Text txtVirtualProcessorName;
	private List<Button> btnCompSelectionType = new ArrayList<>();
	private List<Button> btnComponents = new ArrayList<>();
	private List<Button> btnLogPortType = new ArrayList<>();
	private Combo cboIsolatorRequirement;
	private Text txtAgreeProperty;

	private String isolatorImplementationName = "";
	private String virtualProcessorName = "";
	private List<String> components = new ArrayList<>();
	private PortCategory logPortType = null;
	private String isolatorRequirement = "";
	private String agreeProperty = "";

	private Subcomponent component = null;
	private List<String> requirements = new ArrayList<>();

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
		createIsolatorImplementationNameField(container);
		createVirtualProcessorNameField(container);
		createComponentSelectionField(container);
		createLogPortField(container);
		createRequirementField(container);
		createAgreeField(container);

		return area;
	}

	/**
	 * Creates the input text field for specifying the isolator implementation name
	 * @param container
	 */
	private void createIsolatorImplementationNameField(Composite container) {
		Label lblIsolatorImplNameField = new Label(container, SWT.NONE);
		lblIsolatorImplNameField.setText("Isolator implementation name");

		GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = SWT.FILL;
		txtIsolatorImplementationName = new Text(container, SWT.BORDER);
		txtIsolatorImplementationName.setLayoutData(dataInfoField);
		txtIsolatorImplementationName.setText("VM");
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
		txtVirtualProcessorName.setText("VPROC");
	}

	/**
	 * Creates the field for specifying the components to isolate
	 * @param container
	 */
	private void createComponentSelectionField(Composite container) {

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

		btnCompSelectionType.clear();
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
		btnComponents.clear();
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
	 * Creates the input field for specifying if the isolator should contain
	 * a port for logging messages
	 * @param container
	 */
	private void createLogPortField(Composite container) {
		Label lblLogField = new Label(container, SWT.NONE);
		lblLogField.setText("Create log port");
		lblLogField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));

		// Create a group to contain the log port options
		Group logGroup = new Group(container, SWT.NONE);
		logGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		logGroup.setLayout(new RowLayout(SWT.HORIZONTAL));

		btnLogPortType.clear();

		Button btnNoLogPort = new Button(logGroup, SWT.RADIO);
		btnNoLogPort.setText("None");
		btnNoLogPort.setSelection(true);

		Button btnEventLogPort = new Button(logGroup, SWT.RADIO);
		btnEventLogPort.setText("Event");
		btnEventLogPort.setSelection(false);

		Button btnDataLogPort = new Button(logGroup, SWT.RADIO);
		btnDataLogPort.setText("Data");
		btnDataLogPort.setSelection(false);

		Button btnEventDataLogPort = new Button(logGroup, SWT.RADIO);
		btnEventDataLogPort.setText("Event Data");
		btnEventDataLogPort.setSelection(false);

		btnLogPortType.add(btnDataLogPort);
		btnLogPortType.add(btnEventLogPort);
		btnLogPortType.add(btnEventDataLogPort);
		btnLogPortType.add(btnNoLogPort);

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
		cboIsolatorRequirement = new Combo(container, SWT.BORDER | SWT.READ_ONLY);
		cboIsolatorRequirement.setLayoutData(dataInfoField);
		for (String clause : requirements) {
			cboIsolatorRequirement.add(clause);
		}
	}

	/**
	 * Creates the input text field for specifying the filter agree property
	 * @param container
	 */
	private void createAgreeField(Composite container) {
		Label lblAgreeField = new Label(container, SWT.NONE);
		lblAgreeField.setText("Isolator AGREE contract");

		GridData dataInfoField = new GridData(SWT.FILL, SWT.FILL, true, false);
		txtAgreeProperty = new Text(container, SWT.BORDER);
		txtAgreeProperty.setLayoutData(dataInfoField);

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
	private void saveInput() {

		isolatorImplementationName = txtIsolatorImplementationName.getText();
		virtualProcessorName = txtVirtualProcessorName.getText();
		components.clear();
		if (btnCompSelectionType.get(0).getSelection()) {
			components.add(component.getName());
		} else {
			for (Button btn : btnComponents) {
				if (btn.getSelection()) {
					components.add(btn.getText());
				}
			}
		}
		logPortType = null;
		for (int i = 0; i < btnLogPortType.size(); i++) {
			if (btnLogPortType.get(i).getSelection()) {
				logPortType = PortCategory.get(i);
				break;
			}
		}
		isolatorRequirement = cboIsolatorRequirement.getText();
		agreeProperty = txtAgreeProperty.getText();

	}

	public String getIsolatorImplementationName() {
		return isolatorImplementationName;
	}

	public String getVirtualProcessorName() {
		return virtualProcessorName;
	}

	public List<String> getIsolatedComponents() {
		return components;
	}

	public PortCategory getLogPortType() {
		return logPortType;
	}

	public String getRequirement() {
		return isolatorRequirement;
	}

	public String getAgreeProperty() {
		return agreeProperty;
	}

	public void setSelectedComponent(Subcomponent component) {
		this.component = component;
	}

	public void setRequirements(List<String> requirements) {
		this.requirements = requirements;
	}

}
