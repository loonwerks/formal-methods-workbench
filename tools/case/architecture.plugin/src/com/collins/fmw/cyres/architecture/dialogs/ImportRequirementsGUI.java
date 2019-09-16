package com.collins.fmw.cyres.architecture.dialogs;


import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;
import org.osate.aadl2.Classifier;

import com.collins.fmw.cyres.architecture.requirements.CyberRequirement;

public class ImportRequirementsGUI extends Dialog {

	private int returnCode = SWT.OK;

	final private List<CyberRequirement> requirements = new ArrayList<CyberRequirement>();

	final Shell shlReqManager;
	final Table tblReqBrowser;
	final Combo cmbStatus;
	final Text txtID;
	final Text txtDesc;
	final Text textReason;
	final Label lblContext2;
	final Label lblGenTool2;
	final Label lblType2;

	final List<String> status = Arrays.asList(CyberRequirement.toDo, CyberRequirement.add,
			CyberRequirement.addPlusAgree,
			CyberRequirement.omit);

	private int oldIndex = -1;

	public ImportRequirementsGUI(Shell parent) {
		this(parent, 0); // your default style bits go here (not the Shell's style bits)
	}

	public ImportRequirementsGUI(Shell parent, int style) {
		super(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);

		shlReqManager = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.APPLICATION_MODAL);

		shlReqManager.setMinimumSize(new Point(1200, 400));
		shlReqManager.setSize(1200, 450);
		shlReqManager.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		shlReqManager.setText("Import Cyber Security Requirements");
		shlReqManager.setLayout(new GridLayout(1, true));

		Composite cmpReqManager = new Composite(shlReqManager, SWT.NONE);
		cmpReqManager.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		cmpReqManager.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		cmpReqManager.setLayout(new FillLayout(SWT.HORIZONTAL));
		Group grpReqBrowser = new Group(cmpReqManager, SWT.NONE);
		grpReqBrowser.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		grpReqBrowser.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		grpReqBrowser.setText("Requirements Browser");
		FillLayout fl_grpReqBrowser = new FillLayout(SWT.HORIZONTAL);
		// fl_grpReqBrowser.marginHeight = 10;
		grpReqBrowser.setLayout(fl_grpReqBrowser);

