package com.example.bot.spring.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.linecorp.bot.model.event.message.TextMessageContent;

public class SQDBEngine extends DBEngine {
	private String tname1; // name of table in charge; 
	private String column1_1;
	private String column1_2;
	
	public SQDBEngine() {
		this.tname1 = "sq_table"; // name of table in charge; 
		this.column1_1 = "keyword";
		this.column1_2 = "label";
	}
	
	public int sqQuery(String text) {
	
		return 0;
	}
	
	public String search(String text) throws Exception {
		//Write your code here
		String reply = null;
		text = text.toLowerCase();
		
		Connection connection = super.getConnection();
		String statement = "SELECT " + column1_2 + " FROM "+ tname1 + " WHERE " + text + " LIKE '%" + column1_1 + "%' ";		
		PreparedStatement stmt = connection.prepareStatement(statement);
		
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery();			
			if (rs.next()) {
				reply = rs.getString(1);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}finally {	
			rs.close();
			stmt.close();
			connection.close();
			//return -1; // Exception in executing query; 
		}
		
		if(reply != null) {
			return reply;
		}else {
			throw new Exception("NOT FOUND");	
			//return -2; // Exception "Not found"; 
		}
		
	}
}
