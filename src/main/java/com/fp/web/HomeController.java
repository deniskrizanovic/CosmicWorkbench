package com.fp.web;

import com.fp.dao.Repository;
import com.fp.domain.SystemContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Controller
public class HomeController {

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
    public String showSystemContext(Model model, HttpServletRequest request, HttpSession session) {

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

        String contextName = request.getParameter("contextname");
        String notes = request.getParameter("notes");
        MultipartFile file = request.getFile("diagram");

        if (contextName != null) { // it looks like we are using the contextname to figure out what state we're in.

            if (createNewContext(request)) {

                try {

                    session.setAttribute("systemcontextname", contextName);
                    if (this.systemContext != null) {
                        session.setAttribute("systemcontextid", this.systemContext.getSystemContextId());
                    }

                    String username = (String) session.getAttribute("username");

                    repository.insertNewSystemContext(username, contextName, notes, file);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                return "system-context";
            } else {

                repository.updateSystemContext(contextName);

                session.setAttribute("systemcontextname", contextName);

                model.addAttribute("systemContext", this.systemContext);

                return "create-new-system-context";
            }
        } else {

            model.addAttribute("systemContext", this.systemContext);
            return "/";

        }
    }

    private boolean createNewContext(MultipartHttpServletRequest request) {
        return request.getParameter("option").equals("1");
    }

    @RequestMapping(value = "/show-system-context", method = {RequestMethod.GET, RequestMethod.POST})
    public String showCreateSystemContext(Model model, HttpServletRequest request, HttpSession session) {

        String username = request.getParameter("username");

        model.addAttribute("username", username);

        session.setAttribute("username", username);

        model.addAttribute("systemContext", this.systemContext);

        return "create-new-system-context";
    }

    @RequestMapping("/getdata")
    public String getData(Model model, HttpServletRequest request, HttpSession session) {

        String contextname = request.getParameter("contextname");

        if (contextname != null) { //this just seems to protect against a null.

            List<SystemContext> systemContextList = this.jdbcTemplate
                    .query("select systemcontextid, version, name, notes, diagram from systemcontext where not deleteflag and name = '"
                                    + contextname.replace("'", "''") + "'",
                            new RowMapper<SystemContext>() {
                                public SystemContext mapRow(ResultSet rs,
                                                            int rowNum) throws SQLException {
                                    SystemContext systemContexttmp = new SystemContext();
                                    systemContexttmp.setName(rs.getString("name"));
                                    systemContexttmp.setNotes(rs.getString("notes"));
                                    return systemContexttmp;
                                }
                            });

            if (systemContextList.size() > 0) {
                this.systemContext = systemContextList.get(0);
            } else {
                this.systemContext = new SystemContext();
                this.systemContext.setName(contextname);
            }

            model.addAttribute("systemcontext", this.systemContext);

        }

        return "create-new-system-context";

    }

    @RequestMapping("/delete-system-context")
    public String deleteSystemContext(Model model, HttpServletRequest request) {

        String contextname = request.getParameter("contextname");

        if (contextname != null) {
            this.jdbcTemplate
                    .update(" update systemcontext set deleteflag = true where name = '"
                            + contextname + "'");

        }

        return "create-new-system-context";
    }

}