package com.github.romualdrousseau.shuju;

public interface IClassifier {
    public DataSet getTrainingSet();

    public void train(DataSet trainingSet);

    public DataRow predict(DataRow features);
}
