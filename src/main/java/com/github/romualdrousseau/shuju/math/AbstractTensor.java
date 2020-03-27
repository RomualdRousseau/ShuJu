package com.github.romualdrousseau.shuju.math;

public abstract class AbstractTensor<A> {

    public int[] shape;
    public A data;

    public AbstractTensor(int[] shape, A data) {
        this.shape = shape;
        this.data = data;
    }
}
