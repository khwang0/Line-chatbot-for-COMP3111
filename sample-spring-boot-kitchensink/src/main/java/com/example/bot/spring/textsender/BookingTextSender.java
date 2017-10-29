package com.example.bot.spring.textsender;

import com.example.bot.spring.database.BookingDBEngine;

public class BookingTextSender implements TextSender {
	
	private BookingDBEngine bookingDB;

	public BookingTextSender() {
		// TODO Auto-generated constructor stub
		bookingDB = new BookingDBEngine();
	}
	
	@Override
	public String process(String userId, String msg) {
		String status = bookingDB.getStatus(userId);
		switch(status) {
			case "default":{
				break;
			}
			case "name":{
				bookingDB.recordName(userId,msg);
				break;
			}
			case "adults":{
				bookingDB.recordAdults(userId,msg);
				break;
			}
			case "children":{
				bookingDB.recordChildren(userId,msg);
				break;
			}
			case "toodler":{
				bookingDB.recordToodler(userId,msg);
				break;
			}
			case "phone" :{
				bookingDB.recordPhone(userId,msg);
				break;
			}
		}
		
		return null;
	}
	
	public int record() {
		
		return 0;
	}

}
