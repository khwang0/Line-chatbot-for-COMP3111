package com.example.bot.spring.textsender;

import java.util.Set;
import com.example.bot.spring.database.*;
import com.linecorp.bot.client.*;
import com.linecorp.bot.model.*;
import com.linecorp.bot.model.message.*;
import org.springframework.beans.factory.annotation.Autowired;

public class DoubleElevBroadcaster implements Broadcaster {
	
	DoubleElevDBEngine doubledb; 
	@Autowired
	private LineMessagingClient lineMessagingClient;	
	
	public DoubleElevBroadcaster() {
		
	}
	
	public void broadcast() throws Exception{
			doubledb = new DoubleElevDBEngine();
			// check each trip available in db
			// if there are any trip get to min tourist yet haven't been confirmed, confirm order; 
			String tourid = doubledb.getDiscountBookid();
			
			// extract all contactor info into a Set<String> to;
			// extract tourid into String tourid;
			String broadcast_content = "START COMPETE FOR TOUR " + tourid + " AT 50% DISCOUNT!! \n Wanna Grab one? "; // later if reply yes, then jump to booking, else, do nothing

			Set<String> tourist = doubledb.getAllClient();				
			Message message = new TextMessage(broadcast_content);
				
			lineMessagingClient.multicast(new Multicast(tourist, message));  // Multicast(Set<String> to, String msg)
			doubledb.updateBroadcastedTours(tourid);  // update "confirmed" col of informed tours into TRUE; 
	}
}
