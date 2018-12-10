package com.rockwellcollins.atc.darpacase.architecture.dialogs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.osate.aadl2.ComponentType;

import com.rockwellcollins.atc.darpacase.architecture.handlers.ModelAnnotations.BOUNDARY;
import com.rockwellcollins.atc.darpacase.architecture.handlers.ModelAnnotations.CIA;
import com.rockwellcollins.atc.darpacase.architecture.handlers.ModelAnnotations.COMM_MODALITY;
import com.rockwellcollins.atc.darpacase.architecture.handlers.ModelAnnotations.COMP_TYPE;

public class ModelAnnotationsDialog extends TitleAreaDialog {

	private List<Button> btnConfidentiality = new ArrayList<>();
	private List<Button> btnIntegrity = new ArrayList<>();
	private List<Button> btnAvailability = new ArrayList<>();
	private List<Button> btnCompType = new ArrayList<>();
	private List<Button> btnCommModality = new ArrayList<>();
	private List<Button> btnBoundary = new ArrayList<>();

	private Map<String, String> prettyName = new HashMap<>();
	private Map<String, String> uglyName = new HashMap<>();

	private ComponentType component = null;
	private CIA confidentiality = CIA.NULL;
	private CIA integrity = CIA.NULL;
	private CIA availability = CIA.NULL;
	private COMP_TYPE compType = COMP_TYPE.NULL;
	private COMM_MODALITY commModality = COMM_MODALITY.NULL;
	private Set<BOUNDARY> boundary = new HashSet<>();

	public ModelAnnotationsDialog(Shell parentShell) {
		super(parentShell);
		setHelpAvailable(false);

		prettyName.clear();
		prettyName.put("HIGH", "High");
		prettyName.put("MEDIUM", "Medium");
		prettyName.put("LOW", "Low");
		prettyName.put("NULL", "None");
		prettyName.put("FILTER", "Filter");
		prettyName.put("ATTESTATION", "Attestation");
		prettyName.put("MONITOR", "Monitor");
		prettyName.put("ROUTER", "Router");
		prettyName.put("ISOLATOR", "Isolator");
		prettyName.put("COMM_DRIVER", "Comm Driver");
		prettyName.put("RF", "RF");
		prettyName.put("WIFI", "WiFi");
		prettyName.put("WIRED_ETHERNET", "Wired Ethernet");
		prettyName.put("SERIAL", "Serial");
		prettyName.put("BT", "Bluetooth");
		prettyName.put("TRUSTED", "Trusted");
		prettyName.put("PHYSICAL", "Physical");
		uglyName.clear();
		uglyName.put("High", "HIGH");
		uglyName.put("Medium", "MEDIUM");
		uglyName.put("Low", "LOW");
		uglyName.put("None", "NULL");
		uglyName.put("Other", "NULL");
		uglyName.put("Filter", "FILTER");
		uglyName.put("Attestation", "ATTESTATION");
		uglyName.put("Monitor", "MONITOR");
		uglyName.put("Router", "ROUTER");
		uglyName.put("Isolator", "ISOLATOR");
		uglyName.put("Comm Driver", "COMM_DRIVER");
		uglyName.put("RF", "RF");
		uglyName.put("WiFi", "WIFI");
		uglyName.put("Wired Ethernet", "WIRED_ETHERNET");
		uglyName.put("Serial", "SERIAL");
		uglyName.put("Bluetooth", "BT");
		uglyName.put("Trusted", "TRUSTED");
		uglyName.put("Physical", "PHYSICAL");

	}

	@Override
	public void create() {
		super.create();
		if (component == null) {
			setTitle("Annotate Model");
			setMessage("Annotate model with CASE-specific properties.", IMessageProvider.NONE);
		} else {
			setTitle("Annotate Model");
			setMessage("Annotate " + component.getName() + " with CASE-specific properties.", IMessageProvider.NONE);
		}
	}


