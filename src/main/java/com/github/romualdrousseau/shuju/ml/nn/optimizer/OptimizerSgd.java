package com.github.romualdrousseau.shuju.ml.nn.optimizer;

import com.github.romualdrousseau.shuju.math.Tensor2D;
import com.github.romualdrousseau.shuju.math.Tensor3D;
import com.github.romualdrousseau.shuju.ml.nn.Model;
import com.github.romualdrousseau.shuju.ml.nn.Optimizer;
import com.github.romualdrousseau.shuju.ml.nn.Parameters2D;
import com.github.romualdrousseau.shuju.ml.nn.Parameters3D;
import com.github.romualdrousseau.shuju.ml.nn.LearningRateScheduler;

public class OptimizerSgd extends Optimizer {
    private float momemtum;

    public OptimizerSgd(Model model, float learningRate, LearningRateScheduler scheduler) {
        this(model, learningRate, scheduler, 0.9f);
      }

    public OptimizerSgd(Model model, float learningRate, LearningRateScheduler scheduler, float momentum) {
      super(model, learningRate, scheduler);
      this.momemtum = momentum;
    }

    public Tensor2D computeGradients(Parameters2D p) {
      final float lr = this.learningRate;
      p.M.mul(this.momemtum).fma(p.G, 1.0f - this.momemtum);
      return p.M.copy().mul(lr);
    }

    public Tensor3D computeGradients(Parameters3D p) {
        final float lr = this.learningRate;
        p.M.mul(this.momemtum).fma(p.G, 1.0f - this.momemtum);
        return p.M.copy().mul(lr);
      }
  }
