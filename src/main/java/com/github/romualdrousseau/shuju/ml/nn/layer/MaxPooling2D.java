package com.github.romualdrousseau.shuju.ml.nn.layer;

import com.github.romualdrousseau.shuju.json.JSON;
import com.github.romualdrousseau.shuju.json.JSONObject;
import com.github.romualdrousseau.shuju.math.Tensor2D;
import com.github.romualdrousseau.shuju.math.Tensor3D;
import com.github.romualdrousseau.shuju.ml.nn.Helper;
import com.github.romualdrousseau.shuju.ml.nn.Layer;
import com.github.romualdrousseau.shuju.ml.nn.Optimizer;

public class MaxPooling2D extends Layer {

    public MaxPooling2D(final int inputUnits, final int inputChannels, final int size) {
        super(inputUnits, inputChannels, inputUnits / size, inputChannels, 1.0f);
        assert (inputUnits == this.units * size);

        this.size = size;
    }

    private MaxPooling2D(MaxPooling2D parent) {
        super(parent);

        this.size = parent.size;
    }

    public Layer clone() {
        return new MaxPooling2D(this);
    }

    public void reset() {
    }

    public Tensor2D callForward(final Tensor2D input) {
        final Tensor3D input_res = input.transpose().reshape(this.inputChannels, this.inputUnits, -1);
        final Tensor3D output = Helper.Im2Col(input_res, this.size, this.size, 0).max(1);
        return output.reshape(this.channels, -1).transpose();
    }

    public void startBackward(final Optimizer optimizer) {
    }

    public Tensor2D callBackward(final Tensor2D d_L_d_out) {
        final Tensor3D input_res = this.lastInput.transpose().reshape(this.inputChannels, this.inputUnits, -1);
        final Tensor3D output_res = this.output.transpose().reshape(this.inputChannels, this.units, -1);
        final Tensor3D d_L_d_out_res = d_L_d_out.transpose().reshape(this.inputChannels, this.units, -1);
        final Tensor3D d_L_d_in = Helper.expandMinMax(output_res, input_res, d_L_d_out_res);
        return d_L_d_in.reshape(this.inputChannels, -1).transpose();
    }

    public void completeBackward(final Optimizer optimizer) {
    }

    public void fromJSON(final JSONObject json) {
    }

    public JSONObject toJSON() {
        final JSONObject json = JSON.newJSONObject();
        return json;
    }

    // Hyper-parameters
    private final int size;
}
