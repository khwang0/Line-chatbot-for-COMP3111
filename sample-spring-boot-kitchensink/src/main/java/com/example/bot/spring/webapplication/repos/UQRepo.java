package com.example.bot.spring.webapplication.repos;

import java.util.LinkedList;

import org.springframework.stereotype.Repository;

import com.example.bot.spring.database.WebAppDBEngine;
import com.example.bot.spring.webapplication.domain.UQ;

@Repository
public class UQRepo {

	public LinkedList<UQ> getUQs() throws Exception {
		WebAppDBEngine webDB = new WebAppDBEngine();
		return webDB.getUQs();
	}

	public void answerUQ(String question, String id, String answer) throws Exception {
		// TODO Auto-generated method stub
		WebAppDBEngine webDB = new WebAppDBEngine();
		webDB.answerUQ(question,id,answer);
	}
	
}
