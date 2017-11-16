package com.example.bot.spring.webapplication.domain;

import static org.mockito.Mockito.RETURNS_DEEP_STUBS;

public class UQ {
	
	String id;
	String question;
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}
	
	public void setQuestion(String question) {
		this.question = question;
	}
	
	public String getQuestion() {
		return question;
	}
}
