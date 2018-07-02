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
		double dist = (Double) predictedValue.getValue() - this.getValue();
		return dist * dist;
	}
}
