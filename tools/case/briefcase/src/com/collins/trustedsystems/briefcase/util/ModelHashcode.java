package com.collins.trustedsystems.briefcase.util;

import java.io.InputStream;
import java.io.SequenceInputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.osate.aadl2.AadlPackage;
import org.osate.aadl2.Element;
import org.osate.aadl2.ModelUnit;

public class ModelHashcode {

	/**
	 * Collects files that contain the current model and its references.
	 * @return String - A combined hash for all model files.
	 * @throws Exception
	 */
	public static String getHashcode(Element root) throws Exception {

		SortedSet<IFile> files = new TreeSet<IFile>((f1, f2) -> f2.getName().compareTo(f1.getName()));

		// Get the file containing the component instance under evaluation
		final Resource thisResource = root.eResource();
		if (thisResource == null) {
			throw new Exception("Could not generate hashcode because resource not found.");
		}

		// Get the hash for this resource, as well as all resources
		// referred to by this resource (recursively)
		collectResourceFiles(thisResource, files);

		MessageDigest md = MessageDigest.getInstance("MD5");
		Vector<InputStream> fileStreams = new Vector<>();
		for (IFile f : files) {
			fileStreams.add(f.getContents());
		}

		DigestInputStream dis = null;
		try {
			SequenceInputStream sequenceInputStream = new SequenceInputStream(fileStreams.elements());
			dis = new DigestInputStream(sequenceInputStream, md);
			dis.on(true);
			while (dis.read() >= 0) {
			}
		} catch (Exception e) {
			throw e;
		} finally {
			dis.close();
		}

		return new String(md.digest()).trim();
	}

	/**
	 * Recursive function to recursively collect all referenced files of the current resource.
	 * @param resource - The resource representing the file.
	 * @param files - Set of resource path/file names.
	 * @throws Exception
	 */
	private static void collectResourceFiles(Resource resource, Set<IFile> files) throws Exception {

		final IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		URI uri = resource.getURI();
		IFile file = workspaceRoot.getFile(new Path(uri.toPlatformString(true)));

		if (uri.segment(0).equals("plugin")) {
			// We're going to ignore these
			return;
		} else if (!file.isAccessible() || !file.exists()) {
			throw new Exception("Model hashcode generation: Unable to access file " + file.getName() + ".");
		} else {
			files.add(file);
		}

		// Get the resources that are referred to (via the 'with' statement)
		EList<EObject> resourceContents = resource.getContents();
		for (EObject eObj : resourceContents) {
			if (eObj instanceof AadlPackage) {
				AadlPackage aadlPackage = (AadlPackage) eObj;

				// Resources included in the public section
				if (aadlPackage.getOwnedPublicSection() != null) {
					final EList<ModelUnit> importedUnits = aadlPackage.getPublicSection().getImportedUnits();
					for (ModelUnit mUnit : importedUnits) {
						IFile resourceFile = workspaceRoot
								.getFile(new Path(mUnit.eResource().getURI().toPlatformString(true)));
						if (!files.contains(resourceFile)) {
							collectResourceFiles(mUnit.eResource(), files);
						}
					}
				}
				// Resources included in the private section
				if (aadlPackage.getOwnedPrivateSection() != null) {
					final EList<ModelUnit> importedUnits = aadlPackage.getPrivateSection().getImportedUnits();
					for (ModelUnit mUnit : importedUnits) {
						IFile resourceFile = workspaceRoot
								.getFile(new Path(mUnit.eResource().getURI().toPlatformString(true)));
						if (!files.contains(resourceFile)) {
							collectResourceFiles(mUnit.eResource(), files);
						}
					}
				}
			}
		}

		return;
	}
}
