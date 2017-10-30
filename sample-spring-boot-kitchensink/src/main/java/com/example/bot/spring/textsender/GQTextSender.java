package com.example.bot.spring.textsender;

import com.example.bot.spring.database.GQDBEngine;

public class GQTextSender implements TextSender {
	
	private GQDBEngine DBE;
	public GQTextSender() {
		DBE= new GQDBEngine();
	}

	@Override
	public String process(String userID, String msg) throws Exception{
			String TourID=DBE.getTourID(userID,msg);
			DBE.update(userID,TourID);
			String reply =DBE.query(userID,msg,TourID);
			if(reply == null)
				throw new Exception("No matching");
			return reply;
	}
}
