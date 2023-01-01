package com.github.romualdrousseau.shuju.ml.nn.activation;

import com.github.romualdrousseau.shuju.math.Scalar;
import com.github.romualdrousseau.shuju.math.deprecated.Tensor2D;
import com.github.romualdrousseau.shuju.math.deprecated.TensorFunction;
import com.github.romualdrousseau.shuju.ml.nn.ActivationFunc;

public class Tanh implements ActivationFunc {
    public Tensor2D apply(Tensor2D input) {
        final TensorFunction<Tensor2D> fn = new TensorFunction<Tensor2D>() {
            public final float apply(float x, int[] ij, Tensor2D matrix) {
                return Scalar.tanh(x);
            }
        };
        return input.map(fn);
    }

    public Tensor2D derivate(Tensor2D output) {
        final TensorFunction<Tensor2D> fn = new TensorFunction<Tensor2D>() {
            public final float apply(float y, int[] ij, Tensor2D matrix) {
                return 1.0f - y * y;
            }
        };
        return output.map(fn);
    }
}
