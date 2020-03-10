package com.github.romualdrousseau.shuju.ml.nn.layer;

import com.github.romualdrousseau.shuju.json.JSON;
import com.github.romualdrousseau.shuju.json.JSONObject;
import com.github.romualdrousseau.shuju.math.Matrix;
import com.github.romualdrousseau.shuju.ml.nn.Layer;
import com.github.romualdrousseau.shuju.ml.nn.Optimizer;

public class MaxPooling2D extends Layer {

    public MaxPooling2D(int inputUnits, int inputChannels, int size, int channels, float bias) {
        super(bias);

        this.inputUnits = inputUnits;
        this.inputChannels = inputChannels;
        this.size = size;
        this.units = inputUnits / size;
        this.channels = channels;

        this.reset(false);
    }

    public void reset(boolean parametersOnly) {
    }

    public Matrix callForward(Matrix input) {
        Matrix output = new Matrix(this.units * this.units, 0);
        for(int i = 0; i < this.inputChannels; i++) {
            Matrix tmp = input.extract(i, 1).reshape(this.inputUnits, this.inputUnits, 1);
            tmp = tmp.poolmax(this.size);
            output = output.concat(tmp.reshape(this.units * this.units, 1, 1), 1);
        }
        if(this.inputChannels == this.channels) {
            return output;
        } else {
            return output.reshape(this.units * this.units * this.inputChannels / this.channels, this.channels, 0);
        }
    }

    public void startBackward(Optimizer optimizer) {
    }

    public Matrix callBackward(Matrix error) {
        Matrix lastInput = this.prev.output;
        Matrix deflatedOutput = output;
        Matrix deflatedError = error;

        if(this.inputChannels != this.channels) {
            deflatedOutput = deflatedOutput.reshape(this.units * this.units, this.inputChannels, 0);
            deflatedError = deflatedError.reshape(this.units * this.units, this.inputChannels, 0);
        }

        error = new Matrix(this.inputUnits * this.inputUnits, 0);
        for(int k = 0; k < this.inputChannels; k++) {
            Matrix tmpInput = lastInput.extract(k, 1).reshape(this.inputUnits, this.inputUnits, 1);
            Matrix tmpOutput = deflatedOutput.extract(k, 1).reshape(this.units, this.units, 1);
            Matrix tmpError = deflatedError.extract(k, 1).reshape(this.units, this.units, 1);
            Matrix tmp = tmpOutput.deflating(tmpInput, tmpError, this.size);
            error = error.concat(tmp.reshape(this.inputUnits * this.inputUnits, 1, 1), 1);
        }

        return error;
    }

    public void completeBackward(Optimizer optimizer) {
    }

    public void fromJSON(JSONObject json) {
    }

    public JSONObject toJSON() {
        JSONObject json = JSON.newJSONObject();
        return json;
    }

    private int inputUnits;
    private int inputChannels;
    private int size;
    private int units;
    private int channels;
}
