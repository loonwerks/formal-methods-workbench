package com.collins.fmw.cyres.architecture.dialogs;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.osate.aadl2.Classifier;
import org.osate.ui.dialogs.Dialog;

import com.collins.fmw.cyres.architecture.requirements.CyberRequirement;

public class ImportRequirementsDialog extends TitleAreaDialog {

	List<Button> btnReqs = new ArrayList<>();
	List<Label> lblReqTypes = new ArrayList<>();
	List<Label> lblReqTexts = new ArrayList<>();
	List<Text> txtIDs = new ArrayList<>();
	List<Label> lblComponents = new ArrayList<>();
	List<Button> btnAgreeProps = new ArrayList<>();
	List<Text> txtRationales = new ArrayList<>();

	final int nSortableColums = 7;
	List<Boolean> sortOrder = null;

	List<CyberRequirement> importedRequirements = new ArrayList<>();
	List<CyberRequirement> omittedRequirements = new ArrayList<>();
	List<CyberRequirement> newRequirements = null;

	public ImportRequirementsDialog(Shell parentShell) {
		super(parentShell);
		setHelpAvailable(false);
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}

	@Override
	public void create() {
		super.create();
		setTitle("Import Cybersecurity Requirements");
		setMessage("Select the cybersecurity requirements to be imported into the model.", IMessageProvider.NONE);
	}

//	Saved -- this works, gives a scrollable composite
//	@Override
//	protected Control createDialogArea(Composite parent) {
//		Composite area = (Composite) super.createDialogArea(parent);
//		area.setLayout(new GridLayout(1, false));
//
//		final ScrolledComposite scrolledContainer = new ScrolledComposite(area,
//				SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
//		final Composite container = new Composite(scrolledContainer, SWT.NONE);
//
//		scrolledContainer.setLayoutData(new GridData(SWT.NONE, SWT.NONE, true, true));
//		scrolledContainer.setLayout(new GridLayout());
//		container.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
//		GridLayout layout = new GridLayout(6, false);
//		container.setLayout(layout);
//
//		// Add filter information fields
//		createRequirementsSelectionField(container);
//
//		scrolledContainer.setContent(container);
//		// scrolledContainer.setExpandHorizontal(true);
//		// scrolledContainer.setExpandVertical(true);
//		container.setSize(container.computeSize(SWT.DEFAULT, SWT.DEFAULT));
//		// scrolledContainer.setMinSize(container.computeSize(SWT.DEFAULT, SWT.DEFAULT));
//
//		// return area;
//		return parent;
//	}

	@Override
	protected Control createDialogArea(Composite parent) {
		// Composite area = (Composite) super.createDialogArea(parent);
		// Composite area = parent.getShell();
		Composite area = parent;

		final ScrolledComposite scrolledContainer = new ScrolledComposite(area,
				SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.RESIZE);
		final Composite container = new Composite(scrolledContainer, SWT.NONE);
		scrolledContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		scrolledContainer.setLayout(new FillLayout()); // not necessary for Win 10

		// scrolledContainer.setLayout(new GridLayout());
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout layout = new GridLayout(7, false);
		container.setLayout(layout);

		// Add filter information fields
		createRequirementsSelectionField(container);
		container.pack();

		scrolledContainer.setExpandHorizontal(true);
		scrolledContainer.setExpandVertical(true);
		scrolledContainer.setContent(container);
		scrolledContainer.setMinSize(container.getSize());

		return parent;
	}

