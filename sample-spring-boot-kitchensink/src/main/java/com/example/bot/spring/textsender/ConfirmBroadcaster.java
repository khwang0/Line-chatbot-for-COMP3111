package com.example.bot.spring.textsender;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import com.example.bot.spring.database.*;
import com.linecorp.bot.client.*;
import com.linecorp.bot.model.*;
import com.linecorp.bot.model.message.*;
import org.springframework.beans.factory.annotation.Autowired;

public class ConfirmBroadcaster implements Broadcaster {
	
	DBEngine confirmdb; 
	@Autowired
	private LineMessagingClient lineMessagingClient;
	
	public ConfirmBroadcaster() {
		// TODO Auto-generated constructor stub
		//this.confirmdb = new ConfirmDBEngine();
	}
	
	public void broadcast() throws Exception{}
	
	public void orderConfirm(String[] ids) throws Exception{
		try {
			this.confirmdb = new ConfirmDBEngine();
			// check each trip available in db
			// if there are any trip get to min tourist yet haven't been confirmed, confirm order; 
			List<String> tourids = confirmdb.getAllUnconfirmedTours();
			
			// extract all contactor info into a Set<String> to
			// extract tour name into String tourname; 
			// prepare Message: "Your tour " + tourname + " has been confirmed! Looking forward to see you on the depart day!";

			for (String tourid: tourids) {
				String broadcast_content = "Your tour " + tourid + " has been confirmed! Looking forward to see you on the depart day!";
				Set<String> tourists = confirmdb.getAllContactors(tourid);
				
				Message message = new TextMessage(broadcast_content);			
				lineMessagingClient.multicast(new Multicast(tourists, message));
			}
		}catch (Exception e) {
			System.err.println(e);
		}
	}
	
}
