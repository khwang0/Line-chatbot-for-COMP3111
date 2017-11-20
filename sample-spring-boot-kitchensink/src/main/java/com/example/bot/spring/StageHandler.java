package com.example.bot.spring;

import java.io.IOException;
import org.json.*;
import java.io.*;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.Date;
import java.util.TimeZone;
import com.linecorp.bot.model.profile.UserProfileResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.google.common.io.ByteStreams;
import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.client.MessageContentResponse;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.action.MessageAction;
import com.linecorp.bot.model.action.PostbackAction;
import com.linecorp.bot.model.action.URIAction;
import com.linecorp.bot.model.event.Event;


import lombok.NonNull;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;


@Slf4j
/**
* StageHandler will mainly perform the function of handling the event and deciding
* what is the reply message to users. More importantly, it also perform the functions
* of updating corresponding database and other interactions of different classes.
* In short, a mediator
* @version 1.0
* @since   2017/11/19
*/
public class StageHandler {
	private String time;
	private InputChecker inputChecker = new InputChecker();
	private FoodInput foodInput = null;
	private FoodInfo foodInfo = null;
	private MenuReader menuReader = new MenuReader();

	private	String[] question = {"Q1: You limit your intake of high-fat or sugary foods to a minimum of one a day\n",
								"Q2: You understand the difference between types of fat (saturated and unsaturated fat) and always opt for heart friendly options when cooking\n",
								"Q3: You believe in eating what you want but in moderation\n",
								"Q4: You eat at least five portions of fruits and vegetables a day(One portion should be around 80g or 3 tablespoons full cooked vegetables or green leaves)\n",
								"Q5: When you snack you generally stick to nuts, fruits or vegetables snacks\n" ,
								"Q6: You never skip main meals (breakfast, lunch and dinner)\n",
								"Q7: Your daily diet is varied and full of colours (green, yellow, red etc.)\n",
								"Q8: You eat fish at least twice a week\n",
								"Q9: You rarely eat processed foods\n",
								"Q10: You include all 5 food groups in your daily meals (cereals, vegetables, fruits, milk or milk products, pulses /fish / meat / eggs or soya)\n"};
	private String[][] feedback = {{"You 'd should pay high attention the fat intake of food everyday or You might get trouble with obesity.\n",""},
			{"Diferent types of fat may cause different consequences, which should not be ignored.\n",""},
			{"Never eat anything too much even if you love it desperately./n",""},
			{"Keeping balance of nutrients is important. It's time to add some fruits or vegatable in your daily menu.\n",""},
			{"","Even if nuts are generally healthy, never eat them too much or the intake of fat would be amazingly huge.\n"},
			{"",""},
			{"Other nutrients like vitamins and minerals are also necessary to your body's process of metabolism, please make your diet varied.\n",""},
			{"Seafood contains lowfat and plenty of trace element that is good to our body. It's not a bad idea to eat some seafood.\n",""},
			{"",""},
			{"",""}};
	private String suggestion = "";
	private HealthSearch healthSearcher = new HealthSearch();
	private String REDIRECT = "Redirecting...type anything to continue.";

	// used for serve-weight conversion
	private float VF_weight_per_serve = 75;//75g Vegetable&Fruit
	private float Grain_weight_per_serve = 500;//500kj Grain
	private float MM_weight_per_serve = 100;

  	/**
	* InputChecker will perform the function of checking whether users' inputs are correct
	* and updating the database corresponding to different input
	* @param replyToken
	*/
	public String initStageHandler(   String text, Users currentUser, SQLDatabaseEngine database) {
		String replymsg = "";
		switch(currentUser.getSubStage()) {
		case 0:{
			//if(!text.equals("")) {
        		replymsg = "Please enter your name: (1-32 characters)";
        		currentUser.setSubStage(currentUser.getSubStage()+1);
        	//}
        	// else {
        	// 	replymsg = "I will be deactivated. To reactivate me, please block->unblock me. Bye.";
        	// 	currentUser.setStage("");
        	// 	currentUser.setSubStage(0);
        	// 	currentUser = null;
        	// }
		}break;
		case 1:{
			if(inputChecker.NameEditting(text,currentUser,database,"set")) {
				replymsg = "Please enter your gender: (M for male F for female)";
				currentUser.setSubStage(currentUser.getSubStage()+1);
				}
			else {
				replymsg = "Please enter your name: (1-32 characters)";
			}
		}break;
		case 2:{
			if(inputChecker.GenderEditting(text,currentUser,database,"set")) {
				replymsg="Please enter your height in cm:";
				currentUser.setSubStage(currentUser.getSubStage()+1);
			}
			else
				replymsg="Please enter your gender: (M for male F for female):";
		}break;
		case 3:{
			if( inputChecker.HeightEditting(text,currentUser,database,"set") ) {
				replymsg="Please enter your weight in kg:";
				currentUser.setSubStage(currentUser.getSubStage()+1);
			}
			else
				replymsg="Please enter reasonable numbers!";
		}break;
		case 4:{
			if( inputChecker.WeightEditting(text,currentUser,database,"set") ) {
				replymsg="Please enter your age in years old:";
				currentUser.setSubStage(currentUser.getSubStage()+1);
			}
			else
				replymsg="Please enter reasonable numbers!";
		}break;
		case 5:{
			if(inputChecker.AgeEditting(text, currentUser, database, "set")) {
       			replymsg="Your data has been recorded.\nInput anything to conitnue.";
				//database.pushUser(currentUser);
       			currentUser.setStage("Main");
       			currentUser.setSubStage(0);
			}
			else
				replymsg="Please enter reasonable numbers!";
		}break;
		default:{
				//log.info("Stage error.");
			}
		}

		database.updateUser(currentUser);//update user stage when the stage has been changed
		return replymsg;
	}

