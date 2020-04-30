package com.github.romualdrousseau.shuju.math;

import java.util.Arrays;

public class Tensor extends MArray {

    public static final Tensor Null = new Tensor();

    private Tensor _T = null;
    public Tensor T() {
        if(this._T == null) {
            this._T = new Tensor(this.clone().transpose());
        }
        return this._T;
    }

    public Tensor() {
    }

    public Tensor(final int... shape) {
        super(shape);
    }

    private Tensor(MArray other) {
        super(other);
    }

    public Tensor reshape(final int... shape) {
        return new Tensor(super.resize(shape));
    }

    @Override
    public Tensor transpose() {
        return new Tensor(super.transpose());
    }

    @Override
    public Tensor transpose(final int... indices) {
        return new Tensor(super.transpose(indices));
    }

    public Tensor copy() {
        return new Tensor(this.clone());
    }

    public Tensor get(final int... slice) {
        return new Tensor(new MArray(this, this.slicer(slice)));
    }

    public Tensor create(final float... data) {
        return new Tensor(this.setFloats(data));
    }

    public Tensor create(final float[][] data) {
        return new Tensor(this.setFloats(data));
    }

    public Tensor zeros() {
        return (Tensor) MFuncs.Full.outer(this, 0.0f, this);
    }

    public Tensor ones() {
        return (Tensor) MFuncs.Full.outer(this, 1.0f, this);
    }

    public Tensor full(final float v) {
        return (Tensor) MFuncs.Full.outer(this, v, this);
    }

    public Tensor arrange(final int start, final int step, final int axis) {
        if (step == 1) {
            return ((Tensor) MFuncs.Inc.accumulate(this, start - 1, axis, this));
        }
        if (step == -1) {
            return ((Tensor) MFuncs.Dec.accumulate(this, start + 1, axis, this));
        }
        if(step != 0) {
            MFuncs.Inc.accumulate(this, -step / Math.abs(step), axis, this);
            this.imul(step);
        }
        if(start != 0) {
            this.iadd(start);
        }
        return this;
    }

    public Tensor chop(final float e) {
        return (Tensor) MFuncs.Chop.outer(this, e, this);
    }

    public Tensor iadd(final float v) {
        return (Tensor) MFuncs.Add.outer(this, v, this);
    }

    public Tensor iadd(final Tensor m) {
        return (Tensor) MFuncs.Add.outer(this, m, this);
    }

    public Tensor add(final float v) {
        return new Tensor(MFuncs.Add.outer(this, v, null));
    }

    public Tensor add(final Tensor m) {
        return new Tensor(MFuncs.Add.outer(this, m, null));
    }

    public Tensor isub(float v) {
        return (Tensor) MFuncs.Sub.outer(this, v, this);
    }

    public Tensor isub(Tensor m) {
        return (Tensor) MFuncs.Sub.outer(this, m, this);
    }

    public Tensor sub(final float v) {
        return new Tensor(MFuncs.Sub.outer(this, v, null));
    }

    public Tensor sub(final Tensor m) {
        return new Tensor(MFuncs.Sub.outer(this, m, null));
    }

    public Tensor imul(final float v) {
        return (Tensor) MFuncs.Mul.outer(this, v, this);
    }

    public Tensor imul(final Tensor m) {
        return (Tensor) MFuncs.Mul.outer(this, m, this);
    }

    public Tensor mul(final float v) {
        return new Tensor(MFuncs.Mul.outer(this, v, null));
    }

    public Tensor mul(final Tensor m) {
        return new Tensor(MFuncs.Mul.outer(this, m, null));
    }

    public Tensor idiv(final float v) {
        return (Tensor) MFuncs.Div.outer(this, v, this);
    }

    public Tensor idiv(final Tensor m) {
        return (Tensor) MFuncs.Div.outer(this, m, this);
    }

    public Tensor div(final float v) {
        return new Tensor(MFuncs.Div.outer(this, v, null));
    }

    public Tensor div(final Tensor m) {
        return new Tensor(MFuncs.Div.outer(this, m, null));
    }

    public Tensor isqrt() {
        return (Tensor) MFuncs.Sqrt.outer(this, 0.0f, this);
    }

    public Tensor sqrt() {
        return new Tensor(MFuncs.Sqrt.outer(this, 0.0f, null));
    }

    public Tensor ipow(final float n) {
        return (Tensor) MFuncs.Sqrt.outer(this, n, this);
    }

    public Tensor pow(final float n) {
        return new Tensor(MFuncs.Pow.outer(this, n, null));
    }

    public Tensor dot(final Tensor m, final int axis) {
        return new Tensor(MFuncs.Mul.inner(this, m, 0.0f, axis, null));
    }

    public Tensor outer(final Tensor m) {
        return new Tensor(MFuncs.Mul.outer(this, m, null));
    }

    public Tensor sum(final int axis) {
        return new Tensor(MFuncs.Add.reduce(this, 0.0f, axis, null));
    }

    public Tensor avg(final int axis) {
        final float b = (axis == -1) ? this.size : this.shape[axis];
        return this.sum(axis).idiv(b);
    }

    public Tensor var(final int axis) {
        final float n = (axis == -1) ? this.size : this.shape[axis];
        final Tensor avg = this.avg(axis);
        final Tensor var = new Tensor(MFuncs.MagSq.inner(this, avg, 0.0f, axis, null));
        return var.idiv(n - 1);
    }

    public Tensor cov(final Tensor v, final int axis) {
        final float n = (axis == -1) ? this.size : this.shape[axis];
        final Tensor avg1 = this.avg(axis);
        final Tensor tmp1 = this.sub(avg1);
        final Tensor avg2 = v.avg(axis);
        final Tensor tmp2 = v.sub(avg2);
        final Tensor cov = new Tensor(MFuncs.Mul.inner(tmp1, tmp2, 0.0f, axis, null));
        return cov.idiv(n - 1);
    }

    public Tensor normSq(final int axis) {
        return new Tensor(MFuncs.SumSq.reduce(this, 0.0f, axis, null));
    }

    public Tensor norm(final int axis) {
        return this.normSq(axis).isqrt();
    }

    public Tensor magSq(final Tensor m, final int axis) {
        return new Tensor(MFuncs.MagSq.inner(this, m, 0.0f, axis, null));
    }

    public Tensor mag(final Tensor m, final int axis) {
        return this.magSq(m, axis).isqrt();
    }

    public boolean isSimilar(final Tensor v, final float e) {
        return Arrays.equals(this.shape, v.shape) && (1.0f - Scalar.abs(this.similarity(v, -1).item(0)) <= e);
    }

    public Tensor sparsity(final int axis) {
        final float b = (axis == -1) ? this.size : this.shape[axis];
        MArray tmp = MFuncs.SumSparse.reduce(this, 0.0f, axis, null);
        return new Tensor(MFuncs.Div.outer(tmp, b, tmp));
    }

    public Tensor similarity(final Tensor v, final int axis) {
        if(this.sparsity(axis).equals(1.0f) || v.sparsity(axis).equals(1.0f)) {
            return new Tensor(this.shape).zeros();
        } else {
            return this.dot(v, axis).div(this.norm(axis).mul(v.norm(axis)));
        }
    }
}
