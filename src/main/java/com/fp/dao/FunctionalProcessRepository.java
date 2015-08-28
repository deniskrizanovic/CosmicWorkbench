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

        return this.jdbcTemplate.query(sql, getFunctionalProcessListRowMapper());
    }

    public RowMapper<FunctionalProcess> getFunctionalProcessRowMapper(final FunctionalProcess fp) {
        return new RowMapper<FunctionalProcess>() {
            public FunctionalProcess mapRow(ResultSet rs, int rowNum) throws SQLException {
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

    public FunctionalProcess createNewFunctionalProcess(Long systemContextId, Long functionalProcessId, String name, String notes, String username) {


        Map bindVariables = new HashMap();
        bindVariables.put("systemContextId", systemContextId);
        bindVariables.put("name", name.replace("'", "''"));
        bindVariables.put("notes", notes.replace("'", "''"));
        bindVariables.put("username", username);
        bindVariables.put("functionalProcessId", functionalProcessId);


        String sql = " insert into functionalprocess (functionalprocessid, systemcontextid, name, notes, userid ) " +
                "values ( seq_FunctionalProcess.nextVal, :systemContextId, :name, :notes, :username )";


        String sqlForAnUpdate = " insert into functionalprocess (functionalprocessid, systemcontextid, name, notes, userid ) " +
                "values ( :functionalProcessId, :systemContextId, :name, :notes, :username )";


        boolean needsUpdating = functionalProcessNeedsUpdating(systemContextId, functionalProcessId, name.replace("'", "''"), notes.replace("'", "''"), username);
        if (functionalProcessId > 0 && needsUpdating) {

            sql = sqlForAnUpdate;
        }


        this.namedJdbcTemplate.update(sql, bindVariables);
        return getFunctionalProcessByName(systemContextId, name);


    }

    private boolean functionalProcessNeedsUpdating(Long systemContextId, Long functionalProcessId, String name, String notes, String username) {

        FunctionalProcess existing = getFunctionalProcessById(systemContextId, String.valueOf(functionalProcessId));
        FunctionalProcess edited = new FunctionalProcess(systemContextId, functionalProcessId, name, notes);


        return existing.equals(edited);

    }

    public FunctionalProcess getFunctionalProcessByName(Long systemContextId, String name) {

        Map bindVariables = new HashMap();
        bindVariables.put("systemContextId", systemContextId);
        bindVariables.put("name", name.replace("'", "''"));


        String returnFPsql = "select functionalprocessid, version, name, notes " +
                "from functionalprocess " +
                "where not deleteflag " +
                "and version = 0" +
                "and systemcontextid = :systemContextId " +
                "and name = :name";

        List listOfFPs = this.namedJdbcTemplate.query(returnFPsql, bindVariables, getFunctionalProcessRowMapper(new FunctionalProcess()));

        FunctionalProcess fp = new FunctionalProcess();

        if (listOfFPs.size() > 0) {

            fp = (FunctionalProcess) listOfFPs.get(0);

        } else {
            fp.setName(name);
        }

        return fp;

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


    public FunctionalProcess getFunctionalProcessById(Long systemContextId, String functionalprocessid) {

        Map bindVariables = new HashMap();
        bindVariables.put("systemContextId", systemContextId);
        bindVariables.put("id", functionalprocessid);


        String returnFPsql = "select functionalprocessid, version, name, notes " +
                "from functionalprocess " +
                "where not deleteflag " +
                "and version = 0" +
                "and systemcontextid = :systemContextId " +
                "and functionalprocessid = :id";

        List listOfFPs = this.namedJdbcTemplate.query(returnFPsql, bindVariables, getFunctionalProcessRowMapper(new FunctionalProcess()));

        FunctionalProcess fp = new FunctionalProcess();

        if (listOfFPs.size() > 0) {

            fp = (FunctionalProcess) listOfFPs.get(0);
        }
        return fp;


    }

    public RowMapper<FunctionalProcess> getFunctionalProcessListRowMapper() {
        return new RowMapper<FunctionalProcess>() {
            public FunctionalProcess mapRow(ResultSet rs, int rowNum) throws SQLException {
                FunctionalProcess fp = new FunctionalProcess();
                fp.setFunctionalProcessId(rs.getLong("functionalprocessid"));
                fp.setName(rs.getString("name"));
                fp.setNotes(rs.getString("notes"));
                return fp;
            }
        };
    }
}
