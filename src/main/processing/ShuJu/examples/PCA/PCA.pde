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

Vector[] trainingLabel;
Vector[] trainingData;
Vector[] reducedTrainingData;

float t = 0;
float dt = 0.01;

void setup() {
  size(800, 800);
  textSize(16);
  
  DataSet fisherSet = loadTrainingSet("fisher's data.csv");

  // transform
  fisherSet
    .transform(new NumericScaler(new DataSummary(fisherSet, DataRow.FEATURES, 0)), DataRow.FEATURES, 0)
    .transform(new NumericScaler(new DataSummary(fisherSet, DataRow.FEATURES, 1)), DataRow.FEATURES, 1)
    .transform(new NumericScaler(new DataSummary(fisherSet, DataRow.FEATURES, 2)), DataRow.FEATURES, 2)
    .transform(new NumericScaler(new DataSummary(fisherSet, DataRow.FEATURES, 3)), DataRow.FEATURES, 3)
    .shuffle();

  // Training samples
  DataSet training = fisherSet; //.subset(0, 110);
  // Test samples
  // DataSet test = fisherSet.subset(110, 150);

  trainingLabel = training.labelsAsVectorArray();
  
  trainingData = training.featuresAsVectorArray();
  Matrix m = new Matrix(trainingData);
  Matrix pca = Linalg.PCA(m, 2, 1e-4f);
  reducedTrainingData = m.sub(m.avg(0).toVector(0), 0).matmul(pca).toVectorArray();
}

void draw() {
  background(255);
  
  noStroke();
  for(int i = 0; i < reducedTrainingData.length; i++) {
    float x = lerp(trainingData[i].get(0), reducedTrainingData[i].get(0), t);
    float y = lerp(trainingData[i].get(1), reducedTrainingData[i].get(1), t);
    int l = trainingLabel[i].argmax();
    
    if(l == 0) {
      fill(255, 0, 0);
    } else if (l == 1) {
      fill(0, 255, 0);
    } else if (l == 2) {
      fill(0, 0, 255);
    } else {
      fill(0);
    }
    
    circle(map(x, -1.5, 1.5, 0, width), map(y, -1.5, 1.5, 0, height), 8);
  }
  
  t += dt;
  if(t < 0 || t > 1) {
    dt = -dt;
  }
}
