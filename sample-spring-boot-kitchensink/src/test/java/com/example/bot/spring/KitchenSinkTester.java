package com.example.bot.spring;


import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;

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

import com.example.bot.spring.database.RecommendationDBEngine;
import com.example.bot.spring.database.UQDBEngine;

import com.example.bot.spring.textsender.*;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = { KitchenSinkTester.class, RecommendationDBEngine.class, UQDBEngine.class })
public class KitchenSinkTester {
	@Autowired
	private UQDBEngine UQEngine;
	@Autowired
	private RecommendationDBEngine REngine;
	
//	@Test
//	public void simpleReply() throws Exception {
//	}
	
	@Test
	public void testUQ() throws Exception {
		boolean thrown = false;
		String result = null;
		UQEngine = new UQDBEngine();
		try {
			result = this.UQEngine.uqQuery("123456", "Stupid");
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo("Sorry, I can't answer your question. My colleague will follow up with you.");
	}
	
	@Test
	public void testRecommendation() throws Exception {
		boolean thrown = false;
		String result = null;
		ArrayList<String> temp = new ArrayList<String>();
		REngine = new RecommendationDBEngine();
		temp.add("food");
		temp.add("spring");
		System.err.println(temp.get(0) + " " + temp.get(1));
		System.err.println("it is good here");
		try {
			System.err.println("it is good here");
			result = this.REngine.recommendationQuery("123456",temp);
		} catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo("Tours with good food : 2D005\nTours with good spring : 2D001, 2D002, 2D003\n");
	}
	
	
}
