package org.shuju;

public abstract class ITransform
{
	public abstract void apply(IFeature feature, int rowIndex, int colIndex);
}
