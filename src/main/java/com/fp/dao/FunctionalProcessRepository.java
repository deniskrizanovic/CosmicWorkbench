package com.fp.dao;

import com.fp.domain.FunctionalProcess;
import com.fp.domain.FunctionalSubProcess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@org.springframework.stereotype.Repository
public class FunctionalProcessRepository {

    private JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedJdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }


    public List<FunctionalProcess> getListOfFunctionalProcessesForContext(Long systemContextId) {


        String sql = "select functionalprocessid, version, name, notes " +
                "from functionalprocess where systemcontextid = " + systemContextId + " " +
                "and not deleteflag " +
                "and version = 0 " +
                "order by functionalprocessid";

        return this.jdbcTemplate.query(sql, getFunctionalProcessRowMapper(new FunctionalProcess()));
    }

    public RowMapper<FunctionalProcess> getFunctionalProcessRowMapper(final FunctionalProcess fp) {
        return new RowMapper<FunctionalProcess>() {
            public FunctionalProcess mapRow(ResultSet rs,
                                            int rowNum) throws SQLException {
                fp.setFunctionalProcessId(rs.getLong("functionalprocessid"));
                fp.setName(rs.getString("name"));
                fp.setNotes(rs.getString("notes"));
                return fp;
            }
        };
    }


    public List<FunctionalSubProcess> getListofSubProcesses(long functionalProcessId) {
        return this.jdbcTemplate
                .query("select functionalsubprocessid, functionalprocessid, version, name from functionalsubprocess where not deleteflag and version = 0 and functionalprocessid = "
                                + functionalProcessId,
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
    }

    public FunctionalProcess createNewFunctionalProcess(Long systemContextId, String name, String notes, String username) {


        Map bindVariables = new HashMap();
        bindVariables.put("systemContextId", systemContextId);
        bindVariables.put("name", name.replace("'", "''"));
        bindVariables.put("notes", notes.replace("'", "''"));
        bindVariables.put("username", username);


        String sql = " insert into functionalprocess (functionalprocessid, systemcontextid, name, notes, userid ) " +
                "values ( seq_FunctionalProcess.nextVal, :systemContextId, :name, :notes, :username )";

        //I could get this by the sequences current value..maybe.
        String returnFPsql = "select functionalprocessid, version, name, notes " +
                "from functionalprocess " +
                "where not deleteflag " +
                "and version = 0" +
                "and systemcontextid = :systemContextId " +
                "and name = :name";

        this.namedJdbcTemplate.update(sql, bindVariables);
        return this.namedJdbcTemplate.queryForObject(returnFPsql, bindVariables, getFunctionalProcessRowMapper(new FunctionalProcess()));


    }


    public void createSubProcessSteps(String functionalsubprocessname, int version, long functionalProcessId, Object username) {
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
                        + username
                        + ""
                        + "')");
    }

    public List<FunctionalSubProcess> getSubProcessSteps(String functionalsubprocessname, long functionalProcessId) {
        return this.jdbcTemplate
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
    }


}
