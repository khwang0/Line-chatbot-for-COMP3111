package com.example.bot.spring;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.io.*;
import org.json.*;
//import org.json.simple.parser.JSONParser;
public class MenuReader {
	String[] nameList;
	int[] priceList;
	String [][] ingredientList;
	//private SQLDatabaseEngine database;

	public MenuReader(){
	}
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

	public boolean readFromText(String plainText,SQLDatabaseEngine database){
		nameList = new String[1];
		priceList = new int[1];
		ingredientList = new String[1][];
		String patternString = "(.+?)([0-9]+)";
		Pattern pattern = Pattern.compile(patternString);
		Matcher matcher = pattern.matcher(plainText);
		boolean fi = matcher.find();
		if(!fi) {
			return fi;
		}
		nameList[0] = matcher.group(1);
		priceList[0] = Integer.parseInt(matcher.group(2));
		String[] temp = database.getFoodInfo();
		ArrayList<String> tempList = new ArrayList<String>();
		for (int i = 0; i<temp.length; i++ ) {
			if (nameList[0].contains(temp[i])) {
				tempList.add(temp[i]);
			}
		}
		//ingredientList[0] = new String[(tempList.toArray(new String[0])).length];
		ingredientList[0] = tempList.toArray(new String[0]);
		return fi;
	}


	public boolean readFromJSON(String url) {
		String JSONString = readJSONFile(url);
		try{
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
		return true;
	}

	public String[] getName() {
		return nameList;
	}
	public int[] getPrice() {
		return priceList;
	}
	public String[][] getIngredient() {
		return ingredientList;
	}
}
