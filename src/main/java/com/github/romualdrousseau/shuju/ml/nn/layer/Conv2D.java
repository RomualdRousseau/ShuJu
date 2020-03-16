package com.github.romualdrousseau.shuju.ml.nn.layer;

import com.github.romualdrousseau.shuju.json.JSON;
import com.github.romualdrousseau.shuju.json.JSONObject;
import com.github.romualdrousseau.shuju.math.Matrix;
import com.github.romualdrousseau.shuju.ml.nn.Helper;
import com.github.romualdrousseau.shuju.ml.nn.InitializerFunc;
import com.github.romualdrousseau.shuju.ml.nn.Layer;
import com.github.romualdrousseau.shuju.ml.nn.Optimizer;
import com.github.romualdrousseau.shuju.ml.nn.Parameters;
import com.github.romualdrousseau.shuju.ml.nn.RegularizerFunc;

public class Conv2D extends Layer {

    public Conv2D(final int inputUnits, final int inputChannels, final int filters, final int channels,
            final float bias, final InitializerFunc initializer, final RegularizerFunc regularizer) {
        super(inputUnits, inputChannels, inputUnits - filters + 1, inputChannels * channels, bias);

        this.initializer = initializer;
        this.regularizer = regularizer;
        this.filters = new Parameters(filters * filters, inputChannels * channels);
        this.biases = new Parameters(1, inputChannels * channels);

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
        final int n_filters = this.inputUnits - this.units + 1;
        final int c_filters = this.channels / this.inputChannels;
        final Matrix[] filters_res = Helper.reshape(this.filters.W, this.inputChannels, c_filters, n_filters * n_filters);
        final Matrix[] input_res_T = Helper.reshape(input.transpose(), this.inputChannels, this.inputUnits, this.inputUnits);
        final Matrix[] input_col = Helper.Img2Conv(input_res_T, n_filters, 1, false);
        final Matrix output = Helper.reshape(Helper.a_mul_b(input_col, filters_res), this.channels, this.units * this.units);
        return output.add(this.biases.W.toVector(0, false), 1).transpose();
    }

    public void startBackward(final Optimizer optimizer) {
        this.filters.G.zero();
        this.biases.G.zero();
    }

    public Matrix callBackward(final Matrix d_L_d_out) {
        final int n_filters = this.inputUnits - this.units + 1;
        final int c_filters = this.channels / this.inputChannels;
        final Matrix[] filters_res_T = Helper.reshape(this.filters.W.transpose(), this.inputChannels, c_filters, n_filters * n_filters);
        final Matrix[] input_res_T = Helper.reshape(this.lastInput.transpose(), this.inputChannels, this.inputUnits, this.inputUnits);
        final Matrix[] input_col_T = Helper.Img2Conv(input_res_T, n_filters, 1, true);
        final Matrix d_L_d_out_T = d_L_d_out.transpose();
        this.filters.G.add(Helper.reshape(Helper.a_mul_b(d_L_d_out_T, input_col_T), this.channels, n_filters * n_filters));
        this.biases.G.add(d_L_d_out_T.flatten(1).mul(this.bias));
        final Matrix[] d_L_d_in = Helper.Conv2Img(Helper.a_mul_b(d_L_d_out_T, filters_res_T), this.inputUnits, this.inputUnits, n_filters, 1);
        return Helper.reshape(d_L_d_in, this.inputChannels, this.inputUnits * this.inputUnits).transpose();
    }

    public void completeBackward(final Optimizer optimizer) {
        if(this.regularizer != null) {
            this.filters.G.add(this.regularizer.apply(this.filters.W));
        }
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

    private final InitializerFunc initializer;
    private final RegularizerFunc regularizer;
    private final Parameters filters;
    private final Parameters biases;
}
