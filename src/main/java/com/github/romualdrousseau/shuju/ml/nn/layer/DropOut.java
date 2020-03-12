package com.github.romualdrousseau.shuju.ml.nn.layer;

import com.github.romualdrousseau.shuju.json.JSON;
import com.github.romualdrousseau.shuju.json.JSONObject;
import com.github.romualdrousseau.shuju.math.Matrix;
import com.github.romualdrousseau.shuju.ml.nn.Layer;
import com.github.romualdrousseau.shuju.ml.nn.Optimizer;

public class DropOut extends Layer {

    public DropOut() {
        super(1.0f);
        this.reset(false);
    }

    public void reset(boolean parametersOnly) {
    }

    public Matrix callForward(Matrix input) {
        this.U = new Matrix(input.rowCount(), input.colCount()).randomize().if_lt_then(0, 0, 1);
        return input.mul(this.U);
    }

    public void startBackward(Optimizer optimizer) {
    }

    public Matrix callBackward(Matrix d_L_d_out) {
        return d_L_d_out.mul(this.U);
    }

    public void completeBackward(Optimizer optimizer) {
    }

    public void fromJSON(JSONObject json) {
    }

    public JSONObject toJSON() {
        JSONObject json = JSON.newJSONObject();
        return json;
    }

    // cache
    private Matrix U;
}
