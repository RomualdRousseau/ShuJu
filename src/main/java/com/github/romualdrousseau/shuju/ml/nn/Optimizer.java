package com.github.romualdrousseau.shuju.ml.nn;

import com.github.romualdrousseau.shuju.math.Matrix;

public abstract class Optimizer {
    protected Model model;
    protected LearningRateScheduler scheduler;

    public float learningRate0;
    public float learningRate;
    public int epoch;

    public Optimizer(Model model, float learningRate, LearningRateScheduler scheduler) {
        this.model = model;
        this.learningRate0 = learningRate;
        this.learningRate = learningRate;
        this.epoch = 1;
        this.scheduler = scheduler;
    }

    public void reset() {
        this.learningRate = this.learningRate0;
        this.epoch = 1;
        for (Layer layer = this.model.start.next; layer != null; layer = layer.next) {
            layer.reset(true);
        }
    }

    public void zeroGradients() {
        for (Layer layer = this.model.start.next; layer != null; layer = layer.next) {
            layer.resetGradients(this);
        }
    }

    public void step() {
        for (Layer layer = this.model.start.next; layer != null; layer = layer.next) {
            if(!layer.frozen) {
                layer.adjustGradients(this);
            }
        }

        this.epoch++;

        if (this.scheduler != null) {
            this.scheduler.apply(this);
        }
    }

    public abstract Matrix computeGradients(Parameters p);
}
