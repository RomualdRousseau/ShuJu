package com.github.romualdrousseau.shuju.ml.nn.loss;

import com.github.romualdrousseau.shuju.math.Scalar;
import com.github.romualdrousseau.shuju.math.Tensor2D;
import com.github.romualdrousseau.shuju.math.TensorFunction;
import com.github.romualdrousseau.shuju.ml.nn.LossFunc;

public class SoftmaxCrossEntropy implements LossFunc {
    public Tensor2D apply(Tensor2D output, final Tensor2D target) {
        final TensorFunction<Tensor2D> fn = new TensorFunction<Tensor2D>() {
            public final float apply(float y, int[] ij, Tensor2D target) {
                float a = output.get(ij[0], ij[1]);
                return (a > 0.0f) ? -y * Scalar.log(a) : 0.0f;
            }
        };
        return target.copy().map(fn, output);
    }

    public Tensor2D derivate(Tensor2D output, Tensor2D target) {
        final TensorFunction<Tensor2D> fn = new TensorFunction<Tensor2D>() {
            public final float apply(float y, int[] ij, Tensor2D output) {
                float a = output.get(ij[0], ij[1]);
                return (a > 0.0f) ? -y / a : 0.0f;
            }
        };
        return target.copy().map(fn, output);
    }
}
