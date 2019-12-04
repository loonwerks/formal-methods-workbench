package com.collins.fmw.cyres.splat.plugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.URI;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.utils.EditorUtils;
import org.osate.aadl2.Aadl2Factory;
import org.osate.aadl2.AadlPackage;
import org.osate.aadl2.ComponentImplementation;
import org.osate.aadl2.Property;
import org.osate.aadl2.StringLiteral;
import org.osate.ui.dialogs.Dialog;
import org.osate.xtext.aadl2.properties.util.GetProperties;
import org.osate.xtext.aadl2.properties.util.ProgrammingProperties;
import org.osgi.framework.Bundle;

import com.collins.fmw.cyres.architecture.utils.CaseUtils;
import com.collins.fmw.cyres.json.plugin.Aadl2Json;
import com.collins.fmw.cyres.splat.Activator;
import com.collins.fmw.cyres.splat.preferences.SplatPreferenceConstants;
import com.collins.fmw.cyres.util.plugin.Filesystem;
import com.collins.fmw.cyres.util.plugin.TraverseProject;

public class SplatHandler extends AbstractHandler {

	static final String bundleId = "com.collins.fmw.cyres.splat.plugin";
	private final static String FOLDER_PACKAGE_DELIMITER = "_";

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

			// Replace bounded numbers with unbounded equivalents
			try {
				String contents = Filesystem.readFile(file);
				contents = contents.replaceAll("Base_Types::(Integer_|Unsigned_|Natural)\\d*", "Base_Types::Integer");
				contents = contents.replaceAll("Base_Types::Float_\\d+", "Base_Types::Float");
				Filesystem.writeFile(file, contents);
			} catch (Exception e) {
				Dialog.showWarning("SPLAT",
						"Attempt to replace bounded numbers with their unbounded equivalents failed.  Check json file to ensure it does not contain bounded numbers.");
			}


			String jsonPath = file.getRawLocation().toOSString();

			Bundle bundle = Platform.getBundle(bundleId);

			String splatDir = (FileLocator.toFileURL(FileLocator.find(bundle, new Path("resources"), null))).getFile();
			String splatPath = (FileLocator.toFileURL(FileLocator.find(bundle, new Path("resources/splat"), null)))
					.getFile();

			Runtime rt = Runtime.getRuntime();
			rt.exec("chmod a+x " + splatPath);

			// command line parameters
			List<String> cmds = new ArrayList<>();

			cmds.add(splatPath);

			String assuranceLevel = Activator.getDefault().getPreferenceStore()
					.getString(SplatPreferenceConstants.ASSURANCE_LEVEL);
			if (assuranceLevel.equals(SplatPreferenceConstants.ASSURANCE_LEVEL_CAKE)) {
				cmds.add("cake");
			} else if (assuranceLevel.equals(SplatPreferenceConstants.ASSURANCE_LEVEL_HOL)) {
				cmds.add("hol");
			} else if (assuranceLevel.equals(SplatPreferenceConstants.ASSURANCE_LEVEL_FULL)) {
				cmds.add("full");
			} else {
				cmds.add("basic");
			}

			if (Activator.getDefault().getPreferenceStore().getBoolean(SplatPreferenceConstants.CHECK_PROPERTIES)) {
				cmds.add("-checkprops");
			}

			cmds.add("-outdir");
			cmds.add(Activator.getDefault().getPreferenceStore().getString(SplatPreferenceConstants.OUTPUT_DIRECTORY));

			cmds.add("-intwidth");
			cmds.add(Integer.toString(
					Activator.getDefault().getPreferenceStore().getInt(SplatPreferenceConstants.INTEGER_WIDTH)));
			if (Activator.getDefault().getPreferenceStore().getBoolean(SplatPreferenceConstants.OPTIMIZE)) {
				cmds.add("optimize");
			}

			cmds.add("-endian");
			if (Activator.getDefault().getPreferenceStore().getBoolean(SplatPreferenceConstants.ENDIAN_BIG)) {
				cmds.add("MSB");
			} else {
				cmds.add("LSB");
			}

			cmds.add("-encoding");
			String encoding = Activator.getDefault().getPreferenceStore().getString(SplatPreferenceConstants.ENCODING);
			if (encoding.equals(SplatPreferenceConstants.ENCODING_UNSIGNED)) {
				cmds.add("Unsigned");
			} else if (encoding.equals(SplatPreferenceConstants.ENCODING_SIGN_MAG)) {
				cmds.add("Sign_mag");
			} else if (encoding.equals(SplatPreferenceConstants.ENCODING_ZIGZAG)) {
				cmds.add("Zigzag");
			} else {
				cmds.add("Twos_comp");
			}

			cmds.add(jsonPath);

			String[] commands = cmds.toArray(new String[cmds.size()]);
			String[] environmentVars = { "LD_LIBRARY_PATH=" + splatDir };

