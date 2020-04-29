package com.github.romualdrousseau.shuju.math;

import java.util.function.BiFunction;

public class UFunc0 implements UFunc {

    public UFunc0(BiFunction<Float, Float, Float> fn) {
        this.fn = fn;
    }

    @Override
    public MArray call(final MArray a, final float v, final float w, MArray out) {
        if (out == null) {
            out = new MArray(a.shape);
        }

        for (int i = 0; i < a.size; i++) {
            out.data[i] = this.fn.apply(a.data[i], v * out.data[i] + w);
        }

        return out;
    }

    @Override
    public MArray reduce(MArray a, final float v, final float w, final int axis, MArray out) {
        assert (axis < a.shape.length) : "Axis must be less than dimension";

        if (out == null) {
            if (a.shape.length == 1 || axis == -1) {
                out = new MArray(1);
            } else {
                int[] newShape = new int[a.shape.length - 1];
                for (int i = 0, j = 0; i < a.shape.length; i++) {
                    if (i != axis) {
                        newShape[j++] = a.shape[i];
                    }
                }
                out = new MArray(newShape);
            }
        }

        if (axis == -1) {
            for (int i = 0; i < a.size; i++) {
                out.data[0] = fn.apply(a.data[i], v * out.data[0] + w);
            }
        } else {
            this.reduceWalk(0, 0, a, 0, v, w, axis, out, 0);
        }

        return out;
    }

    @Override
    public MArray accumulate(final MArray a, final float v, final float w, final int axis, MArray out) {
        assert (axis < a.shape.length) : "Axis must be less than dimension";

        if (out == null) {
            out = new MArray(a.shape);
        }

        if (axis == -1) {
            float sum = 0.0f;
            for (int i = 0; i < a.size; i++) {
                sum = fn.apply(v * sum + w, a.data[i]);
                out.data[i] = sum;
            }
        } else {
            this.accumulateWalk(0, a, 0, v, w, axis, out, 0);
        }

        return out;
    }

    @Override
    public MArray inner(final MArray a, final MArray b, final float v, final float w, MArray out) {
        assert (a.shape.length == b.shape.length) : "Arrays must be same dimensions";

        if (out == null) {
            int newShapeLen = a.shape.length;
            int[] newShape = new int[newShapeLen];
            for (int i = 0; i < newShapeLen; i++) {
                newShape[i] = Math.max(a.shape[i], b.shape[i]);
            }
            out = new MArray(newShape);
        }
        this.innerWalk(0, a, 0, b, 0, v, w, out, 0);

        return out;
    }

    private void reduceWalk(final int n1, final int n2, final MArray a, final int aoff, final float v, final float w, int axis,
            final MArray b, final int boff) {
        final int cnt = a.shape.length - 1;
        final int n1_i = n1 + 1;
        final int n2_i;
        final int dim_i = a.shape[n1];
        final int astr_i = a.stride[n1];
        final int bstr_i;
        int aoff_i = aoff;
        int boff_i = boff;
        boolean terminated = false;

        if (n1 == axis) {
            bstr_i = 0;
            n2_i = n2;
        } else {
            bstr_i = b.stride[n2];
            n2_i = n2 + 1;
        }

        switch (cnt - n1 + 1) {
            case 1:

                // Case for vector

                for (int i = 0; i < dim_i; i++) {
                    b.data[boff_i] = fn.apply(a.data[aoff_i], v * b.data[boff_i] + w);
                    aoff_i += astr_i;
                    boff_i += bstr_i;
                }

                terminated = true;
                break;

            case 2:

                // Case for matrix

                final int dim_ij = a.shape[n1 + 1];
                final int astr_ij = a.stride[n1 + 1];
                final int bstr_ij;

                if (n1 + 1 == axis) {
                    bstr_ij = 0;
                } else {
                    bstr_ij = b.stride[n2_i];
                }

                for (int i = 0; i < dim_i; i++) {
                    int aoff_ij = aoff_i;
                    int boff_ij = boff_i;
                    for (int j = 0; j < dim_ij; j++) {
                        b.data[boff_ij] = fn.apply(a.data[aoff_ij], v * b.data[boff_ij] + w);
                        aoff_ij += astr_ij;
                        boff_ij += bstr_ij;
                    }
                    aoff_i += astr_i;
                    boff_i += bstr_i;
                }

                terminated = true;
                break;
        }

        if (!terminated) {

            // Recursively broadcast

            for (int i = 0; i < dim_i; i++) {
                this.reduceWalk(n1_i, n2_i, a, aoff_i, v, w, axis, b, boff_i);
                aoff_i += astr_i;
                boff_i += bstr_i;
            }
        }
    }

