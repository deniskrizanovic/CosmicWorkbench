package com.fp.web;

import com.fp.dao.DataGroupRepository;
import com.fp.dao.SystemContextRepository;
import com.fp.domain.DataField;
import com.fp.domain.DataGroup;
import com.fp.domain.SystemContext;
import com.fp.model.ModelBuilder;
import com.fp.model.SizingContext;
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
public class DataGroupController {

    @Autowired
    SizingContext sizingContext;
    private JdbcTemplate jdbcTemplate;
    private SystemContext systemContext;
    private DataGroup dataGroup;
    private DataGroupRepository dataGroupRepository;
    private SystemContextRepository systemContextRepository;

    @Autowired
    public void setSystemContextRepository(SystemContextRepository repository) {
        this.systemContextRepository = repository;
    }

    @Autowired
    public void setDataGroupRepository(DataGroupRepository repository) {
        this.dataGroupRepository = repository;
    }

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void setupSystemContext(Model model, HttpSession session) {

        Long name = (Long) session.getAttribute("systemcontextid");
        sizingContext.setId(name.intValue());
        model.addAttribute("sizingCtx", sizingContext);
    }


    @RequestMapping(value = "/disp-data-groups", method = {RequestMethod.GET, RequestMethod.POST})
    public String dispDataGroup(Model model, HttpSession session) {

        setupSystemContext(model, session);
        return "/define-data-groups";
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
        String dataGroupId = request.getParameter("dgId");
        String userName = (String) session.getAttribute("username");

        com.fp.model.DataGroup dg = ModelBuilder.buildDataGroup(dataGroupName, notes, attribs, Integer.parseInt(dataGroupId), userName);
        sizingContext.saveDataGroup(dg);


        return "define-data-groups";
    }


    @RequestMapping(value = "/oldcreate-new-data-group", method = {RequestMethod.GET, RequestMethod.POST})
    public String OldcreatingNewDataGroup(Model model, HttpServletRequest request, HttpSession session) {

        String systemContextId = String.valueOf(session.getAttribute("systemcontextid")); //todo not sure why it's a long in the session.

        String datagroupname = request.getParameter("datagroupname");
        String datagroupnotes = request.getParameter("datagroupnotes");
        String datafieldname = request.getParameter("datafieldname");

        String userName = (String) session.getAttribute("username");

        long dataGroupId = 0;

        if (request.getParameter("datagroupid") != null && !request.getParameter("datagroupid").equals("")) {
            dataGroupId = Long.parseLong(request.getParameter("datagroupid"));
        }


        if (systemContextId != null) //and why the fuck would it ever be?
        {

            this.systemContext = systemContextRepository.getSystemContextDetailsById(systemContextId);

            if (creatingNewDataGroup(request)) {
                createNewDataGroup(systemContextId, datagroupname, datagroupnotes, datafieldname, dataGroupId, userName);

                session.setAttribute("datagroupname", datagroupname);
                getDataGroup(model, request, session);

                return "define-data-groups";

            } else if (amIDeletingADataGroup(request)) {

                this.jdbcTemplate
                        .update(" update datafield set deleteflag = true where version = 0 and datagroupid in (select datagroupid from datagroup where systemcontextid = "
                                + systemContextId
                                + " and name = '"
                                + datagroupname.replace("'", "''") + "')");

                this.jdbcTemplate
                        .update(" update datagroup set deleteflag = true where name = '"
                                + datagroupname.replace("'", "''")
                                + "' and systemcontextid = "
                                + systemContextId);

                return dispDataGroup(model, session);

            } else if (amIDeletingDataAttribute(request)) {

                //todo can't get here yet till I fix the datafield table
                System.out.println("I'm deleting an attribute");

                long dataFieldId = 0l;

                if (request.getParameter("delete") != null) {
                    dataFieldId = Long.parseLong(request.getParameter("delete"));
                }

                this.jdbcTemplate
                        .update(" update datafield set deleteflag = true where version = 0 and datafieldid = "
                                + dataFieldId);

            } else {
                System.out.println("I don't think I ever get here. option=" + request.getParameter("option"));
                return "define-data-groups";
            }

        }


        this.dataGroup = null;

        if (datagroupname == null || datagroupname.equals("") || (request.getParameter("option") != null && request.getParameter("option").equals("0"))) {

            return "define-data-groups";

        } else {

            return getDataGroup(model, request, session);
        }

    }

