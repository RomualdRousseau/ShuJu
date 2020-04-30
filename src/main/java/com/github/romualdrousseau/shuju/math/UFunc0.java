package com.github.romualdrousseau.shuju.math;

import java.util.function.BiFunction;

public class UFunc0 extends UFunc {

    public BiFunction<Float, Float, Float> func;

    public UFunc0(BiFunction<Float, Float, Float> func) {
        this.func = func;
    }

    @Override
    public MArray reduce(MArray a, final float initital, final int axis, MArray out) {
        assert (axis == -1 || axis >= 0 && axis < a.shape.length) : "axis must be less than dimension";

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

        if (axis == -1 && a.base == 0) {
            float acc = initital;
            for (int i = 0; i < a.size; i++) {
                acc = func.apply(acc, a.data[i]);
                out.data[0] = acc;
            }
        } else {
            this._reduce(0, 0, a, a.base, initital, axis, out, out.base);
        }

        return out;
    }

    @Override
    public MArray accumulate(final MArray a, final float initital, final int axis, MArray out) {
        assert (axis >= 0 && axis < a.shape.length) : "axis must be less than dimension";

        if (out == null) {
            out = new MArray(a.shape);
        }

        this._accumulate(0, a, a.base, initital, axis, out, out.base);

        return out;
    }

    @Override
    public MArray outer(final MArray a, final float b, MArray out) {
        if (out == null) {
            out = new MArray(a.shape);
        }

        if (a.base == 0) {
            for (int i = 0; i < a.size; i++) {
                out.data[i] = this.func.apply(b, a.data[i]);
            }
        } else {
            this._outer(0, a, a.base, b, out, out.base);
        }

        return out;
    }

    @Override
    public MArray outer(final MArray a, final MArray b, MArray out) {
        assert (a.shape.length == b.shape.length) : "Arrays must be same dimensions";

        if (out == null) {
            int newShapeLen = a.shape.length;
            int[] newShape = new int[newShapeLen];
            for (int i = 0; i < newShapeLen; i++) {
                newShape[i] = Math.max(a.shape[i], b.shape[i]);
            }
            out = new MArray(newShape);
        }

        this._outer(0, a, a.base, b, b.base, out, out.base);

        return out;
    }

    private void _reduce(final int n1, final int n2, final MArray a, final int aoff, final float initial, int axis,
            final MArray b, final int boff) {
        final int cnt = a.shape.length - 1;
        final int n1_i = n1 + 1;
        final int n2_i;
        final int dim_i = a.shape[n1];
        final int astr_i = a.stride[n1];
        final int bstr_i;
        int aoff_i = aoff;
        int boff_i = boff;
        float acc;
        boolean terminated = false;

        if (axis == -1 || n1 == axis) {
            bstr_i = 0;
            n2_i = n2;
        } else {
            bstr_i = b.stride[n2];
            n2_i = n2 + 1;
        }

        switch (cnt - n1 + 1) {
            case 1:

                // Case for vector

                acc = initial;
                for (int i = 0; i < dim_i; i++) {
                    acc = func.apply(acc, a.data[aoff_i]);
                    b.data[boff_i] = acc;
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

                if (axis == -1 || n1 + 1 == axis) {
                    bstr_ij = 0;
                } else {
                    bstr_ij = b.stride[n2_i];
                }

                for (int i = 0; i < dim_i; i++) {
                    int aoff_ij = aoff_i;
                    int boff_ij = boff_i;
                    acc = initial;
                    for (int j = 0; j < dim_ij; j++) {
                        acc = func.apply(acc, a.data[aoff_ij]);
                        b.data[boff_ij] = acc;
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
                this._reduce(n1_i, n2_i, a, aoff_i, initial, axis, b, boff_i);
                aoff_i += astr_i;
                boff_i += bstr_i;
            }
        }
    }

    private void _accumulate(final int n, final MArray a, final int aoff, final float initial, final int axis,
            final MArray b, final int boff) {
        final int cnt = a.shape.length - 1;
        final int dim_i;
        final int astr_i;
        final int bstr_i;
        int aoff_i = aoff;
        int boff_i = boff;
        float acc;
        boolean terminated = false;

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
                    acc = func.apply(acc, a.data[aoff_i]);
                    b.data[boff_i] = acc;
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
                    acc = 0.0f;
                    for (int j = 0; j < dim_ij; j++) {
                        acc = func.apply(acc, a.data[aoff_ij]);
                        b.data[boff_ij] = acc;
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
                this._accumulate(n + 1, a, aoff_i, initial, axis, b, boff_i);
                aoff_i += astr_i;
                boff_i += bstr_i;
            }
        }
    }

    private void _outer(final int n, final MArray a, final int aoff, final float b, final MArray c,
            final int coff) {
        final int cnt = a.shape.length - 1;
        final int adim_i = a.shape[n];
        final int cdim_i = c.shape[n];
        final int astr_i;
        final int cstr_i = c.stride[n];
        int aoff_i = aoff;
        int coff_i = coff;
        boolean terminated = false;

        if (adim_i == 1) {
            astr_i = 0;
        } else {
            astr_i = a.stride[n];
        }

        switch (cnt - n + 1) {
            case 1:

                // Case for vector

                for (int i = 0; i < cdim_i; i++) {
                    c.data[coff_i] = func.apply(b, a.data[aoff_i]);
                    aoff_i += astr_i;
                    coff_i += cstr_i;
                }

                terminated = true;
                break;

            case 2:

                // Case for Matrix

                if (a.shape[n] == 1 && a.shape[n + 1] == 1) {
                    final int cdim_ij = c.shape[n + 1];
                    final int astr_ij = a.stride[n + 1];
                    final int cstr_ij = c.stride[n + 1];
                    for (int i = 0; i < cdim_i; i++) {
                        int aoff_ij = aoff_i;
                        int coff_ij = coff_i;
                        for (int j = 0; j < cdim_ij; j++) {
                            c.data[coff_ij] = func.apply(b, a.data[aoff_ij]);
                            aoff_ij += astr_ij;
                            coff_ij += cstr_ij;
                        }
                        aoff_i += astr_i;
                        coff_i += cstr_i;
                    }

                    terminated = true;
                }
                break;
        }

        if (!terminated) {

            // Recursively broadcast

            for (int i = 0; i < cdim_i; i++) {
                this._outer(n + 1, a, aoff_i, b, c, coff_i);
                aoff_i += astr_i;
                coff_i += cstr_i;
            }
        }
    }

    private void _outer(final int n, final MArray a, final int aoff, final MArray b, final int boff, final MArray c,
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
                    c.data[coff_i] = func.apply(b.data[boff_i], a.data[aoff_i]);
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
                            c.data[coff_ij] = func.apply(b.data[boff_ij], a.data[aoff_ij]);
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
                this._outer(n + 1, a, aoff_i, b, boff_i, c, coff_i);
                aoff_i += astr_i;
                boff_i += bstr_i;
                coff_i += cstr_i;
            }
        }
    }
}
