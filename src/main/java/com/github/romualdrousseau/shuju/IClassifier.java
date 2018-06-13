package com.github.romualdrousseau.shuju;

public abstract class IClassifier
{
	public abstract IClassifier train(DataSet trainingSet);

	public abstract Result predict(DataRow features);
}
