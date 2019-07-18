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
import org.osate.aadl2.PortCategory;
import org.osate.ui.dialogs.Dialog;

/**
 * This class creates the Add Attestation Manager wizard
 */
public class AddAttestationManagerDialog extends TitleAreaDialog {

	private static int MAX_CACHE_SIZE = 6;
	private static int DEFAULT_CACHE_SIZE = 4;

	private Text txtImplementationName;
	private Text txtImplementationLanguage;
	private Text txtCacheTimeout;
	private Combo cboCacheSize;
	private List<Button> btnLogPortType = new ArrayList<>();
	private Combo cboRequirement;
	private Button btnPropagateGuarantees;
	private Text txtAgreeProperty;
	private String implementationName;
	private String implementationLanguage;
	private String cacheTimeout;
	private String cacheSize;
	private PortCategory logPortType = null;
	private String requirement;
	private boolean propagateGuarantees;
	private String commDriver = "";
	private List<String> requirements = new ArrayList<>();
	private String agreeProperty;

	private static final String NO_REQUIREMENT_SELECTED = "<No requirement selected>";

	public AddAttestationManagerDialog(Shell parentShell) {
		super(parentShell);
		setHelpAvailable(false);
	}

	@Override
	public void create() {
		super.create();
		setTitle("Add Attestation Manager");
		setMessage(
				"Enter Attestation Manager details.  You may optionally leave these fields empty and manually edit the model.",
				IMessageProvider.NONE);
	}

	@Override
	protected Point getInitialSize() {
		final Point size = super.getInitialSize();
		size.y += convertHeightInCharsToPixels(1);
		return size;
	}

	public void create(String commDriver, List<String> requirements) {
		this.commDriver = commDriver;
		this.requirements = requirements;
		if (commDriver == null || commDriver.isEmpty()) {
			Dialog.showError("Add Attestation Manager", "Unknown communication driver.");
			return;
		}
		super.create();
		setTitle("Add Attestation Manager to " + commDriver);
		setMessage("Enter Attestation Manager details for adding attestation to " + commDriver
				+ ".  You may optionally leave these fields empty and manually edit the AADL attestation manager component once it is added to the model.",
				IMessageProvider.NONE);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		GridLayout layout = new GridLayout(2, false);
		container.setLayout(layout);

		// Add attestation manager information fields
		createImplementationNameField(container);
		createImplementationLanguageField(container);
		createCacheTimeoutField(container);
		createCacheSizeField(container);
		createLogPortField(container);
		createRequirementField(container);
		createPropagateGuaranteesField(container);
		createAgreePropertyField(container);

		return area;
	}


	private void createImplementationNameField(Composite container) {

		Label lblImplNameField = new Label(container, SWT.NONE);
		lblImplNameField.setText("Attestation Manager implementation name");

		GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = SWT.FILL;
		txtImplementationName = new Text(container, SWT.BORDER);
		txtImplementationName.setLayoutData(dataInfoField);
		txtImplementationName.setText("AM");

	}

	private void createImplementationLanguageField(Composite container) {

		Label lblImplLangField = new Label(container, SWT.NONE);
		lblImplLangField.setText("Attestation Manager implementation language");

		GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = SWT.FILL;
		txtImplementationLanguage = new Text(container, SWT.BORDER);
		txtImplementationLanguage.setLayoutData(dataInfoField);

	}

	private void createCacheTimeoutField(Composite container) {

		Label lblCacheTimeoutField = new Label(container, SWT.NONE);
		lblCacheTimeoutField.setText("Cache timeout (minutes)");

		GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = SWT.FILL;
		txtCacheTimeout = new Text(container, SWT.BORDER);
		txtCacheTimeout.setLayoutData(dataInfoField);

	}

