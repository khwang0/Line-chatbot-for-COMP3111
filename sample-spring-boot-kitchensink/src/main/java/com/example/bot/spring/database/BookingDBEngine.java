package com.example.bot.spring.database;

import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;

public class BookingDBEngine extends DBEngine {
	
	private static final String OFFERTABLE = "tour_tourOffer_relation";
	private static final String CUSTOMER = "customer_info";
	private static final String LINEUSER = "line_user_info";
	private static final String PRICE = "tour_price";
	private static final String DESCRIPTION = "tour_description";
	private static final String TOURINFO = "tour_info";
	private static final String BOOKTABLE = "booking_table";
	private static final String ASKKEYS = "askingkeys";
	private static final String CANCELKEYS = "cancelkeys";
	private static final String POSITIVEKEYS = "positivekeys";
	private static final String REPLIES = "replies";
	
	private Connection connection = null;

	public BookingDBEngine() {
	}
	
	/** Record the specified date for the tour
	 * 
	 * @param userId
	 * @param dd
	 * @param mm
	 */
	public void recordDate(String userId, int dd, int mm) {
		String tourId = null;
		PreparedStatement nstmt = null;
		ResultSet rs = null;
		try {
			nstmt = connection.prepareStatement(
					"SELECT tourIDs "
					+ " FROM "+LINEUSER
					+ " WHERE userID = ?");
			nstmt.setString(1, userId);
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
			this.setTourid(userId, tourId);
		}
		try {
			rs.close();
			nstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/** Record the number of adults in the tour
	 * 
	 * @param userId
	 * @param i
	 */
	public void recordAdults(String userId, int i) {
		PreparedStatement nstmt = null;
		try {
			nstmt = connection.prepareStatement(
					"UPDATE "+CUSTOMER
					+ " SET adultnum = ? "
					+ "FROM "+LINEUSER+" l "
					+ "WHERE customer_info.customername = l.name "
					+ "AND l.userID = ? "
					+ " AND l.tourids = customer_info.bootableid");
			nstmt.setInt(1, i);
			nstmt.setString(2, userId);
			this.update(nstmt);
			nstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/** Record the number of children in the tour
	 * 
	 * @param userId
	 * @param i
	 */
	public void recordChildren(String userId, int i){
		PreparedStatement nstmt = null;
		try {
			nstmt = connection.prepareStatement(
					"UPDATE "+CUSTOMER
					+ " SET childnum = ? "
					+ "FROM "+LINEUSER+" l "
					+ "WHERE customer_info.customername = l.name "
					+ "AND l.userID = ? "
					+ " AND l.tourids = customer_info.bootableid");
			nstmt.setInt(1, i);
			nstmt.setString(2, userId);
			this.update(nstmt);
			nstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/** Record the number of toddlers in the tour
	 * 
	 * @param userId
	 * @param i
	 */
	public void recordToddler(String userId, int i) {
		PreparedStatement nstmt = null;
		try {
			nstmt = connection.prepareStatement(
					"UPDATE "+CUSTOMER
					+ " SET toodlernum = ? "
					+ "FROM "+LINEUSER+" l "
					+ "WHERE customer_info.customername = l.name "
					+ "AND l.userID = ? "
					+ " AND l.tourids = customer_info.bootableid");
			nstmt.setInt(1, i);
			nstmt.setString(2, userId);
			this.update(nstmt);
			nstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/** Record the phone number of user accordingly
	 * 
	 * @param userId
	 * @param i
	 */
	public void recordPhone(String userId, long i) {
		PreparedStatement nstmt = null;
		try {
			nstmt = connection.prepareStatement(
					"UPDATE "+CUSTOMER
					+ " SET phonenumber = ? "
					+ "FROM "+LINEUSER+" l "
					+ "WHERE customer_info.customername = l.name "
					+ "AND l.userID = ? "
					+ " AND l.tourids = customer_info.bootableid");
			nstmt.setString(1, Long.toString(i));
			nstmt.setString(2, userId);
			this.update(nstmt);
			nstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/** Set the tour fee for a customer
	 * 
	 * @param totalPrice
	 * @param userId
	 */
	public void recordTotalPrice(double totalPrice, String userId) {
		PreparedStatement nstmt = null;
		try {
			nstmt = connection.prepareStatement(
					"UPDATE "+CUSTOMER
					+ " SET tourfee = ? "
					+ "FROM "+LINEUSER+" l "
					+ "WHERE customer_info.customername = l.name "
					+ "AND l.userID = ? "
					+ " AND l.tourids = customer_info.bootableid");
			nstmt.setDouble(1, totalPrice);
			nstmt.setString(2, userId);
			this.update(nstmt);
			nstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/** Set the status in the flow of booking for a given user id
	 * 
	 * @param status
	 * @param userId
	 */
	public void setStatus(String status, String userId){
		PreparedStatement nstmt = null;
		String cat = null;
		if(status == "default")
			cat = "default";
		else
			cat = "book";
		try {
			nstmt = connection.prepareStatement(
					"UPDATE "+LINEUSER
					+ " SET status = ?, categorization = ?"
					+ " WHERE userID = ?");
		
			nstmt.setString(1, status);
			nstmt.setString(2, cat);
			nstmt.setString(3, userId);
			this.update(nstmt);
			nstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/** Set the name of a user given userid
	 * 
	 * @param userId
	 * @param name
	 */
	private void setName(String userId, String name) {
		PreparedStatement nstmt = null;
		try {
			nstmt = connection.prepareStatement(
					"UPDATE "+LINEUSER
					+ " SET name = ?"
					+ " WHERE userID = ?");
		
			nstmt.setString(1, name);
			nstmt.setString(2, userId);
			this.update(nstmt);
			nstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/** Set the field tourids
	 * 
	 * @param tourId
	 */
	public void setTourid(String userId, String tourId) {
		PreparedStatement nstmt = null;
		try {
			nstmt = connection.prepareStatement(
					"UPDATE "+LINEUSER
					+ " SET tourIds = ?"
					+ " WHERE userID = ?");
		
			nstmt.setString(1, tourId);
			nstmt.setString(2, userId);
			this.update(nstmt);
			nstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/** Check if the date is a valid one for a given tour
	 * 
	 * @param dd
	 * @param mm
	 * @param userId
	 * @return
	 */
	public boolean checkValidDate(int dd, int mm, String userId) throws Exception{
		PreparedStatement nstmt;
		String offerId = null;
		try {
			nstmt = connection.prepareStatement(
					"SELECT o.bootableid, b.tourcapcity-b.registerednum "
					+ " FROM "+OFFERTABLE+" o, "+LINEUSER+" l, "+BOOKTABLE+" b"
					+ " WHERE o.tourid = l.tourids"
					+ " AND o.bootableid = b.bootableid"
					+ " AND l.userID = ?");
			nstmt.setString(1, userId);
			ResultSet rs = this.query(nstmt);
			int quota = -100;
			while(rs.next()) {
				offerId = rs.getString(1);
				if(!offerId.equals("")) {
					String date = offerId.substring(9);
					int m = Integer.parseInt(date.substring(0,2));
					int d = Integer.parseInt(date.substring(2));
					if(d==dd && m==mm) {
						quota = rs.getInt(2);
						break;
					}
				}
			}
			nstmt = connection.prepareStatement(
					"SELECT c.bootableid "
					+ " FROM "+CUSTOMER+" c, "+LINEUSER+" l"
					+ " WHERE c.customername = l.name"
					+ " AND l.userID = ?");
			nstmt.setString(1,userId);
			rs = this.query(nstmt);
			while(rs.next()) {
				if(rs.getString(1).equals(offerId))
					throw new Exception("REBOOK");
			}
			if(quota > 0) {
				nstmt.close();
				nstmt = null;
				nstmt = connection.prepareStatement(
						"SELECT bootableid"
						+ " FROM "+CUSTOMER+" c, "+LINEUSER+" l "
						+ " WHERE c.customername = l.name"
						+ " AND l.userid = ?");
				nstmt.setString(1, userId);
				rs.close();
				rs = null;
				rs = this.query(nstmt);
				while(rs.next()) {
					offerId = rs.getString(1);
					if(offerId!=null&&offerId!="") {
						String date = offerId.substring(9);
						int m = Integer.parseInt(date.substring(0,2));
						int d = Integer.parseInt(date.substring(2));
						if(dd == d && mm == m) {
							nstmt.close();
							rs.close();
							throw new Exception("OCCUPIED");
						}
						Calendar cal = new GregorianCalendar(2017,m-1,d);
						int len = Integer.parseInt(offerId.substring(0, 1));
						for(int i = 0; i < len; i++) {
							cal.add(Calendar.DATE, 1);
							if(dd == cal.get(Calendar.DATE) && mm == cal.get(Calendar.MONTH)) {
								nstmt.close();
								rs.close();
								throw new Exception("OCCUPIED");
							}
						}
					}
				}
				return true;
			}else if(quota == -100){
				nstmt.close();
				rs.close();
				throw new Exception("NO SUCH DATE");
			}else {
				nstmt.close();
				rs.close();
				throw new Exception("FULL");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/** Start a new booking request from user
	 * 
	 * @param userId
	 * @param name
	 * @return
	 */
	public String createNewBooking(String userId, String name) {
		PreparedStatement nstmt;
		String tourId = this.getTourIds(userId)[0];
		try {
			nstmt = connection.prepareStatement(
					"SELECT");
			nstmt = connection.prepareStatement(
					"INSERT INTO "+CUSTOMER
					+ " VALUES (0,?,'',0,?,0,0,0,0,0,'') ");
			nstmt.setString(1, name);
			nstmt.setString(2, tourId);
			this.execute(nstmt);
			this.setName(userId, name);
			nstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/** Get the user name given the userid
	 * 
	 * @param userId
	 * @return
	 */
	public String getName(String userId) {
		String name = "";
		PreparedStatement nstmt;
		try {
			nstmt = connection.prepareStatement(
					"SELECT name "
					+ " FROM "+ LINEUSER
					+ " WHERE userID = ?");
			nstmt.setString(1, userId);
			ResultSet rs = this.query(nstmt);
			while(rs.next()) {
				try {
				if(rs.getString(1).length()>=1) {
					name = rs.getString(1);
				}
				}catch(NullPointerException e) {
					return name;
				}
			}
			nstmt.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return name;
	}
	
	/** Set the status of booking flow for a given userid
	 * 
	 * @param userId
	 * @return
	 */
	public String getStatus(String userId){
		String status = "default";
		PreparedStatement nstmt;
		try {
			nstmt = connection.prepareStatement(
					"SELECT status "
					+ " FROM "+ LINEUSER
					+ " WHERE userID = ?");
			nstmt.setString(1, userId);
			ResultSet rs = this.query(nstmt);
			while(rs.next()) {
				try {
					if(!(rs.toString().length()==0))
						status = rs.getString(1);
				}catch(NullPointerException e) {
					break;
				}
			}
			nstmt.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return status;
	}
	
	/** Get possible tour ids for one user
	 * 
	 * @param userId
	 * @return
	 */
	public String[] getTourIds(String userId) {
		String ids = null;
		String[] allId = null;
		PreparedStatement nstmt;
		try {
			nstmt = connection.prepareStatement(
					"SELECT tourids "
					+ " FROM " + LINEUSER
					+ " WHERE userID = ?");
			nstmt.setString(1, userId);
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

	/** Get the number of adults
	 * 
	 * @param userId
	 * @return
	 */
	public int getAdult(String userId) {
		PreparedStatement nstmt = null;
		int adult = -1;
		try {
			nstmt = connection.prepareStatement(
					"SELECT c.adultnum "
					+ " FROM "+CUSTOMER+ " c, "+LINEUSER+" l"
					+ " WHERE c.customername = l.name"
					+ " AND l.userID = ?"
					+ " AND l.tourids = c.bootableid");
			nstmt.setString(1, userId);
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

	/** Get the number of toddlers
	 * 
	 * @param userId
	 * @return
	 */
	public int getToddler(String userId) {
		PreparedStatement nstmt = null;
		int toddler = -1;
		try {
			nstmt = connection.prepareStatement(
					"SELECT c.toodlernum "
					+ " FROM "+CUSTOMER+" c, "+LINEUSER+" l"
					+ " WHERE c.customername = l.name"
					+ " AND l.userID = ?"
					+ " AND l.tourids = c.bootableid");
			nstmt.setString(1, userId);
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

	/** Get the number of children
	 * 
	 * @param userId
	 * @return
	 */
	public int getChildren(String userId) {
		PreparedStatement nstmt = null;
		int children = -1;
		try {
			nstmt = connection.prepareStatement(
					"SELECT c.childnum "
					+ " FROM "+CUSTOMER+" c, "+LINEUSER+" l"
					+ " WHERE c.customername = l.name"
					+ " AND l.userID = ?"
					+ " AND l.tourids = c.bootableid");
			nstmt.setString(1, userId);
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

	/** Get the quota remaining for one tour
	 * 
	 * @param tourId
	 * @return
	 */
	public int getQuota(String tourId) {
		int quota = -1;
		PreparedStatement nstmt;
		try {
			nstmt = connection.prepareStatement(
					"SELECT tourcapcity - registerednum "
					+ " FROM "+BOOKTABLE
					+ " WHERE bootableid = ?");
			nstmt.setString(1, tourId);
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

	/** Get the one-day price for a tour
	 * 
	 * @param tourId
	 * @return
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
					"SELECT "+field
					+ " FROM "+PRICE
					+ " WHERE tourid = ?");
			nstmt.setString(1, id);
			ResultSet rs = this.query(nstmt);
			while(rs.next()) {
				price = rs.getDouble(1);
			}
			rs.close();
			nstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return price;
	}
	
	/** Get all possible tour IDs
	 * 
	 * @return
	 */
	public LinkedList<String> getAllTourIds() {
		LinkedList<String> tourIds = new LinkedList<String>();
		PreparedStatement nstmt;
		try {
			nstmt = connection.prepareStatement(
					"SELECT DISTINCT tourid"
					+ " FROM "+TOURINFO);
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


	/** Get all possible tour names
	 * 
	 * @return
	 */
	public LinkedList<String> getAllTourNames() {
		LinkedList<String> tourNames = new LinkedList<String>();
		PreparedStatement nstmt;
		try {
			nstmt = connection.prepareStatement(
					"SELECT DISTINCT tour_name"
					+ " FROM "+TOURINFO);
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
	
	/** Get all tour information and store them in a linked list
	 *  In the order of name >> description >>  weekday fee >> weekend fee
	 * @param tourId
	 * @return
	 */
	public LinkedList<String> getTourInfos(String tourId) {
		LinkedList<String> allInfos = new LinkedList<String>();
		PreparedStatement nstmt;
		try {
			nstmt = connection.prepareStatement(
					"SELECT i.tour_name,d.description,p.weekday_price,p.weekend_price"
					+ " FROM "+TOURINFO+" i, "+PRICE+" p, "+DESCRIPTION+" d"
					+ " WHERE i.tourid = ?"
					+ " AND i.tourid = d.tourid"
					+ " AND p.tourid = i.tourid");
			nstmt.setString(1, tourId);
			ResultSet rs = this.query(nstmt);
			while(rs.next()) {
				 allInfos.add(rs.getString(1));
				 allInfos.add(rs.getString(2));
				 allInfos.add(Integer.toString(rs.getInt(3)));
				 allInfos.add(Integer.toString(rs.getInt(4)));
			}
			nstmt.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return allInfos;
	}
	

	/** Get all possible departure dates of one tour
	 * 
	 * @param tourId
	 * @return
	 */
	public String getAllDates(String tourId) {
		PreparedStatement nstmt;
		String allDates = "";
		try {
			nstmt = connection.prepareStatement(
					"SELECT o.bootableid "
					+ " FROM "+OFFERTABLE+" o, "+BOOKTABLE+" b"
					+ " WHERE o.bootableid = b.bootableid"
					+ " AND o.tourid = ?"
					+ " AND b.registerednum < b.tourcapcity");
			nstmt.setString(1, tourId);
			ResultSet rs = this.query(nstmt);
			while(rs.next()) {
				String offerId = rs.getString(1);
				String date = offerId.substring(9); // parse date from the last 4 digit of offerID?
				// <<<<<<<<<<<<<<<<<<<<<<<<<<<<<< ???? 
				if(!allDates.equals(""))
					allDates = allDates + "," + date;
				else
					allDates = allDates + date;
			}
			nstmt.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return allDates;
	}
	
	/** Find the tour ID use tour name
	 * 
	 * @param tourName
	 * @return
	 */
	public String findTourId(String tourName) {
		String tourId = null;
		PreparedStatement nstmt;
		try {
			nstmt = connection.prepareStatement(
					"SELECT DISTINCT tourid"
					+ " FROM "+TOURINFO
					+ " WHERE tour_name = ?");
			nstmt.setString(1, tourName);
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

	/** Remove a booking record of one user from table
	 * 
	 * @param userId
	 */
	public void removeBooking(String userId) {
		PreparedStatement nstmt;
		String name = this.getName(userId);
		try {
			nstmt = connection.prepareStatement(
					"DELETE FROM "+CUSTOMER
					+ " WHERE customername = ?");
			nstmt.setString(1, name);
			this.execute(nstmt);
			nstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/** Update the registered number of people after finishing a booking request
	 * 
	 * @param userId
	 */
	public void updateRegisteredNumber(String userId) {
		PreparedStatement nstmt = null;
		int num = 0;
		try {
			nstmt = connection.prepareStatement(
					"SELECT registerednum"
					+ " FROM "+BOOKTABLE+" b, "+CUSTOMER+" c, "+LINEUSER+" l"
					+ " WHERE c.customername = l.name "
					+ " AND l.userID = ?"
					+ " AND c.bootableid = l.tourids");
			nstmt.setString(1, userId);
			ResultSet rs = this.query(nstmt);
			while(rs.next()) {
				num = rs.getInt(1);
			}
			nstmt.close();
			nstmt = null;
			int adult = this.getAdult(userId);
			int child = this.getChildren(userId);
			int toddler = this.getToddler(userId);
			int total = adult + child + toddler;
			nstmt = connection.prepareStatement(
					"UPDATE "+BOOKTABLE
					+ " SET registerednum = ? "
					+ "FROM "+CUSTOMER+" c, "+LINEUSER+" l "
					+ "WHERE c.customername = l.name "
					+ "AND l.userID = ? "
					+ "AND booking_table.bootableid = l.tourids");
			nstmt.setInt(1, num+total);
			nstmt.setString(2, userId);
			this.update(nstmt);
			nstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/** Determine if a message is asking about something
	 * 
	 * @param msg
	 * @return
	 */
	public boolean detectAsk(String msg) {
		String key = null;
		PreparedStatement nstmt;
		try {
			nstmt = connection.prepareStatement(
					"SELECT keywords"
					+ " FROM "+ASKKEYS);
			ResultSet rs = this.query(nstmt);
			while(rs.next()) {
				 key = rs.getString(1);
				 if(msg.toLowerCase().contains(key.toLowerCase())) {
					 return true;
				 }
			}
			nstmt.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/** Determine if a message contains rejecting information
	 * 
	 * @param msg
	 * @return
	 */
	public boolean detectCancel(String msg) {
		String key = null;
		PreparedStatement nstmt;
		try {
			nstmt = connection.prepareStatement(
					"SELECT keywords"
					+ " FROM "+CANCELKEYS);
			ResultSet rs = this.query(nstmt);
			while(rs.next()) {
				 key = rs.getString(1);
				 if(msg.toLowerCase().contains(key.toLowerCase())) {
					 return true;
				 }
			}
			nstmt.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	

	/** Determine if a message contains positive information
	 * 
	 * @param msg
	 * @return
	 */
	public boolean detectPositive(String msg) {
		String key = null;
		PreparedStatement nstmt;
		try {
			nstmt = connection.prepareStatement(
					"SELECT keywords"
					+ " FROM "+POSITIVEKEYS);
			ResultSet rs = this.query(nstmt);
			while(rs.next()) {
				 key = rs.getString(1);
				 if(msg.toLowerCase().contains(key.toLowerCase())) {
					 return true;
				 }
			}
			nstmt.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/** Get the reply message from database tat is associated with the given tag
	 * 
	 * @param tag
	 * @return
	 */
	public String getReplyMessage(String tag) {
		String reply = null;
		PreparedStatement nstmt;
		try {
			nstmt = connection.prepareStatement(
					"SELECT reply"
					+ " FROM "+REPLIES
					+ " WHERE tag = ?");
			nstmt.setString(1, tag);
			ResultSet rs = this.query(nstmt);
			while(rs.next()) {
				reply = rs.getString(1);
				return reply;
			}
			nstmt.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
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
	
	private void execute(PreparedStatement nstmt) {
		try {
			nstmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
