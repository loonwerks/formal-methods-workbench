package com.collins.fmw.cyres.architecture.handlers;

import java.io.File;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

public class TouchHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		// UofM
		touch("C:/Users/ieamunds/Desktop/AAHAA/Demo/Test_Input_Prioritization.txt");
		// Marabou
		touch("C:/Users/ieamunds/Desktop/AAHAA/Demo/NN_Formal_Analysis.txt");
		// ?
		touch("C:/Users/ieamunds/Desktop/AAHAA/Demo/Safety_Aware_Constrained_Learning.txt");
		// APT
		touch("C:/Users/ieamunds/Desktop/AAHAA/Demo/APT.txt");

		return null;
	}

	private void touch(String filename) {
		File file = new File(filename);
		if (file.exists()) {
			file.setLastModified(System.currentTimeMillis());
		}
	}

}