		ScrolledComposite reqBrowser = new ScrolledComposite(grpReqBrowser, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		reqBrowser.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		reqBrowser.setExpandHorizontal(true);
		reqBrowser.setExpandVertical(true);

		tblReqBrowser = new Table(reqBrowser, SWT.BORDER | SWT.FULL_SELECTION);
		tblReqBrowser.setHeaderBackground(SWTResourceManager.getColor(248, 248, 255));
		tblReqBrowser.setLinesVisible(true);
		tblReqBrowser.setHeaderVisible(true);

		TableColumn tblclmnStatus = new TableColumn(tblReqBrowser, SWT.LEFT);
		tblclmnStatus.setWidth(45);
		tblclmnStatus.setText("Status");

		TableColumn tblclmnType = new TableColumn(tblReqBrowser, SWT.LEFT);
		tblclmnType.setWidth(99);
		tblclmnType.setText("Type");

		TableColumn tblclmnId = new TableColumn(tblReqBrowser, SWT.LEFT);
		tblclmnId.setWidth(100);
		tblclmnId.setText("ID");

		TableColumn tblclmnShortDesciption = new TableColumn(tblReqBrowser, SWT.LEFT);
		tblclmnShortDesciption.setWidth(309);
		tblclmnShortDesciption.setText("Short Desciption");
		reqBrowser.setContent(tblReqBrowser);
		reqBrowser.setMinSize(tblReqBrowser.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		Group grpViewEditReq = new Group(cmpReqManager, SWT.NONE);
		grpViewEditReq.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		grpViewEditReq.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		grpViewEditReq.setText("View/Edit Requirement");
		grpViewEditReq.setLayout(new FillLayout(SWT.HORIZONTAL));

		ScrolledComposite scrolledComposite = new ScrolledComposite(grpViewEditReq,
				SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);

		Composite composite = new Composite(scrolledComposite, SWT.NONE);
		composite.setBackground(SWTResourceManager.getColor(SWT.COLOR_LIST_BACKGROUND));
		composite.setLayout(new GridLayout(5, true));

		Label lblStatus = new Label(composite, SWT.NONE);
		lblStatus.setBackground(SWTResourceManager.getColor(SWT.COLOR_LIST_BACKGROUND));
		lblStatus.setAlignment(SWT.CENTER);
		lblStatus.setText("Status");

		cmbStatus = new Combo(composite, SWT.NONE);
		cmbStatus.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		cmbStatus.setItems(this.status.toArray(new String[0]));
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);

		Label lblGenTool = new Label(composite, SWT.CENTER);
		lblGenTool.setBackground(SWTResourceManager.getColor(SWT.COLOR_LIST_BACKGROUND));
		lblGenTool.setText("Generation Tool");

		lblGenTool2 = new Label(composite, SWT.BORDER);
		lblGenTool2.setBackground(SWTResourceManager.getColor(255, 240, 245));
		lblGenTool2.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 4, 1));

		scrolledComposite.setContent(composite);
		scrolledComposite.setMinSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		Label lblType = new Label(composite, SWT.CENTER);
		lblType.setBackground(SWTResourceManager.getColor(SWT.COLOR_LIST_BACKGROUND));
		lblType.setText("Type");

		lblType2 = new Label(composite, SWT.BORDER);
		lblType2.setBackground(SWTResourceManager.getColor(255, 240, 245));
		lblType2.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 4, 1));

		scrolledComposite.setContent(composite);
		scrolledComposite.setMinSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		Label lblID = new Label(composite, SWT.CENTER);
		lblID.setBackground(SWTResourceManager.getColor(SWT.COLOR_LIST_BACKGROUND));
		lblID.setText("ID");

		txtID = new Text(composite, SWT.BORDER);
		txtID.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 4, 1));

		scrolledComposite.setContent(composite);
		scrolledComposite.setMinSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		Label lblDesc = new Label(composite, SWT.CENTER);
		lblDesc.setBackground(SWTResourceManager.getColor(SWT.COLOR_LIST_BACKGROUND));
		lblDesc.setText("Descrption");

		txtDesc = new Text(composite, SWT.BORDER | SWT.MULTI);
		txtDesc.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 4, 4));

		new Label(composite, SWT.NONE);
		scrolledComposite.setContent(composite);
		scrolledComposite.setMinSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);

		Label lblContext = new Label(composite, SWT.CENTER);
		lblContext.setBackground(SWTResourceManager.getColor(SWT.COLOR_LIST_BACKGROUND));
		lblContext.setText("Component(s)");

		lblContext2 = new Label(composite, SWT.BORDER);
		lblContext2.setBackground(SWTResourceManager.getColor(255, 240, 245));
		lblContext2.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 4, 1));

		Label lblReason = new Label(composite, SWT.NONE);
		lblReason.setBackground(SWTResourceManager.getColor(SWT.COLOR_LIST_BACKGROUND));
		// lblReason.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblReason.setText("Reason for omission");

		textReason = new Text(composite, SWT.BORDER | SWT.MULTI);
		textReason.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 4, 3));
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		scrolledComposite.setContent(composite);
		scrolledComposite.setMinSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		Composite cmpButtonBar = new Composite(shlReqManager, SWT.NONE);
		cmpButtonBar.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		FillLayout fl_cmpButtonBar = new FillLayout(SWT.HORIZONTAL);
		fl_cmpButtonBar.spacing = 20;
		cmpButtonBar.setLayout(fl_cmpButtonBar);
		cmpButtonBar.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		// createButton(cmpButtonBar, SWT.SAVE, " Save ", false);
		createButton(cmpButtonBar, SWT.OK, "        OK        ", false);
		createButton(cmpButtonBar, SWT.CANCEL, "        Cancel        ", true);
