package com.fp.model;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class ModelBuilder {


    public static Movement buildMovement(DataGroup dg, SubProcess sp, String[] attributeIds, String type, String username) {

        List attributesToAdd = new ArrayList();
        if (attributeIds != null) {
            List<String> attribs = Arrays.asList(attributeIds);

            if (!attribs.isEmpty()) {

                for (Iterator<String> iterator = attribs.iterator(); iterator.hasNext(); ) {
                    String next = iterator.next();
                    DataAttribute dm = new DataAttribute();
                    dm.setParent(dg);
                    dm.setId(Integer.parseInt(next));
                    dm.setCreatedBy(username);
                    attributesToAdd.add(dm);
                }
            }
        }

        Movement m = new Movement();
        m.setAttributes(attributesToAdd);
        m.setSubProcess(sp);
        m.setDataGroup(dg);
        m.setType(type);
        m.setCreatedBy(username);

        return m;
    }


    public static DataGroup buildDataGroup(String dataGroupName, String notes, String[] attribs, int dataGroupId, String userName) {

        DataGroup dg = new DataGroup();
        dg.setName(dataGroupName);
        dg.setNotes(notes);
        dg.setCreatedBy(userName);
        dg.setId(dataGroupId);

        List<DataAttribute> attribList = new ArrayList<>();

        List<String> attribNameList = Arrays.asList(attribs);

        for (Iterator i = attribNameList.iterator(); i.hasNext(); ) {
            String attribName = (String) i.next();

            if (!attribName.equals("")) {
                DataAttribute newAttrib = new DataAttribute();
                newAttrib.setName(attribName);
                newAttrib.setParent(dg);
                attribList.add(newAttrib);
            }
        }

        dg.setAttributes(attribList);

        return dg;

    }
}
