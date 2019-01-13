package com.github.romualdrousseau.shuju.features;

import com.github.romualdrousseau.shuju.IFeature;

public class StringFeature extends IFeature<String>
{
	public StringFeature(String value) {
		super(value);
	}

	public StringFeature(String value, double probability) {
		super(value, probability);
	}

	protected double costFuncImpl(IFeature<?> predictedValue) {
		return predictedValue.getValue().equals(this.getValue()) ? 0.0 : 1.0;
	}
}
