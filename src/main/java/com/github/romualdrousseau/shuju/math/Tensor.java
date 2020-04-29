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
        return (Tensor) MArray.Full.call(this, 0.0f, 0.0f, this);
    }

    public Tensor ones() {
        return (Tensor) MArray.Full.call(this, 0.0f, 1.0f, this);
    }

    public Tensor full(final float v) {
        return (Tensor) MArray.Full.call(this, 0.0f, v, this);
    }

    public Tensor arrange(final float s) {
        return (Tensor) MArray.Add.accumulate(this, 1.0f, s, -1, this);
    }

    public Tensor chop(final float e) {
        return (Tensor) MArray.Chop.call(this, 0.0f, e, this);
    }

    public Tensor iadd(Tensor m) {
        return (Tensor) MArray.Add.inner(this, m, 1.0f, 0.0f, this);
    }

    public Tensor add(final Tensor m) {
        return new Tensor(MArray.Add.inner(this, m, 1.0f, 0.0f, null));
    }

    public Tensor avg(final int axis) {
        final float b = (axis == -1) ? this.size : this.shape[axis];
        MArray tmp = MArray.Add.reduce(this, 1.0f, 0.0f, axis, null);
        return new Tensor(MArray.Div.call(tmp, 0.0f, b, tmp));
    }

    public Tensor norm(final int axis) {
        MArray tmp = MArray.Norm.reduce(this, 1.0f, 0.0f, axis, null);
        return new Tensor(MArray.Sqrt.call(tmp, 0.0f, 0.0f, tmp));
    }

    public Tensor dot(final Tensor m, final int axis) {
        MArray tmp1 = MArray.Mul.inner(this, m, 1.0f, 0.0f, null);
        return new Tensor(MArray.Add.reduce(tmp1, 1.0f, 0.0f, axis, null));
    }
}
