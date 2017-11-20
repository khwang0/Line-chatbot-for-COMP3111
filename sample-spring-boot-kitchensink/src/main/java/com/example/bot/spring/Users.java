package com.example.bot.spring;

import java.util.*;
/**
* Users will store the basic variable and get/set/display function that a chatbot user need to has.
*
* @author  G8
* @version 1.0Z
* @since   2017/11/19
*/
public class Users {
	/**
	* the User Id of the Users
	*/
	final private String UID;
	/**
	* the name of the Users
	*/
	private String name;
	/**
	* the gender of the Users
	*/
	private char gender = 'M'; // this can be "M" or "F"
	/**
	* the weight of the Users
	*/
	private double weight = 0;
	/**
	* the height of the Users
	*/
	private double height = 0;
	/**
	* the age of the Users
	*/
	private int age = 0;
	/**
	* the subStage of the Users, will be used to handle user's message
	*/
	private int subStage = 0;
	/**
	* the stage of the Users, will be used to handle user's message
	*/
	private String stage  = "Init";
	/**
	* the Exercise amount of the Users (in hour/day)
	*/
	private int amountOfExercise = 0; // in hour/day average a week
	/**
	* the bodyfat percentage of the Users
	*/
    private double bodyFat = 0; //in %
    /**
	* the Calories consumption of the Users
	*/
    private int caloriesConsump = 0; //per day
    /**
	* the Carbohydrates consumption of the Users
	*/
    private double carbsConsump = 0; // in g
    /**
	* the Protein consumption of the Users
	*/
    private double proteinConsump = 0; //in g
    /**
	* the vegetable and fruit consumption of the Users
	*/
    private double vegfruitConsump = 0; // in servings
    /**
	* the eatinghabits of the Users, to specify that if they eatBF,eatLunch,eatAFT,eatDinner,eatMS, eatMore
	*/
    private boolean[] eatingHabits = {false,false,false,false,false,false};//eatBF,eatLunch,eatAFT,eatDinner,eatMS, eatMore
    /**
	* the other information that Users want us know
	*/
    private String otherInfo  = "Default";
		private double budget = 0;
		//private String registerTime;
   // modified user's assessment scores
    /**
	* the assessment score of the Users, default is -1 if they do not take the self-assessment
	*/
    private int assessmentScore = -1;

    /**
     * Constructor with User Id specified
     * @param UID This is the First parameter to Users constructor
     */
	public Users(String UID) {
		this.UID = UID;
	//	Date now = new Date();
	//	registerTime = now.toString();
	}

	/**
     * Constructor with User Id and User name Specified
     * @param UID This is the first parameter to Users constructor
     * @param name This is the second parameter to Users constructor
     */
	public Users(String UID, String name) {
		this.UID = UID;
		this.name = name;
	//	Date now = new Date();
	//	registerTime = now.toString();
	}

	/**
     * Constructor with User Id, name, gender, weight, height age, subStage, stage Specified
     * @param UID This is the first parameter to Users constructor
     * @param name This is the second parameter to Users constructor
     * @param gender This is the third parameter to Users constructor
     * @param weight This is the fourth parameter to Users constructor
     * @param height This is the fifth parameter to Users constructor
     * @param age This is the sixth parameter to Users constructor
     * @param subStage This is the seventh parameter to Users constructor
	 * @param stage This is the eighth parameter to Users constructor
     */
	public Users(String UID, String name, char gender, double weight, double height, int age, int subStage, String stage) {
		this.UID = UID;
		this.name = name;
		this.gender = gender;
		this.weight = weight;
		this.height = height;
		this.age = age;
		this.subStage = subStage;
		this.stage = stage;
	//	Date now = new Date();
		//registerTime = now.toString();
	}
	/**
     * Copy constructor
     * @param u This is the first parameter to Users copy constructor
     */
	public Users(Users u) {
		this.UID = u.UID;
		this.name = u.name;
		this.gender = u.gender;
		this.weight = u.weight;
		this.height = u.height;
		this.age = u.age;
		this.subStage = u.subStage;
		this.stage = u.stage;
	//	Date now = new Date();
	//	registerTime = now.toString();
	}

