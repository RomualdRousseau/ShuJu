package com.github.romualdrousseau.shuju.ml.nn.activation;

import com.github.romualdrousseau.shuju.math.Tensor2D;
import com.github.romualdrousseau.shuju.math.TensorFunction;
import com.github.romualdrousseau.shuju.ml.nn.ActivationFunc;

public class Relu implements ActivationFunc {
    public Tensor2D apply(Tensor2D input) {
        final TensorFunction<Tensor2D> fn = new TensorFunction<Tensor2D>() {
            public final float apply(float x, int[] ij, Tensor2D matrix) {
                return (x <= 0.0f) ? 0.0f : x;
            }
        };
        return input.map(fn);
    }

    public Tensor2D derivate(Tensor2D output) {
        final TensorFunction<Tensor2D> fn = new TensorFunction<Tensor2D>() {
            public final float apply(float y, int[] ij, Tensor2D matrix) {
                return (y <= 0.0f) ? 0.0f : 1.0f;
            }
        };
        return output.copy().map(fn);
    }
}
