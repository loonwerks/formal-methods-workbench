package com.collins.fmw.cyres.architecture.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
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
import org.osate.aadl2.ComponentImplementation;
import org.osate.aadl2.ComponentType;
import org.osate.aadl2.DataPort;
import org.osate.aadl2.EventDataPort;
import org.osate.aadl2.EventPort;
import org.osate.aadl2.Feature;
import org.osate.aadl2.PortCategory;
import org.osate.aadl2.PropertyExpression;
import org.osate.aadl2.StringLiteral;
import org.osate.aadl2.Subcomponent;

import com.collins.fmw.cyres.architecture.handlers.AddFilterHandler;
import com.collins.fmw.cyres.architecture.utils.CaseUtils;

/**
 * This class creates the Add Filter wizard
 */
public class AddFilterDialog extends TitleAreaDialog {

	private Subcomponent compoundFilter = null;
	private Text txtFilterImplementationName;
	private Text txtFilterImplementationLanguage;
	private List<Button> btnLogPortType = new ArrayList<>();
	private Combo cboFilterRequirement;
	private Text txtAgreeProperty;
	private List<Button> btnPropagateGuarantees = new ArrayList<>();
	private String filterImplementationLanguage = "";
	private String filterImplementationName = "";
	private PortCategory logPortType = null;
	private String filterRequirement = "";
	private String agreeProperty = "";
	private String sourceName = "";
	private List<String> sourceGuarantees = new ArrayList<>();
	private List<String> propagateGuarantees = new ArrayList<>();
	private List<String> requirements = new ArrayList<>();

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
		createFilterImplementationNameField(container);
		createImplementationLanguageField(container);
		createLogPortField(container);

		createRequirementField(container);
		if (compoundFilter == null) {
			// Don't show the propagate guarantees field if we're adding a spec to an existing filter.
			// Too complicated to figure out at this time
			createGuaranteeSelectionField(container);
		}
		createAgreeField(container);

		return area;
	}

	/**
	 * Creates the input text field for specifying the filter implementation name
	 * @param container
	 */
	private void createFilterImplementationNameField(Composite container) {
		Label lblFilterImplNameField = new Label(container, SWT.NONE);
		lblFilterImplNameField.setText("Filter implementation name");

		GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = SWT.FILL;
		txtFilterImplementationName = new Text(container, SWT.BORDER);
		txtFilterImplementationName.setLayoutData(dataInfoField);
		if (compoundFilter == null) {
			txtFilterImplementationName.setText(AddFilterHandler.FILTER_IMPL_NAME);
		} else {
			txtFilterImplementationName.setText(compoundFilter.getName());
			txtFilterImplementationName.setEnabled(false);
		}
	}

	/**
	 * Creates the input text field for specifying the filter implementation language
	 * @param container
	 */
	private void createImplementationLanguageField(Composite container) {
		Label lblImplLangField = new Label(container, SWT.NONE);
		lblImplLangField.setText("Filter implementation language");

		GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = SWT.FILL;
		txtFilterImplementationLanguage = new Text(container, SWT.BORDER);
		txtFilterImplementationLanguage.setLayoutData(dataInfoField);
		if (compoundFilter != null) {
			ComponentImplementation ci = compoundFilter.getComponentImplementation();
			EList<PropertyExpression> propVals = ci.getPropertyValues(CaseUtils.CASE_PROPSET_NAME, "COMP_IMPL");
			if (!propVals.isEmpty()) {
				// There should be only one property value
				PropertyExpression expr = propVals.get(0);
				if (expr instanceof StringLiteral) {
					txtFilterImplementationLanguage.setText(((StringLiteral) expr).getValue());
				}
			}
			txtFilterImplementationLanguage.setEnabled(false);
		}
	}

	/**
	 * Creates the input field for specifying if the filter should contain
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

		if (compoundFilter != null) {
			ComponentType ct = compoundFilter.getComponentType();
			for (Feature f : ct.getOwnedFeatures()) {
				if (f.getName().equalsIgnoreCase(AddFilterHandler.FILTER_LOG_PORT_NAME)) {
					btnNoLogPort.setSelection(false);
					if (f instanceof DataPort) {
						btnDataLogPort.setSelection(true);
					} else if (f instanceof EventPort) {
						btnEventLogPort.setSelection(true);
					} else if (f instanceof EventDataPort) {
						btnEventDataLogPort.setSelection(true);
					}
					break;
				}
			}
			btnDataLogPort.setEnabled(false);
			btnEventLogPort.setEnabled(false);
			btnEventDataLogPort.setEnabled(false);
			btnNoLogPort.setEnabled(false);
		}

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
		cboFilterRequirement = new Combo(container, SWT.BORDER | SWT.READ_ONLY);
		cboFilterRequirement.setLayoutData(dataInfoField);

		requirements.forEach(r -> cboFilterRequirement.add(r));

	}

	/**
	 * Creates the input text field for specifying the filter agree property
	 * @param container
	 */
	private void createAgreeField(Composite container) {
		Label lblAgreeField = new Label(container, SWT.NONE);
		lblAgreeField.setText("Filter AGREE contract");

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
		if (sourceGuarantees.size() > 0) {

			Label lblSelectionField = new Label(container, SWT.NONE);
			lblSelectionField.setText("Preserve Guarantees from " + sourceName);
			lblSelectionField.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

			GridData selectionFieldLayoutData = new GridData(SWT.FILL, SWT.FILL, true, false);

			Composite selectionField = new Composite(container, SWT.BORDER);
			GridLayout layout = new GridLayout(1, true);
			selectionField.setLayout(layout);
			selectionField.setLayoutData(selectionFieldLayoutData);

			btnPropagateGuarantees.clear();
			for (String guarantee : sourceGuarantees) {
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
		filterImplementationLanguage = txtFilterImplementationLanguage.getText();
		filterImplementationName = txtFilterImplementationName.getText();
		logPortType = null;
		for (int i = 0; i < btnLogPortType.size(); i++) {
			if (btnLogPortType.get(i).getSelection()) {
				logPortType = PortCategory.get(i);
				break;
			}
		}

		filterRequirement = cboFilterRequirement.getText();
		agreeProperty = txtAgreeProperty.getText();
		propagateGuarantees.clear();
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
				String guarantee = sourceGuarantees.get(i);
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
				propagateGuarantees.add(guarantee);
			}
		}
	}

	@Override
	protected void okPressed() {
		saveInput();
		super.okPressed();
	}

	public String getFilterImplementationName() {
		return filterImplementationName;
	}

	public String getFilterImplementationLanguage() {
		return filterImplementationLanguage;
	}

	public PortCategory getLogPortType() {
		return logPortType;
	}

	public String getAgreeProperty() {
		return agreeProperty;
	}

	public List<String> getGuaranteeList() {
		return propagateGuarantees;
	}

	public String getRequirement() {
		return filterRequirement;
	}

	public void setGuaranteeList(String sourceName, List<String> guarantees) {
		this.sourceName = sourceName;
		this.sourceGuarantees = guarantees;
	}

	public void setRequirements(List<String> requirements) {
		this.requirements = requirements;
	}

	public void createCompoundFilter(Subcomponent comp) {
		this.compoundFilter = comp;
	}

}