import com.github.romualdrousseau.shuju.columns.*;
import com.github.romualdrousseau.shuju.cv.*;
import com.github.romualdrousseau.shuju.json.*;
import com.github.romualdrousseau.shuju.math.*;
import com.github.romualdrousseau.shuju.ml.nn.activation.*;
import com.github.romualdrousseau.shuju.ml.nn.initializer.*;
import com.github.romualdrousseau.shuju.ml.nn.loss.*;
import com.github.romualdrousseau.shuju.ml.nn.normalizer.*;
import com.github.romualdrousseau.shuju.ml.nn.optimizer.*;
import com.github.romualdrousseau.shuju.ml.qlearner.*;
import com.github.romualdrousseau.shuju.nlp.impl.*;
import com.github.romualdrousseau.shuju.nlp.*;
import com.github.romualdrousseau.shuju.transforms.*;
import com.github.romualdrousseau.shuju.ml.nn.*;
import com.github.romualdrousseau.shuju.ml.nn.optimizer.builder.*;
import com.github.romualdrousseau.shuju.cv.templatematching.*;
import com.github.romualdrousseau.shuju.*;
import com.github.romualdrousseau.shuju.json.processing.*;
import com.github.romualdrousseau.shuju.ml.knn.*;
import com.github.romualdrousseau.shuju.ml.nn.scheduler.*;
import com.github.romualdrousseau.shuju.ml.slr.*;
import com.github.romualdrousseau.shuju.cv.templatematching.shapeextractor.*;
import com.github.romualdrousseau.shuju.json.jackson.*;
import com.github.romualdrousseau.shuju.util.*;

Model model;
Optimizer optimizer;
Loss loss;

Matrix[] inputs = {
  new Matrix(new float[] {0, 0}),
  new Matrix(new float[] {0, 1}),
  new Matrix(new float[] {1, 0}),
  new Matrix(new float[] {1, 1})
};

Matrix[] targets = {
  new Matrix(new float[] {0}),
  new Matrix(new float[] {1}),
  new Matrix(new float[] {1}),
  new Matrix(new float[] {0})
};

void setup() {
  size(400, 400, P2D);

  model = new Model();

  model.add(new LayerBuilder()
    .setInputUnits(2)
    .setUnits(4)
    .setActivation(new Tanh())
    .setInitializer(new GlorotUniformInitializer()).build());

  model.add(new LayerBuilder()
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
      Matrix input = new Matrix(new float[] {(float) i / (float) r, (float) j / (float) r});
      fill(model.model(input).detachAsVector().get(0) * 255);
      rect(j * w, i * h, w, h);
    }
  }
}

void keyPressed() {
  model.reset();
}
