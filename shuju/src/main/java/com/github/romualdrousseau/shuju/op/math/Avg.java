package com.github.romualdrousseau.shuju.op.math;

import com.github.romualdrousseau.shuju.types.Tensor;

public class Avg {

    public static Tensor Op(final Tensor t, final int axis) {
        final float n = (axis == -1) ? t.size : t.shape[axis];
        return t.sum(axis).idiv(n);
    }
}
