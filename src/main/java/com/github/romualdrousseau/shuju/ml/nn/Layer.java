package com.github.romualdrousseau.shuju.ml.nn;

import com.github.romualdrousseau.shuju.json.JSONObject;

import com.github.romualdrousseau.shuju.math.Tensor2D;
import com.github.romualdrousseau.shuju.math.Tensor1D;

public abstract class Layer {

    public Model model;

    public int inputUnits;
    public int inputChannels;
    public int units;
    public int channels;
    public float bias;

    public Tensor2D lastInput;
    public Tensor2D output;

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

    public Tensor2D detach() {
        return this.output;
    }

    public Tensor1D detachAsVector() {
        return new Tensor1D(this.output.transpose().getFloats(0));
    }

    public abstract void reset(boolean parametersOnly);

    public abstract Tensor2D callForward(Tensor2D input);

    public abstract void startBackward(Optimizer optimizer);

    public abstract Tensor2D callBackward(Tensor2D d_L_d_out);

    public abstract void completeBackward(Optimizer optimizer);

    public abstract void fromJSON(JSONObject json);

    public abstract JSONObject toJSON();
}
