package com.github.romualdrousseau.shuju.ml.nn.activation;

import com.github.romualdrousseau.shuju.math.Tensor2D;
import com.github.romualdrousseau.shuju.ml.nn.ActivationFunc;

public class Linear implements ActivationFunc {
    public Tensor2D apply(Tensor2D input) {
        return input;
    }

    public Tensor2D derivate(Tensor2D output) {
        return output.copy().ones();
    }
}
