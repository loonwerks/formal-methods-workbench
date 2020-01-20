package com.collins.fmw.cyres.splat.plugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
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
import org.osate.aadl2.PropertyExpression;
import org.osate.aadl2.StringLiteral;
import org.osate.ui.dialogs.Dialog;
import org.osate.xtext.aadl2.properties.util.GetProperties;
import org.osate.xtext.aadl2.properties.util.ProgrammingProperties;

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

			// name of the splat image
			String dockerImage = "splatimgupdated";

			URI jsonURI = Aadl2Json.createJson();
			IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(jsonURI.toPlatformString(true)));

//			// Replace bounded numbers with unbounded equivalents
//			try {
//				String contents = Filesystem.readFile(file);
//				contents = contents.replaceAll("Base_Types::(Integer_|Unsigned_|Natural)\\d*", "Base_Types::Integer");
//				contents = contents.replaceAll("Base_Types::Float_\\d+", "Base_Types::Float");
//				Filesystem.writeFile(file, contents);
//			} catch (Exception e) {
//				Dialog.showWarning("SPLAT",
//						"Attempt to replace bounded numbers with their unbounded equivalents failed.  Check json file to ensure it does not contain bounded numbers.");
//			}

			// Prepare the volume mounting format for docker
			boolean imageExists = false;
			String jsonPath = file.getRawLocation().toOSString();
			String[] jsonPathArrayTemp = jsonPath.split(Pattern.quote(File.separator));
			String[] jsonPathArrayNew = Arrays.copyOf(jsonPathArrayTemp, jsonPathArrayTemp.length - 1);
			String dockerMountPath = String.join("/", jsonPathArrayNew);
			dockerMountPath += ":/user ";
//			System.out.println(dockermountPath);
//			System.out.println(jsonPathArrayTemp[jsonPathArrayTemp.length - 1]);

//			Bundle bundle = Platform.getBundle(bundleId);

//			String splatDir = (FileLocator.toFileURL(FileLocator.find(bundle, new Path("resources"), null))).getFile();
//			String splatPath = (FileLocator.toFileURL(FileLocator.find(bundle, new Path("resources/splat"), null)))
//					.getFile();
//			String splatPath = "C:/Users/shasan1/git/CASE/TA2/Model_Transformations/Filter/Simple_Example/Transformed_Model/json-generated:/user";

//			Runtime rt = Runtime.getRuntime();
//			rt.exec("chmod a+x " + splatPath);

			// Initialize processes and build the command for runtime exec method to perform docker run
			Process dockerClientProcess = null;
			Process dockerLoadImage = null;
			Process dockerListImages = null;

			MessageConsole console = findConsole("SPLAT");
			MessageConsoleStream out = console.newMessageStream();
			IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
			IWorkbenchPage page = window.getActivePage();
			String id = IConsoleConstants.ID_CONSOLE_VIEW;
			IConsoleView view = (IConsoleView) page.showView(id);
			String s = null;

			// List the available docker images in the local machine and check if the required image exists

			String listDockerImage = "docker image ls";
			dockerListImages = Runtime.getRuntime().exec(listDockerImage);
			BufferedReader stdInp = new BufferedReader(new InputStreamReader(dockerListImages.getInputStream()));
			String s1 = null;
//			dockerListImage.waitFor();
//			int ret_Code = dockerListImage.exitValue();
//			System.out.println("Inspect return code: " + ret_Code);
			while ((s1 = stdInp.readLine()) != null) {
				List<String> tempList = new ArrayList<String>(Arrays.asList(s1.split(" ")));
				tempList.removeAll(Arrays.asList(""));
				for (String item : tempList) {
					if (item.equals(dockerImage)) {
						imageExists = true;
						break;
					}
				}
				if (imageExists) {
					break;
				}
				System.out.println(s1);
				System.out.println(tempList);
			}

			// If the required image does not exist in the local machine then load the image

			if (!imageExists) {
				System.out.println("Loading docker image: " + dockerImage);
				String loadDockerImage = "docker load -i " + "C:/docker_images/splat_image.tar";

				dockerLoadImage = Runtime.getRuntime().exec(loadDockerImage);
				BufferedReader stdErr1 = new BufferedReader(new InputStreamReader(dockerLoadImage.getErrorStream()));

				console = findConsole("SPLAT");
				out = console.newMessageStream();
				window = HandlerUtil.getActiveWorkbenchWindow(event);
				page = window.getActivePage();
				id = IConsoleConstants.ID_CONSOLE_VIEW;
				view = (IConsoleView) page.showView(id);
				view.display(console);

				s = null;
				while ((s = stdErr1.readLine()) != null) {
					out.println(s);
				}
			} else {
				System.out.println("Image " + dockerImage + " is already loaded");
			}

			// build the docker run command

