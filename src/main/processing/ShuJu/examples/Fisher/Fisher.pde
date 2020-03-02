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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

static final String[] flowerNames = { "I. setosa", "I. versicolor", "I. virginica" };

int success = 0;
double absError = 0.0;
double avgError = 0.0;

public DataSet loadTrainingSet(String fileName) throws IOException {
  DataSet result = new DataSet();

  NumericColumn sepalLengthCol = new NumericColumn();
  NumericColumn sepalWidthCol = new NumericColumn();
  NumericColumn petalLengthCol = new NumericColumn();
  NumericColumn petalWidthCol = new NumericColumn();
  StringColumn flowerNameCol = new StringColumn(new com.github.romualdrousseau.shuju.nlp.StringList(flowerNames));

  Table table = loadTable(fileName, "header");
  for (TableRow row : table.rows()) {
    result.addRow(new DataRow()
      .addFeature(sepalLengthCol.valueOf(row.getFloat(1)))
      .addFeature(sepalWidthCol.valueOf(row.getFloat(2)))
      .addFeature(petalLengthCol.valueOf(row.getFloat(3)))
      .addFeature(petalWidthCol.valueOf(row.getFloat(4)))
      .setLabel(flowerNameCol.valueOf(row.getString(5).replaceAll("\\u00a0|\\s", " ")))
    );
  }

  return result;
}

void printSummary(DataSet ts) {
  double correct = (double) success * 100 / ts.rows().size();
  double incorrect = 100.0 - correct;

  println(String.format("Correctly Classified Instances\t\t%d\t%.2f%%", success, correct));
  println(String.format("Incorrectly Classified Instances\t%d\t%.2f%%", ts.rows().size() - success, incorrect));
  println(String.format("Mean error\t\t\t\t%f", avgError));
  println(String.format("Root mean squared error\t\t%f", Math.sqrt(avgError)));
  println(String.format("Absolute error\t\t\t%f", absError));
  println(String.format("Root absolute squared error\t\t%f", Math.sqrt(absError)));
  println(String.format("Total Number of Instances\t\t%d", ts.rows().size()));
}

void setup() {
  size(192, 192);
  textSize(16);
  noLoop();

  try {
    DataSet fisherSet = loadTrainingSet("fisher's data.csv");

    // transform
    fisherSet
      .transform(new NumericScaler(new DataSummary(fisherSet, DataRow.FEATURES, 0)), DataRow.FEATURES, 0)
      .transform(new NumericScaler(new DataSummary(fisherSet, DataRow.FEATURES, 1)), DataRow.FEATURES, 1)
      .transform(new NumericScaler(new DataSummary(fisherSet, DataRow.FEATURES, 2)), DataRow.FEATURES, 2)
      .transform(new NumericScaler(new DataSummary(fisherSet, DataRow.FEATURES, 3)), DataRow.FEATURES, 3)
      .shuffle();

    // Training samples
    DataSet training = fisherSet.subset(0, 110);
    // Test samples
    DataSet test = fisherSet.subset(110, 150);

    // classification
    KNN knn = new KNN(6);
    knn.fit(training.featuresAsVectorArray(), training.labelsAsVectorArray());

    for (DataRow row : test.rows()) {
      Vector result = knn.predict(row.featuresAsOneVector());

      int bestLabel = result.argmax();
      if (bestLabel == row.label().argmax()) {
        success++;
      }

      double squaredError = pow(result.distance(row.label()), 2);
      avgError += squaredError;
      absError = Math.max(absError, squaredError);
    }
    avgError /= test.rows().size();

    printSummary(test);
  }
  catch(Exception x) {
    x.printStackTrace();
  }
}

void draw() {
  background(0);
}
