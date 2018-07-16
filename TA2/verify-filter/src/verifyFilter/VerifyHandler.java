package verifyFilter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.dialogs.MessageDialog;
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

import agreeToJson.AgreeToJson;


public class VerifyHandler extends AbstractHandler {

	private IWorkbenchWindow window;

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
		window = HandlerUtil.getActiveWorkbenchWindowChecked(event);

		if (xtextEditor == null) {
			MessageDialog.openError(window.getShell(), "No AADL editor is active",
					"An AADL editor must be active in order to generate JSON.");
			return null;
		}

		try {

			URI jsonURI = AgreeToJson.createJson(event);

			IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(jsonURI.toPlatformString(true)));
			String fullpath = file.getRawLocation().toOSString();

			MessageDialog.openInformation(window.getShell(), "Transfer to HOL",
					"Running HOL proof of Filter Properties.\n" + "File: " + fullpath + "\n"
							+ "See output in console.");


			String json2holPath = ResourcesPlugin.getWorkspace().getRoot()
					.getFile(new Path("dependencies/json2hol")).getRawLocation().toOSString();

			System.out.println("json2holPath: " + json2holPath);

			Runtime rt = Runtime.getRuntime();
			String[] commands = { json2holPath, fullpath };

			Process proc = rt.exec(commands);

			BufferedReader stdErr = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
			System.out.println("Here is the standard output of the command:\n");

			MessageConsole console = findConsole("HOL Proof of Filter Claims");
			MessageConsoleStream out = console.newMessageStream();
			IWorkbenchPage page = window.getActivePage();
			String id = IConsoleConstants.ID_CONSOLE_VIEW;
			IConsoleView view = (IConsoleView) page.showView(id);
			view.display(console);

			String s = null;
			while ((s = stdErr.readLine()) != null) {
				out.println(s);
			}
			out.println("Done with HOL proof of filter properties.");

		} catch (CoreException | IOException e) {
			System.err.println("Trouble in Filter Verification");
			e.printStackTrace();
		}


		return null;
	}

}
