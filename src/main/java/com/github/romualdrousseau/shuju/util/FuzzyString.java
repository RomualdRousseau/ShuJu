package com.github.romualdrousseau.shuju.utility;

public class FuzzyString
{
	public static String union(String s1, String s2) {
		String result = "";
		String tmp = s1 + s2;
		
		for(char c: tmp.toCharArray()) {
			if(!result.contains(String.valueOf(c))) {
				result += c;
			}
		}
		
		return result;
	}
	
	public static String intersect(String s1, String s2) {
		String result = "";

		for(char c: s1.toCharArray()) {
			if(!result.contains(String.valueOf(c)) && s2.contains(String.valueOf(c))) {
				result += c;
			}
		}
		
		return result;
	}

	public static double distanceLevenshtein(String s1, String s2) {
		return 1.0 - (double) intersect(s1, s2).length() / (double) union(s1, s2).length();
	}
}
