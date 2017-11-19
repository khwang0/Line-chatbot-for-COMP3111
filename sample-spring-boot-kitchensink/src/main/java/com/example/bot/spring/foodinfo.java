package com.example.bot.spring;

public class foodinfo{
	protected String food;
	protected float energy;
	protected float protein;
	protected float fiber;
	protected int price;
	
	public foodinfo() {}
	public void setFood(String food) {
		this.food = food;
	}
	public void setEnergy(float energy) {
		this.energy = energy;
	}
	public void setFiber(float fiber) {
		this.fiber = fiber;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	public void setProtein(float protein) {
		this.protein = protein;
	}
	public String getFood() {
		return food;
	}
	public float getEnergy() {
		return energy;
	}
	public float getProtein() {
		return protein;
	}
	public float getFiber() {
		return fiber;
	}
	public int getPrice() {
		return price;
	}
}