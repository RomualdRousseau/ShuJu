package com.github.romualdrousseau.shuju.nn;

import com.github.romualdrousseau.shuju.core.UFunc0;
import com.github.romualdrousseau.shuju.types.Scalar;
import com.github.romualdrousseau.shuju.types.Tensor;

public class Softmax {

    public static Tensor Op(Tensor a, final int axis) {
        final Tensor c = a.max(axis);
        new UFunc0((x, y) -> Scalar.exp(y - x)).outer(a, c, a);
        final Tensor sum = a.sum(axis);
        return a.idiv(sum);
    }
}
