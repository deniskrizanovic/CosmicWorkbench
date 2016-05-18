package com.fp.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
@Scope("prototype")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Process extends Persisted {

    String name;
    String description;
    String notes;

    @JsonIgnore
    List<SubProcess> steps = new ArrayList<>();

    @JsonIgnore
    private SizingContext parent;

    public String getName() {
        return name;
    }

    public Process setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<SubProcess> getSteps() {

        if(steps.isEmpty()) {
            steps = repository.getSteps(this);
        }

        return steps;
    }

    public void setSteps(List<SubProcess> steps) {
        this.steps = steps;
    }


    public SubProcess getStep(int Id) {

        SubProcess step = new SubProcess();
        for (Iterator<SubProcess> i = getSteps().iterator(); i.hasNext(); ) {
            SubProcess subProcess = i.next();
            if (subProcess.getId() == Id) {
                step = subProcess;
            }

        }

        return step;
    }

    public SizingContext getParent() {
        return parent;
    }

    public void setParent(SizingContext parent) {
        this.parent = parent;
    }

    @Override
    public String toString() {
        return "Process{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", notes='" + notes + '\'' +
                ", steps=" + steps +
                ", parent=" + parent.getId() +
                '}';
    }
}
