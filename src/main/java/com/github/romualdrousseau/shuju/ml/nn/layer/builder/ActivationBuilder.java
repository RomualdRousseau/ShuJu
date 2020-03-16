package com.github.romualdrousseau.shuju.ml.nn.layer.builder;

import com.github.romualdrousseau.shuju.ml.nn.ActivationFunc;
import com.github.romualdrousseau.shuju.ml.nn.LayerBuilder;
import com.github.romualdrousseau.shuju.ml.nn.activation.Linear;
import com.github.romualdrousseau.shuju.ml.nn.layer.Activation;

public class ActivationBuilder extends LayerBuilder<Activation> {

    public ActivationBuilder() {
        super();
        this.inputChannels = 0;
        this.activation = new Linear();
    }

    public Activation build() {
        return new Activation(this.inputUnits, this.inputChannels, this.activation);
    }

    public ActivationBuilder setInputUnits(int inputUnits) {
        this.inputUnits = inputUnits;
        return this;
    }

    public ActivationBuilder setInputChannels(int inputChannels) {
        this.inputChannels = inputChannels;
        return this;
    }

    public ActivationBuilder setActivation(ActivationFunc activation) {
        this.activation = activation;
        return this;
    }

    private ActivationFunc activation;
}

