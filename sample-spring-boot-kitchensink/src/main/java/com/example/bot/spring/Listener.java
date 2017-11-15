package com.example.bot.spring;

import java.util.concurrent.*;
import java.util.*;

public class Listener extends Thread{
	public Listener() {
		
	}
	
	@Override
	protected void run() {
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
