package com.github.romualdrousseau.shuju.ml.nn.layer;

import com.github.romualdrousseau.shuju.json.JSON;
import com.github.romualdrousseau.shuju.json.JSONObject;
import com.github.romualdrousseau.shuju.math.Matrix;
import com.github.romualdrousseau.shuju.math.Scalar;
import com.github.romualdrousseau.shuju.ml.nn.ActivationFunc;
import com.github.romualdrousseau.shuju.ml.nn.Layer;
import com.github.romualdrousseau.shuju.ml.nn.Optimizer;
import com.github.romualdrousseau.shuju.ml.nn.activation.Linear;

public class Activation extends Layer {

    public Activation(ActivationFunc activation) {
        super(1.0f);
        this.activation = (activation == null) ? new Linear() : activation;
        this.reset(false);
    }

    public void reset(boolean parametersOnly) {
    }

    public Matrix callForward(Matrix input) {
        return this.activation.apply(input);
    }

    public void startBackward(Optimizer optimizer) {
    }

    public Matrix callBackward(Matrix d_L_d_out) {
        return Scalar.a_mul_b(d_L_d_out, this.activation.derivate(this.output));
    }

    public void completeBackward(Optimizer optimizer) {
    }

    public void fromJSON(JSONObject json) {
    }

    public JSONObject toJSON() {
        JSONObject json = JSON.newJSONObject();
        return json;
    }

    private ActivationFunc activation;
}
