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
import com.linecorp.bot.model.PushMessage;
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

	private String[] links = {
				"http://www.health.com/food/thanksgiving-dairy-gluten-food-intolerances",
				"http://www.health.com/food/brussels-sprout-recipes",
				"http://www.health.com/syndication/whole-foods-amazon-turkey-deal",
				"http://www.health.com/syndication/drinks-order-bar-unhealthy",
				"http://www.health.com/food/keto-recipes",
				"http://www.health.com/syndication/stove-top-stuffing-thanksgiving-dinner-pants",
				"http://www.health.com/syndication/best-olive-oil-taste-test",
				"http://www.health.com/syndication/guac-lock-keep-guacamole-fresh",
				"http://www.health.com/syndication/halo-top-scoop-shop-los-angeles",
				"http://www.health.com/food/infused-water-recipes",
				"http://www.health.com/syndication/world-health-organization-antibiotics-animals",
				"http://www.health.com/syndication/whole-foods-top-ten-food-trends",
				"http://www.health.com/food/best-matcha-gifts",
				"http://www.health.com/syndication/coffee-add-ins",
				"http://www.health.com/syndication/what-to-do-after-eating-too-much-sugar",
				"http://www.health.com/food/spicy-food-character",
				"http://www.health.com/food/vegan-bacon-recipes-tempeh-bacon-eggplant-bacon",
				"http://www.health.com/syndication/whole-foods-new-ahimi-vegan-tuna-sushi",
				"http://www.health.com/food/mashed-potatoes-recipes",
				"http://www.health.com/food/celebrity-healthy-snacks",
				"http://www.health.com/syndication/new-trader-joes-products",
				"http://www.health.com/food/foods-fight-fat-video",
				"http://www.health.com/syndication/halo-top-non-dairy-flavors-ranked",
				"http://www.health.com/food/how-to-use-instant-pot",
				"http://www.health.com/food/buffalo-cauliflower-tacos-recipe-video",
				"http://www.health.com/health/gallery/0,,20345806,00.html",
				"http://www.health.com/health/gallery/0,,20350502,00.html",
				"http://www.health.com/food/healthy-taco-recipes-video",
				"http://www.health.com/food/broccoli-recipes",
				"http://www.health.com/food/peanut-butter-dessert-recipes-video",
				"http://www.health.com/food/stir-fry-recipes-turkey-thai-beef-shrimp",
				"http://www.health.com/syndication/vegan-hollandaise-edgy-veg",
				"http://www.health.com/food/candy-corn-ingredients-video",
				"http://www.health.com/food/stop-food-guilt",
				"http://www.health.com/food/chicken-recipes-peanut-penne-southwestern-video",
				"http://www.health.com/food/quinoa-recipes-burger-salad-muffin",
				"http://www.health.com/food/slow-cooker-recipes-chicken-tarragon-chickpea-cumin",
				"http://www.health.com/food/spaghetti-squash-recipes",
				"http://www.health.com/food/valerie-bertinelli-cookbook-recipes",
				"http://www.health.com/food/best-healthy-cookbooks-gifts-2017",
				"http://www.health.com/food/best-gifts-breakfast-lovers",
				"http://www.health.com/syndication/halo-top-dairy-free-vegan-flavors"
	};

	@Autowired
	private LineMessagingClient lineMessagingClient;
	private String currentStage = "Init";
	private int subStage = 0;
	private Users currentUser = null;

	private SQLDatabaseEngine database;
	private String itscLOGIN;
	private String replymsg;
	private StageHandler stageHandler = new StageHandler();
	private int number = 0; // No. of links


	public KitchenSinkController() {
		database = new SQLDatabaseEngine();
		itscLOGIN = System.getenv("ITSC_LOGIN");
	}



	@EventMapping
	public void handleTextMessageEvent(MessageEvent<TextMessageContent> event) throws Exception {
		log.info("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		log.info("This is your entry point:");
		log.info("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		TextMessageContent message = event.getMessage();
		handleTextContent(event.getReplyToken(), event, message);

	}
	/*
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
*/
	@EventMapping
	public void handleUnfollowEvent(UnfollowEvent event) {
		log.info("unfollowed this bot: {}", event);
		currentUser = getSourceUser(event);
		if (currentUser!=null) {
			stageHandler.unfollowHandler(currentUser, database);
		}
		//currentUser = null;
	}

	@EventMapping
	public void handleFollowEvent(FollowEvent event) {
		String replyToken = event.getReplyToken();
		String msgbuffer = null;
		msgbuffer = stageHandler.followHandler(event,currentUser,database);
		this.replyText(replyToken, msgbuffer);
	}
	/*
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
*/

	private void reply(@NonNull String replyToken, @NonNull Message message) {
		reply(replyToken, Collections.singletonList(message));
	}

	private void push(@NonNull String to, @NonNull Message message) {
        push(to,  Collections.singletonList(message));
    }

	private void reply(@NonNull String replyToken, @NonNull List<Message> messages) {
		try {
			BotApiResponse apiResponse = lineMessagingClient.replyMessage(new ReplyMessage(replyToken, messages)).get();
			log.info("Sent messages: {}", apiResponse);
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}
	}
	private void push(@NonNull String to, @NonNull List<Message> messages) {
		try {
			BotApiResponse apiResponse = lineMessagingClient.pushMessage(new PushMessage(to, messages)).get();
			log.info("Push messages: {}", apiResponse);
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

	private void pushText(@NonNull String to, @NonNull String message) {
		if (to.isEmpty()) {
			throw new IllegalArgumentException("to must not be empty");
		}
		if (message.length() > 1000) {
			message = message.substring(0, 1000 - 2) + "..";
		}
		this.push(to, new TextMessage(message));
	}

	/*
	private void handleSticker(String replyToken, StickerMessageContent content) {
		reply(replyToken, new StickerMessage(content.getPackageId(), content.getStickerId()));
	}
*/
	private Users getSourceUser(Event event){
		Users user = null;
		try{
			user = database.searchUser(event.getSource().getUserId());
		}
		catch(Exception ex){
			return null;
		}
		return user;
	}


	private void handleTextContent(String replyToken, Event event, TextMessageContent content)
            throws Exception {
        String text = content.getText();
		currentUser = getSourceUser(event);

		if(event.getSource().getUserId().equals("U16d4f0da660c593be7cffe7d1208f036") && text.equals("activate") ) {
			ArrayList<String> usersid= database.findallusers();
			number++;
			if(number > links.length-1) {
				number = 0;
			}
			for (int i=0;i<usersid.size();i++) {
				pushText(usersid.get(i),("Regular Healthy Tips!: \n"+ links[number]));
			}
		}

		else {
	        switch(currentUser.getStage()) {
	        	case "Init":
	        		replymsg = stageHandler.initStageHandler( text, currentUser, database);
	        		break;
	        	case "Main":
	        		replymsg = stageHandler.mainStageHandler( text, currentUser, database);
	        		break;
	        	case "LivingHabitCollector":{
	        		replymsg = stageHandler.livingHabitCollectorHandler(text, currentUser, database);
	        	}	break;
	        	case "LivingHabitEditor":
	        		replymsg = stageHandler.livingHabitCollectorEditor(text, currentUser, database);
	        		break;
	        	case "DietPlanner":
	        		replymsg = stageHandler.dietPlannerHandler(text, currentUser, database);
	        		break;
	        	case "HealthPedia":
	        		replymsg = stageHandler.healthPediaHandler(text, currentUser, database);
	        		break;
	        	/*case "FeedBack":
	        		replymsg = stageHandler.feedBackHandler(text, currentUser, database);
	        		break;
	        	case "UserGuide":
	        		replymsg = stageHandler.userGuideHandler(text, currentUser, database);
	        		break;*/
						case "Coupon":
							replymsg = stageHandler.couponHandler( text, currentUser, database);
							break;
	        	default:
	        		replymsg = "Due to some stage error, I am deactivated. To reactivate me, please block->unblock me.";
	        		break;
	        }
	}
		//database.updateUser(currentUser);
		if(toMultipleUsers(replymsg))
			pushToAll(replymsg);
		else
			this.replyText(replyToken,replymsg);

	}

	private boolean toMultipleUsers(String replymsg){
		return( replymsg.charAt(0)=='@' && replymsg.charAt(1)=='@' );
	}


	private void pushToAll(String replymsg){
		String[] replyinfo = replymsg.split("@@");
		String msg = replyinfo[1];
		for(int i = 2 ; i < replyinfo.length;i++) if(!replyinfo[i].equals("-1")) this.pushText(replyinfo[i],msg); // non null
	}

	static String createUri(String path) {
		return ServletUriComponentsBuilder.fromCurrentContextPath().path(path).build().toUriString();
	}

	private void system(String... args) {
		//Thread chatbotThread = new Thread(() -> {
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
		//});
		//chatbotThread.start();
	}
	/*
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
		*/
}
