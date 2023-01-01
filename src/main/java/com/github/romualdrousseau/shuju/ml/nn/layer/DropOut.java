package com.github.romualdrousseau.shuju.ml.nn.layer;

import com.github.romualdrousseau.shuju.json.JSON;
import com.github.romualdrousseau.shuju.json.JSONObject;
import com.github.romualdrousseau.shuju.math.Scalar;
import com.github.romualdrousseau.shuju.math.deprecated.Tensor2D;
import com.github.romualdrousseau.shuju.ml.nn.Layer;
import com.github.romualdrousseau.shuju.ml.nn.Optimizer;

public class DropOut extends Layer {

    public DropOut(int inputUnits, final int inputChannels, final float rate) {
        super(inputUnits, inputChannels, inputUnits, inputChannels, 1.0f);

        this.rate = Scalar.map(rate, 0.0f, 1.0f, -0.5f, 0.5f);
        this.scale = (rate > 0.0f) ? 1.0f / (1.0f - rate) : 0.0f;
    }

    private DropOut(DropOut parent) {
        super(parent);

        this.rate = parent.rate;
        this.scale = parent.scale;
    }

    public Layer clone() {
        return new DropOut(this);
    }

    public void reset() {
    }

    public Tensor2D callForward(final Tensor2D input) {
        if (this.training) {
            this.noise = new Tensor2D(input.shape[0], input.shape[1]).randomize(0.5f).if_lt_then(this.rate, 0.0f, this.scale);
            return input.copy().mul(this.noise);
        } else {
            return input;
        }
    }

    public void startBackward(final Optimizer optimizer) {
    }

    public Tensor2D callBackward(final Tensor2D d_L_d_out) {
        return d_L_d_out.copy().mul(this.noise);
    }

    public void completeBackward(final Optimizer optimizer) {
    }

    public void fromJSON(final JSONObject json) {
    }

    public JSONObject toJSON() {
        JSONObject json = JSON.newJSONObject();
        return json;
    }

    // Hyper-parameters
    private float rate;
    private float scale;

    // Cache
    private Tensor2D noise;
}
