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

final int epochCount = 12;
final int trainingCount = 60000;
final int testCount = 10000;
final int batchSize = 120;

int trainingPerRow;
PImage trainingImages;
int[][] trainingLabels;
int[][] shuffler;

int testPerRow;
PImage testImages;
int[][] testLabels;
int[] testMap;

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
  
  shuffler = new int[trainingCount][2];
  for (int i = 0; i < trainingCount; i++) {
    shuffler[i][0] = i % trainingPerRow;
    shuffler[i][1] = i / trainingPerRow;
  }
  
  testMap = new int[testCount];
  for (int i = 0; i < testCount; i++) {
    testMap[i] = int(random(0, 10));
  }
}

void shuffleData() {
  for (int i = 0; i < trainingCount; i++) {
    int j = int(random(trainingCount));
    int tmp_x = shuffler[i][0];
    int tmp_y = shuffler[i][1];
    shuffler[i][0] = shuffler[j][0];
    shuffler[i][1] = shuffler[j][1];
    shuffler[j][0] = tmp_x;
    shuffler[j][1] = tmp_y;
  }
}

void buildModel() {
  model = new Model();

  model.add(new Conv2DBuilder()
    .setInputUnits(MnistImageSize)
    .setInputChannels(1)
    .setFilters(3)
    .setChannels(32));

  model.add(new ActivationBuilder()
    .setActivation(new Relu()));

  model.add(new Conv2DBuilder()
    .setFilters(3)
    .setChannels(64));

  model.add(new ActivationBuilder()
    .setActivation(new Relu()));

  model.add(new MaxPooling2DBuilder()
    .setSize(2));

  model.add(new DropOutBuilder()
    .setRate(0.25));

  model.add(new FlattenBuilder());

  model.add(new DenseBuilder()
    .setUnits(128));

  model.add(new ActivationBuilder()
    .setActivation(new Relu()));

  model.add(new DropOutBuilder()
    .setRate(0.5));

  model.add(new DenseBuilder()
    .setUnits(MnistLabelSize));

  model.add(new ActivationBuilder()
    .setActivation(new Softmax()));

  optimizer = new OptimizerAdaDeltaBuilder()
    .build(model);

  loss = new Loss(new SoftmaxCrossEntropy());
}

void testModel(int epoch, boolean trainingMode) {
  float sumAccu = 0;
  float sumMean = 0;

  for (int i = 0; i < testCount; i++) {
    final int imgx = i % testPerRow;
    final int imgy = i / testPerRow;

    Tensor2D x = image2Tensor2D(testImages, imgx, imgy, MnistImageSize, MnistImageSize).reshape(MnistImageSize * MnistImageSize, 1);
    Tensor2D y = new Tensor2D(MnistLabelSize, 1).oneHot(testLabels[imgx][imgy]);

    Layer y_pred = model.model(x);
    loss.loss(y_pred, y);

    final boolean isCorrect = y_pred.detach().argmax(0, 0) == y.argmax(0, 0);
    sumMean += loss.getValue().flatten(0, 0);
    sumAccu += isCorrect ? 1 : 0;
    testMap[i] = y_pred.detach().argmax(0, 0);
  }

  if (trainingMode) {
    println(String.format("Epoch %d/%d: Average Loss: %.3f | Accuracy: %.3f%%", epoch, epochCount, sumMean / testCount, sumAccu * 100 / testCount));
  } else {
    println(String.format("Test: Average Loss: %.3f | Accuracy: %.3f%%", sumMean / testCount, sumAccu * 100 / testCount));
    noLoop();
  }
}

void fitModel() {
  final int batchCount = trainingCount / batchSize;

  float sumAccu = 0;
  float sumMean = 0;

  shuffleData();

  for (int b = 0; b < batchCount; b++) {
    final int batchStart = b * batchSize;
    final int batchEnd = Math.min(batchStart + batchSize, trainingCount);

    optimizer.zeroGradients();
    for (int i = batchStart; i < batchEnd; i++) {
      final int imgx = shuffler[i][0];
      final int imgy = shuffler[i][1];

      Tensor2D x = image2Tensor2D(trainingImages, imgx, imgy, MnistImageSize, MnistImageSize).reshape(MnistImageSize * MnistImageSize, 1);
      Tensor2D y = new Tensor2D(MnistLabelSize, 1).oneHot(trainingLabels[imgx][imgy]);

      Layer y_pred = model.model(x, true);
      loss.loss(y_pred, y);

      sumMean += loss.getValue().flatten(0, 0);
      if (y_pred.detach().argmax(0, 0) == y.argmax(0, 0)) {
        sumAccu++;
      }

      optimizer.minimize(loss);
    }
    optimizer.step();

    print(".");

    if ((b % 100) == 99) {
      println();
      println(String.format("[Step %d] Past 100 batchs: Average Loss: %.3f | Accuracy: %.3f%%", (b % batchCount) + 1, sumMean / (batchSize * 100), sumAccu / batchSize));
      sumAccu = 0;
      sumMean = 0;
    }
  }
}

void setup() {
  size(800, 800);
  textSize(32);
  textAlign(CENTER, CENTER);
  
  loadData();

  buildModel();
  
  thread("train");
}

void draw() {
  drawBadges();
  drawZoom(mouseX, mouseY);
}

void train() {
  for (int epoch = 1; epoch <= epochCount; epoch++) {
    fitModel();
    testModel(epoch, epoch <= epochCount);
  }
}
