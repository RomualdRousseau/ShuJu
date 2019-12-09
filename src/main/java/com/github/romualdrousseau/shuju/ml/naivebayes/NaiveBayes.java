package com.github.romualdrousseau.shuju.ml.naivebayes;

import com.github.romualdrousseau.shuju.DataRow;
import com.github.romualdrousseau.shuju.DataSet;
import com.github.romualdrousseau.shuju.DataStatistics;
import com.github.romualdrousseau.shuju.DataSummary;
import com.github.romualdrousseau.shuju.math.DistributionFunction;
import com.github.romualdrousseau.shuju.math.Vector;
import com.github.romualdrousseau.shuju.math.distribution.GaussianDistribution;

public class NaiveBayes {

    public void fit(final Vector[] inputs, final Vector[] targets) {
        final int f = inputs[0].rowCount();
        final int c = targets[0].rowCount();

        this.priory = new float[c];
        this.matrix = new DistributionFunction[c][f];

        for (int i = 0; i < c; i++) {
            final Vector y = new Vector(c).set(i, 1); // Quick oneHot

            // Select all data for a given class i
            final DataSet Xy = new DataSet();
            for (int j = 0; j < targets.length; j++) {
                if (targets[j].equals(y)) {
                    Xy.addRow(new DataRow().addFeature(inputs[j]).setLabel(targets[j]));
                }
            }

            // Compute priory for the class i
            priory[i] = Float.valueOf(Xy.rows().size()) / Float.valueOf(inputs.length);

            // Get some statistics about the data
            DataSummary summary = new DataSummary(Xy, DataRow.FEATURES, 0);
            Vector mean = DataStatistics.avg(summary);
            Vector sigma = DataStatistics.var(summary).sqrt();

            // Prepare distribution matrix for each feature and class i
            for (int j = 0; j < matrix[i].length; j++) {
                matrix[i][j] = new GaussianDistribution(mean.get(j), sigma.get(j));
            }
        }
    }

    public Vector predict(final Vector input) {
        Vector yhat = new Vector(priory.length);
        float maxPy = 0;
        boolean firstPass = true;

        for (int i = 0; i < priory.length; i++) {

            // Calculate probability for each class (features are independant and using log
            // for numeric stability)
            float py = (float) Math.log(priory[i]);
            for (int j = 0; j < matrix[i].length; j++) {
                py += (float) Math.log(matrix[i][j].get(input.get(j)));
            }

            // Get the max probability
            if (firstPass || py > maxPy) {
                maxPy = py;
                yhat.oneHot(i);
                firstPass = false;
            }
        }

        return yhat;
    }

    private float[] priory;
    private DistributionFunction[][] matrix;
}
