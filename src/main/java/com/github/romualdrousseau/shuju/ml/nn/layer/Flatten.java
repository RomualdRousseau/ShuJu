package com.github.romualdrousseau.shuju.ml.nn.layer;

import com.github.romualdrousseau.shuju.json.JSON;
import com.github.romualdrousseau.shuju.json.JSONObject;
import com.github.romualdrousseau.shuju.math.Matrix;
import com.github.romualdrousseau.shuju.ml.nn.Layer;
import com.github.romualdrousseau.shuju.ml.nn.Optimizer;

public class Flatten extends Layer {

    public Flatten(int inputUnits, int inputChannels) {
        super(1.0f);

        this.inputUnits = inputUnits;
        this.inputChannels = inputChannels;

        this.reset(false);
    }

    public void reset(boolean parametersOnly) {
    }

    public Matrix callForward(Matrix input) {
        return input.reshape(this.inputUnits * this.inputUnits * this.inputChannels, 1, 0);
    }

    public void startBackward(Optimizer optimizer) {
    }

    public Matrix callBackward(Matrix d_L_d_out) {
        return d_L_d_out.reshape(this.inputUnits * this.inputUnits, this.inputChannels, 0);
    }

    public void completeBackward(Optimizer optimizer) {
    }

    public void fromJSON(JSONObject json) {
    }

    public JSONObject toJSON() {
        JSONObject json = JSON.newJSONObject();
        return json;
    }

    private int inputUnits;
    private int inputChannels;
}
