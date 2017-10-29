package com.example.bot.spring.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.linecorp.bot.model.event.message.TextMessageContent;

public class UQDBEngine extends DBEngine {

	public UQDBEngine() {
		// TODO Auto-generated constructor stub
	}
	
	public String uqQuery(String userId, TextMessageContent text) {
		Connection connection = getConnection();
		//insert into the unanswered question table to store the question
		PreparedStatement stmt = connection.prepareStatement(
				"insert into table unanswered_question values ("+userId+", '"+text+"', false);");
		try {
			stmt.executeUpdate();
		}catch(Exception e) {
			e.printStackTrace();
		}

		stmt.close();
		connection.close();
		
		return "Sorry, I can't answer your question. My colleague will follow up with you.";
	}
}
