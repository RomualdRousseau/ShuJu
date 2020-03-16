import com.github.romualdrousseau.shuju.columns.*;
import com.github.romualdrousseau.shuju.cv.*;
import com.github.romualdrousseau.shuju.*;
import com.github.romualdrousseau.shuju.json.*;
import com.github.romualdrousseau.shuju.math.*;
import com.github.romualdrousseau.shuju.ml.nn.activation.*;
import com.github.romualdrousseau.shuju.ml.nn.initializer.*;
import com.github.romualdrousseau.shuju.ml.nn.*;
import com.github.romualdrousseau.shuju.ml.nn.loss.*;
import com.github.romualdrousseau.shuju.ml.nn.optimizer.*;
import com.github.romualdrousseau.shuju.ml.qlearner.*;
import com.github.romualdrousseau.shuju.nlp.*;
import com.github.romualdrousseau.shuju.transforms.*;
import com.github.romualdrousseau.shuju.json.jackson.*;
import com.github.romualdrousseau.shuju.json.processing.*;
import com.github.romualdrousseau.shuju.ml.nn.optimizer.builder.*;
import com.github.romualdrousseau.shuju.util.*;
import com.github.romualdrousseau.shuju.nlp.impl.*;
import com.github.romualdrousseau.shuju.cv.templatematching.*;
import com.github.romualdrousseau.shuju.cv.templatematching.shapeextractor.*;
import com.github.romualdrousseau.shuju.genetic.*;
import com.github.romualdrousseau.shuju.math.distribution.*;
import com.github.romualdrousseau.shuju.ml.kmean.*;
import com.github.romualdrousseau.shuju.ml.nn.layer.builder.*;
import com.github.romualdrousseau.shuju.ml.nn.scheduler.*;
import com.github.romualdrousseau.shuju.ml.slr.*;
import com.github.romualdrousseau.shuju.ml.knn.*;
import com.github.romualdrousseau.shuju.ml.nn.layer.*;
import com.github.romualdrousseau.shuju.ml.naivebayes.*;

final int epochCount = 10;
final int trainingCount = 60000;
final int testCount = 10000;

int trainingPerRow;
PImage trainingImages;
int[][] trainingLabels;

int testPerRow;
PImage testImages;
int[][] testLabels;

Model model;
Optimizer optimizer;
Loss loss;

void loadData() {
  trainingPerRow = ceil(sqrt(trainingCount));
  trainingImages = readMnistImages("train-images.idx3-ubyte", trainingCount);
  trainingLabels = readMnistLabels("train-labels.idx1-ubyte", trainingCount);

  testPerRow = ceil(sqrt(testCount));
  testImages = readMnistImages("t10k-images.idx3-ubyte", testCount);
  testLabels = readMnistLabels("t10k-labels.idx1-ubyte", testCount);
}

void buildModel() {
  model = new Model();

  model.add(new Conv2DBuilder()
    .setBias(0.1)
    .setInputUnits(MnistImageSize)
    .setInputChannels(1)
    .setFilters(5)
    .setChannels(8));

  model.add(new ActivationBuilder()
    .setActivation(new Relu()));

  model.add(new Conv2DBuilder()
    .setBias(0.1)
    .setFilters(3)
    .setChannels(8));

  model.add(new MaxPooling2DBuilder()
    .setSize(2));

  model.add(new FlattenBuilder());

  model.add(new DropOutBuilder()
    .setRate(0.4));

  model.add(new DenseBuilder()
    .setUnits(100));

  model.add(new BatchNormalizerBuilder());

  model.add(new ActivationBuilder()
    .setActivation(new Relu()));

  model.add(new DenseBuilder()
    .setUnits(MnistLabelSize));

  model.add(new ActivationBuilder()
    .setActivation(new Softmax()));

  optimizer = new OptimizerAdamBuilder()
    .build(model);

  loss = new Loss(new SoftmaxCrossEntropy());
}

void fitModel() {
  final int oneEpoch = 1000;
  final int batchSize = trainingCount / oneEpoch;
  int batchStart = 0;
  float sumAccu = 0;
  float sumMean = 0;

  model.setTrainingMode(true);

  for (int k = 0; k < epochCount * oneEpoch; k++) {
    if ((k % oneEpoch) == 0) {
      println(String.format("Epoch %d/%d", k / oneEpoch + 1, epochCount));
    }

    if ((k % 100) == 99) {
      println();
      println(String.format("[Step %d] Past 100 steps: Average Loss: %.3f | Accuracy: %.3f%%", k + 1, sumMean / (batchSize * 100), sumAccu / batchSize));
      sumAccu = 0;
      sumMean = 0;
    }

    optimizer.zeroGradients();
    for (int i = batchStart; i < batchStart + batchSize; i++) {
      final int imgx = i % trainingPerRow;
      final int imgy = i / trainingPerRow;

      Matrix x = image2Matrix(trainingImages, imgx, imgy, MnistImageSize, MnistImageSize).reshape(MnistImageSize * MnistImageSize, 1);
      Matrix y = new Matrix(new Vector(MnistLabelSize).oneHot(trainingLabels[imgx][imgy]), false);

      Layer output = this.model.model(x);
      loss.loss(output, y);

      if (output.detach().argmax(0, 0) == y.argmax(0, 0)) {
        sumAccu++;
      } else {
        optimizer.minimize(loss);
      }

      sumMean += loss.getValue().flatten(0, 0);
    }
    optimizer.step();

    batchStart = (batchStart + batchSize) % trainingCount;

    print(".");
  }
  println();

  model.setTrainingMode(false);
}

void testModel() {
  final int w = width / testPerRow;
  final int h = height / testPerRow;
  float sumAccu = 0;
  float sumMean = 0;

  for (int i = 0; i < testCount; i++) {
    final int imgx = i % testPerRow;
    final int imgy = i / testPerRow;

    Matrix x = image2Matrix(testImages, imgx, imgy, MnistImageSize, MnistImageSize).reshape(MnistImageSize * MnistImageSize, 1);
    Matrix y = new Matrix(new Vector(MnistLabelSize).oneHot(testLabels[imgx][imgy]), false);

    Layer output = this.model.model(x);
    loss.loss(output, y);

    if (output.detach().argmax(0, 0) == y.argmax(0, 0)) {
      fill(0, 255, 0, 128);
      noStroke();
      ellipse((imgx + 0.5) * w, (imgy + 0.5) * h, w, h);
      sumAccu++;
    } else {
      fill(255, 0, 0, 128);
      noStroke();
      ellipse((imgx + 0.5) * w, (imgy + 0.5) * h, w, h);
      fill(255);
      stroke(255);
      text(output.detach().argmax(0, 0), (imgx) * w, (imgy + 1) * h);
    }

    sumMean += loss.getValue().flatten(0, 0);
  }

  println(String.format("[Final step] Test: Average Loss: %.3f | Accuracy: %.3f%%", sumMean / testCount, sumAccu * 100 / testCount));
}

void setup() {
  size(800, 800);
  textSize(8);
  noLoop();

  loadData();

  buildModel();

  fitModel();
}

void draw() {
  background(51);

  image(testImages, 0, 0, width, height);

  testModel();
}
