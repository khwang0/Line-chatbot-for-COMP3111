package com.example.bot.spring.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;

import com.example.bot.spring.webapplication.domain.*;

public class WebAppDBEngine extends DBEngine {
	
	private Connection connection;
	
	public LinkedList<Customer> getAllCustomerInfo() throws Exception{
		connection = this.getConnection();
		System.out.println(connection.getCatalog());
		LinkedList<Customer> allCus = new LinkedList<Customer>();
		PreparedStatement nstmt;
			nstmt = connection.prepareStatement(
					"SELECT * "
					+ " FROM customer_info");
			ResultSet rs = nstmt.executeQuery();
			while(rs.next()) {
				Customer cus = new Customer();
				String name = rs.getString(2);
				String phone = rs.getString(3);
				String bootableid = rs.getString(5);
				int adults = rs.getInt(6);
				int children = rs.getInt(7);
				int toddler = rs.getInt(8);
				double totalPrice = rs.getDouble(9);
				double pricePaid = rs.getDouble(10);
				String special = rs.getString(11);
				cus.setAdults(adults);
				cus.setBootableId(bootableid);
				cus.setChildren(children);
				cus.setName(name);
				cus.setPhone(phone);
				cus.setSpecial(special);
				cus.setToddler(toddler);
				cus.setPricePaid(pricePaid);
				cus.setTotalPrice(totalPrice);
				allCus.add(cus);
			}
			nstmt.close();
			rs.close();
			connection.close();
			connection = null;
		return allCus;
	}
	
	public LinkedList<Tour> getAllTourInfo() throws Exception{
		connection = this.getConnection();
		System.out.println(connection.getCatalog());
		LinkedList<Tour> allTours = new LinkedList<Tour>();
		PreparedStatement nstmt;
			nstmt = connection.prepareStatement(
					"SELECT * "
					+ " FROM booking_table");
			ResultSet rs = nstmt.executeQuery();
			while(rs.next()) {
				Tour tour = new Tour();
				String name = rs.getString(2);
				String phone = rs.getString(3);
				String bootableid = rs.getString(5);
				int adults = rs.getInt(6);
				int children = rs.getInt(7);
				int toddler = rs.getInt(8);
				double totalPrice = rs.getDouble(9);
				double pricePaid = rs.getDouble(10);
				String special = rs.getString(11);
				allTours.add(tour);
			}
			nstmt.close();
			rs.close();
			connection.close();
			connection = null;
		return allTours;
	}

	public void addNewCustomer(Customer customer) throws Exception {
		// TODO Auto-generated method stub
		connection = this.getConnection();
		String name = customer.getName();
		String phone = customer.getPhone();
		String bootableid = customer.getBootableId();
		int adults = customer.getAdults();
		int children = customer.getChildren();
		int toddler = customer.getToddler();
		String special = customer.getSpecial();
		PreparedStatement nstmt = null;
		double price = 0;
		String field = null;
		String ts = bootableid.substring(5);
		String id = bootableid.substring(0, 5);
		int year = Integer.parseInt(ts.substring(0, 4));
		int month = Integer.parseInt(ts.substring(4, 6));
		int day = Integer.parseInt(ts.substring(6, 8));
		Calendar cal = new GregorianCalendar(year, month - 1, day);
		int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
		if(Calendar.SUNDAY == dayOfWeek || Calendar.SATURDAY == dayOfWeek) {
			field = "weekend_price";
		}else {
		   	field = "weekday_price";
		}
		nstmt = connection.prepareStatement(
				"SELECT "+field
				+ " FROM tour_price"
				+ " WHERE tourid = ?");
		nstmt.setString(1, id);
		ResultSet rs = nstmt.executeQuery();
		while(rs.next()) {
			price = rs.getDouble(1);
		}
		rs.close();
		nstmt.close();
		double totalPrice = price*adults+price*0.8*children;
		nstmt = connection.prepareStatement(
				"SELECT");
		nstmt = connection.prepareStatement(
				"INSERT INTO customer_info"
				+ " VALUES (0,?,?,0,?,?,?,?,?,0,?) ");
		nstmt.setString(1, name);
		nstmt.setString(2, phone);
		nstmt.setString(3,bootableid);
		nstmt.setInt(4, adults);
		nstmt.setInt(5, children);
		nstmt.setInt(6, toddler);
		nstmt.setDouble(7, totalPrice);
		nstmt.setString(8, special);
		nstmt.execute();
		nstmt.close();
		connection.close();
		connection = null;
	}
}
