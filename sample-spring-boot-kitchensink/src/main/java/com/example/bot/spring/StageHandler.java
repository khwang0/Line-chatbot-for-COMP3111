package com.example.bot.spring;

import java.io.IOException;

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
public class StageHandler {
	private String time;
	private InputChecker inputChecker = new InputChecker();
	private foodInput foodinput = null;
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
			{"Try to make your snacks not that harmful to your health or stop it.\n","Even if nuts are generally healthy, never eat them too much or the intake of fat would be amazingly huge.\n"},
			{"Eating three meals a day prevents overeating in a single meal and secures more energy throughout the day. Its importance is beyond your imagination\n",""},
			{"Other nutrients like vitamins and minerals are also necessary to your body's process of metabolism, please make your diet varied.\n",""},
			{"Seafood contains lowfat and plenty of trace element that is good to our body. It's not a bad idea to eat some seafood.\n",""},
			{"Processed foods may contain high levels of salt, sugar and fat. Therefore, you're recommended to have some fresh food instead.\n",""},
			{"Based on the fact that your daily menu could be improved, our diet planner can help you improve it.\n",""}};
	private String suggestion = "";
	private HealthSearch healthSearcher = new HealthSearch();
	private String REDIRECT = "Redirecting...type anything to continue.";


	public String initStageHandler(String replyToken, Event event, String text, Users currentUser, SQLDatabaseEngine database) {
		String replymsg = "";
		switch(currentUser.getSubStage()) {
		case 0:{
			if(text.equals("1")) {
        		replymsg = "Please enter your name: (1-32 characters)";
        		currentUser.setSubStage(currentUser.getSubStage()+1);
        	}
        	else {
        		replymsg = "I will be deactivated. To reactivate me, please block->unblock me. Bye.";
        		currentUser.setStage("");
        		currentUser.setSubStage(0);
        		currentUser = null;
        	}
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
				database.updateUser(currentUser);
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
		return replymsg;
	}

	public String mainStageHandler(String replyToken, Event event, String text, Users currentUser, SQLDatabaseEngine database) {
		String replymsg = "";
		switch(currentUser.getSubStage()) {
		case 0:{
			if(! (currentUser instanceof DetailedUser)) {
				replymsg = "Welcome to G8's Diet Planner!\n\n"
				+ "We provide serveral functions for you to keep your fitness."
				+ "Please type the number of function you wish to use. :)\n\n"
				+ "1 Living Habit Collector (INSERT YOUR DATA HERE)\n"
				+ "2 Diet Planner(Please complete 1 first)\n"
				+ "3 Healthpedia \n"
				+ "4 Feedback \n"
				+ "5 User Guide(recommended for first-time users)\n"
//				+ "6 Self Assessment(recommened for first-time users)\n\n"
				+ "Please enter your choice:(1-5)";
			}else {
				replymsg = "Welcome to G8's Diet Planner!\n\n"
						+ "We provide serveral functions for you to keep your fitness."
						+ "Please type the number of function you wish to use. :)\n\n"
						+ "1 Living Habot Editor\n"
						+ "2 Diet Planner\n"
						+ "3 Healthpedia \n"
						+ "4 Feedback \n"
						+ "5 User Guide(recommended for first-time users)\n"
//						+ "6 Self Assessment(recommened for first-time users)\n\n"
						+ "Please enter your choice:(1-5)";
			}
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
				if(!(currentUser instanceof DetailedUser)) {
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
			}break;
//			case "6":{
//				replymsg ="Moving to Self Assessment...Input anything to continue...";
//				currentUser.setStage("SelfAssessment");
//				currentUser.setSubStage(0);
//				//move to self assessment
//			}break;
			default:{replymsg = "Invalid input! Please input numbers from 1 to 4!!";}
			}
			//replymsg= msg);
		}break;
		}
		return replymsg;
	}
	public String dietPlannerHandler(String replyToken, Event event, String text, Users currentUser, SQLDatabaseEngine database) {
		String replymsg = "";
		switch(currentUser.getSubStage()) {
		case 0:{
			replymsg="Welcome to Diet Planner!\n"
					+"Please type the function choice you wish to use as below.\n\n"
									  +"1 Input daily diet\n"
									  +"2 Visualize your diet consumption in a specific day\n"
									  +"3 Design My Diet Plan\n"
									  +"4 Reminder\n"
									  +"5 Self-Assessment\n"
									  +"(type other things to back to menu)";
			currentUser.setSubStage(-1);
		}break;
		case -1:{
			try{
				currentUser.setSubStage(Integer.parseInt(text));
				if (currentUser.getSubStage() >=1 && currentUser.getSubStage() <= 5) {
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
			foodinput = new foodInput(event.getSource().getUserId(),time);
			replymsg= "Please enter the food name: ";
			currentUser.setSubStage(currentUser.getSubStage()+10);
		}break;
		case 11:{
			healthSearcher.setKeyword(text);
			if(healthSearcher.search()) {
				inputChecker.foodAdd(text, foodinput, database);
				replymsg= "Please enter the amount you intake(in g):";
				currentUser.setSubStage(currentUser.getSubStage()+1) ;
				}
			else
				replymsg= "Please enter a reasonable name!";
		}break;
		case 12:{
			if(inputChecker.amountAdd(text, foodinput, database)) {
				inputChecker.consumptionUpdate(healthSearcher,database,Integer.parseInt(text),event.getSource().getUserId(),time);
				replymsg= "Your data has been recorded.\nInput anything to conitnue.";
				currentUser.setSubStage(0) ;
				}
			else
				replymsg= "Please enter a reasonable number!";
		}break;
		case 2:{
			replymsg= "Please enter the date(yyyymmdd): ";
			currentUser.setSubStage(currentUser.getSubStage()+20) ;
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
		//substage 3: Diet plan genereator
		case 3:{
			/*
			 * TODO:Update corresponding user's "diet_plan" table based on his/her information - will be done in Milestone 3
			 * For now just manually input data
			 * */
			String user_id = currentUser.getID();
			if (database.search_diet_plan(user_id)) {
				replymsg = "You've already generated a diet plan!\n";
			}
			else {
				boolean result = database.gen_plan(user_id);//tempory
				if (result) {
					replymsg = "We have successfully generated a diet plan for you!\n";
				}
				else {
					replymsg  = "Sorry, we fail to generate a diet plan for you now.\n";
				}
			}
			replymsg+= "Type anything to go back to Diet Planner...\n";
			currentUser.setSubStage(0);
		}break;


		//substage 4: Reminder
		case 4:{
			replymsg = "Reminder List:\n";
			try {
				String user_id = currentUser.getID();
				// Instantiate a Date object
				Date dNow = new Date();
				SimpleDateFormat ft = new SimpleDateFormat ("yyyyMMdd");
				ft.setTimeZone(TimeZone.getTimeZone("GMT+8"));
				String date = ft.format(dNow);//20171102

				ArrayList<Double> plan_info = database.search_plan(user_id);
				//ArrayList<Integer> current_info = new ArrayList<Integer>();
				ArrayList<Double> current_info = database.search_current(user_id, date); // diet current status
				for (int i = 0; i < plan_info.size(); i++) {
					//current_info.add(50);
					double diff = plan_info.get(i) - current_info.get(i);
					//protein
					if (i==0) {
						replymsg += "Protein: ";
						if (plan_info.get(i) > current_info.get(i)) {
							replymsg += String.format("You still need to consume %.2f g\n", diff);
						}
						else
							replymsg += "Finish!\n";
					}
					//fat
					else if (i== 1){
						replymsg += "Fat: ";
						if (plan_info.get(i) > current_info.get(i)) {
							replymsg += String.format("You still need to consume %.2f g\n", diff);
						}
						else
							replymsg += "Finish!\n";
					}
					//sugar
					else if (i== 2){
						replymsg += "Sugar: ";
						if (plan_info.get(i) > current_info.get(i)) {
							replymsg += String.format("You still need to consume %.2f g\n", diff);
						}
						else
							replymsg += "Finish!\n";
					}
					//other nutrient
					else{
						replymsg += "Not yet set for this type.\n";
					}
				}
			} catch (Exception e) {
				//log.info("plan not found: {}", e.toString());
				replymsg += "We can not find your diet plan, please design your own diet plan first!\n";
			}
			replymsg += "Type 1 to go back to Diet Planner...\nType other things to return to main menu...\n";
			currentUser.setSubStage(currentUser.getSubStage()+10);
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
		
		//subStage5: self Assessment;
		case 5:{
			suggestion = "";
			(currentUser).setAssessmentScore(0);
			replymsg = replymsg + "This quiz will reveal about the your eating habits by answering 10 true or false questions. "
					+ "\nPlease reply 'T' as ture and 'F' as false according your eating habits."
					+ "\nReply anything to start or reply 'q' to reutrn to the main menu...";
			currentUser.setSubStage(-2);
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

		return replymsg;

	}




	public String livingHabitCollectorEditor(String replyToken, Event event, String text, Users currentUser, SQLDatabaseEngine database) {
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
											+ "6 Edit Exercise Amount\n"
											+ "7 Edit Calories Consumption\n"
											+ "8 Edit Carbohydrate Consumption\n"
											+ "9 Edit Protein Consumption\n"
											+ "10 Edit Vegtable/Fruit Consumption \n"
											+ "11 Edit Other Information about you\n"
											+ "12 Show all your states \n"
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
				}
			}catch(Exception e) {
				replymsg="All changed recorded. Type anything to return to main menu.";
				//update db
				currentUser.setStage("Main");//back to main
				currentUser.setSubStage(0);
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
		case 6:{
			replymsg="Please enter the hours of excercise per day you wish to change:";
			currentUser.setSubStage(currentUser.getSubStage()+20) ;
		}break;
		case 7:{
			replymsg="Please enter the calories consumption(kcal) per day you wish to change to:";
			currentUser.setSubStage(currentUser.getSubStage()+20) ;
		}break;
		case 8:{
			replymsg="Please enter the carbohydrates consumption(g) per day you wish to change to:";
			currentUser.setSubStage(currentUser.getSubStage()+20) ;
		}break;
		case 9:{
			replymsg="Please enter the protein consumption(g) per day you wish to change to:";
			currentUser.setSubStage(currentUser.getSubStage()+20) ;
		}break;
		case 10:{
			replymsg="Please enter the veg/fruit consumption(servings) per day you wish to change to:";
			currentUser.setSubStage(currentUser.getSubStage()+20) ;
		}break;
		case 11:{
			replymsg="Please enter other information about yourself that you wish to change to:";
			currentUser.setSubStage(currentUser.getSubStage()+20) ;
		}break;
		case 12:{
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
		case 26:{
			if(inputChecker.ExerciseEditting(text, currentUser, database, "update")) {
	       		replymsg="Your data has been recorded.\nInput anything to conitnue.";
	       		currentUser.setSubStage(0);
        		}
			else
				replymsg="Please enter reasonable numbers!";
		}break;
		case 27:{
			if(inputChecker.CaloriesEditting(text, currentUser, database, "update")) {
	       		replymsg="Your data has been recorded.\nInput anything to conitnue.";
	       		currentUser.setSubStage(0);
        		}
			else
				replymsg="Please enter reasonable numbers!";
		}break;
		case 28:{
			if(inputChecker.CarbsEditting(text, currentUser, database, "update")) {
	       		replymsg="Your data has been recorded.\nInput anything to conitnue.";
	       		currentUser.setSubStage(0);
        		}
			else
				replymsg="Please enter reasonable numbers!";
		}break;
		case 29:{
			if(inputChecker.ProteinEditting(text, currentUser, database, "update")) {
	       		replymsg="Your data has been recorded.\nInput anything to conitnue.";
	       		currentUser.setSubStage(0);
        		}
			else
				replymsg="Please enter reasonable numbers!";
		}break;
		case 30:{
			if(inputChecker.VegfruitEditting(text, currentUser, database, "update")) {
	       		replymsg="Your data has been recorded.\nInput anything to conitnue.";
	       		currentUser.setSubStage(0);
        		}
			else
				replymsg="Please enter reasonable numbers!";
		}break;
		case 31:{
			if(inputChecker.OtherinfoEditting(text, currentUser, database, "update")) {
       			replymsg="Your data has been recorded.\nInput anything to conitnue.";
       			currentUser.setSubStage(0);
			}
			else
				replymsg="Please enter some with characters less then 1000!";
		}break;
		default:{
			replymsg="Some problem occurs.Type any key to return to main menu.";
			//log.info("Stage Error!!");
			currentUser.setStage("Main");//back to main
			currentUser.setSubStage(0);
		}break;
		}
		return replymsg;
	}


	public String livingHabitCollectorHandler(String replyToken, Event event, String text, Users currentUser, SQLDatabaseEngine database) {
		String replymsg = "";
		switch(currentUser.getSubStage()){
		case 0:{
			//currentUser = new DetailedUser(currentUser);
			replymsg = "Please tell us your body fat:(in %)";
			currentUser.setSubStage(currentUser.getSubStage()+1);
		}break;
		case 1:{
			if(inputChecker.BodyfatEditting(text, currentUser, database, "set")) {
        		replymsg = "Please tell us your average daily calories consumption(in kcal):";
        		currentUser.setSubStage(currentUser.getSubStage()+1);
        		}
			else
				replymsg = "Please enter reasonable numbers!";

		}break;
		case 2:{
			if(inputChecker.CaloriesEditting(text, currentUser, database, "set")) {
        		replymsg = "Please tell us your average daily carbohydrates consumption(roughly in g):";
        		currentUser.setSubStage(currentUser.getSubStage()+1);
        		}
			else
				replymsg = "Please enter reasonable numbers!";
		}break;
		case 3:{
			if(inputChecker.CarbsEditting(text, currentUser, database, "set")) {
        			replymsg = "Please tell us your average daily protein consumption(roughly in g):";
        			currentUser.setSubStage(currentUser.getSubStage()+1);
        			}
				else {
					replymsg = "Please enter reasonable numbers!";
				}
		}break;
		case 4:{
			if(inputChecker.ProteinEditting(text, currentUser, database, "set")) {
        			replymsg = "Please tell us your average daily vegetable/fruit consumption(in serving):";
        			currentUser.setSubStage(currentUser.getSubStage()+1);
        			}
				else {
					replymsg = "Please enter reasonable numbers!";
				}
		}break;
		case 5:{
			if(inputChecker.VegfruitEditting(text, currentUser, database, "set")) {
        			replymsg = "Do you eat breakfast?(y/n)";
        			currentUser.setSubStage(currentUser.getSubStage()+1);
        			}
				else {
					replymsg = "Please enter reasonable numbers!";
				}
		}break;
		case 6:{
			boolean input = false;
			if (text.charAt(0)=='y' || text.charAt(0)=='Y') input = true;
			else if( text.charAt(0)=='n'|| text.charAt(0)=='N') input = false;
			else { replymsg = "Do you eat breakfast?(y/n)"; return replymsg;}

			(currentUser).setEatingHabits(input,0);
			replymsg = "Do you eat lunch?(y/n)";
			currentUser.setSubStage(currentUser.getSubStage()+1);
		}break;
		case 7:{
			boolean input = false;
			if (text.charAt(0)=='y' ||  text.charAt(0)=='Y') input = true;
			else if( text.charAt(0)=='n'|| text.charAt(0)=='n') input = false;
			else { replymsg = "Do you eat lunch?(y/n)"; return replymsg;}

			(currentUser).setEatingHabits(input,1);
			replymsg = "Do you eat afternoon tea?(y/n)";
			currentUser.setSubStage(currentUser.getSubStage()+1);
		}break;
		case 8:{
			boolean input = false;
			if (text.charAt(0)=='y' ||  text.charAt(0)=='Y') input = true;
			else if( text.charAt(0)=='n'|| text.charAt(0)=='N') input = false;
			else { replymsg = "Do you eat afternoon tea?(y/n)"; return replymsg;}

			(currentUser).setEatingHabits(input,2);
			replymsg = "Do you eat dinner?(y/n)";
			currentUser.setSubStage(currentUser.getSubStage()+1);
		}break;
		case 9:{
			boolean input = false;
			if (text.charAt(0)=='y' ||  text.charAt(0)=='Y') input = true;
			else if( text.charAt(0)=='n'|| text.charAt(0)=='N') input = false;
			else { replymsg = "Do you eat dinner?(y/n)"; return replymsg;}

			(currentUser).setEatingHabits(input,3);
			replymsg = "Do you eat midnight snacks?(y/n)";
			currentUser.setSubStage(currentUser.getSubStage()+1);
		}break;
		case 10:{
			boolean input = false;
			if (text.charAt(0)=='y' ||  text.charAt(0)=='Y') input = true;
			else if( text.charAt(0)=='n'|| text.charAt(0)=='N') input = false;
			else { replymsg = "Do you eat midnight snacks?(y/n)"; return replymsg;}

			(currentUser).setEatingHabits(input,4);
			replymsg = "Do you eat any extra meals?(y/n)";
			currentUser.setSubStage(currentUser.getSubStage()+1);
		}break;
		case 11:{
			boolean input = false;
			if (text.charAt(0)=='y' ||  text.charAt(0)=='Y') input = true;
			else if( text.charAt(0)=='n'|| text.charAt(0)=='N') input = false;
			else { replymsg = "Do you eat any extra meals?(y/n)"; return replymsg;}

			(currentUser).setEatingHabits(input,5);
			replymsg = "How many hours per day do you exercise in a weekly average?";
			currentUser.setSubStage(currentUser.getSubStage()+1);
		}break;
		case 12:{
			if(inputChecker.ExerciseEditting(text, currentUser, database, "set")) {
				replymsg = "Any other infomation about your body you wish to let us know?(in 1000 characters)";
				currentUser.setSubStage(currentUser.getSubStage()+1);
        		}
			else
				replymsg = "Please enter reasonable numbers!";

		}break;
		case 13:{
			if(inputChecker.OtherinfoEditting(text, currentUser, database, "set")) {
				replymsg = "All set and recorded. Type anything to return to main menu.";
				database.pushUser(currentUser);
				currentUser.setStage("Main");//back to main
				currentUser.setSubStage(0);
			}
			else
				replymsg = "Please enter something in 1000 characters!!";
		}break;

		default:
			break;
		}
		return replymsg;
	}

	public String healthPediaHandler(String replyToken, Event event, String text, Users currentUser, SQLDatabaseEngine database) {
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
		return replymsg;
	}

	public String feedBackHandler(String replyToken, Event event, String text, Users currentUser, SQLDatabaseEngine database) {
		String replymsg = "";
		replymsg = "All set. Type anything to return to main menu...";
		currentUser.setStage("Main");//back to main
		currentUser.setSubStage(0);
		return replymsg;
	}
	public String userGuideHandler(String replyToken, Event event, String text, Users currentUser, SQLDatabaseEngine database) {
		String replymsg = "";
		replymsg = "All set. Type anything to return to main menu...";
		currentUser.setStage("Main");//back to main
		currentUser.setSubStage(0);
		return replymsg;
	}

	// this is self assessment Handler function
//	public String selfAssessmentHandler(String replyToken, Event event, String text, Users currentUser, SQLDatabaseEngine database) {
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
	public void unfollowHandler(Users currentUser){
		currentUser.setStage("Init");
		currentUser.setSubStage(0);
	}

	public String followHandler(Users currentUser){
		String msg = "User data reloaded. Type anything to continue...";
		currentUser.setStage("Main");
		currentUser.setSubStage(0);
		return msg;
	}

}
