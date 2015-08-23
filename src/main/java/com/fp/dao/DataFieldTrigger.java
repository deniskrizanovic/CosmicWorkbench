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
	public void fire(Connection conn, Object[] oldRow, Object[] newRow)throws SQLException {

		Long dataFieldId = (Long) newRow[1]; //

		if (dataFieldId != 0)
		{

			PreparedStatement stmt = conn.prepareStatement("update datafield " +
					"set version = version+1  " +
					"where dataFieldId = ?");
			stmt.setLong(1, dataFieldId);
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
