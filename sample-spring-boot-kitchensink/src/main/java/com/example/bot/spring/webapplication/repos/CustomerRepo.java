package com.example.bot.spring.webapplication.repos;

import java.util.LinkedList;

import com.example.bot.spring.database.WebAppDBEngine;
import com.example.bot.spring.webapplication.domain.Customer;

public class CustomerRepo {

	public void addCustomer(Customer customer) throws Exception {
		// TODO Auto-generated method stub
		WebAppDBEngine webDB = new WebAppDBEngine();
		webDB.addNewCustomer(customer);
	}

	public LinkedList<Customer> getAllCustomers() throws Exception {
		// TODO Auto-generated method stub
		WebAppDBEngine webDB = new WebAppDBEngine();
		return webDB.getAllCustomerInfo();
	}
	 
}
