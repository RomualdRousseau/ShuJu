package com.github.romualdrousseau.shuju.ml.nn.layer.builder;

import com.github.romualdrousseau.shuju.ml.nn.LayerBuilder;
import com.github.romualdrousseau.shuju.ml.nn.layer.MaxPooling2D;

public class MaxPooling2DBuilder extends LayerBuilder<MaxPooling2D> {

    public MaxPooling2DBuilder() {
        super();
        this.inputUnits = 0;
        this.inputChannels = 8;
        this.size = 2;
        this.channels = 8;
    }

    public MaxPooling2D build() {
        return new MaxPooling2D(this.inputUnits, this.inputChannels, this.size, this.channels, this.bias);
    }

    public MaxPooling2DBuilder setBias(float bias) {
        this.bias = bias;
        return this;
    }

    public MaxPooling2DBuilder setInputUnits(int inputUnits) {
        this.inputUnits = inputUnits;
        return this;
    }

    public MaxPooling2DBuilder setInputChannels(int inputChannels) {
        this.inputChannels = inputChannels;
        return this;
    }

    public MaxPooling2DBuilder setSize(int size) {
        this.size = size;
        return this;
    }

    public MaxPooling2DBuilder setChannels(int channels) {
        this.channels = channels;
        return this;
    }

    private int inputUnits;
    private int inputChannels;
    private int size;
    private int channels;
}

