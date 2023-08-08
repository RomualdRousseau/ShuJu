package com.github.romualdrousseau.shuju.op.nn;

import com.github.romualdrousseau.shuju.types.Tensor;

public class L2Norm {

    public static Tensor Op(final Tensor t, final int axis) {
        return t.imul(t.norm(axis).iinv());
    }
}
