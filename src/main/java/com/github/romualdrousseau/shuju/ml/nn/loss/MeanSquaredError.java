package com.github.romualdrousseau.shuju.ml.nn.loss;

import com.github.romualdrousseau.shuju.math.deprecated.Tensor2D;
import com.github.romualdrousseau.shuju.ml.nn.LossFunc;

public class MeanSquaredError implements LossFunc {
    public Tensor2D apply(Tensor2D output, Tensor2D target) {
        return target.copy().sub(output).pow(2.0f).mul(0.5f);
    }

    public Tensor2D derivate(Tensor2D output, Tensor2D target) {
        return output.copy().sub(target);
    }
}
