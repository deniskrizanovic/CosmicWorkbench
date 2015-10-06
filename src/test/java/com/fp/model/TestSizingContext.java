package com.fp.model;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class TestSizingContext {


    SizingContext sc = new SizingContext();

    @Test
    public void testDataGroupsWithNoMovements() {

        DataGroup dg3 = (DataGroup) new DataGroup().setId(3);
        SubProcess sp3 = (SubProcess) new SubProcess().setId(3);
        Movement m3 = new Movement().setDataGroup(dg3).setSubProcess(sp3).setParent(sc);

        sc.removeMovement(m3);
        List<DataGroup> groupsWithNoMovement = sc.getDataGroupsWithNoMovements(1);


        Assert.assertEquals("dg3 is contained in the list", true, groupsWithNoMovement.contains(dg3));
        Assert.assertEquals("Only dg3 is returned", true, groupsWithNoMovement.size() == 1);
    }


    @Test
    public void testMappingGrid() {

        sc.getDataToSubProcessMappingAsGrid(1);

        //Assert.assertEquals();

    }

    @Before
    public void setup() {

        DataGroup dg1 = (DataGroup) new DataGroup().setName("dg1").setId(1);
        DataGroup dg2 = (DataGroup) new DataGroup().setName("dg2").setId(2);
        DataGroup dg3 = (DataGroup) new DataGroup().setName("dg3").setId(3);

        List<DataGroup> dgList = new ArrayList();
        dgList.add(dg1);
        dgList.add(dg2);
        dgList.add(dg3);

        Process p1 = (Process) new Process().setName("p1").setId(1);
        Process p2 = (Process) new Process().setName("p2").setId(2);
        Process p3 = (Process) new Process().setName("p3").setId(3);

        List<Process> processList = new ArrayList<>();
        processList.add(p1);
        processList.add(p2);
        processList.add(p3);

        SubProcess sp1 = (SubProcess) new SubProcess().setParent(p1).setName("sp1").setId(1);
        SubProcess sp2 = (SubProcess) new SubProcess().setParent(p1).setName("sp2").setId(2);
        SubProcess sp3 = (SubProcess) new SubProcess().setParent(p1).setName("sp3").setId(3);
        List<SubProcess> subProcessList = new ArrayList<>();
        subProcessList.add(sp1);
        subProcessList.add(sp2);
        subProcessList.add(sp3);
        p1.setSteps(subProcessList);


        Movement m1 = new Movement().setDataGroup(dg1).setSubProcess(sp1).setParent(sc).setType("E");
        Movement m2 = new Movement().setDataGroup(dg2).setSubProcess(sp2).setParent(sc).setType("X");
        Movement m3 = new Movement().setDataGroup(dg3).setSubProcess(sp3).setParent(sc);

        List<Movement> movementList = new ArrayList();
        movementList.add(m1);
        movementList.add(m2);
        movementList.add(m3);  // do not add this movement, as this is the missing one.

        sc.setMovements(movementList);
        sc.setDatagroups(dgList);
        sc.setProcesses(processList);
    }
}
