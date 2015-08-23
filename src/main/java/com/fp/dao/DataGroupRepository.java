package com.fp.dao;

import com.fp.domain.DataGroup;
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
public class DataGroupRepository
{

    private JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedJdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource)
    {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public DataGroup createDataGroup(String systemContextId, Long dataGroupId, String datagroupName, String dataGroupNotes, String userName)
    {
        String sql = " insert into datagroup ( datagroupid, systemcontextid, name, notes, userid ) " +
                "values ( %s, :systemContextId, :datagroupName, :dataGroupNotes, :userName)";

        if(dataGroupId > 0)
        {
            sql = String.format(sql, dataGroupId);
        }
        else
        {
            sql = String.format(sql, "seq_DataGroup.nextval");
        }

        System.out.println("sql = " + sql);

        Map namedParameters = new HashMap();
        namedParameters.put("systemContextId", systemContextId);
        namedParameters.put("datagroupName", datagroupName.replace("'", "''"));
        namedParameters.put("dataGroupNotes", dataGroupNotes.replace("'", "''"));
        namedParameters.put("userName", userName);


        namedJdbcTemplate.update(sql, namedParameters);


        return getDataGroupByName(Long.valueOf(systemContextId), datagroupName);


    }

    public DataGroup getDataGroupById(Long systemcontextid, String dataGroupId)
    {
        return jdbcTemplate
                .queryForObject(
                        "select datagroupid, version, name, notes from datagroup where not deleteflag and version = 0 and systemcontextid = "
                                + systemcontextid
                                + " and datagroupid = "
                                + Long.parseLong(dataGroupId),
                        getRowMapper());
    }


    public DataGroup getDataGroupByName(Long systemContextId, String dataGroupName)
    {
        String sql = "select datagroupid, version, name, notes " +
                     "from datagroup " +
                     "where not deleteflag " +
                     "and version = 0 " +
                     "and systemcontextid = :systemContextId " +
                     "and name = :dataGroupName";


        Map boundVariables = new HashMap();
        boundVariables.put("systemContextId", systemContextId);
        boundVariables.put("dataGroupName", dataGroupName.replace("'", "''"));

        return namedJdbcTemplate.queryForObject(sql, boundVariables, getRowMapper());
    }

    private RowMapper<DataGroup> getRowMapper()
    {
        return new RowMapper<DataGroup>()
        {
            public DataGroup mapRow(ResultSet rs,int rowNum) throws SQLException
            {
                DataGroup actor = new DataGroup();
                actor.setDataGroupId(rs.getLong("datagroupid"));
                actor.setName(rs.getString("name"));
                actor.setNotes(rs.getString("notes"));
                return actor;
            }
        };
    }


    public List<DataGroup> getDataGroupsForSystemContext(Long systemcontextid)
    {
        return this.jdbcTemplate
                .query("select datagroupid, version, name, notes from datagroup where not deleteflag and version = 0 and systemcontextid = "
                        + systemcontextid + "", getRowMapper());
    }


    private List<DataGroup> getListOfDataGroups(String systemContextId, String datagroupname)
    {
        System.out.println("getListOfDataGroups.systemContextId = " + systemContextId);
        return this.jdbcTemplate
                .query("select datagroupid, version, name, notes from datagroup where not deleteflag and version = 0 and systemcontextid = "
                                + systemContextId
                                + " and name = '"
                                + datagroupname.replace("'", "''") + "'",
                        new RowMapper<DataGroup>()
                        {
                            public DataGroup mapRow(ResultSet rs, int rowNum) throws SQLException
                            {
                                DataGroup actor = new DataGroup();
                                actor.setDataGroupId(rs.getLong("datagroupid"));
                                actor.setName(rs.getString("name"));
                                actor.setNotes(rs.getString("notes"));
                                return actor;
                            }
                        });
    }
}
