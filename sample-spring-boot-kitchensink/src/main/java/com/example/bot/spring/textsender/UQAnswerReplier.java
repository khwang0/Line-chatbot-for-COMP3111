package com.example.bot.spring.textsender;

import java.util.*
//import java.util.GregorianCalendar;
//import java.util.List;
//import java.util.Set;
//import java.util.concurrent.TimeUnit;
import com.example.bot.spring.database.*;

public class UQAnswerReplier implements Broadcaster{

	public UQAnswerReplier() {
		// TODO Auto-generated constructor stub
	}

	public int replyUnansweredQuestion(String msg, String id) {
		UQDBEngine searchEngine = new UQDBEngine();
		ArrayList<String> reply=searchEngine.answer();
		for(int i=0; i<reply.size(); i++) {
			String userID;
			String question;
			String answer;
			String[3] temp = reply.split(",");
			userID=temp[0];
			question=temp[1];
			answer=temp[2];
			Message message = new TextMessage("For your question "+question+", the answer is "+answer);
			lineMessagingClient.pushMessage(new PushMessage(userID, message));
		}
		return 0;
	}
}
