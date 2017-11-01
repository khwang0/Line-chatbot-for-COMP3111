package com.example.bot.spring;

public class foodInput{
	
	private final String key;  //key is the combination of userID and time
	protected String UID;
	protected String currentTime;
	protected int amount;
	protected String foodName;
	
	public foodInput(String UID,String time) {
		this.UID = UID;
		currentTime = time;
		key = UID+time;
	}
	
	
	
	public String getId() {return UID;}
	public String getTime() {return currentTime;}
	public int getAmount() {return amount;}
	public String foodName() {return foodName;}
	
	
	
	public void setFood(String food) {foodName = food;}
	public void setAmount(int n) {amount = n;}
	
}