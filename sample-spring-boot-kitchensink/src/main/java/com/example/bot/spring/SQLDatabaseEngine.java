package com.example.bot.spring;

import lombok.extern.slf4j.Slf4j;
import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.*;
import java.net.URISyntaxException;
import java.net.URI;

@Slf4j
public class SQLDatabaseEngine extends DatabaseEngine {
	@Override
	String search(String text) throws Exception {
		String result = "";
		try {
			//Write your code here
			System.out.println("Entered search: " + text);
			Connection connection = getConnection(); 
			System.out.println("After getConnection()");
			PreparedStatement stmt = connection.prepareStatement("select response from chatbot where keyword = '" + text +"'");
			System.out.println("After prepareStatement()");
			//stmt.setString(1, text);
			
			ResultSet rs = stmt.executeQuery();
			
			System.out.println("response: " + rs.getString(1) );
			result = rs.getString(1);
			
			rs.close();
			stmt.close();
			connection.close();
			
		} catch (RuntimeException e) {
			System.out.print("RuntimeException: ");
	        System.out.println(e.getMessage());
		} catch (Exception e) {
			System.out.print("Exception: ");
	        System.out.println(e.getMessage());
		}
		return result;
	}
	
	
	private Connection getConnection() throws URISyntaxException, SQLException {
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
