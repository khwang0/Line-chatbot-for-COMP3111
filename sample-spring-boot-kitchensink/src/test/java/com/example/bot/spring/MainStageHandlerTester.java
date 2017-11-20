package com.example.bot.spring;


import static org.assertj.core.api.Assertions.assertThat;


import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.InputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;

import com.google.common.io.ByteStreams;

import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.FollowEvent;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.MessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.spring.boot.annotation.LineBotMessages;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { MainStageHandlerTester.class, StageHandler.class })
public class MainStageHandlerTester {
	//	@Autowired
	//	private SQLDatabaseEngine databaseEngine;

	@Test
	public void testMainStageHandler0() throws Exception {
		boolean thrown = false;
		StageHandler stageHandler = new StageHandler();
		SQLDatabaseEngine databaseEngine = new SQLDatabaseEngine();
		String text = "dummy";
		Users currentUser= new Users("test1001","HXH");
		databaseEngine.pushUser(currentUser);
		currentUser.setSubStage(0);
		//case 0 if
		currentUser.setBodyFat(0);
		String reply = stageHandler.mainStageHandler(text, currentUser, databaseEngine);
		if (currentUser.getSubStage() != 1) {
			thrown = true;
		}
		////		//case 0 else
		currentUser.setSubStage(0);
		currentUser.setBodyFat(1);
		reply = stageHandler.mainStageHandler(text, currentUser, databaseEngine);
		if (currentUser.getSubStage() != 1) {
			thrown = true;
		}		
		assertThat(!thrown).isEqualTo(true);
	}	

	//case 1 
	@Test
	public void testMainStageHandler1() throws Exception {
		boolean thrown = false;
		StageHandler stageHandler = new StageHandler();
		SQLDatabaseEngine databaseEngine = new SQLDatabaseEngine();
//		String dummyReplyToken = "nHuyWiB7yP5Zw52FIkcQobQuGDXCTA";
		String text = "dummy";
		Users currentUser= new Users("test1001","HXH");
		databaseEngine.pushUser(currentUser);
		currentUser.setSubStage(1);
		//case 1 if
		currentUser.setBodyFat(0);
		String reply = stageHandler.mainStageHandler("1", currentUser, databaseEngine);
		if (currentUser.getSubStage() != 0) {
			thrown = true;
		}
		//case 1 else
		currentUser.setSubStage(1);
		currentUser.setBodyFat(1);
		reply = stageHandler.mainStageHandler( "1", currentUser, databaseEngine);
		if (currentUser.getSubStage() != 0) {
			thrown = true;
		}		
		assertThat(!thrown).isEqualTo(true);
	}

	//case code
	//	@Test
	//	public void testMainStageHandlerCode() throws Exception {
	//		boolean thrown = false;
	//		StageHandler stageHandler = new StageHandler();
	//		SQLDatabaseEngine databaseEngine = new SQLDatabaseEngine();
	//		String dummyReplyToken = "nHuyWiB7yP5Zw52FIkcQobQuGDXCTA";
	//		String text = "dummy";
	//		Users currentUser= new Users("test1001","HXH");
	//		databaseEngine.pushUser(currentUser);
	//		currentUser.setSubStage(1);
	//		//case 1 if
	//		currentUser.setBodyFat(0);
	//		String reply = stageHandler.mainStageHandler( "code", currentUser, databaseEngine);
	//		if (currentUser.getSubStage() != 0) {
	//			thrown = true;
	//		}
	//		//case 1 else
	//		currentUser.setSubStage(1);
	//		currentUser.setBodyFat(1);
	//		reply = stageHandler.mainStageHandler( "1", currentUser, databaseEngine);
	//		if (currentUser.getSubStage() != 0) {
	//			thrown = true;
	//		}		
	//	assertThat(!thrown).isEqualTo(true);
	//}

}