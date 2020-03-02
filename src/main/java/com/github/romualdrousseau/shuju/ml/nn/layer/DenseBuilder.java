package com.github.romualdrousseau.shuju.ml.nn.layer;

import com.github.romualdrousseau.shuju.ml.nn.ActivationFunc;
import com.github.romualdrousseau.shuju.ml.nn.InitializerFunc;
import com.github.romualdrousseau.shuju.ml.nn.Layer;
import com.github.romualdrousseau.shuju.ml.nn.NormalizerFunc;
import com.github.romualdrousseau.shuju.ml.nn.activation.Linear;
import com.github.romualdrousseau.shuju.ml.nn.initializer.GlorotUniformInitializer;

public class DenseBuilder {
    private int inputUnits;
    private int units;
    private float bias;
    private ActivationFunc activation;
    private InitializerFunc initializer;
    private NormalizerFunc normalizer;

    public DenseBuilder() {
        this.inputUnits = 0;
        this.units = 0;
        this.bias = 1.0f;
        this.activation = new Linear();
        this.initializer = new GlorotUniformInitializer();
        this.normalizer = null;
    }

    public Layer build() {
        return new Dense(this.inputUnits, this.units, this.bias, this.activation, this.initializer, this.normalizer);
    }

    public DenseBuilder setBias(float bias) {
        this.bias = bias;
        return this;
    }

    public DenseBuilder setInputUnits(int inputUnits) {
        this.inputUnits = inputUnits;
        return this;
    }

    public DenseBuilder setUnits(int units) {
        this.units = units;
        return this;
    }

    public DenseBuilder setActivation(ActivationFunc activation) {
        this.activation = activation;
        return this;
    }

    public DenseBuilder setInitializer(InitializerFunc initializer) {
        this.initializer = initializer;
        return this;
    }

    public DenseBuilder setNormalizer(NormalizerFunc normalizer) {
        this.normalizer = normalizer;
        return this;
    }
}

