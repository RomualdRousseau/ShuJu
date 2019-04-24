package com.github.romualdrousseau.shuju.ml.nn.loss;

import com.github.romualdrousseau.shuju.math.Scalar;
import com.github.romualdrousseau.shuju.math.Matrix;
import com.github.romualdrousseau.shuju.math.MatrixFunction;
import com.github.romualdrousseau.shuju.ml.nn.LossFunc;

public class Huber implements LossFunc {
    private final float alpha = 1.0f;

    public Matrix apply(Matrix output, Matrix target) {
        final MatrixFunction<Float, Float> fn = new MatrixFunction<Float, Float>() {
            public final Float apply(Float y, int row, int col, Matrix output) {
                float a = y - output.get(row, col);
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
        final MatrixFunction<Float, Float> fn = new MatrixFunction<Float, Float>() {
            public final Float apply(Float y, int row, int col, Matrix target) {
                float a = y - target.get(row, col);
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
