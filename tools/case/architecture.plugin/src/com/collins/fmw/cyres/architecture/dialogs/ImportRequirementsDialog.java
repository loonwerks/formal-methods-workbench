package com.collins.fmw.cyres.architecture.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
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
import com.collins.fmw.cyres.architecture.requirements.JsonRequirementsFile.JsonRequirement;

public class ImportRequirementsDialog extends TitleAreaDialog {

	List<Button> btnReqs = new ArrayList<>();
	List<String> lblReqTexts = new ArrayList<>();
	List<Text> txtIDs = new ArrayList<>();
	List<String> lblComponents = new ArrayList<>();
	List<Button> btnAgreeProps = new ArrayList<>();
	List<Text> txtRationales = new ArrayList<>();

	List<CyberRequirement> importedRequirements = new ArrayList<>();
	List<CyberRequirement> omittedRequirements = new ArrayList<>();
	List<JsonRequirement> newRequirements = null;
//	List<CyberRequirement> newRequirements = null;

	public ImportRequirementsDialog(Shell parentShell) {
		super(parentShell);
		setHelpAvailable(false);
	}

	@Override
	public void create() {
		super.create();
		setTitle("Import Cybersecurity Requirements");
		setMessage("Select the cybersecurity requirements to be imported into the model.", IMessageProvider.NONE);
	}


	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		GridLayout layout = new GridLayout(6, false);
		container.setLayout(layout);

		// Add filter information fields
		createRequirementsSelectionField(container);

		return area;
	}

	private void createRequirementsSelectionField(Composite container) {

		Label lblHeaderField = new Label(container, SWT.NONE);
		FontData fontData = lblHeaderField.getFont().getFontData()[0];
		Font font = new Font(container.getDisplay(), new FontData(fontData.getName(), fontData.getHeight(), SWT.BOLD));
		lblHeaderField.setFont(font);
		lblHeaderField.setText("Requirement");
		lblHeaderField = new Label(container, SWT.NONE);
		lblHeaderField.setText("ID");
		lblHeaderField.setFont(font);
		lblHeaderField = new Label(container, SWT.NONE);
		lblHeaderField.setText("Text");
		lblHeaderField.setFont(font);
		lblHeaderField = new Label(container, SWT.NONE);
		lblHeaderField.setText("Component");
		lblHeaderField.setFont(font);
		lblHeaderField = new Label(container, SWT.WRAP);
		lblHeaderField.setText("AGREE Property");
		lblHeaderField.setFont(font);
		lblHeaderField = new Label(container, SWT.WRAP);
		lblHeaderField.setText("Rationale for Omission");
		lblHeaderField.setFont(font);

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

		// Add requirements
//		for (CyberRequirement req : newRequirements) {
		for (JsonRequirement req : newRequirements) {
			addRequirement(req, container);
		}

	}

//	private void addRequirement(CyberRequirement req, Composite container) {
	private void addRequirement(JsonRequirement req, Composite container) {

		Button btnReq = new Button(container, SWT.CHECK);
		btnReq.setText(req.getType());
		btnReq.setSelection(false);
		btnReq.addListener(SWT.Selection, e -> {
			if (e.type == SWT.Selection) {
				enableRationaleFields();
			}
		});
		btnReqs.add(btnReq);

		Text txtID = new Text(container, SWT.BORDER);
		GridData reqIDInfoField = new GridData(SWT.FILL, SWT.FILL, true, false);
		txtID.setLayoutData(reqIDInfoField);
		txtIDs.add(txtID);

		Label lblReqText = new Label(container, SWT.NONE);
		lblReqText.setText(req.getText());
		lblReqTexts.add(req.getText());

		Label lblCompName = new Label(container, SWT.NONE);
		lblCompName.setText(req.getContext());
		lblComponents.add(req.getContext());

		Button btnAgree = new Button(container, SWT.CHECK);
		GridData agreeInfoField = new GridData(SWT.CENTER, SWT.FILL, true, false);
		btnAgree.setLayoutData(agreeInfoField);
		btnAgree.setText("");
		btnAgree.setSelection(false);
		btnAgreeProps.add(btnAgree);

		Text txtRationale = new Text(container, SWT.BORDER);
		GridData dataInfoField = new GridData(SWT.FILL, SWT.FILL, true, false);
		txtRationale.setLayoutData(dataInfoField);
		txtRationales.add(txtRationale);

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

	}

	private void enableRationaleFields() {

		for (int i = 0; i < btnReqs.size(); i++) {
			txtRationales.get(i).setEnabled(!btnReqs.get(i).getSelection());
		}

	}

	/**
	 * Saves information entered into the text fields.  This is needed because the
	 * text fields are disposed when the dialog closes.
	 * @param container
	 */
	private boolean saveInput() {

		importedRequirements.clear();
		omittedRequirements.clear();
		for (int i = 0; i < btnReqs.size(); i++) {

			Classifier contextClassifier = CyberRequirement.getClassifier(lblComponents.get(i));
			if (contextClassifier == null) {
				Dialog.showError("Unknown context for " + btnReqs.get(i).getText(), lblComponents.get(i)
						+ " could not be found in any AADL file in the project. A requirement context must be valid in order to import requirements into model.  This requirement will be de-selected.");
				// Uncheck this requirement
				btnReqs.get(i).setSelection(false);
				return false;
			}
			if (btnReqs.get(i).getSelection()) {
				if (txtIDs.get(i).getText().isEmpty()) {
					Dialog.showError("Missing requirement ID", btnReqs.get(i).getText()
							+ " is missing a requirement ID. Requirement IDs must be assigned before requirements can be imported into model.  This requirement will be de-selected.");
					// Uncheck this requirement
					btnReqs.get(i).setSelection(false);
					return false;
				}
//				else {
//					// Create a unique requirement ID by concatenating the requirement type with the user-specified ID
//					txtIDs.get(i).setText(btnReqs.get(i).getText() + "_" + txtIDs.get(i).getText());
//				}
				importedRequirements.add(new CyberRequirement(btnReqs.get(i).getText(), txtIDs.get(i).getText(),
						lblReqTexts.get(i), contextClassifier, btnAgreeProps.get(i).getSelection(), ""));
			} else {
				omittedRequirements
						.add(new CyberRequirement(btnReqs.get(i).getText(), txtIDs.get(i).getText(), lblReqTexts.get(i),
								contextClassifier, btnAgreeProps.get(i).getSelection(),
								txtRationales.get(i).getText()));
			}
		}
		return true;
	}

//	private Classifier getClassifier(String qualifiedName) {
//		Classifier classifier = null;
//		if (!qualifiedName.contains("::")) {
//			return null;
//		}
//		String pkgName = Aadl2Util.getPackageName(qualifiedName);
//
//		for (AadlPackage pkg : TraverseProject.getPackagesInProject(TraverseProject.getCurrentProject())) {
//			if (pkg.getName().equalsIgnoreCase(pkgName)) {
//				for (Classifier c : EcoreUtil2.getAllContentsOfType(pkg, Classifier.class)) {
//					if (c.getQualifiedName().equalsIgnoreCase(qualifiedName)) {
//						classifier = c;
//						break;
//					}
//				}
//				break;
//			}
//		}
//
//		return classifier;
//	}

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


//	public void setRequirements(List<CyberRequirement> requirements) {
	public void setRequirements(List<JsonRequirement> requirements) {
		this.newRequirements = requirements;
	}

}
