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

Model model;
Optimizer optimizer;
Loss loss;

Vector[] inputs = {
  new Vector(new float[] {0, 0}),
  new Vector(new float[] {0, 1}),
  new Vector(new float[] {1, 0}),
  new Vector(new float[] {1, 1})
};

Vector[] targets = {
  new Vector(new float[] {0}),
  new Vector(new float[] {1}),
  new Vector(new float[] {1}),
  new Vector(new float[] {0})
};

void setup() {
  size(400, 400, P2D);

  model = new Model();

  model.add(new DenseBuilder()
    .setInputUnits(2)
    .setUnits(4)
    .setActivation(new Tanh())
    .setInitializer(new GlorotUniformInitializer()).build());

  model.add(new DenseBuilder()
    .setInputUnits(4)
    .setUnits(1)
    .setActivation(new Linear())
    .setInitializer(new GlorotUniformInitializer()).build());

  optimizer = new OptimizerRMSPropBuilder().build(model);

  loss = new Loss(new Huber());
}

void draw() {
  final int r = 100;
  final int w = width / r;
  final int h = height / r;

  for (int i = 0; i < 5; i++) {

    optimizer.zeroGradients();

    for(int j = 0; j < 4; j++) {
      loss.loss(model.model(inputs[j]), targets[j]).backward();
    }

    optimizer.step();
  }

  background(0);
  noStroke();
  for (int i = 0; i <= r; i++) {
    for (int j = 0; j <= r; j++) {
      Vector input = new Vector(new float[] {(float) i / (float) r, (float) j / (float) r});
      fill(model.model(input).detachAsVector().get(0) * 255);
      rect(j * w, i * h, w, h);
    }
  }
}

void keyPressed() {
  model.reset();
}
