package com.fp.domain;

public class FunctionalModelDataField {
	
	private long functionalModelDataFieldId;
	
	private long functionalModelId;
	
	private long dataGroupId;
	
	private long datafieldId;
	
	private int version;
	
	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public long getFunctionalModelId() {
		return functionalModelId;
	}

	public void setFunctionalModelId(long functionalModelId) {
		this.functionalModelId = functionalModelId;
	}

	public long getDatafieldId() {
		return datafieldId;
	}

	public void setDatafieldId(long datafieldId) {
		this.datafieldId = datafieldId;
	}

	public long getDataGroupId() {
		return dataGroupId;
	}

	public void setDataGroupId(long dataGroupId) {
		this.dataGroupId = dataGroupId;
	}

	public long getFunctionalModelDataFieldId() {
		return functionalModelDataFieldId;
	}

	public void setFunctionalModelDataFieldId(long functionalModelDataFieldId) {
		this.functionalModelDataFieldId = functionalModelDataFieldId;
	}


}
