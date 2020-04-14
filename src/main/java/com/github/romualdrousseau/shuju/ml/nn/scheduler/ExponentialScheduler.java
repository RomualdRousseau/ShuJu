package com.github.romualdrousseau.shuju.ml.nn.scheduler;

import com.github.romualdrousseau.shuju.math.Scalar;
import com.github.romualdrousseau.shuju.ml.nn.Optimizer;
import com.github.romualdrousseau.shuju.ml.nn.LearningRateScheduler;

public class ExponentialScheduler implements LearningRateScheduler {
    private float decay;
    private int step;
    private float minRate;

    public ExponentialScheduler(float decay, int step, float minRate) {
      this.decay = decay;
      this.step = step;
      this.minRate = minRate;
    }

    public void apply(Optimizer optimizer) {
      int epoch = optimizer.time / this.step;
      optimizer.learningRate = Scalar.max(this.minRate, optimizer.learningRate0 * Scalar.exp(-this.decay * epoch));
    }
  }
