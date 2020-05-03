package com.github.romualdrousseau.shuju.math;

import java.util.function.BiFunction;

public class UFunc0 extends UFunc<Float> {

    public UFunc0(BiFunction<Float, Float, Float> func) {
        super(func);
    }

    @Override
    public MArray reduce(MArray a, final float initital, final int axis, MArray out) {
        if (out == null) {
            if (axis == MArray.None || a.shape.length == 1) {

                // Output is reduced to one item

                out = new MArray(1);
            } else {

                // Output is reduced of one axis

                int[] newShape = new int[a.shape.length - 1];
                for (int i = 0, j = 0; i < a.shape.length; i++) {
                    if (i != axis) {
                        newShape[j++] = a.shape[i];
                    }
                }
                out = new MArray(newShape);
            }
        }

        if (axis == MArray.None) {

            // Reduce the array as flatten

            float acc = initital;
            for (int i = 0; i < a.size; i++) {
                acc = func.apply(acc, a.data[i]);
                out.data[0] = acc;
            }
        } else {

            // Reduce on one axis

            this._reduceOnAxis(0, 0, a, a.base, initital, axis, out, out.base);
        }

        return out;
    }

    @Override
    public MArray accumulate(final MArray a, final float initital, final int axis, MArray out) {
        if (out == null) {
            out = new MArray(a.shape);
        }

        this._accumulateOnAxis(0, a, a.base, initital, axis, out, out.base);

        return out;
    }

    @Override
    public MArray outer(final MArray a, final float b, MArray out) {
        if (out == null) {
            out = new MArray(a.shape);
        }

        this._outerScalar(0, a, a.base, b, out, out.base);

        return out;
    }

    @Override
    public MArray outer(final MArray a, final MArray b, MArray out) {
        if (b.size == 1) {

            // Outer with a scalar if b has only one item

            return this.outer(a, b.item(0), out);
        }

        // Ensure both array has same shape by prepending ones

        MArray aa = a;
        MArray bb = b;

        if (a.shape.length > b.shape.length) {

            // Expand shape of B by prepending ones as missing shapes

            bb = b.view().expandDims(a.shape.length);
        } else if (b.shape.length > a.shape.length) {

            // Expand shape of A by prepending ones as missing shapes

            aa = a.view().expandDims(b.shape.length);
        }

        if (out == null) {

            // Output is the max of all dimensions of A and B

            int[] newShape = new int[aa.shape.length];
            for (int i = 0; i < aa.shape.length; i++) {
                newShape[i] = Math.max(aa.shape[i], bb.shape[i]);
            }
            out = new MArray(newShape);
        }

        this._outerArray(0, aa, aa.base, bb, bb.base, out, out.base);

        return out;
    }

