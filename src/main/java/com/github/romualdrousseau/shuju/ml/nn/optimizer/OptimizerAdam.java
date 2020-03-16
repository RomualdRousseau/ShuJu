package com.github.romualdrousseau.shuju.ml.nn.optimizer;

import com.github.romualdrousseau.shuju.math.Scalar;
import com.github.romualdrousseau.shuju.math.Matrix;
import com.github.romualdrousseau.shuju.math.MatrixFunction;
import com.github.romualdrousseau.shuju.ml.nn.Model;
import com.github.romualdrousseau.shuju.ml.nn.Optimizer;
import com.github.romualdrousseau.shuju.ml.nn.Parameters;
import com.github.romualdrousseau.shuju.ml.nn.LearningRateScheduler;

public class OptimizerAdam extends Optimizer {
    private float b1;
    private float b2;

    public OptimizerAdam(Model model, float learningRate, LearningRateScheduler scheduler, float b1, float b2) {
        super(model, learningRate, scheduler);
        this.b1 = b1;
        this.b2 = b2;
    }

    public Matrix computeGradients(Parameters p) {
        final float lr = this.learningRate * Scalar.sqrt(1.0f - Scalar.pow(this.b2, this.epoch)) / (1.0f - Scalar.pow(this.b1, this.epoch));

        final MatrixFunction fn = new MatrixFunction() {
            public final float apply(float m, int[] ij, Matrix cache) {
                float v = cache.get(ij[0], ij[1]);
                return lr * m / (Scalar.sqrt(v) + Scalar.EPSILON);
            }
        };

        p.M.mul(this.b1).fma(p.G, 1.0f - this.b1);
        p.V.mul(this.b2).fma(p.G.copy().pow(2.0f), 1.0f - this.b2);

        return p.M.copy().map(fn, p.V);
    }
}
