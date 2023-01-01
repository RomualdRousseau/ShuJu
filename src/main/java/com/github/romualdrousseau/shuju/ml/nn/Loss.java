package com.github.romualdrousseau.shuju.ml.nn;

import com.github.romualdrousseau.shuju.math.deprecated.Tensor2D;

public class Loss {

    public Loss(LossFunc lossFunc) {
        this.lossFunc = lossFunc;
    }

    public Loss loss(Layer output, Tensor2D target) {
        this.value = this.lossFunc.apply(output.output, target);
        this.rate = this.lossFunc.derivate(output.output, target);
        return this;
    }

    public Tensor2D getRate() {
        return this.rate;
    }

    public Tensor2D getValue() {
        return this.value;
    }

    private LossFunc lossFunc;
    private Tensor2D value;
    private Tensor2D rate;
}
