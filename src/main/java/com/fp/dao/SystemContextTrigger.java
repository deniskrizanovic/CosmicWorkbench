package com.fp.dao;

import org.h2.api.Trigger;

import java.sql.Connection;
import java.sql.SQLException;

public class SystemContextTrigger implements Trigger {

	@Override
    public void init(Connection conn, String schemaName, String triggerName, String tableName, boolean before, int type) throws SQLException {
    }

	@Override
    public void fire(Connection conn, Object[] oldRow, Object[] newRow) throws SQLException {

        System.out.println("I am triggered");

	}

	@Override
	public void close() throws SQLException {
	}

	@Override
	public void remove() throws SQLException {
	}

}