	private void createRequirementsSelectionField(Composite container) {

		if (container.getLayout() instanceof GridLayout) {
			GridLayout gridLayout = (GridLayout) container.getLayout();
			gridLayout.horizontalSpacing += 10;
		}

		String[] label = { "Enable", "Requirement Type", "ID", "Description", "Component", "Formalizable?",
				"Rationale for Omission" };
		String[] description = { "Sort requirements by enabled/disabled.", "Sort requirements by requirement type.",
				"Sort requirements by requirement ID.", "Sort requirements by requirement description.",
				"Sort requirements by component name.", "Sort requirements by formalizable or not.",
				"Sort requirements by reason for omission." };

		assert (label.length == description.length);

		for (int i = 0; i < label.length; i++) {
			final int j = i;
			Button btn = new Button(container, SWT.NONE);
			btn.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			btn.setAlignment(SWT.CENTER);
			btn.setText(label[j]);
			btn.setToolTipText(description[j]);
			btn.addListener(SWT.Selection, e -> sortRequirements(j));
		}



//		Label separator = new Label(container, SWT.HORIZONTAL | SWT.SEPARATOR);
//		final GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
//		gridData.horizontalSpan = 7;
//		separator.setLayoutData(gridData);
		Label separator = new Label(container, SWT.HORIZONTAL | SWT.SEPARATOR);
		separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		separator = new Label(container, SWT.HORIZONTAL | SWT.SEPARATOR);
		separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		separator = new Label(container, SWT.HORIZONTAL | SWT.SEPARATOR);
		separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		separator = new Label(container, SWT.HORIZONTAL | SWT.SEPARATOR);
		separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		separator = new Label(container, SWT.HORIZONTAL | SWT.SEPARATOR);
		separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		separator = new Label(container, SWT.HORIZONTAL | SWT.SEPARATOR);
		separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		separator = new Label(container, SWT.HORIZONTAL | SWT.SEPARATOR);
		separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		if (container.getLayout() instanceof GridLayout) {
			((GridLayout) container.getLayout()).verticalSpacing += 5;
		}

		// Add requirements
		for (CyberRequirement req : newRequirements) {
			addRequirement(req, container);
		}
	}

	private void addRequirement(CyberRequirement req, Composite container) {

		Button btnReq = new Button(container, SWT.CHECK);
		btnReq.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, false, false));
		btnReq.setText("");
		btnReq.setSelection(false);
		btnReq.setToolTipText("Check this box to import this requirement.");
//		btnReq.addListener(SWT.Selection, e -> {
//			if (e.type == SWT.Selection) {
//				enableRationaleFields();
//			}
//		});
		btnReqs.add(btnReq);

		Label lblReqType = new Label(container, SWT.NONE);
		lblReqType.setText(req.getType());
		lblReqType.setToolTipText("Class or Type of cyber requirement.");
		// lblReqTypes.add(lblReqType.getText());
		lblReqTypes.add(lblReqType);

		Text txtID = new Text(container, SWT.BORDER);
		GridData reqIDInfoField = new GridData(SWT.FILL, SWT.FILL, true, false);
		txtID.setLayoutData(reqIDInfoField);
		txtID.setToolTipText("Specify a unique identifier for this requirement.");
		txtIDs.add(txtID);

//		Label lblReqText = new Label(container, SWT.WRAP);
		Label lblReqText = new Label(container, SWT.NONE);
//		Text lblReqText = new Text(container, SWT.MULTI | SWT.WRAP | SWT.READ_ONLY | SWT.V_SCROLL);
		lblReqText.setText(req.getText());
		lblReqText.setToolTipText("Requirement description.");
		// lblReqTexts.add(req.getText());
		lblReqTexts.add(lblReqText);

		Label lblCompName = new Label(container, SWT.NONE);
		lblCompName.setText(req.getContext());
		lblCompName.setToolTipText("Component that this requirement pertains to.");
		// lblComponents.add(req.getContext());
		lblComponents.add(lblCompName);

		Button btnAgree = new Button(container, SWT.CHECK);
		GridData agreeInfoField = new GridData(SWT.CENTER, SWT.FILL, false, false);
		btnAgree.setLayoutData(agreeInfoField);
		btnAgree.setText("");
		btnAgree.setSelection(false);
		btnAgree.setToolTipText(
				"Check this box to add an skeleton AGREE property to this component. You must complete the formalization of this requirement once it is imported.");
		btnAgreeProps.add(btnAgree);

		Text txtRationale = new Text(container, SWT.BORDER);
		GridData dataInfoField = new GridData(SWT.FILL, SWT.FILL, true, false);
		txtRationale.setLayoutData(dataInfoField);
		txtRationale.setToolTipText("Reason for not importing this requirement into the model.");
		txtRationales.add(txtRationale);

		txtID.setEnabled(false);
		btnAgree.setEnabled(false);
		txtRationale.setEnabled(true);

		btnReq.addListener(SWT.Selection, e -> {
			if (e.type == SWT.Selection) {
				boolean btn = btnReq.getSelection();
				txtID.setEnabled(btn);
				btnAgree.setEnabled(btn);
				txtRationale.setEnabled(!btn);
			}
		});

