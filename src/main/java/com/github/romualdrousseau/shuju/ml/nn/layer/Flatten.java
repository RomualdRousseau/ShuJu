package com.github.romualdrousseau.shuju.ml.nn.layer;

import com.github.romualdrousseau.shuju.json.JSON;
import com.github.romualdrousseau.shuju.json.JSONObject;
import com.github.romualdrousseau.shuju.math.deprecated.Tensor2D;
import com.github.romualdrousseau.shuju.ml.nn.Layer;
import com.github.romualdrousseau.shuju.ml.nn.Optimizer;

public class Flatten extends Layer {

    public Flatten(int inputUnits, int inputChannels) {
        super(inputUnits, inputChannels, inputUnits * inputUnits * inputChannels, 1, 1.0f);
    }

    private Flatten(Flatten parent) {
        super(parent);
    }

    public Layer clone() {
        return new Flatten(this);
    }

    public void reset() {
    }

    public Tensor2D callForward(Tensor2D input) {
        return input.reshape(this.inputUnits * this.inputUnits * this.inputChannels, 1, 'F');
    }

    public void startBackward(Optimizer optimizer) {
    }

    public Tensor2D callBackward(Tensor2D d_L_d_out) {
        return d_L_d_out.reshape(this.inputUnits * this.inputUnits, this.inputChannels, 'F');
    }

    public void completeBackward(Optimizer optimizer) {
    }

    public void fromJSON(JSONObject json) {
    }

    public JSONObject toJSON() {
        JSONObject json = JSON.newJSONObject();
        return json;
    }
}
