package com.example.bot.spring.textsender;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.bot.spring.database.*;
import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.Multicast;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TextMessage;

public class CancelBroadcaster implements Broadcaster {
	
	@Autowired
	private LineMessagingClient lineMessagingClient;
	
	/**
	 * Get a diff between two dates
	 * @param date1 the oldest date
	 * @param date2 the newest date
	 * @param timeUnit the unit in which you want the diff
	 * @return the diff value, in the provided unit
	 */
	private long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
	    long diffInMillies = date2.getTime() - date1.getTime();
	    return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
	}
	
	private Date getDate(String dateString) {
		int y,m,d;
		y=Integer.parseInt(dateString.substring(0,4));
		m=Integer.parseInt(dateString.substring(4,6));
		d=Integer.parseInt(dateString.substring(6,8));
		GregorianCalendar c= new GregorianCalendar(y,m,d);
		return c.getTime();
	}
	
	CancelDBEngine CDB;
	public CancelBroadcaster() {
		CDB=new CancelDBEngine();
	}
	
	public void broadcast() throws Exception{
		List<String> bids = CDB.getAllUnconfirmedTours();
		for (String bid: bids) {
			orderCancel(bid);
		}
	}
	
	public int orderCancel(String bootid) throws Exception{
		Date td=getDate(bootid.substring(bootid.length()-8));
		Date now=new Date();
		long day=this.getDateDiff(td, now, TimeUnit.DAYS);
		if (day<=3) {
			String broadcast_content = "Your tour " + bootid + " has been canceled due to not enough people! Our staff will arrange your refund as soon as possible.";
			Set<String> tourists = CDB.getAllContactors(bootid);

			Message message = new TextMessage(broadcast_content);			
			lineMessagingClient.multicast(new Multicast(tourists, message));
		}
		return 0;
	}
}
