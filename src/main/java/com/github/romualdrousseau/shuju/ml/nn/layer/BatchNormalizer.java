package com.github.romualdrousseau.shuju.ml.nn.layer;

import com.github.romualdrousseau.shuju.json.JSON;
import com.github.romualdrousseau.shuju.json.JSONObject;
import com.github.romualdrousseau.shuju.math.Tensor2D;
import com.github.romualdrousseau.shuju.math.Scalar;
import com.github.romualdrousseau.shuju.ml.nn.Layer;
import com.github.romualdrousseau.shuju.ml.nn.Optimizer;
import com.github.romualdrousseau.shuju.ml.nn.Parameters2D;

public class BatchNormalizer extends Layer {

    public BatchNormalizer(final int inputUnits, final int inputChannels) {
        super(inputUnits, inputChannels, inputUnits, inputChannels, 1.0f);

        this.gamma = new Parameters2D(inputChannels, 1);
        this.beta = new Parameters2D(inputChannels, 1);
        this.mu_run = new Tensor2D(1, inputChannels).zero();
        this.var_run = new Tensor2D(1, inputChannels).ones();

        this.reset(false);
    }

    public void reset(final boolean parametersOnly) {
        if (parametersOnly) {
            this.gamma.M.zero();
            this.gamma.V.zero();
            this.beta.M.zero();
            this.beta.V.zero();
        } else {
            this.gamma.reset();
            this.gamma.W.ones();
            this.beta.reset();
        }
    }

    public Tensor2D callForward(final Tensor2D input) {
        final Tensor2D mu;
        final Tensor2D var;

        if (this.training) {
            mu = input.avg(0);
            var = input.var(0);
            this.mu_run = this.mu_run.expAvg(mu, 0.9f);
            this.var_run = this.var_run.expAvg(var, 0.9f);
        } else {
            mu = this.mu_run.copy();
            var = this.var_run.copy();
        }

        std_inv = var.add(Scalar.EPSILON).invsqrt();
        x_mu = input.copy().sub(mu);
        x_hat = x_mu.copy().mul(std_inv);
        return x_hat.copy().mul(this.gamma.W).add(this.beta.W);
    }

    public void startBackward(final Optimizer optimizer) {
        this.gamma.G.zero();
        this.beta.G.zero();
    }

    public Tensor2D callBackward(final Tensor2D d_L_d_out) {
        final float N = d_L_d_out.rowCount();

        this.gamma.G.add(d_L_d_out.flatten(0));

        this.beta.G.add(x_hat.copy().mul(d_L_d_out).flatten(0));

        final Tensor2D dva2 = d_L_d_out.copy().mul(this.gamma.W);
        final Tensor2D dstd_inv = x_mu.copy().mul(dva2).flatten(0);
        final Tensor2D dvar = dstd_inv.mul(-0.5f).mul(std_inv.copy().pow(3));
        final Tensor2D dxmu = dva2.mul(std_inv).add(x_mu.copy().mul(dvar).mul(2.0f / N));
        final Tensor2D dmu = dxmu.flatten(0).mul(-1.0f / N);
        return dxmu.add(dmu);
    }

    public void completeBackward(final Optimizer optimizer) {
        this.gamma.W.sub(optimizer.computeGradients(this.gamma));
        this.beta.W.sub(optimizer.computeGradients(this.beta));
    }

    public void fromJSON(final JSONObject json) {
        this.gamma.fromJSON(json.getJSONObject("gamma"));
        this.beta.fromJSON(json.getJSONObject("beta"));
        this.mu_run = new Tensor2D(json.getJSONObject("mu_run"));
        this.var_run = new Tensor2D(json.getJSONObject("var_run"));
    }

    public JSONObject toJSON() {
        final JSONObject json = JSON.newJSONObject();
        json.setJSONObject("gamma", this.gamma.toJSON());
        json.setJSONObject("beta", this.beta.toJSON());
        json.setJSONObject("mu_run", this.mu_run.toJSON());
        json.setJSONObject("var_run", this.var_run.toJSON());
        return json;
    }

    private final Parameters2D gamma;
    private final Parameters2D beta;
    private Tensor2D mu_run;
    private Tensor2D var_run;
    // cache
    private Tensor2D std_inv;
    private Tensor2D x_mu;
    private Tensor2D x_hat;
}
