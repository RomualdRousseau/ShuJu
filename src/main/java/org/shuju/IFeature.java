package org.shuju;

public abstract class IFeature<T>
{
	public static final int LABEL = -1;
	
	public IFeature() {
		this.value = null;
	}

	public IFeature(T value) {
		this.value = value;
	}

	public T getValue() {
		return this.value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	public double lossFunc(IFeature other) {
		return lossFunc(other, 1.0);
	}

	public double lossFunc(IFeature other, double emptyValue) {
		if(this.isEmpty() || other.isEmpty()) {
			return emptyValue;
		}
		else {
			return this.lossFuncImpl(other);
		}
	}
	
	public boolean isEmpty() {
		return this.value == null;
	}

	public boolean equals(IFeature other) {
		return this.value.equals(other.getValue());
	}

	public static boolean isNullOrEmpty(IFeature feature) {
		return feature == null || feature.isEmpty();
	}
	
	public String toString() {
		return this.value.toString();
	}
	
	protected abstract double lossFuncImpl(IFeature otherValue);

	private T value;
}
