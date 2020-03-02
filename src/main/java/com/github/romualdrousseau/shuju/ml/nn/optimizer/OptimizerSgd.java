package com.github.romualdrousseau.shuju.ml.nn.optimizer;

import com.github.romualdrousseau.shuju.math.Matrix;
import com.github.romualdrousseau.shuju.ml.nn.Model;
import com.github.romualdrousseau.shuju.ml.nn.Optimizer;
import com.github.romualdrousseau.shuju.ml.nn.Parameters;
import com.github.romualdrousseau.shuju.ml.nn.LearningRateScheduler;

public class OptimizerSgd extends Optimizer {
    private float momemtum;

    public OptimizerSgd(Model model, float learningRate, LearningRateScheduler scheduler) {
      super(model, learningRate, scheduler);
      this.momemtum = 0.9f;
    }

    public Matrix computeGradients(Parameters p) {
      final float lr = this.learningRate;
      p.M.mul(this.momemtum).fma(p.G, 1.0f - this.momemtum);
      return p.M.copy().mul(lr);
    }
  }
