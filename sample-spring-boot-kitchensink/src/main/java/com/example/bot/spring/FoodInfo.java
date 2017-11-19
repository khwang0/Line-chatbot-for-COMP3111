package com.example.bot.spring;

public class FoodInfo{
	protected String foodName;
	protected double energy;
	protected double protein;
	protected double fiber;
	protected int price;

	public FoodInfo() {}
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
