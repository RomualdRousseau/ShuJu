package com.github.romualdrousseau.shuju.ml.nn.layer;

import com.github.romualdrousseau.shuju.json.JSON;
import com.github.romualdrousseau.shuju.json.JSONObject;
import com.github.romualdrousseau.shuju.math.Tensor2D;
import com.github.romualdrousseau.shuju.ml.nn.InitializerFunc;
import com.github.romualdrousseau.shuju.ml.nn.Layer;
import com.github.romualdrousseau.shuju.ml.nn.Optimizer;
import com.github.romualdrousseau.shuju.ml.nn.Parameters2D;
import com.github.romualdrousseau.shuju.ml.nn.RegularizerFunc;

public class Genetic extends Layer {

    public Genetic(final int inputUnits, final int units, final float bias, final InitializerFunc initializer, final RegularizerFunc regularizer, final float mutationRate) {
        super(inputUnits, units, bias);

        this.initializer = initializer;
        this.regularizer = regularizer;
        this.mutationRate = mutationRate;

        this.kernel = new Parameters2D(inputUnits, units);
        this.biases = new Parameters2D(units);

        this.reset();
    }

    private Genetic(Genetic parent) {
        super(parent);

        this.initializer = parent.initializer;
        this.regularizer = parent.regularizer;
        this.mutationRate = parent.mutationRate;

        this.kernel = parent.kernel.clone();
        this.biases = parent.biases.clone();
    }

    public Layer clone() {
        return new Genetic(this);
    }

    public void reset() {
        this.kernel.reset();
        this.biases.reset();
        this.initializer.apply(this.kernel.W);
    }

    public Tensor2D callForward(final Tensor2D input) {
        return this.kernel.W.matmul(input).add(this.biases.W);
    }

    public void startBackward(final Optimizer optimizer) {
        this.kernel.G.zero();
        this.biases.G.zero();
    }

    public Tensor2D callBackward(final Tensor2D d_L_d_out) {
        return this.kernel.W.matmul(d_L_d_out, true, false);
    }

    public void completeBackward(final Optimizer optimizer) {
        this.kernel.W.mutate(optimizer.learningRate, this.mutationRate);
        this.biases.W.mutate(optimizer.learningRate, this.mutationRate);
    }

    public void fromJSON(final JSONObject json) {
        this.kernel.fromJSON(json.getJSONObject("weights"));
        this.biases.fromJSON(json.getJSONObject("biases"));
        this.bias = json.getFloat("bias");
    }

    public JSONObject toJSON() {
        final JSONObject json = JSON.newJSONObject();
        json.setJSONObject("weights", this.kernel.toJSON());
        json.setJSONObject("biases", this.biases.toJSON());
        json.setFloat("bias", this.bias);
        return json;
    }

    // Hyper-parameters
    private final InitializerFunc initializer;
    private final RegularizerFunc regularizer;
    private final float mutationRate;

    // Parameters
    private final Parameters2D kernel;
    private final Parameters2D biases;
}
