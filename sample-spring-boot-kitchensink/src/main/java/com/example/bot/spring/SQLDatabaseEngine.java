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
		String result=null;
		try {
			Connection con = getConnection();
			PreparedStatement smt = con.prepareStatement("SELECT responses1 FROM responses where keyword like concat('%',?,'%')");
			smt.setString(1, text);
			ResultSet rs = smt.executeQuery();
			while(rs.next())
			{
				result = rs.getString("responses1");
			}
			rs.close();
			smt.close();
			con.close();
		}catch (Exception e) {
			System.out.println(e);
		}
		if(result!=null)
			return result;
		throw new Exception("NOT FOUND");
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

