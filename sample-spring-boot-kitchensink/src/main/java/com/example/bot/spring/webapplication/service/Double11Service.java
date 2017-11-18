package com.example.bot.spring.webapplication.service;

import java.util.LinkedList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.bot.spring.webapplication.domain.Activity;
import com.example.bot.spring.webapplication.domain.Tour;
import com.example.bot.spring.webapplication.repos.ActivityRepo;
import com.example.bot.spring.webapplication.repos.TourRepo;

@Service
public class Double11Service {
	
	@Autowired
	TourRepo tourRepo;
	
	@Autowired
	ActivityRepo activityRepo;

	public LinkedList<Activity> getAllActivities() throws Exception{
		return activityRepo.getAllActivities();
	}
	
	public LinkedList<Tour> getAllTours() throws Exception{
		return tourRepo.getAllTours();
	}
	
	public void createNewActivity(Activity act) throws Exception{
		activityRepo.createNewActivity(act);
	}
	
}
