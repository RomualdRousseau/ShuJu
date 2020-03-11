package com.github.romualdrousseau.shuju.ml.nn.normalizer;

import com.github.romualdrousseau.shuju.math.Matrix;
import com.github.romualdrousseau.shuju.ml.nn.NormalizerFunc;

public class BatchNormalizer implements NormalizerFunc {
    public Matrix apply(Matrix x) {
        return x.batchNorm(1.0f, 0.0f, 0);
    }

    public Matrix derivate(Matrix y) {
        return y;
    }
}
