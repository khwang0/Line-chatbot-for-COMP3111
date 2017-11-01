package com.example.bot.spring;

public class DetailedUser extends Users{
	private final static int numMeals = 6;
	private int amountOfExercise = 0; // in hour/day average a week
    private double bodyFat = 0; //in %
    private int caloriesConsump = 0; //per day
    private double carbsConsump = 0; // in g 
    private double proteinConsump = 0; //in g
    private double vegfruitConsump = 0; // in servings
    private boolean[] eatingHabits = {false,false,false,false,false,false};//eatBF,eatLunch,eatAFT,eatDinner,eatMS, eatMore 
    private String otherInfo  = "Default";
   // private int assessmentScore = 0;
	
    public DetailedUser(Users u) {
    	super(u);
    }
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
	//public int getAssessmentScore() {return assessmentScore;}
	public void setOtherInfo(String s) {otherInfo = s;}
	public int getExercise() {return amountOfExercise;}
	public double getBodyFat() {return bodyFat;}
	public int getCalories() {return caloriesConsump;}
	public double getCarbs() {return carbsConsump;}
	public double getProtein() {return proteinConsump;}
	public double getVegfruit() {return vegfruitConsump;}
	public boolean[] getEatingHabits() {return eatingHabits;}
	public String getOtherInfo() {return otherInfo;}
	//public void setAssessmentScore(int s) {assessmentScore = s;}
	
	@Override
	public String toString() { // this converts user to Json format
		return super.toString()
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
			   +"Other information: "+ otherInfo ;
	}
	
}
