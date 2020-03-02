/**
 * GradientDescent and Neural Network console
 *
 * Author: Romuald Rousseau
 * Date: 2019-03-27
 * Processing 3+
 */

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
import com.github.romualdrousseau.shuju.nlp.*;
import com.github.romualdrousseau.shuju.util.*;
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
import com.github.romualdrousseau.shuju.ml.nn.normalizer.*;
import com.github.romualdrousseau.shuju.nlp.impl.*;
import com.github.romualdrousseau.shuju.ml.naivebayes.*;

static final int SAMPLE_COUNT = 50;
static final int BRAIN_CLOCK = 10;
static final String BRAIN_DEFAULT_MODEL = "Softmax";
static final int BRAIN_HIDDEN_NEURONS = 64;
static final float CUBE_ANGULAR_VELOCITY = 0.5;

static final Action[] actionMap = {
  new Action(com.jogamp.newt.event.KeyEvent.VK_F1, "F1", "Reset the data set"),
  new Action(com.jogamp.newt.event.KeyEvent.VK_F2, "F2", "Reset the simulation"),
  new Action(com.jogamp.newt.event.KeyEvent.VK_F3, "F3", "Save the model"),
  new Action(com.jogamp.newt.event.KeyEvent.VK_F4, "F4", "Load a model")
};

void setup() {
  size(1600, 800, P2D);
  noFill();
  stroke(255);
  strokeWeight(1);

  Brain.init(BRAIN_DEFAULT_MODEL);
  Map2D.init();
  Map3D.init();
  PerfGraph.init();
}

void draw() {
  Brain.fit();
  Map2D.update();
  Map3D.update();
  PerfGraph.update();

  background(51);
  Map2D.draw(0, 0, width / 2 - 1, height / 2 - 1);
  Map3D.draw(width / 2 + 1, 0, width / 2 - 1, height / 2 - 1);
  PerfGraph.draw(0, height / 2 + 1, width, height / 2 - 1 - 24);
  drawHUD();
}

void drawHUD() {
  noStroke();
  fill(255, 32);
  rect(0, height - 24, width, height);

  stroke(255);
  line(0, height / 2, width, height / 2);
  line(width / 2, 0, width / 2, height / 2);

  fill(255);
  for(int i = 0; i < actionMap.length; i++) {
    text(String.format("%s: %s", actionMap[i].keyString, actionMap[i].help), 8 + i * width / actionMap.length, height - 8);
  }
  noFill();
}
