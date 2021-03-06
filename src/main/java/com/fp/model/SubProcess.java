package com.fp.model;


public class SubProcess extends Persisted{

    String name;
    private Process parent;

    @Override
    public String toString() {
        return "SubProcess{" +
                "name='" + name + '\'' +
                ", parent=" + parent.getName() +
                '}';
    }

    public String getName() {
        return name;
    }

    public SubProcess setName(String name) {
        this.name = name;
        return this;
    }

    public SubProcess setParent(Process parent) {
        this.parent = parent;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        SubProcess that = (SubProcess) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return !(parent != null ? !parent.equals(that.parent) : that.parent != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (parent != null ? parent.hashCode() : 0);
        return result;
    }


    public Process getParent() {
        return parent;
    }
}
