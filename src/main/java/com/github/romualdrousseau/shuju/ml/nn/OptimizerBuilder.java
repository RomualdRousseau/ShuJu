package com.github.romualdrousseau.shuju.ml.nn;

public abstract class OptimizerBuilder<T extends Optimizer> {
    protected float learningRate;
    protected LearningRateScheduler scheduler;

    public OptimizerBuilder() {
        this.learningRate = 0.001f;
        this.scheduler = null;
    }

    public OptimizerBuilder<T> setLearningRate(float learningRate) {
        this.learningRate = learningRate;
        return this;
    }

    public OptimizerBuilder<T> setScheduler(LearningRateScheduler scheduler) {
        this.scheduler = scheduler;
        return this;
    }

    public abstract T build(Model model);
}
