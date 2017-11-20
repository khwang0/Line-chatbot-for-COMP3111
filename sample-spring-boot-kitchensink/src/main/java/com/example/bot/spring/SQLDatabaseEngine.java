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

/**
* FoodInfo will be the object that stores the value that will be pushed in the
* foodinfo table in database (AKA local food database)
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
	* Retrieve users from data base.
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

	// DetailedUser searchDetailedUser(Users user) throws Exception { //this contains bug
	// 	DetailedUser newuser = null;
	// 	try {
	// 		Connection connection = this.getConnection();
	// 		PreparedStatement stmt = connection.prepareStatement(
	// 				"SELECT * FROM detailedusers WHERE id=(?)");
	// 		stmt.setString(1,user.getID());
	// 		ResultSet rs = stmt.executeQuery();

	// 		while(rs.next()) {
	// 			newuser = new DetailedUser(user);
	// 			newuser.setExercise(rs.getInt(2)) ;
	// 			newuser.setBodyFat(rs.getDouble(3));
	// 			newuser.setCalories(rs.getInt(4));
	// 			newuser.setCarbs(rs.getDouble(5)) ;
	// 			newuser.setProtein(rs.getDouble(6));
	// 			newuser.setVegfruit(rs.getDouble(7));
	// 			newuser.setOtherInfo(rs.getString(9));
	// 			newuser.setAssessmentScore(rs.getInt(10));
	// 			Array sqlArray = rs.getArray(8);
	// 			newuser.setEatingHabits((Boolean[])sqlArray.getArray());

	// 		}
	// 		rs.close();
	// 		stmt.close();
	// 		connection.close();
	// 	} catch (Exception e) {
	// 		log.info(e.getMessage());
	// 	}
	// 	if(newuser != null)	{
	// 		return newuser;
	// 	}
	// 	throw new Exception("NOT FOUND");
	// }

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
		// if(user instanceof DetailedUser) {
		// try {
		// 	Connection connection = this.getConnection();
		// 	PreparedStatement stmt = connection.prepareStatement(
		// 			"INSERT INTO detailedusers VALUES(?,?,?,?,?,?,?,?,?,?)");
		// 	stmt.setString(1,user.getID());
		// 	stmt.setInt(2, ((DetailedUser)user).getExercise());
		// 	stmt.setDouble(3, ((DetailedUser)user).getBodyFat());
		// 	stmt.setInt(4, ((DetailedUser)user).getCalories());
		// 	stmt.setDouble(5, ((DetailedUser)user).getCarbs());
		// 	stmt.setDouble(6, ((DetailedUser)user).getProtein());
		// 	stmt.setDouble(7, ((DetailedUser)user).getVegfruit());
		// 	boolean[] h = ((DetailedUser)user).getEatingHabits();
		// 	Boolean[] b = new Boolean[h.length];
		// 	for(int i = 0 ; i < h.length ; i++) b[i] = new Boolean(h[i]);
		// 	Array sqlArray = connection.createArrayOf("bool",b);
		// 	stmt.setArray(8,sqlArray);
		// 	stmt.setString(9,((DetailedUser)user).getOtherInfo());
		// 	stmt.setInt(10,((DetailedUser)user).getAssessmentScore());
		// 	result = stmt.execute();
		// 	stmt.close();
		// 	connection.close();
		// } catch (Exception e) {
		// 	System.out.println(e);
		// 	return result;
		// }
		// }
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



	//Display function for the diet plan to return a String
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

		// if(user instanceof DetailedUser) {
		// try {
		// 	Connection connection = this.getConnection();
		// 	PreparedStatement stmt = connection.prepareStatement(
		// 	"UPDATE detailedusers SET amountofexercise=?,bodyfat=?,caloriesconsump=?,carbsconsump=?,proteinconsump=?,vegfruitsonsump=?,"
		// 	+ "eatinghabits=?,otherinformation = ?, assessmentscore = ? WHERE id = ?");
		// 	stmt.setInt(1, ((DetailedUser)user).getExercise());
		// 	stmt.setDouble(2, ((DetailedUser)user).getBodyFat());
		// 	stmt.setInt(3, ((DetailedUser)user).getCalories());
		// 	stmt.setDouble(4, ((DetailedUser)user).getCarbs());
		// 	stmt.setDouble(5, ((DetailedUser)user).getProtein());
		// 	stmt.setDouble(6, ((DetailedUser)user).getVegfruit());
		// 	boolean[] h = ((DetailedUser)user).getEatingHabits();
		// 	Boolean[] b = new Boolean[h.length];
		// 	for(int i = 0 ; i < h.length ; i++) b[i] = new Boolean(h[i]);
		// 	Array sqlArray = connection.createArrayOf("bool",b);
		// 	stmt.setArray(7,sqlArray);
		// 	stmt.setString(8,((DetailedUser)user).getOtherInfo());
		// 	stmt.setInt(9,((DetailedUser)user).getAssessmentScore());
		// 	stmt.setString(10,user.getID());
		// 	result = stmt.execute();
		// 	stmt.close();
		// 	connection.close();
		// } catch (Exception e) {
		// 	System.out.println(e);
		// 	return result;
		// }
		// }
		return result;
	}


	boolean pushTest(int a){
		boolean result = false;
		try {
			Connection connection = this.getConnection();
			PreparedStatement stmt = connection.prepareStatement(
					"INSERT INTO test VALUES(?)");
			stmt.setInt(1,a);
			result = stmt.execute();
			stmt.close();
			connection.close();
		} catch (Exception e) {
			System.out.println(e);
			return result;
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
