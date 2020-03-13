package com.github.romualdrousseau.shuju.ml.nn.layer;

import com.github.romualdrousseau.shuju.json.JSON;
import com.github.romualdrousseau.shuju.json.JSONObject;
import com.github.romualdrousseau.shuju.math.Matrix;
import com.github.romualdrousseau.shuju.math.Scalar;
import com.github.romualdrousseau.shuju.math.Vector;
import com.github.romualdrousseau.shuju.ml.nn.Layer;
import com.github.romualdrousseau.shuju.ml.nn.Optimizer;
import com.github.romualdrousseau.shuju.ml.nn.Parameters;

public class BatchNormalizer extends Layer {

    public BatchNormalizer() {
        super(1.0f);
        this.norms = new Parameters(2);
        this.mu = 0.0f;
        this.var = 0.0f;
        this.mu_run = 0.0f;
        this.var_run = 0.0f;
        this.reset(false);
    }

    public void reset(final boolean parametersOnly) {
        if (parametersOnly) {
            this.norms.M.zero();
            this.norms.V.zero();
        } else {
            this.norms.reset();
            this.norms.W.set(0, 0, 1.0f);
        }
    }

    public Matrix callForward(final Matrix input) {
        final float gamma = this.norms.W.get(0, 0), beta = this.norms.W.get(1, 0);

        final float[] muvar = this.updateMuAndVar(input);
        final float mu = muvar[0], var = muvar[1];

        var_inv = 1.0f / Scalar.sqrt(var + Scalar.EPSILON);
        x_mu = input.copy().sub(mu);
        x_hat = x_mu.copy().mul(var_inv);
        return x_hat.copy().mul(gamma).add(beta);
    }

    public void startBackward(final Optimizer optimizer) {
        this.norms.G.zero();
    }

    public Matrix callBackward(final Matrix d_L_d_out) {
        final float gamma = this.norms.W.get(0, 0);
        final float N = d_L_d_out.rowCount();

        final float dbeta = d_L_d_out.flatten(0, 0);
        final float dgamma = x_hat.copy().mul(d_L_d_out).flatten(0, 0);
        this.norms.G.add(new Vector( new float[] { dgamma, dbeta }), 1);

        final Matrix dva2 = d_L_d_out.copy().mul(gamma);
        final float dvar_inv = x_mu.copy().mul(dva2).flatten(0, 0);
        final float dvar = -0.5f * dvar_inv * Scalar.pow(var_inv, 3);
        final Matrix dxmu = dva2.copy().mul(var_inv).add(x_mu.copy().mul(2.0f * dvar / N));
        final float dmu = -dxmu.flatten(0, 0) / N;
        return dxmu.add(dmu);
    }

    public void completeBackward(final Optimizer optimizer) {
        this.norms.W.sub(optimizer.computeGradients(this.norms));
    }

    public void fromJSON(final JSONObject json) {
        this.norms.fromJSON(json.getJSONObject("norms"));
        this.mu_run = json.getFloat("mu_r");
        this.var_run = json.getFloat("var_r");
    }

    public JSONObject toJSON() {
        final JSONObject json = JSON.newJSONObject();
        json.setJSONObject("norms", this.norms.toJSON());
        json.setFloat("mu_r", this.mu_run);
        json.setFloat("var_r", this.var_run);
        return json;
    }

    private float[] updateMuAndVar(Matrix input) {
        if (this.training) {
            this.mu = input.avg(0, 0);
            this.var = input.var(0, 0);
            this.mu_run = this.mu_run * 0.9f + this.mu * (1.0f - 0.9f);
            this.var_run = this.var_run * 0.9f + this.var * (1.0f - 0.9f);
            return new float[] { mu, var };
        } else {
            return new float[] { mu_run, var_run };
        }
    }

    private final Parameters norms;
    private float mu_run;
    private float var_run;
    // cache
    private float mu;
    private float var;
    private float var_inv;
    private Matrix x_mu;
    private Matrix x_hat;
}
