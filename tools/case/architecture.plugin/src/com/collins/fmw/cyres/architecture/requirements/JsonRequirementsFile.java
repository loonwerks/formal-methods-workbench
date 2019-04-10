package com.collins.fmw.cyres.architecture.requirements;

import java.io.File;
import java.util.List;

import com.collins.fmw.cyres.util.plugin.JsonUtil;

public class JsonRequirementsFile {
	private String tool = "";
	private String implementation = "";
	private long date = 0L;
	private String hash = "";
//	private List<JsonRequirement> requirements = null;
	private List<CyberRequirement> requirements = null;

//	public class JsonRequirement {
//		private String type = "";
//		private String text = "";
//		private String context = "";
//
//		public JsonRequirement(String type, String text, String context) {
//			this.type = type;
//			this.text = text;
//			this.context = context;
//		}
//
//		public String getType() {
//			return this.type;
//		}
//
//		public String getText() {
//			return this.text;
//		}
//
//		public String getContext() {
//			return this.context;
//		}
//
//		public void setType(String type) {
//			this.type = type;
//		}
//
//		public void setText(String text) {
//			this.text = text;
//		}
//
//		public void setContext(String context) {
//			this.context = context;
//		}
//	}

	public JsonRequirementsFile(String tool, long date, String implementation, String hash,
			List<CyberRequirement> requirements) {
//	public JsonRequirementsFile(String tool, long date, String implementation, String hash,
//			List<JsonRequirement> requirements) {
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
//	public List<JsonRequirement> getRequirements() {
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
			this.requirements = req.getRequirements();
		} catch (Exception e) {
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
}
