package com.example.bot.spring.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;


public class UQDBEngine extends DBEngine {

	public UQDBEngine() {
		// TODO Auto-generated constructor stub
	}
	
	public void updateTable() throws Exception{
		Connection connection = null;
		PreparedStatement stmt = null;

		connection=getConnection();
		stmt = connection.prepareStatement(
				"update unanswered_question set sent_or_not = true where answered_or_not = true"
		);
		stmt.executeUpdate();
		
		if (stmt != null) stmt.close();
		if (connection != null) connection.close();
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
	
	public ArrayList<String> answer() throws Exception{
		Connection connection = null;
		PreparedStatement stmt = null;
		ArrayList<String> reply= new ArrayList<String>();

		connection=getConnection();
		stmt = connection.prepareStatement(
				"select * from unanswered_question where answered_or_not = true and sent_or_not= false"
		);
		ResultSet rs=stmt.executeQuery();
		while (rs.next()) {
			String temp="";
			temp+=rs.getString(1);
			temp+=",";
			temp+=rs.getString(2);
			temp+=",";
			temp+=rs.getString(4);
			reply.add(temp);
		}
		
		if (stmt != null) stmt.close();
		if (connection != null) connection.close();
		
		updateTable();

		return reply;
	}
	
	public String uqQuery(String userId, String text) throws Exception{
		//System.out.println("Success");
		Connection connection = null;
		PreparedStatement stmt = null;

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
