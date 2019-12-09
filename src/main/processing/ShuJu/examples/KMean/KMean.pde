import com.github.romualdrousseau.shuju.columns.*;
import com.github.romualdrousseau.shuju.cv.*;
import com.github.romualdrousseau.shuju.*;
import com.github.romualdrousseau.shuju.json.jackson.*;
import com.github.romualdrousseau.shuju.json.processing.*;
import com.github.romualdrousseau.shuju.ml.nn.activation.*;
import com.github.romualdrousseau.shuju.ml.nn.*;
import com.github.romualdrousseau.shuju.ml.nn.loss.*;
import com.github.romualdrousseau.shuju.ml.nn.optimizer.*;
import com.github.romualdrousseau.shuju.ml.nn.scheduler.*;
import com.github.romualdrousseau.shuju.ml.slr.*;
import com.github.romualdrousseau.shuju.nlp.*;
import com.github.romualdrousseau.shuju.transforms.*;
import com.github.romualdrousseau.shuju.genetic.*;
import com.github.romualdrousseau.shuju.math.*;
import com.github.romualdrousseau.shuju.ml.nn.optimizer.builder.*;
import com.github.romualdrousseau.shuju.ml.qlearner.*;
import com.github.romualdrousseau.shuju.util.*;
import com.github.romualdrousseau.shuju.cv.templatematching.*;
import com.github.romualdrousseau.shuju.json.*;
import com.github.romualdrousseau.shuju.cv.templatematching.shapeextractor.*;
import com.github.romualdrousseau.shuju.ml.nn.initializer.*;
import com.github.romualdrousseau.shuju.ml.nn.normalizer.*;
import com.github.romualdrousseau.shuju.nlp.impl.*;
import com.github.romualdrousseau.shuju.ml.knn.*;
import com.github.romualdrousseau.shuju.ml.kmean.*;

final static int K = 3;

Vector[] data = new Vector[100];
Vector[] labels = new Vector[100];

com.github.romualdrousseau.shuju.ml.kmean.KMean kmean = new com.github.romualdrousseau.shuju.ml.kmean.KMean(K);

void setup() {
  size(400, 400);
  frameRate(1);

  for (int i = 0; i < 100; i++) {
    data[i] = new Vector(new float[] { random(1), random(1) });
    labels[i] = new Vector(K).set(int(random(K)), 1);
  }
}

void draw() {
  background(51);

  // for (int e = 0; e < 100; e++) {
    kmean.fit(data, labels);
  // }

  for (int y = 0; y < height; y++) {
    for (int x = 0; x < width; x++) {
      Vector point = new Vector(new float[] {map(x, 0, width, 0, 1), map(y, 0, height, 0, 1)});
      Vector label = kmean.predict(point);
      float[] v = new Vector(K).oneHot(label.argmax()).getFloats();
      float r = map(v[0], 0, 1, 128, 255);
      float g = map(v[1], 0, 1, 128, 255);
      float b = map(v[2], 0, 1, 128, 255);
      stroke(r, g, b, 128);
      point(x, y);
    }
  }

  noStroke();

  for (int i = 0; i < data.length; i++) {
    Vector point = data[i];
    Vector label = labels[i];

    float[] v = new Vector(K).oneHot(label.argmax()).getFloats();
    float r = map(v[0], 0, 1, 128, 255);
    float g = map(v[1], 0, 1, 128, 255);
    float b = map(v[2], 0, 1, 128, 255);
    fill(r, g, b);

    float x = map(point.get(0), 0, 1, 0, width);
    float y = map(point.get(1), 0, 1, 0, height);
    ellipse(x, y, 5, 5);
  }
}

void keyPressed() {
    for (int i = 0; i < 100; i++) {
      data[i] = new Vector(new float[] { random(1), random(1) });
    }
}
