package com.fp.web;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.fp.domain.DataField;
import com.fp.domain.DataGroup;
import com.fp.domain.SystemContext;

@Controller
public class DateGroupController {

    private JdbcTemplate jdbcTemplate;

    private SystemContext systemContext;

    private DataGroup dataGroup;

    private String err;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @RequestMapping(value = "/disp-data-groups", method = {RequestMethod.GET, RequestMethod.POST})
    public String dispDataGroup(Model model, HttpServletRequest request,
                                HttpSession session) {

        if (session.getAttribute("systemcontextname") != null) {
            String name = (String) session.getAttribute("systemcontextname");
            List<DataGroup> actors = this.jdbcTemplate
                    .query("select datagroupid, version, name, notes from datagroup where systemcontextid = ( select systemcontextid from systemcontext where name = '"
                                    + name + "') and not deleteflag order by datagroupid",
                            new RowMapper<DataGroup>() {
                                public DataGroup mapRow(ResultSet rs, int rowNum)
                                        throws SQLException {
                                    DataGroup actor = new DataGroup();
                                    actor.setName(rs.getString("name"));
                                    actor.setNotes(rs.getString("notes"));
                                    return actor;
                                }
                            });

            model.addAttribute("datagrouplist", actors);
        }

        // this.dataGroup = null;
        // model.addAttribute("datagroup", this.dataGroup);

        return "/define-data-groups";
    }

    @RequestMapping("/define-data-groups")
    public String showDataGroup(Model model, HttpServletRequest request,
                                HttpSession session) {

        long dataGroupId = 0l;

        List<DataField> datafieldlist = null;

        List<DataGroup> actors = null;

        if (session.getAttribute("systemcontextname") != null) {
            String name = (String) session.getAttribute("systemcontextname");
            actors = this.jdbcTemplate
                    .query("select datagroupid, version, name, notes from datagroup where systemcontextid = ( select systemcontextid from systemcontext where name = '"
                                    + name + "') and not deleteflag order by datagroupid",
                            new RowMapper<DataGroup>() {
                                public DataGroup mapRow(ResultSet rs, int rowNum)
                                        throws SQLException {
                                    DataGroup actor = new DataGroup();
                                    actor.setName(rs.getString("name"));
                                    actor.setNotes(rs.getString("notes"));
                                    return actor;
                                }
                            });

            model.addAttribute("datagrouplist", actors);

            if (actors.size() > 0) {
                dataGroupId = actors.get(0).getDataGroupId();
            }

			/*
             * List<DataField> datafieldlist = this.jdbcTemplate .query(
			 * "select datafieldid, version, name from datafield where datagroupid in (select datagroupid from datagroup where systemcontextid = ( select systemcontextid from systemcontext where name = '"
			 * + name + "'))", new RowMapper<DataField>() { public DataField
			 * mapRow(ResultSet rs, int rowNum) throws SQLException { DataField
			 * actor = new DataField(); actor.setName(rs.getString("name"));
			 * return actor; } });
			 */
            datafieldlist = this.jdbcTemplate
                    .query("select datafieldid, datagroupid, version, name from datafield where not deleteflag and datagroupid = "
                            + dataGroupId, new RowMapper<DataField>() {
                        public DataField mapRow(ResultSet rs, int rowNum)
                                throws SQLException {
                            DataField actor = new DataField();
                            actor.setDataFieldId(rs.getLong("datafieldid"));
                            actor.setDataGroupId(rs.getLong("datagroupid"));
                            actor.setName(rs.getString("name"));
                            return actor;
                        }
                    });

            model.addAttribute("datafieldlist", datafieldlist);
        }

        model.addAttribute("datagroup", this.dataGroup);

        return "define-data-groups";
    }

