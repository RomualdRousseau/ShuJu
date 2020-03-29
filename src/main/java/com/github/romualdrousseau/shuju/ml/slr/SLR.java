package com.github.romualdrousseau.shuju.ml.slr;

import com.github.romualdrousseau.shuju.math.Tensor2D;
import com.github.romualdrousseau.shuju.math.Tensor1D;

public class SLR {
    public void fit(final Tensor1D[] inputs, final Tensor1D[] targets) {
        assert (inputs.length == targets.length);
        Tensor2D m1 = new Tensor2D(inputs);
        Tensor2D m2 = new Tensor2D(targets);
        Tensor2D m3 = m1.cov(m2, 0, false).div(m1.var(0));
        this.beta = new Tensor1D(m3.getFloats(0));
        this.alpha = new Tensor1D(m2.avg(0).sub(m3.mul(m1.avg(0))).getFloats(0));
    }

    public Tensor1D predict(final Tensor1D row) {
        return row.copy().mul(this.beta).add(this.alpha);
    }

    private Tensor1D beta;
    private Tensor1D alpha;
}
