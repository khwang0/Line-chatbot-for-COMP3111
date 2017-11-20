package com.example.bot.spring;


import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.InputStream;
import java.util.List;
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
//@SpringBootTest(classes = { KitchenSinkTester.class, DatabaseEngine.class })
@SpringBootTest(classes = { StageHandler.class, healthPediaHandlerTester.class })
public class healthPediaHandlerTester {
//	@Autowired
//	private StageHandler handler;

	@Test
	public void healthPediaHandlerTest_case0() {
		StageHandler handler = new StageHandler();
		SQLDatabaseEngine db = new SQLDatabaseEngine();
		Users user = new Users("test");
		boolean thrown = false;
		db.pushUser(user);
		//case 0
	
		try {
			user.setSubStage(-1);
			handler.healthPediaHandler("3", user, db);
			user.setSubStage(-1);
			handler.healthPediaHandler("10", user, db);
			user.setSubStage(-1);
			handler.healthPediaHandler("test", user, db);
			
			user.setSubStage(0);
			handler.healthPediaHandler("test", user, db);
			user.setSubStage(1);
			handler.healthPediaHandler("test", user, db);
			user.setSubStage(11);
			handler.healthPediaHandler("12312312313123", user, db);
			user.setSubStage(11);
			handler.healthPediaHandler("apple", user, db);

			user.setSubStage(21);
			handler.healthPediaHandler("1", user, db);
			user.setSubStage(21);
			handler.healthPediaHandler("2", user, db);
			user.setSubStage(21);
			handler.healthPediaHandler("abc", user, db);

		}catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
	}


	
}


		
