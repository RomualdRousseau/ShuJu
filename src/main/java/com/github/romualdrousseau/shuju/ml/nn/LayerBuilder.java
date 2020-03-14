package com.github.romualdrousseau.shuju.ml.nn;

public abstract class LayerBuilder<T extends Layer> {

    public LayerBuilder() {
        this.inputUnits = 0;
        this.inputChannels = 1;
        this.bias = 1.0f;
    }

    public LayerBuilder<T> setInputUnits(int inputUnits) {
        this.inputUnits = inputUnits;
        return this;
    }

    public LayerBuilder<T> setInputChannels(int inputChannels) {
        this.inputChannels = inputChannels;
        return this;
    }

    public LayerBuilder<T> setBias(float bias) {
        this.bias = bias;
        return this;
    }

    public abstract T build();

    protected int inputUnits;
    protected int inputChannels;
    protected float bias;
}
