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

	Users searchUser(String uidkey) throws Exception {
		Users user = null;
		try {
			Connection connection = this.getConnection();
			PreparedStatement stmt = connection.prepareStatement(
					"SELECT * FROM users WHERE id=(?)");
			stmt.setString(1,uidkey);
			ResultSet rs = stmt.executeQuery();
            
			while(rs.next()) {
				user = new Users(rs.getString(1),rs.getString(2));
				user.setGender(rs.getString(3).charAt(0));
				user.setHeight(rs.getDouble(4));
				user.setWeight(rs.getDouble(5));
				user.setAge(rs.getInt(6));
			} 
			rs.close();
			stmt.close();
			connection.close();
		} catch (Exception e) {
			log.info(e.getMessage());
		}
		if(user != null)	{
			return user;
		}
		throw new Exception("NOT FOUND");
	}

	DetailedUser searchDetailedUser(Users user) throws Exception { //this contains bug
		DetailedUser newuser = null;
		try {
			Connection connection = this.getConnection();
			PreparedStatement stmt = connection.prepareStatement(
					"SELECT * FROM detailedusers WHERE id=(?)");
			stmt.setString(1,user.getID());
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next()) {	
				newuser = new DetailedUser(user);
				newuser.setExercise(rs.getInt(2)) ;
				newuser.setBodyFat(rs.getDouble(3)); 
				newuser.setCalories(rs.getInt(4));
				newuser.setCarbs(rs.getDouble(5)) ;
				newuser.setProtein(rs.getDouble(6));
				newuser.setVegfruit(rs.getDouble(7));
				newuser.setOtherInfo(rs.getString(9));
				Array sqlArray = rs.getArray(8);
				newuser.setEatingHabits((Boolean[])sqlArray.getArray());
				
			} 
			rs.close();
			stmt.close();
			connection.close();
		} catch (Exception e) {
			log.info(e.getMessage());
		}
		if(newuser != null)	{
			return newuser;
		}
		throw new Exception("NOT FOUND");
	}

	boolean pushUser(Users user) {
		boolean result = false;
		try {
			Connection connection = this.getConnection();
			PreparedStatement stmt = connection.prepareStatement(
					"INSERT INTO users VALUES(?,?,?,?,?,?)");
			stmt.setString(1, user.getID());
			stmt.setString(2, user.getName());
			String temp = ""+user.getGender();
			stmt.setString(3, temp) ;
			stmt.setDouble(4, user.getHeight());
			stmt.setDouble(5, user.getWeight());
			stmt.setInt(6, user.getAge());
		    result = stmt.execute();
			stmt.close();
			connection.close();
		} catch (Exception e) {
			System.out.println(e);		
		} 
		if(user instanceof DetailedUser) {
		try {
			Connection connection = this.getConnection();
			PreparedStatement stmt = connection.prepareStatement(
					"INSERT INTO detailedusers VALUES(?,?,?,?,?,?,?,?,?)");
			stmt.setString(1,user.getID());
			stmt.setInt(2, ((DetailedUser)user).getExercise());
			stmt.setDouble(3, ((DetailedUser)user).getBodyFat());
			stmt.setInt(4, ((DetailedUser)user).getCalories());
			stmt.setDouble(5, ((DetailedUser)user).getCarbs());
			stmt.setDouble(6, ((DetailedUser)user).getProtein());
			stmt.setDouble(7, ((DetailedUser)user).getVegfruit());
			boolean[] h = ((DetailedUser)user).getEatingHabits();
			Boolean[] b = new Boolean[h.length];
			for(int i = 0 ; i < h.length ; i++) b[i] = new Boolean(h[i]);
			Array sqlArray = connection.createArrayOf("bool",b);
			stmt.setArray(8,sqlArray);
			stmt.setString(9,((DetailedUser)user).getOtherInfo());
			result = stmt.execute();
			stmt.close();
			connection.close();
		} catch (Exception e) {
			System.out.println(e);
			return result;
		} 
		}
		return result;	
	}
	
	boolean updateUser(Users user) {
		boolean result = false;
		
		try {
			Connection connection = this.getConnection();
			PreparedStatement stmt = connection.prepareStatement(
					"UPDATE users SET name = ?, gender = ?, height = ?, weight =?, age =? WHERE id = ?;");
			stmt.setString(6, user.getID());
			stmt.setString(1, user.getName());
			String temp = ""+user.getGender();
			stmt.setString(2, temp) ;
			stmt.setDouble(3, user.getHeight());
			stmt.setDouble(4, user.getWeight());
			stmt.setInt(5, user.getAge());
		    result = stmt.execute();
			stmt.close();
			connection.close();
		} catch (Exception e) {
			System.out.println(e);
		}
		
		if(user instanceof DetailedUser) {
		try {
			Connection connection = this.getConnection();
			PreparedStatement stmt = connection.prepareStatement(
			"UPDATE detailedusers SET amountofexercise=?,bodyfat=?,caloriesconsump=?,carbsconsump=?,proteinconsump=?,vegfruitsonsump=?,"
			+ "eatinghabits=?,otherinformation = ? WHERE id = ?");
			stmt.setInt(1, ((DetailedUser)user).getExercise());
			stmt.setDouble(2, ((DetailedUser)user).getBodyFat());
			stmt.setInt(3, ((DetailedUser)user).getCalories());
			stmt.setDouble(4, ((DetailedUser)user).getCarbs());
			stmt.setDouble(5, ((DetailedUser)user).getProtein());
			stmt.setDouble(6, ((DetailedUser)user).getVegfruit());
			boolean[] h = ((DetailedUser)user).getEatingHabits();
			Boolean[] b = new Boolean[h.length];
			for(int i = 0 ; i < h.length ; i++) b[i] = new Boolean(h[i]);
			Array sqlArray = connection.createArrayOf("bool",b);
			stmt.setArray(7,sqlArray);
			stmt.setString(8,((DetailedUser)user).getOtherInfo());
			stmt.setString(9,user.getID());
			result = stmt.execute();
			stmt.close();
			connection.close();
		} catch (Exception e) {
			System.out.println(e);
			return result;
		} 
		}
		return result;	
	}
	
	
	private Connection getConnection() throws URISyntaxException, SQLException {
		Connection connection;
		URI dbUri = new URI(System.getenv("DATABASE_URL"));//postgres://eazgxsrhxkhibl:69bfc6652d06407b34ffce5af54a478c07788cf800075f42c70e7e21fdde3630@ec2-107-22-187-21.compute-1.amazonaws.com:5432/ddqi3nebsahm4i
		String username = dbUri.getUserInfo().split(":")[0];
		String password = dbUri.getUserInfo().split(":")[1];
		String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath() +  "?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";

		log.info("Username: {} Password: {}", username, password);
		log.info ("dbUrl: {}", dbUrl);
		
		connection = DriverManager.getConnection(dbUrl, username, password);

		return connection;
	}

}
