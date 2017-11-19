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

import com.example.bot.spring.DatabaseEngine;


@RunWith(SpringRunner.class)
//@SpringBootTest(classes = { KitchenSinkTester.class, DatabaseEngine.class })
@SpringBootTest(classes = { SQLDatabaseEngineTester.class, SQLDatabaseEngine.class})
public class SQLDatabaseEngineTester {
	@Autowired
	private SQLDatabaseEngine databaseEngine;
	
	@Test
	public void testPush_SearchUser() throws Exception {
		boolean thrown = false;
		Users user = new Users("1001test","HXH");
		boolean[] eatingHabits = {false,true,false,false,false,false};
		user.setEatingHabits(eatingHabits);
		try {
			databaseEngine.pushUser(user);
		}catch (Exception e) {
			thrown = true;
		}
		//input successfully
		if(!thrown) {
			Users user_result = null;
			try {
				user_result = databaseEngine.searchUser("1001test");
				if(user_result==null) {
					thrown = true;
				}
			}catch (Exception e) {
				thrown = true;
			}
		}
		assertThat(!thrown).isEqualTo(true);		
	}
	
	@Test
	public void testNotSearchUser() throws Exception {
		boolean thrown = false;
		Users user_result  = null;
		try {
			user_result = databaseEngine.searchUser("1002test");
			if(user_result!=null) {
				thrown = true;
			}
		}catch (Exception e) {
			if(user_result!=null) {
				thrown = true;
			}
		}
		assertThat(!thrown).isEqualTo(true);	
	}
	
	@Test
	public void testSearch_intake_reference() throws Exception {
		boolean thrown = false;
		
		boolean result = databaseEngine.search_intake_reference("M", 5);
		if(result==true) {
			thrown = true;
		}
		
		result = databaseEngine.search_intake_reference("M", 15);
		if(result==false) {
			thrown = true;
		}
		
		assertThat(!thrown).isEqualTo(true);	
	}
	
	@Test
	public void testGen_Search_diet_plan() throws Exception {
		boolean thrown = false;
		
		Users user = new Users("1001test","HXH");
		//gen plan yes -> remeber to delete diet_plan after every test!!!
		user.setAge(50);
		boolean result = databaseEngine.gen_plan(user);
		if(result==false) {
			thrown = true;
		}
		//gen plan no - impossible
//		user.setAge(5);
//		result = databaseEngine.gen_plan(user);
//		if(result==false) {
//			thrown = true;
//		}
		
		//search_diet_plan and search_plan
		result = databaseEngine.search_diet_plan("1001test");
		if(result==false) {
			thrown = true;
		}
		
		ArrayList<Double> plan_info = databaseEngine.search_plan("1001test");
		if(plan_info.size()==0) {
			thrown = true;
		}
		
		assertThat(!thrown).isEqualTo(true);	
	}
	
	//test updateconsumption
	@Test
	public void testUpdateconsumption() throws Exception {
		boolean thrown = false;
		HealthSearch healthSearcher = new HealthSearch();
		healthSearcher.setKeyword("apple");
		
		Users user = new Users("1001test","HXH");
		//gen plan yes -> remeber to delete diet_plan after every test!!!
		user.setAge(50);
		boolean result = databaseEngine.updateUser(user);

		
		assertThat(!thrown).isEqualTo(true);	
	}
	
	//test reportDiet
	//test 
	
	
	@Test
	public void testUpdate_users() throws Exception {
		boolean thrown = false;
		
		Users user = new Users("1001test","HXH");
		//gen plan yes -> remeber to delete diet_plan after every test!!!
		user.setAge(50);
		boolean result = databaseEngine.updateUser(user);

		
		assertThat(!thrown).isEqualTo(true);	
	}
}
	