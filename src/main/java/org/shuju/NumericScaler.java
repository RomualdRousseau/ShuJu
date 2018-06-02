package org.shuju;

public class NumericScaler extends ITransform
{
	public NumericScaler(Summary summary) {
		this.min = summary.min;
		this.ratio = 1.0 / (summary.max - summary.min);
	}
	
	public void apply(IFeature feature) {
		assert(feature instanceof NumericFeature);
		NumericFeature numericFeature = (NumericFeature) feature;
		numericFeature.setValue((numericFeature.getValue() - this.min) * this.ratio);
	}
	
	private double min;
	private double ratio;
}
