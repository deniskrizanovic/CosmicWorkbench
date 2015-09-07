package com.fp.model;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Scope("prototype")
public class DataGroup extends Persisted{

    List<DataAttribute> attributes = new ArrayList<>();
    String name;
    private String notes;


    private SizingContext parent;

    public List<DataAttribute> getAttributes() {

        if(attributes.isEmpty())
        {
            attributes = repository.getAttributes(this);
        }


        return attributes;
    }

    public void setAttributes(List<DataAttribute> attributes) {
        this.attributes = attributes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setParent(SizingContext parent) {
        this.parent = parent;
    }

    public SizingContext getParent() {
        return parent;
    }


    public void saveDataMovements(SubProcess sp, List<String> attributeIds, String type, String username) {

        repository.saveDataMovements(this, sp, attributeIds, type, username);
    }
}
