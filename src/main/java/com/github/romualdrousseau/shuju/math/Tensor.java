package com.github.romualdrousseau.shuju.math;

public class Tensor extends MArray {

    public Tensor(final int... shape) {
        super(shape);
    }

    public Tensor(MArray other) {
        this.size = other.size;
        this.shape = other.shape;
        this.stride = other.stride;
        this.data = other.data;
        this.copied = false;
    }

    public Tensor zeros() {
        return (Tensor) MArray.Full.call(this, 0.0f, this);
    }

    public Tensor ones() {
        return (Tensor) MArray.Full.call(this, 1.0f, this);
    }

    public Tensor full(final float v) {
        return (Tensor) MArray.Full.call(this, v, this);
    }

    public Tensor arrange() {
        return (Tensor) MArray.Full.accumulate(this, 1.0f, -1, this);
    }

    public Tensor chop(final float e) {
        return (Tensor) MArray.Chop.call(this, e, this);
    }

    public Tensor iadd(Tensor m) {
        return (Tensor) MArray.Add.inner(this, m, this);
    }

    public Tensor add(Tensor m) {
        return new Tensor(MArray.Add.inner(this, m, null));
    }
}
