package com.github.romualdrousseau.shuju.transforms;

import com.github.romualdrousseau.shuju.ITransform;
import com.github.romualdrousseau.shuju.IFeature;
import com.github.romualdrousseau.shuju.features.NumericFeature;

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

	private boolean firstRow;
	private double lastValue;
	private double coef;
}