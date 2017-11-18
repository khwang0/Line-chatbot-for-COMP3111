package com.example.bot.spring.database;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DBEngine {
	
	private static final String CLASSIFYTABLE = "classify_table";
	
	private static final String CUSTOMER = "customer_info";
	private static final String LINEUSER = "line_user_info";
	private static final String PRICE = "tour_price";
	private static final String DESCRIPTION = "tour_description";
	private static final String TOURINFO = "tour_info";
	private static final String BOOKTABLE = "booking_table";
	
	public DBEngine() {
		
	}
	
	public void updateLineUserInfo(String userID,String entryName,String value) throws Exception{
		Connection connection= getConnection();
		PreparedStatement stmt;
		try {
			getLineUserInfo(userID,entryName);
		}catch(Exception e) {
			if(e.getMessage().equals("No such entry")) {
				stmt=connection.prepareStatement("INSERT INTO line_user_info VALUES ( ? ,'','','','','','')");
				stmt.setString(1,userID);
				stmt.executeUpdate();
				stmt.close();
			}
			else throw new Exception("Wrong Command1");
		}
		try {
			stmt = connection.prepareStatement(
					"UPDATE " + LINEUSER + " SET "+ entryName +" = ? WHERE userid = ?");
			stmt.setString(1, value);
			stmt.setString(2, userID);
			stmt.executeUpdate();
		}catch(Exception e) {
			throw new Exception("Wrong Command2");
		}
		stmt.close();
		connection.close();
	}
	
	public String getLineUserInfo(String userID,String entryName) throws Exception{
		Connection connection= getConnection();
		PreparedStatement stmt;
		ResultSet rs;
		try{
			stmt = connection.prepareStatement(
					"SELECT "+entryName+" FROM line_user_info WHERE userid = ?");
			stmt.setString(1, userID);
			rs=stmt.executeQuery();
		}catch(Exception e){
			System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
			System.out.println("with userid "+userID);
			e.printStackTrace();
			throw new Exception("Wrong Command1");
		}
		if(!rs.next()) { 
			System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
			System.out.println("No such entry!!!!!");
			throw new Exception("No such entry");
		}
		String tmp=rs.getString(1);
		rs.close();
		stmt.close();
		connection.close();
		return tmp;
	}
	
	/**
	 * given a pure text, check the 'classify_table' in database to determine its type; 
	 * return: 'book'/'reco'/'gq'/'none'
	 * */
	public String getTextType(String text) {	
		if(text == null || text.equals("")) {
			return "";
		}		
		
		Connection connection = null; 
		PreparedStatement stmt  = null;
		ResultSet rs = null;		// string(1): keywords; string(2): position; string(3): label; 		
				
		// classify input according to classify_table: get returned type; 
		// if there r multiple types;
		// check: label(diff with previous label) && position(correct position)  && length of keywords (choose the label to the longer keywords)
	
		String type = "none";	 // type to return, with default value to be "none"
		String selectedKey = ""; // corresponding key of selected type; 
		
		String keywords = "";	 // keyword for cur entry
		String position = "";	 // position for cur entry
		String label = "";		 // label for cur entry
		try{
			connection = getConnection();
			String statement = "SELECT * FROM " +  CLASSIFYTABLE + " WHERE '" + text + "' LIKE concat('%', keywords, '%')";		
			stmt = connection.prepareStatement(statement);
			rs=stmt.executeQuery();

			System.err.println(statement);			
			
			while (rs.next()) {				
				label = rs.getString(3);
				System.err.println("inside rs: lable: " + label + " current type: " + type);
				if(!type.equals(label)) {	// check if rs.label euqals the selectedLabel					
					keywords = rs.getString(1);
					position = rs.getString(2);	
					System.err.println("label : " + label + " keywords: " + keywords + " position: " + position);
					if(!wrongPosition(keywords, text, position)) { // if no: check position;
						// if no: check keywords length:
						// if current keyword is longer: replce selectedKey with currentKey; 
						if(keywords.length() > selectedKey.length()) {	
							System.err.println("replacing : " + type + " with " + label + " 'cause length");
							type = label;
							selectedKey = keywords;
							
							// reset keywords,  position, label
							keywords = "";
							position = "";
							label = "";
													
						}else if(label.length() == type.length()) { // if same, shuffle		
							// no action for now; 
						}
					}
				}				
			}
		}catch(Exception e){
			System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
			System.out.println("-- inside DBENGINE: getTextType --");
			e.printStackTrace();
		}finally {
			try {
				rs.close();
				stmt.close();
				connection.close();
			}catch (Exception e2) {
				System.err.println(e2.getMessage());
			}
		}
		return type;		
	}
	
	private boolean wrongPosition(String key, String message, String position) {
		boolean positionWrong = true;
		
		switch (position) {
		case "front":
			if(key.equals(message.substring(0, key.length())))
				positionWrong = false;
			break;
		case "end":
	        int lenMsg = message.length();
	        int lenKey = key.length();
	        int startIndex = lenMsg - lenKey;
	        if(key.equals(message.substring(startIndex)))
	        	positionWrong = false;
	        break; 
		case "any":
			positionWrong = false;
			break;
		default: 
			// no action; 
		}
		return positionWrong;
	}
	
	protected Connection getConnection() throws URISyntaxException, SQLException {
		Connection connection;
		URI dbUri = new URI(System.getenv("DATABASE_URL"));

		String username = dbUri.getUserInfo().split(":")[0];
		String password = dbUri.getUserInfo().split(":")[1];
		String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath() +  "?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";

		log.info("Username: {} Password: {}", username, password);
		log.info ("dbUrl: {}", dbUrl);
		
		connection = DriverManager.getConnection(dbUrl, username, password);

		return connection;
	}
}
