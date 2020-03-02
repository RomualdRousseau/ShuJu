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

import java.util.List;

PImage image;
List<SearchPoint[]> shapes;
SearchPoint[] bestShape;

void setup() {
  size(500, 500);
  image = loadImage("data/image.png");

  PImage temp = createImage(image.width, image.height, RGB);
  new EdgeFilter().apply(new Bitmap(image), new Bitmap(temp), 1.0);

  shapes = new RectangleExtractor().extractAll(new Bitmap(temp));
  bestShape = new RectangleExtractor().extractBest(new Bitmap(temp));
}

void draw() {
  background(image);

  noFill();
  strokeWeight(4);

  stroke(255, 0, 0);
  for(SearchPoint[] shape: shapes) {
    rect(shape[0].getX(), shape[0].getY(), shape[1].getX() - shape[0].getX(), shape[1].getY() - shape[0].getY());
  }

  stroke(0, 0, 255);
  rect(bestShape[0].getX(), bestShape[0].getY(), bestShape[1].getX() - bestShape[0].getX(), bestShape[1].getY() - bestShape[0].getY());
}
