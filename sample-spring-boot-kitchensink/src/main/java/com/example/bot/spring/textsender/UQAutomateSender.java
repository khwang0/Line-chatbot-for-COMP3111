package com.example.bot.spring.textsender;

import com.example.bot.spring.database.UQDBEngine;

public class UQAutomateSender implements TextSender {

	public UQAutomateSender() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String process(String userId, String msg) throws Exception {
		// TODO Auto-generated method stub
		UQDBEngine searchEngine = new UQDBEngine();
		return searchEngine.uqQuery(userId, msg);
	}
}