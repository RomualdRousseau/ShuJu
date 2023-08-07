package com.github.romualdrousseau.shuju.types;

import java.util.Arrays;

import org.tensorflow.ndarray.Shape;
import org.tensorflow.ndarray.buffer.DataBuffers;
import org.tensorflow.types.TFloat32;

import com.github.romualdrousseau.shuju.core.MArray;
import com.github.romualdrousseau.shuju.linalg.MatMul;
import com.github.romualdrousseau.shuju.math.Avg;
import com.github.romualdrousseau.shuju.math.Cov;
import com.github.romualdrousseau.shuju.math.MathOps;
import com.github.romualdrousseau.shuju.math.Var;
import com.github.romualdrousseau.shuju.nn.BatchNorm;
import com.github.romualdrousseau.shuju.nn.L2Norm;
import com.github.romualdrousseau.shuju.nn.Softmax;

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

    public static Tensor of(final MArray data) {
        return (Tensor) new Tensor(data);
    }

    public static Tensor of(final float... data) {
        return (Tensor) new Tensor(data.length).setItems(data);
    }

    public static Tensor of(final double... data) {
        return (Tensor) new Tensor(data.length).setItems(data);
    }

    public static Tensor of(final float[][] data) {
        return (Tensor) new Tensor(data.length, data[0].length).setItems(data);
    }

    public static Tensor of(final TFloat32 v) {
        final int[] shape = Arrays.stream(v.shape().asArray()).mapToInt(i -> (int) i).toArray();
        final double[] data = v.streamOfObjects().mapToDouble(i -> (double) i).toArray();
        return (Tensor) new Tensor(shape).setItems(data);
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
        return Tensor.of(super.view().transpose());
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
        return Tensor.of(super.view());
    }

    @Override
    public Tensor view(final int... slice) {
        return Tensor.of(super.view(slice));
    }

    @Override
    public Tensor repeat(final int n) {
        return Tensor.of(super.repeat(n));
    }

    @Override
    public Tensor stack(final MArray v) {
        return Tensor.of(super.stack(v));
    }

    @Override
    public Tensor copy() {
        return Tensor.of(super.copy());
    }

    public Tensor mutate(final float rate, final float variance) {
        return (Tensor) MathOps.mutate.apply(rate, variance).outer(this, 0.0f, this);
    }

    public Tensor randomize() {
        return this.randomize(1.0f);
    }

    public Tensor randomize(final float n) {
        return this.randomize(-n, n);
    }

    public Tensor randomize(final float a, final float b) {
        return (Tensor) MathOps.randomize.apply(a, b).outer(this, 0.0f, this);
    }

    public Tensor constrain(final float a, final float b) {
        return (Tensor) MathOps.constrain.apply(a, b).outer(this, 0.0f, this);
    }

    public Tensor if_lt_then(final float p, final float a, final float b) {
        return (Tensor) MathOps.if_lt_then.apply(p, a, b).outer(this, 0.0f, this);
    }

    public Tensor chop(final float e) {
        return (Tensor) MathOps.chop.apply(e).outer(this, 0.0f, this);
    }

    public Tensor iadd(final float v) {
        return (Tensor) MathOps.Add.outer(this, v, this);
    }

    public Tensor iadd(final Tensor m) {
        return (Tensor) MathOps.Add.outer(this, m, this);
    }

    public Tensor add(final float v) {
        return Tensor.of(MathOps.Add.outer(this, v, null));
    }

    public Tensor add(final Tensor m) {
        return Tensor.of(MathOps.Add.outer(this, m, null));
    }

    public Tensor isub(float v) {
        return (Tensor) MathOps.Sub.outer(this, v, this);
    }

    public Tensor isub(Tensor m) {
        return (Tensor) MathOps.Sub.outer(this, m, this);
    }

    public Tensor sub(final float v) {
        return Tensor.of(MathOps.Sub.outer(this, v, null));
    }

    public Tensor sub(final Tensor m) {
        return Tensor.of(MathOps.Sub.outer(this, m, null));
    }

    public Tensor imul(final float v) {
        return (Tensor) MathOps.Mul.outer(this, v, this);
    }

    public Tensor imul(final Tensor m) {
        return (Tensor) MathOps.Mul.outer(this, m, this);
    }

    public Tensor mul(final float v) {
        return Tensor.of(MathOps.Mul.outer(this, v, null));
    }

    public Tensor mul(final Tensor m) {
        return Tensor.of(MathOps.Mul.outer(this, m, null));
    }

    public Tensor idiv(final float v) {
        return (Tensor) MathOps.Div.outer(this, v, this);
    }

    public Tensor idiv(final Tensor m) {
        return (Tensor) MathOps.Div.outer(this, m, this);
    }

    public Tensor div(final float v) {
        return Tensor.of(MathOps.Div.outer(this, v, null));
    }

    public Tensor div(final Tensor m) {
        return Tensor.of(MathOps.Div.outer(this, m, null));
    }

    public Tensor isqrt() {
        return (Tensor) MathOps.Sqrt.outer(this, 0.0f, this);
    }

    public Tensor sqrt() {
        return Tensor.of(MathOps.Sqrt.outer(this, 0.0f, null));
    }

    public Tensor iinvsqrt() {
        return (Tensor) MathOps.InvSqrt.outer(this, 0.0f, this);
    }

    public Tensor invsqrt() {
        return Tensor.of(MathOps.InvSqrt.outer(this, 0.0f, null));
    }

    public Tensor iinv() {
        return (Tensor) MathOps.Inv.outer(this, 0.0f, this);
    }

    public Tensor inv() {
        return Tensor.of(MathOps.Inv.outer(this, 0.0f, null));
    }

    public Tensor isquare() {
        return (Tensor) MathOps.Square.outer(this, 0.0f, this);
    }

    public Tensor square() {
        return Tensor.of(MathOps.Square.outer(this, 0.0f, null));
    }

    public Tensor ipow(final float n) {
        return (Tensor) MathOps.Sqrt.outer(this, n, this);
    }

    public Tensor pow(final float n) {
        return Tensor.of(MathOps.Pow.outer(this, n, null));
    }

    public Tensor iexp() {
        return (Tensor) MathOps.Exp.outer(this, 0.0f, this);
    }

    public Tensor exp() {
        return Tensor.of(MathOps.Exp.outer(this, 0.0f, null));
    }

    public Tensor ilog() {
        return (Tensor) MathOps.Log.outer(this, 0.0f, this);
    }

    public Tensor log() {
        return Tensor.of(MathOps.Log.outer(this, 0.0f, null));
    }

    public Tensor dot(final Tensor m, final int axis) {
        return Tensor.of(MathOps.Mul.inner(this, m, 0.0f, axis, false, null));
    }

    public Tensor outer(final Tensor m) {
        return Tensor.of(MathOps.Mul.outer(this, m, null));
    }

    public Tensor argmax(final int axis) {
        return Tensor.of(MathOps.ArgMax.reduce(this, Float.MIN_VALUE, axis, false, null));
    }

    public Tensor argmin(final int axis) {
        return Tensor.of(MathOps.ArgMin.reduce(this, Float.MIN_VALUE, axis, false, null));
    }

    public Tensor max(final int axis) {
        return Tensor.of(MathOps.Max.reduce(this, Float.MIN_VALUE, axis, false, null));
    }

    public Tensor min(final int axis) {
        return Tensor.of(MathOps.Min.reduce(this, Float.MAX_VALUE, axis, false, null));
    }

    public Tensor sum(final int axis) {
        return Tensor.of(MathOps.Add.reduce(this, 0.0f, axis, false, null));
    }

    public Tensor normSq(final int axis) {
        return Tensor.of(MathOps.SumSq.reduce(this, 0.0f, axis, false, null));
    }

    public Tensor magSq(final Tensor m, final int axis) {
        return Tensor.of(MathOps.MagSq.inner(this, m, 0.0f, axis, false, null));
    }

    public Tensor matmul(final Tensor m) {
        return MatMul.Op.apply(this, m);
    }

    public Tensor norm(final int axis) {
        return this.normSq(axis).isqrt();
    }

    public Tensor mag(final Tensor m, final int axis) {
        return this.magSq(m, axis).isqrt();
    }

    public Tensor avg(final int axis) {
        return Avg.Op(this, axis);
    }

    public Tensor var(final int axis, final float ddof) {
        return Var.Op(this, axis, ddof);
    }

    public Tensor cov(final Tensor v, final boolean rowvar, final float ddof) {
        return Cov.Op(this, v, rowvar, ddof);
    }

    public boolean isSimilar(final Tensor v, final float e) {
        return Arrays.equals(this.shape, v.shape) && (1.0f - Scalar.abs(this.similarity(v, -1).item(0)) <= e);
    }

    public Tensor sparsity(final int axis) {
        final float b = (axis == -1) ? this.size : this.shape[axis];
        MArray tmp = MathOps.SumSparse.reduce(this, 0.0f, axis, false, null);
        return new Tensor(MathOps.Div.outer(tmp, b, tmp));
    }

    public Tensor similarity(final Tensor v, final int axis) {
        if (this.sparsity(axis).equals(1.0f, 0.0f) || v.sparsity(axis).equals(1.0f, 0.0f)) {
            return Tensor.zeros(this.shape);
        } else {
            return this.dot(v, axis).div(this.norm(axis).mul(v.norm(axis)));
        }
    }

    public Tensor l2Norm(final int axis) {
        return L2Norm.Op(this, axis);
    }

    public Tensor batchNorm(final float a, final float b, final int axis) {
        return BatchNorm.Op(this, a, b, axis);
    }

    public Tensor softmax(final int axis) {
        return Softmax.Op(this, axis);
    }

    public TFloat32 toTFloat32() {
        return TFloat32.tensorOf(
                Shape.of(Arrays.stream(this.shape).mapToLong(x -> (long) x).toArray()),
                DataBuffers.of(this.data));
    }
}
