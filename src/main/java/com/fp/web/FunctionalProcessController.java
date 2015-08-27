package com.fp.web;

import com.fp.dao.FunctionalProcessRepository;
import com.fp.dao.SystemContextRepository;
import com.fp.domain.FunctionalProcess;
import com.fp.domain.FunctionalSubProcess;
import com.fp.domain.SystemContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Controller
public class FunctionalProcessController {

    private JdbcTemplate jdbcTemplate;

    private SystemContext systemContext;

    private FunctionalProcess functionalProcess;

    private FunctionalProcessRepository functionalProcessRepository;

    private SystemContextRepository systemContextRepository;

    private String err;

    @Autowired
    public void setFunctionalProcessRepository(FunctionalProcessRepository functionalProcessRepository) {
        this.functionalProcessRepository = functionalProcessRepository;
    }

    @Autowired
    public void setSystemContextRepository(SystemContextRepository systemContextRepository) {
        this.systemContextRepository = systemContextRepository;
    }

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @RequestMapping(value = "/disp-functional-processes", method = {RequestMethod.GET, RequestMethod.POST})
    public String dispFunctionalProcess(Model model, HttpServletRequest request, HttpSession session) {


        if (session.getAttribute("systemcontextid") != null) {
            Long name = (Long) session.getAttribute("systemcontextid");
            List<FunctionalProcess> actors = functionalProcessRepository.getListOfFunctionalProcessesForContext(name);

            model.addAttribute("functionalprocesslist", actors);
        }


        return "/define-functional-processes";
    }

    @RequestMapping("/define-functional-processes")
    public String showFunctionalProcess(Model model, HttpSession session) {

        System.out.println("or is this me?");

        long functionalProcessId = 0l;

        List<FunctionalSubProcess> functionalsubprocesslist = null;

        List<FunctionalProcess> actors = null;

        if (session.getAttribute("systemcontextid") != null) {


            Long systemcontextid = (Long) session.getAttribute("systemcontextid");
            actors = functionalProcessRepository.getListOfFunctionalProcessesForContext(systemcontextid);

            model.addAttribute("functionalprocesslist", actors);

            //todo not sure if I really need this at all.
            if (actors.size() > 0) {
                functionalProcessId = actors.get(0).getFunctionalProcessId();
                this.functionalProcess = actors.get(0);
            } else {
                this.functionalProcess = null;
            }

            functionalsubprocesslist = functionalProcessRepository.getListofSubProcesses(functionalProcessId);

            model.addAttribute("functionalsubprocesslist", functionalsubprocesslist);
        }

        model.addAttribute("functionalprocess", this.functionalProcess);

        return "define-functional-processes";
    }


