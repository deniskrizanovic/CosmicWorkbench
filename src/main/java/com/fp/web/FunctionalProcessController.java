package com.fp.web;

import com.fp.model.ModelBuilder;
import com.fp.model.Process;
import com.fp.model.SizingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class FunctionalProcessController
{

    @Autowired
    SizingContext sizingContext;   //todo this is definitely not thread safe. Even though it's a prototype

    public void setupSystemContext(Model model, HttpSession session) {

        Long name = (Long) session.getAttribute("systemcontextid");
        sizingContext.setId(name.intValue());
        model.addAttribute("sizingCtx", sizingContext);
    }

    @RequestMapping("/define-functional-process")
    public String showFunctionalProcess(Model model, HttpSession session) {
        setupSystemContext(model, session);
        return "define-functional-processes";
    }


    @RequestMapping("/save-functional-process")
    public String saveFunctionalProcess(Model model, HttpSession session, HttpServletRequest request) {

        setupSystemContext(model, session);


        String processName = request.getParameter("name");
        String notes = request.getParameter("notes");
        String[] steps = request.getParameterValues("step");
        int processId = 0;
        String userName = (String) session.getAttribute("username");

        if (!request.getParameter("fpId").equals("")) {
            processId = Integer.parseInt(request.getParameter("fpId"));
        }

        Process newProcess = ModelBuilder.buildProcess(processId, processName, notes, steps, userName);
        sizingContext.saveProcess(newProcess);

        return "define-functional-processes";
    }

    @RequestMapping(value = "/show-functional-process", method = {RequestMethod.GET, RequestMethod.POST})
    public String getFunctionalProcess(Model model, HttpServletRequest request, HttpSession session)
    {

        setupSystemContext(model, session);
        return "define-functional-processes";

    }

}