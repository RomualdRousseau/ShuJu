package com.github.romualdrousseau.shuju.ml.nn;

import com.github.romualdrousseau.shuju.math.Matrix;

public interface InitializerFunc {
    Matrix apply(Matrix m);
}
