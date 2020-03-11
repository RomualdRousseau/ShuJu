package com.github.romualdrousseau.shuju.ml.nn.layer;

import com.github.romualdrousseau.shuju.json.JSON;
import com.github.romualdrousseau.shuju.json.JSONObject;
import com.github.romualdrousseau.shuju.math.Matrix;
import com.github.romualdrousseau.shuju.ml.nn.Layer;
import com.github.romualdrousseau.shuju.ml.nn.NormalizerFunc;
import com.github.romualdrousseau.shuju.ml.nn.Optimizer;

public class Normalizer extends Layer {

    public Normalizer(NormalizerFunc normalizer) {
        super(1.0f);
        this.normalizer = normalizer;
        this.reset(false);
    }

    public void reset(boolean parametersOnly) {
    }

    public void startBackward(Optimizer optimizer) {
    }

    public void completeBackward(Optimizer optimizer) {
    }

    public Matrix callForward(Matrix input) {
        return this.normalizer.apply(input);
    }

    public Matrix callBackward(Matrix d_L_d_out) {
        return this.normalizer.derivate(d_L_d_out);
    }

    public void fromJSON(JSONObject json) {
    }

    public JSONObject toJSON() {
        JSONObject json = JSON.newJSONObject();
        return json;
    }

    private NormalizerFunc normalizer;
}
