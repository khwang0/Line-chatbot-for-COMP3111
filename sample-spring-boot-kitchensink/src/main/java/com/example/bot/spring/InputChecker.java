package com.example.bot.spring;

import java.sql.DatabaseMetaData;
/**
* InputChecker will perform the function of checking whether users' inputs are correct
* and updating the database corresponding to different input
* @version 1.0
* @since   2017/11/19
*/
public class InputChecker {
	/**
	* This method will check whether the name is in correct format
	* @param text input name
	* @return boolean return whether the name is in correct format
	* @see Users
	*/
	public boolean ValidName(String text) {
		return (text.length()<32);
	}

	/**
	* This method will check whether the gender is in correct format
	* @param text input gender
	* @return boolean return whether the gender is in correct format
	* @see Users
	*/
	public boolean ValidGender(String text) {
		return (Character.toUpperCase(text.charAt(0))=='M' ||Character.toUpperCase(text.charAt(0))=='F');
	}

	/**
	* This method will check whether the height is in correct format
	* @param text input height
	* @return boolean return whether the height is in correct format
	* @see Users
	* @throws NumberFormatException if a numberformat exception occured
	*/
	public boolean ValidHeight(String text) throws NumberFormatException {
		return( Double.parseDouble(text) < 260 && Double.parseDouble(text)> 50 );
	}

	/**
	* This method will check whether the weight is in correct format
	* @param text input weight
	* @return boolean return whether the weight is in correct format
	* @see Users
	* @throws NumberFormatException if a numberformat exception occured
	*/
	public boolean ValidWeight(String text) throws NumberFormatException {
		return( Double.parseDouble(text) < 200 && Double.parseDouble(text)> 20 );
	}
	/**
	* This method will check whether the age is in correct format
	* @param text age name
	* @return boolean return whether the age is in correct format
	* @see Users
	* @throws NumberFormatException if a numberformat exception occured
	*/
	public boolean ValidAge(String text) throws NumberFormatException {
		return( Integer.parseInt(text) < 150 && Integer.parseInt(text)> 7 );
	}
	/**
	* This method will check whether the amount of exercise is in correct format
	* @param text input amount of exercise
	* @return boolean return whether the amount of exercise is in correct format
	* @see Users
	* @throws NumberFormatException if a numberformat exception occured
	*/
	public boolean ValidExercise(String text) throws NumberFormatException {
		return(Integer.parseInt(text) < 16 && Integer.parseInt(text)>= 0);
	}

	/**
	* This method will check whether the bodyfat is in correct format
	* @param text input bodyfat
	* @return boolean return whether the bodyfat is in correct format
	* @see Users
	* @throws NumberFormatException if a numberformat exception occured
	*/
	public boolean ValidBodyfat(String text) throws NumberFormatException {
		return( Double.parseDouble(text) < 80 && Double.parseDouble(text)> 1);
	}
	/**
	* This method will check whether the calories consumption is in correct format
	* @param text input calories consumption
	* @return boolean return whether the calories consumption is in correct format
	* @see Users
	* @throws NumberFormatException if a numberformat exception occured
	*/
	public boolean ValidCalories(String text) throws NumberFormatException {
		return( Integer.parseInt(text) < 15000 && Integer.parseInt(text)> 0 );
	}
	/**
	* This method will check whether the amount of FoodInput is in correct format
	* @param text input amount of FoodInput
	* @return boolean return whether the amount of FoodInput is in correct format
	* @see FoodInput
	* @throws NumberFormatException if a numberformat exception occured
	*/
	public boolean ValidAmount(String text) throws NumberFormatException {
		return( Integer.parseInt(text) < 15000 && Integer.parseInt(text)> 0 );
	}
	/**
	* This method will check whether the time of FoodInput is in correct format
	* @param text input time of FoodInput
	* @return boolean return whether the time of FoodInput is in correct format
	* @see FoodInput
	* @throws NumberFormatException if a numberformat exception occured
	*/
	public boolean ValidDate(String text) throws NumberFormatException{
		return( Integer.parseInt(text) < 30000000 && Integer.parseInt(text)>20170000 );
	};
	/**
	* This method will check whether the carbohydrate of Food Input is in correct format
	* @param text input carbohydrate of Food Input
	* @return boolean return whether the carbohydrate of Food Input is in correct format
	* @see FoodInput
	* @see Users
	* @throws NumberFormatException if a numberformat exception occured
	*/
	public boolean ValidCarbs(String text) throws NumberFormatException {
		return( Double.parseDouble(text) < 3000 && Double.parseDouble(text)> 0 ) ;
	}
	/**
	* This method will check whether the protein is in correct format
	* @param text input protein
	* @return boolean return whether the protein is in correct format
	* @see Users
	* @throws NumberFormatException if a numberformat exception occured
	*/
	public boolean ValidProtein(String text) throws NumberFormatException {
		return( Double.parseDouble(text) < 1000 && Double.parseDouble(text)> 0 );
	}
	/**
	* This method will check whether the fruit and vegetable is in correct format
	* @param text input fruit and vegetable
	* @return boolean return whether the fruit and vegetable is in correct format
	* @see Users
	* @throws NumberFormatException if a numberformat exception occured
	*/
	public boolean ValidVegfruit(String text) throws NumberFormatException {
		return( Double.parseDouble(text) < 50 && Double.parseDouble(text)> 0 );
	}
	/**
	* This method will check whether the otherinformation is in correct format
	* @param text input otherinformation
	* @return boolean return whether the otherinformation is in correct format
	* @see Users
	* @throws NumberFormatException if a numberformat exception occured
	*/
	public boolean ValidOtherinfo(String text){
		return (text.length()<=1000);
	}
	/**
	* This method will decide whether to update in database or not
	* @param currentUser current Users related to the call of this function
	* @param database will be used to access database
	* @param mode will be used to decide to update or not
	*/
	private void ModeSwitcher(Users currentUser, SQLDatabaseEngine database, String mode) {
		switch(mode) {
		case "set":break;
		case "update" :database.updateUser(currentUser);break;
		default:System.out.println("Mode error. Set failed.");
		}
	}
	/**
	* This method will check the input text(catch exception if it is not in correct format)
	* and set the age for currentUser
	* @param text users' input text
	* @param currentUser current Users related to the call of this function
	* @param database will be used to access database
	* @param mode will be used to decide to update or not
	* @see Users
	* @return boolean return whether the input text is in correct format
	*/
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

