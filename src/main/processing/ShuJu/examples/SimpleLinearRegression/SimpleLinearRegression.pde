import com.github.romualdrousseau.shuju.transforms.*;
import com.github.romualdrousseau.shuju.nlp.*;
import com.github.romualdrousseau.shuju.genetic.*;
import com.github.romualdrousseau.shuju.ml.kmean.*;
import com.github.romualdrousseau.shuju.ml.qlearner.*;
import com.github.romualdrousseau.shuju.ml.nn.*;
import com.github.romualdrousseau.shuju.ml.nn.optimizer.*;
import com.github.romualdrousseau.shuju.ml.nn.activation.*;
import com.github.romualdrousseau.shuju.ml.nn.loss.*;
import com.github.romualdrousseau.shuju.columns.*;
import com.github.romualdrousseau.shuju.cv.*;
import com.github.romualdrousseau.shuju.math.*;
import com.github.romualdrousseau.shuju.json.processing.*;
import com.github.romualdrousseau.shuju.nlp.impl.*;
import com.github.romualdrousseau.shuju.ml.nn.optimizer.builder.*;
import com.github.romualdrousseau.shuju.ml.knn.*;
import com.github.romualdrousseau.shuju.*;
import com.github.romualdrousseau.shuju.json.*;
import com.github.romualdrousseau.shuju.ml.nn.initializer.*;
import com.github.romualdrousseau.shuju.util.*;
import com.github.romualdrousseau.shuju.ml.slr.*;
import com.github.romualdrousseau.shuju.ml.nn.scheduler.*;
import com.github.romualdrousseau.shuju.json.jackson.*;
import com.github.romualdrousseau.shuju.cv.templatematching.*;
import com.github.romualdrousseau.shuju.cv.templatematching.shapeextractor.*;
import com.github.romualdrousseau.shuju.ml.nn.normalizer.*;

import java.util.List;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

SLR slr;
DataSet training;
DataSet test;

void setup() {
  size(500, 500);
  textSize(16);
  noLoop();

  try {
    training = loadTrainingSet("x62.csv");

    slr = new SLR();
    slr.fit(training.featuresAsVectorArray(), training.labelsAsVectorArray());

    DataSet trend = buildTrendSet();

    test = loadTrainingSet("x62.csv")
      .transform(new VectorAdd(trend, DataRow.LABELS, -1.0), DataRow.LABELS, 0)           // extract seasonability
      .transform(new SmoothScaler(0.5), DataRow.LABELS, 0)                                // filter data by exponential average
      .transform(new VectorAdd(trend.subset(12, 24), DataRow.LABELS), DataRow.LABELS, 0)  // add trend
      .transform(new VectorShift(12.0), DataRow.FEATURES, 0);                             // translate in the future
  }
  catch(IOException x) {
    x.printStackTrace();
  }
}

void draw() {
  final double scaleX = width / (24 - 2);
  final double scaleY = height / (24 - 2);

  background(255);

  // Draw axises
  strokeWeight(1);
  stroke(200, 200, 200);
  line(0, height / 2, width, height / 2);
  for (int i = 0; i < width; i += scaleX) {
    line(i, height / 2 - 3, i, height / 2 + 3);
  }
  line(0, 0, 0, height);
  for (int i = 0; i < height; i += scaleY) {
    line(-3, i, 3, i);
  }

  double prevX = 0.0;
  double prevY = 0.0;
  boolean firstPoint = true;
  // Plot training set
  if (training != null) {
    strokeWeight(5);
    stroke(0, 0, 255);
    for (DataRow row : training.rows()) {
      if (firstPoint) {
        prevX = (double) row.features().get(0).get(0) * scaleX;
        prevY = (double) row.label().get(0) * scaleY;
        firstPoint = false;
      } else {
        double x = (double) row.features().get(0).get(0) * scaleX;
        double y = (double) row.label().get(0) * scaleY;
        line((float) prevX, height / 2 - (float) prevY, (float) x, height / 2 - (float) y);
        prevX = x;
        prevY = y;
      }
    }
  }

  // Plot prediction set
  if (test != null) {
    strokeWeight(1);
    stroke(255, 0, 0);
    for (DataRow row : test.rows()) {
      double x = (double) row.features().get(0).get(0) * scaleX;
      double y = (double) row.label().get(0) * scaleY;
      line((float) prevX, height / 2 - (float) prevY, (float) x, height / 2 - (float) y);
      prevX = x;
      prevY = y;
    }
  }
}

DataSet loadTrainingSet(String fileName) throws IOException {
  DataSet result = new DataSet();

  NumericColumn colT = new NumericColumn();
  NumericColumn ColdD = new NumericColumn();

  Table table = loadTable(fileName, "header");
  for (TableRow row : table.rows()) {
    result.addRow(new DataRow()
      .addFeature(colT.valueOf(row.getFloat(1) - 1.0)) // Time
      .setLabel(ColdD.valueOf(row.getFloat(2)))        // Data
      );
  }

  return result;
}

DataSet buildTrendSet() {
  DataSet result = new DataSet();

  NumericColumn colT = new NumericColumn();

  for (int i = 0; i < 24; i++) {
    result.addRow(new DataRow().addFeature(colT.valueOf((float) i)));
  }

  for (DataRow row : result.rows()) {
    row.setLabel(slr.predict(row.featuresAsOneVector()));
  }

  return result;
}
