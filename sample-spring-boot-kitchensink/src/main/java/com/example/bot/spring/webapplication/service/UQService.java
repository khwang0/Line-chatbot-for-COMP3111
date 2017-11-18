package com.example.bot.spring.webapplication.service;

import java.util.LinkedList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.bot.spring.webapplication.domain.UQ;
import com.example.bot.spring.webapplication.repos.UQRepo;

@Service
public class UQService {
	
	@Autowired
	UQRepo uqRepo;

	public LinkedList<UQ> getAllUQs() throws Exception {
		LinkedList<UQ> uqs = uqRepo.getUQs();
		return uqs;
	}
	
	public void answerUQ(String question, String id, String answer) throws Exception {
		uqRepo.answerUQ(question,id,answer);
	}
}