    @RequestMapping(value = "/create-new-functional-process", method = {RequestMethod.GET, RequestMethod.POST})
    public String createSystemContext(Model model, HttpServletRequest request, HttpSession session) {

        //todo there should be some defensive coding here.

        Long systemContextId = (Long) session.getAttribute("systemcontextid");

        String functionalprocessname = request.getParameter("functionalprocessname");
        String functionalprocessnotes = request.getParameter("functionalprocessnotes");
        String functionalsubprocessname = request.getParameter("functionalsubprocessname");

        int version = 0;
        long functionalProcessId = 0l;

        if (systemContextId != null) {

            this.systemContext = systemContextRepository.getSystemContextDetailsById(String.valueOf(systemContextId));

            if (this.systemContext != null && this.systemContext.getSystemContextId() != 0l) {
                systemContextId = this.systemContext.getSystemContextId(); //don't I already have this?
            }

            if (systemContextId != 0l) {
                if (isSave(request)) {

                    List<FunctionalProcess> functionalProcessList = functionalProcessRepository.getListOfFunctionalProcessesForContext(systemContextId);

                    if (functionalProcessList.size() > 0) {
                        err = "Data group already exist";
                        model.addAttribute("err", err);
                        return getFunctionalProcess(model, request, session);
                    }

                    this.jdbcTemplate
                            .update(" insert into functionalprocess ( version, systemcontextid, name, notes, userid ) values ( "
                                    + version
                                    + ","
                                    + systemContextId
                                    + ",'"
                                    + functionalprocessname.replace("'", "''")
                                    + "','"
                                    + functionalprocessnotes.replace("'", "''")
                                    + "','"
                                    + session.getAttribute("username")
                                    + "" + "')");


                    this.functionalProcess = this.jdbcTemplate
                            .queryForObject(
                                    "select functionalprocessid, version, name, notes from functionalprocess where not deleteflag and systemcontextid = "
                                            + systemContextId
                                            + " and name = '"
                                            + functionalprocessname.replace(
                                            "'", "''") + "'",
                                    new RowMapper<FunctionalProcess>() {
                                        public FunctionalProcess mapRow(
                                                ResultSet rs, int rowNum)
                                                throws SQLException {
                                            FunctionalProcess actor = new FunctionalProcess();
                                            actor.setFunctionalProcessId(rs
                                                    .getLong("functionalprocessid"));
                                            actor.setName(rs.getString("name"));
                                            actor.setNotes(rs
                                                    .getString("notes"));
                                            return actor;
                                        }
                                    });

                    if (this.functionalProcess != null && this.functionalProcess.getFunctionalProcessId() != 0l) {

                        functionalProcessId = this.functionalProcess.getFunctionalProcessId();

                        session.setAttribute("functionalprocessname", this.functionalProcess.getFunctionalProcessId());
                    }

                    if (functionalProcessId != 0l && functionalsubprocessname != null && !functionalsubprocessname.isEmpty()) {

                        this.jdbcTemplate
                                .update(" update functionalsubprocess set version = version + 1 where functionalprocessid = "
                                        + functionalProcessId);

                        this.jdbcTemplate
                                .update(" insert into functionalsubprocess ( version, functionalprocessid, name, userid ) select "
                                        + version
                                        + ","
                                        + functionalProcessId
                                        + ","
                                        + "name"
                                        + ","
                                        + "userid"
                                        + ""
                                        + " from functionalsubprocess where version = 1 and not deleteflag and functionalProcessId = "
                                        + functionalProcessId);

                        this.jdbcTemplate
                                .update(" insert into functionalsubprocess ( version, functionalprocessid, name, userid ) values ( "
                                        + version
                                        + ","
                                        + functionalProcessId
                                        + ",'"
                                        + functionalsubprocessname.replace("'",
                                        "''")
                                        + "','"
                                        + session.getAttribute("username")
                                        + ""
                                        + "')");

                        List<FunctionalSubProcess> sub = this.jdbcTemplate
                                .query("select functionalsubprocessid, functionalprocessid, version, name from functionalsubprocess where not deleteflag and version = 0 and functionalprocessid = "
                                                + functionalProcessId
                                                + " and name = '"
                                                + functionalsubprocessname.replace("'",
                                                "''") + "'",
                                        new RowMapper<FunctionalSubProcess>() {
                                            public FunctionalSubProcess mapRow(
                                                    ResultSet rs, int rowNum)
                                                    throws SQLException {
                                                FunctionalSubProcess actor = new FunctionalSubProcess();
                                                actor.setFunctionalSubProcessId(rs.getLong("functionalsubprocessid"));
                                                actor.setFunctionalProcessId(rs.getLong("functionalprocessid"));
                                                actor.setName(rs.getString("name"));
                                                return actor;
                                            }
                                        });

                        Long tempId = 0l;

                        if (sub.size() > 0) {
                            int i = sub.size();
                            tempId = sub.get(i - 1).getFunctionalSubProcessId();
                        }

                        this.jdbcTemplate
                                .update(" insert into functionalmodel ( systemcontextid, functionalprocessid, datagroupid , functionalsubprocessid, version, grade, notes, score, userid ) select "
                                        + systemContextId
                                        + ","
                                        + functionalProcessId
                                        + ","
                                        + "max(a.datagroupid)"
                                        + ", "
                                        + tempId
                                        + ", "
                                        + version
                                        + ",'','',0,'"
                                        + session.getAttribute("username")
                                        + ""
                                        + "'"
                                        + " from functionalmodel a where exists ( select * from functionalmodel b where b.functionalprocessid = "
                                        + functionalProcessId
                                        + " ) and a.functionalprocessid = "
                                        + functionalProcessId
                                        + " group by a.datagroupid ");

                        this.jdbcTemplate
                                .update(" update functionalmodel c set c.functionalsubprocessid = nvl(( select a.functionalsubprocessid "
                                        + " from functionalsubprocess a, functionalsubprocess b where a.functionalprocessid = "
                                        + functionalProcessId
                                        + " and a.functionalprocessid = b.functionalprocessid and a.version = 0 and not a.deleteflag and b.version = 1 and not b.deleteflag and a.name = b.name  "
                                        + " and b.functionalsubprocessid = c.functionalsubprocessid ), c.functionalsubprocessid)"
                                        + " where c.functionalprocessid = "
                                        + functionalProcessId);

                    }

                } else if (request.getParameter("option") != null && request.getParameter("option").equals("0")) {

                    this.jdbcTemplate
                            .update(" update functionalsubprocess set deleteflag = true where version = 0 and functionalprocessid in (select functionalprocessid from functionalprocess where systemcontextid = "
                                    + systemContextId
                                    + " and name = '"
                                    + functionalprocessname.replace("'", "''")
                                    + "')");

                    this.jdbcTemplate
                            .update(" update functionalprocess set deleteflag = true where name = '"
                                    + functionalprocessname.replace("'", "''")
                                    + "' and systemcontextid = "
                                    + systemContextId);

                    return dispFunctionalProcess(model, request, session);

                } else if (request.getParameter("option") != null && request.getParameter("option").equals("2")) {

                    return "define-functional-processes";

                } else if (request.getParameter("option") != null && request.getParameter("option").equals("3")) {

                    return "define-functional-processes";
                } else if (request.getParameter("option") != null && request.getParameter("option").equals("4")) {

                    Long name = (Long) session.getAttribute("systemcontextid");

                    this.functionalProcess = this.jdbcTemplate
                            .queryForObject(
                                    "select functionalprocessid, version, name, notes from functionalprocess where not deleteflag and systemcontextid = "
                                            + name
                                            + " and name = '"
                                            + functionalprocessname.replace(
                                            "'", "''") + "'",
                                    new RowMapper<FunctionalProcess>() {
                                        public FunctionalProcess mapRow(
                                                ResultSet rs, int rowNum)
                                                throws SQLException {
                                            FunctionalProcess actor = new FunctionalProcess();
                                            actor.setFunctionalProcessId(rs
                                                    .getLong("functionalprocessid"));
                                            actor.setName(rs.getString("name"));
                                            actor.setNotes(rs
                                                    .getString("notes"));
                                            return actor;
                                        }
                                    });

                    if (this.functionalProcess != null && this.functionalProcess.getFunctionalProcessId() != 0l) {
                        functionalProcessId = this.functionalProcess.getFunctionalProcessId();
                    }

                    if (functionalProcessId != 0l && functionalsubprocessname != null && !functionalsubprocessname.isEmpty()) {

                        this.jdbcTemplate
                                .update(" update functionalsubprocess set version = version + 1 where functionalprocessid = "
                                        + functionalProcessId);

                        this.jdbcTemplate
                                .update(" insert into functionalsubprocess ( version, functionalprocessid, name, userid ) select "
                                        + version
                                        + ","
                                        + functionalProcessId
                                        + ","
                                        + "name"
                                        + ","
                                        + "userid"
                                        + ""
                                        + " from functionalsubprocess where version = 1 and not deleteflag and functionalProcessId = "
                                        + functionalProcessId);

                        this.jdbcTemplate
                                .update(" insert into functionalsubprocess ( version, functionalprocessid, name, userid ) values ( "
                                        + version
                                        + ","
                                        + functionalProcessId
                                        + ",'"
                                        + functionalsubprocessname.replace("'",
                                        "''")
                                        + "','"
                                        + session.getAttribute("username")
                                        + ""
                                        + "')");

                        List<FunctionalSubProcess> sub = this.jdbcTemplate
                                .query("select functionalsubprocessid, functionalprocessid, version, name from functionalsubprocess where not deleteflag and version = 0 and functionalprocessid = "
                                                + functionalProcessId
                                                + " and name = '"
                                                + functionalsubprocessname.replace("'",
                                                "''") + "'",
                                        new RowMapper<FunctionalSubProcess>() {
                                            public FunctionalSubProcess mapRow(
                                                    ResultSet rs, int rowNum)
                                                    throws SQLException {
                                                FunctionalSubProcess actor = new FunctionalSubProcess();
                                                actor.setFunctionalSubProcessId(rs
                                                        .getLong("functionalsubprocessid"));
                                                actor.setFunctionalProcessId(rs
                                                        .getLong("functionalprocessid"));
                                                actor.setName(rs
                                                        .getString("name"));
                                                return actor;
                                            }
                                        });

                        Long tempId = 0l;

                        if (sub.size() > 0) {
                            int i = sub.size();
                            tempId = sub.get(i - 1).getFunctionalSubProcessId();
                        }

                        this.jdbcTemplate
                                .update(" insert into functionalmodel ( systemcontextid, functionalprocessid, datagroupid , functionalsubprocessid, version, grade, notes, score, userid ) select "
                                        + systemContextId
                                        + ","
                                        + functionalProcessId
                                        + ","
                                        + "max(a.datagroupid)"
                                        + ", "
                                        + tempId
                                        + ", "
                                        + version
                                        + ",'','',0,'"
                                        + session.getAttribute("username")
                                        + ""
                                        + "'"
                                        + " from functionalmodel a where exists ( select * from functionalmodel b where b.functionalprocessid = "
                                        + functionalProcessId
                                        + " ) and a.functionalprocessid = "
                                        + functionalProcessId
                                        + " group by a.datagroupid ");

                        this.jdbcTemplate
                                .update(" update functionalmodel c set c.functionalsubprocessid = nvl(( select a.functionalsubprocessid "
                                        + " from functionalsubprocess a, functionalsubprocess b where a.functionalprocessid = "
                                        + functionalProcessId
                                        + " and a.functionalprocessid = b.functionalprocessid and a.version = 0 and b.version = 1 and a.name = b.name  "
                                        + " and b.functionalsubprocessid = c.functionalsubprocessid ), c.functionalsubprocessid)"
                                        + " where c.functionalprocessid = "
                                        + functionalProcessId);

                    }
                } else if (request.getParameter("option") != null && request.getParameter("option").equals("5")) {

                    long functionalSubProcessId = 0l;

                    if (request.getParameter("delete") != null) {
                        functionalSubProcessId = Long.parseLong(request.getParameter("delete"));
                    }

                    this.jdbcTemplate
                            .update(" update functionalsubprocess set deleteflag = true where version = 0 and functionalsubprocessid = "
                                    + functionalSubProcessId);

                }

            }

        }

        this.functionalProcess = null;

        if (functionalprocessname == null || functionalprocessname.equals("") || (request.getParameter("option") != null && request.getParameter("option").equals("0"))) {

            return "define-functional-processes";
        } else {

            return getFunctionalProcess(model, request, session);
        }

    }