//		Button btnApply = new Button(cmpButtonBar, SWT.NONE);
//		btnApply.setText("        Apply        ");
//		Button btnOk = new Button(cmpButtonBar, SWT.NONE);
//		btnOk.setText("        OK        ");
//		Button btnCancel = new Button(cmpButtonBar, SWT.NONE);
//		btnCancel.setText("        Cancel        ");

		// Your code goes here (widget creation, set result, etc).

		tblReqBrowser.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				int index = tblReqBrowser.getSelectionIndex();
				System.out.println("Table Selection: oldindex " + oldIndex + " newindex " + index);

				if (oldIndex == index) {
					return;
				}

				// Save previous requirement
				if (oldIndex >= 0) {
					if (!saveReqChanges()) {
						// error in saving changes
						tblReqBrowser.setSelection(oldIndex);
						return;
					}
				}

				// Update requirement dialog
				CyberRequirement r = requirements.get(index);
				cmbStatus.select(getStatusIndex(r.getStatus()));
				lblGenTool2.setText(r.getTool());
				lblType2.setText(r.getType());
				txtID.setText(r.getId());
				txtID.setEditable(r.getId().isEmpty());
				txtDesc.setText(r.getText());
				txtDesc.setEditable(r.getId().isEmpty());
				lblContext2.setText(r.getContext());
				textReason.setText(r.getRationale());

				oldIndex = index;
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});

	}

	private boolean saveReqChanges() {
		String newStatus = getStatusString(cmbStatus.getSelectionIndex());
		String newId = txtID.getText();
		String newDesc = txtDesc.getText();
		String newReason = textReason.getText();

		if (newStatus.equalsIgnoreCase(CyberRequirement.omit)
				&& (newReason.isEmpty() || newReason.equalsIgnoreCase(CyberRequirement.notApplicable))) {
			org.osate.ui.dialogs.Dialog.showError("Missing Requirement Information",
					"Requirements that are marked as omitted must provide a rationale for the omission.");
			return false;
		}

		if ((newStatus.equalsIgnoreCase(CyberRequirement.add) || newStatus.equalsIgnoreCase(CyberRequirement.addPlusAgree))) {
			if (newId.isEmpty()) {
				org.osate.ui.dialogs.Dialog.showError("Missing requirement ID",
						"Requirement IDs must be assigned before requirements can be imported into the model.");
				return false;
			}
		}

		CyberRequirement req = requirements.get(oldIndex);
		req.setStatus(newStatus);
		req.setId(newId);
		req.setText(newDesc);
		req.setRationale(newReason);
		updateTableItem(oldIndex);

		return true;
	}

	private String getStatusString(int index) {
		return this.status.get(index);
	}

	private int getStatusIndex(String status) {
		return this.status.indexOf(status);
	}

	public int open() {

		populateTable(tblReqBrowser);
//		tblReqBrowser.setSortColumn(tblclmnId);
//		tblReqBrowser.setSortDirection(SWT.UP);

		if (requirements.isEmpty()) {
			cmbStatus.select(0);
			lblGenTool2.setText("GearCASE");
			lblType2.setText("well_formed");
			txtID.setText("Req_002");
			txtDesc.setText("UXAS shall only accept well-formed messages from the GroundStation");
			lblContext2.setText("SW::SW.Impl.UXAS");
			textReason.setText("N/A");
		}

		shlReqManager.open();
		Display display = getParent().getDisplay();
		while (!shlReqManager.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return returnCode;
	}

	protected Button createButton(Composite parent, int id, String label, boolean defaultButton) {
		Button button = new Button(parent, SWT.PUSH);
		button.setText(label);
		button.setData(Integer.valueOf(id));
		button.addSelectionListener(
				widgetSelectedAdapter(event -> buttonPressed(((Integer) event.widget.getData()).intValue())));
		if (defaultButton) {
			Shell shell = parent.getShell();
			if (shell != null) {
				shell.setDefaultButton(button);
			}
		}
		return button;
	}

	private void buttonPressed(int intValue) {
		if (intValue == SWT.OK) {
			okPressed();
		} else if (intValue == SWT.CANCEL) {
			cancelPressed();
		} else if (intValue == SWT.SAVE) {
			applyPressed();
		}
	}

	private void addSampleEntries(Table t) {
		TableItem t0 = new TableItem(t, SWT.NONE);
		TableItem t1 = new TableItem(t, SWT.NONE);
		TableItem t2 = new TableItem(t, SWT.NONE);
		TableItem t3 = new TableItem(t, SWT.NONE);
		TableItem t4 = new TableItem(t, SWT.NONE);

		t0.setText(new String[] { "ToDo", "trusted_source", "Req_001",
				"UXAS shall only accept messages from a trusted GroundStation" });
		t1.setText(new String[] { "Add+Agree", "well_formed", "Req_002",
				"UXAS shall only accept well-formed messages from the GroundStation" });
		t2.setText(new String[] { "Add", "isolated", "Req_003",
				"Third-party software shall be isolated from critical components" });
		t3.setText(new String[] { "Add", "monitored", "Req_004",
				"The output of Third-party software shall be monitored for correct behavior" });
		t4.setText(new String[] { "Ignore", "not_hackable", "Req_005",
				"The WaypointPlanManagerService shall not be hackable by anyone ever" });
	}

	private void populateTable(Table t) {
		if (requirements.isEmpty()) {
			addSampleEntries(t);
			return;
		}

		for (CyberRequirement r : requirements) {
			TableItem tItem = new TableItem(t, SWT.NONE);
			if (r.getStatus().isEmpty()) {
				r.setStatus(CyberRequirement.toDo);
			}
			tItem.setText(new String[] { r.getStatus(), r.getType(), r.getId(), r.getText() });
		}
	}


	/*
	 * private void sortRequirements(Table t, int column) {
	 * Comparator<CyReq> byCol = null;
	 * switch (column) {
	 * case 0: // status
	 * byCol = (CyReq r1, CyReq r2) -> r1.type.compareTo(r2.status);
	 * break;
	 * case 1: // type
	 * byCol = (CyReq r1, CyReq r2) -> r1.type.compareTo(r2.type);
	 * break;
	 * case 2: // id
	 * byCol = (CyReq r1, CyReq r2) -> r1.id.compareTo(r2.id);
	 * break;
	 * case 3: // desc
	 * byCol = (CyReq r1, CyReq r2) -> r1.desc.compareTo(r2.desc);
	 * break;
	 * default:
	 * break;
	 * }
	 *
	 * List<CyReq> sorter = new ArrayList<CyReq>();
	 * for (TableItem item : t.getItems()) {
	 *
	 * }
	 * }
	 */
	protected void okPressed() {
		// Save changes and exit dialog
		if (!saveReqChanges()) {
			// error in saving changes
			return;
		}
		saveInput();
		returnCode = SWT.OK;
		shlReqManager.close();
	}

	protected void applyPressed() {
		// Save changes and stay in dialog
		if (!saveReqChanges()) {
			// error in saving changes
			return;
		}
		saveInput();
	}

	protected void cancelPressed() {
		// Exit dialog without saving
		returnCode = SWT.CANCEL;
		shlReqManager.close();
	}

	private boolean updateTableItem(int index) {
		if (index < 0 || index >= tblReqBrowser.getItemCount()) {
			return false;
		}
		CyberRequirement req = requirements.get(index);
		TableItem tItem = tblReqBrowser.getItem(index);
		tItem.setText(new String[] { req.getStatus(), req.getType(), req.getId(), req.getText() });
		return true;
	}

	private boolean saveInput() {
		for (int i = 0; i < requirements.size(); i++) {
			CyberRequirement req = requirements.get(i);

			// Check for invalid requirement
			if (req.getStatus() != CyberRequirement.toDo) {

				// Find the context (component, connection, etc) in the model
				Classifier contextClassifier = CyberRequirement.getImplementationClassifier(req.getContext());

				// Check for invalid context
				if (contextClassifier == null) {
					org.osate.ui.dialogs.Dialog.showError("Unknown context for " + req.getType(), req.getContext()
							+ " could not be found in any AADL file in the project. A requirement context must be valid in order to import requirements into model. This requirement will be de-selected.");
					req.setStatus(CyberRequirement.toDo);
					updateTableItem(i);
					return false;
				}

				// Check for missing requirement ID
				if (req.getId().isEmpty() && req.getStatus() != CyberRequirement.omit) {
					org.osate.ui.dialogs.Dialog.showError("Missing requirement ID", req.getType()
							+ " is missing a requirement ID. Requirement IDs must be assigned before requirements can be imported into model. This requirement will be de-selected.");
					// Uncheck this requirement
					req.setStatus(CyberRequirement.toDo);
					updateTableItem(i);
					return false;
				}
			}
		}

		return true;
	}

	public void setRequirements(List<CyberRequirement> newRequirements) {
		if (!this.requirements.isEmpty()) {
			this.requirements.clear();
		}
		this.requirements.addAll(newRequirements);
	}

	public List<CyberRequirement> getRequirements() {
		return requirements;
	}

//	// Used for sorting requirements table
//	private class CyReq {
//		String status;
//		String type;
//		String id;
//		String desc;
//
//		public CyReq(String status, String type, String id, String desc) {
//			this.status = status;
//			this.type = type;
//			this.id = id;
//			this.desc = desc;
//		}
//
//		@Override
//		public String toString() {
//			return this.status + "-" + this.type + "-" + this.id + "-" + this.desc;
//		}
//	}
}
