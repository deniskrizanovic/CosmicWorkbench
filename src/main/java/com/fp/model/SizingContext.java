package com.fp.model;


import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
@Scope("prototype")
public class SizingContext extends Persisted {


    List<DataGroup> datagroups = new ArrayList<>();
    List<Process> processes = new ArrayList<>();


    public List<DataGroup> getDatagroups() {
        if (datagroups.isEmpty()) {
            repository.getDataGroups(this);
        }
        return datagroups;
    }

    public void setDatagroups(List<DataGroup> datagroups) {
        this.datagroups = datagroups;
    }

    public DataGroup getDataGroup(int id) {
        DataGroup dg = new DataGroup();
        for (Iterator<DataGroup> i = datagroups.iterator(); i.hasNext(); ) {
            DataGroup next = i.next();
            if (next.getId() == id) {
                dg = next;
            }
        }

        //todo need to do something smart if I just return a empty datagroup
        return dg;
    }


    public DataGroup getDataGroupByName(String name) {
        DataGroup dg = new DataGroup();
        for (Iterator<DataGroup> i = datagroups.iterator(); i.hasNext(); ) {
            DataGroup next = i.next();
            if (next.getName().equals(name)) {
                dg = next;
            }
        }

        //todo need to do something smart if I just return a empty datagroup
        return dg;
    }

    public List<Process> getAllProcesses() {
        return processes;
    }

    public void setProcesses(List<Process> processes) {
        this.processes = processes;
    }

    public Process getProcess(int Id) {

        if (processes.isEmpty()) {
            repository.getProcesses(this);
        }

        Process p = new Process();
        for (Iterator<Process> i = processes.iterator(); i.hasNext(); ) {
            Process process = i.next();
            if (process.getId() == Id)
                p = process;

        }

        return p;
    }

    public void saveDataMovements(DataGroup dg, SubProcess sp, List<String> attribs, String username) {

        repository.saveDataMovements(dg, sp, attribs, username);

    }
}
