package com.example.bot.spring.webapplication.service;

import java.util.LinkedList;

import com.example.bot.spring.webapplication.domain.Tour;
import com.example.bot.spring.webapplication.repos.TourRepo;

public class TourService {
	
	TourRepo tourRepo = new TourRepo();

	public LinkedList<Tour> getTours() throws Exception {
		LinkedList<Tour> tours = tourRepo.getAllTours();
		return tours;
	}

}
