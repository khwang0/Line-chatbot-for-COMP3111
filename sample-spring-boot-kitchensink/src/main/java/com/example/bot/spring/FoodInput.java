package com.example.bot.spring;

public class FoodInput{

	private final String key;  //key is the combination of userID and time
	protected String UID;
	protected String currentTime;
	protected int amount;
	protected String foodName;
	protected double price;

	public FoodInput(String UID,String time) {
		this.UID = UID;
		currentTime = time;
		key = UID+time;
	}
	public String getKey() {return key;}
	public String getId() {return UID;}
	public String getTime() {return currentTime;}
	public double getPrice() {return price;}
	public int getAmount() {return amount;}
	public String getFoodName() {return foodName;}
	public void setFoodName(String foodname) {foodName = foodname;}
	public void setAmount(int n) {amount = n;}
	public void setPrice(double price) {this.price = price;}

}
