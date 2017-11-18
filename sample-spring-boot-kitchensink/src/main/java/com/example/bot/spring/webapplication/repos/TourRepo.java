package com.example.bot.spring.webapplication.repos;

import java.util.LinkedList;

import org.springframework.stereotype.Repository;

import com.example.bot.spring.database.WebAppDBEngine;
import com.example.bot.spring.webapplication.domain.Tour;

@Repository
public class TourRepo {

	public LinkedList<Tour> getAllTours() throws Exception {
		WebAppDBEngine webDB = new WebAppDBEngine();
		return webDB.getAllTourInfo();
	}

	public LinkedList<Tour> getGeneralTours() throws Exception {
		WebAppDBEngine webDB = new WebAppDBEngine();
		return webDB.getGeneralTourInfo();
	}

	public void addTour(Tour tour) throws Exception{
		WebAppDBEngine webDB = new WebAppDBEngine();
		webDB.addNewTour(tour);
	}
	
}
