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
public class Repository
{

    private JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedJdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource)
    {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }


    public List<SystemContext> getSystemContexts()
    {
        String sql = "select systemcontextid, version, name, notes, diagram from systemcontext where not deleteflag and version = 0";
        return this.namedJdbcTemplate.query(sql,
                new RowMapper<SystemContext>()
                {
                    public SystemContext mapRow(ResultSet rs, int rowNum)
                            throws SQLException
                    {
                        SystemContext sc = new SystemContext();
                        sc.setName(rs.getString("name"));
                        sc.setNotes(rs.getString("notes"));
                        sc.setSystemContextId(rs.getLong("systemcontextid"));
                        return sc;
                    }
                });
    }

    public SystemContext getSystemContextDetailsById(String id)
    {
        return this.jdbcTemplate
                .queryForObject(
                        "select systemcontextid, version, name, notes, diagram from systemcontext where not deleteflag and systemcontextid = "
                                + id + "", new RowMapper<SystemContext>()
                        {
                            public SystemContext mapRow(ResultSet rs, int rowNum)
                                    throws SQLException
                            {
                                SystemContext actor = new SystemContext();
                                actor.setSystemContextId(rs.getLong("systemcontextid"));
                                actor.setName(rs.getString("name"));
                                actor.setNotes(rs.getString("notes"));
                                return actor;
                            }
                        });
    }


    public SystemContext getSystemContextByName(String contextname)
    {

        final SystemContext context = new SystemContext();

        this.jdbcTemplate
                .query(
                        "select systemcontextid, version, name, notes, diagram from systemcontext where not deleteflag and name = '"
                                + contextname.replace("'", "''")
                                + "'",
                        new RowMapper<SystemContext>()
                        {
                            public SystemContext mapRow(ResultSet rs, int rowNum) throws SQLException
                            {
                                context.setSystemContextId(rs.getLong("systemcontextid"));
                                context.setName(rs.getString("name"));
                                context.setNotes(rs.getString("notes"));
                                return context;
                            }
                        });

        return context;
    }


    public void insertNewSystemContext(String systemContextId, String username, String contextname, String notes, MultipartFile file) throws IOException
    {


        String sql = " insert into systemcontext (systemContextId, name, notes, userid )" +
                " values ( seq_SystemContext.nextval, :contextName, :notes, :username)";

        String sqlAllOthertimes = " insert into systemcontext (systemContextId, name, notes, userid )" +
                " values ( :seq, :contextName, :notes, :username)";

        Map namedParameters = new HashMap();
        namedParameters.put("contextName", contextname.replace("'", "''")); //todo might not need these, as spring might do it.
        namedParameters.put("notes", notes.replace("'", "''"));
        namedParameters.put("username", username);

        if (!systemContextId.equals("0"))
        {

            System.out.println("I'm doing it a second time");
            sql = sqlAllOthertimes;
            namedParameters.put("seq", systemContextId);
        }


        this.namedJdbcTemplate.update(sql, namedParameters);


        SystemContext systemContext = getSystemContextByName(contextname); //todo the question is, why isn't a SystemContext object just passed in, and then persisted?

        if (systemContext != null)
        { //todo not sure why this if statement is useful here?

            if (thereIsAFileToUpload(file))
            {

                File workingFile = null;
                InputStream imageIs = null;
                LobHandler lobHandler = new DefaultLobHandler();

                if (thereIsAFileToUpload(file))
                {

                    workingFile = new File(file.getOriginalFilename());
                    file.transferTo(new File(System.getProperty("java.io.tmpdir") + "/" + workingFile));
                    imageIs = new FileInputStream(System.getProperty("java.io.tmpdir") + "/" + workingFile);
                }

                this.jdbcTemplate.update(
                        " update systemcontext set diagram = ? where systemcontextid = "
                                + systemContext.getSystemContextId(),
                        new Object[]{new SqlLobValue(imageIs, (int) workingFile.length(), lobHandler)},
                        new int[]{Types.BLOB});

            }
        }
    }


    public boolean thereIsAFileToUpload(MultipartFile file)
    {
        return file != null && !file.isEmpty();
    }


    public void updateSystemContext(String contextname)
    {
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


    public void deleteSystemContext(String contextname)
    {

        if (contextname != null)
        {
            this.jdbcTemplate
                    .update(" update systemcontext set deleteflag = true where name = '"
                            + contextname + "'");

        }
    }
}
