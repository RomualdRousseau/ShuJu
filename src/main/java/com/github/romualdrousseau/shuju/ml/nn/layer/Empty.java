package com.github.romualdrousseau.shuju.ml.nn.layer;

import com.github.romualdrousseau.shuju.json.JSON;
import com.github.romualdrousseau.shuju.json.JSONObject;
import com.github.romualdrousseau.shuju.math.Matrix;
import com.github.romualdrousseau.shuju.ml.nn.Layer;
import com.github.romualdrousseau.shuju.ml.nn.Optimizer;

public class Empty extends Layer {

    public Empty(float bias) {
        super(bias);
        this.reset(false);
    }

    public void reset(boolean parametersOnly) {
    }

    public void startBackward(Optimizer optimizer) {
    }

    public void completeBackward(Optimizer optimizer) {
    }

    public Matrix callForward(Matrix input) {
        return input;
    }

    public Matrix callBackward(Matrix error) {
        // final Matrix last_input = this.prev.output;
        // final float last_bias = this.prev.bias;
        return error;
    }

    public void fromJSON(JSONObject json) {
    }

    public JSONObject toJSON() {
        JSONObject json = JSON.newJSONObject();
        return json;
    }
}
