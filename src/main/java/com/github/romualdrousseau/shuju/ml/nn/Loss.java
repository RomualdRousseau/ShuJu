package com.github.romualdrousseau.shuju.ml.nn;

import com.github.romualdrousseau.shuju.math.Vector;
import com.github.romualdrousseau.shuju.math.Matrix;

public class Loss {

    public Loss(LossFunc lossFunc) {
        this.lossFunc = lossFunc;
    }

    public Loss loss(Layer output, Vector target) {
        return this.loss(output, new Matrix(target, false));
    }

    public Loss loss(Layer output, Matrix target) {
        this.value = this.lossFunc.apply(output.output, target);
        this.rate = this.lossFunc.derivate(output.output, target);
        return this;
    }

    public Matrix getRate() {
        return this.rate;
    }

    public Matrix getValue() {
        return this.value;
    }

    public Vector getValueAsVector() {
        return this.value.toVector(0, false);
    }

    private LossFunc lossFunc;
    private Matrix value;
    private Matrix rate;
}
