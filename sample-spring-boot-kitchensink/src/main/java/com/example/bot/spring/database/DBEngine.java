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
					"UPDATE line_user_info SET "+entryName+" = ? WHERE userid = ?");
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
			stmt.setString(1, entryName);
			stmt.setString(2, userID);
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
