package com.github.romualdrousseau.shuju.ml.nn;

import com.github.romualdrousseau.shuju.math.Tensor2D;

public interface ActivationFunc {
    Tensor2D apply(Tensor2D x);

    Tensor2D derivate(Tensor2D y);
}