    public boolean isSave(HttpServletRequest request) {
        return request.getParameter("option") != null && request.getParameter("option").equals("save");
    }

    @RequestMapping(value = "/show-functional-processes", method = {RequestMethod.GET, RequestMethod.POST})
    public String getFunctionalProcess(Model model, HttpServletRequest request, HttpSession session) {

        if (session.getAttribute("systemcontextid") != null) {
            if (request.getParameter("functionalprocessid") != null && !(request.getParameter("functionalprocessid") + "").equals("")) {

                Long name = (Long) session.getAttribute("systemcontextid");
                String functionalprocessname = request.getParameter("functionalprocessid");

                List<FunctionalProcess> actors = this.jdbcTemplate
                        .query("select functionalprocessid, version, name, notes from functionalprocess where not deleteflag and systemcontextid = "
                                        + name + "",
                                new RowMapper<FunctionalProcess>() {
                                    public FunctionalProcess mapRow(
                                            ResultSet rs, int rowNum)
                                            throws SQLException {
                                        FunctionalProcess actor = new FunctionalProcess();
                                        actor.setFunctionalProcessId(rs.getLong("functionalprocessid"));
                                        actor.setName(rs.getString("name"));
                                        actor.setNotes(rs.getString("notes"));
                                        return actor;
                                    }
                                });

                model.addAttribute("functionalprocesslist", actors);

                if (functionalprocessname != null && !functionalprocessname.equals("")) {

                    this.functionalProcess = this.jdbcTemplate
                            .queryForObject(
                                    "select functionalprocessid, version, name, notes from functionalprocess where not deleteflag and systemcontextid = "
                                            + name
                                            + " and functionalprocessid = "
                                            + Long.parseLong(functionalprocessname),
                                    new RowMapper<FunctionalProcess>() {
                                        public FunctionalProcess mapRow(
                                                ResultSet rs, int rowNum)
                                                throws SQLException {
                                            FunctionalProcess actor = new FunctionalProcess();
                                            actor.setFunctionalProcessId(rs.getLong("functionalprocessid"));
                                            actor.setName(rs.getString("name"));
                                            actor.setNotes(rs.getString("notes"));
                                            return actor;
                                        }
                                    });
                }

                model.addAttribute("functionalprocess", this.functionalProcess);

                List<FunctionalSubProcess> functionalsubprocesslist = this.jdbcTemplate
                        .query("select functionalsubprocessid, functionalprocessid, version, name from functionalsubprocess where not deleteflag and version = 0 and functionalprocessid = "
                                        + this.functionalProcess
                                        .getFunctionalProcessId(),
                                new RowMapper<FunctionalSubProcess>() {
                                    public FunctionalSubProcess mapRow(
                                            ResultSet rs, int rowNum)
                                            throws SQLException {
                                        FunctionalSubProcess datafield = new FunctionalSubProcess();
                                        datafield.setFunctionalSubProcessId(rs.getLong("functionalsubprocessid"));
                                        datafield.setFunctionalProcessId(rs.getLong("functionalprocessid"));
                                        datafield.setVersion(rs.getInt("version"));
                                        datafield.setName(rs.getString("name"));
                                        return datafield;
                                    }
                                });

                model.addAttribute("functionalsubprocesslist", functionalsubprocesslist);

            } else {

                Long name = (Long) session.getAttribute("systemcontextid");
                String functionalprocessname = request.getParameter("functionalprocessname");


                List<FunctionalProcess> actors = this.jdbcTemplate
                        .query("select functionalprocessid, version, name, notes from functionalprocess where not deleteflag and systemcontextid = "
                                        + name + "",
                                new RowMapper<FunctionalProcess>() {
                                    public FunctionalProcess mapRow(
                                            ResultSet rs, int rowNum)
                                            throws SQLException {
                                        FunctionalProcess actor = new FunctionalProcess();
                                        actor.setFunctionalProcessId(rs.getLong("functionalprocessid"));
                                        actor.setName(rs.getString("name"));
                                        actor.setNotes(rs.getString("notes"));
                                        return actor;
                                    }
                                });

                model.addAttribute("functionalprocesslist", actors);

                if (functionalprocessname != null && !functionalprocessname.equals("")) {

                    this.functionalProcess = this.jdbcTemplate
                            .queryForObject(
                                    "select functionalprocessid, version, name, notes from functionalprocess where not deleteflag and systemcontextid = "
                                            + name
                                            + " and name = '"
                                            + functionalprocessname.replace("'", "''") + "'",
                                    new RowMapper<FunctionalProcess>() {
                                        public FunctionalProcess mapRow(
                                                ResultSet rs, int rowNum)
                                                throws SQLException {
                                            FunctionalProcess actor = new FunctionalProcess();
                                            actor.setFunctionalProcessId(rs.getLong("functionalprocessid"));
                                            actor.setName(rs.getString("name"));
                                            actor.setNotes(rs.getString("notes"));
                                            return actor;
                                        }
                                    });
                }

                model.addAttribute("functionalprocess", this.functionalProcess);

                List<FunctionalSubProcess> functionalsubprocesslist = this.jdbcTemplate
                        .query("select functionalsubprocessid, functionalprocessid, version, name from functionalsubprocess where not deleteflag and version = 0 and functionalprocessid = "
                                        + this.functionalProcess.getFunctionalProcessId(),
                                new RowMapper<FunctionalSubProcess>() {
                                    public FunctionalSubProcess mapRow(
                                            ResultSet rs, int rowNum)
                                            throws SQLException {
                                        FunctionalSubProcess datafield = new FunctionalSubProcess();
                                        datafield.setFunctionalSubProcessId(rs.getLong("functionalsubprocessid"));
                                        datafield.setFunctionalProcessId(rs.getLong("functionalprocessid"));
                                        datafield.setVersion(rs.getInt("version"));
                                        datafield.setName(rs.getString("name"));
                                        return datafield;
                                    }
                                });

                model.addAttribute("functionalsubprocesslist", functionalsubprocesslist);
            }

        }

        return "define-functional-processes";
    }

}