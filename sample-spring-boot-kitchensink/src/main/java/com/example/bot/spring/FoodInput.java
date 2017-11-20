package com.example.bot.spring;
/**
* FoodInput stores the informations that are provided by clients, which are going to be pushed to the dietrecord database.
*
* @author  G8
* @version 1.0
* @since   2017/11/19
*/
public class FoodInput{
	/**
	* key is the combination of userID and time
	*/
	private final String key;  //key is the combination of userID and time
	
	/**
	* UID stores the user id of the user who input this food
	*/
	protected String UID;
	
	/**
	* the time when the user input the food, in String format "YYYYMMDD"
	*/
	protected String currentTime;
	
	/**
	* the amount of the food
	*/
	protected int amount;
	
	/**
	* the name of the food
	*/
	protected String foodName;
	
	/**
	* the price of the food
	*/
	protected double price;
	
	/**
	* Contructor of FoodInput with UID and time specified
	* @param UID the user ID of user who input the food
	* @param time time when user input the message
	*/
	public FoodInput(String UID,String time) {
		this.UID = UID;
		currentTime = time;
		key = UID+time;
		foodName = "N/A";
		amount = 0;
		price = 0;
	}
	/**
	* This method is to get the key of the FoodInput
	* @return String This returns the key of the FoodInput
	*/
	public String getKey() {return key;}
	/**
	* This method is to get the User Id of the FoodInput
	* @return String This returns the User Id of the FoodInput
	*/
	public String getId() {return UID;}
	/**
	* This method is to get the time of the FoodInput
	* @return String This returns the time of the FoodInput
	*/
	public String getTime() {return currentTime;}
	/**
	* This method is to get the price of the FoodInput
	* @return double This returns the price of the FoodInput
	*/
	public double getPrice() {return price;}
	/**
	* This method is to get the amount of the FoodInput
	* @return int This returns the amount of the FoodInput
	*/
	public int getAmount() {return amount;}
	/**
	* This method is to get the foodname of the FoodInput
	* @return String This returns the foodname of the FoodInput
	*/
	public String getFoodName() {return foodName;}
	/**
	* This method is to change the foodname of the FoodInput
	* @param foodname the foodname of the FoodInput that will be changed to
	*/
	public void setFoodName(String foodname) {foodName = foodname;}
	/**
	* This method is to change the amount of the FoodInput
	* @param n the amount of the FoodInput that will be changed to
	*/
	public void setAmount(int n) {amount = n;}
	/**
	* This method is to change the price of the FoodInput
	* @param price the price of the FoodInput that will be changed to
	*/
	public void setPrice(double price) {this.price = price;}

}
