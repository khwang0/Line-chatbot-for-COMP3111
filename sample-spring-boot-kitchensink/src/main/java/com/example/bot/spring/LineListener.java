package com.example.bot.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.example.bot.spring.textsender.*;

@Component
public class LineListener extends Thread{
	
	@Autowired
	private ConfirmBroadcaster confirmBroadcaster;
	@Autowired
	private CancelBroadcaster cancelBroadcaster;
	@Autowired
	private UQAnswerReplier uqAnswerReplier;
	@Autowired
	private DoubleElevBroadcaster double11Broadcaster;
	
	public LineListener() {
		//confirmBroadcaster = new ConfirmBroadcaster();
		//cancelBroadcaster = new CancelBroadcaster();
		//uqAnswerReplier = new UQAnswerReplier();
		//double11Broadcaster = new DoubleElevBroadcaster();
	}
	
	@Override
	public void run() {
		while(true) {
			//TODO: Add what ever function need to run
			//execute 1 time per hour
			try { 
				//confirmBroadcaster.broadcast();
				//cancelBroadcaster.broadcast();
				uqAnswerReplier.broadcast();
				//double11Broadcaster.broadcast();				
			}catch(Exception e) {
				e.printStackTrace();
			}
			try {
				Thread.sleep(60000);
			}catch (InterruptedException e) {
				System.err.println("<><><><>Sleep Error<><><><>");
			}
		}
	}
}
