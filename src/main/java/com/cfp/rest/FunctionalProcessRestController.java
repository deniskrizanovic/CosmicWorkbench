package com.cfp.rest;

import com.fp.model.Process;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;


@RestController
public class FunctionalProcessRestController {

    @RequestMapping("/{systemContextID}/FunctionalProcess")
    public List getFunctionalProcessList(@PathVariable String systemContextID) {

        //execute a query for all functional processes for a particular systemContext

        Process one = new Process();
        one.setName("A name");
        one.setDescription("A description");

        Process two = new Process();
        two.setName("B Name");
        two.setDescription("B Description");

        List returnList = new ArrayList();
        returnList.add(one);
        returnList.add(two);

        return returnList;

    }

    @RequestMapping("/{systemContextID}/FunctionalProcess/{fpId}")
    public String getFunctionalProcess(@PathVariable String systemContextID, @PathVariable String fpId) {

        return null;
    }


}