	public String mainStageHandler(  String text, Users currentUser, SQLDatabaseEngine database) {
		String replymsg = "";
		switch(currentUser.getSubStage()) {
		case 0:{
			if(currentUser.getBodyFat()==0) {
				replymsg = "Welcome to G8's Diet Planner!\n\n"
				+ "We provide serveral functions for you to keep your fitness."
				+ "Please type the number of function you wish to use. :)\n\n"
				+ "1 Living Habit Collector (INSERT YOUR DATA HERE)\n"
				+ "2 Diet Planner(Please complete 1 first)\n"
				+ "3 Healthpedia \n"
				/*+ "4 Feedback \n"
				+ "5 User Guide(recommended for first-time users)\n"*/
//				+ "6 Self Assessment(recommened for first-time users)\n\n"
				+ "Please enter your choice:(1-3)\n";
			}else {
				replymsg = "Welcome to G8's Diet Planner!\n\n"
						+ "We provide serveral functions for you to keep your fitness."
						+ "Please type the number of function you wish to use. :)\n\n"
						+ "1 Living Habot Editor\n"
						+ "2 Diet Planner\n"
						+ "3 Healthpedia \n"
						/*+ "4 Feedback \n"
						+ "5 User Guide(recommended for first-time users)\n"*/
//						+ "6 Self Assessment(recommened for first-time users)\n\n"
						+ "Please enter your choice:(1-3)";
			}
			if(CouponWarehouse.isCampaignStarted())
			 	replymsg +="\n\nThe campaign is now in progress!! We have "
										+ CouponWarehouse.getInstance().couponRemaining() + " coupons left!! Type \"friend\" to get invitation code and invite your friends!!"
										+ "And for new users please type \"code\" to send you and your inviter a coupon!!";
			//replymsg= msg);
			currentUser.setSubStage(currentUser.getSubStage()+1);
		}break;
		case 1:{
			String msg = null;
			switch(text) {
			case "1":{
				//move to diet planner
				replymsg = "Wellcome to Living Habit Collector! You can edit or input more detailed information"
						+ "about yourself. This can help us make a more precise suggestion for you!\n"
						+ "please follow the instructions below (type any to continue)";
				if(currentUser.getBodyFat()==0) {
					currentUser.setStage("LivingHabitCollector");
					currentUser.setSubStage(0);
				}else {
					currentUser.setStage("LivingHabitEditor");
					currentUser.setSubStage(0);
				}
			}break;
			case "2":{
				//move to diet planner
				replymsg = "Moving to Diet Planner...Input anything to continue...";
				currentUser.setStage( "DietPlanner");
				currentUser.setSubStage(0);
			}break;
			case "3":{
				//move to health pedia
				replymsg = "Moving to HealthPedia...Input anything to continue...";
				currentUser.setStage("HealthPedia");
				currentUser.setSubStage(0);
			}break;
			/*
			case "4":{
				//move to feedback
				replymsg = "Moving to FeedBack...Input anything to continue...";
				currentUser.setStage("FeedBack");
				currentUser.setSubStage(0);
			}break;
			case "5":{
				replymsg ="Moving to User Guide...Input anything to continue...";
				currentUser.setStage("UserGuide");
				currentUser.setSubStage(0);
				//move to user guide
			}break;*/
//			case "6":{
//				replymsg ="Moving to Self Assessment...Input anything to continue...";
//				currentUser.setStage("SelfAssessment");
//				currentUser.setSubStage(0);
//				//move to self assessment
//			}break

			case "code" :{
				if(CouponWarehouse.isCampaignStarted()){
					//if(currentUser.registerTime after compaign starting time)
					if(CouponWarehouse.getInstance().couponRemaining() >0 &&	CouponWarehouse.getInstance().canGetCouponFromCode(currentUser) ){//and other shit
						//CouponWarehouse.getInstance().isCodeRequestValid(currentUser)){
						replymsg = "Please input your invitation code:";

						currentUser.setStage("Coupon");
						currentUser.setSubStage(0);
					}
					else{
						replymsg = "You are not either qualified nor there is no coupons remaining.";
						currentUser.setStage("Main");
						currentUser.setSubStage(0);
					}
				}
				else{
					replymsg = "Invalid input! Please input numbers from 1 to 3!!";
					currentUser.setStage("Main");
					currentUser.setSubStage(0);
				}
			}break;
			case "6" :{
				if(!CouponWarehouse.isCampaignStarted()){
					String replyinfo =  CouponWarehouse.startCampaign();
					ArrayList<String> alluids = CouponWarehouse.getInstance().getNotifiableObservers(replyinfo).getData();
					replymsg = "@@" + replyinfo;
					for(String uid:alluids) replymsg += "@@" + uid;
				}
				else{replymsg = "Invalid input! Please input numbers from 1 to 3!!";}
				currentUser.setStage("Main");
				currentUser.setSubStage(0);
			}break;
			case "friend" :{
				if(CouponWarehouse.isCampaignStarted()){
					//if(currentUser.registerTime after compaign starting time
					replymsg = "This is your code for campaign:"
				 		+ CouponWarehouse.getInstance().issueCode(currentUser.getID());
			 	}
				else{
					replymsg = "Invalid input! Please input numbers from 1 to 3!!";
				}
				currentUser.setStage("Main");
				currentUser.setSubStage(0);

			}break;
			default:{replymsg = "Invalid input! Please input numbers from 1 to 3!!";}
			}
			//replymsg= msg);
		}break;
		}

		database.updateUser(currentUser);//update user stage when the stage has been changed
		return replymsg;
	}
	public String dietPlannerHandler(   String text, Users currentUser, SQLDatabaseEngine database) {
		String replymsg = "";
		switch(currentUser.getSubStage()) {
		case 0:{
			replymsg="Welcome to Diet Planner!\n"
					+"Please type the function choice you wish to use as below.\n\n"
									  +"1 Input daily diet by answering the questions\n"
									  +"2 Input daily diet by menu(formatted plain text)\n"
									  +"3 Input daily diet by menu(URL of JSON doc)\n"
									  +"4 Visualize your diet consumption in a specific day\n"
									  +"5 Design My Diet Plan\n"
									  +"6 Reminder\n"
									  +"7 Self-Assessment\n"
									  +"8 Insert food you what(which you find that is not in our database)\n"
									  +"(type other things to back to menu)";
			currentUser.setSubStage(-1);
		}break;
		case -1:{
			try{
				currentUser.setSubStage(Integer.parseInt(text));
				if (currentUser.getSubStage() >=1 && currentUser.getSubStage() <= 6) {
					replymsg= REDIRECT;
				}
				else {
					replymsg= "All changes recorded. Type anything to return to main menu.";
					//updata db
					currentUser.setStage("Main");//back to main
					currentUser.setSubStage(0);
				}
			}catch(Exception e) {
				replymsg= "All changes recorded. Type anything to return to main menu.";
				//update db
				currentUser.setStage("Main");//back to main
				currentUser.setSubStage(0);
			}
		}break;
		case 1:{
			Date date = new Date();
			SimpleDateFormat ft = new SimpleDateFormat("yyyyMMddHHmmss");
			ft.setTimeZone(TimeZone.getTimeZone("GMT+8"));
			time = ft.format(date);
			foodInput = new FoodInput(currentUser.getID(),time);
			replymsg= "Please enter the food name: ";
			currentUser.setSubStage(currentUser.getSubStage()+10);
		}break;
		case 11:{
			healthSearcher.setKeyword(text);
			if(healthSearcher.search()) {
				inputChecker.foodAdd(text, foodInput, database);
				replymsg= "Please enter the amount you intake(in g):";
				currentUser.setSubStage(currentUser.getSubStage()+1) ;
				}
			else
				replymsg= "Please enter a reasonable name!";
		}break;
		case 12:{
			if(inputChecker.amountAdd(text, foodInput, database)) {
				replymsg= "Please enter the price it roughly costs:";
				currentUser.setSubStage(currentUser.getSubStage()+1) ;
				}
			else
				replymsg= "Please enter a reasonable number!";
		}break;
		case 13:{
			if(inputChecker.priceAdd(text, foodInput, database)) {
				inputChecker.consumptionUpdate(healthSearcher,database,foodInput.getAmount(),currentUser.getID(),time,Double.valueOf(text));
				replymsg= "Your data has been recorded.\nInput anything to conitnue.";
				currentUser.setSubStage(0) ;
				}
			else
				replymsg= "Please enter a reasonable number!";
		}break;

		case 2:{
			replymsg= "Please state your menu in plain text:";
			currentUser.setSubStage(currentUser.getSubStage()+40);
		}break;
		case 42:{
			Date date;
			SimpleDateFormat ft;
			menuReader = new MenuReader();
			boolean fi = menuReader.readFromText(text,database);
			String[][] ingredients = menuReader.getIngredient();
			int[] price = menuReader.getPrice();
			int amount;
			int realPrice;
			boolean check = true;
			if(!fi){
				replymsg= "Please write in the right format(Food name + price)";
			}
			else {
				for (int k =0;k<ingredients.length; k++) {
					if (ingredients[k].length==0) {
						check = false;
						break;
					}
				}
				if(check) {
					for (int i = 0;i<ingredients.length; i++) {
						for (int j =0; j<ingredients[i].length; j++) {
							amount = (100/ingredients[i].length);
							realPrice = (price[i]/ingredients[i].length);

							date = new Date();
							ft = new SimpleDateFormat("yyyyMMddHHmmss");
							ft.setTimeZone(TimeZone.getTimeZone("GMT+8"));
							time = ft.format(date);
							foodInput = new FoodInput(currentUser.getID(),time);
							foodInput.setFoodName(ingredients[i][j]);
							healthSearcher.setKeyword(ingredients[i][j]);
							if (healthSearcher.search()) {
								foodInput.setAmount(amount);
								foodInput.setPrice(realPrice);
								database.pushDietRecord(foodInput);
								inputChecker.consumptionUpdate(healthSearcher,database,foodInput.getAmount(),currentUser.getID(),time,(double)realPrice);
								try{
									Thread.sleep(1000);
								}catch (InterruptedException e) {

								}
								finally{

								}
							}
						}
					}
					replymsg= "Your data has been recorded.\nInput anything to conitnue.";
					currentUser.setSubStage(0);
				}
				else{
					replymsg = "Sorry, we could not find your food data in our database\nIf you want, please help us to complete the database togather\nby typing 8 in Diet Planner\nInput anything to conitnue.";
					currentUser.setSubStage(0);
				}
			}
		}break;

		case 3:{
			replymsg= "Please state your menu in url of JSON:";
			currentUser.setSubStage(currentUser.getSubStage()+40);
		}break;
		case 43:{
			Date date;
			SimpleDateFormat ft;
			menuReader = new MenuReader();
			try {
				menuReader.readFromJSON(text);
			}catch(IOException ex) {
			}catch(JSONException ex2){
			}finally{
			}
			String[][] ingredients = menuReader.getIngredient();
			int[] price = menuReader.getPrice();
			int amount;
			int realPrice;
			if (price[0]!=0) {
				for (int i =0; i<ingredients.length; i++) {
					for(int j =0; j<ingredients[i].length;j++){
						date = new Date();
						ft = new SimpleDateFormat("yyyyMMddHHmmss");
						ft.setTimeZone(TimeZone.getTimeZone("GMT+8"));
						time = ft.format(date);
						foodInput = new FoodInput(currentUser.getID(),time);
						foodInput.setFoodName(ingredients[i][j]);
						healthSearcher.setKeyword(ingredients[i][j]);
						if (healthSearcher.search()) {
							amount = (100/ingredients[i].length);
							realPrice = (price[i]/ingredients[i].length);
							foodInput.setAmount(amount);
							foodInput.setPrice(realPrice);
							database.pushDietRecord(foodInput);
							inputChecker.consumptionUpdate(healthSearcher,database,foodInput.getAmount(),currentUser.getID(),time,(double)realPrice);
							try{
								Thread.sleep(1000);
							}catch (InterruptedException e) {

							}
							finally{

							}
						}
					}
				}
				replymsg= "Your data has been recorded.\nInput anything to conitnue.";
				currentUser.setSubStage(0);
			}
			else{
				replymsg= "Please make sure you enter the right url of JSON file!\nInput anything to conitnue.";
				currentUser.setSubStage(0);
			}
		}break;

		case 4:{
			replymsg= "Please enter the date(yyyymmdd): ";
			currentUser.setSubStage(currentUser.getSubStage()+18) ;
		}break;
		case 22:{
			if(inputChecker.dateCheck(text)) {
				String report = inputChecker.dietsearch(text,database,currentUser.getID());
				replymsg= "Your diet consumption in "+text+" : "+"\n"+report+"\n Input anything to conitnue.";
				currentUser.setSubStage(0) ;
			}
			else {
				replymsg= "Please enter a valid date(yyyymmdd): ";
			}
		}break;

		//substage 5: Diet plan genereator
		case 5:{
			/*
			 * Update corresponding user's "diet_plan" table based on his/her information
			 * */
			String user_id = currentUser.getID();
			//double budget = currentUser.getBudget();
			double budget = 100;
			if (database.search_diet_plan(user_id)) {
				replymsg = "You've already generated a diet plan:\n\n";
				replymsg += database.display_diet_plan(user_id, budget);
				// TODO: allow user to change it
			}
			else {
				boolean result = database.gen_plan(currentUser);
				if (result) {
					replymsg = "We have successfully generated a diet plan for you:\n\n";
					replymsg += database.display_diet_plan(user_id, budget);
				}
				else {
					replymsg  = "Since your personal information is not completed, we have genereated a default one for you.\n";
//					database.gen_plan_default(currentUser);//default
				}
			}
			replymsg+= "Type anything to go back to Diet Planner...\n";
			currentUser.setSubStage(0);
		}break;


		//substage 6: Reminder
		case 6:{
			replymsg = "Reminder List:\n";
			try {
				String user_id = currentUser.getID();
				//double budget = currentUser.getBudget();
				double budget = 100;

				// Instantiate a Date object
				Date dNow = new Date();
				SimpleDateFormat ft = new SimpleDateFormat ("yyyyMMdd");
				ft.setTimeZone(TimeZone.getTimeZone("GMT+8"));
				String date = ft.format(dNow);//20171102

				ArrayList<Double> plan_info = database.search_plan(user_id);
				if(plan_info.size()==0) {
					replymsg += "We can not find your diet plan, please design your own diet plan first!\n";
				}

				double meat_serve = plan_info.get(5);
				double milk_serve = plan_info.get(6);

				//ArrayList<Integer> current_info = new ArrayList<Integer>();
				ArrayList<Double> current_info = database.search_current(user_id, date); // diet current status

				//User hasn't input any diet consumption
				if(current_info.size() == 0) {
					double zero = 0;
					for (int i = 0; i<4; i++) {
						current_info.add(zero);
					}
				}

				for (int i = 0; i < 3/*plan_info.size()*/; i++) {
					//fiber|energy|protein
					double diff = plan_info.get(i) - current_info.get(i);
					//fiber
					if (i==0) {
						replymsg += "######\n";
						replymsg += "Fiber: ";
						if (plan_info.get(i) > current_info.get(i)) {
							replymsg += String.format("You still need to consume %.2f g more", diff);
							replymsg = replymsg + ", try to eat more vegetables & legumes/beans or fruit!\n";
						}
						else
							replymsg += "Finish!\n";
					}
					//energy
					else if (i== 1){
						replymsg += "######\n";
						replymsg += "Energy: ";
						if (plan_info.get(i) > current_info.get(i)) {
							replymsg += String.format("You still need to consume %.2f kcal", diff);
							replymsg += ", try to eat more Grain (cereal) foods, mostly wholegrain!\n";
						}
						else
							replymsg += "Finish!\n";
					}
					//protein: Meat + Milk
					else{
						replymsg += "######\n";
						replymsg += "Meat: ";
						if (plan_info.get(i) > current_info.get(i)) {
							replymsg += String.format("You still need to consume %.2f g", diff*meat_serve/(meat_serve+milk_serve));
							replymsg += ", try to eat more Lean meat and poultry, fish, eggs, nuts and seeds!\n";
						}
						else
							replymsg += "Finish!\n";

						replymsg += "######\n";
						replymsg += "Milk: ";
						if (plan_info.get(i) > current_info.get(i)) {
							replymsg += String.format("You still need to consume %.2f g", diff*milk_serve/(meat_serve+milk_serve));
							replymsg += ", try to consume more milk, yoghurt, cheese and/or alternatives!\n\n";
						}
						else
							replymsg += "Finish!\n\n";
					}
				}

				//generate plan based on budget
				//expensive
				if (current_info.get(3) < budget*2/3){
					replymsg = replymsg + "Budget ~="
							+ String.format("%.2f", budget*1/3) + "!\n";
				}
				//cheap
				else if(budget-current_info.get(3) > 0) {
					replymsg = replymsg + "Budget < "
							+ Double.toString(budget-current_info.get(3))+ "!\n";
				}
				//over budget
				else {
					replymsg = replymsg + "You are OVER budget now!\n";
				}

			} catch (Exception e) {
				//log.info("plan not found: {}", e.toString());
				replymsg += "Exception!\n";
			}
			replymsg += "Type 1 to go back to Diet Planner...\nType other things to return to main menu...\n";
			currentUser.setSubStage(currentUser.getSubStage()+8);
		}break;
		case 14:{
			try {
				if(Integer.parseInt(text) == 1) currentUser.setSubStage(0);
				else {
					throw new Exception("Going back to menu");// handle parseInt()'s exception
				}
			}catch(Exception ex) {
				currentUser.setStage("Main");
				currentUser.setSubStage(0);
			}finally { replymsg= REDIRECT;}
		}break;

		//subStage7: self Assessment;
		case 7:{
			suggestion = "";
			(currentUser).setAssessmentScore(0);
			replymsg = replymsg + "This quiz will reveal about the your eating habits by answering 10 true or false questions. "
					+ "\nPlease reply 'T' as ture and 'F' as false according your eating habits."
					+ "\nReply anything to start or reply 'q' to reutrn to the main menu...";
			currentUser.setSubStage(-2);
		}break;



		//subStage8 : insert user-defined data
		case 8:{
			foodInfo = new FoodInfo();
			replymsg= "Please enter the food name: ";
			currentUser.setSubStage(currentUser.getSubStage()+8);
		}break;
		case 16:{
			if(inputChecker.foodAdd(text,foodInfo,database)) {
				replymsg= "Please enter rough energy in 100g: ";
				currentUser.setSubStage(currentUser.getSubStage()+1);
			}
			else replymsg= "Please enter valid food name: ";
		}break;
		case 17:{

			if(inputChecker.energyAdd(text,foodInfo,database)) {
				replymsg= "Please enter rough protein in 100g: ";
				currentUser.setSubStage(currentUser.getSubStage()+1);
			}
			else replymsg= "Please enter valid number: ";
		}break;
		case 18:{

			if(inputChecker.proteinAdd(text,foodInfo,database)) {
				replymsg= "Please enter rough fiber in 100g: ";
				currentUser.setSubStage(currentUser.getSubStage()+1);
			}
			else replymsg= "Please enter valid number: ";


		}break;
		case 19:{

			if(inputChecker.fiberAdd(text,foodInfo,database)) {
				replymsg= "Please enter rough price in 100g: ";
				currentUser.setSubStage(currentUser.getSubStage()+1);
			}
			else replymsg= "Please enter valid number: ";
		}break;
		case 20:{

			if(inputChecker.priceAdd(text,foodInfo,database)) {
				replymsg= "Your data has been recorded.\\nInput anything to conitnue.";
				currentUser.setSubStage(0);
			}
			else replymsg= "Please enter valid number: ";

		}break;
		case -2:{
			if(text.equalsIgnoreCase("q")) {
				currentUser.setStage("Main");
				currentUser.setSubStage(0);
				replymsg = replymsg + "Heading to Main menu...";
				return replymsg;
				}
				//else the quiz start
				replymsg = replymsg +  "Then let's start the quiz ;) \n"
						+ question[0];
				currentUser.setSubStage(501);
		}break;
		case 511:{
			int score = (currentUser).getAssessmentScore();
			if(score >= 90) {
				replymsg = replymsg + "The healthy level of your eating habit is: A \n Congratulations! "
						+ "You have achieve a deep understanding about the healthy diet and attach great importance to it.";

			}
			else if(score >= 70) {
				replymsg = replymsg + "The healthy level of your eating habit is: B \n That's not bad. "
						+ "Your eating habit is cool, but it can still be improve to a better level."
						+ "Here comes some of the advice:\n" + suggestion;
			}
			else if(score >= 40) {
				replymsg = replymsg + "The healthy level of your eating habit is: C \n You'd better pay attention. "
						+ "Your eating habit is OK if you are always very fit, although some of those habit might be harmful to youre body.\n"
						+ "Here comes some of the advice:\n" + suggestion;
			}
			else {
				replymsg = replymsg + "Oops The healthy level of your eating habit is: D \n "
						+ "You are strongly recommended to change those bad habits right now. "
						+ "Here comes some of the advice:\n" + suggestion;
				}
			replymsg = replymsg + "Would you like to customize your personal plan now? "
					+ "\n reply 'Y' to customize or \n reply anything to return to the main menu";
			currentUser.setSubStage(currentUser.getSubStage()+1);
		}break;
		case 512:{
			if(text.equalsIgnoreCase("Y")) {
				currentUser.setSubStage(0);
				currentUser.setStage("DietPlanner");
				replymsg = replymsg + "Heading to DietPlanner...";
			}else {
				currentUser.setSubStage(0);
				currentUser.setStage("Main");
				replymsg = replymsg + "Heading to Main menu...";
			}
		}break;

		default:{
			if(text.equalsIgnoreCase("T")) {
				(currentUser).setAssessmentScore((currentUser).getAssessmentScore()+10);
				suggestion = suggestion + feedback[currentUser.getSubStage()-501][1];
			}
			else if(text.equalsIgnoreCase("F")){
				suggestion = suggestion + feedback[currentUser.getSubStage()-501][0];
			}
			else if(text.equalsIgnoreCase("q")) {
				currentUser.setStage("Main");
				currentUser.setSubStage(0);
				replymsg= "Heading to mainMenu... \nreply anything to get back to mainMenu...";
				return replymsg;
			}
			else {
				replymsg= "Please reply a valid answer(T/F)";
				return replymsg;
			}
			if(currentUser.getSubStage() == 510) {
				replymsg = "Congratulations that you have finished the quiz!:)\n"
						+ "reply anything to get the feedback";
				currentUser.setSubStage(currentUser.getSubStage()+1);
				return replymsg;
			}
			replymsg= question[currentUser.getSubStage()-500];
			currentUser.setSubStage(currentUser.getSubStage()+1);
		}break;
		}

		database.updateUser(currentUser);//update user stage when the stage has been changed
		return replymsg;

	}




