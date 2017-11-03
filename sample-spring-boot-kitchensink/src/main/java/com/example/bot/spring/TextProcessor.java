package com.example.bot.spring;

import com.example.bot.spring.database.DBEngine;
import com.example.bot.spring.textsender.*;

public class TextProcessor {

	private DBEngine DBE;
	
	public TextProcessor() {
		// TODO Auto-generated constructor stub
		DBE=new DBEngine();
	}
	
	/**
	 * @param
	 * 		userId: userId of sender;
	 * 		text: formated sent message (only contains char and number and decimal point)
	 * 		
	 * */
	private String classifyText(String userId, String text){
		String reply="";		
		
		try {			
			String tag = null;
			String label = null;
			
			tag = DBE.getLineUserInfo(userId,"categorization");		
			label = DBE.getTextType(text);
//			reply = "tag: " + tag + " label: " + label;
			
			if ((tag == null || tag == "" || tag == "default" || tag == "none") 
					&& (label == null || label == "" || label == "default" || label == "none")){
				//reply = "tag: " + tag + " label: " + label;
				SQTextSender sqsender = new SQTextSender();
				reply = sqsender.process(userId, text)+"\n";	
			}
			
			if(tag.equals("book")) {
				BookingTextSender bsender = new BookingTextSender();
				reply += bsender.process(userId, text);	
				return reply;
			}
			// after decide the label:
			// - update user tag;
			// - pass the info to corresponding text processor;		
			

			switch (label) {
			case "reco": 
				RecommendationTextSender rsender = new RecommendationTextSender();
				DBE.updateLineUserInfo(userId,"categorization","reco");	
				reply += rsender.process(userId, text);			
				return reply;
				
			case "gq":
				GQTextSender gqsender = new GQTextSender();
				DBE.updateLineUserInfo(userId,"categorization", "gq");	
				reply += gqsender.process(userId, text);			
				return reply;
				
			case "book":
				BookingTextSender bsender = new BookingTextSender();
				DBE.updateLineUserInfo(userId,"categorization","book");	
				reply += bsender.process(userId, text);			
				return reply;
				
			default:
				// no action, continue to next checking state; 				
			}
			
			switch (tag) {
			case "reco":
				RecommendationTextSender rsender = new RecommendationTextSender();
				reply += rsender.process(userId, text);	
				return reply;
			case "gq":
				GQTextSender gqsender = new GQTextSender();
				reply += gqsender.process(userId, text);	
				return reply;
			default:
				// no action, continue to unanswered question processor; 
			}
			/*
			if(text.toLowerCase().contains("recommend")||
			   text.toLowerCase().contains("do you have")) {
				RecommendationTextSender rsender = new RecommendationTextSender();
				DBE.updateLineUserInfo(userId,"categorization","reco");	
				reply += rsender.process(userId, text);			
				return reply; //return "in recomend";
			}

			if(text.toLowerCase().contains("tell me")||
			   text.toLowerCase().contains("introduc")) {
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
			
			if (tag.equals("gq")) {
				GQTextSender gqsender = new GQTextSender();
				reply += gqsender.process(userId, text);	
				return reply;
			}
			*/
			
			reply += "You can send your request by specifying: recommendation/ general question/booking a trip";
			UQAutomateSender uqSender = new UQAutomateSender();
			uqSender.process(userId, text);	
			
			return reply;
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
		if (text == null) {// yet text won't be null?
			throw new Exception("no input");
		}
		String reply = "";		
		
		text = formatMsg(text);	// format the text before classification 
		DBE.updateLineUserInfo(userId,"lastq",text); // insert the formatted text into database; 
		
		if(text.equals("")) {
			throw new Exception("no input");
		}else {
			reply = classifyText(userId,text);
			DBE.updateLineUserInfo(userId,"lasta",reply);
			return reply;
		}
	}
	
	/** truncate all non-digit and non-char elements from user input (decimal point is reserved)
	 * and transform the input into lower case; 
	 * @param message: user-input;
	 * 
	 * NOTICE: it's possible for formatMsg() to return a empty text, which need to be handled in its calling function
	 */
	private String formatMsg(String message) {
		char preChar = 'S';
		String outputMsg = "";
		
		for (char c: message.toCharArray()) {
			if(isDigit(c) || isChar(c) || isAllowedPunc(c)){
				outputMsg += c;
				preChar = c;
			}else if (preChar != ' '){
				outputMsg += ' ';
				preChar = ' ';
			}
		}
		outputMsg = outputMsg.toLowerCase();	
		return outputMsg;
	}
	
	// helper function for formatMsg()
	private boolean isChar (char c) {
		if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'))
			return true;
		else
			return false; 
	}
	
	// helper function for formatMsg()
	private boolean isDigit (char c) {
		if ((c >= '0' && c <= '9') || c == '.')
			return true;
		else
			return false; 
	}
	
	private boolean isAllowedPunc(char c) {
		if ( c == '/')
			return true;
		else
			return false; 
	}
	/* TODO:
	 *  do you think we need to add ?.. into allowedPunc list?
	 */
}