			Process proc = rt.exec(commands, environmentVars);

			BufferedReader stdErr = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

			MessageConsole console = findConsole("SPLAT");
			MessageConsoleStream out = console.newMessageStream();
			String cmdLine = "";
			for (String s : cmds) {
				cmdLine += s + " ";
			}
			cmdLine += "LD_LIBRARY_PATH=" + splatDir;
			out.println(cmdLine);
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
			insertSourceCodeLocation(xtextEditor);

			out.println("Done running SPLAT");

		} catch (Exception e) {
			Dialog.showError("SPLAT", "SPLAT has encountered an error and was unable to complete.");
			e.printStackTrace();
			return null;
		}

		return null;
	}

	private void insertSourceCodeLocation(XtextEditor currentEditor) {

		// Look in the SPLAT output directory for filters (each will be in its own folder)
		String outputDir = Activator.getDefault().getPreferenceStore()
				.getString(SplatPreferenceConstants.OUTPUT_DIRECTORY).replace("\\", "/") + "/";

		// Get all the folders in the output directory
		File dir = new File(outputDir);
		String[] filterDirs = dir.list((current, name) -> new File(current, name).isDirectory());
		Map<AadlPackage, List<String>> pkgMap = new HashMap<>();

		for (String f : filterDirs) {
			String qualifiedName = f.replaceFirst(FOLDER_PACKAGE_DELIMITER, "::");
			String[] parts = qualifiedName.split("::");
			AadlPackage aadlPackage = null;
			for (AadlPackage pkg : TraverseProject.getPackagesInProject(TraverseProject.getCurrentProject())) {
				if (pkg.getName().equalsIgnoreCase(parts[0])) {
					aadlPackage = pkg;
					break;
				}
			}

			if (aadlPackage == null || parts.length != 2) {
				continue;
			}

			List<String> filters = new ArrayList<>();
			if (pkgMap.containsKey(aadlPackage)) {
				filters = pkgMap.get(aadlPackage);
			}
			filters.add(parts[1]);
			pkgMap.put(aadlPackage, filters);
		}

		// Iterate through project packages
		for (AadlPackage pkg : pkgMap.keySet()) {
			IFile file = Filesystem.getFile(pkg.eResource().getURI());
			XtextEditor editor = getEditor(file);
			if (editor != null) {
				editor.getDocument().modify(resource -> {
					AadlPackage aadlPackage = (AadlPackage) resource.getContents().get(0);
					for (ComponentImplementation ci : EcoreUtil2.getAllContentsOfType(aadlPackage,
							ComponentImplementation.class)) {
						if (pkgMap.get(pkg).contains(ci.getType().getName())) {
							// Insert language property
							String implLang = "c";
							String assuranceLevel = Activator.getDefault().getPreferenceStore()
									.getString(SplatPreferenceConstants.ASSURANCE_LEVEL);
							if (!assuranceLevel.equalsIgnoreCase(SplatPreferenceConstants.ASSURANCE_LEVEL_BASIC)) {
								implLang = "CakeML";
							}
							if (!CaseUtils.addCasePropertyAssociation("COMP_IMPL", implLang, ci)) {
//								return;
							}

							// Insert source text property
							String compName = ci.getType().getQualifiedName().replace("::", FOLDER_PACKAGE_DELIMITER);
							String sourceText = outputDir
									+ compName + "/" + compName;
							if (implLang.equalsIgnoreCase("c")) {
								sourceText += ".c";
							} else {
								sourceText += ".o";
							}
							Property sourceTextProp = GetProperties.lookupPropertyDefinition(ci,
									ProgrammingProperties._NAME, ProgrammingProperties.SOURCE_TEXT);
							StringLiteral sourceTextLit = Aadl2Factory.eINSTANCE.createStringLiteral();
							sourceTextLit.setValue(sourceText);
							List<StringLiteral> listVal = new ArrayList<>();
							listVal.add(sourceTextLit);
							ci.setPropertyValue(sourceTextProp, listVal);
						}
					}
					return null;
				});
				closeEditor(editor, !editor.equals(currentEditor), true);
			}
		}

	}

	private XtextEditor getEditor(IFile file) {
		IWorkbenchPage page = null;
		IEditorPart part = null;

		if (file.exists()) {
			page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			IEditorDescriptor desc = PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor(file.getName());
			try {
				part = page.openEditor(new FileEditorInput(file), desc.getId());
			} catch (PartInitException e) {
				e.printStackTrace();
			}
		}

		if (part == null) {
			return null;
		}

		XtextEditor xedit = null;
		xedit = (XtextEditor) part;

		return xedit;
	}

	private void closeEditor(XtextEditor editor, boolean close, boolean save) {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if (save) {
			page.saveEditor(editor, false);
		}

		if (close) {
			page.closeEditor(editor, false);
		}
	}

}
