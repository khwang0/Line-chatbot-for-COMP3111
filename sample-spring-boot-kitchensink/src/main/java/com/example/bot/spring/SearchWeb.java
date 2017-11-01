package com.example.bot.spring;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SearchWeb {
	private String keyword;
	public SearchWeb(){
		
	}
	public void setKeyword(String keyword) {
		keyword = keyword.replace(" ","+");
		this.keyword = keyword;
	}
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
			System.out.println("发送GET请求出现异常！" + e);
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
