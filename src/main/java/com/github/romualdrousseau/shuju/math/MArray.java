package com.github.romualdrousseau.shuju.math;

import java.util.Arrays;
import java.util.EnumSet;

public class MArray {

    public static final int None = -1;

    public enum Flag {
        CONTINUOUS, OWNDATA;

        public static final EnumSet<Flag> NONE = EnumSet.noneOf(Flag.class);
        public static final EnumSet<Flag> ALL = EnumSet.allOf(Flag.class);
    }

    public int size;
    public int[] shape;
    public int[] stride;
    public float[] data;
    public int base;
    public EnumSet<Flag> flags;

    public MArray() {
        this.shape = new int[1];
        this._updateStrides();
        this._updateSize();
        this.data = new float[this.size];
        this.base = 0;
        this.flags = Flag.ALL;
    }

    public MArray(final int... shape) {
        this.shape = shape.clone();
        this._updateStrides();
        this._updateSize();
        this.data = new float[this.size];
        this.base = 0;
        this.flags = Flag.ALL;
    }

    public MArray(MArray other) {
        this(other, false);
    }

    private MArray(MArray parent, final int... args) {
        this(parent, args[0], Arrays.copyOfRange(args, 1, args.length));
    }

    private MArray(MArray parent, final int base, final int... shape) {
        this.shape = shape;
        this.stride = parent.stride.clone();
        this.size = parent.size;
        this.data = parent.data;
        this.base = base;
        if (this.base == 0) {
            this.flags = EnumSet.of(Flag.CONTINUOUS);
        } else {
            this.flags = Flag.NONE;
        }
    }

    private MArray(MArray other, boolean copy) {
        if (copy) {
            this.shape = other.shape.clone();
            this.stride = other.stride.clone();
            this.size = other.size;
            this.data = other.data;
            this.base = other.base;
            this.flags = other.flags.clone();
            this.flags.remove(Flag.OWNDATA);
        } else {
            this.shape = other.shape;
            this.stride = other.stride;
            this.size = other.size;
            this.data = other.data;
            this.base = other.base;
            this.flags = other.flags;
        }
    }

    public float[] items() {
        return this.data;
    }

    public MArray setItems(final float v) {
        this.require(Flag.OWNDATA, false);
        for(int i = 0; i < this.size; i++) {
            this.data[i] = v;
        }
        return this;
    }

    public MArray setItems(final float... data) {
        this.require(Flag.OWNDATA, false);
        System.arraycopy(data, 0, this.data, this.base, data.length);
        return this;
    }

    public MArray setItems(final float[][] data) {
        this.require(Flag.OWNDATA, false);
        for (int i = 0; i < data.length; i++) {
            System.arraycopy(data[i], 0, this.data, i * data[0].length, data[0].length);
        }
        return this;
    }

    public MArray setItems(final double... data) {
        this.require(Flag.OWNDATA, false);
        for(int i = 0; i < data.length; i++) {
            this.data[i] = (float) data[i];
        }
        return this;
        
    }

    public MArray setItems(final double[][] data) {
        this.require(Flag.OWNDATA, false);
        for (int i = 0; i < data.length; i++) {
            for(int j = 0; i < data[0].length; j++) {
                this.data[i * data[0].length + j] = (float) data[i][j];
            }
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
        int sum = 0;
        for (int i = 0; i < indices.length; i++) {
            sum += this.stride[i] * indices[i];
        }
        return sum;
    }

    public int[] unravelOffset(final int offset) {
        int[] result = new int[this.stride.length];
        int rem = offset;
        int j = this.stride.length - 1;
        for (int i = this.stride.length - 1; i >= 0; i--) {
            if(this.stride[i] > 1) {
                // squeeze if stride of 1
                result[j--] = rem % this.stride[i];
            }
            rem /= this.stride[i];
        }
        result[j] = rem;
        return result;
    }

    public int[] slicer(final int... args) {
        final int n = args.length / 2;

        int[] result = new int[1 + n];

        // Calculate offset

        result[0] = 0;
        for (int i = 0; i < n; i++) {
            result[0] += this.stride[i] * args[i * 2];
        }

        // Extract shape

        for (int i = 0; i < n; i++) {
            int shape = args[1 + i * 2];
            result[1 + i] = (shape < 0) ? this.shape[i] : shape;
        }

        return result;
    }

    public boolean isNull() {
        return this.size == 0;
    }

    public boolean isAligned(final MArray v) {
        return this.flags.contains(Flag.CONTINUOUS) && v.flags.contains(Flag.CONTINUOUS)
                && Arrays.equals(this.stride, v.stride);
    }

    public boolean dimEquals(final MArray v) {
        return this.size == v.size && Arrays.equals(this.shape, v.shape);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof MArray) {
            return this.equals((MArray) o, 0.0f);
        } else if (o instanceof Float) {
            return this.equals((float) o, 0.0f);
        } else {
            return false;
        }
    }

    public boolean equals(Object o, float e) {
        if (o instanceof MArray) {
            return this.equals((MArray) o, e);
        } else if (o instanceof Float) {
            return this.equals((float) o, e);
        } else {
            return false;
        }
    }

    public boolean equals(final float b, final float e) {
        return this._equals(0, this.base, b, e);
    }

    public boolean equals(final MArray v, final float e) {
        if (!this.dimEquals(v)) {
            return false;
        }
        if (this.isAligned(v) && e == 0.0f) {
            return Arrays.equals(this.data, v.data);
        } else {
            return this._equals(0, this.base, v, v.base, e);
        }
    }

    public MArray ravel() {
        return this.reshape(-1);
    }