	public String livingHabitCollectorEditor(   String text, Users currentUser, SQLDatabaseEngine database) {
		String replymsg = "";
		switch(currentUser.getSubStage()) {
		case 0:{
			replymsg="Looks like you have already input your data. "
											+ "Do you wish to edit it? Please type the choice you wish to edit below:\n\n"
											+ "1 Edit Age\n"
											+ "2 Edit Name\n"
											+ "3 Edit Weight\n"
											+ "4 Edit Height\n"
											+ "5 Edit Bodyfat\n"
											// + "6 Edit Exercise Amount\n"
											// + "7 Edit Calories Consumption\n"
											// + "8 Edit Carbohydrate Consumption\n"
											// + "9 Edit Protein Consumption\n"
											// + "10 Edit Vegtable/Fruit Consumption \n"
											// + "11 Edit Other Information about you\n"
											+ "6 Show all your states \n"
											+ "(type other things to back to menu)";
			currentUser.setSubStage(-1);
		}break;
		case -1:{ // redirecting stage
			try{
				currentUser.setSubStage(Integer.parseInt(text));
				if (currentUser.getSubStage() >=1 && currentUser.getSubStage() <= 12) {
					replymsg=REDIRECT;
				}
				else {
					replymsg="All changed recorded. Type anything to return to main menu.";
					//updata db
					currentUser.setStage("Main");//back to main
					currentUser.setSubStage(0);
					database.updateUser(currentUser);
				}
			}catch(Exception e) {
				replymsg="All changed recorded. Type anything to return to main menu.";
				//update db
				currentUser.setStage("Main");//back to main
				currentUser.setSubStage(0);
				database.updateUser(currentUser);
			}
		}break;
		case 1:{
			replymsg="Please enter the age you wish to change to:";
			currentUser.setSubStage(currentUser.getSubStage()+20) ;
		}break;
		case 2:{
			replymsg="Please enter the name you wish to change to:";
			currentUser.setSubStage(currentUser.getSubStage()+20) ;
		}break;
		case 3:{
			replymsg="Please enter the weight you wish to change to:";
			currentUser.setSubStage(currentUser.getSubStage()+20) ;
		}break;
		case 4:{
			replymsg="Please enter the height you wish to change to:";
			currentUser.setSubStage(currentUser.getSubStage()+20) ;
		}break;
		case 5:{
			replymsg="Please enter the bodyfat(%) you wish to change to:";
			currentUser.setSubStage(currentUser.getSubStage()+20) ;
		}break;
		// case 6:{
		// 	replymsg="Please enter the hours of excercise per day you wish to change:";
		// 	currentUser.setSubStage(currentUser.getSubStage()+20) ;
		// }break;
		// case 7:{
		// 	replymsg="Please enter the calories consumption(kcal) per day you wish to change to:";
		// 	currentUser.setSubStage(currentUser.getSubStage()+20) ;
		// }break;
		// case 8:{
		// 	replymsg="Please enter the carbohydrates consumption(g) per day you wish to change to:";
		// 	currentUser.setSubStage(currentUser.getSubStage()+20) ;
		// }break;
		// case 9:{
		// 	replymsg="Please enter the protein consumption(g) per day you wish to change to:";
		// 	currentUser.setSubStage(currentUser.getSubStage()+20) ;
		// }break;
		// case 10:{
		// 	replymsg="Please enter the veg/fruit consumption(servings) per day you wish to change to:";
		// 	currentUser.setSubStage(currentUser.getSubStage()+20) ;
		// }break;
		// case 11:{
		// 	replymsg="Please enter other information about yourself that you wish to change to:";
		// 	currentUser.setSubStage(currentUser.getSubStage()+20) ;
		// }break;
		case 6:{
			replymsg="These are all about your body:\n\n" 	+ currentUser.toString()+"\nType any to continue.";
			currentUser.setSubStage(0);
		}break;
		case 21:{
			if(inputChecker.AgeEditting(text, currentUser, database, "update")) {
       			replymsg="Your data has been recorded.\nInput anything to conitnue.";
       			currentUser.setSubStage(0);
			}
			else
				replymsg="Please enter reasonable numbers!";
		}break;
		case 22:{
			if(inputChecker.NameEditting(text, currentUser, database, "update")) {
       			replymsg="Your data has been recorded.\nInput anything to conitnue.";
       			currentUser.setSubStage(0);
			}
			else
				replymsg="Please enter reasonable numbers!";
		}break;
		case 23:{
			if(inputChecker.WeightEditting(text, currentUser, database, "update")) {
	       		replymsg="Your data has been recorded.\nInput anything to conitnue.";
	       		currentUser.setSubStage(0);
			}
			else
				replymsg="Please enter reasonable numbers!";
		}break;
		case 24:{
			if(inputChecker.HeightEditting(text, currentUser, database, "update")) {
	    		replymsg="Your data has been recorded.\nInput anything to conitnue.";
	       		currentUser.setSubStage(0);
			}
			else
				replymsg="Please enter reasonable numbers!";
		}break;
		case 25:{
			if(inputChecker.BodyfatEditting(text, currentUser, database, "update")) {
	      		replymsg="Your data has been recorded.\nInput anything to conitnue.";
	       		currentUser.setSubStage(0);
			}
			else
				replymsg="Please enter reasonable numbers!";
		}break;
		// case 26:{
		// 	if(inputChecker.ExerciseEditting(text, currentUser, database, "update")) {
	    //    		replymsg="Your data has been recorded.\nInput anything to conitnue.";
	    //    		currentUser.setSubStage(0);
        // 		}
		// 	else
		// 		replymsg="Please enter reasonable numbers!";
		// }break;
		// case 27:{
		// 	if(inputChecker.CaloriesEditting(text, currentUser, database, "update")) {
	    //    		replymsg="Your data has been recorded.\nInput anything to conitnue.";
	    //    		currentUser.setSubStage(0);
        // 		}
		// 	else
		// 		replymsg="Please enter reasonable numbers!";
		// }break;
		// case 28:{
		// 	if(inputChecker.CarbsEditting(text, currentUser, database, "update")) {
	    //    		replymsg="Your data has been recorded.\nInput anything to conitnue.";
	    //    		currentUser.setSubStage(0);
        // 		}
		// 	else
		// 		replymsg="Please enter reasonable numbers!";
		// }break;
		// case 29:{
		// 	if(inputChecker.ProteinEditting(text, currentUser, database, "update")) {
	    //    		replymsg="Your data has been recorded.\nInput anything to conitnue.";
	    //    		currentUser.setSubStage(0);
        // 		}
		// 	else
		// 		replymsg="Please enter reasonable numbers!";
		// }break;
		// case 30:{
		// 	if(inputChecker.VegfruitEditting(text, currentUser, database, "update")) {
	    //    		replymsg="Your data has been recorded.\nInput anything to conitnue.";
	    //    		currentUser.setSubStage(0);
        // 		}
		// 	else
		// 		replymsg="Please enter reasonable numbers!";
		// }break;
		// case 31:{
		// 	if(inputChecker.OtherinfoEditting(text, currentUser, database, "update")) {
       	// 		replymsg="Your data has been recorded.\nInput anything to conitnue.";
       	// 		currentUser.setSubStage(0);
		// 	}
		// 	else
		// 		replymsg="Please enter some with characters less then 1000!";
		// }break;
		default:{
			replymsg="Some problem occurs.Type any key to return to main menu.";
			//log.info("Stage Error!!");
			currentUser.setStage("Main");//back to main
			currentUser.setSubStage(0);
		}break;
		}

		database.updateUser(currentUser);//update user stage when the stage has been changed
		return replymsg;
	}

