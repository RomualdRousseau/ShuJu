package com.github.romualdrousseau.shuju.ml.nn;

import com.github.romualdrousseau.shuju.math.Vector;
import com.github.romualdrousseau.shuju.math.Matrix;

public class Loss {
    private LossFunc lossFunc;
    private Matrix value;
    private Matrix rate;
    private Layer output;

    public Loss(LossFunc lossFunc) {
        this.lossFunc = lossFunc;
    }

    public Loss loss(Layer output, Vector target) {
        return this.loss(output, new Matrix(target, false));
    }

    public Loss loss(Layer output, Matrix target) {
        this.value = this.lossFunc.apply(output.output, target);
        this.rate = this.lossFunc.derivate(output.output, target);
        this.output = output;
        return this;
    }

    public Matrix getValue() {
        return this.value;
    }

    public Vector getValueAsVector() {
        return this.value.toVector(0, false);
    }

    public Loss backward() {
        Matrix error = this.rate;
        for (Layer layer = this.output; layer.prev != null; layer = layer.prev) {
            if (!layer.frozen) {
                error = layer.callBackward(error);
            }
        }
        return this;
    }
}
