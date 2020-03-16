package com.github.romualdrousseau.shuju.ml.nn.loss;

import com.github.romualdrousseau.shuju.math.Scalar;
import com.github.romualdrousseau.shuju.math.Matrix;
import com.github.romualdrousseau.shuju.math.MatrixFunction;
import com.github.romualdrousseau.shuju.ml.nn.LossFunc;

public class Huber implements LossFunc {
    private final float alpha = 1.0f;

    public Matrix apply(Matrix output, Matrix target) {
        final MatrixFunction fn = new MatrixFunction() {
            public final float apply(float y, int[] ij, Matrix output) {
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

    public Matrix derivate(Matrix output, Matrix target) {
        final MatrixFunction fn = new MatrixFunction() {
            public final float apply(float y, int[] ij, Matrix target) {
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
