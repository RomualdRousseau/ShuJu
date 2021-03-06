package com.github.romualdrousseau.shuju.ml.nn.layer.builder;

import com.github.romualdrousseau.shuju.ml.nn.LayerBuilder;
import com.github.romualdrousseau.shuju.ml.nn.layer.DropOut;

public class DropOutBuilder extends LayerBuilder<DropOut> {

    public DropOutBuilder() {
        super();
        this.inputChannels = 0;
        this.rate = 0.8f;
    }

    public DropOut build() {
        return new DropOut(this.inputUnits, this.inputChannels, this.rate);
    }

    public DropOutBuilder setInputUnits(int inputUnits) {
        this.inputUnits = inputUnits;
        return this;
    }

    public DropOutBuilder setInputChannels(int inputChannels) {
        this.inputChannels = inputChannels;
        return this;
    }

    public DropOutBuilder setRate(final float rate) {
        this.rate = rate;
        return this;
    }

    private float rate;
}

