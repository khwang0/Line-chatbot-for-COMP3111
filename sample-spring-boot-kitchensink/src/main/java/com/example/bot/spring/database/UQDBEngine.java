package com.example.bot.spring.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.linecorp.bot.model.event.message.TextMessageContent;

public class UQDBEngine extends DBEngine {

	public UQDBEngine() {
		// TODO Auto-generated constructor stub
	}
	
	public String uqQuery(String userId, String text) {
		
		return "Sorry, I can't answer your question. My colleague will follow up with you.";
	}
}
