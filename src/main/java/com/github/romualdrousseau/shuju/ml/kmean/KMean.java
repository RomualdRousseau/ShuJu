package com.github.romualdrousseau.shuju.ml.kmean;

import com.github.romualdrousseau.shuju.math.Matrix;
import com.github.romualdrousseau.shuju.math.MatrixFunction;
import com.github.romualdrousseau.shuju.math.Vector;

public class KMean {
    public KMean(int k) {
        this.k = k;
        this.initialized = false;
    }

    public void fit(final Vector[] inputs, final Vector[] targets) {
        if (!this.initialized) {
            this.initializer(inputs);
            this.initialized = true;
        }
        this.expectation(inputs, targets);
        this.maximation(inputs, targets);
    }

    public Vector predict(final Vector input) {
        return this.weights.copy().map(MSE, new Matrix(input)).flatten().sqrt().mult(-1).exp().transpose().toVector(0);
    }

    private void initializer(Vector[] inputs) {
        this.weights = new Matrix(inputs[0].rowCount(), 0);
        for (int j = 0; j < this.k; j++) {
            int n = (int) Math.floor(Math.random() * inputs.length);
            this.weights = this.weights.concat(inputs[n]);
        }
    }

    private void expectation(Vector[] inputs, Vector[] labels) {
        for (int i = 0; i < inputs.length; i++) {
            labels[i] = new Vector(this.k)
                    .oneHot(this.weights.copy().map(MSE, new Matrix(inputs[i])).flatten().transpose().argmin(0));
        }
    }

    private void maximation(Vector[] inputs, Vector[] labels) {
        this.weights = new Matrix(this.weights.rowCount(), 0);

        for (int j = 0; j < this.k; j++) {
            Vector sum = new Vector(this.weights.rowCount());
            float count = 0;
            for (int i = 0; i < inputs.length; i++) {
                if (labels[i].argmax() == j) {
                    sum.add(inputs[i]);
                    count++;
                }
            }

            this.weights = this.weights.concat(sum.div(count));
        }
    }

    private MatrixFunction<Float, Float> MSE = new MatrixFunction<Float, Float>() {
        public Float apply(Float v, int row, int col, Matrix matrix) {
            float a = v - matrix.get(row, 0);
            return a * a;
        }
    };

    private Matrix weights;
    private int k = 3;
    private boolean initialized;
}
