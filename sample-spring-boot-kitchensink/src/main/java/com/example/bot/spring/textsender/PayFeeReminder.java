package com.example.bot.spring.textsender;

import java.util.Set;

import com.example.bot.spring.database.BookingDBEngine;

public class PayFeeReminder {

	public PayFeeReminder() {
		// TODO Auto-generated constructor stub
	}
	
	public Multicast feePayReminder(String[] ids) throws Exception{
		BookingDBEngine reminder = new BookingDBEngine();
		
		Set<String> id = new Set<String>();
		
		id=reminder.ReminderChecker();
		
		if(id.isEmpty()) {
			return null
		}
		
		Message reply = new Message ("You have yet to pay the fee. Please pay as soon as possible.")
		
		return Multicast(id, reply);
	}

}
