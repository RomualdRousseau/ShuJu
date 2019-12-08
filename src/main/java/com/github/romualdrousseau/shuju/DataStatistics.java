package com.github.romualdrousseau.shuju;

import com.github.romualdrousseau.shuju.math.Vector;

public class DataStatistics {
    public static int count(DataSummary summary) {
        return summary.count;
    }

    public static Vector min(DataSummary summary) {
        return summary.min;
    }

    public static Vector max(DataSummary summary) {
        return summary.max;
    }

    public static Vector sum(DataSummary summary) {
        return summary.sum;
    }

    public static Vector avg(DataSummary summary) {
        return summary.avg;
    }

    public static Vector var(DataSummary summary) {
        Vector var = new Vector(0);
        boolean firstRow = true;
        for (DataRow row : summary.getDataSet().rows()) {
            Vector feature = (summary.getPart() == DataRow.LABELS) ? row.label()
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

    public static Vector cov(DataSummary summary1, DataSummary summary2) {
        assert (summary1.getDataSet() == summary2.getDataSet());
        assert (summary1.count == summary2.count);

        Vector cov = new Vector(0);
        boolean firstRow = true;
        for (DataRow row : summary1.getDataSet().rows()) {
            Vector feature1 = (summary1.getPart() == DataRow.LABELS) ? row.label()
                    : row.features().get(summary1.getColumn());
            Vector feature2 = (summary2.getPart() == DataRow.LABELS) ? row.label()
                    : row.features().get(summary2.getColumn());

            Vector temp1 = feature1.copy().sub(summary1.avg);
            Vector temp2 = feature2.copy().sub(summary2.avg);

            if (firstRow) {
                cov = temp1.mult(temp2);
                firstRow = false;
            } else {
                cov.add(temp1.mult(temp2));
            }
        }
        return cov.div((float) (summary1.count - 1));
    }

    public static Vector corr(DataSummary summary1, DataSummary summary2) {
        Vector cov = DataStatistics.cov(summary1, summary2);
        Vector var1 = DataStatistics.var(summary1);
        Vector var2 = DataStatistics.var(summary2);
        return cov.div(var1.mult(var2).sqrt());
    }
}
