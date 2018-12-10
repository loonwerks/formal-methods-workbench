package com.rockwellcollins.atc.darpacase.architecture.dialogs;

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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * This class creates the Add Attestation Manager wizard
 */
public class AddAttestationManagerDialog extends TitleAreaDialog {

	private static int MAX_CACHE_SIZE = 6;
	private static int DEFAULT_CACHE_SIZE = 4;

	private Combo cboComponents;
	private Text txtImplementationName;
	private Text txtImplementationLanguage;
	private Text txtCacheTimeout;
//	private Text txtCacheSize;
	private Combo cboCacheSize;
	private Text txtLogSize;
	private Combo cboResoluteClause;
	private Button btnPropagateGuarantees;
	private Text txtAgreeProperty;
	private String commDriverComponent;
	private String implementationName;
	private String implementationLanguage;
	private String cacheTimeout;
	private String cacheSize;
	private String logSize;
	private String resoluteClause;
	private boolean propagateGuarantees;
	private String commDriver = "";
	private List<String> components = new ArrayList<>();
	private List<String> resoluteClauses = new ArrayList<>();
	private String agreeProperty;

	public AddAttestationManagerDialog(Shell parentShell) {
		super(parentShell);
		setHelpAvailable(false);
	}

	@Override
	public void create() {
		super.create();
		setTitle("Add Attestation Manager");
		setMessage(
				"Enter Attestation Manager details.  You may optionally leave these fields empty and manually edit the AADL attestation manager component once it is added to the model.",
				IMessageProvider.NONE);
	}

	@Override
	protected Point getInitialSize() {
		final Point size = super.getInitialSize();
		size.y += convertHeightInCharsToPixels(1);
		return size;
	}

	public void create(String commDriver, List<String> components, List<String> resoluteClauses) {
		this.commDriver = commDriver;
		this.components = components;
		this.resoluteClauses = resoluteClauses;
		if (commDriver == null || commDriver.isEmpty()) {
			create();
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
//		createComponentTypeField(container);
		createCommDriverField(container);
		createImplementationNameField(container);
		createImplementationLanguageField(container);
		createCacheTimeoutField(container);
		createCacheSizeField(container);
		createLogSizeField(container);
		createResoluteClauseField(container);
		createPropagateGuaranteesField(container);
		createAgreePropertyField(container);

		return area;
	}

	private void createCommDriverField(Composite container) {

		Label lblCommDriverField = new Label(container, SWT.NONE);
		lblCommDriverField.setText("Communication Driver Component");

		GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = GridData.FILL;
		cboComponents = new Combo(container, SWT.BORDER | SWT.READ_ONLY);
		cboComponents.setLayoutData(dataInfoField);
		for (String component : components) {
			cboComponents.add(component);
		}
		if (!commDriver.isEmpty()) {
			cboComponents.setText(commDriver);
		}

	}

	private void createImplementationNameField(Composite container) {

		Label lblImplNameField = new Label(container, SWT.NONE);
		lblImplNameField.setText("Attestation Manager Implementation Name");

		GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = SWT.FILL;
		txtImplementationName = new Text(container, SWT.BORDER);
		txtImplementationName.setLayoutData(dataInfoField);
		txtImplementationName.setText("AM");

	}

	private void createImplementationLanguageField(Composite container) {

		Label lblImplLangField = new Label(container, SWT.NONE);
		lblImplLangField.setText("Attestation Manager Implementation Language");

		GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = SWT.FILL;
		txtImplementationLanguage = new Text(container, SWT.BORDER);
		txtImplementationLanguage.setLayoutData(dataInfoField);

	}

	private void createCacheTimeoutField(Composite container) {

		Label lblCacheTimeoutField = new Label(container, SWT.NONE);
		lblCacheTimeoutField.setText("Cache Timeout");

		GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = SWT.FILL;
		txtCacheTimeout = new Text(container, SWT.BORDER);
		txtCacheTimeout.setLayoutData(dataInfoField);

	}

	private void createCacheSizeField(Composite container) {

		Label lblCacheSizeField = new Label(container, SWT.NONE);
		lblCacheSizeField.setText("Cache Size");

		GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = SWT.FILL;
//		txtCacheSize = new Text(container, SWT.BORDER);
//		txtCacheSize.setLayoutData(dataInfoField);
		cboCacheSize = new Combo(container, SWT.BORDER | SWT.READ_ONLY);
		cboCacheSize.setLayoutData(dataInfoField);
		for (int i = 1; i <= MAX_CACHE_SIZE; i++) {
			cboCacheSize.add(Integer.toString(i));
		}
		cboCacheSize.setText(Integer.toString(DEFAULT_CACHE_SIZE));

	}

	private void createLogSizeField(Composite container) {

		Label lblLogSizeField = new Label(container, SWT.NONE);
		lblLogSizeField.setText("Log Size");

		GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = SWT.FILL;
		txtLogSize = new Text(container, SWT.BORDER);
		txtLogSize.setLayoutData(dataInfoField);

	}

	/**
	 * Creates the input field for selecting the resolute clause that drives
	 * the addition of this filter to the design
	 * @param container
	 */
	private void createResoluteClauseField(Composite container) {
		Label lblResoluteField = new Label(container, SWT.NONE);
		lblResoluteField.setText("Resolute Clause");

		GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = GridData.FILL;
		cboResoluteClause = new Combo(container, SWT.BORDER | SWT.READ_ONLY);
		cboResoluteClause.setLayoutData(dataInfoField);
		for (String clause : resoluteClauses) {
			cboResoluteClause.add(clause);
		}
	}

	/**
	 * Creates the input text field for specifying the guarantees to propagate
	 * @param container
	 */
	private void createPropagateGuaranteesField(Composite container) {

		Label lblPropagateGuaranteesField = new Label(container, SWT.NONE);
		if (commDriver.isEmpty()) {
			lblPropagateGuaranteesField.setText("Propagate Guarantees from Comm Driver");
		} else {
			lblPropagateGuaranteesField.setText("Propagate Guarantees from " + commDriver);
		}

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
		lblAgreeField.setText("Attestation AGREE Contract");

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
		commDriverComponent = cboComponents.getText();
		implementationName = txtImplementationName.getText();
		implementationLanguage = txtImplementationLanguage.getText();
		cacheTimeout = txtCacheTimeout.getText();
//		cacheSize = txtCacheSize.getText();
		cacheSize = cboCacheSize.getText();
		logSize = txtLogSize.getText();
		resoluteClause = cboResoluteClause.getText();
		agreeProperty = txtAgreeProperty.getText();
		propagateGuarantees = btnPropagateGuarantees.getSelection();
	}

	public String getCommDriverComponent() {
		return commDriverComponent;
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

	public String getLogSize() {
		return logSize;
	}

	public boolean getPropagateGuarantees() {
		return propagateGuarantees;
	}

	public String getResoluteClause() {
		return resoluteClause;
	}

	public String getAgreeProperty() {
		return agreeProperty;
	}

}