    private void _reduceOnAxis(final int n1, final int n2, final MArray a, int aoff, final float initial, int axis,
            final MArray b, int boff) {
        final int cnt = a.shape.length - 1;
        final int n1_i = n1 + 1;
        final int n2_i;
        final int dim_i;
        final int astr_i;
        final int bstr_i;
        float acc;

        if (axis == n1) {
            dim_i = a.shape[cnt];
            astr_i = a.stride[cnt];
            bstr_i = b.stride[b.shape.length - 1];
            n2_i = n2;
        } else if (n1 == cnt) {
            dim_i = a.shape[axis];
            astr_i = a.stride[axis];
            bstr_i = 0;
            n2_i = n2;
        } else {
            dim_i = a.shape[n1];
            astr_i = a.stride[n1];
            bstr_i = b.stride[n2];
            n2_i = n2 + 1;
        }

        switch (cnt - n1 + 1) {
            case 1:

                // Case for vector

                acc = initial;
                for (int i = 0; i < dim_i; i++) {
                    acc = func.apply(acc, a.data[aoff]);
                    b.data[boff] = acc;
                    aoff += astr_i;
                }

                break;

            case 2:

                // Case for matrix

                final int dim_ij;
                final int astr_ij;
                final int bstr_ij;

                if (cnt == n1 + 1) {
                    dim_ij = a.shape[axis];
                    astr_ij = a.stride[axis];
                    bstr_ij = 0;
                } else {
                    dim_ij = a.shape[n1 + 1];
                    astr_ij = a.stride[n1 + 1];
                    bstr_ij = b.stride[n2 + 1];
                }

                for (int i = 0; i < dim_i; i++) {
                    int aoff_ij = aoff;
                    int boff_ij = boff;
                    acc = initial;
                    for (int j = 0; j < dim_ij; j++) {
                        acc = func.apply(acc, a.data[aoff_ij]);
                        b.data[boff_ij] = acc;
                        aoff_ij += astr_ij;
                        boff_ij += bstr_ij;
                    }
                    aoff += astr_i;
                    boff += bstr_i;
                }

                break;

            default:

                // Recursively broadcast

                for (int i = 0; i < dim_i; i++) {
                    this._reduceOnAxis(n1_i, n2_i, a, aoff, initial, axis, b, boff);
                    aoff += astr_i;
                    boff += bstr_i;
                }
        }
    }

    private void _accumulateOnAxis(final int n, final MArray a, int aoff, final float initial, final int axis,
            final MArray b, int boff) {
        final int cnt = a.shape.length - 1;
        final int dim_i;
        final int astr_i;
        final int bstr_i;
        float acc;

        if (axis < cnt && n == axis) {
            dim_i = a.shape[cnt];
            astr_i = a.stride[cnt];
            bstr_i = b.stride[cnt];
        } else if (axis < cnt && n == cnt) {
            dim_i = a.shape[axis];
            astr_i = a.stride[axis];
            bstr_i = b.stride[axis];
        } else {
            dim_i = a.shape[n];
            astr_i = a.stride[n];
            bstr_i = b.stride[n];
        }

        switch (cnt - n + 1) {
            case 1:

                // Case for vector

                acc = initial;
                for (int i = 0; i < dim_i; i++) {
                    acc = func.apply(acc, a.data[aoff]);
                    b.data[boff] = acc;
                    aoff += astr_i;
                    boff += bstr_i;
                }

                break;

            case 2:

                // Case for matrix

                final int dim_ij;
                final int astr_ij;
                final int bstr_ij;

                if (axis < cnt && n + 1 == cnt) {
                    dim_ij = a.shape[axis];
                    astr_ij = a.stride[axis];
                    bstr_ij = b.stride[axis];
                } else {
                    dim_ij = a.shape[n + 1];
                    astr_ij = a.stride[n + 1];
                    bstr_ij = b.stride[n + 1];
                }

                for (int i = 0; i < dim_i; i++) {
                    int aoff_ij = aoff;
                    int boff_ij = boff;
                    acc = 0.0f;
                    for (int j = 0; j < dim_ij; j++) {
                        acc = func.apply(acc, a.data[aoff_ij]);
                        b.data[boff_ij] = acc;
                        aoff_ij += astr_ij;
                        boff_ij += bstr_ij;
                    }
                    aoff += astr_i;
                    boff += bstr_i;
                }

                break;

            default:

                // Recursively broadcast

                for (int i = 0; i < dim_i; i++) {
                    this._accumulateOnAxis(n + 1, a, aoff, initial, axis, b, boff);
                    aoff += astr_i;
                    boff += bstr_i;
                }
        }
    }

