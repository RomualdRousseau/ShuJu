package com.github.romualdrousseau.shuju.ml.nn.layer;

import com.github.romualdrousseau.shuju.json.JSON;
import com.github.romualdrousseau.shuju.json.JSONObject;
import com.github.romualdrousseau.shuju.math.Linalg;
import com.github.romualdrousseau.shuju.math.Matrix;
import com.github.romualdrousseau.shuju.ml.nn.InitializerFunc;
import com.github.romualdrousseau.shuju.ml.nn.Layer;
import com.github.romualdrousseau.shuju.ml.nn.Optimizer;
import com.github.romualdrousseau.shuju.ml.nn.Parameters;

public class Conv2D extends Layer {

    public Conv2D(int inputUnits, int inputChannels, int filters, int channels, float bias, InitializerFunc initializer) {
        super(inputUnits, inputChannels, inputUnits - filters + 1, inputChannels * channels, bias);

        this.initializer = initializer;
        this.filters = new Parameters(filters * filters,  inputChannels * channels);
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
        final Matrix filters_res = Linalg.BlockDiagonal(this.filters.W, this.inputChannels, false);
        final Matrix input_res = input.transpose().reshape(-1, this.inputUnits);
        final Matrix input_col = Linalg.Img2Conv(input_res, this.inputChannels, n_filters, 1, false);
        final Matrix output = Linalg.xw_plus_b(input_col, filters_res, this.biases.W.toVector(0, false));
        return output.transpose();
    }

    public void startBackward(final Optimizer optimizer) {
        this.filters.G.zero();
        this.biases.G.zero();
    }

    public Matrix callBackward(final Matrix d_L_d_out) {
        final int n_filters = this.inputUnits - this.units + 1;
        final Matrix filters_res = Linalg.BlockDiagonal(this.filters.W, this.inputChannels, true);
        final Matrix input_res = this.lastInput.transpose().reshape(-1, this.inputUnits);
        final Matrix input_col_T = Linalg.Img2Conv(input_res, this.inputChannels, n_filters, 1, true);
        final Matrix d_L_d_out_T = d_L_d_out.transpose();
        this.filters.G.add(Linalg.BlockColumn(d_L_d_out_T.matmul(input_col_T), this.inputChannels, 1));
        this.biases.G.add(d_L_d_out_T.flatten(1).mul(this.bias));
        final Matrix d_L_d_in = Linalg.Conv2Img(filters_res.matmul(d_L_d_out_T), this.inputChannels, this.inputUnits, this.inputUnits, n_filters, 1);
        return d_L_d_in.reshape(-1, this.inputChannels);
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

    private final InitializerFunc initializer;
    private final Parameters filters;
    private final Parameters biases;
}
