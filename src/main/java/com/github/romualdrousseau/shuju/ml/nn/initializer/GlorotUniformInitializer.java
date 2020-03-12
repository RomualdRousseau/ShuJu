package com.github.romualdrousseau.shuju.ml.nn.initializer;

import com.github.romualdrousseau.shuju.math.Scalar;
import com.github.romualdrousseau.shuju.math.Matrix;
import com.github.romualdrousseau.shuju.ml.nn.InitializerFunc;

public class GlorotUniformInitializer implements InitializerFunc {
    public Matrix apply(Matrix matrix) {
        return matrix.randomize(Scalar.sqrt(6.0f / Scalar.sqrt(matrix.rowCount() + matrix.colCount())));
    }
}
