package com.example.bot.spring.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.linecorp.bot.model.event.message.TextMessageContent;

public class GQDBEngine extends DBEngine {
	public GQDBEngine() {
		
	}
	
	public String getTourID(String userID,String Text) {
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
		stmt = connnection.prepareStatement(
			"SELECT tour_name,tourid FROM tour_info");
		rs=stmt.executeQuery();
		String tourname;
		while(rs.next()) {
			boolean found=true;
			tourname=rs.getString(1);
			for(String info:tourname.split("\\s|-")) {
				if(Text.toLowerCase().contains(info.toLowerCase()))continue;
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

	@Override
	public String query(String userID,String Text,String TourID) {
		Connection connection = getConnection();
		PreparedStatement stmt;
		ResultSet rs;
		String answer="";
		if(Text.toLowerCase().contains("how long") || 
		   Text.toLowerCase().contains("how much time") ||
		   Text.toLowerCase().contains("duration") ) 
		{
			stmt = connection.prepareStatement(
				"SELECT tour_name,duration FROM tour_info WHERE tourid = ?");
			stmt.setString(1, TourID);
			rs=stmt.executeQuery();
			rs.next();
			answer+= rs.getString(1) + " takes " + rs.getString(2) + " days.\n";
			rs.close();
			stmt.close();
		}
		if(Text.toLowerCase().contains("feature") ||
		   Text.toLowerCase().contains("descri") ||
		   Text.toLowerCase().contains("introdu"))
		{
			stmt = connection.prepareStatement(
					"SELECT tour_short_description from tour_description WHERE tourid=?");
			stmt.setString(1, TourID);
			answer+= "These are what you can expect there:";
			rs=stmt.executeQuery();
			while(rs.next()) {
				answer+="\n   "+rs.getString(1);
			}
			answer+=".\n"
		}
		return answer;
	}
	public String update(String UserID,String TourID) {
		Connection connection = getConnection();
		PreparedStatement stmt;
		ResultSet rs;
		stmt = connection.prepareStatement(
				"UPDATE line_user_info SET tourids = ? WHERE userid = ?");
		stmt.setString(1, TourID, UserID);
	}
	
}
