package com.collins.atc.ace.cyres.architecture.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.osate.ui.dialogs.Dialog;

public class DockerClient extends Job {

	private String dockerImage = null;
	private String dockerContainerPort = null;
	private String dockerContainerName = null;
	private ExecutionEvent event = null;
	private boolean isStarted = false;

	public DockerClient(String dockerImage, String dockerContainerPort, String dockerContainerName,
			ExecutionEvent event) {
		super("Docker Client");
		this.dockerImage = dockerImage;
		this.dockerContainerPort = dockerContainerPort;
		this.dockerContainerName = dockerContainerName;
		this.event = event;
	}

	public boolean isStarted() {
		return isStarted;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {

		// TODO: Load Image
		monitor.beginTask("Docker Client", IProgressMonitor.UNKNOWN);
		monitor.subTask("Load Image");
		if (!loadDockerImage(monitor)) {
			Dialog.showError("Docker error", "No Docker image has been loaded.");
			return Status.CANCEL_STATUS;
		}

		// TODO: Run
		monitor.subTask("Run Container");
		return runDockerContainer(monitor);

	}

	private boolean loadDockerImage(IProgressMonitor monitor) {

		if (dockerImage == null) {
			return false;
		}

		boolean imageLoaded = false;
		Process loadImage = null;
		final String loadString = "docker load -i " + dockerImage;

		try {
			loadImage = Runtime.getRuntime().exec(loadString);
			BufferedReader stdErr = new BufferedReader(new InputStreamReader(loadImage.getErrorStream()));

			String daemonOutput = null;
			while (loadImage.isAlive() && !monitor.isCanceled()) {
				// Display output
				while ((daemonOutput = stdErr.readLine()) != null) {
					System.out.println(daemonOutput);
				}
				// TODO: update monitor?

			}
			imageLoaded = true;
		} catch (IOException e) {
			Dialog.showError("Docker error", "Could not load the Docker image.");
		} finally {
			loadImage.destroy();
			// TODO: monitor?
		}

		return imageLoaded;
	}

	private IStatus runDockerContainer(IProgressMonitor monitor) {

		Process dockerClientProcess = null;
		String loadString = "docker run --rm -p " + dockerContainerPort + " " + dockerContainerName;
		try {
			dockerClientProcess = Runtime.getRuntime().exec(loadString);
			isStarted = true;
			BufferedReader stdErr = new BufferedReader(new InputStreamReader(dockerClientProcess.getErrorStream()));
//			MessageConsole console = findConsole("Docker Client");
//			MessageConsoleStream out = console.newMessageStream();
//			IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
//			IWorkbenchPage page = window.getActivePage();
//			String id = IConsoleConstants.ID_CONSOLE_VIEW;
//			IConsoleView view = (IConsoleView) page.showView(id);
//			view.display(console);
			String clientOutput = null;
			while (dockerClientProcess.isAlive() && !monitor.isCanceled()) {
				// Display output
				while ((clientOutput = stdErr.readLine()) != null) {
					System.out.println(clientOutput);
				}

			}
		} catch (IOException e) {
			Dialog.showError("Docker error", "Could not start the docker container.");
			return Status.CANCEL_STATUS;
//		} catch (PartInitException e) {
//			Dialog.showError("Docker error", "Could not initialize console to display output.");
//			return Status.CANCEL_STATUS;
		} finally {
			dockerClientProcess.destroy();
		}

		return Status.OK_STATUS;

	}

	// This needs to be part of a utility package
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

}
