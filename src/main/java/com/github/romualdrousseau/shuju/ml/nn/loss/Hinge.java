package com.github.romualdrousseau.shuju.ml.nn.loss;

import com.github.romualdrousseau.shuju.math.Matrix;
import com.github.romualdrousseau.shuju.math.MatrixFunction;
import com.github.romualdrousseau.shuju.ml.nn.LossFunc;

public class Hinge implements LossFunc {

    public Matrix apply(Matrix output, Matrix target) {
        final MatrixFunction fn = new MatrixFunction() {
            public final float apply(float y, int[] ij, Matrix target) {
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

    public Matrix derivate(Matrix output, Matrix target) {
        final MatrixFunction fn = new MatrixFunction() {
            public final float apply(float y, int[] ij, Matrix target) {
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
