package com.github.romualdrousseau.shuju.ml.nn;

public interface LearningRateScheduler {
    void apply(Optimizer optimizer);
}
