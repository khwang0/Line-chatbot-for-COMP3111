package com.example.bot.spring.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class GQDBEngine extends DBEngine {
	private String tname1; // name of table in charge; 
	private String column1_1;
	private String column1_2;
	
	public GQDBEngine() {
		this.tname1 = "gq_table"; // name of table in charge; 
		this.column1_1 = "pattern";
		this.column1_2 = "answer";
	}
	
	public String getTourID (String userID, String Text) throws Exception {
		Connection connection = getConnection();
		PreparedStatement stmt;
		ResultSet rs;
		
		stmt = connection.prepareStatement(
			"SELECT TourIDs FROM line_user_info WHERE userid = ?");
		stmt.setString(1, userID);
		rs = stmt.executeQuery();
		rs.next();
		String TourID = rs.getString(1);
		
		rs.close();
		stmt.close();
		stmt = connection.prepareStatement(
			"SELECT tour_name,tourid FROM tour_info");
		rs=stmt.executeQuery();
		String tourname;
		while(rs.next()) {
			boolean found=true;
			tourname=rs.getString(1);
			for(String info:tourname.split("\\s|-")) {
				if(Text.toLowerCase().contains(info.toLowerCase()))
					continue;
				switch(info.toLowerCase()) {
				case "tour":
				case "trip":
				case "city":
					break;
				default:
					found=false;
				}
				if(!found) break;
			}
			if(found) {
				TourID=rs.getString(2);
				break;
			}
		}
		rs.close();
		stmt.close();
		connection.close();
		return TourID;
	}

	public String query(String userID,String Text,String TourID) throws Exception{		
		/*TODO
		 * 	check whether we need to add "\n" at the end of every line
		 * */
			Connection connection = getConnection();
			PreparedStatement stmt = null;
			String statement = null;
			ResultSet rs = null;
			String answer="";
			
		try {
			// dynamic question
			if(Text.toLowerCase().contains("how long") || 
			   Text.toLowerCase().contains("how much time") ||
			   Text.toLowerCase().contains("duration") ) 
			{
				//System.err.println("ask time");
				stmt = connection.prepareStatement(
					"SELECT tour_name,duration FROM tour_info WHERE tourid = ?");				
				stmt.setString(1, TourID);
				rs=stmt.executeQuery();
								
				if(rs.next()) {
					answer+= rs.getString(1) + " takes " + rs.getInt(2) + " days.\n";
				}
			}
			
			else if(Text.toLowerCase().contains("feature") ||
			   Text.toLowerCase().contains("descri") ||
			   Text.toLowerCase().contains("introdu"))
			{
				//System.err.println("description tourid = "+TourID);
				//System.err.println("SELECT description from tour_description WHERE tourid = ?");
				stmt = connection.prepareStatement(
						"SELECT description from tour_description WHERE tourid = ?");
				stmt.setString(1, TourID);
				rs=stmt.executeQuery();
				
				if(rs.next()) {
					//System.err.println("updating in descri");
					answer+= "These are what you can expect there:";
					answer+="\n   "+rs.getString(1);
					answer+=".\n";
				}
				
			}
			
			else {
				// static answer question
				statement = "SELECT " + column1_2 + " FROM " + tname1 + " WHERE \'" + Text + "\' SIMILAR TO " + column1_1;
				//System.out.print(statement);
				stmt = connection.prepareStatement(statement);
				rs=stmt.executeQuery();
				
				if(rs.next()) {
					answer+="\n   "+rs.getString(1);
					answer+=".\n"; 
				}
			}
		}catch(Exception e) {
			System.err.println(e.getMessage());
		}finally {
			//rs.close();
			//stmt.close();
			try {
				if (rs.next()) rs.close();
				if (stmt != null) stmt.close();
				if (connection != null) connection.close();
			} catch (Exception e) {
				System.err.println(e.getMessage());
			}
		}
		return answer;
	}
	public void update(String UserID,String TourID) throws Exception{
		Connection connection = getConnection();
		PreparedStatement stmt;
		stmt = connection.prepareStatement(
				"UPDATE line_user_info SET tourids = ? WHERE userid = ?");
		stmt.setString(1, TourID);
		stmt.setString(2, UserID);
		stmt.executeUpdate();
		stmt.close();
		connection.close();
	}
	
}
