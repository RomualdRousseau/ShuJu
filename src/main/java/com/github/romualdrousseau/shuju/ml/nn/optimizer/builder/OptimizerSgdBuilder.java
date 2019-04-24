package com.github.romualdrousseau.shuju.ml.nn.optimizer.builder;

import com.github.romualdrousseau.shuju.ml.nn.Model;
import com.github.romualdrousseau.shuju.ml.nn.OptimizerBuilder;
import com.github.romualdrousseau.shuju.ml.nn.optimizer.OptimizerSgd;

public class OptimizerSgdBuilder extends OptimizerBuilder<OptimizerSgd> {
    public OptimizerSgdBuilder() {
        super();
    }

    public OptimizerSgd build(Model model) {
        return new OptimizerSgd(model, this.learningRate, this.scheduler);
    }
}
