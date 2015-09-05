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
import java.util.List;


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
                "and not deleteflag " +
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
                return fp;
            }
        };
    }

    public List<SubProcess> getSteps(final Process parent) {


        String sql = "select id, functionalprocessid, version, name " +
                "from functionalsubprocess " +
                "where not deleteflag " +
                "and version = 0 " +
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


}
