package com.github.romualdrousseau.shuju.ml.nn.loss;

import com.github.romualdrousseau.shuju.math.Scalar;
import com.github.romualdrousseau.shuju.math.Matrix;
import com.github.romualdrousseau.shuju.math.MatrixFunction;
import com.github.romualdrousseau.shuju.ml.nn.LossFunc;

public class SoftmaxCrossEntropy implements LossFunc {
    public Matrix apply(Matrix output, final Matrix target) {
        final MatrixFunction<Float, Float> fn = new MatrixFunction<Float, Float>() {
            public final Float apply(Float y, int row, int col, Matrix output) {
                float a = output.get(row, col);
                return (a > 0.0f) ? -y * Scalar.log(a) : 0.0f;
            }
        };
        return target.copy().map(fn, output);
    }

    public Matrix derivate(Matrix output, Matrix target) {
        final MatrixFunction<Float, Float> fn = new MatrixFunction<Float, Float>() {
            public final Float apply(Float y, int row, int col, Matrix output) {
                float a = output.get(row, col);
                return (a > 0.0f) ? -y / a : 0.0f;
            }
        };
        return target.copy().map(fn, output);
    }
}
