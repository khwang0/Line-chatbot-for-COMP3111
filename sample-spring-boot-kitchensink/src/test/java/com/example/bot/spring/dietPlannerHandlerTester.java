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
@SpringBootTest(classes = { StageHandler.class, dietPlannerHandlerTester.class })
public class dietPlannerHandlerTester {
	@Autowired
	private StageHandler handler;
	
	@Test
	public void initStageHandlerTest() {
		StageHandler handler = new StageHandler();
		SQLDatabaseEngine db = new SQLDatabaseEngine();
		Users user = new Users("test");
		boolean thrown = false;
		user.setSubStage(0);
		db.pushUser(user);
		String[] validInput = 	{"valid ", 
								"validname",
								"M",
								"180",
								"70",
								"30"
								};
		String[] invalidInput = 	{"valid", 
								"invalidinvalidinvalidinvalidinvalidinvalidinvalidinvalidinvalidinvalidinvalidinvalidinvalidinvalidinvalid",
								"invalid",
								"invalid",
								"invalid",
								"invalid"
								};
		try {
			for(int subStage=0; subStage <=5; subStage++){
				user.setSubStage(subStage);
				handler.initStageHandler( validInput[subStage], user, db);
				handler.initStageHandler( invalidInput[subStage], user, db);
			}
		}catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
	}

	@Test
	public void dietPlannerHandlerTest_case0() {
		StageHandler handler = new StageHandler();
		SQLDatabaseEngine db = new SQLDatabaseEngine();
		Users user = new Users("test");
		boolean thrown = false;
		user.setSubStage(0);
		db.pushUser(user);
		//case 0
	
		try {
				user.setBodyFat(0);
				handler.dietPlannerHandler( "test", user, db);
				user.setBodyFat(30);
				handler.dietPlannerHandler("test", user, db);
		}catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
	}
	
	@Test
	public void dietPlannerHandlerTest_case_easy_case() {
		StageHandler handler = new StageHandler();
		SQLDatabaseEngine db = new SQLDatabaseEngine();
		Users user = new Users("test");
		boolean thrown = false;
		db.pushUser(user);
		//case 0

		try {
			user.setSubStage(-2);
			handler.dietPlannerHandler( "q", user, db);
			user.setSubStage(-2);
			handler.dietPlannerHandler( "5", user, db);
			user.setSubStage(-1);
			handler.dietPlannerHandler( "5", user, db);
			user.setSubStage(-1);
			handler.dietPlannerHandler( "10", user, db);
			
			for(int subStage = 1; subStage < 5; subStage ++) {
				user.setSubStage(subStage);
				handler.dietPlannerHandler( "test", user, db);
			}

			user.setSubStage(7);
			handler.dietPlannerHandler("10", user, db);
			user.setSubStage(8);
			handler.dietPlannerHandler("10", user, db);			
			user.setSubStage(11);
			handler.dietPlannerHandler("1010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010", user, db);
			user.setSubStage(11);
			handler.dietPlannerHandler("10", user, db);
			user.setSubStage(12);
			handler.dietPlannerHandler("-100", user, db);
			user.setSubStage(12);
			handler.dietPlannerHandler("500", user, db);			
			user.setSubStage(13);
			handler.dietPlannerHandler("-100", user, db);
			user.setSubStage(13);
			handler.dietPlannerHandler("500", user, db);			
			user.setSubStage(14);
			handler.dietPlannerHandler("1", user, db);
			user.setSubStage(14);
			handler.dietPlannerHandler("abc", user, db);			
			user.setSubStage(16);
			handler.dietPlannerHandler("1010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010", user, db);
			user.setSubStage(16);
			handler.dietPlannerHandler("abc", user, db);			
			user.setSubStage(17);
			handler.dietPlannerHandler("abc", user, db);
			user.setSubStage(17);
			handler.dietPlannerHandler("40", user, db);			
			user.setSubStage(18);
			handler.dietPlannerHandler("abc", user, db);
			user.setSubStage(18);
			handler.dietPlannerHandler("40", user, db);			
			user.setSubStage(19);
			handler.dietPlannerHandler("100", user, db);
			user.setSubStage(19);
			handler.dietPlannerHandler("abc", user, db);			
			user.setSubStage(20);
			handler.dietPlannerHandler("100", user, db);
			user.setSubStage(20);
			handler.dietPlannerHandler("abc", user, db);			
			user.setSubStage(22);
			handler.dietPlannerHandler("20171111", user, db);
			user.setSubStage(22);
			handler.dietPlannerHandler("abc", user, db);

		}catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
	}
	
	@Test
	public void dietPlannerHandlerTest_case_5_6() {
		StageHandler handler = new StageHandler();
		SQLDatabaseEngine db = new SQLDatabaseEngine();
		Users user = new Users("test");
		user.setGender('M');
		user.setAge(30);
		
		boolean thrown = false;
		db.pushUser(user);
		//case 5,6
		try {
			user.setSubStage(6);
			handler.dietPlannerHandler("10", user, db);
			user.setSubStage(5);
			handler.dietPlannerHandler( "5", user, db);
			user.setSubStage(5);
			handler.dietPlannerHandler("10", user, db);
			user.setSubStage(6);
			handler.dietPlannerHandler("10", user, db);

		}catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
	}
	
