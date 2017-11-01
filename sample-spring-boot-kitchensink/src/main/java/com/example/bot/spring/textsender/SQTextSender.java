package com.example.bot.spring.textsender;

import com.example.bot.spring.database.*;

public class SQTextSender implements TextSender {
	private SQDBEngine sqdbengine; 

	public SQTextSender() {
		// TODO Auto-generated constructor stub
		this.sqdbengine = new SQDBEngine();
	}
	
	@Override
	public String process(String userId, String msg) throws Exception{
		if(msg == null) {
			throw new Exception("unvalid input for SQTextSender.");
		}
		// TODO Auto-generated method stub
		/* Label: greeting/ thanks/ goodbye */
		String label = null; 
		// if msg contains certain keywords, label it
			
			label = sqdbengine.search(msg);
			// label = label.replaceAll("\\s+$", "");	// trunc the whitespace at the end 
					
		switch (label) {
			case "greeting":{
				System.out.print("should be here");
				return "Hi! How can I help you?";
			}
			case "thank":{
				return "You are welcome =)";
			}
			case "bye":{
				return "have a nice day!";
			}
			default:{
				throw new Exception("Not a simple question");
			}
		}
	}
}