//			List<String> cmds = new ArrayList<>();
			String commands = "";

//			cmds.add("docker run --rm -v");
//			cmds.add(splatPath);
			commands += "docker run --rm -v ";
			commands += dockerMountPath;
//			commands += "C:/Users/shasan1/git/CASE/TA2/Model_Transformations/Filter/Simple_Example/Transformed_Model/json-generated:/user ";
			commands += dockerImage;
			commands += " ";

			// acquiring user preferences and setting them up accordingly for the exec command
			String assuranceLevel = Activator.getDefault().getPreferenceStore()
					.getString(SplatPreferenceConstants.ASSURANCE_LEVEL);
			if (assuranceLevel.equals(SplatPreferenceConstants.ASSURANCE_LEVEL_CAKE)) {
//				cmds.add("cake");
				System.out.println("cake");
			} else if (assuranceLevel.equals(SplatPreferenceConstants.ASSURANCE_LEVEL_HOL)) {
//				cmds.add("hol");
				System.out.println("hol");
			} else if (assuranceLevel.equals(SplatPreferenceConstants.ASSURANCE_LEVEL_FULL)) {
//				cmds.add("full");
				System.out.println("full");
			} else {
//				cmds.add("basic");
				System.out.println("basic");
			}

			if (Activator.getDefault().getPreferenceStore().getBoolean(SplatPreferenceConstants.CHECK_PROPERTIES)) {
//				cmds.add("-checkprops");
				System.out.println("-checkprops");
			}

//			cmds.add("-outdir");
//			cmds.add(Activator.getDefault().getPreferenceStore().getString(SplatPreferenceConstants.OUTPUT_DIRECTORY));
//			cmds.add(":/user");

//			cmds.add("-intwidth");
			commands += "-intwidth ";
//			cmds.add(Integer.toString(
//					Activator.getDefault().getPreferenceStore().getInt(SplatPreferenceConstants.INTEGER_WIDTH)));
			commands += Integer.toString(
					Activator.getDefault().getPreferenceStore().getInt(SplatPreferenceConstants.INTEGER_WIDTH));
			commands += " ";
			if (Activator.getDefault().getPreferenceStore().getBoolean(SplatPreferenceConstants.OPTIMIZE)) {
//				cmds.add("optimize");
				System.out.println("optimize");
			}

//			cmds.add("-endian ");
			commands += "-endian ";
			if (Activator.getDefault().getPreferenceStore().getBoolean(SplatPreferenceConstants.ENDIAN_BIG)) {
//				cmds.add("MSB");
				commands += "MSB ";
			} else {
//				cmds.add("LSB");
				commands += "LSB ";
			}

//			cmds.add("-encoding");
			commands += "-encoding ";
			String encoding = Activator.getDefault().getPreferenceStore().getString(SplatPreferenceConstants.ENCODING);
			if (encoding.equals(SplatPreferenceConstants.ENCODING_UNSIGNED)) {
//				cmds.add("Unsigned");
				commands += "Unsigned ";
			} else if (encoding.equals(SplatPreferenceConstants.ENCODING_SIGN_MAG)) {
//				cmds.add("Sign_mag");
				commands += "Sign_mag ";
			} else if (encoding.equals(SplatPreferenceConstants.ENCODING_ZIGZAG)) {
//				cmds.add("Zigzag");
				commands += "Zigzag ";
			} else {
//				cmds.add("Twos_comp");
				commands += "Twos_comp ";
			}

			if (Activator.getDefault().getPreferenceStore().getBoolean(SplatPreferenceConstants.PRESERVE_MODEL_NUMS)) {
//				cmds.add("-preserve_model_nums");
				commands += "-preserve_model_nums ";
			}

//			cmds.add(jsonPath);
//			cmds.add("Producer_Consume.json");
			commands += jsonPathArrayTemp[jsonPathArrayTemp.length - 1];