	public String livingHabitCollectorHandler(  String text, Users currentUser, SQLDatabaseEngine database) {
		String replymsg = "";
		switch(currentUser.getSubStage()){
		case 0:{
			//currentUser = new DetailedUser(currentUser);
			replymsg = "Please tell us your body fat:(in %)";
			currentUser.setSubStage(currentUser.getSubStage()+1);
		}break;
		case 1:{
			if(inputChecker.BodyfatEditting(text, currentUser, database, "update")) {
				replymsg = "All set and recorded. Type anything to return to main menu.";
				currentUser.setStage("Main");//back to main
				currentUser.setSubStage(0);
				database.updateUser(currentUser);
        		}
			else
				replymsg = "Please enter reasonable numbers!";

		}break;
		// }break;
		// case 2:{
		// 	if(inputChecker.CaloriesEditting(text, currentUser, database, "update")) {
        // 		replymsg = "Please tell us your average daily carbohydrates consumption(roughly in g):";
        // 		currentUser.setSubStage(currentUser.getSubStage()+1);
        // 		}
		// 	else
		// 		replymsg = "Please enter reasonable numbers!";
		// }break;
		// case 3:{
		// 	if(inputChecker.CarbsEditting(text, currentUser, database, "update")) {
        // 			replymsg = "Please tell us your average daily protein consumption(roughly in g):";
        // 			currentUser.setSubStage(currentUser.getSubStage()+1);
        // 			}
		// 		else {
		// 			replymsg = "Please enter reasonable numbers!";
		// 		}
		// }break;
		// case 4:{
		// 	if(inputChecker.ProteinEditting(text, currentUser, database, "update")) {
        // 			replymsg = "Please tell us your average daily vegetable/fruit consumption(in serving):";
        // 			currentUser.setSubStage(currentUser.getSubStage()+1);
        // 			}
		// 		else {
		// 			replymsg = "Please enter reasonable numbers!";
		// 		}
		// }break;
		// case 5:{
		// 	if(inputChecker.VegfruitEditting(text, currentUser, database, "update")) {
        // 			replymsg = "Do you eat breakfast?(y/n)";
        // 			currentUser.setSubStage(currentUser.getSubStage()+1);
        // 			}
		// 		else {
		// 			replymsg = "Please enter reasonable numbers!";
		// 		}
		// }break;
		// case 6:{
		// 	boolean input = false;
		// 	if (text.charAt(0)=='y' || text.charAt(0)=='Y') input = true;
		// 	else if( text.charAt(0)=='n'|| text.charAt(0)=='N') input = false;
		// 	else { replymsg = "Do you eat breakfast?(y/n)"; return replymsg;}
        //
		// 	(currentUser).setEatingHabits(input,0);
		// 	replymsg = "Do you eat lunch?(y/n)";
		// 	currentUser.setSubStage(currentUser.getSubStage()+1);
		// }break;
		// case 7:{
		// 	boolean input = false;
		// 	if (text.charAt(0)=='y' ||  text.charAt(0)=='Y') input = true;
		// 	else if( text.charAt(0)=='n'|| text.charAt(0)=='n') input = false;
		// 	else { replymsg = "Do you eat lunch?(y/n)"; return replymsg;}
        //
		// 	(currentUser).setEatingHabits(input,1);
		// 	replymsg = "Do you eat afternoon tea?(y/n)";
		// 	currentUser.setSubStage(currentUser.getSubStage()+1);
		// }break;
		// case 8:{
		// 	boolean input = false;
		// 	if (text.charAt(0)=='y' ||  text.charAt(0)=='Y') input = true;
		// 	else if( text.charAt(0)=='n'|| text.charAt(0)=='N') input = false;
		// 	else { replymsg = "Do you eat afternoon tea?(y/n)"; return replymsg;}
        //
		// 	(currentUser).setEatingHabits(input,2);
		// 	replymsg = "Do you eat dinner?(y/n)";
		// 	currentUser.setSubStage(currentUser.getSubStage()+1);
		// }break;
		// case 9:{
		// 	boolean input = false;
		// 	if (text.charAt(0)=='y' ||  text.charAt(0)=='Y') input = true;
		// 	else if( text.charAt(0)=='n'|| text.charAt(0)=='N') input = false;
		// 	else { replymsg = "Do you eat dinner?(y/n)"; return replymsg;}
        //
		// 	(currentUser).setEatingHabits(input,3);
		// 	replymsg = "Do you eat midnight snacks?(y/n)";
		// 	currentUser.setSubStage(currentUser.getSubStage()+1);
		// }break;
		// case 10:{
		// 	boolean input = false;
		// 	if (text.charAt(0)=='y' ||  text.charAt(0)=='Y') input = true;
		// 	else if( text.charAt(0)=='n'|| text.charAt(0)=='N') input = false;
		// 	else { replymsg = "Do you eat midnight snacks?(y/n)"; return replymsg;}
        //
		// 	(currentUser).setEatingHabits(input,4);
		// 	replymsg = "Do you eat any extra meals?(y/n)";
		// 	currentUser.setSubStage(currentUser.getSubStage()+1);
		// }break;
		// case 11:{
		// 	boolean input = false;
		// 	if (text.charAt(0)=='y' ||  text.charAt(0)=='Y') input = true;
		// 	else if( text.charAt(0)=='n'|| text.charAt(0)=='N') input = false;
		// 	else { replymsg = "Do you eat any extra meals?(y/n)"; return replymsg;}
        //
		// 	(currentUser).setEatingHabits(input,5);
		// 	replymsg = "How many hours per day do you exercise in a weekly average?";
		// 	currentUser.setSubStage(currentUser.getSubStage()+1);
		// }break;
		// case 12:{
		// 	if(inputChecker.ExerciseEditting(text, currentUser, database, "update")) {
		// 		replymsg = "Any other infomation about your body you wish to let us know?(in 1000 characters)";
		// 		currentUser.setSubStage(currentUser.getSubStage()+1);
        // 		}
		// 	else
		// 		replymsg = "Please enter reasonable numbers!";
        //
		// }break;
		// case 13:{
		// 	if(inputChecker.OtherinfoEditting(text, currentUser, database, "update")) {
		// 		replymsg = "All set and recorded. Type anything to return to main menu.";
		// 		currentUser.setStage("Main");//back to main
		// 		currentUser.setSubStage(0);
		// 		database.updateUser(currentUser);
		// 	}
		// 	else
		// 		replymsg = "Please enter something in 1000 characters!!";
		// }break;

		default:
			break;
		}

		database.updateUser(currentUser);//update user stage when the stage has been changed
		return replymsg;
	}

