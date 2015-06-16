package com.fp.domain;

import java.util.List;

public class FunctionalModel {
	
	private long functionalModelId;
	
	private long functionalProcessId;
	
	private String functionalProcessName;
	
	private long functionalSubProcessId;
	
	private String functionalSubProcessName;

	private long dataGroupId;
	
	private String dataGroupName;
	
	private long datafieldId;
	
	private int version;
	
	private String notes;

	private String grade;
	
	private List<FunctionalModelFunctionalSubProcess> functionalModelFunctionalSubProcess;
	
	private int score;

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
	
	public String getFunctionalSubProcessName() {
		return functionalSubProcessName;
	}

	public void setFunctionalSubProcessName(String functionalSubProcessName) {
		this.functionalSubProcessName = functionalSubProcessName;
	}

	public long getDatafieldId() {
		return datafieldId;
	}

	public void setDatafieldId(long datafieldId) {
		this.datafieldId = datafieldId;
	}

	public long getFunctionalProcessId() {
		return functionalProcessId;
	}

	public void setFunctionalProcessId(long functionalProcessId) {
		this.functionalProcessId = functionalProcessId;
	}

	public long getDataGroupId() {
		return dataGroupId;
	}

	public void setDataGroupId(long dataGroupId) {
		this.dataGroupId = dataGroupId;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public String getFunctionalProcessName() {
		return functionalProcessName;
	}

	public void setFunctionalProcessName(String functionalProcessName) {
		this.functionalProcessName = functionalProcessName;
	}

	public String getDataGroupName() {
		return dataGroupName;
	}

	public void setDataGroupName(String dataGroupName) {
		this.dataGroupName = dataGroupName;
	}

	
	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public long getFunctionalSubProcessId() {
		return functionalSubProcessId;
	}

	public void setFunctionalSubProcessId(long functionalSubProcessId) {
		this.functionalSubProcessId = functionalSubProcessId;
	}
	
	public List<FunctionalModelFunctionalSubProcess> getFunctionalModelFunctionalSubProcess() {
		return functionalModelFunctionalSubProcess;
	}

	public void setFunctionalModelFunctionalSubProcess(
			List<FunctionalModelFunctionalSubProcess> functionalModelFunctionalSubProcess) {
		this.functionalModelFunctionalSubProcess = functionalModelFunctionalSubProcess;
	}

}
