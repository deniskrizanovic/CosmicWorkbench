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

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		FunctionalProcess that = (FunctionalProcess) o;

		if (systemContextId != that.systemContextId) return false;
		if (functionalProcessId != that.functionalProcessId) return false;
		if (version != that.version) return false;
		if (!name.equals(that.name)) return false;
		return notes.equals(that.notes);

	}

	@Override
	public int hashCode()
	{
		int result = (int) (systemContextId ^ (systemContextId >>> 32));
		result = 31 * result + (int) (functionalProcessId ^ (functionalProcessId >>> 32));
		result = 31 * result + version;
		result = 31 * result + name.hashCode();
		result = 31 * result + notes.hashCode();
		return result;
	}
}
