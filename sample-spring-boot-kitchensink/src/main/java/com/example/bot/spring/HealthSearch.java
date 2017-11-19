package com.example.bot.spring;

public class HealthSearch {
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

	public HealthSearch()
	{
		database = new SQLDatabaseEngine();
		this.foodName = "0";//"N/A";
		this.energy = "0";//"N/A";
		this.protein = "0";//"N/A";
		this.fat = "0";//"N/A";
		this.carbohydrate = "0";//"N/A";
		this.sugar = "0";//"N/A";
		this.water = "0";//"N/A";
		this.calcium = "0";//"N/A";
		this.sodium = "0";//"N/A";
		this.unit = "0";//"N/A";
		this.fiber = "0";//"N/A";
		this.searchweb = new SearchWeb();
		this.isFound = false;
	}

	public void setKeyword(String keyword) {
		this.searchweb.setKeyword(keyword);
		this.foodName = keyword;
	}
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


	public boolean search(){
		if (searchInOwnDatabase()) {
			return true;
		}
		return searchInWeb();
	}
	public boolean getStatus() {
		return this.isFound;
	}
	public String getFoodName(){
		return this.foodName;
	}
	public String getEnergy(){
		return this.energy;
	}
	public String getProtein(){
		return this.protein;
	}
	public String getFat(){
		return this.fat;
	}
	public String getCarbohydrate(){
		return this.carbohydrate;
	}
	public String getSugar(){
		return this.sugar;
	}
	public String getWater(){
		return this.water;
	}
	public String getCalcium(){
		return this.calcium;
	}
	public String getSodium(){
		return this.sodium;
	}
	public String getUnit(){
		return this.unit;
	}
	public String getFiber() {
		return this.fiber;
	}

}
