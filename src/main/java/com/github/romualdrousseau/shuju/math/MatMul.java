package com.github.romualdrousseau.shuju.math;

import java.util.function.BiFunction;

public class MatMul extends UFunc<Float> {

    public MatMul(BiFunction<Float, Float, Float> func) {
        super(func, 2);
    }

    @Override
    protected int[] outerShape(final MArray a, final MArray b) {
        int[] newShape = new int[a.shape.length];
        for (int i = 0; i < a.shape.length; i++) {
            final int n = a.shape.length - i;
            if (n == 1) {
                newShape[i] = b.shape[i];
            } else if (n == 2) {
                newShape[i] = a.shape[i];
            } else {
                newShape[i] = Math.max(a.shape[i], b.shape[i]);
            }
        }
        return newShape;
    }

    @Override
    protected void outerArray(final int n, final MArray a, int aoff, final MArray b, int boff, final MArray c,
            int coff) {
        if (a.shape.length - n > 2) {
            super.outerArray(n, a, aoff, b, boff, c, coff);
        } else {
            final int adim_i = a.shape[n];
            final int astr_i = a.stride[n];
            final int adim_ij = a.shape[n + 1];
            final int astr_ij = a.stride[n + 1];

            final int bstr_ij = b.stride[n];
            final int bdim_ijk = b.shape[n + 1];
            final int bstr_ijk = b.stride[n + 1];

            final int cstr_i = c.stride[n];
            final int cstr_ijk = c.stride[n + 1];

            for (int i = 0; i < adim_i; i++) {
                int aoff_ij = aoff;
                int boff_ij = boff;
                int coff_ij = coff;
                for (int j = 0; j < adim_ij; j++) {
                    final float aa = a.data[aoff_ij];
                    int boff_ijk = boff_ij;
                    int coff_ijk = coff_ij;
                    for (int k = 0; k < bdim_ijk; k++) {
                        c.data[coff_ijk] = aa * b.data[boff_ijk] + c.data[coff_ijk];
                        boff_ijk += bstr_ijk;
                        coff_ijk += cstr_ijk;
                    }
                    aoff_ij += astr_ij;
                    boff_ij += bstr_ij;
                }
                aoff += astr_i;
                coff += cstr_i;
            }
        }
    }

    // private void fmav(final int n, final float[] a, final int oa, final float[] b, final int ob, final float c[],
    //         final int oc) {
    //     final float[] tmpA = new float[8];
    //     final float[] tmpB = new float[8];
    //     final float[] tmpC = new float[8];
    //     for (int j = 0; j < n; j += 8) {
    //         System.arraycopy(a, oa + j, tmpA, 0, 8);
    //         System.arraycopy(b, ob + j, tmpB, 0, 8);
    //         System.arraycopy(c, oc + j, tmpC, 0, 8);
    //         tmpC[0] = Math.fma(tmpA[0], tmpB[0], tmpC[0]);
    //         tmpC[1] = Math.fma(tmpA[1], tmpB[1], tmpC[1]);
    //         tmpC[2] = Math.fma(tmpA[2], tmpB[2], tmpC[2]);
    //         tmpC[3] = Math.fma(tmpA[3], tmpB[3], tmpC[3]);
    //         tmpC[4] = Math.fma(tmpA[4], tmpB[4], tmpC[4]);
    //         tmpC[5] = Math.fma(tmpA[5], tmpB[5], tmpC[5]);
    //         tmpC[6] = Math.fma(tmpA[6], tmpB[5], tmpC[6]);
    //         tmpC[7] = Math.fma(tmpA[7], tmpB[5], tmpC[7]);
    //         System.arraycopy(tmpC, 0, c, oc + j, 8);
    //     }
    // }

    @Override
    protected void applyAccFunc(int dim, MArray a, int aoff, int astr, MArray b, int boff, int bstr, float initital) {
        throw new UnsupportedOperationException("Not implemented");

    }

    @Override
    protected void applyFunc(int dim, MArray a, int aoff, int astr, float b, MArray c, int coff, int cstr) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    protected void applyFunc(int dim, MArray a, int aoff, int astr, MArray b, int boff, int bstr, MArray c, int coff,
            int cstr) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
