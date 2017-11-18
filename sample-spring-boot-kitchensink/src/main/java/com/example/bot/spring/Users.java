package com.example.bot.spring;

public class Users {
	final private String UID;
	protected String name;
	protected char gender = 'M'; // this can be "M" or "F"
	protected double weight = 0;
	protected double height = 0;
	protected int age = 10;
	protected int subStage = 0;
	protected String stage  = "Init";

	private int amountOfExercise = 0; // in hour/day average a week
    private double bodyFat = 0; //in %
    private int caloriesConsump = 0; //per day
    private double carbsConsump = 0; // in g
    private double proteinConsump = 0; //in g
    private double vegfruitConsump = 0; // in servings
    private boolean[] eatingHabits = {false,false,false,false,false,false};//eatBF,eatLunch,eatAFT,eatDinner,eatMS, eatMore
    private String otherInfo  = "Default";

   // modified user's assessment scores
    private int assessmentScore = -1;

	public Users(String UID) {
		this.UID = UID;
	}
	public Users(String UID, String name) {
		this.UID = UID;
		this.name = name;
	}
	public Users(String UID, String name, char gender, double weight, double height, int age, int subStage, String stage) {
		this.UID = UID;
		this.name = name;
		this.gender = gender;
		this.weight = weight;
		this.height = height;
		this.age = age;
		this.subStage = subStage;
		this.stage = stage;
	}
	public Users(Users u) {
		this.UID = u.UID;
		this.name = u.name;
		this.gender = u.gender;
		this.weight = u.weight;
		this.height = u.height;
		this.age = u.age;
		this.subStage = u.subStage;
		this.stage = u.stage;
	}

	public boolean setName(String n) {this.name = n; return true;}
	public boolean setGender(char g) {this.gender = g;return true;}
	public boolean setWeight(double w) {this.weight = w;return true;}
	public boolean setHeight(double h) {this.height = h;return true;}
	public boolean setAge(int a) {this.age = a;return true;}
	public boolean setSubStage(int a){this.subStage = a; return true;}
	public boolean setStage(String a){this.stage = a; return true;}

	public void setExercise(int t) {amountOfExercise = t;}
	public void setBodyFat(double bf) {bodyFat = bf;}
	public void setCalories(int cc) {caloriesConsump = cc;}
	public void setCarbs(double c) {carbsConsump = c;}
	public void setProtein(double p) {proteinConsump = p;}
	public void setVegfruit(double v) {vegfruitConsump = v;}
	public void setEatingHabits(boolean h, int i) {eatingHabits[i] = h;}
	public void setEatingHabits(boolean[] h)  {
		for(int i = 0; i < h.length ; i++)
			eatingHabits[i] = h[i];
	}
	public void setEatingHabits(Boolean[] h)  {
		for(int i = 0 ; i <h.length ; i++) {
			eatingHabits[i] = h[i].booleanValue();
		}
	}
	public void setOtherInfo(String s) {otherInfo = s;}
	public void setAssessmentScore(int s){assessmentScore = s;}

	//assume inputs are always valid
	public String getID() {return UID;}
	public String getName() {return name;}
	public char getGender() {return gender;}
	public double getWeight() {return weight;}
	public double getHeight() {return height;}
	public int getAge() {return age;}
	public int getSubStage() {return subStage;}
	public String getStage() {return stage;}

	public int getExercise() {return amountOfExercise;}
	public double getBodyFat() {return bodyFat;}
	public int getCalories() {return caloriesConsump;}
	public double getCarbs() {return carbsConsump;}
	public double getProtein() {return proteinConsump;}
	public double getVegfruit() {return vegfruitConsump;}
	public boolean[] getEatingHabits() {return eatingHabits;}
	public String getOtherInfo() {return otherInfo;}

	// modified user's assessment scores
	public int getAssessmentScore(){return assessmentScore;}


	@Override
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
			 +"Excercise(hours/day): "+ Integer.toString(amountOfExercise) + "\n"
			 +"BodyFat(%): "+ Double.toString(bodyFat) + "\n"
			 +"Calories(kcal/day): "+ Integer.toString(caloriesConsump) + "\n"
			 +"Carbohydrates(g/day): " + Double.toString(carbsConsump) + "\n"
			 +"Protein(g/day): "+ Double.toString(proteinConsump) + "\n"
			 +"Vegtables and Fruit(servings/day): "+ Double.toString(vegfruitConsump) + "\n"
			 +"Eat Breakfast: "+ Boolean.toString(eatingHabits[0]) +"\n"
			 +"Eat lunch: "+ Boolean.toString(eatingHabits[1]) +"\n"
			 +"Eat afternoon tea: "+ Boolean.toString(eatingHabits[2]) +"\n"
			 +"Eat dinner: "+ Boolean.toString(eatingHabits[3]) +"\n"
			 +"Eat midnight snacks: "+ Boolean.toString(eatingHabits[4]) +"\n"
			 +"More meals: "+ Boolean.toString(eatingHabits[5]) +"\n"
			 +temp
			 +"Other information: "+ otherInfo ;
	}

}
