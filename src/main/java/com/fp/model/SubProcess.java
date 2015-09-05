package com.fp.model;

/**
 * Created by denis on 4/09/15.
 */
public class SubProcess extends Persisted{

    String name;
    private Process parent;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setParent(Process parent) {
        this.parent = parent;
    }
}
