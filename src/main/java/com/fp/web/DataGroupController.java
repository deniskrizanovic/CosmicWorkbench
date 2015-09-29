package com.fp.web;

import com.fp.model.DataGroup;
import com.fp.model.ModelBuilder;
import com.fp.model.SizingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class DataGroupController {

    @Autowired
    SizingContext sizingContext;   //todo this is definitely not thread safe. Even though it's a prototype

    public void setupSystemContext(Model model, HttpSession session) {

        Long name = (Long) session.getAttribute("systemcontextid");
        sizingContext.setId(name.intValue());
        model.addAttribute("sizingCtx", sizingContext);
    }

    @RequestMapping("/define-data-groups")
    public String showDataGroup(Model model, HttpSession session) {

        setupSystemContext(model, session);
        return "define-data-groups";
    }

    @RequestMapping(value = "/save-data-group", method = {RequestMethod.GET, RequestMethod.POST})
    public String creatingNewDataGroup(Model model, HttpServletRequest request, HttpSession session) {

        System.out.println("I am!");

        setupSystemContext(model, session);

        String dataGroupName = request.getParameter("name");
        String notes = request.getParameter("notes");
        String[] attribs = request.getParameterValues("attribute");
        String userName = (String) session.getAttribute("username");

        int dataGroupId = 0;
        if (!request.getParameter("dgId").equals("")) {
            dataGroupId = Integer.parseInt(request.getParameter("dgId"));
        }

        DataGroup dg = ModelBuilder.buildDataGroup(dataGroupName, notes, attribs, dataGroupId, userName);
        sizingContext.saveDataGroup(dg);


        return "define-data-groups";
    }

}