package com.fp.dao;

import com.fp.model.*;
import com.fp.model.Process;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by denis on 4/09/15.
 */

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

    public SizingContext getDataGroups(SizingContext sc) {

        List<DataGroup> dataGroupList = dgDao.getDataGroups(sc);

        sc.setDatagroups(dataGroupList);

        return sc;
    }


    public List<DataAttribute> getAttributes(DataGroup dg) {

        List<DataAttribute> attribs = dgDao.getAttributes(dg);

        return attribs;
    }

    public void saveDataMovements(DataGroup dg, SubProcess sp, List<String> attributeIds, String type, String username) {


        dgDao.saveDataMovements(dg, sp, attributeIds, type, username);
        getMovements(dg.getParent());

    }

    public void getProcesses(SizingContext sc) {

        List<Process> processes = pDao.getProcesses(sc);
        sc.setProcesses(processes);
    }

    public List<SubProcess> getSteps(Process process) {

        List<SubProcess> steps = pDao.getSteps(process);

        return steps;

    }

    public List<Movement> getMovements(SizingContext sc) {

        List<Movement> movements = dgDao.getMovements(sc);
        sc.setMovements(movements);
        return movements;
    }

    public void getAttributesForMovement(SubProcess subProcess, DataGroup dataGroup) {

        //dgDao.get
    }
}