	@Test
	public void dietPlannerHandlerTest_case_511() {
		StageHandler handler = new StageHandler();
		SQLDatabaseEngine db = new SQLDatabaseEngine();
		Users user = new Users("test");
		
		boolean thrown = false;
		db.pushUser(user);
		//case 5,6
		try {
			user.setSubStage(511);
			user.setAssessmentScore(10);
			handler.dietPlannerHandler("abc", user, db);
			user.setSubStage(511);
			user.setAssessmentScore(100);
			handler.dietPlannerHandler("abc", user, db);			
			user.setSubStage(511);
			user.setAssessmentScore(60);
			handler.dietPlannerHandler("abc", user, db);			
			user.setSubStage(511);
			user.setAssessmentScore(80);
			handler.dietPlannerHandler("abc", user, db);
		}catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
	}
	
	@Test
	public void dietPlannerHandlerTest_case_512() {
		StageHandler handler = new StageHandler();
		SQLDatabaseEngine db = new SQLDatabaseEngine();
		Users user = new Users("test");
		
		boolean thrown = false;
		db.pushUser(user);
		//case 5,6
		try {
			user.setSubStage(512);
			handler.dietPlannerHandler("Y", user, db);
			user.setSubStage(512);
			handler.dietPlannerHandler("abc", user, db);			

		}catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
	}
	
	
	@Test
	public void dietPlannerHandlerTest_case_default1() {
		StageHandler handler = new StageHandler();
		SQLDatabaseEngine db = new SQLDatabaseEngine();
		Users user = new Users("test");
		
		boolean thrown = false;
		db.pushUser(user);
		//case 5,6
		try {
			user.setAssessmentScore(0);
			user.setSubStage(123);
			handler.dietPlannerHandler("T", user, db);


		}catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(false);
	}
	
	@Test
	public void dietPlannerHandlerTest_case_default2() {
		StageHandler handler = new StageHandler();
		SQLDatabaseEngine db = new SQLDatabaseEngine();
		Users user = new Users("test");
		
		boolean thrown = false;
		db.pushUser(user);
		//case 5,6
		try {
			user.setAssessmentScore(0);
			user.setSubStage(123);
			handler.dietPlannerHandler("F", user, db);


		}catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(false);
	}
	
	@Test
	public void dietPlannerHandlerTest_case_default3() {
		StageHandler handler = new StageHandler();
		SQLDatabaseEngine db = new SQLDatabaseEngine();
		Users user = new Users("test");
		
		boolean thrown = false;
		db.pushUser(user);
		//case 5,6
		try {
			user.setAssessmentScore(0);
			user.setSubStage(510);
			handler.dietPlannerHandler("123", user, db);


		}catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
	}
	
	@Test
	public void dietPlannerHandlerTest_case_default4() {
		StageHandler handler = new StageHandler();
		SQLDatabaseEngine db = new SQLDatabaseEngine();
		Users user = new Users("test");
		
		boolean thrown = false;
		db.pushUser(user);
		//case 5,6
		try {
			user.setAssessmentScore(0);
			user.setSubStage(123);
			handler.dietPlannerHandler("q", user, db);


		}catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
	}
	
	//many test for the case 42&43
	@Test
	public void dietPlannerHandlerTest_42case_1() {
		StageHandler handler = new StageHandler();
		SQLDatabaseEngine db = new SQLDatabaseEngine();
		Users user = new Users("test");
		
		boolean thrown = false;
		db.pushUser(user);
		//case 5,6
		try {
			user.setSubStage(42);
			handler.dietPlannerHandler("banana apple 40\nbanana water 50", user, db);
		}catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
	}
	
	@Test
	public void dietPlannerHandlerTest_42case_2() {
		StageHandler handler = new StageHandler();
		SQLDatabaseEngine db = new SQLDatabaseEngine();
		Users user = new Users("test");
		
		boolean thrown = false;
		db.pushUser(user);
		//case 5,6
		try {
			user.setSubStage(42);
			handler.dietPlannerHandler("Chili Chicken 40", user, db);
		}catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
	}
	
	@Test
	public void dietPlannerHandlerTest_42case_3() {
		StageHandler handler = new StageHandler();
		SQLDatabaseEngine db = new SQLDatabaseEngine();
		Users user = new Users("test");
		
		boolean thrown = false;
		db.pushUser(user);
		//case 5,6
		try {
			user.setSubStage(42);
			handler.dietPlannerHandler("banana apple", user, db);
		}catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
	}
	
	@Test
	public void dietPlannerHandlerTest_43case_1() {
		StageHandler handler = new StageHandler();
		SQLDatabaseEngine db = new SQLDatabaseEngine();
		Users user = new Users("test");
		
		boolean thrown = false;
		db.pushUser(user);
		//case 5,6
		try {
			user.setSubStage(43);
			handler.dietPlannerHandler("http://www.json-generator.com/api/json/get/cedsZnfwvC?indent=2", user, db);
		}catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
	}
	
	@Test
	public void dietPlannerHandlerTest_43case_2() {
		StageHandler handler = new StageHandler();
		SQLDatabaseEngine db = new SQLDatabaseEngine();
		Users user = new Users("test");
		
		boolean thrown = false;
		db.pushUser(user);
		//case 5,6
		try {
			user.setSubStage(43);
			handler.dietPlannerHandler("lalalalalla", user, db);
		}catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
	}
	
	@Test
	public void dietPlannerHandlerTest_43case_3() {
		StageHandler handler = new StageHandler();
		SQLDatabaseEngine db = new SQLDatabaseEngine();
		Users user = new Users("test");
		
		boolean thrown = false;
		db.pushUser(user);
		//case 5,6
		try {
			user.setSubStage(43);
			handler.dietPlannerHandler("https://www.google.com.hk/search?q=LINE+class+event&oq=LINE+class+event&aqs=chrome..69i57j0.3891j0j4&sourceid=chrome&ie=UTF-8", user, db);
		}catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
	}
	
}


		