	/**
	* This method will check the input text(catch exception if it is not in correct format)
	* and set the gender for currentUser
	* @param text users' input text
	* @param currentUser current Users related to the call of this function
	* @param database will be used to access database
	* @param mode will be used to decide to update or not
	* @see Users
	* @return boolean return whether the input text is in correct format
	*/
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

	/**
	* This method will check the input text(catch exception if it is not in correct format)
	* and set the name for currentUser
	* @param text users' input text
	* @param currentUser current Users related to the call of this function
	* @param database will be used to access database
	* @param mode will be used to decide to update or not
	* @see Users
	* @return boolean return whether the input text is in correct format
	*/
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

	/**
	* This method will check the input text(catch exception if it is not in correct format)
	* and set the weight for currentUser
	* @param text users' input text
	* @param currentUser current Users related to the call of this function
	* @param database will be used to access database
	* @param mode will be used to decide to update or not
	* @see Users
	* @return boolean return whether the input text is in correct format
	*/
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
	/**
	* This method will check the input text(catch exception if it is not in correct format)
	* and set the height for currentUser
	* @param text users' input text
	* @param currentUser current Users related to the call of this function
	* @param database will be used to access database
	* @param mode will be used to decide to update or not
	* @see Users
	* @return boolean return whether the input text is in correct format
	*/
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
	/**
	* This method will check the input text(catch exception if it is not in correct format)
	* and set the bodyfat for currentUser
	* @param text users' input text
	* @param currentUser current Users related to the call of this function
	* @param database will be used to access database
	* @param mode will be used to decide to update or not
	* @see Users
	* @return boolean return whether the input text is in correct format
	*/
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
	/**
	* This method will check the input text(catch exception if it is not in correct format)
	* and set the amount of exercise for currentUser
	* @param text users' input text
	* @param currentUser current Users related to the call of this function
	* @param database will be used to access database
	* @param mode will be used to decide to update or not
	* @see Users
	* @return boolean return whether the input text is in correct format
	*/
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
	/**
	* This method will check the input text(catch exception if it is not in correct format)
	* and set the calories consumption for currentUser
	* @param text users' input text
	* @param currentUser current Users related to the call of this function
	* @param database will be used to access database
	* @param mode will be used to decide to update or not
	* @see Users
	* @return boolean return whether the input text is in correct format
	*/
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
	/**
	* This method will check the input text(catch exception if it is not in correct format)
	* and set the carbohydrate consumption for currentUser
	* @param text users' input text
	* @param currentUser current Users related to the call of this function
	* @param database will be used to access database
	* @param mode will be used to decide to update or not
	* @see Users
	* @return boolean return whether the input text is in correct format
	*/
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
	/**
	* This method will check the input text(catch exception if it is not in correct format)
	* and set the protein consumption for currentUser
	* @param text users' input text
	* @param currentUser current Users related to the call of this function
	* @param database will be used to access database
	* @param mode will be used to decide to update or not
	* @see Users
	* @return boolean return whether the input text is in correct format
	*/
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
	/**
	* This method will check the input text(catch exception if it is not in correct format)
	* and set the vegetable and fruit for currentUser
	* @param text users' input text
	* @param currentUser current Users related to the call of this function
	* @param database will be used to access database
	* @param mode will be used to decide to update or not
	* @see Users
	* @return boolean return whether the input text is in correct format
	*/
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
	/**
	* This method will check the input text(catch exception if it is not in correct format)
	* and set the eatting habits for currentUser
	* @param text users' input text
	* @param currentUser current Users related to the call of this function
	* @param database will be used to access database
	* @param mode will be used to decide to update or not
	* @see Users
	* @return boolean return whether the input text is in correct format
	*/
	public boolean EatingEditting(String text, Users currentUser, SQLDatabaseEngine database, String mode) {
		return false;
	}
	/**
	* This method will check the input text(catch exception if it is not in correct format)
	* and set the otherinformation for currentUser
	* @param text users' input text
	* @param currentUser current Users related to the call of this function
	* @param database will be used to access database
	* @param mode will be used to decide to update or not
	* @see Users
	* @return boolean return whether the input text is in correct format
	*/
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

