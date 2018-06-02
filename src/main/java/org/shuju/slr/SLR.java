package org.shuju.slr;

import org.shuju.IClassifier;
import org.shuju.IFeature;
import org.shuju.DataRow;
import org.shuju.DataSet;
import org.shuju.Summary;
import org.shuju.Result;
import org.shuju.Statistics;
import org.shuju.NumericFeature;

public class SLR extends IClassifier
{
	public IClassifier train(DataSet trainingSet) {
		final Summary summary1 = new Summary(trainingSet, 0);
		final Summary summary2 = new Summary(trainingSet, IFeature.LABEL);
		beta = Statistics.cov(trainingSet, 0, IFeature.LABEL, summary1, summary2) / Statistics.var(trainingSet, 0, summary1);
		alpha = summary2.avg - beta * summary1.avg;
		return this;
	}

	public Result predict(DataRow row) {
		double value = (Double) row.features().get(0).getValue() * beta + alpha;
		return new Result(row, new NumericFeature(value), 0.0);
	}
	
	private double beta;
	private double alpha;
}
