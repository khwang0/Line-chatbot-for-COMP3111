package com.example.bot.spring;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.io.*;
import org.json.*;
//import org.json.simple.parser.JSONParser;

/**
* MenuReader will perform the function of read users menu from users text and given url of JSON file
* @author  G8
* @version 1.0
* @since   2017/11/19
*/
public class MenuReader {
	String[] nameList;
	int[] priceList;
	String [][] ingredientList;
	//private SQLDatabaseEngine database;
	/**
	* Constructor of MenuReader
	*/
	public MenuReader(){
		nameList = new String[1];
		priceList = new int[1];
		nameList[0] = "N/A";
		priceList[0] = 0;
	}

	/**
	* This method is to get the string format JSON file from given url
	* @param url the url of the JSON file
	* @return String the String format of JSON file
	*/
	private String readJSONFile(String url) {
		File file = new File(url);
		String content = "";
		StringBuilder sb = new StringBuilder();
		try {
			BufferedReader bf = new BufferedReader(new FileReader(file));
			 while(content != null){
				 content = bf.readLine();
				 if(content == null){
					 break;
				}
				 sb.append(content.trim());
			 }
			 bf.close();
		}catch(IOException ioexception){

		}
		return sb.toString();
	}

	/**
	* This method is to get the price and ingredient name from the user's input text
	* @param plainText the text of user input
	* @param database SQLDatabase functions that need to be used to access database
	* @see SQLDatabaseEngine
	* @return boolean return true if the food, ingredients and corresponding price is found in the given text
	*/
	public boolean readFromText(String plainText,SQLDatabaseEngine database){
		String[] plainTexts = plainText.split("\n");
		String[] temp = database.getFoodInfo();
		ArrayList<String> tempList;
		String patternString = "(.+?)([0-9]+)";
		Pattern pattern = Pattern.compile(patternString);
		Matcher matcher;
		nameList = new String[plainTexts.length];
		priceList = new int[plainTexts.length];
		ingredientList = new String[plainTexts.length][];
		boolean fi = false;
		for (int i =0; i<plainTexts.length;i++ ) {
			matcher = pattern.matcher(plainTexts[i]);
			fi = matcher.find();
			if(!fi) {
				priceList = new int[1];
				priceList[0] = 0;
				return fi;
			}
			nameList[i] = matcher.group(1);
			priceList[i] = Integer.parseInt(matcher.group(2));
			//database.pushTest(temp.length);

			tempList = new ArrayList<String>();
			for (int j = 0; j<temp.length; j++ ) {
				if (nameList[i].contains(temp[j])) {
					tempList.add(temp[j]);
				}
			}
			//ingredientList[0] = new String[(tempList.toArray(new String[0])).length];
			ingredientList[i] = tempList.toArray(new String[tempList.size()]);
		}
		return fi;
	}

	/**
	* This method is to get the price and ingredient name from the url of the JSON file
	* @param url the url of the JSON file
	* @return boolean return true if the food, ingredients and corresponding price is found in the given JSON
	*/
	public boolean readFromJSON(String url) {
		boolean fi = true;
		try{
			String JSONString = readJSONFile(url);
			JSONArray jsonArray = new JSONArray(JSONString);
			nameList = new String[jsonArray.length()];
			priceList = new int[jsonArray.length()];
			ingredientList = new String[jsonArray.length()][];
			for (int i = 0; i < jsonArray.length(); i++)
			{
				nameList[i]=jsonArray.getJSONObject(i).getString("name");
			    priceList[i] = jsonArray.getJSONObject(i).getInt("price");
			    JSONArray ingreArray = jsonArray.getJSONObject(i).getJSONArray("ingredients");
			    ingredientList[i] = new String[ingreArray.length()];
			    for(int j = 0; j < ingreArray.length();j++)
			    {
			    		System.out.println(ingreArray.getString(j));
			    		ingredientList[i][j] = ingreArray.getString(j);
			    }
			}
		}catch(JSONException exception){

		}finally{

		}
		return fi;
	}

	/**
	* This method is to get the names of the list of food
	* @return String[] names of food
	*/
	public String[] getName() {
		return nameList;
	}
	/**
	* This method is to get the price of the list of food
	* @return int[] price of food
	*/
	public int[] getPrice() {
		return priceList;
	}
	/**
	* This method is to get the ingredients of the list of food
	* @return String[][]  ingredients of list of food
	*/
	public String[][] getIngredient() {
		return ingredientList;
	}
}