	public String healthPediaHandler(  String text, Users currentUser, SQLDatabaseEngine database) {
		String replymsg = "";
		//user key word input
		//String
		switch(currentUser.getSubStage()) {
		case 0:{
			replymsg ="Welcome to HealthPedia! You are welcome to query any thing about food!\n"
					+ "Please type the function choice you wish to use as below.\n\n"
					+ "1 Food Searcher\n"
					+ "Others May be open later...\n"
					+ "Type other things to go back to main menu.";
			currentUser.setSubStage(currentUser.getSubStage()-1);
		}break;

		case -1:{ // redirecting stage
			try{
				currentUser.setSubStage(Integer.parseInt(text));
				if (currentUser.getSubStage() >=1 && currentUser.getSubStage() <= 4) {
					replymsg = REDIRECT;
				}
				else {
					replymsg = REDIRECT;
					currentUser.setStage("Main");//back to main
					currentUser.setSubStage(0);
				}
			}catch(Exception e) {
				replymsg = REDIRECT;
				currentUser.setStage("Main");//back to main
				currentUser.setSubStage(0);
			}
		}break;

		case 1:{
			replymsg = "Please enter the name of food you wish to know about:";
			currentUser.setSubStage(currentUser.getSubStage()+10);
		}break;
		case 11:{
			healthSearcher.setKeyword(text);
			if(healthSearcher.search()) {
				replymsg = healthSearcher.getUnit()+":\n"
						+"Name of the searched food:"+healthSearcher.getFoodName()+"\n"
						+"Energy: "+healthSearcher.getEnergy()+"kcal\n"
						+"Carbohydragate: "+healthSearcher.getCarbohydrate()+"g\n"
						+"Protein:"+healthSearcher.getProtein()+"g\n"
						+"Fat: "+healthSearcher.getFat()+"g\n"
						+"Suger: "+healthSearcher.getSugar()+"g\n"
						+"Water:"+healthSearcher.getWater()+"g\n"
						+"Sodium:"+healthSearcher.getSodium()+"mg\n"
						+"Calcium"+healthSearcher.getCalcium()+"mg\n"
						+"\nType 1 to search for other food.\nType other thing to go back to Healthpedia.";
				currentUser.setSubStage(currentUser.getSubStage()+10);
			}
			else {
				replymsg = "Food not found.\nType 1 to search for other food.\nType other thing to go back to Healthpedia.";
				currentUser.setSubStage(currentUser.getSubStage()+10);
			}
		}break;
		case 21:{
			try {
				if(Integer.parseInt(text) == 1) currentUser.setSubStage(1);
				else {
					throw new Exception("Going back to menu");
				}
			}catch(Exception ex) {
				currentUser.setSubStage(0);
			}finally { replymsg = REDIRECT;}
		}break;
		default:break;
		}
		database.updateUser(currentUser);//update user stage when the stage has been changed
		return replymsg;
	}
	/*
	public String feedBackHandler(  String text, Users currentUser, SQLDatabaseEngine database) {
		String replymsg = "";
		replymsg = "All set. Type anything to return to main menu...";
		currentUser.setStage("Main");//back to main
		currentUser.setSubStage(0);
		database.updateUser(currentUser);//update user stage when the stage has been changed
		return replymsg;
	}
	public String userGuideHandler(   String text, Users currentUser, SQLDatabaseEngine database) {
		String replymsg = "";
		replymsg = "All set. Type anything to return to main menu...";
		currentUser.setStage("Main");//back to main
		currentUser.setSubStage(0);
		database.updateUser(currentUser);//update user stage when the stage has been changed
		return replymsg;
	}*/
	public String couponHandler(   String text, Users currentUser, SQLDatabaseEngine database) {
		String replymsg = "";
		if(CouponWarehouse.getInstance().isCodeValid(currentUser.getID(),text) && !CouponWarehouse.getInstance().checkSelf(currentUser.getID(),text) ){
			 Coupon newCoupon = CouponWarehouse.getInstance().issueCoupon(currentUser.getID(),text);
			// if ( ! CouponWarehouse.getInstance().isNewUser(newCoupon.getInviter()) )
			  replymsg += "@@" + newCoupon.getCoupon();
			  if(CouponWarehouse.getInstance().notGotCoupon(newCoupon.getInviter())) replymsg += "@@"+newCoupon.getInviter();
				else replymsg += "@@" + "-1"; // dummy representation for not sending

			 	replymsg += "@@" + newCoupon.getInvitee();
									log.info("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
									log.info(replymsg);
									log.info("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");

		}
		else{
			replymsg = "oops! Your code is either invalid or used. (You can not get coupon by the code issued to yourself)";
		}
		currentUser.setStage("Main");//back to main
		currentUser.setSubStage(0);
		database.updateUser(currentUser);//update user stage when the stage has been changed
		return replymsg;
	}

	// this is self assessment Handler function
//	public String selfAssessmentHandler(  Event event, String text, Users currentUser, SQLDatabaseEngine database) {
//		String replymsg = "";
//		switch (currentUser.getSubStage()) {
//			case 0:{
//				suggestion = "";
//				(currentUser).setAssessmentScore(0);
//				replymsg = replymsg + "This quiz will reveal about the your eating habits by answering 10 true or false questions. "
//						+ "\nPlease reply 'T' as ture and 'F' as false according your eating habits."
//						+ "\nReply anything to start or reply 'q' to reutrn to the main menu...";
//				currentUser.setSubStage(-1);
//			}break;
//			case -1:{
//					if(text.equals("q")) {
//					currentUser.setStage("Main");
//					currentUser.setSubStage(0);
//					replymsg = replymsg + "Heading to Main menu...";
//					return replymsg;
//					}
//					//else the quiz start
//					replymsg = replymsg +  "Then let's start the quiz ;) \n"
//							+ question[0];
//					currentUser.setSubStage(1);
//			}break;
//			case 11:{
//				int score = (currentUser).getAssessmentScore();
//				if(score >= 90) {
//					replymsg = replymsg + "The healthy level of your eating habit is: A \n Congratulations! "
//							+ "You have achieve a deep understanding about the healthy diet and attach great importance to it.";
//
//				}
//				else if(score >= 70) {
//					replymsg = replymsg + "The healthy level of your eating habit is: B \n That's not bad. "
//							+ "Your eating habit is cool, but it can still be improve to a better level."
//							+ "Here comes some of the advice:\n" + suggestion;
//				}
//				else if(score >= 40) {
//					replymsg = replymsg + "The healthy level of your eating habit is: C \n You'd better pay attention. "
//							+ "Your eating habit is OK if you are always very fit, although some of those habit might be harmful to youre body.\n"
//							+ "Here comes some of the advice:\n" + suggestion;
//				}
//				else {
//					replymsg = replymsg + "Oops The healthy level of your eating habit is: D \n "
//							+ "You are strongly recommended to change those bad habits right now. "
//							+ "Here comes some of the advice:\n" + suggestion;
//					}
//				replymsg = replymsg + "Would you like to customize your personal plan now? "
//						+ "\n reply 'Y' to customize or \n reply anything to return to the main menu";
//				currentUser.setSubStage(currentUser.getSubStage()+1);
//			}break;
//			case 12:{
//				if(text.equalsIgnoreCase("Y")) {
//					currentUser.setSubStage(0);
//					currentUser.setStage("DietPlanner");
//					replymsg = replymsg + "Heading to DietPlanner...";
//				}else {
//					currentUser.setSubStage(0);
//					currentUser.setStage("Main");
//					replymsg = replymsg + "Heading to Main menu...";
//				}
//			}break;
//			default:{
//				if(text.equalsIgnoreCase("T")) {
//					(currentUser).setAssessmentScore((currentUser).getAssessmentScore()+10);
//					suggestion = suggestion + feedback[currentUser.getSubStage()-1][1];
//				}
//				else if(text.equalsIgnoreCase("F")){
//					suggestion = suggestion + feedback[currentUser.getSubStage()-1][0];
//				}
//				else if(text.equalsIgnoreCase("q")) {
//					currentUser.setStage("Main");
//					currentUser.setSubStage(0);
//					replymsg= "Heading to mainMenu... \nreply anything to get back to mainMenu...";
//					return replymsg;
//				}
//				else {
//					replymsg= "Please reply a valid answer(T/F)";
//					return replymsg;
//				}
//				if(currentUser.getSubStage() == 10) {
//					replymsg = "Congratulations that you have finished the quiz!:)\n"
//							+ "reply anything to get the feedback";
//					currentUser.setSubStage(currentUser.getSubStage()+1);
//					return replymsg;
//				}
//				replymsg= question[currentUser.getSubStage()];
//				currentUser.setSubStage(currentUser.getSubStage()+1);
//			}
//
//		}
//		return replymsg;
//	}
	public void unfollowHandler(Users currentUser , SQLDatabaseEngine database){
		currentUser.setStage("Main");
		currentUser.setSubStage(0);
		database.updateUser(currentUser);//update user stage when the stage has been changed
	}

	public String followHandler( Event event, Users currentUser , SQLDatabaseEngine database){
		String msg = "";
		try{
			currentUser = database.searchUser(event.getSource().getUserId());
			if (currentUser.getAge()==0) {
				currentUser.setStage("Init");
				currentUser.setSubStage(0);
				msg = "User data reloaded. Type anything to continue...";
			}
			else{
				currentUser.setStage("Main");
				currentUser.setSubStage(0);
				msg = "User data reloaded. Type anything to continue...";
			}
		}catch(Exception e){
			msg = "Welcome!!\nTo start using our Personal Diet services, please follow the instructions below.\n\n"
					+ "Please first tell us some of your personal information: type anything to continue";
			currentUser = new Users(event.getSource().getUserId());
			database.pushUser(currentUser); // push new user
			CouponWarehouse.getInstance().register(currentUser);
		}finally {
			database.updateUser(currentUser);//update user stage when the stage has been changed
		}
		return msg;
	}

}
