package com.github.romualdrousseau.shuju.op.math;

import com.github.romualdrousseau.shuju.op.linalg.MatMul;
import com.github.romualdrousseau.shuju.types.Tensor;

public class Cov {

    public static Tensor Op(final Tensor a, final Tensor b, final boolean rowvar, final float ddof) {
        assert (a.shape.length <= 2 && a.shape.length == b.shape.length) : "Illegal shape";

        final int axis = rowvar ? 0 : 1;

        final float n1 = a.shape[axis];
        final Tensor avg1 = Tensor.of(MathOps.Add.reduce(a, 0.0f, axis, true, null)).idiv(n1);
        final Tensor step1 = a.sub(avg1);
        if (axis == 0) {
            step1.transpose();
        }

        final Tensor step2;
        if (a == b) {
            step2 = step1.T();
        } else {
            final float n2 = b.shape[axis];
            final Tensor avg2 = Tensor.of(MathOps.Add.reduce(b, 0.0f, axis, true, null)).idiv(n2);
            step2 = b.sub(avg2);
            if (axis == 1) {
                step2.transpose();
            }
        }

        return MatMul.Op.apply(step1, step2).idiv(n1 - ddof);
    }
}
