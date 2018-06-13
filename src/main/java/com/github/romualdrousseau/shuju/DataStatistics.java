package com.github.romualdrousseau.shuju;

import com.github.romualdrousseau.shuju.features.NumericFeature;

public class DataStatistics
{
	public static double var(DataSet dataset, int col) {
		final DataSummary summary = new DataSummary(dataset, col);
		return DataStatistics.var(dataset, col, summary);
	}
	
	public static double var(DataSet dataset, int col, DataSummary summary) {
		double var = 0;
		for(DataRow row: dataset.rows()) {
			IFeature feature = (col == IFeature.LABEL) ? row.getLabel() : row.features().get(col);
			assert(feature instanceof NumericFeature);
			
			double temp = (Double) feature.getValue() - summary.avg;
			 var += temp * temp;
		}
		var /= (double) (summary.count - 1);
		
		return var;
	}
	
	public static double cov(DataSet dataset, int col1, int col2) {
		final DataSummary summary1 = new DataSummary(dataset, col1);
		final DataSummary summary2 = new DataSummary(dataset, col2);
		return DataStatistics.cov(dataset, col1, col2, summary1, summary2);
	}
	
	public static double cov(DataSet dataset, int col1, int col2, DataSummary summary1, DataSummary summary2) {
		assert(summary1.count == summary2.count);
		
		double cov = 0;
		for(DataRow row: dataset.rows()) {
			IFeature feature1 = (col1 == IFeature.LABEL) ? row.getLabel() : row.features().get(col1);
			assert(feature1 instanceof NumericFeature);
			IFeature feature2 = (col2 == IFeature.LABEL) ? row.getLabel() : row.features().get(col2);
			assert(feature2 instanceof NumericFeature);
			
			double temp1 = (Double) feature1.getValue() - summary1.avg;
			double temp2 = (Double) feature2.getValue() - summary2.avg;
			cov += temp1 * temp2;
		}
		return cov / (double) (summary1.count - 1);
	}
	
	public static double corr(DataSet dataset, int col1, int col2) {
		final DataSummary summary1 = new DataSummary(dataset, col1);
		final DataSummary summary2 = new DataSummary(dataset, col2);
		return DataStatistics.corr(dataset, col1, col2, summary1, summary2);
	}
	
	public static double corr(DataSet dataset, int col1, int col2, DataSummary summary1, DataSummary summary2) {
		assert(summary1.count == summary2.count);
		
		double cov = DataStatistics.cov(dataset, col1, col2, summary1, summary2);
		double var1 = DataStatistics.var(dataset, col1, summary1);
		double var2 = DataStatistics.var(dataset, col2, summary2);
		return cov / Math.sqrt(var1 * var2);	
	}
}
