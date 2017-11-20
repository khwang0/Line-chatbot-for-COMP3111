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

	public FoodInfo() {
		energy = 0;
		protein = 0;
		fiber = 0;
		price = 0;
	}
	public void setFoodName(String foodname) {
		this.foodName = foodName;
	}
	public void setEnergy(double energy) {
		this.energy = energy;
	}
	public void setFiber(double fiber) {
		this.fiber = fiber;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	public void setProtein(double protein) {
		this.protein = protein;
	}
	public String getFoodName() {
		return foodName;
	}
	public double getEnergy() {
		return energy;
	}
	public double getProtein() {
		return protein;
	}
	public double getFiber() {
		return fiber;
	}
	public int getPrice() {
		return price;
	}
}
