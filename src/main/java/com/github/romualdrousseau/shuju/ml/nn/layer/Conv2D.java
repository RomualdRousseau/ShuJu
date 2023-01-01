package com.github.romualdrousseau.shuju.ml.nn.layer;

import com.github.romualdrousseau.shuju.json.JSON;
import com.github.romualdrousseau.shuju.json.JSONObject;
import com.github.romualdrousseau.shuju.math.deprecated.Tensor2D;
import com.github.romualdrousseau.shuju.math.deprecated.Tensor3D;
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
        super(inputUnits, inputChannels, paddingValid ? (inputUnits - filters + 1) : inputUnits, channels, bias);

        this.n_filters = filters;
        this.n_pads = paddingValid ? 0 : ((filters - 1) / 2);
        this.initializer = initializer;
        this.regularizer = regularizer;

        this.kernel = new Parameters3D(inputChannels, filters * filters, channels);
        this.biases = new Parameters2D(channels);

        this.reset();
    }

    private Conv2D(Conv2D parent) {
        super(parent);

        this.n_filters = parent.n_filters;
        this.n_pads = parent.n_pads;
        this.initializer = parent.initializer;
        this.regularizer = parent.regularizer;

        this.kernel = parent.kernel.clone();
        this.biases = parent.biases.clone();
    }

    public Layer clone() {
        return new Conv2D(this);
    }

    public void reset() {
        this.kernel.reset();
        this.biases.reset();
        this.initializer.apply(this.kernel.W).div(this.inputUnits - this.units + 1);
    }

    public Tensor2D callForward(final Tensor2D input) {
        final Tensor3D input_res_T = input.transpose().reshape(this.inputChannels, this.inputUnits, -1);
        final Tensor3D input_col = Helper.Im2Col(input_res_T, this.n_filters, 1, this.n_pads);
        final Tensor2D output = this.kernel.W.matmul(input_col).flatten(0).reshape(this.channels, -1).add(this.biases.W);
        this.lastInput_col = input_col;
        return output.transpose();
    }

    public void startBackward(final Optimizer optimizer) {
        this.kernel.G.zero();
        this.biases.G.zero();
    }

    public Tensor2D callBackward(final Tensor2D d_L_d_out) {
        final Tensor2D d_L_d_out_T = d_L_d_out.transpose();
        final Tensor3D d_L_d_out_res_T = d_L_d_out_T.repeat(this.inputChannels, 0).div(this.inputChannels);
        this.kernel.G.add(d_L_d_out_res_T.matmul(this.lastInput_col, false, true));
        this.biases.G.add(d_L_d_out_T.flatten(1).mul(this.bias));
        final Tensor3D d_L_d_in = Helper.Col2Im(this.kernel.W.matmul(d_L_d_out_res_T, true, false), this.inputUnits, this.inputUnits, this.n_filters, 1, this.n_pads);
        return d_L_d_in.reshape(this.inputChannels, -1).transpose();
    }

    public void completeBackward(final Optimizer optimizer) {
        if(this.regularizer != null) {
            this.kernel.G.add(this.regularizer.apply(this.kernel.W));
        }
        this.kernel.W.sub(optimizer.computeGradients(this.kernel));
        this.biases.W.sub(optimizer.computeGradients(this.biases));
    }

    public void fromJSON(final JSONObject json) {
        this.kernel.fromJSON(json.getJSONObject("filters"));
        this.biases.fromJSON(json.getJSONObject("biases"));
        this.bias = json.getFloat("bias");
    }

    public JSONObject toJSON() {
        final JSONObject json = JSON.newJSONObject();
        json.setJSONObject("filters", this.kernel.toJSON());
        json.setJSONObject("biases", this.biases.toJSON());
        json.setFloat("bias", this.bias);
        return json;
    }

    // Hyper-parameters
    private final int n_filters;
    private final int n_pads;
    private final InitializerFunc initializer;
    private final RegularizerFunc regularizer;

    // Parameters
    private final Parameters3D kernel;
    private final Parameters2D biases;

    // Cache
    private Tensor3D lastInput_col;
}
