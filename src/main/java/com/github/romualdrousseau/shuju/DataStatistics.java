package com.github.romualdrousseau.shuju;

import com.github.romualdrousseau.shuju.math.deprecated.Tensor1D;

public class DataStatistics {
    public static int count(DataSummary summary) {
        return summary.count;
    }

    public static Tensor1D min(DataSummary summary) {
        return summary.min;
    }

    public static Tensor1D max(DataSummary summary) {
        return summary.max;
    }

    public static Tensor1D sum(DataSummary summary) {
        return summary.sum;
    }

    public static Tensor1D avg(DataSummary summary) {
        return summary.avg;
    }

    public static Tensor1D var(DataSummary summary) {
        Tensor1D var = new Tensor1D(0);
        boolean firstRow = true;
        for (DataRow row : summary.getDataSet().rows()) {
            Tensor1D feature = (summary.getPart() == DataRow.LABELS) ? row.label()
                    : row.features().get(summary.getColumn());
            if (firstRow) {
                var = feature.copy().sub(summary.avg).pow(2.0f);
                firstRow = false;
            } else {
                var.add(feature.copy().sub(summary.avg).pow(2.0f));
            }
        }
        return var.div((float) (summary.count - 1));
    }

    public static Tensor1D cov(DataSummary summary1, DataSummary summary2) {
        assert (summary1.getDataSet() == summary2.getDataSet());
        assert (summary1.count == summary2.count);

        Tensor1D cov = new Tensor1D(0);
        boolean firstRow = true;
        for (DataRow row : summary1.getDataSet().rows()) {
            Tensor1D feature1 = (summary1.getPart() == DataRow.LABELS) ? row.label()
                    : row.features().get(summary1.getColumn());
            Tensor1D feature2 = (summary2.getPart() == DataRow.LABELS) ? row.label()
                    : row.features().get(summary2.getColumn());

            Tensor1D temp1 = feature1.copy().sub(summary1.avg);
            Tensor1D temp2 = feature2.copy().sub(summary2.avg);

            if (firstRow) {
                cov = temp1.mul(temp2);
                firstRow = false;
            } else {
                cov.add(temp1.mul(temp2));
            }
        }
        return cov.div((float) (summary1.count - 1));
    }

    public static Tensor1D corr(DataSummary summary1, DataSummary summary2) {
        Tensor1D cov = DataStatistics.cov(summary1, summary2);
        Tensor1D var1 = DataStatistics.var(summary1);
        Tensor1D var2 = DataStatistics.var(summary2);
        return cov.div(var1.mul(var2).sqrt());
    }
}
