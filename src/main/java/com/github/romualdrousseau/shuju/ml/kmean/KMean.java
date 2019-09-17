package com.github.romualdrousseau.shuju.ml.kmean;

import com.github.romualdrousseau.shuju.math.Matrix;
import com.github.romualdrousseau.shuju.math.Vector;

public class KMean {
    private Matrix weights;
    private int k = 3;

    public KMean(int k) {
        this.k = k;
    }

    public Matrix predict(Matrix data) {
        return loss(this.weights, data).flatten().sqrt().mult(-1).exp();
    }

    public void fit(Matrix[] data, Matrix[] labels) {
        expectation(data, labels);
        maximation(data, labels);
    }

    public void initializer(Matrix[] data) {
        this.weights = new Matrix(data[0].rowCount(), 0);
        for (int j = 0; j < this.k; j++) {
            int n = (int) Math.floor(Math.random() * data.length);
            this.weights = this.weights.concat(data[n]);
        }
    }

    private void expectation(Matrix[] data, Matrix[] labels) {
        for (int i = 0; i < data.length; i++) {
            labels[i] = new Matrix(new Vector(this.k).oneHot(loss(this.weights, data[i]).flatten().argmin(0)));
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

    private Matrix loss(Matrix a, Matrix b) {
        return a.copy().sub(b).pow(2.0f);
    }
}
