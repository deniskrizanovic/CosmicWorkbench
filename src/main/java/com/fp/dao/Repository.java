package com.fp.dao;

import com.fp.model.*;
import com.fp.model.Process;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


@org.springframework.stereotype.Repository
public class Repository {

    private DataGroupDAO dgDao;
    private ProcessDAO pDao;


    @Autowired
    public void setDgDao(DataGroupDAO dgDao) {
        this.dgDao = dgDao;
    }

    @Autowired
    public void setpDao(ProcessDAO pDao) {
        this.pDao = pDao;
    }

    //todo this pattern of almost having a fluent api needs to be consistent or removed.
    public SizingContext getDataGroups(SizingContext sc) {

        List<DataGroup> dataGroupList = dgDao.getDataGroups(sc);

        sc.setDatagroups(dataGroupList);

        return sc;
    }


    public List<DataAttribute> getAttributes(DataGroup dg) {

        List<DataAttribute> attribs = dgDao.getAttributes(dg);

        return attribs;
    }

    public void saveDataMovements(Movement m) {

        dgDao.saveMovement(m);
    }

    public List<Process> getProcesses(SizingContext sc) {

        List<Process> processes = pDao.getProcesses(sc);

        return processes;
    }

    public List<SubProcess> getSteps(Process process) {

        List<SubProcess> steps = pDao.getSteps(process);

        return steps;

    }

    public List<Movement> getMovements(SizingContext sc) {

        List<Movement> movements = dgDao.getMovements(sc);
        return movements;
    }

    public void saveDataGroup(DataGroup dg) {

        if (dg.getId() > 0) {
            DataGroup existing = dgDao.getDataGroup(dg);

            if (dataGroupNeedsUpdating(dg, existing)) {
                dgDao.saveDataGroupAndAttributes(dg);
            } else {
                dgDao.addAttributes(dg);
            }
        } else {
            dgDao.saveDataGroupAndAttributes(dg);
        }

    }

    private boolean dataGroupNeedsUpdating(DataGroup dg, DataGroup existing) {
        return !(existing.getName().equals(dg.getName()) && existing.getNotes().equals(dg.getNotes()));
    }

    public void saveProcess(Process p) {


        if (p.getId() > 0) {
            Process existing = pDao.getProcess(p);

            if (processNeedsUpdating(p, existing)) {
                pDao.saveProcessAndSteps(p);
            } else {
                pDao.addSteps(p);
            }


        } else {
            pDao.saveProcessAndSteps(p);
        }

    }

    private boolean processNeedsUpdating(Process p, Process existing) {
        return !(existing.getName().equals(p.getName()) && existing.getNotes().equals(p.getNotes()));
    }
}
