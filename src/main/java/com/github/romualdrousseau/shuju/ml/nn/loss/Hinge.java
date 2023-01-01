package com.github.romualdrousseau.shuju.ml.nn.loss;

import com.github.romualdrousseau.shuju.math.deprecated.Tensor2D;
import com.github.romualdrousseau.shuju.math.deprecated.TensorFunction;
import com.github.romualdrousseau.shuju.ml.nn.LossFunc;

public class Hinge implements LossFunc {

    public Tensor2D apply(Tensor2D output, Tensor2D target) {
        final TensorFunction<Tensor2D> fn = new TensorFunction<Tensor2D>() {
            public final float apply(float y, int[] ij, Tensor2D target) {
                float a = y * target.get(ij[0], ij[1]);
                if(a >= 1.0f) {
                    return 0.0f;
                } else {
                    return 1.0f - a;
                }
            }
        };
        return output.copy().map(fn, target);
    }

    public Tensor2D derivate(Tensor2D output, Tensor2D target) {
        final TensorFunction<Tensor2D> fn = new TensorFunction<Tensor2D>() {
            public final float apply(float y, int[] ij, Tensor2D target) {
                float a = y * target.get(ij[0], ij[1]);
                if(a >= 1.0f) {
                    return 0.0f;
                } else {
                    return -target.get(ij[0], ij[1]);
                }
            }
        };
        return output.copy().map(fn, target);
    }
}
