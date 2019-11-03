package com.collins.fmw.cyres.util.plugin;

import java.util.HashSet;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.osate.aadl2.AadlPackage;
import org.osate.aadl2.Element;
import org.osate.aadl2.ModelUnit;
import org.osate.aadl2.modelsupport.util.AadlUtil;
import org.osate.workspace.WorkspacePlugin;

public class TraverseProject {

	public static IProject getCurrentProject() {
		// Eclipse does not have the concept of a "current project"
		// So we will use the project associated with the file that is currently
		// visible in the editor
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage activePage = window.getActivePage();
		IEditorPart activeEditor = activePage.getActiveEditor();
		IProject project = null;

		if (activeEditor != null) {
			IEditorInput input = activeEditor.getEditorInput();

			project = input.getAdapter(IProject.class);
			if (project == null) {
				IResource resource = input.getAdapter(IResource.class);
				if (resource != null) {
					project = resource.getProject();
				}
			}
		}
		return project;
	}

	public static HashSet<IFile> getAadlandInstanceFilesInProject(IProject project) {
		HashSet<IFile> result = new HashSet<IFile>();
		try {
			IContainer container = project;
			if (container != null && !container.getName().startsWith(".")) {
				getFiles(container.members(), result, WorkspacePlugin.SOURCE_FILE_EXT);
				getFiles(container.members(), result, WorkspacePlugin.SOURCE_FILE_EXT2);
				getFiles(container.members(), result, WorkspacePlugin.INSTANCE_FILE_EXT);
			}
		} catch (CoreException e) {
			WorkspacePlugin.log(e);
		}
		return result;
	}

	public static HashSet<IFile> getAadlFilesInProject(IProject project) {
		HashSet<IFile> result = new HashSet<IFile>();
		try {
			IContainer container = project;
			if (container != null && !container.getName().startsWith(".")) {
				getFiles(container.members(), result, WorkspacePlugin.SOURCE_FILE_EXT);
				getFiles(container.members(), result, WorkspacePlugin.SOURCE_FILE_EXT2);
			}
		} catch (CoreException e) {
			WorkspacePlugin.log(e);
		}
		return result;
	}

	public static HashSet<IFile> getInstanceModelFilesInProject(IProject project) {
		HashSet<IFile> result = new HashSet<IFile>();
		try {
			IContainer container = project;
			if (container != null && !container.getName().startsWith(".")) {
				getFiles(container.members(), result, WorkspacePlugin.INSTANCE_FILE_EXT);
			}
		} catch (CoreException e) {
			WorkspacePlugin.log(e);
		}
		return result;
	}

	public static final EList<ModelUnit> getModelUnitsInProject(IProject project) {
		EList<ModelUnit> result = new BasicEList<ModelUnit>();
		HashSet<IFile> files = getAadlFilesInProject(project);
		for (IFile file : files) {
			ModelUnit target = (ModelUnit) AadlUtil.getElement(file);
			if (target != null) {
				result.add(target);
			}
		}
		return result;
	}

	public static final EList<AadlPackage> getPackagesInProject(IProject project) {
		EList<AadlPackage> result = new BasicEList<AadlPackage>();
		for (ModelUnit u : getModelUnitsInProject(project)) {
			if (u instanceof AadlPackage) {
				result.add((AadlPackage) u);
			}
		}
		return result;
	}

	public static AadlPackage getPackageInFile(IFile file) {
		Element target = AadlUtil.getElement(file);
		return (target instanceof AadlPackage) ? ((AadlPackage) target) : null;
	}

	private static HashSet<IFile> getFiles(IResource[] resources, HashSet<IFile> result, String extension) {
		try {
			for (int i = 0; i < resources.length; i++) {
				if (resources[i] instanceof IFile) {
					IFile file = (IFile) resources[i];
					String ext = file.getFileExtension();
					if (ext != null) {
						if ((extension.equalsIgnoreCase(WorkspacePlugin.SOURCE_FILE_EXT)
								&& (ext.equalsIgnoreCase(WorkspacePlugin.SOURCE_FILE_EXT))
								|| (extension.equalsIgnoreCase(WorkspacePlugin.SOURCE_FILE_EXT2)
										&& ext.equalsIgnoreCase(WorkspacePlugin.SOURCE_FILE_EXT2)))) {
							result.add((IFile) resources[i]);
						}
						// looking for old style instance file names (i.e., extension aaxl2
						if (extension.equalsIgnoreCase(WorkspacePlugin.MODEL_FILE_EXT)
								&& ext.equalsIgnoreCase(WorkspacePlugin.MODEL_FILE_EXT)
								&& file.getName().endsWith(WorkspacePlugin.INSTANCE_MODEL_POSTFIX)) {
							result.add((IFile) resources[i]);
						}
						// looking for new style file name extension (aail2) and finding old style instance file names.
						if (extension.equalsIgnoreCase(WorkspacePlugin.INSTANCE_FILE_EXT)
								&& ext.equalsIgnoreCase(WorkspacePlugin.MODEL_FILE_EXT)
								&& file.getName().endsWith(WorkspacePlugin.INSTANCE_MODEL_POSTFIX)) {
							result.add((IFile) resources[i]);
						}
						// looking for and finding new style instance file names
						if (extension.equalsIgnoreCase(WorkspacePlugin.INSTANCE_FILE_EXT)
								&& ext.equalsIgnoreCase(WorkspacePlugin.INSTANCE_FILE_EXT)) {
							result.add((IFile) resources[i]);
						}
					}
				} else if (resources[i] instanceof IContainer) {
					IContainer cont = (IContainer) resources[i];
					if (!cont.getName().startsWith(".")) {
						getFiles(cont.members(), result, extension);
					}
				}
			}
		} catch (CoreException e) {
			WorkspacePlugin.log(e);
		}
		return result;
	}

}
