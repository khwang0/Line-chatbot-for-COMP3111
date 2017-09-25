 package com.example.bot.spring;

import lombok.extern.slf4j.Slf4j;
import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.*;
import java.net.URISyntaxException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;

@Slf4j
public class SQLDatabaseEngine extends DatabaseEngine {
	@Override
	String search(String text) throws Exception {
		try {
			Connection connection = getConnection();
			PreparedStatement stmt = connection.prepareStatement("Select id, keyword, response FROM lab where keyword like concat('%', ?, '%')");
			stmt.setString(1, "vin");
			ResultSet rs = stmt.excuteQuery();
			while(rs.next()) {
				System.out.println("ID: " + rs.getInt(1) + "\tKeyword: " + rs.getString(2) + "\tResponse: " + rs.getString(3));
			}
			rs.close();
			stmt.close();
			connection.close();
		} catch (Exception e) {
			System.out.println(e);
		}
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