    private boolean amIDeletingADataGroup(HttpServletRequest request) {
        return request.getParameter("option") != null && request.getParameter("option").equals("delete");
    }

    private boolean amIDeletingDataAttribute(HttpServletRequest request) {
        return request.getParameter("option") != null && request.getParameter("option").equals("deletingDataAttribute");
    }


    private DataGroup createNewDataGroup(String systemContextId, String dataGroupName, String dataGroupNotes, String datafieldname, long dataGroupId, String userName) {
        dataGroup = dataGroupRepository.createDataGroup(systemContextId, dataGroupId, dataGroupName, dataGroupNotes, userName);
        addDataField(datafieldname, dataGroup, userName);
        return dataGroup;
    }

    private void addDataField(String newAttribute, DataGroup dataGroup, String userName) {

        System.out.println("adding data field::" + newAttribute + "::");
        if (newAttribute == null || newAttribute.equals("")) {
            return;
        }

        dataGroupRepository.createDataFields(dataGroup, newAttribute, userName);

    }


    private boolean creatingNewDataGroup(HttpServletRequest request) {
        return request.getParameter("option") != null && request.getParameter("option").equals("save");
    }

    @RequestMapping(value = "/show-data-groups", method = {RequestMethod.GET, RequestMethod.POST})
    public String getDataGroup(Model model, HttpServletRequest request, HttpSession session) {


        setupSystemContext(model, session);


//        Long systemContextId = (Long) session.getAttribute("systemcontextid");
//        String dataGroupId = request.getParameter("datagroupid");
//        String dataGroupName = request.getParameter("dataGroupName");

//        List<DataGroup> dataGroups = dataGroupRepository.getDataGroupsForSystemContext(systemContextId);
//
//        model.addAttributes("datagrouplist", dataGroups);
//
//        if (dataGroupIdIsPresent(request)) {
//            if (dataGroupId != null && !dataGroupId.equals("")) {
//                this.dataGroup = dataGroupRepository.getDataGroupById(systemContextId, dataGroupId);
//            }
//
//        } else {
//            if (dataGroupName != null && !dataGroupName.equals("")) {
//                this.dataGroup = dataGroupRepository.getDataGroupByName(systemContextId, dataGroupName);
//            }
//        }
//
//        model.addAttributes("datagroup", this.dataGroup);
//
//        List<DataField> datafieldlist = getDataFields(dataGroup.getDataGroupId());
//        model.addAttributes("datafieldlist", datafieldlist);


        return "define-data-groups";
    }

    private List<DataField> getDataFields(long dataGroupId) {
        return this.jdbcTemplate
                .query("select datafieldid, datagroupid, version, name from datafield where not deleteflag and version = 0 and datagroupid = "
                                + dataGroupId,
                        new RowMapper<DataField>() {
                            public DataField mapRow(ResultSet rs, int rowNum) throws SQLException {
                                DataField datafield = new DataField();
                                datafield.setDataFieldId(rs.getLong("datafieldid"));
                                datafield.setDataGroupId(rs.getLong("datagroupid"));
                                datafield.setVersion(rs.getInt("version"));
                                datafield.setName(rs.getString("name"));
                                return datafield;
                            }
                        });
    }


    private boolean dataGroupIdIsPresent(HttpServletRequest request) {
        return request.getParameter("datagroupid") != null && !(request.getParameter("datagroupid") + "").equals("");
    }


}