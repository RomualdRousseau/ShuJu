package com.github.romualdrousseau.shuju.ml.nn.normalizer;

import com.github.romualdrousseau.shuju.math.Matrix;
import com.github.romualdrousseau.shuju.ml.nn.NormalizerFunc;

public class L2Normalizer implements NormalizerFunc {
    public void apply(Matrix matrix) {
        matrix.l2Norm();
    }
}
