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

final int[] classColors = {
  color(255, 185, 185),
  color(185, 185, 255),
  color(255, 255, 255)
};

final Tensor2D[] inputs = {
  new Tensor2D(new float[] {0, 0}),
  new Tensor2D(new float[] {0, 1}),
  new Tensor2D(new float[] {1, 0}),
  new Tensor2D(new float[] {1, 1})
};

final Tensor2D[] targets = {
  new Tensor2D(new float[] {0}),
  new Tensor2D(new float[] {1}),
  new Tensor2D(new float[] {1}),
  new Tensor2D(new float[] {0})
};

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

void buildModelXOR() {
  model = new Model();

  model.add(new DenseBuilder()
    .setInputUnits(2)
    .setUnits(4));

  model.add(new ActivationBuilder()
    .setActivation(new Tanh()));

  model.add(new DenseBuilder()
    .setUnits(1));

  optimizer = new OptimizerRMSPropBuilder().build(model);

  loss = new Loss(new Huber());
}

void fitModel() {
  for (int i = 0; i < 100; i++) {
    optimizer.zeroGradients();
    for (int j = 0; j < 4; j++) {
        optimizer.minimize(loss.loss(model.model(inputs[j].transpose()), targets[j].transpose()));
    }
    optimizer.step();
  }
}

float[][] predictModel(int r) {
  float[][] result = new float[r + 1][r + 1];
  for (int i = 0; i <= r; i++) {
    for (int j = 0; j <= r; j++) {
      Tensor2D input = new Tensor2D(new float[] { j, i }).map(0, r, 0, 1).transpose();
      result[i][j] = model.model(input).detach().get(0, 0);
    }
  }
  return result;
}

void setup() {
  size(400, 400, P2D);
  buildModelXOR();
}

void draw() {
  fitModel();
  final float[][] xorMap = predictModel(100);

  final int r = xorMap.length - 1;
  final int w = width / r;
  final int h = height / r;

  background(51);

  noStroke();
  for (int i = 0; i <= r; i++) {
    for (int j = 0; j <= r; j++) {
      float amt = xorMap[i][j];
      fill(lerpColor3(classColors[0], classColors[2], classColors[1], amt));
      rect(j * w, i * w, w, h);
    }
  }

  stroke(255, 128);
  fill(255, 64);
  for (int k = 0; k < 10; k++) {
    for (int i = 0; i <= r; i++) {
      for (int j = 0; j <= r; j++) {
        float amt = xorMap[i][j] * 10;
        if (abs(amt - k) < 0.025) {
          rect(j * w, i * h, w, h);
        }
      }
    }
  }
}

void keyPressed() {
  model.reset();
}

void mousePressed() {
  model.reset();
}
