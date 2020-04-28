package com.github.romualdrousseau.shuju.math;

import java.util.Arrays;

public class MArray {

    public int size;
    public int[] shape;
    public int[] stride;
    public float[] data;
    public boolean copied;

    public static final UFunc0 Full = new UFunc0((x, y) -> y);

    public static final UFunc0 Random = new UFunc0((x, y) -> Scalar.random(y));

    public static final UFunc0 Chop = new UFunc0((x, y) -> (Scalar.abs(x) < y) ? 0.0f : x);

    public static final UFunc0 Add = new UFunc0((x, y) -> x + y);

    public static final UFunc0 Sub = new UFunc0((x, y) -> x - y);

    public static final UFunc0 Mul = new UFunc0((x, y) -> x * y);

    public static final UFunc0 Div = new UFunc0((x, y) -> x / y);

    public static final UFunc0 Max = new UFunc0((x, y) -> x > y ? x : y);

    public static final UFunc0 Min = new UFunc0((x, y) -> x < y ? x : y);

    public static final UFunc0 Sqrt = new UFunc0((x, y) -> Scalar.sqrt(x));

    public static final UFunc0 InvSqrt = new UFunc0((x, y) -> 1.0f / (Scalar.sqrt(x) + Scalar.EPSILON));

    public static final UFunc0 Pow2 = new UFunc0((x, y) -> Scalar.pow(x, y));

    public static final UFunc0 MagSq = new UFunc0((x, y) -> Scalar.pow(x - y, 2));

    public MArray() {
    }

    public MArray(final int... shape) {
        this.shape = shape.clone();
        this.updateSize();
        this.updateStrides();
        this.data = new float[this.size];
        this.copied = false;
    }

    public boolean isNull() {
        return this.shape.length == 0;
    }

    public float[] getFloats() {
        return this.data;
    }

    public float getItem(int i) {
        return this.data[i];
    }

    public MArray setItem(int i, float v) {
        this.data[i] = v;
        return this;
    }

    public boolean equals(final MArray v) {
        return this.size == v.size && Arrays.equals(this.data, v.data);
    }

    public boolean equals(final MArray v, final float e) {
        boolean result = this.size == v.size;
        for (int i = 0; i < this.size && result; i++) {
            float a = this.data[i];
            float b = v.data[i];
            result &= Math.abs(a - b) < e;
        }
        return result;
    }

    public MArray reshape(final int... shape) {
        this.shape = shape.clone();
        this.updateStrides();
        return this;
    }

    public MArray transpose() {
        final int[] indices = new int[this.shape.length];
        for (int i = 0; i < indices.length; i++) {
            indices[i] = indices.length - i - 1;
        }
        return this.transpose(indices);
    }

    public MArray transpose(final int... indices) {
        int swp;

        final int[] done = new int[indices.length];
        for (int i = 0; i < indices.length; i++) {
            done[i] = -1;
        }

        for (int i = 0; i < indices.length; i++) {
            final int j = indices[i];
            if (j != i && done[j] == -1) {
                swp = this.shape[i];
                this.shape[i] = this.shape[j];
                this.shape[j] = swp;

                swp = this.stride[i];
                this.stride[i] = this.stride[j];
                this.stride[j] = swp;

                done[i] = 1;
                done[j] = 1;
            }
        }

        return this;
    }

    public String toString() {
        final StringBuilder sb = new StringBuilder();
        this.toString(0, 0, sb, false, "%1$10.3f");
        return sb.toString();
    }

    private void toString(final int n, int o, final StringBuilder sb, final boolean indent, final String format) {
        if (indent) {
            sb.append(System.lineSeparator());
            for (int i = 0; i < n; i++)
                sb.append(' ');
        }

        sb.append('[');

        if (this.shape.length - n == 1) {
            sb.append(' ');
            for (int i = 0; i < this.shape[n]; i++) {
                sb.append(String.format(format, this.data[o])).append(' ');
                o += this.stride[n];
            }
        } else {
            for (int i = 0; i < this.shape[n]; i++) {
                this.toString(n + 1, o, sb, i > 0, format);
                o += this.stride[n];
            }
        }

        sb.append(']');
    }

    private void updateSize() {
        this.size = 1;
        for (final int s : this.shape) {
            this.size *= s;
        }
    }

    private void updateStrides() {
        this.stride = new int[this.shape.length];
        this.stride[this.stride.length - 1] = 1;
        for (int i = this.stride.length - 2; i >= 0; i--) {
            this.stride[i] = this.stride[i + 1] * this.shape[i + 1];
        }
    }
}
