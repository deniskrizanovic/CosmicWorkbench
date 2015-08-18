package com.fp.dao;

import org.h2.api.Trigger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DataFieldTrigger implements Trigger {

	@Override
	public void init(Connection conn, String schemaName, String triggerName,
			String tableName, boolean before, int type) throws SQLException {
	}

	@Override
	public void fire(Connection conn, Object[] oldRow, Object[] newRow)
			throws SQLException {

		int datagroupid = 0;

		PreparedStatement prep = conn
				.prepareStatement("update datafield set version = version + 1 where datagroupid = ?");

		prep.setInt(1, datagroupid);
		prep.execute();

	}

	@Override
	public void close() throws SQLException {
	}

	@Override
	public void remove() throws SQLException {
	}

}
