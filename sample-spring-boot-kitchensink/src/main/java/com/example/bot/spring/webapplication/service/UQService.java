package com.example.bot.spring.webapplication.service;

import java.util.LinkedList;

import com.example.bot.spring.webapplication.domain.UQ;
import com.example.bot.spring.webapplication.repos.UQRepo;

public class UQService {
	
	UQRepo uqRepo = new UQRepo();

	public LinkedList<UQ> getAllUQs() throws Exception {
		LinkedList<UQ> uqs = uqRepo.getUQs();
		return uqs;
	}
	
	public void answerUQ(String question, String id, String answer) {
		uqRepo.answerUQ(question,id,answer);
	}
}
