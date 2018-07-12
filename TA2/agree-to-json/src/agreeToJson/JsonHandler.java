package agreeToJson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
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
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.utils.EditorUtils;
import org.osate.aadl2.AadlPackage;
import org.osate.aadl2.ModelUnit;
import org.osate.aadl2.PropertySet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import agreeToJson.json.Value;

public class JsonHandler extends AbstractHandler {

	private IWorkbenchWindow window;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		XtextEditor xtextEditor = EditorUtils.getActiveXtextEditor();
		window = HandlerUtil.getActiveWorkbenchWindowChecked(event);

		if (xtextEditor == null) {
			MessageDialog.openError(window.getShell(), "No AADL editor is active",
					"An AADL editor must be active in order to generate JSON.");
			return null;
		}

		EObject original = xtextEditor.getDocument().readOnly(resource -> resource.getContents().get(0));
		ModelUnit model = (ModelUnit) EcoreUtil.copy(original);



		AadlTranslate aadlTranslate = new AadlTranslate();

		Value jsonValue = null;
		if (model instanceof AadlPackage) {
			jsonValue = aadlTranslate.doSwitch(model);
		} else if (model instanceof PropertySet) {
			jsonValue = aadlTranslate.doSwitch(model);
		} else {
			MessageDialog.openError(window.getShell(), "Expecting AADL Package or Property Set",
					"A AADL package or property set must be the entry point for generating JSON.");
			return null;
		}

		Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
		JsonParser jp = new JsonParser();
		JsonElement je = jp.parse(jsonValue.toString());

		try {

			URI jsonURI = makeJsonFile(xtextEditor);
			printJson(jsonURI, gson.toJson(je));
			Runtime rt = Runtime.getRuntime();
			String[] commands = { "C:\\WINDOWS\\system32\\cmd.exe", "/c", "echo", jsonURI.path() };

			Process proc = rt.exec(commands);

			BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			System.out.println("Here is the standard output of the command:\n");

			MessageDialog.openInformation(window.getShell(), "Transfer to HOL",
					"Running HOL proof of Filter Properties.\n" + "See output in console.");

			MessageConsole console = findConsole("HOL Proof of Filter Claims");
			MessageConsoleStream out = console.newMessageStream();
			IWorkbenchPage page = window.getActivePage();
			String id = IConsoleConstants.ID_CONSOLE_VIEW;
			IConsoleView view = (IConsoleView) page.showView(id);
			view.display(console);

			String s = null;
			while ((s = stdInput.readLine()) != null) {
				out.println(s);
			}
			out.println("Done with HOL proof of filter properties.");


		} catch (CoreException | IOException e) {
			System.err.println("Trouble writing Json representation to filesystem.");
			e.printStackTrace();
		}

		return null;
	}

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

	private URI makeJsonFile(XtextEditor state) throws CoreException, IOException {
		XtextResource resource = state.getDocument().readOnly(r -> r);

		URI dan = resource.getURI();
		URI folder = dan.trimSegments(1);
		String base = FilesystemUtils.getBase(dan);

		URI writeFolder = FilesystemUtils.createFolder(folder, new String[] { "json-generated" });
		URI json = writeFolder.appendSegment(base).appendFileExtension("json");

		return json;
	}

	private void printJson(URI json, String whatToPrint) throws CoreException, IOException {

		IFile print = FilesystemUtils.getFile(json);
		FilesystemUtils.writeFile(print, whatToPrint.getBytes());
	}
}
