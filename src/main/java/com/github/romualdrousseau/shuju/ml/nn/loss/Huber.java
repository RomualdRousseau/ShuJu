package com.github.romualdrousseau.shuju.ml.nn.loss;

import com.github.romualdrousseau.shuju.math.Scalar;
import com.github.romualdrousseau.shuju.math.deprecated.Tensor2D;
import com.github.romualdrousseau.shuju.math.deprecated.TensorFunction;
import com.github.romualdrousseau.shuju.ml.nn.LossFunc;

public class Huber implements LossFunc {
    private final float alpha = 1.0f;

    public Tensor2D apply(Tensor2D output, Tensor2D target) {
        final TensorFunction<Tensor2D> fn = new TensorFunction<Tensor2D>() {
            public final float apply(float y, int[] ij, Tensor2D output) {
                float a = y - output.get(ij[0], ij[1]);
                if (Scalar.abs(a) <= alpha) {
                    return 0.5f * a * a;
                } else {
                    return alpha * (Scalar.abs(a) - 0.5f * alpha);
                }
            }
        };
        return target.copy().map(fn, output);
    }

    public Tensor2D derivate(Tensor2D output, Tensor2D target) {
        final TensorFunction<Tensor2D> fn = new TensorFunction<Tensor2D>() {
            public final float apply(float y, int[] ij, Tensor2D target) {
                float a = y - target.get(ij[0], ij[1]);
                if (a < -alpha) {
                    return -alpha;
                } else if (a <= alpha) {
                    return a;
                } else {
                    return alpha;
                }
            }
        };
        return output.copy().map(fn, target);
    }
}
