package com.example.bot.spring.database;

import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;

import com.linecorp.bot.model.event.message.TextMessageContent;

public class BookingDBEngine extends DBEngine {
	
	private static final String OFFERTABLE = "tour_tourOffer_relation";
	private static final String CUSTOMER = "customer_info";
	private static final String LINEUSER = "line_user_info";
	private static final String PRICE = "tour_price";
	private static final String DESCRIPTION = "tour_description";
	private static final String TOURINFO = "tour_info";
	private static final String BOOKTABLE = "booking_table";
	
	private Connection connection = null;

	public BookingDBEngine() {
		// TODO Auto-generated constructor stub
	}
	
	/* Record the specified date for the tour
	 * 
	 */
	public void recordDate(String userId, int dd, int mm) {
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
			e.printStackTrace();
		}
		try {
			while(rs.next()) {
				tourId = rs.getString(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if(tourId == null) {
			try {
				throw new Exception("Unknown error");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else {
			if(mm<10 && dd>=10) {
				tourId = tourId+"20170"+mm+dd;
			}else if(mm<10 && dd<10) {
				tourId = tourId+"20170"+mm+"0"+dd;
			}else if(mm>=10 && dd<10) {
				tourId = tourId+"2017"+mm+"0"+dd;
			}else {
				tourId = tourId+"2017"+mm+dd;
			}
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
				e.printStackTrace();
			}
		}
		try {
			rs.close();
			nstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/* Record the number of adults in the tour
	 * 
	 */
	public void recordAdults(String userId, int i) {
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
			e.printStackTrace();
		}
	}

	/* Record the number of children in the tour
	 * 
	 */
	public void recordChildren(String userId, int i){
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
			e.printStackTrace();
		}
	}

	/* Record the number of toddlers in the tour
	 * 
	 */
	public void recordToddler(String userId, int i) {
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
			e.printStackTrace();
		}
	}

	/* Record the phone number of user accordingly
	 * 
	 */
	public void recordPhone(String userId, int i) {
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
			e.printStackTrace();
		}
	}

	/* Set the status in the flow of booking for a given user id
	 * 
	 */
	public void setStatus(String status, String userId){
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
			e.printStackTrace();
		}
	}
	
