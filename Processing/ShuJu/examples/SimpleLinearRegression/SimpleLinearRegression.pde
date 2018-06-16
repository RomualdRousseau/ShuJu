import com.github.romualdrousseau.shuju.*;
import com.github.romualdrousseau.shuju.features.*;
import com.github.romualdrousseau.shuju.transforms.*;
import com.github.romualdrousseau.shuju.ml.slr.*;

import java.util.List;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

DataSet training;
DataSet test;

void setup() {
  size(500, 500);
  textSize(16);
  noLoop();
  
  try {
    training = loadTrainingSet("x62.csv");
    
    IClassifier slr = new SLR();
    slr.train(training);
  
    test = new DataSet();
    for(int i = 0; i < 24; i++) {
      test.addRow(new DataRow().addFeature(new NumericFeature((double) i)));
    }
  
    for(DataRow row: test.rows()) {
      Result result = slr.predict(row);
      row.setLabel(result.getLabel());
    }
    
    test = loadTrainingSet("x62.csv")
      .transform(new VectorAdd(test, -1.0), IFeature.LABEL)           // extract seaonability
      .transform(new SmoothScaler(0.5), IFeature.LABEL)               // filter data by exponential average
      .transform(new VectorShift(12.0), 0)                            // translate in the future
      .transform(new VectorAdd(test.subset(12, 24)), IFeature.LABEL); // add trend
  } catch(IOException x) {
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
  for(int i = 0; i < width; i += scaleX) {
    line(i, height / 2 - 3, i, height / 2 + 3);
  }
  line(0, 0, 0, height);
  for(int i = 0; i < height; i += scaleY) {
    line(-3, i, 3, i);
  }
  
  double prevX = 0.0;
  double prevY = 0.0;
  boolean firstPoint = true;
  // Plot training set
  if(training != null) {
    strokeWeight(5);
    stroke(0, 0, 255);
    for(DataRow row: training.rows()) {
      if(firstPoint) {
        prevX = (Double) row.features().get(0).getValue() * scaleX;
        prevY = (Double) row.getLabel().getValue() * scaleY;
        firstPoint = false;
      }
      else {
        double x = (Double) row.features().get(0).getValue() * scaleX;
        double y = (Double) row.getLabel().getValue() * scaleY;
        line((float) prevX, height / 2 - (float) prevY, (float) x, height / 2 - (float) y);
        prevX = x;
        prevY = y;
      }
    }
  }
  
  // Plot prediction set
  if(test != null) {
    strokeWeight(1); 
    stroke(255, 0, 0);
    for(DataRow row: test.rows()) {
      double x = (Double) row.features().get(0).getValue() * scaleX;
      double y = (Double) row.getLabel().getValue() * scaleY;
      line((float) prevX, height / 2 - (float) prevY, (float) x, height / 2 - (float) y);
      prevX = x;
      prevY = y;
    }
  }
}

public DataSet loadTrainingSet(String fileName) throws IOException {
  DataSet result = new DataSet();

  Table table = loadTable(fileName, "header");  
  for (TableRow row : table.rows()) {
    result.addRow(new DataRow()
      .addFeature(new NumericFeature(row.getDouble(1) - 1.0)) // Time
      .setLabel(new NumericFeature(row.getDouble(2)))   // Data
    );
  }
  
  return result;
}