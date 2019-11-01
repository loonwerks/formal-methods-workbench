package com.collins.fmw.cyres.architecture.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.osate.ui.dialogs.Dialog;

import com.collins.fmw.cyres.architecture.handlers.AddMonitorHandler;

public class AddMonitorDialog extends TitleAreaDialog {

	private Text txtMonitorImplementationName;
	private Combo cboExpectedPort;
	private Combo cboAlertPort;
	private Combo cboMonitorRequirement;
	private Text txtAgreeProperty;

	private String monitorImplementationName;
	private String expectedPort = "";
	private String alertPort = "";
	private String monitorRequirement = "";
	private String agreeProperty = "";

	private List<String> inports = new ArrayList<>();
	private List<String> outports = new ArrayList<>();
	private List<String> requirements = new ArrayList<>();

	private static final String NO_PORT_SELECTED = "<No port selected>";
	private static final String NO_REQUIREMENT_SELECTED = "<No requirement selected>";

	public AddMonitorDialog(Shell parentShell) {
		super(parentShell);
		setHelpAvailable(false);
	}

	@Override
	public void create() {
		super.create();
		setTitle("Add Monitor");
		setMessage(
				"Enter Monitor details.  You may optionally leave these fields empty and manually edit the AADL monitor component once it is added to the model.",
				IMessageProvider.NONE);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout layout = new GridLayout(2, false);
		container.setLayout(layout);

		// Add monitor information fields
		createMonitorImplementationNameField(container);
		createExpectedPortField(container);
		createAlertPortField(container);
		createRequirementField(container);
		createAgreeField(container);

		return area;
	}

	/**
	 * Creates the input text field for specifying the monitor implementation name
	 * @param container
	 */
	private void createMonitorImplementationNameField(Composite container) {
		Label lblMonitorImplNameField = new Label(container, SWT.NONE);
		lblMonitorImplNameField.setText("Monitor Implementation Name");

		GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = GridData.FILL;
		txtMonitorImplementationName = new Text(container, SWT.BORDER);
		txtMonitorImplementationName.setLayoutData(dataInfoField);
		txtMonitorImplementationName.setText(AddMonitorHandler.MONITOR_IMPL_NAME);
	}


	/**
	 * Creates the input field for specifying what to connect the 'expected' port to
	 * @param container
	 */
	private void createExpectedPortField(Composite container) {
		Label lblExpectedField = new Label(container, SWT.NONE);
		lblExpectedField.setText("Expected port connection");
		lblExpectedField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));

		GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = GridData.FILL;
		cboExpectedPort = new Combo(container, SWT.BORDER);
		cboExpectedPort.setLayoutData(dataInfoField);
		cboExpectedPort.add(NO_PORT_SELECTED);
		outports.forEach(p -> cboExpectedPort.add(p));
		cboExpectedPort.setText(NO_PORT_SELECTED);
	}

	/**
	 * Creates the input field for specifying what to connect the 'alert' port to
	 * @param container
	 */
	private void createAlertPortField(Composite container) {
		Label lblAlertField = new Label(container, SWT.NONE);
		lblAlertField.setText("Alert port connection");
		lblAlertField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));

		GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = GridData.FILL;
		cboAlertPort = new Combo(container, SWT.BORDER);
		cboAlertPort.setLayoutData(dataInfoField);
		cboAlertPort.add(NO_PORT_SELECTED);
		inports.forEach(p -> cboAlertPort.add(p));
		cboAlertPort.setText(NO_PORT_SELECTED);
	}


	/**
	 * Creates the input field for selecting the resolute clause that drives
	 * the addition of this monitor to the design
	 * @param container
	 */
	private void createRequirementField(Composite container) {
		Label lblResoluteField = new Label(container, SWT.NONE);
		lblResoluteField.setText("Requirement");

		GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = GridData.FILL;
		cboMonitorRequirement = new Combo(container, SWT.BORDER);
		cboMonitorRequirement.setLayoutData(dataInfoField);
		cboMonitorRequirement.add(NO_REQUIREMENT_SELECTED);
		requirements.forEach(r -> cboMonitorRequirement.add(r));
		cboMonitorRequirement.setText(NO_REQUIREMENT_SELECTED);

	}

	/**
	 * Creates the input text field for specifying the monitor agree property
	 * @param container
	 */
	private void createAgreeField(Composite container) {
		Label lblAgreeField = new Label(container, SWT.NONE);
		lblAgreeField.setText("Monitor AGREE contract");

		GridData dataInfoField = new GridData(SWT.FILL, SWT.FILL, true, false);
		txtAgreeProperty = new Text(container, SWT.BORDER);
		txtAgreeProperty.setLayoutData(dataInfoField);

	}

	/**
	 * Saves information entered into the text fields.  This is needed because the
	 * text fields are disposed when the dialog closes.
	 * @param container
	 */
	private boolean saveInput() {
		monitorImplementationName = txtMonitorImplementationName.getText();
		expectedPort = cboExpectedPort.getText();
		if (expectedPort.equals(NO_PORT_SELECTED)) {
			expectedPort = "";
		} else if (!outports.contains(expectedPort)) {
			Dialog.showError("Add Monitor", "Port " + expectedPort
					+ " does not exist in the model.  Select a port from the list to connect the monitor's 'expected' port to, or choose "
					+ NO_PORT_SELECTED + ".");
			return false;
		}
		alertPort = cboAlertPort.getText();
		if (alertPort.equals(NO_PORT_SELECTED)) {
			alertPort = "";
		} else if (!inports.contains(alertPort)) {
			Dialog.showError("Add Monitor", "Port " + alertPort
					+ " does not exist in the model.  Select a port from the list to connect the monitor's 'alert' port to, or choose "
					+ NO_PORT_SELECTED + ".");
			return false;
		}
		monitorRequirement = cboMonitorRequirement.getText();
		if (monitorRequirement.equals(NO_REQUIREMENT_SELECTED)) {
			monitorRequirement = "";
		} else if (!requirements.contains(monitorRequirement)) {
			Dialog.showError("Add Monitor",
					"Monitor requirement " + monitorRequirement
							+ " does not exist in the model.  Select a requirement from the list, or choose "
							+ NO_REQUIREMENT_SELECTED + ".");
			return false;
		}
		agreeProperty = txtAgreeProperty.getText();

		return true;
	}

	@Override
	protected void okPressed() {
		if (!saveInput()) {
			return;
		}
		super.okPressed();
	}

	public String getMonitorImplementationName() {
		return monitorImplementationName;
	}

	public String getExpectedPort() {
		return expectedPort;
	}

	public String getAlertPort() {
		return alertPort;
	}

	public String getAgreeProperty() {
		return agreeProperty;
	}

	public String getRequirement() {
		return monitorRequirement;
	}

	public void setPorts(List<String> inports, List<String> outports) {
		this.inports = inports;
		this.outports = outports;
	}

	public void setRequirements(List<String> requirements) {
		this.requirements = requirements;
	}

}
