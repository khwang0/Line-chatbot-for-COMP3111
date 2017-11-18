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
					"SELECT * FROM users WHERE id=(?)");
			stmt.setString(1,uidkey);
			ResultSet rs = stmt.executeQuery();

			while(rs.next()) {
				user = new Users(rs.getString(1),rs.getString(2));
				user.setGender(rs.getString(3).charAt(0));
				user.setHeight(rs.getDouble(4));
				user.setWeight(rs.getDouble(5));
				user.setAge(rs.getInt(6));
				user.setStage(rs.getString(7));
				user.setSubStage(rs.getInt(8));
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
					"INSERT INTO users VALUES(?,?,?,?,?,?,?,?)");
			stmt.setString(1, user.getID());
			stmt.setString(2, user.getName());
			String temp = ""+user.getGender();
			stmt.setString(3, temp) ;
			stmt.setDouble(4, user.getHeight());
			stmt.setDouble(5, user.getWeight());
			stmt.setInt(6, user.getAge());
			stmt.setString(7, user.getStage());
			stmt.setInt(8, user.getSubStage());
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
	//Search existing user in diet_plan
	boolean search_diet_plan(String user_id) {
		try {
			Connection connection = this.getConnection();
			PreparedStatement stmt = connection.prepareStatement("SELECT id FROM diet_plan WHERE id = ?");
			stmt.setString(1, user_id);

			ResultSet rs = stmt.executeQuery();

			if (rs.next() ) {
				return true;
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}

	//Generate user diet_plan
	boolean gen_plan(String user_id, Users currentUser){

		try {
			Connection connection = this.getConnection();
			String gender = ""+currentUser.getGender();
			PreparedStatement ref = connection.prepareStatement("SELECT * FROM intake_reference where gender = ? AND min_age < ? AND max_age > ?" );
			ref.setString(1, gender);
			ref.setInt(2, currentUser.getAge());
			ref.setInt(3, currentUser.getAge());
			ResultSet rs = ref.executeQuery();
			PreparedStatement stmt = connection.prepareStatement(
					//"INSERT INTO diet_plan VALUES(?,?,?,?,'{\"apple\"}','{10}')");//id | protein | fat | sugar | food_name | food_amount
					"INSERT INTO diet_plan VALUES(?,?,?,?,?,?,?,?,?,?)");//id | fiber | energy | protein | food_name | food_amount
			stmt.setString(1, user_id);
			stmt.setDouble(2, rs.getDouble(9));//fiber
			stmt.setDouble(3, rs.getDouble(10));//energy
			stmt.setDouble(4, rs.getDouble(11));//protein
			//set the food_name
			stmt.setString(5,"default");//default value
			//set the food_amount
			//int[] food_amount = new int[2];
			stmt.setString(6,"default");//default value
			stmt.setDouble(7, rs.getDouble(5));//fiber_serve
			stmt.setDouble(8, rs.getDouble(6));//energy_serve
			stmt.setDouble(9, rs.getDouble(7));//meat_serve
			stmt.setDouble(10, rs.getDouble(8));//milk_serve

			
			stmt.execute();
			ref.close();
			rs.close();
			stmt.close();
			connection.close();
		} catch (Exception e) {
			System.out.println(e);
			return false;
		}
		return true;
	}


	//Query the target diet plan info from "diet_plan" table and return
	ArrayList<Double> search_plan(String user_id) throws Exception {
		ArrayList<Double> plan_info = new ArrayList<Double>(); // store the query result from plan table
		try {
			//connect to the database with table: diet_plan
			Connection connection = this.getConnection();
			//prepare a SQL statement while leaving some parameters
			PreparedStatement stmt = connection.prepareStatement("SELECT protein, fat, sugar FROM diet_plan where id = ? ");
			stmt.setString(1, user_id);//1 is the param location/index
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				plan_info.add(rs.getDouble(1));//protein
				plan_info.add(rs.getDouble(2));//fat 
				plan_info.add(rs.getDouble(3));//sugar
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
	ArrayList<Double> search_current(String user_id, String date) throws Exception {
		ArrayList<Double> current_info = new ArrayList<Double>(); // store the query result from current table
		try {
			//connect
			Connection connection = this.getConnection();
			PreparedStatement stmt = connection.prepareStatement("SELECT protein, fat, sugar FROM diet_conclusion where id = ? AND date = ?");
			stmt.setString(1, user_id);
			stmt.setString(2, date);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				current_info.add(rs.getDouble(1));//protein
				current_info.add(rs.getDouble(2));//fat
				current_info.add(rs.getDouble(3));//sugar
			}
			rs.close();
			stmt.close();
			connection.close();
		} catch (Exception e) {
			log.info("query error for search_current: {}", e.toString());
		}
		if (current_info.size() != 0)
			return current_info;

		throw new Exception("NOT FOUND");
	}

	/*  Function added by ZK*/
	void updateconsumption(HealthSearch healthSearcher,int amount,String id, String time) {

		try {
			Connection connection = this.getConnection();
			
			PreparedStatement stmt = connection.prepareStatement("SELECT protein, fat, sugar FROM diet_conclusion where id = ? AND date = ?");
			stmt.setString(1,id);
			stmt.setString(2,time.substring(0,8));
			ResultSet rs = stmt.executeQuery();
			if(rs.next()) {
				double previous_fat =0;
				double previous_sugar =0;
				double previous_protein =0;
				double current_protein = 0;
				double current_fat =0;
				double current_sugar = 0;
				previous_protein = rs.getDouble(1);
				previous_fat = rs.getDouble(2);
				previous_sugar = rs.getDouble(3);
				
				
				
				if( !healthSearcher.getProtein().equals("N/A")) {
					 current_protein = previous_protein + Double.parseDouble(healthSearcher.getProtein())*amount/100.0;

				}
				else {
					current_protein = previous_protein;
					
				}
				if( !healthSearcher.getFat().equals("N/A")) {
					current_fat = previous_fat + Double.parseDouble(healthSearcher.getFat())*amount/100.0;
				}
				else {
					current_fat = previous_fat;
				}				
				if( !healthSearcher.getSugar().equals("N/A")) {
					current_sugar = previous_sugar + Double.parseDouble(healthSearcher.getSugar())*amount/100.0;
				}
				else {
					current_sugar = previous_sugar;
				}	
				PreparedStatement stmt2 = connection.prepareStatement(
						"UPDATE diet_conclusion SET protein = ?, fat = ?, sugar = ? WHERE id = ? AND date = ?;");

				stmt2.setDouble(1, current_protein);
				stmt2.setDouble(2, current_fat);
				stmt2.setDouble(3, current_sugar);
				stmt2.setString(4, id);
				stmt2.setString(5, time.substring(0,8));
				stmt2.execute();
				stmt2.close();
			}
			else {
				PreparedStatement stmt1 = connection.prepareStatement(
						"INSERT INTO diet_conclusion VALUES(?,?,?,?,?)");
				stmt1.setString(1,id);
				stmt1.setString(2, time.substring(0,8));
				stmt1.setDouble(3, Double.parseDouble(healthSearcher.getProtein())*amount/100.0);
				stmt1.setDouble(4, Double.parseDouble(healthSearcher.getFat())*amount/100.0);
				stmt1.setDouble(5, Double.parseDouble(healthSearcher.getSugar())*amount/100.0);
				stmt1.execute();
				stmt1.close();
			}
			stmt.close();
			rs.close();
			connection.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	} 
	
	
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
					"UPDATE users SET name = ?, gender = ?, height = ?, weight =?, age =?, stage =?, substage =? WHERE id = ?");
			stmt.setString(8, user.getID());
			stmt.setString(1, user.getName());
			String temp = ""+user.getGender();
			stmt.setString(2, temp) ;
			stmt.setDouble(3, user.getHeight());
			stmt.setDouble(4, user.getWeight());
			stmt.setInt(5, user.getAge()); 
			stmt.setString(6,user.getStage());
			stmt.setInt(7,user.getSubStage());
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
