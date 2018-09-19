package com.rockwellcollins.atc.darpacase.architecture.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
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
 * This class creates the Add Filter wizard
 */
public class AddFilterDialog extends TitleAreaDialog {

	private Text txtFilterTypeName;
	private Text txtFilterImplementationLanguage;
	private Text txtFilterRegularExpression;
	private Text txtFilterImplName;
	private Combo cboFilterResoluteClause;
	private Text txtAgreeProperties;
	private List<Button> btnPropagateGuarantees = new ArrayList<>();
	private String strFilterTypeName;
	private String strFilterImplementationLanguage;
	private String strFilterRegularExpression;
	private String strFilterImplName;
	private String strFilterResoluteClause;
	private String strAgreeProperties;
	private String strSourceName;
	private List<String> strSourceGuarantees = new ArrayList<>();
	private List<String> strPropagateGuarantees = new ArrayList<>();
	private List<String> strResoluteClauses = new ArrayList<>();


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
		container.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		GridLayout layout = new GridLayout(2, false);
		container.setLayout(layout);

		// Add filter information fields
		createFilterTypeNameField(container);
		createImplLangField(container);
		createRegExField(container);
		createFilterImplNameField(container);
		createResoluteField(container);
		createGuaranteeSelectionField(container);
		createAgreeField(container);

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
		dataInfoField.horizontalAlignment = SWT.FILL;
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
		dataInfoField.horizontalAlignment = SWT.FILL;
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
		dataInfoField.horizontalAlignment = SWT.FILL;
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
	 * Creates the input field for selecting the resolute clause that drives
	 * the addition of this filter to the design
	 * @param container
	 */
	private void createResoluteField(Composite container) {
		Label lblResoluteField = new Label(container, SWT.NONE);
		lblResoluteField.setText("Resolute Clause");

		GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = GridData.FILL;
		cboFilterResoluteClause = new Combo(container, SWT.BORDER | SWT.READ_ONLY);
		cboFilterResoluteClause.setLayoutData(dataInfoField);
		for (String clause : strResoluteClauses) {
			cboFilterResoluteClause.add(clause);
		}
	}

	/**
	 * Creates the input text field for specifying agree properties
	 * @param container
	 */
	private void createAgreeField(Composite container) {
		Label lblAgreeField = new Label(container, SWT.NONE);
		lblAgreeField.setText("AGREE Properties");
		lblAgreeField.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		txtAgreeProperties = new Text(container, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);

		GridData dataInfoField = new GridData(SWT.FILL, SWT.TOP, true, false);
		dataInfoField.heightHint = 3 * txtAgreeProperties.getLineHeight();

		txtAgreeProperties.setLayoutData(dataInfoField);
	}

	/**
	 * Creates the input text field for specifying the guarantees to propagate
	 * @param container
	 */
	private void createGuaranteeSelectionField(Composite container) {

		// Only create this field if there are guarantees in the source
		// component to propagate
		if (strSourceGuarantees.size() > 0) {

			Label lblSelectionField = new Label(container, SWT.NONE);
			lblSelectionField.setText("Propagate Guarantees from " + strSourceName);
			lblSelectionField.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

			GridData selectionFieldLayoutData = new GridData(SWT.FILL, SWT.FILL, true, false);
//			dataInfoField.grabExcessHorizontalSpace = true;
//			dataInfoField.horizontalAlignment = SWT.FILL;

			Composite selectionField = new Composite(container, SWT.BORDER);
//			selectionField.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
			GridLayout layout = new GridLayout(1, true);
			selectionField.setLayout(layout);
			selectionField.setLayoutData(selectionFieldLayoutData);

			btnPropagateGuarantees.clear();
			for (String guarantee : strSourceGuarantees) {
				Button selectGuarantee = new Button(selectionField, SWT.CHECK);
				String formattedGuarantee = guarantee.trim();
				formattedGuarantee = formattedGuarantee
						.substring("guarantee ".length(),
								formattedGuarantee.length() - 1);
				selectGuarantee.setText(formattedGuarantee);
				selectGuarantee.setSelection(true);
				selectGuarantee.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
				btnPropagateGuarantees.add(selectGuarantee);
			}

		}
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
		strFilterResoluteClause = cboFilterResoluteClause.getText();
		strAgreeProperties = txtAgreeProperties.getText();
		strPropagateGuarantees.clear();
		for (int i = 0; i < btnPropagateGuarantees.size(); i++) {
			if (btnPropagateGuarantees.get(i).getSelection()) {
				strPropagateGuarantees.add(strSourceGuarantees.get(i));
			}
		}
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

	public String getAgreeProperties() {
		return strAgreeProperties;
	}

	public List<String> getGuaranteeList() {
		return strPropagateGuarantees;
	}

	public String getResoluteClause() {
		return strFilterResoluteClause;
	}

	public void setGuaranteeList(String sourceName, List<String> guarantees) {
		strSourceName = sourceName;
		strSourceGuarantees = guarantees;
	}

	public void setResoluteClauses(List<String> clauses) {
		strResoluteClauses = clauses;
	}

}
