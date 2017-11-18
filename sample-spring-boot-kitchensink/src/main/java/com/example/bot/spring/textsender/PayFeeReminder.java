package com.example.bot.spring.textsender;

import java.util.HashSet;
import java.util.Set;

import com.example.bot.spring.database.ReminderDBEngine;
import com.linecorp.bot.model.Multicast;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TextMessage;

public class PayFeeReminder {

	public PayFeeReminder() {
		// TODO Auto-generated constructor stub
	}
	
	public Multicast feePayReminder(String[] ids) throws Exception{
		ReminderDBEngine reminder = new ReminderDBEngine();
		
		Set<String> id = new HashSet<String>();
		
		id=reminder.ReminderChecker();
		
		if(id.isEmpty()) {
			return null;
		}
		
		Message reply = new TextMessage ("You have yet to pay the fee. Please pay as soon as possible.");
		
		return new Multicast(id, reply);
	}

}
