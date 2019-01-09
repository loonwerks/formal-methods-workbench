package com.collins.atc.ace.cyres.toolcheck.plugin;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.osate.aadl2.ModelUnit;
import org.osate.aadl2.impl.AadlPackageImpl;
import org.osate.aadl2.instance.impl.ComponentInstanceImpl;

import com.rockwellcollins.atc.resolute.analysis.execution.EvaluationContext;
import com.rockwellcollins.atc.resolute.analysis.execution.ResoluteExternalAnalysis;
import com.rockwellcollins.atc.resolute.analysis.execution.ResoluteFailException;
import com.rockwellcollins.atc.resolute.analysis.values.BoolValue;
import com.rockwellcollins.atc.resolute.analysis.values.ResoluteValue;

public class ResoluteToolCheck implements ResoluteExternalAnalysis {

	private EvaluationContext evalContext;

	/**
	 * The purpose of this plugin is to check that a specific tool was run on
	 * the latest version of the model.
	 * In order to successfully use this plugin, the path/name of the tool output
	 * must be specified as a preference <tool name>OutputFileName.
	 * Note that <tool name> must be lower case.
	 * @param context - The context for which this plugin was called
	 * @param args - Plugin arguments.  The output filename.
	 */
	@Override
	public ResoluteValue run(EvaluationContext context, List<ResoluteValue> args) {

		this.evalContext = context;

		ResoluteValue arg = args.get(0);
		assert (arg.isString());

		// Make sure this instance is a component
		if (!(evalContext.getThisInstance() instanceof ComponentInstanceImpl)) {
			throw new ResoluteFailException("Resolute ToolCheck can only be called from a component.",
					evalContext.getThisInstance());
		}

		// Get the tool output file
		String outputFileName = Activator.getDefault()
				.getPreferenceStore()
				.getString(arg.getString().toLowerCase() + "OutputFileName");
		if (outputFileName.isEmpty()) {
			throw new ResoluteFailException("Resolute ToolCheck could not determine the output file name of the "
					+ arg.getString() + " tool.  It must be specified in the preferences.",
					evalContext.getThisInstance());
		}
		final File outputFile = new File(outputFileName);

		// If the file doesn't exist, return false
		if (!outputFile.exists()) {
			return new BoolValue(false);
		}

		// Get the date the model was last modified
		final long modelTimestamp = getModelTimestamp();

		// Compare the model timestamp with the tool output timestamp
		if (modelTimestamp <= outputFile.lastModified()) {
			return new BoolValue(true);
		} else {
			return new BoolValue(false);
		}

	}

	/**
	 * Collects timestamps (last modified date) from all files that contain the current model and its references.
	 * Returns the most recent modified date.
	 */
	private long getModelTimestamp() {

		long mostRecentTimestamp = 0;
		final HashMap<String, Long> resourceTimestamps = new HashMap<>();

		// Get the file containing the component instance under evaluation
		final Resource thisResource = evalContext.getThisInstance().getComponentClassifier().eResource();

		// Get the timestamps for this resource, as well as all resources
		// referred to by this resource (recursively)
		collectResourceTimestamps(thisResource, resourceTimestamps);

		// Find the most recently saved file
		for (Long timestamp : resourceTimestamps.values()) {
			if (timestamp > mostRecentTimestamp) {
				mostRecentTimestamp = timestamp;
			}
		}

		return mostRecentTimestamp;

	}

	/**
	 * Recursive function to record the timestamp (last modified date) of a file.  It will then recursively call
	 * itself to record the timestamps of all files that are referred to by the resource.
	 * @param resource - The resource representing the file
	 * @param resourceTimestamps - Set of resources and their last modified date.
	 */
	private void collectResourceTimestamps(Resource resource, HashMap<String, Long> resourceTimestamps) {

		final IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		URI uri = resource.getURI();
		IFile file = workspaceRoot.getFile(new Path(uri.toPlatformString(true)));

		if (uri.segment(0).equals("plugin")) {
			// plugin resources won't have timestamps,
			// so just set it to zero (otherwise it will be IResource.NULL_STAMP)
			resourceTimestamps.put(file.getName(), 0L);
		} else if (!file.isAccessible()) {
			throw new ResoluteFailException("Unable to access file " + file.getName() + ".",
					evalContext.getThisInstance());
		} else if (file.getLocalTimeStamp() == IResource.NULL_STAMP) {
			throw new ResoluteFailException(file.getName() + " does not have a valid modification timestamp.",
					evalContext.getThisInstance());
		} else {
			resourceTimestamps.put(file.getName(), file.getLocalTimeStamp());
		}

		// Get the resources that are referred to (via the 'with' statement)
		EList<EObject> resourceContents = resource.getContents();
		for (EObject eObj : resourceContents) {
			if (eObj instanceof AadlPackageImpl) {
				AadlPackageImpl aadlPackageImpl = (AadlPackageImpl) eObj;

				// Resources included in the public section
				if (aadlPackageImpl.getOwnedPublicSection() != null) {
					final EList<ModelUnit> importedUnits = aadlPackageImpl.getPublicSection().getImportedUnits();
					for (ModelUnit mUnit : importedUnits) {
						String resourceName = mUnit.eResource().getURI().lastSegment();
						if (!resourceTimestamps.containsKey(resourceName)) {
							collectResourceTimestamps(mUnit.eResource(), resourceTimestamps);
						}
					}
				}
				// Resources included in the private section
				if (aadlPackageImpl.getOwnedPrivateSection() != null) {
					final EList<ModelUnit> importedUnits = aadlPackageImpl.getPrivateSection().getImportedUnits();
					for (ModelUnit mUnit : importedUnits) {
						String resourceName = mUnit.eResource().getURI().lastSegment();
						if (!resourceTimestamps.containsKey(resourceName)) {
							collectResourceTimestamps(mUnit.eResource(), resourceTimestamps);
						}
					}
				}
			}
		}

		return;
	}


}

