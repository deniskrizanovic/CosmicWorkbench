package com.fp.web;

import com.fp.dao.DataGroupRepository;
import com.fp.dao.FunctionalModelRepository;
import com.fp.dao.FunctionalProcessRepository;
import com.fp.dao.Repository;
import com.fp.domain.*;
import com.fp.model.ModelBuilder;
import com.fp.model.Movement;
import com.fp.model.SizingContext;
import com.fp.model.SubProcess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class FunctionalModelController {

    private JdbcTemplate jdbcTemplate;

    private SystemContext systemContext;

    private String err;

    private DataGroup dataGroup;

    private FunctionalProcess functionalProcess;

    private FunctionalModel functionalModel;

    private int finalScore = 0;
    private FunctionalProcessRepository fpRepository;
    private FunctionalModelRepository fmRepository;
    private DataGroupRepository dgRepository;
    private Repository repository;
    @Autowired
    SizingContext sizingContext;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    @Autowired
    public void setFpRepository(FunctionalModelRepository fmRepository) {
        this.fmRepository = fmRepository;
    }


    @Autowired
    public void setFpRepository(FunctionalProcessRepository fpRepository) {
        this.fpRepository = fpRepository;
    }

    @Autowired
    public void setDataGroupRepository(DataGroupRepository repository) {
        this.dgRepository = repository;
    }

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @RequestMapping("/define-functional-model")
    public String showFunctionalModel(Model model, HttpServletRequest request, HttpSession session) {

        List<FunctionalSubProcess> functionalsubprocesslist = null;

        List<FunctionalProcess> functionalProcessList = null;

        Long funcProcId = 0l;

        List<Report> report = null;
        List<FunctionalModel> functionalmodellist = null;
        List<FunctionalModel> distinctfunctionalmodellist = null;

        List<FunctionalModelDataField> functionalmodeldatafieldlist = null;

        List<FunctionalModelFunctionalSubProcess> functionalmodelfunctionalsubprocesslist = null;

        long systemContextId = (long) session.getAttribute("systemcontextid");

        if (session.getAttribute("systemcontextid") != null) {

            functionalProcessList = fpRepository.getListOfFunctionalProcessesForContext(systemContextId);

            //todo can this ever be 0? Maybe If I get here before I define a FunctionalProcess?
            //todo perhaps the button should not be enabled if that is the case.
            if (functionalProcessList.size() == 0) {
                session.setAttribute("functionalprocessname", funcProcId);
                return "define-functional-model";
            }

            //I must set this attribute somewhere in this code?
            if (session.getAttribute("functionalprocessname") != null) {

                System.out.println("Why would I get here?");
                //I get in here because I've changed the functional process I want to map
                funcProcId = Long.parseLong(session.getAttribute("functionalprocessname") + "");
                this.functionalProcess = fpRepository.getFunctionalProcessById(systemContextId, String.valueOf(funcProcId));

            } else {

                if (functionalProcessList.size() > 0) {
                    //	functionalprocessname = actors.get(0).getName();
                    funcProcId = functionalProcessList.get(0).getFunctionalProcessId();
                    this.functionalProcess = functionalProcessList.get(0);
                    session.setAttribute("functionalprocessname", funcProcId);

                }
            }

            model.addAttribute("functionalprocesslist", functionalProcessList);

            functionalsubprocesslist = fpRepository.getListofSubProcesses(funcProcId);


            distinctfunctionalmodellist = fmRepository.getListOfDistinctFunctionalModels(systemContextId, functionalProcess);

            model.addAttribute("distinctfunctionalmodellist", distinctfunctionalmodellist);

            functionalmodellist = fmRepository.getListOfFunctionalModels(systemContextId, funcProcId);

            model.addAttribute("functionalmodellist", functionalmodellist);

            functionalmodeldatafieldlist = fmRepository.getDataFieldsForFunctionalModel(systemContextId, functionalProcess);


            for (FunctionalSubProcess functionalSubProcess : functionalsubprocesslist) {
                List<FunctionalModel> temp = new ArrayList<FunctionalModel>();
                for (FunctionalModel functionalModel : functionalmodellist) {
                    if (functionalSubProcess.getFunctionalSubProcessId() == functionalModel.getFunctionalSubProcessId()) {

                        FunctionalModel functionalmodel = new FunctionalModel();
                        functionalmodel = functionalModel;
                        String value = "";

                        for (FunctionalModelDataField functionalmodeldatafield : functionalmodeldatafieldlist) {
                            if (functionalModel.getFunctionalModelId() == functionalmodeldatafield.getFunctionalModelId()) {
                                if (value.equals("")) {
                                    value = value + functionalmodeldatafield.getDatafieldId();
                                } else {
                                    value = value + "," + functionalmodeldatafield.getDatafieldId();
                                }
                            }
                        }
                        functionalmodel.setDatafieldList(value);
                        temp.add(functionalmodel);
                    }
                }
                if (temp != null) {
                    functionalSubProcess.setFunctionalModel(temp);
                }

            }


            model.addAttribute("functionalsubprocesslist", functionalsubprocesslist);

            List<Score> scores = this.jdbcTemplate
                    .query("select sum(score) as score, max(a.datagroupid) as datagroupid from functionalmodel a, datagroup b, functionalsubprocess c where a.datagroupid = b.datagroupid and a.functionalsubprocessid = c.functionalsubprocessid and a.systemcontextid = "
                                    + systemContextId
                                    + " and a.functionalprocessid = "
                                    + this.functionalProcess.getFunctionalProcessId()
                                    + " and not a.deleteflag group by a.datagroupid order by a.datagroupid",
                            new RowMapper<Score>() {
                                public Score mapRow(ResultSet rs, int rowNum)
                                        throws SQLException {
                                    Score actor = new Score();
                                    actor.setScore(rs.getInt("score"));
                                    actor.setDataGroupId(rs.getLong("datagroupid"));
                                    return actor;
                                }
                            });

            model.addAttribute("scores", scores);

            this.finalScore = 0;

            for (Score score : scores) {
                this.finalScore = this.finalScore + score.getScore();
            }

            List<DataGroup> datagrouplist = dgRepository.getDataGroupsForSystemContext(systemContextId);

            model.addAttribute("datagrouplist", datagrouplist);

            model.addAttribute("finalScore", this.finalScore);

//            List<DataField> datafieldlist = dgRepository.getDataFieldsForADataGroup(dataGroup.getDataGroupId());
//
//            model.addAttribute("datafieldlist", datafieldlist);

        }

        return "define-functional-model";
    }


    @RequestMapping(value = "/create-new-functional-model", method = {RequestMethod.GET, RequestMethod.POST})
    public String createFunctionalModel(Model model, HttpServletRequest request, HttpSession session) {

        String datagroupname = "";

        if (request.getParameter("datagroupname") != null) {
            datagroupname = request.getParameter("datagroupname");
            session.setAttribute("datagroupname", datagroupname);
        }

        Long fpId = (Long) session.getAttribute("functionalprocessname");  //todo this gets the process name, which seems to be overloaded in the html

        long systemContextId = (long) session.getAttribute("systemcontextid");

        if (systemContextId != 0l) {

            DataGroup dataGroup = dgRepository.getDataGroupByName(systemContextId, datagroupname.replace("'", "''"));

            long dataGroupId = 0l;
            if (dataGroup != null) {

                dataGroupId = dataGroup.getDataGroupId();

            }

            FunctionalProcess functionalProcess = fpRepository.getFunctionalProcessById(systemContextId, String.valueOf(fpId));

            long functionalProcessId = 0l;
            if (functionalProcess != null) {

                functionalProcessId = functionalProcess.getFunctionalProcessId();

            }


            Object username = session.getAttribute("username");
            if (addingDataGroupToModel(request)) {

                List<FunctionalModel> functionalModel = fmRepository.getListOfFunctionalModelsForFunctionalProcess(dataGroupId, functionalProcessId, systemContextId);

                if (functionalModel.size() > 0) {
                    err = "Data group already exists in this functional model.. so why am I allowing you to select it?";
                    model.addAttribute("err", err);

                    // return getDataAttributeList(model, request, session);

                } else {
                    fmRepository.insertDataGroupIntoFunctionalModel(dataGroupId, functionalProcessId, systemContextId, username);

                }

            } else if (request.getParameter("option") != null && request.getParameter("option").equals("0")) {

                List<FunctionalModel> functionalModeltmp = fmRepository.getListOfFunctionalModelsForFunctionalProcess(dataGroupId, functionalProcessId, systemContextId);

                if (functionalModeltmp.size() > 0) {
                    /*
                     * this.jdbcTemplate .update(
					 * " delete from functionalmodel where systemcontextid = " +
					 * systemContextId + " and functionalprocessid = " +
					 * functionalProcessId + " and datagroupid = " +
					 * dataGroupId);
					 */
                    this.jdbcTemplate
                            .update(" update functionalmodel set deleteflag = true where systemcontextid = "
                                    + systemContextId
                                    + " and functionalprocessid = "
                                    + functionalProcessId
                                    + " and datagroupid = " + dataGroupId);

                }
            } else if (request.getParameter("option") != null && request.getParameter("option").equals("2")) {

                long functionalmodelid = Long.parseLong(request.getParameter("functionalmodelid"));

                this.jdbcTemplate
                        .update(" update functionalmodel set grade = ''"
                                + ", notes = '',"
                                + " score = 0 where functionalmodelid = "
                                + functionalmodelid);

                this.jdbcTemplate
                        .update(" delete from functionalmodeldatafield where functionalmodelid = "
                                + functionalmodelid);

            } else if (request.getParameter("option") != null && request.getParameter("option").equals("saveNewAttributes")) {

                String notes = request.getParameter("notes");
                String grade = request.getParameter("grade");
                String[] dataAttributes = request.getParameterValues("datafields");
                String[] test = request.getParameterValues("dataAttribute");


                long functionalmodelid = Long.parseLong(request.getParameter("functionalmodelid"));

                fmRepository.updateFunctionalModelWithTypeAndNotes(notes, grade, functionalmodelid);

                int len = 0;

                long datafieldid = 0l;

                if (dataAttributes != null) {
                    //	String[] parts = checkbox.split(",");

                    //todo this statmenet should be deleted.
                    this.jdbcTemplate
                            .update(" delete from functionalmodeldatafield where functionalmodelid = "
                                    + functionalmodelid);

                    while (len < dataAttributes.length) {
                        datafieldid = Long.parseLong(dataAttributes[len]);
                        this.jdbcTemplate
                                .update(" insert into functionalmodeldatafield (functionalmodelid, datafieldid, version, userid) values ( "
                                        + functionalmodelid
                                        + ","
                                        + datafieldid
                                        + ",0,'"
                                        + username
                                        + "')");
                        len = len + 1;
                    }
                } else {
                    this.jdbcTemplate
                            .update(" delete from functionalmodeldatafield where functionalmodelid = "
                                    + functionalmodelid);
                }

            }

        }

        return showFunctionalModel(model, request, session);

    }


    private boolean addingDataGroupToModel(HttpServletRequest request) {
        return request.getParameter("option") != null && request.getParameter("option").equals("addDataGroupToModel");
    }


    @RequestMapping(value = "/show-functional-model", method = {RequestMethod.GET, RequestMethod.POST})
    public String getFunctionalModel(Model model, HttpServletRequest request, HttpSession session) {

        List<FunctionalModel> functionalmodellist = null;


        List<FunctionalModelDataField> functionalmodeldatafieldlist = null;

        long systemContextId = (long) session.getAttribute("systemcontextid");

        Long functionalprocessname = 0l;

        if (request.getParameter("functionalprocessname") != null) {
            functionalprocessname = Long.parseLong(request.getParameter("functionalprocessname"));
            session.setAttribute("functionalprocessname", functionalprocessname);
        } else {
            if (session.getAttribute("functionalprocessname") != null) {
                functionalprocessname = (Long) session.getAttribute("functionalprocessname");
            }
        }

        List<FunctionalModel> distinctfunctionalmodellist = null;

        if (session.getAttribute("systemcontextid") != null) {
            Long name = (Long) session.getAttribute("systemcontextid");

            List<FunctionalProcess> actors = this.jdbcTemplate
                    .query("select functionalprocessid, version, name, notes from functionalprocess where not deleteflag and systemcontextid = "
                            + name + "", new RowMapper<FunctionalProcess>() {
                        public FunctionalProcess mapRow(ResultSet rs, int rowNum)
                                throws SQLException {
                            FunctionalProcess actor = new FunctionalProcess();
                            actor.setFunctionalProcessId(rs.getLong("functionalprocessid"));
                            actor.setName(rs.getString("name"));
                            actor.setNotes(rs.getString("notes"));
                            return actor;
                        }
                    });

            model.addAttribute("functionalprocesslist", actors);

            if (functionalprocessname != null) {

                this.functionalProcess = this.jdbcTemplate
                        .queryForObject(
                                "select functionalprocessid, version, name, notes from functionalprocess where not deleteflag and systemcontextid = "
                                        + name
                                        + " and functionalprocessid = "
                                        + functionalprocessname + "",
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
                    .query("select functionalsubprocessid, functionalprocessid, version, name from functionalsubprocess where functionalprocessid = "
                                    + this.functionalProcess.getFunctionalProcessId()
                                    + " and not deleteflag and version = 0 order by functionalsubprocessid",
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

            distinctfunctionalmodellist = fmRepository.getListOfDistinctFunctionalModels(systemContextId, functionalProcess);

            model.addAttribute("distinctfunctionalmodellist", distinctfunctionalmodellist);

            functionalmodellist = fmRepository.getListOfFunctionalModels(systemContextId, this.functionalProcess.getFunctionalProcessId());

            model.addAttribute("functionalmodellist", functionalmodellist);

            functionalmodeldatafieldlist = fmRepository.getDataFieldsForFunctionalModel(systemContextId, functionalProcess);

            for (FunctionalSubProcess functionalSubProcess : functionalsubprocesslist) {
                List<FunctionalModel> temp = new ArrayList<FunctionalModel>();
                for (FunctionalModel functionalModel : functionalmodellist) {
                    if (functionalSubProcess.getFunctionalSubProcessId() == functionalModel.getFunctionalSubProcessId()) {

                        FunctionalModel functionalmodel = new FunctionalModel();
                        functionalmodel = functionalModel;
                        String value = "";

                        for (FunctionalModelDataField functionalmodeldatafield : functionalmodeldatafieldlist) {
                            if (functionalModel.getFunctionalModelId() == functionalmodeldatafield.getFunctionalModelId()) {
                                if (value.equals("")) {
                                    value = value + functionalmodeldatafield.getDatafieldId();
                                } else {
                                    value = value + "," + functionalmodeldatafield.getDatafieldId();
                                }
                            }
                        }
                        functionalmodel.setDatafieldList(value);
                        temp.add(functionalmodel);
                    }
                }
                if (temp != null) {
                    functionalSubProcess.setFunctionalModel(temp);
                }

            }

			/*

			for (FunctionalSubProcess functionalSubProcess : functionalsubprocesslist) {
				List<FunctionalModel> temp = new ArrayList<FunctionalModel>();
				for (FunctionalModel functionalModel : functionalmodellist) {
					if (functionalSubProcess.getFunctionalSubProcessId() == functionalModel
							.getFunctionalSubProcessId()) {
						temp.add(functionalModel);
					}
				}
				if (temp != null) {
					functionalSubProcess.setFunctionalModel(temp);
				}

			}
			*/

            List<Score> scores = this.jdbcTemplate
                    .query("select sum(score) as score, max(a.datagroupid) as datagroupid from functionalmodel a, datagroup b, functionalsubprocess c where a.datagroupid = b.datagroupid and a.functionalsubprocessid = c.functionalsubprocessid and a.systemcontextid = "
                                    + systemContextId
                                    + " and a.functionalprocessid = "
                                    + this.functionalProcess.getFunctionalProcessId()
                                    + " and not a.deleteflag group by a.datagroupid order by a.datagroupid",
                            new RowMapper<Score>() {
                                public Score mapRow(ResultSet rs, int rowNum)
                                        throws SQLException {
                                    Score actor = new Score();
                                    actor.setScore(rs.getInt("score"));
                                    actor.setDataGroupId(rs.getLong("datagroupid"));
                                    return actor;
                                }
                            });

            model.addAttribute("scores", scores);

            this.finalScore = 0;

            for (Score score : scores) {
                this.finalScore = this.finalScore + score.getScore();
            }

            model.addAttribute("finalScore", this.finalScore);

            model.addAttribute("functionalsubprocesslist", functionalsubprocesslist);

            List<DataGroup> datagrouplist = dgRepository.getDataGroupsForSystemContext(name);


            model.addAttribute("datagrouplist", datagrouplist);

            List<DataField> datafieldlist = dgRepository.getDataFieldsForADataGroup(dataGroup.getDataGroupId());

            model.addAttribute("datafieldlist", datafieldlist);

        }

        return "define-functional-model";

    }

    @RequestMapping("/list-of-data-groups")
    public String getDataGroupList(Model model, HttpServletRequest request, HttpSession session) {

        List<DataGroup> actors = null;

        if (session.getAttribute("systemcontextid") != null) {
            Long name = (Long) session.getAttribute("systemcontextid");
            actors = dgRepository.getDataGroupsForSystemContext(name);

            model.addAttribute("datagrouplist", actors);

        }

        return "list-of-data-groups";

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

        System.out.println("before anything");
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


        return "select-data-attributes";
    }


    @RequestMapping(value = "/get-data-attribute-list", method = {RequestMethod.GET, RequestMethod.POST})
    public String getDataAttributeList(Model model, HttpServletRequest request, HttpSession session) {

        if (session.getAttribute("systemcontextid") != null) {
            Long name = (Long) session.getAttribute("systemcontextid");
            String datagroupname = request.getParameter("datagroupname");
            session.setAttribute("datagroupname", datagroupname);
            List<DataGroup> actors = dgRepository.getDataGroupsForSystemContext(name);

            model.addAttribute("datagrouplist", actors);

            if (datagroupname != null && !datagroupname.equals("")) {

                this.dataGroup = this.jdbcTemplate
                        .queryForObject(
                                "select datagroupid, version, name, notes from datagroup where not deleteflag and systemcontextid = "
                                        + name
                                        + " and name = '"
                                        + datagroupname.replace("'", "''") + "'",
                                new RowMapper<DataGroup>() {
                                    public DataGroup mapRow(ResultSet rs,
                                                            int rowNum) throws SQLException {
                                        DataGroup actor = new DataGroup();
                                        actor.setDataGroupId(rs.getLong("datagroupid"));
                                        actor.setName(rs.getString("name"));
                                        actor.setNotes(rs.getString("notes"));
                                        return actor;
                                    }
                                });
            }

            model.addAttribute("datagroup", this.dataGroup);

            List<DataField> datafieldlist = this.jdbcTemplate
                    .query("select datafieldid, datagroupid, version, name from datafield where not deleteflag and version = 0 and datagroupid = "
                                    + this.dataGroup.getDataGroupId(),
                            new RowMapper<DataField>() {
                                public DataField mapRow(ResultSet rs, int rowNum)
                                        throws SQLException {
                                    DataField datafield = new DataField();
                                    datafield.setDataFieldId(rs.getLong("datafieldid"));
                                    datafield.setDataGroupId(rs.getLong("datagroupid"));
                                    datafield.setVersion(rs.getInt("version"));
                                    datafield.setName(rs.getString("name"));
                                    return datafield;
                                }
                            });

            model.addAttribute("datafieldlist", datafieldlist);

        }

        return "list-of-data-groups";
    }
}