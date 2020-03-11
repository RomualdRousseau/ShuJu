package com.github.romualdrousseau.shuju.ml.nn;

import java.util.function.Consumer;

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
        this.model.reset();
    }

    public void zeroGradients() {
        Optimizer me = this;
        this.model.visit(new Consumer<Layer>() {
            public void accept(Layer layer) {
                layer.startBackward(me);
            }
        });
    }

    public Loss minimize(Loss loss) {
        this.model.visitBackward(new Consumer<Layer>() {
            Matrix d_L_d_out = loss.getRate();
            public void accept(Layer layer) {
                d_L_d_out = layer.callBackward(d_L_d_out);
            }
        });
        return loss;
    }

    public void step() {
        Optimizer me = this;
        this.model.visit(new Consumer<Layer>() {
            public void accept(Layer layer) {
                layer.completeBackward(me);
            }
        });

        this.epoch++;

        if (this.scheduler != null) {
            this.scheduler.apply(this);
        }
    }

    public abstract Matrix computeGradients(Parameters p);
}
