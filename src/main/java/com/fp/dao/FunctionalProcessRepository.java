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
import java.util.List;

@org.springframework.stereotype.Repository
public class FunctionalProcessRepository {

    private JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedJdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }


    public List<FunctionalProcess> getListOfFunctionalProcessesForContext(Long name) {
        return this.jdbcTemplate
                .query("select functionalprocessid, version, name, notes from functionalprocess where systemcontextid = "
                                + name
                                + " and not deleteflag order by functionalprocessid",
                        getFunctionalProcessRowMapper());
    }

    public RowMapper<FunctionalProcess> getFunctionalProcessRowMapper() {
        return new RowMapper<FunctionalProcess>() {
            public FunctionalProcess mapRow(ResultSet rs,
                                            int rowNum) throws SQLException {
                FunctionalProcess actor = new FunctionalProcess();
                actor.setFunctionalProcessId(rs.getLong("functionalprocessid"));
                actor.setName(rs.getString("name"));
                actor.setNotes(rs.getString("notes"));
                return actor;
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

    public FunctionalProcess createNewFunctionalProcess(Long systemContextId, String functionalprocessname, String functionalprocessnotes, int version, Object username) {


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
                        + username
                        + "" + "')");


        return this.jdbcTemplate
                .queryForObject(
                        "select functionalprocessid, version, name, notes from functionalprocess where not deleteflag and systemcontextid = "
                                + systemContextId
                                + " and name = '"
                                + functionalprocessname.replace(
                                "'", "''") + "'",
                        getFunctionalProcessRowMapper());


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
