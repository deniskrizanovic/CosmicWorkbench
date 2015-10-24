package com.fp.dao;

import com.fp.model.Process;
import com.fp.model.SizingContext;
import com.fp.model.SubProcess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Repository
public class ProcessDAO {

    private JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedJdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }


    public List<Process> getProcesses(SizingContext sc) {

        String sql = "select functionalprocessid, version, name, notes " +
                "from functionalprocess where systemcontextid = " + sc.getId() + " " +
                "and version = 0 " +
                "order by functionalprocessid";

        return this.jdbcTemplate.query(sql, getFunctionalProcessListRowMapper(sc));

    }

    public RowMapper<Process> getFunctionalProcessListRowMapper(final SizingContext sc) {
        return new RowMapper<Process>() {
            public Process mapRow(ResultSet rs, int rowNum) throws SQLException {
                Process fp = new Process();
                fp.setId(rs.getInt("functionalprocessid"));
                fp.setName(rs.getString("name"));
                fp.setNotes(rs.getString("notes"));
                fp.setRepository(sc.getRepository());
                fp.setParent(sc);
                return fp;
            }
        };
    }

    public List<SubProcess> getSteps(final Process parent) {


        String sql = "select id, functionalprocessid, version, name " +
                "from functionalsubprocess " +
                "where version = 0 " +
                "and functionalprocessid = " + parent.getId();

        return this.jdbcTemplate.query(sql, new RowMapper<SubProcess>() {
            public SubProcess mapRow(ResultSet rs, int rowNum) throws SQLException {
                SubProcess actor = new SubProcess();
                actor.setId(rs.getInt("id"));
                actor.setParent(parent);
                actor.setName(rs.getString("name"));
                return actor;
            }
        });
    }


    public Process getProcess(Process p) {


        Map bindVariables = new HashMap();
        bindVariables.put("id", p.getId());


        String returnFPsql = "select systemcontextid, functionalprocessid, version, name, notes " +
                "from functionalprocess " +
                "where version = 0" +
                "and functionalprocessid = :id";

        List listOfFPs = this.namedJdbcTemplate.query(returnFPsql, bindVariables, getProcessRowMapper(p.getParent()));

        Process fp = new Process();

        if (listOfFPs.size() > 0) {

            fp = (Process) listOfFPs.get(0);
        }
        return fp;

    }

    public RowMapper<Process> getProcessRowMapper(final SizingContext parent) {
        return new RowMapper<Process>() {
            public Process mapRow(ResultSet rs, int rowNum) throws SQLException {
                Process fp = new Process();
                fp.setId(rs.getInt("functionalprocessid"));
                fp.setName(rs.getString("name"));
                fp.setNotes(rs.getString("notes"));
                fp.setSteps(getSteps(fp));
                fp.setParent(parent);
                return fp;
            }
        };
    }

    public Process saveProcessAndSteps(Process p) {

        Process saved = saveProcess(p);


        if (!p.getSteps().isEmpty()) {
            p.setId(saved.getId());
            addSteps(p);
        }

        return getProcess(saved);

    }

    public void addSteps(Process p) {

        Map boundVariables = new HashMap();
        boundVariables.put("processId", p.getId());
        boundVariables.put("userName", p.getCreatedBy());


        String updateVersionOfExistingSteps = " update functionalsubprocess set version = version + 1 where functionalprocessid = " + p.getId();
        namedJdbcTemplate.update(updateVersionOfExistingSteps, boundVariables);


        String insertTheNewSteps = " insert into functionalsubprocess (functionalprocessid, name, createdby ) " +
                "values ( :processId, :spName, :userName)";

        for (SubProcess sp : p.getSteps()) {

            boundVariables.put("spName", sp.getName());

            namedJdbcTemplate.update(insertTheNewSteps, boundVariables);
        }


    }

    private Process saveProcess(Process p) {

        Map bindVariables = new HashMap();
        bindVariables.put("systemContextId", p.getParent().getId());
        bindVariables.put("name", p.getName().replace("'", "''"));
        bindVariables.put("notes", p.getNotes().replace("'", "''"));
        bindVariables.put("username", p.getCreatedBy());


        String sql = " insert into functionalprocess (functionalprocessid, systemcontextid, name, notes, createdBy ) " +
                "values ( :seq, :systemContextId, :name, :notes, :username )";

        int seq;

        if (p.getId() > 0) {

            String updateVersionOfExistingProcess = "update functionalprocess set version = version + 1 where functionalProcessId = " + p.getId();
            jdbcTemplate.update(updateVersionOfExistingProcess);
            seq = p.getId();

        } else {
            String getProcessNextVal = "select seq_FunctionalProcess.nextval";
            seq = jdbcTemplate.queryForObject(getProcessNextVal, Integer.class);
        }

        bindVariables.put("seq", seq);


        namedJdbcTemplate.update(sql, bindVariables);

        Process criteria = (Process) new Process().setId(seq);
        return getProcess(criteria);


    }
}