	/**
	* This method will check the input text(catch exception if it is not in correct format)
	* and set the foodName for FoodInfo
	* @param text users' input text
	* @param food FoodInfo
	* @param database will be used to access database
	* @see FoodInfo
	* @return boolean return whether the input text is in correct format
	*/
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

	/**
	* This method will check the input text(catch exception if it is not in correct format)
	* and set the energy for FoodInfo
	* @param text users' input text
	* @param food FoodInfo
	* @param database will be used to access database
	* @see FoodInfo
	* @return boolean return whether the input text is in correct format
	*/
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
	/**
	* This method will check the input text(catch exception if it is not in correct format)
	* and set the protein for FoodInfo
	* @param text users' input text
	* @param food FoodInfo
	* @param database will be used to access database
	* @see FoodInfo
	* @return boolean return whether the input text is in correct format
	*/
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
	/**
	* This method will check the input text(catch exception if it is not in correct format)
	* and set the fiber for FoodInfo
	* @param text users' input text
	* @param food FoodInfo
	* @param database will be used to access database
	* @see FoodInfo
	* @return boolean return whether the input text is in correct format
	*/
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
	/**
	* This method will check the input text(catch exception if it is not in correct format)
	* and set the price for FoodInfo
	* @param text users' input text
	* @param food FoodInfo
	* @param database will be used to access database
	* @see FoodInfo
	* @return boolean return whether the input text is in correct format
	*/
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



	/**
	* This method will check the input text(catch exception if it is not in correct format)
	* and set the foodName for FoodInput
	* @param text users' input text
	* @param foodInput FoodInput
	* @param database will be used to access database
	* @see FoodInput
	* @return boolean return whether the input text is in correct format
	*/
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





	/**
	* This method will check the input text(catch exception if it is not in correct format)
	* and set the amount for FoodInput
	* @param text users' input text
	* @param foodInput FoodInput
	* @param database will be used to access database
	* @see FoodInput
	* @return boolean return whether the input text is in correct format
	*/
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

	/**
	* This method will check the input text(catch exception if it is not in correct format)
	* and set the price for FoodInput
	* @param text users' input text
	* @param foodInput FoodInput
	* @param database will be used to access database
	* @see FoodInput
	* @return boolean return whether the input text is in correct format
	*/
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



	/**
	* This method will check the date of the FoodInput(catch exception if it is not in correct format)
	* @param text users' input text
	* @see FoodInput
	* @return boolean return whether the input text is in correct format
	*/
	public boolean dateCheck(String text) {
		try {
			if(ValidDate(text)) {
				return true;
			}
			else return false;
		}catch(NumberFormatException ne){return false;}
	}

	/**
	* This method will return the dietrecord in String of a specific user id
	* @param text users' input text
	* @param database SQLDatabase to access database
	* @param id the userid
	* @return String return the dietrecord in String of a specific user id
	*/
	public String dietsearch(String text, SQLDatabaseEngine database, String id ) {
		return database.reportDiet(text,id);
	}

	/**
	* This method will trigger the update the diet_conclusion table in database
	* @param healthSearcher users' input text
	* @param database SQLDatabase to access database
	* @param amount amount of FoodInput
	* @param id the userid
	* @param time time of FoodInput
	* @param price price of FoodInput
	* @see SQLDatabaseEngine
	* @see HealthSearch
	* @see FoodInput
	*/
	public void consumptionUpdate(HealthSearch healthSearcher,SQLDatabaseEngine database,int amount,String id,String time,double price) {
		database.updateconsumption(healthSearcher,amount,id,time,price);
	}
}
