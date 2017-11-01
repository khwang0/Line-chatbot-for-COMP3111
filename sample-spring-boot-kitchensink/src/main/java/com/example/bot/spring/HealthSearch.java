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
	private String unit;
	private int mode;
	
	public HealthSearch()
		{
			
		this.energy = "N/A";
		this.protein = "N/A";
		this.fat = "N/A";
		this.carbohydrate = "N/A";
		this.sugar = "N/A";
		this.water = "N/A";
		this.unit = "N/A";
		this.searchweb = new SearchWeb();
		this.mode = 0;
		this.isFound = false;
		
	}
	public void setMode(int mode) {
		if(mode<0||mode>2) {
			return;
		}
		else {
			this.mode = mode;
		}
	}
	public void setKeyword(String keyword) {
		this.searchweb.setKeyword(keyword);
	}
	public boolean search() {
		String url = "";
		switch(this.mode){
			case 0:
				url = "https://ndb.nal.usda.gov/ndb/search/list?ds=Standard+Reference&&&qlookup=";
				break;
			case 1:
				url = "https://ndb.nal.usda.gov/ndb/search/list?ds=Branded+Food+Products&&qlookup=";
				break;
			case 2:
				url = "https://ndb.nal.usda.gov/ndb/search/list?qlookup=";
				break;
		}
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
	public String getUnit(){
		return this.unit;
	}
	

}
