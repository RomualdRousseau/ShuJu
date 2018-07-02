package com.github.romualdrousseau.shuju.ml.slr;

import com.github.romualdrousseau.shuju.IClassifier;
import com.github.romualdrousseau.shuju.DataRow;
import com.github.romualdrousseau.shuju.DataSet;
import com.github.romualdrousseau.shuju.DataSummary;
import com.github.romualdrousseau.shuju.DataStatistics;
import com.github.romualdrousseau.shuju.Result;
import com.github.romualdrousseau.shuju.IFeature;
import com.github.romualdrousseau.shuju.features.NumericFeature;

public class SLR implements IClassifier
{
	public DataSet getTrainingSet() {
		return this.trainingSet;
	}

	public IClassifier train(DataSet trainingSet) {
		this.trainingSet = trainingSet;
		final DataSummary summary1 = new DataSummary(trainingSet, 0);
		final DataSummary summary2 = new DataSummary(trainingSet, IFeature.LABEL);
		beta = DataStatistics.cov(trainingSet, 0, IFeature.LABEL, summary1, summary2) / DataStatistics.var(trainingSet, 0, summary1);
		alpha = summary2.avg - beta * summary1.avg;
		return this;
	}

	public Result predict(DataRow row) {
		double value = (Double) row.features().get(0).getValue() * beta + alpha;
		return new Result(row, new NumericFeature(value), 1.0);
	}
	
	private double beta;
	private double alpha;
	private DataSet trainingSet;
}
