package com.github.romualdrousseau.shuju.ml.nn.optimizer;

import com.github.romualdrousseau.shuju.math.Scalar;
import com.github.romualdrousseau.shuju.math.Tensor2D;
import com.github.romualdrousseau.shuju.math.Tensor3D;
import com.github.romualdrousseau.shuju.math.TensorFunction;
import com.github.romualdrousseau.shuju.ml.nn.Model;
import com.github.romualdrousseau.shuju.ml.nn.Optimizer;
import com.github.romualdrousseau.shuju.ml.nn.Parameters2D;
import com.github.romualdrousseau.shuju.ml.nn.Parameters3D;
import com.github.romualdrousseau.shuju.ml.nn.LearningRateScheduler;

public class OptimizerRMSProp extends Optimizer {
    private float b;

    public OptimizerRMSProp(Model model, float learningRate, LearningRateScheduler scheduler, float b) {
        super(model, learningRate, scheduler);
        this.b = b;
    }

    public Tensor2D computeGradients(Parameters2D p) {
        final float lr = this.learningRate;

        final TensorFunction<Tensor2D> fn = new TensorFunction<Tensor2D>() {
            public final float apply(float m, int[] ij, Tensor2D cache) {
                float v = cache.get(ij[0], ij[1]);
                return lr * m / (Scalar.sqrt(v) + Scalar.EPSILON);
            }
        };

        p.V.mul(this.b).fma(p.G.copy().pow(2.0f), 1.0f - this.b);

        return p.G.copy().map(fn, p.V);
    }

    public Tensor3D computeGradients(Parameters3D p) {
        final float lr = this.learningRate;

        final TensorFunction<Tensor3D> fn = new TensorFunction<Tensor3D>() {
            public final float apply(float m, int[] ijk, Tensor3D cache) {
                float v = cache.get(ijk[0], ijk[1], ijk[2]);
                return lr * m / (Scalar.sqrt(v) + Scalar.EPSILON);
            }
        };

        p.V.mul(this.b).fma(p.G.copy().pow(2.0f), 1.0f - this.b);

        return p.G.copy().map(fn, p.V);
    }
}
