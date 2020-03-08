package com.github.romualdrousseau.shuju.ml.nn.optimizer.builder;

import com.github.romualdrousseau.shuju.ml.nn.Model;
import com.github.romualdrousseau.shuju.ml.nn.OptimizerBuilder;
import com.github.romualdrousseau.shuju.ml.nn.optimizer.OptimizerSgd;

public class OptimizerSgdBuilder extends OptimizerBuilder<OptimizerSgd> {
    private float momentum;

    public OptimizerSgdBuilder() {
        super();
    }

    public OptimizerSgdBuilder setMomentum(float momentum) {
        this.momentum = momentum;
        return this;
    }

    public OptimizerSgd build(Model model) {
        return new OptimizerSgd(model, this.learningRate, this.scheduler, this.momentum);
    }
}
