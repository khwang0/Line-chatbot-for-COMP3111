package com.example.bot.spring;

import com.example.bot.spring.database.DBEngine;
import com.example.bot.spring.database.DoubleElevDBEngine;
import com.example.bot.spring.textsender.*;

public class TextProcessor {

	private DBEngine DBE;
	private DoubleElevDBEngine DEDBE; 
	
	public TextProcessor() {
		// TODO Auto-generated constructor stub
		DBE=new DBEngine();
	}
	
	/**
	 * @param
	 * 		userId: userId of sender;
	 * 		text: formated sent message (only contains char and number and decimal point)
	 * @throws Exception  
	 * 		
	 * */
	private String classifyText(String userId, String text) throws Exception{
		String reply="";		
		
		try {			
			String tag = DBE.getLineUserInfo(userId,"categorization"); // from database; 
			String label = DBE.getTextType(text);					   // by analysis input 
						
			SQTextSender sqsender = new SQTextSender();
			reply = sqsender.process(userId, text)+"\n";
						
			if(tag.equals("book")) {
				BookingTextSender bsender = new BookingTextSender();
				reply += bsender.process(userId, text);	
				return reply;
			}
			
			if (text.equals("yes")|| text.equals("no")) {
				return double_elev_handler(userId, text);
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
	
	/**
	 * helper function for formatMsg()
	 * check if next character is 'char'
	 * */ 
	private boolean isChar (char c) {
		if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'))
			return true;
		else
			return false; 
	}
	
	/**
	 * helper function for formatMsg()
	 * check if next character is 0-9 or decimal point
	 * */ 
	private boolean isDigit (char c) {
		if ((c >= '0' && c <= '9') || c == '.')
			return true;
		else
			return false; 
	}
	
	/**
	 * helper function for formatMsg()
	 * check if next character is legal puncuation
	 * */ 
	private boolean isAllowedPunc(char c) {
		if ( c == '/')
			return true;
		else
			return false; 
	}

	/**
	 * @param 
	 * userId: the user who reply msg
	 * message: the message sent by user; 
	 * 
	 * special handler for double 11 event; 
	 * after a double11 message is broadcast, check user reply;
	 * if reply yes: 
	 * */
	private String double_elev_handler(String userId, String message) throws Exception {
		assert (message.equals("yes")|| message.equals("no"));
		String reply = "";
		if (message.equals("yes")) {
			// get current discount tour
			String discount_tourid = DEDBE.getDiscountBookid(); 					// check double11 table, get available tour's id; id =  DEDBE.getDiscountBookid()	
			// check if there are still ticket:
			if(DEDBE.ifTourFull(discount_tourid)) {
				DBE.updateLineUserInfo(userId,"categorization", "booking"); 			// update line_user_info.categorization into "booking"	
				DBE.updateLineUserInfo(userId,"status", "double11"); 			// update line_user_info.categorization into "booking"	
				DBE.updateLineUserInfo(userId,"tourids", discount_tourid); 	// update line_user_info.discount_tour_id = id;
				DBE.updateLineUserInfo(userId,"discount", "true"); 	// update line_user_info.discount_tour_id = id;
				reply = "Congratulations! You Got A Discount Ticket! Now you can continue booking!";
			}else {
				reply = "Sorry ticket sold out";
			}		
		}
		else {
			reply = "Sure =) Have a nice days."; 
		}
		
		return reply;
	}
}
