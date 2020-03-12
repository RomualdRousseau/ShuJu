package com.github.romualdrousseau.shuju.ml.nn.layer;

import com.github.romualdrousseau.shuju.json.JSON;
import com.github.romualdrousseau.shuju.json.JSONObject;
import com.github.romualdrousseau.shuju.math.Helper;
import com.github.romualdrousseau.shuju.math.Matrix;
import com.github.romualdrousseau.shuju.ml.nn.InitializerFunc;
import com.github.romualdrousseau.shuju.ml.nn.Layer;
import com.github.romualdrousseau.shuju.ml.nn.Optimizer;
import com.github.romualdrousseau.shuju.ml.nn.Parameters;

public class Conv2D extends Layer {

    public Conv2D(final int inputUnits, final int inputChannels, final int filters, final int channels,
            final float bias, final InitializerFunc initializer) {
        super(bias);

        assert (inputChannels == 1) : "Multiple input channels not supported";

        this.inputUnits = inputUnits;
        this.units = this.inputUnits - filters + 1;
        this.initializer = initializer;
        this.filters = new Parameters(filters * filters, channels);
        this.biases = new Parameters(1, channels);

        this.reset(false);
    }

    public void reset(final boolean parametersOnly) {
        if (parametersOnly) {
            this.filters.M.zero();
            this.filters.V.zero();
            this.biases.M.zero();
            this.biases.V.zero();
        } else {
            this.filters.reset();
            this.biases.reset();
            this.initializer.apply(this.filters.W).div(this.inputUnits - this.units + 1);
            this.initializer.apply(this.biases.W);
        }
    }

    public Matrix callForward(final Matrix input) {
        final Matrix input_reshaped = input.reshape(this.inputUnits, this.inputUnits, 1);
        final Matrix input_norm = Helper.im2col(input_reshaped, this.inputUnits - this.units + 1, 1);
        return Helper.xw_plus_b(input_norm, this.filters.W, this.biases.W.toVector(0, false)).transpose();
    }

    public void startBackward(final Optimizer optimizer) {
        this.filters.G.zero();
        this.biases.G.zero();
    }

    public Matrix callBackward(final Matrix d_L_d_out) {
        final int n_filter = this.inputUnits - this.units + 1;
        final Matrix input_reshaped = this.lastInput.reshape(this.inputUnits, this.inputUnits, 1);
        final Matrix input_norm = Helper.im2col(input_reshaped, this.inputUnits - this.units + 1, 1).transpose();
        final Matrix d_L_d_out_reshaped = d_L_d_out.transpose();
        this.filters.G.fma(d_L_d_out_reshaped, input_norm);
        this.biases.G.add(d_L_d_out_reshaped.flatten(1).mul(this.bias));
        return Helper.col2im(this.filters.W.transpose().matmul(d_L_d_out_reshaped), this.inputUnits, this.inputUnits, n_filter, 1);
    }

    public void completeBackward(final Optimizer optimizer) {
        this.filters.W.sub(optimizer.computeGradients(this.filters));
        this.biases.W.sub(optimizer.computeGradients(this.biases));
    }

    public void fromJSON(final JSONObject json) {
        this.filters.fromJSON(json.getJSONObject("filters"));
        this.biases.fromJSON(json.getJSONObject("biases"));
        this.bias = json.getFloat("bias");
    }

    public JSONObject toJSON() {
        final JSONObject json = JSON.newJSONObject();
        json.setJSONObject("filters", this.filters.toJSON());
        json.setJSONObject("biases", this.biases.toJSON());
        json.setFloat("bias", this.bias);
        return json;
    }

    private final int inputUnits;
    private final int units;
    private final InitializerFunc initializer;

    private final Parameters filters;
    private final Parameters biases;
}
