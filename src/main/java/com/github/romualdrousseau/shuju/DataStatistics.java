package com.github.romualdrousseau.shuju;

import com.github.romualdrousseau.shuju.math.Scalar;
import com.github.romualdrousseau.shuju.math.Vector;

public class DataStatistics
{
	public static Vector var(DataSet dataset, int part, int col) {
		final DataSummary summary = new DataSummary(dataset, part, col);
		return DataStatistics.var(dataset, part, col, summary);
	}

	public static Vector var(DataSet dataset, int part, int col, DataSummary summary) {
        Vector var = new Vector(0);
        boolean firstRow = true;
		for(DataRow row: dataset.rows()) {
            Vector feature = (part == DataRow.LABELS) ? row.label() : row.features().get(col);
            if(firstRow) {
                var = feature.copy().sub(summary.avg).pow(2.0f);
                firstRow = false;
            } else {
                var.add(feature.copy().sub(summary.avg).pow(2.0f));
            }
		}
		return var.div((float) (summary.count - 1));
	}

	public static Vector cov(DataSet dataset, int part, int col1, int col2) {
		final DataSummary summary1 = new DataSummary(dataset, part, col1);
		final DataSummary summary2 = new DataSummary(dataset, part, col2);
		return DataStatistics.cov(dataset, part, col1, col2, summary1, summary2);
	}

	public static Vector cov(DataSet dataset, int part, int col1, int col2, DataSummary summary1, DataSummary summary2) {
		assert(summary1.count == summary2.count);

        Vector cov = new Vector(0);
        boolean firstRow = true;
		for(DataRow row: dataset.rows()) {
			Vector feature1 = (part == DataRow.LABELS) ? row.label() : row.features().get(col1);
			Vector feature2 = (part == DataRow.LABELS) ? row.label() : row.features().get(col2);

            Vector temp1 = feature1.copy().sub(summary1.avg);
            Vector temp2 = feature2.copy().sub(summary2.avg);

            if(firstRow) {
                cov = temp1.mult(temp2);
                firstRow = false;
            } else {
                cov.add(temp1.mult(temp2));
            }
		}
		return cov.div((float) (summary1.count - 1));
	}

	public static Vector corr(DataSet dataset, int part, int col1, int col2) {
		final DataSummary summary1 = new DataSummary(dataset, part, col1);
		final DataSummary summary2 = new DataSummary(dataset, part, col2);
		return DataStatistics.corr(dataset, part, col1, col2, summary1, summary2);
	}

	public static Vector corr(DataSet dataset, int part, int col1, int col2, DataSummary summary1, DataSummary summary2) {
		assert(summary1.count == summary2.count);

		Vector cov = DataStatistics.cov(dataset, part, col1, col2, summary1, summary2);
		Vector var1 = DataStatistics.var(dataset, part, col1, summary1);
		Vector var2 = DataStatistics.var(dataset, part, col2, summary2);
		return cov.div(var1.mult(var2).sqrt());
	}
}
