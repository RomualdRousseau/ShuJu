package com.github.romualdrousseau.shuju.ml.nn.activation;

import com.github.romualdrousseau.shuju.math.Matrix;
import com.github.romualdrousseau.shuju.ml.nn.ActivationFunc;

public class Linear implements ActivationFunc {
    public Matrix apply(Matrix input) {
        return input;
    }

    public Matrix derivate(Matrix output) {
        return output.copy().ones();
    }
}
