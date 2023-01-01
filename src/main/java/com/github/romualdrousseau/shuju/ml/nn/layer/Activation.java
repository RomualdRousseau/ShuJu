package com.github.romualdrousseau.shuju.ml.nn.layer;

import com.github.romualdrousseau.shuju.json.JSON;
import com.github.romualdrousseau.shuju.json.JSONObject;
import com.github.romualdrousseau.shuju.math.deprecated.Tensor2D;
import com.github.romualdrousseau.shuju.ml.nn.ActivationFunc;
import com.github.romualdrousseau.shuju.ml.nn.Layer;
import com.github.romualdrousseau.shuju.ml.nn.Optimizer;
import com.github.romualdrousseau.shuju.ml.nn.activation.Linear;

public class Activation extends Layer {

    public Activation(final int inputUnits, final int inputChannels, final ActivationFunc activation) {
        super(inputUnits, inputChannels, inputUnits, inputChannels, 1.0f);

        this.activation = (activation == null) ? new Linear() : activation;
    }

    private Activation(Activation parent) {
        super(parent);

        this.activation = parent.activation;
    }

    public Layer clone() {
        return new Activation(this);
    }

    public void reset() {
    }

    public Tensor2D callForward(final Tensor2D input) {
        return this.activation.apply(input.copy());
    }

    public void startBackward(final Optimizer optimizer) {
    }

    public Tensor2D callBackward(final Tensor2D d_L_d_out) {
        final Tensor2D f_l_d_f = this.activation.derivate(this.output.copy());
        if (d_L_d_out.shape[1] == f_l_d_f.shape[1]) {
            return f_l_d_f.mul(d_L_d_out);
        } else {
            return f_l_d_f.matmul(d_L_d_out);
        }
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
    private final ActivationFunc activation;
}
