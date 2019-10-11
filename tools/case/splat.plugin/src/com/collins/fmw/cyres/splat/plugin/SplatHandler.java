package com.collins.fmw.cyres.splat.plugin;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.URI;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.utils.EditorUtils;
import org.osate.ui.dialogs.Dialog;
import org.osgi.framework.Bundle;

import com.collins.fmw.cyres.json.plugin.Aadl2Json;

public class SplatHandler extends AbstractHandler {

	static final String bundleId = "com.collins.fmw.cyres.splat.plugin";


	private MessageConsole findConsole(String name) {
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager conMan = plugin.getConsoleManager();
		IConsole[] existing = conMan.getConsoles();
		for (int i = 0; i < existing.length; i++) {
			if (name.equals(existing[i].getName())) {
				return (MessageConsole) existing[i];
			}
		}
		// no console found, so create a new one
		MessageConsole myConsole = new MessageConsole(name, null);
		conMan.addConsoles(new IConsole[] { myConsole });
		return myConsole;
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		XtextEditor xtextEditor = EditorUtils.getActiveXtextEditor();

		if (xtextEditor == null) {
			Dialog.showError("SPLAT", "An AADL editor must be active in order to generate JSON.");
			return null;
		}

		try {

			URI jsonURI = Aadl2Json.createJson();

			IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(jsonURI.toPlatformString(true)));
			String fullpath = file.getRawLocation().toOSString();

			Bundle bundle = Platform.getBundle(bundleId);

			String splatDir = (FileLocator.toFileURL(FileLocator.find(bundle, new Path("resources"), null))).getFile();
			String splatPath = (FileLocator.toFileURL(FileLocator.find(bundle, new Path("resources/splat"), null)))
					.getFile();

			Runtime rt = Runtime.getRuntime();
			rt.exec("chmod a+x " + splatPath);

			String[] commands = { splatPath, fullpath };
			String[] environmentVars = { "LD_LIBRARY_PATH=" + splatDir };

			Process proc = rt.exec(commands, environmentVars);

			BufferedReader stdErr = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

			MessageConsole console = findConsole("SPLAT");
			MessageConsoleStream out = console.newMessageStream();
			out.println(splatPath + " " + fullpath + " LD_LIBRARY_PATH=" + splatDir);
			IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
			IWorkbenchPage page = window.getActivePage();
			String id = IConsoleConstants.ID_CONSOLE_VIEW;
			IConsoleView view = (IConsoleView) page.showView(id);
			view.display(console);

			String s = null;
			while ((s = stdErr.readLine()) != null) {
				out.println(s);
			}

			// Insert the location of the source code into the filter component implementations in the model
			insertSourceCodeLocation();

			out.println("Done running SPLAT");

		} catch (Exception e) {
			Dialog.showError("SPLAT", "SPLAT has encountered an error and was unable to complete.");
			e.printStackTrace();
			return null;
		}

		return null;
	}

	private void insertSourceCodeLocation() {

		// Look in the SPLAT output directory for filters (each will be in its own folder)

		// Find the filter in the model

		// Add the source code location

		// Add implementation language?

	}

}
