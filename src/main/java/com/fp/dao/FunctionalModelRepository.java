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

    public List<FunctionalModel> getListOfDistinctFunctionalModels(long systemContextId, FunctionalProcess fp) {
        return this.jdbcTemplate
                .query("select max(a.functionalmodelid) as functionalmodelid, max(a.functionalprocessid) as functionalprocessid, max(c.functionalsubprocessid) as functionalsubprocessid, max(c.name) as functionalsubprocessname, max(a.datagroupid) as datagroupid, max(b.name) as datagroupname from functionalmodel a, datagroup b, functionalsubprocess c where a.datagroupid = b.datagroupid and a.functionalsubprocessid = c.functionalsubprocessid and a.systemcontextid = "
                                + systemContextId
                                + " and a.functionalprocessid = "
                                + fp.getFunctionalProcessId()
                                + " and not a.deleteflag group by a.datagroupid",
                        new RowMapper<FunctionalModel>() {
                            public FunctionalModel mapRow(ResultSet rs, int rowNum) throws SQLException {
                                FunctionalModel actor = new FunctionalModel();
                                actor.setFunctionalModelId(rs.getLong("functionalmodelid"));
                                actor.setFunctionalProcessId(rs.getLong("functionalprocessid"));
                                actor.setFunctionalSubProcessId(rs.getLong("functionalsubprocessid"));
                                actor.setDataGroupName(rs.getString("datagroupname"));
                                actor.setDisplaydataGroupName(rs.getString("datagroupname").replace("'", "\\'"));
                                actor.setDataGroupId(rs.getLong("datagroupid"));
                                return actor;
                            }
                        });
    }


    public List<FunctionalModel> getListOfFunctionalModelsForFunctionalProcess(long dataGroupId, long functionalProcessId, long systemContextId) {

        Map bindVariables = new HashMap();
        bindVariables.put("systemContextId", systemContextId);
        bindVariables.put("dataGroupId", dataGroupId);
        bindVariables.put("functionalProcessId", functionalProcessId);


        String sql = "select functionalmodelid " +
                "from functionalmodel " +
                "where not deleteflag " +
                "and systemcontextid = :systemContextId " +
                "and functionalprocessid = :functionalProcessId " +
                "and datagroupid = :dataGroupId ";


        List<FunctionalModel> models = this.namedJdbcTemplate.query(sql, bindVariables, new RowMapper<FunctionalModel>() {
            public FunctionalModel mapRow(ResultSet rs, int rowNum) throws SQLException {
                FunctionalModel actor = new FunctionalModel();
                actor.setFunctionalModelId(rs.getLong("functionalmodelid"));
                return actor;
            }
        });

        return models;
    }
}
