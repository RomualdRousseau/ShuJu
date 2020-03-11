package com.github.romualdrousseau.shuju.ml.nn;

import com.github.romualdrousseau.shuju.math.Matrix;

public interface NormalizerFunc {
    Matrix apply(Matrix m);

    Matrix derivate(Matrix y);
}
