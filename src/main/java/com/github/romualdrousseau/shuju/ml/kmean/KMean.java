package com.github.romualdrousseau.shuju.ml.kmean;

import com.github.romualdrousseau.shuju.math.Tensor2D;
import com.github.romualdrousseau.shuju.math.TensorFunction;
import com.github.romualdrousseau.shuju.math.Tensor1D;

public class KMean {
    public KMean(int k) {
        this.k = k;
        this.initialized = false;
    }

    public void fit(final Tensor1D[] inputs, final Tensor1D[] targets) {
        if (!this.initialized) {
            this.initializer(inputs);
            this.initialized = true;
        }
        this.expectation(inputs, targets);
        this.maximation(inputs, targets);
    }

    public Tensor1D predict(final Tensor1D input) {
        return this.weights.copy().map(MSE, new Tensor2D(input, false)).flatten(0).get(0).sqrt().mul(-1.0f).exp();
    }

    private void initializer(Tensor1D[] inputs) {
        this.weights = new Tensor2D(inputs[0].rowCount(), 0);
        for (int j = 0; j < this.k; j++) {
            int n = (int) Math.floor(Math.random() * inputs.length);
            this.weights = this.weights.concatenate(inputs[n], 1);
        }
    }

    private void expectation(Tensor1D[] inputs, Tensor1D[] labels) {
        for (int i = 0; i < inputs.length; i++) {
            labels[i] = new Tensor1D(this.k)
                    .oneHot(this.weights.copy().map(MSE, new Tensor2D(inputs[i], false)).flatten(0).argmin(0, 1));
        }
    }

    private void maximation(Tensor1D[] inputs, Tensor1D[] labels) {
        this.weights = new Tensor2D(this.weights.rowCount(), 0);

        for (int j = 0; j < this.k; j++) {
            Tensor1D sum = new Tensor1D(this.weights.rowCount());
            float count = 0;
            for (int i = 0; i < inputs.length; i++) {
                if (labels[i].argmax() == j) {
                    sum.add(inputs[i]);
                    count++;
                }
            }

            this.weights = this.weights.concatenate(sum.div(count), 1);
        }
    }

    private TensorFunction<Tensor2D> MSE = new TensorFunction<Tensor2D>() {
        public float apply(float v, int[] loc, Tensor2D matrix) {
            float a = v - matrix.get(loc[0], 0);
            return a * a;
        }
    };

    private Tensor2D weights;
    private int k = 3;
    private boolean initialized;
}
