package com.fp.model;

import java.util.Date;
import java.util.List;

public class Movement extends Persisted{

    String type; //todo could be an enumeration
    SubProcess subProcess;
    List<DataAttribute> attributes;
    int version;
    String createdBy;
    Date createdTime;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public SubProcess getSubProcess() {
        return subProcess;
    }

    public void setSubProcess(SubProcess subProcess) {
        this.subProcess = subProcess;
    }

    public List<DataAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<DataAttribute> attributes) {
        this.attributes = attributes;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }
}
