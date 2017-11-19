package com.example.bot.spring;

import java.sql.DatabaseMetaData;

public class InputChecker {

	public boolean ValidName(String text) {
		return (text.length()<32);
	}
	public boolean ValidGender(String text) {
		return (Character.toUpperCase(text.charAt(0))=='M' ||Character.toUpperCase(text.charAt(0))=='F');
	}
	public boolean ValidHeight(String text) throws NumberFormatException {
		return( Double.parseDouble(text) < 260 && Double.parseDouble(text)> 50 );
	}
	public boolean ValidWeight(String text) throws NumberFormatException {
		return( Double.parseDouble(text) < 200 && Double.parseDouble(text)> 20 );
	}
	public boolean ValidAge(String text) throws NumberFormatException {
		return( Integer.parseInt(text) < 150 && Integer.parseInt(text)> 7 );
	}
	public boolean ValidExercise(String text) throws NumberFormatException {
		return(Integer.parseInt(text) < 16 && Integer.parseInt(text)>= 0);
	}
	public boolean ValidBodyfat(String text) throws NumberFormatException {
		return( Double.parseDouble(text) < 80 && Double.parseDouble(text)> 1);
	}
	public boolean ValidCalories(String text) throws NumberFormatException {
		return( Integer.parseInt(text) < 15000 && Integer.parseInt(text)> 0 );
	}
	public boolean ValidAmount(String text) throws NumberFormatException {
		return( Integer.parseInt(text) < 15000 && Integer.parseInt(text)> 0 );
	}
	public boolean ValidDate(String text) throws NumberFormatException{
		return( Integer.parseInt(text) < 30000000 && Integer.parseInt(text)>20170000 );
	};
	public boolean ValidCarbs(String text) throws NumberFormatException {
		return( Double.parseDouble(text) < 3000 && Double.parseDouble(text)> 0 ) ;
	}
	public boolean ValidProtein(String text) throws NumberFormatException {
		return( Double.parseDouble(text) < 1000 && Double.parseDouble(text)> 0 );
	}
	public boolean ValidVegfruit(String text) throws NumberFormatException {
		return( Double.parseDouble(text) < 50 && Double.parseDouble(text)> 0 );
	}
	public boolean ValidOtherinfo(String text){
		return (text.length()<=1000);
	}

	private void ModeSwitcher(Users currentUser, SQLDatabaseEngine database, String mode) {
		switch(mode) {
		case "set":break;
		case "update" :database.updateUser(currentUser);break;
		default:System.out.println("Mode error. Set failed.");
		}
	}

	public boolean AgeEditting(String text, Users currentUser, SQLDatabaseEngine database, String mode) {
		try {
			if( ValidAge(text)) {
    			currentUser.setAge(Integer.parseInt(text));
    			ModeSwitcher(currentUser, database, mode);
    			return true;
    		}
			else
				return false;
		}catch(NumberFormatException ne){return false;}
	}


	public boolean GenderEditting(String text, Users currentUser, SQLDatabaseEngine database, String mode) {
		try {
		if(ValidGender(text)) {
    		currentUser.setGender(text.charAt(0));
    		ModeSwitcher(currentUser, database, mode);
    		return true;
    	}
		else
			return false;
		}catch(NumberFormatException ne){return false;}
	}

