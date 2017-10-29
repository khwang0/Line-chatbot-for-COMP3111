package com.example.bot.spring;

import com.linecorp.bot.model.event.message.TextMessageContent;
import com.example.bot.spring.textsender.*;

public class TextProcessor {
	
	public TextProcessor() {
		// TODO Auto-generated constructor stub
	}
	
	public String processText(String userId, String text) {
		
		// update line_user_info table; 
		
		// categorize input message (ANOTHER DB REQUIRED)
		
		// SQ Test Sender; 
		String reply = null;  // = sq test sender;
		SQTextSender sqsender = new SQTextSender();
       try {
    	   reply = sqsender.process(userId, text);
			
		} catch (Exception e) {
			System.out.println("---------- inside handleTextContent ---------- ");
			System.err.println(e.getMessage());
		}		
		
		
		
				
		return reply;
	}
}
