package com.github.romualdrousseau.shuju.ml.nn.loss;

import com.github.romualdrousseau.shuju.math.Matrix;
import com.github.romualdrousseau.shuju.math.MatrixFunction;
import com.github.romualdrousseau.shuju.ml.nn.LossFunc;

public class Hinge implements LossFunc {

    public Matrix apply(Matrix output, Matrix target) {
        final MatrixFunction<Float, Float> fn = new MatrixFunction<Float, Float>() {
            public final Float apply(Float y, int row, int col, Matrix output) {
                float a = y * output.get(row, col);
                if(a >= 1.0f) {
                    return 0.0f;
                } else {
                    return 1.0f - a;
                }
            }
        };
        return target.copy().map(fn, output);
    }

    public Matrix derivate(Matrix output, Matrix target) {
        final MatrixFunction<Float, Float> fn = new MatrixFunction<Float, Float>() {
            public final Float apply(Float y, int row, int col, Matrix target) {
                float a = y * output.get(row, col);
                if(a >= 1.0f) {
                    return 0.0f;
                } else {
                    return -a;
                }
            }
        };
        return output.copy().map(fn, target);
    }
}
