package com.github.romualdrousseau.shuju.ml.slr;

import com.github.romualdrousseau.shuju.math.Matrix;
import com.github.romualdrousseau.shuju.math.Vector;

public class SLR {
    public void fit(final Vector[] inputs, final Vector[] targets) {
        Matrix m1 = new Matrix(inputs);
        Matrix m2 = new Matrix(targets);
        Matrix m3 = m1.cov(m2).div(m1.var());
        this.beta = m3.toVector(0);
        this.alpha = m2.avg().sub(m3.mult(m1.avg())).toVector(0);
    }

    public Vector predict(final Vector row) {
        return row.copy().mult(this.beta).add(this.alpha);
    }

    private Vector beta;
    private Vector alpha;
}
