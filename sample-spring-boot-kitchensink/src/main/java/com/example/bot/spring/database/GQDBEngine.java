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
		System.err.println("getting tid");
		Connection connection = getConnection();
		PreparedStatement stmt;
		ResultSet rs;
		String TourID = "";
		
		stmt = connection.prepareStatement(
			"SELECT TourIDs FROM line_user_info WHERE userid = ?");
		stmt.setString(1, userID);
		rs = stmt.executeQuery();
		if(rs.next()) {
			TourID = rs.getString(1);
		}

		
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
				found=false;
				if(!found) break;
			}
			if(Text.toLowerCase().contains(rs.getString(2).toLowerCase()))found=true;
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
			Connection connection = null;
			PreparedStatement stmt = null;
			String statement = null;
			ResultSet rs = null;
			String answer="";
			String tag="";
			System.err.println(TourID);
		//try {
			connection = getConnection();
			stmt=connection.prepareStatement("SELECT * FROM gqtag");
			rs=stmt.executeQuery();
			while(rs.next()&&tag.equals("")) {
				if(Text.toLowerCase().contains(rs.getString(1)))
					tag=rs.getString(2);
			}
			System.err.printf("tag=%s\n", tag);
			rs.close();
			stmt.close();
			switch (tag) {
			case "time":
				if(TourID.length()<5) {
					answer+=("Could you please specify which tour you what you ask?");
					break;
				}
				stmt = connection.prepareStatement(
					"SELECT tour_name,duration FROM tour_info WHERE tourid = ?");				
				stmt.setString(1, TourID);
				rs=stmt.executeQuery();
								
				if(rs.next()) {
					answer+= rs.getString(1) + " takes " + rs.getInt(2) + " days.\n";
				}
				break;
			case "feat":
				if(TourID.length()<5) {
					answer+=("Could you please specify which tour you what you ask?");
					break;
				}
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
				break;
			default:
				// static answer question
				statement = "SELECT " + column1_2 + " FROM " + tname1 + " WHERE \'" + Text + "\' SIMILAR TO " + column1_1;
				//System.out.print(statement);
				stmt = connection.prepareStatement(statement);
				rs=stmt.executeQuery();
				
				if(rs.next()) {
					answer+="\n   "+rs.getString(1);
					answer+=".\n"; 
				}
				break;
			}
		//}catch(Exception e) {
		//	e.printStackTrace();
		//	System.err.println(e.getMessage());
		//}finally {
			//rs.close();
			//stmt.close();
			//try {
				if (rs.next()) rs.close();
				if (stmt != null) stmt.close();
				if (connection != null) connection.close();
			//} catch (Exception e) {
				//System.err.println(e.getMessage());
			//}
		//}
		return answer;
	}
	public void update(String UserID,String TourID) throws Exception{
		System.err.println("updating tid");
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
