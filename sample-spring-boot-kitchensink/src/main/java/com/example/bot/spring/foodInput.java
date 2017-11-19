package com.example.bot.spring;

public class foodInput{
	
	private final String key;  //key is the combination of userID and time
	protected String UID;
	protected String currentTime;
	protected int amount;
	protected String foodName;
	protected float price;
	
	public foodInput(String UID,String time) {
		this.UID = UID;
		currentTime = time;
		key = UID+time;
	}
	public String getKey() {return key;}
	public String getId() {return UID;}
	public String getTime() {return currentTime;}
	public float getPrice() {return price;}
	public int getAmount() {return amount;}
	public String getFoodName() {return foodName;}
	public void setFood(String food) {foodName = food;}
	public void setAmount(int n) {amount = n;}
	public void setPrice(float price) {this.price = price;}
	
}