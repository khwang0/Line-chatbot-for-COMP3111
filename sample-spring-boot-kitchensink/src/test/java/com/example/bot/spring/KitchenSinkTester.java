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


import com.example.bot.spring.textsender.*;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = { KitchenSinkTester.class, TextProcessor.class, SQTextSender.class })
public class KitchenSinkTester {
	@Autowired
	private TextProcessor textprocessor;
	@Autowired
	private SQTextSender sqsender;
	
	@Test
	public void testProcessText() throws Exception {
		boolean thrown = false;

		String[] result = new String[5];
		
		String[] message= { 
				"hi",
				"can you recommend",
				"tell me more",
				"can I book that",
				"not sure why here"
				};
		String[] reply = {
				"Hi! How can I help you?",
				"in recomend",
				"in general q",
				"in booking",
				"exception here"
		};
		
		String userid = "123456";
		
		try {
			for (int i = 0; i < 5; i++) {
				result[i] = this.sqsender.process(userid, message[i]);
				System.out.println(result[i]);
			}	
			
		} catch (Exception e) {
			thrown = true;	
		}
		
		for (int i = 0; i < 5; i++) {
			assertThat(result[i].contains(reply[i])).isEqualTo(true);
		}
	}
	
	@Test
	public void testSQsender() throws Exception {
		boolean thrown = false;
		
		String[] SQresult = new String[4];
		
		String userid = "123456";
		
		String[] message= { "hi","hello", "thanks", "bye" };
		String[] reply = {
			"Hi! How can I help you?", 
			"Hi! How can I help you?", 
			"You are welcome =)", 
			"have a nice day!"
		};
		
		try {
			for (int i = 0; i < 4; i++) {
				SQresult[i] = this.sqsender.process(userid, message[i]);
			}
						
		} catch (Exception e) {
			thrown = true;	
			for (int i = 0; i < 4; i++) {
				System.err.println(SQresult[i]);
			}
		}
		
		for (int i = 0; i < 4; i++) {
			assertThat(SQresult[i].contains(reply[i])).isEqualTo(true);
		}
	}
}
