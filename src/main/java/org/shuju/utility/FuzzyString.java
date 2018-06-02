package org.shuju.utility;

public class FuzzyString
{
	public static final String WHITE_SPACES = "\\s\\u00A0\\u3000";

	public static String trim(String s) {
		return trim(s, FuzzyString.WHITE_SPACES);
	}

	public static String trim(String s, String whiteSpaces) {
		if(s == null) {
			return null;
		}
		return s.replaceAll("^[" + whiteSpaces + "]+", "").replaceAll("[" +whiteSpaces + "]+$", "");
	}

	public static String normalizeWhiteSpaces(String s) {
		return normalizeWhiteSpaces(s, FuzzyString.WHITE_SPACES);
	}

	public static String normalizeWhiteSpaces(String s, String whiteSpaces) {
		if(s == null) {
			return null;
		}
		return s.replaceAll("[" + whiteSpaces + "]", " ");
	}

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

	public static double distanceSimple(String s1, String s2) {
		if(s1.equals(s2)) {
			return 0.0;
		}
		else if(s1.contains(s2)) {
			return 0.5;
		}
		else {
			return 1.0;
		}
	}

	public static double distanceLevenshtein(String s1, String s2) {
		return 1.0 - (double) intersect(s1, s2).length() / (double) union(s1, s2).length();
	}
}
