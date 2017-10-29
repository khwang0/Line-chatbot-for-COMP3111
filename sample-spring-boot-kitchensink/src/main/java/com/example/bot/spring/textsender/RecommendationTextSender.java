package com.example.bot.spring.textsender;

import java.util.ArrayList;

public class RecommendationTextSender implements TextSender {

	public RecommendationTextSender() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String process(String userId, String msg) {
		// TODO Auto-generated method stub
		RecommandationDBEngine searchEngine = new RecommandationDBEngine();
		//assume the features are 1.hotel, 2.spring, 3.view
		ArrayList<String> featureList = new ArrayList();
		if (msg.contains("hotel")){
			featureList.add("hotel");
		}
		if (msg.contains("spring")){
			featureList.add("spring");
		}
		if (msg.contains("view")||msg.contains("sight")){
			featureList.add("view");
		}
		return searchEngine(userId, featureList);
	}
	
}
