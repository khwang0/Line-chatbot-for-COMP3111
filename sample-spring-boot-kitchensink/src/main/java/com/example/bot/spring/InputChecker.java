package com.example.bot.spring;
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
	public boolean ValidCarbs(String text) throws NumberFormatException {
		return( Double.parseDouble(text) < 3000 && Double.parseDouble(text)> 0 ) ;
	}
	public boolean ValidProtein(String text) throws NumberFormatException {
		return( Double.parseDouble(text) < 1000 && Double.parseDouble(text)> 0 );
	}
	public boolean ValidVegfruit(String text) throws NumberFormatException {
		return( Double.parseDouble(text) < 50 && Double.parseDouble(text)> 0 );
	}
	public boolean ValidOtherinfo(String text) throws NumberFormatException {
		return (text.length()<=1000);
	}
	
	private void ModeSwitcher( Users currentUser, SQLDatabaseEngine database, String mode) {
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
				((DetailedUser)currentUser).setBodyFat(Double.parseDouble(text)); 
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
				((DetailedUser)currentUser).setExercise(Integer.parseInt(text)); 
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
				((DetailedUser)currentUser).setCalories(Integer.parseInt(text)); 
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
				((DetailedUser)currentUser).setCarbs(Double.parseDouble(text)); 
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
				((DetailedUser)currentUser).setProtein(Double.parseDouble(text)); 
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
				((DetailedUser)currentUser).setVegfruit(Double.parseDouble(text)); 
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
    		((DetailedUser)currentUser).setOtherInfo(text);
    		return true;
    	}
		else
			return false;
		}catch(NumberFormatException ne){return false;}
	}
}
