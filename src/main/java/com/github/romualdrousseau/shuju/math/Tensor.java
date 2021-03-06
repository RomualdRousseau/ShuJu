package com.github.romualdrousseau.shuju.math;

import java.util.Arrays;

public class Tensor extends MArray {

    public static final Tensor Null = new Tensor();

    private Tensor() {
    }

    private Tensor(final int... shape) {
        super(shape);
    }

    private Tensor(MArray other) {
        super(other);
    }

    public static Tensor empty(final int... shape) {
        return new Tensor(shape);
    }

    public static Tensor zeros(final int... shape) {
        return (Tensor) new Tensor(shape).setItems(0.0f);
    }

    public static Tensor ones(final int... shape) {
        return (Tensor) new Tensor(shape).setItems(1.0f);
    }

    public static Tensor full(final float v, final int... shape) {
        return (Tensor) new Tensor(shape).setItems(v);
    }

    public static Tensor empty_like(final Tensor m) {
        return Tensor.empty(m.shape);
    }

    public static Tensor zeros_like(final Tensor m) {
        return Tensor.zeros(m.shape);
    }

    public static Tensor ones_like(final Tensor m) {
        return Tensor.ones(m.shape);
    }

    public static Tensor full_like(final float v, final Tensor m) {
        return Tensor.full(v, m.shape);
    }

    public static Tensor create(final float... data) {
        return (Tensor) new Tensor(data.length).setItems(data);
    }

    public static Tensor create(final float[][] data) {
        return (Tensor) new Tensor(data.length, data[0].length).setItems(data);
    }

    public static Tensor linspace(final float start, final float stop, final int num) {
        final float step = (stop - start) / (float) (num - 1);
        return Tensor.arange(start, stop, step);
    }

    public static Tensor arange(final float start, final float stop, final float step) {
        final int size = (int) ((stop - start) / step);
        final Tensor result = new Tensor(size);
        result.data[0] = start;
        for (int i = 1; i < result.size; i++) {
            result.data[i] = result.data[i - 1] + step;
        }
        return result;
    }

    public static Tensor eye(final int rows, int cols, final int k) {
        final Tensor result = new Tensor(rows, cols);
        final int r = Math.max(0, -k);
        final int c = Math.max(0, k);
        final int m = Math.min(rows - r, cols - c);
        for (int i = 0; i < m; i++) {
            final int off = result.offset(i + r, i + c);
            result.data[off] = 1;
        }
        return result;
    }

    public static Tensor oneHot(final int n, final int size) {
        return (Tensor) Tensor.zeros(size).setItem(n, 1);
    }

    public static <T extends Enum<T>> Tensor oneHot(T e, final int size) {
        final Tensor result = Tensor.zeros(size);
        if (e != null) {
            result.setItem(e.ordinal(), 1);
        }
        return result;
    }

    public static <T extends Enum<T>> Tensor oneHot(T[] s) {
        final Tensor result = Tensor.zeros(s.length);
        if (s != null) {
            for (int i = 0; i < s.length; i++) {
                result.setItem(s[i].ordinal(), 1);
            }
        }
        return result;
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

    public Tensor mutate(final float rate, final float variance) {
        return (Tensor) MFuncs.mutate.apply(rate, variance).outer(this, 0.0f, this);
    }

    public Tensor randomize() {
        return this.randomize(1.0f);
    }

    public Tensor randomize(final float n) {
        return this.randomize(-n, n);
    }

    public Tensor randomize(final float a, final float b) {
        return (Tensor) MFuncs.randomize.apply(a, b).outer(this, 0.0f, this);
    }

    public Tensor constrain(final float a, final float b) {
        return (Tensor) MFuncs.constrain.apply(a, b).outer(this, 0.0f, this);
    }

    public Tensor if_lt_then(final float p, final float a, final float b) {
        return (Tensor) MFuncs.if_lt_then.apply(p, a, b).outer(this, 0.0f, this);
    }

    public Tensor chop(final float e) {
        return (Tensor) MFuncs.chop.apply(e).outer(this, 0.0f, this);
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

    public Tensor iexp() {
        return (Tensor) MFuncs.Exp.outer(this, 0.0f, this);
    }

    public Tensor exp() {
        return new Tensor(MFuncs.Exp.outer(this, 0.0f, null));
    }

    public Tensor ilog() {
        return (Tensor) MFuncs.Log.outer(this, 0.0f, this);
    }

    public Tensor log() {
        return new Tensor(MFuncs.Log.outer(this, 0.0f, null));
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

    public Tensor magSq(final Tensor m, final int axis) {
        return new Tensor(MFuncs.MagSq.inner(this, m, 0.0f, axis, false, null));
    }

    public Tensor matmul(final Tensor m) {
        return new Tensor(MFuncs.MatMul.outer(this, m, null));
    }

    public Tensor norm(final int axis) {
        return this.normSq(axis).isqrt();
    }

    public Tensor mag(final Tensor m, final int axis) {
        return this.magSq(m, axis).isqrt();
    }

    public Tensor avg(final int axis) {
        final float n = (axis == -1) ? this.size : this.shape[axis];
        return this.sum(axis).idiv(n);
    }

    public Tensor var(final int axis, final float ddof) {
        final float n = (axis == -1) ? this.size : this.shape[axis];
        final Tensor avg = new Tensor(MFuncs.Add.reduce(this, 0.0f, axis, true, null)).idiv(n);
        final Tensor var = new Tensor(MFuncs.MagSq.inner(this, avg, 0.0f, axis, false, null));
        return var.idiv(n - ddof);
    }

    public Tensor cov(final Tensor v, final boolean rowvar, final float ddof) {
        assert (this.shape.length <= 2 && this.shape.length == v.shape.length) : "Illegal shape";

        final int axis = rowvar ? 0 : 1;

        final float n1 = this.shape[axis];
        final Tensor avg1 = new Tensor(MFuncs.Add.reduce(this, 0.0f, axis, true, null)).idiv(n1);
        final Tensor step1 = this.sub(avg1);
        if (axis == 0) {
            step1.transpose();
        }

        final Tensor step2;
        if (this == v) {
            step2 = step1.T();
        } else {
            final float n2 = v.shape[axis];
            final Tensor avg2 = new Tensor(MFuncs.Add.reduce(v, 0.0f, axis, true, null)).idiv(n2);
            step2 = v.sub(avg2);
            if (axis == 1) {
                step2.transpose();
            }
        }

        final Tensor cov = new Tensor(MFuncs.MatMul.outer(step1, step2, null));
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

    public Tensor softmax(final int axis) {
        final Tensor c = this.max(axis);
        new UFunc0((x, y) -> Scalar.exp(y - x)).outer(this, c, this);
        final Tensor sum = this.sum(axis);
        return this.idiv(sum);
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
        if (this.sparsity(axis).equals(1.0f, 0.0f) || v.sparsity(axis).equals(1.0f, 0.0f)) {
            return Tensor.zeros(this.shape);
        } else {
            return this.dot(v, axis).div(this.norm(axis).mul(v.norm(axis)));
        }
    }
}