//		Label separator = new Label(container, SWT.HORIZONTAL | SWT.SEPARATOR);
//		final GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
//		gridData.horizontalSpan = 6;
//		separator.setLayoutData(gridData);

//		separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//		separator = new Label(container, SWT.HORIZONTAL | SWT.SEPARATOR);
//		separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//		separator = new Label(container, SWT.HORIZONTAL | SWT.SEPARATOR);
//		separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//		separator = new Label(container, SWT.HORIZONTAL | SWT.SEPARATOR);
//		separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//		separator = new Label(container, SWT.HORIZONTAL | SWT.SEPARATOR);
//		separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//		separator = new Label(container, SWT.HORIZONTAL | SWT.SEPARATOR);
//		separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

	}

//	private void enableRationaleFields() {
//
//		for (int i = 0; i < btnReqs.size(); i++) {
//			txtRationales.get(i).setEnabled(!btnReqs.get(i).getSelection());
//		}
//
//	}


	/**
	 * Saves information entered into the text fields.  This is needed because the
	 * text fields are disposed when the dialog closes.
	 * @param container
	 */
	private boolean saveInput() {
		// New version of saveInput():
		// maintains references to the layout components, and
		// uses them to populate the requirements fields.
		importedRequirements.clear();
		omittedRequirements.clear();
		for (int i = 0; i < btnReqs.size(); i++) {

			// Find the context (component, connection, etc) in the model
			Classifier contextClassifier = CyberRequirement.getImplementationClassifier(lblComponents.get(i).getText());
			if (contextClassifier == null && btnReqs.get(i).getSelection()) {
				Dialog.showError("Unknown context for " + lblReqTypes.get(i), lblComponents.get(i)
						+ " could not be found in any AADL file in the project. A requirement context must be valid in order to import requirements into model.  This requirement will be de-selected.");
				// Uncheck this requirement
				btnReqs.get(i).setSelection(false);
				return false;
			}
			if (btnReqs.get(i).getSelection()) {
				if (txtIDs.get(i).getText().isEmpty()) {
					Dialog.showError("Missing requirement ID", lblReqTypes.get(i)
							+ " is missing a requirement ID. Requirement IDs must be assigned before requirements can be imported into model.  This requirement will be de-selected.");
					// Uncheck this requirement
					btnReqs.get(i).setSelection(false);
					return false;
				}

//				importedRequirements.add(new CyberRequirement(lblReqTypes.get(i).getText(), txtIDs.get(i).getText(),
//						lblReqTexts.get(i).getText(), lblComponents.get(i).getText(),
//						btnAgreeProps.get(i).getSelection(), ""));
				importedRequirements.add(new CyberRequirement(0, "", CyberRequirement.add, lblReqTypes.get(i).getText(),
						txtIDs.get(i).getText(), lblReqTexts.get(i).getText(), lblComponents.get(i).getText(), ""));
			} else {
				ArrayList<String> context = new ArrayList<String>();
				for (String s : lblComponents.get(i).getText().split(" ")) {
					context.add(s);
				}

//				omittedRequirements
//						.add(new CyberRequirement(lblReqTypes.get(i).getText(), txtIDs.get(i).getText(),
//								lblReqTexts.get(i).getText(), lblComponents.get(i).getText(),
//								btnAgreeProps.get(i).getSelection(),
//								txtRationales.get(i).getText()));
				omittedRequirements.add(new CyberRequirement(0, "", CyberRequirement.add, lblReqTypes.get(i).getText(),
						txtIDs.get(i).getText(), lblReqTexts.get(i).getText(), lblComponents.get(i).getText(),
						txtRationales.get(i).getText()));

			}
		}
		return true;
	}

	// Sorts the requirements (in-place) based on the column index.
	// 0: check
	// 1: type
	// 2: id
	// 3: desc
	// 4: component
	// 5: agree
	// 6: reason
	private void sortRequirements(int column) {
		// TODO: only sorting by column 4 (component) here.

		Comparator<Requirement> byCol = null;
		switch (column) {
		case 0: // check
			byCol = (Requirement r1, Requirement r2) -> Boolean.compare(r1.check, r2.check);
			break;
		case 1: // type
			byCol = (Requirement r1, Requirement r2) -> r1.type.compareTo(r2.type);
			break;
		case 2: // id
			byCol = (Requirement r1, Requirement r2) -> r1.id.compareTo(r2.id);
			break;
		case 3: // desc
			byCol = (Requirement r1, Requirement r2) -> r1.desc.compareTo(r2.desc);
			break;
		case 4: // component
			byCol = (Requirement r1, Requirement r2) -> r1.component.compareTo(r2.component);
			break;
		case 5: // agree
			byCol = (Requirement r1, Requirement r2) -> Boolean.compare(r1.agree, r2.agree);
			break;
		case 6: // reason
			byCol = (Requirement r1, Requirement r2) -> r1.reason.compareTo(r2.reason);
			break;
		default:
			break;
		}

		if (sortOrder == null) {
			sortOrder = new ArrayList<Boolean>();
			for (int i = 0; i < nSortableColums; i++) {
				sortOrder.add(false);
			}
		}

		if (byCol != null) {
			List<Requirement> sorter = new ArrayList<Requirement>();
			for (int i = 0; i < btnReqs.size(); i++) {
				sorter.add(new Requirement(btnReqs.get(i).getSelection(), lblReqTypes.get(i).getText(),
						txtIDs.get(i).getText(), lblReqTexts.get(i).getText(), lblComponents.get(i).getText(),
						btnAgreeProps.get(i).getSelection(), txtRationales.get(i).getText()));
				// System.out.println(i + ": " + sorter.get(i));
			}
			if (sortOrder.get(column)) {
				sorter.sort(byCol);
				sortOrder.set(column, false);
			} else {
				sorter.sort(byCol.reversed());
				sortOrder.set(column, true);
			}
			for (int i = 0; i < sorter.size(); i++) {
				Requirement req = sorter.get(i);
				btnReqs.get(i).setSelection(req.check);
				lblReqTypes.get(i).setText(req.type);
				txtIDs.get(i).setText(req.id);
				lblReqTexts.get(i).setText(req.desc);
				lblComponents.get(i).setText(req.component);
				btnAgreeProps.get(i).setSelection(req.agree);
				txtRationales.get(i).setText(req.reason);
				boolean btn = req.check;
				txtIDs.get(i).setEnabled(btn);
				btnAgreeProps.get(i).setEnabled(btn);
				txtRationales.get(i).setEnabled(!btn);
				// System.out.println(i + ": " + sorter.get(i));
			}
		}
	}

	@Override
	protected void okPressed() {
		if (saveInput()) {
			super.okPressed();
		}
	}

	public List<CyberRequirement> getImportedRequirements() {
		return importedRequirements;
	}

	public List<CyberRequirement> getOmittedRequirements() {
		return omittedRequirements;
	}

	public void setRequirements(List<CyberRequirement> requirements) {
		this.newRequirements = requirements;
	}
}



class Requirement {
	boolean check;
	String type;
	String id;
	String desc;
	String component;
	boolean agree;
	String reason;

	public Requirement(boolean check, String type, String id, String desc, String component, boolean agree,
			String reason) {
		this.check = check;
		this.type = type;
		this.id = id;
		this.desc = desc;
		this.component = component;
		this.agree = agree;
		this.reason = reason;
	}

	@Override
	public String toString() {
		return this.check + "-" + this.type + "-" + this.id + "-" + this.desc + "-" + this.component + "-" + this.agree
				+ "-" + this.reason;
	}
}
