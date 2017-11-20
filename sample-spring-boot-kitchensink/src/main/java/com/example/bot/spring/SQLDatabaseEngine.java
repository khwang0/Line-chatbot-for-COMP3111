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
/**
* SQLDatabaseEnging will be perform the function of retrieving, pushing and updating informations from real database
* @author  G8
* @version 1.0
* @since   2017/11/19
*/
@Slf4j
public class SQLDatabaseEngine {


	/**
	* Fetch user ids from the database.
	* @return A Container of type ArrayList<String> cotaining user Ids
	*/
	ArrayList<String> fetchUIDs(){
		ArrayList<String> UIDs = new ArrayList<String>();
		try {
			Connection connection = this.getConnection();
			PreparedStatement stmt = connection.prepareStatement(
					"SELECT * FROM users");
			ResultSet rs = stmt.executeQuery();

			while(rs.next()) {
				UIDs.add(rs.getString(1));
			}
			rs.close();
			stmt.close();
			connection.close();
		} catch (Exception e) {
			log.info(e.getMessage());
		}
		return UIDs;
	}

	/**
	* Retrieve users from database.
	* @param uidkey User id as a key for searching
	* @return A user of type Users with Id specified
	*/
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

				user.setExercise(rs.getInt(9));
		 		user.setBodyFat(rs.getDouble(10));
		 		user.setCalories(rs.getInt(11));
		 		user.setCarbs(rs.getDouble(12));
		 		user.setProtein(rs.getDouble(13));
		 		user.setVegfruit(rs.getDouble(14));
		 		Array a = rs.getArray(15);
		 		Boolean[] b = (Boolean[])a.getArray();
		 		user.setEatingHabits(b);
		 		user.setOtherInfo(rs.getString(16));
		 		user.setAssessmentScore(rs.getInt(17));
				user.setBudget(rs.getDouble(18));
			}
			rs.close();
			stmt.close();
			connection.close();


		} catch (Exception e) {
			log.info(e.getMessage());
		}
		finally {

		}


		if(user != null)	{
			return user;
		}
		else{
			throw new Exception("NOT FOUND");
		}
	}

	/**
	* push users to data base.
	* @param user Obsject of Users
	* @return a boolean to indicate if the push is successful
	*/
	boolean pushUser(Users user) {
		boolean result = false;
		try {
			Connection connection = this.getConnection();
			PreparedStatement stmt = connection.prepareStatement(
					"INSERT INTO users VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			stmt.setString(1, user.getID());
			stmt.setString(2, user.getName());
			String temp = ""+user.getGender();
			stmt.setString(3, temp) ;
			stmt.setDouble(4, user.getHeight());
			stmt.setDouble(5, user.getWeight());
			stmt.setInt(6, user.getAge());
			stmt.setString(7, user.getStage());
			stmt.setInt(8, user.getSubStage());

			stmt.setInt(9, (user).getExercise());
		 	stmt.setDouble(10, (user).getBodyFat());
		 	stmt.setInt(11, (user).getCalories());
		 	stmt.setDouble(12, (user).getCarbs());
		 	stmt.setDouble(13, (user).getProtein());
		 	stmt.setDouble(14, (user).getVegfruit());
		 	boolean[] h = user.getEatingHabits();
		 	Boolean[] b = new Boolean[h.length];
		 	for(int i = 0 ; i < h.length ; i++) b[i] = new Boolean(h[i]);
		 	Array sqlArray = connection.createArrayOf("bool",b);
		 	stmt.setArray(15,sqlArray);
		 	stmt.setString(16,(user).getOtherInfo());
		 	stmt.setInt(17,(user).getAssessmentScore());
			stmt.setDouble(18,(user).getBudget());
			//stmt.setString(19,(user).getRegisterTime());

		  result = stmt.execute();
			stmt.close();
			connection.close();
		} catch (Exception e) {
			System.out.println(e);
		}
		return result;
	}

	/*functions added by Xuhua*/
	//Search existing user in diet_plan
	/**
	* retrieve diet plan of specific user from database.
	* @param user_id user id to find the corresponding plan
	* @return a boolean to indicate if the search is successful
	*/
	boolean search_diet_plan(String user_id) {
		try {
			Connection connection = this.getConnection();
			PreparedStatement stmt = connection.prepareStatement("SELECT id FROM diet_plan WHERE id = ?");
			stmt.setString(1, user_id);

			ResultSet rs = stmt.executeQuery();

			if (rs.next() ) {
				rs.close();
				stmt.close();
				connection.close();
				return true;
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}

	//Generate user diet_plan
	/**
	* generate a diet plan of specific user from database.
	* @param currentUser Object of user to find the corresponding plan
	* @return a boolean to indicate if the search and insert is successful
	*/
	public boolean gen_plan(Users currentUser){

		try {
			Connection connection = this.getConnection();
			String gender =Character.toString(currentUser.getGender());

//			if(search_intake_reference(gender,currentUser.getAge())) {
				ArrayList<Double> plan = new ArrayList<Double>();
				PreparedStatement ref = connection.prepareStatement("SELECT * FROM intake_reference WHERE gender = ? AND min_age <= ? AND max_age >= ?" );
				ref.setString(1, gender);
				ref.setInt(2, currentUser.getAge());
				ref.setInt(3, currentUser.getAge());
				ResultSet result = ref.executeQuery();
				while (result.next()) {
					plan.add(result.getDouble(8));//fiber0
					plan.add(result.getDouble(9));//energy1
					plan.add(result.getDouble(10));//protein2

					plan.add(result.getDouble(4));//fiber_serve3
					plan.add(result.getDouble(5));//energy_serve4
					plan.add(result.getDouble(6));//meat_serve5
					plan.add(result.getDouble(7));//milk_serve6
				}

				ref.close();
				result.close();

				PreparedStatement stmt = connection.prepareStatement(
						"INSERT INTO diet_plan VALUES (?,?,?,?,?,?,?,?,?,?)");//id | fiber | energy | protein | food_name | food_amount
				//String gender = new StringBuilder().append("").append(currentUser.getGender()).toString();

				stmt.setString(1, currentUser.getID());

				stmt.setDouble(2, plan.get(0));//fiber
				stmt.setDouble(3, plan.get(1));//energy
				stmt.setDouble(4, plan.get(2));//protein
//				stmt.setDouble(2, 0.5);//fiber
//				stmt.setDouble(3, 0.5);//energy
//				stmt.setDouble(4, 0.5);//protein

				//set the food_name
	//			stmt.setString(5,"default");//default value
	//			//set the food_amount
				//set the food_name
			    String[] food_name = new String[2];
			    food_name[0] = "apple";
			    food_name[1] = "milk";
			    Array sqlArray1 = connection.createArrayOf("text",food_name);
			    stmt.setArray(5,sqlArray1);
			    //set the food_amount
			    //int[] food_amount = new int[2];
			    Integer[] food_amount = new Integer[2];
			    food_amount[0] = 10;
			    food_amount[1] = 5;
			    Array sqlArray2 = connection.createArrayOf("integer",food_amount);
			    stmt.setArray(6,sqlArray2);
	//			String[] food_name = new String[2];
	//			int[] food_amount = new int[2];
	//			stmt.setArray(5, food_name);//fiber_serve
	//			stmt.setArray(6, food_amount);//energy_serve
	//			stmt.setString(6,"default");//default value
//				stmt.setDouble(7, 0.5);//fiber_serve
//				stmt.setDouble(8, 0.5);//energy_serve
//				stmt.setDouble(9, 0.5);//meat_serve
//				stmt.setDouble(10, 0.5);//milk_serve
				stmt.setDouble(7, plan.get(3));//fiber_serve
				stmt.setDouble(8, plan.get(4));//energy_serve
				stmt.setDouble(9, plan.get(5));//meat_serve
				stmt.setDouble(10, plan.get(6));//milk_serve

				stmt.execute();
				stmt.close();
				connection.close();

//			}
//			else {
//				PreparedStatement stmt = connection.prepareStatement(
//						"INSERT INTO diet_plan VALUES (?,?,?,?,?,?,?,?,?,?)");//id | fiber | energy | protein | food_name | food_amount
//
//				stmt.setString(1, currentUser.getID());
//				stmt.setDouble(2, 40);//fiber
//				stmt.setDouble(3, 717);//energy
//				stmt.setDouble(4, 57);//protein
//				//set the food_name
////				stmt.setString(5,"default");//default value
////				//set the food_amount
//				//set the food_name
//				   String[] food_name = new String[2];
//				   food_name[0] = "apple";
//				   food_name[1] = "milk";
//				   Array sqlArray1 = connection.createArrayOf("text",food_name);
//				   stmt.setArray(5,sqlArray1);
//				   //set the food_amount
//				   //int[] food_amount = new int[2];
//				   Integer[] food_amount = new Integer[2];
//				   food_amount[0] = 10;
//				   food_amount[1] = 5;
//				   Array sqlArray2 = connection.createArrayOf("integer",food_amount);
//				   stmt.setArray(6,sqlArray2);
////				String[] food_name = new String[2];
////				int[] food_amount = new int[2];
////				stmt.setArray(5, food_name);//fiber_serve
////				stmt.setArray(6, food_amount);//energy_serve
////				stmt.setString(6,"default");//default value
//				stmt.setDouble(7, 8);//fiber_serve
//				stmt.setDouble(8, 6);//energy_serve
//				stmt.setDouble(9, 3);//meat_serve
//				stmt.setDouble(10, 2.5);//milk_serve
//
//				stmt.execute();
//				stmt.close();
//				connection.close();
//			}

		} catch (Exception e) {

			return false;
		}
		return true;
	}

	/**
	* retrieve diet plan of specific user from database.
	* @param gender the infomation to search with
	* @param age the infomation to search with
	* @return a boolean to indicate if the search is successful
	*/
	boolean search_intake_reference(String gender, int age){
		try {
			Connection connection = this.getConnection();
			PreparedStatement stmt = connection.prepareStatement("SELECT * FROM intake_reference where gender = ? AND min_age <= ? AND max_age >= ?" );
			stmt.setString(1, gender);
			stmt.setInt(2, age);
			stmt.setInt(3, age);

			ResultSet res = stmt.executeQuery();
			if (res.next() ) {
				res.close();
				stmt.close();
				connection.close();
				return true;
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}


	/**
	* retrieve diet plan of specific user from database.
	* @param user_id the infomation to search with
	* @param budget the infomation to work with
	* @return a String of the diet plan
	*/
	String display_diet_plan(String user_id, double budget) {
		String result = "(DAILY BASIS)\n";


		try {
			ArrayList<Double> plan_info = search_plan(user_id);
			double fiber = plan_info.get(0);
			double energy = plan_info.get(1);
			double protein = plan_info.get(2);
			double meat_serve = plan_info.get(5);
			double milk_serve = plan_info.get(6);

			double protein_meat = protein*meat_serve/(meat_serve+milk_serve);
			double protein_milk = protein*milk_serve/(meat_serve+milk_serve);

			result = result + "Vegetables & Fruit: " + Double.toString(fiber) + "\n";
			result = result + "Grain (cereal) foods: " + Double.toString(energy) + "\n";
			result = result + "Meat: " + String.format("%.2f", protein_meat) + "\n";
			result = result + "Milk: " + String.format("%.2f", protein_milk) + "\n";
			result = result + "Budget: " + Double.toString(budget) + "\n";

//			Connection connection = this.getConnection();
//
//			PreparedStatement stmt = connection.prepareStatement("SELECT fiber, energy, protein FROM diet_plan WHERE id = ?");
//			stmt.setString(1, user_id);
//
//			ResultSet rs = stmt.executeQuery();
//
//			if(rs.next()) {
//
//			}
//
//			stmt.close();
//			connection.close();
		} catch (Exception e) {
			System.out.println(e);
		}
		return result;
	}

	//Query the target diet plan info from "diet_plan" table and return
	/**
	* retrieve the information of diet plan of specific user from database.
	* @param user_id the user id to search with
	* @return an ArrayList of the result
	*/
	ArrayList<Double> search_plan(String user_id) {
		ArrayList<Double> plan_info = new ArrayList<Double>(); // store the query result from plan table
		try {
			//connect to the database with table: diet_plan
			Connection connection = this.getConnection();
			//prepare a SQL statement while leaving some parameters
			PreparedStatement stmt = connection.prepareStatement("SELECT fiber, energy, protein, fiber_serve,  energy_serve,  meat_serve, milk_serve FROM diet_plan where id = ? ");
			//PreparedStatement stmt = connection.prepareStatement("SELECT fiber, energy, protein, budget, fiber_serve,  energy_serve,  meat_serve, milk_serve FROM diet_plan where id = ? ");
			stmt.setString(1, user_id);//1 is the param location/index
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				plan_info.add(rs.getDouble(1));//fiber
				plan_info.add(rs.getDouble(2));//energy
				plan_info.add(rs.getDouble(3));//protein
				//plan.info.add(rs.getDouble(4));//budget
				plan_info.add(rs.getDouble(4));//fiber_serve
				plan_info.add(rs.getDouble(5));//energy_serve
				plan_info.add(rs.getDouble(6));//meat_serve
				plan_info.add(rs.getDouble(7));//milk_serve
			}
			rs.close();
			stmt.close();
			connection.close();
		} catch (Exception e) {
			log.info("query error for search_plan: {}", e.toString());
			//System.out.println(e);
		}
		//if (plan_info.size() != 0)
		return plan_info;

		//throw new Exception("NOT FOUND");
	}

	//Query the current diet status info from "diet_conclusion" table and return
	/**
	* retrieve the information of diet conclusion of specific user and date from database.
	* @param user_id the user id to search with
	* @param date the date to search with
	* @return an ArrayList of the result
	*/
	ArrayList<Double> search_current(String user_id, String date) {
		ArrayList<Double> current_info = new ArrayList<Double>(); // store the query result from current table
		try {
			//connect
			Connection connection = this.getConnection();
			PreparedStatement stmt = connection.prepareStatement("SELECT protein, energy, fiber, price FROM diet_conclusion where id = ? AND date = ?");
			stmt.setString(1, user_id);
			stmt.setString(2, date);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				current_info.add(rs.getDouble(3));//fiber
				current_info.add(rs.getDouble(2));//energy
				current_info.add(rs.getDouble(1));//protein
				current_info.add(rs.getDouble(4));//price
			}
			rs.close();
			stmt.close();
			connection.close();
		} catch (Exception e) {
			log.info("query error for search_current: {}", e.toString());
		}

		return current_info;//can be null

	}

	/*  Function added by ZK*/
	/**
	* update the information of diet_conclusion of specific user and date from database.
	* @param id the user id to search with
	* @param healthSearcher the searcher that the function need to use
	* @param amount the amount of food to calculate with
	* @param time the time to search with
	* @param price the price to caculate with
	*/
	void updateconsumption(HealthSearch healthSearcher,int amount,String id, String time,double price) {
		try {
			Connection connection = this.getConnection();
			PreparedStatement stmt = connection.prepareStatement("SELECT protein, energy, fiber,price FROM diet_conclusion where id = ? AND date = ?");
			stmt.setString(1,id);
			stmt.setString(2,time.substring(0,8));
			ResultSet rs = stmt.executeQuery();
			if(rs.next()) {
				double previous_energy =0;
				double previous_fiber =0;
				double previous_protein =0;
				double previous_price = 0;
				double current_protein = 0;
				double current_energy =0;
				double current_fiber = 0;
				double current_price = 0;
				previous_protein = rs.getDouble(1);
				previous_energy = rs.getDouble(2);
				previous_fiber = rs.getDouble(3);
				previous_price = rs.getDouble(4);

				current_price = price + previous_price;


				if( !healthSearcher.getProtein().equals("0")) {
					 current_protein = previous_protein + Double.parseDouble(healthSearcher.getProtein())*amount/100.0;
				}
				else {
					current_protein = previous_protein;

				}
				if( !healthSearcher.getEnergy().equals("0")) {
					current_energy = previous_energy + Double.parseDouble(healthSearcher.getEnergy())*amount/100.0;
				}
				else {
					current_energy = previous_energy;
				}
				if( !healthSearcher.getFiber().equals("0")) {
					current_fiber = previous_fiber + Double.parseDouble(healthSearcher.getFiber())*amount/100.0;
				}
				else {
					current_fiber = previous_fiber;
				}
				PreparedStatement stmt2 = connection.prepareStatement(
				"UPDATE diet_conclusion SET protein = ?, energy = ?, fiber = ?, price=? WHERE id = ? AND date = ?;");

				stmt2.setDouble(1, current_protein);
				stmt2.setDouble(2, current_energy);
				stmt2.setDouble(3, current_fiber);
				stmt2.setDouble(4, current_price);
				stmt2.setString(5, id);
				stmt2.setString(6, time.substring(0,8));
				stmt2.execute();
				stmt2.close();
			}
			else {
				PreparedStatement stmt1 = connection.prepareStatement(
						"INSERT INTO diet_conclusion VALUES(?,?,?,?,?,?)");
				stmt1.setString(1,id);
				stmt1.setString(2, time.substring(0,8));
				stmt1.setDouble(3, Double.parseDouble(healthSearcher.getProtein())*amount/100.0);
				stmt1.setDouble(4, Double.parseDouble(healthSearcher.getEnergy())*amount/100.0);
				stmt1.setDouble(5, Double.parseDouble(healthSearcher.getFiber())*amount/100.0);
				stmt1.setDouble(6, price);
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

	/**
	* select the information of diet record of specific date from database.
	* @param text user input text
	* @param id the user id to search with
	* @return String of the dietrecord
	*/
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
	/**
	* retrieve all user id in table users from database.
	* @return an ArrayList of user ids
	*/
	ArrayList<String> findallusers() {
		ArrayList<String> answer = new ArrayList<String>();
		try {

			Connection connection = this.getConnection();
			PreparedStatement stmt = connection.prepareStatement(
					"SELECT * FROM users ");
			ResultSet rs = stmt.executeQuery();

			while(rs.next()) {
				answer.add(rs.getString(1));
			}
			rs.close();
			stmt.close();
			connection.close();

		} catch (Exception e) {
			System.out.println(e);
		}

		return answer;
	}



	/**
	* push the information of foodinput to dietrecord table from database.
	* @param foodInput the foodinput to push with
	* @return a boolean to indicate if the push is successful
	*/
	boolean pushDietRecord(FoodInput foodInput){
		boolean result = false;
		try {
			Connection connection = this.getConnection();
			PreparedStatement stmt = connection.prepareStatement(
					"INSERT INTO dietrecord VALUES(?,?,?,?,?,?)");
			stmt.setString(1,foodInput.getKey());
			stmt.setString(2,foodInput.getId());
			stmt.setString(3,foodInput.getFoodName());
			stmt.setInt(4,foodInput.getAmount());
			stmt.setString(5,foodInput.getTime());
			stmt.setDouble(6,foodInput.getPrice());
			result = stmt.execute();
			stmt.close();
			connection.close();
		} catch (Exception e) {
			System.out.println(e);
			return result;
		}
		return result;
	}

	/**
	* push the information of foodInfo to foodinfo table from database.
	* @param food the foodInfo to push with
	* @see FoodInfo
	* @return a boolean to indicate if the push is successful
	*/
	boolean pushFoodInfo(FoodInfo food) {
		boolean result = false;
		try {
			Connection connection = this.getConnection();
			PreparedStatement stmt = connection.prepareStatement(
					"INSERT INTO foodinfo VALUES(?,?,?,?,?)");
			stmt.setString(1,food.getFoodName());
			stmt.setDouble(2,food.getEnergy());
			stmt.setDouble(3,food.getProtein());
			stmt.setDouble(4,food.getFiber());
			stmt.setInt(5,food.getPrice());
			result = stmt.execute();
			stmt.close();
			connection.close();
		} catch (Exception e) {
			log.info("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
			log.info("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
			log.info("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
			log.info("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
			log.info("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
			log.info(e.getMessage());
			log.info("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
			log.info("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
			log.info("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
			log.info("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
			log.info("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
			
			return result;
		}
		return result;
	}

	/**
	* retrieve the information of foodInfo given foodInfo name of foodinfo table from database.
	* @param foodname the food name to search with
	* @see FoodInfo
	* @return return a  FoodInfo object
	*/
	FoodInfo searchFoodInfo(String foodname){
		//boolean result = false;
		FoodInfo foodInfo = null;
		try {
			Connection connection = this.getConnection();
			PreparedStatement stmt = connection.prepareStatement(
					"SELECT * FROM foodinfo WHERE foodname=(?)");
			stmt.setString(1,foodname);
			ResultSet rs = stmt.executeQuery();

			while(rs.next()) {
				foodInfo = new FoodInfo();
				foodInfo.setFoodName(rs.getString(1));
				foodInfo.setEnergy(rs.getDouble(2));
				foodInfo.setProtein(rs.getDouble(3));
				foodInfo.setFiber(rs.getDouble(4));
				foodInfo.setPrice(rs.getInt(5));
			}
			rs.close();
			stmt.close();
			connection.close();


		} catch (Exception e) {
			log.info(e.getMessage());
		}
		finally {

		}
		return foodInfo;
	}

	/**
	* retrieve all the name of foodInfo in foodinfo table from database.
	* @see FoodInfo
	* @return a String[] of foodinfo name
	*/
	String[] getFoodInfo(){
		ArrayList<String> foodNamesArray = new ArrayList<String>();
		String[] foodNames;
		try {
			Connection connection = this.getConnection();
			PreparedStatement stmt = connection.prepareStatement(
					"SELECT foodname FROM foodinfo");
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				foodNamesArray.add(rs.getString(1));
			}
			rs.close();
			stmt.close();
			connection.close();


		} catch (Exception e) {
			log.info(e.getMessage());
		}
		finally {

		}
		foodNames = foodNamesArray.toArray(new String[foodNamesArray.size()]);
		return foodNames;

	}


	/**
	* update the information of user in users table from database.
	* @param user the object of users to search with
	* @see Users
	* @return a boolean to indicate if the update is successful
	*/
	boolean updateUser(Users user) {
		boolean result = false;

		try {
			Connection connection = this.getConnection();
			PreparedStatement stmt = connection.prepareStatement(
					"UPDATE users SET name=?,gender=?, height=?, weight =?, age =?, stage =?, substage =?, amountofexercise=?, bodyfat=?, caloriesconsump=?, carbsconsump=?, proteinconsump=?, vegfruitconsump=?, eatinghabits=?, otherinformation=?, assessmentscore=?, budget = ? WHERE id = ?");
			stmt.setString(18, user.getID());
			stmt.setString(1, user.getName());
			String temp = ""+user.getGender();
			stmt.setString(2, temp) ;
			stmt.setDouble(3, user.getHeight());
			stmt.setDouble(4, user.getWeight());
			stmt.setInt(5, user.getAge());
			stmt.setString(6,user.getStage());
			stmt.setInt(7,user.getSubStage());
			stmt.setInt(8,user.getExercise());
			stmt.setDouble(9, user.getBodyFat());
			stmt.setInt(10, user.getCalories());
			stmt.setDouble(11, user.getCarbs());
			stmt.setDouble(12, user.getProtein( ));
			stmt.setDouble(13, user.getVegfruit());
			boolean[] h = user.getEatingHabits();
			Boolean[] b = new Boolean[h.length];
			for(int i = 0 ; i < h.length ; i++) b[i] = new Boolean(h[i]);
			Array sqlArray = connection.createArrayOf("bool",b);
			stmt.setArray(14,sqlArray);
			stmt.setString(15,user.getOtherInfo());
			stmt.setInt(16,user.getAssessmentScore());
			stmt.setDouble(17,user.getBudget());
			//stmt.setString(18,user.getRegisterTime());
		    result = stmt.execute();
			stmt.close();
			connection.close();
		} catch (Exception e) {
			System.out.println(e);
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
