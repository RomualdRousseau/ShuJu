package com.github.romualdrousseau.shuju.ml.nn.layer;

import com.github.romualdrousseau.shuju.json.JSON;
import com.github.romualdrousseau.shuju.json.JSONObject;
import com.github.romualdrousseau.shuju.math.Linalg;
import com.github.romualdrousseau.shuju.math.Matrix;
import com.github.romualdrousseau.shuju.ml.nn.InitializerFunc;
import com.github.romualdrousseau.shuju.ml.nn.Layer;
import com.github.romualdrousseau.shuju.ml.nn.Optimizer;
import com.github.romualdrousseau.shuju.ml.nn.Parameters;

public class Dense extends Layer {

    public Dense(final int inputUnits, final int units, final float bias, final InitializerFunc initializer) {
        super(inputUnits, units, bias);

        this.initializer = initializer;
        this.weights = new Parameters(inputUnits, units);
        this.biases = new Parameters(units);

        this.reset(false);
    }

    public void reset(final boolean parametersOnly) {
        if (parametersOnly) {
            this.weights.M.zero();
            this.weights.V.zero();
            this.biases.M.zero();
            this.biases.V.zero();
        } else {
            this.weights.reset();
            this.biases.reset();
            this.initializer.apply(this.weights.W);
            this.initializer.apply(this.biases.W);
        }
    }

    public Matrix callForward(final Matrix input) {
        return Linalg.xw_plus_b(input, this.weights.W, this.biases.W);
    }

    public void startBackward(final Optimizer optimizer) {
        this.weights.G.zero();
        this.biases.G.zero();
    }

    public Matrix callBackward(final Matrix d_L_d_out) {
        this.weights.G.fma(d_L_d_out, this.lastInput, false, true);
        this.biases.G.fma(d_L_d_out, this.bias);
        return this.weights.W.matmul(d_L_d_out, true, false);
    }

    public void completeBackward(final Optimizer optimizer) {
        this.weights.W.sub(optimizer.computeGradients(this.weights));
        this.biases.W.sub(optimizer.computeGradients(this.biases));
    }

    public void fromJSON(final JSONObject json) {
        this.weights.fromJSON(json.getJSONObject("weights"));
        this.biases.fromJSON(json.getJSONObject("biases"));
        this.bias = json.getFloat("bias");
    }

    public JSONObject toJSON() {
        final JSONObject json = JSON.newJSONObject();
        json.setJSONObject("weights", this.weights.toJSON());
        json.setJSONObject("biases", this.biases.toJSON());
        json.setFloat("bias", this.bias);
        return json;
    }

    private final Parameters weights;
    private final Parameters biases;
    private final InitializerFunc initializer;
}
