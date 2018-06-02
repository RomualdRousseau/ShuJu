package org.shuju;

public class NumericFeature extends IFeature<Double>
{
	public NumericFeature(Double value) {
		super(value);
	}

	protected double lossFuncImpl(IFeature other) {
		assert(other instanceof NumericFeature);
		return (Double) other.getValue() - this.getValue();	
	}
}
