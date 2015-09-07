package com.fp.model;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class ModelBuilder {


    public static Movement buildMovement(DataGroup dg, SubProcess sp, String[] attributeIds, String type, String username) {

        List attributesToAdd = new ArrayList();
        if(attributeIds != null) {
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


}
