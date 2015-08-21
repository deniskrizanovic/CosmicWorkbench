package com.fp.web;

import com.fp.dao.Repository;
import com.fp.domain.SystemContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Controller
public class SystemContextController {

    private JdbcTemplate jdbcTemplate;

    private SystemContext systemContext;
    private Repository repository;


    @Autowired
    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    @RequestMapping("/")
    public String showSystemContext(Model model, HttpSession session) {

        if (session.getAttribute("username") != null) {
            model.addAttribute("username", session.getAttribute("username"));
        } else {
            model.addAttribute("username", "");
        }

        List<SystemContext> systemContextList = repository.getSystemContexts();

        model.addAttribute("systemcontextlist", systemContextList);

        return "index";
    }


    @RequestMapping(value = "/system-context", method = {RequestMethod.GET, RequestMethod.POST})
    public String show(Model model, HttpServletRequest request, HttpSession session) {

        String username = request.getParameter("username");
        String id = request.getParameter("id");

        model.addAttribute("username", username);
        model.addAttribute("systemcontextid", id);

        session.setAttribute("username", username);
        session.setAttribute("systemcontextid", id);

        systemContext = repository.getSystemContextDetailsById(id);

        if (this.systemContext != null) {
            session.setAttribute("systemcontextid", this.systemContext.getSystemContextId());
            model.addAttribute("contextname", this.systemContext.getName());
            session.setAttribute("systemcontextname", this.systemContext.getName());
        }

        return "system-context";
    }


    @RequestMapping(value = "/create-new-system-context", method = {RequestMethod.GET, RequestMethod.POST})
    public String createSystemContext(Model model, MultipartHttpServletRequest request, HttpSession session) {

        Set<String> params = request.getParameterMap().keySet();
        for (Iterator<String> iterator = params.iterator(); iterator.hasNext(); ) {
            String next = iterator.next();

            System.out.println("key=" + next + " value= " + request.getParameter(next));

        }

        String systemContextId = request.getParameter("systemcontextid");
        String contextName = request.getParameter("contextname");
        String notes = request.getParameter("notes");
        MultipartFile file = request.getFile("diagram");

        System.out.println("systemContextId = " + systemContextId);

        if (contextName != null) { // it looks like we are using the contextname to figure out what state we're in.

            if (createNewContext(request)) {

                System.out.println("In the create");

                try {

                    session.setAttribute("systemcontextname", contextName);
                    if (this.systemContext != null) {
                        session.setAttribute("systemcontextid", this.systemContext.getSystemContextId());
                    }

                    String username = (String) session.getAttribute("username");

                    repository.insertNewSystemContext(systemContextId, username, contextName, notes, file);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                return "system-context";
            } else { //temporarily commented out..
//
//                repository.updateSystemContext(contextName);
//
//                session.setAttribute("systemcontextname", contextName);
//
//                model.addAttribute("systemcontext", this.systemContext);
//
//                return "create-new-system-context";
            }
        } else {

            System.out.println("I'm here I bet!");
            model.addAttribute("systemcontext", this.systemContext);
            return "/";

        }
        return "/";

    }

    private boolean createNewContext(MultipartHttpServletRequest request) {


        return request.getParameter("action").equals("create");
    }

    @RequestMapping(value = "/show-system-context", method = {RequestMethod.GET, RequestMethod.POST})
    public String showCreateSystemContext(Model model, HttpServletRequest request, HttpSession session) {

        String username = request.getParameter("username");

        model.addAttribute("username", username);

        session.setAttribute("username", username);

        model.addAttribute("systemContext", this.systemContext);

        return "/create-new-system-context";
    }

    /**
     * this method is used during the create-system-context page which takes the name of the system context and if it exists,
     * actually becomes a modify system context screen!
     *
     * @param model
     * @param request
     * @return
     */

    @RequestMapping("/getdata")
    public String getData(Model model, HttpServletRequest request) {

        String contextName = request.getParameter("contextname");

        if (contextName != null) { //this just seems to protect against a null.

            SystemContext ctx = repository.getSystemContextByName(contextName);

            if (ctx.getName() != null) {
                this.systemContext = ctx;
            } else {
                this.systemContext = new SystemContext();
                this.systemContext.setName(contextName);
                //return "modify-system-context";
            }

            model.addAttribute("systemcontext", this.systemContext);

        }

        System.out.println("this is the systemcontext that lives in the model  = " + systemContext);

        return "create-new-system-context";

    }

    @RequestMapping("/delete-system-context")
    public String deleteSystemContext(HttpServletRequest request) {

        String contextName = request.getParameter("contextname");

        repository.deleteSystemContext(contextName);

        return "create-new-system-context";
    }

}