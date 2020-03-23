package com.github.romualdrousseau.shuju.ml.nn.regularizer;

import com.github.romualdrousseau.shuju.math.Tensor2D;
import com.github.romualdrousseau.shuju.math.Tensor3D;
import com.github.romualdrousseau.shuju.math.Scalar;
import com.github.romualdrousseau.shuju.ml.nn.RegularizerFunc;

public class L1Regularizer implements RegularizerFunc {
    public Tensor2D apply(Tensor2D w) {
        Tensor2D tmp = w.copy().abs().add(Scalar.EPSILON);
        return w.copy().mul(Scalar.LAM).div(tmp);
    }

    public Tensor3D apply(Tensor3D w) {
        Tensor3D tmp = w.copy().abs().add(Scalar.EPSILON);
        return w.copy().mul(Scalar.LAM).div(tmp);
    }
}
