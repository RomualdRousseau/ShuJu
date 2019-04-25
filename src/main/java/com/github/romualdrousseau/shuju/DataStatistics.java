package com.github.romualdrousseau.shuju;

import com.github.romualdrousseau.shuju.features.NumericFeature;
import com.github.romualdrousseau.shuju.math.Scalar;

public class DataStatistics
{
	public static float var(DataSet dataset, int col) {
		final DataSummary summary = new DataSummary(dataset, col);
		return DataStatistics.var(dataset, col, summary);
	}

	public static float var(DataSet dataset, int col, DataSummary summary) {
		float var = 0;
		for(DataRow row: dataset.rows()) {
			IFeature<?> feature = (col == IFeature.LABEL) ? row.getLabel() : row.features().get(col);
			assert(feature instanceof NumericFeature);

			double temp = (Double) feature.getValue() - summary.avg;
			 var += temp * temp;
		}
		var /= (double) (summary.count - 1);

		return var;
	}

	public static float cov(DataSet dataset, int col1, int col2) {
		final DataSummary summary1 = new DataSummary(dataset, col1);
		final DataSummary summary2 = new DataSummary(dataset, col2);
		return DataStatistics.cov(dataset, col1, col2, summary1, summary2);
	}

	public static float cov(DataSet dataset, int col1, int col2, DataSummary summary1, DataSummary summary2) {
		assert(summary1.count == summary2.count);

		float cov = 0;
		for(DataRow row: dataset.rows()) {
			IFeature<?> feature1 = (col1 == IFeature.LABEL) ? row.getLabel() : row.features().get(col1);
			assert(feature1 instanceof NumericFeature);
			IFeature<?> feature2 = (col2 == IFeature.LABEL) ? row.getLabel() : row.features().get(col2);
			assert(feature2 instanceof NumericFeature);

			float temp1 = (Float) feature1.getValue() - summary1.avg;
			float temp2 = (Float) feature2.getValue() - summary2.avg;
			cov += temp1 * temp2;
		}
		return cov / (float) (summary1.count - 1.0f);
	}

	public static float corr(DataSet dataset, int col1, int col2) {
		final DataSummary summary1 = new DataSummary(dataset, col1);
		final DataSummary summary2 = new DataSummary(dataset, col2);
		return DataStatistics.corr(dataset, col1, col2, summary1, summary2);
	}

	public static float corr(DataSet dataset, int col1, int col2, DataSummary summary1, DataSummary summary2) {
		assert(summary1.count == summary2.count);

		float cov = DataStatistics.cov(dataset, col1, col2, summary1, summary2);
		float var1 = DataStatistics.var(dataset, col1, summary1);
		float var2 = DataStatistics.var(dataset, col2, summary2);
		return cov / Scalar.sqrt(var1 * var2);
	}
}
