package com.fp.dao;


import com.fp.domain.FunctionalModel;
import com.fp.domain.FunctionalProcess;
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
public class FunctionalModelRepository {

    private JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedJdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    //todo not really sure what this thing is trying to do. Looks like it wants to get the latest across all tables,
    //todo maybe before versioning was implemented?
    public List<FunctionalModel> getListOfDistinctFunctionalModels(long systemContextId, FunctionalProcess fp) {

        System.out.println("using the distinct query");
        Map bindVariables = new HashMap();
        bindVariables.put("systemContextId", systemContextId);
        bindVariables.put("functionalProcessId", fp.getFunctionalProcessId());


        String sql = "select max(a.functionalmodelid) as functionalmodelid, max(a.functionalprocessid) as functionalprocessid, " +
                "max(c.functionalsubprocessid) as functionalsubprocessid, max(c.name) as functionalsubprocessname, " +
                "max(a.datagroupid) as datagroupid, max(b.name) as datagroupname " +
                "from functionalmodel a, datagroup b, functionalsubprocess c " +
                "where a.datagroupid = b.datagroupid " +
                "and a.functionalsubprocessid = c.functionalsubprocessid " +
                "and a.systemcontextid = :systemContextId " +
                "and a.functionalprocessid = :functionalProcessId " +
                "and not a.deleteflag " +
                "group by a.datagroupid";

        return this.namedJdbcTemplate.query(sql, bindVariables, getRowMapperFunctionalModel());
    }

    private RowMapper<FunctionalModel> getRowMapperFunctionalModel() {
        return new RowMapper<FunctionalModel>() {
            public FunctionalModel mapRow(ResultSet rs, int rowNum) throws SQLException {
                FunctionalModel actor = new FunctionalModel();
                actor.setFunctionalModelId(rs.getLong("id"));
                actor.setFunctionalProcessId(rs.getLong("functionalprocessid"));
                actor.setFunctionalSubProcessId(rs.getLong("functionalsubprocessid"));
                actor.setDataGroupName(rs.getString("datagroupname"));
                actor.setDisplaydataGroupName(rs.getString("datagroupname").replace("'", "\\'"));
                actor.setDataGroupId(rs.getLong("datagroupid"));
                return actor;
            }
        };
    }


    public List<FunctionalModel> getListOfFunctionalModels(long systemContextId, FunctionalProcess fp) {

        return this.jdbcTemplate
                .query("select a.id, a.functionalprocessid, c.functionalsubprocessid, c.name as functionalsubprocessname, a.datagroupid, b.name as datagroupname, a.grade, a.notes from functionalmodel a, datagroup b, functionalsubprocess c where a.datagroupid = b.datagroupid and a.functionalsubprocessid = c.functionalsubprocessid and a.systemcontextid = "
                                + systemContextId
                                + " and a.functionalprocessid = "
                                + fp.getFunctionalProcessId()
                                + " and not a.deleteflag order by a.datagroupid, a.functionalsubprocessid",
                        getRowMapperFunctionalModel2());
    }

    private RowMapper<FunctionalModel> getRowMapperFunctionalModel2() {
        return new RowMapper<FunctionalModel>() {
            public FunctionalModel mapRow(ResultSet rs, int rowNum) throws SQLException {
                FunctionalModel actor = new FunctionalModel();
                actor.setFunctionalModelId(rs.getLong("id"));
                actor.setFunctionalProcessId(rs.getLong("functionalprocessid"));
                actor.setFunctionalSubProcessName(rs.getString("functionalsubprocessname"));
                actor.setFunctionalSubProcessId(rs.getLong("functionalsubprocessid"));
                actor.setDataGroupName(rs.getString("datagroupname"));
                actor.setDisplaydataGroupName(rs.getString("datagroupname").replace("'", "\\'"));
                actor.setDataGroupId(rs.getLong("datagroupid"));
                actor.setGrade(rs.getString("grade"));
                actor.setNotes(rs.getString("notes").replace("'", "\\'"));
                return actor;
            }
        };
    }


