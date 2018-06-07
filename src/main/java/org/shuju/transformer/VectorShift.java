package org.shuju.transformer;

import org.shuju.*; 

public class VectorShift extends ITransform
{
	public VectorShift(double a) {
		this.a = a;
	}

	public void apply(IFeature feature, int rowIndex, int colIndex) {
		assert(feature instanceof NumericFeature);
		NumericFeature numericFeature = (NumericFeature) feature;
		numericFeature.setValue(numericFeature.getValue() + this.a);	
	}

	private double a;
}