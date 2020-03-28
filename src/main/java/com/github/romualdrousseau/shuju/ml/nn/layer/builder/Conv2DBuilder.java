package com.github.romualdrousseau.shuju.ml.nn.layer.builder;

import com.github.romualdrousseau.shuju.ml.nn.InitializerFunc;
import com.github.romualdrousseau.shuju.ml.nn.LayerBuilder;
import com.github.romualdrousseau.shuju.ml.nn.RegularizerFunc;
import com.github.romualdrousseau.shuju.ml.nn.initializer.GlorotUniformInitializer;
import com.github.romualdrousseau.shuju.ml.nn.layer.Conv2D;

public class Conv2DBuilder extends LayerBuilder<Conv2D> {

    public Conv2DBuilder() {
        super();
        this.inputChannels = 0;
        this.filters = 3;
        this.channels = 8;
        this.initializer = new GlorotUniformInitializer();
        this.regularizer = null;
        this.paddingValid = false;
    }

    public Conv2D build() {
        return new Conv2D(this.inputUnits, this.inputChannels, this.filters, this.channels, this.bias, this.initializer, this.regularizer, this.paddingValid);
    }

    public Conv2DBuilder setInputUnits(int inputUnits) {
        this.inputUnits = inputUnits;
        return this;
    }

    public Conv2DBuilder setInputChannels(int inputChannels) {
        this.inputChannels = inputChannels;
        return this;
    }

    public Conv2DBuilder setBias(float bias) {
        this.bias = bias;
        return this;
    }

    public Conv2DBuilder setFilters(int filters) {
        this.filters = filters;
        return this;
    }

    public Conv2DBuilder setChannels(int channels) {
        this.channels = channels;
        return this;
    }

    public Conv2DBuilder withSamePadding() {
        this.paddingValid = false;
        return this;
    }

    public Conv2DBuilder withValidPadding() {
        this.paddingValid = true;
        return this;
    }

    public Conv2DBuilder setInitializer(InitializerFunc initializer) {
        this.initializer = initializer;
        return this;
    }

    public Conv2DBuilder setRegularizer(RegularizerFunc regularizer) {
        this.regularizer = regularizer;
        return this;
    }

    private int filters;
    private int channels;
    private InitializerFunc initializer;
    private RegularizerFunc regularizer;
    private boolean paddingValid;
}

