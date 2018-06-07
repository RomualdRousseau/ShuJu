package org.shuju.transformer;

import org.shuju.*; 

public class VectorAdd extends ITransform
{
	public VectorAdd(DataSet other) {
		this.other = other;
		this.a = 1.0;
	}

	public VectorAdd(DataSet other, double a) {
		this.other = other;
		this.a = a;
	}
	
	public void apply(IFeature feature, int rowIndex, int colIndex) {
		assert(feature instanceof NumericFeature);
		NumericFeature numericFeature = (NumericFeature) feature;
		NumericFeature otherFeature = (NumericFeature) ((colIndex == IFeature.LABEL) ? this.other.rows().get(rowIndex).getLabel() : this.other.rows().get(rowIndex).features().get(colIndex));
		numericFeature.setValue(numericFeature.getValue() + this.a * otherFeature.getValue());
	}

	private DataSet other;
	private double a;
}