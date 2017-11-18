package com.example.bot.spring.database;

import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;


public class ConfirmDBEngine extends DBEngine {
	private Connection connection;
	
	public ConfirmDBEngine() {
		connection = null;
	}
	
	public void openConnection() {
		try {
			connection = this.getConnection();
		} catch (URISyntaxException | SQLException e) {
			e.printStackTrace();
		}
	}

	public void close() {
		try {
			connection.close();
			connection = null;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private ResultSet query(PreparedStatement nstmt) {
		ResultSet rs = null;
		try {
			rs = nstmt.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
	}
	
	// functions for confirmation 
	// return all tour whose tourist number > min && not yet been confirmed; 
	public List<String> getAllUnconfirmedTours(boolean fullfilled){
		List<String> unconfirmed_tours = new ArrayList<String>();
		PreparedStatement nstmt = null;
		
		openConnection();
		// if fullfillled, retrieve all tour which can be confirmed
		// else, retrieve all tour which should be cancelled; 
		String statement = "";
		if (fullfilled) {
			statement = "SELECT bootableid FROM booking_table "
					+ "WHERE paidnum >= mintourist AND confirmed = 'unconfirmed' ";
			// use paidnum rather than registernum; 			
		}else {
			statement = "SELECT bootableid FROM booking_table "
					+ "WHERE paidnum < mintourist AND confirmed = 'unconfirmed' ";
		}

		try {
			nstmt = connection.prepareStatement(statement);
			ResultSet rs = this.query(nstmt);
			
			while(rs.next()) {
				unconfirmed_tours.add(rs.getString(1));
			}
			nstmt.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		close();	
		
		return unconfirmed_tours;
	}

	public Set<String> getAllContactors(String booktableid){
		Set<String> customers = new HashSet<String>();
		PreparedStatement nstmt = null;	
		openConnection();
		
		String statement = "SELECT L.userid "
				+ "FROM customer_info as C, line_user_info as L "
				+ "WHERE C.paidamount >= C.tourfee AND C.customername = L.name AND C.bootableid = ?";
		// only announce the tourist who paid the full tour fee; 
		try {
			nstmt = connection.prepareStatement(statement);			
			nstmt.setString(1,booktableid);			
			ResultSet rs = this.query(nstmt);	
			
			while(rs.next()) {
				customers.add(rs.getString(1));
			}
			nstmt.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		close();	
		
		return customers;
	}
	
	public void updateConfirmedTours(String booktableid){	
		PreparedStatement nstmt = null;	
		openConnection();		
		String statement = "UPDATE TABLE booking_table "
				+ "SET confirmed = 'confirmed' "
				+ "WHERE bootableid = ?";
		try {
			nstmt = connection.prepareStatement(statement);			
			nstmt.setString(1,booktableid);			
			
			ResultSet rs = this.query(nstmt);	
			
			nstmt.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		close();	
		
	}

}
