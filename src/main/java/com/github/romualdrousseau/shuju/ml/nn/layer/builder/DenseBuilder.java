package com.github.romualdrousseau.shuju.ml.nn.layer.builder;

import com.github.romualdrousseau.shuju.ml.nn.InitializerFunc;
import com.github.romualdrousseau.shuju.ml.nn.LayerBuilder;
import com.github.romualdrousseau.shuju.ml.nn.RegularizerFunc;
import com.github.romualdrousseau.shuju.ml.nn.layer.Dense;
import com.github.romualdrousseau.shuju.ml.nn.initializer.GlorotUniformInitializer;

public class DenseBuilder extends LayerBuilder<Dense> {

    public DenseBuilder() {
        super();
        this.units = 0;
        this.initializer = new GlorotUniformInitializer();
        this.regularizer = null;
    }

    public Dense build() {
        return new Dense(this.inputUnits, this.units, this.bias, this.initializer, this.regularizer);
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

    public DenseBuilder setInitializer(InitializerFunc initializer) {
        this.initializer = initializer;
        return this;
    }

    public DenseBuilder setRegularizer(RegularizerFunc regularizer) {
        this.regularizer = regularizer;
        return this;
    }

    private int units;
    private InitializerFunc initializer;
    private RegularizerFunc regularizer;
}

