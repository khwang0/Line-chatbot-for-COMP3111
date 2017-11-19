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

@SpringBootTest(classes = {GQTester.class, TextProcessor.class})
public class GQTester {
	@Autowired
	private TextProcessor tp;
	
	private String testerId="gq_tester";

	@Test
	public void GQTesterF() throws Exception {
		String msgs[]= {"","How long","Feature"};
		for (String msg:msgs) {
			try{//should fail
				tp.processText(testerId, msg);
			}catch(Exception e) {
				
			}
		}
	}
	
	@Test
	public void GQTesterS() throws Exception {
		String msgs[]= {"How long 2D001?","Feature of 2D001.","How apply"};
		for (String msg:msgs) {
			try{//should fail
				tp.processText(testerId, msg);
			}catch(Exception e) {
				
			}
		}
	}
}


