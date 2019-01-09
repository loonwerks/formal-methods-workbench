package com.collins.atc.ace.cyres.architecture.dialogs;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class AddMonitorDialog extends TitleAreaDialog {

	private Text txtMonitorTypeName;
	private Text txtMonitorImplementationLanguage;
	private Text txtMonitorImplName;
	private String strMonitorTypeName;
	private String strMonitorImplementationLanguage;
	private String strMonitorImplName;

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

		// Add filter information fields
		createMonitorTypeNameField(container);
		createImplLangField(container);
		createMonitorImplNameField(container);

		return area;
	}

	/**
	 * Creates the input text field for specifying the monitor type name
	 * @param container
	 */
	private void createMonitorTypeNameField(Composite container) {
		Label lblMonitorTypeNameField = new Label(container, SWT.NONE);
		lblMonitorTypeNameField.setText("Filter Type Name");

		GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = GridData.FILL;
		txtMonitorTypeName = new Text(container, SWT.BORDER);
		txtMonitorTypeName.setLayoutData(dataInfoField);
		txtMonitorTypeName.setText("Monitor");
	}

	/**
	 * Creates the input text field for specifying the monitor implementation name
	 * @param container
	 */
	private void createMonitorImplNameField(Composite container) {
		Label lblMonitorImplNameField = new Label(container, SWT.NONE);
		lblMonitorImplNameField.setText("Monitor Implementation Name");

		GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = GridData.FILL;
		txtMonitorImplName = new Text(container, SWT.BORDER);
		txtMonitorImplName.setLayoutData(dataInfoField);
		txtMonitorImplName.setText("MON");
	}

	/**
	 * Creates the input text field for specifying the monitor implementation language
	 * @param container
	 */
	private void createImplLangField(Composite container) {
		Label lblImplLangField = new Label(container, SWT.NONE);
		lblImplLangField.setText("Monitor Implementation Language");

		GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = GridData.FILL;
		txtMonitorImplementationLanguage = new Text(container, SWT.BORDER);
		txtMonitorImplementationLanguage.setLayoutData(dataInfoField);
	}


	/**
	 * Saves information entered into the text fields.  This is needed because the
	 * text fields are disposed when the dialog closes.
	 * @param container
	 */
	private void saveInput() {
		strMonitorTypeName = txtMonitorTypeName.getText();
		strMonitorImplementationLanguage = txtMonitorImplementationLanguage.getText();
		strMonitorImplName = txtMonitorImplName.getText();
	}

	@Override
	protected void okPressed() {
		saveInput();
		super.okPressed();
	}

	public String getMonitorTypeName() {
		return strMonitorTypeName;
	}

	public String getMonitorImplementationLanguage() {
		return strMonitorImplementationLanguage;
	}

	public String getMonitorImplName() {
		return strMonitorImplName;
	}

}
