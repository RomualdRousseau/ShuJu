package com.github.romualdrousseau.shuju.ml.nn.layer.builder;

import com.github.romualdrousseau.shuju.ml.nn.LayerBuilder;
import com.github.romualdrousseau.shuju.ml.nn.layer.Flatten;

public class FlattenBuilder extends LayerBuilder<Flatten> {

    public FlattenBuilder() {
        super();
        this.inputChannels = 0;
    }

    public Flatten build() {
        return new Flatten(this.inputUnits, this.inputChannels);
    }

    public FlattenBuilder setInputUnits(int inputUnits) {
        this.inputUnits = inputUnits;
        return this;
    }

    public FlattenBuilder setInputChannels(int inputChannels) {
        this.inputChannels = inputChannels;
        return this;
    }
}

