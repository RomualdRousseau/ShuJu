package com.github.romualdrousseau.shuju.ml.kmean;

import com.github.romualdrousseau.shuju.math.Matrix;
import com.github.romualdrousseau.shuju.math.MatrixFunction;
import com.github.romualdrousseau.shuju.math.Vector;

public class KMean {
    public KMean(int k) {
        this.k = k;
    }

    public void initializer(Matrix[] data) {
        this.weights = new Matrix(data[0].rowCount(), 0);
        for (int j = 0; j < this.k; j++) {
            int n = (int) Math.floor(Math.random() * data.length);
            this.weights = this.weights.concat(data[n]);
        }
    }

    public Matrix predict(Matrix data) {
        return this.weights.copy().map(MSE, data).flatten().sqrt().mult(-1).exp().transpose();
    }

    public void fit(Matrix[] data, Matrix[] labels) {
        expectation(data, labels);
        maximation(data, labels);
    }

    private void expectation(Matrix[] data, Matrix[] labels) {
        for (int i = 0; i < data.length; i++) {
            labels[i] = new Matrix(
                    new Vector(this.k).oneHot(this.weights.copy().map(MSE, data[i]).flatten().transpose().argmin(0)));
        }
    }

    private void maximation(Matrix[] data, Matrix[] labels) {
        this.weights = new Matrix(this.weights.rowCount(), 0);

        for (int j = 0; j < this.k; j++) {
            Matrix sum = new Matrix(this.weights.rowCount(), 1, 0);
            float count = 0;
            for (int i = 0; i < data.length; i++) {
                if (labels[i].argmax(0) == j) {
                    sum.add(data[i]);
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
}
