package com.github.romualdrousseau.shuju.ml.nn.loss;

import com.github.romualdrousseau.shuju.math.Matrix;
import com.github.romualdrousseau.shuju.ml.nn.LossFunc;

public class MeanSquaredError implements LossFunc {
    public Matrix apply(Matrix output, Matrix target) {
        return target.copy().sub(output).pow(2.0f).mul(0.5f);
    }

    public Matrix derivate(Matrix output, Matrix target) {
        return output.copy().sub(target);
    }
}