//			commands += "Producer_Consume.json";

			System.out.println(commands);
//			String[] commands = cmds.toArray(new String[cmds.size()]);
//			String[] environmentVars = { "LD_LIBRARY_PATH=" + splatDir };

//			Process proc = rt.exec(commands, environmentVars);
//			String c = "docker run --rm -v C:/Users/shasan1/git/CASE/TA2/Model_Transformations/Filter/Simple_Example/Transformed_Model/json-generated:/user splatimg -intwidth 32 -endian LSB -encoding Twos_comp Producer_Consume.json";
//			System.out.println(c);
			dockerClientProcess = Runtime.getRuntime().exec(commands);
			BufferedReader stdErr = new BufferedReader(new InputStreamReader(dockerClientProcess.getErrorStream()));

			console = findConsole("SPLAT");
			out = console.newMessageStream();
//			String cmdLine = "";
//			for (String s : cmds) {
//				cmdLine += s + " ";
//			}
//			cmdLine += "LD_LIBRARY_PATH=" + splatDir;
//			out.println(cmdLine);
			out.println(commands);
			window = HandlerUtil.getActiveWorkbenchWindow(event);
			page = window.getActivePage();
			id = IConsoleConstants.ID_CONSOLE_VIEW;
			view = (IConsoleView) page.showView(id);
			view.display(console);

			s = null;
			while ((s = stdErr.readLine()) != null) {
				out.println(s);
			}

//			int exitVal = proc.waitFor();
			int exitVal = dockerClientProcess.waitFor();
			if (exitVal == 0) {

				// Insert the location of the source code into the filter component implementations in the model
				insertSourceCodeLocation(xtextEditor);

				// update log
				if (Activator.getDefault().getPreferenceStore().getBoolean(SplatPreferenceConstants.GENERATE_LOG)) {
					updateLog();
				}

				out.println("SPLAT completed successfully, GENIUS!.");
			} else {
				out.println("SPLAT has encountered an error and was unable to complete.");
			}

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

		for (AadlPackage pkg : TraverseProject.getPackagesInProject(TraverseProject.getCurrentProject())) {
			AadlPackage aadlPackage = null;
			String filterName = null;
			for (String f : filterDirs) {
				if (f.startsWith(pkg.getName())) {
					aadlPackage = pkg;
					filterName = f.substring(pkg.getName().length() + 1);
					break;
				}
			}

			if (aadlPackage == null || filterName == null) {
				continue;
			}

			List<String> filters = new ArrayList<>();
			if (pkgMap.containsKey(aadlPackage)) {
				filters = pkgMap.get(aadlPackage);
			}
			filters.add(filterName);
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
							String sourceText = outputDir + aadlPackage.getName() + FOLDER_PACKAGE_DELIMITER
									+ ci.getType().getName() + "/" + ci.getType().getName();
							if (implLang.equalsIgnoreCase("c")) {
								sourceText += ".c";
							} else {
								sourceText += ".o";
							}
							Property sourceTextProp = GetProperties.lookupPropertyDefinition(ci,
									ProgrammingProperties._NAME, ProgrammingProperties.SOURCE_TEXT);

							// Get any existing source text already in model
							List<PropertyExpression> currentSource = ci.getPropertyValues(ProgrammingProperties._NAME,
									ProgrammingProperties.SOURCE_TEXT);
							List<StringLiteral> listVal = new ArrayList<>();
							for (PropertyExpression pe : currentSource) {
								if (pe instanceof StringLiteral) {
									StringLiteral source = (StringLiteral) pe;
									if (!source.getValue().equalsIgnoreCase(sourceText)) {
										listVal.add(source);
									}
								}
							}

							StringLiteral sourceTextLit = Aadl2Factory.eINSTANCE.createStringLiteral();
							sourceTextLit.setValue(sourceText);
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

	private void updateLog() {
		Date date = new Date(System.currentTimeMillis());
		String status = "SPLAT completed successfully on " + date + System.lineSeparator();
		File file = new File(
				Activator.getDefault().getPreferenceStore().getString(SplatPreferenceConstants.LOG_FILENAME));
		FileWriter writer;
		try {
			writer = new FileWriter(file, true);
			writer.write(status);
			writer.close();
		} catch (IOException e) {
			Dialog.showWarning("SPLAT", "Unable to write to log file.");
		}

	}

}
