package com.github.romualdrousseau.shuju.math;

public abstract class Tensor<A> {

    public int[] shape;

    public A data;

    public Tensor(int[] shape, A data) {
        this.shape = shape;
        this.data = data;
    }
}
