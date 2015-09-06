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


}
