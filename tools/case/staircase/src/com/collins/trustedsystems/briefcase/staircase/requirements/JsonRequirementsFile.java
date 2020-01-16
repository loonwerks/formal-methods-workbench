package com.collins.trustedsystems.briefcase.staircase.requirements;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.collins.trustedsystems.briefcase.util.JsonUtil;

public class JsonRequirementsFile {
	private String tool = "";
	private String implementation = "";
	private long date = 0L;
	private String hash = "";
	private List<CyberRequirement> requirements = null;

	public JsonRequirementsFile(String tool, long date, String implementation, String hash,
			List<CyberRequirement> requirements) {
		this.tool = tool;
		this.implementation = implementation;
		this.date = date;
		this.hash = hash;
		this.requirements = requirements;
	}

	public JsonRequirementsFile() {

	}

	public String getTool() {
		return this.tool;
	}

	public String getImplementation() {
		return this.implementation;
	}

	public long getDate() {
		return this.date;
	}

	public String getHash() {
		return this.hash;
	}

	public List<CyberRequirement> getRequirements() {
		if (this.requirements == null) {
			this.requirements = new ArrayList<CyberRequirement>();
		}
		return this.requirements;
	}

	public boolean importFile(File file) {
		try {
			JsonUtil<JsonRequirementsFile> json = new JsonUtil<JsonRequirementsFile>(JsonRequirementsFile.class);
			JsonRequirementsFile req = json.readObject(file);
			this.tool = req.tool;
			if (this.tool == null) {
				this.tool = "";
			}
			this.date = req.date;
			this.hash = req.hash;
			req.getRequirements().forEach(r -> {
				if (this.requirements == null) {
					this.requirements = new ArrayList<CyberRequirement>();
				}
				final long date = (r.getDate() == 0L ? this.date : r.getDate());
				final String tool = (r.getTool().isEmpty() || r.getTool() == CyberRequirement.unknown ? this.tool
						: r.getTool());
				this.requirements.add(new CyberRequirement(date, tool, r.getStatus(), r.getType(), r.getId(),
						r.getText(), r.getContext(), r.getRationale()));
			});
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean exportFile(File file) {
		try {
			JsonUtil<JsonRequirementsFile> json = new JsonUtil<JsonRequirementsFile>(JsonRequirementsFile.class);
			json.writeObject(this, file);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * Removes requirements from jsonFile if they appear in reqList
	 * @param reqList
	 */
	public void removeRequirements(final List<CyberRequirement> reqList) {
//		Iterator<CyberRequirement> i = getRequirements().iterator();
//		while (i.hasNext()) {
//			CyberRequirement jsonReq = i.next();
//			for (CyberRequirement req : reqList) {
//				if (req.getType().equalsIgnoreCase(jsonReq.getType())
//						&& req.getContext().equalsIgnoreCase(jsonReq.getContext())) {
//					i.remove();
//					break;
//				}
//			}
//		}

		reqList.forEach(r -> removeRequirement(r));
	}

	private void removeRequirement(final CyberRequirement req) {
		for (Iterator<CyberRequirement> reqIter = getRequirements().iterator(); reqIter.hasNext();) {
			CyberRequirement jsonReq = reqIter.next();
			if (equals(jsonReq, req)) {
				reqIter.remove();
				return;
			}
		}
	}

	private boolean equals(final CyberRequirement req1, final CyberRequirement req2) {
		return req1.getType().equalsIgnoreCase(req2.getType()) && req1.getContext().equalsIgnoreCase(req2.getContext());
	}

}
