package com.example.bot.spring;

import com.linecorp.bot.model.event.message.TextMessageContent;
import com.example.bot.spring.textsender.*;

public class TextProcessor {

	DBEngine DBE;
	public TextProcessor() {
		// TODO Auto-generated constructor stub
		DBE=new DBEngine();
	}
	
	private String classifyText(String userId, String text)throws Exception {
		String reply="";
		String tag;
		try {
			SQTextSender sqsender = new SQTextSender();
			reply = sqsender.process(userId, text)+"\n";
		}catch(Exception e) {}
		try {	
			if(text.toLowerCase().contains("recommend")||
			   text.toLowerCase().contains("do you have")) {
				RecommendationTextSender rsender = new RecommendationTextSender();
				reply += rsender.process(userId, text);
				DBE.update(userId,"categorization","reco");
				return reply;
			}

			if(text.toLowerCase().contains("tell me")) {
				GQTextSender gqsender = new GQTextSender;
				reply += gqsender.process(userId, text);
				DBE.update(userId,"categorization", "gq");
				return reply;
			}

			if(text.toLowerCase().contains("book")) {
				BookingTextSender bsender = new BookingTextSender();
				reply += bsender.process(userId, text);
				DBE.update(userId,"categorization","book");
				return reply;
			}
			
			tag=DBE.get(userId,"categorization");
			switch(tag) {
			case "reco":
				RecommendationTextSender rsender = new RecommendationTextSender();
				reply += rsender.process(userId, text);
				break;
			case "book":
				BookingTextSender bsender = new BookingTextSender();
				reply += bsender.process(userId, text);
				break;
			case "gq":
				GQTextSender gqsender = new GQTextSender;
				reply += gqsender.process(userId, text);
				break;
			default:
				reply += "Could you please specify that you want some recommendation, asking some general question, or booking a trip";
			}
			return reply;
			
		} catch (Exception e) {
			// TODO: call unanswered question
		}
		throw Exception("Still not return");
		return null;

	}

	public String processText(String userId, String text) throws Exception{
		if (text=null) {
			throw Exception("no input");
		}
		DBE.update(userId,"lastq",text);
		String reply=classifyText(userID,text);
		DBE.update(userId,"lasta",reply);
	}
}
