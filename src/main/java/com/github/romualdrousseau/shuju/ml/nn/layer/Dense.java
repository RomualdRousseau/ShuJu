package com.github.romualdrousseau.shuju.ml.nn.layer;

import com.github.romualdrousseau.shuju.json.JSON;
import com.github.romualdrousseau.shuju.json.JSONObject;
import com.github.romualdrousseau.shuju.math.Matrix;
import com.github.romualdrousseau.shuju.math.Scalar;
import com.github.romualdrousseau.shuju.ml.nn.ActivationFunc;
import com.github.romualdrousseau.shuju.ml.nn.InitializerFunc;
import com.github.romualdrousseau.shuju.ml.nn.Layer;
import com.github.romualdrousseau.shuju.ml.nn.NormalizerFunc;
import com.github.romualdrousseau.shuju.ml.nn.Optimizer;
import com.github.romualdrousseau.shuju.ml.nn.Parameters;
import com.github.romualdrousseau.shuju.ml.nn.activation.Linear;
import com.github.romualdrousseau.shuju.ml.nn.initializer.GlorotUniformInitializer;

public class Dense extends Layer {

    public Dense(int inputUnits, int units, float bias, ActivationFunc activation, InitializerFunc initializer,
            NormalizerFunc normalizer) {
        super(bias);

        this.weights = new Parameters(inputUnits, units);
        this.biases = new Parameters(units);

        this.activation = (activation == null) ? new Linear() : activation;
        this.initializer = (initializer == null) ? new GlorotUniformInitializer() : initializer;
        this.normalizer = normalizer;

        this.reset(false);
    }

    public void reset(boolean parametersOnly) {
        if (parametersOnly) {
            this.weights.M.zero();
            this.weights.V.zero();
            this.biases.M.zero();
            this.biases.V.zero();
        } else {
            this.weights.reset();
            this.biases.reset();
            this.initializer.apply(this.weights.W);
        }
    }

    public Matrix callForward(Matrix input) {
        Matrix net = Scalar.xw_plus_b(input, this.weights.W, this.biases.W);
        return this.activation.apply(net);
    }

    public void startBackward(Optimizer optimizer) {
        this.weights.G.zero();
        this.biases.G.zero();
    }

    public Matrix callBackward(Matrix d_L_d_out) {
        final Matrix last_input = this.prev.output;
        final float last_bias = this.prev.bias;
        final Matrix d_L_d_t = Scalar.a_mul_b(d_L_d_out, this.activation.derivate(this.output));
        this.weights.G.fma(d_L_d_t, last_input, false, true);
        this.biases.G.fma(d_L_d_t, last_bias);
        return this.weights.W.matmul(d_L_d_t, true, false);
    }

    public void completeBackward(Optimizer optimizer) {
        this.adjustParameters(this.weights, optimizer.computeGradients(this.weights));
        this.adjustParameters(this.biases, optimizer.computeGradients(this.biases));
    }

    public void fromJSON(JSONObject json) {
        this.weights.fromJSON(json.getJSONObject("weights"));
        this.biases.fromJSON(json.getJSONObject("biases"));
        this.bias = json.getFloat("bias");
    }

    public JSONObject toJSON() {
        JSONObject json = JSON.newJSONObject();
        json.setJSONObject("weights", this.weights.toJSON());
        json.setJSONObject("biases", this.biases.toJSON());
        json.setFloat("bias", this.bias);
        return json;
    }

    private void adjustParameters(Parameters p, Matrix g) {
        p.W.sub(g);
        if (this.normalizer != null) {
            this.normalizer.apply(p.W);
        }
    }

    private Parameters weights;
    private Parameters biases;

    private ActivationFunc activation;
    private InitializerFunc initializer;
    private NormalizerFunc normalizer;
}