    @RequestMapping(value = "/create-new-data-group", method = {
            RequestMethod.GET, RequestMethod.POST})
    public String createSystemContext(Model model, HttpServletRequest request,
                                      HttpSession session) {

        String contextname = (String) session.getAttribute("systemcontextname");

        String datagroupname = request.getParameter("datagroupname");
        String datagroupnotes = request.getParameter("datagroupnotes");
        String datafieldname = request.getParameter("datafieldname");
        int version = 0;
        long systemContextId = 0l;
        long dataGroupId = 0l;

        if (contextname != null) {

            this.systemContext = this.jdbcTemplate
                    .queryForObject(
                            "select systemcontextid, version, name, notes, diagram from systemcontext where not deleteflag and name = '"
                                    + contextname + "'",
                            new RowMapper<SystemContext>() {
                                public SystemContext mapRow(ResultSet rs,
                                                            int rowNum) throws SQLException {
                                    SystemContext systemContexttmp = new SystemContext();
                                    systemContexttmp.setSystemContextId(rs.getLong("systemcontextid"));
                                    systemContexttmp.setName(rs.getString("name"));
                                    systemContexttmp.setNotes(rs.getString("notes"));
                                    return systemContexttmp;
                                }
                            });

            if (this.systemContext != null
                    && this.systemContext.getSystemContextId() != 0l) {
                systemContextId = this.systemContext.getSystemContextId();
            }

            if (systemContextId != 0l) {
                if (request.getParameter("option") != null
                        && request.getParameter("option").equals("1")) {

                    String name = (String) session
                            .getAttribute("systemcontextname");

                    List<DataGroup> dataGroupList = this.jdbcTemplate
                            .query("select datagroupid, version, name, notes from datagroup where not deleteflag and systemcontextid = ( select systemcontextid from systemcontext where name = '"
                                    + name
                                    + "') and name = '"
                                    + datagroupname
                                    + "'", new RowMapper<DataGroup>() {
                                public DataGroup mapRow(ResultSet rs, int rowNum)
                                        throws SQLException {
                                    DataGroup actor = new DataGroup();
                                    actor.setDataGroupId(rs.getLong("datagroupid"));
                                    actor.setName(rs.getString("name"));
                                    actor.setNotes(rs.getString("notes"));
                                    return actor;
                                }
                            });

                    if (dataGroupList.size() > 0) {
                        err = "Data group already exist";
                        model.addAttribute("err", err);
                        return getDataGroup(model, request, session);
                    }

                    this.jdbcTemplate
                            .update(" insert into datagroup ( version, systemcontextid, name, notes, userid ) values ( "
                                    + version
                                    + ","
                                    + systemContextId
                                    + ",'"
                                    + datagroupname
                                    + "','"
                                    + datagroupnotes
                                    + "','"
                                    + session.getAttribute("username")
                                    + "" + "')");

                    session.setAttribute("datagroupname", datagroupname);

                    this.dataGroup = this.jdbcTemplate
                            .queryForObject(
                                    "select datagroupid, version, name, notes from datagroup where not deleteflag and systemcontextid = ( select systemcontextid from systemcontext where name = '"
                                            + name
                                            + "') and name = '"
                                            + datagroupname + "'",
                                    new RowMapper<DataGroup>() {
                                        public DataGroup mapRow(ResultSet rs,
                                                                int rowNum) throws SQLException {
                                            DataGroup actor = new DataGroup();
                                            actor.setDataGroupId(rs
                                                    .getLong("datagroupid"));
                                            actor.setName(rs.getString("name"));
                                            actor.setNotes(rs
                                                    .getString("notes"));
                                            return actor;
                                        }
                                    });

                    if (this.dataGroup != null
                            && this.dataGroup.getDataGroupId() != 0l) {
                        dataGroupId = this.dataGroup.getDataGroupId();
                    }

                    if (dataGroupId != 0l && datafieldname != null
                            && !datafieldname.isEmpty()) {

                        this.jdbcTemplate
                                .update(" update datafield set version = version + 1 where datagroupid = "
                                                + dataGroupId
                                );

                        this.jdbcTemplate
                                .update(" insert into datafield ( version, datagroupid, name, userid ) values ( "
                                        + version
                                        + ","
                                        + dataGroupId
                                        + ",'"
                                        + datafieldname
                                        + "','"
                                        + session.getAttribute("username")
                                        + ""
                                        + "')");

                    }

                } else if (request.getParameter("option") != null
                        && request.getParameter("option").equals("0")) {

                    this.jdbcTemplate
                            .update(" update datafield set deleteflag = true where datagroupid in (select datagroupid from datagroup where systemcontextid = "
                                    + systemContextId
                                    + " and name = '"
                                    + datagroupname + "')");

                    this.jdbcTemplate
                            .update(" update datagroup set deleteflag = true where name = '"
                                    + datagroupname
                                    + "' and systemcontextid = "
                                    + systemContextId);
				/*	this.jdbcTemplate
							.update(" delete from datafield where datagroupid in (select datagroupid from datagroup where systemcontextid = "
									+ systemContextId
									+ " and name = '"
									+ datagroupname + "')");

					this.jdbcTemplate
							.update(" delete from datagroup where name = '"
									+ datagroupname
									+ "' and systemcontextid = "
									+ systemContextId); */

                    return dispDataGroup(model, request, session);
                } else if (request.getParameter("option") != null
                        && request.getParameter("option").equals("2")) {
                    return "define-data-groups";

                } else if (request.getParameter("option") != null
                        && request.getParameter("option").equals("3")) {

                    // request.getParameter("datagroupname"));
                    // datagroupname = request.getParameter("datagroupname");
                    return "define-data-groups";
                } else if (request.getParameter("option") != null
                        && request.getParameter("option").equals("4")) {

                    String name = (String) session
                            .getAttribute("systemcontextname");

                    this.dataGroup = this.jdbcTemplate
                            .queryForObject(
                                    "select datagroupid, version, name, notes from datagroup where not deleteflag and systemcontextid = ( select systemcontextid from systemcontext where name = '"
                                            + name
                                            + "') and name = '"
                                            + datagroupname + "'",
                                    new RowMapper<DataGroup>() {
                                        public DataGroup mapRow(ResultSet rs,
                                                                int rowNum) throws SQLException {
                                            DataGroup actor = new DataGroup();
                                            actor.setDataGroupId(rs
                                                    .getLong("datagroupid"));
                                            actor.setName(rs.getString("name"));
                                            actor.setNotes(rs
                                                    .getString("notes"));
                                            return actor;
                                        }
                                    });

                    if (this.dataGroup != null
                            && this.dataGroup.getDataGroupId() != 0l) {
                        dataGroupId = this.dataGroup.getDataGroupId();
                    }

                    if (dataGroupId != 0l && datafieldname != null
                            && !datafieldname.isEmpty()) {


                        this.jdbcTemplate
                                .update(" update datafield set version = version + 1 where datagroupid = "
                                                + dataGroupId
                                );

                        this.jdbcTemplate
                                .update(" insert into datafield ( version, datagroupid, name, userid ) values ( "
                                        + version
                                        + ","
                                        + dataGroupId
                                        + ",'"
                                        + datafieldname
                                        + "','"
                                        + session.getAttribute("username")
                                        + ""
                                        + "')");

                    }
                } else if (request.getParameter("option") != null
                        && request.getParameter("option").equals("5")) {

                    long dataFieldId = 0l;

                    if (request.getParameter("delete") != null) {
                        dataFieldId = Long.parseLong(request
                                .getParameter("delete"));
                    }

				/*	this.jdbcTemplate
							.update(" delete from datafield where datafieldid = " + dataFieldId);   */
                    this.jdbcTemplate
                            .update(" update datafield set deleteflag = true where datafieldid = " + dataFieldId);

                }

            }

        }

        this.dataGroup = null;

        if (datagroupname == null
                || datagroupname.equals("")
                || datagroupnotes == null
                || datagroupnotes.equals("")
                || (request.getParameter("option") != null && request
                .getParameter("option").equals("0"))) {
            return "define-data-groups";
        } else {
            return getDataGroup(model, request, session);
        }

    }

