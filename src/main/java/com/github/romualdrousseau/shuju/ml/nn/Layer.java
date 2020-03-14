package com.github.romualdrousseau.shuju.ml.nn;

import com.github.romualdrousseau.shuju.json.JSONObject;

import com.github.romualdrousseau.shuju.math.Matrix;
import com.github.romualdrousseau.shuju.math.Vector;

public abstract class Layer {

    public Model model;

    public int inputUnits;
    public int inputChannels;
    public int units;
    public int channels;
    public float bias;

    public Matrix lastInput;
    public Matrix output;

    public boolean frozen;
    public boolean training;

    protected Layer(int inputUnits, int units, float bias) {
        this(inputUnits, 1, units, 1, bias);
    }

    protected Layer(int inputUnits, int inputChannels, int units, int channels, float bias) {
        this.inputUnits = inputUnits;
        this.inputChannels = inputChannels;
        this.units = units;
        this.channels = channels;
        this.bias = bias;

        this.frozen = false;
        this.training = false;

        this.lastInput = null;
        this.output = null;
    }

    public Matrix detach() {
        return this.output;
    }

    public Vector detachAsVector() {
        return this.output.toVector(0, false);
    }

    public abstract void reset(boolean parametersOnly);

    public abstract Matrix callForward(Matrix input);

    public abstract void startBackward(Optimizer optimizer);

    public abstract Matrix callBackward(Matrix d_L_d_out);

    public abstract void completeBackward(Optimizer optimizer);

    public abstract void fromJSON(JSONObject json);

    public abstract JSONObject toJSON();
}
