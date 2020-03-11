package com.github.romualdrousseau.shuju.ml.nn.layer.builder;

import com.github.romualdrousseau.shuju.ml.nn.ActivationFunc;
import com.github.romualdrousseau.shuju.ml.nn.LayerBuilder;
import com.github.romualdrousseau.shuju.ml.nn.activation.Linear;
import com.github.romualdrousseau.shuju.ml.nn.layer.Activation;

public class ActivationBuilder extends LayerBuilder<Activation> {

    public ActivationBuilder() {
        super();
        this.activation = new Linear();
    }

    public Activation build() {
        return new Activation(this.activation);
    }

    public ActivationBuilder setActivation(ActivationFunc activation) {
        this.activation = activation;
        return this;
    }

    private ActivationFunc activation;
}

