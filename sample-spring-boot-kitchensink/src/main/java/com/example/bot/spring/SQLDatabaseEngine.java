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
		//Write your code here
		Connection connection = getConnection();
		PreparedStatement stmt = connection.prepareStatement(
				"SELECT * FROM keyResponse");
		//stmt.setString(1, "vin");
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery();
		}catch(Exception e) {
			e.printStackTrace();
		}
		String response = null;
		while (rs.next()) {
			String key = rs.getString(1);
			String res = rs.getString(2);
			int hits = rs.getInt(3);
			if((text.toLowerCase()).contains(key.toLowerCase())) {
				PreparedStatement nstmt = connection.prepareStatement(
						"UPDATE keyResponse "
						+ "SET hits = ? "
						+ "WHERE keyword = ?");
				nstmt.setInt(1, hits+1);
				nstmt.setString(2, key);
				try {
					nstmt.executeUpdate();
				}catch(Exception e) {
					e.printStackTrace();
				}
				response = res;
				break;
			}
		}
		rs.close();
		stmt.close();
		connection.close();
		if(response != null) {
			return response;
		}else {
			throw new Exception("NOT FOUND");
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