	private void createCacheSizeField(Composite container) {

		Label lblCacheSizeField = new Label(container, SWT.NONE);
		lblCacheSizeField.setText("Cache size");

		GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = SWT.FILL;
		cboCacheSize = new Combo(container, SWT.BORDER | SWT.READ_ONLY);
		cboCacheSize.setLayoutData(dataInfoField);
		for (int i = 1; i <= MAX_CACHE_SIZE; i++) {
			cboCacheSize.add(Integer.toString(i));
		}
		cboCacheSize.setText(Integer.toString(DEFAULT_CACHE_SIZE));

	}


	/**
	 * Creates the input field for specifying if the attestation manager should contain
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
	 * the addition of this filter to the design
	 * @param container
	 */
	private void createRequirementField(Composite container) {
		Label lblResoluteField = new Label(container, SWT.NONE);
		lblResoluteField.setText("Requirement");

		GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = GridData.FILL;
		cboRequirement = new Combo(container, SWT.BORDER);
		cboRequirement.setLayoutData(dataInfoField);
		// This seems to be the best way to prevent user from entering text
		cboRequirement.addVerifyListener(e -> {
			for (String s : cboRequirement.getItems()) {
				if (s.equals(e.text)) {
					e.doit = true;
					return;
				}
			}
			e.doit = false;
		});
		cboRequirement.add(NO_REQUIREMENT_SELECTED);
		requirements.forEach(r -> cboRequirement.add(r));
		cboRequirement.setText(NO_REQUIREMENT_SELECTED);

	}

	/**
	 * Creates the input text field for specifying the guarantees to propagate
	 * @param container
	 */
	private void createPropagateGuaranteesField(Composite container) {

		Label lblPropagateGuaranteesField = new Label(container, SWT.NONE);
		lblPropagateGuaranteesField.setText("Preserve Guarantees from " + commDriver);

		GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = SWT.FILL;
		btnPropagateGuarantees = new Button(container, SWT.CHECK);
		btnPropagateGuarantees.setSelection(true);
		btnPropagateGuarantees.setLayoutData(dataInfoField);

	}

	/**
	 * Creates the input text field for specifying the attestation agree property
	 * @param container
	 */
	private void createAgreePropertyField(Composite container) {
		Label lblAgreeField = new Label(container, SWT.NONE);
		lblAgreeField.setText("Attestation AGREE contract");

		GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = GridData.FILL;
		txtAgreeProperty = new Text(container, SWT.BORDER);
		txtAgreeProperty.setLayoutData(dataInfoField);
	}

	@Override
	protected void okPressed() {
		saveInput();
		super.okPressed();
	}

	/**
	 * Saves information entered into the text fields.  This is needed because the
	 * text fields are disposed when the dialog closes.
	 * @param container
	 */
	private void saveInput() {
		implementationName = txtImplementationName.getText();
		implementationLanguage = txtImplementationLanguage.getText();
		cacheTimeout = txtCacheTimeout.getText();
		cacheSize = cboCacheSize.getText();
		logPortType = null;
		for (int i = 0; i < btnLogPortType.size(); i++) {
			if (btnLogPortType.get(i).getSelection()) {
				logPortType = PortCategory.get(i);
				break;
			}
		}
		requirement = cboRequirement.getText();
		if (requirement.equals(NO_REQUIREMENT_SELECTED)) {
			requirement = "";
		}
		agreeProperty = txtAgreeProperty.getText();
		propagateGuarantees = btnPropagateGuarantees.getSelection();
	}

	public String getImplementationName() {
		return implementationName;
	}

	public String getImplementationLanguage() {
		return implementationLanguage;
	}

	public String getCacheTimeout() {
		return cacheTimeout;
	}

	public String getCacheSize() {
		return cacheSize;
	}

	public PortCategory getLogPortType() {
		return logPortType;
	}

	public boolean getPropagateGuarantees() {
		return propagateGuarantees;
	}

	public String getRequirement() {
		return requirement;
	}

	public String getAgreeProperty() {
		return agreeProperty;
	}

}
