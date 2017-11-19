package com.example.bot.spring;

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

@SpringBootTest(classes = {GQTester.class, GQTextSender.class})
public class GQTester {
	@Autowired
	private GQTextSender gqsender;
	
	private String testerId="gq_tester";
	
	@Test
	public void UQTesterF() throws Exception {
		//should fail
		String msg="How long";
		gpsender.process(testerId, msg);
		msg="Feature";
		gpsender.process(testerId, msg);
	}
	
	@Test
	public void UQTesterS() throws Exception {
		String msg="How long 2D001?"
		gpsender.process(testerId, msg);
		msg="Feature of 2D001."
		gpsender.process(testerId, msg);
		msg="How apply";
		gqsender.process(testerId, msg);
	}
}


