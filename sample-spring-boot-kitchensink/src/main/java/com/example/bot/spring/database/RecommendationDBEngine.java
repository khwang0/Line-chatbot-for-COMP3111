package com.example.bot.spring.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

//import com.linecorp.bot.model.event.message.TextMessageContent;

public class RecommendationDBEngine extends DBEngine {
	public RecommendationDBEngine() {
		
	}
	
	public String recommendationQuery(String userId, ArrayList<String> text) {
		Connection connection = getConnection();
		//insert into the unanswered question table to store the question
		String response="";
		String idList="";
		for(int i=0; i<text.size(); i++) {
			try {
				ResultSet rs = null;
				PreparedStatement stmt = connection.prepareStatement(
					"select * from tour_features where "+text.get(i)+"=1");
				String temp="Tours with good "+text.get(i)+" : ";
			
				rs=stmt.executeQuery();
				if (rs==null) {
					response="No tours with good "+text.get(i)+"\n";
				}
				else {
					while (rs.next()) {	
						String tourid = rs.getString(1);
						temp+=tourid+", ";
						idList+=tourid+", ";
					}
					temp=temp.replaceAll(", $", "");
					response+=temp+"\n";
				}
				idList=idList.replaceAll(", $", "");
				stmt.close();
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		PreparedStatement stmt=connection.prepareStatement(
				"update line_user_info set TourIDs ='"+idList+"'where UserID="+userId+";"
		);
		
		stmt.executeUpdate();

		stmt.close();
		connection.close();
		
		return response;
	}
}
