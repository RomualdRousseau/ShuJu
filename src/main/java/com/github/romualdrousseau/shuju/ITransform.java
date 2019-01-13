package com.github.romualdrousseau.shuju;

public interface ITransform
{
	public void apply(IFeature<?> feature, int rowIndex, int colIndex);
}
