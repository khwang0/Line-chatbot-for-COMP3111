package com.example.bot.spring.textsender;

public interface TextSender {

	public String process(String userId, String msg) throws Exception;
}
