package com.github.romualdrousseau.shuju.ml.nn.layer;

import com.github.romualdrousseau.shuju.json.JSON;
import com.github.romualdrousseau.shuju.json.JSONObject;
import com.github.romualdrousseau.shuju.math.Matrix;
import com.github.romualdrousseau.shuju.math.Scalar;
import com.github.romualdrousseau.shuju.ml.nn.Layer;
import com.github.romualdrousseau.shuju.ml.nn.Optimizer;

public class DropOut extends Layer {

    public DropOut(int inputUnits, final float rate) {
        super(inputUnits, inputUnits, 1.0f);

        this.rate = Scalar.map(rate, 0.0f, 1.0f, -1.0f, 1.0f);
        this.U = new Matrix(this.inputUnits, this.inputChannels).ones();
        this.reset(false);
    }

    public void reset(final boolean parametersOnly) {
    }

    public Matrix callForward(final Matrix input) {
        if (this.training) {
            this.U.randomize().if_lt_then(this.rate, 0.0f, 1.0f);
            return input.mul(this.U);
        } else {
            return input;
        }
    }

    public void startBackward(final Optimizer optimizer) {
    }

    public Matrix callBackward(final Matrix d_L_d_out) {
        return d_L_d_out.mul(this.U);
    }

    public void completeBackward(final Optimizer optimizer) {
    }

    public void fromJSON(final JSONObject json) {
    }

    public JSONObject toJSON() {
        JSONObject json = JSON.newJSONObject();
        return json;
    }

    private float rate;
    // cache
    private Matrix U;
}
