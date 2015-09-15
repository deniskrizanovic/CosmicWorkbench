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
        List<DataGroup> groupsWithNoMovement = sc.getDataGroupsWithNoMovements();


        Assert.assertEquals("dg3 is contained in the list", true, groupsWithNoMovement.contains(dg3));
        Assert.assertEquals("Only dg3 is returned", true, groupsWithNoMovement.size() == 1);
    }

    @Before
    public void setup() {

        DataGroup dg1 = (DataGroup) new DataGroup().setId(1);
        DataGroup dg2 = (DataGroup) new DataGroup().setId(2);
        DataGroup dg3 = (DataGroup) new DataGroup().setId(3);

        List<DataGroup> dgList = new ArrayList();
        dgList.add(dg1);
        dgList.add(dg2);
        dgList.add(dg3);

        SubProcess sp1 = (SubProcess) new SubProcess().setId(1);
        SubProcess sp2 = (SubProcess) new SubProcess().setId(2);
        SubProcess sp3 = (SubProcess) new SubProcess().setId(3);

        Movement m1 = new Movement().setDataGroup(dg1).setSubProcess(sp1).setParent(sc);
        Movement m2 = new Movement().setDataGroup(dg2).setSubProcess(sp2).setParent(sc);
        Movement m3 = new Movement().setDataGroup(dg3).setSubProcess(sp3).setParent(sc);

        List<Movement> movementList = new ArrayList();
        movementList.add(m1);
        movementList.add(m2);
        movementList.add(m3);  // do not add this movement, as this is the missing one.

        sc.setMovements(movementList);
        sc.setDatagroups(dgList);
    }
}
