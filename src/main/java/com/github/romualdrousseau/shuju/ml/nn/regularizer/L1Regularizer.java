package com.github.romualdrousseau.shuju.ml.nn.regularizer;

import com.github.romualdrousseau.shuju.math.Matrix;
import com.github.romualdrousseau.shuju.math.Scalar;
import com.github.romualdrousseau.shuju.ml.nn.RegularizerFunc;

public class L1Regularizer implements RegularizerFunc {
    public Matrix apply(Matrix w) {
        Matrix tmp = w.copy().abs().add(Scalar.EPSILON);
        return w.copy().mul(Scalar.LAM).div(tmp);
    }
}
