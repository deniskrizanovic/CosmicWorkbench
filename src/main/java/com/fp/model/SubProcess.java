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

    public SubProcess setName(String name) {
        this.name = name;
        return this;
    }

    public void setParent(Process parent) {
        this.parent = parent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SubProcess that = (SubProcess) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return !(parent != null ? !parent.equals(that.parent) : that.parent != null);

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (parent != null ? parent.hashCode() : 0);
        return result;
    }
}
