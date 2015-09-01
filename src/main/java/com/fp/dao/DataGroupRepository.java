package com.fp.dao;

import com.fp.domain.DataField;
import com.fp.domain.DataGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

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
                        + datagroupname.replace("'", "''") + "'", getRowMapper());
    }


    @Transactional
    public void createDataFields(DataGroup dataGroup, String newAttribute, String userName)
    {
        //datafields cannot operate with triggers on the tables as we need to version these
        //as a set. Triggers work on a per-row concept. This may invalidate the entire
        //design that uses triggers.


        Map boundVariables = new HashMap();
        boundVariables.put("dataGroupId", dataGroup.getDataGroupId());
        boundVariables.put("userName", userName);
        boundVariables.put("newAttribute", newAttribute.replace("'", "''"));


        String updateVersionOfExistingAttributes = "update datafield set version = version + 1 where datagroupid = :dataGroupId ";

        String insertTheExisitngAttributesForTheCurrentVersion = "insert into datafield (datafieldid, datagroupid, name, userid ) " +
                "select datafieldid, datagroupid, name, userid " +
                                                                 "from datafield " +
                                                                 "where version = 1 " +
                                                                 "and not deleteflag " +
                                                                 "and datagroupid =:dataGroupId" ;

        String insertTheNewAttribute = "insert into datafield ( datafieldid, datagroupid, name, userid ) " +
                "values (seq_DataField.nextval, :dataGroupId, :newAttribute, :userName)";



        namedJdbcTemplate.update(updateVersionOfExistingAttributes, boundVariables);
        namedJdbcTemplate.update(insertTheExisitngAttributesForTheCurrentVersion, boundVariables);
        namedJdbcTemplate.update(insertTheNewAttribute, boundVariables);
    }


    public List<DataField> getDataFieldsForADataGroup(long dgId) {

        Map boundVariables = new HashMap();
          boundVariables.put("dgId", dgId);

        String sql = "select datafieldid, datagroupid, version, name " +
                "from datafield " +
                "where not deleteflag " +
                "and version = 0 " +
                "and datagroupid = :dgId";


        return this.namedJdbcTemplate.query(sql, boundVariables, new RowMapper<DataField>() {
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


}
