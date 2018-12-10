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

//	private Combo cboFilterComponentType;
	private Text txtFilterImplementationName;
	private Text txtFilterImplementationLanguage;
	private Combo cboFilterResoluteClause;
	private Text txtAgreeProperty;
	private List<Button> btnPropagateGuarantees = new ArrayList<>();
//	private String strFilterComponentType = "";
	private String strFilterImplementationLanguage = "";
	private String strFilterImplementationName = "";
	private String strFilterResoluteClause = "";
	private String strAgreeProperty = "";
	private String strSourceName = "";
//	private String strRecommendedType = "";
//	private String strParentType = "";
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
//		createFilterComponentTypeField(container);
		createFilterImplementationNameField(container);
		createImplementationLanguageField(container);
		createResoluteField(container);
		createGuaranteeSelectionField(container);
		createAgreeField(container);

		return area;
	}

//	/**
//	 * Creates the input drop-down field for specifying the filter component type
//	 * @param container
//	 */
//	private void createFilterComponentTypeField(Composite container) {
//		Label lblFilterComponentType = new Label(container, SWT.NONE);
//		lblFilterComponentType.setText("Filter Component Type");
//
//		GridData dataInfoField = new GridData();
//		dataInfoField.grabExcessHorizontalSpace = true;
//		dataInfoField.horizontalAlignment = GridData.FILL;
//		cboFilterComponentType = new Combo(container, SWT.BORDER | SWT.READ_ONLY);
//		cboFilterComponentType.setLayoutData(dataInfoField);
//
//		if (strParentType.isEmpty()) {
//			cboFilterComponentType.add("abstract");
//			cboFilterComponentType.setText("abstract");
//		}
//		else {
//
//			switch (strParentType) {
//			case "system":
//				cboFilterComponentType.add("system");
//				cboFilterComponentType.add("process");
//				cboFilterComponentType.add("device");
//				cboFilterComponentType.add("abstract");
//				break;
//			case "process":
//				cboFilterComponentType.add("thread");
//				cboFilterComponentType.add("abstract");
//				break;
//			case "thread":
//				cboFilterComponentType.add("abstract");
//				break;
//			case "device":
//				cboFilterComponentType.add("abstract");
//				break;
//			case "abstract":
//				cboFilterComponentType.add("system");
//				cboFilterComponentType.add("process");
//				cboFilterComponentType.add("thread");
//				cboFilterComponentType.add("device");
//				cboFilterComponentType.add("abstract");
//			}
//
//			// Set default value
//			if (!strRecommendedType.isEmpty()) {
//				cboFilterComponentType.setText(strRecommendedType);
//			}
//
//		}
//
//	}


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
	 * Creates the input text field for specifying the filter agree property
	 * @param container
	 */
	private void createAgreeField(Composite container) {
		Label lblAgreeField = new Label(container, SWT.NONE);
		lblAgreeField.setText("Filter AGREE Contract");
//		lblAgreeField.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
//
//		txtAgreeProperty = new Text(container, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
//
//		GridData dataInfoField = new GridData(SWT.FILL, SWT.TOP, true, false);
//		dataInfoField.heightHint = 3 * txtAgreeProperty.getLineHeight();
//
//		txtAgreeProperty.setLayoutData(dataInfoField);

		GridData dataInfoField = new GridData(SWT.FILL, SWT.FILL, true, false);
//		dataInfoField.grabExcessHorizontalSpace = true;
//		dataInfoField.horizontalAlignment = SWT.FILL;
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
								formattedGuarantee.lastIndexOf(":") - 1);
//								formattedGuarantee.length() - 1);
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
//		strFilterComponentType = cboFilterComponentType.getText();
		strFilterImplementationLanguage = txtFilterImplementationLanguage.getText();
		strFilterImplementationName = txtFilterImplementationName.getText();
		strFilterResoluteClause = cboFilterResoluteClause.getText();
		strAgreeProperty = txtAgreeProperty.getText();
		strPropagateGuarantees.clear();
		for (int i = 0; i < btnPropagateGuarantees.size(); i++) {
			if (btnPropagateGuarantees.get(i).getSelection()) {

				// Parse the guarantee (for now do it the old fashioned way)
				String guarantee = strSourceGuarantees.get(i);
				String expr = guarantee.substring(guarantee.lastIndexOf(":") + 1, guarantee.lastIndexOf(";"))
						.trim();
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

//	public void setFilterComponentTypeInfo(String recommendedType, String parentType) {
//		strRecommendedType = recommendedType;
//		strParentType = parentType;
//	}

//	public String getFilterComponentType() {
//		return strFilterComponentType;
//	}

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
