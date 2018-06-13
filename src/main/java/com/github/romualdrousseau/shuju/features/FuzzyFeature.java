package com.github.romualdrousseau.shuju.features;

import com.github.romualdrousseau.shuju.IFeature;
import com.github.romualdrousseau.shuju.utility.FuzzyString;

public class FuzzyFeature extends StringFeature
{
	public FuzzyFeature(String value) {
		super(value);
	}

	public FuzzyFeature(String value, double probability) {
		super(value, probability);
	}

	protected double costFuncImpl(IFeature predictedValue) {
		assert predictedValue instanceof StringFeature;
		FuzzyFeature typedPredictedValue = (FuzzyFeature) predictedValue;
		double dist = FuzzyString.distanceLevenshtein(typedPredictedValue.getValue(), this.getValue()); 
		return dist * dist;
	}
}
