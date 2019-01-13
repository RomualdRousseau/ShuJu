package com.github.romualdrousseau.shuju.ml.kmean;

import com.github.romualdrousseau.shuju.IClassifier;
import com.github.romualdrousseau.shuju.DataRow;
import com.github.romualdrousseau.shuju.DataSet;
import com.github.romualdrousseau.shuju.Result;

public class Kmean implements IClassifier
{
	public DataSet getTrainingSet() {
		throw new UnsupportedOperationException("Not Implemented");
	}

	public IClassifier train(DataSet trainingSet) {
		throw new UnsupportedOperationException("Not Implemented");
	}

	public Result predict(DataRow features) {
		throw new UnsupportedOperationException("Not Implemented");
	}
}
