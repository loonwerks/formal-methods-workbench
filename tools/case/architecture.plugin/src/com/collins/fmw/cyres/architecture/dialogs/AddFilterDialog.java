package com.collins.fmw.cyres.architecture.dialogs;

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

	private Text txtFilterImplementationName;
	private Text txtFilterImplementationLanguage;
	private Combo cboFilterRequirement;
	private Text txtAgreeProperty;
	private List<Button> btnPropagateGuarantees = new ArrayList<>();
	private String strFilterImplementationLanguage = "";
	private String strFilterImplementationName = "";
	private String strFilterRequirement = "";
	private String strAgreeProperty = "";
	private String strSourceName = "";
	private List<String> strSourceGuarantees = new ArrayList<>();
	private List<String> strPropagateGuarantees = new ArrayList<>();
	private List<String> strRequirements = new ArrayList<>();

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
		createFilterImplementationNameField(container);
		createImplementationLanguageField(container);
		createRequirementField(container);
		createGuaranteeSelectionField(container);
		createAgreeField(container);

		return area;
	}

	/**
	 * Creates the input text field for specifying the filter implementation name
	 * @param container
	 */
	private void createFilterImplementationNameField(Composite container) {
		Label lblFilterImplNameField = new Label(container, SWT.NONE);
		lblFilterImplNameField.setText("Filter Implementation Name");

		GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = SWT.FILL;
		txtFilterImplementationName = new Text(container, SWT.BORDER);
		txtFilterImplementationName.setLayoutData(dataInfoField);
		txtFilterImplementationName.setText("FLT");
	}

	/**
	 * Creates the input text field for specifying the filter implementation language
	 * @param container
	 */
	private void createImplementationLanguageField(Composite container) {
		Label lblImplLangField = new Label(container, SWT.NONE);
		lblImplLangField.setText("Filter Implementation Language");

		GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = SWT.FILL;
		txtFilterImplementationLanguage = new Text(container, SWT.BORDER);
		txtFilterImplementationLanguage.setLayoutData(dataInfoField);
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
		cboFilterRequirement = new Combo(container, SWT.BORDER | SWT.READ_ONLY);
		cboFilterRequirement.setLayoutData(dataInfoField);
		for (String clause : strRequirements) {
			cboFilterRequirement.add(clause);
		}
	}

	/**
	 * Creates the input text field for specifying the filter agree property
	 * @param container
	 */
	private void createAgreeField(Composite container) {
		Label lblAgreeField = new Label(container, SWT.NONE);
		lblAgreeField.setText("Filter AGREE Contract");

		GridData dataInfoField = new GridData(SWT.FILL, SWT.FILL, true, false);
		txtAgreeProperty = new Text(container, SWT.BORDER);
		txtAgreeProperty.setLayoutData(dataInfoField);

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
			lblSelectionField.setText("Preserve Guarantees from " + strSourceName);
			lblSelectionField.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

			GridData selectionFieldLayoutData = new GridData(SWT.FILL, SWT.FILL, true, false);

			Composite selectionField = new Composite(container, SWT.BORDER);
			GridLayout layout = new GridLayout(1, true);
			selectionField.setLayout(layout);
			selectionField.setLayoutData(selectionFieldLayoutData);

			btnPropagateGuarantees.clear();
			for (String guarantee : strSourceGuarantees) {
				Button selectGuarantee = new Button(selectionField, SWT.CHECK);
				String formattedGuarantee = guarantee.trim();
				formattedGuarantee = formattedGuarantee
						.substring("guarantee ".length(),
								formattedGuarantee.lastIndexOf(":") - 1);
				selectGuarantee.setText(formattedGuarantee);
				selectGuarantee.setSelection(true);
				selectGuarantee.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
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
		strFilterImplementationLanguage = txtFilterImplementationLanguage.getText();
		strFilterImplementationName = txtFilterImplementationName.getText();
		strFilterRequirement = cboFilterRequirement.getText();
		strAgreeProperty = txtAgreeProperty.getText();
		strPropagateGuarantees.clear();
		for (int i = 0; i < btnPropagateGuarantees.size(); i++) {
			if (btnPropagateGuarantees.get(i).getSelection()) {

				// TODO: Maybe look into passing actual guarantees instead of strings, that way we can parse/unparse it
//				AgreeAnnexParser parser = new AgreeAnnexParser();
//				NamedSpecStatement nss = parser.parseNamedSpecStatement(strSourceGuarantees.get(i));
//				AgreeAnnexUnparser unparser = new AgreeAnnexUnparser();
//				String expr = unparser.unparseExpr(nss.getExpr(), "").trim();
//				String desc = nss.getStr().trim();
//				String id = nss.getName();
				// Parse the guarantee (for now do it the old fashioned way)
				String guarantee = strSourceGuarantees.get(i);
				String expr = guarantee.substring(guarantee.lastIndexOf(":") + 1, guarantee.lastIndexOf(";")).trim();
				String desc = guarantee.substring(guarantee.indexOf("\""), guarantee.lastIndexOf("\"") + 1).trim();
				String id = guarantee.substring(guarantee.toLowerCase().indexOf("guarantee ") + "guarantee ".length(),
						guarantee.indexOf("\"")).trim();

				// If guarantee has an ID, append a suffix to maintain ID uniqueness
				if (id.length() > 0) {
					id = id.concat("_Filter");
				} else {
					id = "Filter";
				}
				guarantee = "guarantee " + id + " " + desc + " : " + expr + ";";
				strPropagateGuarantees.add(guarantee);
			}
		}
	}

	@Override
	protected void okPressed() {
		saveInput();
		super.okPressed();
	}

	public String getFilterImplementationName() {
		return strFilterImplementationName;
	}

	public String getFilterImplementationLanguage() {
		return strFilterImplementationLanguage;
	}

	public String getAgreeProperty() {
		return strAgreeProperty;
	}

	public List<String> getGuaranteeList() {
		return strPropagateGuarantees;
	}

	public String getRequirement() {
		return strFilterRequirement;
	}

	public void setGuaranteeList(String sourceName, List<String> guarantees) {
		strSourceName = sourceName;
		strSourceGuarantees = guarantees;
	}

	public void setRequirements(List<String> requirements) {
		strRequirements = requirements;
	}

}
