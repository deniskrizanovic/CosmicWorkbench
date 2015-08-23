package com.fp.dao;

import org.h2.api.Trigger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DataGroupTrigger implements Trigger {

    @Override
    public void init(Connection conn, String schemaName, String triggerName, String tableName, boolean before, int type) throws SQLException {
    }

    @Override
    public void fire(Connection conn, Object[] oldRow, Object[] newRow) throws SQLException {

        //this is a really shit way of doing this. Real databases are much smarter.
        //and I don't need to guess what each parameter is based on an array order.

        Long dataGroupId = (Long) newRow[1]; //

        if (dataGroupId != 0) {

            PreparedStatement stmt = conn.prepareStatement("update datagroup " +
                    "set version = version+1  " +
                    "where dataGroupId = ?");
            stmt.setLong(1, dataGroupId);
            stmt.execute();

        }


    }

    @Override
    public void close() throws SQLException {
    }

    @Override
    public void remove() throws SQLException {
    }

}
