package com.github.romualdrousseau.shuju.ml.nn;

public abstract class LayerBuilder<T extends Layer> {

    public LayerBuilder() {
        this.bias = 1.0f;
    }

    public LayerBuilder<T> setBias(float bias) {
        this.bias = bias;
        return this;
    }

    public abstract T build();

    protected float bias;
}