    private void _outerScalar(final int n, final MArray a, int aoff, final float b, final MArray c, int coff) {
        final int cnt = a.shape.length - 1;
        final int adim_i = a.shape[n];
        final int cdim_i = c.shape[n];
        final int astr_i;
        final int cstr_i = c.stride[n];
        if (adim_i == 1) {
            astr_i = 0;
        } else {
            astr_i = a.stride[n];
        }

        switch (cnt - n + 1) {
            case 1:

                // Case for vector

                for (int i = 0; i < cdim_i; i++) {
                    c.data[coff] = func.apply(b, a.data[aoff]);
                    aoff += astr_i;
                    coff += cstr_i;
                }

                break;

            case 2:

                // Case for Matrix

                final int adim_ij = a.shape[n + 1];
                final int cdim_ij = c.shape[n + 1];
                final int astr_ij;
                final int cstr_ij = c.stride[n + 1];

                if (adim_ij == 1) {
                    astr_ij = 0;
                } else {
                    astr_ij = a.stride[n + 1];
                }

                for (int i = 0; i < cdim_i; i++) {
                    int aoff_ij = aoff;
                    int coff_ij = coff;
                    for (int j = 0; j < cdim_ij; j++) {
                        c.data[coff_ij] = func.apply(b, a.data[aoff_ij]);
                        aoff_ij += astr_ij;
                        coff_ij += cstr_ij;
                    }
                    aoff += astr_i;
                    coff += cstr_i;
                }

                break;

            default:

                // Recursively broadcast

                for (int i = 0; i < cdim_i; i++) {
                    this._outerScalar(n + 1, a, aoff, b, c, coff);
                    aoff += astr_i;
                    coff += cstr_i;
                }
        }
    }

    private void _outerArray(final int n, final MArray a, int aoff, final MArray b, int boff, final MArray c,
            int coff) {
        final int cnt = a.shape.length - 1;
        final int adim_i = a.shape[n];
        final int bdim_i = b.shape[n];
        final int cdim_i = c.shape[n];
        final int astr_i;
        final int bstr_i;
        final int cstr_i = c.stride[n];

        if (bdim_i < adim_i) {
            astr_i = a.stride[n];
            bstr_i = 0;
        } else if (bdim_i > adim_i) {
            astr_i = 0;
            bstr_i = b.stride[n];
        } else {
            astr_i = a.stride[n];
            bstr_i = b.stride[n];
        }

        switch (cnt - n + 1) {
            case 1:

                // Case for vector

                for (int i = 0; i < cdim_i; i++) {
                    c.data[coff] = func.apply(b.data[boff], a.data[aoff]);
                    aoff += astr_i;
                    boff += bstr_i;
                    coff += cstr_i;
                }

                break;

            case 2:

                // Case for Matrix

                final int adim_ij = a.shape[n + 1];
                final int bdim_ij = b.shape[n + 1];
                final int cdim_ij = c.shape[n + 1];
                final int astr_ij;
                final int bstr_ij;
                final int cstr_ij = c.stride[n + 1];

                if (bdim_ij < adim_ij) {
                    astr_ij = a.stride[n + 1];
                    bstr_ij = 0;
                } else if (bdim_ij > adim_ij) {
                    astr_ij = 0;
                    bstr_ij = b.stride[n + 1];
                } else {
                    astr_ij = a.stride[n + 1];
                    bstr_ij = b.stride[n + 1];
                }

                for (int i = 0; i < cdim_i; i++) {
                    int aoff_ij = aoff;
                    int boff_ij = boff;
                    int coff_ij = coff;
                    for (int j = 0; j < cdim_ij; j++) {
                        c.data[coff_ij] = func.apply(b.data[boff_ij], a.data[aoff_ij]);
                        aoff_ij += astr_ij;
                        boff_ij += bstr_ij;
                        coff_ij += cstr_ij;
                    }
                    aoff += astr_i;
                    boff += bstr_i;
                    coff += cstr_i;
                }

                break;

            default:

                // Recursively broadcast

                for (int i = 0; i < cdim_i; i++) {
                    this._outerArray(n + 1, a, aoff, b, boff, c, coff);
                    aoff += astr_i;
                    boff += bstr_i;
                    coff += cstr_i;
                }
        }
    }
}
