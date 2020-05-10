package com.github.romualdrousseau.shuju.math;

import java.util.Arrays;
import java.util.function.BiFunction;

import com.github.romualdrousseau.shuju.math.MArray.Flag;

public abstract class UFunc<T> {

    protected final BiFunction<T, T, T> func;
    protected final int minShape;

    protected abstract void applyAccFunc(final int dim, final MArray a, int aoff, final int astr, final MArray b, int boff, final int bstr, final float initital);

    protected abstract void applyFunc(final int dim, final MArray a, int aoff, final int astr, final float b, final MArray c, int coff, final int cstr);

    protected abstract void applyFunc(final int dim, final MArray a, int aoff, final int astr, final MArray b, int boff, final int bstr, final MArray c, int coff, final int cstr);

    public UFunc(final BiFunction<T, T, T> func, final int minShape) {
        this.func = func;
        this.minShape = minShape;
    }

    public MArray reduce(MArray a, final float initial, final int axis, final boolean keepShape, MArray out) {
        assert (axis == MArray.None || axis >= 0 && axis < a.shape.length) : "Illegal axis";

        int[] outShape = this.reduceShape(a, axis, keepShape);
        assert (out == null || Arrays.equals(out.shape, outShape)) : "Illegal output shape";

        if (out == null) {
            out = new MArray(outShape);
        }

        if (axis == MArray.None && a.flags.contains(Flag.CONTINUOUS)) {

            // Reduce the flatten array

            this.applyAccFunc(a.size, a, 0, 1, out, 0, 0, initial);
        } else {

            // Reduce on one axis

            this.reduceOnOneAxis(0, 0, a, a.base, initial, axis, out, out.base);
        }

        return out;
    }

    public MArray accumulate(final MArray a, final float initital, final int axis, MArray out) {
        assert (axis >= 0 && axis < a.shape.length) : "Illegal axis";
        assert (out == null || Arrays.equals(out.shape, a.shape)) : "Illegal output shape";

        if (out == null) {
            out = new MArray(a.shape);
        }

        this.accumulateOnAxis(0, a, a.base, initital, axis, out, out.base);

        return out;
    }

    public MArray outer(final MArray a, final float b, MArray out) {
        assert (out == null || Arrays.equals(out.shape, a.shape)) : "Illegal output shape";

        if (out == null) {
            out = new MArray(a.shape);
        }

        if (a.flags.contains(Flag.CONTINUOUS)) {
            this.applyFunc(a.size, a, 0, 1, b, out, 0, 1);
        } else {
            this.outerScalar(0, a, a.base, b, out, out.base);
        }

        return out;
    }

    public MArray outer(final MArray a, final MArray b, MArray out) {
        if (a.size == 1) {

            // Outer with a scalar if a has only one item

            return this.outer(b, a.item(0), out);
        }

        if (b.size == 1) {

            // Outer with a scalar if b has only one item

            return this.outer(a, b.item(0), out);
        }

        // Ensure both array has same shape

        MArray aa = a;
        MArray bb = b;

        if (aa.shape.length < this.minShape) {

            // Expand shape of A by prepending ones as missing shapes

            aa = aa.view().expandShape(2, false);
        }

        if (bb.shape.length < this.minShape) {

            // Expand shape of B by appending ones as missing shapes

            bb = bb.view().expandShape(2, true);
        }

        if (aa.shape.length < bb.shape.length) {

            // Expand shape of A by prepending ones as missing shapes

            aa = aa.view().expandShape(bb.shape.length,  false);
        }

        if (bb.shape.length < aa.shape.length) {

            // Expand shape of B by prepending ones as missing shapes

            bb = bb.view().expandShape(aa.shape.length, false);
        }

        int[] outShape = outerShape(aa, bb);
        assert (out == null || Arrays.equals(out.shape, outShape)) : "Illegal out shape";

        if (out == null) {
            out = new MArray(outShape);
        }

        this.outerArray(0, aa, aa.base, bb, bb.base, out, out.base);

        return out;
    }

    public MArray inner(final MArray a, final float b, final float initial, final int axis, final boolean keepShape, MArray out) {
        return MFuncs.Add.reduce(this.outer(a, b, null), initial, axis, keepShape, out);
    }

    public MArray inner(final MArray a, final MArray b, final float initial, final int axis, final boolean keepShape, MArray out) {
        return MFuncs.Add.reduce(this.outer(a, b, null), initial, axis, keepShape, out);
    }

