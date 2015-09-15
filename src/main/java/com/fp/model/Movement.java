package com.fp.model;

import java.util.ArrayList;
import java.util.List;

public class Movement extends Persisted {

    String type;
    SubProcess subProcess;
    List<DataAttribute> attributes = new ArrayList<>();
    DataGroup dataGroup;
    private SizingContext parent;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public SubProcess getSubProcess() {
        return subProcess;
    }

    public Movement setSubProcess(SubProcess subProcess) {
        this.subProcess = subProcess;
        return this;
    }

    public List<DataAttribute> getAttributes() {

        return attributes;
    }

    public void setAttributes(List<DataAttribute> attributes) {
        this.attributes = attributes;
    }

    public DataGroup getDataGroup() {
        return dataGroup;
    }

    public Movement setDataGroup(DataGroup dg) {
        dataGroup = dg;
        return this;

    }

    public boolean attributeAlreadyMapped(int attribId)
    {
        for (DataAttribute next : attributes) {
            if (next.getId() == attribId) {
                return true;
            }
        }
        return false;
    }

    public void addAttribute(DataAttribute attrib) {
      attributes.add(attrib);
    }

    public boolean isEntry()
    {
        return type.equals("E");

    }

    public boolean isExit()
    {
        return type.equals("X");

    }

    public boolean isRead()
    {
        return type.equals("R");

    }

    public boolean isWrite()
    {
        return type.equals("W");

    }

    public SizingContext getParent() {
        return parent;
    }


    public Movement setParent(SizingContext parent) {
        this.parent = parent;
        return this;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;


        Movement movement = (Movement) o;

        if (type != null ? !type.equals(movement.type) : movement.type != null) return false;
        if (subProcess != null ? !subProcess.equals(movement.subProcess) : movement.subProcess != null) return false;
        if (attributes != null ? !attributes.equals(movement.attributes) : movement.attributes != null) return false;
        if (dataGroup != null ? !dataGroup.equals(movement.dataGroup) : movement.dataGroup != null) return false;
        return !(parent != null ? !parent.equals(movement.parent) : movement.parent != null);

    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (subProcess != null ? subProcess.hashCode() : 0);
        result = 31 * result + (attributes != null ? attributes.hashCode() : 0);
        result = 31 * result + (dataGroup != null ? dataGroup.hashCode() : 0);
        result = 31 * result + (parent != null ? parent.hashCode() : 0);
        return result;
    }
}


