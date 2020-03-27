package com.github.romualdrousseau.shuju.ml.nn.layer;

import com.github.romualdrousseau.shuju.json.JSON;
import com.github.romualdrousseau.shuju.json.JSONObject;
import com.github.romualdrousseau.shuju.math.Tensor2D;
import com.github.romualdrousseau.shuju.ml.nn.Helper;
import com.github.romualdrousseau.shuju.ml.nn.Layer;
import com.github.romualdrousseau.shuju.ml.nn.Optimizer;

public class MaxPooling2D extends Layer {

    public MaxPooling2D(final int inputUnits, final int inputChannels, final int size) {
        super(inputUnits, inputChannels, inputUnits / size, inputChannels, 1.0f);
        assert (inputUnits == this.units * size);

        this.size = size;

        this.reset(false);
    }

    public void reset(final boolean parametersOnly) {
    }

    public Tensor2D callForward(final Tensor2D input) {
        final Tensor2D output = new Tensor2D(this.inputChannels, this.units * this.units);
        for (int k = 0; k < this.inputChannels; k++) {
            final Tensor2D input_k = input.slice(0, k, -1, 1).reshape(this.inputUnits, -1);
            output.replace(k, 0, Helper.Img2Conv(input_k, this.size, this.size).max(0));
        }
        return output.transpose();
    }

    public void startBackward(final Optimizer optimizer) {
    }

    public Tensor2D callBackward(final Tensor2D d_L_d_out) {
        final Tensor2D d_L_d_in = new Tensor2D(this.inputChannels, this.inputUnits * this.inputUnits);
        for (int k = 0; k < this.inputChannels; k++) {
            final Tensor2D d_L_d_out_k = d_L_d_out.slice(0, k, -1, 1).reshape(this.units, -1);
            final Tensor2D input_k = this.lastInput.slice(0, k, -1, 1).reshape(this.inputUnits, -1);
            final Tensor2D output_k = this.output.slice(0, k, -1, 1).reshape(this.units, -1);
            d_L_d_in.replace(k, 0, Helper.expand_minmax(output_k, input_k, d_L_d_out_k));
        }
        return d_L_d_in.transpose();
    }

    public void completeBackward(final Optimizer optimizer) {
    }

    public void fromJSON(final JSONObject json) {
    }

    public JSONObject toJSON() {
        final JSONObject json = JSON.newJSONObject();
        return json;
    }

    private final int size;
}
