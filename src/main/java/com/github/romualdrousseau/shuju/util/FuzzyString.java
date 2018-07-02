package com.github.romualdrousseau.shuju.util;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public class FuzzyString
{
	public static double similarity(String s1, String s2) {
		return levenshtein(s1, s2);
	}

	public static double similarity(String s1, String s2, String separator) {
		String[] w1 = s1.split(separator);
		String[] w2 = s2.split(separator);
        String[] q = FuzzyString.union(w1, w2);
        double[] v1 = Vector.fromTokens(q, w1);
        double[] v2 = Vector.fromTokens(q, w2);
        return Vector.scalar(v1, v2) / (Vector.norm(v1) * Vector.norm(v2));
    }

    public static double distance(String s1, String s2) {
		return 1.0 - FuzzyString.similarity(s1, s2);
	}

	public static double distance(String s1, String s2, String separator) {
        return 1.0 - FuzzyString.similarity(s1, s2, separator);
    }

    public static double levenshtein(String s1, String s2) {
		return Double.valueOf(FuzzyString.intersect(s1, s2).length()) / Double.valueOf(FuzzyString.union(s1, s2).length());
	}

	public static double levenshtein(String[] w, String s) {
		double max = 0;
    	for(String v: w) {
    		max = Math.max(max, FuzzyString.levenshtein(v, s));
    	}
    	return max;
    }

	public static String union(String s1, String s2) {
		String result = "";

		for(char c: s1.toCharArray()) {
			if(!result.contains(String.valueOf(c))) {
				result += c;
			}
		}

		for(char c: s2.toCharArray()) {
			if(!result.contains(String.valueOf(c))) {
				result += c;
			}
		}
		
		return result;
	}

	public static String[] union(String[] s1, String[] s2) {
		ArrayList<String> result = new ArrayList<String>(s1.length + s2.length);

		for(String v: s1) {
			if(!result.contains(v)) {
            	result.add(v);
            }
        }

        for(String v: s2) {
			if(!result.contains(v)) {
            	result.add(v);
            }
        }
        
        return result.toArray(new String[result.size()]);
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

	public static String[] intersect(String[] s1, String[] s2) {
		ArrayList<String> result = new ArrayList<String>(s1.length + s2.length);
		List<String> tmp = Arrays.asList(s2);

		for(String v: s1) {
			if(!result.contains(v) && tmp.contains(v)) {
				result.add(v);
			}
		}
		
		return result.toArray(new String[result.size()]);
	}
}
