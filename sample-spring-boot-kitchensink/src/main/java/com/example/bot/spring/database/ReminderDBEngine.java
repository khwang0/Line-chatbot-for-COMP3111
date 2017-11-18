package com.example.bot.spring.database;

import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

public class ReminderDBEngine extends DBEngine {
	private Connection connection;
	
	public ReminderDBEngine() {
		connection = null;
	}
	
	public void openConnection() {
		try {
			connection = this.getConnection();
		} catch (URISyntaxException | SQLException e) {
			e.printStackTrace();
		}
	}

	public void close() {
		try {
			connection.close();
			connection = null;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private ResultSet query(PreparedStatement nstmt) {
		ResultSet rs = null;
		try {
			rs = nstmt.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
	}
	
	public Set<String> ReminderChecker() {
		openConnection();
		PreparedStatement stmt;
		Set<String> idSet=new Set<String>();
		try {
			stmt = connection.prepareStatement(
					"select line_user_info.userid from line_user_info join customer_info on "
					+ "customer_info.customername=line_user_info.name "
					+ "where customer_info.tourfee>customer_info.paidamount");
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				idSet.add(rs.getString(1));
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		close();
		return idSet;
	}

}
