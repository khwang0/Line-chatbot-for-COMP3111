package com.example.bot.spring.textsender;

public class SQTextSender implements TextSender {
	private SQDBEngine sqdbengine; 

	public SQTextSender() {
		// TODO Auto-generated constructor stub
		this.sqdbengine = new SQDBEngine();
	}
	
	@Override
	public String process(String userId, String msg) {
		if(msg == null) {
			return "unvalid input for SQTextSender.";
		}
		// TODO Auto-generated method stub
		/* Label: greeting/ thanks/ goodbye*/
		String label = null; 
		// if msg contains certain keywords, label it
		label = sqdbengine.search(msg);
		
		switch (label) {
			case "greeting":
				return "Hi! How can I help yoU?";
			case "Thanks":
				return "You are welcome =)";
			case "goodbye":
				return "have a nice day!";
			default:
				return "Sorry /o\\, not understand"; 
		}
				
		return null;
	}
}
