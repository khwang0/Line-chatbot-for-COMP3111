package com.example.bot.spring;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
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
					"SELECT * FROM users WHERE id=(?)");          // SELECT * FROM dietrecord WHERE userid= id GROUP by time
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
				newuser.setAssessmentScore(rs.getInt(10));
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
					"INSERT INTO detailedusers VALUES(?,?,?,?,?,?,?,?,?,?)");
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
			stmt.setInt(10,((DetailedUser)user).getAssessmentScore());
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
	
	/*functions added by Xuhua*/
	
	//Generate user diet_plan
	boolean gen_plan(String user_id){
		boolean result = false;
		
		try {
			Connection connection = this.getConnection();
			PreparedStatement stmt = connection.prepareStatement(
					"INSERT INTO diet_plan VALUES(?,?,?,?,'{\"apple\"}','{10}')");//id | protein | fat | sugar | food_name | food_amount
			stmt.setString(1, user_id);
			stmt.setInt(2, 100);//protein
			stmt.setInt(3, 100);//fat
			stmt.setInt(4, 100);//sugar
			//set the food_name
//			String[] food_name = new String[2];
//			food_name[0] = "apple";
//			food_name[1] = "milk";
//			Array sqlArray1 = connection.createArrayOf("text[]",food_name);
//			stmt.setArray(5,sqlArray1);
//			//set the food_amount
//			//int[] food_amount = new int[2];
//			Integer[] food_amount = new Integer[2];
//			food_amount[0] = 10;
//			food_amount[1] = 5;
//			Array sqlArray2 = connection.createArrayOf("integer[]",food_amount);
//			stmt.setArray(6,sqlArray2);
			
		    result = stmt.execute();
			stmt.close();
			connection.close();
		} catch (Exception e) {
			System.out.println(e);
			return result;
		}
		return result;
	}
	
	
	//Query the target diet plan info from "diet_plan" table and return
	ArrayList<Integer> search_plan(String user_id) throws Exception {
		ArrayList<Integer> plan_info = new ArrayList<Integer>(); // store the query result from plan table
		try {
			//connect to the database with two tables: current & plan
			Connection connection = this.getConnection();
			//prepare a SQL statement while leaving some parameters
			PreparedStatement stmt = connection.prepareStatement("SELECT protein, fat, sugar FROM diet_plan where id = ? ");
			stmt.setString(1, user_id);//1 is the param location/index
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				plan_info.add(rs.getInt(1));//protein
				plan_info.add(rs.getInt(2));//fat
				plan_info.add(rs.getInt(3));//sugar
			}
			rs.close();
			stmt.close();
			connection.close();
		} catch (Exception e) {
			log.info("query error for search_plan: {}", e.toString());
			//System.out.println(e);
		}
		if (plan_info.size() != 0)
			return plan_info;
		
		throw new Exception("NOT FOUND");
	}
	
	//Query the current diet status info from "diet_conclusion" table and return
	ArrayList<Integer> search_current(String user_id, String date) throws Exception {
		ArrayList<Integer> current_info = new ArrayList<Integer>(); // store the query result from current table
		return current_info;
	}
		
	
	
	/*  Function added by ZK*/
	
	String reportDiet(String text, String id) {
		String answer =" ";
		try {

			Connection connection = this.getConnection();
			PreparedStatement stmt = connection.prepareStatement(
					"SELECT foodname,amount,time,userid FROM dietrecord WHERE time LIKE concat( ?, '%') ");
			stmt.setString(1, text);
			ResultSet rs = stmt.executeQuery();
            
			while(rs.next()) {
				if(rs.getString(4).equals(id)) {
					answer += (rs.getString(1)+"  "+rs.getInt(2)+"g  "+rs.getString(3).substring(8,10)+":"+rs.getString(3).substring(10,12)+":"+rs.getString(3).substring(12,14)+"\n");
				}
			}
			rs.close();
			stmt.close();
			connection.close();

		} catch (Exception e) {
			System.out.println(e);
		} 
		
		return answer;
		
	}
	boolean pushDietRecord(foodInput foodinput) {
		boolean result = false;
		try {
			Connection connection = this.getConnection();
			PreparedStatement stmt = connection.prepareStatement(
					"INSERT INTO dietrecord VALUES(?,?,?,?,?)");
			stmt.setString(1,foodinput.getKey());
			stmt.setString(2,foodinput.getId());
			stmt.setString(3,foodinput.getFoodName());
			stmt.setInt(4,foodinput.getAmount());
			stmt.setString(5,foodinput.getTime());
			result = stmt.execute();
			stmt.close();
			connection.close();
		} catch (Exception e) {
			System.out.println(e);
			return result;
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
			+ "eatinghabits=?,otherinformation = ?, assessmentscore = ? WHERE id = ?");
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
			stmt.setInt(9,((DetailedUser)user).getAssessmentScore());
			stmt.setString(10,user.getID());
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
