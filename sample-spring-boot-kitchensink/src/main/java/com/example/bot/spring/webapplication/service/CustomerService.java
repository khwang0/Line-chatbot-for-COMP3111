package com.example.bot.spring.webapplication.service;

import java.util.LinkedList;

import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Service;

import com.example.bot.spring.webapplication.domain.Customer;
import com.example.bot.spring.webapplication.domain.Tour;
import com.example.bot.spring.webapplication.repos.CustomerRepo;
import com.example.bot.spring.webapplication.repos.TourRepo;

@Service
public class CustomerService {
	
	@Autowired
	CustomerRepo customerRepo;
	
	@Autowired
	TourRepo tourRepo;

	public Customer addCustomer(Customer customer) throws Exception {
		customerRepo.addCustomer(customer);
		return customer;
	}

	public LinkedList<Customer> getCustomers() throws Exception {
		LinkedList<Customer> allCus = customerRepo.getAllCustomers();
		return allCus;
	}

	public LinkedList<Tour> getAllTours() throws Exception {
		LinkedList<Tour> tours = tourRepo.getAllTours();
		return tours;
	}

	public void updatePayment(Customer customer) throws Exception{
		customerRepo.updateCusomter(customer);
	}

}
