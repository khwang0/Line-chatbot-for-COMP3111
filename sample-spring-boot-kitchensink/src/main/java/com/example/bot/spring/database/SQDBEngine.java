package com.example.bot.spring.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SQDBEngine extends DBEngine {
	private String tname1; // name of table in charge; 
	private String column1_1;
	private String column1_2;
	
	public SQDBEngine() {
		this.tname1 = "sq_table"; // name of table in charge; 
		this.column1_1 = "keywords";
		this.column1_2 = "label";
	}
	
	public String search(String text) throws Exception {
		//Write your code here
		String reply = null;
		text = text.toLowerCase();
		
		Connection connection = null;
		String statement = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			connection = super.getConnection();	
		
			statement = "SELECT " + column1_2 + " FROM "+ tname1 + " WHERE \'" + text + "\' SIMILAR TO "
					+ "concat('(', keywords, ')|(', keywords, '[^\\d\\w](%)*)|((%)*[^\\d\\w](' ,keywords, '))|((%)*[^\\d\\w](', keywords, ')[^\\d\\w](%)*)')";
			
			//statement = "SELECT " + column1_2 + " FROM "+ tname1 + " WHERE \'" + text + "\' SIMILAR TO '" + pattern + "'";
			//statement = "SELECT " + column1_2 + " FROM "+ tname1 + " WHERE \'" + text + "\' SIMILAR TO "
			//		+ "concat(keywords, '|(', keywords, '[^\\d\\w])|([^\\d\\w]' ,keywords, '|([^\\d\\w](', keywords, ')[^\\d\\w])')";
			
			//stmt.setString(1, pattern);	
			//statement = "SELECT " + column1_2 + " FROM "+ tname1 + " WHERE \'" + text + "\' LIKE keywords;";
						
			//statement = "SELECT " + column1_2 + " FROM "+ tname1 + " WHERE \'" + text + "\' LIKE concat('%', keywords, '%')";
			
			stmt = connection.prepareStatement(statement);
			System.out.println("statement: " + statement);			
			rs = stmt.executeQuery();			
			
			if (rs.next()) {				
				reply = rs.getString(1);
			}
			
			System.out.println(reply);
			
		}catch(Exception e) {
			System.out.println("---------- inside search ---------- ");
			System.err.println(e.getMessage());
			e.printStackTrace();
		}finally {	
			//rs.close();
			//stmt.close();
			//connection.close(); 
			try {
				if (rs.next()) rs.close();
				if (stmt != null) stmt.close();
				if (connection != null) connection.close();
			} catch (Exception e) {
				System.err.println(e.getMessage());
			}
		}
		System.out.print(reply);
		if(reply != null) {
			System.out.print(reply);
			return reply;
		}else {
			throw new Exception("NOT FOUND");	
		}
		
	}
}
