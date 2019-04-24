package com.github.romualdrousseau.shuju.ml.nn.initializer;

import com.github.romualdrousseau.shuju.math.Scalar;
import com.github.romualdrousseau.shuju.math.Matrix;
import com.github.romualdrousseau.shuju.ml.nn.InitializerFunc;

public class HeUniformInitializer implements InitializerFunc {
    public void apply(Matrix matrix) {
        matrix.randomize(Scalar.sqrt(3.0f / Scalar.sqrt(matrix.rowCount())));
    }
}
