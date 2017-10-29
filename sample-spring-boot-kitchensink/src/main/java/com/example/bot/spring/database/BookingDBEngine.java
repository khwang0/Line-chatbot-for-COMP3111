package com.example.bot.spring.database;

import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.linecorp.bot.model.event.message.TextMessageContent;

public class BookingDBEngine extends DBEngine {
	
	private static final String OFFERTABLE = "tour_tourOffer_relation";
	private static final String CUSTOMER = "customer_info";
	private static final String LINEUSER = "line_user_info";
	private static final String PRICE = "tour_price";
	private static final String DESCRIPTION = "tour_description";
	
	private Connection connection = null;

	public BookingDBEngine() {
		// TODO Auto-generated constructor stub
	}
	
	/* Set the status of booking flow for a given userid
	 * 
	 */
	public String getStatus(String userId) throws Exception {
		// TODO Auto-generated method stub
		String status = null;
		PreparedStatement nstmt = connection.prepareStatement(
				"SELECT state "
				+ "FROM ?"
				+ "WHERE userID = ?");
		nstmt.setString(1, LINEUSER);
		nstmt.setString(2, userId);
		ResultSet rs = this.query(nstmt);
		while(rs.next()) {
			status = rs.getString(1);
		}
		nstmt.close();
		rs.close();
		return status;
	}
	