    private int[] reduceShape(final MArray a, final int axis, final boolean keepShape) {
        if(keepShape) {

            // shape axis is simply put at 1

            int[] newShape = a.shape.clone();
            newShape[axis] = 1;
            return newShape;
        } else if (axis == MArray.None || a.shape.length == 1) {

            // shape is reduced to one dimension

            return new int[] { 1 };
        } else {

            // shape is reduced of one axis

            int[] newShape = new int[a.shape.length - 1];
            for (int i = 0, j = 0; i < a.shape.length; i++) {
                if (i != axis) {
                    newShape[j++] = a.shape[i];
                }
            }

            return newShape;
        }
    }

    protected int[] outerShape(final MArray a, final MArray b) {
        int[] newShape = new int[a.shape.length];
        for (int i = 0; i < a.shape.length; i++) {
            newShape[i] = Math.max(a.shape[i], b.shape[i]);
        }
        return newShape;
    }

    protected void reduceOnOneAxis(final int n1, final int n2, final MArray a, int aoff, final float initial, int axis,
            final MArray b, int boff) {
        final int cnt = a.shape.length - 1;
        final int n2_plus1;
        final int dim;
        final int astr;
        final int bstr;

        // Swap axis with the last dimension and then reduce the last dimension

        if (axis == n1) {
            dim = a.shape[cnt];
            astr = a.stride[cnt];
            bstr = b.stride[b.shape.length - 1];
            n2_plus1 = n2;
        } else if (n1 == cnt) {
            dim = a.shape[axis];
            astr = a.stride[axis];
            bstr = 0;
            n2_plus1 = n2;
        } else {
            dim = a.shape[n1];
            astr = a.stride[n1];
            bstr = b.stride[n2];
            n2_plus1 = n2 + 1;
        }

        // Optimize vector and matrix, then recursively broadcast

        switch (cnt - n1 + 1) {
            case 1:

                // Case for vector

                this.applyAccFunc(dim, a, aoff, astr, b, boff, 0, initial);
                break;

            case 2:

                // Case for matrix

                final int dim_j;
                final int astr_j;
                final int bstr_j;

                // Swap axis with the last dimension and then reduce the last dimension

                if (cnt == n1 + 1) {
                    dim_j = a.shape[axis];
                    astr_j = a.stride[axis];
                    bstr_j = 0;
                } else {
                    dim_j = a.shape[n1 + 1];
                    astr_j = a.stride[n1 + 1];
                    bstr_j = b.stride[n2 + 1];
                }

                for (int i = 0; i < dim; i++) {
                    this.applyAccFunc(dim_j, a, aoff, astr_j, b, boff, bstr_j, initial);
                    aoff += astr;
                    boff += bstr;
                }
                break;

            default:

                // Recursively broadcast

                for (int i = 0; i < dim; i++) {
                    this.reduceOnOneAxis(n1 + 1, n2_plus1, a, aoff, initial, axis, b, boff);
                    aoff += astr;
                    boff += bstr;
                }
        }
    }

    protected void accumulateOnAxis(final int n, final MArray a, int aoff, final float initial, final int axis,
            final MArray b, int boff) {
        final int cnt = a.shape.length - 1;
        final int dim;
        final int astr;
        final int bstr;

        // Swap axis with the last dimension and then accumulate the last dimension

        if (axis < cnt && n == axis) {
            dim = a.shape[cnt];
            astr = a.stride[cnt];
            bstr = b.stride[cnt];
        } else if (axis < cnt && n == cnt) {
            dim = a.shape[axis];
            astr = a.stride[axis];
            bstr = b.stride[axis];
        } else {
            dim = a.shape[n];
            astr = a.stride[n];
            bstr = b.stride[n];
        }

        // Optimize vector and matrix, then recursively broadcast

        switch (cnt - n + 1) {
            case 1:

                // Case for vector

                this.applyAccFunc(dim, a, aoff, astr, b, boff, bstr, initial);
                break;

            case 2:

                // Case for matrix

                final int dim_ij;
                final int astr_ij;
                final int bstr_ij;

                // Swap axis with the last dimension and then accumulate the last dimension

                if (axis < cnt && n + 1 == cnt) {
                    dim_ij = a.shape[axis];
                    astr_ij = a.stride[axis];
                    bstr_ij = b.stride[axis];
                } else {
                    dim_ij = a.shape[n + 1];
                    astr_ij = a.stride[n + 1];
                    bstr_ij = b.stride[n + 1];
                }

                for (int i = 0; i < dim; i++) {
                    this.applyAccFunc(dim_ij, a, aoff, astr_ij, b, boff, bstr_ij, initial);
                    aoff += astr;
                    boff += bstr;
                }
                break;

            default:

                // Recursively broadcast

                for (int i = 0; i < dim; i++) {
                    this.accumulateOnAxis(n + 1, a, aoff, initial, axis, b, boff);
                    aoff += astr;
                    boff += bstr;
                }
        }
    }

