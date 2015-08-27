package com.fp.dao;

import com.fp.domain.SystemContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.support.SqlLobValue;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@org.springframework.stereotype.Repository
public class SystemContextRepository {

    private JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedJdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }


    public List<SystemContext> getSystemContexts() {
        String sql = "select systemcontextid, name, notes from systemcontext where not deleteflag and version = 0";
        return this.namedJdbcTemplate.query(sql, getRowMapper(new SystemContext()));
    }

    public SystemContext getSystemContextDetailsById(String id) {

        final SystemContext context = new SystemContext();

        String sql = "select systemcontextid,name, notes " +
                "from systemcontext " +
                "where not deleteflag " +
                "and version = 0 " +
                "and systemcontextid = " + id + "";

        this.jdbcTemplate.query(sql, getRowMapper(context));

        return context;
    }

    public RowMapper<SystemContext> getRowMapper(final SystemContext sc) {
        return new RowMapper<SystemContext>() {
            public SystemContext mapRow(ResultSet rs, int rowNum) throws SQLException {
                sc.setSystemContextId(rs.getLong("systemcontextid"));
                sc.setName(rs.getString("name"));
                sc.setNotes(rs.getString("notes"));
                return sc;
            }
        };
    }


    public SystemContext getSystemContextByName(String contextname) {

        final SystemContext context = new SystemContext();

        String sql = "select systemcontextid, name, notes " +
                     "from systemcontext " +
                     "where not deleteflag " +
                     "and version = 0 " +
                     "and name = '" + contextname.replace("'", "''") + "'";


        this.jdbcTemplate.query(sql, getRowMapper(context));

        return context;
    }

    //todo the question is, why isn't a SystemContext object just passed in, and then persisted?
    public void insertNewSystemContext(String systemContextId, String username, String contextname, String notes, MultipartFile uploadedFile) throws IOException {


        String sql = " insert into systemcontext (systemContextId, name, notes, userid )" +
                     " values ( seq_SystemContext.nextval, :contextName, :notes, :username)";

        String sqlAllOthertimes = " insert into systemcontext (systemContextId, name, notes, userid )" +
                                  " values ( :seq, :contextName, :notes, :username)";

        Map namedParameters = new HashMap();
        namedParameters.put("contextName", contextname.replace("'", "''")); //todo might not need these, as spring might do it.
        namedParameters.put("notes", notes.replace("'", "''"));
        namedParameters.put("username", username);

        if (creatingANewContext(systemContextId)) {
            sql = sqlAllOthertimes;
            namedParameters.put("seq", systemContextId);
        }


        this.namedJdbcTemplate.update(sql, namedParameters);

        doFileUpload(contextname, uploadedFile);


    }

    private void doFileUpload(String contextname, MultipartFile uploadedFile) throws IOException {
        //this is to get back the id that was created.
        SystemContext systemContext = getSystemContextByName(contextname);

        if (systemContext != null) { //todo not sure why this if statement is useful here?

            if (thereIsANewFileToUpload(uploadedFile)) {
                //todo this is one of those times we probably need to do transactions.
                uploadFileToDatabase(uploadedFile, systemContext);

            } else //we need to copy the blob from the previous version, which is always 1
            {
                copyPreviousVersionOfFileToNewRecord(systemContext);
            }
        }
    }

    private void copyPreviousVersionOfFileToNewRecord(SystemContext systemContext) {
        Map updatedDiagramParams = new HashMap();
        updatedDiagramParams.put("systemContextId", systemContext.getSystemContextId());

        this.namedJdbcTemplate.update("update systemcontext " +
                "set diagram = (select diagram from systemcontext where systemcontextid = :systemContextId and version = 1)" +
                "where version = 0 and systemcontextid = :systemContextId", updatedDiagramParams);
    }

    private void uploadFileToDatabase(MultipartFile uploadedFile, SystemContext systemContext) throws IOException {
        System.out.println("uploadedFile = " + uploadedFile.getSize());

        File workingFile = new File(System.getProperty("java.io.tmpdir") + "/" + uploadedFile.getOriginalFilename());

        uploadedFile.transferTo(workingFile);

        InputStream imageIs = new FileInputStream(workingFile);   //todo should this be tomcat's work directory?
        LobHandler lobHandler = new DefaultLobHandler();


        this.jdbcTemplate.update(
                "update systemcontext set diagram = ? where version = 0 and systemcontextid = "
                        + systemContext.getSystemContextId(),
                new Object[]{new SqlLobValue(imageIs, (int) workingFile.length(), lobHandler)},
                new int[]{Types.BLOB});
    }

    private boolean creatingANewContext(String systemContextId) {
        return !systemContextId.equals("0");
    }


    public boolean thereIsANewFileToUpload(MultipartFile file) {
        return file != null && !file.isEmpty();
    }

    //This looks like it cascades delete flags to all tables if the system context is deleted.
    public void updateSystemContext(String contextname) {
        this.jdbcTemplate
                .update(" update functionalmodeldatafield set deleteflag = true where functionalmodelid in (select functionalmodelid from functionalmodel where systemcontextid in "
                        + " (select systemcontextid from systemcontext where name = '"
                        + contextname + "'" + "))");

        this.jdbcTemplate
                .update(" update functionalmodel set deleteflag = true where systemcontextid in (select systemcontextid from systemcontext where name = '"
                        + contextname + "')");

        this.jdbcTemplate
                .update(" update functionalsubprocess set deleteflag = true where version = 0 and functionalprocessid in (select functionalprocessid from functionalprocess where systemcontextid in "
                        + " (select systemcontextid from systemcontext where name = '"
                        + contextname + "'" + "))");

        this.jdbcTemplate
                .update(" update functionalprocess set deleteflag = true where systemcontextid in (select systemcontextid from systemcontext where name = '"
                        + contextname + "')");

        this.jdbcTemplate
                .update(" update datafield set deleteflag = true where version = 0 and datagroupid in (select datagroupid from datagroup where systemcontextid in "
                        + " (select systemcontextid from systemcontext where name = '"
                        + contextname + "'" + "))");

        this.jdbcTemplate
                .update(" update datagroup set deleteflag = true where systemcontextid in (select systemcontextid from systemcontext where name = '"
                        + contextname + "')");

        this.jdbcTemplate
                .update(" update systemcontext set deleteflag = true where name = '"
                        + contextname + "'");
    }


    public void deleteSystemContext(String contextname) {

        if (contextname != null) {
            this.jdbcTemplate
                    .update(" update systemcontext set deleteflag = true where name = '"
                            + contextname + "'");

        }
    }
}
