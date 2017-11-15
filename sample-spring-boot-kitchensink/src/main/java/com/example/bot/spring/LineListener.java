package com.example.bot.spring;

public class LineListener extends Thread{
	public LineListener() {
		
	}
	
	@Override
	public void run() {
		while(true) {
			//TODO: Add what ever function need to run
			//execute 1 time per hour
			
			
			try {
				Thread.currentThread().sleep(3600000);
			}catch (InterruptedException e) {
				System.err.println("<><><><>Sleep Error<><><><>");
			}
		}
	}
}
