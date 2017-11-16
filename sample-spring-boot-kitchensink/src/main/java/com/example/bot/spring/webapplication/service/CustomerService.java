package com.example.bot.spring.webapplication.service;

import java.util.LinkedList;

import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Service;

import com.example.bot.spring.webapplication.domain.Customer;
import com.example.bot.spring.webapplication.repos.CustomerRepo;

@Service
public class CustomerService {
	
	@Autowired
	CustomerRepo customerRepo;

	public Customer addCustomer(Customer customer) throws Exception {
		// TODO Auto-generated method stub
		customerRepo.addCustomer(customer);
		return customer;
	}

	public LinkedList<Customer> getCustomers() throws Exception {
		// TODO Auto-generated method stub
		LinkedList<Customer> allCus = customerRepo.getAllCustomers();
		return allCus;
	}

}
