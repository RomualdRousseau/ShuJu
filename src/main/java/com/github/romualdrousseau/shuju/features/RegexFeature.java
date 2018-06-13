package com.github.romualdrousseau.shuju.features;

import com.github.romualdrousseau.shuju.IFeature;

public class RegexFeature extends StringFeature
{
	public RegexFeature(String value) {
		super(value);
	}

	public RegexFeature(String value, double probability) {
		super(value, probability);
	}

	protected double costFuncImpl(IFeature predictedValue) {
		assert predictedValue instanceof RegexFeature;
		RegexFeature typedPredictedValue = (RegexFeature) predictedValue;
		if(this.getValue().startsWith("^")) {
			return typedPredictedValue.getValue().matches(this.getValue()) ? 1.0 : 0.0;	
		}
		else {
			return typedPredictedValue.getValue().matches(this.getValue()) ? 0.0 : 1.0;	
		}
	}
}