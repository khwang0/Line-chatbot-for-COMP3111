package com.example.bot.spring;

import com.example.bot.spring.textsender.*;

public class LineListener extends Thread{
	
	private ConfirmBroadcaster confirmBroadcaster;
	private CancelBroadcaster cancelBroadcaster;
	private UQAnswerReplier uqAnswerReplier;
	private DoubleElevBroadcaster double11Broadcaster;
	
	public LineListener() {
		confirmBroadcaster = new ConfirmBroadcaster();
		cancelBroadcaster = new CancelBroadcaster();
		uqAnswerReplier = new UQAnswerReplier();
		double11Broadcaster = new DoubleElevBroadcaster();
	}
	
	@Override
	public void run() {
		while(true) {
			//TODO: Add what ever function need to run
			//execute 1 time per hour
			try {
				confirmBroadcaster.broadcast();
				cancelBroadcaster.broadcast();
				uqAnswerReplier.broadcast();
				double11Broadcaster.broadcast();				
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
