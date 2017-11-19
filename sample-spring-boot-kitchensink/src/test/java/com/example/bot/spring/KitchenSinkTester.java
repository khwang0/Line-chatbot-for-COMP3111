package com.example.bot.spring;
/*
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;

import org.junit.Test;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.bot.spring.database.RecommendationDBEngine;
import com.example.bot.spring.database.UQDBEngine;
import com.example.bot.spring.textsender.*;
import com.example.bot.spring.database.*;

@RunWith(SpringRunner.class)

@SpringBootTest(classes = { KitchenSinkTester.class, RecommendationTextSender.class, UQAutomateSender.class, TextProcessor.class, SQTextSender.class, GQTextSender.class })
public class KitchenSinkTester {
	@Autowired
	private UQAutomateSender UQSender;
	@Autowired
	private RecommendationTextSender Rsender;
	@Autowired
	private GQTextSender gqsender;   
	@Autowired
	private TextProcessor textprocessor;
	@Autowired
	private SQTextSender sqsender;
	
	private String testerId="234567";
	
	@Test
	public void UQTester() throws Exception {
		System.out.println("-------- inside UQTester --------------");
		boolean thrown = false;
		String result = null;
		UQSender = new UQAutomateSender();
		try {
			result = this.UQSender.process(testerId, "Stupid");
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo("Sorry, I cannot answer your question.");
	}

  // only applicable when textProcessor calling no external function
	@Ignore("not ready yet") @Test
	public void TextProcessorTester() throws Exception {
		System.out.println("-------- inside TextProcessorTester --------------");
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
		
		try {
			for (int i = 0; i < 5; i++) {
				result[i] = this.sqsender.process(testerId, message[i]);
				System.out.println(result[i]);
			}				
		} catch (Exception e) {
			thrown = true;	
		}
		
		for (int i = 0; i < 5; i++)
			assertThat(result[i].contains(reply[i])).isEqualTo(true);
	}
	
	@Test
	public void SQTester() throws Exception {
		System.out.println("-------- inside SQTester --------------");
		boolean thrown = false;
		int testNum = 5; 
		
		String[] SQresult = new String[testNum];
		
		String userid = "123456";
		
		String[] message= { "hi","hello", "thanks", "bye", "hi! I am wondering..." };
		String[] reply = {
			"Hi! How can I help you?", 
			"Hi! How can I help you?", 
			"You are welcome =)", 
			"have a nice day!",
			"Hi! How can I help you?"
		};
		
		try {
			for (int i = 0; i < testNum; i++) {
				SQresult[i] = this.sqsender.process(testerId, message[i]);
			}
						
		} catch (Exception e) {
			thrown = true;	
			for (int i = 0; i < testNum; i++) {
				System.err.println(SQresult[i]);
			}
		}
		
		for (int i = 0; i < testNum; i++) {
			assertThat(SQresult[i].contains(reply[i])).isEqualTo(true);
		}
	}
    
	@Test
	public void GQTester() throws Exception {
		System.out.println("-------- inside GQTester --------------");
		boolean thrown = false;
		boolean WA = false;
		int length=2;
		String[] inputs= {
				"could you please introduce the shenzhen city tour?",
				"how long is the trip?"};
		String[] outputs= {
				"Window of The World  * Splendid China & Chinese Folk Culture Village * Dafen Oil Painting Village (All tickets included)",
				"3 days"
		};
		//System.err.println("it is still working here");
		String reply;
		try {
			for(int i=0;i<length;i++) {
				reply=gqsender.process(testerId,inputs[i]);
				System.out.println("inputs [" + i + "]: " + inputs[i]);
				System.out.println("reply [" + i + "]: " + reply);
				if(!reply.contains(outputs[i])) {
					WA = true;
				}
			}
		}catch(Exception e) {
			System.err.println(e.getMessage());
			thrown = true;
		}
		assertThat(WA).isEqualTo(false);
		assertThat(thrown).isEqualTo(false);
	}
	
	@Test
	public void RecommendationTester() throws Exception {
		System.out.println("-------- inside RecommendationTester --------------");
		boolean thrown = false;
		String result = null;
		//ArrayList<String> temp = new ArrayList<String>();
		Rsender = new RecommendationTextSender();
		//temp.add("food");
		//temp.add("spring");
		//System.err.println(temp.get(0) + " " + temp.get(1));
		//System.err.println("it is good here");
		try {
			//System.err.println("it is good here");
			result = this.Rsender.process(testerId,"food spring");
		} catch (Exception e) {
			//System.err.println(e.getMessage());
			e.printStackTrace();
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo("Tours with good spring : 2D001: Shimen National Forest Tour\n2D002: Yangshan Hot Spring Tour\n2D003: Heyuan Hotspring Tour\n\nNo tours with good food.");
		try {
			//System.err.println("it is good here");
			result = this.Rsender.process(testerId,"I want nothing");
		} catch (Exception e) {
			//System.err.println(e.getMessage());
			e.printStackTrace();
			thrown = true;
		}
		assertThat(thrown).isEqualTo(true);
		//assertThat(result).isEqualTo("No matching");
		try {
			//System.err.println("it is good here");
			result = this.Rsender.process(testerId,"hotel view");
		} catch (Exception e) {
			//System.err.println(e.getMessage());
			e.printStackTrace();
			thrown = true;
		}
		//assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo("Tours with good hotel : 2D001: Shimen National Forest Tour\n2D004: National Park Tour\n\n"
				+ "Tours with good view : 2D001: Shimen National Forest Tour\n2D002: Yangshan Hot Spring Tour\n2D003: Heyuan Hotspring Tour\n2D004: National Park Tour\n2D005: Yummy Sight seeing Tour..");
	}
	
	@Test
	public void bookingTester() throws Exception {
		System.out.println("-------- inside bookingTester --------------");
		
		BookingTextSender bookingTS = new BookingTextSender();
		String reply = null;
		reply = bookingTS.process(testerId, "I would like to book tour 2D001");
		reply = bookingTS.process(testerId, "Yes.");
		assertThat(reply).contains("On which date you are going? (in DD/MM format)");
		reply = bookingTS.process(testerId, "18/11");

		assertThat(reply).isEqualTo("Your name please (Firstname LASTNAME)");
		reply = bookingTS.process(testerId, "233 HHH");
		assertThat(reply).isEqualTo("How many adults?");

		reply = bookingTS.process(testerId, "2");
		assertThat(reply).contains("How many children (Age 4 to 11)?");
		reply = bookingTS.process(testerId, "3");
		assertThat(reply).contains("How many children (Age 0 to 3)?");
		reply = bookingTS.process(testerId, "0");
		assertThat(reply).contains("Your phone number please.");
		reply = bookingTS.process(testerId, "12345678");
		reply = bookingTS.process(testerId, "Yes.");
		assertThat(reply).contains("Thank you. Please pay the tour fee by ATM to "
							+ "123-345-432-211 of ABC Bank or by cash in our store."
							+ "When you complete the ATM payment, please send the bank "
							+ "in slip to us. Our staff will validate it.");
	}
}
*/