	/* Get possible tour ids for one user
	 * 
	 */
	public String[] getTourIds(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* Record the name of user
	 * 
	 *
	public void recordName(String userId, String t) {
		// TODO Auto-generated method stub
		
	}
	*/

	/* Record the specified date for the tour
	 * 
	 */
	public void recordDate(String userId, int dd, int mm) {
		// TODO Auto-generated method stub
		String tourId = null;
		PreparedStatement nstmt = null;
		ResultSet rs = null;
		try {
			nstmt = connection.prepareStatement(
					"SELECT tourIDs "
					+ "FROM ?"
					+ "WHERE userID = ?");
			nstmt.setString(1, LINEUSER);
			nstmt.setString(2, userId);
			rs = this.query(nstmt);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			while(rs.next()) {
				tourId = rs.getString(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(tourId == null) {
			try {
				throw new Exception("Unknown error");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else {
			tourId = tourId+"2017"+mm+dd;
			nstmt = null;
			try {
				nstmt = connection.prepareStatement(
						"UPDATE ? "
						+ "SET tourIDs = ?"
						+ "WHERE userID = ?");
			nstmt.setString(1,LINEUSER);
			nstmt.setString(2, tourId);
			nstmt.setString(3, userId);
			this.update(nstmt);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			rs.close();
			nstmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/* Record the number of adults in the tour
	 * 
	 */
	public void recordAdults(String userId, int i) {
		// TODO Auto-generated method stub
		PreparedStatement nstmt = null;
		try {
			nstmt = connection.prepareStatement(
					"UPDATE c "
					+ "SET c.adultnum = ?"
					+ "FROM ? c, ? l"
					+ "WHERE c.name = l.name"
					+ "AND l.userID = ?");
			nstmt.setInt(1, i);
			nstmt.setString(2, CUSTOMER);
			nstmt.setString(3, LINEUSER);
			nstmt.setString(4, userId);
			this.update(nstmt);
			nstmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/* Record the number of children in the tour
	 * 
	 */
	public void recordChildren(String userId, int i){
		// TODO Auto-generated method stub
		PreparedStatement nstmt = null;
		try {
			nstmt = connection.prepareStatement(
					"UPDATE c "
					+ "SET c.childnum = ?"
					+ "FROM ? c, ? l"
					+ "WHERE c.name = l.name"
					+ "AND l.userID = ?");
			nstmt.setInt(1, i);
			nstmt.setString(2, CUSTOMER);
			nstmt.setString(3, LINEUSER);
			nstmt.setString(4, userId);
			this.update(nstmt);
			nstmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/* Record the number of toddlers in the tour
	 * 
	 */
	public void recordToddler(String userId, int i) {
		// TODO Auto-generated method stub
		PreparedStatement nstmt = null;
		try {
			nstmt = connection.prepareStatement(
					"UPDATE c "
					+ "SET c.toodlernum = ?"
					+ "FROM ? c, ? l"
					+ "WHERE c.name = l.name"
					+ "AND l.userID = ?");
			nstmt.setInt(1, i);
			nstmt.setString(2, CUSTOMER);
			nstmt.setString(3, LINEUSER);
			nstmt.setString(4, userId);
			this.update(nstmt);
			nstmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/* Record the phone number of user accordingly
	 * 
	 */
	public void recordPhone(String userId, int i) {
		// TODO Auto-generated method stub
		PreparedStatement nstmt = null;
		try {
			nstmt = connection.prepareStatement(
					"UPDATE c "
					+ "SET c.phonenumber = ?"
					+ "FROM ? c, ? l"
					+ "WHERE c.name = l.name"
					+ "AND l.userID = ?");
			nstmt.setInt(1, i);
			nstmt.setString(2, CUSTOMER);
			nstmt.setString(3, LINEUSER);
			nstmt.setString(4, userId);
			this.update(nstmt);
			nstmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/* Find next unrecorded information field. If all information
	 * are recorded, return null
	 *
	public String findNextEmptyInfo(String userId) {
		// TODO Auto-generated method stub
		
		return null;
	}
	*/

	/* Set the status in the flow of booking for a given user id
	 * 
	 */
	public void setStatus(String status, String userId){
		// TODO Auto-generated method stub
		PreparedStatement nstmt = null;
		try {
			nstmt = connection.prepareStatement(
					"UPDATE ?"
					+ "SET state = ?"
					+ "WHERE userID = ?");
		
		nstmt.setString(1, LINEUSER);
		nstmt.setString(2, status);
		nstmt.setString(3, userId);
		this.update(nstmt);
		nstmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/* Check if the date is a valid one for a given tour
	 * 
	 */
	public boolean checkValidDate(int dd, int mm, String userId) {
		// TODO Auto-generated method stub
		return false;
	}

	/* Start a new booking request from user
	 * 
	 */
	public String createNewBooking(String userId, String name) {
		// TODO Auto-generated method stub
		return null;
	}

	/* Get the number of adults
	 * 
	 */
	public int getAdult(String userId) {
		// TODO Auto-generated method stub
		PreparedStatement nstmt = null;
		int adult = -1;
		try {
			nstmt = connection.prepareStatement(
					"SELECT adult "
					+ "FROM ? c, ? l"
					+ "WHERE c.name = l.name"
					+ "AND l.userID = ?");
			nstmt.setString(1, CUSTOMER);
			nstmt.setString(2, LINEUSER);
			nstmt.setString(3, userId);
			ResultSet rs = this.query(nstmt);
			while(rs.next()) {
				adult = rs.getInt(1);
			}
			rs.close();
			nstmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return adult;
	}

	/* Get the number of toddlers
	 * 
	 */
	public int getToddler(String userId) {
		// TODO Auto-generated method stub
		PreparedStatement nstmt = null;
		int toddler = -1;
		try {
			nstmt = connection.prepareStatement(
					"SELECT toddler "
					+ "FROM ? c, ? l"
					+ "WHERE c.name = l.name"
					+ "AND l.userID = ?");
			nstmt.setString(1, CUSTOMER);
			nstmt.setString(2, LINEUSER);
			nstmt.setString(3, userId);
			ResultSet rs = this.query(nstmt);
			while(rs.next()) {
				toddler = rs.getInt(1);
			}
			rs.close();
			nstmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return toddler;
	}

	/* Get the number of children
	 * 
	 */
	public int getChildren(String userId) {
		// TODO Auto-generated method stub
		PreparedStatement nstmt = null;
		int children = -1;
		try {
			nstmt = connection.prepareStatement(
					"SELECT children "
					+ "FROM ? c, ? l"
					+ "WHERE c.name = l.name"
					+ "AND l.userID = ?");
			nstmt.setString(1, CUSTOMER);
			nstmt.setString(2, LINEUSER);
			nstmt.setString(3, userId);
			ResultSet rs = this.query(nstmt);
			while(rs.next()) {
				children = rs.getInt(1);
			}
			rs.close();
			nstmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return children;
	}

	/* Get the id of the tour for one user
	 * 
	 */
	public String getTourJoined(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* Get the quota remaining for one tour
	 * 
	 */
	public int getQuota(String tourId) {
		// TODO Auto-generated method stub
		return 0;
	}

	/* Get the one-day price for a tour
	 * 
	 */
	public double getPrice(String tourId) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	/* Get all possible tour IDs
	 * 
	 */
	public String[] getAllTourIds() {
		// TODO Auto-generated method stub
		return null;
	}


	/* Get all possible tour names
	 * 
	 */
	public String[] getAllTourNames() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/* Find the tour ID use tour name
	 * 
	 */
	public String findTourId(String tourName) {
		// TODO Auto-generated method stub
		return null;
	}

	/* Remove a booking record from table
	 * 
	 */
	public void removeBooking(String userId) {
		// TODO Auto-generated method stub
		
	}

	public void openConnection() {
		// TODO Auto-generated method stub
		try {
			connection = this.getConnection();
		} catch (URISyntaxException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void close() {
		// TODO Auto-generated method stub
		try {
			connection.close();
			connection = null;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private ResultSet query(PreparedStatement nstmt) {
		ResultSet rs = null;
		try {
			rs = nstmt.executeQuery();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rs;
	}
	
	private void update(PreparedStatement nstmt) {
		try {
			nstmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}