	/**
     * This method is to change the name of the Users
     * @param n This is the name that will be change to
     */
	public void setName(String n) {this.name = n; }

	/**
     * This method is to change the gender of the Users
     * @param g This is the gender that will be change to
     */
	public void setGender(char g) {this.gender = g;}

	/**
     * This method is to change the weight of the Users
     * @param w This is the weight that will be change to
     */
	public void setWeight(double w) {this.weight = w;}

	/**
     * This method is to change the height of the Users
     * @param h This is the height that will be change to
     */
	public void setHeight(double h) {this.height = h;}

	/**
     * This method is to change the age of the Users
     * @param a This is the age that will be change to
     */
	public void setAge(int a) {this.age = a;}

	/**
     * This method is to change the subStage of the Users
     * @param a This is the subStage that will be change to
     */
	public void setSubStage(int a){this.subStage = a; }

	/**
     * This method is to change the stage of the Users
     * @param a This is the stage that will be change to
     */
	public void setStage(String a){this.stage = a; }

	/**
     * This method is to change the Exercise times of the Users
     * @param t This is the Exercise times that will be change to
     */
	public void setExercise(int t) {amountOfExercise = t;}

	/**
     * This method is to change the bodyfat of the Users
     * @param bf This is the bodyfat that will be change to
     */
	public void setBodyFat(double bf) {bodyFat = bf;}

	/**
     * This method is to change the Calories consumption of the Users
     * @param cc This is the Calories consumption that will be change to
     */
	public void setCalories(int cc) {caloriesConsump = cc;}

	/**
     * This method is to change the Carbohydrate consumptions of the Users
     * @param c This is the Carbohydrate consumptions that will be change to
     */
	public void setCarbs(double c) {carbsConsump = c;}

	/**
     * This method is to change the Protein consumption of the Users
     * @param p This is the Protein consumption that will be change to
     */
	public void setProtein(double p) {proteinConsump = p;}

	/**
     * This method is to change the vegetable and fruit consumption of the Users
     * @param v This is the vegetable and fruit consumption that will be change to
     */
	public void setVegfruit(double v) {vegfruitConsump = v;}

	/**
     * This method is to change the eating habits of the Users
     * @param h This is one eating habit(boolean) that will be changed to
	 * @param i This indicate that which boolean in the array will be changed
     */
	public void setEatingHabits(boolean h, int i) {eatingHabits[i] = h;}

	/**
     * This method is to change the eating habits of the Users
     * @param h This is the eatinghabits(array of boolean)that will be change to
     */
	public void setEatingHabits(boolean[] h)  {
		for(int i = 0; i < h.length ; i++)
			eatingHabits[i] = h[i];
	}

	/**
     * This method is to change the eating habits of the Users (array of booleans to store whether he/she will have breakfast,lunch,afternoon tea, dinner, midnight snack)
     * @param h This is the eatinghabits(array of Boolean)that will be change to
     */
	public void setEatingHabits(Boolean[] h)  {
		for(int i = 0 ; i <h.length ; i++) {
			eatingHabits[i] = h[i].booleanValue();
		}
	}

	/**
     * This method is to change the other infomations of the Users
     * @param s This is the other infomations that will be change to
     */
	public void setOtherInfo(String s) {otherInfo = s;}
	public void setBudget(double b) {budget = b;}
	/**
     * This method is to change the assessment score of the Users
     * @param s This is the assessment score that will be change to
     */
	public void setAssessmentScore(int s){assessmentScore = s;}

	//assume inputs are always valid
	/**
     * This method will return the Use ID of the Users
	 * @return String this is the Use ID of the Users
     */
	public String getID() {return UID;}

	/**
     * This method will return the name of the Users
	 * @return String this is the name of the Users
     */
	public String getName() {return name;}

	/**
     * This method will return the gender of the Users
	 * @return char this is the gender of the Users
     */
	public char getGender() {return gender;}

	/**
     * This method will return the weight of the Users
	 * @return double this is the weight of the Users
     */
	public double getWeight() {return weight;}

	/**
     * This method will return the height of the Users
	 * @return double this is the height of the Users
     */
	public double getHeight() {return height;}

