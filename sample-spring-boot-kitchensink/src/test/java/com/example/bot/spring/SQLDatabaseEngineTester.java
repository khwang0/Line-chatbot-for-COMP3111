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

		result = databaseEngine.search_diet_plan("1002test");
		if(result==true) {
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
		HealthSearch healthSearcherNA = new HealthSearch();
		//if case & null
		Users user3 = new Users("1003test","HXH");
		//else case
		databaseEngine.updateconsumption(healthSearcher,100, "1003test", "20171119010101",30);
		//if case -> user exist && null
		databaseEngine.updateconsumption(healthSearcherNA,100, "1003test", "20171119010122",30);

		//if case & not null
		healthSearcher.setKeyword("apple");
		healthSearcher.search();
		Users user2 = new Users("1001test","HXH");
		//else case
		databaseEngine.updateconsumption(healthSearcher,100, "1001test", "20171119010101",30);
		//if case && not null
		databaseEngine.updateconsumption(healthSearcher,100, "1001test", "20171119010122",30);

		assertThat(!thrown).isEqualTo(true);
	}

	//test search_current makes use of diet_conlusion
	@Test
	public void testSearch_current() throws Exception {
		boolean thrown =  false;
		ArrayList<Double> current_info = databaseEngine.search_current("1001test", "20171119");
		if (current_info.size() == 0) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
	}

	//set up before testing reportDiet
	@Test
	public void testPushDietRecord() throws Exception {
		boolean thrown =  false;

		FoodInput foodinput = new FoodInput("1001test","20171119010101");
		boolean result = databaseEngine.pushDietRecord(foodinput);
		assertThat(!thrown).isEqualTo(true);
	}

	///??????????
	@Test
	public void testPushFoodInfo() throws Exception {
		boolean thrown =  false;

		FoodInfo food = new FoodInfo();
		food.setFoodName("apple");
//		food.setEnergy(2);
//        food.setProtein(2);
//        food.setFiber(2);
//        food.setPrice(2);
		boolean result = databaseEngine.pushFoodInfo(food);

		assertThat(!thrown).isEqualTo(true);
	}

	//test searchFoodInfo(String foodname)
	@Test
	public void testSearchFoodInfo() throws Exception {
		boolean thrown =  false;
		FoodInfo food_info_null = databaseEngine.searchFoodInfo("banana");
		if (food_info_null != null)
			thrown = true;

		FoodInfo food_info = databaseEngine.searchFoodInfo("Chicken");
		if(food_info == null)
			thrown = false;

		assertThat(!thrown).isEqualTo(true);
	}

	//test reportDiet
	@Test
	public void testReportDiet() throws Exception {
		boolean thrown =  false;
		String answer = databaseEngine.reportDiet("20171119", "1001test");
		if (answer.equals(" ")) {
			thrown = true;
		}
		//else case
		String answer2 = databaseEngine.reportDiet("20171119", "100000test");
		if (!answer2.equals(" ")) {
			thrown = true;
		}

		assertThat(!thrown).isEqualTo(true);
	}

	//test getFoodInfo
	@Test
	public void testGetFoodInfo() throws Exception {
		boolean thrown =  false;

		String[] answer = databaseEngine.getFoodInfo();
		if (answer.length==0) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
	}

	//test findallusers()
	@Test
	public void testFindallusers() throws Exception {
		boolean thrown =  false;

		ArrayList<String> answer = databaseEngine.findallusers();
		if (answer.size()==0) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
	}

	//test updateUser
	@Test
	public void testUpdateUser() throws Exception {
		boolean thrown =  false;

		Users user = new Users("1004test","HHH");
		boolean answer = databaseEngine.updateUser(user);

		assertThat(!thrown).isEqualTo(true);
	}


//	@Test
//	public void testUpdate_users() throws Exception {
//		boolean thrown = false;
//
//		Users user = new Users("1001test","HXH");
//		//gen plan yes -> remeber to delete diet_plan after every test!!!
//		user.setAge(50);
//		boolean result = databaseEngine.updateUser(user);
//
//
//		assertThat(!thrown).isEqualTo(true);
//	}
}
