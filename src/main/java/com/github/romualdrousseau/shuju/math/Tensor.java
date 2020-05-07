package com.github.romualdrousseau.shuju.math;

import java.util.Arrays;

public class Tensor extends MArray {

    public static final Tensor Null = new Tensor();

    public Tensor() {
    }

    public Tensor(final int... shape) {
        super(shape);
    }

    public Tensor(MArray other) {
        super(other);
    }

    public Tensor T() {
        return new Tensor(super.view().transpose());
    }

    @Override
    public Tensor ravel() {
        return (Tensor) super.ravel();
    }

    @Override
    public Tensor squeeze() {
        return (Tensor) super.squeeze();
    }

    @Override
    public Tensor reshape(final int... shape) {
        return (Tensor) super.reshape(shape);
    }

    @Override
    public Tensor transpose() {
        return (Tensor) super.transpose();
    }

    @Override
    public Tensor transpose(final int... indices) {
        return (Tensor) super.transpose(indices);
    }

    @Override
    public Tensor view() {
        return new Tensor(super.view());
    }

    @Override
    public Tensor view(final int... slice) {
        return new Tensor(super.view(slice));
    }

    @Override
    public Tensor repeat(final int n) {
        return new Tensor(super.repeat(n));
    }

    @Override
    public Tensor stack(final MArray v) {
        return new Tensor(super.stack(v));
    }

    @Override
    public Tensor copy() {
        return new Tensor(super.copy());
    }

    public Tensor zeros() {
        return this.fill(0.0f);
    }

    public Tensor ones() {
        return this.fill(1.0f);
    }

    // TODO: Use UFunc ?
    public Tensor fill(final float v) {
        for(int i = 0; i < this.size; i++) {
            this.setItem(i, v);
        }
        return this;
    }

    public Tensor fill(final float... data) {
        return (Tensor) this.setItems(data);
    }

    public Tensor fill(final float[][] data) {
        return (Tensor) this.setItems(data);
    }

    // TODO: Use UFunc
    public Tensor arange(final float start, final float step) {
        for(int i = 0; i < this.size; i++) {
            this.setItem(i, start + i * step);
        }
        return this;
    }

    public Tensor linspace(final float start, final float stop) {
        final float step = (stop - start) / (float) (this.size - 1);
        return arange(start, step);
    }

    public Tensor eye(final int k) {
        assert (this.shape.length == 2) : "Illegal shape";
        for(int i = 0; i < this.shape[0]; i++) {
            for(int j = 0; j < this.shape[1]; j++) {
                if (i == j - k) {
                    this.setItem(this.offset(i, j), 1);
                } else {
                    this.setItem(this.offset(i, j), 0);
                }
            }
        }
        return this;
    }

    public Tensor oneHot(final int n) {
        assert (this.shape.length == 1) : "Illegal shape";
        for(int i = 0; i < this.shape[0]; i++) {
            if (i == n) {
                this.setItem(i, 1);
            }
        }
        return this;
    }

    public <T extends Enum<T>> Tensor oneHot(T e) {
        this.zeros();
        if (e != null) {
            this.oneHot(e.ordinal());
        }
        return this;
    }

    public <T extends Enum<T>> Tensor oneHot(T[] s) {
        this.zeros();
        if (s != null) {
            for (int i = 0; i < s.length; i++) {
                this.oneHot(s[i].ordinal());
            }
        }
        return this;
    }

    public Tensor softmax() {
        assert (this.shape.length == 1) : "Illegal shape";

        final float c = -this.data[(int) this.argmax(-1).item(0)];

        float sum = 0.0f;
        for (int i = 0; i < this.shape[0]; i++) {
            sum += Scalar.exp(this.item(i) + c);
        }
        final float w = 1.0f / sum;

        for (int i = 0; i < this.shape[0]; i++) {
            this.setItem(i, Scalar.exp(this.item(i) + c) * w);
        }

        return this;
    }

    // TODO: Use UFunc
    public Tensor mutate(float rate, final float variance) {
        for (int i = 0; i < this.size; i++) {
            if (Scalar.random(1.0f) < rate) {
                this.setItem(i, Scalar.randomGaussian() * variance);
            }
        }
        return this;
    }

    public Tensor randomize() {
        return new Tensor(MFuncs.Randomize.outer(this, 1.0f, null));
    }

    public Tensor randomize(final float n) {
        return new Tensor(MFuncs.Randomize.outer(this, n, null));
    }

    // TODO: Use UFunc
    public Tensor constrain(float a, float b) {
        for (int i = 0; i < this.size; i++) {
            this.setItem(i, Scalar.constrain(this.data[i], a, b));
        }
        return this;
    }

