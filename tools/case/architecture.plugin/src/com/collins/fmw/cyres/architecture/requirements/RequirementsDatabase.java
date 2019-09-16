package com.collins.fmw.cyres.architecture.requirements;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.osate.ui.dialogs.Dialog;

import com.collins.fmw.cyres.architecture.utils.CaseUtils;

/**
 * A database for cyber-security requirements.
 * @author jbabar {Junaid Babar}
 *
 */
public class RequirementsDatabase {

	private static RequirementsDatabase reqDb = new RequirementsDatabase();

	private static String makeKey(CyberRequirement req) {
		return req.getType() + req.getContext();
	}

	private static String makeKey(String type, String context) {
		return type + context;
	}

//	private Date modelModifiedAt;
	private HashMap<String, CyberRequirement> requirements;

	protected RequirementsDatabase() {
//		modelModifiedAt = new Date();
		requirements = new HashMap<String, CyberRequirement>();
		readRequirementsDatabase();
	}

	public void reset() {
//		modelModifiedAt = new Date();
		requirements.clear();
		// TODO: wipe the physical file as well?
//		final File reqFile = new File(CaseUtils.CASE_REQUIREMENTS_DATABASE_FILE);
//		if (reqFile.isFile()) {
//			reqFile.delete();
//		}
	}

	protected void readRequirementsDatabase() {
		// Read database from the physical requirements database file
		final File reqFile = new File(CaseUtils.CASE_REQUIREMENTS_DATABASE_FILE);
		JsonRequirementsFile jsonReqFile = new JsonRequirementsFile();
		if (!jsonReqFile.importFile(reqFile)) {
			Dialog.showInfo("Missing requirements database",
					"No requirements database found. Starting a new database.");
		} else {
			// Add the requirements in this file to the accumulated list of requirements
			importRequirements(jsonReqFile.getRequirements());
		}
	}

	public void saveRequirementsDatabase() {
		// Write database to physical requirements database file
		final File reqFile = new File(CaseUtils.CASE_REQUIREMENTS_DATABASE_FILE);
		JsonRequirementsFile jsonReqFile = new JsonRequirementsFile(CyberRequirement.notApplicable,
				new Date().getTime(), CyberRequirement.notApplicable, CyberRequirement.notApplicable,
				getRequirements());
		if (!jsonReqFile.exportFile(reqFile)) {
			throw new RuntimeException("Could not save cyber requirements file " + reqFile.getName() + ".");
		}
	}

	public void importJsonRequrementsFiles(List<JsonRequirementsFile> reqFiles) {
		reqFiles.forEach(file -> {
			importRequirements(file.getRequirements());
		});
	}

	public void importRequirements(List<CyberRequirement> reqs) {
		// Add reqs to the database of requirements
		// Note: if requirement already exists, it will get updated
		reqs.forEach(e -> requirements.put(makeKey(e), e));
	}

	public void updateRequirement(CyberRequirement req) {
		requirements.put(makeKey(req), req);
	}

	public boolean contains(CyberRequirement req) {
		return requirements.containsKey(makeKey(req));
	}

	public boolean contains(String type, String context) {
		return requirements.containsKey(makeKey(type, context));
	}

	public CyberRequirement get(CyberRequirement req) {
		return requirements.get(makeKey(req));
	}

	public CyberRequirement get(String type, String context) {
		return requirements.get(makeKey(type, context));
	}

	public List<CyberRequirement> getRequirements() {
		List<CyberRequirement> list = new ArrayList<CyberRequirement>();
		requirements.values().forEach(e -> list.add(new CyberRequirement(e)));
		return list;
	}

	public List<CyberRequirement> getRequirements(final String filterString) {
		List<CyberRequirement> list = new ArrayList<CyberRequirement>();
		requirements.values().forEach(r -> {
			// Note: using "==" instead of equals since filter strings are constants defined in CyberRequirement
			if (r.getId() == filterString) {
				list.add(new CyberRequirement(r));
			}
		});
		return list;
	}

	public final List<CyberRequirement> getOmittedRequirements() {
		return getRequirements(CyberRequirement.omit);
	}

	public List<CyberRequirement> getToDoRequirements() {
		return getRequirements(CyberRequirement.toDo);
	}

	public List<CyberRequirement> getAddRequirements() {
		return getRequirements(CyberRequirement.add);
	}

	public List<CyberRequirement> getAddPlusAgreeRequirements() {
		return getRequirements(CyberRequirement.addPlusAgree);
	}

	public List<CyberRequirement> getImportedRequirements() {
		List<CyberRequirement> list = getAddRequirements();
		list.addAll(getAddPlusAgreeRequirements());
		return list;
	}

	public static RequirementsDatabase getInstance() {
		return reqDb;
	}

}
