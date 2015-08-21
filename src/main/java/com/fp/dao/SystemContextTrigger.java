package com.fp.dao;

import org.h2.api.Trigger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SystemContextTrigger implements Trigger {

    @Override
    public void init(Connection conn, String schemaName, String triggerName, String tableName, boolean before, int type) throws SQLException {
    }

    @Override
    public void fire(Connection conn, Object[] oldRow, Object[] newRow) throws SQLException {

        //this is a really shit way of doing this. Real databases are much smarter.
        //and I don't need to guess what each parameter is based on an array order.

        Long systemContextId = (Long) newRow[1]; //

        if (systemContextId != 0) {

            PreparedStatement stmt = conn.prepareStatement("update systemcontext " +
                    "set version = version+1\n " +
                    "where systemcontextid = ?");
            stmt.setLong(1, systemContextId);
            stmt.execute();

        }


        System.out.println("I am triggered");

        for (int i = 0; i < newRow.length; i++) {
            Object o = newRow[i];
            if (o != null) System.out.println("o = " + o.getClass() + " val:" + o);
        }

    }

    @Override
    public void close() throws SQLException {
    }

    @Override
    public void remove() throws SQLException {
    }

}
