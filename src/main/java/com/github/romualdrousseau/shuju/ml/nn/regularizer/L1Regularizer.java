package com.github.romualdrousseau.shuju.ml.nn.regularizer;

import com.github.romualdrousseau.shuju.math.Scalar;
import com.github.romualdrousseau.shuju.math.deprecated.Tensor2D;
import com.github.romualdrousseau.shuju.math.deprecated.Tensor3D;
import com.github.romualdrousseau.shuju.ml.nn.RegularizerFunc;

public class L1Regularizer implements RegularizerFunc {
    public Tensor2D apply(Tensor2D w) {
        return w.copy().if_lt_then(0.0f, -Scalar.LAM, Scalar.LAM);
    }

    public Tensor3D apply(Tensor3D w) {
        return w.copy().if_lt_then(0.0f, -Scalar.LAM, Scalar.LAM);
    }
}
