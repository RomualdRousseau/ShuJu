package com.github.romualdrousseau.shuju.op.nn;

import com.github.romualdrousseau.shuju.types.Tensor;

public class BatchNorm {

    public static Tensor Op(final Tensor t, final float a, final float b, final int axis) {
        final Tensor avg = t.avg(axis);
        final Tensor invvar = t.var(axis, 0).invsqrt();
        return t.isub(avg).imul(invvar).imul(a).iadd(b);
    }
}
