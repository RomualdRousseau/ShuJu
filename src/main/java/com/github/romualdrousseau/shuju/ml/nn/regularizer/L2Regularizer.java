package com.github.romualdrousseau.shuju.ml.nn.regularizer;

import com.github.romualdrousseau.shuju.math.Tensor2D;
import com.github.romualdrousseau.shuju.math.Tensor3D;
import com.github.romualdrousseau.shuju.math.Scalar;
import com.github.romualdrousseau.shuju.ml.nn.RegularizerFunc;

public class L2Regularizer implements RegularizerFunc {
    public Tensor2D apply(Tensor2D w) {
        return w.copy().mul(Scalar.LAM);
    }

    public Tensor3D apply(Tensor3D w) {
        return w.copy().mul(Scalar.LAM);
    }
}