    protected void outerScalar(final int n, final MArray a, int aoff, final float b, final MArray c, int coff) {
        final int cnt = a.shape.length - 1;
        final int adim = a.shape[n];
        final int cdim = c.shape[n];
        final int astr;
        final int cstr = c.stride[n];

        // Calculate the right stride to avoid modulo in the loops
        // Array dimensions must be broadcastable, i.e. same or 1

        if (adim == 1) {
            astr = 0;
        } else {
            astr = a.stride[n];
        }

        // Optimize vector and matrix, then recursively broadcast

        switch (cnt - n + 1) {
            case 1:

                // Case for vector

                this.applyFunc(cdim, a, aoff, astr, b, c, coff, cstr);
                break;

            case 2:

                // Case for Matrix

                final int adim_j = a.shape[n + 1];
                final int cdim_j = c.shape[n + 1];
                final int astr_j;
                final int cstr_j = c.stride[n + 1];

                // Calculate the right stride to avoid modulo in the loops
                // Array dimensions must be broadcastable, i.e. same or 1

                if (adim_j == 1) {
                    astr_j = 0;
                } else {
                    astr_j = a.stride[n + 1];
                }

                for (int i = 0; i < cdim; i++) {
                    this.applyFunc(cdim_j, a, aoff, astr_j, b, c, coff, cstr_j);
                    aoff += astr;
                    coff += cstr;
                }

                break;

            default:

                // Recursively broadcast

                for (int i = 0; i < cdim; i++) {
                    this.outerScalar(n + 1, a, aoff, b, c, coff);
                    aoff += astr;
                    coff += cstr;
                }
        }
    }

    protected void outerArray(final int n, final MArray a, int aoff, final MArray b, int boff, final MArray c, int coff) {
        final int cnt = a.shape.length - 1;
        final int adim = a.shape[n];
        final int bdim = b.shape[n];
        final int cdim = c.shape[n];
        final int astr;
        final int bstr;
        final int cstr = c.stride[n];

        // Calculate the right strides to avoid modulo in the loops
        // Array dimensions must be broadcastable, i.e. same or 1

        if (adim == 1) {
            astr = 0;
            bstr = b.stride[n];
        } else if (bdim == 1) {
            astr = a.stride[n];
            bstr = 0;
        } else {
            astr = a.stride[n];
            bstr = b.stride[n];
        }

        // Optimize vector and matrix, then recursively broadcast

        switch (cnt - n + 1) {
            case 1:

                // Case for vector

                this.applyFunc(cdim, a, aoff, astr, b, boff, bstr, c, coff, cstr);
                break;

            case 2:

                // Case for Matrix

                final int adim_j = a.shape[n + 1];
                final int bdim_j = b.shape[n + 1];
                final int cdim_j = c.shape[n + 1];
                final int astr_j;
                final int bstr_j;
                final int cstr_j = c.stride[n + 1];

                // Calculate the right strides to avoid modulo in the loops
                // Array dimensions must be broadcastable, i.e. same or 1

                if (adim_j == 1) {
                    astr_j = 0;
                    bstr_j = b.stride[n + 1];
                } else if (bdim_j == 1) {
                    astr_j = a.stride[n + 1];
                    bstr_j = 0;
                } else {
                    astr_j = a.stride[n + 1];
                    bstr_j = b.stride[n + 1];
                }

                for (int i = 0; i < cdim; i++) {
                    this.applyFunc(cdim_j, a, aoff, astr_j, b, boff, bstr_j, c, coff, cstr_j);
                    aoff += astr;
                    boff += bstr;
                    coff += cstr;
                }
                break;

            default:

                // Recursively broadcast

                for (int i = 0; i < cdim; i++) {
                    this.outerArray(n + 1, a, aoff, b, boff, c, coff);
                    aoff += astr;
                    boff += bstr;
                    coff += cstr;
                }
        }
    }
}
