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

public class OptimizerAdam extends Optimizer {
    private float b1;
    private float b2;

    public OptimizerAdam(Model model, float learningRate, LearningRateScheduler scheduler, float b1, float b2) {
        super(model, learningRate, scheduler);
        this.b1 = b1;
        this.b2 = b2;
    }

    public Tensor2D computeGradients(Parameters2D p) {
        final float lr = this.learningRate * Scalar.sqrt(1.0f - Scalar.pow(this.b2, this.time)) / (1.0f - Scalar.pow(this.b1, this.time));

        p.M.expAvg(p.G, this.b1);
        p.V.expAvg(p.G.copy().pow(2.0f), this.b2);

        return p.M.copy().map(new TensorFunction<Tensor2D>() {
            public final float apply(float m_ij, int[] ij, Tensor2D V) {
                final float v_ij = p.V.get(ij[0], ij[1]);
                return lr * m_ij / Scalar.sqrt(v_ij + Scalar.EPSILON);
            }
        });
    }

    public Tensor3D computeGradients(Parameters3D p) {
        final float lr = this.learningRate * Scalar.sqrt(1.0f - Scalar.pow(this.b2, this.time)) / (1.0f - Scalar.pow(this.b1, this.time));

        p.M.expAvg(p.G, this.b1);
        p.V.expAvg(p.G.copy().pow(2.0f), this.b2);

        return p.M.copy().map(new TensorFunction<Tensor3D>() {
            public final float apply(float m_ij, int[] ijk, Tensor3D V) {
                final float v_ij = p.V.get(ijk[0], ijk[1], ijk[2]);
                return lr * m_ij / Scalar.sqrt(v_ij + Scalar.EPSILON);
            }
        });
    }
}
