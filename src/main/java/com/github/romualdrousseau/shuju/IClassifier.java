package com.github.romualdrousseau.shuju;

public interface IClassifier
{
	public DataSet getTrainingSet();

	public IClassifier train(DataSet trainingSet);

	public Result predict(DataRow features);
}
