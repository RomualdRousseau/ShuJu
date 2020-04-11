package com.github.romualdrousseau.shuju.math;

import java.util.function.Function;
import java.util.function.BiFunction;

public class Marray {

    public int size;
    public int[] shape;
    public int[] stride;
    public float[] data;
    public boolean transposed;
    public boolean copied;

    public Marray(final int... shape) {
        this.shape = shape;
        this.updateSize();
        this.updateStrides();
        this.data = new float[this.size];
        this.transposed = false;
        this.copied = false;
    }

    public Marray reshape(final int... shape) {
        this.shape = shape;
        this.updateStrides();
        return this;
    }

    public Marray transpose() {
        final int[] indices = new int[this.shape.length];
        for (int i = 0; i < indices.length; i++) {
            indices[i] = indices.length - i - 1;
        }
        return this.transpose(indices);
    }

    public Marray transpose(final int... indices) {
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

    public Marray zero() {
        this.walk(x -> 0.0f);
        return this;
    }

    public Marray ones() {
        this.walk(x -> 1.0f);
        return this;
    }

    public Marray arrange() {
        for (int i = 0; i < this.size; i++) {
            this.data[i] = i + 1;
        }
        return this;
    }

    public Marray iadd(final float v) {
        this.walk(x -> x + v);
        return this;
    }

    public Marray iadd(final Marray m) {
        this.walk(m, (x, y) -> x + y);
        return this;
    }

    public Marray isub(final float v) {
        this.walk(x -> x - v);
        return this;
    }

    public Marray isub(final Marray m) {
        this.walk(m, (x, y) -> x - y);
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

    private void walk(final Function<Float, Float> fn) {
        for (int i = 0; i < this.size; i++) {
            this.data[i] = fn.apply(this.data[i]);
        }
    }

    private void walk(final Marray arg, final BiFunction<Float, Float, Float> fn) {
        this.walk(0, 0, 0, arg, fn);
    }

    private void walk(final int n, final int o1, final int o2, final Marray that,
            final BiFunction<Float, Float, Float> fn) {
        switch (this.shape.length - n) {
            case 1:
                if (this.shape[n] == that.shape[n]) {
                    final int c_i = this.shape[n];
                    final int s_i = this.stride[n];
                    int o1_i = o1;
                    int o2_i = o2;
                    for (int i = 0; i < c_i; i++) {
                        this.data[o1_i] = fn.apply(this.data[o1_i], that.data[o2_i]);
                        o1_i += s_i;
                        o2_i += s_i;
                    }
                } else {
                    final int c_i = Math.max(this.shape[n], that.shape[n]);
                    for (int i = 0; i < c_i; i++) {
                        final int o1_i = o1 + (i % this.shape[n]) * this.stride[n];
                        final int o2_i = o2 + (i % that.shape[n]) * that.stride[n];
                        this.data[o1_i] = fn.apply(this.data[o1_i], that.data[o2_i]);
                    }
                }
                break;

            case 2:
                if (this.shape[n] == that.shape[n] && this.shape[n + 1] == that.shape[n + 1]) {
                    final int c_i = this.shape[n];
                    final int c_ij = this.shape[n + 1];
                    final int s_i = this.stride[n];
                    final int s_ij = this.stride[n + 1];
                    int o1_i = o1;
                    int o2_i = o2;
                    for (int i = 0; i < c_i; i++) {
                        int o1_ij = o1_i;
                        int o2_ij = o2_i;
                        for (int j = 0; j < c_ij; j++) {
                            this.data[o1_ij] = fn.apply(this.data[o1_ij], that.data[o2_ij]);
                            o1_ij += s_ij;
                            o2_ij += s_ij;
                        }
                        o1_i += s_i;
                        o2_i += s_i;
                    }
                } else {
                    final int c_i = Math.max(this.shape[n], that.shape[n]);
                    final int c_ij = Math.max(this.shape[n + 1], that.shape[n + 1]);
                    for (int i = 0; i < c_i; i++) {
                        final int o1_i = o1 + (i % this.shape[n]) * this.stride[n];
                        final int o2_i = o2 + (i % that.shape[n]) * that.stride[n];
                        for (int j = 0; j < c_ij; j++) {
                            final int o1_ij = o1_i + (j % this.shape[n + 1]) * this.stride[n + 1];
                            final int o2_ij = o2_i + (j % that.shape[n + 1]) * that.stride[n + 1];
                            this.data[o1_ij] = fn.apply(this.data[o1_ij], that.data[o2_ij]);
                        }
                    }
                }
                break;

            default:
                final int c_i = Math.max(this.shape[n], that.shape[n]);
                for (int i = 0; i < c_i; i++) {
                    final int o1_i = o1 + (i % this.shape[n]) * this.stride[n];
                    final int o2_i = o2 + (i % that.shape[n]) * that.stride[n];
                    walk(n + 1, o1_i, o2_i, that, fn);
                }
        }
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