    public MArray squeeze() {
        int newShapeSize = 0;
        for (int i = 0; i < this.shape.length; i++) {
            if (this.shape[i] != 1) {
                newShapeSize++;
            }
        }

        int[] newShape = new int[newShapeSize];
        for (int i = 0, j = 0; i < this.shape.length; i++) {
            if (this.shape[i] != 1) {
                newShape[j++] = this.shape[i];
            }
        }

        return this.reshape(newShape);
    }

    public MArray expandShape(final int n) {
        return this.expandShape(n, false);
    }

    public MArray expandShape(final int n, boolean append) {
        int delta = n - this.shape.length;
        if(delta <= 0) {
            return this;
        }

        int[] newShape = new int[n];

        if (append) {
            for (int i = 0; i < this.shape.length; i++) {
                newShape[i] = this.shape[i];
            }
            for (int i = this.shape.length; i < n; i++) {
                newShape[i] = 1;
            }
        } else {
            for (int i = 0; i < delta; i++) {
                newShape[i] = 1;
            }
            for (int i = delta; i < n; i++) {
                newShape[i] = this.shape[i - delta];
            }
        }

        return this.reshape(newShape);
    }

    public MArray reshape(final int... shape) {

        // Check if some dimension should be infered i.e. missing (= -1)

        int size = 1;
        int infered = 0;
        for (int i = 0; i < shape.length; i++) {
            if (shape[i] < 0) {
                infered++;
            } else {
                size *= shape[i];
            }
        }

        // Sanity checks

        assert infered < 2 : "only one dimension can be infered";
        assert infered == 0 && size == this.size || infered == 1 && size < this.size : "incompatible shapes";

        if (infered == 1) {

            // Infere the missing dimension

            int delta = this.size;
            for (int i = 0; i < shape.length; i++) {
                if (shape[i] > 0) {
                    delta /= shape[i];
                }
            }
            if (delta > 0) {
                for (int i = 0; i < shape.length; i++) {
                    if (shape[i] < 0) {
                        shape[i] = delta;
                    }
                }
            }
        }

        this.require(Flag.CONTINUOUS, true);
        this.shape = shape.clone();
        this._updateStrides();
        return this;
    }

    public MArray transpose() {

        if(this.shape.length == 1) {
            return this;
        }

        // Inverse all dimensions

        final int[] indices = new int[this.shape.length];
        for (int i = 0; i < indices.length; i++) {
            indices[i] = indices.length - i - 1;
        }

        return this._swapaxes(indices);
    }

    public MArray transpose(final int... indices) {
        return this._swapaxes(indices);
    }

    public MArray view() {
        return new MArray(this, 0, this.shape.clone());
    }

    public MArray view(final int... slice) {
        return new MArray(this, this.slicer(slice));
    }

    public MArray repeat(final int n) {
        int[] newShape = this.shape.clone();
        newShape[0] *= n;

        MArray newArray = new MArray(newShape);

        for (int i = 0; i < n; i++) {
            System.arraycopy(this.data, 0, newArray.data, i * this.data.length, this.data.length);
        }

        return newArray;
    }

    public MArray stack(final MArray m) {
        int[] newShape = this.shape.clone();
        newShape[0] += m.shape[0];

        MArray newArray = new MArray(newShape);

        System.arraycopy(this.data, 0, newArray.data, 0, this.data.length);
        System.arraycopy(m.data, 0, newArray.data, this.data.length, m.data.length);

        return newArray;
    }

    public MArray copy() {
        return new MArray(this, true);
    }

    public MArray asContinuousArray() {
        MArray out = new MArray(this.shape);
        this._deepcopy(0, this.base, out, out.base);
        return out;
    }

    public MArray require(Flag flag, boolean copy) {
        if (flag.equals(Flag.CONTINUOUS) && !this.flags.contains(Flag.CONTINUOUS)) {
            final float[] data;
            if (copy) {
                data = this.asContinuousArray().data;
            } else {
                data = new float[this.size];
            }

            this.data = data;
            this.base = 0;
            this.flags.add(Flag.CONTINUOUS);
        } else if (flag.equals(Flag.OWNDATA) && !this.flags.contains(Flag.OWNDATA)) {
            final float[] data;
            if (copy) {
                data = this.data.clone();
            } else {
                data = new float[this.size];
            }

            this.data = data;
            this.base = 0;
            this.flags.add(Flag.OWNDATA);
        }
        return this;
    }

    public String toString() {
        if (this.isNull()) {
            return "[ ]";
        }
        final StringBuilder sb = new StringBuilder();
        this._toString(0, this.base, sb, false, "%1$10.3f");
        return sb.toString();
    }

    private MArray _swapaxes(final int... indices) {
        int swp;

        final int[] done = new int[indices.length];
        for (int i = 0; i < indices.length; i++) {
            done[i] = None;
        }

        for (int i = 0; i < indices.length; i++) {
            final int j = indices[i];
            if (j != i && done[j] == None) {
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

    private void _deepcopy(final int n, int off, final MArray b, int boff) {
        if (this.shape.length - n == 1) {
            for (int i = 0; i < this.shape[n]; i++) {
                b.data[boff] = this.data[off];
                off += this.stride[n];
                boff += b.stride[n];
            }
        } else {
            for (int i = 0; i < this.shape[n]; i++) {
                this._deepcopy(n + 1, off, b, boff);
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

    private void _updateSize() {
        this.size = 1;
        for (int i = 0; i < this.shape.length; i++) {
            this.size *= this.shape[i];
        }
    }

    private void _updateStrides() {
        this.stride = new int[this.shape.length];
        this.stride[this.stride.length - 1] = 1;
        for (int i = this.stride.length - 2; i >= 0; i--) {
            this.stride[i] = this.stride[i + 1] * this.shape[i + 1];
        }
    }
}
