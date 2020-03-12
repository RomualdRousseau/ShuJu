package com.github.romualdrousseau.shuju.ml.nn.layer;

import com.github.romualdrousseau.shuju.json.JSON;
import com.github.romualdrousseau.shuju.json.JSONObject;
import com.github.romualdrousseau.shuju.math.Helper;
import com.github.romualdrousseau.shuju.math.Matrix;
import com.github.romualdrousseau.shuju.ml.nn.Layer;
import com.github.romualdrousseau.shuju.ml.nn.Optimizer;

public class MaxPooling2D extends Layer {

    public MaxPooling2D(final int inputUnits, final int inputChannels, final int size) {
        super(1.0f);
        this.inputUnits = inputUnits;
        this.inputChannels = inputChannels;
        this.size = size;
        this.units = inputUnits / size;
        assert (this.units * size == inputUnits);
        this.reset(false);
    }

    public void reset(final boolean parametersOnly) {
    }

    public Matrix callForward(final Matrix input) {
        final Matrix output = new Matrix(this.inputChannels, this.units * this.units);
        for (int k = 0; k < this.inputChannels; k++) {
            final Matrix input_k = input.extract(k, 1).reshape(this.inputUnits, this.inputUnits, 1);
            output.replace(k, Helper.im2col(input_k, this.size, this.size).max(0), 0);
        }
        return output.transpose();
    }

    public void startBackward(final Optimizer optimizer) {
    }

    public Matrix callBackward(final Matrix d_L_d_out) {
        final Matrix d_L_d_in = new Matrix(this.inputChannels, this.inputUnits * this.inputUnits);
        for (int k = 0; k < this.inputChannels; k++) {
            final Matrix input_k = this.lastInput.extract(k, 1).reshape(this.inputUnits, this.inputUnits, 1);
            final Matrix output_k = this.output.extract(k, 1).reshape(this.units, this.units, 1);
            final Matrix d_L_d_out_k = d_L_d_out.extract(k, 1).reshape(this.units, this.units, 1);
            d_L_d_in.replace(k, Helper.expand_minmax(output_k, input_k, d_L_d_out_k), 0);
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

    private final int inputUnits;
    private final int inputChannels;
    private final int size;
    private final int units;
}
