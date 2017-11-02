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
	
	protected Connection getConnection() throws URISyntaxException, SQLException {
		Connection connection;
		//URI dbUri = new URI(System.getenv("DATABASE_URL"));
		URI dbUri = new URI("postgres://wwwbvxwsfefwqo:eb8451f02c101b8722c2ff3222814acae1a53125d0a3207dc871a5d23def557d@ec2-50-19-218-160.compute-1.amazonaws.com:5432/d6p7sk8brnkcvl");

		String username = dbUri.getUserInfo().split(":")[0];
		String password = dbUri.getUserInfo().split(":")[1];
		String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath() +  "?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";

		//log.info("Username: {} Password: {}", username, password);
		//log.info ("dbUrl: {}", dbUrl);
		
		connection = DriverManager.getConnection(dbUrl, username, password);

		return connection;
	}
}
