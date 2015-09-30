package com.fp.model;


import org.apache.commons.collections.CollectionUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Scope("prototype")
public class SizingContext extends Persisted {


    List<DataGroup> datagroups = new ArrayList<>();
    List<Process> processes = new ArrayList<>();
    List<Movement> movements = new ArrayList<>();


    public List<DataGroup> getDataGroups() {
        if (datagroups.isEmpty()) {
            repository.getDataGroups(this);
        }
        return datagroups;
    }

    public void setDatagroups(List<DataGroup> datagroups) {
        this.datagroups = datagroups;
    }

    public Set<DataGroup> getDataGroupsForProcessId(int processId) {

        List<Movement> movements = getMovements();
        Set<DataGroup> dgForProcess = new HashSet<>();

        for (Movement m : movements) {
            if (m.getProcess().getId() == processId) {
                dgForProcess.add(m.getDataGroup());
            }
        }
        return dgForProcess;

    }

    public List<DataGroup> getDataGroupsWithNoMovements(int processId) {
        List<Movement> movements = getMovements();
        List<DataGroup> dgInMovements = new ArrayList<>();

        for (Movement m : movements) {
            if (m.getProcess().getId() == processId) {
                dgInMovements.add(m.getDataGroup());
            }
        }

        List<DataGroup> allDataGroups = getDataGroups();

        return (List<DataGroup>) CollectionUtils.disjunction(allDataGroups, dgInMovements);
    }

    public DataGroup getDataGroup(int id) {
        DataGroup dg = new DataGroup();
        for (DataGroup next : getDataGroups()) {
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

    public Object[][] getDataToSubProcessMappingAsGrid(int processId) {

        Set<DataGroup> allGroupsForProcess = getDataGroupsForProcessId(processId);
        List<SubProcess> allStepsForProcess = getProcess(processId).getSteps();
        //the grid accommodates a header row and a header column on the left
        Object[][] grid = new Object[allStepsForProcess.size() + 1][allGroupsForProcess.size() + 1];

        //setup first line
        grid[0][0] = ""; //this is the first blank one

        int colIndexForHeadingRow = 1;
        for (Iterator<DataGroup> dgi = allGroupsForProcess.iterator(); dgi.hasNext(); ) {
            DataGroup dataGroup = dgi.next();
            grid[0][colIndexForHeadingRow] = dataGroup.getName();
            colIndexForHeadingRow++;
        }


        int rowIndex = 1;
        for (Iterator<SubProcess> iterator = allStepsForProcess.iterator(); iterator.hasNext(); ) {
            SubProcess sp = iterator.next();
            grid[rowIndex][0] = sp.getName();

            int colIndex = 1;
            for (Iterator<DataGroup> dataGroupIterator = allGroupsForProcess.iterator(); dataGroupIterator.hasNext(); ) {
                DataGroup dg = dataGroupIterator.next();
                Movement m = getMovement(sp.getId(), dg.getId());
                grid[rowIndex][colIndex] = m.setDataGroup(dg).setSubProcess(sp); //I reset datagroup and subprocess, as I need the id's
                colIndex++;
            }
            rowIndex++;
        }

        return grid;


    }


    public void saveMovement(Movement m) {

        m.setParent(this);
        repository.saveDataMovements(m);
        movements.clear();
        getMovements();
    }

    public List<Movement> getMovements() {
        if (movements.isEmpty()) {
            movements = repository.getMovements(this);
        }

        return movements;

    }

    public void setMovements(List<Movement> movements) {
        this.movements = movements;
    }

    public List<Movement> getMovementsForProcessId(int processId) {

        List<Movement> movements = getMovements();
        List<Movement> movementsForProcess = new ArrayList<>();

        for (Movement m : movements) {
            if (m.getProcess().getId() == processId) {
                movementsForProcess.add(m);
            }
        }

        return movementsForProcess;

    }

    public boolean isExistingMovement(int dataGroupId, int subProcessId, int attribId) {

        for (Movement m : getMovements()) {
            if (m.getSubProcess().getId() == subProcessId && m.getDataGroup().getId() == dataGroupId) {
                return m.attributeAlreadyMapped(attribId);
            }
        }
        return false;
    }

    public Movement getMovement(int subProcessId, int dataGroupId) {
        Movement m = new Movement();
        for (Movement movement : getMovements()) {
            if (movement.getSubProcess().getId() == subProcessId && movement.getDataGroup().getId() == dataGroupId) {
                return movement;
            }
        }
        return m;

    }

    public void removeMovement(Movement m) {

        movements.remove(m);
    }

    public void setProcesses(List<Process> processes) {
        this.processes = processes;
    }

    public void saveDataGroup(DataGroup dg) {

        dg.setParent(this);
        repository.saveDataGroup(dg);
        datagroups.clear();
        getDataGroups();


    }

    public void saveProcess(Process p) {

        p.setParent(this);
        repository.saveProcess(p);
        processes.clear();
        getAllProcesses();
    }

    @Override
    public String toString() {
        return "SizingContext{" +
                "datagroups=" + datagroups +
                ", processes=" + processes +
                ", movements=" + movements +
                '}';
    }
}
