package com.github.romualdrousseau.shuju.ml.nn.optimizer;

import com.github.romualdrousseau.shuju.math.Scalar;
import com.github.romualdrousseau.shuju.math.Matrix;
import com.github.romualdrousseau.shuju.math.MatrixFunction;
import com.github.romualdrousseau.shuju.ml.nn.Model;
import com.github.romualdrousseau.shuju.ml.nn.Optimizer;
import com.github.romualdrousseau.shuju.ml.nn.Parameters;
import com.github.romualdrousseau.shuju.ml.nn.LearningRateScheduler;

public class OptimizerRMSProp extends Optimizer {
    private float b;

    public OptimizerRMSProp(Model model, float learningRate, LearningRateScheduler scheduler, float b) {
        super(model, learningRate, scheduler);
        this.b = b;
    }

    public Matrix computeGradients(Parameters p) {
        final float lr = this.learningRate;

        final MatrixFunction fn = new MatrixFunction() {
            public final float apply(float m, int[] ij, Matrix cache) {
                float v = cache.get(ij[0], ij[1]);
                return lr * m / (Scalar.sqrt(v) + Scalar.EPSILON);
            }
        };

        p.V.mul(this.b).fma(p.G.copy().pow(2.0f), 1.0f - this.b);

        return p.G.copy().map(fn, p.V);
    }
}
