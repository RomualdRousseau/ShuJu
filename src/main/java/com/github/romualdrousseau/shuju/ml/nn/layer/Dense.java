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

    public void resetGradients(Optimizer optimizer) {
        this.weights.G.zero();
        this.biases.G.zero();
    }

    public void adjustGradients(Optimizer optimizer) {
        this.adjustParameters(this.weights, optimizer.computeGradients(this.weights));
        this.adjustParameters(this.biases, optimizer.computeGradients(this.biases));
    }

    public void callForward() {
        Matrix net = Scalar.xw_plus_b(this.prev.output, this.weights.W, this.biases.W);
        this.output = this.activation.apply(net);
    }

    public Matrix callBackward(Matrix error) {
        error = Scalar.a_mul_b(error, this.activation.derivate(this.output));
        this.weights.G.fma(error, this.prev.output, false, true);
        this.biases.G.fma(error, this.prev.bias);
        return this.weights.W.matmul(error, true, false);
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
