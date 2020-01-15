package com.collins.trustedsystems.briefcase.splat.preferences;

import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class IntegerWidthFieldEditor extends IntegerFieldEditor {

	protected Button optimize;
	protected final String optimizePreferenceName;
	protected final String optimizeLabel;
	private final static int FIELD_WIDTH = 10;

	public IntegerWidthFieldEditor(String widthPreferenceName, String widthLabel, String optimizePreferenceName,
			String optimizeLabel,
			Composite parent) {
//		super(widthPreferenceName, labelText, parent, 4);
		this.optimizePreferenceName = optimizePreferenceName;
		this.optimizeLabel = optimizeLabel;
		setErrorMessage("Integer width must be a positive multiple of 8.");
		init(widthPreferenceName, widthLabel);
		createControl(parent);
	}

	@Override
	protected void adjustForNumColumns(int numColumns) {
		super.adjustForNumColumns(numColumns - 1);
	}

	@Override
	public int getNumberOfControls() {
		return 3;
	}

	@Override
	protected void doFillIntoGrid(Composite parent, int numColumns) {
		super.doFillIntoGrid(parent, numColumns - 1);

		// Integer Width field
		Text textField = getTextControl(parent);
		GridData gd = new GridData();
		gd.horizontalSpan = numColumns - 1;
		GC gc = new GC(textField);
		try {
			Point extent = gc.textExtent("X");//$NON-NLS-1$
			gd.widthHint = FIELD_WIDTH * extent.x;
		} finally {
			gc.dispose();
		}
		textField.setLayoutData(gd);

		// Optimize checkbox field
		Button button = getOptimize(parent);
		gd = new GridData();
		button.setLayoutData(gd);

	}

	@Override
	protected boolean checkState() {

		Text text = getTextControl();

		if (text == null) {
			return false;
		}

		String numberString = text.getText();
		try {
			int number = Integer.valueOf(numberString).intValue();

			if (number > 0 && number % 8 == 0) {
				clearErrorMessage();
				return true;
			}

			showErrorMessage();
			return false;

		} catch (NumberFormatException e1) {
			showErrorMessage();
		}

		return false;
	}

	@Override
	protected void doLoad() {
		Text text = getTextControl();
		if (text != null) {
			int value = getPreferenceStore().getInt(getPreferenceName());
			text.setText("" + value);//$NON-NLS-1$
			oldValue = "" + value; //$NON-NLS-1$
			optimize.setSelection(getPreferenceStore().getBoolean(optimizePreferenceName));
		}

	}

	@Override
	protected void doLoadDefault() {
		Text text = getTextControl();
		if (text != null) {
			int value = getPreferenceStore().getDefaultInt(getPreferenceName());
			text.setText("" + value);//$NON-NLS-1$
			optimize.setSelection(getPreferenceStore().getBoolean(optimizePreferenceName));
		}
		valueChanged();
	}

	@Override
	protected void doStore() {
		Text text = getTextControl();
		if (text != null) {
			Integer i = Integer.valueOf(text.getText());
			getPreferenceStore().setValue(getPreferenceName(), i.intValue());
			getPreferenceStore().setValue(optimizePreferenceName, optimize.getSelection());
		}
	}

	protected Button getOptimize(Composite parent) {
		if (optimize == null) {
			optimize = new Button(parent, SWT.CHECK);
			optimize.setText(optimizeLabel);
		}
		return optimize;
	}

}
