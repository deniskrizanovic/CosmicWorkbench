package com.fp.domain;

public class FunctionalProcess {

	private long systemContextId;
	
	private long functionalProcessId;

	private int version;

	private String name;

	private String notes;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public long getSystemContextId() {
		return systemContextId;
	}

	public void setSystemContextId(long systemContextId) {
		this.systemContextId = systemContextId;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public long getFunctionalProcessId() {
		return functionalProcessId;
	}

	public void setFunctionalProcessId(long functionalProcessId) {
		this.functionalProcessId = functionalProcessId;
	}
}
