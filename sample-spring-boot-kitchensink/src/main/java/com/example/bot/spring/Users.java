package com.example.bot.spring;

public class Users {
	final private String UID;
	protected String name;
	protected char gender = 'M'; // this can be "M" or "F"
	protected double weight = 0;
	protected double height = 0;
	protected int age = 10;

	public Users(String UID) {
		this.UID = UID;
	}
	public Users(String UID, String name) {
		this.UID = UID;
		this.name = name;	
	}
	public Users(String UID, String name, char gender, double weight, double height, int age) {
		this.UID = UID;
		this.name = name;	
		this.gender = gender;
		this.weight = weight;
		this.height = height;
		this.age = age;
	}
	public Users(Users u) {
		this.UID = u.UID;
		this.name = u.name;	
		this.gender = u.gender;
		this.weight = u.weight;
		this.height = u.height;	
		this.age = u.age;
	}

	public boolean setName(String n) {this.name = n; return true;}	
	public boolean setGender(char g) {this.gender = g;return true;}
	public boolean setWeight(double w) {this.weight = w;return true;}
	public boolean setHeight(double h) {this.height = h;return true;}
	public boolean setAge(int a) {this.age = a;return true;}
	//assume inputs are always valid
	public String getID() {return UID;}
	public String getName() {return name;}	
	public char getGender() {return gender;}
	public double getWeight() {return weight;}
	public double getHeight() {return height;}
	public int getAge() {return age;}

	
	@Override
	public String toString() { 
		return "Name: " + name + "\n"
			 + "Gender: " + gender +"\n"
			 + "Height: " + height +"\n"
			 + "Weight: " + weight +"\n"
			 + "Age: " + age +"\n";
	}

}
