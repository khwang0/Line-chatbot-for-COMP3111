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
		nameList = new String[1];
		priceList = new int[1];
		nameList[0] = "N/A";
		priceList[0] = 0;
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
		String[] plainTexts = plainText.split("\n");
		String[] temp = database.getFoodInfo();
		ArrayList<String> tempList;
		String patternString = "(.+?)([0-9]+)";
		Pattern pattern = Pattern.compile(patternString);
		Matcher matcher;
		nameList = new String[plainTexts.length];
		priceList = new int[plainTexts.length];
		ingredientList = new String[plainTexts.length][];
		boolean fi;
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
