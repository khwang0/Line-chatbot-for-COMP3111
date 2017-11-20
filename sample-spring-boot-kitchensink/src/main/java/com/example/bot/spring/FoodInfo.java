package com.example.bot.spring;
/**
* FoodInfo will be the object that stores the value that will be pushed in the
* foodinfo table in database (AKA local food database)
* @author  G8
* @version 1.0
* @since   2017/11/19
*/
public class FoodInfo{
	private String foodName;
	private double energy;
	private double protein;
	private double fiber;
	private int price;

	/**
	* Constructor of Food Into
	*/
	public FoodInfo() {
		energy = 0;
		protein = 0;
		fiber = 0;
		price = 0;
	}

	/**
	* Set the foodName of FoodInfo
	* @param foodname  the input parameter of the set function
	*/
	public void setFoodName(String foodname) {
		this.foodName = foodname;
	}

	/**
	* Set the energy of FoodInfo
	* @param energy  the input parameter of the set function
	*/
	public void setEnergy(double energy) {
		this.energy = energy;
	}
	/**
	* Set the fiber of FoodInfo
	* @param fiber  the input parameter of the set function
	*/
	public void setFiber(double fiber) {
		this.fiber = fiber;
	}
	/**
	* Set the price of FoodInfo
	* @param price  the input parameter of the set function
	*/
	public void setPrice(int price) {
		this.price = price;
	}
	/**
	* Set the protein of FoodInfo
	* @param protein  the input parameter of the set function
	*/
	public void setProtein(double protein) {
		this.protein = protein;
	}

	/**
	* Get the foodName of FoodInfo
	* @return the name of the FoodInfo
	*/
	public String getFoodName() {
		return foodName;
	}
	/**
	* Get the energy of FoodInfo
	* @return the energy of the FoodInfo
	*/
	public double getEnergy() {
		return energy;
	}
	/**
	* Get the protein of FoodInfo
	* @return the protein of the FoodInfo
	*/
	public double getProtein() {
		return protein;
	}
	/**
	* Get the fiber of FoodInfo
	* @return the fiber of the FoodInfo
	*/
	public double getFiber() {
		return fiber;
	}
	/**
	* Get the price of FoodInfo
	* @return the price of the FoodInfo
	*/
	public int getPrice() {
		return price;
	}
}