	/* Check if the date is a valid one for a given tour
	 * 
	 */
	public int checkValidDate(int dd, int mm, String userId) {
		PreparedStatement nstmt;
		try {
			nstmt = connection.prepareStatement(
					"SELECT o.bootableid, b.tourcapcity-b.registerednum "
					+ "FROM ? o, ? l, ? b"
					+ "WHERE o.tourid = l.tourids"
					+ "AND o.bootableid = b.bootableid"
					+ "AND l.userID = ?");
			nstmt.setString(1, OFFERTABLE);
			nstmt.setString(2, LINEUSER);
			nstmt.setString(3, BOOKTABLE);
			nstmt.setString(4, userId);
			ResultSet rs = this.query(nstmt);
			while(rs.next()) {
				String offerId = rs.getString(1);
				int quota = rs.getInt(2);
				if(offerId!=null&&offerId!="") {
					String date = offerId.substring(9);
					int d = Integer.parseInt(date.substring(0,2));
					int m = Integer.parseInt(date.substring(2));
					if(d==dd && m==mm && quota > 0) {
						nstmt.close();
						rs.close();
						return quota;
					}
				}
			}
			nstmt.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	/* Start a new booking request from user
	 * 
	 */
	public String createNewBooking(String userId, String name) {
		// TODO Auto-generated method stub
		
		return null;
	}
	
	/* Set the status of booking flow for a given userid
	 * 
	 */
	public String getStatus(String userId){
		String status = null;
		PreparedStatement nstmt;
		try {
			nstmt = connection.prepareStatement(
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
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return status;
	}
	
	/* Get possible tour ids for one user
	 * 
	 */
	public String[] getTourIds(String userId) {
		String ids = null;
		String[] allId = null;
		PreparedStatement nstmt;
		try {
			nstmt = connection.prepareStatement(
					"SELECT state "
					+ "FROM ?"
					+ "WHERE userID = ?");
			nstmt.setString(1, LINEUSER);
			nstmt.setString(2, userId);
			ResultSet rs = this.query(nstmt);
			while(rs.next()) {
				ids = rs.getString(1);
			}
			if(ids != null) {
				allId = ids.split(",");
			}
			nstmt.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return allId;
	}

	/* Get the number of adults
	 * 
	 */
	public int getAdult(String userId) {
		PreparedStatement nstmt = null;
		int adult = -1;
		try {
			nstmt = connection.prepareStatement(
					"SELECT c.adult "
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
			e.printStackTrace();
		}
		return adult;
	}

	/* Get the number of toddlers
	 * 
	 */
	public int getToddler(String userId) {
		PreparedStatement nstmt = null;
		int toddler = -1;
		try {
			nstmt = connection.prepareStatement(
					"SELECT c.toddler "
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
			e.printStackTrace();
		}
		return toddler;
	}

	/* Get the number of children
	 * 
	 */
	public int getChildren(String userId) {
		PreparedStatement nstmt = null;
		int children = -1;
		try {
			nstmt = connection.prepareStatement(
					"SELECT c.children "
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
			e.printStackTrace();
		}
		return children;
	}

	/* Get the id of the tour for one user
	 * 
	 */
	public String getTourJoined(String userId) {
		PreparedStatement nstmt = null;
		String tourId = null;
		try {
			nstmt = connection.prepareStatement(
					"SELECT c.bootableid "
					+ "FROM ? c, ? l"
					+ "WHERE c.name = l.name"
					+ "AND l.userID = ?");
			nstmt.setString(1, CUSTOMER);
			nstmt.setString(2, LINEUSER);
			nstmt.setString(3, userId);
			ResultSet rs = this.query(nstmt);
			while(rs.next()) {
				tourId = rs.getString(1);
			}
			rs.close();
			nstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return tourId;
	}

	/* Get the quota remaining for one tour
	 * 
	 */
	public int getQuota(String tourId) {
		int quota = -1;
		PreparedStatement nstmt;
		try {
			nstmt = connection.prepareStatement(
					"SELECT tourcapcity - registerednum "
					+ "FROM ?"
					+ "WHERE bootableid = ?");
			nstmt.setString(1, BOOKTABLE);
			nstmt.setString(2, tourId);
			ResultSet rs = this.query(nstmt);
			while(rs.next()) {
				quota = rs.getInt(1);
			}
			nstmt.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return quota;
	}

	/* Get the one-day price for a tour
	 * 
	 */
	public double getPrice(String tourId) {
		PreparedStatement nstmt = null;
		double price = 0;
		try {
			String field = null;
			String ts = tourId.substring(5);
			String id = tourId.substring(0, 5);
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
					"SELECT ? "
					+ "FROM ?"
					+ "WHERE tourid = ?");
			nstmt.setString(1, field);
			nstmt.setString(2, PRICE);
			nstmt.setString(3, id);
			ResultSet rs = this.query(nstmt);
			while(rs.next()) {
				price = rs.getInt(1)*1.0;
			}
			rs.close();
			nstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return price;
	}
	
	/* Get all possible tour IDs
	 * 
	 */
	public LinkedList<String> getAllTourIds() {
		LinkedList<String> tourIds = new LinkedList<String>();
		PreparedStatement nstmt;
		try {
			nstmt = connection.prepareStatement(
					"SELECT DISTINCT tourid"
					+ "FROM ?");
			nstmt.setString(1, TOURINFO);
			ResultSet rs = this.query(nstmt);
			while(rs.next()) {
				 tourIds.add(rs.getString(1));
			}
			nstmt.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return tourIds;
	}


	/* Get all possible tour names
	 * 
	 */
	public LinkedList<String> getAllTourNames() {
		LinkedList<String> tourNames = new LinkedList<String>();
		PreparedStatement nstmt;
		try {
			nstmt = connection.prepareStatement(
					"SELECT DISTINCT tour_name"
					+ "FROM ?");
			nstmt.setString(1, TOURINFO);
			ResultSet rs = this.query(nstmt);
			while(rs.next()) {
				 tourNames.add(rs.getString(1));
			}
			nstmt.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return tourNames;
	}
	
	/* Find the tour ID use tour name
	 * 
	 */
	public String findTourId(String tourName) {
		String tourId = null;
		PreparedStatement nstmt;
		try {
			nstmt = connection.prepareStatement(
					"SELECT DISTINCT tourid"
					+ "FROM ?"
					+ "WHERE tour_name = ?");
			nstmt.setString(1, TOURINFO);
			nstmt.setString(2, tourName);
			ResultSet rs = this.query(nstmt);
			while(rs.next()) {
				 tourId = rs.getString(1);
			}
			nstmt.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return tourId;
	}

	/* Remove a booking record from table
	 * 
	 */
	public void removeBooking(String userId) {
		// TODO Auto-generated method stub
		
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
	
	private void update(PreparedStatement nstmt) {
		try {
			nstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


}
