package com.github.romualdrousseau.shuju.transforms;

import com.github.romualdrousseau.shuju.ITransform;
import com.github.romualdrousseau.shuju.IFeature;
import com.github.romualdrousseau.shuju.DataSet;
import com.github.romualdrousseau.shuju.features.NumericFeature;

public class VectorAdd implements ITransform
{
	public VectorAdd(DataSet other) {
		this.other = other;
		this.a = 1.0;
	}

	public VectorAdd(DataSet other, double a) {
		this.other = other;
		this.a = a;
	}

	public void apply(IFeature<?> feature, int rowIndex, int colIndex) {
		assert(feature instanceof NumericFeature);
		NumericFeature numericFeature = (NumericFeature) feature;
		NumericFeature otherFeature = (NumericFeature) ((colIndex == IFeature.LABEL) ? this.other.rows().get(rowIndex).getLabel() : this.other.rows().get(rowIndex).features().get(colIndex));
		numericFeature.setValue(numericFeature.getValue() + this.a * otherFeature.getValue());
	}

	private DataSet other;
	private double a;
}
