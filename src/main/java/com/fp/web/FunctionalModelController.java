package com.fp.web;

import com.fp.model.ModelBuilder;
import com.fp.model.Movement;
import com.fp.model.SizingContext;
import com.fp.model.SubProcess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class FunctionalModelController {

    @Autowired
    SizingContext sizingContext;

    @RequestMapping(value = "/add-datagroup-and-attributes", method = {RequestMethod.GET, RequestMethod.POST})
    public String addDataAttributes(Model model, HttpServletRequest request, HttpSession session) {

        Long name = (Long) session.getAttribute("systemcontextid");
        sizingContext.setId(name.intValue());
        model.addAttribute("sizingCtx", sizingContext);

        return "add-datagroup-and-attributes";
    }


    @RequestMapping(value = "/select-data-attributes", method = {RequestMethod.GET, RequestMethod.POST})
    public String selectDataAttributes(Model model, HttpServletRequest request, HttpSession session) {

        Long name = (Long) session.getAttribute("systemcontextid");
        sizingContext.setId(name.intValue());
        model.addAttribute("sizingCtx", sizingContext);
        return "select-data-attributes";
    }


    @RequestMapping(value = "/save-data-attributes", method = {RequestMethod.GET, RequestMethod.POST})
    public String saveDataAttributes(Model model, HttpServletRequest request, HttpSession session) {

        //todo some validation


        int dgId = Integer.parseInt(request.getParameter("dg"));
        int processId = Integer.parseInt(request.getParameter("p"));
        int stepId = Integer.parseInt(request.getParameter("sp"));
        String[] attribs = request.getParameterValues("attribId");
        String username = (String) session.getAttribute("username");
        String type = request.getParameter("type");


        Long name = (Long) session.getAttribute("systemcontextid");

        sizingContext.setId(name.intValue());
        SubProcess sp = sizingContext.getProcess(processId).getStep(stepId);

        com.fp.model.DataGroup dg = sizingContext.getDataGroup(dgId);
        Movement dm = ModelBuilder.buildMovement(dg, sp, attribs, type, username);
        sizingContext.saveMovement(dm);
        model.addAttribute("sizingCtx", sizingContext);


        return "define-functional-model";
    }


    @RequestMapping(value = "/define-functional-model", method = {RequestMethod.GET, RequestMethod.POST})
    public String renderFunctionalModel(Model model, HttpServletRequest request, HttpSession session) {

        System.out.println("I am in here!");

        Long ctxId = (Long) session.getAttribute("systemcontextid");
        sizingContext.setId(ctxId.intValue());
        model.addAttribute("sizingCtx", sizingContext);

        return "define-functional-model";
    }
}