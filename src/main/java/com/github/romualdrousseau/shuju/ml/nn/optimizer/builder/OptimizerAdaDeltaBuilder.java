package com.github.romualdrousseau.shuju.ml.nn.optimizer.builder;

import com.github.romualdrousseau.shuju.ml.nn.LearningRateScheduler;
import com.github.romualdrousseau.shuju.ml.nn.Model;
import com.github.romualdrousseau.shuju.ml.nn.OptimizerBuilder;
import com.github.romualdrousseau.shuju.ml.nn.optimizer.OptimizerAdaDelta;

public class OptimizerAdaDeltaBuilder extends OptimizerBuilder<OptimizerAdaDelta> {
    private float b;

    public OptimizerAdaDeltaBuilder() {
        super();
        this.b = 0.9f;
    }

    public OptimizerAdaDelta build(Model model) {
        return new OptimizerAdaDelta(model, this.learningRate, this.scheduler, this.b);
    }

    public OptimizerAdaDeltaBuilder setLearningRate(float learningRate) {
        this.learningRate = learningRate;
        return this;
    }

    public OptimizerAdaDeltaBuilder setScheduler(LearningRateScheduler scheduler) {
        this.scheduler = scheduler;
        return this;
    }

    public OptimizerAdaDeltaBuilder setB(float b) {
        this.b = b;
        return this;
    }
}