	/**
     * This method will return the age of the Users
	 * @return int this is the age of the Users
     */
	public int getAge() {return age;}

	/**
     * This method will return the substage of the Users
	 * @return int this is the substage of the Users
     */
	public int getSubStage() {return subStage;}

	/**
     * This method will return the stage of the Users
	 * @return String this is the stage of the Users
     */
	public String getStage() {return stage;}

	/**
     * This method will return the amount of exercise of the Users
	 * @return int this is the amount of exercise of the Users
     */
	public int getExercise() {return amountOfExercise;}

	/**
     * This method will return the bodyfat of the Users
	 * @return double this is the bodyfat of the Users
     */
	public double getBodyFat() {return bodyFat;}

	/**
     * This method will return the Calories consumption of the Users
	 * @return int this is the Calories consumption of the Users
     */
	public int getCalories() {return caloriesConsump;}

	/**
     * This method will return the Carbohydrates consumption of the Users
	 * @return double this is the Carbohydrates consumption of the Users
     */
	public double getCarbs() {return carbsConsump;}

	/**
     * This method will return the Protein consuption of the Users
	 * @return double this is the Protein consuption of the Users
     */
	public double getProtein() {return proteinConsump;}

	/**
     * This method will return the vegetable and fruit consumption of the Users
	 * @return double this is the vegetable and fruit consumption of the Users
     */
	public double getVegfruit() {return vegfruitConsump;}

	/**
     * This method will return the eatinghabits of the Users
	 * @return boolean[] this is the eatinghabits of the Users
     */
	public boolean[] getEatingHabits() {return eatingHabits;}

	/**
     * This method will return the other information of the Users
	 * @return String this is the other information of the Users
     */
	public String getOtherInfo() {return otherInfo;}
	/**
     * This method will return the Budget of the Users
	 * @return double this is the Budget of the Users
     */
	public double getBudget() {return budget;}
	//public String getRegisterTime(){return registerTime;}
	// modified user's assessment scores
	/**
     * This method will return the assessmentScore of the Users
	 * @return int this is the assessmentScore of the Users
     */
	public int getAssessmentScore(){return assessmentScore;}


	/**
     * This method will return formatted display of all needed information of the Users
	 * @return String this is String that contains all the needed information of the Users
     */
	public String toString() {
		String temp = null;
  		if(assessmentScore == -1)
   			temp = "*AssessmentScore will be updated once you complete assessments in Planner)" + "\n";
  		else if (assessmentScore >=0 && assessmentScore <=100)
   			temp = "AssessmentScore: " + Integer.toString(assessmentScore) + "\n";
  		else
   			temp = "AssessmentScore: error(invalid score)\n";
		return "Name: " + name + "\n"
			 + "Gender: " + gender +"\n"
			 + "Height: " + height +"\n"
			 + "Weight: " + weight +"\n"
			 + "Age: " + age +"\n"
			 //+"Excercise(hours/day): "+ Integer.toString(amountOfExercise) + "\n"
			 +"BodyFat(%): "+ Double.toString(bodyFat) + "\n"
			 // +"Calories(kcal/day): "+ Integer.toString(caloriesConsump) + "\n"
			 // +"Carbohydrates(g/day): " + Double.toString(carbsConsump) + "\n"
			 // +"Protein(g/day): "+ Double.toString(proteinConsump) + "\n"
			 // +"Vegtables and Fruit(servings/day): "+ Double.toString(vegfruitConsump) + "\n"
			 // +"Eat Breakfast: "+ Boolean.toString(eatingHabits[0]) +"\n"
			 // +"Eat lunch: "+ Boolean.toString(eatingHabits[1]) +"\n"
			 // +"Eat afternoon tea: "+ Boolean.toString(eatingHabits[2]) +"\n"
			 // +"Eat dinner: "+ Boolean.toString(eatingHabits[3]) +"\n"
			 // +"Eat midnight snacks: "+ Boolean.toString(eatingHabits[4]) +"\n"
			 // +"More meals: "+ Boolean.toString(eatingHabits[5]) +"\n"
			 +temp;
			 //+"Other information: "+ otherInfo ;
	}

}
