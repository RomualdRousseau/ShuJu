package com.github.romualdrousseau.shuju.ml.nn.layer;

import com.github.romualdrousseau.shuju.json.JSON;
import com.github.romualdrousseau.shuju.json.JSONObject;
import com.github.romualdrousseau.shuju.math.Matrix;
import com.github.romualdrousseau.shuju.ml.nn.Layer;
import com.github.romualdrousseau.shuju.ml.nn.Optimizer;

public class MaxPooling2D extends Layer {

    public MaxPooling2D(int inputUnits, int inputChannels, int size) {
        super(1.0f);

        this.inputUnits = inputUnits;
        this.inputChannels = inputChannels;
        this.size = size;
        this.units = inputUnits / size;

        this.reset(false);
    }

    public void reset(boolean parametersOnly) {
    }

    public Matrix callForward(Matrix input) {
        Matrix output = new Matrix(this.units * this.units, 0);
        for(int i = 0; i < this.inputChannels; i++) {
            Matrix oneInput = input.extract(i, 1).reshape(this.inputUnits, this.inputUnits, 1);
            Matrix oneOutput = oneInput.poolmax(this.size).reshape(this.units * this.units, 1, 1);
            output = output.concat(oneOutput, 1);
        }
        return output;
    }

    public void startBackward(Optimizer optimizer) {
    }

    public Matrix callBackward(Matrix d_L_d_out) {
        Matrix d_out_d_x = new Matrix(this.inputUnits * this.inputUnits, 0);
        for(int k = 0; k < this.inputChannels; k++) {
            Matrix input1 = this.lastInput.extract(k, 1).reshape(this.inputUnits, this.inputUnits, 1);
            Matrix output1 = this.output.extract(k, 1).reshape(this.units, this.units, 1);
            Matrix d_L_d_out1 = d_L_d_out.extract(k, 1).reshape(this.units, this.units, 1);
            Matrix d_out_d_x1 = output1.deflating(input1, d_L_d_out1, this.size).reshape(this.inputUnits * this.inputUnits, 1, 1);
            d_out_d_x = d_out_d_x.concat(d_out_d_x1, 1);
        }
        return d_out_d_x;
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
}