    // TODO: Use UFunc
    public Tensor if_lt_then(float p, float a, float b) {
        for (int i = 0; i < this.size; i++) {
            this.setItem(i, Scalar.if_lt_then(this.item(i), p, a, b));
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

    public Tensor iinvsqrt() {
        return (Tensor) MFuncs.InvSqrt.outer(this, 0.0f, this);
    }

    public Tensor invsqrt() {
        return new Tensor(MFuncs.InvSqrt.outer(this, 0.0f, null));
    }

    public Tensor iinv() {
        return (Tensor) MFuncs.Inv.outer(this, 0.0f, this);
    }

    public Tensor inv() {
        return new Tensor(MFuncs.Inv.outer(this, 0.0f, null));
    }

    public Tensor isquare() {
        return (Tensor) MFuncs.Square.outer(this, 0.0f, this);
    }

    public Tensor square() {
        return new Tensor(MFuncs.Square.outer(this, 0.0f, null));
    }

    public Tensor ipow(final float n) {
        return (Tensor) MFuncs.Sqrt.outer(this, n, this);
    }

    public Tensor pow(final float n) {
        return new Tensor(MFuncs.Pow.outer(this, n, null));
    }

    public Tensor dot(final Tensor m, final int axis) {
        return new Tensor(MFuncs.Mul.inner(this, m, 0.0f, axis, false, null));
    }

    public Tensor outer(final Tensor m) {
        return new Tensor(MFuncs.Mul.outer(this, m, null));
    }

    public Tensor argmax(final int axis) {
        return new Tensor(MFuncs.ArgMax.reduce(this, Float.MIN_VALUE, axis, false, null));
    }

    public Tensor argmin(final int axis) {
        return new Tensor(MFuncs.ArgMin.reduce(this, Float.MIN_VALUE, axis, false, null));
    }

    public Tensor max(final int axis) {
        return new Tensor(MFuncs.Max.reduce(this, Float.MIN_VALUE, axis, false, null));
    }

    public Tensor min(final int axis) {
        return new Tensor(MFuncs.Min.reduce(this, Float.MAX_VALUE, axis, false, null));
    }

    public Tensor sum(final int axis) {
        return new Tensor(MFuncs.Add.reduce(this, 0.0f, axis, false, null));
    }

    public Tensor normSq(final int axis) {
        return new Tensor(MFuncs.SumSq.reduce(this, 0.0f, axis, false, null));
    }

    public Tensor norm(final int axis) {
        return this.normSq(axis).isqrt();
    }

    public Tensor magSq(final Tensor m, final int axis) {
        return new Tensor(MFuncs.MagSq.inner(this, m, 0.0f, axis, false, null));
    }

    public Tensor mag(final Tensor m, final int axis) {
        return this.magSq(m, axis).isqrt();
    }

    public Tensor matmul(final Tensor m) {
        return new Tensor(MFuncs.MatMul.outer(this, m, null));
    }

    public Tensor avg(final int axis) {
        final float b = (axis == -1) ? this.size : this.shape[axis];
        return this.sum(axis).idiv(b);
    }

    public Tensor var(final int axis, final float ddof) {
        final float n = (axis == -1) ? this.size : this.shape[axis];
        final Tensor avg = new Tensor(MFuncs.Add.reduce(this, 0.0f, axis, true, null)).idiv(n);
        final Tensor var = new Tensor(MFuncs.MagSq.inner(this, avg, 0.0f, axis, false, null));
        return var.idiv(n - ddof);
    }

    public Tensor cov(final Tensor v, final int axis, final float ddof) {
        assert (this.shape.length == v.shape.length) : "Illegal shape";

        final float n1 = (axis == -1) ? this.size : this.shape[axis];
        final Tensor avg1 = new Tensor(MFuncs.Add.reduce(this, 0.0f, axis, true, null)).idiv(n1);
        final Tensor tmp1 = this.sub(avg1);

        final Tensor tmp2;
        if (this == v) {
            tmp2 = tmp1;
        } else {
            final float n2 = (axis == -1) ? v.size : v.shape[axis];
            final Tensor avg2 = new Tensor(MFuncs.Add.reduce(v, 0.0f, axis, true, null)).idiv(n2);
            tmp2 = v.sub(avg2);
        }

        final Tensor cov = new Tensor(MFuncs.Mul.inner(tmp1, tmp2, 0.0f, axis, false, null));
        return cov.idiv(n1 - ddof);
    }

    public Tensor cov2(final Tensor v, final int axis, final float ddof) {
        assert (this.shape.length == v.shape.length) : "Illegal shape";

        final float n1 = (axis == -1) ? this.size : this.shape[axis];
        final Tensor avg1 = new Tensor(MFuncs.Add.reduce(this, 0.0f, axis, true, null)).idiv(n1);
        final Tensor tmp1 = this.sub(avg1);
        if (axis == 0) {
            tmp1.transpose();
        }

        final Tensor tmp2;
        if (this == v) {
            tmp2 = tmp1.T();
        } else {
            final float n2 = (axis == -1) ? v.size : v.shape[axis];
            final Tensor avg2 = new Tensor(MFuncs.Add.reduce(v, 0.0f, axis, true, null)).idiv(n2);
            tmp2 = v.sub(avg2);
            if (axis == 1) {
                tmp2.transpose();
            }
        }

        final Tensor cov = tmp1.matmul(tmp2);
        return cov.idiv(n1 - ddof);
    }

    public Tensor l2Norm(final int axis) {
        return this.imul(this.norm(axis).iinv());
    }

    public Tensor batchNorm(final float a, final float b, final int axis) {
        final Tensor avg = this.avg(axis);
        final Tensor invvar = this.var(axis, 0).invsqrt();
        return this.isub(avg).imul(invvar).imul(a).iadd(b);
    }

    public boolean isSimilar(final Tensor v, final float e) {
        return Arrays.equals(this.shape, v.shape) && (1.0f - Scalar.abs(this.similarity(v, -1).item(0)) <= e);
    }

    public Tensor sparsity(final int axis) {
        final float b = (axis == -1) ? this.size : this.shape[axis];
        MArray tmp = MFuncs.SumSparse.reduce(this, 0.0f, axis, false, null);
        return new Tensor(MFuncs.Div.outer(tmp, b, tmp));
    }

    public Tensor similarity(final Tensor v, final int axis) {
        if(this.sparsity(axis).equals(1.0f, 0.0f) || v.sparsity(axis).equals(1.0f, 0.0f)) {
            return new Tensor(this.shape).zeros();
        } else {
            return this.dot(v, axis).div(this.norm(axis).mul(v.norm(axis)));
        }
    }
}
