package com.github.romualdrousseau.shuju.math;

public class MArray {

    public int size;
    public int[] shape;
    public int[] stride;
    public float[] data;
    public boolean transposed;
    public boolean copied;

    public MArray(final int... shape) {
        this.shape = shape.clone();
        this.updateSize();
        this.updateStrides();
        this.data = new float[this.size];
        this.transposed = false;
        this.copied = false;
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

    public MArray zero() {
        return new UFunc0((x, y) -> 0.0f).call(this, this);
    }

    public MArray ones() {
        return new UFunc0((x, y) -> 1.0f).call(this, this);
    }

    public MArray arrange() {
        return new UFunc0((x, y) -> 1.0f).accumulate(this, 0, this);
    }

    public MArray iadd(final float v) {
        return new UFunc0((x, y) -> x + v).call(this, this);
    }

    public MArray iadd(final MArray m) {
        return new UFunc0((x, y) -> x + y).inner(this, m, this);
    }

    public MArray isub(final float v) {
        return new UFunc0((x, y) -> x - v).call(this, this);
    }

    public MArray isub(final MArray m) {
        return new UFunc0((x, y) -> x - y).inner(this, m, this);
    }

    public MArray imul(final float v) {
        return new UFunc0((x, y) -> x * v).call(this, this);
    }

    public MArray imul(final MArray m) {
        return new UFunc0((x, y) -> x * y).inner(this, m, this);
    }

    public MArray idiv(final float v) {
        return new UFunc0((x, y) -> x / v).call(this, this);
    }

    public MArray idiv(final MArray m) {
        return new UFunc0((x, y) -> x / y).inner(this, m, this);
    }

    public MArray max(int axis) {
        return new UFunc0((x, y) -> x > y ? x : y).reduce(this, axis, null);
    }

    public MArray min(int axis) {
        return new UFunc0((x, y) -> x < y ? x : y).reduce(this, axis, null);
    }

    public MArray sum(int axis) {
        return new UFunc0((x, y) -> x + y).reduce(this, axis, null);
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
