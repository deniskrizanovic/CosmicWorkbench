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
import java.util.Iterator;
import java.util.List;

@Controller
public class FunctionalProcessController
{

    private JdbcTemplate jdbcTemplate;

    private SystemContext systemContext;

    private FunctionalProcess funcProc;

    private FunctionalProcessRepository fpRepository;

    private SystemContextRepository scRepository;

    private String err;

    @Autowired
    public void setFpRepository(FunctionalProcessRepository fpRepository)
    {
        this.fpRepository = fpRepository;
    }

    @Autowired
    public void setScRepository(SystemContextRepository scRepository)
    {
        this.scRepository = scRepository;
    }

    @Autowired
    public void setDataSource(DataSource dataSource)
    {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @RequestMapping(value = "/disp-functional-processes", method = {RequestMethod.GET, RequestMethod.POST})
    public String dispFunctionalProcess(Model model, HttpServletRequest request, HttpSession session)
    {


        if (session.getAttribute("systemcontextid") != null)
        {
            Long name = (Long) session.getAttribute("systemcontextid");
            List<FunctionalProcess> actors = fpRepository.getListOfFunctionalProcessesForContext(name);

            model.addAttribute("functionalprocesslist", actors);
        }


        return "/define-functional-processes";
    }

    @RequestMapping("/define-functional-processes")
    public String showFunctionalProcess(Model model, HttpSession session)
    {

        System.out.println("or is this me?");

        long functionalProcessId = 0l;

        List<FunctionalSubProcess> functionalsubprocesslist = null;

        List<FunctionalProcess> actors = null;

        if (session.getAttribute("systemcontextid") != null)
        {


            Long systemcontextid = (Long) session.getAttribute("systemcontextid");
            actors = fpRepository.getListOfFunctionalProcessesForContext(systemcontextid);

            model.addAttribute("functionalprocesslist", actors);

            //todo not sure if I really need this at all.
            if (actors.size() > 0)
            {
                functionalProcessId = actors.get(0).getFunctionalProcessId();
                this.funcProc = actors.get(0);
            } else
            {
                this.funcProc = null;
            }

            functionalsubprocesslist = fpRepository.getListofSubProcesses(functionalProcessId);

            model.addAttribute("functionalsubprocesslist", functionalsubprocesslist);
        }

        model.addAttribute("functionalprocess", this.funcProc);

        return "define-functional-processes";
    }


    @RequestMapping(value = "/create-new-functional-process", method = {RequestMethod.GET, RequestMethod.POST})
    public String createSystemContext(Model model, HttpServletRequest request, HttpSession session)
    {

        //todo there should be some defensive coding here.

        Long systemContextId = (Long) session.getAttribute("systemcontextid");

        String functionalprocessname = request.getParameter("functionalprocessname");
        String functionalprocessnotes = request.getParameter("functionalprocessnotes");
        String functionalsubprocessname = request.getParameter("functionalsubprocessname");

        int version = 0;
        long functionalProcessId = 0l;

        if (systemContextId != null)
        {

            this.systemContext = scRepository.getSystemContextDetailsById(String.valueOf(systemContextId));

            if (this.systemContext != null && this.systemContext.getSystemContextId() != 0l)
            {
                systemContextId = this.systemContext.getSystemContextId(); //don't I already have this?
            }

            if (systemContextId != 0l)
            {
                String username = (String) session.getAttribute("username");
                if (isSave(request))
                {
                     //todo an interesting test is if I try and create a fp by a name that already exists.
                    //todo I think they fixed this by using a lookup in the systemcontext .. but what about data group?
                    if (fPAlreadyExists(functionalprocessname, systemContextId))
                    {
                        err = "Data group already exist";
                        model.addAttribute("err", err);
                        return getFunctionalProcess(model, request, session);
                    }

                    funcProc = fpRepository.createNewFunctionalProcess(systemContextId, functionalprocessname, functionalprocessnotes, username);


                    if (this.funcProc != null && this.funcProc.getFunctionalProcessId() != 0l)
                    {

                        functionalProcessId = this.funcProc.getFunctionalProcessId();

                        session.setAttribute("functionalprocessname", this.funcProc.getFunctionalProcessId());
                    }

                    if (addNewSubProcessSteps(functionalsubprocessname, functionalProcessId))
                    {

                        fpRepository.createSubProcessSteps(functionalsubprocessname, version, functionalProcessId, username);

                        List<FunctionalSubProcess> sub = fpRepository.getSubProcessSteps(functionalsubprocessname, functionalProcessId);

                        Long tempId = 0l;

                        if (sub.size() > 0)
                        {
                            int i = sub.size();
                            tempId = sub.get(i - 1).getFunctionalSubProcessId();
                        }
                          //todo need to work this out too
                        //addNewSubProcessStepsToFunctionalModel(systemContextId, version, functionalProcessId, username, tempId);

                    }

                } else if (isDeleteFunctionalProcess(request))
                {

                    deleteFunctionalProcess(systemContextId, functionalprocessname);

                    return dispFunctionalProcess(model, request, session);

                } else if (request.getParameter("option") != null && request.getParameter("option").equals("2"))
                {

                    return "define-functional-processes";

                } else if (request.getParameter("option") != null && request.getParameter("option").equals("3"))
                {

                    return "define-functional-processes";
                } else if (isAnUpdate(request))
                {

                    Long name = (Long) session.getAttribute("systemcontextid");

                    this.funcProc = this.jdbcTemplate
                            .queryForObject(
                                    "select functionalprocessid, version, name, notes from functionalprocess where not deleteflag and systemcontextid = "
                                            + name
                                            + " and name = '"
                                            + functionalprocessname.replace(
                                            "'", "''") + "'",
                                    new RowMapper<FunctionalProcess>()
                                    {
                                        public FunctionalProcess mapRow(
                                                ResultSet rs, int rowNum)
                                                throws SQLException
                                        {
                                            FunctionalProcess actor = new FunctionalProcess();
                                            actor.setFunctionalProcessId(rs.getLong("functionalprocessid"));
                                            actor.setName(rs.getString("name"));
                                            actor.setNotes(rs.getString("notes"));
                                            return actor;
                                        }
                                    });

                    if (this.funcProc != null && this.funcProc.getFunctionalProcessId() != 0l)
                    {
                        functionalProcessId = this.funcProc.getFunctionalProcessId();
                    }

                    if (functionalProcessId != 0l && functionalsubprocessname != null && !functionalsubprocessname.isEmpty())
                    {

                        fpRepository.createSubProcessSteps(functionalsubprocessname, version, functionalProcessId, username);

                        List<FunctionalSubProcess> sub = fpRepository.getSubProcessSteps(functionalsubprocessname, functionalProcessId);

                        Long tempId = 0l;

                        if (sub.size() > 0)
                        {
                            int i = sub.size();
                            tempId = sub.get(i - 1).getFunctionalSubProcessId();
                        }


                        //todo need to figure out what he's doing here
                        //updateFunctionalModel(systemContextId, version, functionalProcessId, username, tempId);

                    }
                } else if (request.getParameter("option") != null && request.getParameter("option").equals("delete"))
                {

                    long functionalSubProcessId = 0l;

                    if (request.getParameter("delete") != null)
                    {
                        functionalSubProcessId = Long.parseLong(request.getParameter("delete"));
                    }

                    this.jdbcTemplate
                            .update(" update functionalsubprocess set deleteflag = true where version = 0 and functionalsubprocessid = "
                                    + functionalSubProcessId);

                }

            }

        }

        this.funcProc = null;

        if (functionalprocessname == null || functionalprocessname.equals("") || (request.getParameter("option") != null && request.getParameter("option").equals("0")))
        {

            return "define-functional-processes";
        } else
        {

            return getFunctionalProcess(model, request, session);
        }

    }

    private boolean isAnUpdate(HttpServletRequest request)
    {
        return request.getParameter("option") != null && request.getParameter("option").equals("4");
    }

    private void updateFunctionalModel(Long systemContextId, int version, long functionalProcessId, String username, Long tempId)
    {
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
                        + username
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

    private boolean fPAlreadyExists(String functionalprocessname, Long systemContextId)
    {

        List<FunctionalProcess> functionalProcessList = fpRepository.getListOfFunctionalProcessesForContext(systemContextId);

        for (Iterator<FunctionalProcess> iterator = functionalProcessList.iterator(); iterator.hasNext(); )
        {
            FunctionalProcess next = iterator.next();
            if (next.getName().equals(functionalprocessname))
            {
                return true;
            }

        }

        return functionalProcessList.contains(functionalprocessname);
    }

    public void deleteFunctionalProcess(Long systemContextId, String functionalprocessname)
    {
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
    }

    public boolean isDeleteFunctionalProcess(HttpServletRequest request)
    {
        return request.getParameter("option") != null && request.getParameter("option").equals("0");
    }

    public void addNewSubProcessStepsToFunctionalModel(Long systemContextId, int version, long functionalProcessId, Object username, Long tempId)
    {
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
                        + username
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


    public boolean addNewSubProcessSteps(String functionalsubprocessname, long functionalProcessId)
    {
        return functionalProcessId != 0l && functionalsubprocessname != null && !functionalsubprocessname.isEmpty();
    }


    public boolean isSave(HttpServletRequest request)
    {
        return request.getParameter("option") != null && request.getParameter("option").equals("save");
    }

    @RequestMapping(value = "/show-functional-processes", method = {RequestMethod.GET, RequestMethod.POST})
    public String getFunctionalProcess(Model model, HttpServletRequest request, HttpSession session)
    {
        if (session.getAttribute("systemcontextid") != null)
        {
            if (request.getParameter("functionalprocessid") != null && !(request.getParameter("functionalprocessid") + "").equals(""))
            {

                Long systemContextId = (Long) session.getAttribute("systemcontextid");
                String functionalprocessid = request.getParameter("functionalprocessid");

                List<FunctionalProcess> fpList =  fpRepository.getListOfFunctionalProcessesForContext(systemContextId);

                model.addAttribute("functionalprocesslist", fpList);

                if (functionalprocessid != null && !functionalprocessid.equals(""))
                {

                    this.funcProc = fpRepository.getFunctionalProcessById(systemContextId, functionalprocessid) ;
                }

                model.addAttribute("functionalprocess", this.funcProc);

                List<FunctionalSubProcess> functionalsubprocesslist = fpRepository.getListofSubProcesses(Long.parseLong(functionalprocessid));

                model.addAttribute("functionalsubprocesslist", functionalsubprocesslist);

            } else
            {

                System.out.println("do I get here at any time?");
                Long systemContextId = (Long) session.getAttribute("systemcontextid");
                String functionalprocessname = request.getParameter("functionalprocessname");


                List<FunctionalProcess> actors =  fpRepository.getListOfFunctionalProcessesForContext(systemContextId);

                model.addAttribute("functionalprocesslist", actors);

                if (functionalprocessname != null && !functionalprocessname.equals(""))
                {

                    this.funcProc =  fpRepository.getFunctionalProcessByName(systemContextId, functionalprocessname);
                }

                model.addAttribute("functionalprocess", this.funcProc);
                //todo why doens't this just comeback as part of the model? who taught this person to code?
                List<FunctionalSubProcess> functionalsubprocesslist = fpRepository.getListofSubProcesses(funcProc.getFunctionalProcessId());


                model.addAttribute("functionalsubprocesslist", functionalsubprocesslist);
            }

        }

        return "define-functional-processes";
    }

}