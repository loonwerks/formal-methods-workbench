package com.rockwellcollins.atc.darpacase.architecture.dialogs;

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

/**
 * This class creates the Add Filter wizard
 */
public class AddFilterDialog extends TitleAreaDialog {

	private Text txtFilterTypeName;
	private Text txtFilterImplementationLanguage;
	private Text txtFilterRegularExpression;
	private Text txtFilterImplName;
	private String strFilterTypeName;
	private String strFilterImplementationLanguage;
	private String strFilterRegularExpression;
	private String strFilterImplName;

	public AddFilterDialog(Shell parentShell) {
		super(parentShell);
		setHelpAvailable(false);
	}

	@Override
	public void create() {
		super.create();
		setTitle("Add Filter");
		setMessage(
				"Enter Filter details.  You may optionally leave these fields empty and manually edit the AADL filter component once it is added to the model.",
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
		createFilterTypeNameField(container);
		createImplLangField(container);
		createRegExField(container);
		createFilterImplNameField(container);

		return area;
	}

	/**
	 * Creates the input text field for specifying the filter type name
	 * @param container
	 */
	private void createFilterTypeNameField(Composite container) {
		Label lblFilterTypeNameField = new Label(container, SWT.NONE);
		lblFilterTypeNameField.setText("Filter Type Name");

		GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = GridData.FILL;
		txtFilterTypeName = new Text(container, SWT.BORDER);
		txtFilterTypeName.setLayoutData(dataInfoField);
		txtFilterTypeName.setText("Filter");
	}

	/**
	 * Creates the input text field for specifying the filter implementation name
	 * @param container
	 */
	private void createFilterImplNameField(Composite container) {
		Label lblFilterImplNameField = new Label(container, SWT.NONE);
		lblFilterImplNameField.setText("Filter Implementation Name");

		GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = GridData.FILL;
		txtFilterImplName = new Text(container, SWT.BORDER);
		txtFilterImplName.setLayoutData(dataInfoField);
		txtFilterImplName.setText("FLT");
	}

	/**
	 * Creates the input text field for specifying the filter implementation language
	 * @param container
	 */
	private void createImplLangField(Composite container) {
		Label lblImplLangField = new Label(container, SWT.NONE);
		lblImplLangField.setText("Filter Implementation Language");

		GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = GridData.FILL;
		txtFilterImplementationLanguage = new Text(container, SWT.BORDER);
		txtFilterImplementationLanguage.setLayoutData(dataInfoField);
	}

	/**
	 * Creates the input text field for specifying the filter regular expression
	 * @param container
	 */
	private void createRegExField(Composite container) {
		Label lblRegExField = new Label(container, SWT.NONE);
		lblRegExField.setText("Filter Regular Expression");

		GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = GridData.FILL;
		txtFilterRegularExpression = new Text(container, SWT.BORDER);
		txtFilterRegularExpression.setLayoutData(dataInfoField);
	}

	/**
	 * Saves information entered into the text fields.  This is needed because the
	 * text fields are disposed when the dialog closes.
	 * @param container
	 */
	private void saveInput() {
		strFilterTypeName = txtFilterTypeName.getText();
		strFilterImplementationLanguage = txtFilterImplementationLanguage.getText();
		strFilterRegularExpression = txtFilterRegularExpression.getText();
		strFilterImplName = txtFilterImplName.getText();
	}

	@Override
	protected void okPressed() {
		saveInput();
		super.okPressed();
	}

	public String getFilterTypeName() {
		return strFilterTypeName;
	}

	public String getFilterImplementationLanguage() {
		return strFilterImplementationLanguage;
	}

	public String getFilterRegularExpression() {
		return strFilterRegularExpression;
	}

	public String getFilterImplName() {
		return strFilterImplName;
	}

}
