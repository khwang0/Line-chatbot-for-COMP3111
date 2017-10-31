package com.example.bot.spring.database;

import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class UQDBEngine extends DBEngine {

	public UQDBEngine() {
		// TODO Auto-generated constructor stub
	}
	
	public String uqQuery(String userId, String text) {
		System.out.println("Success");
		Connection connection = null;
		PreparedStatement stmt = null;
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

		return "Sorry, I can't answer your question. My colleague will follow up with you.";
	}
}
