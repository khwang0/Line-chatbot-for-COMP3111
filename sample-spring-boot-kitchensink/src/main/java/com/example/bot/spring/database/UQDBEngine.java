package com.example.bot.spring.database;

import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class UQDBEngine extends DBEngine {

	public UQDBEngine() {
		// TODO Auto-generated constructor stub
	}
	
	public String retrieveReply(){
		Connection connection = null;
		PreparedStatement stmt = null;
		String reply="";
		try {
			connection=getConnection();
			stmt = connection.prepareStatement("select * from unanswered_default_reply");
			ResultSet rs=stmt.executeQuery();
			while (rs.next()) {
				reply=rs.getString(1);
			}
		}catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				if (stmt != null) stmt.close();
				if (connection != null) connection.close();
			} catch (Exception e2) {
				System.err.println(e2.getMessage());
			}
		}
		if (reply.equals("")) {
			return "Database fatal error";
		}
		else {
			return reply;
		}
	}
	
	public String uqQuery(String userId, String text) {
		//System.out.println("Success");
		Connection connection = null;
		PreparedStatement stmt = null;
		String reply="";
		try {
			connection = getConnection();
			//insert into the unanswered question table to store the question
			stmt = connection.prepareStatement(
					"insert into unanswered_question values( '"+userId+"', '"+text+"', false)"
			);
			stmt.executeUpdate();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			//
			//stmt.close();
			//connection.close();
			try {
				if (stmt != null) stmt.close();
				if (connection != null) connection.close();
			} catch (Exception e2) {
				System.err.println(e2.getMessage());
			}
		}

		return retrieveReply();
	}
}
