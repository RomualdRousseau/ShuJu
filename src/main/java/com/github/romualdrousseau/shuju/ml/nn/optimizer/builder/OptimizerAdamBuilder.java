package com.github.romualdrousseau.shuju.ml.nn.optimizer.builder;

import com.github.romualdrousseau.shuju.ml.nn.Model;
import com.github.romualdrousseau.shuju.ml.nn.OptimizerBuilder;
import com.github.romualdrousseau.shuju.ml.nn.optimizer.OptimizerAdam;

public class OptimizerAdamBuilder extends OptimizerBuilder<OptimizerAdam> {
    private float b1;
    private float b2;

    public OptimizerAdamBuilder() {
      super();
      this.b1 = 0.9f;
      this.b2 = 0.999f;
    }

    public OptimizerAdamBuilder setB1(float b1) {
      this.b1 = b1;
      return this;
    }

    public OptimizerAdamBuilder setB2(float b2) {
      this.b2 = b2;
      return this;
    }

    public OptimizerAdam build(Model model) {
      return new OptimizerAdam(model, this.learningRate, this.scheduler, this.b1, this.b2);
    }
  }
