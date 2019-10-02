package com.collins.fmw.cyres.architecture.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
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
import org.osate.aadl2.PortCategory;
import org.osate.ui.dialogs.Dialog;

import com.collins.fmw.cyres.architecture.handlers.AddMonitorHandler;

public class AddMonitorDialog extends TitleAreaDialog {

	private Text txtMonitorImplementationName;
	private Text txtMonitorImplementationLanguage;
	private List<Button> btnAlertPortType = new ArrayList<>();
	private Combo cboMonitorRequirement;
	private Text txtAgreeProperty;

	private String monitorImplementationName;
	private String monitorImplementationLanguage;
	private PortCategory alertPortType = null;
	private String monitorRequirement = "";
	private String agreeProperty = "";

	private List<String> requirements = new ArrayList<>();

	private static final String DEFAULT_IMPL_LANGUAGE = "CakeML";
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
		createImplementationLanguageField(container);
		createAlertPortTypeField(container);
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
	 * Creates the input text field for specifying the monitor implementation language
	 * @param container
	 */
	private void createImplementationLanguageField(Composite container) {
		Label lblImplLangField = new Label(container, SWT.NONE);
		lblImplLangField.setText("Monitor Implementation Language");

		GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = GridData.FILL;
		txtMonitorImplementationLanguage = new Text(container, SWT.BORDER);
		txtMonitorImplementationLanguage.setLayoutData(dataInfoField);
		txtMonitorImplementationLanguage.setText(DEFAULT_IMPL_LANGUAGE);
	}

	/**
	 * Creates the input field for specifying the alert port type
	 * @param container
	 */
	private void createAlertPortTypeField(Composite container) {
		Label lblAlertField = new Label(container, SWT.NONE);
		lblAlertField.setText("Alert port type");
		lblAlertField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));

		// Create a group to contain the log port options
		Group alertGroup = new Group(container, SWT.NONE);
		alertGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		alertGroup.setLayout(new RowLayout(SWT.HORIZONTAL));

		btnAlertPortType.clear();

		Button btnEventAlertPort = new Button(alertGroup, SWT.RADIO);
		btnEventAlertPort.setText("Event");
		btnEventAlertPort.setSelection(true);

		Button btnDataAlertPort = new Button(alertGroup, SWT.RADIO);
		btnDataAlertPort.setText("Data");
		btnDataAlertPort.setSelection(false);

		Button btnEventDataAlertPort = new Button(alertGroup, SWT.RADIO);
		btnEventDataAlertPort.setText("Event Data");
		btnEventDataAlertPort.setSelection(false);

		btnAlertPortType.add(btnDataAlertPort);
		btnAlertPortType.add(btnEventAlertPort);
		btnAlertPortType.add(btnEventDataAlertPort);

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
		monitorImplementationLanguage = txtMonitorImplementationLanguage.getText();
		alertPortType = null;
		for (int i = 0; i < btnAlertPortType.size(); i++) {
			if (btnAlertPortType.get(i).getSelection()) {
				alertPortType = PortCategory.get(i);
				break;
			}
		}
		monitorRequirement = cboMonitorRequirement.getText();
		if (monitorRequirement.equals(NO_REQUIREMENT_SELECTED)) {
			monitorRequirement = "";
		} else if (!requirements.contains(monitorRequirement)) {
			Dialog.showError("Add Filter",
					"Filter requirement " + monitorRequirement
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

	public String getMonitorImplementationLanguage() {
		return monitorImplementationLanguage;
	}

	public PortCategory getAlertPortType() {
		return alertPortType;
	}

	public String getAgreeProperty() {
		return agreeProperty;
	}

	public String getRequirement() {
		return monitorRequirement;
	}

	public void setRequirements(List<String> requirements) {
		this.requirements = requirements;
	}

}
