package com.github.romualdrousseau.shuju.ml.nn.loss;

import com.github.romualdrousseau.shuju.math.Scalar;
import com.github.romualdrousseau.shuju.math.Matrix;
import com.github.romualdrousseau.shuju.math.MatrixFunction;
import com.github.romualdrousseau.shuju.ml.nn.LossFunc;

public class SoftmaxCrossEntropy implements LossFunc {
    public Matrix apply(Matrix output, final Matrix target) {
        final MatrixFunction fn = new MatrixFunction() {
            public final float apply(float y, int[] ij, Matrix target) {
                float a = output.get(ij[0], ij[1]);
                return (a > 0.0f) ? -y * Scalar.log(a) : 0.0f;
            }
        };
        return target.copy().map(fn, output);
    }

    public Matrix derivate(Matrix output, Matrix target) {
        final MatrixFunction fn = new MatrixFunction() {
            public final float apply(float y, int[] ij, Matrix output) {
                float a = output.get(ij[0], ij[1]);
                return (a > 0.0f) ? -y / a : 0.0f;
            }
        };
        return target.copy().map(fn, output);
    }
}
