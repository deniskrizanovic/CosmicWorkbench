package com.fp.model;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModelBuilder {


    public static Movement buildMovement(DataGroup dg, SubProcess sp, String[] attributeIds, String type, String username) {

        List<DataAttribute> attributesToAdd = new ArrayList();
        if (attributeIds != null) {
            List<String> attribs = Arrays.asList(attributeIds);

            if (!attribs.isEmpty()) {

                for (String next : attribs) {
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

        for (String attrib : attribNameList) {
            if (!attrib.equals("")) {

                DataAttribute newAttrib = new DataAttribute();

                boolean existingAttribute = attrib.indexOf(":") > 0;
                int id = 0;
                if (existingAttribute) {

                    id = Integer.parseInt(attrib.substring(0, attrib.indexOf(":")));
                }

                String name = attrib.substring(attrib.indexOf(":") + 1, attrib.length());
                newAttrib.setId(id);
                newAttrib.setName(name);
                newAttrib.setParent(dg);
                attribList.add(newAttrib);
            }
        }

        dg.setAttributes(attribList);

        return dg;

    }

    public static Process buildProcess(int processId, String processName, String notes, String[] steps, String userName) {

        Process p = new Process();
        p.setName(processName);
        p.setNotes(notes);
        p.setCreatedBy(userName);
        p.setId(processId);

        List<SubProcess> spList = new ArrayList<>();
        List<String> stepsNameList = Arrays.asList(steps);

        for (String step : stepsNameList) {

            if (!step.equals("")) {
                SubProcess sp = new SubProcess();
                sp.setName(step);
                sp.setParent(p);
                spList.add(sp);
            }

        }
        p.setSteps(spList);

        return p;


    }
}
