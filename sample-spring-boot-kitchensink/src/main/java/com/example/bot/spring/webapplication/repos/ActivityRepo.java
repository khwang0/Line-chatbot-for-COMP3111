package com.example.bot.spring.webapplication.repos;

import java.util.LinkedList;

import org.springframework.stereotype.Repository;

import com.example.bot.spring.database.WebAppDBEngine;
import com.example.bot.spring.webapplication.domain.Activity;

@Repository
public class ActivityRepo {
	public LinkedList<Activity> getAllActivities() throws Exception{
		WebAppDBEngine db = new WebAppDBEngine();
		return db.getAllActivities();
	}
	
	public void createNewActivity(Activity act) throws Exception{
		WebAppDBEngine db = new WebAppDBEngine();
		db.createNewActivity(act);
	}
}
