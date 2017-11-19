package com.example.bot.spring;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
* SearchWeb will perform the function of getting content with given regex from given url
* (the url of the database, which is https://ndb.nal.usda.gov/ndb/search/list)
*
* @author  G8
* @version 1.0
* @since   2017/11/19
*/
public class SearchWeb {
	/**
	* the word need to be searched in external database
	*/
	private String keyword;

	/**
	* Constructor of SearchWeb
	*/
	public SearchWeb(){
	}

	/**
	* This method is to set the word need to be searched in external database
	* @param keyword the word that the keyword will be changed to
	*/
	public void setKeyword(String keyword) {
		keyword = keyword.replace(" ","+");
		this.keyword = keyword;
	}
	/**
	* This method is to send url connection request and get html content of given url
	* @param url the url that is needed to be accessed
	* @return String the whole content of given url
	*/
	public String SendGet(String url)
	{
		String result = "";
		BufferedReader in = null;
		url = url+this.keyword;
		try
		{
			URL realUrl = new URL(url);
			URLConnection connection = realUrl.openConnection();
			connection.connect();
			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			while ((line = in.readLine()) != null)
			{
				result += line;
			}
		} catch (Exception e)
		{
			System.out.println("abnormal GET！" + e);
			e.printStackTrace();
		}
		// 使用finally来关闭输入流
		finally
		{
			try
			{
				if (in != null)
				{
					in.close();
				}
			} catch (Exception e2)
			{
				e2.printStackTrace();
			}
		}
		return result;
	}

	/**
	* This method is to extract first food information url from the content of the foods that are returned by the external database
	* @param targetStr the content where we need to extract from
	* @param patternStr the regex that is needed to be compiled
	* @return String the url of target food information or "N/A" if no food is found
	*/
	public String RegexString(String targetStr, String patternStr)
	{
		String checkPatternStr = "(No food)";
		Pattern checkPattern = Pattern.compile(checkPatternStr);
		Matcher checkMatcher = checkPattern.matcher(targetStr);
		boolean fi = checkMatcher.find();
		if(fi) {
			return "N/A";
		}

		Pattern pattern = Pattern.compile(patternStr);
		Matcher matcher = pattern.matcher(targetStr);
		fi = matcher.find();
		String result = matcher.group(1);
  		return result;
	}

	/**
	* This method is to extract food name from the content in the food infomation url
	* @param targetStr the content where we need to extract from
	* @param patternStr the regex that is needed to be compiled
	* @return String the name of food or "N/A" if no name is found
	*/
	public String RegexStringName(String targetStr, String patternStr){
		Pattern pattern = Pattern.compile(patternStr);
		Matcher matcher = pattern.matcher(targetStr);
		boolean fi = matcher.find();
		String[] propertyList;
		if (!fi) {
			return "N/A";
		}
		propertyList= new String[matcher.groupCount()];
		for(int i=0;i<matcher.groupCount();i++){
         	propertyList[i]=matcher.group(i+1);
         	//System.out.println((i+1)+"th:  "+ matcher.group(i+1));
        }
        String result = propertyList[2];
		result = result.replace("\t","");
  		return result;
	}

	/**
	* This method is to extract unit of the food from the content in the food infomation url
	* @param targetStr the content where we need to extract from
	* @param patternStr the regex that is needed to be compiled
	* @return String the unit of food or "N/A" if no unit is found
	*/
	public String RegexStringUnit(String targetStr, String patternStr)
	{

		Pattern pattern = Pattern.compile(patternStr);
		Matcher matcher = pattern.matcher(targetStr);
		boolean fi = matcher.find();
		if(!fi) {
			return "N/A";
		}
		String result = matcher.group(1);
  		return result;
	}

	/**
	* This method is to extract the amount food properties in unit from the content in the food infomation url
	* @param targetStr the content where we need to extract from
	* @param property the specified property that is needed to be compiled
	* @return String the amount of the specified property of food in unit or "N/A" if no amount of property is found
	*/
	public String RegexStringProperty(String targetStr, String property)
	{
		String patternStr = property+"(.*?)([0-9][0-9]*[\\.]?[0-9]{0,2})</td>";
		Pattern pattern = Pattern.compile(patternStr);
		Matcher matcher = pattern.matcher(targetStr);
		boolean fi = matcher.find();
		String[] propertyList;
		if (!fi) {
			return "N/A";
		}
		propertyList= new String[matcher.groupCount()];
		for(int i=0;i<matcher.groupCount();i++){
         	propertyList[i]=matcher.group(i+1);
         	//System.out.println((i+1)+"th:  "+ matcher.group(i+1));
        }
        String result = propertyList[1];
  		return result;
	}



}
