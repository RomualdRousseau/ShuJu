import com.github.romualdrousseau.shuju.columns.*;
import com.github.romualdrousseau.shuju.cv.*;
import com.github.romualdrousseau.shuju.*;
import com.github.romualdrousseau.shuju.json.jackson.*;
import com.github.romualdrousseau.shuju.json.processing.*;
import com.github.romualdrousseau.shuju.math.*;
import com.github.romualdrousseau.shuju.ml.nn.activation.*;
import com.github.romualdrousseau.shuju.ml.nn.initializer.*;
import com.github.romualdrousseau.shuju.ml.nn.loss.*;
import com.github.romualdrousseau.shuju.ml.nn.optimizer.builder.*;
import com.github.romualdrousseau.shuju.ml.nn.*;
import com.github.romualdrousseau.shuju.ml.qlearner.*;
import com.github.romualdrousseau.shuju.util.*;
import com.github.romualdrousseau.shuju.nlp.*;
import com.github.romualdrousseau.shuju.cv.templatematching.*;
import com.github.romualdrousseau.shuju.genetic.*;
import com.github.romualdrousseau.shuju.math.distribution.*;
import com.github.romualdrousseau.shuju.ml.kmean.*;
import com.github.romualdrousseau.shuju.cv.templatematching.shapeextractor.*;
import com.github.romualdrousseau.shuju.json.*;
import com.github.romualdrousseau.shuju.ml.nn.layer.builder.*;
import com.github.romualdrousseau.shuju.ml.nn.optimizer.*;
import com.github.romualdrousseau.shuju.ml.nn.scheduler.*;
import com.github.romualdrousseau.shuju.ml.slr.*;
import com.github.romualdrousseau.shuju.transforms.*;
import com.github.romualdrousseau.shuju.ml.knn.*;
import com.github.romualdrousseau.shuju.ml.nn.layer.*;
import com.github.romualdrousseau.shuju.ml.naivebayes.*;
import com.github.romualdrousseau.shuju.nlp.impl.*;

static final String[] flowerWords = {
  "I. setosa",
  "I. versicolor",
  "I. virginica"
};

final int iterationCount = 100;
final int timeStepX = 10;

ArrayList<Float[]> fisherSet;
Vector[] trainingInputs;
Vector[] trainingTargets;
Vector[] testInputs;
Vector[] testTargets;
Model model;
Optimizer optimizer;
Loss loss;
int epochs = 0;

FloatList timeLine1 = new FloatList();
FloatList timeLine2 = new FloatList();

public void loadDataSet(String fileName) throws IOException {
  fisherSet = new ArrayList<Float[]>();
  Table table = loadTable(fileName, "header");
  for (TableRow row : table.rows()) {
    Float[] dataRow = new Float[7];
    float[] label = categoricalFeature(row.getString(5), flowerWords);
    dataRow[0] = row.getFloat(1);
    dataRow[1] = row.getFloat(2);
    dataRow[2] = row.getFloat(3);
    dataRow[3] = row.getFloat(4);
    dataRow[4] = label[0];
    dataRow[5] = label[1];
    dataRow[6] = label[2];
    fisherSet.add(dataRow);
  }
  normalize(fisherSet, 0);
  normalize(fisherSet, 1);
  normalize(fisherSet, 2);
  normalize(fisherSet, 3);
}

void buildModel() {
  model = new Model();

  model.add(new DenseBuilder()
    .setInputUnits(4)
    .setUnits(64));

  model.add(new DropOutBuilder());

  model.add(new BatchNormalizerBuilder());

  model.add(new ActivationBuilder()
    .setActivation(new Relu()));

  model.add(new DenseBuilder()
    .setUnits(3));

  model.add(new ActivationBuilder()
    .setActivation(new Softmax()));

  optimizer = new OptimizerRMSPropBuilder().build(model);

  loss = new Loss(new SoftmaxCrossEntropy());
}

void buildTraingAndTestSets() {
  // Partition the data: 80% training, 20% test
  shuffle(fisherSet);
  int p = floor(fisherSet.size() * 0.7);
  ArrayList<Float[]> training = subset(fisherSet, 0, p);
  ArrayList<Float[]> test = subset(fisherSet, p, fisherSet.size());

  // Training to Matrix
  trainingInputs = new Vector[training.size()];
  trainingTargets = new Vector[training.size()];
  for (int i = 0; i < training.size(); i++) {
    Float[] data = training.get(i);
    trainingInputs[i] = new Vector(4);
    trainingInputs[i].set(0, data[0]);
    trainingInputs[i].set(1, data[1]);
    trainingInputs[i].set(2, data[2]);
    trainingInputs[i].set(3, data[3]);
    trainingTargets[i] = new Vector(3);
    trainingTargets[i].set(0, data[4]);
    trainingTargets[i].set(1, data[5]);
    trainingTargets[i].set(2, data[6]);
  }

  // Test to Matrix
  testInputs = new Vector[test.size()];
  testTargets = new Vector[test.size()];
  for (int i = 0; i < test.size(); i++) {
    Float[] data = test.get(i);
    testInputs[i] = new Vector(4);
    testInputs[i].set(0, data[0]);
    testInputs[i].set(1, data[1]);
    testInputs[i].set(2, data[2]);
    testInputs[i].set(3, data[3]);
    testTargets[i] = new Vector(3);
    testTargets[i].set(0, data[4]);
    testTargets[i].set(1, data[5]);
    testTargets[i].set(2, data[6]);
  }
}

void setup() {
  size(800, 800);
  textSize(16);
  colorMode(HSB, 360, 100, 100, 100);

  try {
    loadDataSet("fisher's data.csv");
    buildTraingAndTestSets();
    buildModel();
  }
  catch(Exception x) {
    x.printStackTrace();
  }
}

