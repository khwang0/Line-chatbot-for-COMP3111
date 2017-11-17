package com.example.bot.spring.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;

import com.example.bot.spring.webapplication.domain.*;


public class WebAppDBEngine extends DBEngine {
	
	private Connection connection;
	
	public LinkedList<Customer> getAllCustomerInfo() throws Exception{
		connection = this.getConnection();
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
		LinkedList<Tour> allTours = new LinkedList<Tour>();
		PreparedStatement nstmt;
			nstmt = connection.prepareStatement(
					"SELECT  b.bootableid, i.tour_name, b.tourdate, b.tourguideid, b.hotel, b.tourcapcity, b.registerednum"
					+ " FROM booking_table b, tour_touroffer_relation o, tour_info i "
					+ " WHERE b.bootableid = o.bootableid"
					+ " AND i.tourid = o.tourid");
			ResultSet rs = nstmt.executeQuery();
			while(rs.next()) {
				Tour tour = new Tour();
				String tourId = rs.getString(1);
				String tourName = rs.getString(2);
				String tourDate = rs.getString(3);
				int tourGuideId = rs.getInt(4);
				String nameOfHotel = rs.getString(5);
				int tourCapacity = rs.getInt(6);
				int registeredNum = rs.getInt(7);
				tour.setNameOfHotel(nameOfHotel);
				tour.setRegisteredNum(registeredNum);
				tour.setTourCapacity(tourCapacity);
				tour.setTourDate(tourDate);
				tour.setTourGuideId(tourGuideId);
				tour.setTourId(tourId);
				tour.setTourName(tourName);
				allTours.add(tour);
			}
			nstmt.close();
			rs.close();
			connection.close();
			connection = null;
		return allTours;
	}

	public void addNewCustomer(Customer customer) throws Exception {
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
	
	public LinkedList<UQ>getUQs() throws Exception{
		connection = this.getConnection();
		PreparedStatement stmt;
		ResultSet rs;
		LinkedList<UQ> result = new LinkedList<UQ>(); 
	
		String statement = "SELECT * FROM unanswered_question ";
		stmt = connection.prepareStatement(statement);
		rs = stmt.executeQuery();			
		while (rs.next()) {
			String id = rs.getString(1); 
			String question = rs.getString(2);
			boolean answered = rs.getBoolean(3);
			if(!answered) {
				UQ uq = new UQ();
				uq.setId(id);
				uq.setQuestion(question);
				result.add(uq);
			}
		}
		
		rs.close();
		stmt.close();
		connection.close();
		connection = null;
		return result;
	}

	public void answerUQ(String question, String id, String answer) throws Exception {
		connection = this.getConnection();
		PreparedStatement nstmt;
		nstmt = connection.prepareStatement(
				"UPDATE unanswered_question"
				+ " SET answer = ?, answered_or_not = true"
				+ " WHERE id = ? AND question = ?");
	
		nstmt.setString(1, answer);
		nstmt.setString(2, id);
		nstmt.setString(3, question);
		nstmt.executeUpdate();
		nstmt.close();
		connection.close();
		connection = null;
	}
	
}
