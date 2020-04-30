package com.github.romualdrousseau.shuju.math;

import java.util.Arrays;

public class MArray {

    public int size;
    public int[] shape;
    public int[] stride;
    public float[] data;
    public boolean copied;
    public int base;

    public MArray() {
    }

    public MArray(final int... shape) {
        this.shape = shape.clone();
        this.updateSize();
        this.updateStrides();
        this.data = new float[this.size];
        this.base = 0;
        this.copied = false;
    }

    public MArray(MArray parent, final int... slice) {
        this.shape = Arrays.copyOfRange(slice, 1, slice.length);
        this.updateSize();

        this.stride = new int[this.shape.length];
        for (int i = 0; i < this.shape.length; i++) {
            this.stride[i] = parent.stride[i];
        }

        this.data = parent.data;
        this.base = slice[0];
        this.copied = false;
    }

    public MArray(MArray other) {
        this(other, false);
    }

    public MArray(MArray other, boolean copy) {
        if (copy) {
            this.shape = other.shape.clone();
            this.size = other.size;
            this.stride = other.stride.clone();
            this.data = other.data;
            this.base = other.base;
            this.copied = true;
        } else {
            this.shape = other.shape;
            this.size = other.size;
            this.stride = other.stride;
            this.data = other.data;
            this.base = other.base;
            this.copied = other.copied;
        }
    }

    public float[] floats() {
        return this.data;
    }

    public MArray setFloats(final float... data) {
        assert (this.size == data.length);
        this.dupDataIf(this.copied, false);
        System.arraycopy(data, 0, this.data, this.base, data.length);
        return this;
    }

    public MArray setFloats(final float[][] data) {
        assert (this.size == data.length * data[0].length);
        this.dupDataIf(this.copied, false);
        for (int i = 0; i < data.length; i++) {
            System.arraycopy(data[i], 0, this.data, i * data[0].length, data[0].length);
        }
        return this;
    }

    public float item(int off) {
        return this.data[this.base + off];
    }

    public MArray setItem(int off, float v) {
        this.data[this.base + off] = v;
        return this;
    }

    public int offset(int... indices) {
        int result = 0;
        for (int i = 0; i < indices.length; i++) {
            result += this.stride[i] * indices[i];
        }
        return result;
    }

    public int[] slicer(final int... slice) {
        final int n = slice.length / 2;

        int[] result = new int[1 + n];

        result[0] = 0;
        for (int i = 0; i < n; i++) {
            result[0] += this.stride[i] * slice[i];
        }

        System.arraycopy(slice, n, result, 1, n);

        return result;
    }

    public boolean isNull() {
        return this.shape == null || this.shape.length == 0;
    }

    public boolean equals(final float b) {
        return this.equals(b, 0.0f);
    }

    public boolean equals(final float b, final float e) {
        return this._equals(0, this.base, b, e);
    }

    public boolean equals(final MArray v) {
        return this.equals(v, 0.0f);
    }

    public boolean equals(final MArray v, final float e) {
        if (this.size != v.size || !Arrays.equals(this.shape, v.shape)) {
            return false;
        }
        if (this.base == 0 && v.base == 0 && Arrays.equals(this.stride, v.stride)) {
            return Arrays.equals(this.data, v.data);
        } else {
            return this._equals(0, this.base, v, v.base, e);
        }
    }

    public MArray resize(final int... shape) {
        this.dupDataIf(this.base > 0, true);
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

    public MArray clone() {
        return new MArray(this, true);
    }

    public MArray dup() {
        MArray out = new MArray(this);
        this._dup(0, this.base, out, out.base);
        return out;
    }

    public MArray dupData(boolean copy) {
        float[] data = new float[this.size];
        if (copy) {
            System.arraycopy(this.data, this.base, data, 0, this.size);
        }
        this.data = data;
        this.base = 0;
        this.copied = false;
        return this;
    }

    public MArray dupDataIf(boolean cond, boolean copy) {
        if (cond) {
            float[] data = new float[this.size];
            if (copy) {
                System.arraycopy(this.data, this.base, data, 0, this.size);
            }
            this.data = data;
            this.base = 0;
            this.copied = false;
        }
        return this;
    }

    public String toString() {
        final StringBuilder sb = new StringBuilder();
        this._toString(0, this.base, sb, false, "%1$10.3f");
        return sb.toString();
    }

    private void _dup(final int n, int off, final MArray b, int boff) {
        if (this.shape.length - n == 1) {
            for (int i = 0; i < this.shape[n]; i++) {
                b.data[boff] = this.data[off];
                off += this.stride[n];
                boff += b.stride[n];
            }
        } else {
            for (int i = 0; i < this.shape[n]; i++) {
                this._dup(n + 1, off, b, boff);
                off += this.stride[n];
                boff += b.stride[n];
            }
        }
    }

    private boolean _equals(final int n, int off, final float b, final float e) {
        boolean result = true;
        if (this.shape.length - n == 1) {
            for (int i = 0; i < this.shape[n] && result; i++) {
                result &= Math.abs(this.data[off] - b) <= e;
                off += this.stride[n];
            }
        } else {
            for (int i = 0; i < this.shape[n] && result; i++) {
                result &= this._equals(n + 1, off, b, e);
                off += this.stride[n];
            }
        }
        return result;
    }

    private boolean _equals(final int n, int off, final MArray b, int boff, final float e) {
        boolean result = true;
        if (this.shape.length - n == 1) {
            for (int i = 0; i < this.shape[n] && result; i++) {
                result &= Math.abs(this.data[off] - b.data[boff]) <= e;
                off += this.stride[n];
                boff += b.stride[n];
            }
        } else {
            for (int i = 0; i < this.shape[n] && result; i++) {
                result &= this._equals(n + 1, off, b, boff, e);
                off += this.stride[n];
                boff += b.stride[n];
            }
        }
        return result;
    }

    private void _toString(final int n, int o, final StringBuilder sb, final boolean indent, final String format) {
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
                this._toString(n + 1, o, sb, i > 0, format);
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
