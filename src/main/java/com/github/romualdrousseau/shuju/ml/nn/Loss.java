package com.github.romualdrousseau.shuju.ml.nn;

import com.github.romualdrousseau.shuju.math.Scalar;
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
        return this.loss(output, new Matrix(target));
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

    public Loss backward() {
        Matrix error = this.rate;
        for (Layer layer = this.output; layer.prev != null; layer = layer.prev) {
            error = Scalar.a_mul_b(error, layer.activation.derivate(layer.output));
            layer.weights.G.fma(error, layer.prev.output, false, true);
            layer.biases.G.fma(error, layer.prev.bias);
            error = layer.weights.W.transform(error, true, false);
        }
        return this;
    }
}
