package com.github.romualdrousseau.shuju.transforms;

import com.github.romualdrousseau.shuju.ITransform;
import com.github.romualdrousseau.shuju.IFeature;
import com.github.romualdrousseau.shuju.DataSummary;
import com.github.romualdrousseau.shuju.features.NumericFeature;

public class NumericScaler implements ITransform
{
	public NumericScaler(DataSummary summary) {
		this.min = summary.min;
		this.ratio = 1.0 / (summary.max - summary.min);
	}

	public void apply(IFeature<?> feature, int rowIndex, int colIndex) {
		assert(feature instanceof NumericFeature);
		NumericFeature numericFeature = (NumericFeature) feature;
		numericFeature.setValue((numericFeature.getValue() - this.min) * this.ratio);
	}

	private double min;
	private double ratio;
}
