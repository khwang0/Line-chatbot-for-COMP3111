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
@SpringBootTest(classes = { UsersTester.class, SQLDatabaseEngine.class })
public class UsersTester {
	//@Autowired
	//private SQLDatabaseEngine databaseEngine;
	
	@Test
	public void testUserContrt1() throws Exception {
		boolean thrown = false;
		Users user = new Users("1001");
		assertThat(user != null).isEqualTo(true);

	}
	
	@Test
	public void testUserContrt2() throws Exception {
		boolean thrown = false;
		Users user = new Users("1001", "HXH");
		assertThat(user != null).isEqualTo(true);
	}
	
	@Test
	public void testUserSetEatingHabits() throws Exception {
		boolean[] eatingHabits = {false,false,false,false,false,false};
		Users user = new Users("1001", "HXH");
		user.setEatingHabits(eatingHabits);
		boolean[] read = user.getEatingHabits();

		assertThat(read.length != 0).isEqualTo(true);
	}
	
	@Test
	public void testUserSetEatingHabits2() throws Exception {
		Boolean[] eatingHabits = {false,false,false,false,false,false};
		Users user = new Users("1001", "HXH");
		user.setEatingHabits(eatingHabits);
		boolean[] read = user.getEatingHabits();

		assertThat(read.length != 0).isEqualTo(true);
	}
	
	@Test
	public void testUsertoString() throws Exception {
		boolean thrown = false;
		Users user = new Users("1001", "HXH");
		user.setAssessmentScore(-1);
		if(user.toString() == null) {
			thrown = true;
		}

		user.setAssessmentScore(20);
		if(user.toString() == null) {
			thrown = true;
		}

		user.setAssessmentScore(101);
		if(user.toString() == null) {
			thrown = true;
		}

		assertThat(!thrown).isEqualTo(true);
	}


//	@Test
//	public void testNotFound() throws Exception {
//		boolean thrown = false;
//		try {
//			this.databaseEngine.search("0000000000");
//		} catch (Exception e) {
//			thrown = true;
//		}
//		assertThat(thrown).isEqualTo(true);
//	}

}
