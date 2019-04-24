package com.github.romualdrousseau.shuju.ml.nn;

import com.github.romualdrousseau.shuju.math.Matrix;

public interface ActivationFunc {
    Matrix apply(Matrix x);

    Matrix derivate(Matrix y);
}
