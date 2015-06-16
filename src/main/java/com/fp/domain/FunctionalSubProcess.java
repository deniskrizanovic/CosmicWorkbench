package com.fp.domain;

import java.util.List;

public class FunctionalSubProcess {
	
	private long functionalProcessId;
	
	private long functionalSubProcessId;

	private int version;

	private String name;
	
	List<FunctionalModel> functionalModel;

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

	public long getFunctionalProcessId() {
		return functionalProcessId;
	}

	public List<FunctionalModel> getFunctionalModel() {
		return functionalModel;
	}

	public void setFunctionalModel(List<FunctionalModel> functionalModel) {
		this.functionalModel = functionalModel;
	}

	public void setFunctionalProcessId(long functionalProcessId) {
		this.functionalProcessId = functionalProcessId;
	}

	public long getFunctionalSubProcessId() {
		return functionalSubProcessId;
	}

	public void setFunctionalSubProcessId(long functionalSubProcessId) {
		this.functionalSubProcessId = functionalSubProcessId;
	}

}
