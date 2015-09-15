package com.fp.model;


import org.apache.commons.collections.CollectionUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Scope("prototype")
public class SizingContext extends Persisted {


    List<DataGroup> datagroups = new ArrayList<>();
    List<Process> processes = new ArrayList<>();
    List<Movement> movements = new ArrayList<>();


    public List<DataGroup> getDatagroups() {
        if (datagroups.isEmpty()) {
            repository.getDataGroups(this);
        }
        return datagroups;
    }

    public void setDatagroups(List<DataGroup> datagroups) {
        this.datagroups = datagroups;
    }

    public List<DataGroup> getDataGroupsForProcessId(int processId) {

        List<Movement> movements = getDataMovements();
        List<DataGroup> dgForProcess = new ArrayList<>();

        for (Movement m : movements) {
            if (m.getProcess().getId() == processId) {
                dgForProcess.add(m.getDataGroup());
            }
        }
        return dgForProcess;

    }

    public List<DataGroup> getDataGroupsWithNoMovements() {
        List<Movement> movements = getDataMovements();
        List<DataGroup> dgInMovements = new ArrayList<>();

        for (Movement m : movements) {
            dgInMovements.add(m.getDataGroup());
        }

        List<DataGroup> allDataGroups = getDatagroups();

        return (List<DataGroup>) CollectionUtils.disjunction(allDataGroups, dgInMovements);
    }

    public DataGroup getDataGroup(int id) {
        DataGroup dg = new DataGroup();
        for (DataGroup next : getDatagroups()) {
            if (next.getId() == id) {
                dg = next;
            }
        }

        //todo need to do something smart if I just return a empty datagroup
        return dg;
    }

    public List<Process> getAllProcesses() {

        if (processes.isEmpty()) {
            processes = repository.getProcesses(this);
        }
        return processes;
    }

    public Process getProcess(int Id) {

        Process p = new Process();
        for (Process process : getAllProcesses()) {
            if (process.getId() == Id)
                p = process;

        }

        return p;
    }

    public void saveMovement(Movement m) {

        m.setParent(this);
        repository.saveDataMovements(m);
        movements.clear();
        getDataMovements();
    }

    public List<Movement> getDataMovements() {
        if (movements.isEmpty()) {
            movements = repository.getMovements(this);
        }

        return movements;

    }

    public List<Movement> getMovementsForProcessId(int processId) {

        List<Movement> movements = getDataMovements();
        List<Movement> movementsForProcess = new ArrayList<>();

        for (Movement m : movements) {
            if (m.getProcess().getId() == processId) {
                movementsForProcess.add(m);
            }
        }

        return movementsForProcess;

    }

    public boolean isExistingMovement(int dataGroupId, int subProcessId, int attribId) {

        for (Movement m : getDataMovements()) {
            if (m.getSubProcess().getId() == subProcessId && m.getDataGroup().getId() == dataGroupId) {
                return m.attributeAlreadyMapped(attribId);
            }
        }
        return false;
    }

    public Movement getMovement(int subProcessId, int dataGroupId) {
        Movement m = new Movement();
        for (Movement movement : getDataMovements()) {
            m = movement;
            if (m.getSubProcess().getId() == subProcessId && m.getDataGroup().getId() == dataGroupId) {
                return m;
            }
        }
        return m;

    }

    public void removeMovement(Movement m) {

        movements.remove(m);
    }

    public void setMovements(List<Movement> movements) {
        this.movements = movements;
    }

    public void setProcesses(List<Process> processes) {
        this.processes = processes;
    }
}
