package com.example.bot.spring.webapplication.service;

import java.util.LinkedList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.bot.spring.webapplication.domain.Tour;
import com.example.bot.spring.webapplication.repos.TourRepo;

@Service
public class TourService {
	
	@Autowired
	TourRepo tourRepo;

	public LinkedList<Tour> getTours() throws Exception {
		LinkedList<Tour> tours = tourRepo.getAllTours();
		return tours;
	}

	public LinkedList<Tour> getGeneralTours() throws Exception {
		LinkedList<Tour> tours = tourRepo.getGeneralTours();
		return tours;
	}

	public void addTour(Tour tour) throws Exception {
		tourRepo.addTour(tour);
	}

}
