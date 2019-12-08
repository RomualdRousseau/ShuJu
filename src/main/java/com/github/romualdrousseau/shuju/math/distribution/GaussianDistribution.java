package com.github.romualdrousseau.shuju.math.distribution;

import com.github.romualdrousseau.shuju.math.DistributionFunction;

public class GaussianDistribution implements DistributionFunction<Float> {

    public GaussianDistribution(float mu, float sigma) {
        this.mu = mu;
        this.sigma = sigma;
    }

    public Float get(Float x) {
        final float c = (float) Math.sqrt(2.0 * Math.PI);
        float a = 1.0f / (this.sigma * c);
        float b = (x - this.mu) / this.sigma;
        return a * (float) Math.exp(-0.5 * b * b);
    }

    private float mu;
    private float sigma;
}
