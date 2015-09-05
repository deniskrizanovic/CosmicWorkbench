package com.fp.dao;

import com.fp.model.DataAttribute;
import com.fp.model.DataGroup;
import com.fp.model.SizingContext;
import com.fp.model.SubProcess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


@org.springframework.stereotype.Repository
public class DataGroupDAO {


    private JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedJdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public List<DataGroup> getDataGroups(SizingContext sc) {
        String sql = "select datagroupid, version, name, notes " +
                "from datagroup " +
                "where not deleteflag " +
                "and version = 0 " +
                "and systemcontextid = " + sc.getId();
        return this.jdbcTemplate.query(sql, getRowToModelMapper(sc));
    }


    public RowMapper<DataGroup> getRowToModelMapper(final SizingContext sc) {
        return new RowMapper<DataGroup>() {
            public DataGroup mapRow(ResultSet rs, int rowNum) throws SQLException {

                DataGroup dg = new DataGroup();
                dg.setId(rs.getInt("datagroupid"));
                dg.setName(rs.getString("name"));
                dg.setNotes(rs.getString("notes"));
                dg.setRepository(sc.getRepository());  //todo, I know this is a bit dodgy.. but because I'm using "new", Spring don't work.
                return dg;
            }
        };
    }

    public List<DataAttribute> getAttributes(final DataGroup dg) {

        Map boundVariables = new HashMap();
        boundVariables.put("dgId", dg.getId());

        String sql = "select datafieldid, datagroupid, version, name " +
                "from datafield " +
                "where not deleteflag " +
                "and version = 0 " +
                "and datagroupid = :dgId";


        return this.namedJdbcTemplate.query(sql, boundVariables, new RowMapper<DataAttribute>() {
            public DataAttribute mapRow(ResultSet rs, int rowNum) throws SQLException {
                DataAttribute att = new DataAttribute();
                att.setId(rs.getInt("datafieldid"));
                att.setParent(dg);
                att.setVersion(rs.getInt("version"));
                att.setName(rs.getString("name"));
                return att;
            }
        });
    }

    @Transactional
    public void saveDataMovements(DataGroup dg, SubProcess sp, List<String> attributeIds, String userName) {

        Map boundVariables = new HashMap();
        boundVariables.put("dataGroupId", dg.getId());
        boundVariables.put("stepId", sp.getId());
        boundVariables.put("userName", userName);

        String updateVersionOfExistingAttributes = "update DataMovement set version = version + 1 " +
                "where datagroupid = :dataGroupId " +
                "and subprocessid = :stepId";

        namedJdbcTemplate.update(updateVersionOfExistingAttributes, boundVariables);

        for (Iterator<String> iterator = attributeIds.iterator(); iterator.hasNext(); ) {
            String attribId = iterator.next();

            boundVariables.put("attrib", attribId);

            String insertTheNewAttribute = "insert into DataMovement ( id, datagroupid, datafieldid, createdby, subprocessid ) " +
                    "values (seq_DataField.nextval, :dataGroupId, :attrib, :userName, :stepId)";

            namedJdbcTemplate.update(insertTheNewAttribute, boundVariables);

        }




    }
}
