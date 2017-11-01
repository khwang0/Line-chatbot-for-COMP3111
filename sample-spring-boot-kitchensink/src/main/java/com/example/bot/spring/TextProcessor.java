package com.example.bot.spring;

import com.example.bot.spring.database.DBEngine;
import com.example.bot.spring.textsender.*;

public class TextProcessor {

	private DBEngine DBE;
	public TextProcessor() {
		// TODO Auto-generated constructor stub
		DBE=new DBEngine();
	}
	
	private String classifyText(String userId, String text){
		String reply="";		
		
		try {
			SQTextSender sqsender = new SQTextSender();
			reply = sqsender.process(userId, text)+"\n";					
		}catch(Exception e) {
			// no action; 
		}
		try {			
			String tag = null;
			//TODO:change this to database
			tag=DBE.getLineUserInfo(userId,"categorization");		
			if(tag == "book") {
				BookingTextSender bsender = new BookingTextSender();
				reply += bsender.process(userId, text);	
				return reply;
			}

			if(text.toLowerCase().contains("recommend")||
			   text.toLowerCase().contains("do you have")) {
				RecommendationTextSender rsender = new RecommendationTextSender();
				DBE.updateLineUserInfo(userId,"categorization","reco");	
				reply += rsender.process(userId, text);			
				return reply; //return "in recomend";
			}

			if(text.toLowerCase().contains("tell me")) {
				GQTextSender gqsender = new GQTextSender();
				DBE.updateLineUserInfo(userId,"categorization", "gq");	
				reply += gqsender.process(userId, text);			
				return reply; //return "in general q";
			}

			if(text.toLowerCase().contains("book")) {
				BookingTextSender bsender = new BookingTextSender();
				DBE.updateLineUserInfo(userId,"categorization","book");	
				reply += bsender.process(userId, text);			
				return reply; //return "in booking";
			}
			
			if (tag.equals("reco")) {				
				RecommendationTextSender rsender = new RecommendationTextSender();
				reply += rsender.process(userId, text);	
				return reply;
			}
			
			if (tag == "gq") {
				GQTextSender gqsender = new GQTextSender();
				reply += gqsender.process(userId, text);	
				return reply;
			}
			
			reply += "Could you please specify that you want some recommendation, asking some general question, or booking a trip";
			UQAutomateSender uqSender = new UQAutomateSender();
			uqSender.process(userId, text);	
			
			return reply;
			
			// tag=DBE.getLineUserInfo(userId,"categorization");
			// switch(tag) {
			//	case "reco":
			//		RecommendationTextSender rsender = new RecommendationTextSender();
			//		reply += rsender.process(userId, text);				
			//		break;  //return "in recomend via tag";
			
			//	case "book":
			//		BookingTextSender bsender = new BookingTextSender();
			//		reply += bsender.process(userId, text);				
			//		break; //return "in booking via tag";
			//	case "gq":
			//		GQTextSender gqsender = new GQTextSender();
			//		reply += gqsender.process(userId, text);	
			//		break; //return "in general q via tag";
			//	default:
		} catch (Exception e) {
			try {
				DBE.updateLineUserInfo(userId,"categorization", "default");
			}catch(Exception e2) {
				// no action; 
			}	
			
			UQAutomateSender uqSender = new UQAutomateSender();			
			reply = uqSender.process(userId, text);			
			
			return reply; //return "exception here";
		}

	}

	public String processText(String userId, String text) throws Exception{
		DBE.updateLineUserInfo(userId,"lastq",text);
		if (text==null) {// yet text won't be null?
			throw new Exception("no input");
		}
		String reply=classifyText(userId,text);
		DBE.updateLineUserInfo(userId,"lasta",reply);
		return reply;
	}
	
	private void formatMsg(String message) {
		String processedMsg = null; 
		// 01 clear unneeded whitespace and punctuation;
		int len = message.length();
		for (int i = 0; i < len; i++) {
			// add message content into processedMsg if it's not "  " or punctuation;
		} 
				
		// 02 transfer the input message into lowercase
	}
}
