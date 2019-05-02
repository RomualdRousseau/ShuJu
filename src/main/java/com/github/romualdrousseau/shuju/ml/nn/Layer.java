package com.github.romualdrousseau.shuju.ml.nn;

import com.github.romualdrousseau.shuju.json.JSON;
import com.github.romualdrousseau.shuju.json.JSONFactory;
import com.github.romualdrousseau.shuju.json.JSONObject;

import com.github.romualdrousseau.shuju.math.Matrix;
import com.github.romualdrousseau.shuju.ml.nn.activation.Linear;
import com.github.romualdrousseau.shuju.ml.nn.initializer.GlorotUniformInitializer;

public class Layer {
    protected Parameters weights;
    protected Parameters biases;
    protected float bias;

    protected ActivationFunc activation;
    protected InitializerFunc initializer;
    protected NormalizerFunc normalizer;

    protected Matrix output;

    protected Layer prev;
    protected Layer next;

    public Layer(int inputUnits, int units, float bias, ActivationFunc activation, InitializerFunc initializer,
            NormalizerFunc normalizer) {
        this.weights = new Parameters(inputUnits, units);
        this.biases = new Parameters(units);
        this.bias = bias;

        this.activation = (activation == null) ? new Linear() : activation;
        this.initializer = (initializer == null) ? new GlorotUniformInitializer() : initializer;
        this.normalizer = normalizer;

        this.output = null;

        this.prev = null;
        this.next = null;
        this.reset();
    }

    public void reset() {
        this.weights.reset();
        this.biases.reset();
        this.initializer.apply(this.weights.W);
    }

    public void adjustGradients(Parameters p, Matrix g) {
        p.W.sub(g);
        if (this.normalizer != null) {
            this.normalizer.apply(p.W);
        }
    }

    public Matrix detach() {
        return this.output;
    }

    public void fromJSON(JSONObject json) {
        this.weights.fromJSON(json.getJSONObject("weights"));
        this.biases.fromJSON(json.getJSONObject("biases"));
        this.bias = json.getFloat("bias");
    }

    public JSONObject toJSON() {
        JSONObject json = JSON.getFactory().newJSONObject();
        json.setJSONObject("weights", this.weights.toJSON());
        json.setJSONObject("biases", this.biases.toJSON());
        json.setFloat("bias", this.bias);
        return json;
    }
}
