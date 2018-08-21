package com.rockwellcollins.atc.darpacase.architecture.dialogs;

import java.io.File;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * This class creates the Select Implementation wizard for
 * assigning an implementation to an AADL component
 */
public class SelectImplementationDialog extends TitleAreaDialog {

	String implementationType;
	String implementationLocation;
	Button rdoImplementationTypeBinary;
	Button rdoImplementationTypeSource;
	Text txtImplementationLocation;
	Button btnImplementationLocation;
	FileDialog dlgImplementationLocation;

	public SelectImplementationDialog(Shell parentShell) {
		super(parentShell);
		setHelpAvailable(false);
	}

	@Override
	public void create() {
		super.create();
		setTitle("Select Implementation");
		setMessage(
				"Enter Implementation details.  You may optionally leave these fields empty and manually edit the AADL software component directly in the model.",
				IMessageProvider.NONE);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout layout = new GridLayout(3, false);
		container.setLayout(layout);

		// Add implementation information fields
		createImplementationTypeField(container);
		createImplementationLocationField(container);

		return area;
	}

	/**
	 * Creates the radio button field for specifying the implementation type
	 * (source code / binary)
	 * @param container
	 */
	private void createImplementationTypeField(Composite container) {
		Label lblImplementationTypeField = new Label(container, SWT.NONE);
		lblImplementationTypeField.setText("Implementation type:");

		GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = GridData.FILL;
		rdoImplementationTypeBinary = new Button(container, SWT.RADIO);
		rdoImplementationTypeBinary.setText("Binary");
		rdoImplementationTypeBinary.setSelection(true);
		rdoImplementationTypeBinary.setLayoutData(dataInfoField);
		rdoImplementationTypeSource = new Button(container, SWT.RADIO);
		rdoImplementationTypeSource.setText("Source code");
		rdoImplementationTypeSource.setLayoutData(dataInfoField);

	}

	/**
	 * Creates the open file dialog for selecting the implementation
	 * @param container
	 */
	private void createImplementationLocationField(Composite container) {
		Label lblImplementationLocationField = new Label(container, SWT.NONE);
		lblImplementationLocationField.setText("Implementation location:");

		GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = GridData.FILL;
		txtImplementationLocation = new Text(container, SWT.BORDER);
		txtImplementationLocation.setLayoutData(dataInfoField);
		btnImplementationLocation = new Button(container, SWT.PUSH);
		btnImplementationLocation.setLayoutData(dataInfoField);
		btnImplementationLocation.setText("Browse...");
		btnImplementationLocation.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dlgImplementationLocation = new FileDialog(getParentShell());
				dlgImplementationLocation.setText("Select Implementation");
				dlgImplementationLocation.open();
				String filename = dlgImplementationLocation.getFilterPath();
				if (!filename.isEmpty()) {
					filename = filename.concat(File.separator);
				}
				filename = filename.concat(dlgImplementationLocation.getFileName());
				txtImplementationLocation.setText(filename);
			}
		});
		btnImplementationLocation.setLayoutData(dataInfoField);

	}

	/**
	 * Saves information entered into the wizard.  This is needed because the
	 * input fields are disposed when the dialog closes.
	 * @param container
	 */
	private void saveInput() {
		if (rdoImplementationTypeSource.getSelection()) {
			implementationType = "SOURCE_CODE";
		} else if (rdoImplementationTypeBinary.getSelection()) {
			implementationType = "BINARY";
		} else {
			implementationType = "NO_IMPL";
		}
		implementationLocation = txtImplementationLocation.getText();
	}

	@Override
	protected void okPressed() {
		saveInput();
		super.okPressed();
	}

	public String getImplementationType() {
		return implementationType;
	}

	public String getImplementationLocation() {
		return implementationLocation;
	}

}
