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
import com.github.romualdrousseau.shuju.ml.nn.normalizer.*;
import com.github.romualdrousseau.shuju.nlp.impl.*;

final int[] classColors = {
  color(255, 185, 185), 
  color(185, 185, 255), 
  color(255, 255, 255)
};

DataSet dataset;
Model model;
Optimizer optimizer;
Loss loss;

int lerpColor3(int c1, int c2, int c3, float amt) {
  if (amt < 0.5) {
    return lerpColor(c1, c2, map(amt, 0, 0.5, 0, 1));
  } else {
    return lerpColor(c2, c3, map(amt, 0.5, 1, 0, 1));
  }
}

Vector kernel(Vector v) {
  float g = 0.5;
  float z = exp(-pow(v.norm(), 2) / 2 * pow(g, 2));
  return new Vector(new float[] { v.get(0), v.get(1), z });
}

void buildModelSVM() {
  model = new Model();

  model.add(new DenseBuilder()
    .setInputUnits(3)
    .setUnits(1)
    .setActivation(new Linear())
    .setInitializer(new GlorotUniformInitializer())
    .build());

  optimizer = new OptimizerSgdBuilder()
    .setMomentum(0)
    .build(model);

  loss = new Loss(new Hinge());
}

void trainModelSVM() {
  for (int i = 0; i < 1000; i++) {
    optimizer.zeroGradients();
    for (DataRow row : dataset.rows()) {
      Vector input = row.features().get(0).copy().map(-10, 10, -1, 1);
      Vector target = new Vector(new float[] { row.label().argmax() * 2 - 1 });
      loss.loss(model.model(kernel(input)), target).backward();
    }
    optimizer.step();
  }
}

void setup() {
  size(400, 400, P2D);
  dataset = DataSet.makeCircles(100, 2, 2);
  buildModelSVM();
}

void draw() {
  final int r = 100;
  final int w = width / r;
  final int h = height / r;

  trainModelSVM();

  background(51);

  noStroke();
  for (int i = 0; i <= r; i++) {
    for (int j = 0; j <= r; j++) {
      Vector input = new Vector(new float[] { j, i }).map(0, r, -1, 1);
      float amt = model.model(kernel(input)).detachAsVector().map(-1, 1, 0, 1).get(0);
      fill(lerpColor3(classColors[0], classColors[2], classColors[1], amt));
      rect(j * w, i * w, w, h);
    }
  }

  stroke(255);
  fill(0, 64);
  for (int k = 0; k < 10; k++) {
    for (int i = 0; i <= r; i++) {
      for (int j = 0; j <= r; j++) {
        Vector input = new Vector(new float[] { j, i }).map(0, r, -1, 1);
        float amt = model.model(kernel(input)).detachAsVector().map(-1, 1, 0, 100).get(0);
        if (abs(amt - k * 10) < 0.5) {
          rect(j * w, i * w, w, h);
        }
      }
    }
  }

  stroke(0);
  fill(0, 64);
  for (DataRow row : dataset.rows()) {
    float x = map(row.features().get(0).get(0), -10, 10, 0, width);
    float y = map(row.features().get(0).get(1), -10, 10, 0, height);
    if (row.label().argmax() == 0) {
      circle(x, y, 8);
    } else {
      triangle(x, y -4, x + 4, y + 4, x - 4, y + 4);
    }
  }
}

void keyPressed() {
  model.reset();
  dataset = DataSet.makeCircles(100, 2, 2);
}

void mousePressed() {
  model.reset();
  dataset = DataSet.makeCircles(100, 2, 2);
}
