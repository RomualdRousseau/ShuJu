package com.github.romualdrousseau.shuju.ml.nn;

import com.github.romualdrousseau.shuju.math.deprecated.Tensor2D;

public interface LossFunc {
    Tensor2D apply(Tensor2D output, Tensor2D target);

    Tensor2D derivate(Tensor2D output, Tensor2D target);
}
