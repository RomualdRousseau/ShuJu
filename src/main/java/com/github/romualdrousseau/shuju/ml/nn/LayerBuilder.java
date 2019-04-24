package com.github.romualdrousseau.shuju.ml.nn;

import com.github.romualdrousseau.shuju.ml.nn.activation.Linear;
import com.github.romualdrousseau.shuju.ml.nn.initializer.GlorotUniformInitializer;

public class LayerBuilder {
    private int inputUnits;
    private int units;
    private float bias;
    private ActivationFunc activation;
    private InitializerFunc initializer;
    private NormalizerFunc normalizer;

    public LayerBuilder() {
        this.inputUnits = 0;
        this.units = 0;
        this.bias = 1.0f;
        this.activation = new Linear();
        this.initializer = new GlorotUniformInitializer();
        this.normalizer = null;
    }

    public Layer build() {
        return new Layer(this.inputUnits, this.units, this.bias, this.activation, this.initializer, this.normalizer);
    }

    public LayerBuilder setBias(float bias) {
        this.bias = bias;
        return this;
    }

    public LayerBuilder setInputUnits(int inputUnits) {
        this.inputUnits = inputUnits;
        return this;
    }

    public LayerBuilder setUnits(int units) {
        this.units = units;
        return this;
    }

    public LayerBuilder setActivation(ActivationFunc activation) {
        this.activation = activation;
        return this;
    }

    public LayerBuilder setInitializer(InitializerFunc initializer) {
        this.initializer = initializer;
        return this;
    }

    public LayerBuilder setNormalizer(NormalizerFunc normalizer) {
        this.normalizer = normalizer;
        return this;
    }
}