    public List<FunctionalModel> getListOfFunctionalModelsForFunctionalProcess(long dataGroupId, long functionalProcessId, long systemContextId) {

        Map bindVariables = new HashMap();
        bindVariables.put("systemContextId", systemContextId);
        bindVariables.put("dataGroupId", dataGroupId);
        bindVariables.put("functionalProcessId", functionalProcessId);


        String sql = "select id " +
                "from functionalmodel " +
                "where not deleteflag " +
                "and systemcontextid = :systemContextId " +
                "and functionalprocessid = :functionalProcessId " +
                "and datagroupid = :dataGroupId ";


        List<FunctionalModel> models = this.namedJdbcTemplate.query(sql, bindVariables, new RowMapper<FunctionalModel>() {
            public FunctionalModel mapRow(ResultSet rs, int rowNum) throws SQLException {
                FunctionalModel actor = new FunctionalModel();
                actor.setFunctionalModelId(rs.getLong("id"));
                return actor;
            }
        });

        return models;
    }


    public void insertDataGroupIntoFunctionalModel(long dataGroupId, long functionalProcessId, long systemContextId, Object username) {

        Map bindVariables = new HashMap();
        bindVariables.put("systemContextId", systemContextId);
        bindVariables.put("dataGroupId", dataGroupId);
        bindVariables.put("functionalProcessId", functionalProcessId);
        bindVariables.put("dataGroupId", dataGroupId);
        bindVariables.put("username", username);


        String sql = " insert into functionalmodel ( systemcontextid, functionalprocessid, datagroupid, functionalsubprocessid,  userid ) " +
                "select :systemContextId, :functionalProcessId, :dataGroupId, sp.id, :username " +
                "from functionalsubprocess sp where sp.version = 0 and sp.functionalprocessid = :functionalProcessId";

        this.namedJdbcTemplate.update(sql, bindVariables);
    }

    public List<FunctionalModel> getListOfFunctionalModels(long systemContextId, long fpId) {

        Map bindVariables = new HashMap();
        bindVariables.put("systemContextId", systemContextId);
        bindVariables.put("fpId", fpId);


        String sql = "select fm.id, fm.functionalprocessid, fsp.name as functionalsubprocessname, " +
                "fm.datagroupid, fm.grade, fm.notes , dg.name as datagroupname, fm.functionalsubprocessid " +
                "from functionalmodel fm,  datagroup dg, functionalsubprocess fsp " +
                "where fm.systemcontextid = :systemContextId " +
                "and fm.functionalprocessid = :fpId " +
                "and not fm.deleteflag  " +
                "and fm.datagroupid = dg.datagroupid " +
                "and dg.version=0 " +
                "and fm.functionalsubprocessid = fsp.id  " +
                "and fsp.version = 0 " +
                "order by fm.datagroupid, fm.functionalsubprocessid";


        return this.namedJdbcTemplate.query(sql, bindVariables, getRowMapperFunctionalModel3());
    }

    public RowMapper<FunctionalModel> getRowMapperFunctionalModel3() {
        return new RowMapper<FunctionalModel>() {
            public FunctionalModel mapRow(ResultSet rs, int rowNum) throws SQLException {
                FunctionalModel actor = new FunctionalModel();
                actor.setFunctionalModelId(rs.getLong("id"));
                actor.setFunctionalProcessId(rs.getLong("functionalprocessid"));
                actor.setFunctionalSubProcessName(rs.getString("functionalsubprocessname"));
                actor.setFunctionalSubProcessId(rs.getLong("functionalsubprocessid"));
                actor.setDataGroupName(rs.getString("datagroupname"));
                actor.setDisplaydataGroupName(rs.getString("datagroupname").replace("'", "\\'"));
                actor.setDataGroupId(rs.getLong("datagroupid"));
                actor.setGrade(rs.getString("grade"));
                actor.setNotes(rs.getString("notes"));
                return actor;
            }
        };
    }

}