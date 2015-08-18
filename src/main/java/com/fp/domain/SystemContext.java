package com.fp.domain;

public class SystemContext {

	private long systemContextId;

	private int version;

	private String name;

	private String notes;

	private String diagram;

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

	@Override
	public String toString() {
		return "SystemContext{" +
				"systemContextId=" + systemContextId +
				", version=" + version +
				", name='" + name + '\'' +
				", notes='" + notes + '\'' +
				", diagram='" + diagram + '\'' +
				'}';
	}

	public String getDiagram() {
		return diagram;
	}

	public void setDiagram(String diagram) {
		this.diagram = diagram;
	}

}
