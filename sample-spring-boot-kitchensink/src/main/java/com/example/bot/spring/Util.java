package com.example.bot.spring;

public class Util {
	public int min(int a, int b, int c) {
        return Math.min(a, Math.min(b, c));
    }
	    /**
     * Using Dynamic Programming, the Wagner-Fischer algorithm is able to 
     * calculate the edit distance between two strings.
     * @return edit distance between s1 and s2
     */
    public int WFDistance(String ss1, String ss2) {
    	char[] s1 = ss1.toLowerCase().toCharArray();
    	char[] s2 = ss2.toLowerCase().toCharArray();
    	int[][] dp = new int[s1.length + 1][s2.length + 1];
        for (int i = 0; i <= s1.length; dp[i][0] = i++);
        for (int j = 0; j <= s2.length; dp[0][j] = j++);
	        for (int i = 1; i <= s1.length; i++) {
            for (int j = 1; j <= s2.length; j++) {
                if (s1[i - 1] == s2[j - 1]) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = min(dp[i - 1][j] + 1, dp[i][j - 1] + 1, 
                    		dp[i - 1][j - 1] + 1);
                }
            }
       }
        return dp[s1.length][s2.length];
    }

	
	
}
