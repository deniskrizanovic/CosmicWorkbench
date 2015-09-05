package com.fp.model;

public class DataAttribute extends Persisted {

    String name;
    DataGroup parent;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DataGroup getParent() {
        return parent;
    }

    public void setParent(DataGroup parent) {
        this.parent = parent;
    }
}
