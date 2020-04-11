package com.github.romualdrousseau.shuju.ml.nn.layer;

import com.github.romualdrousseau.shuju.json.JSON;
import com.github.romualdrousseau.shuju.json.JSONObject;
import com.github.romualdrousseau.shuju.math.Tensor2D;
import com.github.romualdrousseau.shuju.math.Tensor3D;
import com.github.romualdrousseau.shuju.ml.nn.Helper;
import com.github.romualdrousseau.shuju.ml.nn.InitializerFunc;
import com.github.romualdrousseau.shuju.ml.nn.Layer;
import com.github.romualdrousseau.shuju.ml.nn.Optimizer;
import com.github.romualdrousseau.shuju.ml.nn.Parameters2D;
import com.github.romualdrousseau.shuju.ml.nn.Parameters3D;
import com.github.romualdrousseau.shuju.ml.nn.RegularizerFunc;

public class Conv2D extends Layer {

    public Conv2D(final int inputUnits, final int inputChannels, final int filters, final int channels,
            final float bias, final InitializerFunc initializer, final RegularizerFunc regularizer, final boolean paddingValid) {
        super(inputUnits, inputChannels, paddingValid ? (inputUnits - filters + 1) : inputUnits, inputChannels * channels, bias);

        this.n_filters = filters;
        this.n_pads = paddingValid ? 0 : ((filters - 1) / 2);
        this.initializer = initializer;
        this.regularizer = regularizer;
        this.filters = new Parameters3D(inputChannels, filters * filters, channels);
        this.biases = new Parameters2D(inputChannels * channels);

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
            this.biases.W.zero();
        }
    }

    public Tensor2D callForward(final Tensor2D input) {
        final Tensor3D input_res_T = input.transpose().reshape(this.inputChannels, this.inputUnits, -1);
        final Tensor3D input_col = Helper.Img2Conv(input_res_T, this.n_filters, 1, this.n_pads);
        final Tensor2D output = this.filters.W.matmul(input_col).reshape(this.channels, -1).add(this.biases.W);
        return output.transpose();
    }

    public void startBackward(final Optimizer optimizer) {
        this.filters.G.zero();
        this.biases.G.zero();
    }

    public Tensor2D callBackward(final Tensor2D d_L_d_out) {
        final Tensor3D input_res_T = this.lastInput.transpose().reshape(this.inputChannels, this.inputUnits, -1);
        final Tensor3D input_col = Helper.Img2Conv(input_res_T, n_filters, 1, this.n_pads);
        final Tensor2D d_L_d_out_T = d_L_d_out.transpose();
        final Tensor3D d_L_d_out_res_T = d_L_d_out_T.reshape(this.inputChannels, -1, this.units * this.units);
        final Tensor3D d_L_d_in = Helper.Conv2Img(this.filters.W.matmul(d_L_d_out_res_T, true, false), this.inputUnits, this.inputUnits, this.n_filters, 1, this.n_pads);
        this.filters.G.add(d_L_d_out_res_T.matmul(input_col, false, true));
        this.biases.G.add(d_L_d_out_T.flatten(1).mul(this.bias));
        return d_L_d_in.reshape(this.inputChannels, -1).transpose();
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

    private final int n_filters;
    private final int n_pads;
    private final InitializerFunc initializer;
    private final RegularizerFunc regularizer;
    private final Parameters3D filters;
    private final Parameters2D biases;
}