    @RequestMapping(value = "/show-data-groups", method = {RequestMethod.GET,
            RequestMethod.POST})
    public String getDataGroup(Model model, HttpServletRequest request,
                               HttpSession session) {

        if (session.getAttribute("systemcontextname") != null) {
            String name = (String) session.getAttribute("systemcontextname");
            String datagroupname = request.getParameter("datagroupname");
            List<DataGroup> actors = this.jdbcTemplate
                    .query("select datagroupid, version, name, notes from datagroup where not deleteflag and systemcontextid = ( select systemcontextid from systemcontext where name = '"
                            + name + "')", new RowMapper<DataGroup>() {
                        public DataGroup mapRow(ResultSet rs, int rowNum)
                                throws SQLException {
                            DataGroup actor = new DataGroup();
                            actor.setName(rs.getString("name"));
                            actor.setNotes(rs.getString("notes"));
                            return actor;
                        }
                    });

            model.addAttribute("datagrouplist", actors);

            if (datagroupname != null && !datagroupname.equals("")) {

                this.dataGroup = this.jdbcTemplate
                        .queryForObject(
                                "select datagroupid, version, name, notes from datagroup where not deleteflag and systemcontextid = ( select systemcontextid from systemcontext where name = '"
                                        + name
                                        + "') and name = '"
                                        + datagroupname + "'",
                                new RowMapper<DataGroup>() {
                                    public DataGroup mapRow(ResultSet rs,
                                                            int rowNum) throws SQLException {
                                        DataGroup actor = new DataGroup();
                                        actor.setDataGroupId(rs
                                                .getLong("datagroupid"));
                                        actor.setName(rs.getString("name"));
                                        actor.setNotes(rs.getString("notes"));
                                        return actor;
                                    }
                                });
            }

            model.addAttribute("datagroup", this.dataGroup);


            List<DataField> datafieldlist = this.jdbcTemplate
                    .query("select datafieldid, datagroupid, version, name from datafield where not deleteflag and datagroupid = "
                                    + this.dataGroup.getDataGroupId(),
                            new RowMapper<DataField>() {
                                public DataField mapRow(ResultSet rs, int rowNum)
                                        throws SQLException {
                                    DataField datafield = new DataField();
                                    datafield.setDataFieldId(rs
                                            .getLong("datafieldid"));
                                    datafield.setDataGroupId(rs
                                            .getLong("datagroupid"));
                                    datafield.setVersion(rs.getInt("version"));
                                    datafield.setName(rs.getString("name"));
                                    return datafield;
                                }
                            });

            model.addAttribute("datafieldlist", datafieldlist);

        }

        return "define-data-groups";
    }
}