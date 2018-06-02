package org.shuju;

import org.shuju.utility.FuzzyString;

public class FuzzyStringFeature extends StringFeature
{
	public FuzzyStringFeature(String value) {
		super(value);
	}

	protected double lossFuncImpl(StringFeature other) {
		assert(other instanceof StringFeature);
		return FuzzyString.distanceLevenshtein((String) this.getValue(), (String) other.getValue());
	}
}
