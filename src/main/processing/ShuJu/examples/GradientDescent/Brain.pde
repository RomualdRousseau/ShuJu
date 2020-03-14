class Brain_ {
  Model model;
  Optimizer optimizer;
  Loss criterion;
  float accuracy;
  float mean;
  boolean dataChanged;

  Brain_() {
    this.accuracy = 0.0;
    this.mean = 1.0;
    this.dataChanged = false;
  }

  void init(String modelName) {
    if (modelName.equals("Softmax")) {
      this.buildModelSoftmax();
    } else if (modelName.equals("Huber")) {
      this.buildModelHuber();
    } else if (modelName.equals("MSE")) {
      this.buildModelMSE();
    }
  }

  Matrix predict(PVector point) {
    Vector input = new Vector(new float[] { point.x, point.y });
    return this.model.model(input).detach();
  }

  void fit() {
    if (!this.dataChanged && (Map2D.points.size() == 0 || this.mean < 1e-4)) {
      this.dataChanged = false;
      return;
    }

    model.setTrainingMode(true);

    for (int n = 0; n < BRAIN_CLOCK; n++) {
      float sumAccu = 0.0;
      float sumMean = 0.0;

      this.optimizer.zeroGradients();

      for (int i = 0; i < Map2D.points.size(); i++) {
        PVector point = Map2D.points.get(i);

        Vector input = new Vector(new float[] { point.x, point.y });
        Vector target = new Vector(2).oneHot(int(point.z));

        Layer output = this.model.model(input);
        Loss loss = this.criterion.loss(output, target);

        if (output.detachAsVector().argmax() != target.argmax()) {
          this.optimizer.minimize(loss);
        } else {
          sumAccu++;
        }

        sumMean += loss.getValueAsVector().flatten();

        if (Float.isNaN(sumMean)) {
          sumMean = 0.0;
          println(loss.getValue());
          println(target);
          println(output.detach());
        }
      }

      this.optimizer.step();

      this.accuracy = constrain(sumAccu / Map2D.points.size(), 0, 1);
      this.mean = constrain(sumMean / Map2D.points.size(), 0, 1);
    }

    model.setTrainingMode(false);
  }

  void buildModelSoftmax() {
    this.model = new Model();

    this.model.add(new DenseBuilder()
      .setInputUnits(2)
      .setUnits(BRAIN_HIDDEN_NEURONS));

    this.model.add(new BatchNormalizerBuilder());

    this.model.add(new ActivationBuilder()
      .setActivation(new LeakyRelu()));

    this.model.add(new DenseBuilder()
      .setUnits(BRAIN_HIDDEN_NEURONS));

    this.model.add(new BatchNormalizerBuilder());

    this.model.add(new ActivationBuilder()
      .setActivation(new LeakyRelu()));

    this.model.add(new DenseBuilder()
      .setUnits(2));

    this.model.add(new ActivationBuilder()
      .setActivation(new Softmax()));

    this.optimizer = new OptimizerAdamBuilder().build(this.model);

    this.criterion = new Loss(new SoftmaxCrossEntropy());
  }

  void buildModelMSE() {
    this.model = new Model();

    this.model.add(new DenseBuilder()
      .setInputUnits(2)
      .setUnits(BRAIN_HIDDEN_NEURONS));

    this.model.add(new ActivationBuilder()
      .setActivation(new Tanh()));

    this.model.add(new DenseBuilder()
      .setUnits(2));

    this.model.add(new ActivationBuilder()
      .setActivation(new Linear()));

    this.optimizer = new OptimizerSgdBuilder()
      .setLearningRate(0.1)
      .setScheduler(new ExponentialScheduler(0.0001, 1, 0.001))
      .build(this.model);

    this.criterion = new Loss(new MeanSquaredError());
  }

  void buildModelHuber() {
    this.model = new Model();

    this.model.add(new DenseBuilder()
      .setInputUnits(2)
      .setUnits(BRAIN_HIDDEN_NEURONS));

    this.model.add(new ActivationBuilder()
      .setActivation(new Relu()));

    this.model.add(new DenseBuilder()
      .setUnits(2));

    this.model.add(new ActivationBuilder()
      .setActivation(new Linear()));

    this.optimizer = new OptimizerRMSPropBuilder().build(this.model);

    this.criterion = new Loss(new Huber());
  }

  String toString() {
    final StringBuilder result = new StringBuilder();
    this.model.visit(new java.util.function.Consumer<Layer>() {
        public void accept(Layer layer) {
            result.append(String.format("%d -> %d -> %s ->", layer.inputUnits, layer.units, getClassInfo(layer)));
        }
    });
    return result.toString();
  }
}
Brain_ Brain = new Brain_();
