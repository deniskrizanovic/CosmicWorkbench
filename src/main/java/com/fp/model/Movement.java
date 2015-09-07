package com.fp.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Movement extends Persisted {

    String type; //todo could be an enumeration
    SubProcess subProcess;
    List<DataAttribute> attributes = new ArrayList<>();
    DataGroup dataGroup;

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

        if(attributes.isEmpty())
        {
            if(subProcess == null || dataGroup == null)
            {
                System.out.println("these should not be null!");
            }

            dataGroup.getRepository().getAttributesForMovement(subProcess, dataGroup);
        }

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
}