	@Override
	protected Point getInitialSize() {
		final Point size = super.getInitialSize();
		size.x -= convertWidthInCharsToPixels(6);
		size.y += convertHeightInCharsToPixels(1);

		return size;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));
		GridLayout layout = new GridLayout(2, false);
		layout.horizontalSpacing = convertWidthInCharsToPixels(5);
		layout.marginLeft = convertWidthInCharsToPixels(2);
		layout.marginRight = convertWidthInCharsToPixels(2);
		container.setLayout(layout);

		// Add annotation information fields
		createConfidentialityField(container);
		createIntegrityField(container);
		createAvailabilityField(container);
		createCompTypeField(container);
		if (component.getCategory().getName().equalsIgnoreCase("bus")) {
			createCommModalityField(container);
		} else {
			createBoundaryField(container);
		}

		return area;
	}

	private void createConfidentialityField(Composite container) {

		Label lblConfidentialityField = new Label(container, SWT.NONE);
		lblConfidentialityField.setText("Confidentiality");

		FontData fontData = lblConfidentialityField.getFont().getFontData()[0];
		Font font = new Font(container.getDisplay(), new FontData(fontData.getName(), fontData.getHeight(), SWT.BOLD));
		lblConfidentialityField.setFont(font);

		// Create a group to contain CIA options
		Group ciaGroup = new Group(container, SWT.NONE);
		ciaGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		ciaGroup.setLayout(new RowLayout(SWT.HORIZONTAL));

		btnConfidentiality.clear();
		for (CIA cia : CIA.values()) {
			Button option = new Button(ciaGroup, SWT.RADIO);
			option.setText(prettyName.getOrDefault(cia.toString(), cia.toString()));
			if (cia == confidentiality) {
				option.setSelection(true);
			}
			btnConfidentiality.add(option);
		}

	}

	private void createIntegrityField(Composite container) {
		Label lblIntegrityField = new Label(container, SWT.NONE);
		lblIntegrityField.setText("Integrity");

		FontData fontData = lblIntegrityField.getFont().getFontData()[0];
		Font font = new Font(container.getDisplay(), new FontData(fontData.getName(), fontData.getHeight(), SWT.BOLD));
		lblIntegrityField.setFont(font);

		// Create a group to contain CIA options
		Group ciaGroup = new Group(container, SWT.NONE);
		ciaGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		ciaGroup.setLayout(new RowLayout(SWT.HORIZONTAL));

		btnIntegrity.clear();
		for (CIA cia : CIA.values()) {
			Button option = new Button(ciaGroup, SWT.RADIO);
			option.setText(prettyName.getOrDefault(cia.toString(), cia.toString()));
			if (cia == integrity) {
				option.setSelection(true);
			}
			btnIntegrity.add(option);
		}

	}

	private void createAvailabilityField(Composite container) {
		Label lblAvailabilityField = new Label(container, SWT.NONE);
		lblAvailabilityField.setText("Availability");

		FontData fontData = lblAvailabilityField.getFont().getFontData()[0];
		Font font = new Font(container.getDisplay(), new FontData(fontData.getName(), fontData.getHeight(), SWT.BOLD));
		lblAvailabilityField.setFont(font);

		// Create a group to contain CIA options
		Group ciaGroup = new Group(container, SWT.NONE);
		ciaGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		ciaGroup.setLayout(new RowLayout(SWT.HORIZONTAL));

		btnAvailability.clear();
		for (CIA cia : CIA.values()) {
			Button option = new Button(ciaGroup, SWT.RADIO);
			option.setText(prettyName.getOrDefault(cia.toString(), cia.toString()));
			if (cia == availability) {
				option.setSelection(true);
			}
			btnAvailability.add(option);
		}

	}

	private void createCompTypeField(Composite container) {
		Label lblCompTypeField = new Label(container, SWT.NONE);
		lblCompTypeField.setText("Component Type");

		FontData fontData = lblCompTypeField.getFont().getFontData()[0];
		Font font = new Font(container.getDisplay(), new FontData(fontData.getName(), fontData.getHeight(), SWT.BOLD));
		lblCompTypeField.setFont(font);


		// Create a group to contain COMP_TYPE options
		Group compTypeGroup = new Group(container, SWT.NONE);
//		compTypeGroup.setLayout(new RowLayout(SWT.HORIZONTAL));
		compTypeGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		compTypeGroup.setLayout(new GridLayout(3, true));

		btnCompType.clear();
		for (COMP_TYPE ct : COMP_TYPE.values()) {
			Button option = new Button(compTypeGroup, SWT.RADIO);
			option.setText(ct.toString().equalsIgnoreCase("NULL") ? "Other"
					: prettyName.getOrDefault(ct.toString(), ct.toString()));
			if (ct == compType) {
				option.setSelection(true);
			}
			btnCompType.add(option);
		}

	}

	private void createCommModalityField(Composite container) {
		Label lblCommModalityField = new Label(container, SWT.NONE);
		lblCommModalityField.setText("Communication Modality");

		FontData fontData = lblCommModalityField.getFont().getFontData()[0];
		Font font = new Font(container.getDisplay(), new FontData(fontData.getName(), fontData.getHeight(), SWT.BOLD));
		lblCommModalityField.setFont(font);

		// Create a group to contain COMM_MODALITY options
		Group commModalityGroup = new Group(container, SWT.NONE);
		commModalityGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
//		commModalityGroup.setLayout(new RowLayout(SWT.HORIZONTAL));
		commModalityGroup.setLayout(new GridLayout(3, true));

		btnCommModality.clear();
		for (COMM_MODALITY cm : COMM_MODALITY.values()) {
			Button option = new Button(commModalityGroup, SWT.RADIO);
			option.setText(prettyName.getOrDefault(cm.toString(), cm.toString()));
			if (cm == commModality) {
				option.setSelection(true);
			}
			btnCommModality.add(option);
		}
	}

	private void createBoundaryField(Composite container) {
		Label lblBoundaryField = new Label(container, SWT.NONE);
		lblBoundaryField.setText("Boundary");

		FontData fontData = lblBoundaryField.getFont().getFontData()[0];
		Font font = new Font(container.getDisplay(), new FontData(fontData.getName(), fontData.getHeight(), SWT.BOLD));
		lblBoundaryField.setFont(font);

		// Create a composite to contain BOUNDARY options
		Group selectionField = new Group(container, SWT.NO_RADIO_GROUP);
		selectionField.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		selectionField.setLayout(new RowLayout(SWT.HORIZONTAL));

		btnBoundary.clear();
		for (BOUNDARY b : BOUNDARY.values()) {
			if (b == BOUNDARY.NULL) {
				continue;
			}
			Button option = new Button(selectionField, SWT.CHECK);
			option.setText(prettyName.getOrDefault(b.toString(), b.toString()));
			if (boundary.contains(b)) {
				option.setSelection(true);
			}
			btnBoundary.add(option);
		}
	}

	@Override
	protected void okPressed() {

		for (Button b : btnConfidentiality) {
			if (b.getSelection()) {
				confidentiality = CIA.valueOf(uglyName.get(b.getText()));
				break;
			}
		}
		for (Button b : btnIntegrity) {
			if (b.getSelection()) {
				integrity = CIA.valueOf(uglyName.get(b.getText()));
				break;
			}
		}
		for (Button b : btnAvailability) {
			if (b.getSelection()) {
				availability = CIA.valueOf(uglyName.get(b.getText()));
				break;
			}
		}
		for (Button b : btnCompType) {
			if (b.getSelection()) {
				compType = COMP_TYPE.valueOf(uglyName.get(b.getText()));
				break;
			}
		}
		for (Button b : btnCommModality) {
			if (b.getSelection()) {
				commModality = COMM_MODALITY.valueOf(uglyName.get(b.getText()));
				break;
			}
		}
		boundary.clear();
		for (Button b : btnBoundary) {
			if (b.getSelection()) {
				boundary.add(BOUNDARY.valueOf(uglyName.get(b.getText())));
			}
		}
		super.okPressed();
	}


	public void setComponentAnnotations(ComponentType component, CIA confidentiality, CIA integrity, CIA availability,
			COMP_TYPE compType, COMM_MODALITY commModality, Set<BOUNDARY> boundary) {
		this.component = component;
		this.confidentiality = confidentiality;
		this.integrity = integrity;
		this.availability = availability;
		this.compType = compType;
		this.commModality = commModality;
		this.boundary = boundary;
	}

	public CIA getConfidentiality() {
		return confidentiality;
	}

	public CIA getIntegrity() {
		return integrity;
	}

	public CIA getAvailability() {
		return availability;
	}

	public COMP_TYPE getCompType() {
		return compType;
	}

	public COMM_MODALITY getCommModality() {
		return commModality;
	}

	public Set<BOUNDARY> getBoundary() {
		return boundary;
	}

}
