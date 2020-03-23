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

public class Conv2D_ extends Layer {

    public Conv2D_(final int inputUnits, final int inputChannels, final int filters, final int channels,
            final float bias, final InitializerFunc initializer, final RegularizerFunc regularizer) {
        super(inputUnits, inputChannels, inputUnits - filters + 1, inputChannels * channels, bias);

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
            this.initializer.apply(this.biases.W);
        }
    }

    public Tensor2D callForward(final Tensor2D input) {
        final int n_filters = this.inputUnits - this.units + 1;
        final Tensor3D input_res_T = input.transpose().reshape(this.inputChannels, this.inputUnits, -1);
        final Tensor3D input_col = Helper.Img2Conv(input_res_T, n_filters, 1, false);
        final Tensor2D output = (Tensor2D) ((Tensor3D) this.filters.W.matmul(input_col)).reshape(-1, this.units * this.units).add(this.biases.W);
        return output.transpose();
    }

    public void startBackward(final Optimizer optimizer) {
        this.filters.G.zero();
        this.biases.G.zero();
    }

    public Tensor2D callBackward(final Tensor2D d_L_d_out) {
        final int n_filters = this.inputUnits - this.units + 1;
        final Tensor3D input_res_T = this.lastInput.transpose().reshape(this.inputChannels, this.inputUnits, -1);
        final Tensor3D input_col_T = Helper.Img2Conv(input_res_T, n_filters, 1, true);
        final Tensor2D d_L_d_out_T = d_L_d_out.transpose();
        this.filters.G.add(Helper.a_mul_b(d_L_d_out_T, input_col_T).reshape(this.channels, -1));
        this.biases.G.add(d_L_d_out_T.flatten(1).mul(this.bias));
        final Tensor3D d_L_d_in = Helper.Conv2Img(Helper.a_mul_b(d_L_d_out_T, this.filters.W), this.inputUnits, this.inputUnits, n_filters, 1);
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

    private final InitializerFunc initializer;
    private final RegularizerFunc regularizer;
    private final Parameters3D filters;
    private final Parameters2D biases;
}
