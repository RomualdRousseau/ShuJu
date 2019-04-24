package com.github.romualdrousseau.shuju.ml.nn;

import com.github.romualdrousseau.shuju.math.Matrix;

public interface LossFunc {
    Matrix apply(Matrix output, Matrix target);

    Matrix derivate(Matrix output, Matrix target);
}
