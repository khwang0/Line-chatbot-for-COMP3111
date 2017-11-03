package com.example.bot.spring.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
		return allCus;
	}
}