void draw() {
  background(51);
  stroke(255);
  line(width / 2, 0, width / 2, height);
  line(0, height / 2, width, height / 2);

  model.setTrainingMode(true);
  float error = 0.0;
  for (int i = 0; i < iterationCount; i++) {
    optimizer.zeroGradients();
    for(int j = 0; j < trainingInputs.length; j++) {
      optimizer.minimize(loss.loss(model.model(trainingInputs[j]), trainingTargets[j]));
    }
    optimizer.step();
    epochs++;
    error += loss.getValueAsVector().flatten();
  }
  error /= iterationCount;
  model.setTrainingMode(false);

  timeLine1.append(error);
  if (timeLine1.size() > width / timeStepX + 1) {
    timeLine1.remove(0);
  }

  strokeWeight(4);
  for (int i = 0; i < trainingInputs.length; i++) {
    float c = map(trainingTargets[i].argmax(), 0, 3, 0, 360);
    stroke(c, 100, 100);

    float x = map(trainingInputs[i].get(0), 0, 1.0, 1, width / 2 - 1);
    float y = map(trainingInputs[i].get(1), 0, 1.0, height / 2 - 1, 1);
    point(x, y);

    x = map(trainingInputs[i].get(0), 0, 1.0, width / 2 + 1, width - 1);
    y = map(trainingInputs[i].get(2), 0, 1.0, height / 2 - 1, 1);
    point(x, y);

    x = map(trainingInputs[i].get(1), 0, 1.0, width / 2 + 1, width);
    y = map(trainingInputs[i].get(3), 0, 1.0, height - 1, height / 2 + 1);
    point(x, y);

    x = map(trainingInputs[i].get(2), 0, 1.0, 1, width / 2 - 1);
    y = map(trainingInputs[i].get(3), 0, 1.0, height - 1, height / 2 + 1);
    point(x, y);
  }

  int success = 0;
  for (int i = 0; i < testInputs.length; i++) {
    Vector r = model.model(testInputs[i]).detachAsVector();
    int i1 = r.argmax();
    int i2 = testTargets[i].argmax();
    if (i1 == i2) {
      success++;
    }

    float x = map(testInputs[i].get(0), 0, 1.0, 1, width / 2 - 1);
    float y = map(testInputs[i].get(1), 0, 1.0, height / 2 - 1, 1);
    float c = map(i2, 0, 3, 0, 360);
    strokeWeight(8);
    stroke(c, 100, 100);
    point(x, y);
    if (i1 != i2) {
      c = map(i1, 0, 3, 0, 360);
      strokeWeight(16);
      stroke(c, 100, 100, 50);
      point(x, y);
    }

    x = map(testInputs[i].get(0), 0, 1.0, width / 2 + 1, width - 1);
    y = map(testInputs[i].get(2), 0, 1.0, height / 2 - 1, 1);
    c = map(i2, 0, 3, 0, 360);
    strokeWeight(8);
    stroke(c, 100, 100);
    point(x, y);
    if (i1 != i2) {
      c = map(i1, 0, 3, 0, 360);
      strokeWeight(16);
      stroke(c, 100, 100, 50);
      point(x, y);
    }

    x = map(testInputs[i].get(1), 0, 1.0, width / 2 + 1, width - 1);
    y = map(testInputs[i].get(3), 0, 1.0, height - 1, height / 2 + 1);
    c = map(i2, 0, 3, 0, 360);
    strokeWeight(8);
    stroke(c, 100, 100);
    point(x, y);
    if (i1 != i2) {
      c = map(i1, 0, 3, 0, 360);
      strokeWeight(16);
      stroke(c, 100, 100, 50);
      point(x, y);
    }

    x = map(testInputs[i].get(2), 0, 1.0, 1, width / 2 - 1);
    y = map(testInputs[i].get(3), 0, 1.0, height - 1, height / 2 + 1);
    c = map(i2, 0, 3, 0, 360);
    strokeWeight(8);
    stroke(c, 100, 100);
    point(x, y);
    if (i1 != i2) {
      c = map(i1, 0, 3, 0, 360);
      strokeWeight(16);
      stroke(c, 100, 100, 50);
      point(x, y);
    }
  }
  float accuracy = (float) success / (float) testInputs.length;

  timeLine2.append(accuracy);
  if (timeLine2.size() > width / timeStepX + 1) {
    timeLine2.remove(0);
  }

  noFill();
  strokeWeight(2);
  stroke(300, 100, 100);
  beginShape();
  for (int i = 0; i < timeLine1.size(); i++) {
    float x = map(i, 0, width / timeStepX, 0, width);
    float y = map(timeLine1.get(i), 0, 1.0, height, 0);
    vertex(x, y);
  }
  endShape();

  stroke(60, 100, 100);
  beginShape();
  for (int i = 0; i < timeLine2.size(); i++) {
    float x = map(i, 0, width / timeStepX, 0, width);
    float y = map(timeLine2.get(i), 0, 1.0, height, 0);
    vertex(x, y);
  }
  endShape();

  text(String.format("%d %.5f %.2f%%", epochs, optimizer.learningRate, accuracy * 100), 1, height - 1);

  if (epochs > 1000 && accuracy < 0.8) {
    model.reset();
    epochs = 0;
  }
}

void keyPressed() {
  if (key == ' ') {
    println("mutate model");
    optimizer.reset();
    //model.mutate();
  } else if (key == 'n') {
    println("new training");
    buildTraingAndTestSets();
    optimizer.reset();
  } else if (key == 's') {
    println("save model");
    println(model.toJSON());
  }
}
