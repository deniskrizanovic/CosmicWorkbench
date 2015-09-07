package com.fp.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Movement extends Persisted {

    String type; //todo could be an enumeration
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

    public void setSubProcess(SubProcess subProcess) {
        this.subProcess = subProcess;
    }

    public List<DataAttribute> getAttributes() {

        return attributes;
    }

    public void setAttributes(List<DataAttribute> attributes) {
        this.attributes = attributes;
    }


    public void setDataGroup(DataGroup dg) {
        dataGroup = dg;

    }

    public DataGroup getDataGroup() {
        return dataGroup;
    }

    public boolean attributeAlreadyMapped(int attribId)
    {
        for (Iterator<DataAttribute> attributeIterator = attributes.iterator(); attributeIterator.hasNext(); ) {
            DataAttribute next = attributeIterator.next();
            if(next.getId()==attribId){
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

    public void setParent(SizingContext parent) {
        this.parent = parent;
    }
}