	public boolean NameEditting(String text, Users currentUser, SQLDatabaseEngine database, String mode) {
		try {
		if(ValidName(text)) {
    		currentUser.setName(text);
    		ModeSwitcher(currentUser, database, mode);
    		return true;
    	}
		else
			return false;
		}catch(NumberFormatException ne){return false;}
	}
	public boolean WeightEditting(String text, Users currentUser, SQLDatabaseEngine database, String mode) {
		try {
			if( ValidWeight(text)) {
				currentUser.setWeight(Double.parseDouble(text));
				ModeSwitcher(currentUser, database, mode);
				return true;
			}
			else
				return false;
		}catch(NumberFormatException ne){return false;}
	}
	public boolean HeightEditting(String text, Users currentUser, SQLDatabaseEngine database, String mode) {
		try {
			if( ValidHeight(text)) {
				currentUser.setHeight(Double.parseDouble(text));
				ModeSwitcher(currentUser, database, mode);
				return true;
				}
			else {
				return false;
			}
		}catch(NumberFormatException ne){return false;}
	}
	public boolean BodyfatEditting(String text, Users currentUser, SQLDatabaseEngine database, String mode) {
		try {
			if( ValidBodyfat(text)) {
				(currentUser).setBodyFat(Double.parseDouble(text));
				ModeSwitcher(currentUser, database, mode);
				return true;
				}
			else {
				return false;
			}
		}catch(NumberFormatException ne){return false;}
	}
	public boolean ExerciseEditting(String text, Users currentUser, SQLDatabaseEngine database, String mode) {
		try {
			if( ValidExercise(text)) {
				(currentUser).setExercise(Integer.parseInt(text));
				ModeSwitcher(currentUser, database, mode);
				return true;
				}
			else {
				return false;
			}
		}catch(NumberFormatException ne){return false;}
	}
	public boolean CaloriesEditting(String text, Users currentUser, SQLDatabaseEngine database, String mode) {
		try {
			if( ValidCalories(text)) {
				(currentUser).setCalories(Integer.parseInt(text));
				ModeSwitcher(currentUser, database, mode);
				return true;
				}
			else {
				return false;
			}
		}catch(NumberFormatException ne){return false;}
	}
	public boolean CarbsEditting(String text, Users currentUser, SQLDatabaseEngine database, String mode) {
		try {
			if( ValidCarbs(text)) {
				(currentUser).setCarbs(Double.parseDouble(text));
				ModeSwitcher(currentUser, database, mode);
				return true;
				}
			else {
				return false;
			}
		}catch(NumberFormatException ne){return false;}
	}
	public boolean ProteinEditting(String text, Users currentUser, SQLDatabaseEngine database, String mode) {
		try {
			if( ValidProtein(text)) {
				(currentUser).setProtein(Double.parseDouble(text));
				ModeSwitcher(currentUser, database, mode);
				return true;
				}
			else {
				return false;
			}
		}catch(NumberFormatException ne){return false;}
	}
	public boolean VegfruitEditting(String text, Users currentUser, SQLDatabaseEngine database, String mode) {
		try {
			if( ValidVegfruit(text)) {
				(currentUser).setVegfruit(Double.parseDouble(text));
				ModeSwitcher(currentUser, database, mode);
				return true;
				}
			else {
				return false;
			}
		}catch(NumberFormatException ne){return false;}
	}
	public boolean EatingEditting(String text, Users currentUser, SQLDatabaseEngine database, String mode) {
		return false;
	}
	public boolean OtherinfoEditting(String text, Users currentUser, SQLDatabaseEngine database, String mode) {
		try {
		if(ValidOtherinfo(text)) {
    		(currentUser).setOtherInfo(text);
    		return true;
    	}
		else
			return false;
		}catch(NumberFormatException ne){return false;}
	}
	/* added by ZK*/
	public boolean foodAdd(String text, FoodInfo food, SQLDatabaseEngine database) {
		try {
		if(ValidOtherinfo(text)) {
    		food.setFoodName(text);
    		return true;
    	}
		else
			return false;
		}catch(NumberFormatException ne){return false;}
	}


	public boolean energyAdd(String text, FoodInfo food, SQLDatabaseEngine database) {
		try {
		if(ValidCarbs(text)) {
    		food.setEnergy(Double.valueOf(text));
    		return true;
    	}
		else
			return false;
		}catch(NumberFormatException ne){return false;}
	}

	public boolean proteinAdd(String text, FoodInfo food, SQLDatabaseEngine database) {
		try {
			if(ValidCarbs(text)) {
	    		food.setProtein(Double.valueOf(text));
	    		return true;
	    	}
			else
				return false;
		}catch(NumberFormatException ne){return false;}
	}

	public boolean fiberAdd(String text, FoodInfo food, SQLDatabaseEngine database) {
		try {
			if(ValidCarbs(text)) {
	    		food.setFiber(Double.valueOf(text));
	    		return true;
	    	}
			else
				return false;
		}catch(NumberFormatException ne){return false;}
	}

	public boolean priceAdd(String text, FoodInfo food, SQLDatabaseEngine database) {
		try {
			if(ValidCarbs(text)) {
	    		food.setPrice(Integer.valueOf(text));
	    		database.pushFoodInfo(food);
	    		return true;
	    	}
			else
				return false;
		}catch(NumberFormatException ne){return false;}
	}


	public boolean foodAdd(String text, FoodInput foodInput, SQLDatabaseEngine database) {
		try {
		if(ValidOtherinfo(text)) {
    		foodInput.setFoodName(text);
    		return true;
    	}
		else
			return false;
		}catch(NumberFormatException ne){return false;}
	}


	public boolean amountAdd(String text,FoodInput foodInput, SQLDatabaseEngine database) {
		try {
		if(ValidAmount(text)) {
			foodInput.setAmount(Integer.parseInt(text));
    		return true;
    	}
		else
			return false;
		}catch(NumberFormatException ne){return false;}
	}

	public boolean priceAdd(String text,FoodInput foodInput, SQLDatabaseEngine database) {
		try {
		if(ValidAmount(text)) {
			foodInput.setPrice(Double.valueOf(text));
			database.pushDietRecord(foodInput);
    		return true;
    	}
		else
			return false;
		}catch(NumberFormatException ne){return false;}
	}


	public boolean dateCheck(String text) {
		try {
			if(ValidDate(text)) {
				return true;
			}
			else return false;
		}catch(NumberFormatException ne){return false;}
	}

	public String dietsearch(String text, SQLDatabaseEngine database, String id ) {
		return database.reportDiet(text,id);
	}
	public void consumptionUpdate(HealthSearch healthSearcher,SQLDatabaseEngine database,int amount,String id,String time,double price) {
		database.updateconsumption(healthSearcher,amount,id,time,price);
	}
}
