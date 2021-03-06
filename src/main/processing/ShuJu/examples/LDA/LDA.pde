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
import com.github.romualdrousseau.shuju.nlp.impl.*;
import com.github.romualdrousseau.shuju.ml.naivebayes.*;

Tensor1D[] trainingLabel;
Tensor1D[] trainingData;
Tensor1D[][] trainingDataByClass = {null, null, null};
Tensor2D reducedTrainingData;

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

  DataSet training = fisherSet;

  trainingLabel = training.labelsAsVectorArray();
  trainingData = training.featuresAsVectorArray();
  
  for(int i = 0; i < flowerNames.size(); i++) {
    final Tensor1D label = flowerNames.word2vec(flowerNames.get(i));
    trainingDataByClass[i] = training.filter(new java.util.function.Predicate<DataRow>() {
      public boolean test(DataRow row) {
        return row.label().equals(label);
      }
    }).featuresAsVectorArray();
  }
  
  Tensor2D featuresMeans = new Tensor2D(trainingData).avg(0);
  System.out.println(featuresMeans);
  
  Tensor2D featuresMeansByClass = new Tensor2D(0, trainingDataByClass[0][0].rowCount()); 
  for(int i = 0; i < trainingDataByClass.length; i++) {
      Tensor2D m = new Tensor2D(trainingDataByClass[i]);
      featuresMeansByClass = featuresMeansByClass.concatenate(m.avg(0), 0);
  }
  System.out.println(featuresMeansByClass);

  Tensor2D withinClassScatter = new Tensor2D(trainingDataByClass[0][0].rowCount(), trainingDataByClass[0][0].rowCount());
  for(int i = 0; i < trainingDataByClass.length; i++) {
      Tensor2D m = new Tensor2D(trainingDataByClass[i]);
      m = m.sub(featuresMeansByClass.slice(i, 0, 1, -1));
      m = m.transpose().matmul(m);
      withinClassScatter = withinClassScatter.add(m);
  }
  System.out.println(withinClassScatter);

  Tensor2D betweenClassScatter = new Tensor2D(trainingDataByClass[0][0].rowCount(), trainingDataByClass[0][0].rowCount());
  Tensor2D m = featuresMeansByClass.copy();
  m = m.sub(featuresMeans.slice(0, 0, 1, -1));
  betweenClassScatter = m.transpose().matmul(m).mul(trainingDataByClass[0].length);
  System.out.println(betweenClassScatter);
  
  m = withinClassScatter.inv().matmul(betweenClassScatter);
  System.out.println(m);
  Tensor2D[] eig = Linalg.Eig(m, 1e-4);
  Tensor2D sort = Linalg.Sort(eig[0]);
  Tensor2D lda = eig[1].matmul(sort).slice(0, 0, eig[1].rowCount(), 2);
  System.out.println(lda);
  
  Tensor2D all = new Tensor2D(training.featuresAsVectorArray());
  reducedTrainingData = all.sub(featuresMeans.slice(0, 0, 1, -1)).matmul(lda);
}

void draw() {
  background(255);
  
  noStroke();
  for(int i = 0; i < reducedTrainingData.shape[0]; i++) {
    float x = lerp(trainingData[i].get(0), reducedTrainingData.get(i, 0), t);
    float y = lerp(trainingData[i].get(1), reducedTrainingData.get(i, 1), t);
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
    dt = 0;
  }
}