    private void accumulateWalk(final int n, final MArray a, final int aoff, final float v, final float w, final int axis,
            final MArray b, final int boff) {
        final int cnt = a.shape.length - 1;
        final int dim_i;
        final int astr_i;
        final int bstr_i;
        int aoff_i = aoff;
        int boff_i = boff;
        boolean terminated = false;
        float sum;

        if (axis < cnt && n == axis) {
            dim_i = a.shape[cnt];
            astr_i = a.stride[cnt];
            bstr_i = b.stride[cnt];
        } else if (axis < cnt && n == cnt) {
            dim_i = a.shape[axis];
            astr_i = a.stride[axis];
            bstr_i = b.stride[axis];
        }  else {
            dim_i = a.shape[n];
            astr_i = a.stride[n];
            bstr_i = b.stride[n];
        }

        switch (cnt - n + 1) {
            case 1:

                // Case for vector

                sum = 0.0f;
                for (int i = 0; i < dim_i; i++) {
                    sum += fn.apply(v * sum + w, a.data[aoff_i]);
                    b.data[boff_i] = sum;
                    aoff_i += astr_i;
                    boff_i += bstr_i;
                }

                terminated = true;
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
                    int aoff_ij = aoff_i;
                    int boff_ij = boff_i;
                    sum = 0.0f;
                    for (int j = 0; j < dim_ij; j++) {
                        sum += fn.apply(v * sum + w, a.data[aoff_ij]);
                        b.data[boff_ij] = sum;
                        aoff_ij += astr_ij;
                        boff_ij += bstr_ij;
                    }
                    aoff_i += astr_i;
                    boff_i += bstr_i;
                }

                terminated = true;
                break;
        }

        if (!terminated) {

            // Recursively broadcast

            for (int i = 0; i < dim_i; i++) {
                this.accumulateWalk(n + 1, a, aoff_i, v, w, axis, b, boff_i);
                aoff_i += astr_i;
                boff_i += bstr_i;
            }
        }
    }

    private void innerWalk(final int n, final MArray a, final int aoff, final MArray b, final int boff, final float v, final float w, final MArray c,
            final int coff) {
        final int cnt = a.shape.length - 1;
        final int adim_i = a.shape[n];
        final int bdim_i = b.shape[n];
        final int cdim_i = c.shape[n];
        final int astr_i;
        final int bstr_i;
        final int cstr_i = c.stride[n];
        int aoff_i = aoff;
        int boff_i = boff;
        int coff_i = coff;
        boolean terminated = false;

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
                    c.data[coff_i] = fn.apply(a.data[aoff_i], v * b.data[boff_i] + w);
                    aoff_i += astr_i;
                    boff_i += bstr_i;
                    coff_i += cstr_i;
                }

                terminated = true;
                break;

            case 2:

                // Case for Matrix

                if (a.shape[n] == b.shape[n] && a.shape[n + 1] == b.shape[n + 1]) {
                    final int cdim_ij = c.shape[n + 1];
                    final int astr_ij = a.stride[n + 1];
                    final int bstr_ij = b.stride[n + 1];
                    final int cstr_ij = c.stride[n + 1];
                    for (int i = 0; i < cdim_i; i++) {
                        int aoff_ij = aoff_i;
                        int boff_ij = boff_i;
                        int coff_ij = coff_i;
                        for (int j = 0; j < cdim_ij; j++) {
                            c.data[coff_ij] = fn.apply(a.data[aoff_ij], v * b.data[boff_ij] + w);
                            aoff_ij += astr_ij;
                            boff_ij += bstr_ij;
                            coff_ij += cstr_ij;
                        }
                        aoff_i += astr_i;
                        boff_i += bstr_i;
                        coff_i += cstr_i;
                    }

                    terminated = true;
                }
                break;
        }

        if (!terminated) {

            // Recursively broadcast

            for (int i = 0; i < cdim_i; i++) {
                this.innerWalk(n + 1, a, aoff_i, b, boff_i, v, w, c, coff_i);
                aoff_i += astr_i;
                boff_i += bstr_i;
                coff_i += cstr_i;
            }
        }
    }

    private BiFunction<Float, Float, Float> fn;
}
