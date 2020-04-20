package com.github.romualdrousseau.shuju.ml.nn;

import java.util.function.Consumer;

import com.github.romualdrousseau.shuju.math.Tensor2D;
import com.github.romualdrousseau.shuju.math.Tensor3D;

public abstract class Optimizer {

    protected Model model;
    protected LearningRateScheduler scheduler;

    public float learningRate0;
    public float learningRate;
    public int time;

    public Optimizer(Model model, float learningRate, LearningRateScheduler scheduler) {
        this.model = model;
        this.learningRate0 = learningRate;
        this.learningRate = learningRate;
        this.time = 1;
        this.scheduler = scheduler;
    }

    public void reset() {
        this.learningRate = this.learningRate0;
        this.time = 1;
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
            Tensor2D d_L_d_out = loss.getRate();
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
                if (!layer.frozen) {
                    layer.completeBackward(me);
                }
            }
        });

        this.time++;

        if (this.scheduler != null) {
            this.scheduler.apply(this);
        }
    }

    public abstract Tensor2D computeGradients(Parameters2D p);

    public abstract Tensor3D computeGradients(Parameters3D p);
}
