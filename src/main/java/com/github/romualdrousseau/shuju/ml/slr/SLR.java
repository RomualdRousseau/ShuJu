package com.github.romualdrousseau.shuju.ml.slr;

import com.github.romualdrousseau.shuju.math.Matrix;
import com.github.romualdrousseau.shuju.math.Vector;

public class SLR {
    public void fit(final Vector[] inputs, final Vector[] targets) {
        assert (inputs.length == targets.length);
        Matrix m1 = new Matrix(inputs);
        Matrix m2 = new Matrix(targets);
        Matrix m3 = m1.cov(m2, 0, false).div(m1.var(0));
        this.beta = m3.get(0);
        this.alpha = m2.avg(0).sub(m3.mul(m1.avg(0))).get(0);
    }

    public Vector predict(final Vector row) {
        return row.copy().mul(this.beta).add(this.alpha);
    }

    private Vector beta;
    private Vector alpha;
}
