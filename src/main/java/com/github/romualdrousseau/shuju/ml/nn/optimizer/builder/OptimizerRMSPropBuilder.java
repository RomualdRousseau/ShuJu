package com.github.romualdrousseau.shuju.ml.nn.optimizer.builder;

import com.github.romualdrousseau.shuju.ml.nn.Model;
import com.github.romualdrousseau.shuju.ml.nn.OptimizerBuilder;
import com.github.romualdrousseau.shuju.ml.nn.optimizer.OptimizerRMSProp;

public class OptimizerRMSPropBuilder extends OptimizerBuilder<OptimizerRMSProp> {
    private float b;

    public OptimizerRMSPropBuilder() {
        super();
        this.b = 0.9f;
    }

    public OptimizerRMSPropBuilder setB(float b) {
        this.b = b;
        return this;
    }

    public OptimizerRMSProp build(Model model) {
        return new OptimizerRMSProp(model, this.learningRate, this.scheduler, this.b);
    }
}
