package com.fp.domain;

public class DataGroup {

	private long systemContextId;

	private long dataGroupId;

	private int version;

	private String name;

	private String notes;

	private FunctionalModelFunctionalSubProcess functionalModelFunctionalSubProcess;

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

	public long getDataGroupId() {
		return dataGroupId;
	}

	public void setDataGroupId(long dataGroupId) {
		this.dataGroupId = dataGroupId;
	}

	public FunctionalModelFunctionalSubProcess getFunctionalModelFunctionalSubProcess() {
		return functionalModelFunctionalSubProcess;
	}

	public void setFunctionalModelFunctionalSubProcess(
			FunctionalModelFunctionalSubProcess functionalModelFunctionalSubProcess) {
		this.functionalModelFunctionalSubProcess = functionalModelFunctionalSubProcess;
	}

}
