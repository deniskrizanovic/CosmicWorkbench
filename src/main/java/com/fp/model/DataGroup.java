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

    public DataGroup setName(String name) {
        this.name = name;
        return this;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public SizingContext getParent() {
        return parent;
    }

    public void setParent(SizingContext parent) {
        this.parent = parent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;


        DataGroup dataGroup = (DataGroup) o;

        if (attributes != null ? !attributes.equals(dataGroup.attributes) : dataGroup.attributes != null) return false;
        if (name != null ? !name.equals(dataGroup.name) : dataGroup.name != null) return false;
        if (notes != null ? !notes.equals(dataGroup.notes) : dataGroup.notes != null) return false;
        return !(parent != null ? !parent.equals(dataGroup.parent) : dataGroup.parent != null);

    }

    @Override
    public int hashCode() {
        int result = attributes != null ? attributes.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (notes != null ? notes.hashCode() : 0);
        result = 31 * result + (parent != null ? parent.hashCode() : 0);
        return result;
    }
}
