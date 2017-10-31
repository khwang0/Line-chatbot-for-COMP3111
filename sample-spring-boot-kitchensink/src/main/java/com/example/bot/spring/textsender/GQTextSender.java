package com.example.bot.spring.textsender;

import com.example.bot.spring.database.GQDBEngine;

public class GQTextSender implements TextSender {
	
	private GQDBEngine DBE;
	public GQTextSender() {
		DBE= new GQDBEngine();
	}

	@Override
	public String process(String userID, String msg) {
		try{
			//System.err.println(userID+" : "+msg);
			String TourID=DBE.getTourID(userID,msg);
			//System.err.println(TourID);
			DBE.update(userID,TourID);
			return DBE.query(userID,msg,TourID);
		}catch(Exception e) {
			return null;
		}
	}
}
