package com.github.romualdrousseau.shuju.op.math;

import com.github.romualdrousseau.shuju.types.Tensor;

public class Var {

    public static Tensor Op(Tensor a, final int axis, final float ddof) {
        final float n = (axis == -1) ? a.size : a.shape[axis];
        final Tensor avg = Tensor.of(MathOps.Add.reduce(a, 0.0f, axis, true, null)).idiv(n);
        final Tensor var = Tensor.of(MathOps.MagSq.inner(a, avg, 0.0f, axis, false, null));
        return var.idiv(n - ddof);
    }
}
