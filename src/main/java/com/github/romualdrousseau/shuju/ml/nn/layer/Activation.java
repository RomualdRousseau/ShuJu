package com.github.romualdrousseau.shuju.ml.nn.layer;

import com.github.romualdrousseau.shuju.json.JSON;
import com.github.romualdrousseau.shuju.json.JSONObject;
import com.github.romualdrousseau.shuju.math.Linalg;
import com.github.romualdrousseau.shuju.math.Matrix;
import com.github.romualdrousseau.shuju.ml.nn.ActivationFunc;
import com.github.romualdrousseau.shuju.ml.nn.Layer;
import com.github.romualdrousseau.shuju.ml.nn.Optimizer;
import com.github.romualdrousseau.shuju.ml.nn.activation.Linear;

public class Activation extends Layer {

    public Activation(final int inputUnits, final ActivationFunc activation) {
        super(inputUnits, inputUnits, 1.0f);

        this.activation = (activation == null) ? new Linear() : activation;

        this.reset(false);
    }

    public void reset(final boolean parametersOnly) {
    }

    public Matrix callForward(final Matrix input) {
        return this.activation.apply(input);
    }

    public void startBackward(final Optimizer optimizer) {
    }

    public Matrix callBackward(final Matrix d_L_d_out) {
        return Linalg.a_mul_b(d_L_d_out, this.activation.derivate(this.output));
    }

    public void completeBackward(final Optimizer optimizer) {
    }

    public void fromJSON(final JSONObject json) {
    }

    public JSONObject toJSON() {
        final JSONObject json = JSON.newJSONObject();
        return json;
    }

    private final ActivationFunc activation;
}
