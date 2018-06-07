package org.shuju.transformer;

import org.shuju.*; 

public class SmoothScaler extends ITransform
{
	public SmoothScaler(double coef) {
		this.firstRow = false; 
		this.coef = coef;
	}
	
	public void apply(IFeature feature, int rowIndex, int colIndex) {
		assert(feature instanceof NumericFeature);
		NumericFeature numericFeature = (NumericFeature) feature;
		if(this.firstRow) {
			this.lastValue = numericFeature.getValue();
			firstRow = false;
		}
		else {
			numericFeature.setValue(this.lastValue * (1.0 - this.coef) + numericFeature.getValue() * this.coef);
		}
	}

	boolean firstRow;
	double lastValue;
	double coef;
}