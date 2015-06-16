package com.fp.domain;

public class DataField {
	
	private long dataGroupId;
	
	private long dataFieldId;

	private int version;

	private String name;

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

	public long getDataGroupId() {
		return dataGroupId;
	}

	public void setDataGroupId(long dataGroupId) {
		this.dataGroupId = dataGroupId;
	}

	public long getDataFieldId() {
		return dataFieldId;
	}

	public void setDataFieldId(long dataFieldId) {
		this.dataFieldId = dataFieldId;
	}

}
