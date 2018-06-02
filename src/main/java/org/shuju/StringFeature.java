package org.shuju;

import org.shuju.utility.FuzzyString;

public class StringFeature extends IFeature<String>
{
	public StringFeature(String value) {
		super(value);
	}

	protected double lossFuncImpl(IFeature other) {
		assert(other instanceof StringFeature);
		return FuzzyString.distanceSimple(this.getValue(), (String) other.getValue());
	}
}
