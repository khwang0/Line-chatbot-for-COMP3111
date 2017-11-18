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
	
	public String retrieveReply() throws Exception {
		Connection connection = null;
		PreparedStatement stmt = null;
		String reply="";

		connection=getConnection();
		stmt = connection.prepareStatement("select * from unanswered_default_reply");
		ResultSet rs=stmt.executeQuery();
		while (rs.next()) {
			reply=rs.getString(1);
		}
		
		if (stmt != null) stmt.close();
		if (connection != null) connection.close();

		return reply;
	}
	
	public String uqQuery(String userId, String text) throws Exception{
		//System.out.println("Success");
		Connection connection = null;
		PreparedStatement stmt = null;
		String reply="";

		connection = getConnection();
		//insert into the unanswered question table to store the question
		stmt = connection.prepareStatement(
				"insert into unanswered_question values( '"+userId+"', '"+text+"', false, , false)"
		);
		stmt.executeUpdate();

		if (stmt != null) stmt.close();
		if (connection != null) connection.close();


		return retrieveReply();
	}
}
