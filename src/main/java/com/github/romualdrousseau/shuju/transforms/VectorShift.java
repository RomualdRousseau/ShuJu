package com.github.romualdrousseau.shuju.transforms;

import com.github.romualdrousseau.shuju.ITransform;
import com.github.romualdrousseau.shuju.IFeature;
import com.github.romualdrousseau.shuju.features.NumericFeature;

public class VectorShift implements ITransform
{
	public VectorShift(double a) {
		this.a = a;
	}

	public void apply(IFeature<?> feature, int rowIndex, int colIndex) {
		assert(feature instanceof NumericFeature);
		NumericFeature numericFeature = (NumericFeature) feature;
		numericFeature.setValue(numericFeature.getValue() + this.a);
	}

	private double a;
}
