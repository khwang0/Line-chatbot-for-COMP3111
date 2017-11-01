/*
 * Copyright 2016 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.example.bot.spring;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
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
import com.linecorp.bot.model.event.BeaconEvent;
import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.FollowEvent;
import com.linecorp.bot.model.event.JoinEvent;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.PostbackEvent;
import com.linecorp.bot.model.event.UnfollowEvent;
import com.linecorp.bot.model.event.message.AudioMessageContent;
import com.linecorp.bot.model.event.message.ImageMessageContent;
import com.linecorp.bot.model.event.message.LocationMessageContent;
import com.linecorp.bot.model.event.message.StickerMessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.event.source.GroupSource;
import com.linecorp.bot.model.event.source.RoomSource;
import com.linecorp.bot.model.event.source.Source;
import com.linecorp.bot.model.message.AudioMessage;
import com.linecorp.bot.model.message.ImageMessage;
import com.linecorp.bot.model.message.ImagemapMessage;
import com.linecorp.bot.model.message.LocationMessage;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.StickerMessage;
import com.linecorp.bot.model.message.TemplateMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.message.imagemap.ImagemapArea;
import com.linecorp.bot.model.message.imagemap.ImagemapBaseSize;
import com.linecorp.bot.model.message.imagemap.MessageImagemapAction;
import com.linecorp.bot.model.message.imagemap.URIImagemapAction;
import com.linecorp.bot.model.message.template.ButtonsTemplate;
import com.linecorp.bot.model.message.template.CarouselColumn;
import com.linecorp.bot.model.message.template.CarouselTemplate;
import com.linecorp.bot.model.message.template.ConfirmTemplate;
import com.linecorp.bot.model.response.BotApiResponse;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;

import lombok.NonNull;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;

@Slf4j
@LineMessageHandler
public class KitchenSinkController {
	


	@Autowired
	private LineMessagingClient lineMessagingClient;
	private String currentStage = "Init";
	private int subStage = 0;
	private Users currentUser = null;
	private SQLDatabaseEngine database;
	private String itscLOGIN;
	private InputChecker inputChecker = new InputChecker();
	private HealthSearch healthSearcher = new HealthSearch();
	
	public KitchenSinkController() {
		database = new SQLDatabaseEngine();
		itscLOGIN = System.getenv("ITSC_LOGIN");
	}
	
	

	@EventMapping
	public void handleTextMessageEvent(MessageEvent<TextMessageContent> event) throws Exception {
		log.info("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		log.info("This is your entry point:");
		log.info("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		TextMessageContent message = event.getMessage();
		handleTextContent(event.getReplyToken(), event, message);
		
	}

	@EventMapping
	public void handleStickerMessageEvent(MessageEvent<StickerMessageContent> event) {
		handleSticker(event.getReplyToken(), event.getMessage());
	}

	@EventMapping
	public void handleLocationMessageEvent(MessageEvent<LocationMessageContent> event) {
		LocationMessageContent locationMessage = event.getMessage();
		reply(event.getReplyToken(), new LocationMessage(locationMessage.getTitle(), locationMessage.getAddress(),
				locationMessage.getLatitude(), locationMessage.getLongitude()));
	}

	@EventMapping
	public void handleImageMessageEvent(MessageEvent<ImageMessageContent> event) throws IOException {
		final MessageContentResponse response;
		String replyToken = event.getReplyToken();
		String messageId = event.getMessage().getId();
		try {
			response = lineMessagingClient.getMessageContent(messageId).get();
		} catch (InterruptedException | ExecutionException e) {
			reply(replyToken, new TextMessage("Cannot get image: " + e.getMessage()));
			throw new RuntimeException(e);
		}
		DownloadedContent jpg = saveContent("jpg", response);
		reply(((MessageEvent) event).getReplyToken(), new ImageMessage(jpg.getUri(), jpg.getUri()));

	}

	@EventMapping
	public void handleAudioMessageEvent(MessageEvent<AudioMessageContent> event) throws IOException {
		final MessageContentResponse response;
		String replyToken = event.getReplyToken();
		String messageId = event.getMessage().getId();
		try {
			response = lineMessagingClient.getMessageContent(messageId).get();
		} catch (InterruptedException | ExecutionException e) {
			reply(replyToken, new TextMessage("Cannot get image: " + e.getMessage()));
			throw new RuntimeException(e);
		}
		DownloadedContent mp4 = saveContent("mp4", response);
		reply(event.getReplyToken(), new AudioMessage(mp4.getUri(), 100));
	}

	@EventMapping
	public void handleUnfollowEvent(UnfollowEvent event) {
		log.info("unfollowed this bot: {}", event);
		currentStage = "Init";
		subStage = 0;
		currentUser = null;
	}

	@EventMapping
	public void handleFollowEvent(FollowEvent event) {
		String replyToken = event.getReplyToken();
		String msgbuffer = null;
		try{
			currentUser = database.searchUser(event.getSource().getUserId());
			try {
				currentUser = database.searchDetailedUser(currentUser);
			}catch(Exception e) {
				log.info(e.getMessage());
			}finally {
				currentStage = "Main";
				subStage = 0;
				msgbuffer = "User data reloaded. Type anything to continue...";
			}
		}catch(Exception e){
			msgbuffer = "Welcome!!\nTo start using our services, please follow the instructions below.\n\n"
					+ "Create Personal Diet Tracker: type \'1\'\n\n"
					+ "Say goodbye to me: type any\n";
			currentStage = "Init";
			subStage = 0;
			currentUser = null;
		}finally {
			this.replyText(replyToken, msgbuffer);	
		}
	}

	@EventMapping
	public void handleJoinEvent(JoinEvent event) {
		String replyToken = event.getReplyToken();
		this.replyText(replyToken, "Joined " + event.getSource());
	}

	@EventMapping
	public void handlePostbackEvent(PostbackEvent event) {
		String replyToken = event.getReplyToken();
		this.replyText(replyToken, "Got postback " + event.getPostbackContent().getData());
	}

	@EventMapping
	public void handleBeaconEvent(BeaconEvent event) {
		String replyToken = event.getReplyToken();
		this.replyText(replyToken, "Got beacon message " + event.getBeacon().getHwid());
	}

	@EventMapping
	public void handleOtherEvent(Event event) {
		log.info("Received message(Ignored): {}", event);
	}

	private void reply(@NonNull String replyToken, @NonNull Message message) {
		reply(replyToken, Collections.singletonList(message));
	}

	private void reply(@NonNull String replyToken, @NonNull List<Message> messages) {
		try {
			BotApiResponse apiResponse = lineMessagingClient.replyMessage(new ReplyMessage(replyToken, messages)).get();
			log.info("Sent messages: {}", apiResponse);
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}
	}

	private void replyText(@NonNull String replyToken, @NonNull String message) {
		if (replyToken.isEmpty()) {
			throw new IllegalArgumentException("replyToken must not be empty");
		}
		if (message.length() > 1000) {
			message = message.substring(0, 1000 - 2) + "..";
		}
		this.reply(replyToken, new TextMessage(message));
	}


	private void handleSticker(String replyToken, StickerMessageContent content) {
		reply(replyToken, new StickerMessage(content.getPackageId(), content.getStickerId()));
	}
	
	private void initStageHandler(String replyToken, Event event, String text) {
		switch(subStage) {	
		case 0:{
			if(text.equals("1")) {
				currentUser = new Users(event.getSource().getUserId());
        		this.replyText(replyToken, "Please enter your name: (1-32 characters)");
        		subStage += 1;
        	}
        	else {
        		String msg = "I will be deactivated. To reactivate me, please block->unblock me. Bye.";
        		this.replyText(replyToken,msg);
        		currentStage = "";
        		subStage = 0;
        		currentUser = null;
        	}
		}break;
		case 1:{
			if(inputChecker.NameEditting(text,currentUser,database,"set")) {
				this.replyText(replyToken, "Please enter your gender: (M for male F for female)");
				subStage += 1;
				}
			else 
				this.replyText(replyToken,"Please enter your name: (1-32 characters)");
		}break;
		case 2:{
			if(inputChecker.GenderEditting(text,currentUser,database,"set")) {
				this.replyText(replyToken, "Please enter your height in cm:");
				subStage+=1;
			}
			else 
				this.replyText(replyToken, "Please enter your gender: (M for male F for female):");
		}break;
		case 3:{
			if( inputChecker.HeightEditting(text,currentUser,database,"set") ) {
				this.replyText(replyToken, "Please enter your weight in kg:");
				subStage+=1;
			}
			else 
				this.replyText(replyToken, "Please enter reasonable numbers!");
		}break;
		case 4:{
			if( inputChecker.WeightEditting(text,currentUser,database,"set") ) {
				this.replyText(replyToken, "Please enter your age in years old:");
				subStage+=1;
			}
			else 
				this.replyText(replyToken, "Please enter reasonable numbers!");
		}break;
		case 5:{
			if(inputChecker.AgeEditting(text, currentUser, database, "set")) {
       			this.replyText(replyToken, "Your data has been recorded.\nInput anything to conitnue.");
       			database.pushUser(currentUser);
       			currentStage = "Main";
       			subStage = 0;
			}
			else
				this.replyText(replyToken, "Please enter reasonable numbers!");  
		}break;
		default:{log.info("Stage error.");}
		}
	}
	
	private void mainStageHandler(String replyToken, Event event, String text) {
		switch(subStage) {
		case 0:{
			String msg = null;
			if(! (currentUser instanceof DetailedUser)) {
				msg = "Welcome to ZK's Diet Planner!\n\n"
				+ "We provide serveral functions for you to keep your fitness."
				+ "Please type the number of function you wish to use. :)\n\n"
				+ "1 Living Habit Collector (INSERT YOUR DATA HERE)\n"
				+ "2 Diet Planner(Please complete 1 first)\n"
				+ "3 Healthpedia \n"
				+ "4 Feedback \n"
				+ "5 User Guide(recommended for first-time users)\n\n"
				+ "Please enter your choice:(1-5)";
			}else {
				msg = "Welcome to ZK's Diet Planner!\n\n"
						+ "We provide serveral functions for you to keep your fitness."
						+ "Please type the number of function you wish to use. :)\n\n"
						+ "1 Living Habot Editor\n"
						+ "2 Diet Planner\n"
						+ "3 Healthpedia \n"
						+ "4 Feedback \n"
						+ "5 User Guide(recommended for first-time users)\n\n"
						+ "Please enter your choice:(1-5)";
			}
			this.replyText(replyToken, msg);
			subStage+=1;
			
		}break;
		case 1:{
			String msg = null;
			switch(text) {
			case "1":{
				//move to diet planner
				msg = "Wellcome to Living Habit Collector! You can edit or input more detailed information"
						+ "about yourself. This can help us make a more precise suggestion for you!\n"
						+ "please follow the instructions below (type any to continue)";
				if(!(currentUser instanceof DetailedUser)) {
					currentStage = "LivingHabitCollector";
					subStage = 0;
				}else {
					currentStage = "LivingHabitEditor";
					subStage = 0;
				}
			}break;
			case "2":{
				//move to diet planner
				msg = "Moving to Diet Planner...Input anything to continue...";
				currentStage = "DietPlanner";
				subStage = 0;
			}break;
			case "3":{
				//move to health pedia
				msg = "Moving to Diet Planner...Input anything to continue...";
				currentStage = "HealthPedia";
				subStage = 0;
			}break;
			case "4":{
				//move to feedback
				msg = "Moving to FeedBack...Input anything to continue...";
				currentStage = "FeedBack";
				subStage = 0;
			}break;
			case "5":{
				msg ="Moving to User Guide...Input anything to continue...";
				currentStage = "UserGuide";
				subStage = 0;
				//move to user guide
			}break;
			default:{msg = "Invalid input! Please input numbers from 1 to 4!!";}
			}
			this.replyText(replyToken, msg);
		}break;
		}
	}
	
	private void livingHabitCollectorEditor(String replyToken, Event event, String text) {

		switch(subStage) {
		case 0:{
			this.replyText(replyToken, "Looks like you have already input your data. "
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
											+ "(type other things to back to menu)");
			subStage = -1;
		}break;
		case -1:{ // redirecting stage
			try{
				subStage = Integer.parseInt(text);
				if (subStage >=1 && subStage <= 12) { 
					this.replyText(replyToken, "Redirecting...type anything to continue.");
				}
				else {
					this.replyText(replyToken, "All changed recorded. Type anything to return to main menu.");
					//updata db
					currentStage = "Main";//back to main 
					subStage =0; 
				}
			}catch(Exception e) {
				this.replyText(replyToken, "All changed recorded. Type anything to return to main menu.");
				//update db
				currentStage = "Main";//back to main 
				subStage =0; 
			}
		}break;
		case 1:{ 
			this.replyText(replyToken, "Please enter the age you wish to change to:");
			subStage +=20 ; 
		}break;
		case 21:{
			if(inputChecker.AgeEditting(text, currentUser, database, "update")) {
       			this.replyText(replyToken, "Your data has been recorded.\nInput anything to conitnue.");
       			subStage = 0;
			}
			else
				this.replyText(replyToken, "Please enter reasonable numbers!");
		}break;
		case 2:{
			this.replyText(replyToken, "Please enter the name you wish to change to:");
			subStage +=20 ; 
		}break;
		case 22:{
			if(inputChecker.NameEditting(text, currentUser, database, "update")) {
       			this.replyText(replyToken, "Your data has been recorded.\nInput anything to conitnue.");
       			subStage = 0;
			}
			else
				this.replyText(replyToken, "Please enter reasonable numbers!");
		}break;
		
		case 3:{
			this.replyText(replyToken, "Please enter the weight you wish to change to:");
			subStage +=20 ; 
		}break;
		case 23:{
			if(inputChecker.WeightEditting(text, currentUser, database, "update")) {
	       		this.replyText(replyToken, "Your data has been recorded.\nInput anything to conitnue.");
	       		subStage = 0;
			}
			else 
				this.replyText(replyToken, "Please enter reasonable numbers!");
		}break;
		case 4:{
			this.replyText(replyToken, "Please enter the height you wish to change to:");
			subStage +=20 ; 
		}break;
		case 24:{
			if(inputChecker.HeightEditting(text, currentUser, database, "update")) {
	    		this.replyText(replyToken, "Your data has been recorded.\nInput anything to conitnue.");
	       		subStage = 0;
			}
			else 
				this.replyText(replyToken, "Please enter reasonable numbers!");
		}break;
		case 5:{
			this.replyText(replyToken, "Please enter the bodyfat(%) you wish to change to:");
			subStage +=20 ; 
		}break;
		case 25:{
			if(inputChecker.BodyfatEditting(text, currentUser, database, "update")) {
	      		this.replyText(replyToken, "Your data has been recorded.\nInput anything to conitnue.");
	       		subStage = 0;
			}
			else 
				this.replyText(replyToken, "Please enter reasonable numbers!");
		}break;
		case 6:{
			this.replyText(replyToken, "Please enter the hours of excercise per day you wish to change:");
			subStage +=20 ; 
		}break;
		case 26:{	
			if(inputChecker.ExerciseEditting(text, currentUser, database, "update")) {
	       		this.replyText(replyToken, "Your data has been recorded.\nInput anything to conitnue.");
	       		subStage = 0;
        		}
			else 
				this.replyText(replyToken, "Please enter reasonable numbers!");
		}break;
		case 7:{
			this.replyText(replyToken, "Please enter the calories consumption(kcal) per day you wish to change to:");
			subStage +=20 ; 
		}break;
		case 27:{
			if(inputChecker.CaloriesEditting(text, currentUser, database, "update")) {
	       		this.replyText(replyToken, "Your data has been recorded.\nInput anything to conitnue.");
	       		subStage = 0;
        		}
			else 
				this.replyText(replyToken, "Please enter reasonable numbers!");
		}break;
		case 8:{
			this.replyText(replyToken, "Please enter the carbohydrates consumption(g) per day you wish to change to:");
			subStage +=20 ; 
		}break;
		case 28:{
			if(inputChecker.CarbsEditting(text, currentUser, database, "update")) {
	       		this.replyText(replyToken, "Your data has been recorded.\nInput anything to conitnue.");
	       		subStage = 0;
        		}
			else 
				this.replyText(replyToken, "Please enter reasonable numbers!");
		}break;
		case 9:{
			this.replyText(replyToken, "Please enter the protein consumption(g) per day you wish to change to:");
			subStage +=20 ; 
		}break;
		case 29:{
			if(inputChecker.ProteinEditting(text, currentUser, database, "update")) {
	       		this.replyText(replyToken, "Your data has been recorded.\nInput anything to conitnue.");
	       		subStage = 0;
        		}
			else
				this.replyText(replyToken, "Please enter reasonable numbers!");
		}break;
		case 10:{
			this.replyText(replyToken, "Please enter the veg/fruit consumption(servings) per day you wish to change to:");
			subStage +=20 ; 
		}break;
		case 30:{
			if(inputChecker.VegfruitEditting(text, currentUser, database, "update")) {
	       		this.replyText(replyToken, "Your data has been recorded.\nInput anything to conitnue.");
	       		subStage = 0;
        		}
			else 
				this.replyText(replyToken, "Please enter reasonable numbers!");
		}break;
		case 11:{
			this.replyText(replyToken, "Please enter other information about yourself that you wish to change to:");
			subStage +=20 ; 
		}break;
		case 31:{
			if(inputChecker.OtherinfoEditting(text, currentUser, database, "update")) {
       			this.replyText(replyToken, "Your data has been recorded.\nInput anything to conitnue.");
       			subStage = 0;
			}
			else 
				this.replyText(replyToken, "Please enter some with characters less then 1000!");
		}break;
		case 12:{
			this.replyText(replyToken,"These are all about your body:\n\n" 	+ currentUser.toString()+"\nType any to continue.");
			subStage = 0;  
		}break;
			
		default:{
			this.replyText(replyToken, "Some problem occurs.Type any key to return to main menu.");
			log.info("Stage Error!!");
			currentStage = "Main";//back to main 
			subStage =0; 
		}break;
		}
	}

	
	private void livingHabitCollectorHandler(String replyToken, Event event, String text) {
		switch(subStage){
		case 0:{
			currentUser = new DetailedUser(currentUser);
			this.replyText(replyToken, "Please tell us your body fat:(in %)");
			subStage +=1;
		}break;
		case 1:{
			if(inputChecker.BodyfatEditting(text, currentUser, database, "set")) {
        		this.replyText(replyToken, "Please tell us your average daily calories consumption(in kcal):");
        		subStage +=1 ;   
        		}
			else 
				this.replyText(replyToken, "Please enter reasonable numbers!");
				
		}break;
		case 2:{
			if(inputChecker.CaloriesEditting(text, currentUser, database, "set")) {
        		this.replyText(replyToken, "Please tell us your average daily carbohydrates consumption(roughly in g):");
        		subStage +=1 ;   
        		}
			else 
				this.replyText(replyToken, "Please enter reasonable numbers!");
		}break;
		case 3:{
			if(inputChecker.CarbsEditting(text, currentUser, database, "set")) {
        			this.replyText(replyToken, "Please tell us your average daily protein consumption(roughly in g):");
        			subStage +=1 ;   
        			}
				else {
					this.replyText(replyToken, "Please enter reasonable numbers!");
				}
		}break;
		case 4:{
			if(inputChecker.ProteinEditting(text, currentUser, database, "set")) {
        			this.replyText(replyToken, "Please tell us your average daily vegetable/fruit consumption(in serving):");
        			subStage +=1 ;   
        			}
				else {
					this.replyText(replyToken, "Please enter reasonable numbers!");
				}
		}break;
		case 5:{
			if(inputChecker.VegfruitEditting(text, currentUser, database, "set")) {
        			this.replyText(replyToken, "Do you eat breakfast?(y/n)");
        			subStage +=1 ;   
        			}
				else {
					this.replyText(replyToken, "Please enter reasonable numbers!");
				}
		}break;
		case 6:{
			boolean input = false;
			if (text.charAt(0)=='y' || text.charAt(0)=='Y') input = true;
			else if( text.charAt(0)=='n'|| text.charAt(0)=='N') input = false;
			else { this.replyText(replyToken, "Do you eat breakfast?(y/n)"); return;}

			((DetailedUser)currentUser).setEatingHabits(input,0);
			this.replyText(replyToken, "Do you eat lunch?(y/n)");
			subStage +=1;
		}break;
		case 7:{
			boolean input = false;
			if (text.charAt(0)=='y' ||  text.charAt(0)=='Y') input = true;
			else if( text.charAt(0)=='n'|| text.charAt(0)=='n') input = false;
			else { this.replyText(replyToken, "Do you eat lunch?(y/n)"); return;}

			((DetailedUser)currentUser).setEatingHabits(input,1);
			this.replyText(replyToken, "Do you eat afternoon tea?(y/n)");
			subStage +=1;
		}break;
		case 8:{
			boolean input = false;
			if (text.charAt(0)=='y' ||  text.charAt(0)=='Y') input = true;
			else if( text.charAt(0)=='n'|| text.charAt(0)=='N') input = false;
			else { this.replyText(replyToken, "Do you eat afternoon tea?(y/n)"); return;}

			((DetailedUser)currentUser).setEatingHabits(input,2);
			this.replyText(replyToken, "Do you eat dinner?(y/n)");
			subStage +=1;
		}break;
		case 9:{
			boolean input = false;
			if (text.charAt(0)=='y' ||  text.charAt(0)=='Y') input = true;
			else if( text.charAt(0)=='n'|| text.charAt(0)=='N') input = false;
			else { this.replyText(replyToken, "Do you eat dinner?(y/n)"); return;}

			((DetailedUser)currentUser).setEatingHabits(input,3);
			this.replyText(replyToken, "Do you eat midnight snacks?(y/n)");
			subStage +=1;
		}break;
		case 10:{
			boolean input = false;
			if (text.charAt(0)=='y' ||  text.charAt(0)=='Y') input = true;
			else if( text.charAt(0)=='n'|| text.charAt(0)=='N') input = false;
			else { this.replyText(replyToken, "Do you eat midnight snacks?(y/n)"); return;}

			((DetailedUser)currentUser).setEatingHabits(input,4);
			this.replyText(replyToken, "Do you eat any extra meals?(y/n)");
			subStage +=1;
		}break;
		case 11:{
			boolean input = false;
			if (text.charAt(0)=='y' ||  text.charAt(0)=='Y') input = true;
			else if( text.charAt(0)=='n'|| text.charAt(0)=='N') input = false;
			else { this.replyText(replyToken, "Do you eat any extra meals?(y/n)"); return;}

			((DetailedUser)currentUser).setEatingHabits(input,5);
			this.replyText(replyToken, "How many hours per day do you exercise in a weekly average?");
			subStage += 1; 
		}break;
		case 12:{
			if(inputChecker.ExerciseEditting(text, currentUser, database, "set")) {
				this.replyText(replyToken, "Any other infomation about your body you wish to let us know?(in 1000 characters)");
				subStage +=1;   
        		}
			else 
				this.replyText(replyToken, "Please enter reasonable numbers!");
			
		}break;
		case 13:{
			if(inputChecker.OtherinfoEditting(text, currentUser, database, "set")) {
				database.pushUser(currentUser);
				this.replyText(replyToken, "All set and recorded. Type anything to return to main menu.");
				currentStage = "Main";//back to main 
				subStage =0;  
			}
			else
				this.replyText(replyToken, "Please enter something in 1000 characters!!");
		}break;
		
		default:
			break;
		}
	}
	private void dietPlannerHandler(String replyToken, Event event, String text) {
		/*switch(subStage) {
		case 0:{
			
		}break;
		default:break;
		}
		*/
		
		this.replyText(replyToken, "All set. Type anything to return to main menu...");
		currentStage = "Main";//back to main 
		subStage = 0; 
	}
	private void healthPediaHandler(String replyToken, Event event, String text) {
		//user key word input
		//String 
		switch(subStage) {
		case 0:{
			this.replyText(replyToken,"Welcome to HealthPedia! You are welcome to query any thing about food!\n"
					+ "Please type the function choice you wish to use as below.\n\n"
					+ "1 Food Searcher\n"
					+ "2\n"
					+ "3\n"
					+ "4\n\n"
					+ "Type other things to go back to main menu.");
			subStage -= 1;
		}break;
		
		case -1:{ // redirecting stage
			try{
				subStage = Integer.parseInt(text);
				if (subStage >=1 && subStage <= 4) { 
					this.replyText(replyToken, "Redirecting...type anything to continue.");
				}
				else {
					this.replyText(replyToken, "Redirecting...type anything to continue.");
					currentStage = "Main";//back to main 
					subStage =0; 
				}
			}catch(Exception e) {
				this.replyText(replyToken, "Redirecting...type anything to continue.");
				currentStage = "Main";//back to main 
				subStage =0; 
			}
		}break;
		
		case 1:{
			this.replyText(replyToken, "Please enter the name of food you wish to know about:");
			subStage += 10;
		}break;
		case 11:{
			healthSearcher.setKeyword(text);
			healthSearcher.setMode(0);
			if(healthSearcher.search()) {
				String msg = healthSearcher.getUnit()+":\n"
						+"Energy: "+healthSearcher.getEnergy()+"kcal\n"
						+"Carbohydragate: "+healthSearcher.getCarbohydrate()+"g\n"
						+"Protein:"+healthSearcher.getProtein()+"g\n"
						+"Fat: "+healthSearcher.getFat()+"g\n"
						+"Suger: "+healthSearcher.getSugar()+"g\n"
						+"Water:"+healthSearcher.getWater()+"g\n"
						+"\nType 1 to search for other food.\nType other thing to go back to Healthpedia.";
				this.replyText(replyToken, msg);
				subStage += 10;
			}
			else {
				this.replyText(replyToken, "Food not found.\nType 1 to search for other food.\nType other thing to go back to Healthpedia.");
				subStage +=10;
			}
		}break;
		case 21:{
			try {
				if(Integer.parseInt(text) == 1) subStage = 1;
				else {
					throws new Exception ex;
					subStage = 0;
				}
			}catch(Exception ex) {
				subStage = 0;
			}finally { this.replyText(replyToken, "redirecting...type anything to continue.");}	
		}break;
		default:break;
		}

		this.replyText(replyToken, "All set. Type anything to return to main menu...");
		currentStage = "Main";//back to main 
		subStage = 0; 
	}
	private void feedBackHandler(String replyToken, Event event, String text) {
		this.replyText(replyToken, "All set. Type anything to return to main menu...");
		currentStage = "Main";//back to main 
		subStage = 0; 
	}
	private void userGuideHandler(String replyToken, Event event, String text) {
		this.replyText(replyToken, "All set. Type anything to return to main menu...");
		currentStage = "Main";//back to main 
		subStage = 0; 
	}
	
	
	private void handleTextContent(String replyToken, Event event, TextMessageContent content)
            throws Exception {
        String text = content.getText();
        switch(currentStage) {
        	case "Init": 
        		initStageHandler(replyToken, event, text);
        		break;
        	case "Main":
        		mainStageHandler(replyToken, event, text);
        		break;
        	case "LivingHabitCollector":
        		livingHabitCollectorHandler(replyToken, event, text);
        		break;
        	case "LivingHabitEditor":
        		livingHabitCollectorEditor(replyToken, event, text);
        		break;
        	case "DietPlanner":
        		dietPlannerHandler(replyToken, event, text);
        		break;
        	case "HealthPedia":
        		healthPediaHandler(replyToken, event, text);
        		break;
        	case "FeedBack":
        		feedBackHandler(replyToken, event, text);
        		break;
        	case "UserGuide":
        		userGuideHandler(replyToken, event, text);
        		break;
        	default:
        		String msg = "Due to some stage error, I am deactivated. To reactivate me, please block->unblock me.";
        		this.replyText(replyToken, msg);
        		break;
        }
            
    }

	static String createUri(String path) {
		return ServletUriComponentsBuilder.fromCurrentContextPath().path(path).build().toUriString();
	}

	private void system(String... args) {
		ProcessBuilder processBuilder = new ProcessBuilder(args);
		try {
			Process start = processBuilder.start();
			int i = start.waitFor();
			log.info("result: {} =>  {}", Arrays.toString(args), i);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		} catch (InterruptedException e) {
			log.info("Interrupted", e);
			Thread.currentThread().interrupt();
		}
	}

	private static DownloadedContent saveContent(String ext, MessageContentResponse responseBody) {
		log.info("Got content-type: {}", responseBody);

		DownloadedContent tempFile = createTempFile(ext);
		try (OutputStream outputStream = Files.newOutputStream(tempFile.path)) {
			ByteStreams.copy(responseBody.getStream(), outputStream);
			log.info("Saved {}: {}", ext, tempFile);
			return tempFile;
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private static DownloadedContent createTempFile(String ext) {
		String fileName = LocalDateTime.now().toString() + '-' + UUID.randomUUID().toString() + '.' + ext;
		Path tempFile = KitchenSinkApplication.downloadedContentDir.resolve(fileName);
		tempFile.toFile().deleteOnExit();
		return new DownloadedContent(tempFile, createUri("/downloaded/" + tempFile.getFileName()));
	}



	//The annontation @Value is from the package lombok.Value
	//Basically what it does is to generate constructor and getter for the class below
	//See https://projectlombok.org/features/Value
	@Value
	public static class DownloadedContent {
		Path path;
		String uri;
	}


	//an inner class that gets the user profile and status message
	class ProfileGetter implements BiConsumer<UserProfileResponse, Throwable> {
		private KitchenSinkController ksc;
		private String replyToken;
		
		public ProfileGetter(KitchenSinkController ksc, String replyToken) {
			this.ksc = ksc;
			this.replyToken = replyToken;
		}
		@Override
    	public void accept(UserProfileResponse profile, Throwable throwable) {
    		if (throwable != null) {
            	ksc.replyText(replyToken, throwable.getMessage());
            	return;
        	}
        	ksc.reply(
                	replyToken,
                	Arrays.asList(new TextMessage(
                		"Display name: " + profile.getDisplayName()),
                              	new TextMessage("Status message: "
                            		  + profile.getStatusMessage()))
        	);
    	}
    }
	
	

}
