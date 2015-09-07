package com.fp.dao;

import com.fp.model.*;
import com.fp.model.Process;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


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
                dg.setParent(sc); //todo might be able to remove the setRepo beneath
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
    public void saveDataMovements(DataGroup dg, SubProcess sp, List<String> attributeIds, String type, String userName) {

        Map boundVariables = new HashMap();
        boundVariables.put("dataGroupId", dg.getId());
        boundVariables.put("stepId", sp.getId());
        boundVariables.put("userName", userName);
        boundVariables.put("type", type);
        boundVariables.put("sizingCtxId", dg.getParent().getId());

        String updateVersionOfExistingAttributes = "update DataMovement set version = version + 1 " +
                "where datagroupid = :dataGroupId " +
                "and subprocessid = :stepId";

        namedJdbcTemplate.update(updateVersionOfExistingAttributes, boundVariables);

        for (Iterator<String> iterator = attributeIds.iterator(); iterator.hasNext(); ) {
            String attribId = iterator.next();

            boundVariables.put("attrib", attribId);

            String insertTheNewAttribute = "insert into DataMovement ( id, datagroupid, datafieldid, createdby, subprocessid, sizingContextId, type ) " +
                    "values (seq_DataField.nextval, :dataGroupId, :attrib, :userName, :stepId, :sizingCtxId, :type)";

            namedJdbcTemplate.update(insertTheNewAttribute, boundVariables);

        }


    }

    public List getMovements(final SizingContext sc) {

        Map boundVariables = new HashMap();
        boundVariables.put("sizingCtxId", sc.getId());

        String sql = "select m.id, m.SubProcessId, m.DataGroupId, m.DataFieldId, sp.functionalprocessid, m.type " +
                "from datamovement m, functionalsubprocess sp " +
                "where m.subprocessid = sp.id " +
                "and m.version = 0 " +
                "and m.SizingContextId = :sizingCtxId " +
                "order by m.subProcessId, m.DataGroupId";

        //because I need to do some grouping as I iterate over the resultset, I can't use the mappers
        SqlRowSet rs = namedJdbcTemplate.queryForRowSet(sql, boundVariables);

        int previousDataGroupId = 0;
        int previousSubProcessId = 0;
        Movement movement = new Movement();

        List<Movement> movements = new ArrayList<>();
        while (rs.next()) {

            int currentSubProcessId = rs.getInt("SubProcessId");
            int currentDataGroupId = rs.getInt("DataGroupId");
            Process parentProcess = null;
            DataGroup dataGroup = null;

            if (iteratedOverToANewMovement(previousDataGroupId, previousSubProcessId, currentSubProcessId, currentDataGroupId)) {

                parentProcess = sc.getProcess(rs.getInt("functionalprocessid"));
                dataGroup = sc.getDataGroup(rs.getInt("DataGroupId"));

                movement = new Movement();
                movement.setSubProcess(parentProcess.getStep(rs.getInt("subprocessid")));
                movement.setDataGroup(dataGroup);
                movements.add(movement);

            }

            DataAttribute attribute = getAttributeById(rs.getInt("DataFieldId"), dataGroup);

            movement.addAttribute(attribute);
            movement.setType(rs.getString("type"));

            previousSubProcessId = currentSubProcessId;
            previousDataGroupId = currentDataGroupId;


        }


        return movements;

    }

    private boolean iteratedOverToANewMovement(int previousDataGroupId, int previousSubProcessId, int currentSubProcessId, int currentDataGroupId) {
        return currentDataGroupId != previousDataGroupId || currentSubProcessId != previousSubProcessId;
    }

    private RowMapper<Movement> getRowMapper(final SizingContext sc) {


        return new RowMapper<Movement>() {
            @Override
            public Movement mapRow(ResultSet rs, int rowNum) throws SQLException {

                DataGroup dataGroup = sc.getDataGroup(rs.getInt("DataGroupId"));
                DataAttribute attrib = getAttributeById(rs.getInt("dataFieldId"), dataGroup);

                Movement m = new Movement();
                m.setId(rs.getInt("id"));
                m.setSubProcess((sc.getProcess(rs.getInt("functionalprocessid"))).getStep(rs.getInt("subprocessid")));

                m.setDataGroup(dataGroup);
                m.setType(rs.getString("type"));
                m.getAttributes();

                return m;


            }
        };
    }


    private DataAttribute getAttributeById(int id, final DataGroup dg) {
        Map boundVariables = new HashMap();
        boundVariables.put("attribId", id);

        String sql = "select datafieldid, datagroupid, name " +
                "from datafield " +
                "where not deleteflag " +
                "and version = 0 " +
                "and datafieldid = :attribId";

        return namedJdbcTemplate.queryForObject(sql, boundVariables, new RowMapper<DataAttribute>() {
            public DataAttribute mapRow(ResultSet rs, int rowNum) throws SQLException {
                DataAttribute att = new DataAttribute();
                att.setId(rs.getInt("datafieldid"));
                att.setParent(dg);
                att.setName(rs.getString("name"));
                return att;

            }
        });
    }
}
