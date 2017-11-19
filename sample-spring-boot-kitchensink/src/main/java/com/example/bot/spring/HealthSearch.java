package com.example.bot.spring;

/**
* HealthSearch will perform the function of searching given food name in local and external database
* and return corresponding properties
* @author  G8
* @version 1.0
* @since   2017/11/19
*/
public class HealthSearch {
	/**
	* SearchWeb object that is need to be used when searching in external database
	*
	*/
	private SearchWeb searchweb;
	private SQLDatabaseEngine database;
	private boolean isFound;
	private String foodName;
	private String energy;
	private String protein;
	private String fat;
	private String carbohydrate;
	private String sugar;
	private String water;
	private String calcium;
	private String sodium;
	private String unit;
	private String fiber;
	//private int mode;

	/**
	* Constructor
	*/
	public HealthSearch()
	{
		database = new SQLDatabaseEngine();
		this.foodName = "N/A";
		this.energy = "N/A";
		this.protein = "N/A";
		this.fat = "N/A";
		this.carbohydrate = "N/A";
		this.sugar = "N/A";
		this.water = "N/A";
		this.calcium = "N/A";
		this.sodium = "N/A";
		this.unit = "N/A";
		this.fiber = "N/A";
		this.searchweb = new SearchWeb();
		this.isFound = false;
	}
	/**
	* This method is to set keyword both for external searching in SearchWeb object
	* and the foodName in local database searching
	* @see SearchWeb
	* @param keyword the name of the food need to be searched
	*/
	public void setKeyword(String keyword) {
		this.searchweb.setKeyword(keyword);
		this.foodName = keyword;
	}
	/**
	* This method is to search in local database
	* @return boolean return whether the food is found in local database or not
	*/
	public boolean searchInOwnDatabase(){
		FoodInfo foodInfo = database.searchFoodInfo(this.foodName);
		if (foodInfo!=null) {
			this.protein = Double.toString(foodInfo.getProtein());
			this.energy = Double.toString(foodInfo.getEnergy());
			this.fiber = Double.toString(foodInfo.getFiber());
			return true;
		}
		return false;
	}

	/**
	* This method is to search in local database
	* @return boolean return whether the food is found in external database or not
	*/
	public boolean searchInWeb() {
		String url = "";

		url = "https://ndb.nal.usda.gov/ndb/search/list?ds=Standard+Reference&&&qlookup=";
		String result = this.searchweb.SendGet(url);
		String newurl = this.searchweb.RegexString(result, "href=\"(/ndb/foods/show.+?)\"");
		foodName = this.searchweb.RegexStringName(result,"href=\"/ndb/foods/show(.+?)fgcd(.+?)>(.*?)<");

		if(newurl.equals("0")){
			url = "https://ndb.nal.usda.gov/ndb/search/list?ds=Branded+Food+Products&&qlookup=";
			result = this.searchweb.SendGet(url);
			newurl = this.searchweb.RegexString(result, "href=\"(/ndb/foods/show.+?)\"");
		}

		if(!newurl.equals("0")) {
			this.isFound = true;
			newurl = "https://ndb.nal.usda.gov" + newurl;
			result = this.searchweb.SendGet(newurl);
			this.unit = this.searchweb.RegexStringUnit(result,"<br/>(.*?)</th>");

			this.energy = this.searchweb.RegexStringProperty(result, "Energy");

			this.protein = this.searchweb.RegexStringProperty(result, "Protein");

			this.fat = searchweb.RegexStringProperty(result, "fat");

			this.carbohydrate = searchweb.RegexStringProperty(result,"Carbohydrate");

			this.sugar = searchweb.RegexStringProperty(result,"Sugar");

			this.water = searchweb.RegexStringProperty(result, "Water");

			this.calcium = searchweb.RegexStringProperty(result,"Sodium, Na");

			this.sodium = searchweb.RegexStringProperty(result,"Calcium, Ca");

			this.fiber = searchweb.RegexStringProperty(result,"Fiber");
		}
		else {
			this.isFound=false;
		}
		return this.isFound;
	}

	/**
	* This method is to search first in local database and in external database if the food
	* is not found in local database
	* @return boolean return whether the food is found in local or external database or not
	*/
	public boolean search(){
		if (searchInOwnDatabase()) {
			return true;
		}
		return searchInWeb();
	}

	/**
	* This method is to get whether the food is found in external database or not
	* @return boolean return whether the food is found in external database or not
	*/
	public boolean getStatus() {
		return this.isFound;
	}

	/**
	* This method is to get the food name found in local or external database
	* @return String the food name that will be presented to users
	*/
	public String getFoodName(){
		return this.foodName;
	}

	/**
	* This method is to get the energy of the food found in local or external database
	* @return String return the energy in kcal of the food. will be "0" if no food is found
	*/
	public String getEnergy(){
		return this.energy;
	}

	/**
	* This method is to get the protein of the food found in local or external database
	* @return String return the protein in kcal of the food. will be "0" if no food is found
	*/
	public String getProtein(){
		return this.protein;
	}

	/**
	* This method is to get the fat of the food found in local or external database
	* @return String return the fat in g of the food. will be "0" if no food is found
	*/
	public String getFat(){
		return this.fat;
	}

	/**
	* This method is to get the carbohydrate of the food found in local or external database
	* @return String return the carbohydrate in g of the food. will be "0" if no food is found
	*/
	public String getCarbohydrate(){
		return this.carbohydrate;
	}
	/**
	* This method is to get the sugar of the food found in local or external database
	* @return String return the sugar in g of the food. will be "0" if no food is found
	*/
	public String getSugar(){
		return this.sugar;
	}
	/**
	* This method is to get the water of the food found in local or external database
	* @return String return the water in g of the food. will be "0" if no food is found
	*/
	public String getWater(){
		return this.water;
	}

	/**
	* This method is to get the calcium of the food found in local or external database
	* @return String return the calcium in mg of the food. will be "0" if no food is found
	*/
	public String getCalcium(){
		return this.calcium;
	}

	/**
	* This method is to get the sodium of the food found in local or external database
	* @return String return the sodium in mg of the food. will be "0" if no food is found
	*/
	public String getSodium(){
		return this.sodium;
	}

	/**
	* This method is to get the unit of the food property found in local or external database
	* @return String return the unit of the food. will be "0" if no food is found
	*/
	public String getUnit(){
		return this.unit;
	}

	/**
	* This method is to get the fiber of the food found in local or external database
	* @return String return the fiber in g of the food. will be "0" if no food is found
	*/
	public String getFiber() {
		return this.fiber;
	}

}
