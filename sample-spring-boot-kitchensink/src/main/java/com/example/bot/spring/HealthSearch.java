package com.example.bot.spring;

public class HealthSearch {
	private SearchWeb searchweb;
	private boolean isFound;
	private String energy;
	private String protein;
	private String fat;
	private String carbohydrate;
	private String sugar;
	private String water;
	private String ca;
	private String na;
	private String unit;
	//private int mode;
	
	public HealthSearch()
		{
			
		this.energy = "N/A";
		this.protein = "N/A";
		this.fat = "N/A";
		this.carbohydrate = "N/A";
		this.sugar = "N/A";
		this.water = "N/A";
		this.calcium = "N/A";
		this.sodium = "N/A";
		this.unit = "N/A";
		this.searchweb = new SearchWeb();
		this.isFound = false;

		
	}

	public void setKeyword(String keyword) {
		this.searchweb.setKeyword(keyword);
	}
	public boolean search() {
		String url = "";
		//switch(this.mode){
		//	case 0:
				url = "https://ndb.nal.usda.gov/ndb/search/list?ds=Standard+Reference&&&qlookup=";
		//	case 1:
		//		url = "https://ndb.nal.usda.gov/ndb/search/list?ds=Branded+Food+Products&&qlookup=";
		//		break;
		String result = this.searchweb.SendGet(url);
		String newurl = this.searchweb.RegexString(result, "href=\"(/ndb/foods/show.+?)\"");
		if(!newurl.equals("N/A")) {
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

			this.calcium = searchweb.RegexStringProperty(result,"Na");

			this.sodium = searchweb.RegexStringProperty(result,"Ca");
		}
		else {
			this.isFound=false;
		}
		return this.isFound;
	}
	public boolean getStatus() {
		return this.isFound;
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
	

}
