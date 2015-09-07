package com.fp.model;


import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
@Scope("prototype")
public class Process extends Persisted {

    String name;
    String description;
    String notes;
    List<SubProcess> steps = new ArrayList<>();
    private SizingContext parent;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        return steps;
    }

    public void setSteps(List<SubProcess> steps) {
        this.steps = steps;
    }


    public SubProcess getStep(int Id) {
        if (steps.isEmpty()) {
            steps = repository.getSteps(this);
        }

        SubProcess step = new SubProcess();
        for (Iterator<SubProcess> iterator = steps.iterator(); iterator.hasNext(); ) {
            SubProcess subProcess = iterator.next();
            if (subProcess.getId() == Id) {
                step = subProcess;
            }

        }

        return step;
    }


    public void setParent(SizingContext parent) {
        this.parent = parent;
    }
}
