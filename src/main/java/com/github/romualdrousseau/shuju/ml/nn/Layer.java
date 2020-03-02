package com.github.romualdrousseau.shuju.ml.nn;

import com.github.romualdrousseau.shuju.json.JSONObject;

import com.github.romualdrousseau.shuju.math.Matrix;
import com.github.romualdrousseau.shuju.math.Vector;

public abstract class Layer {

    public boolean frozen;

    public float bias;
    public Matrix output;

    public Layer prev;
    public Layer next;

    protected Layer(float bias) {
        this.frozen = false;

        this.bias = bias;
        this.output = null;

        this.prev = null;
        this.next = null;
    }

    public Matrix detach() {
        return this.output;
    }

    public Vector detachAsVector() {
        return this.output.toVector(0, false);
    }

    public abstract void reset(boolean parametersOnly);

    public abstract void resetGradients(Optimizer optimizer);

    public abstract void adjustGradients(Optimizer optimizer);

    public abstract void callForward();

    public abstract Matrix callBackward(Matrix error);

    public abstract void fromJSON(JSONObject json);

    public abstract JSONObject toJSON();
}
