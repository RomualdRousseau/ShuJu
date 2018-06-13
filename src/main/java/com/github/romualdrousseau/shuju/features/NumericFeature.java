package com.github.romualdrousseau.shuju.features;

import com.github.romualdrousseau.shuju.IFeature;

public class NumericFeature extends IFeature<Double>
{
	public NumericFeature(Double value) {
		super(value);
	}

	public NumericFeature(Double value, double probability) {
		super(value, probability);
	}

	protected double costFuncImpl(IFeature predictedValue) {
		assert predictedValue instanceof NumericFeature;
		NumericFeature typedPredictedValue = (NumericFeature) predictedValue;
		double dist = typedPredictedValue.getValue() - this.getValue();
		return dist * dist;
	}
}
