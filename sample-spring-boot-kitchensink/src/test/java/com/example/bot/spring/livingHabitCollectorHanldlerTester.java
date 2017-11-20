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
@SpringBootTest(classes = { livingHabitCollectorHanldlerTester.class, StageHandler.class })
public class livingHabitCollectorHanldlerTester {
	//	@Autowired
	//	private SQLDatabaseEngine databaseEngine;

	@Test
	public void testCase0() throws Exception {
		boolean thrown = false;
		StageHandler stageHandler = new StageHandler();
		SQLDatabaseEngine databaseEngine = new SQLDatabaseEngine();
		String text = "dummy";
		Users currentUser= new Users("test1001","HXH");
		databaseEngine.pushUser(currentUser);
		String reply = "";
		currentUser.setSubStage(0);
		reply = stageHandler.livingHabitCollectorHandler("ha", currentUser,databaseEngine);
		if(currentUser.getSubStage() == 1) {
			reply = stageHandler.livingHabitCollectorHandler("100", currentUser,databaseEngine);//invalid
			currentUser.setSubStage(1);
			reply = stageHandler.livingHabitCollectorHandler("50", currentUser,databaseEngine);
		}
		else
			thrown = true;

		assertThat(!thrown).isEqualTo(true);
	}	
	
	@Test
	public void testCaseDefault() throws Exception {
		boolean thrown = false;
		StageHandler stageHandler = new StageHandler();
		SQLDatabaseEngine databaseEngine = new SQLDatabaseEngine();
		String text = "dummy";
		Users currentUser= new Users("test1001","HXH");
		databaseEngine.pushUser(currentUser);
		String reply = "";
		currentUser.setSubStage(3);
		reply = stageHandler.livingHabitCollectorHandler("ha", currentUser,databaseEngine);
		assertThat(!thrown).isEqualTo(true);
	}
}