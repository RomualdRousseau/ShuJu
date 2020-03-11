package com.github.romualdrousseau.shuju.ml.nn.layer;

import com.github.romualdrousseau.shuju.json.JSON;
import com.github.romualdrousseau.shuju.json.JSONObject;
import com.github.romualdrousseau.shuju.math.Matrix;
import com.github.romualdrousseau.shuju.ml.nn.InitializerFunc;
import com.github.romualdrousseau.shuju.ml.nn.Layer;
import com.github.romualdrousseau.shuju.ml.nn.Optimizer;
import com.github.romualdrousseau.shuju.ml.nn.Parameters;

public class Conv2D extends Layer {

    public Conv2D(final int inputUnits, final int inputChannels, final int filters, final int channels,
            final float bias, final InitializerFunc initializer) {
        super(bias);

        assert (inputChannels == 1) : "Multiple input channels not supported";

        this.inputUnits = inputUnits;
        this.units = this.inputUnits - filters + 1;
        this.channels = channels;
        this.initializer = initializer;

        this.filters = new Parameters[channels];
        for (int i = 0; i < channels; i++) {
            this.filters[i] = new Parameters(filters, filters);
        }

        this.reset(false);
    }

    public void reset(final boolean parametersOnly) {
        if (parametersOnly) {
            for (int i = 0; i < this.channels; i++) {
                this.filters[i].M.zero();
                this.filters[i].V.zero();
            }
        } else {
            for (int i = 0; i < this.channels; i++) {
                this.filters[i].reset();
                this.initializer.apply(this.filters[i].W);
                this.filters[i].W.div(this.filters[i].W.rowCount() * this.filters[i].W.colCount());
            }
        }
    }

    public Matrix callForward(final Matrix input) {
        Matrix output = new Matrix(this.units * this.units, this.channels);
        final Matrix oneInput = input.reshape(this.inputUnits, this.inputUnits, 1);
        for (int j = 0; j < this.channels; j++) {
            final Matrix oneOutput = oneInput.conv(this.filters[j].W).reshape(this.units * this.units, 1, 1);
            output = output.replace(j, oneOutput, 1);
        }
        return output;
    }

    public void startBackward(final Optimizer optimizer) {
        for (int i = 0; i < this.channels; i++) {
            this.filters[i].G.zero();
        }
    }

    public Matrix callBackward(Matrix d_L_d_out) {
        final Matrix lastInput = this.lastInput;
        final Matrix deflatedError = d_L_d_out.reshape(this.units * this.units, this.channels, 0);

        d_L_d_out = new Matrix(this.inputUnits * this.inputUnits, 0);

        final Matrix tmpInput = lastInput.reshape(this.inputUnits, this.inputUnits, 1);
        final Matrix tmpError2 = new Matrix(this.inputUnits, this.inputUnits);
        for (int k = 0; k < this.channels; k++) {

            Matrix tmpError = deflatedError.extract(k, 1).reshape(this.units, this.units, 1);

            for (int i = 0; i < tmpError.rowCount(); i++) {
                for (int j = 0; j < tmpError.colCount(); j++) {
                    final Matrix tmpRegion = tmpInput.copy(i, j, this.filters[k].G.rowCount(), this.filters[k].G.colCount());
                    final Matrix d_L_d_t = tmpRegion.mul(tmpError.get(i, j));
                    this.filters[k].G.add(d_L_d_t);
                }
            }

            final Matrix W_ = this.filters[k].W.copy();
            for (int i = 0; i < W_.rowCount() / 2; i++) {
                W_.swap(i, W_.rowCount() - i - 1, 0);
            }
            final int pad = W_.rowCount() - 1;
            tmpError = tmpError.pad(pad, 0.0f).conv(W_);
            tmpError2.add(tmpError);
        }

        d_L_d_out = d_L_d_out.concat(tmpError2.reshape(this.inputUnits * this.inputUnits, 1, 1), 1);

        return d_L_d_out;
    }

    public void completeBackward(final Optimizer optimizer) {
        for (int i = 0; i < this.channels; i++) {
            this.filters[i].W.sub(optimizer.computeGradients(this.filters[i]));
        }
    }

    public void fromJSON(final JSONObject json) {
        for (int i = 0; i < this.channels; i++) {
            this.filters[i].fromJSON(json.getJSONObject("filters_" + i));
        }
        this.bias = json.getFloat("bias");
    }

    public JSONObject toJSON() {
        final JSONObject json = JSON.newJSONObject();
        for (int i = 0; i < this.channels; i++) {
            json.setJSONObject("filters_" + i, this.filters[i].toJSON());
        }
        json.setFloat("bias", this.bias);
        return json;
    }

    private final int inputUnits;
    private final int units;
    private final int channels;
    private final InitializerFunc initializer;
    private final Parameters[] filters;
}
